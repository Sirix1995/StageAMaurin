
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

package de.grogra.persistence;

import de.grogra.xl.util.ObjectList;

public abstract class ShareableBase implements Shareable, Manageable
{
	private transient SharedObjectProvider sop;
	private transient int stamp = 0;
	private transient ObjectList refs = null;


	public void initProvider (SharedObjectProvider provider)
	{
		if (sop != null)
		{
			throw new IllegalStateException ();
		}
		sop = provider;
	}


	public SharedObjectProvider getProvider ()
	{
		return sop;
	}


	public void fieldModified (PersistenceField field, int[] indices, Transaction t)
	{
		stamp++;
		if ((t != null) && (sop != null))
		{
			t.fireSharedObjectModified (this);
		}
	}

	
	public int getStamp ()
	{
		return stamp;
	}

	
	public Manageable manageableReadResolve ()
	{
		return this;
	}
	
	public Object manageableWriteReplace ()
	{
		return this;
	}

	public synchronized void addReference (SharedObjectReference ref)
	{
		if (refs == null)
		{
			refs = new ObjectList (4, false);
		}
		refs.add (ref);
	}

	
	public synchronized void removeReference (SharedObjectReference ref)
	{
		if (refs != null)
		{
			refs.remove (ref);
		}
	}

	
	public synchronized void appendReferencesTo (java.util.List out)
	{
		if (refs != null)
		{
			out.addAll (refs);
		}
	}

}
