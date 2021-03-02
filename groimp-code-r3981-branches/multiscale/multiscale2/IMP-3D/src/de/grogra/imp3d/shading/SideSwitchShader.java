
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

package de.grogra.imp3d.shading;

import java.util.Random;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.persistence.ShareableBase;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;

public class SideSwitchShader extends SwitchShader
{
	//enh:sco

	Shader frontShader;
	//enh:field getter setter

	Shader backShader;
	//enh:field getter setter


	public SideSwitchShader ()
	{
	}


	public SideSwitchShader (Shader frontShader)
	{
		this.frontShader = frontShader;
	}


	public SideSwitchShader (Shader frontShader, Shader backShader)
	{
		this.frontShader = frontShader;
		this.backShader = backShader;
	}


	public int getAverageColor ()
	{
		return (frontShader != null) ? frontShader.getAverageColor ()
			: 0xff808080;
	}

	
	public int getFlags ()
	{
		return ((frontShader != null) ? frontShader.getFlags () : 0)
			| ((backShader != null) ? backShader.getFlags () : 0);
	}

	
	@Override
	protected Shader getShaderFor (Environment env, Vector3f in)
	{
		Shader m = (env.normal.dot (in) >= 0) ? frontShader : backShader;
		return (m != null) ? m : (frontShader != null) ? frontShader : RGBAShader.GRAY;
	}


	@Override
	public boolean isTransparent() {
		if(frontShader !=null) return frontShader.isTransparent();
		return false;
	}
	

	public void setShaders (Shader front, Shader back)
	{
		setFrontShader (front);
		setBackShader (back);
	}
	

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field frontShader$FIELD;
	public static final Type.Field backShader$FIELD;

	public static class Type extends SwitchShader.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SideSwitchShader representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SwitchShader.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SwitchShader.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = SwitchShader.Type.FIELD_COUNT + 2;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((SideSwitchShader) o).frontShader = (Shader) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((SideSwitchShader) o).backShader = (Shader) value;
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((SideSwitchShader) o).getFrontShader ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((SideSwitchShader) o).getBackShader ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SideSwitchShader ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SideSwitchShader.class);
		frontShader$FIELD = Type._addManagedField ($TYPE, "frontShader", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Shader.class), null, Type.SUPER_FIELD_COUNT + 0);
		backShader$FIELD = Type._addManagedField ($TYPE, "backShader", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Shader.class), null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

	public Shader getFrontShader ()
	{
		return frontShader;
	}

	public void setFrontShader (Shader value)
	{
		frontShader$FIELD.setObject (this, value);
	}

	public Shader getBackShader ()
	{
		return backShader;
	}

	public void setBackShader (Shader value)
	{
		backShader$FIELD.setObject (this, value);
	}

//enh:end

	public void accept(ShaderVisitor visitor) {
		visitor.visit( this );
	}
	
}
