
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

import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;

import de.grogra.graph.BooleanAttribute;
import de.grogra.graph.GraphState;
import de.grogra.graph.ObjectAttribute;
import de.grogra.graph.Path;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.State;
import de.grogra.graph.impl.Node.NType;
import de.grogra.imp3d.objects.Cylinder;
import de.grogra.imp3d.objects.Transformation;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.imp3d.shading.Shader;
import de.grogra.math.TVector3d;
import de.grogra.math.Transform3D;
import de.grogra.persistence.Transaction;
import de.grogra.pf.ui.event.ClickEvent;
import de.grogra.xl.impl.base.FieldListPattern;

public class Cell extends Node implements de.grogra.util.EventListener, Transformation
{
	public static final int TRANSFORMING_MASK = 1 << Node.USED_BITS;

	public static final int USED_BITS = Node.USED_BITS + 1;

	protected int state = 0;
	//enh:field getter setter attr=Attributes.STATE

	protected Shader shader = null;
	//enh:field setter

	// boolean transforming
	//enh:field type=bits(TRANSFORMING_MASK) attr=Attributes.TRANSFORMING getter setter

	final TVector3d position = new TVector3d ();

	// double x
	//enh:field getmethod=getX setmethod=setX

	// double y
	//enh:field getmethod=getY setmethod=setY 

	// double z
	//enh:field getmethod=getZ setmethod=setZ

	protected float radius = 0;
	//enh:field getter setter

	protected float length = 0;
	//enh:field attr=Attributes.LENGTH getter setter


	public Cell ()
	{
		this (0, 0, 0, 0);
	}


	public Cell (float x, float y, float z, int state)
	{
		this (x, y, z, 0.3f, -1, true, state);
	}


	public Cell (double x, double y, double z, float l, float r, boolean transform, int state)
	{
		super ();
		position.set (x, y, z);
		length = l;
		radius = r;
		this.state = state;
		if (transform)
		{
			bits |= TRANSFORMING_MASK;
		}
	}


	public Shader getShader ()
	{
		return (shader != null) ? shader
			: (state > 0) ? RGBAShader.YELLOW
			: (state < 0) ? RGBAShader.RED
			: RGBAShader.GRAY;
	}

	float getRadius0 ()
	{
		return (radius >= 0) ? radius : (state != 0) ? 0.4f : 0.2f;
	}

	@Override
	protected boolean getBoolean (BooleanAttribute a, GraphState gs)
	{
		return !((a == Attributes.BASE_OPEN) || (a == Attributes.TOP_OPEN)
				 || (a == Attributes.SCALE_V))
			&& super.getBoolean (a, gs);
	}


	@Override
	protected Object getObject (ObjectAttribute a, Object placeIn, GraphState gs)
	{
		if (a == Attributes.TRANSFORM)
		{
			return position;
		}
		else if (a == Attributes.SHAPE)
		{
			return Cylinder.$TYPE.getRepresentative ();
		}
		else
		{
			return super.getObject (a, placeIn, gs);
		}
	}


	public double getX ()
	{
		return position.x;
	}


	public void setX (double x)
	{
		position.x = x;
	}


	public double getY ()
	{
		return position.y;
	}


	public void setY (double y)
	{
		position.y = y;
	}


	public double getZ ()
	{
		return position.z;
	}


	public void setZ (double z)
	{
		position.z = z;
	}


	public TVector3d getPosition ()
	{
		return position;
	}


	public double distance (Cell c)
	{
		Tuple3d t = position, o = c.position;
		double s;
		return Math.sqrt ((s = t.x - o.x) * s + (s = t.y - o.y) * s
						  + (s = t.z - o.z) * s); 
	}


	public double distanceLinf (Cell c)
	{
		Tuple3d t = position, o = c.position;
		double s = t.x - o.x;
		double m = (s >= 0) ? s : -s;
		s = t.y - o.y;
		if (s > m)
		{
			m = s;
		}
		else if (-s > m)
		{
			m = -s;
		}
		s = t.z - o.z;
		return (s > m) ? s : (-s > m) ? -s : m;
	}


	public void preTransform (Object object, boolean asNode,
							  Matrix4d in, Matrix4d out, GraphState gs)
	{
		Transform3D t;
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				t = position;
			}
			else
			{
				t = (Transform3D) gs.checkObject (this, true, Attributes.TRANSFORM, position);
			}
		}
		else
		{
			t = (Transform3D) gs.getObject (object, asNode, null, Attributes.TRANSFORM);
		}
		if (t != null)
		{
			t.transform (in, out);
		}
		else
		{
			out.set (in);
		}
	}


	public void postTransform (Object object, boolean asNode,
							   Matrix4d in, Matrix4d out, Matrix4d pre,
							   GraphState gs)
	{
		boolean t;
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				t = (bits & TRANSFORMING_MASK) != 0;
			}
			else
			{
				t = gs.checkBoolean (this, true, Attributes.TRANSFORMING,
									 (bits & TRANSFORMING_MASK) != 0);
			}
		}
		else
		{
			t = gs.getBoolean (object, asNode, Attributes.TRANSFORMING);
		}
		if (t)
		{
			out.set (in);
		}
		else if (out != pre)
		{
			out.set (pre);
		}
	}


	public void eventOccured (java.util.EventObject event)
	{
		if (event instanceof ClickEvent)
		{
/*			ClickEvent c = (ClickEvent) event;
			Path path = (Path) c.getSource ();
			if (this == path.getObject (-1))
			{
				if (!(c.isShiftDown () || c.isControlDown ()
					  || c.isAltDown () || c.isMetaDown ()))
				{
					state$FIELD.setInt (this, null, 1 - state, getGraph ().getActiveTransaction ());
					c.consume ();
					return;
				}
			}*/
		}
	}


	@Override
	public String toString ()
	{
		return "Cell[" + position + ",state=" + state + ']';
	}


	@Override
	public int getSymbolColor ()
	{
		return 0x00ffffa0;
	}

	
	public static class Pattern extends FieldListPattern
	{
		public Pattern ()
		{
			super (Cell.$TYPE, state$FIELD);
		}
		
		private static void signature (@In @Out Cell c, int s)
		{
		}
	}

	
	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TRANSFORMATION);

		$TYPE.addAccessor (new AccessorBridge (Attributes.BASE_OPEN));
		$TYPE.addAccessor (new AccessorBridge (Attributes.TOP_OPEN));
		$TYPE.addAccessor (new AccessorBridge (Attributes.SCALE_V));
		$TYPE.addAccessor (new AccessorBridge (Attributes.SHAPE));
		$TYPE.addAccessor (new FieldAttributeAccessor (Attributes.SHADER, shader$FIELD)
			{
				@Override
				public Object getObject (Object o, Object placeIn, GraphState gs)
				{
					return ((Cell) o).getShader ();
				}
			});
		$TYPE.setAttribute (shader$FIELD, Attributes.SHADER);
		$TYPE.addAccessor (new FieldAttributeAccessor (Attributes.RADIUS, radius$FIELD)
			{
				@Override
				public float getFloat (Object o, GraphState gs)
				{
					return ((Cell) o).getRadius0 ();
				}
			});
		$TYPE.setAttribute (radius$FIELD, Attributes.RADIUS);

		$TYPE.addAccessor (new AccessorBridge (Attributes.TRANSFORM)
		{
			@Override
			public boolean isWritable (Object object, GraphState gs)
			{
				return true;
			}

			@Override
			protected Object clone (Object orig)
			{
				return new Vector3d ((Tuple3d) orig); 
			}

			@Override
			public Object setObject (Object object, Object value,
									 GraphState gs)
			{
				Transaction t = ((State) gs).getActiveTransaction ();
				Cell c = (Cell) object;
				if (value instanceof Tuple3d)
				{
					Tuple3d p = (Tuple3d) value;
					x$FIELD.setDouble (c, null, p.x, t);
					y$FIELD.setDouble (c, null, p.y, t);
					z$FIELD.setDouble (c, null, p.z, t);
				}
				else if (value instanceof Tuple3f)
				{
					Tuple3f p = (Tuple3f) value;
					x$FIELD.setDouble (c, null, p.x, t);
					y$FIELD.setDouble (c, null, p.y, t);
					z$FIELD.setDouble (c, null, p.z, t);
				}
				else if (value instanceof Matrix4f)
				{
					Matrix4f m = (Matrix4f) value;
					x$FIELD.setDouble (c, null, m.m03, t);
					y$FIELD.setDouble (c, null, m.m13, t);
					z$FIELD.setDouble (c, null, m.m23, t);
				}
				else
				{
					Matrix4d m;
					if (value instanceof Matrix4d)
					{
						m = (Matrix4d) value;
					}
					else
					{
						m = new Matrix4d ();
						m.setIdentity ();
						if (value instanceof Transform3D)
						{
							((Transform3D) value).transform (m, m);
						}
					}
					x$FIELD.setDouble (c, null, m.m03, t);
					y$FIELD.setDouble (c, null, m.m13, t);
					z$FIELD.setDouble (c, null, m.m23, t);
				}
				return c.position;
			}
		});

		$TYPE.setDependentAttribute (x$FIELD, Attributes.TRANSFORM);
		$TYPE.setDependentAttribute (y$FIELD, Attributes.TRANSFORM);
		$TYPE.setDependentAttribute (z$FIELD, Attributes.TRANSFORM);
		$TYPE.addDependency (Attributes.TRANSFORM, Attributes.TRANSFORMATION);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field state$FIELD;
	public static final NType.Field shader$FIELD;
	public static final NType.Field transforming$FIELD;
	public static final NType.Field x$FIELD;
	public static final NType.Field y$FIELD;
	public static final NType.Field z$FIELD;
	public static final NType.Field radius$FIELD;
	public static final NType.Field length$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Cell.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 0:
					((Cell) o).state = (int) value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Cell) o).getState ();
			}
			return super.getInt (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 5:
					((Cell) o).radius = (float) value;
					return;
				case 6:
					((Cell) o).length = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 5:
					return ((Cell) o).getRadius ();
				case 6:
					return ((Cell) o).getLength ();
			}
			return super.getFloat (o);
		}

		@Override
		public void setDouble (Object o, double value)
		{
			switch (id)
			{
				case 2:
					((Cell) o).setX ((double) value);
					return;
				case 3:
					((Cell) o).setY ((double) value);
					return;
				case 4:
					((Cell) o).setZ ((double) value);
					return;
			}
			super.setDouble (o, value);
		}

		@Override
		public double getDouble (Object o)
		{
			switch (id)
			{
				case 2:
					return ((Cell) o).getX ();
				case 3:
					return ((Cell) o).getY ();
				case 4:
					return ((Cell) o).getZ ();
			}
			return super.getDouble (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 1:
					((Cell) o).shader = (Shader) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 1:
					return ((Cell) o).shader;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Cell ());
		$TYPE.addManagedField (state$FIELD = new _Field ("state", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.INT, null, 0));
		$TYPE.addManagedField (shader$FIELD = new _Field ("shader", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Shader.class), null, 1));
		$TYPE.addManagedField (transforming$FIELD = new NType.BitField ($TYPE, "transforming", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, TRANSFORMING_MASK));
		$TYPE.addManagedField (x$FIELD = new _Field ("x", 0 | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 2));
		$TYPE.addManagedField (y$FIELD = new _Field ("y", 0 | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 3));
		$TYPE.addManagedField (z$FIELD = new _Field ("z", 0 | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 4));
		$TYPE.addManagedField (radius$FIELD = new _Field ("radius", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 5));
		$TYPE.addManagedField (length$FIELD = new _Field ("length", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 6));
		$TYPE.declareFieldAttribute (state$FIELD, Attributes.STATE);
		$TYPE.declareFieldAttribute (transforming$FIELD, Attributes.TRANSFORMING);
		$TYPE.declareFieldAttribute (length$FIELD, Attributes.LENGTH);
		initType ();
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
		return new Cell ();
	}

	public int getState ()
	{
		return state;
	}

	public void setState (int value)
	{
		this.state = (int) value;
	}

	public float getRadius ()
	{
		return radius;
	}

	public void setRadius (float value)
	{
		this.radius = (float) value;
	}

	public float getLength ()
	{
		return length;
	}

	public void setLength (float value)
	{
		this.length = (float) value;
	}

	public void setShader (Shader value)
	{
		shader$FIELD.setObject (this, value);
	}

	public boolean isTransforming ()
	{
		return (bits & TRANSFORMING_MASK) != 0;
	}

	public void setTransforming (boolean v)
	{
		if (v) bits |= TRANSFORMING_MASK; else bits &= ~TRANSFORMING_MASK;
	}

//enh:end

}
