package com.test.ps;

import static java.lang.System.*;

public class TestState
{

    public static void main(String[] args)
    {

        BBSUser u = new BBSUser();

        u.setState(new GuestState());

        u.publish();

        u.setState(new NormalState());

        u.publish();

        u.setState(new BlockedState());

        u.publish();

        u.setState(new NewComerState());

        u.publish();

    }

}

class BBSUser
{

    private State state;

    public void setState(State state)
    {

        this.state = state;

    }

    public void publish()
    {

        state.action();

    }

}

abstract class State
{

    public abstract void action();

}

class GuestState extends State
{

    public void action()
    {

        out.println("您处在游客状态，请先登录");

    }

}

class NormalState extends State
{

    public void action()
    {

        out.println("您处在正常状态，文章发表成功");

    }

}

class BlockedState extends State
{

    public void action()
    {

        out.println("您处在被封状态，文章发表失败");

    }

}

class NewComerState extends State
{

    public void action()
    {

        out.println("您是新手，请先学习一下，3天后再来");

    }

}

//class StateFactory
//{
//
//    public static State createState(int i)
//    {
//
//        if (i == 1)
//            return new GuestState();
//
//        else
//            return new NormalState();
//
//    }
//
//}