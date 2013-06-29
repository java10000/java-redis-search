package com.zp.index;

import com.zp.instance.C;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import redis.clients.jedis.Jedis;

public class Suggest
{
    private Jedis jedis = null;

    private int autoscore = 1;

    private String cPinyin(String str)
    {
        String pinyinString = "";
        char[] arr = str.toCharArray();

        for (int i = 0; i < arr.length; i++)
        {
            char ch = arr[i];
            try
            {
                if (ch > 128)
                {
                    String[] hanyuArr = PinyinHelper.toHanyuPinyinStringArray(ch, getFormat());
                    if (null != hanyuArr && hanyuArr.length > 0)
                    {
                        pinyinString += PinyinHelper.toHanyuPinyinStringArray(ch, getFormat())[0];
                    }
                }
                else
                {
                    pinyinString += arr[i];
                }
            }
            catch (BadHanyuPinyinOutputFormatCombination e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return pinyinString;
    }

    private HanyuPinyinOutputFormat getFormat()
    {
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        return defaultFormat;
    }

    public Suggest(Jedis jedis)
    {
        if (!jedis.isConnected())
        {
            jedis.connect();
        }
        this.jedis = jedis;
    }

    public void build(String word, boolean flag)
    {
        for (int i = 1; i <= word.length(); i++)
        {
            jedis.zadd(C.AUTOCOMPLETE_ACKEY, this.autoscore, word.substring(0, i));
        }
        if (flag)
        {
            jedis.zadd(C.AUTOCOMPLETE_ACKEY, this.autoscore, word + C.AUTOCOMPLETE_SUFFIX_SYMBOLE);
        }
    }

    public void write(String word)
    {
        if (C.AUTOCOMPLETE_USE_PINYIN)
        {
            this.build(cPinyin(word), false);
            this.build(word, true);
        }
        else
        {
            this.build(word, true);
        }
    }
}
