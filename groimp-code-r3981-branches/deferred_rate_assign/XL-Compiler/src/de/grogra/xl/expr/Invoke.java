
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

import de.grogra.reflect.*;
import de.grogra.util.Int2ObjectMap;
import de.grogra.xl.compiler.*;
import de.grogra.xl.compiler.scope.*;
import de.grogra.xl.vmx.*;

public abstract class Invoke extends EvalExpression
	implements NonlocalGenerator, Completable
{
	protected final Type targetType;
	protected String descriptor;
	final int size;

	private final boolean isStatic;
	private boolean generator;
	private final Method method;
	private AccessMethod accessMethod;

	private XMethod invokeMethod;


	public Invoke (Method method)
	{
		super ("<init>".equals (method.getSimpleName ()) ? method.getDeclaringType ()
			   : method.getReturnType ());
		targetType = method.getDeclaringType ();
		this.method = method;
		isStatic = Reflection.isStatic (method);
		size = Reflection.getJVMStackSize (Reflection.getParameterTypes (method))
			+ (((method.getModifiers () & Member.STATIC) == 0) ? 1 : 0);
	}

	
	public Method getOriginalMethod ()
	{
		return method;
	}

	
	public void complete (MethodScope scope)
	{
		descriptor = method.getDescriptor ();
	}

	
	protected abstract Method getMethod (VMXState t);


	protected void pushParameters (VMXState t)
	{
		for (Expression e = getFirstExpression (); e != null;
			 e = e.getNextExpression ()) 
		{
			e.push (t);	    
		}
	}


	@Override
	public AbruptCompletion.Return evaluateRet (VMXState t)
	{
		pushParameters (t);
		Method m = getMethod (t);
		if (m instanceof MethodExpression)
		{
			((MethodExpression) m).linkGraph (true);
		}
		if (generator)
		{
			try
			{
				if (m instanceof MethodExpression)
				{
					return t.invoke ((MethodExpression) m, -1, null);
				}
				else
				{
					return t.invoke (m);
				}
			}
			catch (AbruptCompletion.Nonlocal e)
			{
				throw e.getReason (null);
			}
		}
		else
		{
			if (m instanceof MethodExpression)
			{
				return t.invoke ((MethodExpression) m, -1, null);
			}
			else
			{
				return t.invoke (m);
			}
		}
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	@Override				
	protected final $pp.jtype evaluate${pp.Type}Impl (VMXState t)
	{
#if ($pp.void)
		evaluateRet (t).vget ();
#else
		return ($pp.jtype) (evaluateRet (t).${pp.prefix}get () $pp.vm2type);
#end
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	@Override				
	protected final boolean evaluateBooleanImpl (VMXState t)
	{
		return (boolean) (evaluateRet (t).iget ()  != 0);
	}
// generated
// generated
// generated
	@Override				
	protected final byte evaluateByteImpl (VMXState t)
	{
		return (byte) (evaluateRet (t).iget () );
	}
// generated
// generated
// generated
	@Override				
	protected final short evaluateShortImpl (VMXState t)
	{
		return (short) (evaluateRet (t).iget () );
	}
// generated
// generated
// generated
	@Override				
	protected final char evaluateCharImpl (VMXState t)
	{
		return (char) (evaluateRet (t).iget () );
	}
// generated
// generated
// generated
	@Override				
	protected final int evaluateIntImpl (VMXState t)
	{
		return (int) (evaluateRet (t).iget () );
	}
// generated
// generated
// generated
	@Override				
	protected final long evaluateLongImpl (VMXState t)
	{
		return (long) (evaluateRet (t).lget () );
	}
// generated
// generated
// generated
	@Override				
	protected final float evaluateFloatImpl (VMXState t)
	{
		return (float) (evaluateRet (t).fget () );
	}
// generated
// generated
// generated
	@Override				
	protected final double evaluateDoubleImpl (VMXState t)
	{
		return (double) (evaluateRet (t).dget () );
	}
// generated
// generated
// generated
	@Override				
	protected final Object evaluateObjectImpl (VMXState t)
	{
		return (Object) (evaluateRet (t).aget () );
	}
// generated
//!! *# End of generated code

	@Override
	protected final void evaluateVoidImpl (VMXState t)
	{
		evaluateRet (t);
	}


	private Int2ObjectMap transfers;

	public void setGenerator ()
	{
		generator = true;
		transfers = new Int2ObjectMap ();
		transfers.put (AbruptCompletion.Throw.LABEL, null);
		setType (XMethod.getGeneratorType (method));
	}

	protected void checkSetType (Type type)
	{
		if (!generator)
		{
			super.checkSetType (type);
		}
	}

	public int getGeneratorType ()
	{
		return generator ? NONLOCAL : NONE;
	}

	
	public void receiveRoutine (TypeScope scope, Expression descriptor)
	{
		CClass c = scope.getDeclaredType ().getCallbackClass ();
		int s = Reflection.isStatic (method) ? 0 : 1;
		final Type[] t = new Type[method.getParameterCount () + s];
		for (int i = method.getParameterCount () - 1; i >= 0; i--)
		{
			t[i + s] = method.getParameterType (i);
		}
		if (s > 0)
		{
			t[0] = Reflection.getBinaryType (getExpression (0).getType ());
		}
		invokeMethod = new XMethod
			(Reflection.getSyntheticName (Reflection.getDeclaredMethods (c),
										  "invoke$" + method.getSimpleName ()),
			 Member.STATIC | Member.FINAL | Member.SYNTHETIC, c,
			 t, ABRUPT_COMPLETION_TYPE, new Expression ()
		{
			@Override
			protected void writeImpl (BytecodeWriter writer, boolean discard)
			{
				int a = 0;
				for (int i = 0; i < t.length; i++)
				{
					writer.visitLoad (a, t[i].getTypeId ());
					a += Reflection.getJVMStackSize (t[i]);
				}
				Label start = new Label ();
				writer.visitLabel (start);
				writeInvocation (writer, false);
				writer.visitPop (method.getReturnType ().getTypeId ());
				Label end = new Label ();
				writer.visitLabel (end);
				writer.visitInsn (Opcodes.ACONST_NULL);
				writer.visitInsn (Opcodes.ARETURN);

				Label handler = new Label ();
				writer.visitLabel (handler);

				writer.visitTryCatchBlock
					(start, end, handler, writer.toName (NONLOCAL_TYPE));

				writer.visitaconst (null);//AUTH
				writer.visitMethodInsn (NONLOCAL_TYPE, "getReason");
				writer.visitInsn (Opcodes.ARETURN);
			}
		});
		insertBranchNode (isStatic ? 0 : 1, descriptor);
	}


	public void addTransfer (ControlTransfer transfer)
	{
		if (transfer instanceof Return)
		{
			transfers.put (AbruptCompletion.Return.LABEL, transfer);
		}
		else
		{
			transfers.put (((Break) transfer).getLabel (), transfer);
		}
	}


	public void receiveConsumer (Expression consumer)
	{
		assert generator;
		setType (method.getReturnType ());
		generator = false;
		insertBranchNode (isStatic ? 0 : 1, consumer);
	}
	

	public void setBreakTarget (BreakTarget target)
	{
	}


	@Override
	protected String paramString ()
	{
		return super.paramString () + ',' + method.getDescriptor () + ','
			+ method.getDeclaringType ().getBinaryName ();
	}

	
	public void useAccessMethod (CClass amc)
	{
		accessMethod = amc.getAccessMethodFor (method, false);
	}

	
	protected abstract int getOpcode ();
	
	
	void writeInvocation (BytecodeWriter writer, boolean discard)
	{
		if (accessMethod != null)
		{
			for (int i = accessMethod.getAdditionalArgumentCount (); i > 0; i--)
			{
				writer.visitInsn (Opcodes.ACONST_NULL);
			}
			String n = accessMethod.getName ();
			writer.visitMethodInsn
				("<init>".equals (n) ? Opcodes.INVOKESPECIAL : Opcodes.INVOKESTATIC,
				 writer.toName (accessMethod.getDeclaringClass ()), n,
				 accessMethod.getDescriptor ());
		}
		else
		{
			writer.visitMethodInsn (getOpcode (), method);
		}
		if (discard)
		{
			writer.visitPop (etype);
		}
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		boolean iface = getOpcode () == Opcodes.INVOKEINTERFACE;
		for (Expression e = getFirstExpression (); e != null; e = e.getNextExpression ())
		{
			e.write (writer, false);
			if (iface)
			{
				iface = false;
				if (e.getType () instanceof IntersectionType)
				{
					writer.visitCheckCast (method.getDeclaringType ());
				}
			}
		}
		if (!generator)
		{
			writeInvocation (writer, discard);
			return;
		}

		assert discard;
		writer.visitMethodInsn (Opcodes.INVOKESTATIC, invokeMethod);
		writer.visitInsn (Opcodes.DUP);
		Label next = new Label ();
		writer.visitJumpInsn (Opcodes.IFNULL, next);

		writer.visitInsn (Opcodes.DUP);
		writer.visitMethodInsn (ABRUPT_COMPLETION_TYPE, "getLabel");
		
		Label def = new Label ();
		int[] keys = transfers.getKeys (null);
		Label[] lbls = new Label[keys.length];
		for (int i = 0; i < lbls.length; i++)
		{
			lbls[i] = new Label ();
		}
		writer.visitLookupSwitchInsn (def, keys, lbls);
		for (int i = 0; i < lbls.length; i++)
		{
			writer.visitLabel (lbls[i]);
			ControlTransfer ct = (ControlTransfer) transfers.getValueAt (i);
			switch (keys[i])
			{
				case AbruptCompletion.Throw.LABEL:
					writer.visitTypeInsn (Opcodes.CHECKCAST, writer.toName (THROW_TYPE));
					writer.visitMethodInsn (THROW_TYPE, "getCauseAndDispose");
					writer.visitInsn (Opcodes.ATHROW);
					break;
				case AbruptCompletion.Return.LABEL:
					writer.visitTypeInsn (Opcodes.CHECKCAST, writer.toName (RETURN_TYPE));
					Type rt = ((Return) ct).getScope ().getMethod ().getReturnType ();
					writer.visitMethodInsn
						(RETURN_TYPE, Reflection.getJVMPrefix (rt) + "get");
					writer.visitCheckCast (rt);
					ct.writeLocal (writer, this);
					break;
				default:
					writer.visitMethodInsn
						(Opcodes.INVOKEVIRTUAL, writer.toName (ABRUPT_COMPLETION_TYPE),
						 "dispose", "()V");
					ct.writeLocal (writer, this);
					break;
			}
		}
		writer.visitLabel (def);
		writer.visitTypeInsn (Opcodes.NEW, "java/lang/AssertionError");
		writer.visitInsn (Opcodes.DUP_X1);
		writer.visitInsn (Opcodes.SWAP);
		writer.visitMethodInsn (Opcodes.INVOKESPECIAL, "java/lang/AssertionError", "<init>", "(Ljava/lang/Object;)V");
		writer.visitInsn (Opcodes.ATHROW);
		
		writer.visitLabel (next);
		writer.visitInsn (Opcodes.POP);
	}

}
