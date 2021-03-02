
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

import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.vmx.*;
import de.grogra.reflect.*;

public final class Catch extends VoidExpression implements LocalAccess
{ 
	private Local exLocal;
	private VMXState.Local exception;
	final Type catchType;
	

	public Catch (Local exLocal)
	{
		this.exLocal = exLocal;
		this.catchType = exLocal.getType ();
	}

	
	@Override
	public boolean discards (int index)
	{
		return true;
	}


	public int getLocalCount ()
	{
		return 1;
	}

	
	public int getAccessType (int index)
	{
		return PRE_ASSIGNMENT;
	}

	
	public Local getLocal (int index)
	{
		return exLocal;
	}

	
	public void setLocal (int index, Local local)
	{
		exLocal = local;
	}
	

	public void complete (MethodScope scope)
	{
		exception = exLocal.createVMXLocal ();
	}


	@Override
	protected void evaluateVoidImpl (VMXState t)
	{
		t.aset (exception, t.apop (), null);
		getFirstExpression ().evaluateAsVoid (t);
	}
	

	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		writer.visitStore (exception, catchType);
		writeChildren (writer);
	}

}
