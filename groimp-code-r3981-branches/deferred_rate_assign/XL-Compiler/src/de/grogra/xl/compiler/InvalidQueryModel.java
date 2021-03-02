
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

import java.io.Serializable;

import de.grogra.reflect.Type;
import de.grogra.xl.property.CompiletimeModel.Property;
import de.grogra.xl.query.CompiletimeModel;
import de.grogra.xl.query.Producer;
import de.grogra.xl.query.QueryState;

public final class InvalidQueryModel implements CompiletimeModel
{
	public static final InvalidQueryModel INSTANCE = new InvalidQueryModel ();
	
	public Type<?> getEdgeType ()
	{
		return Type.INVALID;
	}

	public Type<?> getEdgeTypeFor (Type<?> type)
	{
		return type;
	}

	public Type<?> getNodeType ()
	{
		return Type.INVALID;
	}

	public Type<? extends Producer> getProducerType ()
	{
		return Type.INVALID;
	}

	public Type<? extends QueryState> getQueryStateType ()
	{
		return Type.INVALID;
	}

	public String getRuntimeName ()
	{
		return "";
	}

	public Serializable getStandardEdgeFor (int edge)
	{
		return Integer.valueOf (edge);
	}

	public Property getWrapProperty (Type<?> wrapperType)
	{
		return null;
	}

	public Type<?> getWrapperTypeFor (Type<?> type)
	{
		return null;
	}

	public boolean needsWrapperFor (Type<?> type)
	{
		return false;
	}
}
