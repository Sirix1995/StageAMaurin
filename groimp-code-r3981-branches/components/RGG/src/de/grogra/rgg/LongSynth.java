
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

package de.grogra.rgg;

/**
 * Instances of <code>LongSynth</code> are used in the context of the method
 * {@link Library#synthesize(Object, ObjectToObjectGenerator, ObjectToLong)}.
 * and represent the information needed for the computation of a synthesized
 * attribute: <code>object</code> is the current object for which the
 * synthesized attribute has to be computed, the generator method
 * <code>evaluateLong</code> yields the values of the synthesized attributes of
 * <code>object</code>'s descendants.
 *
 * @author Ole Kniemeyer
 *
 * @param <T> the type of objects for which synthesized attributes are computed
 *
 */
public final class LongSynth<T> implements de.grogra.xl.lang.VoidToLongGenerator
{
	/**
	 * The current object.
	 */
	public T object;
	
	de.grogra.xl.util.LongList valuesList;
	int startIndex;
	
	/**
	 * This generator method yields the values of the synthesized attribute
	 * for every descendant of <code>object</code>. These values have been
	 * computed previously.
	 *
	 * @param cons the consumer which receives the values of synthesized
	 * attribute of descendants
	 */
	public void evaluateLong (de.grogra.xl.lang.LongConsumer cons)
	{
		for (int i = startIndex; i < valuesList.size; i++)
		{
			cons.consume (valuesList.get (i));
		}
	}

	/**
	 * This method is an alias for {@link #evaluateLong}.
	 */
	public void values (de.grogra.xl.lang.LongConsumer cons)
	{
		evaluateLong (cons);
	}

	public int size ()
	{
		return valuesList.size - startIndex;
	}

	public long get (int index)
	{
		return valuesList.get (index - startIndex);
	}

}

