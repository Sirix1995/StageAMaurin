
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

import java.io.*;
import de.grogra.util.*;
import de.grogra.xl.util.IntList;

public final class Input implements CharSequence
{
	public static final int EOF = -1;
	public static final char EOL = '\n';

	private String sourceName;

	private IntList buffer = new IntList (5000);
	private int position, lineCount;
	private final boolean ignoreUnicodeEscapes;
	private Int2IntMap unicodeEscapes = new Int2IntMap ();
	private IntList lineStart = new IntList ();

	private Reader source;

	private final StringBuffer tmpBuffer = new StringBuffer ();

	
	public Input (boolean ignoreUnicodeEscapes)
	{
		this.ignoreUnicodeEscapes = ignoreUnicodeEscapes;
	}


	public String getSourceName ()
	{
		return sourceName;
	}


	public void setSource (Reader source, String name)
	{
		this.source = source;
		sourceName = name;
		buffer.setSize (0);
		unicodeEscapes.clear ();
		unicodeEscapes.put (Integer.MAX_VALUE, 0);
		lineCount = 0;
		position = 0;
		lineStart.set (0, 0);
	}


	public boolean isClosed ()
	{
		return source == null;
	}


	public void close () throws IOException
	{
		try
		{
			while ((source != null) && (getChar () != EOL))
			{
			}
		}
		catch (LexicalException e)
		{
		}
		if (source != null)
		{
			source.close ();
			source = null;
		}
	}


	public int getPosition ()
	{
		return position;
	}


	public void reset (int position)
	{
		this.position = position;
	}


	public void ungetChar ()
	{
		position--;
	}
	
	
	private int unget = -2;
	private int bsCount = 0;

	private int getc () throws IOException, LexicalException
	{
		int c = unget;
		if (c != -2)
		{
			unget = -2;
		}
		else
		{
			c = source.read ();
		}
		if (c == '\\')
		{
			if ((++bsCount & 1) != 0)
			{
				c = source.read ();
				if (ignoreUnicodeEscapes || (c != 'u'))
				{
					unget = c;
					c = '\\';
				}
				else
				{
					c = 0;
					int uc = 0;
					int nd = 0, nu = 1;
					while (nd < 4)
					{
						int i = source.read ();
						if ((i == 'u') && (nd == 0))
						{
							nu++;
						}
						else
						{
							uc = (uc << 1) + ((i < 'a') ? 1 : 0);
							i = Utils.fromHexDigit (i);
							if (i < 0)
							{
								throw new LexicalException ("Illegal Unicode");
							}
							nd++;
							c = (c << 4) + i;
						}
					}
					c |= ((nu + 5) << 20) + (uc << 16);
					bsCount = 0;
				}
			}
		}
		else
		{
			bsCount = 0;
		}
		return c;
	}


	public int getChar () throws IOException, LexicalException
	{
		if (position == buffer.size ())
		{
			int c = getc ();
			if (c < 0)
			{
				c = EOF;
				source.close ();
				source = null;
				buffer.push (EOL).push (EOF);
			}
			else if ((char) c == '\r')
			{
				c = getc ();
				if (c != EOL)
				{
					unget = c;
					bsCount = 0;
					c = EOL;
				}
			}
			if (c >= 0)
			{
				buffer.push ((char) c);
				if ((char) c == EOL)
				{
					lineStart.set (++lineCount, position + 1);
				}
				if (c != (char) c)
				{
					unicodeEscapes.put (position, c >> 16);
				}
			}
			lineStart.set (lineCount + 1, position + 2);
		}
		return buffer.get (position++);
	}


	private static final int CS_LENGTH = 256;
	private static final String[] CHAR_STRINGS;

	static
	{
		CHAR_STRINGS = new String[CS_LENGTH];
		char[] a = new char[CS_LENGTH];
		for (char i = 0; i < CS_LENGTH; i++)
		{
			a[i] = i;
		}
		String s = new String (a);
		for (int i = 0; i < CS_LENGTH; i++)
		{
			CHAR_STRINGS[i] = s.substring (i, i + 1);
		}
	}

	
	public CharSequence subSequence (int start, int end)
	{
		return substring (start, end);
	}


	public String substring (int start)
	{
		return substring (start, position);
	}


	private char[] tmpChar = null;

	public String substring (int start, int end)
	{
		switch (end - start)
		{
			case 0:
				return "";
			case 1:
				int c = buffer.get (start);
				if ((c >= 0) && (c < CS_LENGTH))
				{
					return CHAR_STRINGS[c];
				}
				// no break
			default:
				end -= start;
				char[] a;
				if (((a = tmpChar) == null) || (a.length < end))
				{
					tmpChar = a = new char[end];
				}
				for (int i = end - 1; i >= 0; i--)
				{
					a[i] = charAt (start + i);
				}
				return new String (a, 0, end);
		}
	}


	@Override
	public String toString ()
	{
		return substring (0, position);
	}

	
	public int length ()
	{
		return position;
	}

	
	public char charAt (int index)
	{
		index = buffer.get (index);
		return (index < 0) ? '\032' : (char) index;
	}


	public int getLineCount ()
	{
		return lineCount;
	}


	public int getLineAt (int position)
	{
		position = lineStart.binarySearch (position);
		return (position >= 0) ? position : ~position - 1;
	}
	
	
	public int getColumnAt (int position, int tabWidth)
	{
		int line = getLineAt (position);
		int tp = 0;
		int p = lineStart.elements[line];
		int esc = unicodeEscapes.findIndex (p);
		if (esc < 0)
		{
			esc = ~esc;
		}
		int escPos = unicodeEscapes.getKeyAt (esc);
		while (p < position)
		{
			char c = charAt (p);
			if (p++ == escPos)
			{
				tp += unicodeEscapes.getValueAt (esc) >> 4;
				escPos = unicodeEscapes.getKeyAt (++esc);
			}
			else if (c == '\t')
			{
				tp = (tp / tabWidth + 1) * tabWidth;
			}
			else
			{
				tp++;
			}
		}
		return tp;
	}

	
	public int getLineLength (int line, int tabWidth)
	{
		return getColumnAt (lineStart.elements[line + 1] - 1, tabWidth);
	}


	public String getTextForLine (int line)
	{
		tmpBuffer.setLength (0);
		int p = lineStart.elements[line];
		int n = lineStart.elements[line + 1] - 1;
		int esc = unicodeEscapes.findIndex (p);
		if (esc < 0)
		{
			esc = ~esc;
		}
		int escPos = unicodeEscapes.getKeyAt (esc);
		while (p < n)
		{
			char c = charAt (p);
			if (p++ == escPos)
			{
				int h = unicodeEscapes.getValueAt (esc);
				escPos = unicodeEscapes.getKeyAt (++esc);
				tmpBuffer.append ('\\');
				for (int i = h >> 4; i > 5; i--)
				{
					tmpBuffer.append ('u');
				}
				for (int i = 4; i > 0; i--)
				{
					tmpBuffer.append (Utils.toHexDigit (c >> 12, (h & 8) != 0));
					c <<= 4;
					h <<= 1;
				}
			}
			else
			{
				tmpBuffer.append (c);
			}
		}
		return tmpBuffer.toString ();
	}

	
	public int getPositionOfLine (int line)
	{
		return lineStart.elements[line];
	}


	public StringBuffer getTmpBuffer ()
	{
		tmpBuffer.setLength (0);
		return tmpBuffer;
	}

}
