
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
import java.io.IOException;

import javax.vecmath.Color3f;
import javax.vecmath.Tuple3f;

import de.grogra.icon.Icon;
import de.grogra.icon.IconSource;
import de.grogra.persistence.Manageable;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.PersistenceInput;
import de.grogra.persistence.PersistenceOutput;
import de.grogra.persistence.Shareable;
import de.grogra.persistence.SharedObjectProvider;
import de.grogra.persistence.SharedObjectReference;
import de.grogra.persistence.Transaction;
import de.grogra.persistence.XMLPersistenceReader;
import de.grogra.reflect.ClassAdapter;
import de.grogra.xl.util.ObjectList;

public class RGBColor extends Color3f
	implements ColorMap, IconSource, Icon, Manageable, Shareable 
{
	/**
	 * <code>RGBColor</code> whose color is {@link Color#BLACK}.
	 */
	public static final RGBColor BLACK;

	/**
	 * <code>RGBColor</code> whose color is {@link Color#BLUE}.
	 */
	public static final RGBColor BLUE;

	/**
	 * <code>RGBColor</code> whose color is {@link Color#CYAN}.
	 */
	public static final RGBColor CYAN;

	/**
	 * <code>RGBColor</code> whose color is {@link Color#DARK_GRAY}.
	 */
	public static final RGBColor DARK_GRAY;

	/**
	 * <code>RGBColor</code> whose color is {@link Color#GRAY}.
	 */
	public static final RGBColor GRAY;

	/**
	 * <code>RGBColor</code> whose color is {@link Color#GREEN}.
	 */
	public static final RGBColor GREEN;

	/**
	 * <code>RGBColor</code> whose color is {@link Color#LIGHT_GRAY}.
	 */
	public static final RGBColor LIGHT_GRAY;

	/**
	 * <code>RGBColor</code> whose color is {@link Color#MAGENTA}.
	 */
	public static final RGBColor MAGENTA;

	/**
	 * <code>RGBColor</code> whose color is {@link Color#ORANGE}.
	 */
	public static final RGBColor ORANGE;

	/**
	 * <code>RGBColor</code> whose color is {@link Color#PINK}.
	 */
	public static final RGBColor PINK;

	/**
	 * <code>RGBColor</code> whose color is {@link Color#RED}.
	 */
	public static final RGBColor RED;

	/**
	 * <code>RGBColor</code> whose color is {@link Color#YELLOW}.
	 */
	public static final RGBColor YELLOW;

	/**
	 * <code>RGBColor</code> whose color is {@link Color#WHITE}.
	 */
	public static final RGBColor WHITE;

	static final RGBColor[] STANDARD;

	public static final ManageableType $TYPE;

	static
	{
		$TYPE = new ManageableType (ClassAdapter.wrap (RGBColor.class),
									Tuple3fType.$TYPE, true)
			{
				@Override
				protected Object readObject (PersistenceInput in,
										 Object placeIn,
										 boolean fieldsProvided)
					throws IOException
				{
					boolean b;
					if (in instanceof XMLPersistenceReader)
					{
						// backwards compatibility with GroIMP <= 0.9.7.2
						String n = ((XMLPersistenceReader) in).peekName ();
						b = ("true".equals (n) || "false".equals (n)) && in.readBoolean ();
					}
					else
					{
						b = in.readBoolean ();
					}
					return b
						? STANDARD[in.readByte () % STANDARD.length]
						: super.readObject
							(in, !(placeIn instanceof RGBColor)
							 || (((RGBColor) placeIn).index >= 0)
							 ? new RGBColor () : placeIn, fieldsProvided);
				}
				
				
				@Override
				protected void write (Object value, PersistenceOutput out,
								  boolean onlyDiff)
					throws IOException
				{
					RGBColor s = (RGBColor) value;
					if (s.index >= 0)
					{
						out.writeBoolean (true);
						out.writeByte (s.index);
					}
					else
					{
						out.writeBoolean (false);
						super.write (value, out, onlyDiff);
					}
				}
				
				@Override
				public Object getRepresentative ()
				{
					return null;
				}
				
				@Override
				public Object newInstance ()
				{
					return new RGBColor ();
				}
			}.validate ();
		
		Color[] c = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY,
			 Color.GRAY, Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA,
			 Color.ORANGE, Color.PINK, Color.RED, Color.YELLOW,
			 Color.WHITE};
		STANDARD = new RGBColor[c.length];
		for (int i = 0; i < c.length; i++)
		{
			STANDARD[i] = new RGBColor (c[i].getRGB (), i);
		}
		int i = -1;
		BLACK = STANDARD[++i];
		BLUE = STANDARD[++i];
		CYAN = STANDARD[++i];
		DARK_GRAY = STANDARD[++i];
		GRAY = STANDARD[++i];
		GREEN = STANDARD[++i];
		LIGHT_GRAY = STANDARD[++i];
		MAGENTA = STANDARD[++i];
		ORANGE = STANDARD[++i];
		PINK = STANDARD[++i];
		RED = STANDARD[++i];
		YELLOW = STANDARD[++i];
		WHITE = STANDARD[++i];
		assert i + 1 == c.length;
	}


	private SharedObjectProvider sop;
	private final int index;
	private transient ObjectList refs = null;
	private transient Color awtColor;
	private transient int stamp = 0;


	public RGBColor (float red, float green, float blue)
	{
		super (red, green, blue);
		index = -1;
	}


	private RGBColor (int rgb, int index)
	{
		super (((rgb >> 16) & 255) / 255f, ((rgb >> 8) & 255) / 255f, (rgb & 255) / 255f);
		this.index = index;
	}


	public RGBColor ()
	{
		this (0.5f, 0.5f, 0.5f);
	}


	public RGBColor (Tuple3f color)
	{
		this (color.x, color.y, color.z);
	}


	public ManageableType getManageableType ()
	{
		return $TYPE;
	}


	@Override
	public Object clone ()
	{
		return (index >= 0) ? this : new RGBColor (this);
	}


	public boolean isPredefined ()
	{
		return index >= 0; 
	}



	public int getAverageColor ()
	{
		return Tuple3fType.colorToInt (this);
	}


	public void initProvider (SharedObjectProvider provider)
	{
		if (index >= 0)
		{
			return;
		}
		if (sop != null)
		{
			throw new IllegalStateException ();
		}
		sop = provider;
	}


	public SharedObjectProvider getProvider ()
	{
		return sop;
	}


	public synchronized void addReference (SharedObjectReference ref)
	{
		if (refs == null)
		{
			refs = new ObjectList (4, false);
		}
		refs.add (ref);
	}

	
	public synchronized void removeReference (SharedObjectReference ref)
	{
		if (refs != null)
		{
			refs.remove (ref);
		}
	}

	
	public synchronized void appendReferencesTo (java.util.List out)
	{
		if (refs != null)
		{
			out.addAll (refs);
		}
	}


	public float getFloatValue (ChannelData data, int channel)
	{
		if ((channel >= Channel.MIN_DERIVATIVE)
			&& (channel <= Channel.MAX_DERIVATIVE))
		{
			return 0;
		}
		switch (channel & 3)
		{
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return z;
			case 3:
				return 1;
			default:
				throw new AssertionError ();
		}
	}


	public Object getObjectValue (ChannelData data, int channel)
	{
		return data.forwardGetObjectValue (data.getData (null));
	}


	public void fieldModified (PersistenceField field, int[] indices, Transaction t)
	{
		stamp++;
		if ((t != null) && (sop != null))
		{
			t.fireSharedObjectModified (this);
		}
	}
	
	
	public int getStamp ()
	{
		return stamp;
	}

	
	public Manageable manageableReadResolve ()
	{
		return this;
	}

	public Object manageableWriteReplace ()
	{
		return this;
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


	public boolean isMutable ()
	{
		return index < 0;
	}


	public void prepareIcon ()	
	{
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
