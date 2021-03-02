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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.WriterSource;
import de.grogra.util.MimeType;

public class CSVWriter extends FilterBase implements WriterSource
{
	public static final IOFlavor FLAVOR = new IOFlavor (MimeType.CSV,
		IOFlavor.WRITER, null);

	public CSVWriter (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (FLAVOR);
	}

	public void write (Writer out) throws IOException
	{
		Dataset ds = (Dataset) ((ObjectSource) source).getObject ();
		PrintWriter pw = new PrintWriter (out);
		pw.println (ds.getTitle ());
		pw.println("Created by GroIMP");
		
		int column = ds.getColumnCount();
		int row = ds.getRowCount();
		
		// Write columnkeys
		for(int i = 0; i < column; i++) {
			if(i == (column - 1))
				pw.println(ds.getColumnKey(i));
			else
				pw.print(ds.getColumnKey(i)+";");
		}
		
		// Write values
		for(int i = 0; i < row; i++) {
			for(int j = 0; j < column; j++) {
				if(j == (column - 1))
					pw.println(ds.getCell(i, j).getX());
				else
					pw.print(ds.getCell(i, j).getX()+";");
			}
		}
	}

}
