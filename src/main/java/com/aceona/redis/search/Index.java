package com.aceona.redis.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.Jedis;

import com.google.gson.Gson;

public class Index
{
   public String type;

   public String title;

   public String id;

   public String score;

   public Set<String> aliases ;

   public Map<String, String> exts;

   public List<String> condition_fields;

   public Boolean prefix_index_enable = true;

   public Jedis jedis;

    public Index(Map options)
    {
        this.condition_fields = (List<String>) (options.get("condition_fields")== null ? new ArrayList<String>() : options.get("condition_fields"));
        this.exts = (Map<String, String>) (options.get("exts") == null ? new HashMap<String, String>() : options.get("exts"));
        this.aliases = (Set<String>) (options.get("aliases") == null ? new HashSet<String>() : options.get("aliases"));
        this.prefix_index_enable = (Boolean)(options.get("prefix_index_enable") == null ? true : options.get("prefix_index_enable"));
        this.type = (String) options.get("type");
        this.title = (String) options.get("title");
        this.id = (String) options.get("id");
        this.score = (String) options.get("score");
        this.aliases.add(this.title);

        jedis = SearchConfig.redisPool.getResource();
    }

//    public Index(String type, String id, String title, String score, Map<String, String>  exts ,
//                                    List<String> condition_fields, boolean prefix_index_enable)
//    {
//        this.condition_fields = condition_fields;
//        this.prefix_index_enable = prefix_index_enable;
//        this.type = type;
//        this.title = title;
//        this.id = id;
//        this.score = score;
//        this.aliases.add(this.title);
//
//        Search.configure();
//        jedis = SearchConfig.redisPool.getResource();
//    }
    
    /**
     * 保存
     * @throws Exception
     */
    public void save() throws Exception
    {
        if (StringUtils.isBlank(title))
            return;

        Map<String, String> data = new HashMap<String, String>();
        data.put("title", this.title);
        data.put("id", this.id);
        data.put("type", this.type);
        data.putAll(exts);

        Gson gson = new Gson();

        jedis.hset(this.type, this.id, gson.toJson(data).toString());

        for (String field : condition_fields)
        {
            jedis.sadd(Search.mk_condition_key(this.type, field, data.get(field.toString())), this.id);
        }

        // score for search set
        jedis.set(Search.mk_score_key(this.type, this.id), StringUtils.isNotBlank(this.score) ? this.score : "0");

        // save set index
        for (String val : aliases)
        {
            Set<String> words = split_words_for_index(val);
            if (null == words || words.size() == 0)
                return;

            for (String word : words)
            {
                jedis.sadd(Search.mk_sets_key(this.type, word), this.id);
            }
        }

        if (this.prefix_index_enable)
            save_prefix_index();
    }

    /**
     * 删除
     * @param options
     * @throws Exception
     */
    public static void remove(Map options) throws Exception
    {
        String type = (String) options.get("type");
        Jedis jedis = SearchConfig.redisPool.getResource();
        jedis.hdel(type, (String) options.get("id"));
        Set<String> words = split_words_for_index((String) options.get("title"));
        for (String word : words)
        {
            jedis.srem(Search.mk_sets_key(type, word), (String) options.get("id"));
            jedis.del(Search.mk_score_key(type, (String) options.get("id")));
        }
        jedis.srem(Search.mk_sets_key(type, (String) options.get("title")), (String) options.get("id"));
    }

    /**
     * 分词
     * @param title
     * @return
     * @throws Exception
     */
    public static Set<String> split_words_for_index(String title) throws Exception
    {
        List<String> words = Search.split(title);
        if (SearchConfig.pinyin_match)
        {
            List<String> pinyin_full = Search.split_pinyin(title);
            StringBuffer pinyin_first = new StringBuffer();
            for (String it : pinyin_full)
            {
                pinyin_first.append(it.substring(0, 1));
            }

            words.addAll(pinyin_full);
            words.add(pinyin_first.toString());
        }
        return new HashSet<String>(words);
    }

    /**
     * 前缀
     * @throws Exception
     */
    private void save_prefix_index() throws Exception
    {
        for (String val : aliases)
        {
            List<String> words = new ArrayList<String>();
            words.add(val.toLowerCase());
            jedis.sadd(Search.mk_sets_key(this.type, val), this.id);
            if (SearchConfig.pinyin_match)
            {
                List<String> pinyin_full = Search.split_pinyin(val.toLowerCase());
                StringBuffer pinyin_first = new StringBuffer();
                for (String it : pinyin_full)
                {
                    pinyin_first.append(it.substring(0, 1));
                }

                String pinyin = StringUtils.join(pinyin_full, "");
                words.add(pinyin);
                words.add(pinyin_first.toString());
                jedis.sadd(Search.mk_sets_key(this.type, pinyin), this.id);
            }

            for (String word : words)
            {
                String key = Search.mk_complete_key(this.type);
                int len = word.length();
                for (int i = 0; i < len; i++)
                {
                    String prefix = word.substring(0, i);
                    jedis.zadd(key, 0, prefix);
                }

                jedis.zadd(key, 0, word + "*");
            }
        }
    }
}
