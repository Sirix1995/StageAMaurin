
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

package de.grogra.math;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.vecmath.Tuple3f;

import de.grogra.icon.Icon;
import de.grogra.icon.IconSource;
import de.grogra.persistence.ShareableBase;

public final class Graytone extends ShareableBase
	implements ColorMap, IconSource, Icon
{
	//enh:sco de.grogra.persistence.SCOType

	float value;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field value$FIELD;

	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (Graytone representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, de.grogra.persistence.SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((Graytone) o).value = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((Graytone) o).getValue ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new Graytone ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (Graytone.class);
		value$FIELD = Type._addManagedField ($TYPE, "value", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public float getValue ()
	{
		return value;
	}

	public void setValue (float value)
	{
		this.value = (float) value;
	}

//enh:end

	private transient Color awtColor;


	public Graytone (float value)
	{
		this.value = value;
	}


	public Graytone ()
	{
		this (0.5f);
	}


	public Graytone (Tuple3f color)
	{
		this ((color.x + color.y + color.z) / 3);
	}


	public int getAverageColor ()
	{
		int i = Math.round (value * 255);
		i = (i < 0) ? 0 : (i > 255) ? 255 : i;
		return (i << 16) + (i << 8) + i + (255 << 24);
	}


	public float getFloatValue (ChannelData data, int channel)
	{
		return ((channel >= Channel.MIN_DERIVATIVE)
				&& (channel <= Channel.MAX_DERIVATIVE)) ? 0
			: ((channel & 3) < 3) ? value
			: 1;
	}


	public Object getObjectValue (ChannelData data, int channel)
	{
		return data.forwardGetObjectValue (data.getData (null));
	}


	public Icon getIcon (Dimension size, int state)
	{
		return this;
	}


	public Dimension getPreferredIconSize (boolean small)
	{
		return null;
	}


	public void paintIcon (Component c, Graphics2D g,
						   int x, int y, int w, int h, int state)
	{
		Color old = g.getColor ();
		g.setColor (Color.BLACK);
		g.drawRect (x, y, w - 1, h - 1);
		int i = getAverageColor ();
		if ((awtColor == null) || (i != awtColor.getRGB ()))
		{
			awtColor = new Color (i, true);
		}
		g.setColor (awtColor);
		g.fillRect (x + 1, y + 1, w - 2, h - 2);
		g.setColor (old);
	}


	public IconSource getIconSource ()
	{
		return this;
	}


	public void prepareIcon ()	
	{
	}


	public boolean isMutable ()
	{
		return true;
	}


	public Image getImage ()
	{
		return null;
	}


	public Image getImage (int w, int h)
	{
		return null;
	}


	public java.net.URL getImageSource ()
	{
		return null;
	}


	public Rectangle getIconBounds ()
	{
		return null;
	}

	public void accept(ChannelMapVisitor visitor) {
		visitor.visit(this);
	}

}
