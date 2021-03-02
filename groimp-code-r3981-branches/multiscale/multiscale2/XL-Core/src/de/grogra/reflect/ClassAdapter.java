
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

package de.grogra.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import de.grogra.xl.util.ObjectList;

public final class ClassAdapter<T> extends TypeImpl<T>
{
	private static final HashMap<String,ClassAdapter> globalPool = new HashMap<String,ClassAdapter> (200);

	static
	{
		if (Type.TYPE_0 == null)
		{
			throw new AssertionError ();
		}
	}
	
	
	public interface ClassLoaderWithPool
	{
		Map<String,ClassAdapter> getClassAdapterPool ();
	}
	
	
	public static class URLClassLoaderWithPool extends URLClassLoader
		implements ClassLoaderWithPool
	{
		private final HashMap<String,ClassAdapter> pool = new HashMap<String,ClassAdapter> ();

		public URLClassLoaderWithPool (URL[] urls, ClassLoader parent)
		{
			super (urls, parent);
		}
		
		
		public Map<String,ClassAdapter> getClassAdapterPool ()
		{
			return pool;
		}
	}


	final Class<T> cls;
	final boolean pooled;

	private Field[] declaredFields = null;
	private Method[] declaredMethods = null;
	private Type[] interfaces = null, declaredTypes = null;
	private Object array0 = null;

	static final ObjectList<Annotation> NO_ANNOTATIONS = new ObjectList<Annotation> ();

	private abstract class MemberAdapter implements Member
	{
		final java.lang.reflect.Member member;
		ObjectList<Annotation> memberAnnots;

		MemberAdapter (java.lang.reflect.Member member)
		{
			this.member = member;
		}


		public Type getDeclaringType ()
		{
			return ClassAdapter.this;
		}


		public int getModifiers ()
		{
			return member.getModifiers () & JAVA_MODIFIERS;
		}


		public String getName ()
		{
			return member.getName ();
		}


		public String getSimpleName ()
		{
			return member.getName ();
		}
		
		
		@Override
		public String toString ()
		{
			return member.toString ();
		}


		public synchronized int getDeclaredAnnotationCount ()
		{
			if (memberAnnots == null)
			{
				memberAnnots = getAnnotations (((AccessibleObject) member).getDeclaredAnnotations ());
			}
			return memberAnnots.size ();
		}


		public Annotation getDeclaredAnnotation (int index)
		{
			getDeclaredAnnotationCount ();
			return memberAnnots.get (index);
		}

	}

	private static class AnnotationAdapter<A extends java.lang.annotation.Annotation> implements Annotation<A>
	{
		final A annot;
		final ClassAdapter<A> type;

		AnnotationAdapter (A annot)
		{
			this.annot = annot;
			type = wrap ((Class<A>) annot.annotationType (), false);
		}

		public Type<A> annotationType ()
		{
			return type;
		}

		public Object value (String element)
		{
			Method m = Reflection.getElementMethod (type, element);
			if (m == null)
			{
				throw new NoSuchMethodError (type + " " + element);
			}
			try
			{
				return convert (m.invoke (annot, null));
			}
			catch (Exception e)
			{
				IllegalAccessError f = new IllegalAccessError ();
				f.initCause (e);
				throw f;
			}
		}
		
		private static Object convert (Object v)
		{
			if (v instanceof Class)
			{
				return wrap ((Class) v);
			}
			else if (v instanceof java.lang.annotation.Annotation)
			{
				return new AnnotationAdapter<java.lang.annotation.Annotation> ((java.lang.annotation.Annotation) v);
			}
			else if (v instanceof Enum)
			{
				return ((Enum) v).name ();
			}
			else if (v instanceof Object[])
			{
				int len = ((Object[]) v).length;
				Object[] a;
				if (v instanceof Class[])
				{
					a = new Type[len];
				}
				else if (v instanceof java.lang.annotation.Annotation)
				{
					a = new Annotation[len];
				}
				else if (v instanceof Enum[])
				{
					a = new String[len];
				}
				else
				{
					return v;
				}
				for (int i = 0; i < len; i++)
				{
					a[i] = convert (((Object[]) v)[i]);
				}
				return a;
			}
			else
			{
				return v;
			}
		}
	}

	static ObjectList<Annotation> getAnnotations (java.lang.annotation.Annotation[] a)
	{
		ObjectList<Annotation> list = null;
		for (int i = 0; i < a.length; i++)
		{
			if (list == null)
			{
				list = new ObjectList<Annotation> ();
			}
			list.add (new AnnotationAdapter (a[i]));
		}
		if (list != null)
		{
			list.trimToSize ();
			return list;
		}
		return NO_ANNOTATIONS;
	}

	private final class FieldAdapter extends MemberAdapter implements Field
	{
		private final String descriptor;
		private final java.lang.reflect.Field field;
		private final Type type;


		FieldAdapter (java.lang.reflect.Field field)
		{
			super (field);
			this.field = field;
			type = create0 (field.getType ());
			descriptor = Reflection.getFieldDescriptor (field.getName (), type);
		}


		public String getDescriptor ()
		{
			return descriptor;
		}


		public Type getType ()
		{
			return type;
		}

/*!!
#foreach ($type in $types)
$pp.setType($type)

		public $type get$pp.Type (Object object) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
#if ($pp.Object)
			return field.get (object);
#else
			return field.get$pp.Type (object);
#end
		}


		public void set$pp.Type (Object object, $type value) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
#if ($pp.Object)
			field.set (object, value);
#else
			field.set$pp.Type (object, value);
#end
		}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
		public boolean getBoolean (Object object) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			return field.getBoolean (object);
		}
// generated
// generated
		public void setBoolean (Object object, boolean value) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			field.setBoolean (object, value);
		}
// generated
// generated
// generated
		public byte getByte (Object object) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			return field.getByte (object);
		}
// generated
// generated
		public void setByte (Object object, byte value) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			field.setByte (object, value);
		}
// generated
// generated
// generated
		public short getShort (Object object) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			return field.getShort (object);
		}
// generated
// generated
		public void setShort (Object object, short value) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			field.setShort (object, value);
		}
// generated
// generated
// generated
		public char getChar (Object object) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			return field.getChar (object);
		}
// generated
// generated
		public void setChar (Object object, char value) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			field.setChar (object, value);
		}
// generated
// generated
// generated
		public int getInt (Object object) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			return field.getInt (object);
		}
// generated
// generated
		public void setInt (Object object, int value) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			field.setInt (object, value);
		}
// generated
// generated
// generated
		public long getLong (Object object) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			return field.getLong (object);
		}
// generated
// generated
		public void setLong (Object object, long value) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			field.setLong (object, value);
		}
// generated
// generated
// generated
		public float getFloat (Object object) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			return field.getFloat (object);
		}
// generated
// generated
		public void setFloat (Object object, float value) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			field.setFloat (object, value);
		}
// generated
// generated
// generated
		public double getDouble (Object object) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			return field.getDouble (object);
		}
// generated
// generated
		public void setDouble (Object object, double value) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			field.setDouble (object, value);
		}
// generated
// generated
// generated
		public Object getObject (Object object) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			return field.get (object);
		}
// generated
// generated
		public void setObject (Object object, Object value) throws IllegalAccessException
		{
			if ((object != null) && !cls.isInstance (object))
			{
				throw new ClassCastException (cls.getName ());
			}
			field.set (object, value);
		}
// generated
//!! *# End of generated code

		@Override
		public String toString ()
		{
			return Reflection.toString (this);
		}

	}


	private abstract class InvokableAdapter extends MemberAdapter implements Method
	{
		final Type[] parameterTypes;
		final Type[] exceptionTypes;

		ObjectList<Annotation>[] paramAnnots;

		InvokableAdapter (java.lang.reflect.Member invokable,
						  Class[] parameterTypes, Class[] exceptionTypes)
		{
			super (invokable);
			this.parameterTypes = wrap (parameterTypes, pooled);
			this.exceptionTypes = wrap (exceptionTypes, pooled);
		}


		public int getParameterCount ()
		{
			return parameterTypes.length;
		}


		public Type getParameterType (int index)
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

		abstract java.lang.annotation.Annotation[][] getParameterAnnotations ();

		public synchronized int getParameterAnnotationCount (int param)
		{
			if (paramAnnots == null)
			{
				java.lang.annotation.Annotation[][] pa = getParameterAnnotations ();
				paramAnnots = new ObjectList[pa.length];
				for (int i = 0; i < pa.length; i++)
				{
					paramAnnots[i] = getAnnotations (pa[i]);
				}
			}
			return paramAnnots[param].size ();
		}

		public Annotation getParameterAnnotation (int param, int index)
		{
			getParameterAnnotationCount (param);
			return paramAnnots[param].get (index);
		}

	}


	private final class MethodAdapter extends InvokableAdapter
	{
		private final Type returnType;
		private final String descriptor;


		MethodAdapter (java.lang.reflect.Method method)
		{
			super (method, method.getParameterTypes (),
				   method.getExceptionTypes ());
			returnType = create0 (method.getReturnType ());
			descriptor = Reflection.getMethodDescriptor
				(method.getName (), returnType, parameterTypes);
		}


		public String getDescriptor ()
		{
			return descriptor;
		}


		public Type getReturnType ()
		{
			return returnType;
		}


		public Object invoke (Object instance, Object[] arguments)
			throws InvocationTargetException
		{
			try
			{
				return ((java.lang.reflect.Method) member)
					.invoke (instance, arguments);
			}
			catch (IllegalAccessException e)
			{
				throw new InvocationTargetException (e);
			}
		}

		@Override
		java.lang.annotation.Annotation[][] getParameterAnnotations ()
		{
			return ((java.lang.reflect.Method) member).getParameterAnnotations ();
		}

	}


	private final class ConstructorAdapter extends InvokableAdapter
	{
		private final String descriptor;


		ConstructorAdapter (java.lang.reflect.Constructor constructor)
		{
			super (constructor, constructor.getParameterTypes (),
				   constructor.getExceptionTypes ());
			descriptor = Reflection.getMethodDescriptor ("<init>", VOID, parameterTypes);
		}


		public String getDescriptor ()
		{
			return descriptor;
		}


		@Override
		public String getSimpleName ()
		{
			return "<init>";
		}
		

		public Type getReturnType ()
		{
			return ClassAdapter.this;
		}


		public Object invoke (Object instance, Object[] arguments)
			throws InvocationTargetException
		{
			try
			{
				return ((java.lang.reflect.Constructor) member)
					.newInstance (arguments);
			}
			catch (IllegalAccessException e)
			{
				throw new InvocationTargetException (e);
			}
			catch (InstantiationException e)
			{
				throw new InvocationTargetException (e);
			}
		}

		@Override
		java.lang.annotation.Annotation[][] getParameterAnnotations ()
		{
			return ((java.lang.reflect.Constructor) member).getParameterAnnotations ();
		}
	}


	private static <T> Type<? super T> supertype (Class<T> cls, boolean pooled)
	{
		if (cls.isInterface ())
		{
			return Type.OBJECT;
		}
		if (cls.isPrimitive ())
		{
			if ((cls == Byte.TYPE) || (cls == Short.TYPE)
				|| (cls == Integer.TYPE) || (cls == Long.TYPE)
				|| (cls == Character.TYPE))
			{
				return (Type<? super T>) Type.INTEGRAL;
			}
			if ((cls == Float.TYPE) || (cls == Double.TYPE))
			{
				return (Type<? super T>) Type.FLOATING_POINT;
			}
			return null;
		}
		return (cls.getSuperclass () == null) ? null
			: wrap (cls.getSuperclass (), pooled);
	}


	private ClassAdapter (Class<T> cls, int typeId, boolean pooled)
	{
		super (typeId, cls.isArray () ? getDescriptor (cls) : cls.getName (),
			   getDescriptor (cls),
			   (cls.getModifiers () & JAVA_MODIFIERS)
			   | (cls.isArray () ? ARRAY : 0),
			   (cls.getDeclaringClass () == null) ? null
			   : wrap (cls.getDeclaringClass (), pooled),
			   supertype (cls, pooled),
			   cls.isArray () ? wrap (cls.getComponentType (), pooled) : null);
		this.pooled = pooled;
		this.cls = cls;
	}


	public static synchronized <T> ClassAdapter<T> wrap (Class<T> cls)
	{
		ClassLoader l = cls.getClassLoader ();
		return create (cls, (l instanceof ClassLoaderWithPool)
			? ((ClassLoaderWithPool) l).getClassAdapterPool () : globalPool);
	}


	public static <T> ClassAdapter<T> wrap (Class<T> cls, boolean pooled)
	{
		return pooled ? wrap (cls) : create (cls, null);
	}

	
	<T2> ClassAdapter<T2> create0 (Class<T2> cls)
	{
		return pooled ? wrap (cls) : create (cls, null);
	}


	private static <T> ClassAdapter<T> create (Class<T> cls, Map<String,ClassAdapter> pool)
	{
		String n = cls.getName ();
		if (cls.isPrimitive ())
		{
			n += ' ';
		}
		ClassAdapter cw = (pool != null) ? (ClassAdapter) pool.get (n) : null;
		if (cw == null)
		{
			int id;
			if (cls.isPrimitive ())
			{
				switch (n.charAt (0))
				{
					case 'v':
						id = TypeId.VOID;
						break;
					case 'b':
						id = (n.charAt (1) == 'o') ? TypeId.BOOLEAN : TypeId.BYTE;
						break;
					case 's':
						id = TypeId.SHORT;
						break;
					case 'c':
						id = TypeId.CHAR;
						break;
					case 'i':
						id = TypeId.INT;
						break;
					case 'l':
						id = TypeId.LONG;
						break;
					case 'f':
						id = TypeId.FLOAT;
						break;
					case 'd':
						id = TypeId.DOUBLE;
						break;
					default:
						throw new AssertionError ();
				}
			}
			else
			{
				id = TypeId.OBJECT;
			}
			cw = new ClassAdapter (cls, id, pool != null);
			if (pool != null)
			{
				pool.put (n, cw);
			}
		}
		return cw;
	}


	public static Type[] wrap (Class[] classes, boolean pooled)
	{
		Type[] a = new Type[classes.length];
		for (int i = classes.length - 1; i >= 0; i--)
		{
			a[i] = wrap (classes[i], pooled);
		}
		return a;
	}


	public static String getDescriptor (Class cls)
	{
		if (cls.isArray ())
		{
			return '[' + getDescriptor (cls.getComponentType ());
		}
		else if (cls.isPrimitive ())
		{
			if (cls == boolean.class)
			{
				return "Z";
			}
			else if (cls == long.class)
			{
				return "J";
			}
			else
			{
				return String.valueOf
					(Character.toUpperCase (cls.getName ().charAt (0)));
			}
		}
		else
		{
			return 'L' + cls.getName ().replace ('.', '/') + ';';
		}
	}


	@Override
	public boolean isInstance (Object object)
	{
		return cls.isInstance (object);
	}


	@Override
	public Class<T> getImplementationClass ()
	{
		return cls;
	}


	private synchronized Field[] getDeclaredFields ()
	{
		if (declaredFields == null)
		{
			if (cls.isArray ())
			{
				declaredFields = new Field[] {new LengthField (this)};
			}
			else
			{
				java.lang.reflect.Field[] f = cls.getDeclaredFields ();
				declaredFields = new Field[f.length];
				for (int i = f.length - 1; i >= 0; i--)
				{
					declaredFields[i] = new FieldAdapter (f[i]);
				}
			}
		}
		return declaredFields;
	}


	@Override
	public int getDeclaredFieldCount ()
	{
		return getDeclaredFields ().length;
	}


	@Override
	public Field getDeclaredField (int index)
	{
		return getDeclaredFields ()[index];
	}


	private synchronized Method[] getDeclaredMethods ()
	{
		if (declaredMethods == null)
		{
			if (cls.isArray ())
			{
				declaredMethods = new Method[] {new CloneMethod (this)};
			}
			else
			{
				java.lang.reflect.Method[] m = cls.getDeclaredMethods ();
				java.lang.reflect.Constructor[] c = cls.getDeclaredConstructors ();
				declaredMethods = new Method[m.length + c.length];
				for (int i = m.length - 1; i >= 0; i--)
				{
					declaredMethods[i] = new MethodAdapter (m[i]);
				}
				for (int i = c.length - 1; i >= 0; i--)
				{
					declaredMethods[m.length + i] = new ConstructorAdapter (c[i]);
				}
			}
		}
		return declaredMethods;
	}


	@Override
	public int getDeclaredMethodCount ()
	{
		return getDeclaredMethods ().length;
	}


	@Override
	public Method getDeclaredMethod (int index)
	{
		return getDeclaredMethods ()[index];
	}


	private static final Type[] ARRAY_INTERFACES
		= {wrap (Cloneable.class), wrap (java.io.Serializable.class)};


	private synchronized Type[] getInterfaces ()
	{
		if (interfaces == null)
		{
			if (cls.isArray ())
			{
				interfaces = ARRAY_INTERFACES;
			}
			else
			{
				Class[] c = cls.getInterfaces ();
				interfaces = new Type[c.length];
				for (int i = c.length - 1; i >= 0; i--)
				{
					interfaces[i] = create0 (c[i]);
				}
			}
		}
		return interfaces;
	}


	@Override
	public int getDeclaredInterfaceCount ()
	{
		return getInterfaces ().length;
	}


	@Override
	public Type<?> getDeclaredInterface (int index)
	{
		return getInterfaces ()[index];
	}


	private synchronized Type[] getDeclaredTypes ()
	{
		if (declaredTypes == null)
		{
			Class[] c;
			int length;
			try
			{
				c = cls.getDeclaredClasses ();
				length = c.length;
			}
			catch (SecurityException e)
			{
				c = cls.getClasses ();
				length = 0;
				for (int i = c.length; i > 0; i--)
				{
					if (c[length].getDeclaringClass () == cls)
					{
						length++;
					}
					else
					{
						System.arraycopy (c, length + 1, c, length, i - 1);
					}
				}
			}
			declaredTypes = new Type[length];
			for (int i = length - 1; i >= 0; i--)
			{
				declaredTypes[i] = create0 (c[i]);
			}
		}
		return declaredTypes;
	}


	@Override
	public int getDeclaredTypeCount ()
	{
		return getDeclaredTypes ().length;
	}


	@Override
	public Type<?> getDeclaredType (int index)
	{
		return getDeclaredTypes ()[index];
	}


	@Override
	public Type<?> getArrayType ()
	{
		return create0 (Array.newInstance (cls, 0).getClass ());
	}


	@Override
	public synchronized int getDeclaredAnnotationCount ()
	{
		if (annots == null)
		{
			annots = getAnnotations (cls.getDeclaredAnnotations ());
		}
		return super.getDeclaredAnnotationCount ();
	}


	@Override
	public Annotation getDeclaredAnnotation (int index)
	{
		getDeclaredAnnotationCount ();
		return super.getDeclaredAnnotation (index);
	}


	@Override
	public Object createArray (int length)
	{
		if (cls.isArray ())
		{
			if (length == 0)
			{
				if (array0 == null)
				{
					array0 = Array.newInstance (cls.getComponentType (), 0);
				}
				return array0;
			}
			return Array.newInstance (cls.getComponentType (), length);
		}
		else
		{
			throw new UnsupportedOperationException (this + " is not an array class");
		}
	}

	
	@Override
	public T newInstance () throws InvocationTargetException,
		InstantiationException, IllegalAccessException
	{
		return cls.newInstance ();
	}

}
