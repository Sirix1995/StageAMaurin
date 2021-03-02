
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

package de.grogra.pf.registry.expr;

import java.awt.Point;
import de.grogra.pf.registry.RegistryContext;
import de.grogra.util.StringMap;

public final class PointConst extends Expression
{
	Point value;

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new PointConst ());
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new PointConst ();
	}

//enh:end

	public PointConst ()
	{
		super (null);
	}


	@Override
	public Object evaluate (RegistryContext ctx, StringMap args)
	{
		return value;
	}


	@Override
	protected boolean readAttribute (String uri, String name, String value)
		throws org.xml.sax.SAXException
	{
		if ("".equals (uri) && "value".equals (name))
		{
			this.value = de.grogra.util.Utils.parsePoint (value);
			return true;
		}
		return super.readAttribute (uri, name, value);
	}

}
