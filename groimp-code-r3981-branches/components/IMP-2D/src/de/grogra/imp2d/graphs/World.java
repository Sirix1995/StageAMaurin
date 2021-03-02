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

package de.grogra.imp2d.graphs;

public class World extends GroIMPComponent {

	/**
	 * Default serial ID.
	 */
	private static final long serialVersionUID = 1235813234621L;


	public World() {
		initComponent();
	}


	/**
	 * Returns a list of key words which identifies and/or characterises this component.
	 * 
	 * @return list of key words
	 */
	@Override
	protected String getKeyWords() {
		return "Wold component";
	}

	/**
	 * Returns a short Description which identifies and/or characterises this component.
	 * 
	 * @return list of key words
	 */
	@Override
	protected String getShortDescription() {
		return "The Wold component is the uppermost component in the component graph.";
	}

	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new World ());
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
		return new World ();
	}

//enh:end

}
