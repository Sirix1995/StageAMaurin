
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

package de.grogra.xl.compiler;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.ClassReader;

import de.grogra.reflect.Type;
import de.grogra.reflect.TypeLoader;
import de.grogra.util.PathListIterator;
import de.grogra.vfs.FSFile;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.LocalFileSystem;
import de.grogra.xl.util.EHashMap;
import de.grogra.xl.util.XHashMap;

public final class ASMTypeLoader implements TypeLoader
{

	static final class TypeEntry extends EHashMap.ObjectEntry<String,Object>
	{
		TypeEntry (String key, FSFile file)
		{
			setKey (key);
			this.value = file;
		}

		TypeEntry (String key, byte[] bytes)
		{
			setKey (key);
			this.value = bytes;
		}

		TypeEntry (String key, ZipFile zipFile)
		{
			setKey (key);
			this.value = zipFile;
		}

		InputStream getStream () throws IOException
		{
			InputStream is;
			if (value instanceof FSFile)
			{
				FSFile f = (FSFile) value;
				is = f.fileSystem.getInputStream (f.file);
			}
			else if (value instanceof byte[])
			{
				is = new ByteArrayInputStream ((byte[]) value);
			}
			else
			{
				ZipFile z = (ZipFile) value;
				is = z.getInputStream
					(z.getEntry (getKey ().replace ('.', '/') + ".class"));
			}
			value = null;
			return new BufferedInputStream (is);
		}
	}


	private final TypeLoader parent;
	private final ASMTypeLoader asmParent;
	private ClassLoader loader = null;
	private final EHashMap types = new EHashMap (25000);
	private final HashSet<String> packages = new HashSet<String> (2000);
	private final XHashMap<String,FSFile> filePackages = new XHashMap<String,FSFile> (100);
	private final HashSet<FSFile> added = new HashSet<FSFile> (100);


	public ASMTypeLoader (TypeLoader parent, ClassLoader loader)
	{
		this.parent = parent;
		this.asmParent = (parent instanceof ASMTypeLoader)
			? (ASMTypeLoader) parent : null;
		this.loader = loader;
		packages.add ("");
	}

	
	private void scanDirectory (FileSystem fs, Object dir, StringBuffer name)
	{
		int len = name.length ();
		Object[] files = fs.listFiles (dir);
		for (int i = 0; i < files.length; i++)
		{
			if (name.length () > 0)
			{
				name.append ('.');
			}
			String n = fs.getName (files[i]);
			if (!fs.isLeaf (files[i]))
			{
				name.append (n);
				filePackages.add (name.toString (), new FSFile (fs, files[i]));
			}
			else if ((n.length () > 6) && n.endsWith (".class"))
			{
				name.append (n);
				name.setLength (name.length () - 6);
				types.put (new TypeEntry (name.toString (), new FSFile (fs, files[i])));
			}
			name.setLength (len);
		}
		packages.add (name.toString ());
	}

	
	private void loadPackages (String name)
	{
		int i = 0;
		StringBuffer buf = new StringBuffer ();
		do
		{
			i = name.indexOf ('.', i + 1);
			String sub = (i < 0) ? name : name.substring (0, i);
			XHashMap.Entry<String,FSFile> e;
			while ((e = filePackages.getEntry (sub)) != null)
			{
				buf.setLength (0);
				buf.append (sub);
				FSFile f = e.getValue ();
				scanDirectory (f.fileSystem, f.file, buf);
				e.remove ();
			}
		}
		while (i > 0);
	}


	private void readZip (InputStream in) throws IOException
	{
		ZipInputStream zip = new ZipInputStream (new BufferedInputStream (in));
		ZipEntry z;
		while ((z = zip.getNextEntry ()) != null)
		{
			readZipEntry (z, zip, null);
		}
	}
	
	private void readZipEntry (ZipEntry z, InputStream in, ZipFile zip) throws IOException
	{
		String n = z.getName ();
		if (n.endsWith (".class"))
		{
			n = n.substring (0, n.length () - 6);
			int p = n.lastIndexOf ('/');
			if (p + 1 < n.length ())
			{
				n = n.replace ('/', '.');
				if (in != null)
				{
					byte[] b = new byte[(int) z.getSize ()];
					de.grogra.util.Utils.readFully (in, b);
					types.put (new TypeEntry (n, b));
				}
				else
				{
					types.put (new TypeEntry (n, zip));
				}
				while (p > 0)
				{
					if (!packages.add (n.substring (0, p)))
					{
						break;
					}
					p = n.lastIndexOf ('.', p - 1);
				}
			}
		}
	}

	public synchronized void addJars (URL[] jars, boolean updateClassLoader)
		throws IOException
	{
		for (int i = 0; i < jars.length; i++)
		{
			readZip (jars[i].openStream ());
		}
		if (updateClassLoader && (jars.length > 0))
		{
			loader = new URLClassLoader (jars, loader);
		}
	}


	public synchronized void addFiles (FileSystem fs, Object[] files, boolean updateClassLoader)
	{
		ArrayList<URL> urls = new ArrayList<URL> ();
		for (int i = 0; i < files.length; i++)
		{
			if (!added.add (new FSFile (fs, files[i])))
			{
				continue;
			}
			if (fs.isLeaf (files[i]))
			{
				try
				{
					if (files[i] instanceof File)
					{
						ZipFile zip = new ZipFile ((File) files[i]);
						for (Enumeration e = zip.entries (); e.hasMoreElements (); )
						{
							readZipEntry ((ZipEntry) e.nextElement (), null, zip);
						}
					}
					else
					{
						InputStream in = fs.getInputStream (files[i]);
						readZip (in);
						in.close ();
					}
					urls.add (fs.toURL (files[i]));
				}
				catch (IOException e)
				{
				}
			}
			else
			{
				scanDirectory (fs, files[i], new StringBuffer ());
				urls.add (fs.toURL (files[i]));
			}
		}
		if (updateClassLoader && !urls.isEmpty ())
		{
			loader = new URLClassLoader (urls.toArray (new URL[urls.size ()]), loader);
		}
	}
	
	
	public synchronized boolean hasPackage (String name)
	{
		if (packages.contains (name))
		{
			return true;
		}
		loadPackages (name);
		return packages.contains (name)
			|| ((parent instanceof ASMTypeLoader)
				&& ((ASMTypeLoader) parent).hasPackage (name));
	}

	
	private final TypeEntry getKey = new TypeEntry (null, (FSFile) null);

	public synchronized Type typeForNameOrNull (String name)
		throws ClassNotFoundException
	{
		getKey.setKey (name);
		TypeEntry e = (TypeEntry) types.get (getKey);
		if (e == null)
		{
			int i = name.lastIndexOf ('.');
			if (i > 0)
			{
				loadPackages (name.substring (0, i));
			}
			e = (TypeEntry) types.get (getKey);
		}
		if ((e == null) || (e.value == null))
		{
			return (asmParent != null) ? asmParent.typeForNameOrNull (name)
				: (parent != null) ? parent.typeForName (name)
				: null;
		}
		if (e.value instanceof ASMType)
		{
			return (ASMType) e.value;
		}
		ClassReader reader;
		try
		{
			reader = new ClassReader (e.getStream ());
		}
		catch (IOException ex)
		{
			e.value = null;
			throw new ClassNotFoundException (name + " " + ex.getMessage ());
		}
		ASMType t = new ASMType (this);
		e.value = t;
		reader.accept (t, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
		return t;
	}


	public Type typeForName (String name) throws ClassNotFoundException
	{
		Type t = typeForNameOrNull (name);
		if (t == null)
		{
			throw new ClassNotFoundException (name);
		}
		return t;
	}


	public Class classForName (String name) throws ClassNotFoundException
	{
		return Class.forName (name, false, loader);
	}


	public ClassLoader getClassLoader ()
	{
		return loader;
	}

	
	public static void main (String[] args) throws Exception
	{
		File[] exts = getExtensionClassPath (System.getProperty ("java.ext.dirs"));
		System.out.println (Arrays.toString (exts));
		File[] f = getBootClassPath (exts);
		System.out.println (Arrays.toString (f));
		new ASMTypeLoader (null, null).addFiles (LocalFileSystem.FILE_ADAPTER, f, false);
		new ASMTypeLoader (null, null).addFiles (LocalFileSystem.FILE_ADAPTER, new File[] {new File("/home/okn/platform/Platform/build")}, false);
	}

	
	public static File[] getClassPath (String list)
	{
		ArrayList path = new ArrayList ();
		for (PathListIterator pi = new PathListIterator (list); pi.hasNext (); )
		{
			File p = pi.nextPath ();
			if (p.isDirectory ())
			{
				path.add (p);
			}
			else
			{
				String n = p.getName ().toLowerCase ();
				if (n.endsWith (".jar") || n.endsWith (".zip"))
				{
					path.add (p);
				}
			}
		}
		return (File[]) path.toArray (new File[path.size ()]);
	}

	
	public static File[] getExtensionClassPath (String dirs)
	{
		ArrayList extPath = new ArrayList ();
		for (PathListIterator pi = new PathListIterator (dirs); pi.hasNext (); )
		{
			File p = pi.nextPath ();
			if (p.isDirectory ())
			{
				File[] files = p.listFiles ();
				for (int i = 0; i < files.length; i++)
				{
					String n = files[i].getName ().toLowerCase ();
					if (n.endsWith (".jar") || n.endsWith (".zip"))
					{
						extPath.add (files[i]);
					}
				}
			}
		}
		return (File[]) extPath.toArray (new File[extPath.size ()]);
	}


	public static File[] getBootClassPath (File[] toExclude) throws IOException
	{
		HashSet exclude = new HashSet ();
		for (int i = 0; i < toExclude.length; i++)
		{
			exclude.add (toExclude[i].getCanonicalFile ());
		}
		ArrayList classPath = new ArrayList ();
		String bcp = System.getProperty ("sun.boot.class.path");
		if (bcp != null)
		{
			for (PathListIterator pi = new PathListIterator (bcp); pi.hasNext (); )
			{
				File p = pi.nextPath ();
				if (p.exists ())
				{
					classPath.add (p);
				}
			}
		}
		else
		{
			File home = new File (System.getProperty ("java.home")).getCanonicalFile ();
			if (!home.isDirectory ())
			{
				throw new IOException ("java.home does not point to a directory");
			}
			scan (home, exclude, classPath);
		}
		return (File[]) classPath.toArray (new File[classPath.size ()]);
	}

	
	private static void scan (File dir, HashSet exclude, ArrayList<File> classPath)
		throws IOException
	{
		File[] files = dir.listFiles ();
		for (int i = 0; i < files.length; i++)
		{
			File f = files[i].getCanonicalFile ();
			if (exclude.add (f))
			{
				if (f.isDirectory ())
				{
					if (f.getName ().equalsIgnoreCase ("classes"))
					{
						classPath.add (f);
					}
					scan (f, exclude, classPath);
				}
				else
				{
					String n = f.getName ().toLowerCase ();
					if (n.endsWith (".jar") || n.endsWith (".zip"))
					{
						classPath.add (f);
					}
				}
			}
		}
	}

}
