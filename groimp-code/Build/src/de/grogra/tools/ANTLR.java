
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
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class ANTLR extends org.apache.tools.ant.taskdefs.optional.ANTLR
{
	protected File target, outputDirectory, workingDir;
	protected String className, importVocab, exportVocab;


	@Override
	public void setDir (File d)
	{
		super.setDir (d);
		setOutputdirectory (d);
		this.workingDir = d;
	}


	@Override
	public void setTarget (File target)
	{
		if (!target.isFile ())
		{
			target = new File (workingDir, target.getName ());
		}
		super.setTarget (target);
		this.target = target;
	}


	@Override
	public void setOutputdirectory (File outputDirectory)
	{
		super.setOutputdirectory (outputDirectory);
		this.outputDirectory = outputDirectory;
	}


	@Override
	public void execute ()
	{
		getFiles ();
		if (className != null)
		{
			if (importVocab != null)
			{
				File i = new File (outputDirectory,
								   importVocab + "TokenTypes.txt"),
					j = new File (outputDirectory, className + ".java");
				if (i.isFile () && j.isFile () && target.isFile ()
					&& (i.lastModified () > j.lastModified ()))
				{
					log ("Import vocabulary " + importVocab + " of "
						 + target + " has changed", Project.MSG_INFO);
					j.setLastModified (target.lastModified () - 10000);
				}
			}
		}
		super.execute ();
		if (className != null)
		{
			File vocabFile = new File
				(outputDirectory, exportVocab + "TokenTypes.txt"),
				out = new File
				(outputDirectory, exportVocab + "Tokenizer.inc");
			if (vocabFile.lastModified () > out.lastModified ())
			{
				try
				{
					log ("Generating inc-file for tokens of " + target,
						 Project.MSG_INFO);
					PrintWriter p = new PrintWriter (new FileWriter (out));
					new TokenizerBuilder
						(new VocabularyLexer (new FileReader (vocabFile)))
						.parse (p);
					p.flush ();
					p.close ();
				}
				catch (Exception e)
				{
					throw new BuildException (e);
				}
			}
		}
	}


	protected void getFiles () throws BuildException
	{
		try
		{
			BufferedReader in = new BufferedReader
				(new FileReader (target));
			String s;
			while ((s = in.readLine ()) != null)
			{
				int i = s.indexOf ("class ");
				int j = s.indexOf (" extends ");
				if ((i >= 0) && (j > i))
				{
					className = s.substring (i + 6, j).trim ();
				}
				i = s.indexOf ("importVocab");
				if (i >= 0)
				{
					j = s.indexOf ('=', i) + 1;
					if (j > 0)
					{
						i = s.indexOf (';', j);
						importVocab = ((i < 0) ? s.substring (j)
									   : s.substring (j, i)).trim ();
					}
				}
				i = s.indexOf ("exportVocab");
				if (i >= 0)
				{
					j = s.indexOf ('=', i) + 1;
					if (j > 0)
					{
						i = s.indexOf (';', j);
						exportVocab = ((i < 0) ? s.substring (j)
									   : s.substring (j, i)).trim ();
					}
				}
				if ((className != null) && (importVocab != null)
					&& (exportVocab != null))
				{
					break;
				}
			}
			in.close ();
		}
		catch (IOException e)
		{
			throw new BuildException (e);
		}
	}

}
