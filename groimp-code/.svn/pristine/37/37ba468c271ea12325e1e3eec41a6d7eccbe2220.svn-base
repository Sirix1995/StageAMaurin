
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

import de.grogra.math.Channel;
import de.grogra.math.ChannelData;

public abstract class UVTransformation extends ChannelMapNode
{
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (UVTransformation.class);
		$TYPE.validate ();
	}

//enh:end



	@Override
	public float getFloatValue (ChannelData data, int channel)
	{
		ChannelData in = data.getData (input);
		boolean derive = false;
		switch (channel)
		{
			case Channel.DPXDU:
			case Channel.DPYDU:
			case Channel.DPZDU:
			case Channel.DPXDV:
			case Channel.DPYDV:
			case Channel.DPZDV:
				derive = true;
				// no break
			case Channel.U:
			case Channel.V:
				transform (in, data, derive);
				return data.getValidFloatValue (channel);
		}
		return data.forwardGetFloatValue (in);
	}


	protected abstract void transform (ChannelData in, ChannelData out,
									   boolean calculateDerivatives);


	protected static final void setDerivatives
		(float duds, float dudt, float dvds, float dvdt,
		 ChannelData in, ChannelData out)
	{
		for (int c = 0; c <= 2; c++)
		{
			out.setFloat
				(Channel.DPXDU + c,
				 in.getFloatValue (out, Channel.DPXDU + c) * duds
				 + in.getFloatValue (out, Channel.DPXDV + c) * dvds);
			out.setFloat
				(Channel.DPXDV + c,
				 in.getFloatValue (out, Channel.DPXDU + c) * dudt
				 + in.getFloatValue (out, Channel.DPXDV + c) * dvdt);
		}
	}

}
