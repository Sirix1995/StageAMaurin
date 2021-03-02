
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
import java.io.FileWriter;
import java.io.IOException;
import de.grogra.graph.impl.Node;
import de.grogra.pf.io.FileWriterSource;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.Workbench;

public class ExportModule extends FilterBase implements FileWriterSource {

	XEGExport exporter = null;
	
	public ExportModule(FilterItem item, FilterSource source) {
		super(item, source);
		setFlavor (item.getOutputFlavor ());


	}

	public void write(File out) throws IOException {
		
		// find root node of scene
		Node rootNode = UI.getRootOfProjectGraph(Workbench.current());
		// then find the rggRoot
		Node rggRoot = rootNode.getFirstEdge().getTarget();
		Node lastRggRoot = (Node) de.grogra.rgg.Library.workbench().getProperty("lastRggRoot");
		de.grogra.rgg.Library.workbench().setProperty("lastRggRoot", rggRoot);

		IOContext ctx = (IOContext)de.grogra.rgg.Library.workbench().getProperty(IOContext.class.getName());
		if (ctx == null || (ctx != null && !lastRggRoot.equals(rggRoot))){
			System.err.println("pass into the condition to make a new context instance!!");
			ctx= new IOContext();
		}
		exporter = new XEGExport(rggRoot, ctx);
		
		String graphString = null;
		try {
			graphString = exporter.doExport();
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileWriter fw = new FileWriter(out);
		fw.write(graphString);
		fw.close();
	}

}
