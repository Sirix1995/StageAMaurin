
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

import javax.vecmath.Vector3f;

import de.grogra.ray.physics.Environment;

public class AlgorithmSwitchShader extends SwitchShader
{
	//enh:sco

	Shader guiShader;
	//enh:field setter

	Shader raytracerShader;
	//enh:field setter

	Shader radiationShader;
	//enh:field setter

	public AlgorithmSwitchShader ()
	{
	}


	public AlgorithmSwitchShader (Shader guiShader, Shader raytracerShader, Shader radiationShader)
	{
		this.guiShader = guiShader;
		this.raytracerShader = raytracerShader;
		this.radiationShader = radiationShader;
	}


	public AlgorithmSwitchShader (Shader guiShader, Shader radiationShader)
	{
		this.guiShader = guiShader;
		this.radiationShader = radiationShader;
	}


	public int getAverageColor ()
	{
		return (guiShader != null) ? guiShader.getAverageColor ()
			: 0xff808080;
	}

	
	public int getFlags ()
	{
		return ((raytracerShader != null) ? raytracerShader.getFlags () : 0)
			| ((radiationShader != null) ? radiationShader.getFlags () : 0);
	}

	
	@Override
	protected Shader getShaderFor (Environment env, Vector3f in)
	{
		Shader m = (env.type == Environment.RADIATION_MODEL) ? radiationShader : raytracerShader;
		return (m != null) ? m : RGBAShader.GRAY;
	}


	@Override
	public boolean isTransparent() {
		if(raytracerShader !=null) return raytracerShader.isTransparent();
		return false;
	}


	public Shader getGUIShader ()
	{
		return guiShader;
	}

	public Shader getRaytracerShader ()
	{
		return (raytracerShader != null) ? raytracerShader : guiShader;
	}

	public Shader getRadiationShader ()
	{
		return (radiationShader != null) ? radiationShader : getRaytracerShader ();
	}

	public void setGUIShader (Shader value)
	{
		guiShader$FIELD.setObject (this, value);
	}


	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field guiShader$FIELD;
	public static final Type.Field raytracerShader$FIELD;
	public static final Type.Field radiationShader$FIELD;

	public static class Type extends SwitchShader.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (AlgorithmSwitchShader representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SwitchShader.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SwitchShader.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = SwitchShader.Type.FIELD_COUNT + 3;

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
					((AlgorithmSwitchShader) o).guiShader = (Shader) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((AlgorithmSwitchShader) o).raytracerShader = (Shader) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((AlgorithmSwitchShader) o).radiationShader = (Shader) value;
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
					return ((AlgorithmSwitchShader) o).guiShader;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((AlgorithmSwitchShader) o).raytracerShader;
				case Type.SUPER_FIELD_COUNT + 2:
					return ((AlgorithmSwitchShader) o).radiationShader;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new AlgorithmSwitchShader ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (AlgorithmSwitchShader.class);
		guiShader$FIELD = Type._addManagedField ($TYPE, "guiShader", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Shader.class), null, Type.SUPER_FIELD_COUNT + 0);
		raytracerShader$FIELD = Type._addManagedField ($TYPE, "raytracerShader", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Shader.class), null, Type.SUPER_FIELD_COUNT + 1);
		radiationShader$FIELD = Type._addManagedField ($TYPE, "radiationShader", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Shader.class), null, Type.SUPER_FIELD_COUNT + 2);
		$TYPE.validate ();
	}

	public void setGuiShader (Shader value)
	{
		guiShader$FIELD.setObject (this, value);
	}

	public void setRaytracerShader (Shader value)
	{
		raytracerShader$FIELD.setObject (this, value);
	}

	public void setRadiationShader (Shader value)
	{
		radiationShader$FIELD.setObject (this, value);
	}

//enh:end

	public void accept(ShaderVisitor visitor) {
		visitor.visit( this );
	}
	
}
