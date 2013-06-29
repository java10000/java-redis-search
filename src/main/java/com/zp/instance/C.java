package com.zp.instance;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class C
{
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

    public static boolean AUTOCOMPLETE_USE_PINYIN;

    public static int AUTOCOMPLETE_MAX_RESULT;

    public static String AUTOCOMPLETE_ACKEY;

    public static String AUTOCOMPLETE_SUFFIX_SYMBOLE = "*";

    private static Properties prop;

    private static FileInputStream inputStream;

//    private static String CONFIG_FILE = System.getProperty("user.dir") + File.pathSeparator
//            + "redis_search_config.properties";
    
    private static String CONFIG_FILE = Thread.currentThread().getContextClassLoader().getResource("redis_search_config.properties").getPath();
    
    static
    {
        prop = new Properties();
        try
        {
            inputStream = new FileInputStream(CONFIG_FILE);
            prop.load(inputStream);
            inputStream.close();
            inputStream = null;

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

            AUTOCOMPLETE_USE_PINYIN = Boolean.parseBoolean(prop.getProperty("usePinyin"));
            AUTOCOMPLETE_MAX_RESULT = Integer.parseInt(prop.getProperty("maxRusult"));
            AUTOCOMPLETE_ACKEY = prop.getProperty("autoCompleteKey");
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void main(String[] args)
    {
        System.out.println(C.AUTOCOMPLETE_USE_PINYIN);

    }

}
