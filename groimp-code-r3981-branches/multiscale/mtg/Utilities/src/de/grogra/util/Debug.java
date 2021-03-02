package de.grogra.util;

/**
 * This helper class is used to control the debugging facilities.
 * In each class that contains additional debug code, a flag is set
 * by code similar to this one:
 * 
 * private static final boolean DEBUG = Debug.debug("GLDisplay");
 * 
 * Making the flag private, static and final allows the javavm to
 * perform some optimizations at runtime. For instance consider a
 * statement like this one:
 * 
 * if (DEBUG) {
 *   <some debug code>
 * }
 * 
 * The java compiler will generate bytecode for this statement and
 * the code contained therein, but at runtime this code may be
 * optimized away by the executing javavm, if the flag DEBUG was
 * evaluated to false.
 * 
 * By having each class query the debug state via the same function
 * Debug.debug(), debugging of those classes can be easily enabled.
 * 
 * The function debug() will look at the defined system properties
 * for a property with the name "groimp.debug.<name>", where <name>
 * is the string passed to the debug() function. If such a property
 * is defined, debugging is enabled for that name and debug() returns
 * true.
 * 
 * @author Reinhard Hemmerling
 *
 */
final public class Debug
{
	public static final String DEBUG_PREFIX = "groimp.debug.";
	
	/**
	 * Returns true if debugging for that name was enabled.
	 * @param name symbol that is checked for debugging
	 * @return true if debugging was enabled for that symbol
	 */
	public static final boolean debug(String name)
	{
//		return Boolean.getBoolean(DEBUG_PREFIX+name);
		return System.getProperty(DEBUG_PREFIX+name) != null;
	}
}
