
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

package de.grogra.xl.util;

import java.util.Random;
import java.lang.reflect.Array;
import de.grogra.xl.lang.*;

public final class Operators
{
	private static final ThreadLocal<Random> randomGenerators = new ThreadLocal<Random> ();

	
	public static Random getRandomGenerator ()
	{
		Random r = randomGenerators.get ();
		if (r == null)
		{
			r = new Random ();
			randomGenerators.set (r);
		}
		return r;
	}


	private Operators ()
	{
	}


	public static void forall (Aggregate a, boolean value)
	{
		if (a.initialize ())
		{
			a.ival = 1;
		}
		if (!a.isFinished () && !value)
		{
			a.ival = 0;
			a.setFinished ();
		}
	}


	public static void exist (Aggregate a, boolean value)
	{
		if (a.initialize ())
		{
			a.ival = 0;
		}
		if (!a.isFinished () && value)
		{
			a.ival = 1;
			a.setFinished ();
		}
	}

/*!!
#foreach ($type in ["boolean", "int", "long", "float", "double", "Object"])
$pp.setType($type)

	public static long count (Aggregate a, $type value)
	{
		if (a.initialize ())
		{
			a.lval = 0;
		}
		if (!a.isFinished ())
		{
			a.lval++;
		}
		return 0;
	}
	

	public static boolean empty (Aggregate a, $type value)
	{
		if (a.initialize ())
		{
			a.ival = 1;
		}
		if (!a.isFinished ())
		{
			a.ival = 0;
			a.setFinished ();
		}
		return false;
	}
	

	public static String string (Aggregate a, $type value)
	{
		if (a.initialize ())
		{
			a.aval1 = new StringBuffer ("[]");
			a.ival = 0;
		}
		if (a.isFinished ())
		{
			a.aval = a.aval1.toString ();
		}
		else
		{
			StringBuffer b = (StringBuffer) a.aval1;
			if (a.ival == 0)
			{
				a.ival = 1;
			}
			else
			{
				b.insert (b.length () - 1, ',');
			}
			b.insert (b.length () - 1, value);
		}
		return null;
	}
	
#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public static long count (Aggregate a, boolean value)
	{
		if (a.initialize ())
		{
			a.lval = 0;
		}
		if (!a.isFinished ())
		{
			a.lval++;
		}
		return 0;
	}
	
// generated
	public static boolean empty (Aggregate a, boolean value)
	{
		if (a.initialize ())
		{
			a.ival = 1;
		}
		if (!a.isFinished ())
		{
			a.ival = 0;
			a.setFinished ();
		}
		return false;
	}
	
// generated
	public static String string (Aggregate a, boolean value)
	{
		if (a.initialize ())
		{
			a.aval1 = new StringBuffer ("[]");
			a.ival = 0;
		}
		if (a.isFinished ())
		{
			a.aval = a.aval1.toString ();
		}
		else
		{
			StringBuffer b = (StringBuffer) a.aval1;
			if (a.ival == 0)
			{
				a.ival = 1;
			}
			else
			{
				b.insert (b.length () - 1, ',');
			}
			b.insert (b.length () - 1, value);
		}
		return null;
	}
	
// generated
// generated
	public static long count (Aggregate a, int value)
	{
		if (a.initialize ())
		{
			a.lval = 0;
		}
		if (!a.isFinished ())
		{
			a.lval++;
		}
		return 0;
	}
	
// generated
	public static boolean empty (Aggregate a, int value)
	{
		if (a.initialize ())
		{
			a.ival = 1;
		}
		if (!a.isFinished ())
		{
			a.ival = 0;
			a.setFinished ();
		}
		return false;
	}
	
// generated
	public static String string (Aggregate a, int value)
	{
		if (a.initialize ())
		{
			a.aval1 = new StringBuffer ("[]");
			a.ival = 0;
		}
		if (a.isFinished ())
		{
			a.aval = a.aval1.toString ();
		}
		else
		{
			StringBuffer b = (StringBuffer) a.aval1;
			if (a.ival == 0)
			{
				a.ival = 1;
			}
			else
			{
				b.insert (b.length () - 1, ',');
			}
			b.insert (b.length () - 1, value);
		}
		return null;
	}
	
// generated
// generated
	public static long count (Aggregate a, long value)
	{
		if (a.initialize ())
		{
			a.lval = 0;
		}
		if (!a.isFinished ())
		{
			a.lval++;
		}
		return 0;
	}
	
// generated
	public static boolean empty (Aggregate a, long value)
	{
		if (a.initialize ())
		{
			a.ival = 1;
		}
		if (!a.isFinished ())
		{
			a.ival = 0;
			a.setFinished ();
		}
		return false;
	}
	
// generated
	public static String string (Aggregate a, long value)
	{
		if (a.initialize ())
		{
			a.aval1 = new StringBuffer ("[]");
			a.ival = 0;
		}
		if (a.isFinished ())
		{
			a.aval = a.aval1.toString ();
		}
		else
		{
			StringBuffer b = (StringBuffer) a.aval1;
			if (a.ival == 0)
			{
				a.ival = 1;
			}
			else
			{
				b.insert (b.length () - 1, ',');
			}
			b.insert (b.length () - 1, value);
		}
		return null;
	}
	
// generated
// generated
	public static long count (Aggregate a, float value)
	{
		if (a.initialize ())
		{
			a.lval = 0;
		}
		if (!a.isFinished ())
		{
			a.lval++;
		}
		return 0;
	}
	
// generated
	public static boolean empty (Aggregate a, float value)
	{
		if (a.initialize ())
		{
			a.ival = 1;
		}
		if (!a.isFinished ())
		{
			a.ival = 0;
			a.setFinished ();
		}
		return false;
	}
	
// generated
	public static String string (Aggregate a, float value)
	{
		if (a.initialize ())
		{
			a.aval1 = new StringBuffer ("[]");
			a.ival = 0;
		}
		if (a.isFinished ())
		{
			a.aval = a.aval1.toString ();
		}
		else
		{
			StringBuffer b = (StringBuffer) a.aval1;
			if (a.ival == 0)
			{
				a.ival = 1;
			}
			else
			{
				b.insert (b.length () - 1, ',');
			}
			b.insert (b.length () - 1, value);
		}
		return null;
	}
	
// generated
// generated
	public static long count (Aggregate a, double value)
	{
		if (a.initialize ())
		{
			a.lval = 0;
		}
		if (!a.isFinished ())
		{
			a.lval++;
		}
		return 0;
	}
	
// generated
	public static boolean empty (Aggregate a, double value)
	{
		if (a.initialize ())
		{
			a.ival = 1;
		}
		if (!a.isFinished ())
		{
			a.ival = 0;
			a.setFinished ();
		}
		return false;
	}
	
// generated
	public static String string (Aggregate a, double value)
	{
		if (a.initialize ())
		{
			a.aval1 = new StringBuffer ("[]");
			a.ival = 0;
		}
		if (a.isFinished ())
		{
			a.aval = a.aval1.toString ();
		}
		else
		{
			StringBuffer b = (StringBuffer) a.aval1;
			if (a.ival == 0)
			{
				a.ival = 1;
			}
			else
			{
				b.insert (b.length () - 1, ',');
			}
			b.insert (b.length () - 1, value);
		}
		return null;
	}
	
// generated
// generated
	public static long count (Aggregate a, Object value)
	{
		if (a.initialize ())
		{
			a.lval = 0;
		}
		if (!a.isFinished ())
		{
			a.lval++;
		}
		return 0;
	}
	
// generated
	public static boolean empty (Aggregate a, Object value)
	{
		if (a.initialize ())
		{
			a.ival = 1;
		}
		if (!a.isFinished ())
		{
			a.ival = 0;
			a.setFinished ();
		}
		return false;
	}
	
// generated
	public static String string (Aggregate a, Object value)
	{
		if (a.initialize ())
		{
			a.aval1 = new StringBuffer ("[]");
			a.ival = 0;
		}
		if (a.isFinished ())
		{
			a.aval = a.aval1.toString ();
		}
		else
		{
			StringBuffer b = (StringBuffer) a.aval1;
			if (a.ival == 0)
			{
				a.ival = 1;
			}
			else
			{
				b.insert (b.length () - 1, ',');
			}
			b.insert (b.length () - 1, value);
		}
		return null;
	}
	
//!! *# End of generated code

/*!!
#foreach ($type in $types)
$pp.setType($type)

	#set ($list = "${pp.Type}List")

	public static Array array (Aggregate a, $type value)
	{
		if (a.initialize ())
		{
			a.aval1 = new $list ();
		}
		if (a.isFinished ())
		{
#if ($pp.object)
			a.aval = (($list) a.aval1).toArray
				((Object[]) Array.newInstance (a.getType ().getComponentType (), (($list) a.aval1).size ()));
#else
			a.aval = (($list) a.aval1).toArray ();
#end
		}
		else
		{
			(($list) a.aval1).add (value);
		}
		return null;
	}


	public static void first (Aggregate a, $type value)
	{
		if (a.initialize ())
		{
			a.${pp.prefix}val =
#if ($pp.fnumeric)
				${pp.wrapper}.NaN;
#else
				$pp.null $pp.type2vm;
#end
		}
		if (!a.isFinished ())
		{
			a.${pp.prefix}val = value $pp.type2vm;
			a.setFinished ();
		}
	}

	
	public static void first (Filter f, $type value, long count)
	{
		if (f.initialize ())
		{
			if (count <= 0)
			{
				f.accept = false;
				f.setFinished ();
				return;
			}
			f.accept = true;
			f.lval1 = count;
		}
		f.${pp.prefix}val = value $pp.type2vm;
		if (--f.lval1 == 0)
		{
			f.setFinished ();
		}
	}

	
	public static void slice (Filter f, $type value, long start, long end)
	{
		if (f.initialize ())
		{
			if (start < 0)
			{
				start = 0;
			}
			f.accept = false;
			if (start >= end)
			{
				f.setFinished ();
				return;
			}
			f.lval1 = end - start;
			f.lval2 = start;
		}
		if (!f.accept && (--f.lval2 < 0))
		{
			f.accept = true;
		}
		if (f.accept)
		{
			f.${pp.prefix}val = value $pp.type2vm;
			if (--f.lval1 == 0)
			{
				f.setFinished ();
			}
		}
	}


	public static void last (Aggregate a, $type value)
	{
		if (a.initialize ())
		{
			a.${pp.prefix}val =
#if ($pp.fnumeric)
				${pp.wrapper}.NaN;
#else
				$pp.null $pp.type2vm;
#end
		}
		if (!a.isFinished ())
		{
			a.${pp.prefix}val = value $pp.type2vm;
		}
	}


	public static void selectWhere (Aggregate a, $type value, boolean sel)
	{
		if (a.initialize ())
		{
			a.${pp.prefix}val =
#if ($pp.fnumeric)
				${pp.wrapper}.NaN;
#else
				$pp.null $pp.type2vm;
#end
		}
		if (!a.isFinished () && sel)
		{
			a.${pp.prefix}val = value $pp.type2vm;
			a.setFinished ();
		}
	}


	public static void selectRandomly (Aggregate a, $type value)
	{
		if (a.initialize ())
		{
			a.${pp.prefix}val =
#if ($pp.fnumeric)
				${pp.wrapper}.NaN;
#else
				$pp.null $pp.type2vm;
#end
			a.aval1 = getRandomGenerator ();
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			if (((Random) a.aval1).nextInt (++a.ival1) == 0)
			{
				a.${pp.prefix}val = value $pp.type2vm;
			}
		}
	}


	/**
	 * This aggregate method selects one of the provided
	 * <code>value</code>s, where each <code>value</code>
	 * has a relative probability <code>prob</code> of
	 * being chosen. Relative probability means that
	 * the <code>prob</code> values do not have to be
	 * normalized so that their sum is 1.
	 * 
	 * @param a aggregate instance (provided by the XL compiler)
	 * @param value a value of the sequence of values
	 * @param prob relative probability of <code>value</code>
	 $C
	public static void selectRandomly (Aggregate a, $type value, double prob)
	{
		if (a.initialize ())
		{
			a.${pp.prefix}val =
#if ($pp.fnumeric)
				${pp.wrapper}.NaN;
#else
				$pp.null $pp.type2vm;
#end
			a.aval1 = getRandomGenerator ();
			a.dval1 = 0;
		}
		if (!a.isFinished ())
		{
			if (prob <= 0)
			{
				prob = 0;
			}
			a.dval1 += prob;
			if (((Random) a.aval1).nextDouble () * a.dval1 <= prob)
			{
				a.${pp.prefix}val = value $pp.type2vm;
			}
		}
	}


	public static void selectWhereMin (Aggregate a, $type value, long n)
	{
		if (a.initialize ())
		{
			a.${pp.prefix}val =
#if ($pp.fnumeric)
				${pp.wrapper}.NaN;
#else
				$pp.null $pp.type2vm;
#end
			a.lval1 = Long.MAX_VALUE;
		}
		if (!a.isFinished () && (n <= a.lval1))
		{
			a.${pp.prefix}val = value $pp.type2vm;
			a.lval1 = n;
		}
	}


	public static void selectWhereMin (Aggregate a, $type value, double n)
	{
		if (a.initialize ())
		{
			a.${pp.prefix}val =
#if ($pp.fnumeric)
				${pp.wrapper}.NaN;
#else
				$pp.null $pp.type2vm;
#end
			a.dval1 = Double.POSITIVE_INFINITY;
		}
		if (!a.isFinished () && (n <= a.dval1))
		{
			a.${pp.prefix}val = value $pp.type2vm;
			a.dval1 = n;
		}
	}


	public static void selectWhereMax (Aggregate a, $type value, long n)
	{
		if (a.initialize ())
		{
			a.${pp.prefix}val =
#if ($pp.fnumeric)
				${pp.wrapper}.NaN;
#else
				$pp.null $pp.type2vm;
#end
			a.lval1 = Long.MIN_VALUE;
		}
		if (!a.isFinished () && (n >= a.lval1))
		{
			a.${pp.prefix}val = value $pp.type2vm;
			a.lval1 = n;
		}
	}


	public static void selectWhereMax (Aggregate a, $type value, double n)
	{
		if (a.initialize ())
		{
			a.${pp.prefix}val =
#if ($pp.fnumeric)
				${pp.wrapper}.NaN;
#else
				$pp.null $pp.type2vm;
#end
			a.dval1 = Double.NEGATIVE_INFINITY;
		}
		if (!a.isFinished () && (n >= a.dval1))
		{
			a.${pp.prefix}val = value $pp.type2vm;
			a.dval1 = n;
		}
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	
	public static Array array (Aggregate a, boolean value)
	{
		if (a.initialize ())
		{
			a.aval1 = new BooleanList ();
		}
		if (a.isFinished ())
		{
			a.aval = ((BooleanList) a.aval1).toArray ();
		}
		else
		{
			((BooleanList) a.aval1).add (value);
		}
		return null;
	}
// generated
// generated
	public static void first (Aggregate a, boolean value)
	{
		if (a.initialize ())
		{
			a.ival =
				false  ? 1 : 0;
		}
		if (!a.isFinished ())
		{
			a.ival = value  ? 1 : 0;
			a.setFinished ();
		}
	}
// generated
	
	public static void first (Filter f, boolean value, long count)
	{
		if (f.initialize ())
		{
			if (count <= 0)
			{
				f.accept = false;
				f.setFinished ();
				return;
			}
			f.accept = true;
			f.lval1 = count;
		}
		f.ival = value  ? 1 : 0;
		if (--f.lval1 == 0)
		{
			f.setFinished ();
		}
	}
// generated
	
	public static void slice (Filter f, boolean value, long start, long end)
	{
		if (f.initialize ())
		{
			if (start < 0)
			{
				start = 0;
			}
			f.accept = false;
			if (start >= end)
			{
				f.setFinished ();
				return;
			}
			f.lval1 = end - start;
			f.lval2 = start;
		}
		if (!f.accept && (--f.lval2 < 0))
		{
			f.accept = true;
		}
		if (f.accept)
		{
			f.ival = value  ? 1 : 0;
			if (--f.lval1 == 0)
			{
				f.setFinished ();
			}
		}
	}
// generated
// generated
	public static void last (Aggregate a, boolean value)
	{
		if (a.initialize ())
		{
			a.ival =
				false  ? 1 : 0;
		}
		if (!a.isFinished ())
		{
			a.ival = value  ? 1 : 0;
		}
	}
// generated
// generated
	public static void selectWhere (Aggregate a, boolean value, boolean sel)
	{
		if (a.initialize ())
		{
			a.ival =
				false  ? 1 : 0;
		}
		if (!a.isFinished () && sel)
		{
			a.ival = value  ? 1 : 0;
			a.setFinished ();
		}
	}
// generated
// generated
	public static void selectRandomly (Aggregate a, boolean value)
	{
		if (a.initialize ())
		{
			a.ival =
				false  ? 1 : 0;
			a.aval1 = getRandomGenerator ();
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			if (((Random) a.aval1).nextInt (++a.ival1) == 0)
			{
				a.ival = value  ? 1 : 0;
			}
		}
	}
// generated
// generated
	/**
	 * This aggregate method selects one of the provided
	 * <code>value</code>s, where each <code>value</code>
	 * has a relative probability <code>prob</code> of
	 * being chosen. Relative probability means that
	 * the <code>prob</code> values do not have to be
	 * normalized so that their sum is 1.
	 * 
	 * @param a aggregate instance (provided by the XL compiler)
	 * @param value a value of the sequence of values
	 * @param prob relative probability of <code>value</code>
	 */
	public static void selectRandomly (Aggregate a, boolean value, double prob)
	{
		if (a.initialize ())
		{
			a.ival =
				false  ? 1 : 0;
			a.aval1 = getRandomGenerator ();
			a.dval1 = 0;
		}
		if (!a.isFinished ())
		{
			if (prob <= 0)
			{
				prob = 0;
			}
			a.dval1 += prob;
			if (((Random) a.aval1).nextDouble () * a.dval1 <= prob)
			{
				a.ival = value  ? 1 : 0;
			}
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, boolean value, long n)
	{
		if (a.initialize ())
		{
			a.ival =
				false  ? 1 : 0;
			a.lval1 = Long.MAX_VALUE;
		}
		if (!a.isFinished () && (n <= a.lval1))
		{
			a.ival = value  ? 1 : 0;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, boolean value, double n)
	{
		if (a.initialize ())
		{
			a.ival =
				false  ? 1 : 0;
			a.dval1 = Double.POSITIVE_INFINITY;
		}
		if (!a.isFinished () && (n <= a.dval1))
		{
			a.ival = value  ? 1 : 0;
			a.dval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, boolean value, long n)
	{
		if (a.initialize ())
		{
			a.ival =
				false  ? 1 : 0;
			a.lval1 = Long.MIN_VALUE;
		}
		if (!a.isFinished () && (n >= a.lval1))
		{
			a.ival = value  ? 1 : 0;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, boolean value, double n)
	{
		if (a.initialize ())
		{
			a.ival =
				false  ? 1 : 0;
			a.dval1 = Double.NEGATIVE_INFINITY;
		}
		if (!a.isFinished () && (n >= a.dval1))
		{
			a.ival = value  ? 1 : 0;
			a.dval1 = n;
		}
	}
// generated
// generated
// generated
	
	public static Array array (Aggregate a, byte value)
	{
		if (a.initialize ())
		{
			a.aval1 = new ByteList ();
		}
		if (a.isFinished ())
		{
			a.aval = ((ByteList) a.aval1).toArray ();
		}
		else
		{
			((ByteList) a.aval1).add (value);
		}
		return null;
	}
// generated
// generated
	public static void first (Aggregate a, byte value)
	{
		if (a.initialize ())
		{
			a.ival =
				((byte) 0) ;
		}
		if (!a.isFinished ())
		{
			a.ival = value ;
			a.setFinished ();
		}
	}
// generated
	
	public static void first (Filter f, byte value, long count)
	{
		if (f.initialize ())
		{
			if (count <= 0)
			{
				f.accept = false;
				f.setFinished ();
				return;
			}
			f.accept = true;
			f.lval1 = count;
		}
		f.ival = value ;
		if (--f.lval1 == 0)
		{
			f.setFinished ();
		}
	}
// generated
	
	public static void slice (Filter f, byte value, long start, long end)
	{
		if (f.initialize ())
		{
			if (start < 0)
			{
				start = 0;
			}
			f.accept = false;
			if (start >= end)
			{
				f.setFinished ();
				return;
			}
			f.lval1 = end - start;
			f.lval2 = start;
		}
		if (!f.accept && (--f.lval2 < 0))
		{
			f.accept = true;
		}
		if (f.accept)
		{
			f.ival = value ;
			if (--f.lval1 == 0)
			{
				f.setFinished ();
			}
		}
	}
// generated
// generated
	public static void last (Aggregate a, byte value)
	{
		if (a.initialize ())
		{
			a.ival =
				((byte) 0) ;
		}
		if (!a.isFinished ())
		{
			a.ival = value ;
		}
	}
// generated
// generated
	public static void selectWhere (Aggregate a, byte value, boolean sel)
	{
		if (a.initialize ())
		{
			a.ival =
				((byte) 0) ;
		}
		if (!a.isFinished () && sel)
		{
			a.ival = value ;
			a.setFinished ();
		}
	}
// generated
// generated
	public static void selectRandomly (Aggregate a, byte value)
	{
		if (a.initialize ())
		{
			a.ival =
				((byte) 0) ;
			a.aval1 = getRandomGenerator ();
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			if (((Random) a.aval1).nextInt (++a.ival1) == 0)
			{
				a.ival = value ;
			}
		}
	}
// generated
// generated
	/**
	 * This aggregate method selects one of the provided
	 * <code>value</code>s, where each <code>value</code>
	 * has a relative probability <code>prob</code> of
	 * being chosen. Relative probability means that
	 * the <code>prob</code> values do not have to be
	 * normalized so that their sum is 1.
	 * 
	 * @param a aggregate instance (provided by the XL compiler)
	 * @param value a value of the sequence of values
	 * @param prob relative probability of <code>value</code>
	 */
	public static void selectRandomly (Aggregate a, byte value, double prob)
	{
		if (a.initialize ())
		{
			a.ival =
				((byte) 0) ;
			a.aval1 = getRandomGenerator ();
			a.dval1 = 0;
		}
		if (!a.isFinished ())
		{
			if (prob <= 0)
			{
				prob = 0;
			}
			a.dval1 += prob;
			if (((Random) a.aval1).nextDouble () * a.dval1 <= prob)
			{
				a.ival = value ;
			}
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, byte value, long n)
	{
		if (a.initialize ())
		{
			a.ival =
				((byte) 0) ;
			a.lval1 = Long.MAX_VALUE;
		}
		if (!a.isFinished () && (n <= a.lval1))
		{
			a.ival = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, byte value, double n)
	{
		if (a.initialize ())
		{
			a.ival =
				((byte) 0) ;
			a.dval1 = Double.POSITIVE_INFINITY;
		}
		if (!a.isFinished () && (n <= a.dval1))
		{
			a.ival = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, byte value, long n)
	{
		if (a.initialize ())
		{
			a.ival =
				((byte) 0) ;
			a.lval1 = Long.MIN_VALUE;
		}
		if (!a.isFinished () && (n >= a.lval1))
		{
			a.ival = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, byte value, double n)
	{
		if (a.initialize ())
		{
			a.ival =
				((byte) 0) ;
			a.dval1 = Double.NEGATIVE_INFINITY;
		}
		if (!a.isFinished () && (n >= a.dval1))
		{
			a.ival = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
// generated
	
	public static Array array (Aggregate a, short value)
	{
		if (a.initialize ())
		{
			a.aval1 = new ShortList ();
		}
		if (a.isFinished ())
		{
			a.aval = ((ShortList) a.aval1).toArray ();
		}
		else
		{
			((ShortList) a.aval1).add (value);
		}
		return null;
	}
// generated
// generated
	public static void first (Aggregate a, short value)
	{
		if (a.initialize ())
		{
			a.ival =
				((short) 0) ;
		}
		if (!a.isFinished ())
		{
			a.ival = value ;
			a.setFinished ();
		}
	}
// generated
	
	public static void first (Filter f, short value, long count)
	{
		if (f.initialize ())
		{
			if (count <= 0)
			{
				f.accept = false;
				f.setFinished ();
				return;
			}
			f.accept = true;
			f.lval1 = count;
		}
		f.ival = value ;
		if (--f.lval1 == 0)
		{
			f.setFinished ();
		}
	}
// generated
	
	public static void slice (Filter f, short value, long start, long end)
	{
		if (f.initialize ())
		{
			if (start < 0)
			{
				start = 0;
			}
			f.accept = false;
			if (start >= end)
			{
				f.setFinished ();
				return;
			}
			f.lval1 = end - start;
			f.lval2 = start;
		}
		if (!f.accept && (--f.lval2 < 0))
		{
			f.accept = true;
		}
		if (f.accept)
		{
			f.ival = value ;
			if (--f.lval1 == 0)
			{
				f.setFinished ();
			}
		}
	}
// generated
// generated
	public static void last (Aggregate a, short value)
	{
		if (a.initialize ())
		{
			a.ival =
				((short) 0) ;
		}
		if (!a.isFinished ())
		{
			a.ival = value ;
		}
	}
// generated
// generated
	public static void selectWhere (Aggregate a, short value, boolean sel)
	{
		if (a.initialize ())
		{
			a.ival =
				((short) 0) ;
		}
		if (!a.isFinished () && sel)
		{
			a.ival = value ;
			a.setFinished ();
		}
	}
// generated
// generated
	public static void selectRandomly (Aggregate a, short value)
	{
		if (a.initialize ())
		{
			a.ival =
				((short) 0) ;
			a.aval1 = getRandomGenerator ();
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			if (((Random) a.aval1).nextInt (++a.ival1) == 0)
			{
				a.ival = value ;
			}
		}
	}
// generated
// generated
	/**
	 * This aggregate method selects one of the provided
	 * <code>value</code>s, where each <code>value</code>
	 * has a relative probability <code>prob</code> of
	 * being chosen. Relative probability means that
	 * the <code>prob</code> values do not have to be
	 * normalized so that their sum is 1.
	 * 
	 * @param a aggregate instance (provided by the XL compiler)
	 * @param value a value of the sequence of values
	 * @param prob relative probability of <code>value</code>
	 */
	public static void selectRandomly (Aggregate a, short value, double prob)
	{
		if (a.initialize ())
		{
			a.ival =
				((short) 0) ;
			a.aval1 = getRandomGenerator ();
			a.dval1 = 0;
		}
		if (!a.isFinished ())
		{
			if (prob <= 0)
			{
				prob = 0;
			}
			a.dval1 += prob;
			if (((Random) a.aval1).nextDouble () * a.dval1 <= prob)
			{
				a.ival = value ;
			}
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, short value, long n)
	{
		if (a.initialize ())
		{
			a.ival =
				((short) 0) ;
			a.lval1 = Long.MAX_VALUE;
		}
		if (!a.isFinished () && (n <= a.lval1))
		{
			a.ival = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, short value, double n)
	{
		if (a.initialize ())
		{
			a.ival =
				((short) 0) ;
			a.dval1 = Double.POSITIVE_INFINITY;
		}
		if (!a.isFinished () && (n <= a.dval1))
		{
			a.ival = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, short value, long n)
	{
		if (a.initialize ())
		{
			a.ival =
				((short) 0) ;
			a.lval1 = Long.MIN_VALUE;
		}
		if (!a.isFinished () && (n >= a.lval1))
		{
			a.ival = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, short value, double n)
	{
		if (a.initialize ())
		{
			a.ival =
				((short) 0) ;
			a.dval1 = Double.NEGATIVE_INFINITY;
		}
		if (!a.isFinished () && (n >= a.dval1))
		{
			a.ival = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
// generated
	
	public static Array array (Aggregate a, char value)
	{
		if (a.initialize ())
		{
			a.aval1 = new CharList ();
		}
		if (a.isFinished ())
		{
			a.aval = ((CharList) a.aval1).toArray ();
		}
		else
		{
			((CharList) a.aval1).add (value);
		}
		return null;
	}
// generated
// generated
	public static void first (Aggregate a, char value)
	{
		if (a.initialize ())
		{
			a.ival =
				((char) 0) ;
		}
		if (!a.isFinished ())
		{
			a.ival = value ;
			a.setFinished ();
		}
	}
// generated
	
	public static void first (Filter f, char value, long count)
	{
		if (f.initialize ())
		{
			if (count <= 0)
			{
				f.accept = false;
				f.setFinished ();
				return;
			}
			f.accept = true;
			f.lval1 = count;
		}
		f.ival = value ;
		if (--f.lval1 == 0)
		{
			f.setFinished ();
		}
	}
// generated
	
	public static void slice (Filter f, char value, long start, long end)
	{
		if (f.initialize ())
		{
			if (start < 0)
			{
				start = 0;
			}
			f.accept = false;
			if (start >= end)
			{
				f.setFinished ();
				return;
			}
			f.lval1 = end - start;
			f.lval2 = start;
		}
		if (!f.accept && (--f.lval2 < 0))
		{
			f.accept = true;
		}
		if (f.accept)
		{
			f.ival = value ;
			if (--f.lval1 == 0)
			{
				f.setFinished ();
			}
		}
	}
// generated
// generated
	public static void last (Aggregate a, char value)
	{
		if (a.initialize ())
		{
			a.ival =
				((char) 0) ;
		}
		if (!a.isFinished ())
		{
			a.ival = value ;
		}
	}
// generated
// generated
	public static void selectWhere (Aggregate a, char value, boolean sel)
	{
		if (a.initialize ())
		{
			a.ival =
				((char) 0) ;
		}
		if (!a.isFinished () && sel)
		{
			a.ival = value ;
			a.setFinished ();
		}
	}
// generated
// generated
	public static void selectRandomly (Aggregate a, char value)
	{
		if (a.initialize ())
		{
			a.ival =
				((char) 0) ;
			a.aval1 = getRandomGenerator ();
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			if (((Random) a.aval1).nextInt (++a.ival1) == 0)
			{
				a.ival = value ;
			}
		}
	}
// generated
// generated
	/**
	 * This aggregate method selects one of the provided
	 * <code>value</code>s, where each <code>value</code>
	 * has a relative probability <code>prob</code> of
	 * being chosen. Relative probability means that
	 * the <code>prob</code> values do not have to be
	 * normalized so that their sum is 1.
	 * 
	 * @param a aggregate instance (provided by the XL compiler)
	 * @param value a value of the sequence of values
	 * @param prob relative probability of <code>value</code>
	 */
	public static void selectRandomly (Aggregate a, char value, double prob)
	{
		if (a.initialize ())
		{
			a.ival =
				((char) 0) ;
			a.aval1 = getRandomGenerator ();
			a.dval1 = 0;
		}
		if (!a.isFinished ())
		{
			if (prob <= 0)
			{
				prob = 0;
			}
			a.dval1 += prob;
			if (((Random) a.aval1).nextDouble () * a.dval1 <= prob)
			{
				a.ival = value ;
			}
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, char value, long n)
	{
		if (a.initialize ())
		{
			a.ival =
				((char) 0) ;
			a.lval1 = Long.MAX_VALUE;
		}
		if (!a.isFinished () && (n <= a.lval1))
		{
			a.ival = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, char value, double n)
	{
		if (a.initialize ())
		{
			a.ival =
				((char) 0) ;
			a.dval1 = Double.POSITIVE_INFINITY;
		}
		if (!a.isFinished () && (n <= a.dval1))
		{
			a.ival = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, char value, long n)
	{
		if (a.initialize ())
		{
			a.ival =
				((char) 0) ;
			a.lval1 = Long.MIN_VALUE;
		}
		if (!a.isFinished () && (n >= a.lval1))
		{
			a.ival = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, char value, double n)
	{
		if (a.initialize ())
		{
			a.ival =
				((char) 0) ;
			a.dval1 = Double.NEGATIVE_INFINITY;
		}
		if (!a.isFinished () && (n >= a.dval1))
		{
			a.ival = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
// generated
	
	public static Array array (Aggregate a, int value)
	{
		if (a.initialize ())
		{
			a.aval1 = new IntList ();
		}
		if (a.isFinished ())
		{
			a.aval = ((IntList) a.aval1).toArray ();
		}
		else
		{
			((IntList) a.aval1).add (value);
		}
		return null;
	}
// generated
// generated
	public static void first (Aggregate a, int value)
	{
		if (a.initialize ())
		{
			a.ival =
				((int) 0) ;
		}
		if (!a.isFinished ())
		{
			a.ival = value ;
			a.setFinished ();
		}
	}
// generated
	
	public static void first (Filter f, int value, long count)
	{
		if (f.initialize ())
		{
			if (count <= 0)
			{
				f.accept = false;
				f.setFinished ();
				return;
			}
			f.accept = true;
			f.lval1 = count;
		}
		f.ival = value ;
		if (--f.lval1 == 0)
		{
			f.setFinished ();
		}
	}
// generated
	
	public static void slice (Filter f, int value, long start, long end)
	{
		if (f.initialize ())
		{
			if (start < 0)
			{
				start = 0;
			}
			f.accept = false;
			if (start >= end)
			{
				f.setFinished ();
				return;
			}
			f.lval1 = end - start;
			f.lval2 = start;
		}
		if (!f.accept && (--f.lval2 < 0))
		{
			f.accept = true;
		}
		if (f.accept)
		{
			f.ival = value ;
			if (--f.lval1 == 0)
			{
				f.setFinished ();
			}
		}
	}
// generated
// generated
	public static void last (Aggregate a, int value)
	{
		if (a.initialize ())
		{
			a.ival =
				((int) 0) ;
		}
		if (!a.isFinished ())
		{
			a.ival = value ;
		}
	}
// generated
// generated
	public static void selectWhere (Aggregate a, int value, boolean sel)
	{
		if (a.initialize ())
		{
			a.ival =
				((int) 0) ;
		}
		if (!a.isFinished () && sel)
		{
			a.ival = value ;
			a.setFinished ();
		}
	}
// generated
// generated
	public static void selectRandomly (Aggregate a, int value)
	{
		if (a.initialize ())
		{
			a.ival =
				((int) 0) ;
			a.aval1 = getRandomGenerator ();
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			if (((Random) a.aval1).nextInt (++a.ival1) == 0)
			{
				a.ival = value ;
			}
		}
	}
// generated
// generated
	/**
	 * This aggregate method selects one of the provided
	 * <code>value</code>s, where each <code>value</code>
	 * has a relative probability <code>prob</code> of
	 * being chosen. Relative probability means that
	 * the <code>prob</code> values do not have to be
	 * normalized so that their sum is 1.
	 * 
	 * @param a aggregate instance (provided by the XL compiler)
	 * @param value a value of the sequence of values
	 * @param prob relative probability of <code>value</code>
	 */
	public static void selectRandomly (Aggregate a, int value, double prob)
	{
		if (a.initialize ())
		{
			a.ival =
				((int) 0) ;
			a.aval1 = getRandomGenerator ();
			a.dval1 = 0;
		}
		if (!a.isFinished ())
		{
			if (prob <= 0)
			{
				prob = 0;
			}
			a.dval1 += prob;
			if (((Random) a.aval1).nextDouble () * a.dval1 <= prob)
			{
				a.ival = value ;
			}
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, int value, long n)
	{
		if (a.initialize ())
		{
			a.ival =
				((int) 0) ;
			a.lval1 = Long.MAX_VALUE;
		}
		if (!a.isFinished () && (n <= a.lval1))
		{
			a.ival = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, int value, double n)
	{
		if (a.initialize ())
		{
			a.ival =
				((int) 0) ;
			a.dval1 = Double.POSITIVE_INFINITY;
		}
		if (!a.isFinished () && (n <= a.dval1))
		{
			a.ival = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, int value, long n)
	{
		if (a.initialize ())
		{
			a.ival =
				((int) 0) ;
			a.lval1 = Long.MIN_VALUE;
		}
		if (!a.isFinished () && (n >= a.lval1))
		{
			a.ival = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, int value, double n)
	{
		if (a.initialize ())
		{
			a.ival =
				((int) 0) ;
			a.dval1 = Double.NEGATIVE_INFINITY;
		}
		if (!a.isFinished () && (n >= a.dval1))
		{
			a.ival = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
// generated
	
	public static Array array (Aggregate a, long value)
	{
		if (a.initialize ())
		{
			a.aval1 = new LongList ();
		}
		if (a.isFinished ())
		{
			a.aval = ((LongList) a.aval1).toArray ();
		}
		else
		{
			((LongList) a.aval1).add (value);
		}
		return null;
	}
// generated
// generated
	public static void first (Aggregate a, long value)
	{
		if (a.initialize ())
		{
			a.lval =
				((long) 0) ;
		}
		if (!a.isFinished ())
		{
			a.lval = value ;
			a.setFinished ();
		}
	}
// generated
	
	public static void first (Filter f, long value, long count)
	{
		if (f.initialize ())
		{
			if (count <= 0)
			{
				f.accept = false;
				f.setFinished ();
				return;
			}
			f.accept = true;
			f.lval1 = count;
		}
		f.lval = value ;
		if (--f.lval1 == 0)
		{
			f.setFinished ();
		}
	}
// generated
	
	public static void slice (Filter f, long value, long start, long end)
	{
		if (f.initialize ())
		{
			if (start < 0)
			{
				start = 0;
			}
			f.accept = false;
			if (start >= end)
			{
				f.setFinished ();
				return;
			}
			f.lval1 = end - start;
			f.lval2 = start;
		}
		if (!f.accept && (--f.lval2 < 0))
		{
			f.accept = true;
		}
		if (f.accept)
		{
			f.lval = value ;
			if (--f.lval1 == 0)
			{
				f.setFinished ();
			}
		}
	}
// generated
// generated
	public static void last (Aggregate a, long value)
	{
		if (a.initialize ())
		{
			a.lval =
				((long) 0) ;
		}
		if (!a.isFinished ())
		{
			a.lval = value ;
		}
	}
// generated
// generated
	public static void selectWhere (Aggregate a, long value, boolean sel)
	{
		if (a.initialize ())
		{
			a.lval =
				((long) 0) ;
		}
		if (!a.isFinished () && sel)
		{
			a.lval = value ;
			a.setFinished ();
		}
	}
// generated
// generated
	public static void selectRandomly (Aggregate a, long value)
	{
		if (a.initialize ())
		{
			a.lval =
				((long) 0) ;
			a.aval1 = getRandomGenerator ();
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			if (((Random) a.aval1).nextInt (++a.ival1) == 0)
			{
				a.lval = value ;
			}
		}
	}
// generated
// generated
	/**
	 * This aggregate method selects one of the provided
	 * <code>value</code>s, where each <code>value</code>
	 * has a relative probability <code>prob</code> of
	 * being chosen. Relative probability means that
	 * the <code>prob</code> values do not have to be
	 * normalized so that their sum is 1.
	 * 
	 * @param a aggregate instance (provided by the XL compiler)
	 * @param value a value of the sequence of values
	 * @param prob relative probability of <code>value</code>
	 */
	public static void selectRandomly (Aggregate a, long value, double prob)
	{
		if (a.initialize ())
		{
			a.lval =
				((long) 0) ;
			a.aval1 = getRandomGenerator ();
			a.dval1 = 0;
		}
		if (!a.isFinished ())
		{
			if (prob <= 0)
			{
				prob = 0;
			}
			a.dval1 += prob;
			if (((Random) a.aval1).nextDouble () * a.dval1 <= prob)
			{
				a.lval = value ;
			}
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, long value, long n)
	{
		if (a.initialize ())
		{
			a.lval =
				((long) 0) ;
			a.lval1 = Long.MAX_VALUE;
		}
		if (!a.isFinished () && (n <= a.lval1))
		{
			a.lval = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, long value, double n)
	{
		if (a.initialize ())
		{
			a.lval =
				((long) 0) ;
			a.dval1 = Double.POSITIVE_INFINITY;
		}
		if (!a.isFinished () && (n <= a.dval1))
		{
			a.lval = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, long value, long n)
	{
		if (a.initialize ())
		{
			a.lval =
				((long) 0) ;
			a.lval1 = Long.MIN_VALUE;
		}
		if (!a.isFinished () && (n >= a.lval1))
		{
			a.lval = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, long value, double n)
	{
		if (a.initialize ())
		{
			a.lval =
				((long) 0) ;
			a.dval1 = Double.NEGATIVE_INFINITY;
		}
		if (!a.isFinished () && (n >= a.dval1))
		{
			a.lval = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
// generated
	
	public static Array array (Aggregate a, float value)
	{
		if (a.initialize ())
		{
			a.aval1 = new FloatList ();
		}
		if (a.isFinished ())
		{
			a.aval = ((FloatList) a.aval1).toArray ();
		}
		else
		{
			((FloatList) a.aval1).add (value);
		}
		return null;
	}
// generated
// generated
	public static void first (Aggregate a, float value)
	{
		if (a.initialize ())
		{
			a.fval =
				Float.NaN;
		}
		if (!a.isFinished ())
		{
			a.fval = value ;
			a.setFinished ();
		}
	}
// generated
	
	public static void first (Filter f, float value, long count)
	{
		if (f.initialize ())
		{
			if (count <= 0)
			{
				f.accept = false;
				f.setFinished ();
				return;
			}
			f.accept = true;
			f.lval1 = count;
		}
		f.fval = value ;
		if (--f.lval1 == 0)
		{
			f.setFinished ();
		}
	}
// generated
	
	public static void slice (Filter f, float value, long start, long end)
	{
		if (f.initialize ())
		{
			if (start < 0)
			{
				start = 0;
			}
			f.accept = false;
			if (start >= end)
			{
				f.setFinished ();
				return;
			}
			f.lval1 = end - start;
			f.lval2 = start;
		}
		if (!f.accept && (--f.lval2 < 0))
		{
			f.accept = true;
		}
		if (f.accept)
		{
			f.fval = value ;
			if (--f.lval1 == 0)
			{
				f.setFinished ();
			}
		}
	}
// generated
// generated
	public static void last (Aggregate a, float value)
	{
		if (a.initialize ())
		{
			a.fval =
				Float.NaN;
		}
		if (!a.isFinished ())
		{
			a.fval = value ;
		}
	}
// generated
// generated
	public static void selectWhere (Aggregate a, float value, boolean sel)
	{
		if (a.initialize ())
		{
			a.fval =
				Float.NaN;
		}
		if (!a.isFinished () && sel)
		{
			a.fval = value ;
			a.setFinished ();
		}
	}
// generated
// generated
	public static void selectRandomly (Aggregate a, float value)
	{
		if (a.initialize ())
		{
			a.fval =
				Float.NaN;
			a.aval1 = getRandomGenerator ();
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			if (((Random) a.aval1).nextInt (++a.ival1) == 0)
			{
				a.fval = value ;
			}
		}
	}
// generated
// generated
	/**
	 * This aggregate method selects one of the provided
	 * <code>value</code>s, where each <code>value</code>
	 * has a relative probability <code>prob</code> of
	 * being chosen. Relative probability means that
	 * the <code>prob</code> values do not have to be
	 * normalized so that their sum is 1.
	 * 
	 * @param a aggregate instance (provided by the XL compiler)
	 * @param value a value of the sequence of values
	 * @param prob relative probability of <code>value</code>
	 */
	public static void selectRandomly (Aggregate a, float value, double prob)
	{
		if (a.initialize ())
		{
			a.fval =
				Float.NaN;
			a.aval1 = getRandomGenerator ();
			a.dval1 = 0;
		}
		if (!a.isFinished ())
		{
			if (prob <= 0)
			{
				prob = 0;
			}
			a.dval1 += prob;
			if (((Random) a.aval1).nextDouble () * a.dval1 <= prob)
			{
				a.fval = value ;
			}
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, float value, long n)
	{
		if (a.initialize ())
		{
			a.fval =
				Float.NaN;
			a.lval1 = Long.MAX_VALUE;
		}
		if (!a.isFinished () && (n <= a.lval1))
		{
			a.fval = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, float value, double n)
	{
		if (a.initialize ())
		{
			a.fval =
				Float.NaN;
			a.dval1 = Double.POSITIVE_INFINITY;
		}
		if (!a.isFinished () && (n <= a.dval1))
		{
			a.fval = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, float value, long n)
	{
		if (a.initialize ())
		{
			a.fval =
				Float.NaN;
			a.lval1 = Long.MIN_VALUE;
		}
		if (!a.isFinished () && (n >= a.lval1))
		{
			a.fval = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, float value, double n)
	{
		if (a.initialize ())
		{
			a.fval =
				Float.NaN;
			a.dval1 = Double.NEGATIVE_INFINITY;
		}
		if (!a.isFinished () && (n >= a.dval1))
		{
			a.fval = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
// generated
	
	public static Array array (Aggregate a, double value)
	{
		if (a.initialize ())
		{
			a.aval1 = new DoubleList ();
		}
		if (a.isFinished ())
		{
			a.aval = ((DoubleList) a.aval1).toArray ();
		}
		else
		{
			((DoubleList) a.aval1).add (value);
		}
		return null;
	}
// generated
// generated
	public static void first (Aggregate a, double value)
	{
		if (a.initialize ())
		{
			a.dval =
				Double.NaN;
		}
		if (!a.isFinished ())
		{
			a.dval = value ;
			a.setFinished ();
		}
	}
// generated
	
	public static void first (Filter f, double value, long count)
	{
		if (f.initialize ())
		{
			if (count <= 0)
			{
				f.accept = false;
				f.setFinished ();
				return;
			}
			f.accept = true;
			f.lval1 = count;
		}
		f.dval = value ;
		if (--f.lval1 == 0)
		{
			f.setFinished ();
		}
	}
// generated
	
	public static void slice (Filter f, double value, long start, long end)
	{
		if (f.initialize ())
		{
			if (start < 0)
			{
				start = 0;
			}
			f.accept = false;
			if (start >= end)
			{
				f.setFinished ();
				return;
			}
			f.lval1 = end - start;
			f.lval2 = start;
		}
		if (!f.accept && (--f.lval2 < 0))
		{
			f.accept = true;
		}
		if (f.accept)
		{
			f.dval = value ;
			if (--f.lval1 == 0)
			{
				f.setFinished ();
			}
		}
	}
// generated
// generated
	public static void last (Aggregate a, double value)
	{
		if (a.initialize ())
		{
			a.dval =
				Double.NaN;
		}
		if (!a.isFinished ())
		{
			a.dval = value ;
		}
	}
// generated
// generated
	public static void selectWhere (Aggregate a, double value, boolean sel)
	{
		if (a.initialize ())
		{
			a.dval =
				Double.NaN;
		}
		if (!a.isFinished () && sel)
		{
			a.dval = value ;
			a.setFinished ();
		}
	}
// generated
// generated
	public static void selectRandomly (Aggregate a, double value)
	{
		if (a.initialize ())
		{
			a.dval =
				Double.NaN;
			a.aval1 = getRandomGenerator ();
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			if (((Random) a.aval1).nextInt (++a.ival1) == 0)
			{
				a.dval = value ;
			}
		}
	}
// generated
// generated
	/**
	 * This aggregate method selects one of the provided
	 * <code>value</code>s, where each <code>value</code>
	 * has a relative probability <code>prob</code> of
	 * being chosen. Relative probability means that
	 * the <code>prob</code> values do not have to be
	 * normalized so that their sum is 1.
	 * 
	 * @param a aggregate instance (provided by the XL compiler)
	 * @param value a value of the sequence of values
	 * @param prob relative probability of <code>value</code>
	 */
	public static void selectRandomly (Aggregate a, double value, double prob)
	{
		if (a.initialize ())
		{
			a.dval =
				Double.NaN;
			a.aval1 = getRandomGenerator ();
			a.dval1 = 0;
		}
		if (!a.isFinished ())
		{
			if (prob <= 0)
			{
				prob = 0;
			}
			a.dval1 += prob;
			if (((Random) a.aval1).nextDouble () * a.dval1 <= prob)
			{
				a.dval = value ;
			}
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, double value, long n)
	{
		if (a.initialize ())
		{
			a.dval =
				Double.NaN;
			a.lval1 = Long.MAX_VALUE;
		}
		if (!a.isFinished () && (n <= a.lval1))
		{
			a.dval = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, double value, double n)
	{
		if (a.initialize ())
		{
			a.dval =
				Double.NaN;
			a.dval1 = Double.POSITIVE_INFINITY;
		}
		if (!a.isFinished () && (n <= a.dval1))
		{
			a.dval = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, double value, long n)
	{
		if (a.initialize ())
		{
			a.dval =
				Double.NaN;
			a.lval1 = Long.MIN_VALUE;
		}
		if (!a.isFinished () && (n >= a.lval1))
		{
			a.dval = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, double value, double n)
	{
		if (a.initialize ())
		{
			a.dval =
				Double.NaN;
			a.dval1 = Double.NEGATIVE_INFINITY;
		}
		if (!a.isFinished () && (n >= a.dval1))
		{
			a.dval = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
// generated
	
	public static Array array (Aggregate a, Object value)
	{
		if (a.initialize ())
		{
			a.aval1 = new ObjectList ();
		}
		if (a.isFinished ())
		{
			a.aval = ((ObjectList) a.aval1).toArray
				((Object[]) Array.newInstance (a.getType ().getComponentType (), ((ObjectList) a.aval1).size ()));
		}
		else
		{
			((ObjectList) a.aval1).add (value);
		}
		return null;
	}
// generated
// generated
	public static void first (Aggregate a, Object value)
	{
		if (a.initialize ())
		{
			a.aval =
				null ;
		}
		if (!a.isFinished ())
		{
			a.aval = value ;
			a.setFinished ();
		}
	}
// generated
	
	public static void first (Filter f, Object value, long count)
	{
		if (f.initialize ())
		{
			if (count <= 0)
			{
				f.accept = false;
				f.setFinished ();
				return;
			}
			f.accept = true;
			f.lval1 = count;
		}
		f.aval = value ;
		if (--f.lval1 == 0)
		{
			f.setFinished ();
		}
	}
// generated
	
	public static void slice (Filter f, Object value, long start, long end)
	{
		if (f.initialize ())
		{
			if (start < 0)
			{
				start = 0;
			}
			f.accept = false;
			if (start >= end)
			{
				f.setFinished ();
				return;
			}
			f.lval1 = end - start;
			f.lval2 = start;
		}
		if (!f.accept && (--f.lval2 < 0))
		{
			f.accept = true;
		}
		if (f.accept)
		{
			f.aval = value ;
			if (--f.lval1 == 0)
			{
				f.setFinished ();
			}
		}
	}
// generated
// generated
	public static void last (Aggregate a, Object value)
	{
		if (a.initialize ())
		{
			a.aval =
				null ;
		}
		if (!a.isFinished ())
		{
			a.aval = value ;
		}
	}
// generated
// generated
	public static void selectWhere (Aggregate a, Object value, boolean sel)
	{
		if (a.initialize ())
		{
			a.aval =
				null ;
		}
		if (!a.isFinished () && sel)
		{
			a.aval = value ;
			a.setFinished ();
		}
	}
// generated
// generated
	public static void selectRandomly (Aggregate a, Object value)
	{
		if (a.initialize ())
		{
			a.aval =
				null ;
			a.aval1 = getRandomGenerator ();
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			if (((Random) a.aval1).nextInt (++a.ival1) == 0)
			{
				a.aval = value ;
			}
		}
	}
// generated
// generated
	/**
	 * This aggregate method selects one of the provided
	 * <code>value</code>s, where each <code>value</code>
	 * has a relative probability <code>prob</code> of
	 * being chosen. Relative probability means that
	 * the <code>prob</code> values do not have to be
	 * normalized so that their sum is 1.
	 * 
	 * @param a aggregate instance (provided by the XL compiler)
	 * @param value a value of the sequence of values
	 * @param prob relative probability of <code>value</code>
	 */
	public static void selectRandomly (Aggregate a, Object value, double prob)
	{
		if (a.initialize ())
		{
			a.aval =
				null ;
			a.aval1 = getRandomGenerator ();
			a.dval1 = 0;
		}
		if (!a.isFinished ())
		{
			if (prob <= 0)
			{
				prob = 0;
			}
			a.dval1 += prob;
			if (((Random) a.aval1).nextDouble () * a.dval1 <= prob)
			{
				a.aval = value ;
			}
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, Object value, long n)
	{
		if (a.initialize ())
		{
			a.aval =
				null ;
			a.lval1 = Long.MAX_VALUE;
		}
		if (!a.isFinished () && (n <= a.lval1))
		{
			a.aval = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMin (Aggregate a, Object value, double n)
	{
		if (a.initialize ())
		{
			a.aval =
				null ;
			a.dval1 = Double.POSITIVE_INFINITY;
		}
		if (!a.isFinished () && (n <= a.dval1))
		{
			a.aval = value ;
			a.dval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, Object value, long n)
	{
		if (a.initialize ())
		{
			a.aval =
				null ;
			a.lval1 = Long.MIN_VALUE;
		}
		if (!a.isFinished () && (n >= a.lval1))
		{
			a.aval = value ;
			a.lval1 = n;
		}
	}
// generated
// generated
	public static void selectWhereMax (Aggregate a, Object value, double n)
	{
		if (a.initialize ())
		{
			a.aval =
				null ;
			a.dval1 = Double.NEGATIVE_INFINITY;
		}
		if (!a.isFinished () && (n >= a.dval1))
		{
			a.aval = value ;
			a.dval1 = n;
		}
	}
// generated
//!! *# End of generated code

/*!!
#foreach ($type in $numeric)
$pp.setType($type)

	public static void min (Aggregate a, $type value)
	{
#if ($pp.fnumeric)
		if (a.initialize ())
		{
			a.${pp.prefix}val = ${pp.wrapper}.POSITIVE_INFINITY;
		}
		if (!a.isFinished ())
		{
			a.${pp.prefix}val = Math.min (a.${pp.prefix}val, value);
		}
#else
		if (a.initialize ())
		{
			a.${pp.prefix}val = ${pp.wrapper}.MAX_VALUE;
		}
		if (!a.isFinished () && (value < a.${pp.prefix}val))
		{
			a.${pp.prefix}val = value;
		}
#end
	}


	public static void max (Aggregate a, $type value)
	{
#if ($pp.fnumeric)
		if (a.initialize ())
		{
			a.${pp.prefix}val = ${pp.wrapper}.NEGATIVE_INFINITY;
		}
		if (!a.isFinished ())
		{
			a.${pp.prefix}val = Math.max (a.${pp.prefix}val, value);
		}
#else
		if (a.initialize ())
		{
			a.${pp.prefix}val = ${pp.wrapper}.MIN_VALUE;
		}
		if (!a.isFinished () && (value > a.${pp.prefix}val))
		{
			a.${pp.prefix}val = value;
		}
#end
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public static void min (Aggregate a, byte value)
	{
		if (a.initialize ())
		{
			a.ival = Byte.MAX_VALUE;
		}
		if (!a.isFinished () && (value < a.ival))
		{
			a.ival = value;
		}
	}
// generated
// generated
	public static void max (Aggregate a, byte value)
	{
		if (a.initialize ())
		{
			a.ival = Byte.MIN_VALUE;
		}
		if (!a.isFinished () && (value > a.ival))
		{
			a.ival = value;
		}
	}
// generated
// generated
// generated
	public static void min (Aggregate a, short value)
	{
		if (a.initialize ())
		{
			a.ival = Short.MAX_VALUE;
		}
		if (!a.isFinished () && (value < a.ival))
		{
			a.ival = value;
		}
	}
// generated
// generated
	public static void max (Aggregate a, short value)
	{
		if (a.initialize ())
		{
			a.ival = Short.MIN_VALUE;
		}
		if (!a.isFinished () && (value > a.ival))
		{
			a.ival = value;
		}
	}
// generated
// generated
// generated
	public static void min (Aggregate a, int value)
	{
		if (a.initialize ())
		{
			a.ival = Integer.MAX_VALUE;
		}
		if (!a.isFinished () && (value < a.ival))
		{
			a.ival = value;
		}
	}
// generated
// generated
	public static void max (Aggregate a, int value)
	{
		if (a.initialize ())
		{
			a.ival = Integer.MIN_VALUE;
		}
		if (!a.isFinished () && (value > a.ival))
		{
			a.ival = value;
		}
	}
// generated
// generated
// generated
	public static void min (Aggregate a, long value)
	{
		if (a.initialize ())
		{
			a.lval = Long.MAX_VALUE;
		}
		if (!a.isFinished () && (value < a.lval))
		{
			a.lval = value;
		}
	}
// generated
// generated
	public static void max (Aggregate a, long value)
	{
		if (a.initialize ())
		{
			a.lval = Long.MIN_VALUE;
		}
		if (!a.isFinished () && (value > a.lval))
		{
			a.lval = value;
		}
	}
// generated
// generated
// generated
	public static void min (Aggregate a, float value)
	{
		if (a.initialize ())
		{
			a.fval = Float.POSITIVE_INFINITY;
		}
		if (!a.isFinished ())
		{
			a.fval = Math.min (a.fval, value);
		}
	}
// generated
// generated
	public static void max (Aggregate a, float value)
	{
		if (a.initialize ())
		{
			a.fval = Float.NEGATIVE_INFINITY;
		}
		if (!a.isFinished ())
		{
			a.fval = Math.max (a.fval, value);
		}
	}
// generated
// generated
// generated
	public static void min (Aggregate a, double value)
	{
		if (a.initialize ())
		{
			a.dval = Double.POSITIVE_INFINITY;
		}
		if (!a.isFinished ())
		{
			a.dval = Math.min (a.dval, value);
		}
	}
// generated
// generated
	public static void max (Aggregate a, double value)
	{
		if (a.initialize ())
		{
			a.dval = Double.NEGATIVE_INFINITY;
		}
		if (!a.isFinished ())
		{
			a.dval = Math.max (a.dval, value);
		}
	}
// generated
//!! *# End of generated code

/*!!
#foreach ($type in $vmnumeric)
$pp.setType($type)

	public static void sum (Aggregate a, $type value)
	{
		if (a.initialize ())
		{
			a.${pp.prefix}val = 0;
		}
		if (!a.isFinished ())
		{
			a.${pp.prefix}val += value;
		}
	}

	public static void mean (Aggregate a, $type value)
	{
		if (a.initialize ())
		{
			a.${pp.prefix}val = 0;
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			a.${pp.prefix}val += value;
			a.ival1++;
		}
		else
		{
			a.${pp.prefix}val /= a.ival1;
		}
	}


	public static void prod (Aggregate a, $type value)
	{
		if (a.initialize ())
		{
			a.${pp.prefix}val = 1;
		}
		if (!a.isFinished ())
		{
			a.${pp.prefix}val *= value;
		}
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public static void sum (Aggregate a, int value)
	{
		if (a.initialize ())
		{
			a.ival = 0;
		}
		if (!a.isFinished ())
		{
			a.ival += value;
		}
	}
// generated
	public static void mean (Aggregate a, int value)
	{
		if (a.initialize ())
		{
			a.ival = 0;
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			a.ival += value;
			a.ival1++;
		}
		else
		{
			a.ival /= a.ival1;
		}
	}
// generated
// generated
	public static void prod (Aggregate a, int value)
	{
		if (a.initialize ())
		{
			a.ival = 1;
		}
		if (!a.isFinished ())
		{
			a.ival *= value;
		}
	}
// generated
// generated
// generated
	public static void sum (Aggregate a, long value)
	{
		if (a.initialize ())
		{
			a.lval = 0;
		}
		if (!a.isFinished ())
		{
			a.lval += value;
		}
	}
// generated
	public static void mean (Aggregate a, long value)
	{
		if (a.initialize ())
		{
			a.lval = 0;
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			a.lval += value;
			a.ival1++;
		}
		else
		{
			a.lval /= a.ival1;
		}
	}
// generated
// generated
	public static void prod (Aggregate a, long value)
	{
		if (a.initialize ())
		{
			a.lval = 1;
		}
		if (!a.isFinished ())
		{
			a.lval *= value;
		}
	}
// generated
// generated
// generated
	public static void sum (Aggregate a, float value)
	{
		if (a.initialize ())
		{
			a.fval = 0;
		}
		if (!a.isFinished ())
		{
			a.fval += value;
		}
	}
// generated
	public static void mean (Aggregate a, float value)
	{
		if (a.initialize ())
		{
			a.fval = 0;
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			a.fval += value;
			a.ival1++;
		}
		else
		{
			a.fval /= a.ival1;
		}
	}
// generated
// generated
	public static void prod (Aggregate a, float value)
	{
		if (a.initialize ())
		{
			a.fval = 1;
		}
		if (!a.isFinished ())
		{
			a.fval *= value;
		}
	}
// generated
// generated
// generated
	public static void sum (Aggregate a, double value)
	{
		if (a.initialize ())
		{
			a.dval = 0;
		}
		if (!a.isFinished ())
		{
			a.dval += value;
		}
	}
// generated
	public static void mean (Aggregate a, double value)
	{
		if (a.initialize ())
		{
			a.dval = 0;
			a.ival1 = 0;
		}
		if (!a.isFinished ())
		{
			a.dval += value;
			a.ival1++;
		}
		else
		{
			a.dval /= a.ival1;
		}
	}
// generated
// generated
	public static void prod (Aggregate a, double value)
	{
		if (a.initialize ())
		{
			a.dval = 1;
		}
		if (!a.isFinished ())
		{
			a.dval *= value;
		}
	}
// generated
//!! *# End of generated code


}
