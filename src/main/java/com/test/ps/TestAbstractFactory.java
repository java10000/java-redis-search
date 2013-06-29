package com.test.ps;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

public class TestAbstractFactory
{

    public static void main(String[] args)
    {

        GUIFactory fact = new SwingFactory();

        Frame f = fact.getFrame();

        Component c1 = fact.getButton();

        Component c2 = fact.getTextField();

        f.setSize(500, 300);

        f.setLayout(new FlowLayout());

        f.add(c1);

        f.add(c2);

        f.setVisible(true);

        f.addWindowListener(new WindowAdapter()
        {

            public void windowClosing(WindowEvent e)
            {

                System.exit(0);

            }

        });

    }

}

abstract class GUIFactory
{

    public abstract Component getButton();

    public abstract Component getTextField();

    public abstract Frame getFrame();

}

class AWTFactory extends GUIFactory
{

    public Component getButton()
    {

        return new Button("AWT Button");

    }

    public Frame getFrame()
    {

        return new Frame("AWT Frame");

    }

    public Component getTextField()
    {

        return new TextField(20);

    }

}

class SwingFactory extends GUIFactory
{

    public Component getButton()
    {

        return new JButton("Swing Button");

    }

    public Frame getFrame()
    {

        return new JFrame("Swing Frame");

    }

    public Component getTextField()
    {

        return new JTextField(20);

    }

}