/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus This
 * program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package de.grogra.imp.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageInputStream;

import de.grogra.imp.objects.FixedImageAdapter;
import de.grogra.pf.boot.Main;
import de.grogra.pf.io.ExtensionItem;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.InputStreamSource;
import de.grogra.pf.io.MimeTypeItem;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.RegistryContext;
import de.grogra.pf.ui.registry.FileFactory;
import de.grogra.util.Described;
import de.grogra.util.MimeType;

public class ImageReader extends FilterBase implements ObjectSource
{
	public static final IOFlavor FLAVOR = IOFlavor
		.valueOf (FixedImageAdapter.class);

	public ImageReader (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (FLAVOR);
	}

	/**
	 * Returns the file factory item which is responsible for reading
	 * images from streams. The file factory returns objects
	 * of class {@link FixedImageAdapter}.
	 * 
	 * @param ctx the registry context to search for
	 * @return the file factory for images in <code>ctx</code>
	 */
	public static FileFactory getFactory (RegistryContext ctx)
	{
		return (FileFactory) Item.resolveItem (ctx, "/objects/images/file");
	}

	public Object getObject () throws IOException
	{
		InputStream in = ((InputStreamSource) source).getInputStream ();
		javax.imageio.ImageReader r = createImageIOReader (in, source
			.getFlavor ().getMimeType ());
		ImageInputStream iin = (ImageInputStream) r.getInput ();
		java.awt.image.BufferedImage img = r.read (0, r.getDefaultReadParam ());
		r.dispose ();
		try
		{
			iin.close ();
		}
		catch (IOException e)
		{
			Main.logWarning (e);
		}
		in.close ();
		return new FixedImageAdapter (img);
	}

	public static javax.imageio.ImageReader createImageIOReader (
			InputStream in, MimeType mimeType) throws IOException
	{
		Iterator<javax.imageio.ImageReader> i = javax.imageio.ImageIO
			.getImageReadersByMIMEType (mimeType.getMediaType ());
		if (!i.hasNext ())
		{
			throw new IOException ("Unsupported image MIME-type " + mimeType);
		}
		ImageInputStream iin = javax.imageio.ImageIO
			.createImageInputStream (in);
		javax.imageio.ImageReader r = i.next ();
		r.setInput (iin, true);
		return r;
	}

	static class Extension
	{
		String name;
		HashSet<String> extensions = new HashSet<String> ();
	}

	static void getSupport (Iterator<? extends ImageReaderWriterSpi> it,
			HashMap<MimeType, Extension> extensions, HashSet<MimeType> mimeTypes)
	{
		Logger log = de.grogra.pf.boot.Main.getLogger ();
		while (it.hasNext ())
		{
			ImageReaderWriterSpi spi = it.next ();
			log.config (spi + ": " + spi.getDescription (Locale.getDefault ()));
			String[] a = spi.getMIMETypes ();
			MimeType firstMime = null;
			if (a != null)
			{
				for (int i = 0; i < a.length; i++)
				{
					String s = a[i];
					try
					{
						MimeType m = new MimeType (s);
						if (firstMime == null)
						{
							firstMime = m;
						}
						log.config ("MIME-type " + m);
						mimeTypes.add (m);
					}
					catch (RuntimeException e)
					{
						log.config ("Illegal MIME-type " + s);
					}
				}
				a = spi.getFileSuffixes ();
				if ((a != null) && (firstMime != null))
				{
					Extension e = new Extension ();
					e.name = spi.getDescription (Locale.getDefault ());
					if (e.name.toLowerCase ().endsWith ("reader"))
					{
						e.name = e.name.substring (0, e.name.length () - 6);
					}
					else if (e.name.toLowerCase ().endsWith ("writer"))
					{
						e.name = e.name.substring (0, e.name.length () - 6);
					}
					e.name = e.name.trim ();
					for (int i = 0; i < a.length; i++)
					{
						if ((a[i] != null) && (a[i].length () > 0))
						{
							log.config ("File extension " + a[i] + " for "
								+ firstMime);
							e.extensions.add ('.' + a[i].toLowerCase ());
						}
					}
					extensions.put (firstMime, e);
				}
			}
		}
	}

	public static void installImageIO (Registry reg)
	{
		Thread.currentThread ().setContextClassLoader (Main.getLoaderForAll ());
		ImageIO.scanForPlugins ();

		IIORegistry iio = IIORegistry.getDefaultInstance ();
		HashMap<MimeType, Extension> extensions = new HashMap<MimeType, Extension> ();

		HashSet<MimeType> readerTypes = new HashSet<MimeType> ();
		getSupport (iio.getServiceProviders (ImageReaderSpi.class, true),
			extensions, readerTypes);

		HashSet<MimeType> writerTypes = new HashSet<MimeType> ();
		getSupport (iio.getServiceProviders (ImageWriterSpi.class, true),
			extensions, writerTypes);

		Item dir = reg.getDirectory ("/io/filetypes", null);
		for (Map.Entry<MimeType, Extension> entry : extensions.entrySet ())
		{
			addExtensionItem (dir, entry.getKey (),
				entry.getValue ().extensions.toArray (new String[0]), entry
					.getValue ().name);
		}

		dir = reg.getDirectory ("/io/mimetypes", null);
		for (MimeType mimeType : readerTypes)
		{
			MimeTypeItem mt = (MimeTypeItem) dir.getItem (mimeType
				.getMediaType ());
			if (mt == null)
			{
				mt = new MimeTypeItem (mimeType.getMediaType ());
				dir.add (mt);
			}
			FilterItem fi = new FilterItem (null, new IOFlavor (mimeType,
				IOFlavor.INPUT_STREAM, null), IOFlavor
				.valueOf (FixedImageAdapter.class), ImageReader.class
				.getName ());
			mt.add (fi);
		}

		dir = reg.getDirectory (
			"/io/mimetypes/application\\/x-java-jvm-local-objectref", null);
		for (MimeType mimeType : writerTypes)
		{
			FilterItem fi = new FilterItem (null,
				ImageWriter.RENDERED_IMAGE_FLAVOR, new IOFlavor (mimeType,
					IOFlavor.OUTPUT_STREAM, null), ImageWriter.class.getName ());
			dir.add (fi);
		}
	}

	private static void addExtensionItem (Item dir, MimeType mt, String[] exts,
			String descr)
	{
		ExtensionItem ei = new ExtensionItem (null, mt, exts);
		ei.setDescription (Described.NAME, descr);
		ei.setDescription (Described.ICON, de.grogra.pf.ui.UI.I18N
			.getObject ("registry.image.Icon"));
		dir.add (ei);
	}
}
