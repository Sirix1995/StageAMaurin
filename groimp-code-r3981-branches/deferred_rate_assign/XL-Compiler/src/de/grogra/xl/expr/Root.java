
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

import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Type;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.query.Graph;
import de.grogra.xl.query.QueryState;
import de.grogra.xl.vmx.VMXState;

public final class Root extends Expression implements LocalAccess
{
	static final Type QUERY = ClassAdapter.wrap (QueryState.class);
	static final Type GRAPH = ClassAdapter.wrap (Graph.class);

	private Local state;
	private VMXState.Local vmxState;

	public Root (Type type, Local state)
	{
		super (type);
		this.state = state;
	}


	public int getLocalCount ()
	{
		return 1;
	}

	
	public int getAccessType (int index)
	{
		return PRE_USE | POST_USE;
	}

	
	public Local getLocal (int index)
	{
		return state;
	}

	
	public void setLocal (int index, Local local)
	{
		state = local;
	}
	

	public void complete (MethodScope scope)
	{
		vmxState = state.createVMXLocal ();
	}


	@Override
	protected Object evaluateObjectImpl (VMXState vmx)
	{
		return ((QueryState) vmx.aget (vmxState, null)).getGraph ().getRoot ();
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		writer.visitLoad (vmxState, state.getType ());
		writer.visitMethodInsn (QUERY, "getGraph");
		writer.visitMethodInsn (GRAPH, "getRoot");
	}

}
