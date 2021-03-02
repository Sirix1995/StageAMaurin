
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

package de.grogra.ext.exchangegraph;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import de.grogra.graph.Graph;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.registry.Registry;
import de.grogra.rgg.RGGRoot;
import de.grogra.vfs.MemoryFileSystem;

public class ImportModule extends FilterBase implements ObjectSource {

	
	public ImportModule(FilterItem item, FilterSource source)  {
		
		super(item, source);
		setFlavor(IOFlavor.valueOf(Node.class));	
		
	}
	
	public Object getObject() throws IOException {
		IOContext ctx = new IOContext();
		de.grogra.rgg.Library.workbench().setProperty(IOContext.class.getName(), ctx);
		
		File f = ((FileSource) source).getInputFile ();
		String fn = f.getName();
		FileReader frr = new FileReader(f);
		
		Registry r = source.getRegistry();
		GraphManager graph = r.getProjectGraph();
		
		Node root = graph.getRoot();
		Edge e = root.getFirstEdge();
		Node rggRoot;
		if (e != null){
			rggRoot  = e.getTarget();
		}else{
			rggRoot = new RGGRoot();
			root.addEdgeBitsTo(rggRoot, Graph.BRANCH_EDGE, null);
		}
		de.grogra.rgg.Library.workbench().setProperty("lastRggRoot", rggRoot);
		
	    String xlCode;
		try {
			MemoryFileSystem fs = (MemoryFileSystem) r.getFileSystem();
			Object[] list = fs.listFiles(fs.getRoot());  
			xlCode = list[0].toString();
		} catch (Exception e1) {
			xlCode = null;
		}

		final String MODEL_NAME = "LocalModel";
		XEGImport importer = new XEGImport(fn, frr, rggRoot, ctx, xlCode, MODEL_NAME);
		importer.doImport();
		
		return rggRoot;
	}

}
