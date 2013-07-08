package com.aceona.redis.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aceona.redis.search.model.Article;
import com.alibaba.fastjson.JSON;

public class DemoTest
{
    public static void main(String[] args) throws Exception
    {
        List<String> words = Search._split("最主要的更动是：张无忌最后没有选定自己的配偶。");

        for (String w : words)
        {
            System.err.println(w);
        }
        
        
        Map map1 = new HashMap();       
        map1.put("type", "test");
        map1.put("id", "1");
        map1.put("title", "Redis");        
        Index i = new Index(map1);
        i.save();

        map1.put("type", "test");
        map1.put("id", "2");
        map1.put("title", "Redhat");   
        i = new Index(map1);
        i.save();

        
        map1.put("type", "test");
        map1.put("id", "3");
        map1.put("title", "张无忌最后没有选定自己的配偶");   
        map1.put("score", "3");  
        Map<String, String> exts = new HashMap<String, String>();
        exts.put("username", "jiedan");
        exts.put("email", "lxb429@gmail.com");
        map1.put("exts", exts);
        i = new Index(map1);
        i.save();

        
        map1.put("type", "test");
        map1.put("id", "4");
        map1.put("title", "Redis 是一个高性能的key-value数据库");   
        map1.put("score", "4");  
        exts = new HashMap<String, String>();
        exts.put("username", "jiedan");
        exts.put("email", "lxb429@gmail.com");
        map1.put("exts", exts);
        i = new Index(map1);
        i.save();


        Map options = new HashMap();
        List<String> arts = new ArrayList<String>();
        System.out.println("自动完成: r");
        arts = Search.complete("test", "r", options);

        for (String artJson : arts)
        {
            Article art = JSON.parseObject(artJson, Article.class);
            System.err.println(art.getId()+ "---"+art.getTitle());
        }
        
        System.out.println("自动完成: redi");
        arts = Search.complete("test", "redi", options);
        for (String artJson : arts)
        {
            Article art = JSON.parseObject(artJson, Article.class);
            System.err.println(art.getId()+ "---"+art.getTitle());
        }

        System.out.println("自动完成: 张");
        arts = Search.complete("test", "张", options);
        for (String artJson : arts)
        {
            Article art = JSON.parseObject(artJson, Article.class);
            System.err.println(art.getId()+ "---"+art.getTitle());
        }

        
        System.out.println("自动完成: 当给");
        arts = Search.complete("test", "当给", options);
        for (String artJson : arts)
        {
            Article art = JSON.parseObject(artJson, Article.class);
            System.err.println(art.getId()+ "---"+art.getTitle());
        }

        System.out.println("搜索: 张无忌");
        arts = Search.query("test", "张无忌", options);
        for (String artJson : arts)
        {
            Article art = JSON.parseObject(artJson, Article.class);
            System.err.println(art.getId()+ "---"+art.getTitle());
        }
        
        System.out.println("搜索: zhang");
        arts = Search.query("test", "zhang", options);
        for (String artJson : arts)
        {
            Article art = JSON.parseObject(artJson, Article.class);
            System.err.println(art.getId()+ "---"+art.getTitle());
        }


        System.out.println("搜索: Redis");
        arts = Search.query("test", "Redis", options);
        for (String artJson : arts)
        {
            Article art = JSON.parseObject(artJson, Article.class);
            System.err.println(art.getId()+ "---"+art.getTitle());
        }
    }
    
}
