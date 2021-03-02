
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

package de.grogra.turtle;

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Node;
import de.grogra.grogra.LSystem;
import de.grogra.imp3d.shading.Shader;
import de.grogra.persistence.Manageable;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.Transaction;
import de.grogra.rgg.NodeToFloat;
import de.grogra.rgg.NodeToInt;

public final class TurtleState extends Object implements Manageable
{
	//enh:sco SCOType

	public static final TurtleState DEFAULT_TURTLE_STATE;

	static
	{
		DEFAULT_TURTLE_STATE = new TurtleState ();
		DEFAULT_TURTLE_STATE.initialState = DEFAULT_TURTLE_STATE;
	}

	public static final NodeToFloat LENGTH = new NodeToFloat ()
	{
		@Override
		public float evaluateFloat (Node node)
		{
			return TurtleState.getBefore (node, GraphState.current (node.getGraph ())).length;
		}
	};


	public static final NodeToFloat DIAMETER = new NodeToFloat ()
	{
		@Override
		public float evaluateFloat (Node node)
		{
			return TurtleState.getBefore (node, GraphState.current (node.getGraph ())).diameter;
		}
	};


	public static final NodeToFloat PARAMETER = new NodeToFloat ()
	{
		@Override
		public float evaluateFloat (Node node)
		{
			return TurtleState.getBefore (node, GraphState.current (node.getGraph ())).parameter;
		}
	};


	public static final NodeToInt COLOR = new NodeToInt ()
	{
		@Override
		public int evaluateInt (Node node)
		{
			return TurtleState.getBefore (node, GraphState.current (node.getGraph ())).color;
		}
	};


	public static final NodeToInt ORDER = new NodeToInt ()
	{
		@Override
		public int evaluateInt (Node node)
		{
			return TurtleState.getBefore (node, GraphState.current (node.getGraph ())).order;
		}
	};


	public static final NodeToInt GENERATIVE_DISTANCE = new NodeToInt ()
	{
		@Override
		public int evaluateInt (Node node)
		{
			return TurtleState.getBefore (node, GraphState.current (node.getGraph ())).generativeDistance;
		}
	};


	public int color = 14;
	//enh:field
	
	public int localColor = 14;
	
	public int order = 0;
	
	public int generativeDistance = 1;
	
	public float length = 100;
	//enh:field
	
	public float localLength = 0;
	
	public float diameter = 0.1f;
	//enh:field
	
	public float localDiameter = 0.1f;
	
	public float tropism = 0;
	//enh:field

	public float localTropism = 0;
	
	public float parameter;
	//enh:field
	
	public float localParameter;
	
	public float carbon;
	//enh:field
	
	public float localCarbon;
	
	public float relPosition;
	
	public float heartwood;
	//enh:field
	
	public float localHeartwood;

	public int internodeCount;
	//enh:field
	
	public int localInternodeCount;
	
	public int localScale;
	
	public Shader shader;
	
	public Shader localShader;

	public TurtleState initialState;


	public static TurtleState getBefore (Object node, GraphState gs)
	{
		int instanceIndex = gs.getInstancingPathIndex ();
		if (instanceIndex > 0)
		{
			gs.moveToPreviousInstance ();
			Object parent = gs.getInstancingPath ().getObject (instanceIndex - 1);
			TurtleState ts = gs.getObject
				(parent, false, null, TurtleStateAttribute.ATTRIBUTE);
			gs.moveToNextInstance ();
			return ts;
		}
		Object parent = gs.getObject (node, true, gs.getGraph ().getParentAttribute ());
		if (parent == null)
		{
			return TurtleStateAttribute.ATTRIBUTE.getInitialValue (gs);
		}
		return gs.getObject
			(parent, false, null, TurtleStateAttribute.ATTRIBUTE);
	}


	public static TurtleState getAfter (Object node, GraphState gs)
	{
		return gs.getObject (node, true, null, TurtleStateAttribute.ATTRIBUTE);
	}


	public static float length (Node node)
	{
		return getBefore (node, GraphState.current (node.getGraph ())).length;
	}


	public static float diameter (Node node)
	{
		return getBefore (node, GraphState.current (node.getGraph ())).diameter;
	}


	public static float parameter (Node node)
	{
		return getBefore (node, GraphState.current (node.getGraph ())).parameter;
	}


	public static float tropism (Node node)
	{
		return getBefore (node, GraphState.current (node.getGraph ())).tropism;
	}


	public static float relPosition (Node node)
	{
		return getBefore (node, GraphState.current (node.getGraph ())).relPosition;
	}


	public static int color (Node node)
	{
		return getBefore (node, GraphState.current (node.getGraph ())).color;
	}


	public static Shader shader (Node node)
	{
		return getBefore (node, GraphState.current (node.getGraph ())).shader;
	}


	public static int order (Node node)
	{
		return getBefore (node, GraphState.current (node.getGraph ())).order;
	}


	public static int generativeDistance (Node node)
	{
		return getBefore (node, GraphState.current (node.getGraph ())).generativeDistance;
	}


	public void inherit (TurtleState state)
	{
		initialState = state.initialState;
		color = state.color;
		localColor = state.localColor;
		order = state.order;
		generativeDistance = state.generativeDistance;
		length = state.length;
		localLength = state.localLength;
		diameter = state.diameter;
		localDiameter = state.localDiameter;
		tropism = state.tropism;
		localTropism = state.localTropism;
		parameter = state.parameter;
		localParameter = state.localParameter;
		relPosition = state.relPosition;
		carbon = state.carbon;
		localCarbon = state.localCarbon;
		heartwood = state.heartwood;
		localHeartwood = state.localHeartwood;
		internodeCount = state.internodeCount;
		localInternodeCount = state.localInternodeCount;
		shader = state.shader;
		localShader = state.localShader;
		localScale = state.localScale;
	}

	public void fieldModified (PersistenceField field, int[] indices, Transaction t)
	{
	}


	public int getStamp ()
	{
		return 0;
	}


	public Manageable manageableReadResolve ()
	{
		return this;
	}


	public Object manageableWriteReplace ()
	{
		return this;
	}

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field color$FIELD;
	public static final Type.Field length$FIELD;
	public static final Type.Field diameter$FIELD;
	public static final Type.Field tropism$FIELD;
	public static final Type.Field parameter$FIELD;
	public static final Type.Field carbon$FIELD;
	public static final Type.Field heartwood$FIELD;
	public static final Type.Field internodeCount$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (TurtleState representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 8;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((TurtleState) o).color = (int) value;
					return;
				case Type.SUPER_FIELD_COUNT + 7:
					((TurtleState) o).internodeCount = (int) value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((TurtleState) o).color;
				case Type.SUPER_FIELD_COUNT + 7:
					return ((TurtleState) o).internodeCount;
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((TurtleState) o).length = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((TurtleState) o).diameter = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((TurtleState) o).tropism = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 4:
					((TurtleState) o).parameter = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 5:
					((TurtleState) o).carbon = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 6:
					((TurtleState) o).heartwood = (float) value;
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
					return ((TurtleState) o).length;
				case Type.SUPER_FIELD_COUNT + 2:
					return ((TurtleState) o).diameter;
				case Type.SUPER_FIELD_COUNT + 3:
					return ((TurtleState) o).tropism;
				case Type.SUPER_FIELD_COUNT + 4:
					return ((TurtleState) o).parameter;
				case Type.SUPER_FIELD_COUNT + 5:
					return ((TurtleState) o).carbon;
				case Type.SUPER_FIELD_COUNT + 6:
					return ((TurtleState) o).heartwood;
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new TurtleState ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (TurtleState.class);
		color$FIELD = Type._addManagedField ($TYPE, "color", Type.Field.PUBLIC  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 0);
		length$FIELD = Type._addManagedField ($TYPE, "length", Type.Field.PUBLIC  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		diameter$FIELD = Type._addManagedField ($TYPE, "diameter", Type.Field.PUBLIC  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 2);
		tropism$FIELD = Type._addManagedField ($TYPE, "tropism", Type.Field.PUBLIC  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 3);
		parameter$FIELD = Type._addManagedField ($TYPE, "parameter", Type.Field.PUBLIC  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 4);
		carbon$FIELD = Type._addManagedField ($TYPE, "carbon", Type.Field.PUBLIC  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 5);
		heartwood$FIELD = Type._addManagedField ($TYPE, "heartwood", Type.Field.PUBLIC  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 6);
		internodeCount$FIELD = Type._addManagedField ($TYPE, "internodeCount", Type.Field.PUBLIC  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 7);
		$TYPE.validate ();
	}

//enh:end
	
}
