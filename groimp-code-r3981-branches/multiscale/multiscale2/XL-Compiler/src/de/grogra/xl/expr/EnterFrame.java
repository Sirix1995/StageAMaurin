
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

import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.vmx.*;

public final class EnterFrame extends VoidExpression implements LocalAccess
{ 
	private Local vmx;
	private VMXState.Local local;
	

	public EnterFrame (Local vmx)
	{
		this.vmx = vmx;
	}


	public int getLocalCount ()
	{
		return 1;
	}

	
	public int getAccessType (int index)
	{
		return POST_ASSIGNMENT;
	}

	
	public Local getLocal (int index)
	{
		return vmx;
	}

	
	public void setLocal (int index, Local local)
	{
		vmx = local;
	}
	

	public void complete (MethodScope scope)
	{
		local = vmx.createVMXLocal ();
	}


	@Override
	protected void evaluateVoidImpl (VMXState t)
	{
	}
	

	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
		writer.visitMethodInsn (vmx.getType (), "current");
		writer.visitInsn (Opcodes.DUP);
		writer.visitStore (local, vmx.getType ());
		writer.visiticonst (MethodScope.get (this).getMethod ().getFrameSize ());
		writer.visitaconst (null);//AUTH
		writer.visitMethodInsn (vmx.getType (), "enter");
	}

}
