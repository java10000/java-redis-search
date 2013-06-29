package com.test.ps;

public class TestTemplateMethod
{

    public static void main(String[] args)
    {

        XiaoPin xp = new DaPuKe();

        xp.act();

    }

}

abstract class XiaoPin
{

    public abstract void jiaoLiu();

    public abstract void xuShi();

    public abstract void gaoXiao();

    public abstract void shanQing();

    public final void act()
    {

        jiaoLiu();

        xuShi();

        gaoXiao();

        shanQing();

    }

}

class DaPuKe extends XiaoPin
{

    public void jiaoLiu()
    {

        System.out.println("顺口溜");

    }

    public void xuShi()
    {

        System.out.println("火车除夕，老同学见面");

    }

    public void gaoXiao()
    {

        System.out.println("名片当作扑克");

    }

    public void shanQing()
    {

        System.out.println("马家军");

    }

}