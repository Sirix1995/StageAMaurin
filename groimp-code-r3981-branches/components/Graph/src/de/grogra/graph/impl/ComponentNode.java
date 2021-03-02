/*
 * Copyright (C) 2012 GroIMP Developer Team
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

package de.grogra.graph.impl;

public class ComponentNode extends Node
{

	/**
	 * Delault serial ID.
	 */
	private static final long serialVersionUID = 1235123481321L;

	private String key = "";
	
	public ComponentNode() {
		setName("Node");
	}
	
	public ComponentNode(String key, String name) {
		this.key = key;
		setName(name);
	}
	
	//enh:insert
	//enh:begin
	// NOTE: The following lines up to enh:end were generated automatically

		public static final NType $TYPE;


		static
		{
			$TYPE = new NType (new ComponentNode ());
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
			return new ComponentNode ();
		}

	//enh:end

}
