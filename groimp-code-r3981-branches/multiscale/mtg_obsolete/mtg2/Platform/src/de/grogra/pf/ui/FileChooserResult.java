
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

package de.grogra.pf.ui;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import de.grogra.util.*;
import de.grogra.pf.io.*;

public final class FileChooserResult
{
	public File file;

	public FileFilter filter;


	public FileSource createFileSource
		(de.grogra.pf.registry.Registry reg, ModifiableMap map)
	{
		return new FileSource (file, getMimeType (), reg, map);
	}


	public MimeType getMimeType ()
	{
		return (filter instanceof MimeTypeFileFilter)
			? ((MimeTypeFileFilter) filter).getMimeType (file)
			: IO.getMimeType (file.getName ());
	}


	public FileChooserResult validate (boolean mustExist, int type)
	{
		if (file == null)
		{
			return null;
		}
		if (!file.exists ())
		{
			if (mustExist)
			{
				System.err.println ("File " + file + " does not exist.");
				return null;
			}
			if ((filter instanceof FileTypeItem.Filter)
				&& !((FileTypeItem.Filter) filter).getItem ()
				   .matches (file.getName ()))
			{
				file = new File (file.getParentFile (),
								 ((FileTypeItem.Filter) filter).getItem ()
								 .match (file.getName ()));
			}
		}
		return this;
	}

}
