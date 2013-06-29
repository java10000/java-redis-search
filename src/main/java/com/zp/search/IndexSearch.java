package com.zp.search;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;


public class IndexSearch
{
    public static int ALPHA=0;
    public static int DESC=1;
    public static int ASC=2;
    
    private Jedis jedis;
    private int limit=100;
    private String itemName=null;
    private int pager=0;
    
    
    public IndexSearch(Jedis jedis)
    {
	if(!jedis.isConnected())
	{
	    jedis.connect();
	}
	this.jedis=jedis;
	
    }
    
    private SortingParams getSP(String item,int sort)
    {
	SortingParams sp = new SortingParams();
	sp.limit(pager,limit);
	
	if(null==item||"".equals(item))
	{
	    switch(sort)
	    {
		case 1:
		    	sp.desc();
		    	break;
		case 2:
			sp.asc();
			break;
		case 0:
		default:
			sp.alpha();
			break;
	    }
	}
	else
	{
	    switch(sort)
	    {
		case 1:
		    	sp.by(itemName+":*").desc();
		    	break;
		case 2:
			sp.by(itemName+":*").asc();
			break;
		case 0:
		default:
			sp.by(itemName+":*").alpha();
			break;
	    }
	    
	}
	return sp;
    }
    private List<String> isearch(int sort,String...query)
    {
	jedis.sinterstore("tempKey", query);
	return jedis.sort("tempKey",this.getSP(itemName, sort));
    }
    public List<String> search(String...query)
    {
	return this.isearch(0, query);
    }
    public List<String> search(int sort,String...query)
    {
	return this.isearch(sort, query);
    }
    
    public List<String> search(String itemName,int sort,String...query)
    {
	this.itemName=itemName;
	return this.isearch(sort, query);
    }
    
    public List<String> search(String itemName,int sort,int limit,String...query)
    {
	this.itemName=itemName;
	this.limit=limit;
	return this.isearch(sort, query);
    }
    
    public List<String> search(String itemName,int sort,int pager,int limit,String...query)
    {
	this.itemName=itemName;
	this.limit=limit;
	this.pager=pager;
	return this.isearch(sort, query);
    }

}
