package com.zp.search;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;

import com.zp.instance.C;

public class SuggestSearch
{
    private int maxResult = C.AUTOCOMPLETE_MAX_RESULT * 10;

    private Jedis jedis;

    public SuggestSearch(Jedis jedis)
    {
        if (!jedis.isConnected())
        {
            jedis.connect();
        }
        this.jedis = jedis;

    }

    public List<String> search(String word)
    {
        List<String> fr = new ArrayList<String>();
        long t = jedis.zrank(C.AUTOCOMPLETE_ACKEY, word);
        Object[] set = jedis.zrange(C.AUTOCOMPLETE_ACKEY, t, t + maxResult).toArray();
        for (int i = 0; i < set.length; i++)
        {
            if (fr.size() >= C.AUTOCOMPLETE_MAX_RESULT)
            {
                return fr;
            }
            if (set[i].toString().endsWith(C.AUTOCOMPLETE_SUFFIX_SYMBOLE))
            {
                fr.add(set[i].toString().replaceAll("\\" + C.AUTOCOMPLETE_SUFFIX_SYMBOLE, ""));
            }
        }
        return fr;
    }
}
