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

package de.grogra.imp3d;

import javax.vecmath.Matrix4d;

import de.grogra.graph.AttributeChangeEvent;
import de.grogra.graph.AttributeChangeListener;
import de.grogra.graph.GraphState;
import de.grogra.graph.ObjectAttribute;
import de.grogra.graph.ObjectMap;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.GlobalTransformation;
import de.grogra.imp3d.shading.Shader;
import de.grogra.vecmath.geom.EmptyVolume;
import de.grogra.vecmath.geom.Volume;

public final class VolumeAttribute extends ObjectAttribute
{
	public static final VolumeAttribute ATTRIBUTE = new VolumeAttribute ();

	private static final Volume EMPTY = new EmptyVolume ();

	private static class Listener implements AttributeChangeListener
	{
		Volume volume;

		public void attributeChanged (AttributeChangeEvent event)
		{
			if (volume == null)
			{
				return;
			}
			volume = null;
			event.getGraphState ().getGraph ().removeAttributeChangeListener (
				event.getObject (), event.isNode (), this);
			event.getGraphState ().fireAttributeChanged (event.getObject (),
				event.isNode (), ATTRIBUTE, null, null);
		}
	}

	private static class Builder extends VolumeBuilder
	{
		ObjectMap<Listener> listeners;

		Builder (GraphState gs)
		{
			super (new PolygonizationCache (gs, Polygonization.COMPUTE_NORMALS
				| Polygonization.COMPUTE_UV, 10, true), 1e-5f);
			this.listeners = gs.getGraph ().createObjectMap ();
		}

		private Volume addedVolume;
		private GraphState state;
		private Matrix4d currentXform = new Matrix4d ();

		@Override
		protected void addVolume (Volume v, Matrix4d t, Shader s)
		{
			addedVolume = v;
		}

		@Override
		protected Matrix4d getCurrentTransformation ()
		{
			return currentXform;
		}

		public Shader getCurrentShader ()
		{
			return null;
		}

		public GraphState getRenderGraphState ()
		{
			return state;
		}

		synchronized Volume getVolume (Object object, boolean asNode,
				GraphState gs)
		{
			Listener w;
			if ((w = listeners.getObject (object, asNode)) == null)
			{
				w = new Listener ();
				listeners.putObject (object, asNode, w);
			}
			if (w.volume == null)
			{
				addedVolume = EMPTY;
				Object shape = gs.getObjectDefault (object, asNode,
					Attributes.SHAPE, null);
				if (shape instanceof Renderable)
				{
					state = gs;
					GlobalTransformation.get (object, asNode, gs, false).get (currentXform);
					((Renderable) shape).draw (object, asNode, this);
					state = null;
				}
				w.volume = addedVolume;
				addedVolume = null;
				gs.getGraph ().addAttributeChangeListener (object, asNode, w);
			}
			return w.volume;
		}
	}

	private VolumeAttribute ()
	{
		super (Volume.class, false, null);
		initializeName ("de.grogra.imp3d.volume");
	}

	@Override
	public boolean isDerived ()
	{
		return true;
	}

	@Override
	protected Object getDerived (Object object, boolean asNode, Object placeIn,
			GraphState gs)
	{
		Builder builder;
		synchronized (this)
		{
			if ((builder = (Builder) getAttributeState (gs)) == null)
			{
				builder = new Builder (gs);
				setAttributeState (gs, builder);
			}
		}
		return builder.getVolume (object, asNode, gs);
	}

	public static Volume getVolume (Object object, boolean asNode, GraphState gs)
	{
		return (Volume) ATTRIBUTE.getDerived (object, asNode, null, gs);
	}

}
