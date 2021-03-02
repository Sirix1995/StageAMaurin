
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

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Edge;
import de.grogra.persistence.Observer;
import de.grogra.persistence.PersistenceCapable;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Transaction;

public class FieldObserver extends Expression implements Observer
{
	private PersistenceField field;
	private int[] indices;


	public FieldObserver (PersistenceField field)
	{
		super (field.getType ());
		this.field = field;
	}


	public void fieldModified (PersistenceCapable object, PersistenceField field,
							   int[] indices, Transaction t)
	{
		if (field.overlaps (indices, this.field, this.indices))
		{
			update (object, t);
		}
	}


	protected void edgeSetModified (Edge set, int old, Transaction t)
	{
		super.edgeChanged (set, old, t);
		if ((((set.getEdgeBits () ^ old) & Graph.NOTIFIES_EDGE) != 0)
			&& (set.getTarget () == this) && !Transaction.isApplying (t))
		{
			update ((old & Graph.NOTIFIES_EDGE) == 0 ? set.getSource () : null, t);
		}
	}


	private void update (PersistenceCapable object, Transaction t)
	{
		if (object != null)
		{
			switch (etype)
			{
/*!!
#foreach ($type in $types)
$pp.setType($type)

#if ($pp.fnumeric)
	#set ($field = "dval$FIELD")
	#set ($t = "Double")
#elseif ($pp.prefix != "a")
	#set ($field = "lval$FIELD")
	#set ($t = "Long")
#else
	#set ($field = "aval$FIELD")
	#set ($t = "Object")
#end
				case $pp.TYPE:
					${field}.set$t
						(this, null, field.get$pp.Type (object, indices) $pp.type2vm, t);
					break;
#end
!!*/
//!! #* Start of generated code
// generated
// generated
				case BOOLEAN:
					lval$FIELD.setLong
						(this, null, field.getBoolean (object, indices)  ? 1 : 0, t);
					break;
// generated
// generated
				case BYTE:
					lval$FIELD.setLong
						(this, null, field.getByte (object, indices) , t);
					break;
// generated
// generated
				case SHORT:
					lval$FIELD.setLong
						(this, null, field.getShort (object, indices) , t);
					break;
// generated
// generated
				case CHAR:
					lval$FIELD.setLong
						(this, null, field.getChar (object, indices) , t);
					break;
// generated
// generated
				case INT:
					lval$FIELD.setLong
						(this, null, field.getInt (object, indices) , t);
					break;
// generated
// generated
				case LONG:
					lval$FIELD.setLong
						(this, null, field.getLong (object, indices) , t);
					break;
// generated
// generated
				case FLOAT:
					dval$FIELD.setDouble
						(this, null, field.getFloat (object, indices) , t);
					break;
// generated
// generated
				case DOUBLE:
					dval$FIELD.setDouble
						(this, null, field.getDouble (object, indices) , t);
					break;
// generated
// generated
				case OBJECT:
					aval$FIELD.setObject
						(this, null, field.getObject (object, indices) , t);
					break;
//!! *# End of generated code
			}
		}
		if ((object != null) != ((bits & VALID) != 0))
		{
			valid$FIELD.setBoolean (this, null, object != null, t);
		}
		fireValueChanged (t);
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)
	@Override				
	protected $type evaluate${pp.Type}Impl (de.grogra.xl.vmx.VMXState t)
	{
		throw new UndefinedInputException ("Input node of " + this
										   + " is not defined.");
	}
#end
!!*/
//!! #* Start of generated code
// generated
	@Override				
	protected boolean evaluateBooleanImpl (de.grogra.xl.vmx.VMXState t)
	{
		throw new UndefinedInputException ("Input node of " + this
										   + " is not defined.");
	}
// generated
	@Override				
	protected byte evaluateByteImpl (de.grogra.xl.vmx.VMXState t)
	{
		throw new UndefinedInputException ("Input node of " + this
										   + " is not defined.");
	}
// generated
	@Override				
	protected short evaluateShortImpl (de.grogra.xl.vmx.VMXState t)
	{
		throw new UndefinedInputException ("Input node of " + this
										   + " is not defined.");
	}
// generated
	@Override				
	protected char evaluateCharImpl (de.grogra.xl.vmx.VMXState t)
	{
		throw new UndefinedInputException ("Input node of " + this
										   + " is not defined.");
	}
// generated
	@Override				
	protected int evaluateIntImpl (de.grogra.xl.vmx.VMXState t)
	{
		throw new UndefinedInputException ("Input node of " + this
										   + " is not defined.");
	}
// generated
	@Override				
	protected long evaluateLongImpl (de.grogra.xl.vmx.VMXState t)
	{
		throw new UndefinedInputException ("Input node of " + this
										   + " is not defined.");
	}
// generated
	@Override				
	protected float evaluateFloatImpl (de.grogra.xl.vmx.VMXState t)
	{
		throw new UndefinedInputException ("Input node of " + this
										   + " is not defined.");
	}
// generated
	@Override				
	protected double evaluateDoubleImpl (de.grogra.xl.vmx.VMXState t)
	{
		throw new UndefinedInputException ("Input node of " + this
										   + " is not defined.");
	}
// generated
	@Override				
	protected Object evaluateObjectImpl (de.grogra.xl.vmx.VMXState t)
	{
		throw new UndefinedInputException ("Input node of " + this
										   + " is not defined.");
	}
//!! *# End of generated code
}
