
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

import java.awt.datatransfer.*;

public final class DragDockableContext implements Transferable
{
	private static boolean flavorSet = false;
	private static DataFlavor FLAVOR = null;

/*
	private static String ENCODED_FLAVOR = null;


	static String getEncoded ()
	{
		if (ENCODED_FLAVOR == null)
		{
			ENCODED_FLAVOR
				= SystemFlavorMap.encodeDataFlavor (getFlavor ());
		}
		System.err.println ("#####-> " + ENCODED_FLAVOR);
		return ENCODED_FLAVOR;
	}


	static void loadMap ()
	{
		FlavorMap fm = SystemFlavorMap.getDefaultFlavorMap ();
		if (fm instanceof SystemFlavorMap)
		{
			SystemFlavorMap sfm = (SystemFlavorMap) fm;
			sfm.setFlavorsForNative (getEncoded (),
									 new DataFlavor[] {getFlavor ()});
			sfm.setNativesForFlavor (getFlavor (),
									 new String[] {getEncoded ()});
		}
	}


	private static final FlavorMap MAP = new FlavorMap ()
		{
			private static Map createFFNMap ()
			{
				TreeMap t = new TreeMap ();
				t.put (getEncoded (), getFlavor ());
				return t;
			}


			public Map getFlavorsForNatives (String[] natives)
			{
				System.err.println ("FFN " + natives);
				if (natives == null)
				{
					return createFFNMap ();
				}
				else
				{
					for (int i = natives.length - 1; i >= 0; i--)
					{
						System.err.println ("     " + natives[i]);
						if (getEncoded ().equals (natives[i]))
						{
							return createFFNMap ();
						}
					}
				}
				return new TreeMap ();
			}


			private static Map createNFFMap ()
			{
				TreeMap t = new TreeMap ();
				t.put (getFlavor (), getEncoded ());
				return t;
			}


			public Map getNativesForFlavors (DataFlavor[] flavors)
			{
				System.err.println ("NFF " + flavors);
				if (flavors == null)
				{
					return createNFFMap ();
				}
				else
				{
					for (int i = flavors.length - 1; i >= 0; i--)
					{
						System.err.println ("     " + flavors[i]);
						if (getFlavor ().equals (flavors[i]))
						{
							return createNFFMap ();
						}
					}
				}
				getEncoded ();
				return new TreeMap ();
			}
		};


	public static FlavorMap getFlavorMap ()
	{
		return MAP;
	}

*/

	public static DataFlavor getFlavor ()
	{
		if (!flavorSet)
		{
			try 
			{
				FLAVOR = new DataFlavor
					(DataFlavor.javaJVMLocalObjectMimeType + ";class="
					 + DragDockableContext.class.getName (), "Dockable",
					 DragDockableContext.class.getClassLoader ());
			}
			catch (ClassNotFoundException e)
			{
				throw new AssertionError (e);
			}
			flavorSet = true;
		}
		return FLAVOR;
	}


	private final DockManager manager;
	private final DockContainer dockContainer;
	private final DockableComponent dockableContainer;


	public DragDockableContext (DockManager manager,
								DockContainer dockContainer,
								DockableComponent dockableContainer)
	{
		this.manager = manager;
		this.dockContainer = dockContainer;
		this.dockableContainer = dockableContainer;
	}


	public DragDockableContext (DockManager manager,
								DockableComponent dockableContainer)
	{
		this (manager, DockManager.getDockParent (dockableContainer),
			  dockableContainer);
	}


	public DockManager getManager ()
	{
		return manager;
	}


	public DockContainer getDockContainer ()
	{
		return dockContainer;
	}


	public DockableComponent getDockableComponent ()
	{
		return dockableContainer;
	}


	public Dockable getDockable ()
	{
		return dockableContainer.getDockable ();
	}


	public Object getTransferData (DataFlavor flavor)
		throws UnsupportedFlavorException
	{
		if (getFlavor ().equals (flavor))
		{
			return this;
		}
		else if (DataFlavor.stringFlavor.equals (flavor))
		{
			return dockableContainer.getDockable ().getPanelTitle ();
		}
		else
		{
			throw new UnsupportedFlavorException (flavor);
		}
	}


	public boolean isDataFlavorSupported (DataFlavor flavor)
	{
		return getFlavor ().equals (flavor)
			|| DataFlavor.stringFlavor.equals (flavor);
	}


	public DataFlavor[] getTransferDataFlavors ()
	{
		return new DataFlavor[] {getFlavor (), DataFlavor.stringFlavor};
	}

}
