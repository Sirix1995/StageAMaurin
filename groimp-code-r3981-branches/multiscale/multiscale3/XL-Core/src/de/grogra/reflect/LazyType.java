
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

package de.grogra.reflect;

public class LazyType<T> extends TypeDecorator<T>
{
	private final String binaryName;
	private final String descriptor;
	private ClassLoader classLoader;
	private TypeLoader loader;


	private Type<T> type;


	public LazyType (String binaryName, ClassLoader loader)
	{
		super (null);
		this.binaryName = binaryName;
		this.classLoader = loader;
		this.descriptor = 'L' + binaryName.replace ('.', '/') + ';';
	}


	public LazyType (String binaryName, TypeLoader loader)
	{
		super (null);
		this.binaryName = binaryName;
		this.loader = loader;
		this.descriptor = 'L' + binaryName.replace ('.', '/') + ';';
	}


	@Override
	public synchronized Member getDecoratedMember ()
	{
		if (type == null)
		{
			try
			{
				if (classLoader != null)
				{
					type = (Type<T>) ClassAdapter.wrap
						(Class.forName (binaryName, false, classLoader));
				}
				else
				{
					type = loader.typeForName (binaryName);
				}
			}
			catch (ClassNotFoundException e)
			{
				throw new RuntimeException (e);
			}
			loader = null;
			classLoader = null;
		}
		return type;
	}


	@Override
	public String getBinaryName ()
	{
		return binaryName;
	}

	
	@Override
	public String getDescriptor ()
	{
		return descriptor;
	}

}
