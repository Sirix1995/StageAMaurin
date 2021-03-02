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

import javax.vecmath.Point2d;
import javax.vecmath.Vector2f;
import de.grogra.imp2d.layout.Node;
import java.util.Random;

/**
 * A <code>FruchtermanReingoldLayout</code> computes a graph layout based on the
 * force based FruchtermanReingold model.
 * 
 * @date 26.03.2007
 */
public class FruchtermanReingoldLayout extends ForceBasedLayout
{	
	//Variables
	
	//enh:sco
	private double frameX = 10;
	//enh:field
	
	private double frameY = 10;
	//enh:field
	
	private double frameArea = 0;
	
	private double scaleFactor = 1.0;
	//enh:field
	
	public FruchtermanReingoldLayout()
	{
		frameArea = frameX * frameY;
	}
	
	
	/** Calculating and setting the inital positions of the nodes
	 */
	@Override
	protected void setRandomPosition(Node nodeTemp, Random rnd)
	{	
		nodeTemp.x = rnd.nextFloat();
		nodeTemp.y = rnd.nextFloat();
	}//void setRandomPosition
	
	/** Calculating the repelling force of two nodes s and t
	 * 
	 */
	@Override
	protected void computeForce(Node s, Node t, Vector2f force) {
		double idealDistance = Math.sqrt(Math.abs(frameArea / nodesSize)) * scaleFactor;
		double distance = t.distance(s);
		if (distance == 0)
			distance = 0.001;
		force.sub(t, s);
		if (distance > 0)
		{
			force.scale((float)(1/distance));
		}
		force.scale((float)(((idealDistance * idealDistance)) / distance));
	}//void computeForce Nodes

	/** Calculating the attraction force of two nodes
	 * connected by an edge e
	 */
	@Override
	protected void computeForce(Edge e, Vector2f force) {
		double idealDistance = Math.sqrt(Math.abs(frameArea / nodesSize)) * scaleFactor;
		double distance = e.source.distance(e.target);
		if (distance == 0)
			distance = 0.001;
		force.sub(e.source, e.target);
		force.scale((float)(1/distance));
		force.scale((float)(distance * distance / idealDistance));
	}//void computeForce Edge
	
			
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field frameX$FIELD;
	public static final Type.Field frameY$FIELD;
	public static final Type.Field scaleFactor$FIELD;

	public static class Type extends ForceBasedLayout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (FruchtermanReingoldLayout representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, ForceBasedLayout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = ForceBasedLayout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = ForceBasedLayout.Type.FIELD_COUNT + 3;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((FruchtermanReingoldLayout) o).frameX = (double) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((FruchtermanReingoldLayout) o).frameY = (double) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((FruchtermanReingoldLayout) o).scaleFactor = (double) value;
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
					return ((FruchtermanReingoldLayout) o).frameX;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((FruchtermanReingoldLayout) o).frameY;
				case Type.SUPER_FIELD_COUNT + 2:
					return ((FruchtermanReingoldLayout) o).scaleFactor;
			}
			return super.getDouble (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new FruchtermanReingoldLayout ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (FruchtermanReingoldLayout.class);
		frameX$FIELD = Type._addManagedField ($TYPE, "frameX", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 0);
		frameY$FIELD = Type._addManagedField ($TYPE, "frameY", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 1);
		scaleFactor$FIELD = Type._addManagedField ($TYPE, "scaleFactor", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 2);
		$TYPE.validate ();
	}

//enh:end

}//class FruchtermanReingoldLayout
