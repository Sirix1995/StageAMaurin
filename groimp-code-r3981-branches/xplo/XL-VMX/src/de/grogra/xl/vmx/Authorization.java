
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

package de.grogra.xl.vmx;

public final class Authorization 
{
	private String className;

	public Authorization ()
	{
		StackTraceElement[] trace = new Throwable ().getStackTrace ();
		if ((trace != null) && (trace.length >= 2))
		{
			className = trace[1].getClassName ();
			System.out.println (trace[0]);
			System.out.println (trace[1]);
			System.out.println (trace[1].getClassName ());
		}
		else
		{
			className = "?";
		}
	}

	public String toString ()
	{
		return "Authorization[" + className + ']';
	}
}
