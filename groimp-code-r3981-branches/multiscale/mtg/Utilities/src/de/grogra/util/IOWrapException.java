
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

package de.grogra.util;

import org.xml.sax.SAXException;

public class IOWrapException extends java.io.IOException
{

	public IOWrapException (Throwable cause)
	{
		this (cause, null);
	}


	public IOWrapException (Throwable cause, String message)
	{
		super (message);
		initCause (cause);
	}


	public IOWrapException (SAXException cause)
	{
		this (cause, null);
	}

	
	public IOWrapException (SAXException cause, String message)
	{
		super (message);
		initCause (cause);
		if ((cause.getException() != null) && (cause.getCause () == null))
		{
			cause.initCause (cause.getException ());
		}
	}


	public IOWrapException (String message)
	{
		super (message);
	}
	
	
	@Override
	public String getMessage ()
	{
		String s = super.getMessage ();
		if (s != null)
		{
			return s;
		}
		Throwable t = getCause ();
		return (t != null) ? t.getMessage () : null;
	}
}
