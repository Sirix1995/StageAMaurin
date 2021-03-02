
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

package de.grogra.imp2d.objects;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import de.grogra.imp.objects.*;
import de.grogra.graph.*;
import de.grogra.math.*;
import de.grogra.imp2d.edit.Editable;

public class TextLabel extends Label
{

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new TextLabel ());
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
		return new TextLabel ();
	}

//enh:end


	public TextLabel ()
	{
		super ();
		setCaption ("Label");
	}


	private static final FontRenderContext FCTX
		= new FontRenderContext (new AffineTransform (), false, false);

	@Override
	protected Shape getShape (Object object, boolean asNode, Pool pool, GraphState gs)
	{
		String text;
		FontAdapter fa = null;
		if (object == this)
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				text = caption;
				fa = font;
			}
			else
			{
				text = (String) gs.checkObject (this, true, Attributes.CAPTION, caption);
				if (text != null)
				{
					fa = (FontAdapter) gs.checkObject (this, true, Attributes.FONT, font);
				}
			}
		}
		else
		{
			text = (String) gs.getObjectDefault (object, asNode, Attributes.CAPTION, null);
			if (text != null)
			{
				fa = (FontAdapter) gs.getObjectDefault (object, asNode, Attributes.FONT, null);
			}
		}
		Font f = FontAdapter.getFont (fa);
		char[] chars = text.toCharArray ();
		int lineCount = 1;
		double width = 0, height = 0;
		int len = chars.length, pos = 0;
		for (int i = 0; i <= len; i++)
		{
			if ((i == len) || (chars[i] == '\n'))
			{
				if (i > pos)
				{
					width = Math.max (width, f.getStringBounds (chars, pos, i, FCTX).getWidth ());
					height += f.getLineMetrics (chars, pos, i, FCTX).getHeight ();
				}
				if (i < len - 1)
				{
					lineCount++;
				}
				pos = i + 1;
			}
		}
		Rectangle2D.Double r = pool.r0;
		r.setFrameFromCenter (0, 0, 0.0055 * width, 0.0058 * height);
		return r;
	}


	@Override
	protected Editable getEditable ()
	{
		return null;
	}

}
