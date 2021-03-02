
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

public class Sequence
{
	private final boolean escape;
	private final boolean singleLine;
	private final boolean createContent;
	private final String end;


	public Sequence (String end, boolean escape, boolean singleLine,
					 boolean createContent)
	{
		this.end = end;
		this.escape = escape;
		this.singleLine = singleLine;
		this.createContent = createContent;
	}


	public Token getToken (Input in)
		throws LexicalException, java.io.IOException
	{
		StringBuffer b = createContent ? in.getTmpBuffer () : null;
		int d0, d1, d2, endMatched = 0;
		int endLength = end.length ();
		int c;
		while (true)
		{
			c = in.getChar ();
			if (end.charAt (endMatched) == c)
			{
				if (++endMatched == endLength)
				{
					return createLiteral (b);
				}
			}
			else if (endMatched > 0)
			{
				while (endMatched > 0)
				{
					in.ungetChar ();
					endMatched--;
				}
				if (b != null)
				{
					in.ungetChar ();
					b.append ((char) in.getChar ());
				}
			}
			else
			{
				switch (c)
				{
					case Input.EOL:
						if (!singleLine)
						{
							break;
						}
						// no break
					case Input.EOF:
						in.ungetChar ();
						throw createException (end);
					case '\\':
						if (!escape)
						{
							break;
						}
						c = in.getChar ();
						switch (c)
						{
							case Input.EOL:
								in.ungetChar ();
								throw createException (end);
							case 'b':
								c = '\b';
								break;
							case 'f':
								c = '\f';
								break;
							case 'n':
								c = '\n';
								break;
							case 'r':
								c = '\r';
								break;
							case 't':
								c = '\t';
								break;
							case '\'':
							case '\"':
							case '\\':
								break;
							default:
								if (((d0 = c - '0') < 0) || (d0 >= 8))
								{
									throw new LexicalException
										(Tokenizer.I18N.msg ("grammar.illegal-escape"));
								}
								c = d0;
								if (((d1 = in.getChar () - '0') < 0) || (d1 >= 8))
								{
									in.ungetChar ();
									break;
								}
								c = ((c << 3) + d1);
								if (((d2 = in.getChar () - '0') < 0) || (d2 >= 8))
								{
									in.ungetChar ();
									break;
								}
								c = ((c << 3) + d2);
								break;
						}
						break;
				}
				if (b != null)
				{
					b.append ((char) c);
				}
			}
		}
	}


	protected Token createLiteral (CharSequence content)
		throws LexicalException
	{
		return null;
	}

	
	public static LexicalException createException (String terminating)
	{
		return new LexicalException
			(Tokenizer.I18N.msg ("grammar.terminating-not-found",
								 terminating));
	}
}
