
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
import de.grogra.grammar.RecognitionException;
import de.grogra.grammar.SemanticException;
import de.grogra.reflect.Field;
import de.grogra.reflect.Member;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.util.I18NBundle;
import de.grogra.xl.compiler.Compiler;
import de.grogra.xl.compiler.ProblemReporter;
import de.grogra.xl.compiler.ShiftedMethod;
import de.grogra.xl.compiler.pattern.PatternWrapper;
import de.grogra.xl.compiler.scope.Members.Resolution;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;

/**
 * This class is used to collect a set of {@link de.grogra.reflect.Member}s,
 * and to determine the applicable, accessible, and most specific, member
 * thereof. In the context of this class, members are local variables, fields,
 * named predicates, methods, constructors, types, and packages.
 * The usage is as follows:
 * <ol>
 * <li> The name of the members to collect is set by one of the
 * <code>resetName</code> methods.
 * <li> The context in which members of the given name are to be searched
 * is defined by one of the <code>setContext</code> methods. This also
 * includes a set of flags (a union of constants defined in this class)
 * which restrict the possible set of members.
 * <li> The method {@link #find(Resolution)} is invoked in order to
 * collect the members which can be found in the context, have the given
 * name, and match the restrictions imposed by the flags. This method
 * either returns a member or throws an exception.
 * </ol>
 * <b>IMPORTANT</b>: The numeric values of the masks up to
 * {@link #MAX_MEMBER} are chosen to reflect the obscuring relations
 * of the XL programming language. Do not change the values!
 *
 * @author Ole Kniemeyer
 */
public final class Members 
{
	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * local variables are to be included in the search.
	 */
	public static final int LOCAL = 1;

	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * fields are to be included in the search.
	 */
	public static final int FIELD = 2;

	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * named predicates are to be included in the search.
	 */
	public static final int PREDICATE = 4;

	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * methods are to be included in the search.
	 */
	public static final int METHOD = 8;

	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * constructors are to be included in the search.
	 */
	public static final int CONSTRUCTOR = 16;

	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * types are to be included in the search.
	 */
	public static final int TYPE = 32;

	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * sub-packages of packages are to be included in the search.
	 */
	public static final int SUB_PACKAGE = 64;
	
	/**
	 * Largest bit mask of the bit masks which select the type of
	 * members in question.
	 */
	public static final int MAX_MEMBER = SUB_PACKAGE;
	
	/**
	 * This constant can be used as mask to filter the bit masks
	 * which represent member types.
	 */
	public static final int MEMBER_MASK = (MAX_MEMBER << 1) - 1;

	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * interface are to be excluded from the search.
	 */
	public static final int EXCLUDE_INTERFACES = MAX_MEMBER << 1;

	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * types as members of packages are to be excluded from the search.
	 */
	public static final int EXCLUDE_TYPES_IN_PACKAGES = EXCLUDE_INTERFACES << 1;

	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * only static members are to be included in the search.
	 */
	public static final int STATIC_ONLY = EXCLUDE_TYPES_IN_PACKAGES << 1;

	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * only non-static members are to be included in the search.
	 */
	public static final int INSTANCE_ONLY = STATIC_ONLY << 1;
	
	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * only declared members are to be included in the search. This
	 * excludes members declared in supertypes and members imported by
	 * import-on-demand statements.
	 */
	public static final int DECLARED_ONLY = INSTANCE_ONLY << 1;

	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * top-level packages are to be included in the search.
	 */
	public static final int TOP_LEVEL_PACKAGE = DECLARED_ONLY << 1;

	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * only members of the supertype of the context type are to be included
	 * in the search.
	 */
	public static final int SUPER = TOP_LEVEL_PACKAGE << 1;
	
	public static final int FULLY_QUALIFIED = SUPER << 1;

	/**
	 * Bit mask for <code>setContext</code> methods indicating that
	 * instance scopes have to be excluded from the search.
	 */
	public static final int EXCLUDE_INSTANCE_SCOPES = FULLY_QUALIFIED << 1;

	public static final int INCLUDE_FIRST_INSTANCE_SCOPE = EXCLUDE_INSTANCE_SCOPES << 1;

	public static final int OPERATOR_METHODS = INCLUDE_FIRST_INSTANCE_SCOPE << 1;

	public static final int SHIFT_METHODS = OPERATOR_METHODS << 1;

	/**
	 * This bit mask has to be set by {@link Scope#findMembers(String, int, Members)}
	 * before <code>super.findMembers</code> is invoked
	 * if the scope is related to another package than the package of the
	 * context. 
	 */
	public static final int DIFFERENT_PACKAGE = SHIFT_METHODS << 1;

	/**
	 * This is the minimal bit mask which is not defined by this class.
	 */
	public static final int MIN_UNUSED = DIFFERENT_PACKAGE << 1;

	/**
	 * This bit mask is the union of {@link #FIELD} and {@link #LOCAL},
	 * i.e., it represents variables.
	 */
	public static final int VARIABLE = FIELD | LOCAL;


	private static final int NOT_STATIC = 1;
	private static final int NOT_INSTANCE = 2;
	private static final int SHADOWED = 4;
	private static final int INACCESSIBLE = 8;
	private static final int HIDDEN = 16;
	private static final int NOT_APPLICABLE = 32;
	private static final int NOT_IN_SCOPE = 64;

	private static final int HAS_QUALIFIER = 128;
	private static final int REMOVED = 256;
	private static final int MATCH_SET_BIT = 9;
	private static final int INFO_BITS = ~(HAS_QUALIFIER - 1);

	private static final I18NBundle I18N = Compiler.I18N;

	
	/**
	 * This class stores information about the quality of applicability
	 * of a member in a context.
	 * 
	 * @author Ole Kniemeyer
	 */
	public static final class Applicability
	{
		public static final Applicability DEFAULT = new Applicability ();


		public Scope scope;

		public ObjectList<Type> actualArguments = new ObjectList<Type> ();

		/**
		 * <code>true</code> iff the member is applicable only after
		 * an array argument has been converted to an array generator
		 * argument, i.e., a loop over the components of the array.
		 */
		public boolean array2Generator;

		/**
		 * Counts the number of implicit arguments for the invocation.
		 */
		public int implicitCount;
		
		/**
		 * <code>true</code> iff the method is applicable as a
		 * variable arity method.
		 */
		public boolean varArity;

		/**
		 * Indicates the alternative of argument transformations
		 * which has been chosen such that the member is applicable.
		 */
		public int transformationAlternative;

		public int matchSet;
	}


	/**
	 * <code>Resolution</code> defines a strategy how applicable
	 * members for explicitly given arguments are determined and how
	 * the most specific member of these is chosen.
	 * 
	 * @author Ole Kniemeyer
	 */
	public interface Resolution
	{
		/**
		 * Defines if ambiguous members are allowed. If so, ambiguities
		 * between members do not cause an exception be thrown in
		 * {@link Members#find(Resolution)}; the first
		 * member is returned.
		 * 
		 * @param first the first member of a set of ambiguous members
		 * @return <code>true</code> iff ambiguous members are allowed and
		 * <code>first</code> shall be returned as the result of resolution
		 */
		boolean allowsAmbiguousMembers (Member first);

		/**
		 * Checks whether a member <code>m</code> is applicable. The
		 * quality of applicability is stored in <code>applOut</code>
		 * by this method.
		 * 
		 * @param m a member
		 * @param applOut the quality of applicability is stored here by this method
		 * @param scope scope in which the member is accessed
		 * @return <code>true</code> iff <code>m</code> is applicable
		 */
		boolean isApplicable (Member m, Applicability applOut, Scope scope);

		/**
		 * Returns the argument types which are used in
		 * {@link #isApplicable(Member, Members.Applicability, Scope)}
		 * to determine the applicability. This method is only invoked to generate
		 * an error message in case no applicable member was found.
		 * 
		 * @return argument types
		 */
		Type[] getArgumentTypes ();

		/**
		 * Checks whether an applicable member <code>m1</code> is less
		 * specific than another applicable member <code>m2</code>.
		 * The applicablities are the same instances which have been passed
		 * before to {@link #isApplicable(Member, Members.Applicability, Scope)}. 
		 * 
		 * @param m1 a member
		 * @param a1 <code>m1</code>'s applicability as determined by {@link #isApplicable(Member, Members.Applicability, Scope)}
		 * @param m2 another member
		 * @param a2 <code>m2</code>'s applicability as determined by {@link #isApplicable(Member, Members.Applicability, Scope)}
		 * @param scope scope in which the member is accessed
		 * @return <code>true</code> iff <code>m1</code> is less specific than <code>m2</code>
		 */
		boolean isLessThan (Member m1, Applicability a1, Member m2, Applicability a2, Scope scope);
	}


	private final ObjectList<Member> list = new ObjectList<Member> ();
	private final ObjectList<Applicability> applicabilities = new ObjectList<Applicability> ();
	private final ObjectList<Scope> scopes = new ObjectList<Scope> ();
	private final IntList bits = new IntList ();

	private Package contextPackage = null;
	private Type contextType = null;
	private Scope contextScope = null;

	private int flags = 0;

	private Type qualifier = null;

	private AST pos = null;
	private String name = null;
	private int matchSet;
	
	private final Compiler compiler;


	public Members (Compiler c)
	{
		compiler = c;
	}

	
	public void resetName (String name, AST pos)
	{
		list.clear ();
		scopes.clear ();
		bits.clear ();
		applicabilities.clear ();

		contextPackage = null;
		contextType = null;
		contextScope = null;
		
		qualifier = null;

		this.name = name;
		this.pos = pos;
		
		matchSet = 0;
	}
	
	
	public void resetName (AST id)
	{
		resetName (id.getText (), id);
	}
	

	public void reset ()
	{
		resetName (null, null);
	}


	public void addMatches (Scope scope, int flags)
	{
		addMatches (scope, null, flags);
	}


	public void addMatches (Scope scope, Type qualifier, int flags)
	{
		contextPackage = scope.getPackage ();
		if (contextPackage == null)
		{
			contextPackage = compiler.getPackage ();
		}
		if ((flags & FULLY_QUALIFIED) != 0)
		{
			scope = ClassPath.get (scope).getPackage ("", true);
		}
		contextScope = scope;
		contextType = scope.getDeclaredType ();
		this.flags = flags;
		this.qualifier = qualifier;
		if (qualifier != null)
		{
			TypeScope.findMembers (qualifier, null, name, flags, this, null);
		}
		else
		{
			contextScope.findMembers (name, flags, this);
		}
		matchSet++;
	}

	
	public AST getPosition ()
	{
		return pos;
	}


	private void remove (int index)
	{
		list.remove (index);
		scopes.remove (index);
		bits.removeAt (index);
		if (index < applicabilities.size)
		{
			applicabilities.remove (index);
		}
	}


	public void add (final Member m, Scope scope, int findFlags)
	{
		assert (qualifier == null) != (scope == null);
		if (m == null)
		{
			return;
		}
		int mods = m.getModifiers ();

		int f;
		if ((mods & Member.PUBLIC) != 0)
		{
			f = 0;
		}
		else if ((mods & Member.PRIVATE) != 0)
		{
			f = ((contextType != null)
				 && Reflection.equal (Reflection.getTopLevelType (contextType),
						 			  Reflection.getTopLevelType (m.getDeclaringType ())))
				? 0 : INACCESSIBLE | NOT_IN_SCOPE;
		}
		else if ((findFlags & DIFFERENT_PACKAGE) == 0)
		{
			f = 0;
		}
		else if ((contextType != null) && ((mods & Member.PROTECTED) != 0))
		{
			if (m.getDeclaringType ().getPackage ().equals (getPackage ()))
			{
				f = 0;
			}
			else
			{
				Type owner = (qualifier != null) ? qualifier : scope.getOwnerOf (m);
				assert (owner != null) || (m instanceof Type) : m;
				boolean instance = ((mods & Member.STATIC) == 0)
					&& ((m instanceof Field) || (m instanceof Method));
				Type t = m.getDeclaringType ();
				f = INACCESSIBLE | NOT_IN_SCOPE;
				for (Type a = contextType; a != null; a = a.getDeclaringType ())
				{
					if (Reflection.isSuperclassOrSame (t, a))
					{
						f &= ~NOT_IN_SCOPE;
						if (!instance
							|| Reflection.isSuperclassOrSame (a, owner))
						{
							f = 0;
							break;
						}
					}
				}
			}
		}
		else
		{
			f = INACCESSIBLE | NOT_IN_SCOPE;
		}
		if (qualifier != null)
		{
			f |= HAS_QUALIFIER;
		}
		int mtype = getMemberType (m);
		for (int i = list.size () - 1; i >= 0; i--)
		{
			Member mm = m;
			Member o = list.get (i);
			if ((mm instanceof ShiftedMethod) && (o instanceof ShiftedMethod))
			{
				mm = ((ShiftedMethod) m).getMethod ();
				o = ((ShiftedMethod) o).getMethod ();
			}
			Scope s = scopes.get (i);
			if (Reflection.membersEqual (o, mm, false) && ((scope == s) || Reflection.isStatic (mm)))
			{
				return;
			}
			if (((f ^ bits.elements[i]) & HAS_QUALIFIER) == 0)
			{
				if (Reflection.membersEqual (mm, o, (mtype == METHOD) && ((mods & Member.STATIC) == 0)))
				{
					if (scope != s)
					{
						assert (s != null) && (scope != null);
						if (scope.isShadowedBy (s))
						{
							return;
						}
						else
						{
							if (s.isShadowedBy (scope))
							{
								remove (i);
							}
							continue;
						}
					}
					else
					{
						Type tm = mm.getDeclaringType (), to = o.getDeclaringType ();
						if ((tm != null) && (to != null))
						{
							if (Reflection.isSupertypeOrSame (tm, to))
							{
								return;
							}
							else if (Reflection.isSupertype (to, tm))
							{
								remove (i);
								continue;
							}
						}
					}
				}
				if ((mtype == getMemberType (o))
					|| ((mtype == FIELD) && (o instanceof Local))
					|| ((mtype == LOCAL) && (o instanceof Field)))
				{
					if (mtype != METHOD)
					{
						if (((bits.elements[i] & NOT_IN_SCOPE) == 0)
							&& Reflection.isSupertype (mm.getDeclaringType (),
													   o.getDeclaringType ())) 
						{
							f |= HIDDEN;
						}
						else if (((f & NOT_IN_SCOPE) == 0)
								 && Reflection.isSupertype (o.getDeclaringType (),
										 					mm.getDeclaringType ()))
						{
							bits.elements[i] |= HIDDEN;
						}
					}
					if (qualifier == null)
					{
						if (((bits.elements[i] & NOT_IN_SCOPE) == 0)
							&& scope.isShadowedBy (s))
						{
							f |= SHADOWED;
						}
						else if (((f & NOT_IN_SCOPE) == 0)
								 && s.isShadowedBy (scope))
						{
							bits.elements[i] |= SHADOWED;
						}
					}
				}
			}
		}
		
		if (((findFlags & STATIC_ONLY) != 0) && ((mods & Member.STATIC) == 0))
		{
			f |= NOT_STATIC;
		}
		if (((findFlags & INSTANCE_ONLY) != 0) && ((mods & Member.STATIC) != 0))
		{
			f |= NOT_INSTANCE;
		}
		f |= matchSet << MATCH_SET_BIT;
		list.add (m);
		scopes.add (scope);
		bits.add (f);
	}


	public String getMemberName ()
	{
		return name;
	}

	
	public String getPackage ()
	{
		return contextPackage.getName ();
	}


	public Type getQualifier ()
	{
		return qualifier;
	}

	public Scope getContextScope ()
	{
		return contextScope;
	}

	@Override
	public String toString ()
	{
		StringBuilder b = new StringBuilder ("[");
		for (int i = 0; i < list.size (); i++)
		{
			if ((bits.elements[i] & REMOVED) == 0)
			{
				if (b.length () > 1)
				{
					b.append (',');
				}
				b.append (Reflection.getDescription (list.get (i)));
			}
		}
		return b.append (']').toString ();
	}

	public int size ()
	{
		int s = 0;
		for (int i = list.size () - 1; i >= 0; i--)
		{
			if ((bits.elements[i] & REMOVED) == 0)
			{
				s++;
			}
		}
		return s;
	}

	private int index (int j)
	{
		for (int i = 0; i < list.size (); i++)
		{
			if ((bits.elements[i] & REMOVED) == 0)
			{
				if (--j < 0)
				{
					return i;
				}
			}
		}
		return list.size ();
	}


	private String getDescription (int index)
	{
		Member m = list.get (index (index));
		if (m instanceof ShiftedMethod)
		{
			m = ((ShiftedMethod) m).getMethod ();
		}
		return getDescription (m);
	}


	private static String getDescription (Member m)
	{
		if (m instanceof ShiftedMethod)
		{
			m = ((ShiftedMethod) m).getMethod ();
		}
		return Reflection.getDescription (m);
	}


	private String msgForAll (String msg, Object arg)
	{
		int types = 0;
		for (int i = list.size - 1; i >= 0; i--)
		{
			if ((bits.elements[i] & REMOVED) == 0)
			{
				types |= getMemberType (list.get (i));
			}
		}
		String members; 
		int s = size ();
		switch (s)
		{
			case 0:
				throw new IllegalStateException ();
			case 1:
				members = getDescription (0);
				break;
			case 2:
				members = I18N.msg (ProblemReporter.TWO_MEMBERS, getDescription (0), getDescription (1));
				break;
			default:
				members = getDescription (0);
				int n = 0;
				while (++n < s - 2)
				{
					members = I18N.msg (ProblemReporter.ADD_MEMBER, members, getDescription (n));
				}
				members = I18N.msg (ProblemReporter.THREE_OR_MORE_MEMBERS, members, getDescription (n), getDescription (n + 1));
				break;
		}
		return I18N.msg (msg, getMembersDescription (types, s > 1),
						 members, s, arg);
	}


	private void removeWhere (int mask)
	{
		for (int i = list.size - 1; i >= 0; i--)
		{
			if ((bits.elements[i] & mask & ~INFO_BITS) != 0)
			{
				bits.elements[i] |= REMOVED;
			}
		}
	}

	
	private int markApplicability (Resolution res)
	{
		int bestFlags = Integer.MAX_VALUE;
		Applicability a = new Applicability ();
		for (int i = list.size - 1; i >= 0; i--)
		{
			Member m = list.get (i);
			int f = bits.elements[i] & ~(NOT_IN_SCOPE | REMOVED);
			a.scope = scopes.get (i);
			a.matchSet = f >> MATCH_SET_BIT;
			if (res.isApplicable (m, a, contextScope))
			{
				f &= ~NOT_APPLICABLE;
				applicabilities.set (i, a);
				a = new Applicability ();
			}
			else
			{
				f |= NOT_APPLICABLE;
			}
			bits.elements[i] = f;
			f &= ~INFO_BITS;
			if (f < bestFlags)
			{
				bestFlags = f;
			}
		}
		return bestFlags;
	}

	
	private void removeLessSpecific (Resolution res)
	{
		for (int i = list.size - 1; i >= 0; i--)
		{
			if ((bits.elements[i] & REMOVED) != 0)
			{
				continue;
			}
			Member m = list.get (i);
			for (int j = list.size - 1; j >= 0; j--)
			{
				if ((i == j) || ((bits.elements[j] & REMOVED) != 0))
				{
					continue;
				}
				if (res.isLessThan (list.get (j),
									applicabilities.get (j),
									m,
									applicabilities.get (i), contextScope))
				{
					bits.elements[j] |= REMOVED;
				}
			}
		}
	}

	
	public Applicability getApplicability ()
	{
		return applicabilities.get (index (0));
	}

	
	private boolean haveApplicable;
	
	public Member resolve (Resolution res)
		throws RecognitionException
	{
		int bestFlags = markApplicability (res);

		haveApplicable = bestFlags < NOT_APPLICABLE;
		if (haveApplicable)
		{
			if (bestFlags == 0)
			{
				removeWhere (-1);
				removeLessSpecific (res);
				if ((size () == 1) || res.allowsAmbiguousMembers (list.get (index (0))))
				{
					Member m = list.get (index (0));
					if (!res.allowsAmbiguousMembers (m)
						&& compiler.problems.isWarning (ProblemReporter.WARN_ON_DEPRECATED)
						&& (Reflection.getDeclaredAnnotation (m, Deprecated.class) != null))
					{
						compiler.problems.addSemanticWarning (I18N.msg (ProblemReporter.DEPRECATED_MEMBER,
							getMemberTypeDescription (m), getDescription (m)), pos);
					}
					return m;
				}
				else
				{
					throw new SemanticException (msgForAll (ProblemReporter.AMBIGUOUS_MEMBERS, null))
						.set (pos);
				}
			}
			else if (bestFlags < NOT_INSTANCE)
			{
				removeWhere (~NOT_STATIC);
				throw new SemanticException (msgForAll (ProblemReporter.STATIC_MEMBERS_EXPECTED, null))
					.set (pos);
			}
			else if (bestFlags < SHADOWED)
			{
				removeWhere (~(NOT_STATIC | NOT_INSTANCE));
				throw new SemanticException (msgForAll (ProblemReporter.INSTANCE_MEMBERS_EXPECTED, null))
					.set (pos);
			}
			else if (bestFlags < INACCESSIBLE)
			{
				removeWhere (~SHADOWED);
				throw new SemanticException (msgForAll (ProblemReporter.SHADOWED_MEMBERS, null))
					.set (pos);
			}
			else if (bestFlags < HIDDEN)
			{
				removeWhere (~(INACCESSIBLE | SHADOWED));
				throw new SemanticException (msgForAll (ProblemReporter.INACCESSIBLE_MEMBERS, null))
					.set (pos);
			}
			else
			{
				throw new SemanticException (msgForAll (ProblemReporter.HIDDEN_MEMBERS, null))
					.set (pos);
			}
		}
		if (size () == 0)
		{
			String where = null;
			String problem = ProblemReporter.NO_MEMBER_IN_SCOPE;
			if (qualifier != null)
			{
				Type q = qualifier;
				if ((flags & SUPER) != 0)
				{
					q = q.getSupertype ();
					if (q == null)
					{
						q = Type.INVALID;
					}
				}
				where = q.getName ();
				problem = ProblemReporter.NO_MEMBER_IN_TYPE;
			}
			else if (contextScope instanceof Package)
			{
				where = contextPackage.getName ();
				problem = ProblemReporter.NO_MEMBER_IN_PACKAGE;
			}
			throw new SemanticException
				(I18N.msg (problem,
						   getMembersDescription (flags, false),
						   name, where))
				.set (pos);
		}
		bestFlags &= ~NOT_APPLICABLE;
		if (bestFlags == 0)
		{
			removeWhere (~NOT_APPLICABLE);
			Type[] t = res.getArgumentTypes ();
			StringBuffer buf = new StringBuffer ();
			for (int i = 0; i < t.length; i++)
			{
				if (i > 0)
				{
					buf.append (',');
				}
				//MH: better to use "getName" instead of "getSimplename" to distinguish better between duplicate class names. 
				//This happens when there are classes defined in the code of GroIMP and also in a project.
				buf.append (t[i].getName ());
			}
			throw new SemanticException (msgForAll (ProblemReporter.INAPPLICABLE_MEMBERS, buf))
				.set (pos);
		}
		else
		{
			throw new SemanticException (msgForAll (ProblemReporter.INAPPLICABLE_INVISIBLE_MEMBERS, null))
				.set (pos);
		}
	}
	

	public boolean haveApplicable ()
	{
		return haveApplicable;
	}
	
	public Scope getScopeForResult ()
	{
		return scopes.get (index (0));
	}


	public static int getMemberType (Member member)
	{
		if (member instanceof Local)
		{
			return LOCAL;
		}
		else if (member instanceof Field)
		{
			return FIELD;
		}
		else if (member instanceof Method)
		{
			return "<init>".equals (member.getSimpleName ())
				? CONSTRUCTOR : METHOD;
		}
		else if (member instanceof Type)
		{
			return TYPE;
		}
		else if (member instanceof Package)
		{
			return SUB_PACKAGE;
		}
		else if (member instanceof PatternWrapper)
		{
			return PREDICATE;
		}
		else
		{
			throw new IllegalArgumentException (String.valueOf (member));
		}
	}


	public static String getMemberTypeDescription (Member m)
	{
		return getMembersDescription (getMemberType (m), false);
	}


	public static String getMembersDescription (int types, boolean plural)
	{
		if ((types & TOP_LEVEL_PACKAGE) != 0)
		{
			types |= SUB_PACKAGE;
		}
		int n = 0;
		for (int i = 1; i <= MAX_MEMBER; i <<= 1)
		{
			if ((i & types) != 0)
			{
				n++;
			}
		}
		String[] s;
		if (n > 3)
		{
			n = 0;
			s = null;
		}
		else
		{
			s = new String[n];
			n = 0;
			if ((types & LOCAL) != 0)
			{
				s[n++] = I18N.msg (plural ? ProblemReporter.VARIABLES : ProblemReporter.VARIABLE);
				types &= ~FIELD;
			}
			if ((types & FIELD) != 0)
			{
				s[n++] = I18N.msg (plural ? ProblemReporter.FIELDS : ProblemReporter.FIELD);
			}
			if ((types & METHOD) != 0)
			{
				s[n++] = I18N.msg (plural ? ProblemReporter.METHODS : ProblemReporter.METHOD);
			}
			if ((types & CONSTRUCTOR) != 0)
			{
				s[n++] = I18N.msg (plural ? ProblemReporter.CONSTRUCTORS : ProblemReporter.CONSTRUCTOR);
			}
			if ((types & PREDICATE) != 0)
			{
				s[n++] = I18N.msg (plural ? ProblemReporter.PATTERNS : ProblemReporter.PATTERN);
			}
			if ((types & TYPE) != 0)
			{
				s[n++] = I18N.msg (plural ? ProblemReporter.TYPES : ProblemReporter.TYPE);
			}
			if ((types & SUB_PACKAGE) != 0)
			{
				s[n++] = I18N.msg (plural ? ProblemReporter.PACKAGES : ProblemReporter.PACKAGE);
			}
		}
		switch (n)
		{
			case 0:
				return I18N.msg (plural ? ProblemReporter.MEMBERS : ProblemReporter.MEMBER);
			case 1:
				return s[0];
			case 2:
				return I18N.msg (plural ? ProblemReporter.TWO_PLURALS : ProblemReporter.TWO_SINGULARS, s[0], s[1]);
			case 3:
				return I18N.msg (plural ? ProblemReporter.THREE_PLURALS : ProblemReporter.THREE_SINGULARS, s);
			default:
				throw new AssertionError ();
		}
	}

}
