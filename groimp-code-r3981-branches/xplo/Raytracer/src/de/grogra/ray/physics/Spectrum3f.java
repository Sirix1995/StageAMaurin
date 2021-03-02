
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

package de.grogra.ray.physics;

import javax.vecmath.*;


import de.grogra.ray.util.Ray;

/**
 * This class implements a spectrum which is represented by three
 * <code>float</code> values which are interpreted as the red,
 * green and blue part of the spectrum. Specta of this class are
 * compatible with each other and with spectra
 * of class <code>Spectrum3d</code>.
 * 
 * @author Ole Kniemeyer
 */
public class Spectrum3f extends Color3f implements Spectrum
{
	public Spectrum3f ()
	{
	}

	public Spectrum3f (float x, float y, float z)
	{
		super (x, y, z);
	}

	public void clampMinZero ()
	{
		clampMin (0f);
	}

	public double evaluateDouble (double nu)
	{
		// TODO
		return 0;
	}

	public void setZero ()
	{
		x = y = z = 0;
	}

	public void setIdentity ()
	{
		x = y = z = 1;
	}

	@Override
	public Spectrum3f clone ()
	{
		return new Spectrum3f (x, y, z);
	}

	public Spectrum3f newInstance ()
	{
		return new Spectrum3f ();
	}

	public void scale (double factor)
	{
		x *= factor;
		y *= factor;
		z *= factor;
	}

	public void add (Spectrum s)
	{
		if (s instanceof Tuple3d)
		{
			Tuple3d t = (Tuple3d) s;
			x += t.x;
			y += t.y;
			z += t.z;
		}
		else
		{
			add ((Tuple3f) s);
		}
	}

	public void sub (Spectrum s)
	{
		if (s instanceof Tuple3d)
		{
			Tuple3d t = (Tuple3d) s;
			x -= t.x;
			y -= t.y;
			z -= t.z;
		}
		else
		{
			sub ((Tuple3f) s);
		}
	}

	public void dot (Spectrum s, Tuple3d out)
	{
		if (s instanceof Tuple3d)
		{
			Tuple3d t = (Tuple3d) s;
			out.x = x * t.x;
			out.y = y * t.y;
			out.z = z * t.z;
		}
		else
		{
			Tuple3f t = (Tuple3f) s;
			out.x = x * t.x;
			out.y = y * t.y;
			out.z = z * t.z;
		}
	}

	public void mul (Spectrum s)
	{
		if (s instanceof Tuple3d)
		{
			mul ((Tuple3d) s);
		}
		else
		{
			mul ((Tuple3f) s);
		}
	}

	public void mul (Tuple3d t)
	{
		x *= t.x;
		y *= t.y;
		z *= t.z;
	}

	public void mul (Tuple3f t)
	{
		x *= t.x;
		y *= t.y;
		z *= t.z;
	}

	public void div (Spectrum s)
	{
		if (s instanceof Tuple3d)
		{
			div ((Tuple3d) s);
		}
		else
		{
			div ((Tuple3f) s);
		}
	}

	public void div (Tuple3d t)
	{
		if (x!=0) x /= t.x;
		if (y!=0) y /= t.y;
		if (z!=0) z /= t.z;
	}

	public void div (Tuple3f t)
	{
		if (x!=0) x /= t.x;
		if (y!=0) y /= t.y;
		if (z!=0) z /= t.z;
	}
	
	
	public void get (Tuple3d out)
	{
		out.set (this);
	}

	public double integrate ()
	{
		return x + y + z;
	}

	public void set (Spectrum s)
	{
		s.get (this);
	}
	
	public double sum ()
	{
		return x+y+z;
	}	
	
	
	public double getMax(){
		double max = x;
		max = Math.max(max, y);
		max = Math.max(max, z);
		return max;
	}
	
}
