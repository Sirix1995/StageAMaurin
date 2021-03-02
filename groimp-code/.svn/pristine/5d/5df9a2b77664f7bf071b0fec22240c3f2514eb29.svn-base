
/*
 * Copyright (C) 2002 - 2005 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.grogra.tools;

import java.io.*;
import java.util.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

public class Enhance extends org.apache.tools.ant.Task
{
	private ArrayList fileSets = new ArrayList ();
	private File stamp, tmp;


	public void setStamp (File stamp)
	{
		this.stamp = stamp;
	}


	public void setTmp (File tmp)
	{
		this.tmp = tmp;
	}

	
	public void addConfiguredFileset (FileSet set)
	{
		fileSets.add (set);
	}


	@Override
	public void execute ()
	{
		for (int i = 0; i < fileSets.size (); i++)
		{
			DirectoryScanner ds = ((FileSet) fileSets.get (i))
				.getDirectoryScanner (getProject ());
			String[] files = ds.getIncludedFiles ();
			for (int j = 0; j < files.length; j++)
			{
				enhance (new File (ds.getBasedir () + File.separator
								   + files[j]));
			}
		}
		if (stamp != null)
		{
			try
			{
				stamp.createNewFile ();
			}
			catch (IOException e)
			{
				throw new BuildException
					("Cannot create stamp file " + stamp, e);
			}
			stamp.setLastModified (System.currentTimeMillis ());
		}
	}


	private void enhance (File file)
	{
		if (stamp != null)
		{
			if (file.lastModified () <= stamp.lastModified ())
			{
				return;
			}
		}
		try
		{
			InputStream plFile = getClass ().getClassLoader ()
				.getResourceAsStream ("enhance.pl");
			if (plFile == null)
			{
				throw new BuildException ("enhance.pl not found");
			}
			Process p = Runtime.getRuntime ().exec
				(new String[] {"perl", "-w", "-", file.toString (), tmp.toString ()});
			OutputStream perlIn = p.getOutputStream ();
			int i;
			while ((i = plFile.read ()) >= 0)
			{
				perlIn.write (i);
			}
			perlIn.flush ();
			perlIn.close ();

			// display generated error messages
			InputStream perlErr = p.getErrorStream();
			while (perlErr.available() >0)
			{
				System.err.print((char)perlErr.read());
			}
			perlErr.close ();
			
			p.waitFor ();
			if (p.exitValue () != 0)
			{
				throw new BuildException
					("perl returned exit value " + p.exitValue ()
					 + ", command was perl -w - " + file + " " + tmp);
			}
			String s = file.getName ();
			if (!s.endsWith (".java"))
			{
				file = new File (file.getParentFile (),
								 s.substring (0, s.lastIndexOf ('.'))
								 + ".java");
			}
			BufferedReader in;
			String old;
			StringBuffer b;
			if (file.exists ())
			{
				in = new BufferedReader (new FileReader (file));
				b = new StringBuffer ((int) file.length ());
				while ((s = in.readLine ()) != null)
				{
					b.append (s).append ('\n');
				}
				in.close ();
				old = b.toString ();
			}
			else
			{
				b = new StringBuffer ();
				old = null;
			}

			in = new BufferedReader (new FileReader (tmp));
			b.setLength (0);
			while ((s = in.readLine ()) != null)
			{
				b.append (s).append ('\n');
			}
			in.close ();

			if ((old == null) || !old.equals (b.toString ()))
			{
				log ("-> Updating file " + file, Project.MSG_INFO);
				BufferedWriter out
					= new BufferedWriter (new FileWriter (file));
				for (i = 0; i < b.length (); i++)
				{
					if (b.charAt (i) == '\n')
					{
						out.newLine ();
					}
					else
					{
						out.write (b.charAt (i));
					}
				}
				out.flush ();
				out.close ();
			}
		}
		catch (Exception e)
		{
			throw new BuildException
				("Cannot enhance " + file + ", " + e.getMessage (), e);
		}
	}

}
