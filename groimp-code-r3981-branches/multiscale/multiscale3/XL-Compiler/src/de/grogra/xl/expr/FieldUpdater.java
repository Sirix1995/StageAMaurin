
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

package de.grogra.xl.expr;

import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Transaction;
import de.grogra.xl.vmx.VMXState;

public class FieldUpdater extends Node implements ValueObserver
{
	public static final int UPDATES = Node.MIN_UNUSED_SPECIAL_OF_SOURCE;
	public static final int MIN_UNUSED_SPECIAL_OF_SOURCE = UPDATES + 1;


	private PersistenceField field;
	private int[] indices;

	
	private static void initType ()
	{
		$TYPE.declareSpecialEdge (UPDATES, "edge.updates", new Node[0]);
	}

	
	//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new FieldUpdater ());
		initType ();
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new FieldUpdater ();
	}

//enh:end
	
	private FieldUpdater ()
	{
		this (null);
	}


	public FieldUpdater (PersistenceField field)
	{
		this.field = field;
	}


	public void valueChanged (Expression expr, int index, Transaction t)
	{
		if (expr.etype != field.getType ().getTypeId ())
		{
			throw new IllegalArgumentException
				("Expression type differs from field type.");
		}
		Node tg;
		for (Edge e = getFirstEdge (); e != null; e = e.getNext (this))
		{
			if (((tg = e.getTarget ()) != this) && e.testEdgeBits (UPDATES))
			{
				switch (expr.etype)
				{
/*!!
#foreach ($type in $types)
$pp.setType($type)
					case de.grogra.reflect.TypeId.$pp.TYPE:
						field.set$pp.Type (tg, indices,
										   expr.evaluate$pp.Type (VMXState.current ()),
										   t);
						break;
#end
!!*/
//!! #* Start of generated code
// generated
					case de.grogra.reflect.TypeId.BOOLEAN:
						field.setBoolean (tg, indices,
										   expr.evaluateBoolean (VMXState.current ()),
										   t);
						break;
// generated
					case de.grogra.reflect.TypeId.BYTE:
						field.setByte (tg, indices,
										   expr.evaluateByte (VMXState.current ()),
										   t);
						break;
// generated
					case de.grogra.reflect.TypeId.SHORT:
						field.setShort (tg, indices,
										   expr.evaluateShort (VMXState.current ()),
										   t);
						break;
// generated
					case de.grogra.reflect.TypeId.CHAR:
						field.setChar (tg, indices,
										   expr.evaluateChar (VMXState.current ()),
										   t);
						break;
// generated
					case de.grogra.reflect.TypeId.INT:
						field.setInt (tg, indices,
										   expr.evaluateInt (VMXState.current ()),
										   t);
						break;
// generated
					case de.grogra.reflect.TypeId.LONG:
						field.setLong (tg, indices,
										   expr.evaluateLong (VMXState.current ()),
										   t);
						break;
// generated
					case de.grogra.reflect.TypeId.FLOAT:
						field.setFloat (tg, indices,
										   expr.evaluateFloat (VMXState.current ()),
										   t);
						break;
// generated
					case de.grogra.reflect.TypeId.DOUBLE:
						field.setDouble (tg, indices,
										   expr.evaluateDouble (VMXState.current ()),
										   t);
						break;
// generated
					case de.grogra.reflect.TypeId.OBJECT:
						field.setObject (tg, indices,
										   expr.evaluateObject (VMXState.current ()),
										   t);
						break;
//!! *# End of generated code
				}
				return;
			}
		}
	}

}
