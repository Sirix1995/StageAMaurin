
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
import de.grogra.grammar.RecognitionException;
import de.grogra.grammar.RecognitionExceptionList;
import de.grogra.grammar.SemanticException;
import de.grogra.reflect.Member;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.util.I18NBundle;

public final class ProblemReporter extends RecognitionExceptionList
{
	/**
	 * Resource bundle for messages. 
	 */
	public static final I18NBundle I18N
		= I18NBundle.getInstance (ProblemReporter.class);

	public static final String MEMBER = "member.member";
	public static final String MEMBERS = "member.members";
	
	public static final String VARIABLE = "member.variable";
	public static final String VARIABLES = "member.variables";
	
	public static final String FIELD = "member.field";
	public static final String FIELDS = "member.fields";
	
	public static final String METHOD = "member.method";
	public static final String METHODS = "member.methods";
	
	public static final String CONSTRUCTOR = "member.constructor";
	public static final String CONSTRUCTORS = "member.constructors";
	
	public static final String PATTERN = "member.pattern";
	public static final String PATTERNS = "member.patterns";
	
	public static final String TYPE = "member.type";
	public static final String TYPES = "member.types";
	
	public static final String PACKAGE = "member.package";
	public static final String PACKAGES = "member.packages";
	
	public static final String TWO_SINGULARS = "member.two-singulars";
	public static final String TWO_PLURALS = "member.two-plurals";
	
	public static final String THREE_SINGULARS = "member.three-singulars";
	public static final String THREE_PLURALS = "member.three-plurals";
	
	public static final String TWO_MEMBERS = "member.two-members";
	public static final String ADD_MEMBER = "member.add-member";
	public static final String THREE_OR_MORE_MEMBERS = "member.three+-members";
	
	public static final String NO_MEMBER_IN_TYPE = "compiler.no-member-in-type";
	public static final String NO_MEMBER_IN_PACKAGE = "compiler.no-member-in-package";
	public static final String NO_MEMBER_IN_SCOPE = "compiler.no-member-in-scope";
	
	public static final String STATIC_MEMBERS_EXPECTED = "compiler.non-static-members";
	public static final String INSTANCE_MEMBERS_EXPECTED = "compiler.non-instance-members";
	
	public static final String AMBIGUOUS_MEMBERS = "compiler.ambiguous-members";
	public static final String SHADOWED_MEMBERS = "compiler.shadowed-members";
	public static final String INACCESSIBLE_MEMBERS = "compiler.inaccessible-members";
	public static final String HIDDEN_MEMBERS = "compiler.hidden-members";
	public static final String INAPPLICABLE_MEMBERS = "compiler.inapplicable-members";
	public static final String INAPPLICABLE_INVISIBLE_MEMBERS = "compiler.inapplicable-invisible-members";
	
	public static final String THIS_IN_STATIC = "compiler.this-in-static-context";
	public static final String SUPER_IN_STATIC = "compiler.super-in-static-context";
	public static final String NONSTATIC_IN_STATIC_CONTEXT = "compiler.nonstatic-in-static-context";
	public static final String NO_ENCLOSING_INSTANCE = "compiler.no-enclosing-instance";
	public static final String NO_ENCLOSING_INSTANCE_FOR_MEMBER = "compiler.no-enclosing-instance-for-member";
	public static final String QUALIFIED_NEW_OF_STATIC = "compiler.qualified-new-of-static";
	public static final String QUALIFIED_SUPER_OF_STATIC = "compiler.qualified-super-of-static";
	public static final String STATIC_MEMBER_IN_INNER = "compiler.static-member-in-inner";
	public static final String ABSTRACT_IN_CONCRETE = "compiler.abstract-in-concrete";
	public static final String NONFINAL_LOCAL = "compiler.nonfinal-local";
	public static final String ASSIGNMENT_TO_FINAL = "compiler.assignment-to-final";
	public static final String USE_BEFORE_DECLARATION = "compiler.use-before-declaration";
	public static final String EXPR_NEEDS_BLOCK = "compiler.expr-needs-block";
	public static final String ANNOTATION_CONSTANT_EXPECTED = "compiler.annotation-const-expected";
	public static final String ELEMENT_NOT_INITIALIZED = "compiler.element-not-initialized";
	public static final String NO_ELEMENT = "compiler.no-element";
	public static final String OVERLOADED_ELEMENT = "compiler.overloaded-element";

	public static final String NAME_NOT_CANONICAL = "compiler.name-not-canonical";
	
	public static final String TOP_LEVEL_TYPE_CONFLICT = "compiler.top-level-type-conflict";
	
	public static final String ILLEGAL_SUPERCLASS = "compiler.illegal-superclass";
	public static final String CLASS_EXTENDS_INTERFACE = "compiler.class-extends-interface";
	public static final String NO_INTERFACE_TYPE = "compiler.no-interface-type";
	public static final String CLASS_EXTENDS_FINAL = "compiler.class-extends-final";

	public static final String MISMATCHED_CONSTRUCTOR_NAME = "compiler.mismatched-constructor-name";
	
	public static final String DUPLICATE_PARAMETER = "compiler.duplicate-parameter-declaration";
	public static final String DUPLICATE_LOCAL = "compiler.duplicate-local-declaration";
	public static final String DUPLICATE_FIELD = "compiler.duplicate-field-declaration";
	public static final String DUPLICATE_CONSTRUCTOR = "compiler.duplicate-constructor-declaration";
	public static final String DUPLICATE_METHOD = "compiler.duplicate-method-declaration";
	public static final String DUPLICATE_TYPE = "compiler.duplicate-type-declaration";
	public static final String DUPLICATE_LABEL = "compiler.duplicate-label-declaration";
	public static final String DUPLICATE_ELEMENT = "compiler.duplicate-element-initialization";
	
	public static final String CONFLICTING_RETURN_TYPE = "compiler.conflicting-return-type";
	public static final String FINAL_METHOD_OVERRIDEN = "compiler.cannot-override-final";
	public static final String WEAKER_ACCESS_PRIVILEGES = "compiler.weaker-access-privileges";
	
	public static final String CONCRETE_CLASS_EXPECTED = "compiler.concrete-class-expected";
	public static final String PUBLIC_CLASS_EXPECTED = "compiler.public-class-expected";
	public static final String PUBLIC_MEMBER_EXPECTED = "compiler.public-member-expected";
	public static final String NO_DEFAULT_CONSTRUCTOR = "compiler.no-default-constructor";
	public static final String DEFAULT_CONSTRUCTOR_NOT_PUBLIC = "compiler.default-constructor-not-public";
	public static final String INSTANTIATION_EXCEPTION = "compiler.instantiation-exception";
	
	public static final String LHS_VARIABLE = "compiler.lhs-variable";
	public static final String LHS_XL_FIELD = "compiler.lhs-xl-field";
	public static final String INC_VARIABLE = "compiler.inc-variable";
	public static final String IMPLICIT_VOID_VARIABLE = "compiler.implicit-void-var";
	
	public static final String INCOMPATIBLE_TYPES = "compiler.incompatible-types";
	public static final String ILLEGAL_RETURN_CONVERSION = "compiler.illegal-return-conversion";
	public static final String ILLEGAL_ASSIGNMENT_CONVERSION = "compiler.illegal-assignment-conversion";
	public static final String AMBIGUOUS_CONVERSIONS = "compiler.ambiguous-conversion";
	public static final String UNEXPECTED_TYPE = "compiler.unexpected-type";
	public static final String ARRAYINIT_FOR_NONARRAY = "compiler.arrayinit-for-nonarray";
	public static final String NOT_NUMERIC = "compiler.not-numeric";
	public static final String ILLEGAL_UNOP_TYPE = "compiler.illegal-unop-type";
	public static final String ILLEGAL_BINOP_TYPE = "compiler.illegal-binop-type";
	public static final String ILLEGAL_SWITCH_TYPE = "compiler.illegal-switch-type";
	public static final String ILLEGAL_LABEL_TYPE = "compiler.illegal-label-type";
	public static final String WRONG_OPERAND_NUMBER_FOR_OP = "compiler.wrong-operand-number-for-op";
	public static final String INT_EXPECTED_FOR_DUMMY = "compiler.int-expected-for-dummy";
	public static final String AMBIGUOUS_OPERATOR_OVERLOAD = "compiler.ambiguous-operator-overload";
	public static final String NO_REFERENCE_TYPE = "compiler.no-reference-type";
	public static final String NO_ARRAY_TYPE = "compiler.no-array-type";
	public static final String NO_ANNOTATION_TYPE = "compiler.no-annotation-type";
	public static final String EMPTY_TYPE_INTERSECTION = "compiler.empty-type-intersection";
	public static final String ABSTRACT_INSTANTIATION = "compiler.abstract-instantiation";
	public static final String ABSTRACT_METHOD_INVOCATION = "compiler.abstract-method-invocation";

	public static final String NEVER_INSTANCE = "compiler.never-an-instance";
	public static final String ILLEGAL_CAST = "compiler.illegal-cast";
	
	public static final String LABEL_NOT_CONSTANT = "compiler.label-not-constant";
	public static final String DUPLICATE_DEFAULT_LABEL = "compiler.duplicate-default-label";
	public static final String DUPLICATE_SWITCH_LABEL = "compiler.duplicate-switch-label";
	public static final String NO_LABEL_IN_SCOPE = "compiler.no-label-in-scope";
	public static final String NO_BREAK_TARGET = "compiler.no-break-target";
	public static final String NO_CONTINUE_TARGET = "compiler.no-continue-target";
	public static final String NONLOOP_CONTINUE_TARGET = "compiler.nonloop-continue-target";

	public static final String RETURN_OUTSIDE_METHOD = "compiler.return-outside-method";
	public static final String ITERATING_NONVOID_RETURN = "compiler.iterating-nonvoid-return";
	public static final String NONITERATING_YIELD = "compiler.noniterating-yield";
	public static final String NONVOID_RETURN = "compiler.nonvoid-return";
	public static final String VOID_RETURN = "compiler.void-return";
	public static final String NONVOID_YIELD = "compiler.nonvoid-yield";
	public static final String VOID_YIELD = "compiler.void-yield";
	
	public static final String ILLEGAL_MODIFIER = "compiler.illegal-modifier";
	public static final String DUPLICATE_MODIFIER = "compiler.duplicate-modifier";
	public static final String DUPLICATE_ACCESS_MODIFIER = "compiler.duplicate-access-modifier";
	public static final String REDUNDANT_MODIFIER = "compiler.redundant-modifier";
	public static final String INCOMPATIBLE_MODIFIERS = "compiler.incompatible-modifiers";
	public static final String DUPLICATE_ANNOTATION = "compiler.duplicate-annotation";
	public static final String NO_CLASSPATH_ANNOTATION = "compiler.no-classpath-annotation";

	public static final String ILLEGAL_INT = "compiler.illegal-int";
	public static final String ILLEGAL_LONG = "compiler.illegal-long";
	public static final String ILLEGAL_FLOAT = "compiler.illegal-float";
	public static final String ILLEGAL_DOUBLE = "compiler.illegal-double";

	public static final String NONNULL_TYPE_CONSTANT_EXPECTED = "compiler.nonnull-typeconst-expected";
	public static final String CANNOT_WRAP_TYPE = "compiler.wrappable-type-expected";
	public static final String SINGLE_ARGTERM_FOR_TYPE_EXPECTED = "compiler.single-argterm-for-type-expected";
	public static final String LABEL_FOR_VOID = "compiler.label-for-void";
	
	public static final String NO_ITERATION_TARGET = "compiler.no-iteration-target";
	
	public static final String NOT_TRAVERSABLE = "compiler.pattern-not-traversable";
	public static final String CLOSED_NOT_TRAVERSABLE = "compiler.closed-pattern-not-traversable";
	public static final String CONFLICTING_JOIN_TERMS = "compiler.conflicting-join-terms";
	public static final String DANGLING_OUT_TERM = "compiler.dangling-out-term";
	public static final String DANGLING_IN_TERM = "compiler.dangling-in-term";
	public static final String MISSING_PARENT = "compiler.missing-parent-for-branch";
	public static final String OUT_TERM_OF_PARENT_NOT_CLOSED = "compiler.out-term-of-parent-not-closed";
	public static final String PATTERN_NOT_LABELABLE = "compiler.pattern-not-labelable";
	public static final String DUPLICATE_PLACE_LABEL = "compiler.duplicate-place-label";
	public static final String QUERY_VARIABLE_EXPECTED = "compiler.qvar-expected";
	public static final String QUERY_VARIABLE_AT_HIGHER_LEVEL = "compiler.qvar-at-higher-level";

	public static final String NO_PRODUCTION_CONTEXT = "compiler.no-production-context";
	public static final String VOID_PRODUCER = "compiler.void-producer";
	public static final String NO_QUERY_MODEL = "compiler.no-query-model";
	public static final String NO_QUERY_MODEL_IN_GRAPH = "compiler.no-query-model-in-graph";

	public static final String NO_INSTANTIATION_PRODUCER_TYPE = "compiler.no-instantiation-producer-type";

	public static final String DECLARATION_HIDES_FIELD = "compiler.declaration-hides-field";
	public static final String DEPRECATED_MEMBER = "compiler.deprecated-member";

	public static final String UNIMPLEMENTED_METHOD = "compiler.unimplemented-method";
	


	public static final long WARN_ON_HIDDEN_FIELDS = RecognitionException.MIN_UNUSED;
	public static final long WARN_ON_DEPRECATED = WARN_ON_HIDDEN_FIELDS << 1;
	public static final long MIN_UNUSED_WARNING = WARN_ON_DEPRECATED << 1;

	public ProblemReporter (long warningBits, long errorBits)
	{
		super (warningBits, errorBits);
	}


	public static RecognitionException createSemanticError (String msg,
															AST pos)
	{
		return new SemanticException (msg).set (pos);
	}

	
	void checkMethodDeclaration (AST id, XMethod method)
	{
		checkMethodDeclaration (id, method, method.getDeclaringType (), true, true);
	}
	
	
	boolean checkMethodDeclaration
		(AST id, XMethod method, Type t, boolean skip, boolean iface)
	{
		Type topLevel = Reflection.getTopLevelType (t);
		String pkg = topLevel.getPackage ();
		while (true)
		{
			for (int i = t.getDeclaredInterfaceCount () - 1; i >= 0; i--)
			{
				if (checkMethodDeclaration (id, method, t.getDeclaredInterface (i), false, false))
				{
					return true;
				}
			}
			if (skip)
			{
				skip = false;
			}
			else
			{
				for (int i = t.getDeclaredMethodCount () - 1; i >= 0; i--)
				{
					Method m = t.getDeclaredMethod (i);
					if (m.getDescriptor ().startsWith (method.noRetDescriptor))
					{
						int mods = m.getModifiers ();
						if ((mods & Member.PRIVATE) != 0)
						{
							if (topLevel == null)
							{
								return false;
							}
						}
						else if ((mods & (Member.PUBLIC | Member.PROTECTED)) == 0)
						{
							if (pkg == null)
							{
								return false;
							}
						}
						if (!Reflection.equal (m.getReturnType (), method.getReturnType ()))
						{
							addSemanticError
								(I18N.msg (CONFLICTING_RETURN_TYPE,
										   m.getReturnType ().getName (),
										   m.getSimpleName (),
										   m.getDeclaringType ().getName ()), id);
							return true;
						}
						else if (Reflection.isFinal (m))
						{
							addSemanticError
								(I18N.msg (FINAL_METHOD_OVERRIDEN,
										   m.getSimpleName (),
										   m.getDeclaringType ().getName ()), id);
							return true;
						}
						else if (Reflection.isMoreVisible (m.getModifiers (), method.getModifiers ()))
						{
							addSemanticError
								(I18N.msg (WEAKER_ACCESS_PRIVILEGES, m.getSimpleName (), m.getDeclaringType ().getName ()), id);
							return true;
						}
						return false;
					}
				}
			}
			if (!iface && ((t.getModifiers () & Member.INTERFACE) != 0))
			{
				return false;
			}
			t = t.getSupertype ();
			if (t == null)
			{
				return false;
			}
			if (pkg != null)
			{
				if ((topLevel != null)
					&& !Reflection.equal (Reflection.getTopLevelType (t), topLevel)) 
				{
					topLevel = null;
				}
				if (!t.getPackage ().equals (pkg))
				{
					pkg = null;
				}
			}
		}
	}


	public static String enumerate (String[] members)
	{
		switch (members.length)
		{
			case 0:
				throw new IllegalStateException ();
			case 1:
				return members[0];
			case 2:
				return I18N.msg (TWO_MEMBERS, members[0], members[1]);
			default:
				String m = members[0];
				int n = 0;
				while (++n < members.length - 2)
				{
					m = I18N.msg (ADD_MEMBER, m, members[n]);
				}
				return I18N.msg (THREE_OR_MORE_MEMBERS, m, members[n], members[n + 1]);
		}
	}

}
