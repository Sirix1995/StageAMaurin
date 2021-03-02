
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

package de.grogra.pf.ui.event;

public class ActionEditEvent extends InputEditEvent
{
	private final String name;
	private final Object parameter;
	private final int modifiers;


	public ActionEditEvent (String name, Object parameter, int modifiers)
	{
		super ();
		this.name = name;
		this.parameter = parameter;
		this.modifiers = modifiers;
	}


	public ActionEditEvent (String name, int modifiers)
	{
		this (name, null, modifiers);
	}


	public ActionEditEvent (String name, java.awt.event.ActionEvent e)
	{
		this (name, null, e.getModifiers ());
	}


	@Override
	protected String paramString ()
	{
		return super.paramString ()
			+ ",name=" + name + ",parameter=" + parameter + ",modifiers=" + Integer.toHexString (modifiers);
	}


	public final int getModifiers ()
	{
		return modifiers;
	}


	public String getName ()
	{
		return name;
	}


	public Object getParameter ()
	{
		return parameter;
	}

}
