
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


package de.grogra.imp.objects;

import javax.vecmath.*;
import de.grogra.graph.*;

public class Matrix4fAttribute extends ObjectAttribute
{
	public static final Matrix4f IDENTITY = new Matrix4f ();

	static
	{
		IDENTITY.setIdentity ();
	}


	public Matrix4fAttribute ()
	{
		super (de.grogra.math.Matrix4fType.TYPE, true, null);
	}


	public Object cloneValue (Object value)
	{
		return new Matrix4f ((Matrix4f) value);
	}

}

