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

package de.grogra.xl.compiler.scope;

import antlr.collections.AST;
import de.grogra.reflect.Annotation;
import de.grogra.reflect.Member;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.xl.compiler.CClass;
import de.grogra.xl.compiler.Compiler;
import de.grogra.xl.compiler.Extension;
import de.grogra.xl.compiler.InvalidQueryModel;
import de.grogra.xl.compiler.UseExtension;
import de.grogra.xl.lang.ConversionType;
import de.grogra.xl.lang.ImplicitDoubleToFloat;
import de.grogra.xl.lang.UseConversions;
import de.grogra.xl.modules.DefaultModuleSuperclass;
import de.grogra.xl.modules.InstantiationProducerType;
import de.grogra.xl.query.CompiletimeModel;
import de.grogra.xl.query.UseModel;

/**
 * <code>Scode</code> is the abstract base class for all scopes. As defined
 * by the Java language specification, scopes represent regions of
 * a programme in which {@link de.grogra.reflect.Member}s can be identified
 * by their simple name. Scopes are nested.
 *
 * @author Ole Kniemeyer
 */
public abstract class Scope
{

	private Scope enclosing;

	private CompiletimeModel queryModel;
	private boolean queryModelSet = false;
	
	private boolean d2FIsImplicit;
	private boolean d2FIsImplicitSet = false;

	private de.grogra.xl.property.CompiletimeModel propertyModel;
	private Type<?> propertyModelAnnotationType;
	private boolean propertyModelSet = false;

	private Extension extension;
	private boolean extensionSet;
	
	private boolean conversionSet;
	private String[] conversions;

	Scope (Scope enclosing)
	{
		this.enclosing = enclosing;
	}

	/**
	 * Inserts <code>enc</code> such that it becomes the new
	 * enclosing scope of this ecope. The enclosing scope of the outmost
	 * enclosing scope of <code>enc</code> is set to the previously
	 * enclosing scope of this scope.
	 * 
	 * @param enc the new enclosing scope of this. May be <code>null</code>.
	 */
	public void insert (Scope enc)
	{
		Scope s = this.enclosing;
		this.enclosing = enc;
		if (enc != null)
		{
			enc.getRoot ().enclosing = s;
		}
	}

	/**
	 * Returns the enclosing scope of this scope.
	 * 
	 * @return enclosing scope, or <code>null</code>
	 */
	public Scope getEnclosingScope ()
	{
		return enclosing;
	}

	/**
	 * Returns the outmost enclosing scope. This is the (directly or
	 * indirectly) enclosing scope of this scope which has no
	 * enclosing scope.
	 * 
	 * @return outmost enclosing scope
	 */
	public final Scope getRoot ()
	{
		Scope s = this;
		while (s.enclosing != null)
		{
			s = s.enclosing;
		}
		return s;
	}

	/**
	 * Checks if this scope encloses <code>s</code> directly or
	 * indirectly. Returns <code>false</code>
	 * if <code>s</code> is <code>null</code> or this scope.
	 * 
	 * @param s a scope
	 * @return <code>true</code> this scope encloses <code>s</code>
	 */
	public boolean encloses (Scope s)
	{
		while (s != null)
		{
			s = s.enclosing;
			if (s == this)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if declarations of this scope are shadowed by
	 * declarations of <code>s</code>. This is defined by the
	 * Java language specification.
	 * 
	 * @param s a scope
	 * @return <code>true</code> iff this scope is shadowed by <code>s</code>
	 */
	public final boolean isShadowedBy (Scope s)
	{
		if (this instanceof ImportOnDemandScope)
		{
			return !(s instanceof ImportOnDemandScope);
		}
		if (s instanceof ImportOnDemandScope)
		{
			return false;
		}
		if ((this instanceof InstanceScope)
			&& !((InstanceScope) this).shadowsEnclosing)
		{
			return !(s instanceof InstanceScope)
				|| ((InstanceScope) s).shadowsEnclosing || encloses (s);
		}
		if ((s instanceof InstanceScope)
			&& !((InstanceScope) s).shadowsEnclosing)
		{
			return false;
		}
		return encloses (s);
	}

	/**
	 * Returns the type of the innermost type declaration
	 * of which <code>m</code> is a member.
	 * 
	 * @param m a member
	 * @return innermost type of which <code>m</code> is a member
	 */
	public Type getOwnerOf (Member m)
	{
		return Reflection.getEnclosingType (getDeclaredType (), m);
	}

	/**
	 * Returns the type of the innermost enclosing type declaration.  
	 * 
	 * @return type of innermost enclosing type declaration
	 */
	public CClass getDeclaredType ()
	{
		return (enclosing == null) ? null : enclosing.getDeclaredType ();
	}


	public Member getDeclaredEntity ()
	{
		return null;
	}

	private <T extends java.lang.annotation.Annotation> Annotation<T> getAnnotation (Class<T> cls)
	{
		Member m = getDeclaredEntity ();
		if (m == null)
		{
			return null;
		}
		Compiler c = getCompiler ();
		return (c != null) ? c.getAnnotation (m, cls, false) : Reflection.getDeclaredAnnotation (m, cls);
	}

	/**
	 * Returns the compile-time model to use in this scope. This is the
	 * model of the innermost scope (potentially <code>this</code>) for which
	 * a non-<code>null</code> model has been set by {@link #setModel(Model)}.
	 * 
	 * @return compile-time model for this scope
	 */
	public CompiletimeModel getQueryModel (Compiler c, AST pos)
	{
		if (!queryModelSet)
		{
			Annotation<UseModel> a = getAnnotation (UseModel.class);
			if (a != null)
			{
				queryModel = c.createModel (CompiletimeModel.class, (Type<?>) a.value ("value"), pos);
				if (queryModel == null)
				{
					queryModel = InvalidQueryModel.INSTANCE;
				}
			}
			else if (enclosing != null)
			{
				queryModel = enclosing.getQueryModel (c, pos);
			}
			queryModelSet = true;
		}
		return queryModel;
	}

	/**
	 * Returns the compile-time model to use in this scope. This is the
	 * model of the innermost scope (potentially <code>this</code>) for which
	 * a non-<code>null</code> model has been set by {@link #setModel(Model)}.
	 * 
	 * @return compile-time model for this scope
	 */
	public de.grogra.xl.property.CompiletimeModel getPropertyModel (Type type, Compiler c, AST pos)
	{
		if (!propertyModelSet)
		{
			Annotation<de.grogra.xl.property.UseModel> a = getAnnotation (de.grogra.xl.property.UseModel.class);
			if (a != null)
			{
				propertyModelAnnotationType = (Type<?>) a.value ("type");
				propertyModel = c.createModel (de.grogra.xl.property.CompiletimeModel.class, (Type<?>) a.value ("model"), pos);
			}
			propertyModelSet = true;
		}
		if ((propertyModelAnnotationType != null) && Reflection.isSupertypeOrSame (propertyModelAnnotationType, type))
		{
			return propertyModel;
		}
		if (enclosing != null)
		{
			return enclosing.getPropertyModel (type, c, pos);
		}
		return null;
	}

	public Extension getExtension ()
	{
		if (!extensionSet)
		{
			Annotation<UseExtension> a = getAnnotation (UseExtension.class);
			if (a != null)
			{
				try
				{
					extension = ((Type<Extension>) a.value ("value")).newInstance ();
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}
			else if (enclosing != null)
			{
				extension = enclosing.getExtension ();
			}
			extensionSet = true;
		}
		return extension;
	}

	/**
	 * Returns the innermost enclosing scope of class <code>Package</code>,
	 * or <code>null</code>. May be <code>this</code>.
	 * 
	 * @return innermost <code>Package</code> scope
	 */
	public Package getPackage ()
	{
		return (enclosing == null) ? null : enclosing.getPackage ();
	}

	/**
	 * Checks if this scope appears in a static context. This is defined
	 * by the Java Language Specification.
	 * 
	 * @return <code>true</code> iff this scope appears in a static context
	 */
	public boolean isStatic ()
	{
		return enclosing.isStatic ();
	}

	/**
	 * Finds all members which are declared in this scope
	 * or enclosing scopes. This method
	 * should be overwritten by subclasses; an invocation of
	 * <code>super.findMembers</code> has to be included in order to
	 * look for members in enclosing scopes. The <code>flags</code> are
	 * a combination of the bit masks defined in {@link Members}, they
	 * are used to restrict the range of possible members.
	 * 
	 * @param name simple name of the members to find
	 * @param flags combination of masks defined in {@link Members}
	 * @param list found members are added to this list
	 */
	public void findMembers (String name, int flags, Members list)
	{
		if (enclosing != null)
		{
			enclosing.findMembers (name, flags, list);
		}
	}

	public boolean isD2FImplicit ()
	{
		if (!d2FIsImplicitSet)
		{
			Annotation<ImplicitDoubleToFloat> a = getAnnotation (ImplicitDoubleToFloat.class);
			if (a != null)
			{
				d2FIsImplicit = Boolean.TRUE.equals (a.value ("value"));
			}
			else if (enclosing != null)
			{
				d2FIsImplicit = enclosing.isD2FImplicit ();
			}
			d2FIsImplicitSet = true;
		}
		return d2FIsImplicit;
	}

	public boolean isEnabledConversion (ConversionType type)
	{
		if (!conversionSet)
		{
			Annotation<UseConversions> a = getAnnotation (UseConversions.class);
			if (a != null)
			{
				conversions = (String[]) a.value ("value");
			}
			else if (enclosing != null)
			{
				enclosing.isEnabledConversion (type);
				conversions = enclosing.conversions;
			}
			else
			{
				conversions = new String[] {
					ConversionType.VALUE_OF.name (),
					ConversionType.TO_TYPE.name (),
					ConversionType.TO_TYPE_IN_SCOPE.name (),
					ConversionType.CONVERSION_CONSTRUCTOR.name ()};
			}
			conversionSet = true;
		}
		for (int i = 0; i < conversions.length; i++)
		{
			if (conversions[i].equals (type.name ()))
			{
				return true;
			}
		}
		return false;
	}

	public Type<?> getDefaultModuleSuperclass ()
	{
		for (Scope s = this; s != null; s = s.enclosing)
		{
			Annotation<DefaultModuleSuperclass> a = s.getAnnotation (DefaultModuleSuperclass.class);
			if (a != null)
			{
				return (Type<?>) a.value ("value");
			}
		}
		return Type.OBJECT;
	}

	public Type<?> getInstantiationProducerType ()
	{
		for (Scope s = this; s != null; s = s.enclosing)
		{
			Annotation<InstantiationProducerType> a = s.getAnnotation (InstantiationProducerType.class);
			if (a != null)
			{
				return (Type<?>) a.value ("value");
			}
		}
		return null;
	}


	public Compiler getCompiler ()
	{
		return (enclosing != null) ? enclosing.getCompiler () : null;
	}

	public void dump ()
	{
		System.err.println (this);
		Scope s = this;
		while ((s = s.getEnclosingScope ()) != null)
		{
			System.err.print ("  ");
			System.err.println (s);
		}
	}
}
