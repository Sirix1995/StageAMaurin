
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

package de.grogra.pf.math;

import java.io.IOException;
import javax.vecmath.*;
import de.grogra.math.SplineFunction;
import de.grogra.pf.io.*;

public class FunctionReader extends FilterBase implements ObjectSource
{
	public static final IOFlavor FLAVOR
		= IOFlavor.valueOf (SplineFunction.class);


	public FunctionReader (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (FLAVOR);
	}


	public Object getObject () throws IOException
	{
		double[][] a = (double[][]) ((ObjectSource) source).getObject ();
		if (a.length < 2)
		{
			throw new IOException ();
		}
		Point2f[] d = new Point2f[a.length];
		for (int i = 0; i < a.length; i++)
		{
			if (a[i].length < 2)
			{
				throw new IOException ();
			}
			d[i] = new Point2f ((float) a[i][0], (float) a[i][1]);
		}
		return new SplineFunction (d, SplineFunction.B_SPLINE);
	}

}
