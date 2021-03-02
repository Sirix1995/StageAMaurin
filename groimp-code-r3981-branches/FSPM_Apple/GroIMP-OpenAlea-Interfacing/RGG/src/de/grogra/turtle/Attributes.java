
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

package de.grogra.turtle;

import de.grogra.graph.*;
import de.grogra.util.Quantity;

public class Attributes extends de.grogra.rgg.Attributes
{
	public static final FloatAttribute MASS
		= init (new FloatAttribute (Quantity.MASS), "mass", I18N);

	public static final FloatAttribute PARAMETER
		= init (new FloatAttribute (null), "parameter", I18N);
	
	public static final FloatAttribute HEARTWOOD
		= init (new FloatAttribute (null), "heartwood", I18N);
	
	public static final FloatAttribute CARBON
		= init (new FloatAttribute (null), "carbon", I18N);

	public static final FloatAttribute TROPISM_STRENGTH
		= init (new FloatAttribute (null), "tropismStrength", I18N);

	public static final FloatAttribute ARGUMENT
		= init (new FloatAttribute (null), "argument", I18N);

	public static final FloatAttribute REL_POSITION
		= init (new FloatAttribute (null), "relPosition", I18N);

	public static final IntAttribute ORDER
		= init (new IntAttribute (), "order", I18N);
	
	public static final IntAttribute LOCAL_SCALE
		= init (new IntAttribute (), "localScale", I18N);

	public static final IntAttribute DTG_COLOR
		= init (new IntAttribute (), "color.dtg", I18N);

	public static final IntAttribute GENERATIVE_DISTANCE
		= init (new IntAttribute (), "generativeDistance", I18N);

	public static final IntAttribute INTERNODE_COUNT
		= init (new IntAttribute (), "internodeCount", I18N);
	
	public static final IntAttribute NO_OF_YEAR
	= init (new IntAttribute (), "noOfYear", I18N);

	public static final ObjectAttribute LEFT
		= init (new de.grogra.imp.objects.Vector3dAttribute (), "left", I18N);

	public static final ObjectAttribute TURTLE_MODIFIER
		= init (new ObjectAttribute (TurtleModifier.class, false, null), "turtleModifier", I18N);

}
