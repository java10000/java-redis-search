package com.zp.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.zp.index.IndexWriter;
import com.zp.index.Suggest;
import com.zp.instance.JedisHolder;
import com.zp.search.IndexSearch;
import com.zp.search.SuggestSearch;



public class Search4jDemo
{
    //线程池的容量
    private static final int POOL_SIZE = 30;
    //线程池
    private static final int corePoolSize = 2;
    private static final int maxPoolSize = 15;
    private static final int keepAliveTime = 10;
    private static final int workQueue = 20;
    
    /** 线程池 */
    private static ThreadPoolExecutor exec = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
                    keepAliveTime, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(workQueue),
                    new ThreadPoolExecutor.CallerRunsPolicy());
    
    public static JedisPool jp = null;

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
        jp = holder.getJedisPoolInstance("localhost");
        // Jedis jedis=jp.getResource();
    }

    public static void addIndex()
    {
        Jedis jedis = jp.getResource();
        // jedis.select(3);
        IndexWriter iw = new IndexWriter(jedis);

        // addIdAndIndexItem(id,"切分后的字符串，中间以“|”分隔");
        iw.addIdAndIndexItem("1", "Ruby|on|Rails|为什么|什么|如此|高效");
        iw.addNeedSortItem("price", "23.9");// 需要排序的item
        iw.addNeedSortItem("date", "2012");
        iw.addNeedSortItem("author", "Klein");
        iw.writer();

        iw = new IndexWriter(jedis);

        iw.addIdAndIndexItem("2", "Ruby|编程|入门|应该|看|什么");
        iw.addNeedSortItem("price", "12.9");
        iw.addNeedSortItem("date", "2011");
        iw.addNeedSortItem("author", "Kevin");
        iw.writer();

        iw = new IndexWriter(jedis);

        iw.addIdAndIndexItem("3", "Ruby|和|Python|什么|那个|更好");
        iw.addNeedSortItem("price", "34.9");
        iw.addNeedSortItem("date", "2009");
        iw.addNeedSortItem("author", "Ben");
        iw.writer();

        iw = new IndexWriter(jedis);

        iw.addIdAndIndexItem("4", "做|Rubies|开发|应该|用|什么|开发|工具|比较好");
        iw.addNeedSortItem("price", "24.9");
        iw.addNeedSortItem("date", "2012");
        iw.addNeedSortItem("author", "Good");
        iw.writer();

        IndexSearch is = new IndexSearch(jedis);
        System.out.println(is.search("Ruby", "什么"));
        System.out.println(is.search("price", IndexSearch.DESC, "Ruby", "什么"));
        jp.returnResource(jedis);// 将jedis放回pool中
        // redis.disconnect();
    }

    public static void addSuggest()
    {   
        BufferedReader reader = null;
        Jedis jedis = jp.getResource();
        Suggest s = new Suggest(jedis);

        try
        {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream resourceAsStream = loader.getResourceAsStream("userLibrary.dic");       
            InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null)
            {
                // 显示行号
                System.out.println("line " + line + ": " + tempString);
                s.write(tempString);
                line++;
            }
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e1)
                {
                }
            }
            
            jp.returnResource(jedis);// 将jedis放回pool中
        }
    }

    public static void suggest(String key)
    {   
        Jedis jedis = jp.getResource();
        
        long beginTime = System.currentTimeMillis();
        SuggestSearch ss = new SuggestSearch(jedis);
        List<String> list = ss.search(key);
        System.err.println(StringUtils.join(list, "\n"));
        long endTime = System.currentTimeMillis();
        System.err.println("\n耗时："+(endTime-beginTime)+" ms");
        
        jp.returnResource(jedis);// 将jedis放回pool中
    }
    
 
    public static void mutliTest(final String key)
    {
        exec.execute(
           new Thread() {
               public void run() {
                    try
                    {
                        suggest(key);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
               }
           }
       );
    }
    
    
    
    public static void main(String[] args)
    {
        //addSuggest();
        for (int i = 0; i < 10000; i++)
        {
            System.err.println("---------"+i+"---------");
            exec.execute(
                new Thread() {
                    public void run() {
                         try
                         {
                             suggest("项目");
                         }
                         catch (Exception e)
                         {
                             e.printStackTrace();
                         }
                    }
                }
            );
        }
    }
    
    
}
