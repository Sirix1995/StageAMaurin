
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

package de.grogra.grammar;

public class Locator extends org.xml.sax.helpers.LocatorImpl
{
	private String line;


	public Locator (org.xml.sax.Locator locator)
	{
		this (locator.getSystemId (), null, locator.getLineNumber (),
			  locator.getColumnNumber ());
	}


	public Locator (String systemId, String line,
					int lineNumber, int columnNumber)
	{
		super ();
		setSystemId (systemId);
		setLineNumber (lineNumber);
		setColumnNumber (columnNumber);
		this.line = line;
	}


	public Locator (String line, int lineNumber, int columnNumber)
	{
		this (null, line, lineNumber, columnNumber);
	}


	public Locator (String line, int columnNumber)
	{
		this (null, line, -1, columnNumber);
	}


	public Locator ()
	{
		this (null, null, -1, -1);
	}


	public final String getLine ()
	{
		return line;
	}


	public final void complete (String systemId, String line,
								int lineNumber, int columnNumber)
	{
		if (getSystemId () == null)
		{
			setSystemId (systemId);
		}
		if (this.line == null)
		{
			this.line = line;
		}
		if (getLineNumber () < 0)
		{
			setLineNumber (lineNumber);
		}
		if (getColumnNumber () < 0)
		{
			setColumnNumber (columnNumber);
		}
	}


	@Override
	public String toString ()
	{
		return getLineNumber () + ":" + getColumnNumber ();
	}

}
