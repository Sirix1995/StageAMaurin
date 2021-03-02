
// NOTE: This file was generated automatically.

// ********************************************
// *               DO NOT EDIT!               *
// ********************************************


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



/**
 * A <code>ByteList</code> represents a list of <code>byte</code> values.
 * It provides list- and stack-oriented methods for insertion, addition,
 * and removal, of values. The methods are not thread-safe.
 * 
 * @author Ole Kniemeyer
 */
public class ByteList
	implements
Cloneable, java.io.Serializable, de.grogra.xl.lang.ByteConsumer,
de.grogra.xl.lang.VoidToByteGenerator
{
	private static final long serialVersionUID =
		-1968627688492821008L;

	/**
	 * The array holding the elements. Only the components from 0
	 * to <code>size - 1</code> are valid. Direct operation on this
	 * array has to be designed carefully to avoid inconsistencies.
	 */
	public transient byte[] elements;

	/**
	 * The size of this list. Direct operation on this
	 * field has to be designed carefully to avoid inconsistencies.
	 */
	public int size = 0;


	/**
	 * Constructs a new <code>ByteList</code> with a given initial capacity.
	 *
	 * @param capacity the initial capacity
	 */
	public ByteList (int capacity)
	{
		elements = new byte[Math.max (capacity, 8)];
	}


	/**
	 * Constructs a new <code>ByteList</code>.
	 */
	public ByteList ()
	{
		this (16);
	}


	/**
	 * Constructs a new <code>ByteList</code> whose elements are
	 * a copy of <code>elements</code>.
	 *
	 * @param elements the initial elements of the list
	 */
	public ByteList (byte[] elements)
	{
		this (elements.length);
		arraycopy (elements, 0, this.elements, 0, size = elements.length);
	}


	@Override
	public Object clone ()
	{
		try
		{
			ByteList  c = (ByteList ) super.clone ();
			c.elements = new byte[Math.max (size, 8)];
			arraycopy (elements, 0, c.elements, 0, size);
			return c;
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError ();
		}
	}


	/**
	 * Ensures a capacity of the internal array of at least
	 * <code>capacity</code>.
	 *
	 * @param capacity the desired minimum capacity
	 */
	public void ensureCapacity (int capacity)
	{
		if (capacity > elements.length)
		{
			byte[] a = new byte[Math.max (capacity,
											elements.length * 2)];
			arraycopy (elements, 0, a, 0, size);
			elements = a;
		}
	}


	private static final byte[] EMPTY = new byte[0];

	/**
     * Trims the capacity of this list to be its current size.
	 */
	public void trimToSize ()
	{
		if (size < elements.length)
		{
			if (size == 0)
			{
				elements = EMPTY;
			}
			else
			{
				arraycopy (elements, 0, elements = new byte[size], 0, size);
			}
		}
	}


	/**
	 * Pushes <code>o</code> on top of this list, i.e., as last element.
	 *
	 * @param o the value to push
	 * @return this list
	 */
	public final ByteList  push (byte o)
	{
		add (o);
		return this;
	}


	/**
	 * Pushes <code>o1</code> and <code>o2</code> on top of this list,
	 * i.e., as last elements. The effect is the same as the invocation
	 * <code>list.push(o1).push(o2)</code>.
	 *
	 * @param o1 the first value to push
	 * @param o2 the second value to push
	 * @return this list
	 */
	public final ByteList  push (byte o1, byte o2)
	{
		add (o1);
		add (o2);
		return this;
	}


	/**
	 * Pushes <code>o1 ... o3</code> on top of this list,
	 * i.e., as last elements. The effect is the same as the invocation
	 * <code>list.push(o1).push(o2).push(o3)</code>.
	 *
	 * @param o1 the first value to push
	 * @param o2 the second value to push
	 * @param o3 the third value to push
	 * @return this list
	 */
	public final ByteList  push (byte o1, byte o2, byte o3)
	{
		add (o1);
		add (o2);
		add (o3);
		return this;
	}


	/**
	 * Adds <code>o</code> as last element to this list.
	 *
	 * @param o the value to add
	 * @return <code>true</code>
	 */
	public boolean add (byte o)
	{
		if (size == elements.length)
		{
			ensureCapacity (size + 1);
		}
		elements[size] = o;
		size++;
		return true;
	}


	/**
	 * Inserts <code>o</code> at position <code>index</code> to this list.
	 * If <code>index</code> is not less than <code>size</code>, the
	 * list is enlarged and filled with <code>0</code>-values before.
	 *
	 * @param index the insert position
	 * @param o the value to insert
	 */
	public void add (int index, byte o)
	{
		if (index >= size)
		{
			ensureCapacity (index + 1);
			clear (elements, size, index - size);
		}
		else
		{
			ensureCapacity (size + 1);
			arraycopy (elements, index, elements, index + 1, size - index);
		}
		elements[index] = o;
		size++;
	}


	/**
	 * Adds <code>o</code> as last element to this list if is not
	 * yet contained as reported by {@link #contains(byte)}.
	 *
	 * @param o the value to add
	 */
	public void addIfNotContained (byte o)
	{
		if (!contains (o))
		{
			add (o);
		}
	}


	/**
	 * Appends all elements of <code>v</code> to this list.
	 *
	 * @param v the list of elements to add
	 * @return this list
	 */
	public ByteList  addAll (ByteList
		v)
	{
		return addAll (v.elements, 0, v.size);
	}


	/**
	 * Appends <code>length</code> components of <code>v</code> to this list,
	 * starting at index <code>begin</code>.
	 *
	 * @param v the array of elements to add
	 * @param begin the array index to begin with
	 * @param length the number of elements to add
	 * @return this list
	 */
	public ByteList  addAll (byte[] v, int begin, int length)
	{
		if (length <= 0)
		{
			return this;
		}
		ensureCapacity (size + length);
		arraycopy (v, begin, elements, size, length);
		size += length;
		return this;
	}

	/**
	 * Removes the element at position <code>index</code>.
	 *
	 * @param index the position of the element to be removed
	 * @return the value of the removed element
	 */
	public byte removeAt (int index)
	{
		byte o = (byte) elements[index];
		arraycopy (elements, index + 1, elements, index, --size - index);
		return o;
	}


	/**
	 * Removes the element <code>o</code>. The last occurence of
	 * <code>o</code> in this list is removed.
	 *
	 * @param o the element to be removed
	 * @return <code>true</code> iff <code>o</code> was found and removed from the list
	 */
	public boolean remove (byte o)
	{
		for (int i = size - 1; i >= 0; i--)
		{
			if (			((o) == (elements[i]))
	)
			{
				removeAt (i);
				return true;
			}
		}
		return false;
	}


	/**
	 * Sets the element at position <code>index</code> to <code>o</code>.
	 * If <code>index</code> is not less than <code>size</code>, the
	 * list is enlarged and filled with <code>0</code>-values before.
	 *
	 * @param index the position
	 * @param o the new value
	 * @return the old value at <code>index</code>
	 */
	public byte set (int index, byte o)
	{
		byte before;
		if (index >= size)
		{
			ensureCapacity (index + 1);
			clear (elements, size, index - size);
			before = (byte) elements[index];
			elements[index] = o;
			size = index + 1;
		}
		else
		{
			before = (byte) elements[index];
			elements[index] = o;
		}
		return before;
	}


    /**
     * Returns the list element at <code>index</code>. If
     * <code>index</code> is not less than <code>size</code>,
     * <code>0</code> is returned.
     *
     * @param index the position
     * @return the value at <code>index</code>
     */
	public byte get (int index)
	{
		return (index < size) ? (byte) elements[index] : 0;
	}


    /**
     * Returns the list element at <code>index</code> as seen from
     * the top, i.e., at absolute position <code>size - index</code>.
     * Thus, the topmost element has index 1.
     *
     * @param index the position as seen from the top
     * @return the value at that position
     */
	public byte peek (int index)
	{
		return (index > 0) ? (byte) elements[size - index] : 0;
	}


    /**
     * Returns <code>true</code> iff this list contains the given
     * element <code>o</code>.
     *
     * @param o a value
     * @return <code>true</code> iff <code>o</code> is contained
     */
	public boolean contains (byte o)
	{
		for (int i = size - 1; i >= 0; i--)
		{
			if (			((o) == (elements[i]))
	)
			{
				return true;
			}
		}
		return false;
	}


    /**
     * Returns the index of <code>o</code> in this list.
     *
     * @param o a value
     * @return the index of <code>o</code>, or -1 of <code>o</code> is not contained
     */
	public int indexOf (byte o)
	{
		for (int i = 0; i < size; i++)
		{
			if (			((o) == (elements[i]))
	)
			{
				return i;
			}
		}
		return -1;
	}


    /**
     * Returns the last index of <code>o</code> in this list.
     *
     * @param o a value
     * @return the index of <code>o</code>, or -1 of <code>o</code> is not contained
     */
	public int lastIndexOf (byte o)
	{
		for (int i = size - 1; i >= 0; i--)
		{
			if (			((o) == (elements[i]))
	)
			{
				return i;
			}
		}
		return -1;
	}


    /**
     * Searches this list for the specified value using
     * the binary search algorithm. This list has to be sorted
     * in ascending order.
     *
     * @param value the value to be searched for
     * @return index of the searched value, if it is contained in this list;
     * otherwise, <code>(-(<em>insertion point</em>) - 1)</code>.  The
     * <em>insertion point</em> is defined as the point at which the
     * value would be inserted into the list: the index of the first
     * element greater than the value, or {@link #size()}, if all
     * elements in the list are less than the specified value.
     */
	public int binarySearch (byte value)
	{
		return binarySearch (elements, value, 0, size);
	}


	public static int binarySearch (byte[] array, byte value)
	{
		return binarySearch (array, value, 0, array.length);
	}


	public static int binarySearch (byte[] array, byte value,
									int fromIndex, int toIndex)
	{
		while (fromIndex < toIndex)
		{
			int i;
			byte nv;
			if ((nv = array[i = (fromIndex + toIndex) >> 1]) < value)
			{
				fromIndex = i + 1;
			}
			else if (nv > value)
			{
				toIndex = i;
			}
			else
			{
				return i;
			}
		}
		return ~fromIndex;
	}
	
	
	public void writeTo (java.nio.ByteBuffer out)
	{
		out.put (elements, 0, size);
	}


    /**
     * Removes all of the elements from this list. The list will
     * be empty after this call returns.
     */
	public void clear ()
	{
		size = 0;
	}



	public static void arraycopy (byte[] src, int srcIndex,
								  byte[] dest, int destIndex, int length)
	{
		if (length < 20)
		{
			if ((dest != src) || (destIndex > srcIndex))
			{
				while (--length >= 0)
				{
					dest[destIndex + length] = src[srcIndex + length];
				}
			}
			else
			{
				for (int i = 0; i < length; i++)
				{
					dest[destIndex + i] = src[srcIndex + i];
				}
			}
		}
		else
		{
			System.arraycopy (src, srcIndex, dest, destIndex, length);
		}
	}


	private static final byte[] BYTE_NULL = new byte[1024];

	public static void clear (byte[] array, int index, int length)
	{
		if (length < 20)
		{
			while (--length >= 0)
			{
				array[index + length] = ((byte) 0);
			}
		}
		else
		{
			while (length > 0)
			{
				int n = (length > 1024) ? 1024 : length;
				arraycopy (BYTE_NULL, 0, array, index, n);
				length -= n;
				index += n;
			}
		}
	}


    /**
     * Removes and returns the object at the top of this list. 
     *
     * @return the removed object from the top of this list
     */
	public byte pop ()
	{
		return (byte) elements[--size];
	}


    /**
     * Sets the size of this list to the given value. If the new size
     * is greater than the old size, the new elements are initialized
     * with <code>0</code>-values. 
     *
     * @param size the new size
     */
	public void setSize (int size)
	{
		if (size > this.size)
		{
			ensureCapacity (size);
		}
		this.size = size;
	}


    /**
     * Returns the size of this list.
     *
     * @return the size
     */
	public final int size ()
	{
		return size;
	}


    /**
     * Returns if this list is empty, i.e., if its size is zero.
     *
     * @return <code>true</code> iff this list is empty
     */
	public final boolean isEmpty ()
	{
		return size == 0;
	}


    /**
     * Returns an array containing the elements of this list.
     *
     * @return an array copy of this list
     */
	public byte[] toArray ()
	{
		byte[] a = new byte[size];
		arraycopy (elements, 0, a, 0, size);
		return a;
	}


    /**
     * Returns an array containing the elements of this list. The type
     * of the returned array is that of the specified <code>array</code>. If this
     * list fits in the specified <code>array</code>,
     * it is returned therein. Otherwise,
     * a new array is allocated whose length is the size of this list's size,
     * the values of this list are copied into the new array, and this
     * array is returned.
     * <p>
     * If there is room for an additional element in the <code>array</code>,
     * a <code>0</code>-value is written behind the last copied element.
     *
     * @param array an array to use 
     * @return an array copy of this list
     */
	public byte[] toArray (byte[] array)
	{
		int l;
		if ((l = array.length) > size)
		{
			array[size] = 0;
		}
		else if (l < size)
		{
			array = new byte[size];
		}
		arraycopy (elements, 0, array, 0, size);
		return array;
	}

	@Override
	public String toString ()
	{
		StringBuffer b = new StringBuffer (super.toString ()).append ('{');
		for (int i = 0; i < size; i++)
		{
			if (i > 0)
			{
				b.append (", ");
			}
			b.append (elements[i]);
		}
		return b.append ('}').toString ();
	}

	@Override
	public boolean equals (Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (!(o instanceof ByteList))
		{
			return false;
		}
		ByteList list = (ByteList) o;
			if (size != list.size ())
			{
				return false;
			}
			for (int i = 0; i < size; i++)
			{
				if (!			((elements[i]) == (list.get (i)))
	)
				{
					return false;
				}
			}
			return true;
	}


	@Override
	public int hashCode ()
	{
		int hashCode = 1;
		for (int i = 0; i < size; i++)
		{
			hashCode = 31 * hashCode + elements[i];
		}
		return hashCode;
	}


	public void consume (byte value)
	{
		add (value);
	}

	/**
	 * This generator method yields all values to <code>cons</code>.
	 *
	 * @param cons the consumer which receives the values
	 */
	public void evaluateByte (de.grogra.xl.lang.ByteConsumer cons)
	{
		for (int i = 0; i < size; i++)
		{
			cons.consume ((byte) elements[i]);
		}
	}

	/**
	 * This method is an alias for {@link #evaluateByte}.
	 */
	public void values (de.grogra.xl.lang.ByteConsumer cons)
	{
		evaluateByte (cons);
	}


	private void writeObject (java.io.ObjectOutputStream s)
		throws java.io.IOException
	{
		s.defaultWriteObject ();
		for (int i = 0; i < size; i++)
		{
			s.writeByte (elements[i]);
		}
	}

	private void readObject (java.io.ObjectInputStream s)
		throws java.io.IOException, ClassNotFoundException
	{
		s.defaultReadObject ();
		byte[] a = elements = new byte[size];
		for (int i = 0; i < size; i++)
		{
			a[i] = s.readByte ();
		}
	}

}

