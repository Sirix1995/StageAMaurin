
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

package de.grogra.persistence;

import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.lang.reflect.Array;
import de.grogra.xl.util.BooleanList;
import de.grogra.xl.util.ByteList;
import de.grogra.xl.util.CharList;
import de.grogra.xl.util.DoubleList;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.LongList;
import de.grogra.xl.util.ShortList;
import de.grogra.reflect.*;

public final class IndirectField extends PersistenceField
	implements Cloneable
{
	private final Type typeWhen0;
	private final int begin;
	private ManageableType.Field[] chain = new ManageableType.Field[4];
	private int length = 0, indexCount = 0;
	private ManageableType.Field lastField;
	private boolean managed = true;
	private String totalName = null;
	private IndirectField[] subfields = null;
	private IndirectField superfield;
	private IntList ints = null;
	private Type cast = null;


	public IndirectField dup ()
	{
		try
		{
			IndirectField f = (IndirectField) clone ();
			f.chain = chain.clone ();
			f.totalName = null;
			f.subfields = null;
			f.ints = null;
			return f;
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError (e);
		}
	}


	private IndirectField (Type type, int begin)
	{
		super ("IndirectField", "IndirectField", 0, Type.INVALID);
		this.typeWhen0 = type;
		this.begin = begin;
	}


	public IndirectField (Type type)
	{
		this (type, 0);
	}


	public IndirectField ()
	{
		this (null, 0);
	}


	public IndirectField (PersistenceField field)
	{
		this (null, 0);
		set (field);
	}


	@Override
	public PersistenceField getShallowSubchain (int index)
	{
		if (index == 0)
		{
			return this;
		}
		int cc = indexCount;
		for (int i = 0; i < index; i++)
		{
			if (chain[i].isArrayComponent)
			{
				cc--;
			}
		}
		return pooledSubfield (index, cc);
	}

	
	private IndirectField pooledSubfield (int index, int indCount)
	{
		IndirectField[] a;
		if (((a = subfields) == null) || (a.length <= index))
		{
			subfields = a = new IndirectField[index + 1];
		}
		IndirectField f;
		if ((f = a[index]) == null)
		{
			a[index] = f = new IndirectField (null, index);
		}
		f.chain = chain;
		f.fco = fco;
		f.lastField = lastField;
		f.length = length - index;
		f.managed = managed;
		f.typeId = typeId;
		f.indexCount = indCount;
		return f;
	}

	
	@Override
	PersistenceField getShallowSuperchain ()
	{
		if (length < 2)
		{
			return null;
		}
		IndirectField s = superfield;
		if (s == null)
		{
			s = superfield = new IndirectField (null, begin);
		}
		s.chain = chain;
		s.indexCount = lastField.isArrayComponent ? indexCount - 1 : indexCount;
		s.lastField = chain[begin + length - 2];
		s.typeId = s.lastField.typeId;
		s.length = length - 1;
		s.totalName = totalName;
		for (int i = length - 2; i >= 0; i--)
		{
			if (!chain[begin + i].managed)
			{
				s.managed = false;
				s.fco = false;
				return s;
			}
		}
		s.managed = true;
		s.fco = s.lastField.fco;
		return s;
	}


	public static PersistenceField concat (PersistenceField a,
										   PersistenceField b)
	{
		if (b == null)
		{
			return a;
		}
		else if (a == null)
		{
			return b;
		}
		else
		{
			IndirectField f = new IndirectField (a);
			f.add (b);
			return f;
		}		
	}


	public final Type getType ()
	{
		return (cast != null) ? cast
			: (length == 0) ? typeWhen0
			: lastField.getType ();
	}


	@Override
	public final de.grogra.util.Quantity getQuantity ()
	{
		return (lastField != null) ? lastField.getQuantity () : null;
	}


	@Override
	public final Number getMinValue ()
	{
		return (lastField != null) ? lastField.getMinValue () : null;
	}


	@Override
	public final Number getMaxValue ()
	{
		return (lastField != null) ? lastField.getMaxValue () : null;
	}


	@Override
	public String getName ()
	{
		if (totalName == null)
		{
			StringBuffer b = new StringBuffer ();
			for (int i = 0; i < length; i++)
			{
				if (i > 0)
				{
					b.append ('.');
				}
				b.append (chain[begin + i].getSimpleName ());
			}
			totalName = b.toString ();
		}
		return totalName;
	}


	@Override
	public String getSimpleName ()
	{
		return (lastField != null) ? lastField.getSimpleName () : null;
	}


	public int length ()
	{
		return length;
	}


	public int getIndexCount ()
	{
		return indexCount;
	}


	@Override
	public ManageableType.Field getSubfield (int index)
	{
		return chain[begin + index];
	}


	@Override
	public ManageableType.Field getLastField ()
	{
		return lastField;
	}


	public boolean check ()
	{
		for (int i = begin + 1; i < begin + length; i++)
		{
			if (!chain[i].isArrayComponent
				&& !Reflection.isCastableFrom (chain[i - 1].getType (),
											   chain[i].getDeclaringType ()))
			{
				return false;
			}
		}
		return true;
	}


	public boolean overlaps (int[] tindices, FieldChain field,
							 int[] findices)
	{
		if (field instanceof ManageableType.Field)
		{
			return (length > 0) && (field == chain[begin]);
		}
		else if (field instanceof IndirectField)
		{
			IndirectField f = (IndirectField) field;
			int i = f.length;
			i = (length < i) ? length : i;
			int ai = -1;
			while (--i >= 0)
			{
				if (chain[begin + i] != f.chain[f.begin + i])
				{
					return false;
				}
				if (chain[begin + i].isArrayComponent
					&& (tindices[++ai] != findices[ai]))
				{
					return false;
				}
			}
			return true;
		}
		else
		{
			int i = field.length ();
			i = (length < i) ? length : i;
			int ai = -1;
			Type arrayComponent = null;
			while (--i >= 0)
			{
				if (arrayComponent != null)
				{
					if (tindices[++ai] != findices[ai])
					{
						return false;
					}
					arrayComponent = arrayComponent.getComponentType ();
				}
				else
				{
					Field f = field.getField (i);
					if ((chain[begin + i] != f)
						&& ((f instanceof ManageableType.Field)
							|| !Reflection.membersEqual (chain[begin + i], f, false)))
					{
						return false;
					}
					arrayComponent = f.getType ().getComponentType ();
				}
			}
			return true;
		}
	}


	public void clear ()
	{
		length = 0;
		indexCount = 0;
		managed = true;
		lastField = null;
		totalName = null;
		fco = false;
		cast = null;
	}


	public void set (PersistenceField field)
	{
		assert begin == 0;
		cast = null;
		if (field instanceof IndirectField)
		{
			IndirectField f = (IndirectField) field;
			if (f.length > chain.length)
			{
				chain = new ManageableType.Field[f.length];
			}
			for (int i = f.length - 1; i >= 0; i--)
			{
				chain[i] = f.chain[f.begin + i];
			}
			typeId = f.typeId;
			fco = f.fco;
			lastField = f.lastField;
			managed = f.managed;
			length = f.length;
			indexCount = f.indexCount;
		}
		else if (field != null)
		{
			typeId = (chain[0] = lastField = (ManageableType.Field) field)
				.getType ().getTypeId ();
			fco = lastField.fco;
			managed = lastField.managed;
			length = 1;
			indexCount = 0;
		}
		else
		{
			clear ();
		}
	}


	public IndirectField add (Field field)
	{
		PersistenceField pf = PersistenceField.get (field);
		if (pf == null)
		{
			throw new IllegalArgumentException (field.toString ());
		}
		add (pf);
		return this;
	}


	public IndirectField add (FieldChain fields)
	{
		for (int i = 0; i < fields.length (); i++)
		{
			add (fields.getField (i));
		}
		return this;
	}


	public IndirectField add (PersistenceField field)
	{
		assert begin == 0;
		if (field instanceof IndirectField)
		{
			IndirectField f = (IndirectField) field;
			int i;
			if ((i = length + f.length) > chain.length)
			{
				System.arraycopy (chain, 0,
								  chain = new ManageableType.Field[i += 4], 0,
								  length);
			}
			for (i = f.length - 1; i >= 0; i--)
			{
				chain[length + i] = f.chain[f.begin + i];
			}
			typeId = f.typeId;
			lastField = f.lastField;
			cast = f.cast;
			if (managed && f.managed)
			{
				fco = f.fco;
			}
			else
			{
				managed = false;
				fco = false;
			}
			length += f.length;
			indexCount += f.indexCount;
		}
		else if (field != null)
		{
			return add ((ManageableType.Field) field);
		}
		return this;
	}


	public IndirectField add (ManageableType.Field f)
	{
		assert begin == 0;
		cast = null;
		int i;
		if ((i = length + 1) > chain.length)
		{
			System.arraycopy (chain, 0,
							  chain = new ManageableType.Field[i += 4], 0,
							  length);
		}
		chain[length] = f;
		typeId = f.typeId;
		lastField = f;
		if (managed && f.managed)
		{
			fco = f.fco;
		}
		else
		{
			managed = false;
			fco = false;
		}
		length++;
		if (f.isArrayComponent)
		{
			indexCount++;
		}
		return this;
	}


	public void pop ()
	{
		assert begin == 0;
		cast = null;
		if (chain[--length].isArrayComponent)
		{
			indexCount--;
		}
		chain[length] = null;
		if (length > 0)
		{
			lastField = chain[length - 1];
			typeId = lastField.typeId;
			if (managed)
			{
				fco = lastField.fco;
			}
		}
		else
		{
			lastField = null;
			fco = false;
		}
	}


	public IndirectField cast (Type type)
	{
		cast = type;
		return this;
	}


	int[] read (PersistenceCapable pc, XAQueue.Reader in)
	{
		assert begin == 0;
		clear ();
		if (ints != null)
		{
			ints.clear ();
		}
		int i = in.readInt (), len = i >>> CHAIN_LENGTH_BIT;
		ManageableType.Field f = pc.getManageableType ()
			.getManagedField (i & ((1 << CHAIN_LENGTH_BIT) - 1));
		add (f);
		Object o = pc;
		for (i = 1; i < len; i++)
		{
			o = f.isArrayComponent ? f.arrayField.getArrayComponent (o, ints.peek (1))
				: f.getObject (o);
			Type t;
			if (f.isArray)
			{
				if (ints == null)
				{
					ints = new IntList ();
				}
				ints.push (in.readInt ());
				f = f.arrayComponent;
			}
			else if (o instanceof Manageable)
			{
				f = ((Manageable) o).getManageableType ()
					.getManagedField (in.readInt ());
			}
			else if (((t = f.getType ()) instanceof ManageableType)
					 || ((o != null)
						 && ((t = in.getPersistenceManager ().getBindings ().resolveType (o.getClass ())) != null)))
			{
				f = ((ManageableType) t).getManagedField (in.readInt ());
			}
			else
			{
				throw new FatalPersistenceException (f + " " + o);
			}
			if ((f == null) || !(f.isArrayComponent || f.getDeclaringType ().isInstance (o)))
			{
				throw new FatalPersistenceException (f + " " + o);
			}
			add (f);
		}
		return ((ints != null) && (ints.size > 0)) ? ints.elements : null;
	}


	@Override
	void write (int[] indices, XAQueue out)
	{
		out.writeInt (chain[begin].fieldId | (length << CHAIN_LENGTH_BIT));
		int ai = -1;
		for (int i = 1; i < length; i++)
		{
			ManageableType.Field f;
			if ((f = chain[begin + i]).isArrayComponent)
			{
				out.writeInt (indices[++ai]);
			}
			else
			{
				out.writeInt (f.fieldId);
			}
		}
	}


	@Override
	void writeObject (Object value, PersistenceOutput out) throws IOException
	{
		lastField.writeObject (value, out);
	}


	private void remove (int i, int[] indices, int ii, Object object)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				remove (i + 1, indices, ii, o);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		assert f.isArray;
		Object array = f.isArrayComponent ? f.arrayField.getArrayComponent (object, indices[ii++])
			: f.getObject (object);
		i = indices[ii];
		if (f.usesList)
		{
			switch (f.arrayComponent.typeId)
			{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
				case TypeId.$pp.TYPE:
					((${pp.Type}List) array).removeAt (i);
					break;
#end
!!*/
//!! #* Start of generated code
// generated
				case TypeId.BOOLEAN:
					((BooleanList) array).removeAt (i);
					break;
// generated
				case TypeId.BYTE:
					((ByteList) array).removeAt (i);
					break;
// generated
				case TypeId.SHORT:
					((ShortList) array).removeAt (i);
					break;
// generated
				case TypeId.CHAR:
					((CharList) array).removeAt (i);
					break;
// generated
				case TypeId.INT:
					((IntList) array).removeAt (i);
					break;
// generated
				case TypeId.LONG:
					((LongList) array).removeAt (i);
					break;
// generated
				case TypeId.FLOAT:
					((FloatList) array).removeAt (i);
					break;
// generated
				case TypeId.DOUBLE:
					((DoubleList) array).removeAt (i);
					break;
//!! *# End of generated code
				case TypeId.OBJECT:
					((List) array).remove (i);
					break;
			}			
		}
		else
		{
			int n = Array.getLength (array) - 1;
			Object a = f.getType ().createArray (n);
			if (i > 0)
			{
				System.arraycopy (array, 0, a, 0, i);
			}
			if (i < n)
			{
				System.arraycopy (array, i + 1, a, i, n - i);
			}
			if (f.isArrayComponent)
			{
				f.arrayField.setArrayComponent (object, indices[ii - 1], a);
			}
			else
			{
				f.setObject (object, a);
			}
		}
	}


	private static void cycle (int[] a, int n)
	{
		if (n > 1)
		{
			int f = a[0];
			for (int i = 1; i < n; i++)
			{
				a[i - 1] = a[i];
			}
			a[n - 1] = f;
		}
	}


/*!!

#macro (PREPARE_MOD $haveObject)
		assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
#if ($haveObject)
		if ((pm != null) & fco)
		{
			pm.makePersistent (value, t);
		}
#end
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
#end

#macro (FINISH_MOD $fieldSuffix)
		if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f$fieldSuffix, indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc)$fieldSuffix, indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
#end

#macro (PREPARE_READ)
		assert begin == 0;
#if ($pp.Object)
		Object v = pc;
		int i;
		int ai = -1;
		for (i = 0; i < length - 1; i++)
		{
			v = chain[i].isArrayComponent
				? chain[i].arrayField.getArrayComponent (v, indices[++ai])
				: chain[i].getObject (v);
		}
		try
		{
			v = lastField.readObject
				(v, lastField.isArrayComponent ? indices[ai + 1] : -1,
				 reader);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
#else
		$type v = reader.read$pp.Type ();
#end
#end

#foreach ($type in $types)
$pp.setType($type)

	@Override
	public void set$pp.Type (Object o, int[] indices, $type value,
							 Transaction t)
	{
		#PREPARE_MOD($pp.Object)
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logSet$pp.Type ((PersistenceCapable) o, f, indices, f.get$pp.Type (o, indices), value);
		}
		f.set$pp.Type (0, indices, 0, o, value);
		#FINISH_MOD("")
	}


	@Override
	public void insert$pp.Type (Object o, int[] indices,
								$type value, Transaction t)
	{
		#PREPARE_MOD($pp.Object)
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logInsert$pp.Type ((PersistenceCapable) o, f, indices, value);
		}
		((IndirectField) f.getShallowSuperchain ()).insert$pp.Type (0, indices, 0, o, value);
		#FINISH_MOD(".getShallowSuperchain ()")
	}


	@Override
	public void remove$pp.Type (Object o, int[] indices,
								Transaction t)
	{
		#PREPARE_MOD(false)
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logRemove$pp.Type ((PersistenceCapable) o, f, indices, f.get$pp.Type (o, indices));
		}
		((IndirectField) getShallowSuperchain ()).remove (0, indices, 0, o);
		#FINISH_MOD(".getShallowSuperchain ()")
	}


	@Override
	$type readAndSet$pp.Type (PersistenceCapable pc, int[] indices,
							  XAQueue.Reader reader)
	{
		#PREPARE_READ()
		set$pp.Type (0, indices, 0, pc, v);
		return v;
	}


	@Override
	void readAndInsert$pp.Type (PersistenceCapable pc, int[] indices,
								XAQueue.Reader reader)
	{
		#PREPARE_READ()
		((IndirectField) getShallowSuperchain ()).insert$pp.Type (0, indices, 0, pc, v);
	}


	public $type get$pp.Type (Object object)
	{
		if (indexCount > 0)
		{
			throw new IllegalStateException (this + " contains array components");
		}
#if ($pp.Object)
		if (length == 0)
		{
			return object;
		}
#end
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].getObject (object);
		}
		return lastField.get$pp.Type (object);
	}


	@Override
	public $type get$pp.Type (Object object, int[] indices)
	{
#if ($pp.Object)
		if (length == 0)
		{
			return object;
		}
#end
		int ai = -1;
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].isArrayComponent
				? chain[begin + i].arrayField.getArrayComponent (object, indices[++ai])
				: chain[begin + i].getObject (object);
		}
		return lastField.isArrayComponent
#if ($pp.Object)
			? lastField.arrayField.getArrayComponent (object, indices[ai + 1])
#else
			? (($type[]) lastField.arrayField.getArray (object))[indices[ai + 1]]
#end
			: lastField.get$pp.Type (object);
	}


	public final void set$pp.Type (Object object, $type value)
	{
		set$pp.Type (0, null, 0, object, value);
	}


	private void set$pp.Type (int i, int[] indices, int ii, Object object, $type value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				set$pp.Type (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
#if ($pp.Object)
		if (f.usesList)
		{
			Object list = f.isArrayComponent
				? f.arrayField.getArrayComponent (object, indices[ii])
				: f.getObject (object);
			value = f.setList (list, value);
			if ((list == value) && !f.isGetReturningCopy ())
			{
				return;
			}
		}
#end
		if (f.isArrayComponent)
		{
#if ($pp.Object)
			i = indices[ii];
			if (f.manageableType != null)
			{
				f.arrayField.setArrayComponent
					(object, i,
					 f.manageableType.setObject
						(f.arrayField.getArrayComponent (object, i), value));
			}
			else
			{
				f.arrayField.setArrayComponent (object, i, value);
			}
#else
			(($type[]) f.arrayField.getArray (object))[indices[ii]] = value;
#end
		}
		else
		{
			f.set$pp.Type (object, value);
		}
	}


	private void insert$pp.Type (int i, int[] indices, int ii,
								 Object object, $type value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				insert$pp.Type (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		assert f.isArray;
		Object array = f.isArrayComponent ? f.arrayField.getArrayComponent (object, indices[ii++])
			: f.getObject (object);
		i = indices[ii];
		if (f.usesList)
		{
#if ($pp.Object)
			((List) array).add (i, value);
#else
			((${pp.Type}List) array).add (i, value);
#end
		}
		else
		{
			$type[] a = ($type[]) array;
			int n = a.length;
#if ($pp.Object)
			a = (Object[]) f.getType ().createArray (n + 1);
#else
			a = new $type[n + 1];
#end
			if (i > 0)
			{
				System.arraycopy (array, 0, a, 0, i);
			}
			if (i < n)
			{
				System.arraycopy (array, i, a, i + 1, n - i);
			}
			a[i] = value;
			if (f.isArrayComponent)
			{
				f.arrayField.setArrayComponent (object, indices[ii - 1], a);
			}
			else
			{
				f.setObject (object, a);
			}
		}
	}

#end

!!*/
//!! #* Start of generated code
// generated
// generated
// generated
// generated
// generated
// generated
	@Override
	public void setBoolean (Object o, int[] indices, boolean value,
							 Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logSetBoolean ((PersistenceCapable) o, f, indices, f.getBoolean (o, indices), value);
		}
		f.setBoolean (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f, indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void insertBoolean (Object o, int[] indices,
								boolean value, Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logInsertBoolean ((PersistenceCapable) o, f, indices, value);
		}
		((IndirectField) f.getShallowSuperchain ()).insertBoolean (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void removeBoolean (Object o, int[] indices,
								Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logRemoveBoolean ((PersistenceCapable) o, f, indices, f.getBoolean (o, indices));
		}
		((IndirectField) getShallowSuperchain ()).remove (0, indices, 0, o);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	boolean readAndSetBoolean (PersistenceCapable pc, int[] indices,
							  XAQueue.Reader reader)
	{
				assert begin == 0;
		boolean v = reader.readBoolean ();
		setBoolean (0, indices, 0, pc, v);
		return v;
	}
// generated
// generated
	@Override
	void readAndInsertBoolean (PersistenceCapable pc, int[] indices,
								XAQueue.Reader reader)
	{
				assert begin == 0;
		boolean v = reader.readBoolean ();
		((IndirectField) getShallowSuperchain ()).insertBoolean (0, indices, 0, pc, v);
	}
// generated
// generated
	public boolean getBoolean (Object object)
	{
		if (indexCount > 0)
		{
			throw new IllegalStateException (this + " contains array components");
		}
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].getObject (object);
		}
		return lastField.getBoolean (object);
	}
// generated
// generated
	@Override
	public boolean getBoolean (Object object, int[] indices)
	{
		int ai = -1;
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].isArrayComponent
				? chain[begin + i].arrayField.getArrayComponent (object, indices[++ai])
				: chain[begin + i].getObject (object);
		}
		return lastField.isArrayComponent
			? ((boolean[]) lastField.arrayField.getArray (object))[indices[ai + 1]]
			: lastField.getBoolean (object);
	}
// generated
// generated
	public final void setBoolean (Object object, boolean value)
	{
		setBoolean (0, null, 0, object, value);
	}
// generated
// generated
	private void setBoolean (int i, int[] indices, int ii, Object object, boolean value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				setBoolean (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		if (f.isArrayComponent)
		{
			((boolean[]) f.arrayField.getArray (object))[indices[ii]] = value;
		}
		else
		{
			f.setBoolean (object, value);
		}
	}
// generated
// generated
	private void insertBoolean (int i, int[] indices, int ii,
								 Object object, boolean value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				insertBoolean (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		assert f.isArray;
		Object array = f.isArrayComponent ? f.arrayField.getArrayComponent (object, indices[ii++])
			: f.getObject (object);
		i = indices[ii];
		if (f.usesList)
		{
			((BooleanList) array).add (i, value);
		}
		else
		{
			boolean[] a = (boolean[]) array;
			int n = a.length;
			a = new boolean[n + 1];
			if (i > 0)
			{
				System.arraycopy (array, 0, a, 0, i);
			}
			if (i < n)
			{
				System.arraycopy (array, i, a, i + 1, n - i);
			}
			a[i] = value;
			if (f.isArrayComponent)
			{
				f.arrayField.setArrayComponent (object, indices[ii - 1], a);
			}
			else
			{
				f.setObject (object, a);
			}
		}
	}
// generated
// generated
// generated
	@Override
	public void setByte (Object o, int[] indices, byte value,
							 Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logSetByte ((PersistenceCapable) o, f, indices, f.getByte (o, indices), value);
		}
		f.setByte (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f, indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void insertByte (Object o, int[] indices,
								byte value, Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logInsertByte ((PersistenceCapable) o, f, indices, value);
		}
		((IndirectField) f.getShallowSuperchain ()).insertByte (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void removeByte (Object o, int[] indices,
								Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logRemoveByte ((PersistenceCapable) o, f, indices, f.getByte (o, indices));
		}
		((IndirectField) getShallowSuperchain ()).remove (0, indices, 0, o);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	byte readAndSetByte (PersistenceCapable pc, int[] indices,
							  XAQueue.Reader reader)
	{
				assert begin == 0;
		byte v = reader.readByte ();
		setByte (0, indices, 0, pc, v);
		return v;
	}
// generated
// generated
	@Override
	void readAndInsertByte (PersistenceCapable pc, int[] indices,
								XAQueue.Reader reader)
	{
				assert begin == 0;
		byte v = reader.readByte ();
		((IndirectField) getShallowSuperchain ()).insertByte (0, indices, 0, pc, v);
	}
// generated
// generated
	public byte getByte (Object object)
	{
		if (indexCount > 0)
		{
			throw new IllegalStateException (this + " contains array components");
		}
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].getObject (object);
		}
		return lastField.getByte (object);
	}
// generated
// generated
	@Override
	public byte getByte (Object object, int[] indices)
	{
		int ai = -1;
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].isArrayComponent
				? chain[begin + i].arrayField.getArrayComponent (object, indices[++ai])
				: chain[begin + i].getObject (object);
		}
		return lastField.isArrayComponent
			? ((byte[]) lastField.arrayField.getArray (object))[indices[ai + 1]]
			: lastField.getByte (object);
	}
// generated
// generated
	public final void setByte (Object object, byte value)
	{
		setByte (0, null, 0, object, value);
	}
// generated
// generated
	private void setByte (int i, int[] indices, int ii, Object object, byte value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				setByte (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		if (f.isArrayComponent)
		{
			((byte[]) f.arrayField.getArray (object))[indices[ii]] = value;
		}
		else
		{
			f.setByte (object, value);
		}
	}
// generated
// generated
	private void insertByte (int i, int[] indices, int ii,
								 Object object, byte value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				insertByte (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		assert f.isArray;
		Object array = f.isArrayComponent ? f.arrayField.getArrayComponent (object, indices[ii++])
			: f.getObject (object);
		i = indices[ii];
		if (f.usesList)
		{
			((ByteList) array).add (i, value);
		}
		else
		{
			byte[] a = (byte[]) array;
			int n = a.length;
			a = new byte[n + 1];
			if (i > 0)
			{
				System.arraycopy (array, 0, a, 0, i);
			}
			if (i < n)
			{
				System.arraycopy (array, i, a, i + 1, n - i);
			}
			a[i] = value;
			if (f.isArrayComponent)
			{
				f.arrayField.setArrayComponent (object, indices[ii - 1], a);
			}
			else
			{
				f.setObject (object, a);
			}
		}
	}
// generated
// generated
// generated
	@Override
	public void setShort (Object o, int[] indices, short value,
							 Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logSetShort ((PersistenceCapable) o, f, indices, f.getShort (o, indices), value);
		}
		f.setShort (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f, indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void insertShort (Object o, int[] indices,
								short value, Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logInsertShort ((PersistenceCapable) o, f, indices, value);
		}
		((IndirectField) f.getShallowSuperchain ()).insertShort (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void removeShort (Object o, int[] indices,
								Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logRemoveShort ((PersistenceCapable) o, f, indices, f.getShort (o, indices));
		}
		((IndirectField) getShallowSuperchain ()).remove (0, indices, 0, o);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	short readAndSetShort (PersistenceCapable pc, int[] indices,
							  XAQueue.Reader reader)
	{
				assert begin == 0;
		short v = reader.readShort ();
		setShort (0, indices, 0, pc, v);
		return v;
	}
// generated
// generated
	@Override
	void readAndInsertShort (PersistenceCapable pc, int[] indices,
								XAQueue.Reader reader)
	{
				assert begin == 0;
		short v = reader.readShort ();
		((IndirectField) getShallowSuperchain ()).insertShort (0, indices, 0, pc, v);
	}
// generated
// generated
	public short getShort (Object object)
	{
		if (indexCount > 0)
		{
			throw new IllegalStateException (this + " contains array components");
		}
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].getObject (object);
		}
		return lastField.getShort (object);
	}
// generated
// generated
	@Override
	public short getShort (Object object, int[] indices)
	{
		int ai = -1;
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].isArrayComponent
				? chain[begin + i].arrayField.getArrayComponent (object, indices[++ai])
				: chain[begin + i].getObject (object);
		}
		return lastField.isArrayComponent
			? ((short[]) lastField.arrayField.getArray (object))[indices[ai + 1]]
			: lastField.getShort (object);
	}
// generated
// generated
	public final void setShort (Object object, short value)
	{
		setShort (0, null, 0, object, value);
	}
// generated
// generated
	private void setShort (int i, int[] indices, int ii, Object object, short value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				setShort (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		if (f.isArrayComponent)
		{
			((short[]) f.arrayField.getArray (object))[indices[ii]] = value;
		}
		else
		{
			f.setShort (object, value);
		}
	}
// generated
// generated
	private void insertShort (int i, int[] indices, int ii,
								 Object object, short value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				insertShort (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		assert f.isArray;
		Object array = f.isArrayComponent ? f.arrayField.getArrayComponent (object, indices[ii++])
			: f.getObject (object);
		i = indices[ii];
		if (f.usesList)
		{
			((ShortList) array).add (i, value);
		}
		else
		{
			short[] a = (short[]) array;
			int n = a.length;
			a = new short[n + 1];
			if (i > 0)
			{
				System.arraycopy (array, 0, a, 0, i);
			}
			if (i < n)
			{
				System.arraycopy (array, i, a, i + 1, n - i);
			}
			a[i] = value;
			if (f.isArrayComponent)
			{
				f.arrayField.setArrayComponent (object, indices[ii - 1], a);
			}
			else
			{
				f.setObject (object, a);
			}
		}
	}
// generated
// generated
// generated
	@Override
	public void setChar (Object o, int[] indices, char value,
							 Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logSetChar ((PersistenceCapable) o, f, indices, f.getChar (o, indices), value);
		}
		f.setChar (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f, indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void insertChar (Object o, int[] indices,
								char value, Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logInsertChar ((PersistenceCapable) o, f, indices, value);
		}
		((IndirectField) f.getShallowSuperchain ()).insertChar (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void removeChar (Object o, int[] indices,
								Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logRemoveChar ((PersistenceCapable) o, f, indices, f.getChar (o, indices));
		}
		((IndirectField) getShallowSuperchain ()).remove (0, indices, 0, o);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	char readAndSetChar (PersistenceCapable pc, int[] indices,
							  XAQueue.Reader reader)
	{
				assert begin == 0;
		char v = reader.readChar ();
		setChar (0, indices, 0, pc, v);
		return v;
	}
// generated
// generated
	@Override
	void readAndInsertChar (PersistenceCapable pc, int[] indices,
								XAQueue.Reader reader)
	{
				assert begin == 0;
		char v = reader.readChar ();
		((IndirectField) getShallowSuperchain ()).insertChar (0, indices, 0, pc, v);
	}
// generated
// generated
	public char getChar (Object object)
	{
		if (indexCount > 0)
		{
			throw new IllegalStateException (this + " contains array components");
		}
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].getObject (object);
		}
		return lastField.getChar (object);
	}
// generated
// generated
	@Override
	public char getChar (Object object, int[] indices)
	{
		int ai = -1;
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].isArrayComponent
				? chain[begin + i].arrayField.getArrayComponent (object, indices[++ai])
				: chain[begin + i].getObject (object);
		}
		return lastField.isArrayComponent
			? ((char[]) lastField.arrayField.getArray (object))[indices[ai + 1]]
			: lastField.getChar (object);
	}
// generated
// generated
	public final void setChar (Object object, char value)
	{
		setChar (0, null, 0, object, value);
	}
// generated
// generated
	private void setChar (int i, int[] indices, int ii, Object object, char value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				setChar (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		if (f.isArrayComponent)
		{
			((char[]) f.arrayField.getArray (object))[indices[ii]] = value;
		}
		else
		{
			f.setChar (object, value);
		}
	}
// generated
// generated
	private void insertChar (int i, int[] indices, int ii,
								 Object object, char value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				insertChar (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		assert f.isArray;
		Object array = f.isArrayComponent ? f.arrayField.getArrayComponent (object, indices[ii++])
			: f.getObject (object);
		i = indices[ii];
		if (f.usesList)
		{
			((CharList) array).add (i, value);
		}
		else
		{
			char[] a = (char[]) array;
			int n = a.length;
			a = new char[n + 1];
			if (i > 0)
			{
				System.arraycopy (array, 0, a, 0, i);
			}
			if (i < n)
			{
				System.arraycopy (array, i, a, i + 1, n - i);
			}
			a[i] = value;
			if (f.isArrayComponent)
			{
				f.arrayField.setArrayComponent (object, indices[ii - 1], a);
			}
			else
			{
				f.setObject (object, a);
			}
		}
	}
// generated
// generated
// generated
	@Override
	public void setInt (Object o, int[] indices, int value,
							 Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logSetInt ((PersistenceCapable) o, f, indices, f.getInt (o, indices), value);
		}
		f.setInt (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f, indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void insertInt (Object o, int[] indices,
								int value, Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logInsertInt ((PersistenceCapable) o, f, indices, value);
		}
		((IndirectField) f.getShallowSuperchain ()).insertInt (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void removeInt (Object o, int[] indices,
								Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logRemoveInt ((PersistenceCapable) o, f, indices, f.getInt (o, indices));
		}
		((IndirectField) getShallowSuperchain ()).remove (0, indices, 0, o);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	int readAndSetInt (PersistenceCapable pc, int[] indices,
							  XAQueue.Reader reader)
	{
				assert begin == 0;
		int v = reader.readInt ();
		setInt (0, indices, 0, pc, v);
		return v;
	}
// generated
// generated
	@Override
	void readAndInsertInt (PersistenceCapable pc, int[] indices,
								XAQueue.Reader reader)
	{
				assert begin == 0;
		int v = reader.readInt ();
		((IndirectField) getShallowSuperchain ()).insertInt (0, indices, 0, pc, v);
	}
// generated
// generated
	public int getInt (Object object)
	{
		if (indexCount > 0)
		{
			throw new IllegalStateException (this + " contains array components");
		}
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].getObject (object);
		}
		return lastField.getInt (object);
	}
// generated
// generated
	@Override
	public int getInt (Object object, int[] indices)
	{
		int ai = -1;
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].isArrayComponent
				? chain[begin + i].arrayField.getArrayComponent (object, indices[++ai])
				: chain[begin + i].getObject (object);
		}
		return lastField.isArrayComponent
			? ((int[]) lastField.arrayField.getArray (object))[indices[ai + 1]]
			: lastField.getInt (object);
	}
// generated
// generated
	public final void setInt (Object object, int value)
	{
		setInt (0, null, 0, object, value);
	}
// generated
// generated
	private void setInt (int i, int[] indices, int ii, Object object, int value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				setInt (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		if (f.isArrayComponent)
		{
			((int[]) f.arrayField.getArray (object))[indices[ii]] = value;
		}
		else
		{
			f.setInt (object, value);
		}
	}
// generated
// generated
	private void insertInt (int i, int[] indices, int ii,
								 Object object, int value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				insertInt (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		assert f.isArray;
		Object array = f.isArrayComponent ? f.arrayField.getArrayComponent (object, indices[ii++])
			: f.getObject (object);
		i = indices[ii];
		if (f.usesList)
		{
			((IntList) array).add (i, value);
		}
		else
		{
			int[] a = (int[]) array;
			int n = a.length;
			a = new int[n + 1];
			if (i > 0)
			{
				System.arraycopy (array, 0, a, 0, i);
			}
			if (i < n)
			{
				System.arraycopy (array, i, a, i + 1, n - i);
			}
			a[i] = value;
			if (f.isArrayComponent)
			{
				f.arrayField.setArrayComponent (object, indices[ii - 1], a);
			}
			else
			{
				f.setObject (object, a);
			}
		}
	}
// generated
// generated
// generated
	@Override
	public void setLong (Object o, int[] indices, long value,
							 Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logSetLong ((PersistenceCapable) o, f, indices, f.getLong (o, indices), value);
		}
		f.setLong (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f, indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void insertLong (Object o, int[] indices,
								long value, Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logInsertLong ((PersistenceCapable) o, f, indices, value);
		}
		((IndirectField) f.getShallowSuperchain ()).insertLong (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void removeLong (Object o, int[] indices,
								Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logRemoveLong ((PersistenceCapable) o, f, indices, f.getLong (o, indices));
		}
		((IndirectField) getShallowSuperchain ()).remove (0, indices, 0, o);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	long readAndSetLong (PersistenceCapable pc, int[] indices,
							  XAQueue.Reader reader)
	{
				assert begin == 0;
		long v = reader.readLong ();
		setLong (0, indices, 0, pc, v);
		return v;
	}
// generated
// generated
	@Override
	void readAndInsertLong (PersistenceCapable pc, int[] indices,
								XAQueue.Reader reader)
	{
				assert begin == 0;
		long v = reader.readLong ();
		((IndirectField) getShallowSuperchain ()).insertLong (0, indices, 0, pc, v);
	}
// generated
// generated
	public long getLong (Object object)
	{
		if (indexCount > 0)
		{
			throw new IllegalStateException (this + " contains array components");
		}
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].getObject (object);
		}
		return lastField.getLong (object);
	}
// generated
// generated
	@Override
	public long getLong (Object object, int[] indices)
	{
		int ai = -1;
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].isArrayComponent
				? chain[begin + i].arrayField.getArrayComponent (object, indices[++ai])
				: chain[begin + i].getObject (object);
		}
		return lastField.isArrayComponent
			? ((long[]) lastField.arrayField.getArray (object))[indices[ai + 1]]
			: lastField.getLong (object);
	}
// generated
// generated
	public final void setLong (Object object, long value)
	{
		setLong (0, null, 0, object, value);
	}
// generated
// generated
	private void setLong (int i, int[] indices, int ii, Object object, long value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				setLong (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		if (f.isArrayComponent)
		{
			((long[]) f.arrayField.getArray (object))[indices[ii]] = value;
		}
		else
		{
			f.setLong (object, value);
		}
	}
// generated
// generated
	private void insertLong (int i, int[] indices, int ii,
								 Object object, long value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				insertLong (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		assert f.isArray;
		Object array = f.isArrayComponent ? f.arrayField.getArrayComponent (object, indices[ii++])
			: f.getObject (object);
		i = indices[ii];
		if (f.usesList)
		{
			((LongList) array).add (i, value);
		}
		else
		{
			long[] a = (long[]) array;
			int n = a.length;
			a = new long[n + 1];
			if (i > 0)
			{
				System.arraycopy (array, 0, a, 0, i);
			}
			if (i < n)
			{
				System.arraycopy (array, i, a, i + 1, n - i);
			}
			a[i] = value;
			if (f.isArrayComponent)
			{
				f.arrayField.setArrayComponent (object, indices[ii - 1], a);
			}
			else
			{
				f.setObject (object, a);
			}
		}
	}
// generated
// generated
// generated
	@Override
	public void setFloat (Object o, int[] indices, float value,
							 Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logSetFloat ((PersistenceCapable) o, f, indices, f.getFloat (o, indices), value);
		}
		f.setFloat (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f, indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void insertFloat (Object o, int[] indices,
								float value, Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logInsertFloat ((PersistenceCapable) o, f, indices, value);
		}
		((IndirectField) f.getShallowSuperchain ()).insertFloat (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void removeFloat (Object o, int[] indices,
								Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logRemoveFloat ((PersistenceCapable) o, f, indices, f.getFloat (o, indices));
		}
		((IndirectField) getShallowSuperchain ()).remove (0, indices, 0, o);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	float readAndSetFloat (PersistenceCapable pc, int[] indices,
							  XAQueue.Reader reader)
	{
				assert begin == 0;
		float v = reader.readFloat ();
		setFloat (0, indices, 0, pc, v);
		return v;
	}
// generated
// generated
	@Override
	void readAndInsertFloat (PersistenceCapable pc, int[] indices,
								XAQueue.Reader reader)
	{
				assert begin == 0;
		float v = reader.readFloat ();
		((IndirectField) getShallowSuperchain ()).insertFloat (0, indices, 0, pc, v);
	}
// generated
// generated
	public float getFloat (Object object)
	{
		if (indexCount > 0)
		{
			throw new IllegalStateException (this + " contains array components");
		}
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].getObject (object);
		}
		return lastField.getFloat (object);
	}
// generated
// generated
	@Override
	public float getFloat (Object object, int[] indices)
	{
		int ai = -1;
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].isArrayComponent
				? chain[begin + i].arrayField.getArrayComponent (object, indices[++ai])
				: chain[begin + i].getObject (object);
		}
		return lastField.isArrayComponent
			? ((float[]) lastField.arrayField.getArray (object))[indices[ai + 1]]
			: lastField.getFloat (object);
	}
// generated
// generated
	public final void setFloat (Object object, float value)
	{
		setFloat (0, null, 0, object, value);
	}
// generated
// generated
	private void setFloat (int i, int[] indices, int ii, Object object, float value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				setFloat (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		if (f.isArrayComponent)
		{
			((float[]) f.arrayField.getArray (object))[indices[ii]] = value;
		}
		else
		{
			f.setFloat (object, value);
		}
	}
// generated
// generated
	private void insertFloat (int i, int[] indices, int ii,
								 Object object, float value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				insertFloat (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		assert f.isArray;
		Object array = f.isArrayComponent ? f.arrayField.getArrayComponent (object, indices[ii++])
			: f.getObject (object);
		i = indices[ii];
		if (f.usesList)
		{
			((FloatList) array).add (i, value);
		}
		else
		{
			float[] a = (float[]) array;
			int n = a.length;
			a = new float[n + 1];
			if (i > 0)
			{
				System.arraycopy (array, 0, a, 0, i);
			}
			if (i < n)
			{
				System.arraycopy (array, i, a, i + 1, n - i);
			}
			a[i] = value;
			if (f.isArrayComponent)
			{
				f.arrayField.setArrayComponent (object, indices[ii - 1], a);
			}
			else
			{
				f.setObject (object, a);
			}
		}
	}
// generated
// generated
// generated
	@Override
	public void setDouble (Object o, int[] indices, double value,
							 Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logSetDouble ((PersistenceCapable) o, f, indices, f.getDouble (o, indices), value);
		}
		f.setDouble (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f, indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void insertDouble (Object o, int[] indices,
								double value, Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logInsertDouble ((PersistenceCapable) o, f, indices, value);
		}
		((IndirectField) f.getShallowSuperchain ()).insertDouble (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void removeDouble (Object o, int[] indices,
								Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logRemoveDouble ((PersistenceCapable) o, f, indices, f.getDouble (o, indices));
		}
		((IndirectField) getShallowSuperchain ()).remove (0, indices, 0, o);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	double readAndSetDouble (PersistenceCapable pc, int[] indices,
							  XAQueue.Reader reader)
	{
				assert begin == 0;
		double v = reader.readDouble ();
		setDouble (0, indices, 0, pc, v);
		return v;
	}
// generated
// generated
	@Override
	void readAndInsertDouble (PersistenceCapable pc, int[] indices,
								XAQueue.Reader reader)
	{
				assert begin == 0;
		double v = reader.readDouble ();
		((IndirectField) getShallowSuperchain ()).insertDouble (0, indices, 0, pc, v);
	}
// generated
// generated
	public double getDouble (Object object)
	{
		if (indexCount > 0)
		{
			throw new IllegalStateException (this + " contains array components");
		}
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].getObject (object);
		}
		return lastField.getDouble (object);
	}
// generated
// generated
	@Override
	public double getDouble (Object object, int[] indices)
	{
		int ai = -1;
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].isArrayComponent
				? chain[begin + i].arrayField.getArrayComponent (object, indices[++ai])
				: chain[begin + i].getObject (object);
		}
		return lastField.isArrayComponent
			? ((double[]) lastField.arrayField.getArray (object))[indices[ai + 1]]
			: lastField.getDouble (object);
	}
// generated
// generated
	public final void setDouble (Object object, double value)
	{
		setDouble (0, null, 0, object, value);
	}
// generated
// generated
	private void setDouble (int i, int[] indices, int ii, Object object, double value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				setDouble (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		if (f.isArrayComponent)
		{
			((double[]) f.arrayField.getArray (object))[indices[ii]] = value;
		}
		else
		{
			f.setDouble (object, value);
		}
	}
// generated
// generated
	private void insertDouble (int i, int[] indices, int ii,
								 Object object, double value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				insertDouble (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		assert f.isArray;
		Object array = f.isArrayComponent ? f.arrayField.getArrayComponent (object, indices[ii++])
			: f.getObject (object);
		i = indices[ii];
		if (f.usesList)
		{
			((DoubleList) array).add (i, value);
		}
		else
		{
			double[] a = (double[]) array;
			int n = a.length;
			a = new double[n + 1];
			if (i > 0)
			{
				System.arraycopy (array, 0, a, 0, i);
			}
			if (i < n)
			{
				System.arraycopy (array, i, a, i + 1, n - i);
			}
			a[i] = value;
			if (f.isArrayComponent)
			{
				f.arrayField.setArrayComponent (object, indices[ii - 1], a);
			}
			else
			{
				f.setObject (object, a);
			}
		}
	}
// generated
// generated
// generated
	@Override
	public void setObject (Object o, int[] indices, Object value,
							 Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		if ((pm != null) & fco)
		{
			pm.makePersistent (value, t);
		}
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logSetObject ((PersistenceCapable) o, f, indices, f.getObject (o, indices), value);
		}
		f.setObject (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f, indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void insertObject (Object o, int[] indices,
								Object value, Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		if ((pm != null) & fco)
		{
			pm.makePersistent (value, t);
		}
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logInsertObject ((PersistenceCapable) o, f, indices, value);
		}
		((IndirectField) f.getShallowSuperchain ()).insertObject (0, indices, 0, o, value);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	public void removeObject (Object o, int[] indices,
								Transaction t)
	{
				assert begin == 0;
		PersistenceManager pm = (o instanceof PersistenceCapable)
			? ((PersistenceCapable) o).getPersistenceManager () : null;
		Object object = o;
		int subfieldIndex = 0;
		int cc = indexCount;
		int ii = 0;
		for (int i = 0; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[ii++]);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if ((object instanceof PersistenceCapable)
				&& (((PersistenceCapable) object).getPersistenceManager ()
					!= null))
			{
				o = (PersistenceCapable) object;
				pm = ((PersistenceCapable) o).getPersistenceManager ();
				subfieldIndex = i + 1;
				cc -= ii;
				while (ii > 0)
				{
					cycle (indices, indexCount);
					ii--;
				}
			}
		}
		IndirectField f = (subfieldIndex > 0) ? pooledSubfield (subfieldIndex, cc)
			: this;
		if ((t != null) && (pm != null))
		{
			t.makeActive ()
				.logRemoveObject ((PersistenceCapable) o, f, indices, f.getObject (o, indices));
		}
		((IndirectField) getShallowSuperchain ()).remove (0, indices, 0, o);
				if (o instanceof Manageable)
		{
			((Manageable) o).fieldModified (f.getShallowSuperchain (), indices, t);
		}
		object = o;
		for (int i = subfieldIndex; i < length - 1; i++)
		{
			if (chain[i].isArrayComponent)
			{
				object = chain[i].arrayField.getArrayComponent (object, indices[0]);
				cc--;
				cycle (indices, indexCount);
			}
			else
			{
				object = chain[i].getObject (object);
			}
			if (object instanceof Manageable)
			{
				((Manageable) object).fieldModified (pooledSubfield (i + 1, cc).getShallowSuperchain (), indices, t);
			}
		}
		if (cc < indexCount)
		{
			while (--cc >= 0)
			{
				cycle (indices, indexCount);
			}
		}
	}
// generated
// generated
	@Override
	Object readAndSetObject (PersistenceCapable pc, int[] indices,
							  XAQueue.Reader reader)
	{
				assert begin == 0;
		Object v = pc;
		int i;
		int ai = -1;
		for (i = 0; i < length - 1; i++)
		{
			v = chain[i].isArrayComponent
				? chain[i].arrayField.getArrayComponent (v, indices[++ai])
				: chain[i].getObject (v);
		}
		try
		{
			v = lastField.readObject
				(v, lastField.isArrayComponent ? indices[ai + 1] : -1,
				 reader);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
		setObject (0, indices, 0, pc, v);
		return v;
	}
// generated
// generated
	@Override
	void readAndInsertObject (PersistenceCapable pc, int[] indices,
								XAQueue.Reader reader)
	{
				assert begin == 0;
		Object v = pc;
		int i;
		int ai = -1;
		for (i = 0; i < length - 1; i++)
		{
			v = chain[i].isArrayComponent
				? chain[i].arrayField.getArrayComponent (v, indices[++ai])
				: chain[i].getObject (v);
		}
		try
		{
			v = lastField.readObject
				(v, lastField.isArrayComponent ? indices[ai + 1] : -1,
				 reader);
		}
		catch (IOException e)
		{
			throw new FatalPersistenceException (e);
		}
		((IndirectField) getShallowSuperchain ()).insertObject (0, indices, 0, pc, v);
	}
// generated
// generated
	public Object getObject (Object object)
	{
		if (indexCount > 0)
		{
			throw new IllegalStateException (this + " contains array components");
		}
		if (length == 0)
		{
			return object;
		}
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].getObject (object);
		}
		return lastField.getObject (object);
	}
// generated
// generated
	@Override
	public Object getObject (Object object, int[] indices)
	{
		if (length == 0)
		{
			return object;
		}
		int ai = -1;
		for (int i = 0; i < length - 1; i++)
		{
			object = chain[begin + i].isArrayComponent
				? chain[begin + i].arrayField.getArrayComponent (object, indices[++ai])
				: chain[begin + i].getObject (object);
		}
		return lastField.isArrayComponent
			? lastField.arrayField.getArrayComponent (object, indices[ai + 1])
			: lastField.getObject (object);
	}
// generated
// generated
	public final void setObject (Object object, Object value)
	{
		setObject (0, null, 0, object, value);
	}
// generated
// generated
	private void setObject (int i, int[] indices, int ii, Object object, Object value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				setObject (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		if (f.usesList)
		{
			Object list = f.isArrayComponent
				? f.arrayField.getArrayComponent (object, indices[ii])
				: f.getObject (object);
			value = f.setList (list, value);
			if ((list == value) && !f.isGetReturningCopy ())
			{
				return;
			}
		}
		if (f.isArrayComponent)
		{
			i = indices[ii];
			if (f.manageableType != null)
			{
				f.arrayField.setArrayComponent
					(object, i,
					 f.manageableType.setObject
						(f.arrayField.getArrayComponent (object, i), value));
			}
			else
			{
				f.arrayField.setArrayComponent (object, i, value);
			}
		}
		else
		{
			f.setObject (object, value);
		}
	}
// generated
// generated
	private void insertObject (int i, int[] indices, int ii,
								 Object object, Object value)
	{
		ManageableType.Field f;
		while (i < length - 1)
		{
			if ((f = chain[begin + i]).isArrayComponent)
			{
				object = f.arrayField.getArrayComponent (object, indices[ii++]);
			}
			else if (f.isGetReturningCopy ())
			{
				Object o = f.getObject (object);
				insertObject (i + 1, indices, ii, o, value);
				f.setObject (object, o);
				return;
			}
			else
			{
				object = f.getObject (object);
			}
			i++;
		}
		f = lastField;
		assert f.isArray;
		Object array = f.isArrayComponent ? f.arrayField.getArrayComponent (object, indices[ii++])
			: f.getObject (object);
		i = indices[ii];
		if (f.usesList)
		{
			((List) array).add (i, value);
		}
		else
		{
			Object[] a = (Object[]) array;
			int n = a.length;
			a = (Object[]) f.getType ().createArray (n + 1);
			if (i > 0)
			{
				System.arraycopy (array, 0, a, 0, i);
			}
			if (i < n)
			{
				System.arraycopy (array, i, a, i + 1, n - i);
			}
			a[i] = value;
			if (f.isArrayComponent)
			{
				f.arrayField.setArrayComponent (object, indices[ii - 1], a);
			}
			else
			{
				f.setObject (object, a);
			}
		}
	}
// generated
// generated
//!! *# End of generated code


	public final void setObject (PersistenceCapable object, int[] indices, Object value)
	{
		setObject (0, indices, 0, object, value);
	}


	@Override
	public String toString ()
	{
		Object[] copy = new Object[length];
		System.arraycopy (chain, begin, copy, 0, length);
		return "IndirectField" + Arrays.toString (copy);
	}

}
