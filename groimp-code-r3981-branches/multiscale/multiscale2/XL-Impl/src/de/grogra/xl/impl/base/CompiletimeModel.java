
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

package de.grogra.xl.impl.base;

import de.grogra.reflect.*;
import de.grogra.xl.lang.*;
import de.grogra.xl.query.*;

public abstract class CompiletimeModel implements de.grogra.xl.query.CompiletimeModel
{

	private final String runtimeConstant;


	public CompiletimeModel (String runtimeConstant)
	{
		this.runtimeConstant = runtimeConstant;
	}


	public boolean needsWrapperFor (Type<?> type)
	{
		if (Reflection.isSupertypeOrSame (getNodeType (), type))
		{
			return false;
		}
		if (type instanceof IntersectionType)
		{
			return !Reflection.equal (type.getSupertype (), Type.OBJECT); 
		}
		return (type.getModifiers () & Member.INTERFACE) == 0;
	}


	public Type<?> getEdgeTypeFor (Type<?> type)
	{
		return getEdgeType ();
	}


	public Type<?> getEdgeType ()
	{
		return Type.INT;
	}


	private static final Integer ANY = Integer.valueOf (-1);
	private static final Integer BRANCH = Integer.valueOf (RuntimeModel.BRANCH_EDGE);
	private static final Integer SUCCESSOR = Integer.valueOf (RuntimeModel.SUCCESSOR_EDGE);
	private static final Integer REFINEMENT = Integer.valueOf (RuntimeModel.REFINEMENT_EDGE);

	public java.io.Serializable getStandardEdgeFor (int edgeType)
	{
		switch (edgeType)
		{
			case EdgePattern.ANY_EDGE:
				return ANY;
			case EdgePattern.BRANCH_EDGE:
				return BRANCH;
			case EdgePattern.SUCCESSOR_EDGE:
				return SUCCESSOR;
			case EdgePattern.REFINEMENT_EDGE:
				return REFINEMENT;
			default:
				throw new AssertionError (edgeType);
		}
	}


	public de.grogra.xl.property.CompiletimeModel.Property getWrapProperty (Type<?> wrapperType)
	{
		return null;
	}


	private static final Type<Graph.QState> QSTATE = ClassAdapter.wrap (Graph.QState.class);

	public Type<Graph.QState> getQueryStateType ()
	{
		return QSTATE;
	}


	public String getRuntimeName ()
	{
		return runtimeConstant;
	}

}
