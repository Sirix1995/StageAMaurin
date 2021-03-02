
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

import de.grogra.reflect.*;
import de.grogra.xl.vmx.*;
import de.grogra.xl.query.*;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.*;
import de.grogra.xl.compiler.pattern.*;

public final class GetQuery extends EvalExpression implements LocalAccess
{
	
	private final CompoundPattern predicate;
	private final boolean forProduction;
	private Local qsLocal;
	private Local[] locals;
	
	private CompiletimeModel ctModel;
	
	private CompositeData predData;
	private VMXState.Local qsVmxLocal;
	private VMXState.Local[] vmxLocals;
	
	private Query query;


	public GetQuery (PatternBuilder predBuilder, boolean forProduction)
	{
		super (ClassAdapter.wrap (Query.class));
		this.forProduction = forProduction;
		predData = predBuilder.createCompositePattern ();
		predicate = (CompoundPattern) predData.getPattern ();
		add (new ExpressionCompletion (predData));
		locals = predBuilder.getDeclaredVariables ().clone ();
		qsLocal = predBuilder.getQueryState ();
		ctModel = predBuilder.getModel ();
		MethodScope ms = MethodScope.get (predBuilder.getScope ());
		for (int i = 0; i < locals.length; i++)
		{
			locals[i] = ms.makeVMXLocal (locals[i]);
		}
		ms.makeVMXLocal (qsLocal);
	}
	
	
	public CompoundPattern getPattern ()
	{
		return predicate;
	}


	public int getLocalCount ()
	{
		return locals.length + 1;
	}

	
	public int getAccessType (int index)
	{
		return PRE_ASSIGNMENT;
	}

	
	public Local getLocal (int index)
	{
		return (index == locals.length) ? qsLocal : locals[index];
	}

	
	public void setLocal (int index, Local local)
	{
		if (index == locals.length)
		{
			qsLocal = local;
		}
		else
		{
			locals[index] = local;
		}
	}
	
	
	private Field queryField;

	public void complete (MethodScope scope)
	{
		vmxLocals = new VMXState.Local[predicate.getParameterCount ()];
		for (int i = locals.length - 1; i >= 0; i--)
		{
			vmxLocals[predData.getIndexOfVariable (locals[i])]
				= locals[i].createVMXLocal ();
		}
		qsVmxLocal = qsLocal.createVMXLocal ();
		predData = null;
		queryField = scope.getDeclaredType ().getFieldForQuery (ctModel, getQuery ());
		//locals = null;
	}

	
	private synchronized Query getQuery ()
	{
		if (query == null)
		{
			query = new Query (predicate, forProduction, qsVmxLocal, vmxLocals);
		}
		return query;
	}


	@Override
	protected Object evaluateObjectImpl (VMXState vmx)
	{
		return getQuery ();
	}


	@Override
	protected void writeImpl (BytecodeWriter out, boolean discard)
	{
		if (!discard)
		{
			out.visitFieldInsn (Opcodes.GETSTATIC, queryField, null);
		}
	}

}
