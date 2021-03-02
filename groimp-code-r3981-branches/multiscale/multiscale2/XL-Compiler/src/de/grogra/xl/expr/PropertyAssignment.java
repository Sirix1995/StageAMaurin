
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
import de.grogra.reflect.TypeLoader;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.property.RuntimeModelFactory;
import de.grogra.xl.property.RuntimeModel.Property;
import de.grogra.xl.vmx.VMXState;

public final class PropertyAssignment extends Assignment implements Completable
{
	private Expression instance;
	private Expression indices;
	private Expression expr;
	private int indexCount;

	private de.grogra.xl.property.CompiletimeModel.Property property;
	private Property rtProperty;
	private String modelString, propertyString;


	public PropertyAssignment (GetProperty field, int assignmentType)
	{
		super (field.getType (), assignmentType);
		property = field.getProperty (); 
		modelString = property.getModel ().getRuntimeName ();
		propertyString = property.getRuntimeName ();
	}


	private Field propertyField;

	public void complete (MethodScope scope)
	{
		if (getExpressionCount () > 2)
		{
			scope.createLocalForVMX ();
		}
		propertyField = scope.getDeclaredType ().getFieldForProperty (property);
	}


	private Property pushIndices (VMXState t)
	{
		for (Expression e = indices; e != expr; e = e.getNextExpression ())
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
		$type value;
		Property fs = pushIndices (t);
		if (assignmentType == SIMPLE)
		{
			value = expr.evaluate$pp.Type (t);
			fs.set$pp.Type (o, t.popIntArray (indexCount), value);
		}
		else
		{
			t.${pp.prefix}push ((value = fs.get$pp.Type (o, t.peekIntArray (indexCount))) $pp.type2vm);
			$type v = expr.evaluate$pp.Type (t);
			fs.set$pp.Type (o, t.popIntArray (indexCount), v);
			if (assignmentType == COMPOUND)
			{
				value = v;
			}
		}
		return value;
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override				
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		boolean value;
		Property fs = pushIndices (t);
		if (assignmentType == SIMPLE)
		{
			value = expr.evaluateBoolean (t);
			fs.setBoolean (o, t.popIntArray (indexCount), value);
		}
		else
		{
			t.ipush ((value = fs.getBoolean (o, t.peekIntArray (indexCount)))  ? 1 : 0);
			boolean v = expr.evaluateBoolean (t);
			fs.setBoolean (o, t.popIntArray (indexCount), v);
			if (assignmentType == COMPOUND)
			{
				value = v;
			}
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected byte evaluateByteImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		byte value;
		Property fs = pushIndices (t);
		if (assignmentType == SIMPLE)
		{
			value = expr.evaluateByte (t);
			fs.setByte (o, t.popIntArray (indexCount), value);
		}
		else
		{
			t.ipush ((value = fs.getByte (o, t.peekIntArray (indexCount))) );
			byte v = expr.evaluateByte (t);
			fs.setByte (o, t.popIntArray (indexCount), v);
			if (assignmentType == COMPOUND)
			{
				value = v;
			}
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected short evaluateShortImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		short value;
		Property fs = pushIndices (t);
		if (assignmentType == SIMPLE)
		{
			value = expr.evaluateShort (t);
			fs.setShort (o, t.popIntArray (indexCount), value);
		}
		else
		{
			t.ipush ((value = fs.getShort (o, t.peekIntArray (indexCount))) );
			short v = expr.evaluateShort (t);
			fs.setShort (o, t.popIntArray (indexCount), v);
			if (assignmentType == COMPOUND)
			{
				value = v;
			}
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected char evaluateCharImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		char value;
		Property fs = pushIndices (t);
		if (assignmentType == SIMPLE)
		{
			value = expr.evaluateChar (t);
			fs.setChar (o, t.popIntArray (indexCount), value);
		}
		else
		{
			t.ipush ((value = fs.getChar (o, t.peekIntArray (indexCount))) );
			char v = expr.evaluateChar (t);
			fs.setChar (o, t.popIntArray (indexCount), v);
			if (assignmentType == COMPOUND)
			{
				value = v;
			}
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected int evaluateIntImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		int value;
		Property fs = pushIndices (t);
		if (assignmentType == SIMPLE)
		{
			value = expr.evaluateInt (t);
			fs.setInt (o, t.popIntArray (indexCount), value);
		}
		else
		{
			t.ipush ((value = fs.getInt (o, t.peekIntArray (indexCount))) );
			int v = expr.evaluateInt (t);
			fs.setInt (o, t.popIntArray (indexCount), v);
			if (assignmentType == COMPOUND)
			{
				value = v;
			}
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected long evaluateLongImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		long value;
		Property fs = pushIndices (t);
		if (assignmentType == SIMPLE)
		{
			value = expr.evaluateLong (t);
			fs.setLong (o, t.popIntArray (indexCount), value);
		}
		else
		{
			t.lpush ((value = fs.getLong (o, t.peekIntArray (indexCount))) );
			long v = expr.evaluateLong (t);
			fs.setLong (o, t.popIntArray (indexCount), v);
			if (assignmentType == COMPOUND)
			{
				value = v;
			}
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected float evaluateFloatImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		float value;
		Property fs = pushIndices (t);
		if (assignmentType == SIMPLE)
		{
			value = expr.evaluateFloat (t);
			fs.setFloat (o, t.popIntArray (indexCount), value);
		}
		else
		{
			t.fpush ((value = fs.getFloat (o, t.peekIntArray (indexCount))) );
			float v = expr.evaluateFloat (t);
			fs.setFloat (o, t.popIntArray (indexCount), v);
			if (assignmentType == COMPOUND)
			{
				value = v;
			}
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected double evaluateDoubleImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		double value;
		Property fs = pushIndices (t);
		if (assignmentType == SIMPLE)
		{
			value = expr.evaluateDouble (t);
			fs.setDouble (o, t.popIntArray (indexCount), value);
		}
		else
		{
			t.dpush ((value = fs.getDouble (o, t.peekIntArray (indexCount))) );
			double v = expr.evaluateDouble (t);
			fs.setDouble (o, t.popIntArray (indexCount), v);
			if (assignmentType == COMPOUND)
			{
				value = v;
			}
		}
		return value;
	}
// generated
// generated
// generated
	@Override				
	protected Object evaluateObjectImpl (VMXState t)
	{
		Object o = instance.evaluateObject (t);
		Object value;
		Property fs = pushIndices (t);
		if (assignmentType == SIMPLE)
		{
			value = expr.evaluateObject (t);
			fs.setObject (o, t.popIntArray (indexCount), value);
		}
		else
		{
			t.apush ((value = fs.getObject (o, t.peekIntArray (indexCount))) );
			Object v = expr.evaluateObject (t);
			fs.setObject (o, t.popIntArray (indexCount), v);
			if (assignmentType == COMPOUND)
			{
				value = v;
			}
		}
		return value;
	}
// generated
//!! *# End of generated code

	@Override
	public void link (boolean checkTypes)
	{
		instance = getExpression (0, OBJECT, checkTypes);
		indices = instance.getNextExpression ();
		Expression e = indices.getNextExpression ();
		indexCount = 0;
		if (e == null)
		{
			expr = indices;
		}
		else
		{
			while (e != null)
			{
				indexCount++;
				expr = e;
				e = e.getNextExpression ();
			}
		}
	}

	private void writeIndices (BytecodeWriter writer, boolean pop)
	{
		if (indexCount > 0)
		{
			writer.visitVMX ();
			writer.visiticonst (indexCount);
			writer.visitMethodInsn (VMX_TYPE, pop ? "popIntArray" : "peekIntArray");
		}
		else
		{
			writer.visitFieldInsn (Opcodes.GETSTATIC, VMXState.class, "INT_0", "[I");
		}
	}

	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		writer.visitFieldInsn (Opcodes.GETSTATIC, propertyField, null);
		instance.write (writer, false);
		for (Expression e = indices; e != expr; e = e.getNextExpression ())
		{
			writer.visitVMX ();
			e.write (writer, false);
			writer.visitMethodInsn (VMX_TYPE, "ipush");
		}
		switch (assignmentType)
		{
			case SIMPLE:
				expr.write (writer, false);
				if (!discard)
				{
					writer.visitDupX2 (etype);
				}
				writeIndices (writer, true);
				writer.visitSwap (etype, OBJECT);
				writer.visitMethodInsn (propertyField.getType (),
					 					"set" + Reflection.getTypeSuffix (etype));
				break;
			case POSTFIX_COMPOUND:
				if (!discard)
				{
					writer.visitInsn (Opcodes.DUP2);
					writeIndices (writer, false);
					writer.visitMethodInsn
						(propertyField.getType (), "get" + Reflection.getTypeSuffix (etype));
					writer.visitCheckCast (getType ());
					writer.visitDupX2 (etype);
					expr.write (writer, false);
					writeIndices (writer, true);
					writer.visitSwap (etype, OBJECT);
					writer.visitMethodInsn
						(propertyField.getType (), "set" + Reflection.getTypeSuffix (etype));
					break;
				}
				// no break
			case COMPOUND:
				writer.visitInsn (Opcodes.DUP2);
				writeIndices (writer, false);
				writer.visitMethodInsn
					(propertyField.getType (), "get" + Reflection.getTypeSuffix (etype));
				writer.visitCheckCast (getType ());
				expr.write (writer, false);
				if (!discard)
				{
					writer.visitDupX2 (etype);
				}
				writeIndices (writer, true);
				writer.visitSwap (etype, OBJECT);
				writer.visitMethodInsn
					(propertyField.getType (), "set" + Reflection.getTypeSuffix (etype));
				break;
		}
	}

}
