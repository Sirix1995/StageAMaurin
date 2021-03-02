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
package de.grogra.imp2d.layout;

import de.grogra.imp2d.layout.Node;
import java.util.Hashtable;
import javax.vecmath.Point2d;
import de.grogra.pf.registry.Item;
import de.grogra.util.Utils;

/**
 * A <code>TouchLayout</code> computes a graph layout based on a
 * touch model.
 *
 * @date 26.03.2007
 */
public class TouchLayout extends Layout
{	
	//variables
	
	//enh:sco
	
	private double damper = 1;
	//enh:field	
	
	private Hashtable fDeltas = new Hashtable();
	private Hashtable fPositions = new Hashtable();
	
	private double lastMaxMotion = 0;
	private double maxMotion = 0;
	private double motionRatio = 0;
	private double rigidity = 0.00005;
	//enh:field
	
	private int count = 100;
	//enh:field
	
	@Override
	protected Algorithm createAlgorithm ()
	{
		return new Algorithm ()
		{
			@Override
			protected void layout (Node nodes)
			{
				for (int i = 0; i < count; i++) 
				{
					relaxEdges(nodes);
					avoidLabels(nodes);
					moveNodes(nodes);
					//setProgress((float)((i+3)/count),
					//	"Apply Touch Algorithm: run"+(i+1)+"%");
				}
			}//void layout
			
//			 All edges pull nodes closer together;
			private synchronized void relaxEdges(Node nodes) 
			{
				GraphUtilities gu = new GraphUtilities();
				gu.setAllEdgesAccessed(nodes, false);
				
				for (Node m = nodes; m != null; m = m.next)
				{
					for (Edge e = m.getFirstEdge(); e != null; e = e.getNext(m))
					{
						if (!e.isAccessed)
						{
							e.isAccessed = true;
							
							double len = e.target.distance(e.source);
							
							double dx = (e.target.x - e.source.x) * rigidity * len;
							double dy = (e.target.y - e.source.y) * rigidity * len;
							
							//dx /= length;
							//dy /= length;
							movePointSet(e.target, -dx, -dy);
							movePointSet(e.source, dx, dy);
						}
					}
				}
			}
			
			private void movePointSet(Node node, double dx, double dy) 
			{	
				Point2d p = getDelta(node);
				if ((dx+p.x) != Double.NaN && (dy+p.y) != Double.NaN)
				{
					p.set(p.x + dx, p.y + dy);
				} else
					return;
			}
			
			public Point2d getDelta(Node node) 
			{
				Point2d p = (Point2d) fDeltas.get(node);
				if (p == null) 
				{
					p = new Point2d(0, 0);
					fDeltas.put(node, p);
				}
				return p;
			}
			
			private synchronized void avoidLabels(Node nodes) 
			{
				for (Node m = nodes; m != null; m = m.next) 
				{
					for (Node n = m.next; n != null; n = n.next) 
					{
						double vx = m.x - n.x;
						double vy = m.y - n.y;
						double len = m.distance(n);
						double dx = 0;
						double dy = 0;
						if (len == 0) {
							dx = 0.01;
							dy = 0.01;
						} else if (len < 5){
							//dx = vx / len;
							//dy = vy / len;
							dx = vx / len;
							dy = vy / len;
						}
						
						double repX = Math.max(m.width, n.width);
						double repY = Math.max(m.height, m.height);

						repX = Math.pow(repX * 5, 2);
						repY = Math.pow(repY * 5, 2);

						repX = Math.max(repX, repY);
						if (Math.random() > 0.03) 
						{
							movePointSet(m, dx * repX, dy * repY);
							movePointSet(n, -dx * repY, -dy * repX);
						} 
						else 
						{
							movePointSet(m, dx * repX * 3, dy * repY * 3);
							movePointSet(n, -dx * repX*3, -dy * repY * 3);
						}

					}
				}
			}
			
			public Point2d getTouchPosition(Node node) 
			{
				if (node == null) 
				{
					return null;
				}
				
				Point2d p1 = (Point2d) fPositions.get(node);
				Point2d delta = new Point2d(node.x + node.width / 2
							, node.y + node.height / 2);
				if (p1 != null) 
				{
					if (Math.abs(p1.x - delta.x) > 0.05 || Math.abs(p1.y - delta.y) > 0.05) 
					{
						p1.set(delta.x, delta.y);
						
					}
					
					return p1;
				}
				else
				{
					fPositions.put(node, delta);
					return delta;
				}
				
			}
			
			private synchronized void moveNodes(Node nodes) 
			{
				lastMaxMotion = maxMotion;
				double maxMotionA = 0;
				
				for (Node m = nodes; m != null; m = m.next) 
				{	
						Point2d delta = getDelta(m);
						Point2d position = getTouchPosition(m);
						if (position == null || delta == null)
							return;
						double dx = delta.x;
						double dy = delta.y;
						dx *= damper;
						dy *= damper;
						if (!((float)(dx/2) == Float.NaN || (float)(dy /2) == Float.NaN))
						{
							m.x = (float)(dx/2);
							m.y = (float)(dy /2);
						} else
							return;
						//delta.set(dx / 2, dy / 2);
						double distMoved = Math.sqrt(dx * dx + dy * dy);
						
						if (dx != 0 || dy != 0) 
						{						
							position.x += Math.max(-0.05, Math.min(0.05, dx));
							position.y += Math.max(-0.05, Math.min(0.05, dy));
			
							double tmpX = Math.max(0, 
									 (position.x - m.width / 2));
							double tmpY = Math.max(0, 
									 (position.y - m.height / 2));
							
							//setPosition(node,tmpX,tmpY);
							if (tmpX != Double.NaN && tmpY != Double.NaN)
							{
								m.x = (float)tmpX;
								m.y = (float)tmpY;
							} else
								return;
						}
						maxMotionA = Math.max(distMoved, maxMotionA);
					}
				
				maxMotion = maxMotionA;
				if (maxMotion > 0)
					motionRatio = lastMaxMotion / maxMotion - 1;
				else
					motionRatio = 0;
				damp();
			}

			public void damp() 
			{
				if (motionRatio <= 0.001) 
				{
					if ((maxMotion < 0.2 || (maxMotion > 1 && damper < 0.9))
							&& damper > 0.01)
						damper -= 0.01;

					else if (maxMotion < 0.4 && damper > 0.003)
						damper -= 0.003;

					else if (damper > 0.0001)
						damper -= 0.0001;
				}
				if (maxMotion < 0.001)
					damper = 0;

			}
		
		};
	}//Algorithm createAlgorithm	
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field damper$FIELD;
	public static final Type.Field rigidity$FIELD;
	public static final Type.Field count$FIELD;

	public static class Type extends Layout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (TouchLayout representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, Layout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = Layout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = Layout.Type.FIELD_COUNT + 3;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					((TouchLayout) o).count = (int) value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					return ((TouchLayout) o).count;
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((TouchLayout) o).damper = (double) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((TouchLayout) o).rigidity = (double) value;
					return;
			}
			super.setDouble (o, id, value);
		}

		@Override
		protected double getDouble (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((TouchLayout) o).damper;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((TouchLayout) o).rigidity;
			}
			return super.getDouble (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new TouchLayout ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (TouchLayout.class);
		damper$FIELD = Type._addManagedField ($TYPE, "damper", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 0);
		rigidity$FIELD = Type._addManagedField ($TYPE, "rigidity", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 1);
		count$FIELD = Type._addManagedField ($TYPE, "count", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 2);
		$TYPE.validate ();
	}

//enh:end
}//class TouchLayout
