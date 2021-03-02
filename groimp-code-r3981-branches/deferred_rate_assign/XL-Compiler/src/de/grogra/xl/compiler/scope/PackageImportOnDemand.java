
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

import de.grogra.xl.compiler.scope.Package;

public final class PackageImportOnDemand extends ImportOnDemandScope
{
	private final Package imported;


	public PackageImportOnDemand (Scope enclosing, Package imported)
	{
		super (enclosing);
		this.imported = imported;
	}


	@Override
	public void findMembers (String name, int flags, Members list)
	{
		super.findMembers (name, flags, list);
		if (((flags & Members.DECLARED_ONLY) == 0)
			&& ((flags &= (Members.TYPE | Members.PREDICATE)) != 0))
		{
			imported.findMembers (name, flags | Members.DECLARED_ONLY, list, this);
		}
	}


	@Override
	public String toString ()
	{
		return imported.getName ();
	}

}

