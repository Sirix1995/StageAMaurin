
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

package de.grogra.pf.io;

import java.io.*;
import java.net.*;
import java.util.Hashtable;
import de.grogra.util.*;
import de.grogra.pf.registry.*;
import de.grogra.vfs.*;
import de.grogra.xl.util.ObjectList;

public final class IO
{
	public static final String PROJECT_FS = "pfs";
	public static final char SYSTEM_ID_SEPARATOR = ':';

	public static final I18NBundle I18N = I18NBundle.getInstance (IO.class);

	private static IO instance;


	private final Registry registry;
	private final Hashtable writableFileTypes = new Hashtable (20);
	private final Hashtable readableFileTypes = new Hashtable (20);
	private final Hashtable writableFlavors = new Hashtable (20);


	private IO (Registry registry)
	{
		this.registry = registry;
	}


	static synchronized IO getInstance ()
	{
		if (instance == null)
		{
			instance = new IO (de.grogra.pf.boot.Main.getRegistry ());
		}
		return instance;
	}


	static Registry getRegistry ()
	{
		return getInstance ().registry;
	}


	public static FilterSource createPipeline (FilterSource source,
											   final IOFlavor targetFlavor)
	{
		final class OptPipeline implements ItemVisitor
		{
			FilterSource opt = null;

			private static final int MAX_LEN = 15; 
			private int len = 0, optLen = MAX_LEN;
			private final FilterItem[] items = new FilterItem[MAX_LEN];

			void createPipeline (FilterSource s)
			{
				if ((len >= optLen) || (s == null))
				{
					return;
				}
				if (targetFlavor.isAssignableFrom (s.getFlavor ()))
				{
					FilterSource f = targetFlavor.createAdapter (s);
					if (f != s)
					{
						len++;
					}
					if (len < optLen)
					{
						opt = f;
						optLen = len;
					}
					if (f != s)
					{
						len--;
					}
				}
				else if (len + 1 < optLen)
				{
					MimeType mt = s.getFlavor ().getMimeType ();
					Item.forAll (MimeTypeItem.get (getRegistry (), mt),
								 null, null, this, s, true);
					if (!mt.equals (MimeType.OCTET_STREAM))
					{
						Item.forAll
							(MimeTypeItem.get (getRegistry (), MimeType.OCTET_STREAM),
							 null, null, this, s, true);
					}
				}
			}

			public void visit (Item item, Object info)
			{
				if ((len + 1 >= optLen) || !(item instanceof FilterItem))
				{
					return;
				}
				FilterItem i = (FilterItem) item;
				FilterSource s = (FilterSource) info;
				if (i.getInputFlavor ().isAssignableFrom (s.getFlavor ()))
				{
					for (int k = len; k >= 0; k--)
					{
						if (items[k] == i)
						{
							return;
						}
					}
					FilterSource f = i.getInputFlavor ().createAdapter (s);
					if (f != s)
					{
						len++;
					}
					len++;
					if (len < optLen)
					{
						items[len] = i;
						createPipeline (i.createFilter (f));
						items[len] = null;
					} 
					len--;
					if (f != s)
					{
						len--;
					}
				}
			}
		}

		OptPipeline p = new OptPipeline ();
		p.createPipeline (source);
		return p.opt;
	}


	public static FilterSource getRoot (FilterSource source)
	{
		while (source.getFilter () != null)
		{
			source = source.getFilter ().getSource ();
		}
		return source;
	}


	public static String toSystemId (File file)
	{
		file = file.getAbsoluteFile ();
		File r = Utils.relativize (new File (System.getProperty ("user.home")),
								   file);
		return r.isAbsolute () ? file.getPath ()
			: '~' + File.separator + r.getPath ();
	}

	
	public static FSFile toFile (RegistryContext ctx, String systemId)
	{
		FSFile f = toFile (ctx.getRegistry ().getFileSystem (), systemId);
		if (f != null)
		{
			return f;
		}
		f = toFile (Registry.PLUGIN_FILE_SYSTEMS, systemId);
		if (f != null)
		{
			return f;
		}

		int c = systemId.indexOf (':');
		
		// if systemId contains a : prefixed by at least two
		// lower case characters, consider systemId as URL (not as file)
	checkForProtocol:
		if (c > 1)
		{
			while (--c >= 0)
			{
				char ch = systemId.charAt (c);
				if ((ch < 'a') || (ch > 'z'))
				{
					break checkForProtocol;
				}
			}
			// seems to be a URL
			return null;
		}

		return new FSFile (LocalFileSystem.FILE_ADAPTER, IO.toLocalFile (systemId));
	}

	
	private static FSFile toFile (FileSystem fs, String systemId)
	{
		if ((fs == null)
			|| !systemId.startsWith (fs.getFSName () + SYSTEM_ID_SEPARATOR))
		{
			return null;
		}
		Object f = fs.getFile (systemId.substring (fs.getFSName ().length () + 1));
		if (f == null)
		{
			return null;
		}
		return new FSFile (fs, f);
	}


	public static File toLocalFile (String systemIdOfFile)
	{
		switch (systemIdOfFile.length ())
		{
			case 0:
				return null;
			case 1:
				if (systemIdOfFile.charAt (0) == '~')
				{
					systemIdOfFile = System.getProperty ("user.home");
				}
				break;
			default:
				if ((systemIdOfFile.charAt (0) == '~')
					&& (systemIdOfFile.charAt (1) == File.separatorChar))
				{
					return new File (System.getProperty ("user.home"),
									 systemIdOfFile.substring (2));
				}
				break;
		}
		return new File (systemIdOfFile);
	}


	public static String toPath (String systemId)
	{
		int i = systemId.indexOf (SYSTEM_ID_SEPARATOR);
		if ((i > 0) && ((systemId.lastIndexOf ('/', i) >= 0)
						|| (systemId.lastIndexOf (File.separatorChar, i) >= 0)))
		{
			i = -1;
		}
		if (i > 0)
		{
			return systemId.substring (i + 1);
		}
		try
		{
			return new URL (systemId).getPath ();
		}
		catch (MalformedURLException e)
		{
			return systemId;
		}
	}


	public static String toName (String systemId)
	{
		systemId = toPath (systemId);
		return systemId.substring
			(Math.max (systemId.lastIndexOf ('/'), systemId.lastIndexOf (File.separatorChar)) + 1);
	}


	public static String toSimpleName (String systemId)
	{
		systemId = toName (systemId);
		int j = systemId.lastIndexOf ('.');
		return (j <= 0) ? systemId : systemId.substring (0, j);
	}


	public static URL toURL (RegistryContext ctx, String systemId)
		throws MalformedURLException
	{
		FSFile f = toFile (ctx, systemId);
		return (f == null) ? new URL (systemId) : f.fileSystem.toURL (f.file);
	}


	public static String toSystemId (FileSystem fs, URL url)
	{
		Object file = fs.toFile (url);
		return (file != null) ? toSystemId (fs, file)
			: "file".equals (url.getProtocol ()) ? toSystemId (Utils.urlToFile (url))
			: url.toString ();
	}


	public static String toSystemId (FileSystem fs, Object file)
	{
		return fs.getFSName () + SYSTEM_ID_SEPARATOR + fs.getPath (file);
	}


	public static MimeType getMimeType (String fileName)
	{
		FileTypeItem i = FileTypeItem.get (getRegistry (), fileName);
		return (i != null) ? i.getMimeType () : MimeType.OCTET_STREAM;
	}


	public static FileTypeItem.Filter[] getReadableFileTypes
		(IOFlavor[] acceptableFlavors)
	{
		ObjectList key = new ObjectList (acceptableFlavors);
		FileTypeItem.Filter[] f = (FileTypeItem.Filter[])
			getInstance ().readableFileTypes.get (key);
		if (f != null)
		{
			return f;
		}
		Item[] ft = Item.findAll (getRegistry (), "/io/filetypes",
								  ItemCriterion.INSTANCE_OF, FileTypeItem.class,
								  true);
		ObjectList list = new ObjectList ();
		for (int i = 0; i < ft.length; i++)
		{
			NullSource s = new NullSource
				(new IOFlavor (((FileTypeItem) ft[i]).getMimeType (),
							   IOFlavor.INPUT_STREAM | IOFlavor.READER | IOFlavor.VFILE_READER
							   | IOFlavor.FILE_READER, null),
				 getRegistry (), null);
			for (int j = 0; j < acceptableFlavors.length; j++)
			{
				if (createPipeline (s, acceptableFlavors[j]) != null)
				{
					list.add (((FileTypeItem) ft[i]).getFilter ());
					break;
				}
			}
		}
		list.toArray (f = new FileTypeItem.Filter[list.size ()]);
		getInstance ().readableFileTypes.put (key, f);
		return f;
	}


	public static FileTypeItem.Filter[] getWritableFileTypes
		(IOFlavor sourceFlavor)
	{
		FileTypeItem.Filter[] f = (FileTypeItem.Filter[])
			getInstance ().writableFileTypes.get (sourceFlavor);
		if (f != null)
		{
			return f;
		}
		NullSource s = new NullSource (sourceFlavor, getRegistry (), null);
		Item[] ft = Item.findAll (getRegistry (), "/io/filetypes",
								  ItemCriterion.INSTANCE_OF, FileTypeItem.class,
								  true);
		ObjectList list = new ObjectList ();
		for (int i = 0; i < ft.length; i++)
		{
			if (createPipeline (s, new IOFlavor
								(((FileTypeItem) ft[i]).getMimeType (),
								 IOFlavor.FILE_OUT, null)) != null)
			{
				list.add (((FileTypeItem) ft[i]).getFilter ());
			}
		}
		list.toArray (f = new FileTypeItem.Filter[list.size ()]);
		getInstance ().writableFileTypes.put (sourceFlavor, f);
		return f;
	}


	static boolean isWritableTo (IOFlavor src, IOFlavor dest)
	{
		ObjectList key = new ObjectList (2);
		key.add (src);
		key.add (dest);
		Boolean b;
		if ((b = (Boolean) getInstance ().writableFlavors.get (key)) == null)
		{
			b = Boolean.valueOf
				(createPipeline (new NullSource (src, getRegistry (), null), dest)
				 != null);
			getInstance ().writableFlavors.put (key, b);
		}
		return b.booleanValue ();
	}


	public static String getDescription (MimeType mt)
	{
		return mt.getMediaType ();
	}


	public static void writeXML (FilterSource xml,
								 FileSystem fs, String file,
								 MimeType mimeType) throws IOException
	{	
		OutputStream s = null;
		try
		{
			Object f = fs.create (fs.getRoot (), file, false);
			s = fs.getOutputStream (f, false);
			new StreamAdapter (xml, new IOFlavor
							   (mimeType, IOFlavor.OUTPUT_STREAM, null))
				.write (s);
			s.flush ();
			fs.getAttributes (f, true).put
				(java.util.jar.Attributes.Name.CONTENT_TYPE,
				 mimeType.toString ());

		}
		finally
		{
			if (s != null)
			{
				s.close ();
			}
		}
	}

	
}
