/*
 * Copyright (C) 2013 GroIMP Developer Team
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

package de.grogra.components;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * A Transferable String object to be used with Drag & Drop applications within 
 * the component explorer and the ComponentView2D.
 * 
 * @author mhenke
 *
 */
public class TransferableComponentTreeNode implements Transferable
{

	private static final DataFlavor[] flavors = new DataFlavor[1];
	static {
		flavors[0] = DataFlavor.stringFlavor;
	}
	
	private final Object data;

	public TransferableComponentTreeNode (Object data)
	{
		this.data = data;
	}

	@Override
	public synchronized DataFlavor[] getTransferDataFlavors ()
	{
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported (DataFlavor flavor)
	{
		return (flavor == DataFlavor.stringFlavor);
	}

	@Override
	public synchronized Object getTransferData (DataFlavor flavor)
			throws UnsupportedFlavorException, IOException
	{
		if (isDataFlavorSupported (flavor))
		{
			return data;
		}
		else
		{
			throw new UnsupportedFlavorException (flavor);
		}
	}
}
