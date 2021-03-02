package net.sourceforge.retroweaver.runtime.java.util;

import java.util.LinkedList;

public class LinkedList_ {

    private LinkedList_() {}
    

    public static <E> E poll (LinkedList<E> list)
    {
    	return list.isEmpty () ? null : list.removeFirst ();
    }

    public static <E> E remove (LinkedList<E> list)
    {
    	return list.removeFirst ();
    }

}
