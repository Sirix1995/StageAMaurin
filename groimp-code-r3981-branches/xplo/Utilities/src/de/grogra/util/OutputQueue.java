
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

import java.io.*;
import de.grogra.xl.util.*;

/**
 * This class represents a queue of <code>byte</code>, <code>int</code>,
 * <code>long</code>, <code>float</code>, <code>double</code>,
 * and <code>Object</code>-values.
 * For each type, a separate list is stored. 
 * 
 * @author Ole Kniemeyer
 */
public class OutputQueue extends OutputStream implements DataOutput
{
	static final int LOW_BITS = 12;
	static final int LOW_SIZE = 1 << LOW_BITS;
	static final int LOW_MASK = LOW_SIZE - 1;

/*!!
#set ($qtypes = ["byte", "int", "long", "float", "double", "Object"])
#set ($bilfda = ["b", "i", "l", "f", "d", "a"])

#foreach ($type in $qtypes)
$pp.setType($type)

	/**
	 * <code>${pp.bprefix}index</code> is the index into
	 * <code>${pp.bprefix}queue</code> where the next $type value will be written.
	 $C
	int ${pp.bprefix}index = 0;

	/**
	 * <code>${pp.bprefix}queue</code> is the storage for values
	 * of type ${type}.
	 $C
	$type[][] ${pp.bprefix}queue;
#end
!!*/
//!! #* Start of generated code
// generated
// generated
// generated
	/**
	 * <code>bindex</code> is the index into
	 * <code>bqueue</code> where the next byte value will be written.
	 */
	int bindex = 0;
// generated
	/**
	 * <code>bqueue</code> is the storage for values
	 * of type byte.
	 */
	byte[][] bqueue;
// generated
// generated
	/**
	 * <code>iindex</code> is the index into
	 * <code>iqueue</code> where the next int value will be written.
	 */
	int iindex = 0;
// generated
	/**
	 * <code>iqueue</code> is the storage for values
	 * of type int.
	 */
	int[][] iqueue;
// generated
// generated
	/**
	 * <code>lindex</code> is the index into
	 * <code>lqueue</code> where the next long value will be written.
	 */
	int lindex = 0;
// generated
	/**
	 * <code>lqueue</code> is the storage for values
	 * of type long.
	 */
	long[][] lqueue;
// generated
// generated
	/**
	 * <code>findex</code> is the index into
	 * <code>fqueue</code> where the next float value will be written.
	 */
	int findex = 0;
// generated
	/**
	 * <code>fqueue</code> is the storage for values
	 * of type float.
	 */
	float[][] fqueue;
// generated
// generated
	/**
	 * <code>dindex</code> is the index into
	 * <code>dqueue</code> where the next double value will be written.
	 */
	int dindex = 0;
// generated
	/**
	 * <code>dqueue</code> is the storage for values
	 * of type double.
	 */
	double[][] dqueue;
// generated
// generated
	/**
	 * <code>aindex</code> is the index into
	 * <code>aqueue</code> where the next Object value will be written.
	 */
	int aindex = 0;
// generated
	/**
	 * <code>aqueue</code> is the storage for values
	 * of type Object.
	 */
	Object[][] aqueue;
//!! *# End of generated code


	/**
	 * Creates a new <code>OutputQueue</code>.
	 * 
	 * @param usesObjectQueue <code>true</code> iff the queue of
	 * type <code>Object</code> will be used
	 */
	public OutputQueue (boolean usesObjectQueue)
	{
		bqueue = new byte[1][LOW_SIZE];
		iqueue = new int[1][LOW_SIZE];
		lqueue = new long[1][LOW_SIZE];
		fqueue = new float[1][LOW_SIZE];
		dqueue = new double[1][LOW_SIZE];
		aqueue = usesObjectQueue ? new Object[1][LOW_SIZE] : null;
	}


	public void clear ()
	{
		if (aqueue != null)
		{
			for (int i = 0; i < aqueue.length; i++)
			{
				if (aqueue[i] != null)
				{
					ObjectList.clear (aqueue[i], 0, LOW_SIZE);
				}
			}
		}
/*!!
#foreach ($pf in $bilfda)
		${pf}index = 0;
#end
!!*/
//!! #* Start of generated code
		bindex = 0;
		iindex = 0;
		lindex = 0;
		findex = 0;
		dindex = 0;
		aindex = 0;
//!! *# End of generated code
	}


	@Override
	public final void write (int b)
	{
		writeByte (b);
	}


	@Override
	public final void write (byte[] b)
	{
		write (b, 0, b.length);
	}


	@Override
	public final void write (byte[] b, int off, int len)
	{
		while (len > 0)
		{
			int n = Math.min (len, LOW_SIZE - (bindex & LOW_MASK));
			int i = bindex >> LOW_BITS;
			if (i == bqueue.length)
			{
				System.arraycopy (bqueue, 0, bqueue = new byte[Math.max (i * 2, 2)][], 0, i);
			}
			byte[] a = bqueue[i];
			if (a == null)
			{
				bqueue[i] = a = new byte[LOW_SIZE];
			}
			System.arraycopy (b, off, a, bindex & LOW_MASK, n);
			bindex += n;
			off += n;
			len -= n;
		}
	}


	public final void writeBytes (String s)
	{
		int l = s.length ();
		for (int i = 0; i < l; i++)
		{
			writeByte (s.charAt (i));
		}
	}


	public final void writeChars (String s)
	{
		int l = s.length ();
		for (int i = 0; i < l; i++)
		{
			char c = s.charAt (i);
			writeByte (c >> 8);
			writeByte (c);
		}
	}


	@Override
	public void flush ()
	{
	}


	@Override
	public void close ()
	{
	}


	public final void writeBoolean (boolean v)
	{
		writeByte (v ? (byte) 1 : (byte) 0);
	}


	public final void writeShort (int v)
	{
		writeByte (v >> 8);
		writeByte (v & 0xff);
	}


	public final void writeChar (int v)
	{
		writeByte (v >> 8);
		writeByte (v & 0xff);
	}

/*!!
#foreach ($type in $qtypes)
$pp.setType($type)

#if ($pp.byte)
	public final void writeByte (int v)
#elseif ($pp.Object)
	public final void writeObjectInQueue ($type v) 
#else
	public final void write$pp.Type ($type v)
#end
	{
		int i = ${pp.bprefix}index >> LOW_BITS;
		if (i == ${pp.bprefix}queue.length)
		{
			System.arraycopy (${pp.bprefix}queue, 0, ${pp.bprefix}queue = new $type[Math.max (i * 2, 2)][], 0, i);
		}
		$type[] a = ${pp.bprefix}queue[i];
		if (a == null)
		{
			${pp.bprefix}queue[i] = a = new $type[LOW_SIZE];
		}
		a[(${pp.bprefix}index++) & LOW_MASK] = ($type) v;
	}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public final void writeByte (int v)
	{
		int i = bindex >> LOW_BITS;
		if (i == bqueue.length)
		{
			System.arraycopy (bqueue, 0, bqueue = new byte[Math.max (i * 2, 2)][], 0, i);
		}
		byte[] a = bqueue[i];
		if (a == null)
		{
			bqueue[i] = a = new byte[LOW_SIZE];
		}
		a[(bindex++) & LOW_MASK] = (byte) v;
	}
// generated
// generated
	public final void writeInt (int v)
	{
		int i = iindex >> LOW_BITS;
		if (i == iqueue.length)
		{
			System.arraycopy (iqueue, 0, iqueue = new int[Math.max (i * 2, 2)][], 0, i);
		}
		int[] a = iqueue[i];
		if (a == null)
		{
			iqueue[i] = a = new int[LOW_SIZE];
		}
		a[(iindex++) & LOW_MASK] = (int) v;
	}
// generated
// generated
	public final void writeLong (long v)
	{
		int i = lindex >> LOW_BITS;
		if (i == lqueue.length)
		{
			System.arraycopy (lqueue, 0, lqueue = new long[Math.max (i * 2, 2)][], 0, i);
		}
		long[] a = lqueue[i];
		if (a == null)
		{
			lqueue[i] = a = new long[LOW_SIZE];
		}
		a[(lindex++) & LOW_MASK] = (long) v;
	}
// generated
// generated
	public final void writeFloat (float v)
	{
		int i = findex >> LOW_BITS;
		if (i == fqueue.length)
		{
			System.arraycopy (fqueue, 0, fqueue = new float[Math.max (i * 2, 2)][], 0, i);
		}
		float[] a = fqueue[i];
		if (a == null)
		{
			fqueue[i] = a = new float[LOW_SIZE];
		}
		a[(findex++) & LOW_MASK] = (float) v;
	}
// generated
// generated
	public final void writeDouble (double v)
	{
		int i = dindex >> LOW_BITS;
		if (i == dqueue.length)
		{
			System.arraycopy (dqueue, 0, dqueue = new double[Math.max (i * 2, 2)][], 0, i);
		}
		double[] a = dqueue[i];
		if (a == null)
		{
			dqueue[i] = a = new double[LOW_SIZE];
		}
		a[(dindex++) & LOW_MASK] = (double) v;
	}
// generated
// generated
	public final void writeObjectInQueue (Object v) 
	{
		int i = aindex >> LOW_BITS;
		if (i == aqueue.length)
		{
			System.arraycopy (aqueue, 0, aqueue = new Object[Math.max (i * 2, 2)][], 0, i);
		}
		Object[] a = aqueue[i];
		if (a == null)
		{
			aqueue[i] = a = new Object[LOW_SIZE];
		}
		a[(aindex++) & LOW_MASK] = (Object) v;
	}
//!! *# End of generated code

	public void writeObject (Object v) throws IOException
	{
		writeObjectInQueue (v);
	}


	public final void writeUTF (String s)
	{
		int lenIndex = bindex, len = s.length ();
		writeByte (0);
		writeByte (0);
		for (int i = 0; i < len; i++)
		{
			char c = s.charAt (i);
			if ((c != 0) && (c < 0x80))
			{
				writeByte (c);
			}
			else if (c < 0x800)
			{
				writeByte (0xc0 | (0x1f & (c >> 6)));
				writeByte (0x80 | (0x3f & c));
			}
			else
			{
				writeByte (0xe0 | (0x0f & (c >> 12)));
				writeByte (0x80 | (0x3f & (c >>  6)));
				writeByte (0x80 | (0x3f & c));
			}
		}
		len = bindex - lenIndex - 2;
		bqueue[lenIndex >> LOW_BITS][lenIndex & LOW_MASK] = (byte) (len >> 8);
		lenIndex++;
		bqueue[lenIndex >> LOW_BITS][lenIndex & LOW_MASK] = (byte) (len & 0xff);
	}



	public void dump (PrintStream out)
	{
		out.println (this);
/*!!
#foreach ($pf in $bilfda)
		out.print (${pf}index);
		out.println (":");
		for (int i = 0; i < ${pf}index; i++)
		{
			out.print (${pf}queue[i >> LOW_BITS][i & LOW_MASK]);
			out.print (' ');
		}
		out.println ();
#end
!!*/
//!! #* Start of generated code
		out.print (bindex);
		out.println (":");
		for (int i = 0; i < bindex; i++)
		{
			out.print (bqueue[i >> LOW_BITS][i & LOW_MASK]);
			out.print (' ');
		}
		out.println ();
		out.print (iindex);
		out.println (":");
		for (int i = 0; i < iindex; i++)
		{
			out.print (iqueue[i >> LOW_BITS][i & LOW_MASK]);
			out.print (' ');
		}
		out.println ();
		out.print (lindex);
		out.println (":");
		for (int i = 0; i < lindex; i++)
		{
			out.print (lqueue[i >> LOW_BITS][i & LOW_MASK]);
			out.print (' ');
		}
		out.println ();
		out.print (findex);
		out.println (":");
		for (int i = 0; i < findex; i++)
		{
			out.print (fqueue[i >> LOW_BITS][i & LOW_MASK]);
			out.print (' ');
		}
		out.println ();
		out.print (dindex);
		out.println (":");
		for (int i = 0; i < dindex; i++)
		{
			out.print (dqueue[i >> LOW_BITS][i & LOW_MASK]);
			out.print (' ');
		}
		out.println ();
		out.print (aindex);
		out.println (":");
		for (int i = 0; i < aindex; i++)
		{
			out.print (aqueue[i >> LOW_BITS][i & LOW_MASK]);
			out.print (' ');
		}
		out.println ();
//!! *# End of generated code
	}

}
