
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
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.*;

public final class Pop extends Expression
{

	public Pop (Type type)
	{
		super (type);
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)
	@Override
	protected $type evaluate${pp.Type}Impl (VMXState t)
	{
		return ($type) (t.${pp.prefix}pop () $pp.vm2type);
	}
#end
!!*/
//!! #* Start of generated code
// generated
	@Override
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		return (boolean) (t.ipop ()  != 0);
	}
// generated
	@Override
	protected byte evaluateByteImpl (VMXState t)
	{
		return (byte) (t.ipop () );
	}
// generated
	@Override
	protected short evaluateShortImpl (VMXState t)
	{
		return (short) (t.ipop () );
	}
// generated
	@Override
	protected char evaluateCharImpl (VMXState t)
	{
		return (char) (t.ipop () );
	}
// generated
	@Override
	protected int evaluateIntImpl (VMXState t)
	{
		return (int) (t.ipop () );
	}
// generated
	@Override
	protected long evaluateLongImpl (VMXState t)
	{
		return (long) (t.lpop () );
	}
// generated
	@Override
	protected float evaluateFloatImpl (VMXState t)
	{
		return (float) (t.fpop () );
	}
// generated
	@Override
	protected double evaluateDoubleImpl (VMXState t)
	{
		return (double) (t.dpop () );
	}
// generated
	@Override
	protected Object evaluateObjectImpl (VMXState t)
	{
		return (Object) (t.apop () );
	}
//!! *# End of generated code

	@Override
	protected void evaluateVoidImpl (VMXState t)
	{
	}


	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
	}

}
