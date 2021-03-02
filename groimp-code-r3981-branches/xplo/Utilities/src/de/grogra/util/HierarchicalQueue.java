
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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UTFDataFormatException;

import de.grogra.reflect.TypeLoader;
import de.grogra.xl.util.ByteList;
import de.grogra.xl.util.ClassLoaderObjectInputStream;
import de.grogra.xl.util.DoubleList;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.LongList;
import de.grogra.xl.util.ObjectList;

/**
 * A <code>HierarchicalQueue</code> equips an <code>OutputQueue</code>
 * with a record-like, hierarchical structure. Each record is called
 * an item, the type of which is identified by the first
 * <code>int</code>-value of the record. Items follow each other
 * in a linked list (the link is established by relative
 * addresses which are stored in an item's header structure, see below).
 * The item of type {@link #BEGIN_LEVEL} indicates a nesting of items,
 * it contains another linked list of items.
 * <p>
 * A whole item is represented by
 * the following structure in the <code>int</code>-queue:
 * <ol>
 * <li><code>int</code>-value at offset 0: item type
 * <li><code>int</code>-value at offset 1: difference between this item's
 * address (the absolute index of offset 0) and the address of the
 * previous item. If no previous item exists, -1 is used for the latter address.
 * The first item which is contained in a new level has no previous item.
 * <li>The following <code>int</code>s contain the total number of <code>int</code>,
 * <code>byte</code>, <code>long</long>, <code>float</code>,
 * <code>double</code>, <code>Object</code>-values in this order. If
 * the total number of values of one of these types is always the same
 * for the item type, this number is not present in the structure. The
 * total number of values for an item type is set by the method
 * {@link #setItemSize(int, int, int, int, int, int, int)}.
 * <li>The following <code>int</code>s represent the data of the
 * item in its specific format 
 * </ol>
 * <p>
 * An item of type {@link #BEGIN_LEVEL} is used to store the whole
 * information of the nested items it contains. It uses an
 * additional <code>int</code>-value as first value of its data:
 * Its value is the difference between the address of the
 * last contained item and this item.
 * 
 * @author Ole Kniemeyer
 */
public class HierarchicalQueue extends OutputQueue implements ObjectOutput
{

/*!!
#set ($qtypes = ["byte", "int", "long", "float", "double", "Object"])
#set ($nbqtypes = ["int", "long", "float", "double", "Object"])
#set ($bilfda = ["b", "i", "l", "f", "d", "a"])
#set ($iblfda = ["i", "b", "l", "f", "d", "a"])
#set ($blfda = ["b", "l", "f", "d", "a"])

	public final class Cursor
	{
#foreach ($pf in $bilfda)
		int ${pf}start;
#end
		int previousIndex;
		int sp;
	}

!!*/
//!! #* Start of generated code
// generated
	public final class Cursor
	{
		int bstart;
		int istart;
		int lstart;
		int fstart;
		int dstart;
		int astart;
		int previousIndex;
		int sp;
	}
// generated
//!! *# End of generated code

	public static class Data
	{
		private static final int DATA_BEGIN = 0xda7abe61;
		private static final int DATA_END = 0xe2d0fdad;

/*!!
#foreach ($type in $qtypes)
$pp.setType($type)

		int ${pp.bprefix}index;
		$type[][] ${pp.bprefix}queue;
#end
!!*/
//!! #* Start of generated code
// generated
// generated
		int bindex;
		byte[][] bqueue;
// generated
// generated
		int iindex;
		int[][] iqueue;
// generated
// generated
		int lindex;
		long[][] lqueue;
// generated
// generated
		int findex;
		float[][] fqueue;
// generated
// generated
		int dindex;
		double[][] dqueue;
// generated
// generated
		int aindex;
		Object[][] aqueue;
//!! *# End of generated code

		protected Data ()
		{
		}


		Data set (HierarchicalQueue queue, boolean clone)
		{
/*!!
			if (clone)
			{
#foreach ($type in $qtypes)
$pp.setType($type)

#if ($pp.Object)
				if (queue.aqueue == null)
				{
					aqueue = null;
				}
				else
#end
				{
					${pp.bprefix}queue = new $type[(queue.${pp.bprefix}index + LOW_MASK) >> LOW_BITS][];
					for (int i = 0; i < ${pp.bprefix}queue.length; i++)
					{
						${pp.bprefix}queue[i] = queue.${pp.bprefix}queue[i].clone ();
					}
					${pp.bprefix}index = queue.${pp.bprefix}index;
				}
#end
			}
			else
			{
#foreach ($pf in $bilfda)
				${pf}queue = queue.${pf}queue;
				${pf}index = queue.${pf}index;
#end
			}
			return this;
!!*/
//!! #* Start of generated code
			if (clone)
			{
// generated
// generated
				{
					bqueue = new byte[(queue.bindex + LOW_MASK) >> LOW_BITS][];
					for (int i = 0; i < bqueue.length; i++)
					{
						bqueue[i] = queue.bqueue[i].clone ();
					}
					bindex = queue.bindex;
				}
// generated
// generated
				{
					iqueue = new int[(queue.iindex + LOW_MASK) >> LOW_BITS][];
					for (int i = 0; i < iqueue.length; i++)
					{
						iqueue[i] = queue.iqueue[i].clone ();
					}
					iindex = queue.iindex;
				}
// generated
// generated
				{
					lqueue = new long[(queue.lindex + LOW_MASK) >> LOW_BITS][];
					for (int i = 0; i < lqueue.length; i++)
					{
						lqueue[i] = queue.lqueue[i].clone ();
					}
					lindex = queue.lindex;
				}
// generated
// generated
				{
					fqueue = new float[(queue.findex + LOW_MASK) >> LOW_BITS][];
					for (int i = 0; i < fqueue.length; i++)
					{
						fqueue[i] = queue.fqueue[i].clone ();
					}
					findex = queue.findex;
				}
// generated
// generated
				{
					dqueue = new double[(queue.dindex + LOW_MASK) >> LOW_BITS][];
					for (int i = 0; i < dqueue.length; i++)
					{
						dqueue[i] = queue.dqueue[i].clone ();
					}
					dindex = queue.dindex;
				}
// generated
// generated
				if (queue.aqueue == null)
				{
					aqueue = null;
				}
				else
				{
					aqueue = new Object[(queue.aindex + LOW_MASK) >> LOW_BITS][];
					for (int i = 0; i < aqueue.length; i++)
					{
						aqueue[i] = queue.aqueue[i].clone ();
					}
					aindex = queue.aindex;
				}
			}
			else
			{
				bqueue = queue.bqueue;
				bindex = queue.bindex;
				iqueue = queue.iqueue;
				iindex = queue.iindex;
				lqueue = queue.lqueue;
				lindex = queue.lindex;
				fqueue = queue.fqueue;
				findex = queue.findex;
				dqueue = queue.dqueue;
				dindex = queue.dindex;
				aqueue = queue.aqueue;
				aindex = queue.aindex;
			}
			return this;
//!! *# End of generated code
		}


		public final void write (DataOutput out) throws IOException
		{
			if (aqueue != null)
			{
				throw new UnsupportedOperationException ();
			}
			writeImpl (out);
		}


		public final void write (ObjectOutput out) throws IOException
		{
			if (aqueue == null)
			{
				throw new UnsupportedOperationException ();
			}
			writeImpl (out);
		}



		private void writeImpl (DataOutput out) throws IOException
		{
			out.writeInt (DATA_BEGIN);
			int n = bindex;
			out.writeInt (n);
			for (int i = 0; i < bqueue.length; i++)
			{
				int m = Math.min (n, LOW_SIZE);
				out.write (bqueue[i], 0, m);
				n -= m;
				if (n == 0)
				{
					break;
				}
			}
/*!!
#foreach ($type in $nbqtypes)
$pp.setType($type)
#if ($pp.Object)
			if (aqueue != null)
			{
				ObjectOutput o = (ObjectOutput) out;
	#set ($out = "o")
#else
			{
	#set ($out = "out")
#end
				n = ${pp.bprefix}index;
				${out}.writeInt (n);
				$type[][] a = ${pp.bprefix}queue;
				for (int i = 0; i < n; i++)
				{
					${out}.write${pp.Type} (a[i >> LOW_BITS][i & LOW_MASK]);
				}
			}
#end
!!*/
//!! #* Start of generated code
// generated
			{
					n = iindex;
				out.writeInt (n);
				int[][] a = iqueue;
				for (int i = 0; i < n; i++)
				{
					out.writeInt (a[i >> LOW_BITS][i & LOW_MASK]);
				}
			}
// generated
			{
					n = lindex;
				out.writeInt (n);
				long[][] a = lqueue;
				for (int i = 0; i < n; i++)
				{
					out.writeLong (a[i >> LOW_BITS][i & LOW_MASK]);
				}
			}
// generated
			{
					n = findex;
				out.writeInt (n);
				float[][] a = fqueue;
				for (int i = 0; i < n; i++)
				{
					out.writeFloat (a[i >> LOW_BITS][i & LOW_MASK]);
				}
			}
// generated
			{
					n = dindex;
				out.writeInt (n);
				double[][] a = dqueue;
				for (int i = 0; i < n; i++)
				{
					out.writeDouble (a[i >> LOW_BITS][i & LOW_MASK]);
				}
			}
// generated
			if (aqueue != null)
			{
				ObjectOutput o = (ObjectOutput) out;
					n = aindex;
				o.writeInt (n);
				Object[][] a = aqueue;
				for (int i = 0; i < n; i++)
				{
					o.writeObject (a[i >> LOW_BITS][i & LOW_MASK]);
				}
			}
//!! *# End of generated code
			out.writeInt (DATA_END);
		}


		public final void read (DataInput in) throws IOException
		{
			if (aqueue != null)
			{
				throw new UnsupportedOperationException ();
			}
			try
			{
				readImpl (in);
			}
			catch (ClassNotFoundException e)
			{
				throw (AssertionError) new AssertionError ().initCause (e);
			}
		}


		public final void read (ObjectInput in)
			throws IOException, ClassNotFoundException
		{
			if (aqueue == null)
			{
				throw new UnsupportedOperationException ();
			}
			readImpl (in);
		}


		private final void readImpl (DataInput in)
			throws IOException, ClassNotFoundException
		{
			if (in.readInt () != DATA_BEGIN)
			{
				throw new StreamCorruptedException ();
			}
			int n = in.readInt ();
			bindex = n;
			bqueue = new byte[(n + LOW_MASK) >> LOW_BITS][LOW_SIZE];
			for (int i = 0; i < bqueue.length; i++)
			{
				int m = Math.min (n, LOW_SIZE);
				in.readFully (bqueue[i], 0, m);
				n -= m;
			}
/*!!
#foreach ($type in $nbqtypes)
$pp.setType($type)
#if ($pp.Object)
			if (aqueue != null)
			{
				ObjectInput oi = (ObjectInput) in;
	#set ($in = "oi")
#else
			{
	#set ($in = "in")
#end
				n = ${in}.readInt ();
				${pp.bprefix}index = n;
				$type[][] a = new $type[(n + LOW_MASK) >> LOW_BITS][LOW_SIZE];
				for (int i = 0; i < n; i++)
				{
					a[i >> LOW_BITS][i & LOW_MASK] = ${in}.read${pp.Type} ();
				}
				${pp.bprefix}queue = a;
			}
#end
!!*/
//!! #* Start of generated code
// generated
			{
					n = in.readInt ();
				iindex = n;
				int[][] a = new int[(n + LOW_MASK) >> LOW_BITS][LOW_SIZE];
				for (int i = 0; i < n; i++)
				{
					a[i >> LOW_BITS][i & LOW_MASK] = in.readInt ();
				}
				iqueue = a;
			}
// generated
			{
					n = in.readInt ();
				lindex = n;
				long[][] a = new long[(n + LOW_MASK) >> LOW_BITS][LOW_SIZE];
				for (int i = 0; i < n; i++)
				{
					a[i >> LOW_BITS][i & LOW_MASK] = in.readLong ();
				}
				lqueue = a;
			}
// generated
			{
					n = in.readInt ();
				findex = n;
				float[][] a = new float[(n + LOW_MASK) >> LOW_BITS][LOW_SIZE];
				for (int i = 0; i < n; i++)
				{
					a[i >> LOW_BITS][i & LOW_MASK] = in.readFloat ();
				}
				fqueue = a;
			}
// generated
			{
					n = in.readInt ();
				dindex = n;
				double[][] a = new double[(n + LOW_MASK) >> LOW_BITS][LOW_SIZE];
				for (int i = 0; i < n; i++)
				{
					a[i >> LOW_BITS][i & LOW_MASK] = in.readDouble ();
				}
				dqueue = a;
			}
// generated
			if (aqueue != null)
			{
				ObjectInput oi = (ObjectInput) in;
					n = oi.readInt ();
				aindex = n;
				Object[][] a = new Object[(n + LOW_MASK) >> LOW_BITS][LOW_SIZE];
				for (int i = 0; i < n; i++)
				{
					a[i >> LOW_BITS][i & LOW_MASK] = oi.readObject ();
				}
				aqueue = a;
			}
//!! *# End of generated code
			if (in.readInt () != DATA_END)
			{
				throw new StreamCorruptedException ();
			}
		}


		public final int getUsedMemoryForPrimitives ()
		{
			return (bqueue.length + (iqueue.length + fqueue.length) * 4
					+ (lqueue.length + dqueue.length) * 8) * LOW_SIZE;
		}

	}


	public static final int NEXT_EXISTS = 1 << 30;
	public static final int IS_FINISHED = 1 << 31;
	public static final int ITEM_MASK =   0x0fff;

	public static final int MIN_UNUSED_BIT = 12;
	public static final int MAX_UNUSED_BIT = 29;

	public static final int ITEM_AND_BITS_MASK = (1 << (MAX_UNUSED_BIT + 1)) - 1;

	public static final int BEGIN_LEVEL = 0;
	public static final int MIN_UNUSED_ITEM = 1;

	protected final ObjectOutputStream out;

	final int bheader;

	private IntList stack = new IntList ();
	int previousIndex = -1;

	final boolean backLinks;

/*!!
#foreach ($pf in $bilfda)
	int[] ${pf}size = new int[32];
#end
!!*/
//!! #* Start of generated code
	int[] bsize = new int[32];
	int[] isize = new int[32];
	int[] lsize = new int[32];
	int[] fsize = new int[32];
	int[] dsize = new int[32];
	int[] asize = new int[32];
//!! *# End of generated code

	int[] ioffset = new int[32];


	public class Reader extends InputStream implements ObjectInput
	{
		protected final ObjectInputStream in;

/*!!
#foreach ($pf in $bilfda)
		private int ${pf}head = 0, ${pf}start = 0;
#end
!!*/
//!! #* Start of generated code
		private int bhead = 0, bstart = 0;
		private int ihead = 0, istart = 0;
		private int lhead = 0, lstart = 0;
		private int fhead = 0, fstart = 0;
		private int dhead = 0, dstart = 0;
		private int ahead = 0, astart = 0;
//!! *# End of generated code

		private IntList cursorStack = new IntList ();


		public Reader (TypeLoader loader)
		{
			super ();
			try
			{
				in = new ClassLoaderObjectInputStream (this, loader);
				assert bhead == bheader;
				bstart = bhead;
			}
			catch (IOException e)
			{
				throw new AssertionError (e);
			}
		}


		@Override
		public void close ()
		{
		}


		public final int resetCursor ()
		{
/*!!
#foreach ($pf in $bilfda)
			${pf}start = 0;
#end
!!*/
//!! #* Start of generated code
			bstart = 0;
			istart = 0;
			lstart = 0;
			fstart = 0;
			dstart = 0;
			astart = 0;
//!! *# End of generated code
			bstart = bheader;
			return readItem ();
		}


		public final int moveTo (Cursor c)
		{
/*!!
#foreach ($pf in $bilfda)
			${pf}start = c.${pf}start;
#end
!!*/
//!! #* Start of generated code
			bstart = c.bstart;
			istart = c.istart;
			lstart = c.lstart;
			fstart = c.fstart;
			dstart = c.dstart;
			astart = c.astart;
//!! *# End of generated code
			return readItem ();
		}


		public final boolean isAt (Cursor c)
		{
/*!!
#foreach ($pf in $bilfda)
			if (${pf}start != c.${pf}start)
			{
				return false;
			}
#end
!!*/
//!! #* Start of generated code
			if (bstart != c.bstart)
			{
				return false;
			}
			if (istart != c.istart)
			{
				return false;
			}
			if (lstart != c.lstart)
			{
				return false;
			}
			if (fstart != c.fstart)
			{
				return false;
			}
			if (dstart != c.dstart)
			{
				return false;
			}
			if (astart != c.astart)
			{
				return false;
			}
//!! *# End of generated code
			return true;
		}


		public final int moveToCurrent ()
		{
			int t = previousIndex, i = iqueue[t >> LOW_BITS][t & LOW_MASK] & ITEM_MASK;
			if (backLinks)
			{
				t++;
			}
/*!!
#foreach ($pf in $iblfda)
			${pf}start = ${pf}index
				- ((${pf}size[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : ${pf}size[i]);
#end
!!*/
//!! #* Start of generated code
			istart = iindex
				- ((isize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : isize[i]);
			bstart = bindex
				- ((bsize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : bsize[i]);
			lstart = lindex
				- ((lsize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : lsize[i]);
			fstart = findex
				- ((fsize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : fsize[i]);
			dstart = dindex
				- ((dsize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : dsize[i]);
			astart = aindex
				- ((asize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : asize[i]);
//!! *# End of generated code
			return readItem ();
		}


		public final void pushCursor ()
		{
			cursorStack
/*!!
#set ($i = 0)
#foreach ($pf in $bilfda)
				.push (${pf}start)
				.push (${pf}head)
#set ($i = $i + 2)
#end
!!*/
//!! #* Start of generated code
				.push (bstart)
				.push (bhead)
				.push (istart)
				.push (ihead)
				.push (lstart)
				.push (lhead)
				.push (fstart)
				.push (fhead)
				.push (dstart)
				.push (dhead)
				.push (astart)
				.push (ahead)
//!! *# End of generated code
				;
		}


		public final void popCursor ()
		{
			int[] a = cursorStack.elements;
			int sp = cursorStack.size () - 12;
/*!!
#set ($i = 0)
#foreach ($pf in $bilfda)
			${pf}start = a[sp + $i];
			${pf}head = a[sp + $i + 1];
#set ($i = $i + 2)
#end
!!*/
//!! #* Start of generated code
			bstart = a[sp + 0];
			bhead = a[sp + 0 + 1];
			istart = a[sp + 2];
			ihead = a[sp + 2 + 1];
			lstart = a[sp + 4];
			lhead = a[sp + 4 + 1];
			fstart = a[sp + 6];
			fhead = a[sp + 6 + 1];
			dstart = a[sp + 8];
			dhead = a[sp + 8 + 1];
			astart = a[sp + 10];
			ahead = a[sp + 10 + 1];
//!! *# End of generated code
			cursorStack.setSize (sp);
		}


		public final int readItem ()
		{
			if (istart >= iindex)
			{
				return -1;
			}
/*!!
#foreach ($pf in $blfda)
			${pf}head = ${pf}start;
#end
!!*/
//!! #* Start of generated code
			bhead = bstart;
			lhead = lstart;
			fhead = fstart;
			dhead = dstart;
			ahead = astart;
//!! *# End of generated code
			int i = iqueue[istart >> LOW_BITS][istart & LOW_MASK];
			ihead = istart + ioffset[i & ITEM_MASK];
			return i & ITEM_AND_BITS_MASK;
		}


		public final int next ()
		{
			int i = iqueue[istart >> LOW_BITS][istart & LOW_MASK];
			int t = istart;
			if (backLinks)
			{
				t++;
			}
			if ((i & NEXT_EXISTS) == 0)
			{
				return -1;
			}
			i &= ITEM_MASK;
/*!!
#foreach ($pf in $iblfda)
			${pf}start += (${pf}size[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : ${pf}size[i];
#end
!!*/
//!! #* Start of generated code
			istart += (isize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : isize[i];
			bstart += (bsize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : bsize[i];
			lstart += (lsize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : lsize[i];
			fstart += (fsize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : fsize[i];
			dstart += (dsize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : dsize[i];
			astart += (asize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : asize[i];
//!! *# End of generated code
			return readItem ();
		}


		public final int previous ()
		{
			if (!backLinks)
			{
				throw new UnsupportedOperationException ();
			}
			int i = istart + 1;
			int t = istart - iqueue[i >> LOW_BITS][i & LOW_MASK] + 1;
			if (t <= 0)
			{
				return -1;
			}
			i = t - 1;
			i = iqueue[i >> LOW_BITS][i & LOW_MASK] & ITEM_MASK;
/*!!
#foreach ($pf in $iblfda)
			${pf}start -= (${pf}size[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : ${pf}size[i];
#end
!!*/
//!! #* Start of generated code
			istart -= (isize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : isize[i];
			bstart -= (bsize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : bsize[i];
			lstart -= (lsize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : lsize[i];
			fstart -= (fsize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : fsize[i];
			dstart -= (dsize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : dsize[i];
			astart -= (asize[i] < 0) ? iqueue[++t >> LOW_BITS][t & LOW_MASK] : asize[i];
//!! *# End of generated code
			return readItem ();
		}


		public final void enter (boolean last) throws IOException
		{
			if ((readItem () & ITEM_MASK) != BEGIN_LEVEL)
			{
				throw new IOException
					("The current item is not a BEGIN_LEVEL item, "
					 + "its item id is " + readItem ());
			}
			int c = iqueue[ihead >> LOW_BITS][ihead & LOW_MASK];
			if (c < 0)
			{
				throw new IOException ("Empty nested item list");
			}
			pushCursor ();
			if (!last)
			{
				c = ioffset[BEGIN_LEVEL] + 1;
			}
			else
			{
				assert (iqueue[istart >> LOW_BITS][istart & LOW_MASK] & IS_FINISHED) != 0;
				int t = istart + 1;
				if (backLinks)
				{
					t++;
				}
				int u = t + c;
				int i = istart + c;
				i = iqueue[i >> LOW_BITS][i & LOW_MASK] & ITEM_MASK;
				if (iqueue[t >> LOW_BITS][t & LOW_MASK] - ((isize[i] < 0) ? iqueue[u >> LOW_BITS][u & LOW_MASK] : isize[i])
					!= c)
				{
					throw new AssertionError ();
				}
/*!!
#set ($i = 0)
#foreach ($pf in $blfda)
				${pf}start += iqueue[++t >> LOW_BITS][t & LOW_MASK]
					- ((${pf}size[i] < 0) ? iqueue[++u >> LOW_BITS][u & LOW_MASK] : ${pf}size[i]);
#set ($i = $i + 1)
#end
!!*/
//!! #* Start of generated code
				bstart += iqueue[++t >> LOW_BITS][t & LOW_MASK]
					- ((bsize[i] < 0) ? iqueue[++u >> LOW_BITS][u & LOW_MASK] : bsize[i]);
				lstart += iqueue[++t >> LOW_BITS][t & LOW_MASK]
					- ((lsize[i] < 0) ? iqueue[++u >> LOW_BITS][u & LOW_MASK] : lsize[i]);
				fstart += iqueue[++t >> LOW_BITS][t & LOW_MASK]
					- ((fsize[i] < 0) ? iqueue[++u >> LOW_BITS][u & LOW_MASK] : fsize[i]);
				dstart += iqueue[++t >> LOW_BITS][t & LOW_MASK]
					- ((dsize[i] < 0) ? iqueue[++u >> LOW_BITS][u & LOW_MASK] : dsize[i]);
				astart += iqueue[++t >> LOW_BITS][t & LOW_MASK]
					- ((asize[i] < 0) ? iqueue[++u >> LOW_BITS][u & LOW_MASK] : asize[i]);
//!! *# End of generated code
			}
			istart += c;
		}


		public final void leave () throws IOException
		{
			if (cursorStack.size () < 12)
			{
				throw new IOException ("There is no superordinate item");
			}
			popCursor ();
			if ((readItem () & ITEM_MASK) != BEGIN_LEVEL)
			{
				throw new IOException ("Queue corrupted: " + readItem ());
			}
		}


		@Override
		public final int read ()
		{
			return (bqueue[bhead >> LOW_BITS][bhead++ & LOW_MASK]) & 0xff;
		}


		@Override
		public final int read (byte[] b)
		{
			read (b, 0, b.length);
			return b.length;
		}


		@Override
		public final int read (byte[] b, int off, int len)
		{
			int lenOrig = len;
			while (len > 0)
			{
				int n = Math.min (len, LOW_SIZE - (bhead & LOW_MASK));
				System.arraycopy (bqueue[bhead >> LOW_BITS], bhead & LOW_MASK, b, off, n);
				bhead += n;
				off += n;
				len -= n;
			}
			return lenOrig;
		}


		public final void readFully (byte[] b)
		{
			read (b, 0, b.length);
		}


		public final void readFully (byte[] b, int off, int len)
		{
			read (b, off, len);
		}


		public final boolean readBoolean ()
		{
			return read () != 0;
		}


		public final short readShort ()
		{
			return (short) ((read () << 8) | read ());
		}


		public final int readUnsignedByte ()
		{
			return read ();
		}


		public final int readUnsignedShort ()
		{
			return (read () << 8) | read ();
		}


		public final char readChar ()
		{
			return (char) ((read () << 8) | read ());
		}

/*!!
#foreach ($type in $qtypes)
$pp.setType($type)

#if ($pp.Object)
		public final Object readObjectInQueue ()
#else
		public final $type read$pp.Type ()
#end
		{
			int i = ${pp.bprefix}head++;
			return ${pp.bprefix}queue[i >> LOW_BITS][i & LOW_MASK];
		}


#if ($pp.Object)
		public final void skipObjectInQueue ()
#else
		public final void skip$pp.Type ()
#end
		{
			++${pp.bprefix}head;
		}


		public final $type peek$pp.Type ()
		{
			int i = ${pp.bprefix}head;
			return ${pp.bprefix}queue[i >> LOW_BITS][i & LOW_MASK];
		}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
		public final byte readByte ()
		{
			int i = bhead++;
			return bqueue[i >> LOW_BITS][i & LOW_MASK];
		}
// generated
// generated
		public final void skipByte ()
		{
			++bhead;
		}
// generated
// generated
		public final byte peekByte ()
		{
			int i = bhead;
			return bqueue[i >> LOW_BITS][i & LOW_MASK];
		}
// generated
// generated
		public final int readInt ()
		{
			int i = ihead++;
			return iqueue[i >> LOW_BITS][i & LOW_MASK];
		}
// generated
// generated
		public final void skipInt ()
		{
			++ihead;
		}
// generated
// generated
		public final int peekInt ()
		{
			int i = ihead;
			return iqueue[i >> LOW_BITS][i & LOW_MASK];
		}
// generated
// generated
		public final long readLong ()
		{
			int i = lhead++;
			return lqueue[i >> LOW_BITS][i & LOW_MASK];
		}
// generated
// generated
		public final void skipLong ()
		{
			++lhead;
		}
// generated
// generated
		public final long peekLong ()
		{
			int i = lhead;
			return lqueue[i >> LOW_BITS][i & LOW_MASK];
		}
// generated
// generated
		public final float readFloat ()
		{
			int i = fhead++;
			return fqueue[i >> LOW_BITS][i & LOW_MASK];
		}
// generated
// generated
		public final void skipFloat ()
		{
			++fhead;
		}
// generated
// generated
		public final float peekFloat ()
		{
			int i = fhead;
			return fqueue[i >> LOW_BITS][i & LOW_MASK];
		}
// generated
// generated
		public final double readDouble ()
		{
			int i = dhead++;
			return dqueue[i >> LOW_BITS][i & LOW_MASK];
		}
// generated
// generated
		public final void skipDouble ()
		{
			++dhead;
		}
// generated
// generated
		public final double peekDouble ()
		{
			int i = dhead;
			return dqueue[i >> LOW_BITS][i & LOW_MASK];
		}
// generated
// generated
		public final Object readObjectInQueue ()
		{
			int i = ahead++;
			return aqueue[i >> LOW_BITS][i & LOW_MASK];
		}
// generated
// generated
		public final void skipObjectInQueue ()
		{
			++ahead;
		}
// generated
// generated
		public final Object peekObject ()
		{
			int i = ahead;
			return aqueue[i >> LOW_BITS][i & LOW_MASK];
		}
//!! *# End of generated code

		public final String readUTF () throws UTFDataFormatException
		{
			byte[][] b = bqueue;
			int t = bhead;
			int u = ((b[t >> LOW_BITS][t++ & LOW_MASK] & 0xff) << 8) | (b[t >> LOW_BITS][t++ & LOW_MASK] & 0xff);
			char[] a = new char[u];
			int i = 0;
			u += t;
			while (t < u)
			{
				int c = b[t >> LOW_BITS][t++ & LOW_MASK];
				if ((c & 0x80) == 0)
				{
					a[i++] = (char) (c & 0x7f);
				}
				else if ((c & 0xe0) == 0xc0)
				{
					int n = b[t >> LOW_BITS][t++ & LOW_MASK];
					if ((n & 0xc0) != 0x80)
					{
						throw new UTFDataFormatException ();
					}
					a[i++] = (char) (((c & 0x1f) << 6) | (n & 0x3f));
				}
				else if ((c & 0xf0) == 0xe0)
				{
					int n = b[t >> LOW_BITS][t++ & LOW_MASK];
					if ((n & 0xc0) != 0x80)
					{
						throw new UTFDataFormatException ();
					}
					c = ((c & 0x0f) << 6) | (n & 0x3f);
					n = b[t >> LOW_BITS][t++ & LOW_MASK];
					if ((n & 0xc0) != 0x80)
					{
						throw new UTFDataFormatException ();
					}
					a[i++] = (char) ((c << 6) | (n & 0x3f));
				}
				else
				{
					throw new UTFDataFormatException ();
				}
			}
			if (t != u)
			{
				throw new UTFDataFormatException ();
			}
			bhead = t;
			return new String (a, 0, i);
		}


		public final String readLine ()
		{
			StringBuffer b = new StringBuffer ();
			while (true)
			{
				int c = read ();
				if (c == '\n')
				{
					return b.toString ();
				}
				else if (c == '\r')
				{
					if (bqueue[bhead >> LOW_BITS][bhead & LOW_MASK] == '\n')
					{
						bhead++;
					}
					return b.toString ();
				}
				b.append ((char) c);
			}
		}


		@Override
		public final long skip (long n)
		{
			bhead += n;
			return n;
		}


		public final int skipBytes (int n)
		{
			bhead += n;
			return n;
		}


		@Override
		public final int available ()
		{
			return (bindex - bhead) + 4 * (iindex - ihead + findex - fhead)
				+ 8 * (lindex - lhead + dindex - dhead);
		}


		public final Object readObject ()
			throws IOException, ClassNotFoundException
		{
			return (aqueue != null) ? readObjectInQueue () : in.readObject ();
		}


		public final Object readObjectInStream ()
			throws IOException, ClassNotFoundException
		{
			return in.readObject ();
		}


		public final void readSkipBlock ()
		{
			ihead += 6;
		}


		public final void skipBoolean ()
		{
			++bhead;
		}


		public final void skipShort ()
		{
			bhead += 2;
		}


		public final void skipChar ()
		{
			bhead += 2;
		}


		public final void skipBlock ()
		{
			int p = ihead;
/*!!
#foreach ($pf in $iblfda)
			${pf}head += iqueue[p >> LOW_BITS][p++ & LOW_MASK];
#end
!!*/
//!! #* Start of generated code
			ihead += iqueue[p >> LOW_BITS][p++ & LOW_MASK];
			bhead += iqueue[p >> LOW_BITS][p++ & LOW_MASK];
			lhead += iqueue[p >> LOW_BITS][p++ & LOW_MASK];
			fhead += iqueue[p >> LOW_BITS][p++ & LOW_MASK];
			dhead += iqueue[p >> LOW_BITS][p++ & LOW_MASK];
			ahead += iqueue[p >> LOW_BITS][p++ & LOW_MASK];
//!! *# End of generated code
		}


		public final void skipUTF ()
		{
			bhead += (((bqueue[bhead >> LOW_BITS][bhead & LOW_MASK] & 0xff) << 8)
					  | (bqueue[(bhead + 1) >> LOW_BITS][(bhead + 1) & LOW_MASK] & 0xff)) + 2;
		}

	}


	public HierarchicalQueue (boolean usesObjectQueue, boolean createBackLinks)
	{
		super (usesObjectQueue);
		backLinks = createBackLinks;
		try
		{
			out = new ObjectOutputStream (this);
			out.flush ();
		}
		catch (IOException e)
		{
			throw new AssertionError (e);
		}
		bheader = bindex;
		setItemSize (BEGIN_LEVEL, -1, -1, -1, -1, -1, usesObjectQueue ? -1 : 0);
	}


	@Override
	public void clear ()
	{
		super.clear ();
		bindex = bheader;
		previousIndex = -1;
		stack.clear ();
	}


	public final void setItemSize (int item, int bsize, int isize, int lsize,
								   int fsize, int dsize, int asize)
	{
		if ((aqueue == null) && (asize != 0))
		{
			throw new IllegalArgumentException
				("asize must be 0 if no object queue is used");
		}
		item &= ITEM_MASK;
/*!!
		if (item >= ioffset.length)
		{
			int len = ioffset.length;
#foreach ($pf in ["b", "i", "l", "f", "d", "a", "x"])
#if ($pf == "x")
  #set ($array = "ioffset")
#else
  #set ($array = "this.${pf}size")
#end

			IntList.arraycopy ($array, 0, $array = new int[item + 10], 0, len);
#end
		}
		int c = 1;
		if (backLinks)
		{
			c++;
		}
#foreach ($pf in $bilfda)
		if ((this.${pf}size[item] = ${pf}size) < 0)
		{
			c++;
		}
#end
!!*/
//!! #* Start of generated code
		if (item >= ioffset.length)
		{
			int len = ioffset.length;
// generated
			IntList.arraycopy (this.bsize, 0, this.bsize = new int[item + 10], 0, len);
// generated
			IntList.arraycopy (this.isize, 0, this.isize = new int[item + 10], 0, len);
// generated
			IntList.arraycopy (this.lsize, 0, this.lsize = new int[item + 10], 0, len);
// generated
			IntList.arraycopy (this.fsize, 0, this.fsize = new int[item + 10], 0, len);
// generated
			IntList.arraycopy (this.dsize, 0, this.dsize = new int[item + 10], 0, len);
// generated
			IntList.arraycopy (this.asize, 0, this.asize = new int[item + 10], 0, len);
// generated
			IntList.arraycopy (ioffset, 0, ioffset = new int[item + 10], 0, len);
		}
		int c = 1;
		if (backLinks)
		{
			c++;
		}
		if ((this.bsize[item] = bsize) < 0)
		{
			c++;
		}
		if ((this.isize[item] = isize) < 0)
		{
			c++;
		}
		if ((this.lsize[item] = lsize) < 0)
		{
			c++;
		}
		if ((this.fsize[item] = fsize) < 0)
		{
			c++;
		}
		if ((this.dsize[item] = dsize) < 0)
		{
			c++;
		}
		if ((this.asize[item] = asize) < 0)
		{
			c++;
		}
//!! *# End of generated code

		ioffset[item] = c;
		if (isize >= 0)
		{
			this.isize[item] = isize + c;
		}
	}


	public final void beginLevel ()
	{
		stack
/*!!
#set ($i = 0)
#foreach ($pf in $bilfda)
			.push (${pf}index)
#set ($i = $i + 1)
#end
!!*/
//!! #* Start of generated code
			.push (bindex)
			.push (iindex)
			.push (lindex)
			.push (findex)
			.push (dindex)
			.push (aindex)
//!! *# End of generated code
			.push (previousIndex);
		writeItem (BEGIN_LEVEL);
		writeInt (-1);
		stack.push (previousIndex);
		previousIndex = -1;
	}


	public final void endLevel ()
	{
		if (previousIndex < 0)
		{
			discardLevel ();
			return;
		}
		finishPrevious (false);
		int p = stack.pop ();
		stack.setSize (stack.size () - 7);
		if (previousIndex >= 0)
		{
			int i = p + ioffset[BEGIN_LEVEL];
			iqueue[i >> LOW_BITS][i & LOW_MASK] = previousIndex - p;
		}
		previousIndex = p;
		finishPrevious (false);
	}


	public final void discardLevel ()
	{
		int[] a = stack.elements;
		int i = stack.size () - 8;
/*!!
#set ($i = 0)
#foreach ($pf in $bilfda)
		${pf}index = a[i + $i];
#set ($i = $i + 1)
#end
!!*/
//!! #* Start of generated code
		bindex = a[i + 0];
		iindex = a[i + 1];
		lindex = a[i + 2];
		findex = a[i + 3];
		dindex = a[i + 4];
		aindex = a[i + 5];
//!! *# End of generated code
		previousIndex = a[i + 6];
		stack.setSize (i);
	}


	private void finishPrevious (boolean nextExists)
	{
		int t;
		if ((t = previousIndex) >= 0)
		{
			int[][] q = iqueue;
			int i = nextExists ? (q[t >> LOW_BITS][t & LOW_MASK] |= NEXT_EXISTS) : q[t >> LOW_BITS][t & LOW_MASK];
			if ((i & IS_FINISHED) == 0)
			{
				q[t >> LOW_BITS][t & LOW_MASK] = i | IS_FINISHED;
				if (backLinks)
				{
					t++;
				}
				i &= ITEM_MASK;
/*!!
#foreach ($pf in $iblfda)
				if (${pf}size[i] < 0)
				{
					q[++t >> LOW_BITS][t & LOW_MASK] += ${pf}index;
				}
#end
!!*/
//!! #* Start of generated code
				if (isize[i] < 0)
				{
					q[++t >> LOW_BITS][t & LOW_MASK] += iindex;
				}
				if (bsize[i] < 0)
				{
					q[++t >> LOW_BITS][t & LOW_MASK] += bindex;
				}
				if (lsize[i] < 0)
				{
					q[++t >> LOW_BITS][t & LOW_MASK] += lindex;
				}
				if (fsize[i] < 0)
				{
					q[++t >> LOW_BITS][t & LOW_MASK] += findex;
				}
				if (dsize[i] < 0)
				{
					q[++t >> LOW_BITS][t & LOW_MASK] += dindex;
				}
				if (asize[i] < 0)
				{
					q[++t >> LOW_BITS][t & LOW_MASK] += aindex;
				}
//!! *# End of generated code
			}
		}
	}


	public final void writeItem (int item)
	{
		finishPrevious (true);
		int p = iindex;
		writeInt (item);
		item &= ITEM_MASK;
		if (backLinks)
		{
			writeInt (p - previousIndex);
		}
		previousIndex = p;
		if (isize[item] < 0)
		{
			writeInt (-p);
		}
/*!!
#foreach ($pf in $blfda)
		if (${pf}size[item] < 0)
		{
			writeInt (-${pf}index);
		}
#end
!!*/
//!! #* Start of generated code
		if (bsize[item] < 0)
		{
			writeInt (-bindex);
		}
		if (lsize[item] < 0)
		{
			writeInt (-lindex);
		}
		if (fsize[item] < 0)
		{
			writeInt (-findex);
		}
		if (dsize[item] < 0)
		{
			writeInt (-dindex);
		}
		if (asize[item] < 0)
		{
			writeInt (-aindex);
		}
//!! *# End of generated code
	}


	public final boolean hasItems ()
	{
		return (previousIndex >= 0) || !stack.isEmpty ();
	}


	public final boolean hasItemsInCurrentLevel ()
	{
		return previousIndex >= 0;
	}


	@Override
	public void writeObject (Object o) throws IOException
	{
		if (aqueue != null)
		{
			writeObjectInQueue (o);
		}
		else
		{
			writeObjectInStream (o);
		}
	}


	public void writeObjectInStream (Object o) throws IOException
	{
		out.reset ();
		out.writeObject (o);
		out.flush ();
	}


	public final int beginSkipBlock ()
	{
		int p = iindex;
		writeInt (-p);
/*!!
#foreach ($pf in $blfda)
		writeInt (-${pf}index);
#end
!!*/
//!! #* Start of generated code
		writeInt (-bindex);
		writeInt (-lindex);
		writeInt (-findex);
		writeInt (-dindex);
		writeInt (-aindex);
//!! *# End of generated code
		return p;
	}


	public final void endSkipBlock (int beginIndex)
	{
/*!!
#foreach ($pf in $iblfda)
		iqueue[beginIndex >> LOW_BITS][beginIndex++ & LOW_MASK] += ${pf}index;
#end
!!*/
//!! #* Start of generated code
		iqueue[beginIndex >> LOW_BITS][beginIndex++ & LOW_MASK] += iindex;
		iqueue[beginIndex >> LOW_BITS][beginIndex++ & LOW_MASK] += bindex;
		iqueue[beginIndex >> LOW_BITS][beginIndex++ & LOW_MASK] += lindex;
		iqueue[beginIndex >> LOW_BITS][beginIndex++ & LOW_MASK] += findex;
		iqueue[beginIndex >> LOW_BITS][beginIndex++ & LOW_MASK] += dindex;
		iqueue[beginIndex >> LOW_BITS][beginIndex++ & LOW_MASK] += aindex;
//!! *# End of generated code
	}

/*
	public void transferTo (HierarchicalQueue target)
	{
		target.finishPrevious (false);
		int p = target.iindex, i;
		target.ensureCapacity (target.bindex + bindex - bheader,
							   target.iindex + iindex, target.lindex + lindex,
							   target.findex + findex, target.dindex + dindex,
							   target.aindex + aindex);
		ByteList.arraycopy (bqueue, bheader, target.bqueue, target.bindex,
							bindex - bheader);
		target.bindex += bindex - bheader;
/ * ! !
# foreach ($pf in ["i", "l", "f", "d", "a"])
# if ($pf == "a")
		if (aqueue != null)
# end
		{
			System.arraycopy (${pf}queue, 0, target.${pf}queue, target.${pf}index,
							  ${pf}index);
			target.${pf}index += ${pf}index;
		}
# end
! ! * /
// ! ! # * Start of generated code
		{
			System.arraycopy (iqueue, 0, target.iqueue, target.iindex,
							  iindex);
			target.iindex += iindex;
		}
		{
			System.arraycopy (lqueue, 0, target.lqueue, target.lindex,
							  lindex);
			target.lindex += lindex;
		}
		{
			System.arraycopy (fqueue, 0, target.fqueue, target.findex,
							  findex);
			target.findex += findex;
		}
		{
			System.arraycopy (dqueue, 0, target.dqueue, target.dindex,
							  dindex);
			target.dindex += dindex;
		}
		if (aqueue != null)
		{
			System.arraycopy (aqueue, 0, target.aqueue, target.aindex,
							  aindex);
			target.aindex += aindex;
		}
/ / ! ! * # End of generated code
		int[] q = target.iqueue;
		if (backLinks)
		{
			q[p + 1] = p - target.previousIndex;
		}
		while (((i = q[p]) & NEXT_EXISTS) != 0)
		{
			if ((i = target.isize[i & ITEM_MASK]) < 0)
			{
				p += q[p + (backLinks ? 2 : 1)];
			}
			else
			{
				p += i;
			}
		}
		target.previousIndex = p;
		clear ();
	}
*/

	public final Cursor getCursor (Cursor c)
	{
		if (c == null)
		{
			c = new Cursor ();
		}
/*!!
#foreach ($pf in $bilfda)
		c.${pf}start = ${pf}index;
#end
!!*/
//!! #* Start of generated code
		c.bstart = bindex;
		c.istart = iindex;
		c.lstart = lindex;
		c.fstart = findex;
		c.dstart = dindex;
		c.astart = aindex;
//!! *# End of generated code
		c.previousIndex = previousIndex;
		c.sp = stack.size ();
		return c;
	}
	
	public final void moveTo (Cursor c)
	{
		if (c.sp != stack.size ())
		{
			throw new IllegalArgumentException ();
		}
/*!!
#foreach ($pf in $bilfda)
		${pf}index = c.${pf}start;
#end
!!*/
//!! #* Start of generated code
		bindex = c.bstart;
		iindex = c.istart;
		lindex = c.lstart;
		findex = c.fstart;
		dindex = c.dstart;
		aindex = c.astart;
//!! *# End of generated code
		previousIndex = c.previousIndex;
	}

	protected Data createData ()
	{
		return new Data ();
	}


	public final Data getData ()
	{
		return createData ().set (this, false);
	}


	public final Data cloneData ()
	{
		return createData ().set (this, true);
	}


	public void restore (Data data)
	{
/*!!
#foreach ($pf in $bilfda)
		${pf}queue = data.${pf}queue;
		${pf}index = data.${pf}index;
#end
!!*/
//!! #* Start of generated code
		bqueue = data.bqueue;
		bindex = data.bindex;
		iqueue = data.iqueue;
		iindex = data.iindex;
		lqueue = data.lqueue;
		lindex = data.lindex;
		fqueue = data.fqueue;
		findex = data.findex;
		dqueue = data.dqueue;
		dindex = data.dindex;
		aqueue = data.aqueue;
		aindex = data.aindex;
//!! *# End of generated code
	}
}
