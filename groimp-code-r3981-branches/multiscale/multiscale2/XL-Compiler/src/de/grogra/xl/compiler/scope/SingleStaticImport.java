
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

import de.grogra.reflect.Type;

public final class SingleStaticImport extends Scope
{
	private final Type importedType;
	private final String importedMembers;


	public SingleStaticImport (Scope enclosing, Type importedType, String member)
	{
		super (enclosing);
		this.importedType = importedType;
		this.importedMembers = member;
	}


	@Override
	public void findMembers (String name, int flags, Members list)
	{
		if (((flags & (Members.TYPE | Members.PREDICATE | Members.FIELD | Members.METHOD)) != 0)
			&& name.equals (importedMembers))
		{
			TypeScope.findMembers
				(importedType, null, name, flags | Members.STATIC_ONLY, list, this);
		}
		super.findMembers (name, flags, list);
	}


	public String getImportedMembers ()
	{
		return importedMembers;
	}


	public Type getImportedType ()
	{
		return importedType;
	}

}
