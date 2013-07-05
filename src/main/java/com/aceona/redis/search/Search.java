package com.aceona.redis.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.Word;
import com.google.gson.Gson;
import com.sun.tools.jdi.LinkedHashMap;

/**
 * Created with IntelliJ IDEA. User: kxc Date: 13-4-27 Time: 下午2:30 To change
 * this template use File | Settings | File Templates.
 */
public class Search
{

    static List<String> indexed_models;

    static SearchConfig config;

    static Dictionary dic;

    public static SearchConfig configure()
    {
        if (null == config)
        {
            config = new SearchConfig();
        }

        if (null == config.disable_mmseg)
        {
            dic = Dictionary.getInstance();
        }

        return config;
    }



    /**
     * 自动补全 
     * @param type
     * @param w
     * @param options
     * @return
     */
    public static List<String> complete(String type, String w, Map options)
    {
        Jedis jedis = Search.configure().redisPool.getResource();
        int limit = (Integer) (options.get("limit") != null ? options.get("limit") : 10);
        Map<String, String> conditions = (Map<String, String>) (options.get("conditions") != null ? options
                .get("conditions") : new HashMap<String, String>());
        if ((StringUtils.isBlank(w) && conditions.isEmpty()) || (StringUtils.isBlank(type)))
            return new ArrayList<String>();
        Set<String> prefix_matches = new HashSet<String>();

        int range_len = Search.config.complete_max_length;
        String prefix = w.toLowerCase();
        String key = Search.mk_complete_key(type);
        Long start = jedis.zrank(key, prefix);
        if (start != null)
        {
            long count = limit;
            long max_range = start + (range_len * limit) - 1;
            List<String> range = new ArrayList<String>(jedis.zrange(key, start, max_range));
            while (prefix_matches.size() <= count)
            {
                start += range_len;
                if (null == range || range.size() == 0)
                    break;

                for (String entry : range)
                {
                    int min_len = Math.min(entry.length(), prefix.length());
                    if (entry.substring(0, min_len - 1).equals(prefix.substring(0, (min_len - 1))))
                    {
                        count = prefix_matches.size();
                        break;
                    }
                    if (entry.endsWith("*") && prefix_matches.size() != count)
                    {
                        prefix_matches.add(entry.substring(0, (entry.length() - 1)));
                    }
                }

                if (start >= range.size())
                {
                    range = new ArrayList<String>();

                }
                else
                {
                    range = range.subList(start.intValue(), (int) Math.min((max_range - 1), (range.size() - 1)));
                }
            }
        }

        // 组合words 特别key名
        List<String> words = new ArrayList<String>();
        for (String wd : prefix_matches)
        {
            words.add(Search.mk_sets_key(type, wd));
        }

        // 组合特别 key ,但这里不会像 query 那样放入 words， 因为在 complete 里面 words 是用 union
        // 取的，condition_keys 和 words 应该取交集
        List<String> condition_keys = new ArrayList<String>();
        if (null == conditions || conditions.isEmpty())
        {
            // if (conditions instanceof ArrayList) {
            // conditions = conditions.subList(0, 1);
            // }

            Iterator<String> keys = conditions.keySet().iterator();

            while (keys.hasNext())
            {
                String c_key = keys.next();// key
                String c_value = conditions.get(c_key);// 上面key对应的value
                condition_keys.add(Search.mk_condition_key(type, c_key, c_value));
            }
        }

        // 按词搜索
        String temp_store_key = "tmpsunionstore:" + StringUtils.join(words, "+");
        if (words.size() > 0)
        {
            if (!jedis.exists(temp_store_key))
            {
                // 将多个词语组合对比，得到并集，并存入临时区域
                jedis.sunionstore(temp_store_key, (String[]) words.toArray());
                // 将临时搜索设为1天后自动清除
                jedis.expire(temp_store_key, 86400);
            }
        }
        else
        {
            if (words.size() == 1)
            {
                temp_store_key = words.get(0);
            }
            else
            {
                return new ArrayList<String>();
            }
        }

        if (null != condition_keys || !condition_keys.isEmpty())
        {
            if (null == words || words.isEmpty())
            {
                condition_keys.add(temp_store_key);
            }
            temp_store_key = "tmpsinterstore:" + StringUtils.join(condition_keys, "+");
            if (!jedis.exists(temp_store_key))
            {
                jedis.sinterstore(temp_store_key, (String[]) condition_keys.toArray());
                jedis.expire(temp_store_key, 86400);
            }
        }

        SortingParams sort_params = new SortingParams().limit(0, limit).by(Search.mk_score_key(type, "*")).desc();
        List<String> ids = jedis.sort(temp_store_key, sort_params);
        if (null == ids || ids.isEmpty())
            return new ArrayList<String>();
        return hmget(jedis, type, ids, null);
    }

    /**
     * 分词查询
     * @param type
     * @param text
     * @param options
     * @return
     * @throws Exception
     */
    public static List<String> query(String type, String text, LinkedHashMap options) throws Exception
    {
        Jedis jedis = Search.configure().redisPool.getResource();
        long tm = System.currentTimeMillis();
        List<String> result = new ArrayList<String>();
        int limit = (Integer) (options.get("limit") != null ? options.get("limit") : 10);

        String sort_field = (String) (options.get("sort_field") != null ? options.get("sort_field") : "id");
        Map<String, String> conditions = (Map<String, String>) (options.get("conditions") != null ? options
                .get("conditions") : new HashMap<String, String>());

        if ((StringUtils.isBlank(text)) && (null == conditions || conditions.isEmpty()))
            return new ArrayList<String>();

        List<String> words = Search.split(text);
        for (String w : words)
        {
            words.add(Search.mk_sets_key(type, w));
        }

        List<String> condition_keys = new ArrayList<String>();
        if (null != conditions && !conditions.isEmpty())
        {
            // if (conditions instanceof ArrayList)
            // conditions = conditions.subList(0, 1);

            Iterator<String> keys = conditions.keySet().iterator();

            while (keys.hasNext())
            {
                String c_key = keys.next();// key
                String c_value = conditions.get(c_key);// 上面key对应的value
                condition_keys.add(Search.mk_condition_key(type, c_key, c_value));
            }

            words.addAll(condition_keys);
        }

        if (null == words || words.isEmpty())
            return result;
        String temp_store_key = "tmpinterstore:" + StringUtils.join(words, "+");

        if (words.size() > 0)
        {
            if (!jedis.exists(temp_store_key))
            {
                jedis.sinterstore(temp_store_key, (String[]) words.toArray());
                jedis.expire(temp_store_key, 86400);
            }

            // 搜索拼音
            if (Search.config.pinyin_match)
            {
                List<String> pinyin_words = new ArrayList<String>();
                List<String> pinyin_words_temp = Search.split_pinyin(text);

                for (String w : pinyin_words_temp)
                {
                    pinyin_words.add(Search.mk_sets_key(type, w));
                }

                pinyin_words.addAll(condition_keys);

                String temp_sunion_key = "tmpsunionstore:" + StringUtils.join(pinyin_words, "+");
                String temp_pinyin_store_key = StringUtils.EMPTY;
                if (Search.config.pinyin_match)
                    temp_pinyin_store_key = "tmpinterstore:" + StringUtils.join(pinyin_words, "+");
                // 找出拼音的
                jedis.sinterstore(temp_pinyin_store_key, (String[]) pinyin_words.toArray());

                String[] arr_temp =
                { temp_store_key, temp_pinyin_store_key };

                // 合并中文和拼音的搜索结果
                jedis.sunionstore(temp_sunion_key, arr_temp);
                // 将临时搜索设为1天后自动清楚
                jedis.expire(temp_pinyin_store_key, 86400);
                jedis.expire(temp_sunion_key, 86400);
                temp_store_key = temp_sunion_key;
            }
        }
        else
        {
            // if (words.size() == 1) {
            // temp_store_key = words.first()
            // }
            return result;
        }

        SortingParams sort_params = new SortingParams().limit(0, limit).by(Search.mk_score_key(type, "*")).desc();
        List<String> ids = jedis.sort(temp_store_key, sort_params);
        return hmget(jedis, type, ids, null);
    }

    public static String mk_sets_key(String type, String key)
    {
        return type + ":" + key.toLowerCase();
    }

    public static String mk_score_key(String type, String id)
    {
        return type + ":_score_:" + id;
    }

    public static String mk_condition_key(String type, String field, String id)
    {
        return type + ":_by:_" + field + ":" + id;
    }

    public static String mk_complete_key(String type)
    {
        return "Compl" + type;
    }

    /**
     * 
     * @param jedis
     * @param type
     * @param ids
     * @param options
     * @return
     */
    public static List<String> hmget(Jedis jedis, String type, List<String> ids, LinkedHashMap options)
    {
        List<String> result = new ArrayList<String>();
        String sort_field = (String) (options.get("sort_field") == null ? "id" : options.get("sort_field"));
        if (null == ids || ids.isEmpty())
        {
            return result;
        }
        try
        {
            String[] ids_params = new String[ids.size()];
            for (int j = 0; j < ids.size(); j++)
            {
                ids_params[j] = ids.get(j);
            }

            List<String> list = jedis.hmget(type, ids_params);

            for (String r : list)
            {
                Gson gson = new Gson();
                result.add(gson.toJson(r));
            }

            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 分词
     * @param text
     * @return
     * @throws IOException
     */
    public static List<String> _split(String text) throws IOException
    {
        if (Search.config.disable_mmseg)
            return Arrays.asList(text.split(" "));
        Seg seg = new ComplexSeg(dic);
        MMSeg mmSeg = new MMSeg(new StringReader(text), seg);
        List<String> words = new ArrayList<String>();
        Word word = null;
        while ((word = mmSeg.next()) != null)
        {
            words.add(word.toString());
        }
        return words;
    }

    /**
     * 转拼音
     * @param text
     * @return
     * @throws Exception
     */
    public static List<String> split_pinyin(String text) throws Exception
    {
        return _split(PinyinUtil.getHanyuPinyin(text));
    }
    
    /**
     * use mmseg 分词
     * @param text
     * @return
     * @throws IOException
     */
    public static List<String> split(String text) throws IOException
    {
        return _split(text);
    }
}