
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
import de.grogra.reflect.Field;
import de.grogra.reflect.MemberBase;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.xl.compiler.Compiler;
import de.grogra.xl.compiler.ProblemReporter;
import de.grogra.xl.compiler.pattern.PatternBuilder;
import de.grogra.xl.expr.AssignLocal;
import de.grogra.xl.expr.Assignment;
import de.grogra.xl.expr.Expression;
import de.grogra.xl.expr.ExpressionFactory;
import de.grogra.xl.expr.GetLocal;
import de.grogra.xl.expr.LocalAccess;
import de.grogra.xl.util.ObjectList;
import de.grogra.xl.vmx.VMXState;

public class Local extends MemberBase implements ExpressionFactory
{
	public int index = -1;
	public Local wrapper;
	public Local wrapped;

	BlockScope scope;
	private long modifiersHi;
	MethodScope methodScope;

	Expression firstAccess;
	Expression lastAccess;
	Expression accessRoot;

	int nesting = VMXState.Local.JAVA;
	Local ref;
	Type type;
	
	private Object constantValue;

	private PatternBuilder predBuilder;

	private final AST pos;

	
	public static final Local DUMMY = new Local ("dummy", 0, null, null, null);


	Local (String name, long modifiers, Type type, BlockScope scope, AST pos)
	{
		super (name, null, (int) modifiers | PUBLIC, null);
		this.pos = pos;
		this.modifiersHi = modifiers;
		this.type = type;
		this.scope = scope;
		methodScope = null;
		if (scope != null)
		{
			scope.getMethodScope ().receiveLocal (this);
		}
	}


	public final AssignLocal createSet ()
	{
		return new AssignLocal (this, Assignment.SIMPLE);
	}

	public final Expression createSet (Scope scope, AST ast)
	{
		Expression e = createExpression (scope, ast);
		if (!(e instanceof GetLocal))
		{
			scope.getCompiler ().problems.addSemanticError (Compiler.I18N.msg (
				ProblemReporter.ASSIGNMENT_TO_FINAL, getName ()), ast);
			return e;
		}
		return createSet ();
	}

	public final GetLocal createGet ()
	{
		GetLocal e = new GetLocal (this);
		if ((modifiers & FINAL) != 0)
		{
			e.lval |= Compiler.EXPR_FINAL;
		}
		return e;
	}

	public final Expression createExpression (Scope scope, AST ast)
	{
		if ((modifiers & CONSTANT) != 0)
		{
			return Expression.createConst (type, constantValue);
		}
		else if (methodScope == MethodScope.get (scope))
		{
			return createGet ();
		}
		else if (!Reflection.isFinal (this))
		{
			scope.getCompiler ().problems.addSemanticError (Compiler.I18N.msg (
				ProblemReporter.NONFINAL_LOCAL, getName ()), ast);
			return new Expression (getType ());
		}
		Field f = TypeScope.get (scope).getFieldForEnclosingLocal (this);
		Local p = MethodScope.get (scope).getParameterForEnclosingLocal (this);
		return (p != null) ? p.createGet () : scope.getCompiler ().compileFieldExpression (f,
			scope.getCompiler ().compileInstance (f, null, scope, ast), scope, ast);
	}

	
	public void setConstant (Object constant)
	{
		constantValue = constant;
		modifiers |= CONSTANT;
	}


	public final VMXState.Local createVMXLocal ()
	{
		return new VMXState.Local
			(nesting,
			 Math.max ((nesting > 0) ? ref.index : index, 0));
	}

	
	final void setFinal ()
	{
		modifiers |= FINAL;
	}

	
	public long getModifiersEx ()
	{
		return modifiers | (modifiersHi & (-1l << 32));
	}


	public void setVariable (PatternBuilder builder)
	{
		predBuilder = builder;
	}
	
	
	public PatternBuilder getPatternBuilder ()
	{
		return predBuilder;
	}


	public final MethodScope getMethodScope ()
	{
		return methodScope;
	}


	public final BlockScope getScope ()
	{
		return scope;
	}


	public final boolean isAccessed (Expression expr, int accessType)
	{
		if (!(expr instanceof LocalAccess))
		{
			return false;
		}
		LocalAccess a = (LocalAccess) expr;
		for (int i = a.getLocalCount () - 1; i >= 0; i--)
		{
			if ((this == a.getLocal (i))
				&& ((a.getAccessType (i) & accessType) != 0))
			{
				return true;
			}
		}
		return false;
	}


	public final int getAccessesInTree (Expression root)
	{
		int t = 0;
		if (root instanceof LocalAccess)
		{
			LocalAccess a = (LocalAccess) root;
			for (int i = a.getLocalCount () - 1; i >= 0; i--)
			{
				if (this == a.getLocal (i))
				{
					t |= a.getAccessType (i);
				}
			}
		}
		for (root = root.getFirstExpression (); root != null;
			 root = root.getNextExpression ())
		{
			t |= getAccessesInTree (root);
		}
		return t;
	}


	final void unsetParameter ()
	{
		modifiersHi &= ~Compiler.MOD_PARAMETER;
		index = -1;
	}

	
	public final boolean isParameter ()
	{
		return (modifiersHi & Compiler.MOD_PARAMETER) != 0;
	}


	public final boolean isJavaLocal ()
	{
		return nesting == VMXState.Local.JAVA;
	}


	public final boolean isVariable (PatternBuilder builder)
	{
		return (predBuilder != null) && predBuilder.enclosesOrEquals (builder);
	}


	public Type getType ()
	{
		return type;
	}


	@Override
	public String toString ()
	{
		return Compiler.modifiersToString (getModifiersEx ())
			+ ' ' + type + ' ' + getName ()
			+ ' ' + createVMXLocal ();
	}

	
	public AST getAST ()
	{
		return pos;
	}

	
	private static boolean isDescendantOrSelf (AST a, AST parent)
	{
		if (a == parent)
		{
			return true;
		}
		for (parent = parent.getFirstChild (); parent != null;
			 parent = parent.getNextSibling ())
		{
			if (isDescendantOrSelf (a, parent))
			{
				return true;
			}
		}
		return false;
	}


	public boolean isDeclaredBehind (AST p)
	{
		if (pos == null)
		{
			return false;
		}
		for (AST a = p.getFirstChild (); a != null; a = a.getNextSibling ())
		{
			if (isDescendantOrSelf (pos, a))
			{
				return true;
			}
		}
		while ((p = p.getNextSibling ()) != null)
		{
			if (isDescendantOrSelf (pos, p))
			{
				return true;
			}
		}
		return false;
	}

	public ObjectList<Annotation> getDeclaredAnnotations ()
	{
		if (annots == null)
		{
			annots = new ObjectList<Annotation> ();
		}
		return annots;
	}

	public void setAnnotations (ObjectList<Annotation> annots)
	{
		this.annots = annots;
	}
}
