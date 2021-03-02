
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

package de.grogra.imp3d.objects;

import javax.vecmath.*;
import de.grogra.graph.*;
import de.grogra.imp3d.*;
import de.grogra.math.*;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

public class NURBSSurface extends ShadedNull
	implements Renderable, Polygonizable, Pickable
{
	/**
	 * Constant for {@link #NURBSSurface(byte)} indicating a
	 * skinned surface (defined by a set of profile curves in space).
	 */
	public static final byte SKIN = 0;

	/**
	 * Constant for {@link #NURBSSurface(byte)} indicating a
	 * swept surface (defined by a set of vertices in space).
	 */
	public static final byte SWEEP = 1;


	protected BSplineSurface surface;
	//enh:field attr=Attributes.SURFACE getter setter

	protected float flatness = 1;
	//enh:field attr=Attributes.FLATNESS getter setter
	
	protected int visibleSides = Attributes.VISIBLE_SIDES_BOTH;
	//enh:field attr=Attributes.VISIBLE_SIDES getter setter


	public NURBSSurface (BSplineSurface surface)
	{
		this.surface = surface;
		setLayer (1);
	}


	public NURBSSurface ()
	{
		this ((BSplineSurface) null);
	}


	public NURBSSurface (BSplineCurveList curves)
	{
		this (new SkinnedSurface (curves));
	}


	public NURBSSurface (BSplineCurve profile)
	{
		this (profile, null, false);
	}


	public NURBSSurface (BSplineCurve profile, boolean useRail)
	{
		this (profile, null, useRail);
	}


	public NURBSSurface (BSplineCurve profile, String name)
	{
		this (profile, name, false);
	}


	public NURBSSurface (BSplineCurve profile, String name, boolean useRail)
	{
		this (new ProfileSweep (profile, new VertexSequence (name)));
		setUseRail (useRail);
	}


	public NURBSSurface (float radius)
	{
		this (new Circle (radius), null);
	}


	public NURBSSurface (float radius, String name)
	{
		this (new Circle (radius), name);
	}


	public NURBSSurface (byte type)
	{
		switch (type)
		{
			case SKIN:
				setSurface (new SkinnedSurface (new CurveSequence ()));
				break;
			case SWEEP:
				setSurface (new SkinnedSurface (new SweepSequence (null, false)));
				break;
		}
	}


	public NURBSSurface (byte type, String name, boolean useRail)
	{
		switch (type)
		{
			case SKIN:
				setSurface (new SkinnedSurface (new CurveSequence (name)));
				break;
			case SWEEP:
				setSurface (new SkinnedSurface (new SweepSequence (name, useRail)));
				break;
		}
	}


	public void setUseRail (boolean useRail)
	{
		BSplineSurface s = getSurface ();
		if (s instanceof SkinnedSurface)
		{
			BSplineCurveList l = ((SkinnedSurface) s).getProfiles ();
			if (l instanceof Sweep)
			{
				((Sweep) l).setUseRail (useRail);
			}
		}
	}


	public void setHermite (boolean hermite)
	{
		BSplineSurface s = getSurface ();
		if (s instanceof SkinnedSurface)
		{
			BSplineCurveList l = ((SkinnedSurface) s).getProfiles ();
			if (l instanceof SweepSequence)
			{
				((SweepSequence) l).setHermite (hermite);
			}
		}
	}


	public void setTangentLength (float length)
	{
		BSplineSurface s = getSurface ();
		if (s instanceof SkinnedSurface)
		{
			BSplineCurveList l = ((SkinnedSurface) s).getProfiles ();
			if (l instanceof SweepSequence)
			{
				((SweepSequence) l).setTangentLength (length);
			}
		}
	}


	public ContextDependent getPolygonizableSource (GraphState gs)
	{
		return (gs.getObjectContext ().getObject () == this)
			? (gs.getInstancingPathIndex () <= 0) ? surface : (BSplineSurface) gs.checkObject (this, true, Attributes.SURFACE, this.surface)
			: (BSplineSurface) gs.getObject (gs.getObjectContext ().getObject (),
							   gs.getObjectContext ().isNode (), Attributes.SURFACE);
	}


	public Polygonization getPolygonization ()
	{
		final class Poly implements Polygonization
		{
			final float flatness = NURBSSurface.this.flatness;
			final int visibleSides = NURBSSurface.this.visibleSides;

			public void polygonize (ContextDependent source, GraphState gs, PolygonArray out, int flags, float flatness)
			{
				polygonizeImpl (source, gs, out, flags, flatness);
			}

			public boolean equals (Object o)
			{
				if (!(o instanceof Poly))
				{
					return false;
				}
				Poly p = (Poly) o;
				return (p.flatness == flatness) && (p.visibleSides == visibleSides);
			}

			public int hashCode ()
			{
				return Float.floatToIntBits (flatness) ^ visibleSides;
			}
		}

		return new Poly ();
	}

	void polygonizeImpl (ContextDependent source, GraphState gs, PolygonArray out,
						 int flags, float flatness)
	{
		float f;
		int vs;
		if (gs.getObjectContext ().getObject () == this)
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				f = this.flatness;
				vs = this.visibleSides;
			}
			else
			{
				f = gs.checkFloat (this, true, Attributes.FLATNESS, this.flatness);
				vs = gs.checkInt (this, true, Attributes.VISIBLE_SIDES, this.visibleSides);
			}
		}
		else
		{
			f = gs.getFloatDefault (gs.getObjectContext ().getObject (), gs.getObjectContext ().isNode (), Attributes.FLATNESS, this.flatness);
			vs = gs.getIntDefault (gs.getObjectContext ().getObject (), gs.getObjectContext ().isNode (), Attributes.VISIBLE_SIDES, this.visibleSides);
		}
		BSplineSurface s = (BSplineSurface) source;
		if (BSpline.isValid (s, gs))
		{
			out.init (3);
			NURBSPolygonizer pz = new NURBSPolygonizer
				(out, s.isRational (gs), Pool.get (gs), flags);
			pz.setFlatness (0.0003f * flatness * f);
			BSpline.decompose (pz, s, true, gs, null);
		}
		else
		{
			out.init (3);
		}
		out.visibleSides = vs;
	}

	public void draw (Object object, boolean asNode, RenderState rs)
	{
		rs.drawPolygons (this, object, asNode, null, -1, null);
	}

	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
					  Matrix4d transformation, de.grogra.imp.PickList list)
	{
		Sphere.pick (1, origin, direction, list);
	}

//	enh:insert $TYPE.addIdentityAccessor (Attributes.SHAPE);
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field surface$FIELD;
	public static final NType.Field flatness$FIELD;
	public static final NType.Field visibleSides$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (NURBSSurface.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 2:
					((NURBSSurface) o).visibleSides = (int) value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 2:
					return ((NURBSSurface) o).getVisibleSides ();
			}
			return super.getInt (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 1:
					((NURBSSurface) o).flatness = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 1:
					return ((NURBSSurface) o).getFlatness ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((NURBSSurface) o).surface = (BSplineSurface) value;
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
					return ((NURBSSurface) o).getSurface ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new NURBSSurface ());
		$TYPE.addManagedField (surface$FIELD = new _Field ("surface", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (BSplineSurface.class), null, 0));
		$TYPE.addManagedField (flatness$FIELD = new _Field ("flatness", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (visibleSides$FIELD = new _Field ("visibleSides", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.INT, null, 2));
		$TYPE.declareFieldAttribute (surface$FIELD, Attributes.SURFACE);
		$TYPE.declareFieldAttribute (flatness$FIELD, Attributes.FLATNESS);
		$TYPE.declareFieldAttribute (visibleSides$FIELD, Attributes.VISIBLE_SIDES);
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
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
		return new NURBSSurface ();
	}

	public int getVisibleSides ()
	{
		return visibleSides;
	}

	public void setVisibleSides (int value)
	{
		this.visibleSides = (int) value;
	}

	public float getFlatness ()
	{
		return flatness;
	}

	public void setFlatness (float value)
	{
		this.flatness = (float) value;
	}

	public BSplineSurface getSurface ()
	{
		return surface;
	}

	public void setSurface (BSplineSurface value)
	{
		surface$FIELD.setObject (this, value);
	}

//enh:end

}
