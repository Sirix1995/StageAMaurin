
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

package de.grogra.xl.compiler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;

import de.grogra.reflect.Annotation;
import de.grogra.reflect.Field;
import de.grogra.reflect.Member;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.reflect.TypeLoader;
import de.grogra.reflect.XField;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.MethodScope;
import de.grogra.xl.compiler.scope.TypeScope;
import de.grogra.xl.expr.Block;
import de.grogra.xl.expr.ComplexMethod;
import de.grogra.xl.expr.Expression;
import de.grogra.xl.vmx.AbruptCompletion;
import de.grogra.xl.lang.BooleanConsumer;
import de.grogra.xl.lang.ByteConsumer;
import de.grogra.xl.lang.CharConsumer;
import de.grogra.xl.lang.DoubleConsumer;
import de.grogra.xl.lang.FloatConsumer;
import de.grogra.xl.lang.IntConsumer;
import de.grogra.xl.lang.LongConsumer;
import de.grogra.xl.lang.ObjectConsumer;
import de.grogra.xl.lang.ShortConsumer;
import de.grogra.xl.vmx.VMXState;
import de.grogra.xl.lang.VoidConsumer;

public class XMethod extends ComplexMethod implements Method
{
	String noRetDescriptor;
	AccessMethod accessMethod;

	private final String routineId;
	private String descriptor;
	private long modifiers;
	private final CClass declaringClass;
	private final MethodScope scope;
	private final Type returnType;
	private Type[] parameterTypes;
	private int xsize = 0;
	private final Type[] exceptionTypes;
	private final XField routineField;

	private ArrayList<Annotation> annotations = new ArrayList<Annotation> ();
	
	public XMethod (String name, long modifiers, CClass declaringClass,
					Type[] params, Type returnType, Expression expr)
	{
		setName (name);
		this.declaringClass = declaringClass;
		this.scope = null;
		this.modifiers = modifiers;
		this.returnType = returnType;
		this.parameterTypes = params;
		this.exceptionTypes = Type.TYPE_0;
		updateParameters ();
		this.routineId = null;
		declaringClass.declareMethod (this);
		routineField = null;
		add (expr);
	}


	public XMethod (String name, long modifiers, MethodScope scope,
					Type returnType, Type[] exceptionTypes)
	{
		setName (name);
		this.scope = scope;
		this.modifiers = modifiers;
		this.returnType = returnType;
		this.exceptionTypes = exceptionTypes;
		if ((modifiers & Compiler.MOD_ROUTINE) != 0)
		{
			this.declaringClass = scope.getDeclaredType ().getCallbackClass ();
			routineField = this.declaringClass.addRoutine (this);
		}
		else
		{
			this.declaringClass = scope.getDeclaredType ();
			routineField = null;
		}
		updateParameters ();
		this.routineId = (declaringClass.getBinaryName () + '.' + name + '('
						  + Reflection.getDescriptor (parameterTypes) + ')').intern ();
		setJFrameSize (0);
		declaringClass.declareMethod (this);
	}

	
	public Local getLocalForVMX ()
	{
		return scope.getLocalForVMX ();
	}

	
	public Field getRoutineField ()
	{
		return routineField;
	}


	public void updateParameters ()
	{
		boolean upd;
		if (scope != null)
		{
			upd = parameterTypes != null;
			parameterTypes = scope.getParameterTypes ();
		}
		else
		{
			upd = false;
		}
		descriptor = Reflection.getMethodDescriptor
			(getName (), returnType, parameterTypes);
		noRetDescriptor = descriptor.substring (0, descriptor.lastIndexOf (')') + 1);
		setParameterSize
			(Reflection.getJVMStackSize (parameterTypes)
			 + (((modifiers & Member.STATIC) == 0) ? 1 : 0));
		if (upd)
		{
			declaringClass.getLookup ().update ();
		}
	}
	
	
	public String getRoutineId ()
	{
		return routineId;
	}


	@Override
	public TypeLoader getTypeLoader ()
	{
		return declaringClass.getTypeLoader ();
	}


	public final void setXFrameSize (int xsize)
	{
		this.xsize = xsize;
	}

	
	@Override
	public int getFrameSize ()
	{
		return xsize;
	}

	
	@Override
	public boolean hasJavaParameters ()
	{
		return (modifiers & Compiler.MOD_ROUTINE) == 0;
	}


	public final int getModifiers ()
	{
		return (int) modifiers;
	}


	public final long getModifiersEx ()
	{
		return modifiers;
	}

	
	public final void setPublic ()
	{
		modifiers = (modifiers & ~ACCESS_MODIFIERS) | PUBLIC;
	}

	public final Type getDeclaringType ()
	{
		return declaringClass;
	}


	public final String getSimpleName ()
	{
		return getName ();
	}


	public final String getDescriptor ()
	{
		return descriptor;
	}


	public final Type getReturnType ()
	{
		return returnType;
	}


	public final int getParameterCount ()
	{
		return parameterTypes.length;
	}


	public final Type getParameterType (int index)
	{
		return parameterTypes[index];
	}

	
	public int getExceptionCount ()
	{
		return exceptionTypes.length; 
	}

	
	public Type getExceptionType (int index)
	{
		return exceptionTypes[index];
	}


	public int getDeclaredAnnotationCount ()
	{
		return annotations.size ();
	}


	public Annotation getDeclaredAnnotation (int index)
	{
		return annotations.get (index);
	}

	List<Annotation> getDeclaredAnnotations ()
	{
		return annotations;
	}

	public int getParameterAnnotationCount (int param)
	{
		if (scope == null)
		{
			return 0;
		}
		return scope.getParameter (param).getDeclaredAnnotationCount ();
	}

	public Annotation getParameterAnnotation (int param, int index)
	{
		return scope.getParameter (param).getDeclaredAnnotation (index);
	}


	public final Object invoke (Object instance, Object[] arguments)
		throws InvocationTargetException
	{
		return invoke (instance, arguments, VMXState.current ());
	}


	public final Object invoke (Object instance, Object[] arguments,
								VMXState t)
		throws InvocationTargetException
	{
		AbruptCompletion.Return e = invoke0 (instance, arguments, t);
		if (e == null)
		{
			return null;
		}
		switch (e.getTypeId ())
		{
			case BOOLEAN:
				return (e.iget () != 0) ? Boolean.TRUE : Boolean.FALSE;
			case BYTE:
				return Byte.valueOf ((byte) e.iget ());
			case SHORT:
				return Short.valueOf ((short) e.iget ());
			case CHAR:
				return Character.valueOf ((char) e.iget ());
			case INT:
				return Integer.valueOf (e.iget ());
			case LONG:
				return Long.valueOf (e.lget ());
			case FLOAT:
				return Float.valueOf (e.fget ());
			case DOUBLE:
				return Double.valueOf (e.dget ());
			default:
				return e.aget ();
		}
	}


	private AbruptCompletion.Return invoke0
		(Object instance, Object[] arguments, VMXState t)
		throws InvocationTargetException
	{
		if ((modifiers & STATIC) == 0)
		{
			if ((modifiers & Compiler.MOD_CONSTRUCTOR) != 0)
			{
				t.apush (declaringClass);
			}
			else
			{
				if (instance == null)
				{
					throw new NullPointerException ();
				}
				if (!(declaringClass.isInstance (instance)))
				{
					throw new IllegalArgumentException ();
				}
				t.apush (instance);
			}
		}
		if (arguments == null)
		{
			if (parameterTypes.length != 0)
			{
				throw new IllegalArgumentException ();
			}
		}
		else
		{
			if (parameterTypes.length != arguments.length)
			{
				throw new IllegalArgumentException ();
			}
		}
		for (int i = 0; i < parameterTypes.length; i++)
		{
			Object a = arguments[i];
			int id = parameterTypes[i].getTypeId ();
			switch (id)
			{
				case BOOLEAN:
					if (a instanceof Boolean)
					{
						t.ipush (((Boolean) a).booleanValue ()
								 ? 1 : 0);
						continue;
					}
					break;
				case CHAR:
					if (a instanceof Character)
					{
						t.ipush (((Character) a).charValue ());
						continue;
					}
					break;
				case LONG:
					if (a instanceof Number)
					{
						t.lpush (((Number) a).longValue ());
						continue;
					}
					break;
				case FLOAT:
					if (a instanceof Number)
					{
						t.fpush (((Number) a).floatValue ());
						continue;
					}
					break;
				case DOUBLE:
					if (a instanceof Number)
					{
						t.dpush (((Number) a).doubleValue ());
						continue;
					}
					break;
				default:
					if (((1 << id) & I_VALUE) != 0)
					{
						if (a instanceof Number)
						{
							t.ipush (((Number) a).intValue ());
							continue;
						}
					}
					else
					{
						if ((a == null) || parameterTypes[i].isInstance (a))
						{
							t.apush (a);
							continue;
						}
					}
			}
			throw new IllegalArgumentException ();
		}
		AbruptCompletion.Return e = t.invoke (this, -1, null);
		if ((e != null) && (e.getTypeId () == VOID))
		{
			e.dispose ();
			e = null;
		}
		if ((((modifiers & Compiler.MOD_CONSTRUCTOR) != 0) ? OBJECT
			 : returnType.getTypeId ()) != ((e == null) ? VOID : e.getTypeId ()))
		{
			throw new AssertionError ();
		}
		return e;
	}	


	@Override
	protected void evaluateImpl (VMXState t)
	{
		if ((modifiers & (STATIC | Compiler.MOD_CONSTRUCTOR)) != 0)
		{
			declaringClass.initialize ();
		}
		if ((modifiers & Compiler.MOD_CONSTRUCTOR) != 0)
		{
			try
			{
				super.evaluateImpl (t);
			}
			catch (AbruptCompletion.Return e)
			{
				e.dispose ();
			}
			throw t.areturn (t.agetj (0, null));
		}
		else
		{
			super.evaluateImpl (t);
		}
		if ((modifiers & Compiler.MOD_ITERATING) != 0)
		{
			switch (returnType.getTypeId ())
			{
				case BOOLEAN:
				case BYTE:
				case SHORT:
				case CHAR:
				case INT:
					throw t.ireturn (0);
				case LONG:
					throw t.lreturn (0);
				case FLOAT:
					throw t.freturn (0);
				case DOUBLE:
					throw t.dreturn (0);
				case OBJECT:
					throw t.areturn (null);
				case VOID:
					break;
				default:
					throw new AssertionError ();
			}
		}
		if (returnType.getTypeId () != VOID)
		{
			throw new RuntimeException
				("Method " + Reflection.getDescription (this) + " exited without return value");
		}
	}

	
	@Override
	protected String paramString ()
	{
		return super.paramString () + ',' + Reflection.getDescription (this) + ',' + Long.toHexString (getModifiersEx ());
	}

	public static Type getGeneratorType (Method method)
	{
		if (method.getParameterCount () == 0)
		{
			return null;
		}
		Type consumerType = method.getParameterType (0);
		for (int i = 0; i < TYPE_COUNT; i++)
		{
			if (Reflection.equal (getConsumerType (i), consumerType))
			{
				if (i != OBJECT)
				{
					return Reflection.getType (i);
				}
				Type r = method.getReturnType ();
				return (r.getTypeId () == OBJECT) ? r : Type.OBJECT;
			}
		}
		return null;
	}

	public static Type getGeneratorOrReturnType (Method method)
	{
		Type t = getGeneratorType (method);
		return (t != null) ? t : method.getReturnType ();
	}

	public static Class getConsumerType (int typeId)
	{
		switch (typeId)
		{
/*!!
#foreach ($type in $types_void)
$pp.setType($type)
			case TypeId.$pp.TYPE:
				return ${pp.Type}Consumer.class;
#end
!!*/
//!! #* Start of generated code
// generated
			case TypeId.BOOLEAN:
				return BooleanConsumer.class;
// generated
			case TypeId.BYTE:
				return ByteConsumer.class;
// generated
			case TypeId.SHORT:
				return ShortConsumer.class;
// generated
			case TypeId.CHAR:
				return CharConsumer.class;
// generated
			case TypeId.INT:
				return IntConsumer.class;
// generated
			case TypeId.LONG:
				return LongConsumer.class;
// generated
			case TypeId.FLOAT:
				return FloatConsumer.class;
// generated
			case TypeId.DOUBLE:
				return DoubleConsumer.class;
// generated
			case TypeId.OBJECT:
				return ObjectConsumer.class;
// generated
			case TypeId.VOID:
				return VoidConsumer.class;
//!! *# End of generated code
			default:
				throw new AssertionError (typeId);
		 }
	}


	public boolean hasEmptyBytecode ()
	{
		Expression e = getFirstExpression ();
		return (e instanceof Block) && ((Block) e).isEmpty ()
			&& !containsAssertionStatusInit ();
	}

	
	private boolean containsAssertionStatusInit ()
	{
		return (scope != null) && "<clinit>".equals (getName ())
			&& TypeScope.get (scope).hasAssertionsDisabledField ();
	}


	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		assert discard;
		if (containsAssertionStatusInit ())
		{
			writer.visitaconst (declaringClass);
			writer.visitMethodInsn
				(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "desiredAssertionStatus", "()Z");
			writer.visiticonst (1);
			writer.visitInsn (Opcodes.IXOR);
			writer.visitFieldInsn
				(Opcodes.PUTSTATIC, TypeScope.get (scope).getAssertionsDisabledField (), null);
		}
		writeChildren (writer);
		if (!writer.isUnreachable ())
		{
			writer.visitNull (returnType.getTypeId ());
			writer.visitReturn (returnType.getTypeId ());
		}
	}

	void dispose ()
	{
		removeAll (null);
		if (scope != null)
		{
			scope.dispose ();
		}
	}

}
