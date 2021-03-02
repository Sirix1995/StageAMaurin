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

import javax.vecmath.Vector2f;
import de.grogra.imp2d.layout.Node;
import java.util.Random;

/**
 * A <code>EadesLayout</code> computes a graph layout based on the
 * force based eades model.
 * 
 * @date 26.03.2007
 */
public class EadesLayout extends ForceBasedLayout
{	
	//variables
	
	//enh:sco
	
	//minimum X-Distance between two Nodes
	private float minDistanceX = 0.4f;
	//enh:field

	// minimum Y-Distance between two Nodes
	private float minDistanceY = 0.4f;
	//enh:field
	
	private float repellingConst = 1f;
	//enh:field
	
	private float springConst = 4f;
	//enh:field
	
	private double springLength = Math.sqrt((minDistanceX * minDistanceX) + (minDistanceY * minDistanceY));
	
	/** Calculating the inital positions of the nodes
	 */
	@Override
	protected void setRandomPosition(Node nodeTemp, Random rnd)
	{
		nodeTemp.x = rnd.nextFloat();
		nodeTemp.y = rnd.nextFloat();
	}//void setRandomPosition
	
	/** Comnputes the repelling force of two nodes s and t
	 * 
	 */
	@Override
	protected void computeForce(Node s, Node t, Vector2f force) {	
		double distance = Math.abs(t.distance(s));
		if (distance == 0)
			distance = 0.001;
		float factor = (float)(repellingConst / distance);
		
		force.sub(t, s);
		force.scale((float)(1/distance));
		force.scale(factor);
	}

	/** Computes the attraction force of two nodes connected
	 * by an edge e
	 */
	@Override
	protected void computeForce(Edge e, Vector2f force) {
		double distance = Math.abs(e.source.distance(e.target));
		if (distance == 0)
			distance = 0.001;
		float factor = (float)(Math.log(distance / springLength) * springConst);
		
		force.sub(e.source, e.target);
		force.scale((float)(1 / distance));
		force.scale(factor);
	}

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field minDistanceX$FIELD;
	public static final Type.Field minDistanceY$FIELD;
	public static final Type.Field repellingConst$FIELD;
	public static final Type.Field springConst$FIELD;

	public static class Type extends ForceBasedLayout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (EadesLayout representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, ForceBasedLayout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = ForceBasedLayout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = ForceBasedLayout.Type.FIELD_COUNT + 4;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((EadesLayout) o).minDistanceX = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((EadesLayout) o).minDistanceY = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((EadesLayout) o).repellingConst = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((EadesLayout) o).springConst = (float) value;
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
					return ((EadesLayout) o).minDistanceX;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((EadesLayout) o).minDistanceY;
				case Type.SUPER_FIELD_COUNT + 2:
					return ((EadesLayout) o).repellingConst;
				case Type.SUPER_FIELD_COUNT + 3:
					return ((EadesLayout) o).springConst;
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new EadesLayout ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (EadesLayout.class);
		minDistanceX$FIELD = Type._addManagedField ($TYPE, "minDistanceX", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		minDistanceY$FIELD = Type._addManagedField ($TYPE, "minDistanceY", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		repellingConst$FIELD = Type._addManagedField ($TYPE, "repellingConst", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 2);
		springConst$FIELD = Type._addManagedField ($TYPE, "springConst", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 3);
		$TYPE.validate ();
	}

//enh:end
}//class EadesLayout
