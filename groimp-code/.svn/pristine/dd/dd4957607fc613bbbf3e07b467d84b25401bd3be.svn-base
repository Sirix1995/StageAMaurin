
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

import de.grogra.graph.BooleanAttribute;
import de.grogra.graph.FloatAttribute;
import de.grogra.graph.GraphState;
import de.grogra.graph.IntAttribute;
import de.grogra.graph.ObjectAttribute;
import de.grogra.imp3d.objects.Cylinder;
import de.grogra.imp3d.objects.GlobalTransformation;
import de.grogra.imp3d.objects.VertexSequence;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.imp3d.shading.Shader;
import de.grogra.vecmath.Matrix34d;

public abstract class Shoot extends TurtleStep implements TurtleModifier, VertexSequence.Vertex
{	
	private float[] localRegisters;
	// enh:field getter setter

	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TURTLE_MODIFIER);
		$TYPE.addAccessor (new AccessorBridge (Attributes.BASE_OPEN));
		$TYPE.addAccessor (new AccessorBridge (Attributes.TOP_OPEN));
		$TYPE.addAccessor (new AccessorBridge (Attributes.SHAPE));
		$TYPE.addAccessor (new AccessorBridge (Attributes.RADIUS));
		$TYPE.addAccessor (new AccessorBridge (Attributes.PARAMETER));
		$TYPE.addAccessor (new AccessorBridge (Attributes.LOCAL_SCALE));
		$TYPE.addAccessor (new AccessorBridge (Attributes.HEARTWOOD));
		$TYPE.addAccessor (new AccessorBridge (Attributes.CARBON));
		$TYPE.addAccessor (new AccessorBridge (Attributes.TROPISM_STRENGTH));
		$TYPE.addAccessor (new AccessorBridge (Attributes.REL_POSITION));
		$TYPE.addAccessor (new AccessorBridge (Attributes.ORDER));
		$TYPE.addAccessor (new AccessorBridge (Attributes.INTERNODE_COUNT));
		$TYPE.addAccessor (new AccessorBridge (Attributes.GENERATIVE_DISTANCE));
		$TYPE.addAccessor (new AccessorBridge (Attributes.DTG_COLOR));
		$TYPE.addAccessor (new FieldAttributeAccessor (Attributes.SHADER, shader$FIELD)
			{
				@Override
				public Object getObject (Object o, Object placeIn, GraphState gs)
				{
					return ((Shoot) o).getShaderOrColor (gs);
				}
			});
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field localRegisters$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Shoot.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Shoot) o).localRegisters = (float[]) value;
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
					return ((Shoot) o).getLocalRegisters ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (Shoot.class);
		$TYPE.addManagedField (localRegisters$FIELD = new _Field ("localRegisters", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (float[].class), null, 0));
		initType ();
		$TYPE.validate ();
	}

	public float[] getLocalRegisters ()
	{
		return localRegisters;
	}

	public void setLocalRegisters (float[] value)
	{
		localRegisters$FIELD.setObject (this, value);
	}

//enh:end
	
	@Override
	public int getSymbolColor ()
	{
		return 0x00ffffa0;
	}

	
	Shader getShaderOrColor (GraphState gs)
	{
		if (getShader () != null)
		{
			return getShader ();
		}
		int c = getInt (Attributes.DTG_COLOR, gs);
		if (c >= 0)
		{
			return RGBAShader.forEGAColor (c);
		}
		return TurtleState.getBefore (this, gs).localShader;
	}


	@Override
	protected boolean getBoolean (BooleanAttribute a, GraphState gs)
	{
		return !((a == Attributes.BASE_OPEN) || (a == Attributes.TOP_OPEN))
			&& super.getBoolean (a, gs);
	}


	@Override
	protected float getFloat (FloatAttribute a, GraphState gs)
	{
		return (a == Attributes.RADIUS)
			? TurtleState.getBefore (this, gs).localDiameter / 2
			: (a == Attributes.PARAMETER)
			? TurtleState.getBefore (this, gs).localParameter
			: (a == Attributes.REL_POSITION)
			? TurtleState.getBefore (this, gs).relPosition
			: (a == Attributes.CARBON)
			? TurtleState.getBefore (this, gs).localCarbon
			: (a == Attributes.HEARTWOOD)
			? TurtleState.getBefore (this, gs).localHeartwood
			: (a == Attributes.TROPISM_STRENGTH)
			? TurtleState.getBefore (this, gs).localTropism
			: super.getFloat (a, gs);
	}


	@Override
	protected int getInt (IntAttribute a, GraphState gs)
	{
		return (a == Attributes.ORDER)
			? TurtleState.getBefore (this, gs).order
			: (a == Attributes.GENERATIVE_DISTANCE)
			? TurtleState.getBefore (this, gs).generativeDistance
			: (a == Attributes.DTG_COLOR)
			? TurtleState.getBefore (this, gs).localColor
			: (a == Attributes.INTERNODE_COUNT)
			? TurtleState.getBefore (this, gs).internodeCount
			: (a == Attributes.LOCAL_SCALE)
			? TurtleState.getBefore (this, gs).localScale
			: super.getInt (a, gs);
	}


	@Override
	protected Object getObject (ObjectAttribute a, Object placeIn, GraphState gs)
	{
		if (a == Attributes.SHAPE)
		{
			return Cylinder.$TYPE.getRepresentative ();
		}
		else
		{
			return super.getObject (a, placeIn, gs);
		}
	}


	public void execute (Object node, TurtleState state, GraphState gs)
	{
		state.localLength = state.length;
		state.localDiameter = state.diameter;
		state.localColor = state.color;
		state.localShader = state.shader;
		state.localParameter = state.parameter;
		state.localCarbon = state.carbon;
		state.localHeartwood = state.heartwood;
		state.localInternodeCount = state.internodeCount;
		state.relPosition = 0;
		state.generativeDistance++;
		state.localScale = 0;
	}

	
	public Matrix34d getVertexTransformation (Object node, GraphState gs)
	{
		Matrix34d m = GlobalTransformation.get (node, true, gs, true);
		float r = gs.getFloat (node, true, Attributes.RADIUS);
		if (r != 1)
		{
			m = new Matrix34d (m);
			m.m00 *= r;
			m.m10 *= r;
			m.m20 *= r;
			m.m01 *= r;
			m.m11 *= r;
			m.m21 *= r;
		}
		return m;
	}

}
