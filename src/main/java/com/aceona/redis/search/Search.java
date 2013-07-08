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
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.Word;
import com.sun.tools.jdi.LinkedHashMap;

/**
 * Created with IntelliJ IDEA. User: kxc Date: 13-4-27 Time: 下午2:30 To change
 * this template use File | Settings | File Templates.
 */
public class Search
{
    /**
     * 自动补全 
     * @param type
     * @param w
     * @param options
     * @return
     */
    public static List<String> complete(String type, String w, Map options)
    {
        Jedis jedis = SearchConfig.redisPool.getResource();
        int limit = (Integer) (options.get("limit") == null ?  10 : options.get("limit"));
        Map<String, String> conditions = (Map<String, String>) (options.get("conditions") == null ?
                new HashMap<String, String>()  : options.get("conditions"));
        if ((StringUtils.isBlank(w) && conditions.isEmpty()) || (StringUtils.isBlank(type)))
            return new ArrayList<String>();
        Set<String> prefix_matches = new HashSet<String>();

        int range_len = SearchConfig.complete_max_length;
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
                    if (!entry.substring(0, min_len).equals(prefix.substring(0, min_len)))
                    {
                        count = prefix_matches.size();
                        break;
                    }
                    if (entry.endsWith("*") && prefix_matches.size() != count)
                    {
                        prefix_matches.add(entry.substring(0, entry.length()-1));
                    }
                }

                if (start >= range.size())
                {
                    range = new ArrayList<String>();
                }
                else
                {
                    range = range.subList(start.intValue(), (int) Math.min(max_range ,range.size()));
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
        if (null != conditions && !conditions.isEmpty())
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
        if (words.size() > 1)
        {
            if (!jedis.exists(temp_store_key))
            {
                // 将多个词语组合对比，得到并集，并存入临时区域
                String[] arr = (String[])words.toArray(new String[words.size()]);
                jedis.sunionstore(temp_store_key, arr);
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

        if (null != condition_keys && !condition_keys.isEmpty())
        {
            if (null != words && !words.isEmpty())
            {
                condition_keys.add(temp_store_key);
            }
            temp_store_key = "tmpsinterstore:" + StringUtils.join(condition_keys, "+");
            if (!jedis.exists(temp_store_key))
            {
                jedis.sinterstore(temp_store_key, (String[])condition_keys.toArray(new String[condition_keys.size()]));
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
    public static List<String> query(String type, String text, Map options) throws Exception
    {
        Jedis jedis = SearchConfig.redisPool.getResource();
        List<String> result = new ArrayList<String>();
        int limit = (Integer) (options.get("limit") == null ?  10 : options.get("limit"));

        String sort_field = (String) (options.get("sort_field") == null ? "id":options.get("sort_field"));
        Map<String, String> conditions = (Map<String, String>) (options.get("conditions") == null ? 
                new HashMap<String, String>()  : options.get("conditions"));

        if ((StringUtils.isBlank(text)) && (null == conditions || conditions.isEmpty()))
            return new ArrayList<String>();

        List<String> words = Search.split(text);
        List<String> wordList = new ArrayList<String>();
        for (String w : words)
        {
            wordList.add(Search.mk_sets_key(type, w));
        }
        words.clear();
        words.addAll(wordList);
        
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
                String[] arr = (String[])words.toArray(new String[words.size()]);
                jedis.sinterstore(temp_store_key, arr);
                jedis.expire(temp_store_key, 86400);
            }

            // 搜索拼音
            if (SearchConfig.pinyin_match)
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
                if (SearchConfig.pinyin_match)
                    temp_pinyin_store_key = "tmpinterstore:" + StringUtils.join(pinyin_words, "+");
                // 找出拼音的
                jedis.sinterstore(temp_pinyin_store_key, (String[])pinyin_words.toArray(new String[pinyin_words.size()]));

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
            temp_store_key = words.get(0);
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
        //String sort_field = (String) (options.get("sort_field") == null ? "id" : options.get("sort_field"));
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
                result.add(r);
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
        if (!SearchConfig.use_mmseg)
            return Arrays.asList(text.split(" "));
        Seg seg = new ComplexSeg(SearchConfig.dic);
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