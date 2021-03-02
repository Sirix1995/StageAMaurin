
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

import de.grogra.reflect.UserFields;
import de.grogra.util.*;

public class Attributes
{

/*!!
#foreach ($t in ["Boolean", "Byte", "Short", "Char", "Int", "Long", "Float", "Double", "Object", "String", "Void"])

	public static final ${t}Attribute init
		(${t}Attribute a, String name, I18NBundle bundle, String resourceKey)
	{
		a.initializeName (name);
		a.initializeI18N (bundle, resourceKey);
		return a;
	}


	public static final ${t}Attribute init
		(${t}Attribute a, String simpleName, I18NBundle bundle)
	{
		return init (a, bundle.getBaseName () + '.' + simpleName, bundle,
					 "attribute/" + simpleName);
	}

#end
!!*/
//!! #* Start of generated code
// generated
	public static final BooleanAttribute init
		(BooleanAttribute a, String name, I18NBundle bundle, String resourceKey)
	{
		a.initializeName (name);
		a.initializeI18N (bundle, resourceKey);
		return a;
	}
// generated
// generated
	public static final BooleanAttribute init
		(BooleanAttribute a, String simpleName, I18NBundle bundle)
	{
		return init (a, bundle.getBaseName () + '.' + simpleName, bundle,
					 "attribute/" + simpleName);
	}
// generated
// generated
	public static final ByteAttribute init
		(ByteAttribute a, String name, I18NBundle bundle, String resourceKey)
	{
		a.initializeName (name);
		a.initializeI18N (bundle, resourceKey);
		return a;
	}
// generated
// generated
	public static final ByteAttribute init
		(ByteAttribute a, String simpleName, I18NBundle bundle)
	{
		return init (a, bundle.getBaseName () + '.' + simpleName, bundle,
					 "attribute/" + simpleName);
	}
// generated
// generated
	public static final ShortAttribute init
		(ShortAttribute a, String name, I18NBundle bundle, String resourceKey)
	{
		a.initializeName (name);
		a.initializeI18N (bundle, resourceKey);
		return a;
	}
// generated
// generated
	public static final ShortAttribute init
		(ShortAttribute a, String simpleName, I18NBundle bundle)
	{
		return init (a, bundle.getBaseName () + '.' + simpleName, bundle,
					 "attribute/" + simpleName);
	}
// generated
// generated
	public static final CharAttribute init
		(CharAttribute a, String name, I18NBundle bundle, String resourceKey)
	{
		a.initializeName (name);
		a.initializeI18N (bundle, resourceKey);
		return a;
	}
// generated
// generated
	public static final CharAttribute init
		(CharAttribute a, String simpleName, I18NBundle bundle)
	{
		return init (a, bundle.getBaseName () + '.' + simpleName, bundle,
					 "attribute/" + simpleName);
	}
// generated
// generated
	public static final IntAttribute init
		(IntAttribute a, String name, I18NBundle bundle, String resourceKey)
	{
		a.initializeName (name);
		a.initializeI18N (bundle, resourceKey);
		return a;
	}
// generated
// generated
	public static final IntAttribute init
		(IntAttribute a, String simpleName, I18NBundle bundle)
	{
		return init (a, bundle.getBaseName () + '.' + simpleName, bundle,
					 "attribute/" + simpleName);
	}
// generated
// generated
	public static final LongAttribute init
		(LongAttribute a, String name, I18NBundle bundle, String resourceKey)
	{
		a.initializeName (name);
		a.initializeI18N (bundle, resourceKey);
		return a;
	}
// generated
// generated
	public static final LongAttribute init
		(LongAttribute a, String simpleName, I18NBundle bundle)
	{
		return init (a, bundle.getBaseName () + '.' + simpleName, bundle,
					 "attribute/" + simpleName);
	}
// generated
// generated
	public static final FloatAttribute init
		(FloatAttribute a, String name, I18NBundle bundle, String resourceKey)
	{
		a.initializeName (name);
		a.initializeI18N (bundle, resourceKey);
		return a;
	}
// generated
// generated
	public static final FloatAttribute init
		(FloatAttribute a, String simpleName, I18NBundle bundle)
	{
		return init (a, bundle.getBaseName () + '.' + simpleName, bundle,
					 "attribute/" + simpleName);
	}
// generated
// generated
	public static final DoubleAttribute init
		(DoubleAttribute a, String name, I18NBundle bundle, String resourceKey)
	{
		a.initializeName (name);
		a.initializeI18N (bundle, resourceKey);
		return a;
	}
// generated
// generated
	public static final DoubleAttribute init
		(DoubleAttribute a, String simpleName, I18NBundle bundle)
	{
		return init (a, bundle.getBaseName () + '.' + simpleName, bundle,
					 "attribute/" + simpleName);
	}
// generated
// generated
	public static final ObjectAttribute init
		(ObjectAttribute a, String name, I18NBundle bundle, String resourceKey)
	{
		a.initializeName (name);
		a.initializeI18N (bundle, resourceKey);
		return a;
	}
// generated
// generated
	public static final ObjectAttribute init
		(ObjectAttribute a, String simpleName, I18NBundle bundle)
	{
		return init (a, bundle.getBaseName () + '.' + simpleName, bundle,
					 "attribute/" + simpleName);
	}
// generated
// generated
	public static final StringAttribute init
		(StringAttribute a, String name, I18NBundle bundle, String resourceKey)
	{
		a.initializeName (name);
		a.initializeI18N (bundle, resourceKey);
		return a;
	}
// generated
// generated
	public static final StringAttribute init
		(StringAttribute a, String simpleName, I18NBundle bundle)
	{
		return init (a, bundle.getBaseName () + '.' + simpleName, bundle,
					 "attribute/" + simpleName);
	}
// generated
// generated
	public static final VoidAttribute init
		(VoidAttribute a, String name, I18NBundle bundle, String resourceKey)
	{
		a.initializeName (name);
		a.initializeI18N (bundle, resourceKey);
		return a;
	}
// generated
// generated
	public static final VoidAttribute init
		(VoidAttribute a, String simpleName, I18NBundle bundle)
	{
		return init (a, bundle.getBaseName () + '.' + simpleName, bundle,
					 "attribute/" + simpleName);
	}
// generated
//!! *# End of generated code

	public static final StringAttribute NAME
		= init (new StringAttribute (), "name", GraphUtils.I18N);

	public static final FloatAttribute WEIGHT
		= init (new FloatAttribute (null), "weight", GraphUtils.I18N);
	
	public static final int LAYER_COUNT = 16;

	public static final IntAttribute LAYER
		= init (new IntAttribute (), "layer", GraphUtils.I18N);

	public static final ObjectAttribute USER_FIELDS
		= init (new ObjectAttribute (UserFields.class, false, null), "userFields", GraphUtils.I18N);
	
	//multiscale begin
	public static final BooleanAttribute VISIBLE
	= init (new BooleanAttribute (), "visible", GraphUtils.I18N);
	
	public static final ObjectAttribute RGG
	= init (new ObjectAttribute (UserFields.class, false, null), "rgg", GraphUtils.I18N);
	//multiscale end
}
