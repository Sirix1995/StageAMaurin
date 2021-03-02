
// NOTE: This file was generated automatically.

// ********************************************
// *               DO NOT EDIT!               *
// ********************************************


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


package de.grogra.graph;

import de.grogra.reflect.*;
import de.grogra.util.*;

public class DoubleAttribute extends Attribute<Double>
{


	public DoubleAttribute (Type type, Quantity quantity)
	{
		super (type, quantity);
	}


	public DoubleAttribute (Quantity quantity)
	{
		this (Type.DOUBLE, quantity);
	}



	@Override
	public final Double get (Object o, boolean asNode, GraphState gs)
	{
		return Double.valueOf (gs.getDouble (o, asNode, this));
	}


	@Override
	public final Double set (Object o, boolean asNode, Object value, GraphState gs)
	{
		double v = setDouble (o, asNode, (Double) (((Number) (value)).doubleValue ()), gs);
		return Double.valueOf (v);
	}




	protected double getDerived (Object object, boolean asNode,
								GraphState gs)
	{
		throw new AssertionError ("getDerived not implemented in "
								  + this.getClass ());
	}


	public double setDouble (Object object, boolean asNode, double value, GraphState gs)
	{
		if (isDerived ())
		{
			return setDerived (object, asNode, value, gs);
		}
		AttributeAccessor a;
		if ((a = gs.getGraph ().getAccessor (object, asNode, this)) != null)
		{
			return ((DoubleAttributeAccessor) a).setDouble (object, value, gs);
		}
		throw new NoSuchKeyException (object, this);
	}


	protected double setDerived (Object object, boolean asNode, double value, GraphState gs)
	{
		throw new UnsupportedOperationException ();
	}



	public double getMinValue ()
	{
		return -Double.MAX_VALUE;
	}


	public double getMaxValue ()
	{
		return Double.MAX_VALUE;
	}

}

