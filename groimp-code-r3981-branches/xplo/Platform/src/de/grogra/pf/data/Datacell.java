
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

package de.grogra.pf.data;

import java.io.ObjectStreamException;
import java.util.StringTokenizer;

/**
 * A <code>Datacell</code> represents a aingle cell of a
 * tabular {@link de.grogra.pf.data.Dataset}. It may contain
 * either a text value or up to three numbers. 
 *
 * @author Ole Kniemeyer
 */
public final class Datacell implements java.io.Serializable
{
	private static final class Null extends Number
	{
		private static final long serialVersionUID = -3690894023817622459L;

		static final Null INSTANCE = new Null ();
	
		private Null ()
		{
		}

		@Override
		public double doubleValue ()
		{
			return 0;
		}

		@Override
		public float floatValue ()
		{
			return 0;
		}

		@Override
		public int intValue ()
		{
			return 0;
		}

		@Override
		public long longValue ()
		{
			return 0;
		}

	    private Object readResolve() throws ObjectStreamException
	    {
	    	return INSTANCE;
	    }
	}

	private static final long serialVersionUID = -6908940238176224539L;


	Number x = Null.INSTANCE;
	Number y = Null.INSTANCE;
	Number z = Null.INSTANCE;
	String text = null;

	
	private final Dataset dataset;
	

	public Datacell (String value)
	{
		dataset = null;
		text = value;
	}


	Datacell (Dataset ds)
	{
		dataset = ds;
	}


	public boolean isNull ()
	{
		return (x == Null.INSTANCE) && (y == Null.INSTANCE) && (z == Null.INSTANCE);
	}

	public boolean isScalar ()
	{
		return x == y;
	}

	public boolean isXNull ()
	{
		return x == Null.INSTANCE;
	}

	public boolean isYNull ()
	{
		return y == Null.INSTANCE;
	}

	public boolean isZNull ()
	{
		return z == Null.INSTANCE;
	}

	public double getX ()
	{
		return x.doubleValue ();
	}


	public double getY ()
	{
		return y.doubleValue ();
	}


	public double getZ ()
	{
		return z.doubleValue ();
	}


	public void setX (Number v)
	{
		x = v;
		dataset.fireDatasetChanged (this);
	}
	

	public void setY (Number v)
	{
		y = v;
		dataset.fireDatasetChanged (this);
	}
	

	public void setZ (Number v)
	{
		z = v;
		dataset.fireDatasetChanged (this);
	}
	

	public void set (Number v)
	{
		x = y = z = v;
		dataset.fireDatasetChanged (this);
	}


	public void setText (String v)
	{
		text = v;
		dataset.fireDatasetChanged (this);
	}

	
	public void set (Number x, Number y)
	{
		this.x = x;
		this.y = this.z = y;
		dataset.fireDatasetChanged (this);
	}

	
	public void set (Number x, Number y, Number z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		dataset.fireDatasetChanged (this);
	}

	
	private void makeNumeric ()
	{
		if (text == null)
		{
			return;
		}
		StringTokenizer tok = new StringTokenizer (text);
		text = null;
		Double[] values = new Double[3];
		int i = 0;
		while ((i < 3) && tok.hasMoreTokens ())
		{
			try
			{
				values[i] = new Double (tok.nextToken ());
				i++;
			}
			catch (NumberFormatException e)
			{
			}
		}
		switch (i)
		{
			case 1:
				x = y = z = values[0];
				break;
			case 2:
				x = values[0];
				y = z = values[1];
				break;
			case 3:
				x = values[0];
				y = values[1];
				z = values[2];
				break;
		}
	}

	
	void set (Datacell cell)
	{
		this.x = cell.x;
		this.y = cell.y;
		this.z = cell.z;
		if (this.text != null)
		{
			this.text = cell.toString ();
		}
		else if ((cell.text != null) && (cell.dataset == null))
		{
			this.text = cell.text;
			makeNumeric ();
		}
		dataset.fireDatasetChanged (this);
	}


	@Override
	public String toString ()
	{
		if (text != null)
		{
			return text;
		}
		else if (y == z)
		{
			if (x == y)
			{
				return (x == Null.INSTANCE) ? "" : x.toString ();
			}
			else
			{
				return x + " " + y;
			}
		}
		else
		{
			return x + " " + y + " " + z;
		}
	}

}
