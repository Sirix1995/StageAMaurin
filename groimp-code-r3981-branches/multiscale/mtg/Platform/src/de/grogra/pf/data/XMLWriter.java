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

package de.grogra.pf.data;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.OutputStreamSource;
import de.grogra.util.MimeType;

public class XMLWriter extends FilterBase implements OutputStreamSource {
	
	public static final IOFlavor FLAVOR = new IOFlavor (MimeType.TEXT_XML, IOFlavor.OUTPUT_STREAM, null);

	public XMLWriter(FilterItem item, FilterSource source) {
		super(item, source);
		setFlavor(FLAVOR);
	}

	public void write(OutputStream out) throws IOException {
		Dataset ds = (Dataset) ((ObjectSource) source).getObject();
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		int column = ds.getColumnCount();
		int row = ds.getRowCount();
		
		baos.write(new String("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n").getBytes());
		baos.write(new String("<table>\n").getBytes());
		baos.write(new String("\t<tableinfo>\n").getBytes());
		baos.write(new String("\t\t<name>").getBytes());
		baos.write(new String(ds.getTitle()).getBytes());
		baos.write(new String("</name>\n").getBytes());
		baos.write(new String("\t</tableinfo>\n").getBytes());
		
		baos.write(new String("\t<tableheader>\n").getBytes());
		for(int i = 0; i < column; i++) {
			baos.write(new String("\t\t<column>").getBytes());
			baos.write(ds.getColumnKey(i).toString().getBytes());
			baos.write(new String("</column>\n").getBytes());
		}
		baos.write(new String("\t</tableheader>\n").getBytes());
		
		for(int i = 0; i < row; i++) {
			baos.write(new String("\t<row>\n").getBytes());
			for(int j = 0; j < column; j++) {
				baos.write(new String("\t\t<column>").getBytes());
				baos.write(String.valueOf(ds.getCell(i, j).getX()).getBytes());
				baos.write(new String("</column>\n").getBytes());
			}
			baos.write(new String("\t</row>\n").getBytes());
		}
		
		baos.writeTo(out);
		baos.close();
	}

}
