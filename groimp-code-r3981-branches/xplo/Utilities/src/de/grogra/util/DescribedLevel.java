
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

package de.grogra.util;

import java.util.logging.Level;

public class DescribedLevel extends Level implements Described
{
	private final I18NBundle bundle;


	public DescribedLevel (I18NBundle bundle, String key, int value)
	{
		super (key, value, null);
		this.bundle = bundle;
	}

	
	@Override
	public String getLocalizedName ()
	{
		return (String) getDescription (NAME);
	}
	

	public Object getDescription (String type)
	{
		return Utils.get (bundle, getName (), type, null);
	}
}
