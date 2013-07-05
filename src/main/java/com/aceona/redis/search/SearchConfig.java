package com.aceona.redis.search;

import com.zp.instance.JedisHolder;

import redis.clients.jedis.JedisPool;

public class SearchConfig
{
    public Boolean debug;
    public Integer complete_max_length;
    public Boolean pinyin_match;
    public Boolean disable_mmseg;
    public static JedisPool redisPool = null;

    static
    {
        JedisHolder holder = JedisHolder.singleton();
        // JedisPool jp=holder.getJedisPoolInstance(String host,int port)
        // JedisPool jp=holder.getJedisPoolInstance(String host,int port,int
        // timeout)
        // JedisPool jp=holder.getJedisPoolInstance(String host,int port,int
        // timeout,String password)
        // JedisPool jp=holder.getJedisPoolInstance(String host,int port,int
        // timeout,String password,int database)
        redisPool = holder.getJedisPoolInstance("localhost");
        // Jedis jedis=jp.getResource();
    }

    
    public SearchConfig()
    {
        this.debug = false;
        this.complete_max_length = 100;
        this.pinyin_match = false;
        this.disable_mmseg = false;
    }
}
