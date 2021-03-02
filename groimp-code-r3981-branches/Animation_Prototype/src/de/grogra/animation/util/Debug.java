package de.grogra.animation.util;

import java.io.PrintStream;

public class Debug {

	private static PrintStream out = System.out;
	
	public static void println(Object o) {
		out.println(o.toString());
	}
	
	public static void setPrintStream(PrintStream out) {
		Debug.out = out;
	}
	
}
