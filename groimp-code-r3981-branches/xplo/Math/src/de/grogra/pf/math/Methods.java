
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

package de.grogra.pf.math;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import javax.vecmath.*;
import de.grogra.math.*;
import de.grogra.pf.ui.edit.*;
import de.grogra.pf.ui.edit.PropertyEditorTree.*;

public final class Methods
{
	private Methods ()
	{
	}

	
	public static Node createColorEditor
		(PropertyEditor editor, PropertyEditorTree tree, Property p, String label)
	{
		final Property red = p.createSubProperty (editor.getPropertyType (), "x", -1),
			green = p.createSubProperty (editor.getPropertyType (), "y", -1),
			blue = p.createSubProperty (editor.getPropertyType (), "z", -1);
		return tree.new PropertyNode
			(p, p.getToolkit ().createColorWidget (editor), label)
		{
			private Tuple3f currentValue;

			@Override
			protected Object toWidget (Object propertyValue)
			{
				if (propertyValue == null)
				{
					return Color.WHITE;
				}
				currentValue = (Tuple3f) propertyValue;
				return new Color
					(Tuple3fType.colorToInt (currentValue));
			}

			@Override
			protected Object fromWidget (Object widgetValue)
			{
				if (currentValue == null) 
				{
					return null;
				}
				Tuple3f t = (Tuple3f) currentValue.clone ();
				Tuple3fType.setColor
					(t, ((Color) widgetValue).getRGB ());
				return t;
			}

			@Override
			public void propertyChange (PropertyChangeEvent event)
			{
				try
				{
					Tuple3f t = (Tuple3f) fromWidget (event.getNewValue ());
					if (t.x != currentValue.x)
					{
						red.setValue (new Float (t.x));
					}
					if (t.y != currentValue.y)
					{
						green.setValue (new Float (t.y));
					}
					if (t.z != currentValue.z)
					{
						blue.setValue (new Float (t.z));
					}
				}
				catch (InterruptedException e)
				{
					System.err.println (e);
				}
			}
		};
	}
}
