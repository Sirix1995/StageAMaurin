
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

import de.grogra.xl.compiler.scope.Local;

public interface LocalAccess extends Completable
{
	int PRE_USE = 1;
	int POST_USE = 2;
	int PRE_ASSIGNMENT = 4;
	int PRE_1_ASSIGNMENT = 8;
	int PRE_2_ASSIGNMENT = 16;
	int POST_ASSIGNMENT = 32;

	int USES_LOCAL = PRE_USE | POST_USE;
	int ASSIGNS_LOCAL = PRE_ASSIGNMENT | PRE_1_ASSIGNMENT | PRE_2_ASSIGNMENT | POST_ASSIGNMENT;


	int getLocalCount ();

	int getAccessType (int index);

	Local getLocal (int index);

	void setLocal (int index, Local local);
}
