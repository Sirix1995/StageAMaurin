
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

package de.grogra.pf.io;

import java.io.*;

public class IndentWriter extends FilterWriter
{
	private int indentation = 0;
	private final int indentAmount;
	private boolean indented = false;
	private final String lineSeparator;


	public IndentWriter (Writer out, int indentAmount)
	{
		super (out);
		this.indentAmount = indentAmount;
		this.lineSeparator = System.getProperty ("line.separator");
	}


	public void print (String s) throws IOException
	{
		checkIndentation ();
		write (s);
	}


	public void print (char c) throws IOException
	{
		checkIndentation ();
		write (c);
	}


	public void print (int i) throws IOException
	{
		checkIndentation ();
		if (i == 0)
		{
			write ('0');
			return;
		}
		if (i < 0)
		{
			write ('-');
			i = -i;
		}
		print0 (i);
	}


	private void print0 (int i) throws IOException
	{
		if (i > 0)
		{
			print0 (i / 10);
			write ((i % 10) + '0');
		}
	}


	public void print (float f) throws IOException
	{
		checkIndentation ();
		if (f == (int) f)
		{
			print ((int) f);
		}
		else
		{
			write (String.valueOf (f));
		}
	}


	public void print (double d) throws IOException
	{
		checkIndentation ();
		if (d == (int) d)
		{
			print ((int) d);
		}
		else
		{
			write (String.valueOf (d));
		}
	}


	public void println () throws IOException
	{
		indented = false;
		write (lineSeparator);
	}


	public void indent ()
	{
		indentation += indentAmount;
	}


	public void unindent ()
	{
		indentation -= indentAmount;
	}


	protected void checkIndentation () throws IOException
	{
		if (!indented)
		{
			for (int i = 0; i < indentation; i++)
			{
				write (' ');
			}
			indented = true;
		}
	}

}
