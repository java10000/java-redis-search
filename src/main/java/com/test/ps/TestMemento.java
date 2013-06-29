package com.test.ps;

public class TestMemento
{

    public static void main(String[] args)
    {

        Originator ori = new Originator();

        Caretaker c = new Caretaker();

        ori.setState("State 1");

        IFMemento m = ori.createMemento();

        c.save(m);

        ori.setState("State 2");

        m = c.retrieve();

        ori.restore(m);

        System.out.println("Now State:" + ori.getState());

    }

}

class Originator
{

    String state;

    public void setState(String s)
    {

        state = s;

        System.out.println("State change to: " + s);

    }

    public String getState()
    {

        return this.state;

    }

    public IFMemento createMemento()
    {

        return new Memento(state);

    }

    public void restore(IFMemento m)
    {

        Memento mt = (Memento) m;

        this.state = mt.getState();

    }

    private class Memento implements IFMemento
    {

        private String state;

        public Memento(String s)
        {

            this.state = s;

        }

        public String getState()
        {

            return this.state;

        }

    }

}

class Caretaker
{

    private IFMemento m;

    public IFMemento retrieve()
    {

        return this.m;

    }

    public void save(IFMemento m)
    {

        this.m = m;

    }

}

interface IFMemento
{

}