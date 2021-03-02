
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

package de.grogra.docking;

public interface LayoutConsumer
{
	LayoutConsumer DUMP_LAYOUT = new LayoutConsumer ()
		{
			private int depth = 0;

			public void startLayout ()
			{
				start ("Layout");
			}

			public void endLayout ()
			{
				end ();
			}

			public void startMainWindow ()
			{
				start ("MainWindow");
			}

			public void endMainWindow ()
			{
				end ();
			}

			public void startFloatingWindow (String title, int width,
											 int height)
			{
				start ("FloatingWindow[" + title + ',' + width + 'x'
					   + height + ']');
			}

			public void endFloatingWindow ()
			{
				end ();
			}

			public void startSplit (int orientation, float location)
			{
				start ("Split[" + orientation + ',' + location + ']');
			}

			public void endSplit ()
			{
				end ();
			}

			public void startTabbed (int selectedIndex)
			{
				start ("Tabbed[" + selectedIndex + ']');
			}

			public void endTabbed ()
			{
				end ();
			}

			public void addDockable (Dockable dockable)
			{
				start ("Dockable[" + dockable.getClass ().getName () + ']');
				end ();
			}

			private void start (String s)
			{
				for (int i = 0; i < depth; i++)
				{
					System.err.print ("  ");
				}
				System.err.println (s);
				depth++;
			}

			private void end ()
			{
				depth--;
			}
		};

	void startLayout ();
	
	void endLayout ();

	void startMainWindow ();

	void endMainWindow ();

	void startFloatingWindow (String title, int width, int height);

	void endFloatingWindow ();

	void startSplit (int orientation, float location);

	void endSplit ();

	void startTabbed (int selectedIndex);

	void endTabbed ();

	void addDockable (Dockable dockable);
}
