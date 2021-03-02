
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

package de.grogra.xl.property;

import java.util.HashMap;

public class RuntimeModelFactory
{
	private static RuntimeModelFactory factory;


	public static synchronized RuntimeModelFactory getInstance ()
	{
		if (factory == null)
		{
			factory = new RuntimeModelFactory ();
		}
		return factory;
	}

	
	public static synchronized void setInstance (RuntimeModelFactory factory)
	{
		RuntimeModelFactory.factory = factory;
	}


	private final HashMap<String,RuntimeModel> models = new HashMap<String,RuntimeModel> ();


	public synchronized boolean defineModel (String classFileConstant, RuntimeModel model)
	{
		models.put (classFileConstant, model);
		return true;
	}


	public final synchronized RuntimeModel modelForName
		(String name, ClassLoader loader)
	{
		RuntimeModel m;
		if ((m = models.get (name)) == null)
		{
			m = modelForNameImpl (name, loader);
			if (m == null)
			{
				throw new NoClassDefFoundError (name);
			}
			models.put (name, m);
		}
		return m;
	}


	protected RuntimeModel modelForNameImpl
		(String classFileConstant, ClassLoader loader)
	{
		try
		{
			int i = classFileConstant.indexOf (':');
			RuntimeModel m = (RuntimeModel) Class.forName
				((i < 0) ? classFileConstant
				 : classFileConstant.substring (0, i), true, loader)
				.newInstance ();
			m.initialize ((i < 0) ? null : classFileConstant.substring (i + 1));
			return m;
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
