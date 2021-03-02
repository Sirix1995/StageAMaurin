
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

import org.objectweb.asm.Opcodes;

import de.grogra.reflect.Field;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeLoader;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.property.RuntimeModelFactory;
import de.grogra.xl.property.CompiletimeModel.Property;
import de.grogra.xl.vmx.VMXState;

public final class GetProperty extends Variable implements Completable
{
	private Expression instance;
	private Expression indices;

	private int indexCount;

	private Property property;
	private de.grogra.xl.property.RuntimeModel.Property rtProperty;
	private String modelString, propertyString;


	public GetProperty (Property p)
	{
		super (p.getType ());
		this.property = p;
	}


	public Property getProperty ()
	{
		return property;
	}
	
	
	public void setProperty (Property p)
	{
		this.property = p;
		setType (p.getType ());
	}


	private Field propertyField;

	public void complete (MethodScope scope)
	{
		if (getExpressionCount () > 1)
		{
			scope.createLocalForVMX ();
		}
		propertyField = scope.getDeclaredType ().getFieldForProperty (property);
	}


	@Override
	protected void checkSetType (Type type)
	{
	}


	private de.grogra.xl.property.RuntimeModel.Property pushIndices (VMXState t)
	{
		for (Expression e = indices; e != null; e = e.getNextExpression ())
		{
			e.push (t);
		}
		if (rtProperty == null)
		{
			TypeLoader l = getTypeLoader ();
			rtProperty = RuntimeModelFactory.getInstance ()
				.modelForName (modelString, l.getClassLoader ())
				.propertyForName (propertyString, l);
		}
		return rtProperty;
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)
	@Override				
	protected $type evaluate${pp.Type}Impl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		return pushIndices (t).get$pp.Type (o, t.popIntArray (indexCount));
	}
#end
!!*/
//!! #* Start of generated code
// generated
	@Override				
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		return pushIndices (t).getBoolean (o, t.popIntArray (indexCount));
	}
// generated
	@Override				
	protected byte evaluateByteImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		return pushIndices (t).getByte (o, t.popIntArray (indexCount));
	}
// generated
	@Override				
	protected short evaluateShortImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		return pushIndices (t).getShort (o, t.popIntArray (indexCount));
	}
// generated
	@Override				
	protected char evaluateCharImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		return pushIndices (t).getChar (o, t.popIntArray (indexCount));
	}
// generated
	@Override				
	protected int evaluateIntImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		return pushIndices (t).getInt (o, t.popIntArray (indexCount));
	}
// generated
	@Override				
	protected long evaluateLongImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		return pushIndices (t).getLong (o, t.popIntArray (indexCount));
	}
// generated
	@Override				
	protected float evaluateFloatImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		return pushIndices (t).getFloat (o, t.popIntArray (indexCount));
	}
// generated
	@Override				
	protected double evaluateDoubleImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		return pushIndices (t).getDouble (o, t.popIntArray (indexCount));
	}
// generated
	@Override				
	protected Object evaluateObjectImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		return pushIndices (t).getObject (o, t.popIntArray (indexCount));
	}
//!! *# End of generated code

	@Override
	public void link (boolean checkTypes)
	{
		instance = getExpression (0, OBJECT, checkTypes);
		indices = instance.getNextExpression ();
		modelString = property.getModel ().getRuntimeName ();
		propertyString = property.getRuntimeName ();
		indexCount = getExpressionCount () - 1;
	}


	@Override
	public Expression toAssignment (int assignmentType)
	{
		return new PropertyAssignment (this, assignmentType)
			.receiveChildren (this);
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		writer.visitFieldInsn (Opcodes.GETSTATIC, propertyField, null);
		instance.write (writer, false);
		if (indexCount > 0)
		{
			writer.visitVMX ();
			for (Expression e = indices; e != null; e = e.getNextExpression ())
			{
				writer.visitInsn (Opcodes.DUP);
				e.write (writer, false);
				writer.visitMethodInsn (VMX_TYPE, "ipush");
			}
			writer.visiticonst (indexCount);
			writer.visitMethodInsn (VMX_TYPE, "popIntArray");
		}
		else
		{
			writer.visitFieldInsn (Opcodes.GETSTATIC, VMXState.class, "INT_0", "[I");
		}
		writer.visitMethodInsn
			(propertyField.getType (), "get" + Reflection.getTypeSuffix (etype));
		if (discard)
		{
			writer.visitPop (etype);
		}
		else
		{
			writer.visitCheckCast (getType ());
		}
	}

}
