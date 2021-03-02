
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

package de.grogra.pf.ui.edit;

import de.grogra.reflect.Type;
import de.grogra.pf.ui.edit.PropertyEditorTree.Node;

public class StringEditor extends PropertyEditor
{
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new StringEditor ());
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
		return new StringEditor ();
	}

//enh:end

	private StringEditor ()
	{
		this (null);
	}


	public StringEditor (String key)
	{
		super (key);
	}


	@Override
	public boolean isNullAllowed ()
	{
		return true;
	}

	
	@Override
	public Node createNodes (PropertyEditorTree tree, Property p, String label)
	{
		return tree.isMenu () ? null : tree.new PropertyNode
			(p, p.getToolkit ().createStringWidget (this), label);
	}


	@Override
	public Type getPropertyType ()
	{
		return Type.STRING;
	}
}
