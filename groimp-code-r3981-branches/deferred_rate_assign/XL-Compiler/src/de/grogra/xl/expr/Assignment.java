
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

package de.grogra.xl.expr;

import de.grogra.reflect.*;

public abstract class Assignment extends Expression
{
	public static final int SIMPLE = 0;
	public static final int COMPOUND = 1;
	public static final int POSTFIX_COMPOUND = 2;

	public final int assignmentType;


	public Assignment (Type type, int assignmentType)
	{
		super (type);
		this.assignmentType = assignmentType;
	}


	@Override
	public boolean allowsIteration (int index)
	{
		return (index == 0) || (assignmentType == SIMPLE);
	}


	@Override
	protected String paramString ()
	{
		return super.paramString ()
			+ ((assignmentType == SIMPLE) ? ",simple"
			   : (assignmentType == COMPOUND) ? ",compound" : ",postfix");
	}

}
