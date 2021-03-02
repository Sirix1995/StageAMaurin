
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

package de.grogra.pf.ui;

import java.io.*;

/**
 * A <code>Console</code> represents a <code>Panel</code> of
 * the GUI which provides textual input and output.
 * 
 * @author Ole Kniemeyer
 */
public interface Console extends Panel 
{
	/**
	 * A <code>ConsoleWriter</code> is a <code>PrintWriter</code>
	 * with the additional possibility to set the text color
	 * to use.
	 * 
	 * @author Ole Kniemeyer
	 */
	abstract class ConsoleWriter extends PrintWriter
	{
		public ConsoleWriter (Writer out, boolean autoFlush)
		{
			super (out, autoFlush);
		}
		
		/**
		 * Prints <code>text</code> using the color
		 * encoded in <code>color</code>. The <code>color</code>
		 * has to be specified in Java's default sRGB color model
		 * (<code>0xrrggbb</code>).
		 * 
		 * @param text text to print
		 * @param color color to use (<code>0xrrggbb</code>)
		 */
		public abstract void print (Object text, int color);

		/**
		 * Prints <code>text</code> using the color
		 * encoded in <code>color</code>, then
		 * terminates the line. The <code>color</code>
		 * has to be specified in Java's default sRGB color model
		 * (<code>0xrrggbb</code>).
		 * 
		 * @param text text to print
		 * @param color color to use (<code>0xrrggbb</code>)
		 */
		public abstract void println (Object text, int color);
	}

	
	/**
	 * Enters <code>text</code> as if the user had typed this text.
	 * 
	 * @param text text to enter in the console
	 */
	void enter (String text);
	
	/**
	 * Clears the console.
	 */
	void clear ();

	/**
	 * This method returns a <code>Reader</code> which can be
	 * used to obtain the textual input from the user.
	 * 
	 * @return a reader
	 */
	Reader getIn ();
	
	/**
	 * This method returns a <code>ConsoleWriter</code> which is
	 * used to write to the console. It should be used for normal
	 * messages.
	 * 
	 * @return a writer
	 */
	ConsoleWriter getOut ();
	
	/**
	 * This method returns a <code>ConsoleWriter</code> which is
	 * used to write to the console. It should be used for error
	 * messages. Implementations should use a different color
	 * to highlight the characters which are written through
	 * this writer.
	 * 
	 * @return a writer
	 */
	ConsoleWriter getErr ();

	void setNameCompletion (NameCompletion nc);
}
