
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

import de.grogra.xl.expr.*;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.reflect.TypeId;

public final class DefiniteAssignment
{
	private DefiniteAssignment ()
	{
	}


	public static boolean isAssignedBefore (Object v, Expression e,
											boolean unassigned)
	{
/*		if ((v instanceof Local) && (((Local) v).getScopeExpr () == e))
		{
			return unassigned ^ ((Local) v).isParameter ();
		}
		*/
		Expression a = (Expression) e.getPredecessor ();
		Expression p = (Expression) ((a != null) ? a : e).getAxisParent ();
		if (a != null)
		{
			if (p instanceof ConditionalAnd)
			{
				return isAssignedAfter (v, a, true, unassigned);
			}
			else if (p instanceof ConditionalOr)
			{
				return isAssignedAfter (v, a, false, unassigned);
			}
			else if ((p instanceof Conditional) || (p instanceof If))
			{
				boolean b = (a == (a = p.getFirstExpression ()));
				return isAssignedAfter (v, a, b, unassigned);
			}
			else
			{
				return isAssignedAfter (v, a, unassigned);
			}
		}
		else if (p != null)
		{
			return isAssignedBefore (v, p, unassigned);
		}
		else
		{
			return unassigned;
		}
	}


	public static boolean isAssignedAfter (Object v, Expression e,
										   boolean unassigned)
	{
		if (e instanceof AssignField)
		{
			if (((AssignField) e).getField () == v)
			{
				return !unassigned;
			}
			else
			{
				return isAssignedAfter (v, e.getLastExpression (), unassigned);
			}
		}
		else if ((v instanceof Local)
				 && ((Local) v).isAccessed (e, LocalAccess.ASSIGNS_LOCAL))
		{
			return !unassigned;
		}
		else if ((e.etype == TypeId.BOOLEAN) && !(e instanceof Assignment))
		{
			return isAssignedAfter (v, e, true, unassigned)
				&& isAssignedAfter (v, e, false, unassigned);
		}
		else if ((e instanceof Conditional)
				 || ((e instanceof If) && (e.getExpressionCount () == 3)))
		{
			return isAssignedAfter (v, e.getExpression (1), unassigned)
				&& isAssignedAfter (v, e.getExpression (2), unassigned);
		}
		else if (e instanceof If)
		{
			return isAssignedAfter (v, e.getExpression (1), unassigned)
				&& isAssignedAfter (v, e.getExpression (0), false, unassigned);
		}
		else
		{
			return isAssignedAfter0 (v, e, unassigned);
		}
	}


	private static boolean isAssignedAfter (Object v, Expression e, boolean b,
											boolean unassigned)
	{
		if (e instanceof Assignment)
		{
			return isAssignedAfter (v, e, unassigned);
		}
		if (e instanceof Constant)
		{
			return (e.evaluateBoolean (null) != b)
				|| isAssignedBefore (v, e, unassigned);
		}
		else if (e instanceof ConditionalAnd)
		{
			if (b)
			{
				return isAssignedAfter (v, e.getExpression (1), true,
										unassigned);
			}
			else
			{
				return isAssignedAfter (v, e.getExpression (0), false,
										unassigned)
					&& isAssignedAfter (v, e.getExpression (1), false,
										unassigned);
			}
		}
		else if (e instanceof ConditionalOr)
		{
			if (b)
			{
				return isAssignedAfter (v, e.getExpression (0), true,
										unassigned)
					&& isAssignedAfter (v, e.getExpression (1), true,
										unassigned);
			}
			else
			{
				return isAssignedAfter (v, e.getExpression (1), false,
										unassigned);
			}
		}
		else if (e instanceof Not)
		{
			return isAssignedAfter (v, e.getExpression (0), !b, unassigned);
		}
		else if (e instanceof Conditional)
		{
			return isAssignedAfter (v, e.getExpression (1), b, unassigned)
				&& isAssignedAfter (v, e.getExpression (2), b, unassigned);
		}
		else
		{
			return isAssignedAfter0 (v, e, unassigned);
		}
	}


	private static boolean isAssignedAfter0 (Object v, Expression e,
											 boolean unassigned)
	{
		Expression l = e.getLastExpression ();
		if (l == null)
		{
			return isAssignedBefore (v, e, unassigned);
		}
		else
		{
			return isAssignedAfter (v, l, unassigned);
		}
	}

}
