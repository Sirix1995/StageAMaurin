
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

import java.lang.ref.*;

class WeakListenerDelegate extends WeakReference<Object>
	implements AttributeChangeListener, EdgeChangeListener, ChangeBoundaryListener
{

	public WeakListenerDelegate (Object source)
	{
		super (source);
	}


	public void attributeChanged (AttributeChangeEvent event)
	{
		AttributeChangeListener l = (AttributeChangeListener) get ();
		if (l != null)
		{
			l.attributeChanged (event);
		}
		else
		{
			event.getGraphState ().getGraph ()
				.removeAttributeChangeListener (this);
		}
	}


	public void edgeChanged (Object src, Object target, Object edge,
							 GraphState gs)
	{
		EdgeChangeListener l = (EdgeChangeListener) get ();
		if (l != null)
		{
			l.edgeChanged (src, target, edge, gs);
		}
		else
		{
			gs.getGraph ().removeEdgeChangeListener (this);
		}
	}


	public void beginChange (GraphState gs)
	{
		ChangeBoundaryListener l = (ChangeBoundaryListener) get ();
		if (l != null)
		{
			l.beginChange (gs);
		}
		else
		{
			gs.getGraph ().removeChangeBoundaryListener (this);
		}
	}


	public void endChange (GraphState gs)
	{
		ChangeBoundaryListener l = (ChangeBoundaryListener) get ();
		if (l != null)
		{
			l.endChange (gs);
		}
		else
		{
			gs.getGraph ().removeChangeBoundaryListener (this);
		}
	}


	public int getPriority ()
	{
		ChangeBoundaryListener l = (ChangeBoundaryListener) get ();
		return (l != null) ? l.getPriority () : ATTRIBUTE_PRIORITY;
	}

}
