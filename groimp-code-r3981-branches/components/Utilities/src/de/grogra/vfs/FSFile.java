
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

package de.grogra.vfs;

public final class FSFile
{
	public FileSystem fileSystem;
	public Object file;

	public FSFile (FileSystem fileSystem, Object file)
	{
		this.fileSystem = fileSystem;
		this.file = file;
	}

	@Override
	public boolean equals (Object o)
	{
		if (!(o instanceof FSFile))
		{
			return false;
		}
		FSFile f = (FSFile) o;
		return fileSystem.toURL (file).equals (f.fileSystem.toURL (f.file));
	}

	@Override
	public int hashCode ()
	{
		return fileSystem.toURL (file).hashCode ();
	}
}
