
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

import de.grogra.reflect.Type;
import de.grogra.xl.compiler.scope.TypeScope;

public interface Extension
{
	void preprocess (TypeScope scope, int run);

	void postprocess (TypeScope scope, int run);

	/**
	 * Should a default constructor with no arguments be declared
	 * in the given <code>type</code>?
	 * Returns <code>true</code> iff the class declaration for
	 * <code>type</code> has to
	 * provide a default constructor with no arguments. If no such
	 * constructor is declared explicitly, an implicit declaration has
	 * to be added by the compiler.
	 * 
	 * @param type the current state of the class declaration
	 * @return <code>true</code> iff a constructor with no arguments is required 
	 */
	boolean forcesDefaultConstructorFor (Type<?> type);

}
