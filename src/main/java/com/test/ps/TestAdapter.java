package com.test.ps;

public class TestAdapter
{

    public static void main(String[] args)
    {

        USB mouse = new Mouse();

        PC pc = new PC();

        // pc.useMouse(mouse);

        PS2 adapter = new USB2PS2Adapter(mouse);

        pc.useMouse(adapter);

    }

}

interface PS2
{

    void usePs2();

}

interface USB
{

    void useUsb();

}

class Mouse implements USB
{

    public void useUsb()
    {

        System.out.println("通过USB接口工作");

    }

}

class PC
{

    public void useMouse(PS2 ps2Mouse)
    {

        ps2Mouse.usePs2();

    }

}

class USB2PS2Adapter implements PS2
{

    private USB usb;

    public USB2PS2Adapter(USB usb)
    {

        this.usb = usb;

    }

    public void usePs2()
    {

        System.out.println("把对usePS2的方法调用转换成对useUSB的方法调用");

        usb.useUsb();

    }

}