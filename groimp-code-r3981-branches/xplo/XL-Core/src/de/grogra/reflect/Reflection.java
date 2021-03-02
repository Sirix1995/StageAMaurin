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

import java.lang.reflect.Modifier;

public final class Reflection
{
	private Reflection ()
	{
	}

	public static int getJVMTypeId (Type type)
	{
		int m = 1 << type.getTypeId ();
		if ((m & TypeId.I_VALUE) != 0)
		{
			return TypeId.INT;
		}
		else if ((m & TypeId.L_VALUE) != 0)
		{
			return TypeId.LONG;
		}
		else if ((m & TypeId.F_VALUE) != 0)
		{
			return TypeId.FLOAT;
		}
		else if ((m & TypeId.D_VALUE) != 0)
		{
			return TypeId.DOUBLE;
		}
		else if ((m & TypeId.A_VALUE) != 0)
		{
			return TypeId.OBJECT;
		}
		throw new AssertionError (m);
	}

	public static char getJVMPrefix (int typeId)
	{
		typeId = 1 << typeId;
		if ((typeId & TypeId.VOID_MASK) != 0)
		{
			return 'v';
		}
		else if ((typeId & TypeId.I_VALUE) != 0)
		{
			return 'i';
		}
		else if ((typeId & TypeId.L_VALUE) != 0)
		{
			return 'l';
		}
		else if ((typeId & TypeId.F_VALUE) != 0)
		{
			return 'f';
		}
		else if ((typeId & TypeId.D_VALUE) != 0)
		{
			return 'd';
		}
		else if ((typeId & TypeId.A_VALUE) != 0)
		{
			return 'a';
		}
		throw new AssertionError (typeId);
	}

	public static char getJVMPrefix (Type type)
	{
		return getJVMPrefix (type.getTypeId ());
	}

	/**
	 * Returns the number of stack elements which are needed for
	 * the storage of values of the given <code>type</code> within
	 * the Java virtual machine.
	 * 
	 * @param type a type
	 * @return the required size in terms of stack elements
	 */
	public static int getJVMStackSize (Type type)
	{
		return hasCategory2 (type.getTypeId ()) ? 2 : 1;
	}

	public static int getJVMStackSize (Type[] types)
	{
		int s = 0;
		for (int i = types.length - 1; i >= 0; i--)
		{
			s += getJVMStackSize (types[i]);
		}
		return s;
	}

	public static int getDimensionCount (Type type)
	{
		int n = 0;
		while (isArray (type))
		{
			n++;
			type = type.getComponentType ();
		}
		return n;
	}

	public static Field[] getDeclaredFields (Type type)
	{
		int n = type.getDeclaredFieldCount ();
		if (n == 0)
		{
			return Field.FIELD_0;
		}
		Field[] m = new Field[n];
		for (int i = n - 1; i >= 0; i--)
		{
			m[i] = type.getDeclaredField (i);
		}
		return m;
	}

	public static Method[] getDeclaredMethods (Type type)
	{
		int n = type.getDeclaredMethodCount ();
		if (n == 0)
		{
			return Method.METHOD_0;
		}
		Method[] m = new Method[n];
		for (int i = n - 1; i >= 0; i--)
		{
			m[i] = type.getDeclaredMethod (i);
		}
		return m;
	}

	public static Type[] getDeclaredInterfaces (Type type)
	{
		int n = type.getDeclaredInterfaceCount ();
		if (n == 0)
		{
			return Type.TYPE_0;
		}
		Type[] t = new Type[n];
		for (int i = n - 1; i >= 0; i--)
		{
			t[i] = type.getDeclaredInterface (i);
		}
		return t;
	}

	public static Type[] getDeclaredTypes (Type type)
	{
		int n = type.getDeclaredTypeCount ();
		if (n == 0)
		{
			return Type.TYPE_0;
		}
		Type[] t = new Type[n];
		for (int i = n - 1; i >= 0; i--)
		{
			t[i] = type.getDeclaredType (i);
		}
		return t;
	}

	public static Type[] getExceptions (Method method)
	{
		int n = method.getExceptionCount ();
		if (n == 0)
		{
			return Type.TYPE_0;
		}
		Type[] t = new Type[n];
		for (int i = n - 1; i >= 0; i--)
		{
			t[i] = method.getExceptionType (i);
		}
		return t;
	}

	public static Type[] getParameterTypes (Signature m)
	{
		int n = m.getParameterCount ();
		if (n == 0)
		{
			return Type.TYPE_0;
		}
		Type[] a = new Type[n];
		for (int i = n - 1; i >= 0; i--)
		{
			a[i] = m.getParameterType (i);
		}
		return a;
	}


	public static Type getParameterType (Signature m, int index, boolean varArity)
	{
		int n;
		return (varArity && (index >= (n = m.getParameterCount () - 1)))
			? m.getParameterType (n).getComponentType ()
			: m.getParameterType (index);
	}


	public static boolean membersEqual (Member m1, Member m2,
			boolean ignoreDeclaringType)
	{
		return (m1 == m2)
			|| ((m1 != null) && (m2 != null)
				&& m1.getSimpleName ().equals (m2.getSimpleName ())
				&& (m1.getDescriptor () != null)
				&& m1.getDescriptor ().equals (m2.getDescriptor ()) && (ignoreDeclaringType || equal (
				m1.getDeclaringType (), m2.getDeclaringType ())));
	}

	public static Member find (Member[] members, String nameOrDescriptor)
	{
		if (members == null)
		{
			return null;
		}
		for (int i = members.length - 1; i >= 0; i--)
		{
			if (members[i].getDescriptor ().equals (nameOrDescriptor)
				|| members[i].getSimpleName ().equals (nameOrDescriptor))
			{
				return members[i];
			}
		}
		return null;
	}

	public static String getSyntheticName (Member[] members, String name)
	{
		int i = 0;
		String n;
		do
		{
			n = name + '$' + i++;
		}
		while (find (members, n) != null);
		return n;
	}

	public static String modifiersToString (int mods)
	{
		int m = mods & Member.ACCESS_MODIFIERS;
		StringBuffer b = (m != 0) ? new StringBuffer (Modifier.toString (m))
			.append (' ') : new StringBuffer ();
		m = mods & Member.JAVA_MODIFIERS & ~Member.ACCESS_MODIFIERS;
		if (m != 0)
		{
			b.append (Modifier.toString (m));
		}
		else if (b.length () > 0)
		{
			b.setLength (b.length () - 1);
		}
		return b.toString ();
	}

	public static boolean isStatic (Member m)
	{
		return (m.getModifiers () & Member.STATIC) != 0;
	}

	public static boolean isFinal (Member m)
	{
		return (m.getModifiers () & Member.FINAL) != 0;
	}

	public static boolean isPrivate (Member m)
	{
		return (m.getModifiers () & Member.PRIVATE) != 0;
	}

	public static boolean isProtected (Member m)
	{
		return (m.getModifiers () & Member.PROTECTED) != 0;
	}

	public static boolean isPublic (Member m)
	{
		return (m.getModifiers () & Member.PUBLIC) != 0;
	}

	public static boolean hasDefaultAccess (Member m)
	{
		return (m.getModifiers () & Member.ACCESS_MODIFIERS) == 0;
	}

	public static boolean isAbstract (Member m)
	{
		return (m.getModifiers () & Member.ABSTRACT) != 0;
	}

	public static boolean isInner (Type m)
	{
		return (m.getDeclaringType () != null) && ((m.getModifiers () & Member.STATIC) == 0);
	}

	public static boolean isMoreVisible (int mods1, int mods2)
	{
		return ((mods1 & Member.PUBLIC) != 0) ? ((mods2 & Member.PUBLIC) == 0)
				: ((mods1 & Member.PROTECTED) != 0) ? ((mods2 & (Member.PUBLIC | Member.PROTECTED)) == 0)
						: (((mods1 & Member.PRIVATE) == 0) && ((mods2 & Member.PRIVATE) != 0));
	}

	public static boolean equal (Signature s1, Signature s2)
	{
		int n1 = s1.getParameterCount ();
		if (n1 != s2.getParameterCount ())
		{
			return false;
		}
		for (int i = n1 - 1; i >= 0; i--)
		{
			if (!equal (s1.getParameterType (i), s2.getParameterType (i)))
			{
				return false;
			}
		}
		return true;
	}

	public static String getDescription (Member m)
	{
		if (m instanceof Signature)
		{
			StringBuffer b = new StringBuffer ("<init>".equals (m
				.getSimpleName ()) ? m.getDeclaringType ().getSimpleName () : m
				.getName ()).append ('(');
			Signature sig = (Signature) m;
			for (int i = 0; i < sig.getParameterCount (); i++)
			{
				if (i > 0)
				{
					b.append (',');
				}
				Type pt = sig.getParameterType (i);
				if ((i == sig.getParameterCount () - 1)
					&& (m instanceof Method)
					&& ((((Method) m).getModifiers () & Member.VARARGS) != 0))
				{
					b.append (pt.getComponentType ().getSimpleName ())
						.append ("...");
				}
				else
				{
					b.append (pt.getSimpleName ());
				}
			}
			b.append (')');
			if ((m instanceof Method) && !"<init>".equals (m.getSimpleName ()))
			{
				b.insert (0, '.').insert (0,
					m.getDeclaringType ().getSimpleName ());
			}
			return b.toString ();
		}
		else
		{
			return m.getName ();
		}
	}

	public static String getMethodDescriptor (String name, Type returnType,
			Type[] parameterTypes)
	{
		return 'm' + name + ";(" + getDescriptor (parameterTypes) + ')'
			+ returnType.getDescriptor ();
	}

	public static String getFieldDescriptor (String name, Type type)
	{
		return 'f' + name + ';' + type.getDescriptor ();
	}

	public static Object get (Object object, Field field)
			throws IllegalAccessException
	{
		if (field == null)
		{
			return object;
		}
		switch (field.getType ().getTypeId ())
		{
			/*!!
			 #foreach ($type in $types)
			 $pp.setType($type)
			 case TypeId.$pp.TYPE:
			 return $pp.wrap("field.get$pp.Type (object)");
			 #end
			 !!*/
//!! #* Start of generated code
			 			 
			 case TypeId.BOOLEAN:
			 return ((field.getBoolean (object)) ? Boolean.TRUE : Boolean.FALSE);
			 			 
			 case TypeId.BYTE:
			 return Byte.valueOf (field.getByte (object));
			 			 
			 case TypeId.SHORT:
			 return Short.valueOf (field.getShort (object));
			 			 
			 case TypeId.CHAR:
			 return Character.valueOf (field.getChar (object));
			 			 
			 case TypeId.INT:
			 return Integer.valueOf (field.getInt (object));
			 			 
			 case TypeId.LONG:
			 return Long.valueOf (field.getLong (object));
			 			 
			 case TypeId.FLOAT:
			 return Float.valueOf (field.getFloat (object));
			 			 
			 case TypeId.DOUBLE:
			 return Double.valueOf (field.getDouble (object));
			 			 
			 case TypeId.OBJECT:
			 return (field.getObject (object));
//!! *# End of generated code
		}
		throw new AssertionError ();
	}

	public static void set (Object object, Field field, Object value)
			throws IllegalAccessException
	{
		switch (field.getType ().getTypeId ())
		{
			/*!!
			 #foreach ($type in $types)
			 $pp.setType($type)
			 case TypeId.$pp.TYPE:
			 field.set$pp.Type (object, $pp.unwrap("value"));
			 return;
			 #end
			 !!*/
//!! #* Start of generated code
			 			 
			 case TypeId.BOOLEAN:
			 field.setBoolean (object, (((Boolean) (value)).booleanValue ()));
			 return;
			 			 
			 case TypeId.BYTE:
			 field.setByte (object, (((Number) (value)).byteValue ()));
			 return;
			 			 
			 case TypeId.SHORT:
			 field.setShort (object, (((Number) (value)).shortValue ()));
			 return;
			 			 
			 case TypeId.CHAR:
			 field.setChar (object, (((Character) (value)).charValue ()));
			 return;
			 			 
			 case TypeId.INT:
			 field.setInt (object, (((Number) (value)).intValue ()));
			 return;
			 			 
			 case TypeId.LONG:
			 field.setLong (object, (((Number) (value)).longValue ()));
			 return;
			 			 
			 case TypeId.FLOAT:
			 field.setFloat (object, (((Number) (value)).floatValue ()));
			 return;
			 			 
			 case TypeId.DOUBLE:
			 field.setDouble (object, (((Number) (value)).doubleValue ()));
			 return;
			 			 
			 case TypeId.OBJECT:
			 field.setObject (object, (value));
			 return;
//!! *# End of generated code
		}
		throw new AssertionError ();
	}

	public static String toString (Field field)
	{
		return field.getClass ().getName () + '[' + field.getType () + ' '
			+ field.getDeclaringType () + '.' + field.getName () + ']';
	}

	public static String getDescriptor (Type[] types)
	{
		StringBuffer b = new StringBuffer (types.length * 20);
		for (int i = 0; i < types.length; i++)
		{
			b.append (types[i].getDescriptor ());
		}
		return b.toString ();
	}

	public static Type getTopLevelType (Type type)
	{
		while (type.getDeclaringType () != null)
		{
			type = type.getDeclaringType ();
		}
		return type;
	}

	/**
	 * Returns <code>true</code> iff <code>member</code> is a member
	 * of <code>type</code>. This is the case if <code>member</code>
	 * is declared in <code>type</code> or one of its supertypes.
	 * 
	 * @param member a member
	 * @param type a type
	 * @return <code>true</code> iff <code>member</code> is a member
	 * of <code>type</code>
	 */
	public static boolean isMember (Member member, Type type)
	{
		return isSupertypeOrSame (member.getDeclaringType (), type);
	}

	/**
	 * Returns the innermost of the enclosing types of <code>inner</code>
	 * of which <code>member</code> is a member. May be <code>inner</code>
	 * itself. Returns <code>null</code> if no such type can be found.
	 * 
	 * @param inner a type
	 * @param member a member
	 * @return innermost enclosing type of <code>inner</code>
	 * which has <code>member</code> as member
	 */
	public static Type getEnclosingType (Type inner, Member member)
	{
		while ((inner != null) && !isMember (member, inner))
		{
			inner = inner.getDeclaringType ();
		}
		return inner;
	}

	public static Type getDeclaredType (Type type, String nameOrDescriptor)
	{
		for (int i = type.getDeclaredTypeCount () - 1; i >= 0; i--)
		{
			Type t = type.getDeclaredType (i);
			if (t.getDescriptor ().equals (nameOrDescriptor)
				|| t.getSimpleName ().equals (nameOrDescriptor))
			{
				return t;
			}
		}
		return null;
	}

	public static boolean overlaps (FieldChain a, int[] aindices, FieldChain b,
			int[] bindices)
	{
		return (a == null) || (b == null) || a.overlaps (aindices, b, bindices);
	}

	public static Field getDeclaredField (Type type, String nameOrDescriptor)
	{
		for (int i = type.getDeclaredFieldCount () - 1; i >= 0; i--)
		{
			Field f = type.getDeclaredField (i);
			if (f.getDescriptor ().equals (nameOrDescriptor)
				|| f.getSimpleName ().equals (nameOrDescriptor))
			{
				return f;
			}
		}
		return null;
	}

	public static Method getDeclaredMethod (Type type, String nameOrDescriptor)
	{
		for (int i = type.getDeclaredMethodCount () - 1; i >= 0; i--)
		{
			Method m = type.getDeclaredMethod (i);
			if (m.getDescriptor ().equals (nameOrDescriptor)
				|| m.getSimpleName ().equals (nameOrDescriptor))
			{
				return m;
			}
		}
		return null;
	}

	public static Method getElementMethod (Type type, String element)
	{
		for (int i = type.getDeclaredMethodCount () - 1; i >= 0; i--)
		{
			Method m = type.getDeclaredMethod (i);
			if (m.getSimpleName ().equals (element)
				&& (m.getParameterCount () == 0))
			{
				return m;
			}
		}
		return null;
	}

	public static Method getMostSpecificPublicConstructor (Type type,
			Object value)
	{
		Method msc = null;
		Type mst = null;
		for (int i = type.getDeclaredMethodCount () - 1; i >= 0; i--)
		{
			Method c = type.getDeclaredMethod (i);
			if ("<init>".equals (c.getName ())
				&& ((c.getModifiers () & Member.PUBLIC) != 0))
			{
				if (c.getParameterCount () == 1)
				{
					Type t = c.getParameterType (0);
					if (t.isInstance (value))
					{
						if ((mst == null) || isAssignableFrom (mst, t))
						{
							mst = t;
							msc = c;
						}
					}
				}
			}
		}
		return msc;
	}

	public static Method getDefaultConstructor (Type type)
	{
		return getDeclaredMethod (type, getMethodDescriptor ("<init>",
			Type.VOID, Type.TYPE_0));
	}

	public static Method findMethodInClasses (Type type, String name)
	{
		while (type != null)
		{
			de.grogra.xl.util.XHashMap.Entry e = type.getLookup ().getMethods (
				name);
			if (e != null)
			{
				return (Method) e.getValue ();
			}
			type = type.getSupertype ();
		}
		return null;
	}

	public static Method findMethodWithPrefixInTypes (Type type,
			String descriptorPrefix, boolean includeInterfaces,
			boolean publicOnly)
	{
		while (type != null)
		{
			for (int i = type.getDeclaredMethodCount () - 1; i >= 0; i--)
			{
				Method m = type.getDeclaredMethod (i);
				if ((!publicOnly || isPublic (m))
					&& m.getDescriptor ().startsWith (descriptorPrefix))
				{
					return m;
				}
			}
			if (includeInterfaces)
			{
				for (int i = type.getDeclaredInterfaceCount () - 1; i >= 0; i--)
				{
					Method m = findMethodWithPrefixInTypes (type
						.getDeclaredInterface (i), descriptorPrefix, true,
						publicOnly);
					if (m != null)
					{
						return m;
					}
				}
			}
			type = type.getSupertype ();
		}
		return null;
	}

	public static Field findFieldInClasses (Type type, String nameOrDescriptor)
	{
		while (type != null)
		{
			Field f = getDeclaredField (type, nameOrDescriptor);
			if (f != null)
			{
				return f;
			}
			type = type.getSupertype ();
		}
		return null;
	}

	public static <T extends java.lang.annotation.Annotation> Annotation<T> getDeclaredAnnotation (Member member, Class<T> cls)
	{
		for (int i = member.getDeclaredAnnotationCount () - 1; i >= 0; i--)
		{
			Annotation a = member.getDeclaredAnnotation (i);
			if (Reflection.equal (cls, a.annotationType ()))
			{
				return a;
			}
		}
		return null;
	}

	public static Type findFirst (Type[] types, Type type)
	{
		for (int i = 0; i < types.length; i++)
		{
			if (isAssignableFrom (type, types[i]))
			{
				return types[i];
			}
		}
		return null;
	}

	public static Type getType (int typeId)
	{
		switch (typeId)
		{
			case TypeId.BOOLEAN:
				return Type.BOOLEAN;
			case TypeId.BYTE:
				return Type.BYTE;
			case TypeId.SHORT:
				return Type.SHORT;
			case TypeId.CHAR:
				return Type.CHAR;
			case TypeId.INT:
				return Type.INT;
			case TypeId.LONG:
				return Type.LONG;
			case TypeId.FLOAT:
				return Type.FLOAT;
			case TypeId.DOUBLE:
				return Type.DOUBLE;
			case TypeId.OBJECT:
				return Type.OBJECT;
			case TypeId.VOID:
				return Type.VOID;
			default:
				throw new IllegalArgumentException ("typeId = " + typeId);
		}
	}

	public final static Type getType (Object object)
	{
		Type t;
		return ((object instanceof XObject) && ((t = ((XObject) object)
			.getXClass ()) != null)) ? t : ClassAdapter.wrap (object
			.getClass ());
	}

	public static String getTypeName (int typeId)
	{
		return getType (typeId).getName ();
	}

	public static String getTypeSuffix (int typeId)
	{
		switch (typeId)
		{
			case TypeId.BOOLEAN:
				return "Boolean";
			case TypeId.BYTE:
				return "Byte";
			case TypeId.SHORT:
				return "Short";
			case TypeId.CHAR:
				return "Char";
			case TypeId.INT:
				return "Int";
			case TypeId.LONG:
				return "Long";
			case TypeId.FLOAT:
				return "Float";
			case TypeId.DOUBLE:
				return "Double";
			case TypeId.OBJECT:
				return "Object";
			case TypeId.VOID:
				return "Void";
			default:
				throw new IllegalArgumentException ("typeId = " + typeId);
		}
	}

	public static boolean isSuperclassOrSame (Type sup, Type type)
	{
		return (sup != null) && (type != null)
			&& (isPrimitive (sup) == isPrimitive (type))
			&& isSuperclassOrSame (sup.getBinaryName (), type);
	}

	public static boolean isSuperclassOrSame (Class sup, Type type)
	{
		return (sup != null) && (type != null)
			&& (sup.isPrimitive () == isPrimitive (type))
			&& isSuperclassOrSame (sup.getName (), type);
	}

	public static boolean isSupertypeOrSame (Type sup, Type type)
	{
		return (sup != null) && (type != null)
			&& (isPrimitive (sup) == isPrimitive (type))
			&& isSupertypeOrSame (sup.getBinaryName (), type);
	}

	public static boolean isSupertypeOrSame (Class sup, Type type)
	{
		return (sup != null) && (type != null)
			&& (sup.isPrimitive () == isPrimitive (type))
			&& isSupertypeOrSame (sup.getName (), type);
	}

	public static boolean isSuperclassOrSame (String sup, Type type)
	{
		if (sup == null)
		{
			return false;
		}
		while (type != null)
		{
			if (sup.equals (type.getBinaryName ()))
			{
				return true;
			}
			type = type.getSupertype ();
		}
		return false;
	}

	public static boolean isSupertypeOrSame (String sup, Type type)
	{
		if (sup == null)
		{
			return false;
		}
		while (type != null)
		{
			if (sup.equals (type.getBinaryName ()))
			{
				return true;
			}
			for (int i = type.getDeclaredInterfaceCount () - 1; i >= 0; i--)
			{
				if (isSupertypeOrSame (sup, type.getDeclaredInterface (i)))
				{
					return true;
				}
			}
			type = type.getSupertype ();
		}
		return false;
	}

	public static boolean isSupertype (Type sup, Type type)
	{
		return !equal (sup, type) && isSupertypeOrSame (sup, type);
	}

	public static boolean isAssignableFrom (Type target, Type source)
	{
		return (equal (source, Type.NULL) && (target.getTypeId () == TypeId.OBJECT))
			|| equal (source, target)
			|| ((isArray (target) && isArray (source)) ? isAssignableFrom (
				target.getComponentType (), source.getComponentType ())
					: isSupertypeOrSame (target, source));
	}

	public static boolean incompatibleReturnTypesExist (Type t1, Type t2)
	{
		return false;
	}

	public static Type<?> getBinaryType (Type<?> type)
	{
		return (type instanceof IntersectionType) ? ((IntersectionType) type)
			.getBinaryType () : type;
	}

	public static boolean isCastableFrom (Type target, Type source)
	{
		if (target instanceof IntersectionType)
		{
			target = target.getSupertype ();
		}
		if (source instanceof IntersectionType)
		{
			source = source.getSupertype ();
		}
		int m = (1 << source.getTypeId ()) | (1 << target.getTypeId ());
		if ((m & TypeId.VOID_MASK) != 0)
		{
			return false;
		}
		if (equal (source, target) || ((m & ~TypeId.NUMERIC_MASK) == 0))
		{
			return true;
		}
		if (m != TypeId.OBJECT_MASK)
		{
			return false;
		}
		if (equal (source, Type.NULL))
		{
			return true;
		}
		if (isArray (source))
		{
			if (isArray (target))
			{
				source = source.getComponentType ();
				target = target.getComponentType ();
				return equal (source, target)
					|| ((source.getTypeId () == TypeId.OBJECT)
						&& (target.getTypeId () == TypeId.OBJECT) && isCastableFrom (
						target, source));
			}
			else
			{
				return isAssignableFrom (target, source);
			}
		}
		else if ((source.getModifiers () & Member.INTERFACE) != 0)
		{
			if (isArray (target))
			{
				return isAssignableFrom (source, target);
			}
			else if ((target.getModifiers () & Member.INTERFACE) != 0)
			{
				return !incompatibleReturnTypesExist (source, target);
			}
			else
			{
				return ((target.getModifiers () & Member.FINAL) == 0)
					|| isAssignableFrom (source, target);
			}
		}
		else
		{
			if (isArray (target))
			{
				return equal (source, Type.OBJECT);
			}
			else if ((target.getModifiers () & Member.INTERFACE) != 0)
			{
				return ((source.getModifiers () & Member.FINAL) == 0)
					|| isAssignableFrom (target, source);
			}
			else
			{
				return isAssignableFrom (source, target)
					|| isAssignableFrom (target, source);
			}
		}
	}

	public static boolean equal (Type t1, Type t2)
	{
		return (t1 == t2)
			|| ((t1 != null) && (t2 != null) && ((t1.getTypeId () == t2
				.getTypeId ()) && (t1.getBinaryName ().equals (t2
				.getBinaryName ()))));
	}

	public static boolean equal (Class t1, Type t2)
	{
		return (t1 != null) && (t2 != null)
			&& ((t2.getTypeId () != TypeId.OBJECT) == t1.isPrimitive ())
			&& t2.getBinaryName ().equals (t1.getName ());
	}

	public static String toString (Type t)
	{
		if (isArray (t))
		{
			StringBuffer b = new StringBuffer ();
			while (isArray (t))
			{
				b.append ("[]");
				t = t.getComponentType ();
			}
			return b.insert (0, t.getName ()).toString ();
		}
		else
		{
			return t.getName ();
		}
	}

	public static boolean isWideningConversion (int src, int dest)
	{
		if (src == dest)
		{
			return true;
		}
		int sm = 1 << src, dm = 1 << dest;
		if ((sm & TypeId.PRIMITIVE_MASK) != 0)
		{
			if (((sm | dm) & ~TypeId.NUMERIC_MASK) == 0)
			{
				if ((dest != TypeId.CHAR) && (dest > src))
				{
					return true;
				}
			}
		}
		else if ((sm | dm) == TypeId.OBJECT_MASK)
		{
			return true;
		}
		return false;
	}

	public static boolean isWideningConversion (Type src, Type dest)
	{
		if (equal (src, dest))
		{
			return true;
		}
		else if ((src.getTypeId () == TypeId.OBJECT)
			&& (dest.getTypeId () == TypeId.OBJECT))
		{
			return isAssignableFrom (dest, src);
		}
		else
		{
			return isWideningConversion (src.getTypeId (), dest.getTypeId ());
		}
	}

	public static Object toType (Object v, Type t)
	{
		if (t.isInstance (v) || (v == null))
		{
			return v;
		}
		if (t instanceof Conversion)
		{
			return ((Conversion) t).valueOf (v);
		}
		throw new ClassCastException ("Can't convert " + v + " to " + t);
	}

	public static Type getPublicType (Type[] types)
	{
		for (int i = types.length - 1; i >= 0; i--)
		{
			if (isPublic (types[i]))
			{
				return types[i];
			}
		}
		return null;
	}

	public static Type getType (String type, ClassLoader loader)
	{
		if (type == null)
		{
			return Type.OBJECT;
		}
		if (type.indexOf ('.') < 0)
		{
			if ("null".equals (type))
			{
				return Type.NULL;
			}
			/*!!
			 #foreach ($type in $primitives)
			 $pp.setType($type)
			 else if ("$type".equals (type))
			 {
			 return Type.$pp.TYPE;
			 }
			 #end
			 !!*/
//!! #* Start of generated code
			 			 
			 else if ("boolean".equals (type))
			 {
			 return Type.BOOLEAN;
			 }
			 			 
			 else if ("byte".equals (type))
			 {
			 return Type.BYTE;
			 }
			 			 
			 else if ("short".equals (type))
			 {
			 return Type.SHORT;
			 }
			 			 
			 else if ("char".equals (type))
			 {
			 return Type.CHAR;
			 }
			 			 
			 else if ("int".equals (type))
			 {
			 return Type.INT;
			 }
			 			 
			 else if ("long".equals (type))
			 {
			 return Type.LONG;
			 }
			 			 
			 else if ("float".equals (type))
			 {
			 return Type.FLOAT;
			 }
			 			 
			 else if ("double".equals (type))
			 {
			 return Type.DOUBLE;
			 }
//!! *# End of generated code
		}

		Class c;
		try
		{
			c = Class.forName (type, true, loader);
		}
		catch (ClassNotFoundException e)
		{
			int i = type.lastIndexOf ('.');
			if (i > 0)
			{
				try
				{
					return (Type) Class.forName (type.substring (0, i), true,
						loader).getField (type.substring (i + 1)).get (null);
				}
				catch (ClassNotFoundException f)
				{
					throw new AssertionError (f);
				}
				catch (NoSuchFieldException f)
				{
					throw new AssertionError (f);
				}
				catch (IllegalAccessException f)
				{
					throw new AssertionError (f);
				}
			}
			throw new AssertionError (e);
		}
		try
		{
			Object o = c.getDeclaredField ("$TYPE").get (null);
			if (o instanceof Type)
			{
				return (Type) o;
			}
		}
		catch (NoSuchFieldException e)
		{
		}
		catch (IllegalAccessException e)
		{
		}
		return ClassAdapter.wrap (c);
	}

	public static Type getType (Class cls)
	{
		try
		{
			Object o = cls.getDeclaredField ("$TYPE").get (null);
			if (o instanceof Type)
			{
				return (Type) o;
			}
		}
		catch (Exception e)
		{
		}
		return ClassAdapter.wrap (cls);
	}

	public static boolean isIntegral (Type type)
	{
		return ((1 << type.getTypeId ()) & TypeId.INTEGRAL_MASK) != 0;
	}

	public static boolean isPrimitive (Type type)
	{
		return ((1 << type.getTypeId ()) & TypeId.PRIMITIVE_MASK) != 0;
	}

	public static boolean isInterface (Type type)
	{
		return (type.getModifiers () & Member.INTERFACE) != 0;
	}

	public static boolean isArray (Type type)
	{
		return (type.getModifiers () & Member.ARRAY) != 0;
	}

	/**
	 * Returns true if the method m is a constructor, otherwise returns false.
	 * 
	 * @param m a method
	 * @return <code>true</code> iff <code>m</code> is a constructor
	 */
	public static boolean isCtor (Method m)
	{
		return "<init>".equals (m.getSimpleName ());
	}

	public static boolean isPrimitiveOrVoid (Type type)
	{
		return ((1 << type.getTypeId ()) & (TypeId.PRIMITIVE_MASK | TypeId.VOID_MASK)) != 0;
	}

	public static boolean isPrimitiveOrString (Type type)
	{
		return (((1 << type.getTypeId ()) & TypeId.PRIMITIVE_MASK) != 0)
			|| equal (type, Type.STRING);
	}

	public static boolean hasCategory2 (int typeId)
	{
		return ((1 << typeId) & (TypeId.L_VALUE | TypeId.D_VALUE)) != 0;
	}

	public static Class getWrapperClass (int typeId)
	{
		switch (typeId)
		{
			/*!!
			 #foreach ($type in $types_void)
			 $pp.setType($type)
			 case TypeId.$pp.TYPE:
			 return ${pp.Wrapper}.class;
			 #end
			 !!*/
//!! #* Start of generated code
			 			 
			 case TypeId.BOOLEAN:
			 return Boolean.class;
			 			 
			 case TypeId.BYTE:
			 return Byte.class;
			 			 
			 case TypeId.SHORT:
			 return Short.class;
			 			 
			 case TypeId.CHAR:
			 return Character.class;
			 			 
			 case TypeId.INT:
			 return Integer.class;
			 			 
			 case TypeId.LONG:
			 return Long.class;
			 			 
			 case TypeId.FLOAT:
			 return Float.class;
			 			 
			 case TypeId.DOUBLE:
			 return Double.class;
			 			 
			 case TypeId.OBJECT:
			 return Object.class;
			 			 
			 case TypeId.VOID:
			 return Void.class;
//!! *# End of generated code
			default:
				throw new AssertionError (typeId);
		}
	}

	/**
	 * If the input type is one of the wrapper classes (Boolean,
	 * Byte, Short, Integer, Long, Float, Double, Character),
	 * return the matching unwrapped type (boolean, byte, short,
	 * int, long, float, double, char), otherwise return
	 * Type.INVALID instead.
	 * @param type
	 * @return unwrapped type
	 */
	public static Type getUnwrappedType (Type type)
	{
		Type result = Type.INVALID;
		/*!!
		 #foreach ($type in $primitives)
		 $pp.setType($type)
		 if (Reflection.getWrapperClass(TypeId.${pp.TYPE}).getName().equals(type.getBinaryName()))
		 {
		 result = Type.${pp.TYPE};
		 }
		 else
		 #end
		 !!*/
//!! #* Start of generated code
		 		 
		 if (Reflection.getWrapperClass(TypeId.BOOLEAN).getName().equals(type.getBinaryName()))
		 {
		 result = Type.BOOLEAN;
		 }
		 else
		 		 
		 if (Reflection.getWrapperClass(TypeId.BYTE).getName().equals(type.getBinaryName()))
		 {
		 result = Type.BYTE;
		 }
		 else
		 		 
		 if (Reflection.getWrapperClass(TypeId.SHORT).getName().equals(type.getBinaryName()))
		 {
		 result = Type.SHORT;
		 }
		 else
		 		 
		 if (Reflection.getWrapperClass(TypeId.CHAR).getName().equals(type.getBinaryName()))
		 {
		 result = Type.CHAR;
		 }
		 else
		 		 
		 if (Reflection.getWrapperClass(TypeId.INT).getName().equals(type.getBinaryName()))
		 {
		 result = Type.INT;
		 }
		 else
		 		 
		 if (Reflection.getWrapperClass(TypeId.LONG).getName().equals(type.getBinaryName()))
		 {
		 result = Type.LONG;
		 }
		 else
		 		 
		 if (Reflection.getWrapperClass(TypeId.FLOAT).getName().equals(type.getBinaryName()))
		 {
		 result = Type.FLOAT;
		 }
		 else
		 		 
		 if (Reflection.getWrapperClass(TypeId.DOUBLE).getName().equals(type.getBinaryName()))
		 {
		 result = Type.DOUBLE;
		 }
		 else
//!! *# End of generated code
		{
			// leave result as Type.INVALID
		}
		return result;
	}

	/**
	 * Determines if member <code>m</code> is accessible on
	 * <code>instance</code> in the context <code>scope</code>.
	 * <code>instance</code> is only needed for instance methods
	 * and fields and denotes the instance on which these members
	 * are accessed. <code>scope</code> defines the type in whose
	 * declaration the access of <code>m</code> occurs.
	 * <br>
	 * NOTE: This method does not check if the declaring type of
	 * <code>m</code> is accessible.
	 * 
	 * @param m a member
	 * @param instance instance for instance methods and fields  
	 * @param scope type in whose declaration <code>m</code> is accessed
	 * @return <code>true</code> iff the access is permitted
	 */
	public static boolean isAccessible (Member m, Type instance, Type scope)
	{
		int mods = m.getModifiers ();
		if ((mods & Member.PUBLIC) != 0)
		{
			return true;
		}

		Type mtype = (m instanceof Type) ? (Type) m : m.getDeclaringType ();
		if ((mods & Member.PRIVATE) != 0)
		{
			// private access: top level types have to be the same
			return equal (getTopLevelType (scope), getTopLevelType (mtype));
		}

		// the remaining cases are protected access or default access

		if (mtype.getPackage ().equals (scope.getPackage ()))
		{
			// if the packages are equal, access is allowed
			return true;
		}

		// the packages differ, default access is not permitted
		if ((mods & Member.PROTECTED) == 0)
		{
			return false;
		}

		// now we have protected access with differing packages

		if (((mods & Member.STATIC) != 0)
			|| !((m instanceof Field) || (m instanceof Method)))
		{
			// clear instance if it is not needed. Thus, instance!=null
			// is true iff the access is an access on an instance
			instance = null;
		}

		// find an enclosing type e which has m as member
		for (Type e = scope; e != null; e = e.getDeclaringType ())
		{
			if (isSuperclassOrSame (m.getDeclaringType (), e)
			// now e has m as member
				&& ((instance == null)
				// for instance methods and fields, e has to be a
				// superclass of the instance's class on which the access
				// occurs (or this class itself)
				|| isSuperclassOrSame (e, instance)))
			{
				return true;
			}
		}
		return false;
	}

	
	public static boolean canLoad (ClassLoader loader, Class cls)
	{
		try
		{
			return Class.forName (cls.getName (), false, loader) == cls;
		}
		catch (Throwable e)
		{
			return false;
		}
	}

	
	public static boolean isAncestorOrSame (ClassLoader a, ClassLoader b)
	{
		while (b != null)
		{
			if (a == b)
			{
				return true;
			}
			b = b.getParent ();
		}
		return false;
	}

	public static boolean isInvalid (Type type)
	{
		// compare using simple name only: usage of binary name would trigger resolution
		// within de.grogra.xl.compiler.UnresolvedType
		return (type == null) || Type.INVALID.getSimpleName ().equals (type.getSimpleName ());
	}

}
