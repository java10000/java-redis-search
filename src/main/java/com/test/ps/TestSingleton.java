package com.test.ps;

public class TestSingleton
{

    public static void main(String[] args)
    {

    }

}

class ClassA
{ // 饿汉式

    private static ClassA i = new ClassA();

    public static ClassA newInstance()
    {

        return i;

    }

    private ClassA()
    {
    }

}

class ClassB
{ // 懒汉式

    private static ClassB i = null;

    public static synchronized ClassB newInstance()
    {

        if (i == null)
            i = new ClassB();

        return i;

    }

    private ClassB()
    {
    }

}