
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

public class XBitSet
{
	private int[] bits;
	private int capacity;


	public XBitSet ()
	{
		this (256);
	}


	public XBitSet (int capacity)
	{
		bits = new int[(Math.max (capacity, 32) + 31) >> 5];
		this.capacity = bits.length << 5;
	}


	private void ensureCapacity (int capacity)
	{
		capacity = (capacity + 31) >> 5;
		int len = bits.length;
		if (capacity > len)
		{
			IntList.arraycopy (bits, 0, bits = new int[capacity], 0, this.capacity >> 5);
			this.capacity = capacity << 5;
		}
		else if (capacity > (len = this.capacity >> 5))
		{
			IntList.clear (bits, len, capacity - len);
			this.capacity = capacity << 5;
		}
	}


	public void set (int bitIndex, boolean value)
	{
		if (value)
		{
			ensureCapacity (bitIndex + 1);
			bits[bitIndex >> 5] |= 1 << bitIndex;
		}
		else if (bitIndex < capacity)
		{
			bits[bitIndex >> 5] &= ~(1 << bitIndex);
		}
	}


	public void set (int bitIndex)
	{
		ensureCapacity (bitIndex + 1);
		bits[bitIndex >> 5] |= 1 << bitIndex;
	}


	public void setRange (int startIndex, int endIndex, boolean value)
	{
		if (value)
		{
			ensureCapacity (endIndex + 1);
			for (int i = startIndex; i < endIndex; i++)
			{
				if (((i & 31) == 0) && (i + 32 <= endIndex))
				{
					bits[i >> 5] = -1;
					i += 31;
				}
				else
				{
					bits[i >> 5] |= 1 << i;
				}
			}
		}
		else if (startIndex < capacity)
		{
			if (endIndex > capacity)
			{
				endIndex = capacity;
			}
			for (int i = startIndex; i < endIndex; i++)
			{
				if (((i & 31) == 0) && (i + 32 <= endIndex))
				{
					bits[i >> 5] = 0;
					i += 31;
				}
				else
				{
					bits[i >> 5] &= ~(1 << i);
				}
			}
		}
	}

	
	public boolean add (int bitIndex)
	{
		ensureCapacity (bitIndex + 1);
		if ((bits[bitIndex >> 5] & (1 << bitIndex)) == 0)
		{
			bits[bitIndex >> 5] |= 1 << bitIndex;
			return true;
		}
		else
		{
			return false;
		}
	}

	
	public boolean remove (int bitIndex)
	{
		ensureCapacity (bitIndex + 1);
		if ((bitIndex < capacity) && ((bits[bitIndex >> 5] & (1 << bitIndex)) != 0))
		{
			bits[bitIndex >> 5] &= ~(1 << bitIndex);
			return true;
		}
		else
		{
			return false;
		}
	}

	
	public boolean flip (int bitIndex)
	{
		if (bitIndex >= capacity)
		{
			set (bitIndex);
			return false;
		}
		else if ((bits[bitIndex >> 5] & (1 << bitIndex)) != 0)
		{
			bits[bitIndex >> 5] &= ~(1 << bitIndex);
			return true;
		}
		else
		{
			bits[bitIndex >> 5] |= 1 << bitIndex;
			return false;
		}
	}


	public void clear (int bitIndex)
	{
		if (bitIndex < capacity)
		{
			bits[bitIndex >> 5] &= ~(1 << bitIndex);
		}
	}


	public boolean get (int bitIndex)
	{
		return (bitIndex < capacity)
			&& ((bits[bitIndex >> 5] & (1 << bitIndex)) != 0);
	}


	public int getBits (int bitIndex)
	{
		return (bitIndex < capacity) ? bits[bitIndex >> 5] : 0;
	}


	public void setBits (int bitIndex, int bits)
	{
		ensureCapacity (bitIndex + 1);
		this.bits[bitIndex >> 5] = bits;
	}

	
	public void set (XBitSet src)
	{
		capacity = src.capacity;
		bits = src.bits.clone ();
	}


	public boolean intersects (XBitSet set)
	{
		int c = Math.min (capacity, set.capacity) >> 5;
		int[] a = bits, b = set.bits;
		while (--c >= 0)
		{
			if ((a[c] & b[c]) != 0)
			{
				return true;
			}
		}
		return false;
	}


	public void andNot (XBitSet a, XBitSet b)
	{
		ensureCapacity (a.capacity);
		capacity = a.capacity;
		int c = capacity >> 5, bc = b.capacity >> 5;
		int[] abits = a.bits, bbits = b.bits, tbits = bits;
		if ((tbits == abits) && (c > bc))
		{
			c = bc;
		}
		while (--c >= 0)
		{
			tbits[c] = (c >= bc) ? abits[c] : (abits[c] & ~bbits[c]);
		}
	}


	public void and (XBitSet a, XBitSet b)
	{
		int c;
		ensureCapacity (c = Math.min (a.capacity, b.capacity));
		capacity = c;
		c >>= 5;
		int[] abits = a.bits, bbits = b.bits, tbits = bits;
		while (--c >= 0)
		{
			tbits[c] = abits[c] & bbits[c];
		}
	}


	public void or (XBitSet a, XBitSet b)
	{
		int c;
		ensureCapacity (c = Math.max (a.capacity, b.capacity));
		capacity = c;
		c >>= 5;
		int ac = a.capacity >> 5, bc = b.capacity >> 5;
		int[] abits = a.bits, bbits = b.bits, tbits = bits;
		while (--c >= 0)
		{
			tbits[c] = (c >= bc) ? abits[c] : (c >= ac) ? bbits[c] : (abits[c] | bbits[c]);
		}
	}

	
	public int cardinality ()
	{
		int c = capacity >> 5;
		int n = 0;
		while (--c >= 0)
		{
			n += Integer.bitCount (bits[c]);
		}
		return n;
	}


	public void clear ()
	{
		setRange (0, capacity, false);
	}


	public final int nextSetBit (int fromIndex)
	{
		if (fromIndex >= capacity)
		{
			return -1;
		}
		int m = bits[fromIndex >> 5];
		while (true)
		{
			if ((fromIndex & 31) == 0)
			{
				while ((m = bits[fromIndex >> 5]) == 0)
				{
					fromIndex += 32;
					if (fromIndex >= capacity)
					{
						return -1;
					}
				}
			}
			do
			{
				if ((m & (1 << fromIndex)) != 0)
				{
					return fromIndex;
				}
				if (++fromIndex == capacity)
				{
					return -1;
				}
			} while ((fromIndex & 31) != 0);
		}
	}


	public final int nextClearBit (int fromIndex)
	{
		if (fromIndex >= capacity)
		{
			return -1;
		}
		int m = bits[fromIndex >> 5];
		while (true)
		{
			if ((fromIndex & 31) == 0)
			{
				while ((m = bits[fromIndex >> 5]) == -1)
				{
					fromIndex += 32;
					if (fromIndex >= capacity)
					{
						return -1;
					}
				}
			}
			do
			{
				if ((m & (1 << fromIndex)) == 0)
				{
					return fromIndex;
				}
				if (++fromIndex == capacity)
				{
					return -1;
				}
			} while ((fromIndex & 31) != 0);
		}
	}


	public final int nextBit (int fromIndex, boolean value)
	{
		return value ? nextSetBit (fromIndex) : nextClearBit (fromIndex);
	}


	public int size ()
	{
		return capacity;
	}

	
	public void addToList (IntList list)
	{
		int c = 0;
		for (int i = 0; i < capacity; i++)
		{
			if ((i & 31) == 0)
			{
				c = bits[i >> 5];
			}
			if ((c & (1 << i)) != 0)
			{
				list.add (i);
			}
		}
	}

	
	@Override
	public String toString ()
	{
		StringBuffer b = new StringBuffer ("{");
		boolean first = true;
		for (int i = 0; i < capacity; i++)
		{
			if (get (i))
			{
				if (first)
				{
					first = false;
				}
				else
				{
					b.append (',');
				}
				b.append (i);
			}
		}
		return b.append ('}').toString ();
	}
}
