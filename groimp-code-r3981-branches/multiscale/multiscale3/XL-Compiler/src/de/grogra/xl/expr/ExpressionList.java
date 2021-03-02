
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

public final class ExpressionList extends EvalExpression
{
	private Expression[] exprs;
	private final boolean returnFirst;


	public ExpressionList ()
	{
		super ();
		returnFirst = false;
	}


	public ExpressionList (Type type)
	{
		super (type);
		returnFirst = false;
	}


	public ExpressionList (Type type, boolean returnFirst)
	{
		super (type);
		this.returnFirst = returnFirst;
	}


	@Override
	public boolean isRequired (int index)
	{
		return index == (returnFirst ? 0 : getExpressionCount () - 1); 
	}

/*!!
#foreach ($type in $types_void)
$pp.setType($type)

	@Override				
	protected $type evaluate${pp.Type}Impl (VMXState t)
	{
		int ri = returnFirst ? 0 : exprs.length - 1;
		for (int i = 0; i < ri; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
#if ($pp.void)
		exprs[ri].evaluateAsVoid (t);
#else
		$type v = exprs[ri].evaluate$pp.Type (t);
#end
		for (int i = ri + 1; i < exprs.length; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
#if (!$pp.void)
		return v;
#end
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override				
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		int ri = returnFirst ? 0 : exprs.length - 1;
		for (int i = 0; i < ri; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		boolean v = exprs[ri].evaluateBoolean (t);
		for (int i = ri + 1; i < exprs.length; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		return v;
	}
// generated
// generated
// generated
	@Override				
	protected byte evaluateByteImpl (VMXState t)
	{
		int ri = returnFirst ? 0 : exprs.length - 1;
		for (int i = 0; i < ri; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		byte v = exprs[ri].evaluateByte (t);
		for (int i = ri + 1; i < exprs.length; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		return v;
	}
// generated
// generated
// generated
	@Override				
	protected short evaluateShortImpl (VMXState t)
	{
		int ri = returnFirst ? 0 : exprs.length - 1;
		for (int i = 0; i < ri; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		short v = exprs[ri].evaluateShort (t);
		for (int i = ri + 1; i < exprs.length; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		return v;
	}
// generated
// generated
// generated
	@Override				
	protected char evaluateCharImpl (VMXState t)
	{
		int ri = returnFirst ? 0 : exprs.length - 1;
		for (int i = 0; i < ri; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		char v = exprs[ri].evaluateChar (t);
		for (int i = ri + 1; i < exprs.length; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		return v;
	}
// generated
// generated
// generated
	@Override				
	protected int evaluateIntImpl (VMXState t)
	{
		int ri = returnFirst ? 0 : exprs.length - 1;
		for (int i = 0; i < ri; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		int v = exprs[ri].evaluateInt (t);
		for (int i = ri + 1; i < exprs.length; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		return v;
	}
// generated
// generated
// generated
	@Override				
	protected long evaluateLongImpl (VMXState t)
	{
		int ri = returnFirst ? 0 : exprs.length - 1;
		for (int i = 0; i < ri; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		long v = exprs[ri].evaluateLong (t);
		for (int i = ri + 1; i < exprs.length; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		return v;
	}
// generated
// generated
// generated
	@Override				
	protected float evaluateFloatImpl (VMXState t)
	{
		int ri = returnFirst ? 0 : exprs.length - 1;
		for (int i = 0; i < ri; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		float v = exprs[ri].evaluateFloat (t);
		for (int i = ri + 1; i < exprs.length; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		return v;
	}
// generated
// generated
// generated
	@Override				
	protected double evaluateDoubleImpl (VMXState t)
	{
		int ri = returnFirst ? 0 : exprs.length - 1;
		for (int i = 0; i < ri; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		double v = exprs[ri].evaluateDouble (t);
		for (int i = ri + 1; i < exprs.length; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		return v;
	}
// generated
// generated
// generated
	@Override				
	protected Object evaluateObjectImpl (VMXState t)
	{
		int ri = returnFirst ? 0 : exprs.length - 1;
		for (int i = 0; i < ri; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		Object v = exprs[ri].evaluateObject (t);
		for (int i = ri + 1; i < exprs.length; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		return v;
	}
// generated
// generated
// generated
	@Override				
	protected void evaluateVoidImpl (VMXState t)
	{
		int ri = returnFirst ? 0 : exprs.length - 1;
		for (int i = 0; i < ri; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
		exprs[ri].evaluateAsVoid (t);
		for (int i = ri + 1; i < exprs.length; i++)
		{
			exprs[i].evaluateAsVoid (t);
		}
	}
// generated
//!! *# End of generated code

	@Override
	public void link (boolean checkTypes)
	{
		exprs = new Expression[getExpressionCount ()];
		for (int i = 0; i < exprs.length; i++)
		{
			exprs[i] = getExpression (i);
		}
	}


	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
	}

	
	@Override
	protected String paramString ()
	{
		return super.paramString () + ",returnFirst=" + returnFirst;
	}
}
