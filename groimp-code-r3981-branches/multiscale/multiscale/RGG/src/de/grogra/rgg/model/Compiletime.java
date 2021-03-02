
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

import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.NURBSCurve;
import de.grogra.imp3d.objects.NURBSSurface;
import de.grogra.math.BSplineCurve;
import de.grogra.math.BSplineSurface;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.rgg.BooleanNode;
import de.grogra.rgg.ByteNode;
import de.grogra.rgg.CharNode;
import de.grogra.rgg.DoubleNode;
import de.grogra.rgg.FloatNode;
import de.grogra.rgg.IntNode;
import de.grogra.rgg.LongNode;
import de.grogra.rgg.ObjectNode;
import de.grogra.rgg.ShortNode;
import de.grogra.xl.impl.base.CompiletimeModel;
import de.grogra.xl.property.CompiletimeModel.Property;
import de.grogra.xl.query.Producer;

public class Compiletime extends CompiletimeModel
{
	static final Type<RGGProducer> PRODUCER = ClassAdapter.wrap (RGGProducer.class);


	public Compiletime (String runtimeConstant)
	{
		super (runtimeConstant);
	}

	
	public Compiletime ()
	{
		this (Runtime.class.getName ());
	}

	
	public Type<Node> getNodeType ()
	{
		return Node.$TYPE;
	}


	public Type<?> getWrapperTypeFor (Type<?> type)
	{
		switch (type.getTypeId ())
		{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
			case TypeId.$pp.TYPE:
				return ${pp.Type}Node.$TYPE;
#end
!!*/
//!! #* Start of generated code
// generated
			case TypeId.BOOLEAN:
				return BooleanNode.$TYPE;
// generated
			case TypeId.BYTE:
				return ByteNode.$TYPE;
// generated
			case TypeId.SHORT:
				return ShortNode.$TYPE;
// generated
			case TypeId.CHAR:
				return CharNode.$TYPE;
// generated
			case TypeId.INT:
				return IntNode.$TYPE;
// generated
			case TypeId.LONG:
				return LongNode.$TYPE;
// generated
			case TypeId.FLOAT:
				return FloatNode.$TYPE;
// generated
			case TypeId.DOUBLE:
				return DoubleNode.$TYPE;
//!! *# End of generated code
			case TypeId.OBJECT:
				return Reflection.isSupertypeOrSame (BSplineCurve.class, type)
					? NURBSCurve.$TYPE
					: Reflection.isSupertypeOrSame (BSplineSurface.class, type)
					? NURBSSurface.$TYPE
					: ObjectNode.$TYPE;
		}
		throw new AssertionError ();
	}

	@Override
	public boolean needsWrapperFor (Type<?> type)
	{
		return Reflection.isSupertypeOrSame (BSplineCurve.class, type)
			|| Reflection.isSupertypeOrSame (BSplineSurface.class, type)
			|| super.needsWrapperFor (type);
	}

	public Type<? extends Producer> getProducerType ()
	{
		return PRODUCER;
	}

	@Override
	public Property getWrapProperty (Type<?> wrapperType)
	{
		return PropertyCompiletime.INSTANCE.getDirectProperty
			(wrapperType,
			 Reflection.equal (NURBSCurve.$TYPE, wrapperType) ? "curve"
			 : Reflection.equal (NURBSSurface.$TYPE, wrapperType) ? "surface" : "value");
	}

}
