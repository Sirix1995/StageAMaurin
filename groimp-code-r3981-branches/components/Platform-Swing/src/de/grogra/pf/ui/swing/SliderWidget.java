
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

package de.grogra.pf.ui.swing;

import javax.swing.*;
import javax.swing.event.*;
import de.grogra.pf.ui.awt.*;

class SliderWidget extends AWTWidgetSupport implements ChangeListener
{
	private int lastSliderValue = Integer.MIN_VALUE;


	@Override
	protected void install ()
	{
		((JSlider) component).addChangeListener (this);
	}


	@Override
	protected void uninstall ()
	{
		((JSlider) component).removeChangeListener (this);
	}


	@Override
	protected void setComponentValue (Object value)
	{
		if (value != null)
		{
			lastSliderValue = (value instanceof Integer)
				? ((Number) value).intValue ()
				: Math.round (((Number) value).floatValue ()); 
				((JSlider) component).setValue (lastSliderValue);
		}
	}


	public void stateChanged (ChangeEvent e)
	{
		int v = ((JSlider) component).getValue (); 
		if (v != lastSliderValue)
		{
			lastSliderValue = v;
			checkForChange (v);
		}
	}

}
