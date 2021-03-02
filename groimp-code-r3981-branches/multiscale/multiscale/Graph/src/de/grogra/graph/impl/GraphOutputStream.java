
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

package de.grogra.graph.impl;

import java.io.*;
import de.grogra.persistence.*;

class GraphOutputStream extends PersistenceOutputDecorator implements GraphOutput
{
	static final int IO_ROOT = 0xe80fb4af;
	static final int IO_NODE_BEGIN = 0x09;
	static final int IO_NODE_END = 0x73;
	static final int IO_EDGE = 0xda;


	private final PersistenceOutputStream sout;

	public GraphOutputStream (PersistenceOutputStream out) throws IOException
	{
		super (out);
		this.sout = out;
	}


	public void beginExtent (GraphManager manager, int rootCount) throws IOException
	{
		sout.beginExtent (manager);
		sout.writeInt (rootCount);
	}


	public void endExtent () throws IOException
	{
		sout.endExtent ();
	}


	public void beginRoot (String name) throws IOException
	{
		sout.writeInt (IO_ROOT);
		sout.writeUTF (name);
	}


	public void endRoot (String name)
	{
	}


	public void beginNode (Node node, Edge edge) throws IOException
	{
		if (edge != null)
		{
			sout.write (IO_EDGE);
			sout.writeInt (edge.edgeBits);
		}
		else
		{
			sout.write (IO_NODE_BEGIN);
		}
		sout.writePersistentObjectReference (node);
	}


	public void endNode (Node node) throws IOException
	{
		sout.write (IO_NODE_END);
	}

}
