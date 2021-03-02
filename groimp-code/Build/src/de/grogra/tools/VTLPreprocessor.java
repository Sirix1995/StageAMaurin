
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
import org.apache.velocity.context.Context;

public class VTLPreprocessor extends org.apache.velocity.texen.ant.TexenTask
{
	private static final int BOOLEAN = 0;
	private static final int BYTE = 1;
	private static final int SHORT = 2;
	private static final int CHAR = 3;
	private static final int INT = 4;
	private static final int LONG = 5;
	private static final int FLOAT = 6;
	private static final int DOUBLE = 7;
	private static final int OBJECT = 8;
	private static final int VOID = 9;

	private static final String[] names = {
		"boolean", "byte", "short", "char", "int", "long", "float", "double",
		"Object", "void"
	};


	private boolean inplace = false;
	private ArrayList fileSets = new ArrayList ();
	private File stamp;


	public VTLPreprocessor ()
	{
		super ();
		setOutputDirectory (new File ("."));
		setOutputFile (".vtlpreprocessor.tmp");
	}


	public void setInplace (boolean inplace)
	{
		this.inplace = inplace;
	}


	public void setStamp (File stamp)
	{
		this.stamp = stamp;
	}


	public void addConfiguredFileset (FileSet set)
	{
		fileSets.add (set);
	}


	public static String firstToUpperCase (String s)
	{
		StringBuffer b = new StringBuffer (s);
		b.setCharAt (0, Character.toUpperCase (b.charAt (0)));
		return b.toString ();
	}


	@Override
	public Context initControlContext () throws Exception
	{
		Context c = super.initControlContext ();
		c.put ("types_void", new String[] {"boolean", "byte", "short", "char",
										   "int", "long", "float", "double",
										   "Object", "void"});
		c.put ("types", new String[] {"boolean", "byte", "short", "char",
									  "int", "long", "float", "double",
									  "Object"});
		c.put ("vmtypes", new String[] {"int", "long", "float", "double",
										"Object"});
		c.put ("primitives", new String[] {"boolean", "byte", "short", "char",
										   "int", "long", "float", "double"});
		c.put ("numeric", new String[] {"byte", "short", "int", "long",
										  "float", "double"});
		c.put ("numeric_char", new String[] {"byte", "short", "char", "int",
											   "long", "float", "double"});
		c.put ("vmnumeric", new String[] {"int", "long", "float", "double"});
		c.put ("bittypes", new String[] {"boolean", "byte", "short", "char",
										 "int", "long"});
		c.put ("pp", this);
		c.put ("C", "*/");
		return c;
	}


	private String type = "type not set";
	private int typeId;

	public void setType (String type)
	{
		this.type = type;
		for (int i = 0; i < names.length; i++)
		{
			if (type.equals (names[i]))
			{
				typeId = i;
				return;
			}
		}
		throw new IllegalArgumentException ("Type " + type + " not known.");
	}


	private String getTypeImpl ()
	{
		return type;
	}


	private int getTypeId ()
	{
		return typeId;
	}


	public String getjtype ()
	{
		return getTypeImpl ();
	}


	public String gettype ()
	{
		return getTypeImpl ().toLowerCase ();
	}


	public String getType ()
	{
		return firstToUpperCase (getTypeImpl ());
	}


	public String getTYPE ()
	{
		return getTypeImpl ().toUpperCase ();
	}


	private static String getWrapper (int id)
	{
		switch (id)
		{
			case CHAR:
				return "Character";
			case INT:
				return "Integer";
			default:
				return firstToUpperCase (names[id]);
		}
	}


	private static String getBaseWrapper (int id)
	{
		return ((id >= BYTE) && (id <= DOUBLE) && (id != CHAR)) ? "Number"
			: getWrapper (id);
	}


	public String wrap (String arg)
	{
		int id = getTypeId ();
		switch (id)
		{
			case BOOLEAN:
				return "((" + arg + ") ? Boolean.TRUE : Boolean.FALSE)";
			case OBJECT:
				return '(' + arg + ')';
			default:
				return getWrapper (id) + ".valueOf (" + arg + ')';
		}
	}


	public String unwrap (String arg)
	{
		int id = getTypeId ();
		return (id == OBJECT) ? '(' + arg + ')'
			: "(((" + getBaseWrapper (id) + ") (" + arg + "))."
				+ gettype () + "Value ())";
	}


	public String getnull ()
	{
		int id = getTypeId ();
		switch (id)
		{
			case BOOLEAN:
				return "false";
			case OBJECT:
				return "null";
			case VOID:
				return "";
			default:
				return "((" + names[id] + ") 0)";
		}
	}


	public String getsimplenull ()
	{
		int id = getTypeId ();
		switch (id)
		{
			case BOOLEAN:
				return "false";
			case OBJECT:
				return "null";
			case VOID:
				return "";
			default:
				return "0";
		}
	}


	public String getwrapper ()
	{
		return getWrapper (getTypeId ());
	}


	public String getbasewrapper ()
	{
		return getBaseWrapper (getTypeId ());
	}


	public boolean getboolean ()
	{
		return getTypeId () == BOOLEAN;
	}


	public boolean getbyte ()
	{
		return getTypeId () == BYTE;
	}


	public boolean getshort ()
	{
		return getTypeId () == SHORT;
	}


	public boolean getchar ()
	{
		return getTypeId () == CHAR;
	}


	public boolean getint ()
	{
		return getTypeId () == INT;
	}


	public boolean getlong ()
	{
		return getTypeId () == LONG;
	}


	public boolean getfloat ()
	{
		return getTypeId () == FLOAT;
	}


	public boolean getdouble ()
	{
		return getTypeId () == DOUBLE;
	}


	public boolean getObject ()
	{
		return getTypeId () == OBJECT;
	}


	public boolean getvoid ()
	{
		return getTypeId () == VOID;
	}


	public boolean getnumeric_char ()
	{
		int id = getTypeId ();
		return (id >= BYTE) && (id <= DOUBLE);
	}


	public boolean getnumeric ()
	{
		int id = getTypeId ();
		return (id >= BYTE) && (id <= DOUBLE) && (id != CHAR);
	}


	public boolean getfnumeric ()
	{
		int id = getTypeId ();
		return (id == FLOAT) || (id == DOUBLE);
	}


	public boolean getintegral ()
	{
		int id = getTypeId ();
		return (id >= BYTE) && (id <= LONG);
	}


	public String getvm2type ()
	{
		return getTypeId () == BOOLEAN ? " != 0" : "";
	}


	public String gettype2vm ()
	{
		return getTypeId () == BOOLEAN ? " ? 1 : 0" : "";
	}


	public String getprefix ()
	{
		switch (getTypeId ())
		{
			case LONG:
				return "l";
			case FLOAT:
				return "f";
			case DOUBLE:
				return "d";
			case OBJECT:
				return "a";
			default:
				return "i";
		}
	}


	public String getPREFIX ()
	{
		return getprefix ().toUpperCase ();
	}


	public String getbprefix ()
	{
		return (getTypeId () == BYTE) ? "b" : getprefix ();
	}


	public String getautogenerated ()
	{
		return "// NOTE: This file was generated automatically.\n\n"
			+ "// ********************************************\n"
			+ "// *               DO NOT EDIT!               *\n"
			+ "// ********************************************\n";
	}

	
	public String getgen ()
	{
		return "// generated\n";
	}


	@Override
	public void execute ()
	{
		if (fileSets.size () == 0)
		{
			executeImpl ();
		}
		else
		{
			for (int i = 0; i < fileSets.size (); i++)
			{
				DirectoryScanner ds = ((FileSet) fileSets.get (i))
					.getDirectoryScanner (getProject ());
				try
				{	
					setTemplatePath (ds.getBasedir ().getPath ());
				}
				catch (Exception e)
				{
					throw new BuildException ("Cannot set template path", e);
				}
				String[] files = ds.getIncludedFiles ();
				for (int j = 0; j < files.length; j++)
				{
					setControlTemplate (files[j]);
					executeImpl ();
				}
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


	private void executeImpl ()
	{
		File t = null;
		StringTokenizer st = new StringTokenizer (getTemplatePath (), ",");
		while (st.hasMoreTokens ())
		{
			t = new File (st.nextToken (), getControlTemplate ());
			if (t.isFile ())
			{
				break;
			}
			t = null;
		}
		if (t == null)
		{
			throw new BuildException ("Cannot find template file "
									  + getControlTemplate ());
		}
		if (stamp != null)
		{
			if (t.lastModified () <= stamp.lastModified ())
			{
				return;
			}
		}
		log ("Preprocessing file " + getControlTemplate (),
			 Project.MSG_INFO);
		super.execute ();
		if (inplace)
		{
			File r = new File (getOutputDirectory (), getOutputFile ());
			if (!r.isFile ())
			{
				throw new BuildException ("Cannot find output file " + r);
			}
			try
			{
				BufferedReader tin = new BufferedReader (new FileReader (t)),
					rin = new BufferedReader (new FileReader (r));
				StringBuffer old = new StringBuffer ((int) t.length ()),
					res = new StringBuffer ((int) r.length ());
				String s;
				while ((s = tin.readLine ()) != null)
				{
					old.append (s).append ('\n');
					res.append (s).append ('\n');
					if (s.trim ().endsWith ("!!*/"))
					{
						res.append ("//!! #* Start of generated code\n");
						while (!rin.readLine ().trim ().startsWith ("/*!!"))
							;
						while (!(s = rin.readLine ()).trim ().endsWith ("!!*/"))
						{
							if (s.length () == 0)
							{
								res.append (getgen ());
							}
							else
							{
								res.append (s).append ('\n');
							}
						}
						res.append ("//!! *# End of generated code\n");
						s = tin.readLine ();
						if (s != null)
						{
							old.append (s).append ('\n');
							if (s.trim ().startsWith ("//!!"))
							{
								do
								{
									s = tin.readLine ();
									old.append (s).append ('\n');
								} while (!s.trim ().startsWith ("//!!"));
							}
							else
							{
								res.append (s).append ('\n');
							}
						}
					}
				}
				tin.close ();
				rin.close ();
				if (!old.toString ().equals (res.toString ()))
				{
					log ("-> Updating file " + getControlTemplate (),
						 Project.MSG_INFO);
					BufferedWriter out
						= new BufferedWriter (new FileWriter (t));
					for (int i = 0; i < res.length (); i++)
					{
						if (res.charAt (i) == '\n')
						{
							out.newLine ();
						}
						else
						{
							out.write (res.charAt (i));
						}
					}
					out.flush ();
					out.close ();
				}
			}
			catch (IOException e)
			{
				throw new BuildException ("Cannot write file in place", e);
			}
		}
	}

}
