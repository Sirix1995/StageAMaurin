
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
import de.grogra.reflect.Field;
import de.grogra.reflect.Lookup;
import de.grogra.reflect.Member;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.XField;
import de.grogra.xl.compiler.CClass;
import de.grogra.xl.compiler.Compiler;
import de.grogra.xl.compiler.InheritedField;
import de.grogra.xl.compiler.InheritedMethod;
import de.grogra.xl.compiler.ShiftedMethod;
import de.grogra.xl.compiler.pattern.PatternWrapper;
import de.grogra.xl.expr.Invoke;
import de.grogra.xl.query.UserDefinedPattern;
import de.grogra.xl.util.ObjectList;
import de.grogra.xl.util.XHashMap;

public final class TypeScope extends Scope
{
	final CClass type;

	public final MethodScope instanceInit;
	public final MethodScope staticInit;
	private final Compiler compiler;
	public final XField enclosingInstance;

	private long modifiersHi;
	
	private final HashMap astForFields = new HashMap ();

	private final ObjectList methodScopes = new ObjectList ();
	private final ObjectList constructorScopes = new ObjectList ();


	private final ObjectList constrInvocations = new ObjectList ();
	private final ObjectList<Local> enclosingLocals = new ObjectList<Local> ();
	private final HashMap fieldsForLocals = new HashMap ();
	
	private int nextId;

	private Field assertionsDisabled;
	
	private final AST node;


	public int nextLocalClassId ()
	{
		return nextId++;
	}


	public static TypeScope get (Scope scope)
	{
		while (!(scope instanceof TypeScope))
		{
			if (scope == null)
			{
				return null;
			}
			scope = scope.getEnclosingScope ();
		}
		return (TypeScope) scope;
	}


	public static TypeScope getNonlocal (Scope scope)
	{
		TypeScope ts = null;
		while (scope != null)
		{
			if (scope instanceof TypeScope)
			{
				if ((((TypeScope) scope).type.getModifiers ()
					 & Member.LOCAL_CLASS) != 0)
				{
					ts = null;
				}
				else if (ts == null)
				{
					ts = (TypeScope) scope;
				}
			}
			scope = scope.getEnclosingScope ();
		}
		return ts;
	}


	public TypeScope (Scope enclosing, CClass type, long modifiersHi,
					  Compiler compiler, AST node)
	{
		super (enclosing);
		this.node = node;
		this.compiler = compiler;
		this.modifiersHi = modifiersHi;
		this.type = type;
		staticInit = createMethodScope
			(Member.STATIC | Compiler.MOD_INITIALIZER);
		staticInit.createAndDeclareMethod ("<clinit>", Type.VOID);
		TypeScope e = TypeScope.get (enclosing);
		enclosingInstance = ((e != null) && !Reflection.isStatic (type))
			? type.declareField ("this$0", Member.FINAL | Member.SYNTHETIC,
								 e.getDeclaredType ())
			: null;
		if ((type.getModifiers () & Member.INTERFACE) == 0)
		{
			instanceInit = createMethodScope
				(Member.PRIVATE | Member.FINAL | Member.SYNTHETIC | Compiler.MOD_INITIALIZER);
			instanceInit.createAndDeclareMethod ("this", Type.VOID);
		}
		else
		{
			instanceInit = null;
		}
	}
	
	
	public AST getAST ()
	{
		return node;
	}

	
	@Override
	public Compiler getCompiler ()
	{
		return compiler;
	}

	
	public MethodScope createMethodScope (long modifiers)
	{
		MethodScope ms = new MethodScope (this, modifiers);
		if (ms.isConstructor ())
		{
			constructorScopes.add (ms);
		}
		TypeScope.getNonlocal (this).methodScopes.add (ms);
		return ms;
	}


	public long getModifiersEx ()
	{
		return type.getModifiers () | (modifiersHi & (-1l << 32));
	}

	
	public Field getAssertionsDisabledField ()
	{
		if (assertionsDisabled == null)
		{
			assertionsDisabled = type.declareAuxField
				("$assertionsDisabled", Member.STATIC | Member.FINAL, Type.BOOLEAN);
		}
		return assertionsDisabled;
	}

	
	public boolean hasAssertionsDisabledField ()
	{
		return assertionsDisabled != null;
	}
	
	
	public Field getFieldForEnclosingLocal (Local local)
	{
		Field f = (Field) fieldsForLocals.get (local);
		if (f == null)
		{
			assert !isNonlocal ();
			TypeScope enc = TypeScope.get (getEnclosingScope ());
			if (local.getScope ().encloses (enc))
			{
				f = enc.getFieldForEnclosingLocal (local);
			}
			else
			{
				f = getDeclaredType ().declareField ("val$" + local.getName (), Member.FINAL, local.getType ());
				fieldsForLocals.put (local, f);
				enclosingLocals.add (local);
			}
		}
		return f;
	}

	
	public ObjectList<Local> getEnclosingLocals ()
	{
		return enclosingLocals;
	}

	
	public ObjectList getConstructorScopes ()
	{
		return constructorScopes;
	}

	
	public ObjectList getAllContainedMethodScopes ()
	{
		return methodScopes;
	}

	
	public void setInstantiatorModule ()
	{
		modifiersHi |= Compiler.MOD_INSTANTIATOR;
	}


	@Override
	public CClass getDeclaredType ()
	{
		return type;
	}

	@Override
	public Member getDeclaredEntity ()
	{
		return type;
	}

	@Override
	public boolean isStatic ()
	{
		return enclosingInstance == null;
	}


	public boolean isNonlocal ()
	{
		return TypeScope.getNonlocal (this) == this;
	}
	
	
	public void addIncompleteConstructorInvocation (Invoke e)
	{
		constrInvocations.add (e);
	}
	
	
	public ObjectList getIncompleteConstructorInvocations ()
	{
		return constrInvocations;
	}

	
	public void setASTOfDeclaration (Field field, AST ast)
	{
		astForFields.put (field, ast);
	}
	
	
	public AST getASTOfDeclaration (Field field)
	{
		return (AST) astForFields.get (field);
	}


	@Override
	public void findMembers (String name, int flags, Members list)
	{
		findMembers (type, null, name, flags, list, this);
		super.findMembers (name, flags, list);
	}


	static void addTypeOrPatterns (Type type, Members list, Scope scope,
									 int flags)
	{
		if (type == null)
		{
			return;
		}
		if (!type.getPackage ().equals (list.getPackage ()))
		{
			flags |= Members.DIFFERENT_PACKAGE;
		}
		if ((flags & Members.TYPE) != 0)
		{
			list.add (type, scope, flags);
		}
		if ((flags & Members.PREDICATE) != 0)
		{
			addPattern (type, type, list, scope, flags);
			for (int i = type.getDeclaredTypeCount () - 1; i >= 0; i--)
			{
				addPattern (type.getDeclaredType (i), type, list, scope, flags);
			}
		}
	}


	private static void addPattern (Type predType, Type naming, Members list, Scope scope, int flags)
	{
		if (Reflection.isAbstract (predType)
			|| !Reflection.isSuperclassOrSame (UserDefinedPattern.class, predType))
		{
			return;
		}
		try
		{
			predType = ClassPath.get (list.getContextScope ()).typeForType (predType);
		}
		catch (ClassNotFoundException e)
		{
		}
		Method m = UserDefinedPattern.findSignatureMethod (predType);
		if (m == null)
		{
			return;
		}
		int[] inOut = new int[2];
		Type[] t = UserDefinedPattern.getSignature (m, inOut);
		if (t == null)
		{
			return;
		}
		list.add (new PatternWrapper (predType, naming, t, inOut[0], inOut[1]), scope, flags);
	}


	static void findMembers (Type type, Type qualifyingType, String name, int flags,
							 Members list, Scope scope)
	{
		if ((flags & Members.SUPER) != 0)
		{
			flags &= ~Members.SUPER;
			type = type.getSupertype ();
		}
		if (qualifyingType == null)
		{
			qualifyingType = type;
		}
		while (type != null)
		{
			if (!type.getPackage ().equals (list.getPackage ()))
			{
				flags |= Members.DIFFERENT_PACKAGE;
			}
			Lookup map = type.getLookup ();
			if ((flags & Members.FIELD) != 0)
			{
				Field f = map.getField (name);
				if (f != null)
				{
					if (type != qualifyingType)
					{
						f = new InheritedField (f, qualifyingType);
					}
					list.add (f, scope, flags);
				}
			}
			if ((flags & (Members.METHOD | Members.CONSTRUCTOR)) != 0)
			{
				for (XHashMap.Entry e = map.getMethods (name); e != null; e = e.next ())
				{
					Method m = (Method) e.getValue ();
					if (type != qualifyingType)
					{
						m = new InheritedMethod (m, qualifyingType);
					}
					if ((flags & Members.SHIFT_METHODS) != 0)
					{
						if (Reflection.isStatic (m))
						{
							continue;
						}
						m = new ShiftedMethod (m);
					}
					list.add (m, scope, flags);
				}
				if ((flags & Members.CONSTRUCTOR) != 0)
				{
					flags &= ~Members.CONSTRUCTOR;
				}
			}
			addTypeOrPatterns (map.getType (name), list, scope, flags);
			if ((flags & Members.DECLARED_ONLY) != 0)
			{
				return;
			}
			if ((flags & Members.EXCLUDE_INTERFACES) == 0)
			{
				for (int i = type.getDeclaredInterfaceCount () - 1; i >= 0; i--)
				{
					findMembers (type.getDeclaredInterface (i), qualifyingType, name, flags, list, scope);
				}
			}
			type = type.getSupertype ();
		}
	}
	
	
	@Override
	public String toString ()
	{
		return "TypeScope[" + type + ']';
	}
}
