package com.aceona.redis.search;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.chenlb.mmseg4j.Dictionary;
import com.zp.instance.JedisHolder;

import redis.clients.jedis.JedisPool;

public class SearchConfig
{   
    public static Integer complete_max_length;
    
    public static Boolean pinyin_match;
    
    public static Boolean use_mmseg;
    
    public static JedisPool redisPool = null;
    
    public static String REDIS_SERVER_IP;
    
    public static Integer REDIS_SERVER_PORT;
    
    public static int REDIS_CONNECT_TIMEOUT;

    public static int POOL_MAXACTIVIE;

    public static int POOL_MAXIDLE;

    public static int POOL_MINIDLE;

    public static int POOL_MAXWAIT;

    public static boolean POOL_TESTONBORROW;

    public static boolean POOL_TESTONRETURN;

    public static boolean POOL_TESTWHILEIDLE;

    public static int POOL_MINEVICTABLEIDLETIMEMILLIS;

    public static int POOL_TIMEBETWEENEVICTIONRUNSMILLIS;

    public static int POOL_NUMTESTSPEREVICTIONRUN;

//    public static boolean AUTOCOMPLETE_USE_PINYIN;
//    
//    public static boolean AUTOCOMPLETE_USE_MMSEG;
//
//    public static int AUTOCOMPLETE_MAX_RESULT;

    public static String AUTOCOMPLETE_ACKEY;

    public static String AUTOCOMPLETE_SUFFIX_SYMBOLE = "*";

    private static Properties prop;

    private static FileInputStream inputStream;

    private static String CONFIG_FILE = Thread.currentThread().getContextClassLoader().getResource("redis_search_config.properties").getPath();
  
    public static Dictionary dic;

    static
    {
        prop = new Properties();
        try
        {
            /******** 读取prop配置 ********/
            inputStream = new FileInputStream(CONFIG_FILE);
            prop.load(inputStream);
            inputStream.close();
            inputStream = null;

            /***** 分词基本配置 ****/
            pinyin_match = Boolean.parseBoolean(prop.getProperty("usePinyin"));
            use_mmseg = Boolean.parseBoolean(prop.getProperty("useMmseg"));
            complete_max_length = Integer.parseInt(prop.getProperty("maxRusult"));
            
            /**** 初始化 redis 连接池 ****/
            REDIS_SERVER_IP = prop.getProperty("redisServerIp");
            REDIS_SERVER_PORT = Integer.parseInt(prop.getProperty("redisServerPort"));
            REDIS_CONNECT_TIMEOUT = Integer.parseInt(prop.getProperty("timeout"));
            POOL_MAXACTIVIE = Integer.parseInt(prop.getProperty("maxActive"));
            POOL_MAXIDLE = Integer.parseInt(prop.getProperty("maxIdle"));
            POOL_MINIDLE = Integer.parseInt(prop.getProperty("minIdle"));
            POOL_MAXWAIT = Integer.parseInt(prop.getProperty("maxWait"));

            POOL_TESTONBORROW = Boolean.parseBoolean(prop.getProperty("testOnBorrow"));
            POOL_TESTONRETURN = Boolean.parseBoolean(prop.getProperty("testOnReturn"));
            POOL_TESTWHILEIDLE = Boolean.parseBoolean(prop.getProperty("testWhileIdle"));

            POOL_MINEVICTABLEIDLETIMEMILLIS = Integer.parseInt(prop.getProperty("minEvictableIdleTimeMillis"));
            POOL_TIMEBETWEENEVICTIONRUNSMILLIS = Integer.parseInt(prop.getProperty("timeBetweenEvictionRunsMillis"));
            POOL_NUMTESTSPEREVICTIONRUN = Integer.parseInt(prop.getProperty("numTestsPerEvictionRun"));

            JedisHolder holder = JedisHolder.singleton();
            redisPool = holder.getJedisPoolInstance(REDIS_SERVER_IP, REDIS_SERVER_PORT);
            
            if (use_mmseg)
            {
                dic = Dictionary.getInstance();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
