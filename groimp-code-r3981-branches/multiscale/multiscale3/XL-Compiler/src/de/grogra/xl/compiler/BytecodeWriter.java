
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.Opcodes;

import de.grogra.reflect.Annotation;
import de.grogra.reflect.AnnotationImpl;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Field;
import de.grogra.reflect.Member;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.reflect.XField;
import de.grogra.util.Utils;
import de.grogra.util.WrapException;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.MemoryFileSystem;
import de.grogra.xl.compiler.scope.CompilationUnitScope;
import de.grogra.xl.expr.Expression;
import de.grogra.xl.util.XHashMap;
import de.grogra.xl.vmx.Authorization;
import de.grogra.xl.vmx.VMXState;

public strictfp class BytecodeWriter extends MethodAdapter implements Opcodes
{
	private final CompilerOptions opts;
	private boolean fpStrict;
	private CClass currentType;
	private XMethod currentMethod;
	private int currentLine;
	private HashMap<String,Field> classConstants = new HashMap<String,Field> ();
	private HashMap<String,Type> innerClasses = new HashMap<String,Type> ();

	
	private static final String VMX_DESCR = ClassAdapter.wrap (VMXState.class)
		.getDescriptor ();
	
	public static final String AUTH_DESCR = ClassAdapter.wrap (Authorization.class)
		.getDescriptor ();

	private static String toNameImpl (Type type)
	{
		return type.getBinaryName ().replace ('.', '/');
	}


	public String toName (Type type)
	{
		type = Reflection.getBinaryType (type);
		if (type == null)
		{
			return null;
		}
		if (type.getTypeId () != TypeId.OBJECT)
		{
			throw new IllegalArgumentException (type.getName ());
		}
		String name = toNameImpl (type);
		while (Reflection.isArray (type))
		{
			type = type.getComponentType ();
		}
		if (type.getDeclaringType () != null)
		{
			addInnerClass (type);
		}
		return name;
	}

	
	private void addInnerClass (Type type)
	{
		innerClasses.put (type.getBinaryName (), type);
	}

	
	public static String getDescriptor (Method method)
	{
		if (method instanceof XMethod)
		{
			XMethod xm = (XMethod) method;
			if ((xm.getModifiersEx () & Compiler.MOD_ROUTINE) != 0)
			{
				return "(" + VMX_DESCR + ")" + xm.getReturnType ().getDescriptor ();
			}
		}
		String d = method.getDescriptor ();
		return d.substring (d.indexOf ('('));
	}


	public String[] toNames (Type[] types)
	{
		if (types.length == 0)
		{
			return Utils.STRING_0;
		}
		String[] a = new String[types.length];
		for (int i = 0; i < types.length; i++)
		{
			a[i] = toName (types[i]);
		}
		return a;
	}

	
	public BytecodeWriter (CompilerOptions opts)
	{
		super (null);
		this.opts = opts;
	}

	
	public boolean isFPStrict ()
	{
		return fpStrict;
	}
	
	
	public int getVersion ()
	{
		return opts.javaVersion;
	}

	
	public boolean supportsVersion (int ver)
	{
		return CompilerOptions.isVersionGE (opts.javaVersion, ver);
	}


	private static final int INNER_CLASS_MODS
		= Member.PUBLIC | Member.PROTECTED | Member.PRIVATE | Member.FINAL
		| Member.STATIC | Member.INTERFACE | Member.ABSTRACT;

	public byte[] toByteArray (String source, CClass type)
	{
		innerClasses.clear ();
		ClassWriter w = new ClassWriter (ClassWriter.COMPUTE_MAXS);
		int tmods = type.getModifiers () & (Member.JAVA_MODIFIERS & ~Member.PRIVATE);
		if ((tmods & Member.PROTECTED) != 0)
		{
			tmods = (tmods & ~Member.PROTECTED) | Member.PUBLIC;
		}
		if ((tmods & Member.INTERFACE) == 0)
		{
			tmods |= ACC_SUPER;
		}
		w.visit (opts.javaVersion, tmods, toName (type), null,
				 toName (type.getSupertype ()),
				 toNames (Reflection.getDeclaredInterfaces (type)));

		if ((source != null) && opts.sourceInfo)
		{
			w.visitSource (source, null);
		}
		
		boolean fps = false;
		Type t = type;
		while ((t != null) && !fps)
		{
			fps = (t.getModifiers () & Member.STRICT) != 0;
			t = t.getSupertype ();
		}
		
		classConstants.clear ();
		currentType = type;

		for (int i = 0; i < type.getDeclaredMethodCount (); i++)
		{
			((XMethod) type.getDeclaredMethod (i)).linkGraph (true);
		}

		for (int i = 0; i < type.getDeclaredMethodCount (); i++)
		{
			XMethod m = (XMethod) type.getDeclaredMethod (i);
			if (m.getSimpleName ().equals ("<clinit>")
				&& m.hasEmptyBytecode ())
			{
				continue;
			}
			int mmods = m.getModifiers () & Member.JAVA_MODIFIERS;
			mv = w.visitMethod
				(mmods, m.getSimpleName (),
				 getDescriptor (m), null,
				 toNames (Reflection.getExceptions (m)));
			if (mv != null)
			{
				for (int a = 0; a < m.getDeclaredAnnotationCount (); a++)
				{
					Annotation an = m.getDeclaredAnnotation (a);
					writeAnnotation (mv.visitAnnotation (an.annotationType ().getDescriptor (), true), an);
				}
				for (int p = 0; p < m.getParameterCount (); p++)
				{
					for (int a = 0; a < m.getParameterAnnotationCount (p); a++)
					{
						Annotation an = m.getParameterAnnotation (p, a);
						writeAnnotation (mv.visitParameterAnnotation (p, an.annotationType ().getDescriptor (), true), an);
					}
				}
				if (!Reflection.isAbstract (m))
				{
					mv.visitCode ();
					fpStrict = fps || ((m.getModifiers () & Member.STRICT) != 0);
					currentMethod = m;
					currentLine = 0;
					unreachable = null;
					m.write (this, true);
					currentMethod = null;
					mv.visitMaxs (0, 0);
				}
				mv.visitEnd ();
				mv = null;
			}
		}
		
		for (int i = 0; i < type.getDeclaredFieldCount (); i++)
		{
			XField f = (XField) type.getDeclaredField (i);
			Object c = null;
			if ((f.getModifiers () & Member.CONSTANT) != 0)
			{
				switch (f.getType ().getTypeId ())
				{
					case TypeId.BOOLEAN:
						c = Integer.valueOf (f.getBoolean (null) ? 1 : 0);
						break;
					case TypeId.BYTE:
						c = Integer.valueOf (f.getByte (null));
						break;
					case TypeId.SHORT:
						c = Integer.valueOf (f.getShort (null));
						break;
					case TypeId.CHAR:
						c = Integer.valueOf (f.getChar (null));
						break;
					case TypeId.INT:
						c = Integer.valueOf (f.getInt (null));
						break;
					case TypeId.LONG:
						c = Long.valueOf (f.getLong (null));
						break;
					case TypeId.FLOAT:
						c = Float.valueOf (f.getFloat (null));
						break;
					case TypeId.DOUBLE:
						c = Double.valueOf (f.getDouble (null));
						break;
					case TypeId.OBJECT:
						c = (String) f.getObject (null);
						break;
				}
			}
			FieldVisitor v = w.visitField
				(f.getModifiers () & Member.JAVA_MODIFIERS, f.getSimpleName (),
				 f.getType ().getDescriptor (), null, c);
			if (v != null)
			{
				for (int a = 0; a < f.getDeclaredAnnotationCount (); a++)
				{
					Annotation an = f.getDeclaredAnnotation (a);
					writeAnnotation (v.visitAnnotation (an.annotationType ().getDescriptor (), true), an);
				}
				v.visitEnd ();
			}
		}
		
		for (Iterator ams = type.getAccessMethods (); ams.hasNext (); )
		{
			AccessMethod am = (AccessMethod) ams.next ();
			mv = w.visitMethod
				("<init>".equals (am.getName ()) ? ACC_SYNTHETIC : ACC_STATIC | ACC_SYNTHETIC,
				 am.getName (), am.getDescriptor (), null, Utils.STRING_0);
			if (mv != null)
			{
				mv.visitCode ();
				am.write (this);
				mv.visitMaxs (0, 0);
				mv.visitEnd ();
				mv = null;
			}
		}
		
		classConstants.clear ();

		for (int i = 0; i < type.getDeclaredTypeCount (); i++)
		{
			addInnerClass (type.getDeclaredType (i));
		}

		for (Type ic : innerClasses.values ())
		{
			if ((ic.getModifiers () & Member.LOCAL_CLASS) != 0)
			{
				String n = ic.getSimpleName ();
				w.visitInnerClass
					(toNameImpl (ic), null, "".equals (n) ? null : n,
					 ic.getModifiers () & INNER_CLASS_MODS);
			}
			else
			{
				w.visitInnerClass
					(toNameImpl (ic), toNameImpl (ic.getDeclaringType ()),
					 ic.getSimpleName (), ic.getModifiers () & INNER_CLASS_MODS);
			}
		}
		innerClasses.clear ();
		
		currentType = null;
		
		w.visitEnd ();
		return w.toByteArray ();
	}

	private static void writeAnnotation (AnnotationVisitor av, Annotation a)
	{
		String[] e = ((AnnotationImpl<?>) a).elements ();
		for (int i = 0; i < e.length; i++)
		{
			Object v = a.value (e[i]);
			if (v instanceof Object[])
			{
				Object[] array = (Object[]) v;
				AnnotationVisitor av2 = av.visitArray (e[i]);
				for (int j = 0; j < array.length; j++)
				{
					visitElement (av2, null, array[j]);
				}
				av2.visitEnd ();
			}
			else
			{
				visitElement (av, e[i], v);
			}
		}
		av.visitEnd ();
	}

	private static void visitElement (AnnotationVisitor av, String name, Object value)
	{
		if (value instanceof Type)
		{
			av.visit (name, org.objectweb.asm.Type.getType (((Type) value).getDescriptor ()));
		}
		else
		{
			av.visit (name, value);
		}
	}

	private Label unreachable;
	
	@Override
	public void visitInsn (int opcode)
	{
		mv.visitInsn (opcode);
		switch (opcode)
		{
			case RETURN:
			case IRETURN:
			case LRETURN:
			case FRETURN:
			case DRETURN:
			case ARETURN:
			case ATHROW:
				unreachable = new Label ();
				mv.visitLabel (unreachable);
				break;
		}
	}

	
	@Override
	public void visitVarInsn (int opcode, int var)
	{
		mv.visitVarInsn (opcode, var);
		if (opcode == RET)
		{
			unreachable = new Label ();
			mv.visitLabel (unreachable);
		}
	}

	
	@Override
	public void visitJumpInsn (int opcode, Label lbl)
	{
		mv.visitJumpInsn (opcode, lbl);
		if (opcode == GOTO)
		{
			unreachable = new Label ();
			mv.visitLabel (unreachable);
		}
	}
	
	
	@Override
	public void visitLabel (Label lbl)
	{
		mv.visitLabel (lbl);
		unreachable = null;
	}

	
	public boolean isUnreachable ()
	{
		if (unreachable == null)
		{
			return false;
		}
		Label l = new Label ();
		mv.visitLabel (l);
		return l.getOffset () == unreachable.getOffset ();
	}

	
	public void visitLineNumber (int line)
	{
		if ((line > 0) && (line != currentLine) && opts.lineNumberInfo)
		{
			Label lbl = new Label ();
			mv.visitLabel (lbl);
			mv.visitLineNumber (line, lbl);
			currentLine = line;
		}
	}

	
	private Type qualifyingType (Member m, Type inheriting)
	{
		inheriting = Reflection.getBinaryType (inheriting);
		return Reflection.isSupertypeOrSame (m.getDeclaringType (), inheriting)
			? inheriting : m.getDeclaringType ();
	}


	public void visitFieldInsn (int opcode, Field field, AccessMethod access)
	{
		if (Reflection.isArray (field.getDeclaringType ()))
		{
			assert (opcode == GETFIELD) && (field.getSimpleName ().equals ("length"));
			mv.visitInsn (ARRAYLENGTH);
		}
		else if (access != null)
		{
			mv.visitMethodInsn
				(INVOKESTATIC, toName (access.getDeclaringClass ()),
				 access.getName (), access.getDescriptor ());
		}
		else
		{
			mv.visitFieldInsn
				(opcode,
				 toName (qualifyingType (field, InheritedField.getQualifyingType (field))),
				 field.getSimpleName (), field.getType ().getDescriptor ());
		}
	}

	
	public void visitFieldInsn (int opcode, Class type, String name, String descr)
	{
		Type t = ClassAdapter.wrap (type);
		Field f = t.getLookup ().getField (name);
		int i = f.getDescriptor ().indexOf (';');
		if (f.getDescriptor ().regionMatches (i + 1, descr, 0, descr.length ()))
		{	
			visitFieldInsn (opcode, toName (t), name, descr);
			return;
		}
		throw new NoSuchFieldError (type + " " + name + " " + descr);
	}

	
	public void visitMethodInsn (int opcode, Method method)
	{
		Type q = qualifyingType (method, InheritedMethod.getQualifyingType (method));
		if (Reflection.isInterface (q))
		{
			if (Reflection.equal (Type.OBJECT, method.getDeclaringType ()))
			{
				q = Type.OBJECT;
			}
		}
		if ((opcode == INVOKEINTERFACE) && !Reflection.isInterface (q))
		{
			opcode = INVOKEVIRTUAL;
		}
		mv.visitMethodInsn
			(opcode,
			 toName (q),
			 method.getSimpleName (), getDescriptor (method));
	}

	
	public void visitMethodInsn (Method method)
	{
		visitMethodInsn
			(Reflection.isStatic (method) ? INVOKESTATIC
			 : Reflection.isInterface (method.getDeclaringType ()) ? INVOKEINTERFACE
			 : (method.getSimpleName ().equals ("<init>") || Reflection.isPrivate (method)) ? INVOKESPECIAL
			 : INVOKEVIRTUAL, method);
	}


	public void visitMethodInsn (Type type, String name)
	{
		visitMethodInsn (type, name, null);
	}



	public void visitMethodInsn (Class type, String name, String descr)
	{
		visitMethodInsn (ClassAdapter.wrap (type), name, descr);
	}


	public void visitMethodInsn (Class type, String name)
	{
		visitMethodInsn (ClassAdapter.wrap (type), name, null);
	}


	public void visitMethodInsn (Type type, String name, String descr)
	{
		if (!visitMethodInsnImpl (type, name, descr))
		{
			throw new NoSuchMethodError (type + " " + name + " " + descr);
		}
	}

	private boolean visitMethodInsnImpl (Type type, String name, String descr)
	{
		while (type != null)
		{
			for (XHashMap.Entry e = type.getLookup ().getMethods (name); e != null;
				 e = e.next ())
			{
				Method m = (Method) e.getValue ();
				if (descr == null)
				{
					if (e.next () != null)
					{
						throw new AssertionError ("Method " + name + " is overloaded");
					}
					descr = getDescriptor (m);
				}
				else if (!m.getDescriptor ().endsWith (descr))
				{
					continue;
				}
				visitMethodInsn (m);
				return true;
			}
			for (int i = 0; i < type.getDeclaredInterfaceCount (); i++)
			{
				if (visitMethodInsnImpl (type.getDeclaredInterface (i), name, descr))
				{
					return true;
				}
			}
			type = type.getSupertype ();
		}
		return false;
	}


	public void visitTypeInsn (int opcode, Type type)
	{
		mv.visitTypeInsn (opcode, toName (type));
	}


	public void visitCheckCast (Type type)
	{
		if ((type.getTypeId () == TypeId.OBJECT)
			&& !Reflection.equal (Type.OBJECT, type))
		{
			visitTypeInsn (CHECKCAST, type);
		}
	}

	
	public void visitNewArray (Type type)
	{
		switch (type.getTypeId ())
		{
			case TypeId.BOOLEAN:
				mv.visitIntInsn (NEWARRAY, T_BOOLEAN);
				break;
			case TypeId.BYTE:
				mv.visitIntInsn (NEWARRAY, T_BYTE);
				break;
			case TypeId.SHORT:
				mv.visitIntInsn (NEWARRAY, T_SHORT);
				break;
			case TypeId.CHAR:
				mv.visitIntInsn (NEWARRAY, T_CHAR);
				break;
			case TypeId.INT:
				mv.visitIntInsn (NEWARRAY, T_INT);
				break;
			case TypeId.LONG:
				mv.visitIntInsn (NEWARRAY, T_LONG);
				break;
			case TypeId.FLOAT:
				mv.visitIntInsn (NEWARRAY, T_FLOAT);
				break;
			case TypeId.DOUBLE:
				mv.visitIntInsn (NEWARRAY, T_DOUBLE);
				break;
			case TypeId.OBJECT:
				visitTypeInsn (ANEWARRAY, type);
				break;
			default:
				throw new AssertionError ();
		}
	}

	
	public void visitVMX ()
	{
		de.grogra.xl.compiler.scope.Local l = currentMethod.getLocalForVMX ();
		visitLoad (l.createVMXLocal (), l.getType ());
	}


	private static final int[] LOAD_OPCODES
		= {ILOAD, LLOAD, FLOAD, DLOAD, ALOAD};

	public void visitLoad (VMXState.Local local, Type type)
	{
		if (local.isJavaLocal ())
		{
			visitLoad (local.getIndex (), type.getTypeId ());
		}
		else
		{
			visitVMX ();
			visiticonst (local.getNesting ());
			visiticonst (local.getIndex ());
			visitaconst (null);//AUTH
			visitMethodInsn
				(VMXState.class,
				 Reflection.getJVMPrefix (type) + "get",
				 '(' + VMX_DESCR + "II" + AUTH_DESCR + ')'
				 + Reflection.getType (Reflection.getJVMTypeId (type)).getDescriptor ());
			visitCheckCast (type);
		}
	}


	public void visitLoad (int index, int typeId)
	{
		mv.visitVarInsn (Expression.opcode (typeId, LOAD_OPCODES), index);
	}


	private static final int[] STORE_OPCODES
		= {ISTORE, LSTORE, FSTORE, DSTORE, ASTORE};

	public void visitStore (VMXState.Local local, Type type)
	{
		if (local.isJavaLocal ())
		{
			mv.visitVarInsn (Expression.opcode (type.getTypeId (), STORE_OPCODES),
							 local.getIndex ());
		}
		else
		{
			visitVMX ();
			visiticonst (local.getNesting ());
			visiticonst (local.getIndex ());
			visitaconst (null);//AUTH
			visitMethodInsn
				(VMXState.class,
				 Reflection.getJVMPrefix (type) + "set",
				 '(' + Reflection.getType (Reflection.getJVMTypeId (type)).getDescriptor ()
				 + (VMX_DESCR + "II" + AUTH_DESCR + ")V"));
		}
	}


	private static final int[] RETURN_OPCODES
		= {IRETURN, LRETURN, FRETURN, DRETURN, ARETURN};

	public void visitReturn (int typeId)
	{
		visitInsn ((typeId == TypeId.VOID) ? RETURN
				   : Expression.opcode (typeId, RETURN_OPCODES));
	}


	private static final int[] CONST
		= {Opcodes.ICONST_0, Opcodes.LCONST_0, Opcodes.FCONST_0, Opcodes.DCONST_0, Opcodes.ACONST_NULL};

	
	public void visitNull (int typeId)
	{
		if (typeId != TypeId.VOID)
		{
			visitInsn (Expression.opcode (typeId, CONST));
		}
	}


	public void visitPop (int typeId)
	{
		if (typeId != TypeId.VOID)
		{
			visitInsn (Reflection.hasCategory2 (typeId) ? Opcodes.POP2 : Opcodes.POP);
		}
	}


	public void visitALoad (int typeId)
	{
		switch (typeId)
		{
			case TypeId.BOOLEAN:
			case TypeId.BYTE:
				typeId = BALOAD;
				break;
			case TypeId.SHORT:
				typeId = SALOAD;
				break;
			case TypeId.CHAR:
				typeId = CALOAD;
				break;
			case TypeId.INT:
				typeId = IALOAD;
				break;
			case TypeId.LONG:
				typeId = LALOAD;
				break;
			case TypeId.FLOAT:
				typeId = FALOAD;
				break;
			case TypeId.DOUBLE:
				typeId = DALOAD;
				break;
			case TypeId.OBJECT:
				typeId = AALOAD;
				break;
			default:
				throw new AssertionError ();
		}
		mv.visitInsn (typeId);
	}


	public void visitAStore (int typeId)
	{
		switch (typeId)
		{
			case TypeId.BOOLEAN:
			case TypeId.BYTE:
				typeId = BASTORE;
				break;
			case TypeId.SHORT:
				typeId = SASTORE;
				break;
			case TypeId.CHAR:
				typeId = CASTORE;
				break;
			case TypeId.INT:
				typeId = IASTORE;
				break;
			case TypeId.LONG:
				typeId = LASTORE;
				break;
			case TypeId.FLOAT:
				typeId = FASTORE;
				break;
			case TypeId.DOUBLE:
				typeId = DASTORE;
				break;
			case TypeId.OBJECT:
				typeId = AASTORE;
				break;
			default:
				throw new AssertionError ();
		}
		mv.visitInsn (typeId);
	}


	public void visitDup (int typeId)
	{
		mv.visitInsn (Reflection.hasCategory2 (typeId) ? DUP2 : DUP);
	}


	public void visitDupX1 (int typeId)
	{
		mv.visitInsn (Reflection.hasCategory2 (typeId) ? DUP2_X1 : DUP_X1);
	}


	public void visitDupX2 (int typeId)
	{
		mv.visitInsn (Reflection.hasCategory2 (typeId) ? DUP2_X2 : DUP_X2);
	}


	public void visitSwap (int second, int top)
	{
		if ((second != TypeId.VOID) && (top != TypeId.VOID))
		{
			if (Reflection.hasCategory2 (second))
			{
				if (Reflection.hasCategory2 (top))
				{
					visitInsn (Opcodes.DUP2_X2);
					visitInsn (Opcodes.POP2);
				}
				else
				{
					visitInsn (Opcodes.DUP_X2);
					visitInsn (Opcodes.POP);
				}
			}
			else
			{
				if (Reflection.hasCategory2 (top))
				{
					visitInsn (Opcodes.DUP2_X1);
					visitInsn (Opcodes.POP2);
				}
				else
				{
					visitInsn (Opcodes.SWAP);
				}
			}
		}
	}

	public void visitVM2T (int typeId)
	{
		switch (typeId)
		{
			case TypeId.BOOLEAN:
				mv.visitInsn (ICONST_1);
				mv.visitInsn (IAND);
				break;
			case TypeId.BYTE:
				mv.visitInsn (I2B);
				break;
			case TypeId.SHORT:
				mv.visitInsn (I2S);
				break;
			case TypeId.CHAR:
				mv.visitInsn (I2C);
				break;
		}
	}


	public void visitClass2Type ()
	{
		mv.visitInsn (Opcodes.ICONST_0);
		visitMethodInsn (ClassAdapter.class, "wrap", "(Ljava/lang/Class;Z)Lde/grogra/reflect/ClassAdapter;");
	}


	public void visiticonst (int value)
	{
		switch (value)
		{
			case -1:
				mv.visitInsn (ICONST_M1);
				break;
			case 0:
				mv.visitInsn (ICONST_0);
				break;
			case 1:
				mv.visitInsn (ICONST_1);
				break;
			case 2:
				mv.visitInsn (ICONST_2);
				break;
			case 3:
				mv.visitInsn (ICONST_3);
				break;
			case 4:
				mv.visitInsn (ICONST_4);
				break;
			case 5:
				mv.visitInsn (ICONST_5);
				break;
			default:
				if ((Byte.MIN_VALUE <= value) && (value <= Byte.MAX_VALUE))
				{
					mv.visitIntInsn (BIPUSH, value);
				}
				else if ((Short.MIN_VALUE <= value) && (value <= Short.MAX_VALUE))
				{
					mv.visitIntInsn (SIPUSH, value);
				}
				else
				{
					mv.visitLdcInsn (Integer.valueOf (value));
				}
		}
	}

	
	public void visitlconst (long value)
	{
		if (value == 0)
		{
			mv.visitInsn (LCONST_0);
		}
		else if (value == 1)
		{
			mv.visitInsn (LCONST_1);
		}
		else
		{
			mv.visitLdcInsn (Long.valueOf (value));
		}
	}

	
	private static final int FLOAT_0_BITS = Float.floatToIntBits (0);

	public void visitfconst (float value)
	{
		if (Float.floatToIntBits (value) == FLOAT_0_BITS)
		{
			mv.visitInsn (FCONST_0);
		}
		else if (value == 1f)
		{
			mv.visitInsn (FCONST_1);
		}
		else if (value == 2f)
		{
			mv.visitInsn (FCONST_2);
		}
		else
		{
			mv.visitLdcInsn (Float.valueOf (value));
		}
	}

	
	private static final long DOUBLE_0_BITS = Double.doubleToLongBits (0);

	public void visitdconst (double value)
	{
		if (Double.doubleToLongBits (value) == DOUBLE_0_BITS)
		{
			mv.visitInsn (DCONST_0);
		}
		else if (value == 1d)
		{
			mv.visitInsn (DCONST_1);
		}
		else
		{
			mv.visitLdcInsn (Double.valueOf (value));
		}
	}

	
	private void visitClassFromArray (Type type)
	{
		mv.visitInsn (ICONST_0);
		visitTypeInsn (ANEWARRAY, type);
		mv.visitMethodInsn
			(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
		mv.visitMethodInsn
			(INVOKEVIRTUAL, "java/lang/Class", "getComponentType", "()Ljava/lang/Class;");
		
	}


	public void visitaconst (Object value)
	{
		if (value == null)
		{
			mv.visitInsn (ACONST_NULL);
		}
		else if (value instanceof String)
		{
			mv.visitLdcInsn (value);
		}
		else if ((value instanceof Class) || (value instanceof Type))
		{
			Type type = (value instanceof Type) ? (Type) value
				: ClassAdapter.wrap ((Class) value);
			type = Reflection.getBinaryType (type);
			if (Reflection.isPrimitiveOrVoid (type))
			{
				mv.visitFieldInsn
					(GETSTATIC,
					 Reflection.getWrapperClass (type.getTypeId ()).getName ().replace ('.', '/'),
					 "TYPE", "Ljava/lang/Class;");
			}
			else if (supportsVersion (V1_5))
			{
				mv.visitLdcInsn (org.objectweb.asm.Type.getType (type.getDescriptor ()));
			}
			else if ("<clinit>".equals (currentMethod.getSimpleName ()))
			{
				visitClassFromArray (type);
			}
			else
			{
				assert (currentType.getModifiers () & Member.INTERFACE) == 0;
				Field f = classConstants.get (type.getBinaryName ());
				if (f == null)
				{
					f = currentType.declareAuxField ("class$", Member.STATIC, Type.CLASS);
					classConstants.put (type.getBinaryName (), f);
				}
				Label classOk = new Label ();
				visitFieldInsn (GETSTATIC, f, null);
				mv.visitInsn (DUP);
				mv.visitJumpInsn (IFNONNULL, classOk);
				mv.visitInsn (POP);
				visitClassFromArray (type);
				mv.visitInsn (DUP);
				visitFieldInsn (PUTSTATIC, f, null);
				mv.visitLabel (classOk);
			}
		}
		else
		{
			throw new IllegalArgumentException (String.valueOf (value));
		}
	}

	
	public MemoryFileSystem createFileSystemFor (CompilationUnitScope[] units)
	{
		MemoryFileSystem fs = new MemoryFileSystem ("classes");
		for (int i = 0; i < units.length; i++)
		{
			try
			{
				write (units[i], fs, fs.getRoot ());
			}
			catch (IOException e)
			{
				throw new WrapException (e);
			}
		}
		return fs;
	}


	public void write (CompilationUnitScope cs, FileSystem fs, Object root) throws IOException
	{
		Type[] a = cs.getDeclaredTypes ();
		for (int j = 0; j < a.length; j++)
		{
			writeType (a[j], cs.getSource (), fs, root);
		}
		a = cs.getLocalClasses ();
		for (int j = 0; j < a.length; j++)
		{
			writeType (a[j], cs.getSource (), fs, root);
		}
	}


	private void writeType (Type type, String source, FileSystem fs, Object root)
		throws IOException
	{
		Object dir = root;
		String n = type.getBinaryName ();
		int i;
		while ((i = n.indexOf ('.')) >= 0)
		{
			String d = n.substring (0, i);
			Object subdir = fs.getFile (dir, d);
			if (subdir != null)
			{
				if (fs.isLeaf (subdir))
				{
					throw new IOException (fs.getPath (subdir) + " exists, but is not a directory");
				}
			}
			else
			{
				subdir = fs.create (dir, d, true);
			}
			dir = subdir;
			n = n.substring (i + 1);
		}
		Object file = fs.create (dir, n + ".class", false);
		OutputStream os = fs.getOutputStream (file, false);
		os.write (toByteArray (source, (CClass) type));
		os.flush ();
		os.close ();
		for (int k = 0; k < type.getDeclaredTypeCount (); k++)
		{
			writeType (type.getDeclaredType (k), source, fs, root);
		}
	}

}
