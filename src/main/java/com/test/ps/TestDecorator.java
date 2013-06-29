package com.test.ps;

public class TestDecorator
{

    public static void main(String[] args)
    {

        Teacher t1 = new SimpleTeacher();

        Teacher t2 = new CppTeacher(t1);

        Teacher t3 = new JavaTeacher(t2);

        t3.teach();

        // t.teach();

    }

}

abstract class Teacher
{

    public abstract void teach();

}

class SimpleTeacher extends Teacher
{

    public void teach()
    {

        System.out.println("Good Good Study, Day Day Up");

    }

}

class JavaTeacher extends Teacher
{

    Teacher teacher;

    public JavaTeacher(Teacher t)
    {

        this.teacher = t;

    }

    public void teach()
    {

        teacher.teach();

        System.out.println("Teach Java");

    }

}

class CppTeacher extends Teacher
{

    Teacher teacher;

    public CppTeacher(Teacher t)
    {

        this.teacher = t;

    }

    public void teach()
    {

        teacher.teach();

        System.out.println("Teach C++");

    }

}