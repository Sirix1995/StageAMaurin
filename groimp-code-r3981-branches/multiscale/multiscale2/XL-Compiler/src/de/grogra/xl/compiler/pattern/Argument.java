
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

package de.grogra.xl.compiler.pattern;

import de.grogra.reflect.*;
import de.grogra.xl.compiler.scope.*;

public final class Argument
{
	final Place place;
	final Local local;
	boolean node = false;
	boolean context = false;
	
	private final int argId;


	Argument (Place place, Local local)
	{
		this.place = place;
		this.local = local;
		this.argId = place.getBuilder ().nextArgumentId ();
	}
	
	
	public Type getType ()
	{
		return local.getType ();
	}


	@Override
	public String toString ()
	{
		return "Param " + argId + " [" + place + ',' + local + ',' + node + ']';
	}

}
