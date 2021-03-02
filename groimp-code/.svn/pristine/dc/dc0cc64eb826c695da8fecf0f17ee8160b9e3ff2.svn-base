
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

package de.grogra.imp3d.shading;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import de.grogra.imp.objects.ImageAdapter;
import de.grogra.math.ChannelData;

public class ImageMap extends SurfaceMap
{
	ImageAdapter image;
	//enh:field
	
	boolean bilinearFiltering = true;
	//enh:field

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field image$FIELD;
	public static final NType.Field bilinearFiltering$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (ImageMap.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 1:
					((ImageMap) o).bilinearFiltering = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 1:
					return ((ImageMap) o).bilinearFiltering;
			}
			return super.getBoolean (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((ImageMap) o).image = (ImageAdapter) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((ImageMap) o).image;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new ImageMap ());
		$TYPE.addManagedField (image$FIELD = new _Field ("image", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (ImageAdapter.class), null, 0));
		$TYPE.addManagedField (bilinearFiltering$FIELD = new _Field ("bilinearFiltering", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 1));
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new ImageMap ();
	}

//enh:end


	public ImageAdapter getImageAdapter ()
	{
		return image;
	}


	public void setImageAdapter (ImageAdapter image)
	{
		this.image = image;
	}

	
	public boolean hasBilinearFiltering ()
	{
		return this.bilinearFiltering;
	}
	
	public void setBilinearFiltering(boolean bilinear) {
		this.bilinearFiltering = bilinear;
	}

	public int getAverageColor ()
	{
		return 0xff808080;
	}


	@Override
	protected float getDu ()
	{
		BufferedImage img = getBufferedImage ();
		return (img != null) ? 1f / img.getWidth () : 0.01f;
	}


	@Override
	protected float getDv ()
	{
		BufferedImage img = getBufferedImage ();
		return (img != null) ? 1f / img.getHeight () : 0.01f;
	}


	private BufferedImage getBufferedImage ()
	{
		return (image != null) ? image.getNativeImage () : null;
	}


	@Override
	protected float getFloatValueImpl
		(float u, float v, ChannelData data, int channel)
	{
		float x;
		if (bilinearFiltering)
			x = getFloatValueImplBilinear(u, v, data, channel);
		else
			x = getFloatValueImplSimple(u, v, data, channel);
		return x;
	}

	private static void validateCache(Raster r, ChannelData data)
	{
		if ((data.cache.size != 2) || (data.cache.get(0) != r))
		{
			data.cache.setSize(2);
			data.cache.set(0, r);
			data.cache.set(1, null);
		}
	}

	private static Object getDataElements(Raster r, ChannelData data, int x, int y)
	{
		Object dataElements = r.getDataElements (x, y, data.cache.get(1));
		data.cache.set(1, dataElements);
		return dataElements;
	}

	protected float getFloatValueImplSimple
		(float u, float v, ChannelData data, int channel)
	{
		BufferedImage img = getBufferedImage ();
		if (img == null)
		{
			return ((channel & 3) < 3) ? 0.5f : 1;
		}
		Raster r = img.getRaster ();
		int w = r.getWidth (), h = r.getHeight ();
	
		u -= (int) u;
		if (u < 0)
		{
			u++;
		}
		u *= w;
		if ((u <= 0) || (u >= w))
		{
			u = 0;
		}
		v -= (int) v;
		if (v < 0)
		{
			v++;
		}
		v *= h;
		if ((v <= 0) || (v >= h))
		{
			v = 0;
		}
		int x = (int) u;
		int y = (int) v;
		
		float s = 0;

		validateCache(r, data);
		Object dataElements = getDataElements(r, data, x, h - 1 - y);
		int a;
		switch (channel & 3)
		{
			case 0:
				a = img.getColorModel ().getRed (dataElements);
				break;
			case 1:
				a = img.getColorModel ().getGreen (dataElements);
				break;
			case 2:
				a = img.getColorModel ().getBlue (dataElements);
				break;
			case 3:
				a = img.getColorModel ().getAlpha (dataElements);
				break;
			default:
				throw new AssertionError ();
		}
		s = a;
		return s / 255f;
	}
	
	protected float getFloatValueImplBilinear
		(float u, float v, ChannelData data, int channel)
	{
		BufferedImage img = getBufferedImage ();
		if (img == null)
		{
			return ((channel & 3) < 3) ? 0.5f : 1;
		}
		Raster r = img.getRaster ();
		int w = r.getWidth (), h = r.getHeight ();
		u -= (int) u;
		if (u < 0)
		{
			u++;
		}
		u *= w;
		if ((u <= 0) || (u >= w))
		{
			u = 0;
		}
		v -= (int) v;
		if (v < 0)
		{
			v++;
		}
		v *= h;
		if ((v <= 0) || (v >= h))
		{
			v = 0;
		}
		int x = (int) u;
		u -= x;
		int y = (int) v;
		v -= y;
		float s = 0;
		validateCache(r, data);
		for (int dx = 0; dx <= 1; dx++)
		{
			for (int dy = 0; dy <= 1; dy++)
			{
				Object dataElements = getDataElements (r, data, (x + dx) % w, (h - 1) - ((y + dy) % h));
				int a;
				switch (channel & 3)
				{
					case 0:
						a = img.getColorModel ().getRed (dataElements);
						break;
					case 1:
						a = img.getColorModel ().getGreen (dataElements);
						break;
					case 2:
						a = img.getColorModel ().getBlue (dataElements);
						break;
					case 3:
						a = img.getColorModel ().getAlpha (dataElements);
						break;
					default:
						throw new AssertionError ();
				}
				s += ((dx == 0) ? (1 - u) : u) * ((dy == 0) ? (1 - v) : v) * a;
			}
		}
		return s / 255f;
	}
	
	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}

/*
	public BufferedImage getImage (int supersampling, boolean useInput)
	{
		if (((input == null) || !useInput) && (image != null))
		{
			return image.getBufferedImage ();
		}
		else
		{
			return super.getImage (supersampling, useInput);
		}
	}
*/
}
