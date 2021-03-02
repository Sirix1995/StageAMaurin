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

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.OutputStreamSource;
import de.grogra.util.MimeType;

public class PDFWriter extends FilterBase implements OutputStreamSource {
	
	public static final IOFlavor FLAVOR = new IOFlavor (MimeType.PDF, IOFlavor.OUTPUT_STREAM, null);

	public PDFWriter(FilterItem item, FilterSource source) {
		super(item, source);
		setFlavor(FLAVOR);
	}

	public void write(OutputStream out) throws IOException {
		Dataset ds = (Dataset) ((ObjectSource) source).getObject();
		Document document = new Document(PageSize.A4.rotate(), 50, 50, 50, 50);
		ByteArrayOutputStream baos;
		int column = ds.getColumnCount();
		int row = ds.getRowCount();
		try {
			PdfWriter.getInstance(document, baos=new ByteArrayOutputStream());
			document.addAuthor("GroIMP");  
			document.addSubject(ds.getTitle());
			document.addTitle(ds.getTitle());
			document.addCreator("GroIMP");
			document.addKeywords(ds.getTitle());
			HeaderFooter footer =new HeaderFooter(
					new Phrase("Created by GroIMP                   " +ds.getTitle()+
					"                                        Seite "),true);
			footer.setAlignment(HeaderFooter.ALIGN_CENTER);
			footer.setBorderWidth(0);
			footer.setBorderWidthTop(1);
			document.setFooter(footer);
			document.open();
			
			int[] widths = new int[column];
			for(int i = 0; i < column; i++)
				widths[i] = 10;
			
			PdfPTable table=new PdfPTable(column);
			table.setWidthPercentage(100);
			table.setWidths(widths);
			table.getDefaultCell().setBorderWidth(1);
			table.getDefaultCell().setBackgroundColor(Color.LIGHT_GRAY);
			table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			Paragraph par;
			
			for(int i = 0; i < column; i++) {
				table.addCell(par=new Paragraph(ds.getColumnKey(i).toString()));
				par.font().setSize(9);
			}
			
			table.setHeaderRows(1);
			table.getDefaultCell().setBackgroundColor(Color.WHITE);
			table.getDefaultCell().setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
			
			for(int i = 0; i < row; i++) {
				for(int j = 0; j < column; j++) {
					table.addCell(par=new Paragraph(String.valueOf(ds.getCell(i, j).getX())));
					par.font().setSize(9);
				}
			}
			
			document.add(table);
			document.close();
			baos.writeTo(out);
			baos.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

}
