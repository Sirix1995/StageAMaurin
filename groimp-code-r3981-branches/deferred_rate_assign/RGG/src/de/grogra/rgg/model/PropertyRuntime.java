
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

package de.grogra.rgg.model;

import java.io.PrintWriter;

import de.grogra.graph.impl.Extent;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.numeric.Dummy;
import de.grogra.numeric.Solver;
import de.grogra.numeric.Dummy.PropertyKey;
import de.grogra.persistence.IndirectField;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.PersistenceField;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.reflect.TypeLoader;
import de.grogra.xl.impl.property.RuntimeModel;
import de.grogra.xl.property.CompiletimeModel;
import de.grogra.xl.property.RuntimeModelFactory;

import de.grogra.rgg.Library;

public class PropertyRuntime extends RuntimeModel
{
	public static final PropertyRuntime INSTANCE = new PropertyRuntime ();
	

	public static final class GraphProperty<T> implements Property
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
		, ${pp.Type}Property
#end
!!*/
//!! #* Start of generated code
// generated
		, BooleanProperty
// generated
		, ByteProperty
// generated
		, ShortProperty
// generated
		, CharProperty
// generated
		, IntProperty
// generated
		, LongProperty
// generated
		, FloatProperty
// generated
		, DoubleProperty
//!! *# End of generated code
		, ObjectProperty<T>

	{
		final PersistenceField field;


		GraphProperty (PropertyRuntime runtime, PersistenceField field)
		{
			this.field = field;
		}

		public PersistenceField getField()
		{
			return field;
		}

		public Class<?> getType ()
		{
			return field.getType ().getImplementationClass ();
		}

/*!!
#foreach ($type in $types)
$pp.setType($type)
		public $type get$pp.Type (Object object, int[] indices)
		{
			return field.get$pp.Type (object, indices);
		}

		public void set$pp.Type (Object object, int[] indices, $type value)
		{
			field.set$pp.Type (object, indices,
							   value, ((Node) object).getTransaction (true));
		}

#if ($pp.Object)
	#set ($type = "T")
#end
		public void operator$defAssign (Object node, int[] indices, $type value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.set$pp.Type ((Node) node, field, indices, value);
		}


#end
!!*/
//!! #* Start of generated code
// generated
		public boolean getBoolean (Object object, int[] indices)
		{
			return field.getBoolean (object, indices);
		}
// generated
		public void setBoolean (Object object, int[] indices, boolean value)
		{
			field.setBoolean (object, indices,
							   value, ((Node) object).getTransaction (true));
		}
// generated
		public void operator$defAssign (Object node, int[] indices, boolean value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.setBoolean ((Node) node, field, indices, value);
		}
// generated
// generated
// generated
		public byte getByte (Object object, int[] indices)
		{
			return field.getByte (object, indices);
		}
// generated
		public void setByte (Object object, int[] indices, byte value)
		{
			field.setByte (object, indices,
							   value, ((Node) object).getTransaction (true));
		}
// generated
		public void operator$defAssign (Object node, int[] indices, byte value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.setByte ((Node) node, field, indices, value);
		}
// generated
// generated
// generated
		public short getShort (Object object, int[] indices)
		{
			return field.getShort (object, indices);
		}
// generated
		public void setShort (Object object, int[] indices, short value)
		{
			field.setShort (object, indices,
							   value, ((Node) object).getTransaction (true));
		}
// generated
		public void operator$defAssign (Object node, int[] indices, short value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.setShort ((Node) node, field, indices, value);
		}
// generated
// generated
// generated
		public char getChar (Object object, int[] indices)
		{
			return field.getChar (object, indices);
		}
// generated
		public void setChar (Object object, int[] indices, char value)
		{
			field.setChar (object, indices,
							   value, ((Node) object).getTransaction (true));
		}
// generated
		public void operator$defAssign (Object node, int[] indices, char value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.setChar ((Node) node, field, indices, value);
		}
// generated
// generated
// generated
		public int getInt (Object object, int[] indices)
		{
			return field.getInt (object, indices);
		}
// generated
		public void setInt (Object object, int[] indices, int value)
		{
			field.setInt (object, indices,
							   value, ((Node) object).getTransaction (true));
		}
// generated
		public void operator$defAssign (Object node, int[] indices, int value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.setInt ((Node) node, field, indices, value);
		}
// generated
// generated
// generated
		public long getLong (Object object, int[] indices)
		{
			return field.getLong (object, indices);
		}
// generated
		public void setLong (Object object, int[] indices, long value)
		{
			field.setLong (object, indices,
							   value, ((Node) object).getTransaction (true));
		}
// generated
		public void operator$defAssign (Object node, int[] indices, long value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.setLong ((Node) node, field, indices, value);
		}
// generated
// generated
// generated
		public float getFloat (Object object, int[] indices)
		{
			return field.getFloat (object, indices);
		}
// generated
		public void setFloat (Object object, int[] indices, float value)
		{
			field.setFloat (object, indices,
							   value, ((Node) object).getTransaction (true));
		}
// generated
		public void operator$defAssign (Object node, int[] indices, float value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.setFloat ((Node) node, field, indices, value);
		}
// generated
// generated
// generated
		public double getDouble (Object object, int[] indices)
		{
			return field.getDouble (object, indices);
		}
// generated
		public void setDouble (Object object, int[] indices, double value)
		{
			field.setDouble (object, indices,
							   value, ((Node) object).getTransaction (true));
		}
// generated
		public void operator$defAssign (Object node, int[] indices, double value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.setDouble ((Node) node, field, indices, value);
		}
// generated
// generated
// generated
		public Object getObject (Object object, int[] indices)
		{
			return field.getObject (object, indices);
		}
// generated
		public void setObject (Object object, int[] indices, Object value)
		{
			field.setObject (object, indices,
							   value, ((Node) object).getTransaction (true));
		}
// generated
		public void operator$defAssign (Object node, int[] indices, T value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.setObject ((Node) node, field, indices, value);
		}
// generated
// generated
//!! *# End of generated code

/*!!
#foreach ($type in $bittypes)
$pp.setType($type)

		public void operator$deforAssign (Object node, int[] indices, $type value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.or$pp.Type ((Node) node, field, indices, value);
		}
	
		public void operator$defandAssign (Object node, int[] indices, $type value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.and$pp.Type ((Node) node, field, indices, value);
		}
	
		public void operator$defxorAssign (Object node, int[] indices, $type value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.xor$pp.Type ((Node) node, field, indices, value);
		}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
		public void operator$deforAssign (Object node, int[] indices, boolean value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.orBoolean ((Node) node, field, indices, value);
		}
	
		public void operator$defandAssign (Object node, int[] indices, boolean value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.andBoolean ((Node) node, field, indices, value);
		}
	
		public void operator$defxorAssign (Object node, int[] indices, boolean value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.xorBoolean ((Node) node, field, indices, value);
		}
// generated
// generated
		public void operator$deforAssign (Object node, int[] indices, byte value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.orByte ((Node) node, field, indices, value);
		}
	
		public void operator$defandAssign (Object node, int[] indices, byte value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.andByte ((Node) node, field, indices, value);
		}
	
		public void operator$defxorAssign (Object node, int[] indices, byte value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.xorByte ((Node) node, field, indices, value);
		}
// generated
// generated
		public void operator$deforAssign (Object node, int[] indices, short value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.orShort ((Node) node, field, indices, value);
		}
	
		public void operator$defandAssign (Object node, int[] indices, short value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.andShort ((Node) node, field, indices, value);
		}
	
		public void operator$defxorAssign (Object node, int[] indices, short value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.xorShort ((Node) node, field, indices, value);
		}
// generated
// generated
		public void operator$deforAssign (Object node, int[] indices, char value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.orChar ((Node) node, field, indices, value);
		}
	
		public void operator$defandAssign (Object node, int[] indices, char value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.andChar ((Node) node, field, indices, value);
		}
	
		public void operator$defxorAssign (Object node, int[] indices, char value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.xorChar ((Node) node, field, indices, value);
		}
// generated
// generated
		public void operator$deforAssign (Object node, int[] indices, int value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.orInt ((Node) node, field, indices, value);
		}
	
		public void operator$defandAssign (Object node, int[] indices, int value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.andInt ((Node) node, field, indices, value);
		}
	
		public void operator$defxorAssign (Object node, int[] indices, int value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.xorInt ((Node) node, field, indices, value);
		}
// generated
// generated
		public void operator$deforAssign (Object node, int[] indices, long value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.orLong ((Node) node, field, indices, value);
		}
	
		public void operator$defandAssign (Object node, int[] indices, long value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.andLong ((Node) node, field, indices, value);
		}
	
		public void operator$defxorAssign (Object node, int[] indices, long value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.xorLong ((Node) node, field, indices, value);
		}
//!! *# End of generated code

/*!!
#foreach ($type in $numeric_char)
$pp.setType($type)

		public void operator$defaddAssign (Object node, int[] indices, $type value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.add$pp.Type ((Node) node, field, indices, value);
		}

		public void operator$defsubAssign (Object node, int[] indices, $type value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.add$pp.Type ((Node) node, field, indices, ($type) -value);
		}
	
		public void operator$defmulAssign (Object node, int[] indices, $type value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.mul$pp.Type ((Node) node, field, indices, value);
		}
	
		public void operator$defdivAssign (Object node, int[] indices, $type value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.div$pp.Type ((Node) node, field, indices, value);
		}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
		public void operator$defaddAssign (Object node, int[] indices, byte value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.addByte ((Node) node, field, indices, value);
		}
// generated
		public void operator$defsubAssign (Object node, int[] indices, byte value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.addByte ((Node) node, field, indices, (byte) -value);
		}
	
		public void operator$defmulAssign (Object node, int[] indices, byte value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.mulByte ((Node) node, field, indices, value);
		}
	
		public void operator$defdivAssign (Object node, int[] indices, byte value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.divByte ((Node) node, field, indices, value);
		}
// generated
// generated
		public void operator$defaddAssign (Object node, int[] indices, short value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.addShort ((Node) node, field, indices, value);
		}
// generated
		public void operator$defsubAssign (Object node, int[] indices, short value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.addShort ((Node) node, field, indices, (short) -value);
		}
	
		public void operator$defmulAssign (Object node, int[] indices, short value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.mulShort ((Node) node, field, indices, value);
		}
	
		public void operator$defdivAssign (Object node, int[] indices, short value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.divShort ((Node) node, field, indices, value);
		}
// generated
// generated
		public void operator$defaddAssign (Object node, int[] indices, char value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.addChar ((Node) node, field, indices, value);
		}
// generated
		public void operator$defsubAssign (Object node, int[] indices, char value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.addChar ((Node) node, field, indices, (char) -value);
		}
	
		public void operator$defmulAssign (Object node, int[] indices, char value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.mulChar ((Node) node, field, indices, value);
		}
	
		public void operator$defdivAssign (Object node, int[] indices, char value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.divChar ((Node) node, field, indices, value);
		}
// generated
// generated
		public void operator$defaddAssign (Object node, int[] indices, int value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.addInt ((Node) node, field, indices, value);
		}
// generated
		public void operator$defsubAssign (Object node, int[] indices, int value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.addInt ((Node) node, field, indices, (int) -value);
		}
	
		public void operator$defmulAssign (Object node, int[] indices, int value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.mulInt ((Node) node, field, indices, value);
		}
	
		public void operator$defdivAssign (Object node, int[] indices, int value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.divInt ((Node) node, field, indices, value);
		}
// generated
// generated
		public void operator$defaddAssign (Object node, int[] indices, long value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.addLong ((Node) node, field, indices, value);
		}
// generated
		public void operator$defsubAssign (Object node, int[] indices, long value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.addLong ((Node) node, field, indices, (long) -value);
		}
	
		public void operator$defmulAssign (Object node, int[] indices, long value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.mulLong ((Node) node, field, indices, value);
		}
	
		public void operator$defdivAssign (Object node, int[] indices, long value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.divLong ((Node) node, field, indices, value);
		}
// generated
// generated
		public void operator$defaddAssign (Object node, int[] indices, float value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.addFloat ((Node) node, field, indices, value);
		}
// generated
		public void operator$defsubAssign (Object node, int[] indices, float value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.addFloat ((Node) node, field, indices, (float) -value);
		}
	
		public void operator$defmulAssign (Object node, int[] indices, float value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.mulFloat ((Node) node, field, indices, value);
		}
	
		public void operator$defdivAssign (Object node, int[] indices, float value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.divFloat ((Node) node, field, indices, value);
		}
// generated
// generated
		public void operator$defaddAssign (Object node, int[] indices, double value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.addDouble ((Node) node, field, indices, value);
		}
// generated
		public void operator$defsubAssign (Object node, int[] indices, double value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.addDouble ((Node) node, field, indices, (double) -value);
		}
	
		public void operator$defmulAssign (Object node, int[] indices, double value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.mulDouble ((Node) node, field, indices, value);
		}
	
		public void operator$defdivAssign (Object node, int[] indices, double value)
		{
			PropertyQueue q = PropertyQueue.current ((Node) node);
			q.divDouble ((Node) node, field, indices, value);
		}
//!! *# End of generated code
	
		public void operator$defRateAssign (Object node, int[] indices, double value)
		{
			defRateAssign ((Node) node, indices, value);
		}
		
		private void defRateAssign (Node node, int[] indices, double value)
		{
			GraphManager graph = node.getGraph();
			long id = node.getId();
			int base = graph.baseMap.get(id);
			int index = base + this.offset;
				
			graph.rate[index] += value;
		}
		
		public int offset;
	}

	public PropertyRuntime ()
	{
	}

	
	private static Type typeForName (TypeLoader tloader, String name)
	{
		try
		{
			return resolveType (tloader.typeForName (name));
		}
		catch (ClassNotFoundException e)
		{
			return null;
		}
	}

	
	static Type resolveType (Type type)
	{
		ManageableType t = ManageableType.forType (type);
		return (t != null) ? t : type;
	}


	public Property propertyForName (String cfc, TypeLoader loader)
	{
		int i, p = 0;
		Type t = null;
		IndirectField inf = null;
		PersistenceField f = null;
		while ((i = cfc.indexOf (';', p)) >= 0)
		{
			String s;
			if (p == 0)
			{
				s = cfc.substring (p, i);
				t = typeForName (loader, s);
				if (t == null)
				{
					throw new NoClassDefFoundError (s);
				}
			}
			else if (cfc.charAt (p) == '(')
			{
				s = cfc.substring (p + 1, i);
				Type m = typeForName (loader, s);
				if (m != null)
				{
					t = m;
				}
			}
			else
			{
				s = cfc.substring (p, i);
				if (f == null)
				{
					f = ((ManageableType) t).getManagedField (s);
					if (f == null)
					{
						f = ((ManageableType) t).resolveAliasField (s);
					}
				}
				else
				{
					if (inf == null)
					{
						inf = new IndirectField (f);
					}
					if (Reflection.isArray (t))
					{
						assert s.equals ("[");
						f = f.getLastField ().getArrayComponent ();
					}
					else if (t instanceof ManageableType)
					{
						f = ((ManageableType) t).getManagedField (s);
						if (f == null)
						{
							f = ((ManageableType) t).resolveAliasField (s);
						}
					}
					else
					{
						throw new NoSuchFieldError
							("Type " + t + " not manageable.");
					}
				}
				if (f == null)
				{
					throw new NoSuchFieldError (s);
				}
				if (inf != null)
				{
					inf.add (f);
				}
				t = f.getType ();
				if ((t.getTypeId () == TypeId.OBJECT) && !Reflection.isArray (t)
					&& !(t instanceof ManageableType))
				{
					t = resolveType (t);
				}
			}
			p = i + 1;
		}
		return new GraphProperty (this, (inf != null) ? inf : f);
	}

}
