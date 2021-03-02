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

import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.UIToolkit;
import de.grogra.pf.ui.edit.PropertyEditorTree.Node;
import de.grogra.pf.ui.util.ButtonWidget;

public class ButtonEditor extends PropertyEditor
{

	private ButtonEditor ()
	{
		this (null);
	}

	public ButtonEditor (String key)
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
		Item cmd = (Item) getBranch ();
		if (!(cmd instanceof Command))
		{
			return null;
		}
		ButtonWidget w = new ButtonWidget ((Command) cmd, p);
		w.setButton (p.getToolkit ().createButton (label, tree.isMenu () ? UIToolkit.FOR_MENU : 0, w, tree.getContext ()));
		return tree.new PropertyNode (p, w, "");
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new ButtonEditor ());
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
		return new ButtonEditor ();
	}

//enh:end

}
