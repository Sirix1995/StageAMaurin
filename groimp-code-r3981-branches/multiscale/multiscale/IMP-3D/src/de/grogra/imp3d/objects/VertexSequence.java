
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

import java.util.ArrayList;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;

import de.grogra.graph.Cache;
import de.grogra.graph.GraphState;
import de.grogra.math.BSpline;
import de.grogra.math.BSplineCurve;
import de.grogra.math.Pool;
import de.grogra.util.Int2ObjectMap;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.Matrix34d;

public class VertexSequence extends Sequence implements BSplineCurve
{
	//enh:sco

	boolean hermite = true;
	//enh:field getter setter

	float tangentLength = 1.2f;
	//enh:field getter setter

	boolean periodic = false;
	//enh:field getter setter
	
	public interface Vertex
	{
		Matrix34d getVertexTransformation (Object node, GraphState gs);
	}

	public VertexSequence ()
	{	
	}


	public VertexSequence (String name)
	{
		this.name = name;
	}


	public boolean dependsOnContext ()
	{
		return true;
	}


	@Override
	public void writeStamp (Cache.Entry cache, GraphState gs)
	{
		super.writeStamp (cache, gs);
		cache.write ((float[]) getCache (gs));
	}


	public int getSize (GraphState gs)
	{
		return getSize0 (gs) + (periodic ? getDegree (gs) : 0);
	}


	private int getSize0 (GraphState gs)
	{
		return ((float[]) getCache (gs)).length / 7;
	}


	public int getDegree (GraphState gs)
	{
		return Math.min (3, getSize0 (gs) - 1);
	}

	
	boolean collectCurves ()
	{
		return false;
	}


	@Override
	protected void visitNode
		(Object node, Object shape, ArrayList list, GraphState gs)
	{
		if (shape instanceof Vertex)
		{
			Matrix34d m = ((Vertex) shape).getVertexTransformation (node, gs);
			if (m != null)
			{
				list.add (m);
			}
		}
		else if (node instanceof Vertex)
		{
			Matrix34d m = ((Vertex) node).getVertexTransformation (node, gs);
			if (m != null)
			{
				list.add (m);
			}
		}
		else if (collectCurves () && (shape instanceof NURBSCurve))
		{
			BSplineCurve c
				= (BSplineCurve) gs.getObject (node, true, null, Attributes.CURVE);
			GraphState.ObjectContext s = gs.createObjectState (node, true),
				os = gs.getObjectContext ();
			gs.setObjectContext (s);
			if (BSpline.isValid (c, gs))
			{
				list.add (s);
				list.add (c);
				list.add (this);
			}
			gs.setObjectContext (os);
		}
	}


	@Override
	protected Object calculateCache
		(Matrix4d inv, ArrayList list, GraphState gs, Object info)
	{
		int n = list.size ();
		int count = 0, j = 0;
		Pool pool = Pool.get (gs);
		Point3d p0 = pool.p3d0, p1 = pool.p3d1,
			p2 = pool.p3d2, p3 = pool.p3d3, tp;
		Point4f o0 = pool.p4f0, o1 = pool.p4f1,
			o2 = pool.p4f2, to;
		Vector3d hp = pool.v3d3,
			dp = pool.w3d0, dp02 = pool.w3d1, q = pool.w3d2;
		Vector4f ho = pool.v4f0, do0 = pool.v4f1,
			do02 = pool.v4f2, oq = pool.v4f3;
		float epsP = 0, epsO = 0;
		for (int i = n - 1; i >= 0; i--)
		{
			if (list.get (i) != this)
			{
				Matrix34d t = (Matrix34d) list.get (i);
				p0.set (t.m03, t.m13, t.m23);
				Math2.transformPoint (inv, p0);
				p2.set (t.m00, t.m10, t.m20);
				Math2.transformVector (inv, p2);
				if (count == 0)
				{
					p1.set (p0);
					p3.set (p2);
				}
				else
				{
					epsP = Math.max (epsP, (float) p0.distanceSquared (p1));
					epsO = Math.max (epsO, (float) p2.distanceSquared (p3));
				}
				count++;
			}
			else
			{
				i -= 2;
				j++;
			}
		}
		boolean h = hermite && (count > 2);
		if ((collectCurves () && (j == 0)) || (count <= 1))
		{
			count = 0;
		}
		else if (h)
		{
			count = 3 * count - 2;
			epsP *= 1e-6f;
			epsO *= 1e-6f;
		}
		float[] vertices = new float[count * 7];
		if (count > 0)
		{
			j = -1;
			count = h ? -3 : -1;
			Int2ObjectMap profiles = (Int2ObjectMap) info;
			float f;
			for (int i = n - 1; i >= 0; i--)
			{
				if (list.get (i) != this)
				{
					Matrix34d t = (Matrix34d) list.get (i);
					p0.set (t.m03, t.m13, t.m23);
					Math2.transformPoint (inv, p0);
					hp.set (t.m00, t.m10, t.m20);
					Math2.transformVector (inv, hp);
					o0.set ((float) hp.x, (float) hp.y, (float) hp.z,
							(float) hp.length ());
					if (h)
					{
						tp = p0; p0 = p1; p1 = p2; p2 = tp;
						to = o0; o0 = o1; o1 = o2; o2 = to;
						count += 3;
						if (count > 3)
						{
							dp02.sub (p2, p0);
							double s = dp02.lengthSquared ();
							s = (s > epsP) ? (1d / 9) / s : 0;

							dp.sub (p1, p0);
							q.scaleAdd (-tangentLength * Math.sqrt
										(s * dp.lengthSquared ()), dp02, p1);

							do02.sub (o2, o0);
							double os = do02.lengthSquared ();
							os = (os > epsO) ? (1d / 9) / os : 0;

							do0.sub (o1, o0);
							f = do0.w;
							do0.w = 0;
							oq.scaleAdd (-tangentLength * (float) Math.sqrt
										 (os * do0.lengthSquared ()), do02, o1);
							do0.w = f;
							oq.w = -tangentLength * (float) Math.sqrt (os)
								* Math.abs (f) * do02.w + o1.w;

							if (count == 6)
							{
								vertices[++j] = (float) p0.x;
								vertices[++j] = (float) p0.y;
								vertices[++j] = (float) p0.z;

								vertices[++j] = o0.x;
								vertices[++j] = o0.y;
								vertices[++j] = o0.z;
								vertices[++j] = o0.w;

								if ((f = (float) dp.lengthSquared ()) > epsP)
								{									
									hp.sub (q, p1);
									hp.scaleAdd (-2 * hp.dot (dp) / f, dp, hp);
									vertices[++j] = (float) hp.x + (float) p0.x;
									vertices[++j] = (float) hp.y + (float) p0.y;
									vertices[++j] = (float) hp.z + (float) p0.z;
								}
								else
								{
									vertices[++j] = (float) p0.x;
									vertices[++j] = (float) p0.y;
									vertices[++j] = (float) p0.z;
								}

								if ((f = do0.lengthSquared ()) > epsO)
								{
									ho.sub (oq, o1);
									ho.scaleAdd (-2 * ho.dot (do0) / f, do0, ho);
									vertices[++j] = ho.x + o0.x;
									vertices[++j] = ho.y + o0.y;
									vertices[++j] = ho.z + o0.z;
									vertices[++j] = ho.w + o0.w;
								}
								else
								{
									vertices[++j] = o0.x;
									vertices[++j] = o0.y;
									vertices[++j] = o0.z;
									vertices[++j] = o0.w;
								}
							}

							vertices[++j] = (float) q.x;
							vertices[++j] = (float) q.y;
							vertices[++j] = (float) q.z;

							vertices[++j] = oq.x;
							vertices[++j] = oq.y;
							vertices[++j] = oq.z;
							vertices[++j] = oq.w;

							vertices[++j] = (float) p1.x;
							vertices[++j] = (float) p1.y;
							vertices[++j] = (float) p1.z;

							vertices[++j] = o1.x;
							vertices[++j] = o1.y;
							vertices[++j] = o1.z;
							vertices[++j] = o1.w;

							dp.sub (p2, p1);
							q.scaleAdd (tangentLength * Math.sqrt
										(s * dp.lengthSquared ()), dp02, p1);
							vertices[++j] = (float) q.x;
							vertices[++j] = (float) q.y;
							vertices[++j] = (float) q.z;

							do0.sub (o2, o1);
							f = do0.w;
							do0.w = 0;
							oq.scaleAdd (tangentLength * (float) Math.sqrt
										 (os * do0.lengthSquared ()), do02, o1);
							do0.w = f;
							oq.w = tangentLength * (float) Math.sqrt (os)
								* Math.abs (f) * do02.w + o1.w;
							vertices[++j] = oq.x;
							vertices[++j] = oq.y;
							vertices[++j] = oq.z;
							vertices[++j] = oq.w;
						}
					}
					else
					{
						vertices[++j] = (float) p0.x;
						vertices[++j] = (float) p0.y;
						vertices[++j] = (float) p0.z;
						vertices[++j] = o0.x;
						vertices[++j] = o0.y;
						vertices[++j] = o0.z;
						vertices[++j] = o0.w;
						count++;
					}
				}
				else
				{
					i -= 2;
					profiles.put (count, Integer.valueOf (i));
				}
			}
			if (h)
			{
				if ((f = (float) dp.lengthSquared ()) > epsP)
				{									
					hp.sub (q, p1);
					hp.scaleAdd (-2 * hp.dot (dp) / f, dp, hp);
					vertices[++j] = (float) hp.x + (float) p2.x;
					vertices[++j] = (float) hp.y + (float) p2.y;
					vertices[++j] = (float) hp.z + (float) p2.z;
				}
				else
				{
					vertices[++j] = (float) p2.x;
					vertices[++j] = (float) p2.y;
					vertices[++j] = (float) p2.z;
				}

				if ((f = do0.lengthSquared ()) > epsO)
				{
					ho.sub (oq, o1);
					ho.scaleAdd (-2 * ho.dot (do0) / f, do0, ho);
					vertices[++j] = ho.x + o2.x;
					vertices[++j] = ho.y + o2.y;
					vertices[++j] = ho.z + o2.z;
					vertices[++j] = ho.w + o2.w;
				}
				else
				{
					vertices[++j] = o2.x;
					vertices[++j] = o2.y;
					vertices[++j] = o2.z;
					vertices[++j] = o2.w;
				}

				vertices[++j] = (float) p2.x;
				vertices[++j] = (float) p2.y;
				vertices[++j] = (float) p2.z;

				vertices[++j] = o2.x;
				vertices[++j] = o2.y;
				vertices[++j] = o2.z;
				vertices[++j] = o2.w;
			}
			if (collectCurves ())
			{
				profiles.put (Integer.MAX_VALUE, list.toArray ());
			}
		}
		return vertices;
	}


	public int getDimension (GraphState gs)
	{
		return 7;
	}


	public boolean isRational (GraphState gs)
	{
		return false;
	}


	public float getKnot (int dim, int index, GraphState gs)
	{
		int n = getSize0 (gs);
		return BSpline.getDefaultKnot
			(n, getDegree (gs), periodic, hermite && (n > 2), index);
	}


	public int getVertex (float[] out, int index, GraphState gs)
	{
		float[] cache = (float[]) getCache (gs);
		index *= 7;
		if (index >= cache.length)
		{
			index -= cache.length;
		}
		return BSpline.set (out, cache[index], cache[index + 1],
							cache[index + 2], cache[index + 3],
							cache[index + 4], cache[index + 5],
							cache[index + 6]);
	}


	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field hermite$FIELD;
	public static final Type.Field tangentLength$FIELD;
	public static final Type.Field periodic$FIELD;

	public static class Type extends Sequence.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (VertexSequence representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, Sequence.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = Sequence.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = Sequence.Type.FIELD_COUNT + 3;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((VertexSequence) o).hermite = (boolean) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((VertexSequence) o).periodic = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((VertexSequence) o).isHermite ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((VertexSequence) o).isPeriodic ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((VertexSequence) o).tangentLength = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((VertexSequence) o).getTangentLength ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new VertexSequence ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (VertexSequence.class);
		hermite$FIELD = Type._addManagedField ($TYPE, "hermite", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 0);
		tangentLength$FIELD = Type._addManagedField ($TYPE, "tangentLength", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		periodic$FIELD = Type._addManagedField ($TYPE, "periodic", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 2);
		$TYPE.validate ();
	}

	public boolean isHermite ()
	{
		return hermite;
	}

	public void setHermite (boolean value)
	{
		this.hermite = (boolean) value;
	}

	public boolean isPeriodic ()
	{
		return periodic;
	}

	public void setPeriodic (boolean value)
	{
		this.periodic = (boolean) value;
	}

	public float getTangentLength ()
	{
		return tangentLength;
	}

	public void setTangentLength (float value)
	{
		this.tangentLength = (float) value;
	}

//enh:end

}
