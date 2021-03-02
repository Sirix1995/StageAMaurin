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

package de.grogra.msml;

import java.io.IOException;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Node;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ProjectLoader;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.vfs.MemoryFileSystem;

public class MSMLProjectLoader extends FilterBase implements ObjectSource, ProjectLoader
{
	public MSMLProjectLoader (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (IOFlavor.PROJECT_LOADER);
	}

	public Object getObject ()
	{
		return this;
	}

	private MSMLReader reader;
	private Node result;

	public void loadRegistry (Registry r) throws IOException
	{
		r.setEmptyGraph ();
		r.initFileSystem(new MemoryFileSystem(de.grogra.pf.io.IO.PROJECT_FS));
		reader = new MSMLReader(r, (FilterItem) ((Item) item.getAxisParent()).getItem("msml"), source);
		result = (Node) reader.getObject ();
	}


	public void loadGraph (Registry r) throws IOException
	{
		((Node)r.getProjectGraph().getRoot(Graph.MAIN_GRAPH))
		.addEdgeBitsTo(result, Graph.BRANCH_EDGE, null);
	}

}
