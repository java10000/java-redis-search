//package com.test.ps;
//
//public class TestIterator
//{
//
//    public static void main(String[] args)
//    {
//
//        Stack s = new Stack();
//
//        s.push("Liucy");
//
//        s.push("Huxz");
//
//        s.push("George");
//
//        LinkedList l = new LinkedList();
//
//        l.addFirst("Liucy");
//
//        l.addFirst("Huxz");
//
//        l.addFirst("George");
//
//        print(l.iterator());
//
//    }
//
//    public static void print(Itr it)
//    {
//
//        while (it.hasNext())
//        {
//
//            System.out.println(it.next());
//
//        }
//
//    }
//
//}
//
//interface Itr
//{
//
//    boolean hasNext();
//
//    Object next();
//
//}
//
//class Stack
//{
//
//    Object[] os = new Object[10];
//
//    int index = 0;
//
//    private void expand()
//    {
//
//        Object[] os2 = new Object[os.length * 2];
//
//        System.arraycopy(os, 0, os2, 0, os.length);
//
//        os = os2;
//
//    }
//
//    public void push(Object o)
//    {
//
//        if (index == os.length)
//            expand();
//
//        os[index] = o;
//
//        index++;
//
//    }
//
//    public Object pop()
//    {
//
//        index--;
//
//        Object o = os[index];
//
//        os[index] = null;
//
//        return o;
//
//    }
//
//    private class StackItr implements Itr
//    {
//
//        int cursor = 0;
//
//        public boolean hasNext(){
//
//            return boolean;//cursor;
//        }
//
//        public Object next()
//        {
//
//            return os[cursor++];
//
//        }
//
//    }
//
//    public Itr iterator()
//    {
//
//        return new StackItr();
//
//    }
//
//}
//
//class LinkedList
//{
//
//    private class Node
//    {
//
//        Object o;
//
//        Node next;
//
//        public Node(Object o)
//        {
//
//            this.o = o;
//
//        }
//
//        public void setNext(Node next)
//        {
//
//            this.next = next;
//
//        }
//
//        public Node getNext()
//        {
//
//            return this.next;
//
//        }
//
//    }
//
//    Node head;
//
//    public void addFirst(Object o)
//    {
//
//        Node n = new Node(o);
//
//        n.setNext(head);
//
//        head = n;
//
//    }
//
//    public Object removeFirst()
//    {
//
//        Node n = head;
//
//        head = head.getNext();
//
//        return n.o;
//
//    }
//
//    class LinkedListItr implements Itr
//    {
//
//        Node currentNode = head;
//
//        public boolean hasNext()
//        {
//
//            return this.currentNode != null;
//
//        }
//
//        public Object next()
//        {
//
//            Node n = currentNode;
//
//            currentNode = currentNode.getNext();
//
//            return n.o;
//
//        }
//
//    }
//
//    public Itr iterator()
//    {
//
//        return new LinkedListItr();
//
//    }
//
//}