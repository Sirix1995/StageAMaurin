
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
import de.grogra.reflect.Type;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.vmx.VMXState;
import de.grogra.xl.query.CompiletimeModel;
import de.grogra.xl.query.RuntimeModel;
import de.grogra.xl.query.RuntimeModelFactory;

public class ModelExpression extends Expression implements Completable
{
	private CompiletimeModel model;
	private String modelString;
	private RuntimeModel rtModel;


	public ModelExpression (Type type)
	{
		super (type);
	}
	
	
	public ModelExpression (CompiletimeModel model)
	{
		super (ClassAdapter.wrap (RuntimeModel.class));
		setModel (model);
	}


	@Override
	public boolean allowsIteration (int index)
	{
		return false;
	}


	protected void setModel (CompiletimeModel model)
	{
		this.model = model;
		modelString = model.getRuntimeName ();
	}


	Field modelField;

	public void complete (MethodScope scope)
	{
		modelField = scope.getDeclaredType ().getFieldForModel (model);
	}


	protected RuntimeModel getRuntimeModel ()
	{
		RuntimeModel m;
		if ((m = rtModel) == null)
		{
			rtModel = m = RuntimeModelFactory.getInstance ()
				.modelForName (modelString, getTypeLoader ().getClassLoader ());
		}
		return m;
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ",model=" + modelString;
	}


	@Override
	protected Object evaluateObjectImpl (VMXState vmx)
	{
		return getRuntimeModel ();
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		if (!discard)
		{
			writer.visitFieldInsn (Opcodes.GETSTATIC, modelField, null);
		}
	}

}
