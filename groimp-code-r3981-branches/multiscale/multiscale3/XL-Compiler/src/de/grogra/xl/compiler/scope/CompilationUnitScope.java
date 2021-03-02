
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

import java.util.HashMap;

import antlr.collections.AST;
import de.grogra.reflect.Annotation;
import de.grogra.reflect.Member;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.util.StringMap;
import de.grogra.xl.compiler.CClass;
import de.grogra.xl.compiler.Compiler;
import de.grogra.xl.util.ObjectList;

public final class CompilationUnitScope extends Scope implements Member
{
	public HashMap<AST,Object> properties = new HashMap<AST,Object> ();

	private final StringMap types = new StringMap (32, true);
	private final ObjectList<CClass> localClasses = new ObjectList<CClass> ();
	private final String source;
	private final Compiler compiler;

	private Annotation[] annotations;


	public static CompilationUnitScope get (Scope scope)
	{
		while (!(scope instanceof CompilationUnitScope))
		{
			if (scope == null)
			{
				return null;
			}
			scope = scope.getEnclosingScope ();
		}
		return (CompilationUnitScope) scope;
	}


	public CompilationUnitScope (Scope enclosing, String source, Compiler compiler)
	{
		super (enclosing);
		this.source = source;
		this.compiler = compiler;
	}

	@Override
	public Compiler getCompiler ()
	{
		return compiler;
	}
	
	public String getSource ()
	{
		return source;
	}

	@Override
	public void findMembers (String name, int flags, Members list)
	{
		if ((flags & (Members.TYPE | Members.PREDICATE)) != 0)
		{
			TypeScope.addTypeOrPatterns
				((Type) types.get (name), list, this, flags);
		}
		super.findMembers (name, flags, list);
	}


	public void declareType (CClass type)
	{
		types.put (type.getSimpleName (), type);
		getPackage ().declareType (type);
	}

	
	public void declareLocalClass (CClass type)
	{
		localClasses.add (type);
	}


	public Type[] getDeclaredTypes ()
	{
		Type[] t = new Type[types.size ()];
		for (int i = t.length - 1; i >= 0; i--)
		{
			t[i] = (Type) types.getValueAt (i);
		}
		return t;
	}

	
	public Type[] getLocalClasses ()
	{
		return localClasses.toArray (new Type[localClasses.size ()]);
	}


	public Type getDeclaredPublicType ()
	{
		return Reflection.getPublicType (getDeclaredTypes ());
	}

	public void setAnnotations (Annotation[] annotations)
	{
		this.annotations = annotations;
	}

	public Annotation getDeclaredAnnotation (int index)
	{
		return annotations[index];
	}

	public int getDeclaredAnnotationCount ()
	{
		return (annotations != null) ? annotations.length : 0;
	}

	public void dispose ()
	{
		properties.clear ();
		properties = null;
		for (int i = types.size () - 1; i >= 0; i--)
		{
			((CClass) types.getValueAt (i)).dispose ();
		}
		for (int i = localClasses.size () - 1; i >= 0; i--)
		{
			localClasses.get (i).dispose ();
		}
	}


	public Member getDeclaredEntity ()
	{
		return this;
	}

	public Type getDeclaringType ()
	{
		return null;
	}

	public String getDescriptor ()
	{
		return 'u' + source + ';';
	}

	public int getModifiers ()
	{
		return PUBLIC;
	}

	public String getName ()
	{
		return source;
	}

	public String getSimpleName ()
	{
		return source;
	}

}
