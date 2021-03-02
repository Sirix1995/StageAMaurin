
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

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.Random;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

/** Visualizing the graph in random style
 * 
 * @date 26.03.2007
 */
public class RandomLayout extends Layout
{
	//enh:sco
	
	float magnitude = 5;//weight of the random number
	//enh:field

	int count = 1;//how often the algorithm is executed
	//enh:field
	
	boolean startWithRandom = false;
	//enh:field
	
	boolean displayTransformation = true;
	//enh:field
	
	@Override
	protected Algorithm createAlgorithm ()
	{
		return new Algorithm ()
		{
			
			@Override
			protected void layout (Node nodes)
			{	
				if (startWithRandom)
				{
					setRandomStartPositions(nodes);
				}
				
				if (transformationSteps > 1)
				{
					startWithRandom = false;
				}
				
				for (int i = 0; i < count; i++)
				{
					layoutList (nodes);
				}
			}
			
			
			private Random rnd = new Random ();
			
			private void layoutList (Node n)
			{
				GraphUtilities gu = new GraphUtilities();
				LinkedList nodesList = gu.getNodesList(n);
				
				Vector3d nullNodePosition = new Vector3d();
				Dimension dim = view.getSize();
				Matrix3d transformation = view.getTransformation();
				transformation.invert();
				
				transformation.transform(new Vector3d((-dim.width/2), (-dim.height/2), 0), nullNodePosition);
				
				((Node)nodesList.get(0)).x = (float)nullNodePosition.x;
				((Node)nodesList.get(0)).y = (float)nullNodePosition.y;
				
				for (int i = 1; i < nodesList.size(); i++)
				{
					Node nodeTemp = (Node)nodesList.get(i);
					
					float rndTemp = rnd.nextFloat();
					nodeTemp.x += magnitude * (rndTemp - 0.5f);
					float rndTemp2 = rnd.nextFloat();
					nodeTemp.y += magnitude * (rndTemp2 - 0.5f);
				}
				
			}
			
			private void setRandomStartPositions(Node n)
			{
				while (n != null)
				{
					n.x = rnd.nextFloat();
					n.y = rnd.nextFloat();
					n = n.next;
				}
			}
		};
	}

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field magnitude$FIELD;
	public static final Type.Field count$FIELD;
	public static final Type.Field startWithRandom$FIELD;
	public static final Type.Field displayTransformation$FIELD;

	public static class Type extends Layout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (RandomLayout representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, Layout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = Layout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = Layout.Type.FIELD_COUNT + 4;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					((RandomLayout) o).startWithRandom = (boolean) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((RandomLayout) o).displayTransformation = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					return ((RandomLayout) o).startWithRandom;
				case Type.SUPER_FIELD_COUNT + 3:
					return ((RandomLayout) o).displayTransformation;
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((RandomLayout) o).count = (int) value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((RandomLayout) o).count;
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((RandomLayout) o).magnitude = (float) value;
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
					return ((RandomLayout) o).magnitude;
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new RandomLayout ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (RandomLayout.class);
		magnitude$FIELD = Type._addManagedField ($TYPE, "magnitude", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		count$FIELD = Type._addManagedField ($TYPE, "count", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 1);
		startWithRandom$FIELD = Type._addManagedField ($TYPE, "startWithRandom", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 2);
		displayTransformation$FIELD = Type._addManagedField ($TYPE, "displayTransformation", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 3);
		$TYPE.validate ();
	}

//enh:end
	
}
