
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

package de.grogra.xl.impl.property;

import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.ClassLoaderAdapter;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;

public abstract class RuntimeModel implements de.grogra.xl.property.RuntimeModel
{
	
	public void initialize (String params)
	{
	}


	public Property propertyForName (String cfc, ClassLoader loader)
	{
		return propertyForName (cfc, new ClassLoaderAdapter (loader));
	}

	public static Type<? extends Property> getInterface (Type<?> type)
	{
		switch (type.getTypeId ())
		{
/*!!
#foreach ($type in $types)
$pp.setType($type)
			case TypeId.$pp.TYPE:
				return ${pp.TYPE}_PROPERTY;
#end
!!*/
//!! #* Start of generated code
// generated
			case TypeId.BOOLEAN:
				return BOOLEAN_PROPERTY;
// generated
			case TypeId.BYTE:
				return BYTE_PROPERTY;
// generated
			case TypeId.SHORT:
				return SHORT_PROPERTY;
// generated
			case TypeId.CHAR:
				return CHAR_PROPERTY;
// generated
			case TypeId.INT:
				return INT_PROPERTY;
// generated
			case TypeId.LONG:
				return LONG_PROPERTY;
// generated
			case TypeId.FLOAT:
				return FLOAT_PROPERTY;
// generated
			case TypeId.DOUBLE:
				return DOUBLE_PROPERTY;
// generated
			case TypeId.OBJECT:
				return OBJECT_PROPERTY;
//!! *# End of generated code
			default:
				throw new IllegalArgumentException (type.toString ());
		}
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	private static final Type<${pp.Type}Property> ${pp.TYPE}_PROPERTY = ClassAdapter.wrap (${pp.Type}Property.class);

	public interface ${pp.Type}Property
#if ($pp.Object)
		<T>
		#set ($type = "T")
#end
	 	extends Property
	{
		void operator$defAssign (Object node, int[] indices, $type value);

#if ($pp.double)
		void operator$defRateAssign (Object node, int[] indices, $type value);
#end

#if ($pp.integral || $pp.boolean)
		void operator$defxorAssign (Object node, int[] indices, $type value);
	
		void operator$defandAssign (Object node, int[] indices, $type value);
	
		void operator$deforAssign (Object node, int[] indices, $type vsalue);
#end

#if ($pp.numeric_char)
		void operator$defaddAssign (Object node, int[] indices, $type value);
	
		void operator$defsubAssign (Object node, int[] indices, $type value);
	
		void operator$defmulAssign (Object node, int[] indices, $type value);

		void operator$defdivAssign (Object node, int[] indices, $type value);
#end		
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	private static final Type<BooleanProperty> BOOLEAN_PROPERTY = ClassAdapter.wrap (BooleanProperty.class);
// generated
	public interface BooleanProperty
	 	extends Property
	{
		void operator$defAssign (Object node, int[] indices, boolean value);
// generated
// generated
		void operator$defxorAssign (Object node, int[] indices, boolean value);
	
		void operator$defandAssign (Object node, int[] indices, boolean value);
	
		void operator$deforAssign (Object node, int[] indices, boolean vsalue);
// generated
	}
// generated
// generated
// generated
	private static final Type<ByteProperty> BYTE_PROPERTY = ClassAdapter.wrap (ByteProperty.class);
// generated
	public interface ByteProperty
	 	extends Property
	{
		void operator$defAssign (Object node, int[] indices, byte value);
// generated
// generated
		void operator$defxorAssign (Object node, int[] indices, byte value);
	
		void operator$defandAssign (Object node, int[] indices, byte value);
	
		void operator$deforAssign (Object node, int[] indices, byte vsalue);
// generated
		void operator$defaddAssign (Object node, int[] indices, byte value);
	
		void operator$defsubAssign (Object node, int[] indices, byte value);
	
		void operator$defmulAssign (Object node, int[] indices, byte value);
// generated
		void operator$defdivAssign (Object node, int[] indices, byte value);
	}
// generated
// generated
// generated
	private static final Type<ShortProperty> SHORT_PROPERTY = ClassAdapter.wrap (ShortProperty.class);
// generated
	public interface ShortProperty
	 	extends Property
	{
		void operator$defAssign (Object node, int[] indices, short value);
// generated
// generated
		void operator$defxorAssign (Object node, int[] indices, short value);
	
		void operator$defandAssign (Object node, int[] indices, short value);
	
		void operator$deforAssign (Object node, int[] indices, short vsalue);
// generated
		void operator$defaddAssign (Object node, int[] indices, short value);
	
		void operator$defsubAssign (Object node, int[] indices, short value);
	
		void operator$defmulAssign (Object node, int[] indices, short value);
// generated
		void operator$defdivAssign (Object node, int[] indices, short value);
	}
// generated
// generated
// generated
	private static final Type<CharProperty> CHAR_PROPERTY = ClassAdapter.wrap (CharProperty.class);
// generated
	public interface CharProperty
	 	extends Property
	{
		void operator$defAssign (Object node, int[] indices, char value);
// generated
// generated
		void operator$defxorAssign (Object node, int[] indices, char value);
	
		void operator$defandAssign (Object node, int[] indices, char value);
	
		void operator$deforAssign (Object node, int[] indices, char vsalue);
// generated
		void operator$defaddAssign (Object node, int[] indices, char value);
	
		void operator$defsubAssign (Object node, int[] indices, char value);
	
		void operator$defmulAssign (Object node, int[] indices, char value);
// generated
		void operator$defdivAssign (Object node, int[] indices, char value);
	}
// generated
// generated
// generated
	private static final Type<IntProperty> INT_PROPERTY = ClassAdapter.wrap (IntProperty.class);
// generated
	public interface IntProperty
	 	extends Property
	{
		void operator$defAssign (Object node, int[] indices, int value);
// generated
// generated
		void operator$defxorAssign (Object node, int[] indices, int value);
	
		void operator$defandAssign (Object node, int[] indices, int value);
	
		void operator$deforAssign (Object node, int[] indices, int vsalue);
// generated
		void operator$defaddAssign (Object node, int[] indices, int value);
	
		void operator$defsubAssign (Object node, int[] indices, int value);
	
		void operator$defmulAssign (Object node, int[] indices, int value);
// generated
		void operator$defdivAssign (Object node, int[] indices, int value);
	}
// generated
// generated
// generated
	private static final Type<LongProperty> LONG_PROPERTY = ClassAdapter.wrap (LongProperty.class);
// generated
	public interface LongProperty
	 	extends Property
	{
		void operator$defAssign (Object node, int[] indices, long value);
// generated
// generated
		void operator$defxorAssign (Object node, int[] indices, long value);
	
		void operator$defandAssign (Object node, int[] indices, long value);
	
		void operator$deforAssign (Object node, int[] indices, long vsalue);
// generated
		void operator$defaddAssign (Object node, int[] indices, long value);
	
		void operator$defsubAssign (Object node, int[] indices, long value);
	
		void operator$defmulAssign (Object node, int[] indices, long value);
// generated
		void operator$defdivAssign (Object node, int[] indices, long value);
	}
// generated
// generated
// generated
	private static final Type<FloatProperty> FLOAT_PROPERTY = ClassAdapter.wrap (FloatProperty.class);
// generated
	public interface FloatProperty
	 	extends Property
	{
		void operator$defAssign (Object node, int[] indices, float value);
// generated
		void operator$defaddAssign (Object node, int[] indices, float value);
	
		void operator$defsubAssign (Object node, int[] indices, float value);
	
		void operator$defmulAssign (Object node, int[] indices, float value);
// generated
		void operator$defdivAssign (Object node, int[] indices, float value);
	}
// generated
// generated
// generated
	private static final Type<DoubleProperty> DOUBLE_PROPERTY = ClassAdapter.wrap (DoubleProperty.class);
// generated
	public interface DoubleProperty
	 	extends Property
	{
		void operator$defAssign (Object node, int[] indices, double value);
// generated
		void operator$defRateAssign (Object node, int[] indices, double value);
// generated
// generated
		void operator$defaddAssign (Object node, int[] indices, double value);
	
		void operator$defsubAssign (Object node, int[] indices, double value);
	
		void operator$defmulAssign (Object node, int[] indices, double value);
// generated
		void operator$defdivAssign (Object node, int[] indices, double value);
	}
// generated
// generated
// generated
	private static final Type<ObjectProperty> OBJECT_PROPERTY = ClassAdapter.wrap (ObjectProperty.class);
// generated
	public interface ObjectProperty
		<T>
			 	extends Property
	{
		void operator$defAssign (Object node, int[] indices, T value);
// generated
// generated
// generated
	}
// generated
//!! *# End of generated code

}
