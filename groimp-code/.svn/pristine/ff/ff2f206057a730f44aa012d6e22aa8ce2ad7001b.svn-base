
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

package de.grogra.tools;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ReferencePath extends ArrayList<ReferencePath>
{
	String line;
	ReferencePath parent;


	public static void main (String[] args) throws IOException
	{
		BufferedReader in
			= new BufferedReader (new InputStreamReader (System.in));
		String s, next;
		next = in.readLine ();
		ArrayList<ReferencePath> roots = new ArrayList<ReferencePath> ();
		HashMap<Integer,ReferencePath> idToObj = new HashMap<Integer,ReferencePath> ();
		while ((s = next) != null)
		{
			if (s.startsWith ("ROOT"))
			{
				int rootId = (int) Long.parseLong
					(s.substring (5, s.indexOf (' ', 5)), 16);
				ReferencePath o = new ReferencePath ();
				o.parent = o;
				idToObj.put (rootId, o);
				roots.add (o);
				next = in.readLine ();
			}
			else if (s.startsWith ("OBJ") || s.startsWith ("ARR")
					 || s.startsWith ("CLS"))
			{
				int id = (int) Long.parseLong
					(s.substring (4, s.indexOf (' ', 4)), 16);
				ReferencePath o = idToObj.get (id);
				if (o == null)
				{
					o = new ReferencePath ();
					idToObj.put (id, o);
				}
				o.line = s;
				while (((s = in.readLine ()) != null)
					   && (s.charAt (0) == '\t'))
				{
					int k = s.indexOf ('\t', 2);
					while (s.charAt (k) == '\t')
					{
						k++;
					}
					k = (int) Long.parseLong (s.substring (k), 16);
					ReferencePath r = idToObj.get (k);
					if (r == null)
					{
						r = new ReferencePath ();
						idToObj.put (k, r);
					}
					o.add (r);
				}
				next = s;
			}
			else
			{
				next = in.readLine ();
			}
		}
		ArrayList<ReferencePath> nextRoots = new ArrayList<ReferencePath> ();
		while (!roots.isEmpty ())
		{
			for (int i = roots.size () - 1; i >= 0; i--)
			{
				ReferencePath r = roots.get (i);
				for (int j = r.size () - 1; j >= 0; j--)
				{
					ReferencePath c = r.get (j);
					if (c.parent == null)
					{
						c.parent = r;
						nextRoots.add (c);
					}
				}
			}
			ArrayList<ReferencePath> t = roots;
			roots = nextRoots;
			nextRoots = t;
			nextRoots.clear ();
		}
		for (int i = 0; i < args.length; i++)
		{
			int d = (int) Long.parseLong (args[i], 16);
			ReferencePath o = idToObj.get (d);
			if (o == null)
			{
				System.out.println ("Object " + args[i] + " does not exist!");
			}
			else
			{
				System.out.println ("Path from " + args[i] + " to root:");
				while (true)
				{
					System.out.println (o.line);
					if (o == o.parent)
					{
						break;
					}
					o = o.parent;
					if (o == null)
					{
						System.out.println ("No path to root exists!");
						break;
					}
				}
			}
			System.out.println ();
		}
	}

}
