
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.tree.TreeModel;

import org.xml.sax.SAXException;

import de.grogra.reflect.TypeLoader;
import de.grogra.xl.util.ClassLoaderObjectInputStream;

public final class Utils
{
	public static final int DEBUG = 1;

	public static final I18NBundle I18N = I18NBundle.getInstance (Utils.class);

/*!!
#foreach ($type in $types)
$pp.setType($type)

	public static final $type[] ${pp.TYPE}_0 = new $type[0];

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public static final boolean[] BOOLEAN_0 = new boolean[0];
// generated
// generated
// generated
	public static final byte[] BYTE_0 = new byte[0];
// generated
// generated
// generated
	public static final short[] SHORT_0 = new short[0];
// generated
// generated
// generated
	public static final char[] CHAR_0 = new char[0];
// generated
// generated
// generated
	public static final int[] INT_0 = new int[0];
// generated
// generated
// generated
	public static final long[] LONG_0 = new long[0];
// generated
// generated
// generated
	public static final float[] FLOAT_0 = new float[0];
// generated
// generated
// generated
	public static final double[] DOUBLE_0 = new double[0];
// generated
// generated
// generated
	public static final Object[] OBJECT_0 = new Object[0];
// generated
//!! *# End of generated code

	public static final String[] STRING_0 = new String[0];

	private static final Method nanoTimeMethod;

	private static final Color[] COLORS = new Color[512];
	

	static
	{
		Method m;
		try
		{
			m = System.class.getMethod ("nanoTime", new Class[0]);
		}
		catch (NoSuchMethodException e)
		{
			m = null;
		}
		nanoTimeMethod = m;
		for (int i = 0; i < 512; i++)
		{
			COLORS[i] = new Color (o2c ((i & 0700) >> 6), o2c ((i & 070) >> 3),
								   o2c (i & 7));
		}
	}


	public static long nanoTime ()
	{
		if (nanoTimeMethod == null)
		{
			return System.currentTimeMillis () * 1000000;
		}
		try
		{
			return ((Long) nanoTimeMethod.invoke (null, (Object[]) null)).longValue ();
		}
		catch (Exception e)
		{
			throw new AssertionError (e);
		}
	}
	
	
	public static boolean hasNanoTimeMethod ()
	{
		return nanoTimeMethod != null;
	}


	private static int o2c (int octDigit)
	{
		return (octDigit * 255) / 7;
	}


	public static Color getApproximateColor (int rgb)
	{
		return COLORS[((rgb >> 15) & 0700) + ((rgb >> 10) & 070) + ((rgb >> 5) & 7)];
	}


	public static int indexOfOne (int i)
	{
		switch (i & -i)
		{
			case 0:
				return -1;
/*!!
#foreach ($i in [0..31])
			case 1 << $i:
				return $i;
#end
!!*/
//!! #* Start of generated code
			case 1 << 0:
				return 0;
			case 1 << 1:
				return 1;
			case 1 << 2:
				return 2;
			case 1 << 3:
				return 3;
			case 1 << 4:
				return 4;
			case 1 << 5:
				return 5;
			case 1 << 6:
				return 6;
			case 1 << 7:
				return 7;
			case 1 << 8:
				return 8;
			case 1 << 9:
				return 9;
			case 1 << 10:
				return 10;
			case 1 << 11:
				return 11;
			case 1 << 12:
				return 12;
			case 1 << 13:
				return 13;
			case 1 << 14:
				return 14;
			case 1 << 15:
				return 15;
			case 1 << 16:
				return 16;
			case 1 << 17:
				return 17;
			case 1 << 18:
				return 18;
			case 1 << 19:
				return 19;
			case 1 << 20:
				return 20;
			case 1 << 21:
				return 21;
			case 1 << 22:
				return 22;
			case 1 << 23:
				return 23;
			case 1 << 24:
				return 24;
			case 1 << 25:
				return 25;
			case 1 << 26:
				return 26;
			case 1 << 27:
				return 27;
			case 1 << 28:
				return 28;
			case 1 << 29:
				return 29;
			case 1 << 30:
				return 30;
			case 1 << 31:
				return 31;
//!! *# End of generated code
			default:
				throw new AssertionError ();
		}
	}


	public static PrintWriter debug = new PrintWriter
		(new FilterOutputStream (System.err)
			{
				@Override
				public void write (int b) throws IOException
				{
					if (DEBUG != 0)
					{
						out.write (b);
					}
				}

				@Override
				public void write (byte[] b, int offset, int length)
					throws IOException
				{
					if (DEBUG != 0)
					{
						out.write (b, offset, length);
					}
				}

			}, true);


	public static URL getURLResource (String url)
	{
		URL u = ClassLoader.getSystemResource (url);
		if (u == null)
		{
			ClassLoader l = null; // !! Main.class.getClassLoader ();
			if (l != null)
			{
				u = l.getResource (url);
			}
		}
		return u;
	}


	public static InputStream getStreamResource (String url)
	{
		URL u = getURLResource (url);
		if (u == null)
		{
			return null;
		}
		try
		{
			return u.openStream ();
		}
		catch (IOException e)
		{
			return null;
		}
	}


	public static Object get (Map a, Object key, Object defaultValue)
	{
		return (a == null) ? defaultValue : a.get (key, defaultValue);
	}


/*!!
#foreach ($type in $types)
$pp.setType($type)

	public static $type get$pp.Type (Map a, Object key)
	{
		return get$pp.Type (a, key, $pp.null);
	}


	public static $type get$pp.Type (Map a, Object key, $type defaultValue)
	{
		Object o;
		return (a == null)
			|| ((o = a.get (key, Map.DEFAULT_VALUE)) == Map.DEFAULT_VALUE)
			? defaultValue
			: $pp.unwrap("o");
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public static boolean getBoolean (Map a, Object key)
	{
		return getBoolean (a, key, false);
	}
// generated
// generated
	public static boolean getBoolean (Map a, Object key, boolean defaultValue)
	{
		Object o;
		return (a == null)
			|| ((o = a.get (key, Map.DEFAULT_VALUE)) == Map.DEFAULT_VALUE)
			? defaultValue
			: (((Boolean) (o)).booleanValue ());
	}
// generated
// generated
// generated
	public static byte getByte (Map a, Object key)
	{
		return getByte (a, key, ((byte) 0));
	}
// generated
// generated
	public static byte getByte (Map a, Object key, byte defaultValue)
	{
		Object o;
		return (a == null)
			|| ((o = a.get (key, Map.DEFAULT_VALUE)) == Map.DEFAULT_VALUE)
			? defaultValue
			: (((Number) (o)).byteValue ());
	}
// generated
// generated
// generated
	public static short getShort (Map a, Object key)
	{
		return getShort (a, key, ((short) 0));
	}
// generated
// generated
	public static short getShort (Map a, Object key, short defaultValue)
	{
		Object o;
		return (a == null)
			|| ((o = a.get (key, Map.DEFAULT_VALUE)) == Map.DEFAULT_VALUE)
			? defaultValue
			: (((Number) (o)).shortValue ());
	}
// generated
// generated
// generated
	public static char getChar (Map a, Object key)
	{
		return getChar (a, key, ((char) 0));
	}
// generated
// generated
	public static char getChar (Map a, Object key, char defaultValue)
	{
		Object o;
		return (a == null)
			|| ((o = a.get (key, Map.DEFAULT_VALUE)) == Map.DEFAULT_VALUE)
			? defaultValue
			: (((Character) (o)).charValue ());
	}
// generated
// generated
// generated
	public static int getInt (Map a, Object key)
	{
		return getInt (a, key, (0));
	}
// generated
// generated
	public static int getInt (Map a, Object key, int defaultValue)
	{
		Object o;
		return (a == null)
			|| ((o = a.get (key, Map.DEFAULT_VALUE)) == Map.DEFAULT_VALUE)
			? defaultValue
			: (((Number) (o)).intValue ());
	}
// generated
// generated
// generated
	public static long getLong (Map a, Object key)
	{
		return getLong (a, key, (0));
	}
// generated
// generated
	public static long getLong (Map a, Object key, long defaultValue)
	{
		Object o;
		return (a == null)
			|| ((o = a.get (key, Map.DEFAULT_VALUE)) == Map.DEFAULT_VALUE)
			? defaultValue
			: (((Number) (o)).longValue ());
	}
// generated
// generated
// generated
	public static float getFloat (Map a, Object key)
	{
		return getFloat (a, key, (0));
	}
// generated
// generated
	public static float getFloat (Map a, Object key, float defaultValue)
	{
		Object o;
		return (a == null)
			|| ((o = a.get (key, Map.DEFAULT_VALUE)) == Map.DEFAULT_VALUE)
			? defaultValue
			: (((Number) (o)).floatValue ());
	}
// generated
// generated
// generated
	public static double getDouble (Map a, Object key)
	{
		return getDouble (a, key, (0));
	}
// generated
// generated
	public static double getDouble (Map a, Object key, double defaultValue)
	{
		Object o;
		return (a == null)
			|| ((o = a.get (key, Map.DEFAULT_VALUE)) == Map.DEFAULT_VALUE)
			? defaultValue
			: (((Number) (o)).doubleValue ());
	}
// generated
// generated
// generated
	public static Object getObject (Map a, Object key)
	{
		return getObject (a, key, null);
	}
// generated
// generated
	public static Object getObject (Map a, Object key, Object defaultValue)
	{
		Object o;
		return (a == null)
			|| ((o = a.get (key, Map.DEFAULT_VALUE)) == Map.DEFAULT_VALUE)
			? defaultValue
			: (o);
	}
// generated
//!! *# End of generated code

	public static String firstToUpperCase (String s)
	{
		if ((s == null) || (s.length () == 0))
		{
			return s;
		}
		return Character.toUpperCase (s.charAt (0)) + s.substring (1);
	}


	public static boolean equal (Object o1, Object o2)
	{
		return (o1 == o2) || ((o1 != null) && (o2 != null) && o1.equals (o2));
	}

	
	public static boolean contentEquals (CharSequence a, CharSequence b)
	{
		if (a == b)
		{
			return true;
		}
		if ((a == null) || (b == null))
		{
			return false;
		}
		if (a.equals (b))
		{
			return true;
		}
		int n = a.length ();
		if (n != b.length ())
		{
			return false;
		}
		while (--n >= 0)
		{
			if (a.charAt (n) != b.charAt (n))
			{
				return false;
			}
		}
		return true;
	}


	public static int getStringHashCode (CharSequence c, int begin, int end)
	{
		int hc = 0;
		for (int i = begin; i < end; i++)
		{
			hc = (hc << 5) - hc + c.charAt (i);
		}
        return hc;
	}


	public static int getStringHashCode (CharSequence c)
	{
		return (c != null) ? getStringHashCode (c, 0, c.length ()) : 0;
	}


	private Utils ()
	{
	}



	public static Throwable getUserException (Throwable t)
	{
		while (!(t instanceof UserException))
		{
			if (t == null)
			{
				return null;
			}
			t = t.getCause ();
		}
		return t;
	}


	public static Throwable unwrap (Throwable t)
	{
		while ((t.getCause () != null) && ((t instanceof WrapException)
										   || (t instanceof IOWrapException)))
		{
			t = t.getCause ();
		}
		return t;
	}


	public static Throwable unwrapFully (Throwable t)
	{
		while (t.getCause () != null)
		{
			t = t.getCause ();
		}
		return t;
	}


	public static Throwable getMainException (Throwable t)
	{
		if (t == null)
		{
			return null;
		}
		Throwable u = getUserException (t);
		return (u != null) ? u : unwrap (t);
	}


	public static Throwable initCauses (Throwable t)
	{
		if (t != null)
		{
			Throwable a = t;
			while (a != null)
			{
				Throwable c;
				if (a instanceof SAXException)
				{
					c = ((SAXException) a).getException ();
				}
				else
				{
					c = null;
				}
				if (c == null)
				{
					c = a.getCause ();
				}
				if ((c != null) && (a.getCause () == null))
				{
					try
					{
						a.initCause (c);
					}
					catch (Exception e)
					{
					}
				}
				a = c;
			}
		}
		return t;
	}


	public static String getStackTrace (Throwable t)
	{
		StringWriter s = new StringWriter ();
		PrintWriter p = new PrintWriter (s);
		t.printStackTrace (p);
		p.flush ();
		return s.toString ();
	}


	public static Object evaluate (String clsAndMember, Object[] args,
								   ClassLoader loader)
		throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException,
		InstantiationException, IllegalAccessException, InvocationTargetException
	{
		int i = clsAndMember.lastIndexOf ('.');
		if (Character.isUpperCase (clsAndMember.charAt (i + 1)))
		{
			int j = clsAndMember.lastIndexOf ('.', i - 1);
			if (!Character.isUpperCase (clsAndMember.charAt (j + 1)))
			{
				return newInstance (clsAndMember, args, loader);
			}
		}
		String c = clsAndMember.substring (0, i);
		String m = clsAndMember.substring (i + 1);
		if (Character.isUpperCase (m.charAt (0)))
		{
			return Class.forName (c, true, loader).getField (m).get (null);
		}
		else
		{
			return invokeStatic (c, m, args, loader);
		}
	}


	public static Object invoke (String clsAndMethod, Object[] args,
								 ClassLoader loader)
		throws ClassNotFoundException, NoSuchMethodException,
		InstantiationException, IllegalAccessException, InvocationTargetException
	{
		int i = clsAndMethod.lastIndexOf ('.');
		if (Character.isUpperCase (clsAndMethod.charAt (i + 1)))
		{
			return newInstance (clsAndMethod, args, loader);
		}
		else
		{
			return invokeStatic (clsAndMethod.substring (0, i),
								 clsAndMethod.substring (i + 1), args, loader);
		}
	}


	public static Object invokeStatic (String cls, String method, Object[] args,
									   ClassLoader loader)
		throws ClassNotFoundException, NoSuchMethodException,
		IllegalAccessException, InvocationTargetException
	{
		return invoke (Class.forName (cls, true, loader), method, null, args);
	}


	public static Object invokeStatic (Class cls, String method, Object[] args)
		throws NoSuchMethodException,
		IllegalAccessException, InvocationTargetException
	{
		return invoke (cls, method, null, args);
	}


	public static Object invokeVirtual (Object instance, String method,
										Object[] args)
		throws NoSuchMethodException,
		IllegalAccessException, InvocationTargetException
	{
		return invoke (instance.getClass (), method, instance, args);
	}


	private static Object invoke (Class cls, String method, Object object,
								  Object[] args)
		throws NoSuchMethodException,
		IllegalAccessException, InvocationTargetException
	{
		Method[] a = cls.getMethods ();
		for (int i = 0; i < a.length; i++)
		{
			if (a[i].getName ().equals (method)
				&& isApplicable (a[i].getParameterTypes (), args))
			{
				return a[i].invoke (object, args);
			}
		}
		throw new NoSuchMethodException (cls.getName () + '.' + method);
	}


	public static Object newInstance (String cls, Object[] args,
									  ClassLoader loader)
		throws ClassNotFoundException, NoSuchMethodException,
		InstantiationException,
		IllegalAccessException, InvocationTargetException
	{
		return newInstance (Class.forName (cls, true, loader), args);
	}


	public static Object newInstance (Class cls, Object[] args)
		throws NoSuchMethodException, InstantiationException,
		IllegalAccessException, InvocationTargetException
	{
		Constructor[] a = cls.getConstructors ();
		for (int i = 0; i < a.length; i++)
		{
			if (isApplicable (a[i].getParameterTypes (), args))
			{
				return a[i].newInstance (args);
			}
		}
		throw new NoSuchMethodException (cls.getName () + " " + Arrays.deepToString (args));
	}


	private static boolean isApplicable (Class[] c, Object[] args)
	{
		if (c.length != ((args != null) ? args.length : 0))
		{
			return false;
		}
		for (int j = 0; j < c.length; j++)
		{
			if (c[j].isPrimitive ())
			{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)

#if (!$pp.boolean)
				else
#end

				if (c[j] == ${type}.class)
				{
					if (!(args[j] instanceof $pp.wrapper))
					{
						return false;
					}
				}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
// generated
				if (c[j] == boolean.class)
				{
					if (!(args[j] instanceof Boolean))
					{
						return false;
					}
				}
// generated
// generated
				else
// generated
				if (c[j] == byte.class)
				{
					if (!(args[j] instanceof Byte))
					{
						return false;
					}
				}
// generated
// generated
				else
// generated
				if (c[j] == short.class)
				{
					if (!(args[j] instanceof Short))
					{
						return false;
					}
				}
// generated
// generated
				else
// generated
				if (c[j] == char.class)
				{
					if (!(args[j] instanceof Character))
					{
						return false;
					}
				}
// generated
// generated
				else
// generated
				if (c[j] == int.class)
				{
					if (!(args[j] instanceof Integer))
					{
						return false;
					}
				}
// generated
// generated
				else
// generated
				if (c[j] == long.class)
				{
					if (!(args[j] instanceof Long))
					{
						return false;
					}
				}
// generated
// generated
				else
// generated
				if (c[j] == float.class)
				{
					if (!(args[j] instanceof Float))
					{
						return false;
					}
				}
// generated
// generated
				else
// generated
				if (c[j] == double.class)
				{
					if (!(args[j] instanceof Double))
					{
						return false;
					}
				}
//!! *# End of generated code
			}
			else if ((args[j] != null) && !c[j].isInstance (args[j]))
			{
				return false;
			}
		}
		return true;
	}


	public static SAXException newSAXException (Exception cause)
	{
		SAXException e = new SAXException (cause);
		if (e.getCause () == null)
		{
			e.initCause (cause);
		}
		return e;
	}


	public static void rethrow (Throwable t)
	{
		if (t instanceof RuntimeException)
		{
			throw (RuntimeException) t;
		}
		else if (t instanceof Error)
		{
			throw (Error) t;
		}
		else if (t != null)
		{
			throw new WrapException (t);
		}
	}

	public static void dumpArray (Object array)
	{
		if (array == null)
		{
			System.err.println ((Object) null);
		}
		else
		{
			for (int i = 0; i < Array.getLength (array); i++)
			{
				System.err.println ("[" + i + "]=" + Array.get (array, i));
			}
		}
	}


	public static boolean isContained (Object value, Object[] array)
	{
		if (array != null)
		{
			for (int i = array.length - 1; i >= 0; i--)
			{
				if (equal (value, array[i]))
				{
					return true;
				}
			}
		}
		return false;
	}


	public static int hashCode (Object[] array)
	{
		if (array == null)
		{
			return 0;
		}
		int hashCode = 1;
		for (int i = 0; i < array.length; i++)
		{
			Object o;
			hashCode = 31 * hashCode
				+ (((o = array[i]) != null) ? o.hashCode () : 0);
		}
		return hashCode;
	}


	public static void dumpTree (TreeModel tree)
	{
		dumpTree (tree, tree.getRoot (), 0);
	}


	private static void dumpTree (TreeModel tree, Object node, int depth)
	{
		for (int i = 0; i < depth; i++)
		{
			System.err.print ("  ");
		}
		System.err.println (node);
		if ((node != null) && !tree.isLeaf (node))
		{
			for (int i = 0, n = tree.getChildCount (node); i < n; i++)
			{
				dumpTree (tree, tree.getChild (node, i), depth + 1);
			}
		}
	}


	public static String escape (CharSequence s, String special)
	{
		StringBuffer b = new StringBuffer (s.length () * 3 / 2);
		for (int i = 0; i < s.length (); i++)
		{
			char c = s.charAt (i);
			if (special.indexOf (c) >= 0)
			{
				b.append ('\\');
			}
			b.append (c);
		}
		return b.toString ();
	}


	public static void escapeForXML (StringBuffer b, int start)
	{
		escapeForXML (b, start, b.length ());
	}


	public static void escapeForXML (StringBuffer b, int start, int end)
	{
		for (int i = end - 1; i >= start; i--)
		{
			String ent;
			switch (b.charAt (i))
			{
				case '<':
					ent = "&lt;";
					break;
				case '>':
					ent = "&gt;";
					break;
				case '&':
					ent = "&amp;";
					break;
				default:
					ent = null;
					break;
			}
			if (ent != null)
			{
				b.replace (i, i + 1, ent);
			}
		}
	}


	public static File relativize (File base, File f)
	{
		if (!(base.isAbsolute () && f.isAbsolute ()))
		{
			System.out.println("On Windows systems, this problem can occur when your Java is running not on the same device as your home directory is located.");
			System.out.println("Try to tell Java where to find your home directory: '-Duser.home=<your path>'");
			throw new IllegalArgumentException ("Files must be absolute.\n Base("+base.isAbsolute ()+"): "+base+" \n File("+f.isAbsolute ()+"): "+f);
		}
		base = relativize0 (base, f);
		return (base == null) ? f : base;
	}


	private static File relativize0 (File base, File f)
	{
		File p = f.getParentFile ();
		if (p == null)
		{
			return null;
		}
		else if (base.equals (p))
		{
			return new File (f.getName ());
		}
		else
		{
			File a = relativize0 (base, p);
			return (a == null) ? null : new File (a, f.getName ());
		}
	}


	public static File resolve (File base, File f)
	{
		if (!base.isAbsolute ())
		{
			throw new IllegalArgumentException ("Base must be absolute: " +base);
		}
		if (f.isAbsolute ())
		{
			return f;
		}
		return resolve0 (base, f);
	}	


	private static File resolve0 (File base, File f)
	{
		if (f == null)
		{
			return base;
		}
		else
		{
			return new File (resolve0 (base, f.getParentFile ()), f.getName ());
		}
	}


	public static boolean isURL (String systemId)
	{
		try
		{
			new URL (systemId);
			return true;
		}
		catch (MalformedURLException e)
		{
			return false;
		}
	}


	public static URL fileToURL (File file)
	{
		try
		{
			return file.toURI ().toURL ();
		}
		catch (MalformedURLException e)
		{
			throw new AssertionError (e);
		}
	}


	public static File urlToFile (URL url)
	{
		return new File (URI.create (url.toString ()));
	}

	public static String quote (CharSequence s)
	{
		StringBuffer b = new StringBuffer (2 + s.length () * 3 / 2);
		quote (s, b);
		return b.toString ();
	}


	public static void quote (CharSequence s, StringBuffer out)
	{
		out.append ('"');
		for (int i = 0; i < s.length (); i++)
		{
			char c = s.charAt (i);
			if ((c == '"') || (c == '\\'))
			{
				out.append ('\\');
			}
			out.append (c);
		}
		out.append ('"');
	}


	public static String unquote (CharSequence s)
	{
		return unquote (s, 0, s.length (), null);
	}


	public static String unquote (CharSequence s, int start, int end,
								  int[] endOut)
	{
		if (start == end)
		{
			if (endOut != null)
			{
				endOut[0] = end;
			}
			return "";
		}
		else if (s.charAt (start) != '"')
		{
			if (end < 0)
			{
				throw new IllegalArgumentException ();
			}
			if (endOut != null)
			{
				endOut[0] = end;
			}
			return s.subSequence (start, end).toString ();
		}
		StringBuffer b = new StringBuffer (Math.max (0, end - start - 2));
		int i = start + 1;
		while (true)
		{
			char c = s.charAt (i++);
			if (i == end)
			{
				if (c != '"')
				{
					throw new IllegalArgumentException ();
				}
				if (endOut != null)
				{
					endOut[0] = end;
				}
				return b.toString ();
			}
			else if (c == '"')
			{
				if (end >= 0)
				{
					throw new IllegalArgumentException ();
				}
				if (endOut != null)
				{
					endOut[0] = i;
				}
				return b.toString ();
			}
			else if (c == '\\')
			{
				c = s.charAt (i++);
			}
			b.append (c);
		}
	}


	public static Dimension parseDimension (String s)
	{
		int[] p = new int[1];
		return new Dimension (parseInt (s, p, -1), parseInt (s, p, 'x'));
	}


	private static int parseInt (String s, int[] pos, int delim)
	{
		boolean lookForDigit = true;
		int n = 0;
		while (true)
		{
			char c = (pos[0] < s.length ()) ? s.charAt (pos[0]++) : 0;
			if (delim >= 0)
			{
				if (c == delim)
				{
					delim = -1;
				}
			}
			else if ((c >= '0') && (c <= '9'))
			{
				if (lookForDigit)
				{
					n = c - '0';
					lookForDigit = false;
				}
				else
				{
					n = 10 * n + c - '0';
				}
			}
			else
			{
				if (!lookForDigit)
				{
					pos[0]--;
					return n;
				}
			}
			if (c == 0)
			{
				throw new IllegalArgumentException (s);
			}
		}
	}


	public static Point parsePoint (String s)
	{
		int[] p = new int[1];
		return new Point (parseInt (s, p, -1), parseInt (s, p, -1));
	}


	public static Color parseColor (String s)
	{
		int[] p = new int[1];
		return new Color (parseInt (s, p, -1), parseInt (s, p, -1), parseInt (s, p, -1));
	}


	public static Rectangle parseRectangle (String s)
	{
		int[] p = new int[1];
		int w = parseInt (s, p, -1);
		int h = parseInt (s, p, 'x');
		return new Rectangle (parseInt (s, p, -1), parseInt (s, p, -1), w, h);
	}


	public static int parseFloatArray (String s, float[] a, String delim)
	{
		int p = 0, len = s.length (), n = 0;
		while (true)
		{
			while ((p < len) && (delim.indexOf (s.charAt (p)) >= 0))
			{
				p++;
			}
			if (p == len)
			{
				return n;
			}
			int i = p;
			while ((i < len) && (delim.indexOf (s.charAt (i)) < 0))
			{
				i++;
			}
			a[n++] = Float.parseFloat (s.substring (p, i));
			if (n == a.length)
			{
				return n;
			}
			p = i;
		}
	}


	public static void watchShutdown ()
	{
		System.err.println (new Date ());
		Thread sw = new Thread ("ShutdownWatcher")
		{
			@Override
			public void run ()
			{
				ThreadGroup g = getThreadGroup ();
				Thread[] active = null;
				int activeCount = 0;
				while (true)
				{
					Date d = new Date ();
					Thread[] a;
					int n = g.enumerate (a = new Thread[g.activeCount () * 2]);
					for (int i = 0; i < n; i++)
					{
						Thread t = a[i];
						int j;
						for (j = activeCount - 1; j >= 0; j--)
						{
							if (active[j] == t)
							{
								break;
							}
						}
						if (j < 0)
						{
							System.err.println ("Active Thread: " + t
												+ "  deamon=" + t.isDaemon ());
						}
					}
					for (int i = 0; i < activeCount; i++)
					{
						Thread t = active[i];
						int j;
						for (j = n - 1; j >= 0; j--)
						{
							if (a[j] == t)
							{
								break;
							}
						}
						if (j < 0)
						{
							System.err.println (d + ": Terminated: " + t);
						}
					}
					activeCount = n;
					active = a;
					try
					{
						Thread.sleep (50);
					}
					catch (InterruptedException e)
					{
						return;
					}
				}
			}
		};
		sw.setDaemon (true);
		sw.start ();
	}


	public static boolean isStringDescription (String type)
	{
		return type.equals (Described.NAME) || type.equals (Described.SHORT_DESCRIPTION)
			|| type.equals (Described.TITLE);
	}


	public static Object get (I18NBundle bundle, String key, String type,
							  Object defaultDescription)
	{
		if (bundle != null)
		{
			try
			{
				return bundle.getObject
					((key.length () > 0) ? key + '.' + type : type);
			}
			catch (MissingResourceException e)
			{
			}
		}
		if (isStringDescription (type))
		{
			if (bundle != null)
			{
				try
				{
					return bundle.getString (key);
				}
				catch (MissingResourceException e)
				{
				}
			}
			if (defaultDescription == null)
			{
				return key;
			}
		}
		return defaultDescription;
	}


	private static final char[] byteToBase64
		= {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		   'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		   'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		   'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		   '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

	private static final byte[] base64ToByte
		= {-1, -1, -1, -1, -1, -1, -1, -1,
		   -1, -1, -1, -1, -1, -1, -1, -1,
		   -1, -1, -1, -1, -1, -1, -1, -1,
		   -1, -1, -1, -1, -1, -1, -1, -1,
		   -1, -1, -1, -1, -1, -1, -1, -1,
		   -1, -1, -1, 62, -1, -1, -1, 63,
		   52, 53, 54, 55, 56, 57, 58, 59,
		   60, 61, -1, -1, -1, -1, -1, -1,
		   -1, 0, 1, 2, 3, 4, 5, 6,
		   7, 8, 9, 10, 11, 12, 13, 14,
		   15, 16, 17, 18, 19, 20, 21, 22,
		   23, 24, 25, -1, -1, -1, -1, -1,
		   -1, 26, 27, 28, 29, 30, 31, 32,
		   33, 34, 35, 36, 37, 38, 39, 40,
		   41, 42, 43, 44, 45, 46, 47, 48,
		   49, 50, 51};


	private static byte base64ToByte (char c) throws IOException
	{
		if ((c >= base64ToByte.length) || (base64ToByte[c] < 0))
		{
			throw new IOException ("Illegal Base64-character " + c);
		}
		return base64ToByte[c];
	}


	public static void encodeBase64 (byte[] buf, int off, int len,
									 StringBuffer out)
	{
		int fullGroups = len / 3, partialBytes = len % 3;
		int p = off;
		int b;
		while (--fullGroups >= 0)
		{
			out.append (byteToBase64[(b = buf[p++] & 255) >> 2])
				.append (byteToBase64[((b & 3) << 4) | ((b = buf[p++] & 255) >>> 4)])
				.append (byteToBase64[((b & 15) << 2) | ((b = buf[p++] & 255) >>> 6)])
				.append (byteToBase64[b & 63]);
		}
		if (partialBytes != 0)
		{
			out.append (byteToBase64[(b = buf[p++] & 255) >>> 2]);
			if (partialBytes == 1)
			{
				out.append (byteToBase64[(b & 3) << 4]).append ("==");
			}
			else
			{
				out.append (byteToBase64[((b & 3) << 4) | ((b = buf[p++] & 255) >>> 4)])
					.append (byteToBase64[(b & 15) << 2]).append ('=');
			}
		}
		assert p == off + len;
	}
	
	
	public static int decodeBase64 (CharSequence in, int offset, int len,
									byte[] out) throws IOException
	{
		int numGroups = len >> 2;
		if (numGroups << 2 != len)
		{
	        throw new IOException ("String length must be a multiple of four.");
		}
		int missingBytesInLastGroup = 0;
		int numFullGroups = numGroups;
		if (len != 0)
		{
			if (in.charAt (offset + len - 1) == '=')
			{
				missingBytesInLastGroup++;
				numFullGroups--;
		        if (in.charAt (offset + len - 2) == '=')
		        {
		            missingBytesInLastGroup++;
		        }
			}
		}

		int q = 0;
		while (--numFullGroups >= 0)
		{
			byte t;
			out[q++] = (byte) ((base64ToByte (in.charAt (offset++)) << 2)
							   | ((t = base64ToByte (in.charAt (offset++))) >> 4));
			out[q++] = (byte) ((t << 4)
							   | ((t = base64ToByte (in.charAt (offset++))) >> 2));
			out[q++] = (byte) ((t << 6) | base64ToByte (in.charAt (offset++)));
		}
		
		if (missingBytesInLastGroup != 0)
		{
			byte t;
			out[q++] = (byte) ((base64ToByte (in.charAt (offset++)) << 2)
							   | ((t = base64ToByte (in.charAt (offset++))) >> 4));
			if (missingBytesInLastGroup == 1)
			{
				out[q++] = (byte) ((t << 4)
								   | (base64ToByte (in.charAt (offset)) >> 2));
			}
		}

		return 3 * numGroups - missingBytesInLastGroup;
	}


	public static void encodeBase64 (Object object, StringBuffer out)
		throws IOException
	{
		XByteArrayOutputStream bout
			= new XByteArrayOutputStream (2048);
		ObjectOutputStream oos = new ObjectOutputStream (bout);
		oos.writeObject (object);
		oos.flush ();
		oos.close ();
		encodeBase64 (bout.getBuffer (), 0, bout.size (), out);
	}


	public static Object decodeBase64 (CharSequence in, int offset, int len,
									   TypeLoader loader)
		throws IOException, ClassNotFoundException
	{
		byte[] buf = new byte[(len >> 2) * 3];
		len = decodeBase64 (in, offset, len, buf);
		return new ClassLoaderObjectInputStream
			(new ByteArrayInputStream (buf, 0, len), loader).readObject ();
	}

	
	public static void readFully (InputStream in, byte[] buf, int off, int len) throws IOException
	{
		while (len > 0)
		{
			int r = in.read (buf, off, len);
			if (r < 0)
			{
				throw new EOFException ();
			}
			len -= r;
			off += r;
		}
	}

	
	public static void readFully (InputStream in, byte[] buf) throws IOException
	{
		readFully (in, buf, 0, buf.length);
	}

	public static void read (Reader in, StringBuffer out) throws IOException
	{
		int c;
		while ((c = in.read ()) >= 0)
		{
			out.append ((char) c);
		}
		in.close ();
	}


	public static final String eval (String s, Map vars)
	{
		int n = s.length (), i = 0;
		StringBuffer b = new StringBuffer (n);
		while (i < n)
		{
			char c = s.charAt (i++);
			switch (c)
			{
				case '\\':
					if (i < n)
					{
						b.append (s.charAt (i++));
					}
					break;
				case '$':
					if (i < n)
					{
						int start, end;
						c = s.charAt (i);
						if (c == '{')
						{
							start = ++i;
							while (s.charAt (i) != '}')
							{
								if (++i == n)
								{
									break;
								}
							}
							end = i;
							i++;
						}
						else
						{
							start = i;
							while (Character.isLetter (s.charAt (i)))
							{
								if (++i == n)
								{
									break;
								}
							}
							end = i;
						}
						if (end > start)
						{
							Object v = vars.get (s.substring (start, end), null);
							if (v != null)
							{
								b.append (v);
							}
						}
					}
					break;
				default:
					b.append (c);
					break;
			}
		}
		return b.toString ();
	}


	public static char toHexDigit (int digit, boolean upperCase)
	{
		return (char)
			((digit < 10) ? digit + '0'
			 : upperCase ? digit + ('A' - 10) : digit + ('a' - 10));
	}

	
	public static int fromHexDigit (int c)
	{
		switch (c)
		{
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				return c - '0';
			case 'A':
			case 'a':
				return 10;
			case 'B':
			case 'b':
				return 11;
			case 'C':
			case 'c':
				return 12;
			case 'D':
			case 'd':
				return 13;
			case 'E':
			case 'e':
				return 14;
			case 'F':
			case 'f':
				return 15;
			default:
				return -1;
		}
	}


	private static long time, lastTime;

	public static void resetTime ()
	{
		time = lastTime = System.currentTimeMillis ();
	}


	public static void printTime (String msg)
	{
		long t = System.currentTimeMillis ();
		System.err.println (msg + ' ' + (t - time) + " ms, Delta "
							+ (t - lastTime) + " ms");
		lastTime = t;
	}


	public static void flushHandlers (Logger logger)
	{
		Handler[] h = logger.getHandlers ();
		if (h != null)
		{
			for (int i = h.length - 1; i >= 0; i--)
			{
				h[i].flush ();
			}
		}
	}

	
	private static StringMap loggerNames = new StringMap ();

	public static synchronized String getDisplayLoggerName (String logger)
	{
		return (String) loggerNames.get (logger, logger);
	}


	public static synchronized void setDisplayLoggerName (String logger, String name)
	{
		loggerNames.put (logger, name);
	}

	
	public static void formatDateAndName (LogRecord log, StringBuffer buffer)
	{
		new MessageFormat ("{0,date} {0,time}").format
			(new Object[] {new Date (log.getMillis ())}, buffer, null);
		if (log.getLoggerName () != null)
		{
			buffer.append (' ')
				.append (getDisplayLoggerName (log.getLoggerName ()));
		}
	}
	
	
	public static int executeForcedlyAndUninterruptibly
		(Lockable resource, LockProtectedRunnable task, boolean write)
	{
		int n = 0;
		while (true)
		{
			try
			{
				resource.executeForcedly (task, write);
				return n;
			}
			catch (InterruptedException e)
			{
				n++;
			}
			catch (Lockable.DeadLockException e)
			{
				throw new IllegalStateException (e); // executeForcedlyAndUninterruptibly shouldn't be invoked in a context that could lead to a dead-lock
			}
		}
	}

	
	public static int executeForcedlyAndUninterruptibly
		(Lockable resource, LockProtectedRunnable task, Lock retained)
	{
		int n = 0;
		while (true)
		{
			try
			{
				resource.executeForcedly (task, retained);
				return n;
			}
			catch (InterruptedException e)
			{
				n++;
			}
		}
	}

}
