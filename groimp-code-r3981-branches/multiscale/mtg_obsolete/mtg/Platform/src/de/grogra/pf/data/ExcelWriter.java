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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.hpsf.MutableProperty;
import org.apache.poi.hpsf.MutablePropertySet;
import org.apache.poi.hpsf.MutableSection;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.Variant;
import org.apache.poi.hpsf.WritingNotSupportedException;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.hpsf.wellknown.SectionIDMap;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.OutputStreamSource;
import de.grogra.util.MimeType;
//import de.grogra.util.MimeType;

public class ExcelWriter extends FilterBase implements OutputStreamSource {

	public static final IOFlavor FLAVOR = new IOFlavor (new MimeType ("application/vnd.ms-excel", null), IOFlavor.OUTPUT_STREAM, null);

	public ExcelWriter(FilterItem item, FilterSource source) {
		super(item, source);
		setFlavor(FLAVOR);
	}

	private static String makeValidString (String s)
	{
		StringBuffer b = new StringBuffer (s);
		for (int i = 0; i < b.length (); i++)
		{
			switch (b.charAt (i))
			{
				case '/':
				case '\\':
				case '*':
				case '?':
				case '[':
				case ']':
					b.setCharAt (i, '_');
					break;
			}
		}
		return b.toString ();
	}

	public void write(OutputStream out) throws IOException {
		Dataset ds = (Dataset) ((ObjectSource) source).getObject();
		ByteArrayOutputStream baos;
		int column = ds.getColumnCount();
		int row = ds.getRowCount();
		
		HSSFWorkbook document = new HSSFWorkbook();
		baos=new ByteArrayOutputStream();
		HSSFSheet sheet1 = document.createSheet(makeValidString (ds.getTitle()));
		sheet1.setZoom(9,10);
		sheet1.setAutobreaks(true);
		sheet1.setFitToPage(true);
		HSSFPrintSetup ps=sheet1.getPrintSetup();
		ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		ps.setLandscape(true);
		ps.setFitWidth((short)1);
		HSSFFooter footer=sheet1.getFooter();
		footer.setLeft("Created by GroIMP");
		footer.setCenter(ds.getTitle());
		footer.setRight("Page "+HSSFFooter.page()+"/"+HSSFFooter.numPages());
		HSSFCellStyle style=document.createCellStyle();
		HSSFFont font=document.createFont();
		style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.BLACK.index);
		style.setFont(font);
		
		HSSFRow rowHead = sheet1.createRow((short)0);
		HSSFCell cell;
		for(int i = 0; i < column; i++) {
			cell=rowHead.createCell((short)i);
			sheet1.setColumnWidth((short)i,(short)((30)/((double)1/256)));
			cell.setCellStyle(style);
			cell.setCellValue(new HSSFRichTextString(ds.getColumnKey(i).toString()));
		}
		
		HSSFCellStyle standardCellStyle=document.createCellStyle();
		standardCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		standardCellStyle.setBottomBorderColor(HSSFColor.GREY_40_PERCENT.index);
		standardCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		standardCellStyle.setLeftBorderColor(HSSFColor.GREY_40_PERCENT.index);
		standardCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		standardCellStyle.setRightBorderColor(HSSFColor.GREY_40_PERCENT.index);
		style=document.createCellStyle();
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBottomBorderColor(HSSFColor.GREY_40_PERCENT.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setLeftBorderColor(HSSFColor.GREY_40_PERCENT.index);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setRightBorderColor(HSSFColor.GREY_40_PERCENT.index);
		//style.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"));
		short rowCounter = 1;
		
		for(int i = 0; i < row; i++) {
			rowHead=sheet1.createRow(rowCounter++);
			for(int j = 0; j < column; j++) {
				cell=rowHead.createCell((short)j);
				cell.setCellValue(ds.getCell(i, j).getX());
				cell.setCellStyle(standardCellStyle);
			}
		}
		
		document.write(baos);
		
		try {
			MutablePropertySet mps=new MutablePropertySet();
			MutableSection ms=(MutableSection)mps.getFirstSection();
			ms.setFormatID(SectionIDMap.SUMMARY_INFORMATION_ID);
			MutableProperty[] p=new MutableProperty[5];
			for(int i=0;i<5;i++)
				p[i]=new MutableProperty();
			p[0].setID(PropertyIDMap.PID_TITLE);
			p[0].setType(Variant.VT_LPWSTR);
			p[0].setValue(ds.getTitle());

			p[1].setID(PropertyIDMap.PID_AUTHOR);
			p[1].setType(Variant.VT_LPWSTR);
			p[1].setValue("GroIMP");

			p[2].setID(PropertyIDMap.PID_SUBJECT);
			p[2].setType(Variant.VT_LPWSTR);
			p[2].setValue(ds.getTitle());

			p[3].setID(PropertyIDMap.PID_LASTAUTHOR);
			p[3].setType(Variant.VT_LPWSTR);
			p[3].setValue("GroIMP");

			p[4].setID(PropertyIDMap.PID_APPNAME);
			p[4].setType(Variant.VT_LPWSTR);
			p[4].setValue("Microsoft Excel");

			ms.setProperties(p);
			
			POIFSFileSystem poiFs=new POIFSFileSystem();
			InputStream is;
			is = mps.toInputStream();
			poiFs.createDocument(is,SummaryInformation.DEFAULT_STREAM_NAME);
			
			poiFs.writeFilesystem(baos);
		} catch (WritingNotSupportedException e) {}
		
		baos.writeTo(out);
		baos.close();
		
	}

}
