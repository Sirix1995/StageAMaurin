
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

package de.grogra.pf.ui.util;

import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.edit.Property;

public class ButtonWidget extends WidgetBase implements Command
{
	private final Command command;
	private final Property property;
	private Object button;
	
	
	public ButtonWidget (Command command, Property property)
	{
		this.command = command;
		this.property = property;
	}

	public Property getProperty ()
	{
		return property;
	}

	@Override
	public void checkForChange (Object value)
	{
		super.checkForChange (value);
	}

	public void setButton (Object button)
	{
		this.button = button;
	}

	@Override
	protected void setComponentValue (Object value)
	{
	}

	public void setEnabled (boolean enabled)
	{
	}

	public void updateValue (Object value)
	{
	}

	public Object getComponent ()
	{
		return button;
	}

	public String getCommandName ()
	{
		return null;
	}

	public void run (Object info, Context context)
	{
		command.run (this, context);
	}
}
