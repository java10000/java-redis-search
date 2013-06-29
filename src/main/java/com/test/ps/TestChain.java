package com.test.ps;

public class TestChain
{

    public static void main(String[] args)
    {

        String pass1 = "123456";

        String pass2 = "123456";

        String personId = "123456789012345678";

        String email = "chmask@163.com";

        register(pass1, pass2, personId, email);

    }

    public static void register(String pass1, String pass2, String personId, String email)
    {

        Filter f1 = new PasswordFilter1();

        Filter f2 = new PasswordFilter2();

        Filter f3 = new PersonIdFilter();

        Filter f4 = new EmailFilter();

        f1.setNext(f2);

        f2.setNext(f3);

        f3.setNext(f4);

        System.out.println(f1.doFilter(pass1, pass2, personId, email));

    }

}

abstract class Filter
{

    Filter next = null;

    public Filter getNext()
    {

        return next;

    }

    public void setNext(Filter next)
    {

        this.next = next;

    }

    public String doFilter(String pass1, String pass2, String personId, String email)
    {

        if (next == null)
            return "成功";

        else
            return next.doFilter(pass1, pass2, personId, email);

    }

}

class PasswordFilter1 extends Filter
{

    public String doFilter(String pass1, String pass2, String personId, String email)
    {

        if (!(pass1.equals(pass2)))

            return "两次密码输入不一致";

        else
            return super.doFilter(pass1, pass2, personId, email);

    }

}

class PasswordFilter2 extends Filter
{

    public String doFilter(String pass1, String pass2, String personId, String email)
    {

        if (pass1.length() != 6)

            return "密码长度必须为6";

        else
            return super.doFilter(pass1, pass2, personId, email);

    }

}

class PersonIdFilter extends Filter
{

    public String doFilter(String pass1, String pass2, String personId, String email)
    {

        if (personId.length() != 15 && personId.length() != 18)

            return "身份证号码非法";

        else
            return super.doFilter(pass1, pass2, personId, email);

    }

}

class EmailFilter extends Filter
{

    public String doFilter(String pass1, String pass2, String personId, String email)
    {

        int i1 = email.indexOf("@");

        int i2 = email.indexOf(".");

        if (i1 == -1 || i2 == -1 || i2 - i1 <= 1 || i1 == 0 || i2 == email.length() - 1)

            return "email非法";

        else
            return super.doFilter(pass1, pass2, personId, email);

    }

}