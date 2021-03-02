
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

public class Token extends antlr.CommonToken
{
	public static final int BOOLEAN_LITERAL = 4;
	public static final int INT_LITERAL = 5;
	public static final int LONG_LITERAL = 6;
	public static final int FLOAT_LITERAL = 7;
	public static final int DOUBLE_LITERAL = 8;
	public static final int CHAR_LITERAL = 9;
	public static final int STRING_LITERAL = 10;
	public static final int IDENT = 11;

	public static final int MIN_NUMBER_LITERAL = INT_LITERAL;
	public static final int MAX_NUMBER_LITERAL = DOUBLE_LITERAL;

	public static final int MIN_UNUSED = 12;

	static final int SEQUENCE_START = -1;


	private Tokenizer tokenizer = null;


	static
	{
		if (BOOLEAN_LITERAL != MIN_USER_TYPE)
		{
			throw new AssertionError
				("antlr.Token.MIN_USER_TYPE has changed");
		}
	}


	public Token (int type, String text)
	{
		super (type, text);
		setLine (-1);
	}


	private static final Class tokenClass = Token.class;

	public Token dup ()
	{
		if (getClass () == tokenClass)
		{
			Token t = new Token (type, text);
			t.col = col;
			t.line = line;
			t.tokenizer = tokenizer;
			return t;
		}
		else
		{
			try
			{
				return (Token) clone ();
			}
			catch (CloneNotSupportedException e)
			{
				throw new AssertionError (e);
			}
		}
	}


	@Override
	public final String getFilename ()
	{
		return (tokenizer == null) ? null : tokenizer.getInput ().getSourceName ();
	}


	public final void setTokenizer (Tokenizer t)
	{
		tokenizer = t;
	}


	public final Tokenizer getTokenizer ()
	{
		return tokenizer;
	}


	public final void setExtent (int position, int endPosition)
	{
		line = position;
		col = endPosition;
	}


	public final int getStartPosition ()
	{
		return line;
	}


	public final int getEndPosition ()
	{
		return col;
	}

	
	@Override
	public final int getLine ()
	{
		Input in = tokenizer.getInput ();
		return in.getLineAt (line);
	}


	@Override
	public final int getColumn ()
	{
		Input in = tokenizer.getInput ();
		return in.getColumnAt (line, 1);
	}

/*	
	public final String getLineString ()
	{
		return null;//(tokenizer != null) ? tokenizer.getLine (getLine ()) : null;
	}

	
	public final String getTextInLine ()
	{
		return /*(tokenizer != null) ? tokenizer.getString (position, endPosition)
			:* / null;
	}
*/
}
