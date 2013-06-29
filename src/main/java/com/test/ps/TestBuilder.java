package com.test.ps;

public class TestBuilder
{

    public static void main(String[] args)
    {

        Builder b = new BuilderImpl1();

        Director d = new Director(b);

        Product p = d.createProduct();

    }

}

interface Builder
{

    void buildPart1();

    void buildPart2();

    void buildPart3();

    Product getProduct();

}

class BuilderImpl1 implements Builder
{

    public void buildPart1()
    {

        System.out.println("create part1");

    }

    public void buildPart2()
    {

        System.out.println("create part2");

    }

    public void buildPart3()
    {

        System.out.println("create part3");

    }

    public Product getProduct()
    {

        return new Product();

    }

}

class Director
{

    Builder b;

    public Director(Builder b)
    {

        this.b = b;

    }

    public Product createProduct()
    {

        b.buildPart1();
        b.buildPart2();

        b.buildPart3();

        return b.getProduct();

    }

}

class Product
{
}
