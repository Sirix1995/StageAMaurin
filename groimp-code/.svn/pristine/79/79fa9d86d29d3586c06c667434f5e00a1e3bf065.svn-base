
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

package de.grogra.rgg;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.imp.PickList;
import de.grogra.imp3d.PickRayVisitor;
import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.View3D;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.GlobalTransformation;
import de.grogra.imp3d.objects.Null;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.util.ButtonWidget;
import de.grogra.ray2.tracing.RadiationModel;
import de.grogra.vecmath.Matrix34d;
import de.grogra.xl.util.ObjectList;

public class LightModelVisualizer extends Null implements Renderable, Pickable
{
	private LightModel model;
	//enh:field type=LightModel.$TYPE getter setter

	private static void initType()
	{
		$TYPE.addIdentityAccessor(Attributes.SHAPE);
	}

//enh:insert initType();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field model$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (LightModelVisualizer.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((LightModelVisualizer) o).model = (LightModel) LightModel.$TYPE.setObject (((LightModelVisualizer) o).model, value);
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((LightModelVisualizer) o).getModel ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new LightModelVisualizer ());
		$TYPE.addManagedField (model$FIELD = new _Field ("model", _Field.PRIVATE  | _Field.SCO, LightModel.$TYPE, null, 0));
		initType();
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
		return new LightModelVisualizer ();
	}

	public LightModel getModel ()
	{
		return model;
	}

	public void setModel (LightModel value)
	{
		model$FIELD.setObject (this, value);
	}

//enh:end

	public LightModelVisualizer ()
	{
		this (1000000, 10);
	}


	public LightModelVisualizer (int rayCount, int depth)
	{
		this (rayCount, depth, 0.001);
	}


	public LightModelVisualizer (int rayCount, int depth, double minPower)
	{
		model = new LightModel(rayCount, depth, minPower);
	}


	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
					  Matrix4d t, PickList list)
	{
		PickRayVisitor.pickPoint (origin, direction, t, list, 8);
	}


	public void draw (Object object, boolean asNode, RenderState rs)
	{
		rs.drawPoint (new Vector3f(), 10, new Color3f(1, 1, 0), RenderState.CURRENT_HIGHLIGHT, null);
		if ((model != null) && model.hasResults() && (model.getTracedRays() != null))
		{
			Matrix34d r = GlobalTransformation.get (object, asNode, rs.getRenderGraphState(), false);
			Matrix4d t = new Matrix4d();
			r.get(t);
			t.invert();
			Color3f color = new Color3f();
			for (ObjectList<RadiationModel.RayPoint> ray : model.getTracedRays())
			{
				for (int i = 0; i < ray.size()-1; ++i)
				{
					color.set(ray.get(i).color);
					color.scale(1 / Math.max(color.x, Math.max(color.y, color.z)));
					rs.drawLine(ray.get(i).point, ray.get(i+1).point, color, 0, t);
				}
			}
		}
	}

	public static void compute(Item item, Object info, Context ctx)
	{
		if (info instanceof ButtonWidget)
		{
			info = ((ButtonWidget) info).getProperty().getValue();
			if (info instanceof LightModel)
			{
				((LightModel) info).compute(true, true);
				View3D v = View3D.getDefaultView(ctx);
				if (v != null)
				{
					v.repaint();
				}
			}
		}
	}

	public static void computeAndIncSeed(Item item, Object info, Context ctx)
	{
		if (info instanceof ButtonWidget)
		{
			info = ((ButtonWidget) info).getProperty().getValue();
			if (info instanceof LightModel)
			{
				LightModel lm = (LightModel) info;
				lm.compute(true, true);
				lm.setSeed(lm.getSeed() + 1);
				View3D v = View3D.getDefaultView(ctx);
				if (v != null)
				{
					v.repaint();
				}
			}
		}
	}

}
