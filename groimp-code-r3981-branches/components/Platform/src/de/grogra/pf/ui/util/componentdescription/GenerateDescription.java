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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA02111-1307, USA.
 */


package de.grogra.pf.ui.util.componentdescription;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import de.grogra.pf.registry.ComponentDescriptionParser;
import de.grogra.pf.registry.ComponentDescriptionXMLWriter;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.MemoryFileSystem;

/**
 * Parses the input file and extracts the documentation and writes it to the 
 * description file which is included in each component. 
 * 
 * @author mh, yong
 *
 */
public final class GenerateDescription
{

	/**
	 * 
	 * @param rggFile input file
	 * @param descriptionFile output file
	 * @param context
	 */
	public static void generate (Object rggFile, Object descriptionFile,
			FileSystem fs)
	{
		OutputStream out = ((MemoryFileSystem) fs).getOutputStream (descriptionFile, false);
		try
		{
			generateBody (fs.getInputStream (rggFile), out);
			((MemoryFileSystem) fs).closeQuiet (descriptionFile);
		}
		catch (IOException e)
		{
			System.out.println ("Error in GenerateDescription.generate!");
			e.printStackTrace ();
		}
	}

	/**
	 * Parses the streams and generates the description.
	 * 
	 * @param inputStream
	 * @param outputStream
	 * @throws IOException
	 */
	private static void generateBody (InputStream inputStream,
			OutputStream outputStream) throws IOException
	{
		BufferedReader inputStreamR=null;
		try
		{
			inputStreamR = new BufferedReader (
				new InputStreamReader (inputStream));
			ComponentDescriptionParser parser = new ComponentDescriptionParser(inputStreamR);
			parser.parse();
			//XML file
			ComponentDescriptionXMLWriter.writeXML(outputStream, parser);
		}
		catch(Throwable t)
		{
			outputStream.close();
		}
		finally
		{
			if(inputStreamR!=null)
				inputStreamR.close();
		}
	}
}
