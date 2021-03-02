package net.sourceforge.retroweaver.runtime.java.util;

public class IllegalFormatConversionException extends IllegalFormatException {

	private char c;

	private Class<?> arg;

	public IllegalFormatConversionException(char c, Class<?> arg) {
		this.c = c;
		this.arg = arg;
	}

	public char getConversion() { return c; }

	public Class<?> getArgumentClass() { return arg; }

	public String getMessage() {
		return "illegal format for conversion: " + c + " argument class: " + arg;
	}

}
