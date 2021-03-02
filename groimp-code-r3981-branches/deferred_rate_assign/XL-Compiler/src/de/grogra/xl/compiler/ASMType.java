
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

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import de.grogra.reflect.Annotation;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Field;
import de.grogra.reflect.FieldBase;
import de.grogra.reflect.LazyType;
import de.grogra.reflect.Lookup;
import de.grogra.reflect.MemberBase;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.reflect.TypeLoader;
import de.grogra.reflect.XArray;
import de.grogra.util.WrapException;
import de.grogra.xl.util.ObjectList;

public class ASMType extends MemberBase implements Type, ClassVisitor
{
	private final ASMTypeLoader loader;
	private String binaryName;
	private String simpleName;
	private String internalName;
	private Type superType;
	
	private Type[] interfaces;
	private ObjectList<ASMType> types = new ObjectList<ASMType> ();
	private ObjectList<Field> fields = new ObjectList<Field> ();
	private ObjectList<ASMMethod> methods = new ObjectList<ASMMethod> ();
	private ObjectList varArgs = null;

	private HashMap<String, Object> defaultValues;

	private Type resolvedType;


	public ASMType (ASMTypeLoader loader)
	{
		super ();
		this.loader = loader;
	}

	
	synchronized Type resolve ()
	{
		if (resolvedType == null)
		{
			try
			{
				resolvedType = ClassAdapter.wrap
					(Class.forName (binaryName, false, loader.getClassLoader ()), false);
			}
			catch (ClassNotFoundException e)
			{
				throw new AssertionError (e.getMessage ());
			}
		}
		return resolvedType;
	}


	public TypeLoader getTypeLoader ()
	{
		return loader;
	}


	public int getTypeId ()
	{
		return TypeId.OBJECT;
	}


	public String getPackage ()
	{
		return binaryName.substring (0, Math.max (0, binaryName.lastIndexOf ('.')));
	}


	public String getBinaryName ()
	{
		return binaryName;
	}


	public boolean isInstance (Object object)
	{
		return resolve ().isInstance (object);
	}


	public Class getImplementationClass ()
	{
		return resolve ().getImplementationClass ();
	}


	public Type getSupertype ()
	{
		return superType;
	}


	public int getDeclaredInterfaceCount ()
	{
		return interfaces.length;
	}


	public Type getDeclaredInterface (int index)
	{
		return interfaces[index];
	}


	public int getDeclaredFieldCount ()
	{
		return fields.size ();
	}


	public Field getDeclaredField (int index)
	{
		return fields.get (index);
	}


	public int getDeclaredTypeCount ()
	{
		return types.size ();
	}


	public Type getDeclaredType (int index)
	{
		return types.get (index);
	}


	public int getDeclaredMethodCount ()
	{
		return methods.size ();
	}


	public Method getDeclaredMethod (int index)
	{
		return methods.get (index);
	}


	public Type getComponentType ()
	{
		return null;
	}


	public Type getArrayType ()
	{
		return new XArray (this);
	}


	public Object createArray (int length)
	{
		throw new AssertionError ();
	}


	public Object cloneObject (Object o, boolean deep) throws CloneNotSupportedException
	{
		return resolve ().cloneObject (o, deep);
	}


	public boolean isStringSerializable ()
	{
		return resolve ().isStringSerializable ();
	}


	public Object valueOf (String s)
	{
		return resolve ().valueOf (s);
	}


	public Object newInstance () throws InvocationTargetException, InstantiationException, IllegalAccessException
	{
		return resolve ().newInstance ();
	}


	public synchronized Object getDefaultElementValue (String name)
	{
		if (defaultValues == null)
		{
			return null;
		}
		Object v = defaultValues.get (name);
		Object w = convertElementValue (this, name, v);
		if (w != v)
		{
			defaultValues.put (name, w);
		}
		return w;
	}


	private Lookup lookup;

	public final synchronized Lookup getLookup ()
	{
		if (lookup == null)
		{
			lookup = new Lookup (this);
		}
		return lookup;
	}


	@Override
	public String getSimpleName ()
	{
		return simpleName;
	}


	@Override
	public String getDescriptor ()
	{
		return descriptor;
	}


	public void visit (int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		modifiers = access & JAVA_MODIFIERS;
		internalName = name;
		descriptor = 'L' + name + ';';
		binaryName = name.replace ('/', '.');
		this.name = binaryName.replace ('$', '.');
		this.simpleName = binaryName.substring (binaryName.lastIndexOf ('.') + 1);
		superType = (superName != null) ? new LazyType (superName.replace ('/', '.'), loader)
			: null;
		this.interfaces = getTypes (interfaces);
	}


	public void visitSource (String source, String debug)
	{
	}


	public void visitOuterClass (String owner, String name, String desc)
	{
	}


	public AnnotationVisitor visitAnnotation (String desc, boolean visible)
	{
		if (annots == null)
		{
			annots = new ObjectList<Annotation> ();
		}
		return new AnnVisitor (desc, annots, null, null);
	}


	public void visitAttribute (Attribute attr)
	{
	}


	public void visitInnerClass (String name, String outerName, String innerName, int access)
	{
		name = name.replace ('/', '.');
		try
		{
			ASMType t = (ASMType) loader.typeForName (name);
			t.simpleName = innerName;
			t.modifiers = (t.modifiers & ~(ACCESS_MODIFIERS | STATIC))
				| (access & (ACCESS_MODIFIERS | STATIC));
			if (internalName.equals (outerName))
			{
				t.declaringType = this;
				types.add (t);
			}
		}
		catch (ClassNotFoundException e)
		{
			throw new WrapException (e);
		}
	}

	
	int nextOffset;
	
	private Type getType (CharSequence in, int offset)
	{
		nextOffset = offset + 1;
		switch (in.charAt (offset))
		{
			case 'V':
				return VOID;
			case 'Z':
				return BOOLEAN;
			case 'B':
				return BYTE;
			case 'S':
				return SHORT;
			case 'C':
				return CHAR;
			case 'I':
				return INT;
			case 'J':
				return LONG;
			case 'F':
				return FLOAT;
			case 'D':
				return DOUBLE;
			case '[':
				return new XArray (getType (in, offset + 1));
			case 'L':
				int p = offset + 2;
				while (in.charAt (p) != ';')
				{
					p++;
				}
				nextOffset = p + 1;
				return new LazyType (in.subSequence (offset + 1, p).toString ().replace ('/', '.'), loader);
			default:
				throw new ClassFormatError (in.toString ());
		}
	}

	
	private Type[] getTypes (String[] names)
	{
		if ((names == null) || (names.length == 0))
		{
			return TYPE_0;
		}
		Type[] t = new Type[names.length];
		for (int i = 0; i < names.length; i++)
		{
			t[i] = new LazyType (names[i].replace ('/', '.'), loader);
		}
		return t;
	}


	static final ObjectList<Object> EMPTY_LIST = new ObjectList<Object> ();

	private class AnnVisitor implements AnnotationVisitor, Annotation
	{
		private final String typeDesc;
		private final List<Annotation> list;
		private final AnnVisitor parent;
		private final String elementName;
		private Type type;
		private HashMap<String, Object> values;
		private ObjectList<Object> elements;

		private String currentName;

		AnnVisitor (String typeDesc, List<Annotation> list, AnnVisitor parent, String elementName)
		{
			this.typeDesc = typeDesc;
			this.list = list;
			this.parent = parent;
			this.elementName = elementName;
		}

		public void visit (String name, Object value)
		{
			if (typeDesc == null)
			{
				if (elements == null)
				{
					elements = new ObjectList<Object> (); 
				}
				elements.add (value);
			}
			else
			{
				if (values == null)
				{
					values = new HashMap<String, Object> ();
				}
				values.put (name, value);
			}
		}

		public AnnotationVisitor visitAnnotation (String name, String desc)
		{
			currentName = name;
			return new AnnVisitor (desc, null, this, null);
		}

		public AnnotationVisitor visitArray (String name)
		{
			currentName = name;
			return new AnnVisitor (null, null, this, null);
		}

		public void visitEnd ()
		{
			if (list != null)
			{
				list.add (this);
			}
			else if (typeDesc != null)
			{
				parent.visit (parent.currentName, this);
			}
			else if (parent != null)
			{
				parent.visit (parent.currentName, (elements != null) ? elements : EMPTY_LIST);
			}
			else
			{
				if (elements.size () != 1)
				{
					throw new AnnotationFormatError (elements.toString ());
				}
				if (defaultValues == null)
				{
					defaultValues = new HashMap<String, Object> ();
				}
				defaultValues.put (elementName, elements.get (0));
			}
		}

		public void visitEnum (String name, String desc, String value)
		{
			visit (name, value);
		}


		public synchronized Type<? extends java.lang.annotation.Annotation> annotationType ()
		{
			if (type == null)
			{
				type = getType (typeDesc, 0);
			}
			return type;
		}


		public synchronized Object value (String element)
		{
			if (values != null)
			{
				Object v = values.get (element);
				if (v != null)
				{
					Object w = convertElementValue (annotationType (), element, v);
					if (w != v)
					{
						values.put (element, w);
					}
					return w;
				}
			}
			return annotationType ().getDefaultElementValue (element);
		}

		public String toString ()
		{
			return "Annotation[" + annotationType () + ',' + values + ']';
		}
	}

	Object convertElementValue (Type type, String element, Object v)
	{
		if (v instanceof org.objectweb.asm.Type)
		{
			return getType (((org.objectweb.asm.Type) v).getDescriptor (), 0);
		}
		else if (v instanceof ObjectList)
		{
			Method m = Reflection.getElementMethod (type, element);
			if (m == null)
			{
				throw new NoSuchMethodError (type + " " + element);
			}
			ObjectList<Object> l = (ObjectList<Object>) v;
			Type r = m.getReturnType ().getComponentType ();
			Class cls;
			if (Reflection.isPrimitive (r))
			{
				cls = r.getImplementationClass ();
			}
			else if (Reflection.equal (r, Type.STRING))
			{
				cls = String.class;
			}
			else if (Reflection.equal (r, Type.CLASS))
			{
				cls = Type.class;
			}
			else if (Reflection.isSuperclassOrSame (Enum.class, r))
			{
				cls = String.class;
			}
			else
			{
				cls = Annotation.class;
			}
			v = Array.newInstance (cls, l.size ());
			for (int i = 0; i < l.size (); i++)
			{
				Array.set (v, i, convertElementValue (null, null, l.get (i)));
			}
			return v;
		}
		else
		{
			return v;
		}
	}

	public FieldVisitor visitField (int access, String name, String desc, String signature, final Object value)
	{
		class ASMField extends FieldBase implements FieldVisitor
		{
			ASMField (String n, int m, Type t)
			{
				super (n, m, ASMType.this, t);
			}

			private Field resolved;
			
			private synchronized Field resolveField ()
			{
				if (resolved == null)
				{
					resolved = Reflection.getDeclaredField
						(resolve (), getSimpleName ());
				}
				return resolved;
			}
/*!!
#foreach ($type in $types)
$pp.setType($type)

			@Override
			public $type get$pp.Type (Object object) throws IllegalAccessException
			{
				if (value != null)
				{
#if ($pp.boolean)
					return ((Number) value).intValue () != 0;
#elseif ($pp.char)
					return (char) ((Number) value).intValue ();
#else
					return $pp.unwrap("value");
#end
				}
				return resolveField ().get$pp.Type (object);
			}


			@Override
			public void set$pp.Type (Object object, $type value) throws IllegalAccessException
			{
				resolveField ().set$pp.Type (object, value);
			}
#end
!!*/
//!! #* Start of generated code
// generated
// generated
			@Override
			public boolean getBoolean (Object object) throws IllegalAccessException
			{
				if (value != null)
				{
					return ((Number) value).intValue () != 0;
				}
				return resolveField ().getBoolean (object);
			}
// generated
// generated
			@Override
			public void setBoolean (Object object, boolean value) throws IllegalAccessException
			{
				resolveField ().setBoolean (object, value);
			}
// generated
// generated
			@Override
			public byte getByte (Object object) throws IllegalAccessException
			{
				if (value != null)
				{
					return (((Number) (value)).byteValue ());
				}
				return resolveField ().getByte (object);
			}
// generated
// generated
			@Override
			public void setByte (Object object, byte value) throws IllegalAccessException
			{
				resolveField ().setByte (object, value);
			}
// generated
// generated
			@Override
			public short getShort (Object object) throws IllegalAccessException
			{
				if (value != null)
				{
					return (((Number) (value)).shortValue ());
				}
				return resolveField ().getShort (object);
			}
// generated
// generated
			@Override
			public void setShort (Object object, short value) throws IllegalAccessException
			{
				resolveField ().setShort (object, value);
			}
// generated
// generated
			@Override
			public char getChar (Object object) throws IllegalAccessException
			{
				if (value != null)
				{
					return (char) ((Number) value).intValue ();
				}
				return resolveField ().getChar (object);
			}
// generated
// generated
			@Override
			public void setChar (Object object, char value) throws IllegalAccessException
			{
				resolveField ().setChar (object, value);
			}
// generated
// generated
			@Override
			public int getInt (Object object) throws IllegalAccessException
			{
				if (value != null)
				{
					return (((Number) (value)).intValue ());
				}
				return resolveField ().getInt (object);
			}
// generated
// generated
			@Override
			public void setInt (Object object, int value) throws IllegalAccessException
			{
				resolveField ().setInt (object, value);
			}
// generated
// generated
			@Override
			public long getLong (Object object) throws IllegalAccessException
			{
				if (value != null)
				{
					return (((Number) (value)).longValue ());
				}
				return resolveField ().getLong (object);
			}
// generated
// generated
			@Override
			public void setLong (Object object, long value) throws IllegalAccessException
			{
				resolveField ().setLong (object, value);
			}
// generated
// generated
			@Override
			public float getFloat (Object object) throws IllegalAccessException
			{
				if (value != null)
				{
					return (((Number) (value)).floatValue ());
				}
				return resolveField ().getFloat (object);
			}
// generated
// generated
			@Override
			public void setFloat (Object object, float value) throws IllegalAccessException
			{
				resolveField ().setFloat (object, value);
			}
// generated
// generated
			@Override
			public double getDouble (Object object) throws IllegalAccessException
			{
				if (value != null)
				{
					return (((Number) (value)).doubleValue ());
				}
				return resolveField ().getDouble (object);
			}
// generated
// generated
			@Override
			public void setDouble (Object object, double value) throws IllegalAccessException
			{
				resolveField ().setDouble (object, value);
			}
// generated
// generated
			@Override
			public Object getObject (Object object) throws IllegalAccessException
			{
				if (value != null)
				{
					return (value);
				}
				return resolveField ().getObject (object);
			}
// generated
// generated
			@Override
			public void setObject (Object object, Object value) throws IllegalAccessException
			{
				resolveField ().setObject (object, value);
			}
//!! *# End of generated code

			public AnnotationVisitor visitAnnotation (String desc, boolean visible)
			{
				if (annots == null)
				{
					annots = new ObjectList<Annotation> ();
				}
				return new AnnVisitor (desc, annots, null, null);
			}

			public void visitAttribute (Attribute attr)
			{
			}

			public void visitEnd ()
			{
			}
		}

		ASMField f = new ASMField (name, (access & JAVA_MODIFIERS) | (value != null ? CONSTANT : 0),
			   					   getType (desc, 0));
		fields.add (f);
		if (name.startsWith ("$VARARGS_")
			&& (value instanceof String))
		{
			if (varArgs == null)
			{
				varArgs = new ObjectList ();
			}
			varArgs.add (value);
		}
		return f;
	}

	
	static class ASMMethod extends MemberBase implements Method, MethodVisitor
	{
		private final Type returnType;
		private final Type[] parameterTypes;
		private final Type[] exceptionTypes;

		ASMMethod (String name, String descriptor, int access,
				   ASMType declaring, Type returnType, Type[] parameterTypes,
				   Type[] exceptionTypes)
		{
			super (name, Reflection.getMethodDescriptor (name, returnType, parameterTypes),
				   access, declaring);
			this.returnType = returnType;
			this.parameterTypes = parameterTypes;
			this.exceptionTypes = exceptionTypes;
		}

		void setVarArgs ()
		{
			modifiers |= VARARGS;
		}

		private Method resolved;
		
		private synchronized Method resolveMethod ()
		{
			if (resolved == null)
			{
				resolved = Reflection.getDeclaredMethod
					(((ASMType) getDeclaringType ()).resolve (), getDescriptor ());
			}
			return resolved;
		}

		public Type getReturnType ()
		{
			return returnType;
		}

		public Object invoke (Object instance, Object[] arguments) throws InvocationTargetException, IllegalAccessException
		{
			return resolveMethod ().invoke (instance, arguments);
		}

		public int getExceptionCount ()
		{
			return exceptionTypes.length;
		}

		public Type getExceptionType (int index)
		{
			return exceptionTypes[index];
		}

		public int getParameterCount ()
		{
			return parameterTypes.length;
		}

		public Type getParameterType (int index)
		{
			return parameterTypes[index];
		}

		private ObjectList<Annotation>[] parameterAnnotations;

		public int getParameterAnnotationCount (int param)
		{
			if (parameterAnnotations == null)
			{
				return 0;
			}
			if (parameterAnnotations[param] == null)
			{
				return 0;
			}
			return parameterAnnotations[param].size ();
		}

		public Annotation getParameterAnnotation (int param, int index)
		{
			return parameterAnnotations[param].get (index);
		}

		public AnnotationVisitor visitAnnotation (String desc, boolean visible)
		{
			if (annots == null)
			{
				annots = new ObjectList<Annotation> ();
			}
			return ((ASMType) getDeclaringType ()).new AnnVisitor (desc, annots, null, null);
		}

		public AnnotationVisitor visitAnnotationDefault ()
		{
			return ((ASMType) getDeclaringType ()).new AnnVisitor (null, null, null, getSimpleName ());
		}

		public AnnotationVisitor visitParameterAnnotation (int parameter, String desc, boolean visible)
		{
			if (parameterAnnotations == null)
			{
				parameterAnnotations = new ObjectList[parameterTypes.length];
			}
			if (parameterAnnotations[parameter] == null)
			{
				parameterAnnotations[parameter] = new ObjectList<Annotation> (1);
			}
			return ((ASMType) getDeclaringType ()).new AnnVisitor (desc, parameterAnnotations[parameter], null, null);
		}

		public void visitEnd ()
		{
		}

		public void visitAttribute (Attribute attr)
		{
		}

		public void visitCode ()
		{
		}

		public void visitFieldInsn (int opcode, String owner, String name, String desc)
		{
		}

		public void visitFrame (int type, int nLocal, Object[] local, int nStack, Object[] stack)
		{
		}

		public void visitIincInsn (int var, int increment)
		{
		}

		public void visitInsn (int opcode)
		{
		}

		public void visitIntInsn (int opcode, int operand)
		{
		}

		public void visitJumpInsn (int opcode, Label label)
		{
		}

		public void visitLabel (Label label)
		{
		}

		public void visitLdcInsn (Object cst)
		{
		}

		public void visitLineNumber (int line, Label start)
		{
		}

		public void visitLocalVariable (String name, String desc, String signature, Label start, Label end, int index)
		{
		}

		public void visitLookupSwitchInsn (Label dflt, int[] keys, Label[] labels)
		{
		}

		public void visitMaxs (int maxStack, int maxLocals)
		{
		}

		public void visitMethodInsn (int opcode, String owner, String name, String desc)
		{
		}

		public void visitMultiANewArrayInsn (String desc, int dims)
		{
		}

		public void visitTableSwitchInsn (int min, int max, Label dflt, Label[] labels)
		{
		}

		public void visitTryCatchBlock (Label start, Label end, Label handler, String type)
		{
		}

		public void visitTypeInsn (int opcode, String desc)
		{
		}

		public void visitVarInsn (int opcode, int var)
		{
		}
	}
	
	
	private ObjectList<Type> ptypes = new ObjectList<Type> ();

	public MethodVisitor visitMethod
		(int access, String name, String desc, String signature, String[] exceptions)
	{
		ptypes.clear ();
		int p = 1;
		while (desc.charAt (p) != ')')
		{
			ptypes.add (getType (desc, p));
			p = nextOffset;
		}
		ASMMethod m = new ASMMethod (name, null, access & JAVA_MODIFIERS, this,
									getType (desc, p + 1),
									ptypes.toArray (new Type[ptypes.size ()]),
									getTypes (exceptions));
		methods.add (m);
		return m;
	}


	public void visitEnd ()
	{
		if (varArgs != null)
		{
			while (!varArgs.isEmpty ())
			{
				ASMMethod m = (ASMMethod)
					Reflection.getDeclaredMethod (this, (String) varArgs.pop ());
				if (m != null)
				{
					m.setVarArgs ();
				}
			}
		}
	}
	
	
	@Override
	public String toString ()
	{
		return getName ();
	}
}
