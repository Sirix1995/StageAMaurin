
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

package de.grogra.ext.jedit;

import java.util.*;
import de.grogra.util.*;
import javax.swing.text.Segment;
import org.gjt.sp.jedit.Buffer;

public class StringFoldHandler
	extends org.gjt.sp.jedit.buffer.FoldHandler
{
	private final String[] opening, closing;
	private final boolean javaEscaping;


	public StringFoldHandler (String name, String[] opening, String[] closing,
							  boolean javaEscaping)
	{
		super (name);
		this.opening = opening;
		this.closing = closing;
		this.javaEscaping = javaEscaping;
	}


	public StringFoldHandler (String name, String opening, String closing,
							  boolean javaEscaping)
	{
		this (name, toArray (opening), toArray (closing), javaEscaping);
	}

	
	private static String[] toArray (String s)
	{
		ArrayList list = Collections.list (new StringTokenizer (s));
		return (String[]) list.toArray (new String[list.size ()]);
	}


	private static int find (String[] a, char[] c, int index, int end)
	{
	findLoop:
		for (int i = a.length - 1; i >= 0; i--)
		{
			String s = a[i];
			int len = s.length ();
			if (index + len <= end)
			{
				for (int j = 0; j < len; j++)
				{
					if (s.charAt (j) != c[index + j])
					{
						continue findLoop;
					}
				}
				return len;
			}
		}
		return 0;
	}


	@Override
	public int getFoldLevel (Buffer buffer, int lineIndex, Segment seg)
	{
		if (lineIndex == 0)
		{
			return 0;
		}
		else
		{
			buffer.getLineText (lineIndex - 1, seg);
			int level = buffer.getFoldLevel (lineIndex - 1),
				i = seg.offset,
				end = seg.offset + seg.count;
			boolean inString = false;
			while (i < end)
			{
				if (javaEscaping)
				{
					switch (seg.array[i])
					{
						case '\\':
							i += 2;
							continue;
						case '\'':
						case '"':
							inString = !inString;
							i++;
							continue;
					}
					if (inString)
					{
						i++;
						continue;
					}
				}
				int n = find (opening, seg.array, i, end);
				if (n > 0)
				{
					i += n;
					level++;
				}
				else if ((n = find (closing, seg.array, i, end)) > 0)
				{
					i += n;
					if (level > 0)
					{
						level--;
					}
				}
				else
				{
					i++;
				}
			}
			return level;
		}
	}

	
	@Override
	public boolean equals (Object o)
	{
		if (!(o instanceof StringFoldHandler))
		{
			return false;
		}
		StringFoldHandler h = (StringFoldHandler) o;
		return Arrays.equals (opening, h.opening)
			&& Arrays.equals (closing, h.closing)
			&& (javaEscaping == h.javaEscaping);
	}


	@Override
	public int hashCode ()
	{
		return Utils.hashCode (opening) ^ Utils.hashCode (closing);
	}

}
