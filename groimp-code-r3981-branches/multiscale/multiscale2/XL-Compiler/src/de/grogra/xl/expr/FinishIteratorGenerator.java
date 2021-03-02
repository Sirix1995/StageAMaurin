
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

import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Type;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.lang.DisposableIterator;
import de.grogra.xl.vmx.VMXState;

public final class FinishIteratorGenerator extends Expression
	implements Generator, LocalAccess
{
	private Expression iterator, value, statement;
	private Local iter;
	private VMXState.Local vmxIter;

	private static final Type ITERATOR_TYPE = ClassAdapter.wrap (DisposableIterator.class);

	public FinishIteratorGenerator (Type type, Local iter)
	{
		super (type);
		this.iter = iter;
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override				
	protected $type evaluate${pp.Type}Impl (VMXState t)
	{
		evaluateVoidImpl (t);
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
		evaluateVoidImpl (t);
		return false;
	}
// generated
// generated
	@Override				
	protected byte evaluateByteImpl (VMXState t)
	{
		evaluateVoidImpl (t);
		return ((byte) 0);
	}
// generated
// generated
	@Override				
	protected short evaluateShortImpl (VMXState t)
	{
		evaluateVoidImpl (t);
		return ((short) 0);
	}
// generated
// generated
	@Override				
	protected char evaluateCharImpl (VMXState t)
	{
		evaluateVoidImpl (t);
		return ((char) 0);
	}
// generated
// generated
	@Override				
	protected int evaluateIntImpl (VMXState t)
	{
		evaluateVoidImpl (t);
		return ((int) 0);
	}
// generated
// generated
	@Override				
	protected long evaluateLongImpl (VMXState t)
	{
		evaluateVoidImpl (t);
		return ((long) 0);
	}
// generated
// generated
	@Override				
	protected float evaluateFloatImpl (VMXState t)
	{
		evaluateVoidImpl (t);
		return ((float) 0);
	}
// generated
// generated
	@Override				
	protected double evaluateDoubleImpl (VMXState t)
	{
		evaluateVoidImpl (t);
		return ((double) 0);
	}
// generated
// generated
	@Override				
	protected Object evaluateObjectImpl (VMXState t)
	{
		evaluateVoidImpl (t);
		return null;
	}
//!! *# End of generated code

	@Override
	protected void evaluateVoidImpl (VMXState t)
	{
		DisposableIterator i = (DisposableIterator) iterator.evaluateObject (t);
		t.aset (vmxIter, i, null);
		try
		{
			while (i.next ())
			{
				if (value != null)
				{
					value.push (t);
				}
				statement.evaluateAsVoid (t);
			}
		}
		catch (Throwable e)
		{
			i.dispose (e);
			de.grogra.util.Utils.rethrow (e);
		}
		i.dispose (null);
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
		iterator = getObjectExpression (0, ITERATOR_TYPE, checkTypes);
		statement = iterator.getNextExpression ();
		Expression e = statement.getNextExpression ();
		if (e != null)
		{
			value = statement;
			statement = e;
		}
	}

	/*
	I #i = e;
Throwable #t = null;
try {
    while (#i.next()) {
        m T v = #i.value();
        s
    }
}
catch (Throwable #u) {
    #t = #u;
    throw #u;
}
finally {
    #i.finish(#t);
}
*/

	private Label start;
	private Label handler;

	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		assert discard;
		iterator.write (writer, false);
		writer.visitStore (vmxIter, ITERATOR_TYPE);
		
		Label cond = new Label ();
		writer.visitJumpInsn (Opcodes.GOTO, cond);

		handler = new Label ();
		writer.visitLabel (handler);
		writer.visitInsn (Opcodes.DUP);
		writer.visitLoad (vmxIter, ITERATOR_TYPE);
		writer.visitInsn (Opcodes.SWAP);
		writer.visitMethodInsn (DisposableIterator.class, "dispose");
		writer.visitInsn (Opcodes.ATHROW);

		start = new Label ();
		writer.visitLabel (start);
		Label loop = start;
		if (value != null)
		{
			value.write (writer, false);
		}
		statement.write (writer, true);

		writer.visitLabel (cond);
		writer.visitLoad (vmxIter, ITERATOR_TYPE);
		writer.visitMethodInsn (DisposableIterator.class, "next");
		writer.visitJumpInsn (Opcodes.IFNE, loop);
		
		Label end = new Label ();
		writer.visitLabel (end);
		writer.visitLoad (vmxIter, ITERATOR_TYPE);
		writer.visitaconst (null);
		writer.visitMethodInsn (DisposableIterator.class, "dispose");
		writer.visitTryCatchBlock (start, end, handler, null);
	}


	@Override
	public void writeFinally (BytecodeWriter writer, int label, ControlTransfer transfer)
	{
		Label end = new Label ();
		writer.visitLabel (end);
		
		if (end.getOffset () != start.getOffset ())
		{
			writer.visitTryCatchBlock (start, end, handler, null);
		}

		writer.visitLoad (vmxIter, ITERATOR_TYPE);
		writer.visitaconst (null);
		writer.visitMethodInsn (DisposableIterator.class, "dispose");
		super.writeFinally (writer, label, transfer);
		start = new Label ();
		writer.visitLabel (start);
	}

}
