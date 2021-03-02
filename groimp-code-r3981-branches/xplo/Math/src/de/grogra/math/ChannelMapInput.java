
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

/**
 * This class serves as a base class for input to
 * {@link ChannelMap}-based computations. Its only purpose is to provide an
 * instance of {@link ChannelData} which may be reused for several
 * invokations of methods of {@link ChannelMap}, thus reducing
 * the number of heap operations.
 *
 * @author Ole Kniemeyer
 */
public class ChannelMapInput
{

	/**
	 * This field provides a {@link ChannelData} instance for use
	 * in the invoked shader implementation.
	 */
	private final ChannelData userData = new ChannelData ();
		
	/**
	 * This field records the current sink of {@link #userData}.
	 */
	private ChannelMap sink;
		
	/**
	 * This field records the current default map of {@link #userData}.
	 */
	private ChannelMap def;

	
	/**
	 * Returns an empty instance of {@link ChannelData} for the use in
	 * method implementations that perform computations based
	 * on {@link ChannelMap ChannelMaps}.
	 * It is recommended to use
	 * this instance rather than a new instance of {@link ChannelData}
	 * in order to avoid unnecessary and time consuming heap operations.
	 * 
	 * @param sink the invoker's sink map for the channel data
	 * @param def the invoker's default map for the channel data
	 *  
	 * @return an empty instanceof of {@link ChannelData}
	 */
	public ChannelData getUserData (ChannelMap sink, ChannelMap def)
	{
		if ((sink != this.sink) || (def != this.def))
		{
			userData.clear ();
			this.sink = sink;
			this.def = def;
		}
		return userData;
	}

}
