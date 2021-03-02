
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
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.MethodScope;

public class AssignLocal extends Assignment implements LocalAccess
{
	private VMXState.Local local;
	private Local clocal;
	private Expression expr;


	public AssignLocal (VMXState.Local local, Type type, int assignmentType)
	{
		super (type, assignmentType);
		this.local = local;
	}


	public AssignLocal (Local clocal, int assignmentType)
	{
		super (clocal.getType (), assignmentType);
		this.clocal = clocal;
	}


/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override
	protected $type evaluate${pp.Type}Impl (VMXState t)
	{
		$type value;
		switch (assignmentType)
		{
			case SIMPLE:
				t.${pp.prefix}set
					(local, (value = expr.evaluate$pp.Type (t))
					 $pp.type2vm, null);
				return value;
			case COMPOUND:
				t.${pp.prefix}push (t.${pp.prefix}get (local, null));
				t.${pp.prefix}set
					(local, (value = expr.evaluate$pp.Type (t))
					 $pp.type2vm, null);
				return value;
			case POSTFIX_COMPOUND:
				t.${pp.prefix}push
					((value = ($type) (t.${pp.prefix}get (local, null)
									   $pp.vm2type))
					 $pp.type2vm);
				t.${pp.prefix}set
					(local, expr.evaluate$pp.Type (t) $pp.type2vm, null);
				return value;
			default:
				throw new AssertionError ();
		}
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override
	protected boolean evaluateBooleanImpl (VMXState t)
	{
		boolean value;
		switch (assignmentType)
		{
			case SIMPLE:
				t.iset
					(local, (value = expr.evaluateBoolean (t))
					  ? 1 : 0, null);
				return value;
			case COMPOUND:
				t.ipush (t.iget (local, null));
				t.iset
					(local, (value = expr.evaluateBoolean (t))
					  ? 1 : 0, null);
				return value;
			case POSTFIX_COMPOUND:
				t.ipush
					((value = (boolean) (t.iget (local, null)
									    != 0))
					  ? 1 : 0);
				t.iset
					(local, expr.evaluateBoolean (t)  ? 1 : 0, null);
				return value;
			default:
				throw new AssertionError ();
		}
	}
// generated
// generated
// generated
	@Override
	protected byte evaluateByteImpl (VMXState t)
	{
		byte value;
		switch (assignmentType)
		{
			case SIMPLE:
				t.iset
					(local, (value = expr.evaluateByte (t))
					 , null);
				return value;
			case COMPOUND:
				t.ipush (t.iget (local, null));
				t.iset
					(local, (value = expr.evaluateByte (t))
					 , null);
				return value;
			case POSTFIX_COMPOUND:
				t.ipush
					((value = (byte) (t.iget (local, null)
									   ))
					 );
				t.iset
					(local, expr.evaluateByte (t) , null);
				return value;
			default:
				throw new AssertionError ();
		}
	}
// generated
// generated
// generated
	@Override
	protected short evaluateShortImpl (VMXState t)
	{
		short value;
		switch (assignmentType)
		{
			case SIMPLE:
				t.iset
					(local, (value = expr.evaluateShort (t))
					 , null);
				return value;
			case COMPOUND:
				t.ipush (t.iget (local, null));
				t.iset
					(local, (value = expr.evaluateShort (t))
					 , null);
				return value;
			case POSTFIX_COMPOUND:
				t.ipush
					((value = (short) (t.iget (local, null)
									   ))
					 );
				t.iset
					(local, expr.evaluateShort (t) , null);
				return value;
			default:
				throw new AssertionError ();
		}
	}
// generated
// generated
// generated
	@Override
	protected char evaluateCharImpl (VMXState t)
	{
		char value;
		switch (assignmentType)
		{
			case SIMPLE:
				t.iset
					(local, (value = expr.evaluateChar (t))
					 , null);
				return value;
			case COMPOUND:
				t.ipush (t.iget (local, null));
				t.iset
					(local, (value = expr.evaluateChar (t))
					 , null);
				return value;
			case POSTFIX_COMPOUND:
				t.ipush
					((value = (char) (t.iget (local, null)
									   ))
					 );
				t.iset
					(local, expr.evaluateChar (t) , null);
				return value;
			default:
				throw new AssertionError ();
		}
	}
// generated
// generated
// generated
	@Override
	protected int evaluateIntImpl (VMXState t)
	{
		int value;
		switch (assignmentType)
		{
			case SIMPLE:
				t.iset
					(local, (value = expr.evaluateInt (t))
					 , null);
				return value;
			case COMPOUND:
				t.ipush (t.iget (local, null));
				t.iset
					(local, (value = expr.evaluateInt (t))
					 , null);
				return value;
			case POSTFIX_COMPOUND:
				t.ipush
					((value = (int) (t.iget (local, null)
									   ))
					 );
				t.iset
					(local, expr.evaluateInt (t) , null);
				return value;
			default:
				throw new AssertionError ();
		}
	}
// generated
// generated
// generated
	@Override
	protected long evaluateLongImpl (VMXState t)
	{
		long value;
		switch (assignmentType)
		{
			case SIMPLE:
				t.lset
					(local, (value = expr.evaluateLong (t))
					 , null);
				return value;
			case COMPOUND:
				t.lpush (t.lget (local, null));
				t.lset
					(local, (value = expr.evaluateLong (t))
					 , null);
				return value;
			case POSTFIX_COMPOUND:
				t.lpush
					((value = (long) (t.lget (local, null)
									   ))
					 );
				t.lset
					(local, expr.evaluateLong (t) , null);
				return value;
			default:
				throw new AssertionError ();
		}
	}
// generated
// generated
// generated
	@Override
	protected float evaluateFloatImpl (VMXState t)
	{
		float value;
		switch (assignmentType)
		{
			case SIMPLE:
				t.fset
					(local, (value = expr.evaluateFloat (t))
					 , null);
				return value;
			case COMPOUND:
				t.fpush (t.fget (local, null));
				t.fset
					(local, (value = expr.evaluateFloat (t))
					 , null);
				return value;
			case POSTFIX_COMPOUND:
				t.fpush
					((value = (float) (t.fget (local, null)
									   ))
					 );
				t.fset
					(local, expr.evaluateFloat (t) , null);
				return value;
			default:
				throw new AssertionError ();
		}
	}
// generated
// generated
// generated
	@Override
	protected double evaluateDoubleImpl (VMXState t)
	{
		double value;
		switch (assignmentType)
		{
			case SIMPLE:
				t.dset
					(local, (value = expr.evaluateDouble (t))
					 , null);
				return value;
			case COMPOUND:
				t.dpush (t.dget (local, null));
				t.dset
					(local, (value = expr.evaluateDouble (t))
					 , null);
				return value;
			case POSTFIX_COMPOUND:
				t.dpush
					((value = (double) (t.dget (local, null)
									   ))
					 );
				t.dset
					(local, expr.evaluateDouble (t) , null);
				return value;
			default:
				throw new AssertionError ();
		}
	}
// generated
// generated
// generated
	@Override
	protected Object evaluateObjectImpl (VMXState t)
	{
		Object value;
		switch (assignmentType)
		{
			case SIMPLE:
				t.aset
					(local, (value = expr.evaluateObject (t))
					 , null);
				return value;
			case COMPOUND:
				t.apush (t.aget (local, null));
				t.aset
					(local, (value = expr.evaluateObject (t))
					 , null);
				return value;
			case POSTFIX_COMPOUND:
				t.apush
					((value = (Object) (t.aget (local, null)
									   ))
					 );
				t.aset
					(local, expr.evaluateObject (t) , null);
				return value;
			default:
				throw new AssertionError ();
		}
	}
// generated
//!! *# End of generated code

	public int getLocalCount ()
	{
		return 1;
	}


	public int getAccessType (int index)
	{
		return (assignmentType == SIMPLE) ? POST_ASSIGNMENT
			: POST_ASSIGNMENT | PRE_USE;
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
	public void link (boolean checkTypes)
	{
		checkExpressionCount (1);
		expr = getExpression (0, etype, checkTypes);
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ',' + local + ',' + clocal;
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		switch (assignmentType)
		{
			case SIMPLE:
				writeChildren (writer);
				if (!discard)
				{
					writer.visitDup (etype);
				}
				writer.visitStore (local, getType ());
				break;
			case POSTFIX_COMPOUND:
				if (!discard)
				{
					writer.visitLoad (local, getType ());
					if (!writeIinc (writer))
					{
						writer.visitDup (etype);
						writeChildren (writer);
						writer.visitStore (local, getType ());
					}
					break;
				}
				// no break
			case COMPOUND:
				if (writeIinc (writer))
				{
					if (!discard)
					{
						writer.visitLoad (local, getType ());
					}
				}
				else
				{
					writer.visitLoad (local, getType ());
					writeChildren (writer);
					if (!discard)
					{
						writer.visitDup (etype);
					}
					writer.visitStore (local, getType ());
				}
				break;
		}
	}

	
	private boolean writeIinc (BytecodeWriter writer)
	{
		if ((etype == INT) && (assignmentType != SIMPLE) && local.isJavaLocal ())
		{
			Expression e = getFirstExpression ();
			if (e instanceof Add)
			{
				e = e.getExpression (1);
				if (e instanceof IntConst)
				{
					int i = ((IntConst) e).value;
					if ((Short.MIN_VALUE <= i) && (i <= Short.MAX_VALUE))
					{
						writer.visitIincInsn (local.getIndex (), i);
						return true;
					}
				}
			}
		}
		return false;
	}

}
