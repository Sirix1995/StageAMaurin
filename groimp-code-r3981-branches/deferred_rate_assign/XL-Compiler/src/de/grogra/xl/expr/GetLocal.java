
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

import de.grogra.reflect.*;
import de.grogra.xl.vmx.*;
import de.grogra.xl.compiler.scope.*;
import de.grogra.xl.compiler.BytecodeWriter;

public class GetLocal extends Variable implements LocalValue
{
	private VMXState.Local local;
	private Local clocal;


	public GetLocal (VMXState.Local local, Type type)
	{
		super (type);
		this.local = local;
	}


	public GetLocal (Local clocal)
	{
		super (clocal.getType ());
		this.clocal = clocal;
	}


/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override
	protected $pp.jtype evaluate${pp.Type}Impl (VMXState t)
	{
#if ($pp.Object)
		assert (t.aget (local, null) == null) || getType ().isInstance (t.aget (local, null))
			|| (t.aget (local, null) instanceof Type)
			: t.aget (local, null) + " " + getType ();
#end
		return ($pp.jtype) (t.${pp.prefix}get (local, null) $pp.vm2type);
	}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		return (boolean) (t.iget (local, null)  != 0);
	}
// generated
// generated
	@Override
	protected byte evaluateByteImpl (VMXState t)
	{
		return (byte) (t.iget (local, null) );
	}
// generated
// generated
	@Override
	protected short evaluateShortImpl (VMXState t)
	{
		return (short) (t.iget (local, null) );
	}
// generated
// generated
	@Override
	protected char evaluateCharImpl (VMXState t)
	{
		return (char) (t.iget (local, null) );
	}
// generated
// generated
	@Override
	protected int evaluateIntImpl (VMXState t)
	{
		return (int) (t.iget (local, null) );
	}
// generated
// generated
	@Override
	protected long evaluateLongImpl (VMXState t)
	{
		return (long) (t.lget (local, null) );
	}
// generated
// generated
	@Override
	protected float evaluateFloatImpl (VMXState t)
	{
		return (float) (t.fget (local, null) );
	}
// generated
// generated
	@Override
	protected double evaluateDoubleImpl (VMXState t)
	{
		return (double) (t.dget (local, null) );
	}
// generated
// generated
	@Override
	protected Object evaluateObjectImpl (VMXState t)
	{
		assert (t.aget (local, null) == null) || getType ().isInstance (t.aget (local, null))
			|| (t.aget (local, null) instanceof Type)
			: t.aget (local, null) + " " + getType ();
		return (Object) (t.aget (local, null) );
	}
//!! *# End of generated code

	@Override
	public Expression toAssignment (int assignmentType)
	{
		return (clocal != null) ? new AssignLocal (clocal, assignmentType)
			: new AssignLocal (local, getType (), assignmentType);
	}


	public int getLocalCount ()
	{
		return 1;
	}


	public int getAccessType (int index)
	{
		return POST_USE;
	}


	public Local getLocal (int index)
	{
		return clocal;
	}


	public void setLocal (int index, Local local)
	{
		this.clocal = local;
	}


	public void complete (MethodScope scope)
	{
		local = clocal.createVMXLocal ();
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ',' + local + ',' + clocal;
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		if (!discard)
		{
			writer.visitLoad (local, getType ());
		}
	}

}
