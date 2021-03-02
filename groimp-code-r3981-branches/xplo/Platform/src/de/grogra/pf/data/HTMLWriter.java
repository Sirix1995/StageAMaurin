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

public class HTMLWriter extends FilterBase implements WriterSource {
	
	public static final IOFlavor FLAVOR = new IOFlavor (MimeType.TEXT_HTML, IOFlavor.WRITER, null);

	public HTMLWriter(FilterItem item, FilterSource source) {
		super(item, source);
		setFlavor(FLAVOR);
	}

	public void write(Writer out) throws IOException {
		Dataset ds = (Dataset) ((ObjectSource) source).getObject ();
		PrintWriter pw = new PrintWriter(out);
		
		// Write HTML-Head
		pw.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		pw.println("<html>");
		pw.println("<head>");
		pw.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
		pw.println("<title>"+ds.getTitle()+"</title>");
		pw.println("<style type=\"text/css\">");
		pw.println("<!--");
		pw.println(".Stil1 {font-family: Arial, Helvetica, sans-serif}");
		pw.println(".Stil2 {font-family: Arial, Helvetica, sans-serif; font-size: 24px; }");
		pw.println("-->");
		pw.println("</style>");
		pw.println("</head>");
		
		// Write HTML-Body
		pw.println("<body>");
		pw.println("<p class=\"Stil2\">"+ds.getTitle()+"</p>");
		pw.println("<p class=\"Stil1\">Created  by GroIMP</p>");
		
		int column = ds.getColumnCount();
		int row = ds.getRowCount();
		
		pw.println("<table width=\""+(200*column)+"\" border=\"1\">");
		// Write columnkeys
		pw.println("<tr>");
		for(int i = 0; i < column; i++)
			pw.println("<td bgcolor=\"#99FF00\" class=\"Stil1\"><div align=\"center\">"+ds.getColumnKey(i)+"</div></td>");
		pw.println("</tr>");
		
		// Write values
		for(int i = 0; i < row; i++) {
			pw.println("<tr>");
			for(int j = 0; j < column; j++) {
				pw.println("<td class=\"Stil1\"><div align=\"right\">"+ds.getCell(i, j).getX()+"</div></td>");
			}
			pw.println("</tr>");
		}
		pw.println("</table>");
		
		pw.println("</body>");
		pw.println("</html>");
	}

}
