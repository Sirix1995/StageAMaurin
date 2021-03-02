
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

import java.util.Random;
import javax.vecmath.Vector2f;


/**
 * A <code>ForceBasedLayout</code> computes a graph layout based on a 
 * force model. The concrete force model has to be implemented in subclasses.
 * 
 * @author Ole Kniemeyer
 * @date 26.03.2007
 */
public abstract class ForceBasedLayout extends Layout
{
//enh:sco

	/**
	 * The relaxation coefficient for the numerical solver.
	 */
	float relaxation = 0.0378f;
	//enh:field

	boolean displayTransformation = true;
	//enh:field

	/**
	 * The accuracy defines when the numerical solver has computed
	 * a sufficiently accurate solution.
	 */
	float accuracy = 0.01f;
	//enh:field


	/**
	 * The maximal number of iterations for the solver.
	 */
	int count = 100;
	//enh:field
	
	boolean startWithRandom = true;
	//enh:field
	public int connectedNodesSize = 0;
	public int nodesSize = 0;

	public void setStartWithRandom(boolean value)
	{
		this.startWithRandom = value;
	}
	
	public class FBAlgorithm extends Algorithm
	{
		//maxForce goes to 0
		private float maxForce;

		@Override
		protected void layout (Node nodes)
		{	
			Random rnd = new Random();
			for (Node n = nodes; n != null; n = n.next)
			{
				nodesSize++;
				if (startWithRandom && !n.equals(nodes))
				{
					setRandomPosition(n, rnd);
				}
				if (n.getFirstEdge() != null)
				{
					connectedNodesSize++;
				}
			}//for
				
			if (transformationSteps > 1)
			{
				startWithRandom = false;
			}
			
			float oldMaxForce = 0;
			for (int i = 0; i < count; i++)
			{
				oldMaxForce = maxForce;
				maxForce = 0;
				layoutList (nodes);
				
				if (maxForce > (oldMaxForce+1) && oldMaxForce != 0)
				{
					progressText = "parametres of the layout algorithm do not suit - stop!";
					break;
				}
				if (maxForce < accuracy)
				{
					break;
				}
			}
			/* setting the root node in the middle of
			the drawing node */
			param = 0;
			
			//no error occured
			if (progressText.equals(""))
			{
				progressText = "layout calculated successfully";
			}
		}		
		
		
		
		private final Vector2f sumForce = new Vector2f ();
		private final Vector2f tmpForce = new Vector2f ();
		
		private void layoutList (Node first)
		{
			for (Node n = first; n != null; n = n.next)
			{
				n.layoutVarX = n.layoutVarY = 0;
			}
			for (Node n = first; n != null; n = n.next)
			{
				for (Node m = n.next; m != null; m = m.next)
				{
					if (n != m)
					{
						computeForce (n, m, sumForce);
						Edge e = n.getEdgeTo (m);
						if (e != null)
						{
							computeForce (e, tmpForce);
							sumForce.add (tmpForce);
						}
						e = m.getEdgeTo (n);
						if (e != null)
						{
							computeForce (e, tmpForce);
							sumForce.sub (tmpForce);
						}
						
						n.layoutVarX -= sumForce.x;
						n.layoutVarY -= sumForce.y;
						
						m.layoutVarX += sumForce.x;
						m.layoutVarY += sumForce.y;
						
					}
				}
				maxForce = Math.max (Math.max (Math.abs (n.layoutVarX),
											   Math.abs (n.layoutVarY)),
									 maxForce);
				n.x += relaxation * n.layoutVarX;
				n.y += relaxation * n.layoutVarY;
			}
		}

	}

	
	@Override
	protected Algorithm createAlgorithm ()
	{
		return new FBAlgorithm ();
	}

	
	/**
	 * Computes the force contribution of a pair of nodes. This method
	 * is invoked for every pair of nodes, whether they are connected
	 * or not. The computed <code>force</code> should be anti-symmetric
	 * with respect to <code>s</code> and <code>t</code>, i.e., if these
	 * parameters are exchanged, the resulting <code>force</code> should
	 * be the negative of the original force.
	 * 
	 * @param s the source node
	 * @param t the target node
	 * @param force the force in the direction from <code>s</code> to <code>t</code>
	 * has to be placed in here
	 */
	protected abstract void computeForce (Node s, Node t, Vector2f force);

	
	/**
	 * Computes the force contribution of an edge. This method is invoked
	 * for every edge of the graph.
	 * 
	 * @param e an edge
	 * @param force the force in the direction from <code>edge.source</code>
	 * to <code>edge.target</code> has to be placed in here
	 */
	protected abstract void computeForce (Edge e, Vector2f force);
	

	protected abstract void setRandomPosition(Node nodeTemp, Random rnd);
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field relaxation$FIELD;
	public static final Type.Field displayTransformation$FIELD;
	public static final Type.Field accuracy$FIELD;
	public static final Type.Field count$FIELD;
	public static final Type.Field startWithRandom$FIELD;

	public static class Type extends Layout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (ForceBasedLayout representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, Layout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = Layout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = Layout.Type.FIELD_COUNT + 5;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((ForceBasedLayout) o).displayTransformation = (boolean) value;
					return;
				case Type.SUPER_FIELD_COUNT + 4:
					((ForceBasedLayout) o).startWithRandom = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((ForceBasedLayout) o).displayTransformation;
				case Type.SUPER_FIELD_COUNT + 4:
					return ((ForceBasedLayout) o).startWithRandom;
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 3:
					((ForceBasedLayout) o).count = (int) value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 3:
					return ((ForceBasedLayout) o).count;
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((ForceBasedLayout) o).relaxation = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((ForceBasedLayout) o).accuracy = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((ForceBasedLayout) o).relaxation;
				case Type.SUPER_FIELD_COUNT + 2:
					return ((ForceBasedLayout) o).accuracy;
			}
			return super.getFloat (o, id);
		}
	}

	static
	{
		$TYPE = new Type (ForceBasedLayout.class);
		relaxation$FIELD = Type._addManagedField ($TYPE, "relaxation", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		displayTransformation$FIELD = Type._addManagedField ($TYPE, "displayTransformation", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 1);
		accuracy$FIELD = Type._addManagedField ($TYPE, "accuracy", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 2);
		count$FIELD = Type._addManagedField ($TYPE, "count", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 3);
		startWithRandom$FIELD = Type._addManagedField ($TYPE, "startWithRandom", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 4);
		$TYPE.validate ();
	}

//enh:end

}
