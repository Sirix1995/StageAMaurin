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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.components.librarydocumentation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Generates a latex file with the documentation of all components included inside the 
 * current component library.
 * 
 * Therefore all components will be parsed and the included documentation will be extracted and 
 * a latex documentation will be generated out of it. 
 * 
 * The output will be written into two files, a main latex file which is an complete latex file
 * and a body file where only the content of the library documentation is included.
 * This body file will be used as part for the user manual.  
 * 
 * @author mh
 *
 */
public class GenerateLibraryDocumentation
{

	/**
	 *  Name of the main output file
	 */
	private final static String FILE_NAME = "library.tex";
	
	/**
	 *  Name of the body file which is included into the main output file
	 */
	private final static String FILE_NAME_BODY = "libraryBody.tex";
	
	
	/**
	 * Path to the component library
	 */
	private static final String COMPONENT_LIBRARY_PATH = "componentLibrary" + System.getProperty ("file.separator");
	
	/**
	 * The main output file where the generated latex code will be written into.
	 */
	private static File outputFileBody;
	
	/**
	 * 
	 */
	private static BufferedWriter outputStreamBody;
	
	/**
	 * Recursive search for all components in the specified directory.
	 * 
	 * @param dir base directory
	 * @return list of all found components
	 */
	private static void searchComponents(File dir, byte depth) {
		File[] files = dir.listFiles();
		Arrays.sort (files);
		System.out.println (depth+"  "+dir.getName ());
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().toLowerCase().endsWith("gsz")) { 
					generateComponentDoc(files[i]);
				}
				if (files[i].isDirectory()) {
					if(!files[i].getName ().contains (".")) {
						generateSectionDoc(files[i], depth);
						searchComponents(files[i], ++depth);
						depth--;
					}
				}
			}
		}
	}
	
	private static void generateSectionDoc (File file, byte depth)
	{
		try
		{
			outputStreamBody.newLine();
			switch (depth)
			{
				case 1:
					outputStreamBody.write ("\\section{"+file.getName ()+"}");
					break;
				case 2:
					outputStreamBody.write ("\\subsection{"+file.getName ()+"}");
					break;
				case 3:
					outputStreamBody.write ("\\subsubsection{"+file.getName ()+"}");
					break;
				case 4:
					outputStreamBody.write ("\\paragraph{"+file.getName ()+"}");
					break;
				case 5:
					outputStreamBody.write ("\\sebparagraph{"+file.getName ()+"}");
					break;
				default:
					break;
			}
			outputStreamBody.newLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}		
	}

	private static void generateComponentDoc (File file)
	{
//		File dir = file.getAbsoluteFile ();
//		System.out.println ("bingo "+ dir);
		if (file.isFile ()) {
			ZipFile zipFile;
			try
			{
				zipFile = new ZipFile(file);
				ZipEntry entry = zipFile.getEntry("description.txt");
				if(entry!=null) {
					GenerateComponentDocumentation.generate(zipFile.getInputStream(entry), outputStreamBody);
					outputStreamBody.newLine();
				}
			}
			catch (ZipException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}

	
	/**
	 * 
	 * 
	 * @param args
	 */
	public static void main (String[] args)
	{
		System.out.println ("Start generating component library documentation\n");
		String userDir = System.getProperty("user.dir");
		initOutputFiles(userDir);
		
		File f = new File (new File (userDir).getParent (), COMPONENT_LIBRARY_PATH);
//		System.out.println (" "+userDir+ "    \n"+f);
		if (f.isDirectory ()) {
			searchComponents(f, (byte)0);
		}
		
		try {
			outputStreamBody.flush ();
			outputStreamBody.close ();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println ("Finished");
		System.exit (0);
	}

		
	private static void initOutputFiles (String path) {
		try
		{
			outputFileBody = new File(path + FILE_NAME_BODY);
			outputStreamBody = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileBody), "UTF8"));
			writeMainFile(path);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	
	private static void writeMainFile (String path) throws UnsupportedEncodingException, FileNotFoundException, IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path + FILE_NAME)), "UTF8"));

		// write tex file
		writer.write("\\documentclass[a4paper,12pt]{article}"); writer.newLine();
		writer.write("\\usepackage[utf8x]{inputenc}");  writer.newLine();
		writer.write("\\usepackage[T1]{fontenc}"); writer.newLine();
		writer.write("\\usepackage[left=10mm,right=10mm,top=10mm,bottom=0mm]{geometry}"); writer.newLine();
		writer.write("\\setlength{\\parindent}{0ex}");  writer.newLine();
		writer.newLine();
		writer.write("\\newcommand{\\trennstrich}{");
		writer.write(" {\\vspace{2mm}-----------------------------------------------------------------------------------\\vspace{4mm}}");
		writer.write("}");
        writer.newLine();
		writer.write("\\begin{document}"); 
		writer.newLine(); writer.newLine();
		writer.write("\\include{"+FILE_NAME_BODY.substring (0, FILE_NAME_BODY.indexOf ('.'))+"}"); 
		writer.newLine(); writer.newLine();
		writer.write("\\end{document}"); writer.newLine();
		writer.flush();
		writer.close();		
	}

}
