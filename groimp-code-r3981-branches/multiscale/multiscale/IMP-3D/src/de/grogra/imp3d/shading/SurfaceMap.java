
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

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import de.grogra.math.Channel;
import de.grogra.math.ChannelData;

public abstract class SurfaceMap extends ColorMapNode
{
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (SurfaceMap.class);
		$TYPE.validate ();
	}

//enh:end


	protected float getDu ()
	{
		return 0.005f;
	}


	protected float getDv ()
	{
		return 0.005f;
	}


	@Override
	public float getFloatValue (ChannelData data, int channel)
	{
		ChannelData in = getInputData (data);
		float u = in.getFloatValue (data, Channel.U);
		float v = in.getFloatValue (data, Channel.V);
		boolean ddu;
		if ((ddu = (channel >= Channel.DPXDU) && (channel <= Channel.DPZDU))
			|| ((channel >= Channel.DPXDV) && (channel <= Channel.DPZDV)))
		{
			float f = getFloatValueImpl (u, v, data, channel & 3);
			float d;
			if (ddu)
			{
				u += (d = getDu ());
			}
			else
			{
				v += (d = getDv ());
			}
			u = (getFloatValueImpl (u, v, data, channel & 3) - f) / d;
		}
		else
		{
			u = getFloatValueImpl (u, v, data, channel); 
		}
		return u;
	}


	protected float getFloatValueImpl
		(float u, float v, ChannelData data, int channel)
	{
		throw new AssertionError ("Method not implemented in " + getClass ());
	}


	@Override
	protected void renderLine (BufferedImage image, int supersampling,
							   int iy, boolean useInput, java.util.Map cache)
	{
		ChannelData src = (ChannelData) cache.get ("src");
		if (src == null)
		{
			src = new ChannelData ();
			cache.put ("src", src);
		}
		ChannelData sink = src.createSink (this);
		sink.setProperty ("ignoreInput", Boolean.valueOf (!useInput));
		Vector4f sum = src.v4f0;
		Vector3f tmp = src.v3f2;
		int sx = image.getWidth (), sy = image.getHeight ();
		for (int ix = 0; ix < sx; ix++)
		{
			sum.set (0, 0, 0, 0);
			for (int fy = 0; fy < supersampling; fy++)
			{
				float v = (float) (2 * supersampling * sy
								   - (2 * (supersampling * iy + fy) + 1))
					/ (2 * supersampling * sy); 
				for (int fx = 0; fx < supersampling; fx++)
				{
					float u = (float) (2 * (supersampling * ix + fx) + 1)
						/ (2 * supersampling * sx); 
					src.setFloat (Channel.U, u);
					src.setFloat (Channel.V, v);
					sink.getTuple3f (tmp, null, Channel.R);
					sum.x += tmp.x;
					sum.y += tmp.y;
					sum.z += tmp.z;
					sum.w += sink.getFloatValue (null, Channel.A);
				}
			}
			sum.clamp (0, supersampling * supersampling);
			sum.scale (255.99f / (supersampling * supersampling));
			image.setRGB (ix, iy, ((int) sum.w << 24) + ((int) sum.x << 16)
						  + ((int) sum.y << 8) + (int) sum.z);
		}
	}


}
