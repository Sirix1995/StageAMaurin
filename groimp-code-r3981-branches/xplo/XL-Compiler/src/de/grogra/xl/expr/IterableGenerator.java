
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

import java.util.Iterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Label;

import de.grogra.reflect.*;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.vmx.*;

public final class IterableGenerator extends Expression
	implements Generator, LocalAccess
{
	private Expression iterator, statement;
	private Local iter;
	private VMXState.Local vmxIter;
	
	
	private static final Type ITERATOR_TYPE = ClassAdapter.wrap (Iterator.class);

	
	public IterableGenerator (Type type, Local iter)
	{
		super (type);
		this.iter = iter;
	}


	@Override
	protected Object evaluateObjectImpl (VMXState t)
	{
		for (java.util.Iterator i = (java.util.Iterator) iterator.evaluateObject (t);
			 i.hasNext(); )
		{
			Object o = i.next ();
			if ((o != null) && !getType ().isInstance (o))
			{
				throw new ClassCastException (o + " " + getType ());
			}
			t.apush (o);
			statement.evaluateAsVoid (t);
		}
		return null;
	}


	public int getLocalCount ()
	{
		return 1;
	}

	
	public int getAccessType (int index)
	{
		return PRE_1_ASSIGNMENT | POST_USE;
	}

	
	public Local getLocal (int index)
	{
		return iter;
	}

	
	public void setLocal (int index, Local local)
	{
		iter = local;
	}
	

	public void complete (MethodScope scope)
	{
		vmxIter = iter.createVMXLocal ();
	}


	public int getGeneratorType ()
	{
		return LOCAL;
	}


	public void setBreakTarget (BreakTarget target)
	{
	}


	@Override
	public void link (boolean checkTypes)
	{
		checkExpressionCount (2);
		iterator = getObjectExpression (0, ITERATOR_TYPE, checkTypes);
		statement = iterator.getNextExpression ();
	}

	
	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		assert discard;
		iterator.write (writer, false);
		writer.visitStore (vmxIter, ITERATOR_TYPE);
		
		Label loop = new Label ();
		Label cond = new Label ();
		
		writer.visitJumpInsn (Opcodes.GOTO, cond);

		writer.visitLabel (loop);
		writer.visitLoad (vmxIter, ITERATOR_TYPE);
		writer.visitMethodInsn (java.util.Iterator.class, "next");
		writer.visitCheckCast (getType ());
		statement.write (writer, true);

		writer.visitLabel (cond);
		writer.visitLoad (vmxIter, ITERATOR_TYPE);
		writer.visitMethodInsn (java.util.Iterator.class, "hasNext");
		writer.visitJumpInsn (Opcodes.IFNE, loop);
	}

}
