
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

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.lang.Filter;
import de.grogra.xl.vmx.VMXState;

public class FilterGuard extends Expression implements LocalAccess, Generator
{
	private Expression expr, statement, breakExpr;

	private VMXState.Local vmxLocal;
	private Local local;



	public FilterGuard (Type type, Local local)
	{
		super (type);
		this.local = local;
	}


/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override				
	protected $pp.jtype evaluate${pp.Type}Impl (VMXState t)
	{
		Filter f = (Filter) t.aget (vmxLocal, null);
		expr.evaluateAsVoid (t);
		if (f.accept)
		{
#if ($pp.Object)
			if ((f.aval != null) && !getType ().isInstance (f.aval))
			{
				throw new ClassCastException
					(f.aval.getClass ().getName () + " " + getType ().getName ());
			}
#end
			t.${pp.prefix}push (f.${pp.prefix}val);
			statement.evaluateAsVoid (t);
		}
		if (f.isFinished ())
		{
			breakExpr.evaluateAsVoid (t);
		}
		return $pp.null;
	}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override				
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		Filter f = (Filter) t.aget (vmxLocal, null);
		expr.evaluateAsVoid (t);
		if (f.accept)
		{
			t.ipush (f.ival);
			statement.evaluateAsVoid (t);
		}
		if (f.isFinished ())
		{
			breakExpr.evaluateAsVoid (t);
		}
		return false;
	}
// generated
// generated
	@Override				
	protected byte evaluateByteImpl (VMXState t)
	{
		Filter f = (Filter) t.aget (vmxLocal, null);
		expr.evaluateAsVoid (t);
		if (f.accept)
		{
			t.ipush (f.ival);
			statement.evaluateAsVoid (t);
		}
		if (f.isFinished ())
		{
			breakExpr.evaluateAsVoid (t);
		}
		return ((byte) 0);
	}
// generated
// generated
	@Override				
	protected short evaluateShortImpl (VMXState t)
	{
		Filter f = (Filter) t.aget (vmxLocal, null);
		expr.evaluateAsVoid (t);
		if (f.accept)
		{
			t.ipush (f.ival);
			statement.evaluateAsVoid (t);
		}
		if (f.isFinished ())
		{
			breakExpr.evaluateAsVoid (t);
		}
		return ((short) 0);
	}
// generated
// generated
	@Override				
	protected char evaluateCharImpl (VMXState t)
	{
		Filter f = (Filter) t.aget (vmxLocal, null);
		expr.evaluateAsVoid (t);
		if (f.accept)
		{
			t.ipush (f.ival);
			statement.evaluateAsVoid (t);
		}
		if (f.isFinished ())
		{
			breakExpr.evaluateAsVoid (t);
		}
		return ((char) 0);
	}
// generated
// generated
	@Override				
	protected int evaluateIntImpl (VMXState t)
	{
		Filter f = (Filter) t.aget (vmxLocal, null);
		expr.evaluateAsVoid (t);
		if (f.accept)
		{
			t.ipush (f.ival);
			statement.evaluateAsVoid (t);
		}
		if (f.isFinished ())
		{
			breakExpr.evaluateAsVoid (t);
		}
		return ((int) 0);
	}
// generated
// generated
	@Override				
	protected long evaluateLongImpl (VMXState t)
	{
		Filter f = (Filter) t.aget (vmxLocal, null);
		expr.evaluateAsVoid (t);
		if (f.accept)
		{
			t.lpush (f.lval);
			statement.evaluateAsVoid (t);
		}
		if (f.isFinished ())
		{
			breakExpr.evaluateAsVoid (t);
		}
		return ((long) 0);
	}
// generated
// generated
	@Override				
	protected float evaluateFloatImpl (VMXState t)
	{
		Filter f = (Filter) t.aget (vmxLocal, null);
		expr.evaluateAsVoid (t);
		if (f.accept)
		{
			t.fpush (f.fval);
			statement.evaluateAsVoid (t);
		}
		if (f.isFinished ())
		{
			breakExpr.evaluateAsVoid (t);
		}
		return ((float) 0);
	}
// generated
// generated
	@Override				
	protected double evaluateDoubleImpl (VMXState t)
	{
		Filter f = (Filter) t.aget (vmxLocal, null);
		expr.evaluateAsVoid (t);
		if (f.accept)
		{
			t.dpush (f.dval);
			statement.evaluateAsVoid (t);
		}
		if (f.isFinished ())
		{
			breakExpr.evaluateAsVoid (t);
		}
		return ((double) 0);
	}
// generated
// generated
	@Override				
	protected Object evaluateObjectImpl (VMXState t)
	{
		Filter f = (Filter) t.aget (vmxLocal, null);
		expr.evaluateAsVoid (t);
		if (f.accept)
		{
			if ((f.aval != null) && !getType ().isInstance (f.aval))
			{
				throw new ClassCastException
					(f.aval.getClass ().getName () + " " + getType ().getName ());
			}
			t.apush (f.aval);
			statement.evaluateAsVoid (t);
		}
		if (f.isFinished ())
		{
			breakExpr.evaluateAsVoid (t);
		}
		return null;
	}
//!! *# End of generated code


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
		return local;
	}


	public void setLocal (int index, Local local)
	{
		this.local = local;
	}


	public void complete (MethodScope scope)
	{
		vmxLocal = local.createVMXLocal ();
	}


	public int getGeneratorType ()
	{
		return LOCAL;
	}


	public void setBreakTarget (BreakTarget target)
	{
		add (new Break (target.getLabel ()));
	}

	
	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (3);
		expr = getFirstExpression ();
		statement = expr.getNextExpression ();
		breakExpr = statement.getNextExpression ();
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ',' + local + ',' + vmxLocal;
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		expr.write (writer, true);
		writer.visitLoad (vmxLocal, local.getType ());
		writer.visitInsn (Opcodes.DUP);
		writer.visitFieldInsn (Opcodes.GETFIELD, Filter.class, "accept", "Z");

		Label afterStmt = new Label ();
		writer.visitJumpInsn (Opcodes.IFEQ, afterStmt);

		writer.visitFieldInsn
			(Opcodes.GETFIELD, Filter.class,
			 Reflection.getJVMPrefix (etype) + "val",
			 Reflection.getType (Reflection.getJVMTypeId (getType ())).getDescriptor ());
		writer.visitVM2T (etype);

		statement.write (writer, true);
		writer.visitLoad (vmxLocal, local.getType ());
		
		writer.visitLabel (afterStmt);
		
		writer.visitMethodInsn (Filter.class, "isFinished");
		
		Label afterBreak = new Label ();
		writer.visitJumpInsn (Opcodes.IFEQ, afterBreak);
		
		breakExpr.write (writer, true);
		
		writer.visitLabel (afterBreak);

		if (!discard)
		{
			writer.visitNull (etype);
		}
	}

}
