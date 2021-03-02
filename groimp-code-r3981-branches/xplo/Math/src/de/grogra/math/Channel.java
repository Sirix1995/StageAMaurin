
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

import de.grogra.util.*;

public final class Channel
{
	public static final int PX = 0;
	public static final int PY = 1;
	public static final int PZ = 2;

	public static final int NX = 4;
	public static final int NY = 5;
	public static final int NZ = 6;

	public static final int DPXDU = 8;
	public static final int DPYDU = 9;
	public static final int DPZDU = 10;

	public static final int DPXDV = 12;
	public static final int DPYDV = 13;
	public static final int DPZDV = 14;

	public static final int U = 16;
	public static final int V = 17;
	public static final int W = 18;

	public static final int X = 20;
	public static final int Y = 21;
	public static final int Z = 22;

	public static final int R = 24;
	public static final int G = 25;
	public static final int B = 26;
	public static final int A = 27;

	public static final int IOR = 28;


	public static final int MIN_DERIVATIVE = DPXDU;
	public static final int MAX_DERIVATIVE = DPZDV;


	private static final StringMap channels;
	private static final Int2ObjectMap channelsById;
	private static int nextId;

	static
	{
		channels = new StringMap (64, true);
		channelsById = new Int2ObjectMap (64);

		add ("px", PX);
		add ("py", PY);
		add ("pz", PZ);

		add ("nx", NX);
		add ("ny", NY);
		add ("nz", NZ);

		add ("dpxdu", DPXDU);
		add ("dpydu", DPYDU);
		add ("dpzdu", DPZDU);

		add ("dpxdv", DPXDV);
		add ("dpydv", DPYDV);
		add ("dpzdv", DPZDV);

		add ("u", U);
		add ("v", V);
		add ("w", W);

		add ("x", X);
		add ("y", Y);
		add ("z", Z);

		add ("r", R);
		add ("g", G);
		add ("b", B);

		add ("ior", IOR);
	}


	private static Channel add (String name, int id)
	{
		Channel c = new Channel (name, id);
		channels.put (name, c);
		channelsById.put (id, c);
		nextId = Math.max (nextId, id + 1);
		return c;
	}


	public static int getCurrentChannelCount ()
	{
		return nextId;
	}


	public static synchronized Channel get (String name)
	{
		Channel c = (Channel) channels.get (name);
		return (c != null) ? c : add (name, nextId);
	}


	public static Channel get (int id)
	{
		return (Channel) channelsById.get (id);
	}


	private final String name;
	private final transient int id;

	private Channel (String name, int id)
	{
		this.name = name;
		this.id = id;
	}


	public String getName ()
	{
		return name;
	}


	public int getId ()
	{
		return id;
	}


	private Object readResolve ()
	{
		return get (name);
	}


	@Override
	public String toString ()
	{
		return "Channel " + name + " (id=" + id + ')';
	}

}
