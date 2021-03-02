
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

package de.grogra.pf.registry;

import java.beans.*;
import de.grogra.persistence.*;
import de.grogra.graph.impl.*;

public final class SONodeReference extends NodeReference
	implements PropertyChangeListener
{
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (new SONodeReference ());
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
		return new SONodeReference ();
	}

//enh:end


	private SONodeReference ()
	{
		this (null, null);
	}


	public SONodeReference (String key, SharedObjectNode ref)
	{
		super (key, ref);
	}


	public SONodeReference (SharedObjectNode ref)
	{
		this (null, ref);
	}


	@Override
	public final Object getObject ()
	{
		return getSharedObject ();
	}


	public final Shareable getSharedObject ()
	{
		return ((SharedObjectNode) getBaseObjectImpl ()).getSharedObject ();
	}


	@Override
	protected void activateImpl ()
	{
		super.activateImpl ();
		GraphManager gm = getRegistry ().getProjectGraph ();
		if (gm != null)
		{
			gm.addSharedObjectListener (this);
		}
	}


	@Override
	protected void deactivateImpl ()
	{
		GraphManager gm = getRegistry ().getProjectGraph ();
		if (gm != null)
		{
			gm.removeSharedObjectListener (this);
		}
		super.deactivateImpl ();
	}


	public void propertyChange (PropertyChangeEvent e)
	{
		if (isObjectFetched () && (getSharedObject () == e.getSource ()))
		{
			getRegistry ().fireTreeNodesChanged (this);
		}
	}


	public static SONodeReference get
		(RegistryContext ctx, String dir, Shareable s)
	{
		SharedObjectProvider p = s.getProvider ();
		return (p instanceof SharedObjectNode)
			? (SONodeReference) NodeReference.get (ctx, dir, (SharedObjectNode) p)
			: null;
	}

}
