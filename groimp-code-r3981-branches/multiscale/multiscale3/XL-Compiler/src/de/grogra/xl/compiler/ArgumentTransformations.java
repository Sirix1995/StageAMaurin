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

import antlr.collections.AST;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Member;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Signature;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.xl.compiler.pattern.PatternWrapper;
import de.grogra.xl.compiler.scope.Scope;
import de.grogra.xl.compiler.scope.Members.Applicability;
import de.grogra.xl.expr.Expression;
import de.grogra.xl.lang.Aggregate;

/**
 * This class defines a set of alternatives of argument transformations
 * as described by the XL Language Specification. The method
 * {@link #isApplicable} defines if an alternative is applicable
 * to a signature, the method {@link #createImplicitArgument} creates
 * implicit arguments where necessary.
 * 
 * @author Ole Kniemeyer
 */
class ArgumentTransformations
{
	static final Type[] NO_IMPLICIT = Type.TYPE_0;

	static final Type[] GENERATOR = new Type[1];

	static final Type[] FIRST_TERM = new Type[1];

	static final Type AGGREGATE_TYPE = ClassAdapter.wrap (Aggregate.class);

	static final Type[] AGGREGATE = {AGGREGATE_TYPE};

	static final Type[] FILTER = {ClassAdapter
			.wrap (de.grogra.xl.lang.Filter.class)};

	static final ArgumentTransformations NO_IMPLICIT_ARGS = new ArgumentTransformations (
			new Type[1][0], null);

	static final ArgumentTransformations PREDICATE_ARGS = new ArgumentTransformations (
			new Type[][] {NO_IMPLICIT, FIRST_TERM}, null);

	/**
	 * Contains for each alternative an array of the types of its implicit
	 * arguments. The number of alternatives is also determined hereby,
	 * it is <code>types.length</code>. If a type is <code>null</code>,
	 * this indicates a wildcard, i.e., its implicit argument
	 * matches for any type of the corresponding method parameter.
	 */
	Type[][] types;

	/**
	 * Contains for each alternative an array of the locations of its
	 * implicit arguments: If <code>i = indices[alternative][arg]</code> is
	 * non-negative, the implicit argument number <code>arg</code> is inserted
	 * as actual argument <code>i</code>. If <code>i</code> is negative,
	 * argument <code>arg</code> is inserted as the <code>-i</code>th
	 * actual argument from the right. <code>indices</code> may be
	 * <code>null</code>, this indicates that all implicit arguments
	 * are to be inserted from left in their natural order. 
	 */
	int[][] indices;

	ArgumentTransformations (Type[][] types, int[][] indices)
	{
		this.types = types;
		this.indices = indices;
	}

	ArgumentTransformations (Type[][] types)
	{
		this.types = types;
		int[][] a = new int[types.length][];
		for (int i = 0; i < a.length; i++)
		{
			int[] b = new int[types[i].length];
			for (int j = 0; j < b.length; j++)
			{
				b[j] = j;
			}
			a[i] = b;
		}
		indices = a;
	}

	boolean isGenerator (int alternative)
	{
		return types[alternative] == GENERATOR;
	}

	boolean isAggregate (int alternative)
	{
		return (types[alternative].length > 0) && (types[alternative][0] == AGGREGATE_TYPE);
	}

	boolean isFilter (int alternative)
	{
		return types[alternative] == FILTER;
	}

	final int getAlternativeCount ()
	{
		return types.length;
	}

	Expression createImplicitArgument (int alternative, int index, Type type, Scope scope, AST pos)
	{
		if ((index == 0)
				&& (isGenerator (alternative) || isAggregate (alternative) || isFilter (alternative)))
		{
			return null;
		}
		throw new AssertionError ();
	}

	int getApplicableOption (Signature m, Applicability out, Expression[] args,
			Scope scope)
	{
		for (int i = 0; i < getAlternativeCount (); i++)
		{
			if (isApplicable (m, out, i, args, scope))
			{
				return i;
			}
		}
		return -1;
	}

	final Expression[] complete (Signature m, int alternative, Expression[] args,
								 boolean varArity, Scope scope, AST pos)
	{
		Type[] prefixed = types[alternative];
		if (prefixed.length == 0)
		{
			return args;
		}
		int[] ind = (indices == null) ? null : indices[alternative];
		int n = prefixed.length + args.length;
		Expression[] a = new Expression[n];
		int k = 0, j = 0;
		for (int i = 0; i < n; i++)
		{
			Type t = Reflection.getParameterType (m, i, varArity);
			a[i] = ((k < prefixed.length) && ((ind == null) || (ind[k] == i) || (ind[k] == i
					- n))) ? createImplicitArgument (alternative, k++, t, scope, pos)
					: args[j++];
		}
		return a;
	}

	/**
	 * Checks if the signature <code>m</code> is applicable for the
	 * <code>alternative</code>, given <code>args</code> as explicit
	 * arguments. 
	 * 
	 * @param m a signature
	 * @param out the quality of applicability is stored here by this method 
	 * @param alternative the alternative to check
	 * @param args explicit arguments
	 * @param scope current scope
	 * @return <code>true</code> iff <code>m</code> is applicable
	 */
	boolean isApplicable (Signature m, Applicability out,
			int alternative, Expression[] args, Scope scope)
	{
		Type[] implicit = types[alternative];
		int[] ind = (indices == null) ? null : indices[alternative];

		// n: number of actual arguments
		int n = implicit.length + args.length;

		Compiler comp = scope.getCompiler ();

		out.actualArguments.clear ();
		out.varArity = false;

		if ((comp.resolutionPhase == Compiler.APPLICABLE_BY_VARIABLE_ARITY_PHASE)
			&& ((((Method) m).getModifiers () & Member.VARARGS) != 0))
		{
			if (n < m.getParameterCount () - 1)
			{
				return false;
			}
			out.varArity = true;
		}
		else if (m.getParameterCount () != n)
		{
			return false;
		}
		if ((isAggregate (alternative) || isFilter (alternative)) && (n == 1))
		{
			// aggregate and filter method invocations need at least one
			// explicit argument
			return false;
		}

		// reset quality of applicability
		out.implicitCount = implicit.length;
		out.array2Generator = false;
		out.transformationAlternative = alternative;

		// k indicates current index of implicit arguments
		int k = 0;

		// j indicates current index of explicit arguments
		int j = 0;

		// loop over all arguments of invocation
		checkArguments: for (int i = 0; i < n; i++)
		{
			if ((i == 0) && isGenerator (alternative))
			{
				if (!((m instanceof Method) && (XMethod.getGeneratorType ((Method) m) != null)))
				{
					return false;
				}
			}

			// type of the parameter #i of method m
			Type param = Reflection.getParameterType (m, i, out.varArity);

			// determine if parameter i is provided by an implicit argument
			boolean impl = (k < implicit.length)
					&& ((ind == null) || (ind[k] == i) || (ind[k] == i - n));

			if (!impl && (j == args.length))
			{
				// explicit argument needed, but no one present
				return false;
			}

			// determine type of actual argument for parameter i
			Type actual = impl ? implicit[k++] : args[j++].getType ();
			Type originalActual = actual;

			if (actual == null)
			{
				// type wildcard, argument and parameter are matching
				out.actualArguments.add (actual);
				continue checkArguments;
			}


			if (impl)
			{
				// delegate to matchedImplicit
				if (matchesImplicit (param, k - 1, alternative, actual))
				{
					// argument and parameter are matching
					out.actualArguments.add (actual);
					continue checkArguments;
				}
				return false;
			}

			// special handling if signature is a predicate and
			// argument and parameter have reference types
			if ((m instanceof PatternWrapper)
					&& (actual.getTypeId () == TypeId.OBJECT)
					&& (param.getTypeId () == TypeId.OBJECT))
			{
				if (Reflection.isCastableFrom (param, actual))
				{
					out.actualArguments.add (actual);
					continue checkArguments;
				}
				return false;
			}

			if ((i == 1)
					&& (isAggregate (alternative) || isFilter (alternative))
					&& Reflection.isArray (actual))
			{
				// invocation of aggregate or filter method with
				// an array argument: tentatively replace this by a loop over
				// its components
				actual = actual.getComponentType ();
				out.array2Generator = true;
			}

			while (true)
			{
				if (CompilerBase.stdCvType (actual, param, comp.resolutionPhase >= Compiler.APPLICABLE_BY_METHOD_INVOCATION_CONVERSION_PHASE, scope) != CompilerBase.CV_TYPE_NONE)
				{
					out.actualArguments.add (actual);
					continue checkArguments;
				}

				// no further conversions defined for predicates
				if (m instanceof PatternWrapper)
				{
					return false;
				}

				if ((comp.resolutionPhase == Compiler.APPLICABLE_BY_METHOD_INVOCATION_CONVERSION_PHASE)
					&& (comp.standardImplicitConversion (new Expression (actual), param, true, scope, null, null, true) != null))
				{
					out.actualArguments.add (actual);
					continue checkArguments;
				}
				if ((comp.resolutionPhase >= Compiler.APPLICABLE_BY_ENHANCED_METHOD_INVOCATION_CONVERSION_PHASE)
					&& (comp.implicitConversion (new Expression (actual), param, true, scope, null, null, true) != null))
				{
					out.actualArguments.add (actual);
					continue checkArguments;
				}

				if (actual != originalActual)
				{
					// we tentatively replaced an array argument
					// by its components, undo this and check
					// the original array argument
					out.array2Generator = false;
					actual = originalActual;
					continue;
				}

				// not applicable
				return false;
			}
		}
		return true;
	}

	boolean matchesImplicit (Type param, int option, int implIndex,
			Type implType)
	{
		return Reflection.equal (param, implType);
	}

}
