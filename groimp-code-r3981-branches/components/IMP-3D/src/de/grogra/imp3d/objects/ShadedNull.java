
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

import de.grogra.graph.impl.Node;
import de.grogra.imp3d.shading.*;
import de.grogra.math.Transform3D;

public class ShadedNull extends Null
{
	public static final int INFINITE_MASK = 1 << Null.USED_BITS;

	public static final int USED_BITS = Null.USED_BITS + 1;

	protected Shader shader = null;
	//enh:field attr=Attributes.SHADER getter setmethod=setShader

	protected Interior interior = null;
	//enh:field attr=Attributes.INTERIOR getter setter

	// boolean treatedAsInfinite
	//enh:field type=bits(INFINITE_MASK) attr=Attributes.TREATED_AS_INFINITE getter setter

	public ShadedNull ()
	{
		super ();
	}


	public ShadedNull (Transform3D transform)
	{
		super (transform);
	}

	
	@Override
	public int getSymbolColor ()
	{
		return 0x00ffffa0;
	}


	private SideSwitchShader getSideSwitchShader ()
	{
		if ((shader instanceof SideSwitchShader)
			&& (((SideSwitchShader) shader).getProvider () == null))
		{
			return (SideSwitchShader) shader;
		}
		SideSwitchShader s = new SideSwitchShader ();
		if (!(shader instanceof SideSwitchShader))
		{
			s.setFrontShader (shader);
		}
		setShader (s);
		return s;
	}


	public void setColor (int rgb)
	{
		setShader (new RGBAShader (rgb | 0xff000000));
	}


	public void setColor (float r, float g, float b)
	{
		setShader (new RGBAShader (r, g, b));
	}

	public void setMaterial (Shader mat)
	{
		setShader (mat);
	}


	public void setFrontShader (Shader mat)
	{
		getSideSwitchShader ().setFrontShader (mat);
	}


	public void setBackShader (Shader mat)
	{
		getSideSwitchShader ().setBackShader (mat);
	}


	public void setShaders (Shader front, Shader back)
	{
		getSideSwitchShader ().setShaders (front, back);
	}
	

	public void setShader (Shader shader)
	{
		this.shader = shader;
	}

//	enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field shader$FIELD;
	public static final NType.Field interior$FIELD;
	public static final NType.Field treatedAsInfinite$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (ShadedNull.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((ShadedNull) o).setShader ((Shader) value);
					return;
				case 1:
					((ShadedNull) o).interior = (Interior) value;
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
					return ((ShadedNull) o).getShader ();
				case 1:
					return ((ShadedNull) o).getInterior ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new ShadedNull ());
		$TYPE.addManagedField (shader$FIELD = new _Field ("shader", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Shader.class), null, 0));
		$TYPE.addManagedField (interior$FIELD = new _Field ("interior", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Interior.class), null, 1));
		$TYPE.addManagedField (treatedAsInfinite$FIELD = new NType.BitField ($TYPE, "treatedAsInfinite", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, INFINITE_MASK));
		$TYPE.declareFieldAttribute (shader$FIELD, Attributes.SHADER);
		$TYPE.declareFieldAttribute (interior$FIELD, Attributes.INTERIOR);
		$TYPE.declareFieldAttribute (treatedAsInfinite$FIELD, Attributes.TREATED_AS_INFINITE);
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
		return new ShadedNull ();
	}

	public Shader getShader ()
	{
		return shader;
	}

	public Interior getInterior ()
	{
		return interior;
	}

	public void setInterior (Interior value)
	{
		interior$FIELD.setObject (this, value);
	}

	public boolean isTreatedAsInfinite ()
	{
		return (bits & INFINITE_MASK) != 0;
	}

	public void setTreatedAsInfinite (boolean v)
	{
		if (v) bits |= INFINITE_MASK; else bits &= ~INFINITE_MASK;
	}

//enh:end


}
