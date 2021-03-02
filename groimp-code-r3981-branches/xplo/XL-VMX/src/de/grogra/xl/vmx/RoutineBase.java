
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

package de.grogra.xl.vmx;

public abstract class RoutineBase implements Routine
{
	protected final int id;

	private final boolean javaParams;
	private final int paramSize;
	private final int frameSize;
	private final int jframeSize;
	
	
	public RoutineBase (int id, boolean javaParams, int paramSize,
						int jframeSize, int frameSize)
	{
		this.id = id;
		this.javaParams = javaParams;
		this.paramSize = paramSize;
		this.frameSize = frameSize;
		this.jframeSize = jframeSize;
	}


	public boolean hasJavaParameters ()
	{
		return javaParams;
	}


	public int getParameterSize ()
	{
		return paramSize;
	}


	public int getJavaFrameSize ()
	{
		return jframeSize;
	}


	public int getFrameSize ()
	{
		return frameSize;
	}
	
}
