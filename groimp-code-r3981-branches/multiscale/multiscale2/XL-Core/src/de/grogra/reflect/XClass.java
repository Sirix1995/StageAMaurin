
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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class XClass<T> extends XObjectImpl implements Type<T>
{
	private final String name, binaryName, descriptor, simpleName;
	private int modifiers;
	private final Type<?> declaringClass;
	private TypeLoader loader;

	private Type<? super T> superclass;
	private boolean superclassSet = false;
	
	private ArrayList<Annotation> declaredAnnotations = new ArrayList<Annotation> ();

	private final boolean metaClass, writable;
	private ArrayList<XField> declaredFields = new ArrayList<XField> ();
	private ArrayList<Method> declaredMethods = new ArrayList<Method> ();
	private ArrayList<Type<?>> declaredTypes = new ArrayList<Type<?>> ();
	private ArrayList<Type<?>> interfaces = new ArrayList<Type<?>> ();
	private boolean initialized;
	private int syntheticCount;

	int isize, lsize, fsize, dsize, asize;


	public XClass (String simpleName, String binaryName, int modifiers, Type<?> declaringClass,
				   boolean writable)
	{
		this.simpleName = simpleName;
		this.binaryName = binaryName;
		if (declaringClass != null)
		{
			name = declaringClass.getName () + '.' + simpleName;
		}
		else
		{
			name = binaryName;
		}
		this.descriptor = 'L' + binaryName.replace ('.', '/') + ';';
		this.modifiers = modifiers;
		this.declaringClass = declaringClass;
		metaClass = false;
		this.writable = writable;
	}
	
	
	public void initSupertype (Type<? super T> superclass)
	{
		if (superclassSet)
		{
			throw new IllegalStateException ();
		}
		superclassSet = true;
		this.superclass = superclass;
	}


	private XClass (XClass supertype)
	{
		initSupertype (supertype);
		name = null;
		binaryName = null;
		simpleName = null;
		descriptor = null;
		modifiers = 0;
		declaringClass = null;
		metaClass = true;
		writable = false;
	}

	
	public void initTypeLoader (TypeLoader loader)
	{
		this.loader = loader;
	}

	
	public synchronized void initialize ()
	{
		if (initialized)
		{
			return;
		}
		initialized = true;
		Type<? super T> st = getSupertype ();
		XClass<XClass> smeta;
//		if ((st != null) && !(st instanceof XClass))
//		{
//			st = st.getDeclaredMethod (0).getDeclaringType ();
//		}
		if (st instanceof XClass)
		{
			XClass<? super T> sc = (XClass<? super T>) st;
			sc.initialize ();
			isize = sc.isize;
			lsize = sc.lsize;
			fsize = sc.fsize;
			dsize = sc.dsize;
			asize = sc.asize;
			smeta = (XClass<XClass>) sc.getXClass ();
		}
		else
		{
			isize = 0;
			lsize = 0;
			fsize = 0;
			dsize = 0;
			asize = 0;
			smeta = null;
		}
		if (!metaClass)
		{
			XClass<XClass> meta = new XClass<XClass> (smeta);
			meta.initialize ();
			for (int i = declaredFields.size () - 1; i >= 0; i--)
			{
				XField f = declaredFields.get (i);
				XClass<?> d = Reflection.isStatic (f) ? meta : this;
				int index;
				switch (f.getType ().getTypeId ())
				{
					case TypeId.BOOLEAN:
					case TypeId.BYTE:
					case TypeId.SHORT:
					case TypeId.CHAR:
/*!!
#foreach ($type in $vmtypes)
$pp.setType($type)
					case TypeId.$pp.TYPE:
						index = d.${pp.prefix}size++;
						break;
#end
!!*/
//!! #* Start of generated code
// generated
					case TypeId.INT:
						index = d.isize++;
						break;
// generated
					case TypeId.LONG:
						index = d.lsize++;
						break;
// generated
					case TypeId.FLOAT:
						index = d.fsize++;
						break;
// generated
					case TypeId.DOUBLE:
						index = d.dsize++;
						break;
// generated
					case TypeId.OBJECT:
						index = d.asize++;
						break;
//!! *# End of generated code
					default:
						throw new AssertionError ();
				}
				f.index = index;
			}
			initXClass (meta);
		}
		Method m = Reflection.getDeclaredMethod (this, "<clinit>");
		if (m != null)
		{
			try
			{
				m.invoke (null, null);
			}
			catch (InvocationTargetException e)
			{
				throw new ExceptionInInitializerError (e.getCause ());
			}
			catch (IllegalAccessException e)
			{
				throw new ExceptionInInitializerError (e);
			}
		}
	}


	private void check (int modifiers)
	{
		if (initialized && ((modifiers & SYNTHETIC) == 0))
		{
			if (writable)
			{
				if ((modifiers & STATIC) == 0)
				{
					throw new IllegalStateException
						("Only static members can be modified after the "
						 + "initialization of class " + this);
				}
			}
			else
			{
				throw new IllegalStateException
					("Can't change the declaration of the non-writable class "
					 + this);
			}
		}
	}


	public XField declareSyntheticField (String baseName, int modifiers, Type<?> type)
	{
		return declareField (baseName + '$' + syntheticCount++, modifiers | SYNTHETIC, type);
	}

	
	public XField declareAuxField (String name, int modifiers, Type<?> type)
	{
		String orig = name;
		int i = 0;
		while (getDeclaredField (name) != null)
		{
			name = orig + ++i;
		}
		return declareField (name, modifiers | Member.SYNTHETIC, type);
	}


	public XField declareField (String name, int modifiers, Type<?> type)
	{
		check (modifiers);
		XField x = new XField (this, name, modifiers, type);
		if ((modifiers & STATIC) != 0)
		{
			if (initialized)
			{
				switch (type.getTypeId ())
				{
					case TypeId.BOOLEAN:
					case TypeId.BYTE:
					case TypeId.SHORT:
					case TypeId.CHAR:
/*!!
#foreach ($type in $vmtypes)
$pp.setType($type)
					case TypeId.$pp.TYPE:
						x.index = getXData ().add${pp.PREFIX}Field ();
						break;
#end
!!*/
//!! #* Start of generated code
// generated
					case TypeId.INT:
						x.index = getXData ().addIField ();
						break;
// generated
					case TypeId.LONG:
						x.index = getXData ().addLField ();
						break;
// generated
					case TypeId.FLOAT:
						x.index = getXData ().addFField ();
						break;
// generated
					case TypeId.DOUBLE:
						x.index = getXData ().addDField ();
						break;
// generated
					case TypeId.OBJECT:
						x.index = getXData ().addAField ();
						break;
//!! *# End of generated code
					default:
						throw new AssertionError ();
				}
			}
		}
		declaredFields.add (x);
		updateLookup ();
		return x;
	}
	

	public void removeField (Field field)
	{
		check (field.getModifiers ());
		declaredFields.remove (field);
		updateLookup ();
	}


	public void makeVisible (XField field)
	{
		check (field.getModifiers ());
		int k = declaredFields.size () - 1;
		for (int i = 0; i <= k; i++)
		{
			if (declaredFields.get (i) == field)
			{
				if (i != k)
				{
					declaredFields.remove (i);
					declaredFields.add (field);
					updateLookup ();
				}
				return;
			}
		}
		throw new RuntimeException ("Field " + field + " not found");
	}


	public void declareMethod (Method method)
	{
		check (method.getModifiers ());
		declaredMethods.add (method);
		updateLookup ();
	}


	public void removeMethod (Method method)
	{
		check (method.getModifiers ());
		declaredMethods.remove (method);
		updateLookup ();
	}


	public void declareType (XClass<?> type)
	{
		check (type.getModifiers ());
		declaredTypes.add (type);
		updateLookup ();
	}


	public void addInterface (Type<?> type)
	{
		check (0);
		interfaces.add (type);
		updateLookup ();
	}

	public List<Annotation> getDeclaredAnnotations ()
	{
		return declaredAnnotations;
	}

	public T newInstance () throws InvocationTargetException,
		InstantiationException, IllegalAccessException
	{
		Method c = Reflection.getDefaultConstructor (this);
		if (c == null)
		{
			throw new InstantiationException (getName ());
		}
		return (T) c.invoke (null, null);
	}


	public int getTypeId ()
	{
		return TypeId.OBJECT;
	}

	
	public String getPackage ()
	{
		String n = getBinaryName ();
		return n.substring (0, Math.max (0, n.lastIndexOf ('.')));
	}


	public int getModifiers ()
	{
		return modifiers;
	}
	
	
	public void setPublic ()
	{
		modifiers = (modifiers & ~ACCESS_MODIFIERS) | PUBLIC;
	}


	public Type<?> getDeclaringType ()
	{
		return declaringClass;
	}

	
	public boolean hasSupertypeBeenSet ()
	{
		return superclassSet;
	}


	public Type<? super T> getSupertype ()
	{
		if (!superclassSet)
		{
			return Type.OBJECT;
		}
		return superclass;
	}


	public String getName ()
	{
		return name;
	}


	public String getBinaryName ()
	{
		return binaryName;
	}


	public String getDescriptor ()
	{
		return descriptor;
	}


	public boolean isInstance (Object object)
	{
		return (object instanceof XObject)
			&& getImplementationClass ().isInstance (object)
			&& Reflection.isSupertypeOrSame (this,
											 ((XObject) object).getXClass ());
	}


	public Class<T> getImplementationClass ()
	{
		return (Class<T>) getSupertype ().getImplementationClass ();
	}


	public TypeLoader getTypeLoader ()
	{
		return loader;
	}


	public int getDeclaredInterfaceCount ()
	{
		return interfaces.size ();
	}

	
	public Type<?> getDeclaredInterface (int index)
	{
		return interfaces.get (index);
	}


	public int getDeclaredTypeCount ()
	{
		return declaredTypes.size ();
	}
	
	
	public Type<?> getDeclaredType (int index)
	{
		return declaredTypes.get (index);
	}


	public int getDeclaredFieldCount ()
	{
		return declaredFields.size ();
	}


	public Field getDeclaredField (int index)
	{
		return declaredFields.get (index);
	}


	public XField getDeclaredField (String name)
	{
		for (int i = declaredFields.size () - 1; i >= 0; i--)
		{
			if (declaredFields.get (i).getSimpleName ().equals (name))
			{
				return declaredFields.get (i);
			}
		}
		return null;
	}


	public int getDeclaredMethodCount ()
	{
		return declaredMethods.size ();
	}


	public Method getDeclaredMethod (int index)
	{
		return declaredMethods.get (index);
	}


	public int getDeclaredAnnotationCount ()
	{
		return declaredAnnotations.size ();
	}


	public Annotation getDeclaredAnnotation (int index)
	{
		return declaredAnnotations.get (index);
	}

	
	public String getSimpleName ()
	{
		return simpleName;
	}


	public Type<?> getComponentType ()
	{
		return null;
	}


	public Type<?> getArrayType ()
	{
		return new XArray (this);
	}


	public Object createArray (int length)
	{
		throw new AssertionError ();
	}


	@Override
	public String toString ()
	{
		return name;
	}


	public boolean isStringSerializable ()
	{
		return false;
	}


	public T valueOf (String s)
	{
		throw new UnsupportedOperationException ();
	}


	public T cloneObject (T o, boolean deep)
	{
		return o;
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

	
	protected final void updateLookup ()
	{
		if (lookup != null)
		{
			lookup.update ();
		}
	}

	public Object getDefaultElementValue (String name)
	{
		return null;//TODO;
	}

}
