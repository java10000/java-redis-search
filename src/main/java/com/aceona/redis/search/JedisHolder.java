package com.aceona.redis.search;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/** 全局唯一线程池 */
public class JedisHolder
{
    private JedisPoolConfig jedisPoolConfig;

    private JedisPool jedisPool;

    private final ConcurrentMap<String, JedisPool> poolMap = new ConcurrentHashMap<String, JedisPool>();

    private static JedisHolder holder = new JedisHolder();

    private JedisHolder()
    {
        initJedisPoolConfig();
    }

    public static JedisHolder singleton()
    {
        return holder;
    }

    private void initJedisPoolConfig()
    {
        jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxActive(Constants.POOL_MAXACTIVIE);
        jedisPoolConfig.setMaxIdle(Constants.POOL_MAXIDLE);
        jedisPoolConfig.setMinIdle(Constants.POOL_MINIDLE);
        jedisPoolConfig.setMaxWait(Constants.POOL_MAXWAIT);

        jedisPoolConfig.setTestOnBorrow(Constants.POOL_TESTONBORROW);
        jedisPoolConfig.setTestOnReturn(Constants.POOL_TESTONRETURN);
        jedisPoolConfig.setTestWhileIdle(Constants.POOL_TESTWHILEIDLE);

        jedisPoolConfig.setNumTestsPerEvictionRun(Constants.POOL_NUMTESTSPEREVICTIONRUN);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(Constants.POOL_MINEVICTABLEIDLETIMEMILLIS);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(Constants.POOL_TIMEBETWEENEVICTIONRUNSMILLIS);
    }

    private JedisPool getJedisPool(String key, JedisPool jedisPool)
    {
        if (poolMap.get(key) == null)
        {
            poolMap.putIfAbsent(key, jedisPool);
        }
        return poolMap.get(key);
    }

    public JedisPool getJedisPoolInstance(String host)
    {
        return getJedisPoolInstance(host, Protocol.DEFAULT_PORT);
    }

    public JedisPool getJedisPoolInstance(String host, int port)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(host).append(port);
        jedisPool = new JedisPool(jedisPoolConfig, host, port);
        return this.getJedisPool(sb.toString(), jedisPool);
    }

    /** redis默认的超时时间是2秒 */
    public JedisPool getJedisPoolInstance(String host, int port, int timeout)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(host).append(port).append(timeout);
        jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);

        return this.getJedisPool(sb.toString(), jedisPool);
    }

    public JedisPool getJedisPoolInstance(String host, int port, int timeout, String password)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(host).append(port).append(timeout).append(password);
        jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
        return this.getJedisPool(sb.toString(), jedisPool);
    }

    public JedisPool getJedisPoolInstance(String host, int port, int timeout, String password, int database)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(host).append(port).append(timeout).append(password).append(database);
        jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database);

        return this.getJedisPool(sb.toString(), jedisPool);
    }

    /*
     * public Jedis getJedis() { return jedisPool.getResource(); } public
     * boolean release(Jedis jedis) { if(null!=jedis&& null!=jedisPool) {
     * jedisPool.returnResource(jedis); return true; } return false; }
     */

    public boolean destoryPool()
    {
        if (null != jedisPool)
        {
            jedisPool.destroy();
            return true;
        }
        return false;
    }

    public static void main(String args[])
    {
        JedisHolder holder = JedisHolder.singleton();
        JedisPool jp = holder.getJedisPoolInstance("localhost");

        JedisHolder holder1 = JedisHolder.singleton();
        JedisPool jp1 = holder1.getJedisPoolInstance("localhost");

    }
}
