
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

package de.grogra.util;

import java.util.ArrayList;

public final class Quantity
{
	public static final Quantity FRACTION = new Quantity
		(null, new Unit ("%", 0.01, true));

	public static final Quantity TIME = new Quantity
		(null, new Unit ("s", 1, true));

	public static final Quantity LENGTH = new Quantity
		(null, new Unit ("m", 1, true));

	public static final Quantity MASS = new Quantity
		(null, new Unit ("kg", 1, true));

	public static final Quantity ANGLE = new Quantity
		(null, new Unit ("deg", Math.PI / 180, true));

	public static final Quantity POWER = new Quantity
		(null, new Unit ("W", 1, true));

	public static final Quantity POWER_PER_AREA = new Quantity
		(null, new Unit ("W/m\u00b2", 1, true));
	
	public static final Quantity TEMPERATURE = new Quantity
		(null, new Unit ("K", 1, true));

	static
	{
		FRACTION.add ("ppm", 1e-6);

		LENGTH.add ("km", 1000);
		LENGTH.add ("cm", 0.01);
		LENGTH.add ("mm", 0.001);
		LENGTH.add ("nm", 0.000000001);
		
		MASS.add ("g", 0.001);
		MASS.add ("t", 1000);
		
		ANGLE.add ("arcsec", Math.PI / (180 * 3600));
		ANGLE.add (new Unit ("rad", 1, false));

		POWER.add ("kW", 1000);
		POWER.add ("MW", 1000000);
	}


	public static Quantity get (String name)
	{
		return "TIME".equals (name) ? TIME
			: "LENGTH".equals (name) ? LENGTH
			: "MASS".equals (name) ? MASS
			: "ANGLE".equals (name) ? ANGLE
			: "POWER".equals (name) ? POWER
			: "POWER_PER_AREA".equals (name) ? POWER_PER_AREA
			: "TEMPERATURE".equals(name) ? TEMPERATURE
			: null;
	}


	private final Quantity base;
	private final Unit preferredUnit;
	private final ArrayList units;


	private Quantity (Quantity base, Unit preferredUnit)
	{
		this.base = (base != null) ? base.base : null;
		this.preferredUnit = preferredUnit;
		this.units = (base != null) ? base.units : new ArrayList ();
		add (preferredUnit);
	}


	public Quantity newInstance (Unit preferredUnit)
	{
		return new Quantity (this, preferredUnit);
	}


	public Unit getPreferredUnit ()
	{
		return preferredUnit;
	}


	public void add (Unit unit)
	{
		if (!units.contains (unit))
		{
			units.add (unit);
		}
	}


	public void add (String unit, double factor)
	{
		units.add (new Unit (unit, factor, true));
	}


	public Unit chooseUnit (float value)
	{
		Unit p = preferredUnit;
		float maxQ = 0;
		for (int i = 0; i < units.size (); i++)
		{
			Unit u = (Unit) units.get (i);
			if (u.checkWhenChoosing ())
			{
				float q = u.getRatio (value);
				if (q > maxQ)
				{
					p = u;
					maxQ = q;
				}
			}
		}
		if (p != preferredUnit)
		{
			float q = preferredUnit.getRatio (value);
			if ((q > 0) && (maxQ < 10 * q))
			{
				return preferredUnit;
			}
		}
		return p;
	}


	public Unit[] getUnits ()
	{
		return (Unit[]) units.toArray (new Unit[units.size ()]);
	}


	public String toString (float value)
	{
		return chooseUnit (value).toString (value);
	}


	public String toString (double value)
	{
		return chooseUnit ((float) value).toString (value);
	}


	public Unit parseUnit (String s, Unit defaultUnit)
	{
		for (int i = 0; i < units.size (); i++)
		{
			Unit u = (Unit) units.get (i);
			if (u.isSpecified (s))
			{
				return u;
			}
		}
		return defaultUnit;
	}


	public Unit parseUnit (String s)
	{
		return parseUnit (s, preferredUnit);
	}


	@Override
	public boolean equals (Object o)
	{
		return (o instanceof Quantity) && (((Quantity) o).base == base); 
	}


	@Override
	public int hashCode ()
	{
		return (base == this) ? super.hashCode () : base.hashCode ();
	}

}
