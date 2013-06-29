package com.test.ps;

import java.io.*;

public class TestClonealbe
{

    public static void main(String[] args) throws Exception
    {

        Father f = new Father();

        User u1 = new User("123456", f);

        User u2 = (User) u1.clone();

        System.out.println(u1 == u2);

        System.out.println(u1.f == u2.f);

    }

}

class User implements Cloneable, Serializable
{

    String password;

    Father f;

    public User(String password, Father f)
    {

        this.password = password;

        this.f = f;

    }

    public Object clone() throws CloneNotSupportedException
    {

        // return super.clone();

        ObjectOutputStream out = null;

        ObjectInputStream in = null;

        try
        {

            ByteArrayOutputStream bo = new ByteArrayOutputStream();

            out = new ObjectOutputStream(bo);

            out.writeObject(this);

            out.flush();

            byte[] bs = bo.toByteArray();

            ByteArrayInputStream bi = new ByteArrayInputStream(bs);

            in = new ObjectInputStream(bi);

            Object o = in.readObject();

            return o;

        }
        catch (IOException e)
        {

            e.printStackTrace();

            return null;

        }
        catch (ClassNotFoundException e)
        {

            e.printStackTrace();

            return null;

        }

        finally
        {

            try
            {

                out.close();

                in.close();

            }
            catch (IOException e)
            {

                e.printStackTrace();

            }

        }

    }

}

class Father implements Serializable
{
}