
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

import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Field;
import de.grogra.reflect.TypeLoader;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.CClass;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.vmx.VMXState;
import de.grogra.xl.property.RuntimeModelFactory;
import de.grogra.xl.property.CompiletimeModel.Property;

public final class GetPropertyInstance extends EvalExpression implements Completable
{
	private Property property;
	private de.grogra.xl.property.RuntimeModel.Property rtProperty;
	private String modelString, propertyString;


	public GetPropertyInstance (Property p)
	{
		super (p.getRuntimeType ());
		this.property = p;
	}


	public Property getProperty ()
	{
		return property;
	}
	

	private Field propertyField;

	public void complete (MethodScope scope)
	{
		propertyField = scope.getDeclaredType ().getFieldForProperty (property);
	}


	public void complete (CClass cls)
	{
		propertyField = cls.getFieldForProperty (property);
	}

	@Override				
	protected Object evaluateObjectImpl (VMXState t)
	{
		if (rtProperty == null)
		{
			TypeLoader l = getTypeLoader ();
			rtProperty = RuntimeModelFactory.getInstance ()
				.modelForName (modelString, l.getClassLoader ())
				.propertyForName (propertyString, l);
		}
		return rtProperty;
	}

	public void link (boolean checkTypes)
	{
		modelString = property.getModel ().getRuntimeName ();
		propertyString = property.getRuntimeName ();
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		if (!discard)
		{
			writer.visitFieldInsn (Opcodes.GETSTATIC, propertyField, null);
		}
	}

}
