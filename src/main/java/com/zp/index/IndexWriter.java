package com.zp.index;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;

public class IndexWriter
{
    private Jedis jedis;
    private String id;
    private Map<String,String> items=new HashMap<String,String>();
    private String contentItems[];
    
    public IndexWriter(Jedis jedis)
    {
	if(!jedis.isConnected())
	{
	    jedis.connect();
	}
        this.jedis = jedis;
    }
    
    /**
     * @param id 必须有
     * @param content 是分词程序切分后的内容，每个词中间必须用用“|”分隔，如：中国|中国人|2012*/
    public void addIdAndIndexItem(String id,String content)
    {
	this.id = id;
        contentItems=content.split("\\|");
    }

    
    public void addNeedSortItem(String name,String value)
    {
	items.put(name, value);
    }

    public void writer()
    {
	indexWriter();
	itemWriter();
    }

    private void indexWriter()
    {
	if(!id.equals("")&&contentItems.length!=0)
	{
	    for(int i=0;i<contentItems.length;i++)
	    {
		jedis.sadd(contentItems[i].trim(), id);
	    }
	}
	
    }
    private void itemWriter()
    {
	if(items.size()!=0)
	{
	    Iterator<Entry<String, String>> it = items.entrySet().iterator();
	    while(it.hasNext())
	    {
		Entry<String, String> entry =(Entry<String,String>) it.next();
		jedis.set(entry.getKey().toString()+":"+id, entry.getValue().toString());
	    }
	}
    }
}
