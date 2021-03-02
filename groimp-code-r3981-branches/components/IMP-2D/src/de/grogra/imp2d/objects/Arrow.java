
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

package de.grogra.imp2d.objects;

import java.awt.geom.GeneralPath;

import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;
import de.grogra.util.EnumerationType;

public final class Arrow extends ShareableBase
{
	public static final int NONE = 0;
	public static final int SIMPLE = 1;
	public static final int TECHNICAL = 2;
	public static final int CLASSIC = 3;
	public static final int DIAMOND = 4;
	public static final int FILLED_TECHNICAL = 5;

		
	private static final de.grogra.util.I18NBundle I18N
		= de.grogra.imp2d.IMP2D.I18N;

	private static final EnumerationType ARROW_TYPE
		= new EnumerationType ("arrowType", I18N, 5);

	//enh:sco SCOType

	int type = NONE;
	//enh:field type=ARROW_TYPE getter setter	

	float width = 0.1f;
	//enh:field getter setter	

	float relHeight = 1;
	//enh:field getter setter	
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field type$FIELD;
	public static final Type.Field width$FIELD;
	public static final Type.Field relHeight$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (Arrow representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 3;

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
					((Arrow) o).type = value;
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
					return ((Arrow) o).getType ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((Arrow) o).width = value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((Arrow) o).relHeight = value;
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
					return ((Arrow) o).getWidth ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((Arrow) o).getRelHeight ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new Arrow ();
		}

	}

	@Override
	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (Arrow.class);
		type$FIELD = Type._addManagedField ($TYPE, "type", 0 | Type.Field.SCO, ARROW_TYPE, null, Type.SUPER_FIELD_COUNT + 0);
		width$FIELD = Type._addManagedField ($TYPE, "width", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		relHeight$FIELD = Type._addManagedField ($TYPE, "relHeight", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 2);
		$TYPE.validate ();
	}

	public int getType ()
	{
		return type;
	}

	public void setType (int value)
	{
		this.type = value;
	}

	public float getWidth ()
	{
		return width;
	}

	public void setWidth (float value)
	{
		this.width = value;
	}

	public float getRelHeight ()
	{
		return relHeight;
	}

	public void setRelHeight (float value)
	{
		this.relHeight = value;
	}

//enh:end


	public static void add (Arrow a, GeneralPath shape, boolean append,
							float xbase, float ybase, float xtip, float ytip)
	{
		float lx = xtip - xbase, ly = ytip - ybase;
		float tx, ty;
		tx = lx * lx + ly * ly;
		if ((tx > 0) && (a != null))
		{
			float t = a.width / (float) Math.sqrt (tx);
			ty = 0.5f * a.relHeight * (lx *= t);
			tx = -0.5f * a.relHeight * (ly *= t);
		}
		else
		{
			tx = 0;
			ty = 0;
		}
		switch ((a != null) ? a.type : Arrow.SIMPLE)
		{
			case Arrow.SIMPLE:
				if (append)
				{
					shape.lineTo (xtip, ytip);
					shape.lineTo (xtip - lx - tx, ytip - ly - ty);
					shape.moveTo (xtip, ytip);
					shape.lineTo (xtip - lx + tx, ytip - ly + ty);
				}
				else
				{
					shape.moveTo (xtip - lx - tx, ytip - ly - ty);
					shape.lineTo (xtip, ytip);
					shape.lineTo (xtip - lx + tx, ytip - ly + ty);
					shape.moveTo (xtip, ytip);
				}
				break;
			case Arrow.TECHNICAL:
				if (append)
				{
					shape.lineTo (xtip - lx, ytip - ly);
				}
				shape.moveTo (xtip - lx - tx, ytip - ly - ty);
				shape.lineTo (xtip, ytip);
				shape.lineTo (xtip - lx + tx, ytip - ly + ty);
				shape.closePath ();
				if (!append)
				{
					shape.moveTo (xtip - lx, ytip - ly);
				}
				break;
			case Arrow.FILLED_TECHNICAL:
				if (append)
				{
					shape.lineTo (xtip - lx, ytip - ly);
				}
				shape.moveTo (xtip - lx - tx, ytip - ly - ty);
				shape.lineTo (xtip, ytip);
				shape.lineTo (xtip - lx + tx, ytip - ly + ty);				
				shape.lineTo (xtip - lx - tx, ytip - ly - ty);

				float tx2, ty2;
				tx2 = lx * lx + ly * ly;
				if ((tx2 > 0) && (a != null))
				{
					float t = a.width / (float) Math.sqrt (tx);
					ty2 = 0.2f * a.relHeight * (lx * t);
					tx2 = -0.2f * a.relHeight * (ly * t);
				} else {
					tx2 = 0;
					ty2 = 0;
				}

				if(Math.abs(ty2*5) > 0.08) ty2 = 0.08f / 5f;
				for (int i = 1; i < 5; i++)
				{
					shape.lineTo (xtip - lx - i*tx2, ytip - ly - i*ty2);
					shape.lineTo (xtip, ytip);
					shape.lineTo (xtip - lx + i*tx2, ytip - ly + i*ty2);				
				}
				shape.lineTo (xtip - lx, ytip - ly);
				shape.lineTo (xtip, ytip);
				shape.closePath ();
				if (!append)
				{
					shape.moveTo (xtip - lx, ytip - ly);
				}
				break;				
			case Arrow.CLASSIC:
			case Arrow.DIAMOND:
				float x, y = (a.type == Arrow.CLASSIC) ? 1.2f : 0.5f;
				x = lx * y;
				y *= ly;
				if (append)
				{
					shape.lineTo (xtip - lx, ytip - ly);
				}
				else
				{
					shape.moveTo (xtip - lx, ytip - ly);
				}
				shape.lineTo (xtip - x - tx, ytip - y - ty);
				shape.lineTo (xtip, ytip);
				shape.lineTo (xtip - x + tx, ytip - y + ty);
				shape.lineTo (xtip - lx, ytip - ly);
				break;
			default:
				if (append)
				{
					shape.lineTo (xtip, ytip);
				}
				else
				{
					shape.moveTo (xtip, ytip);
				}
				break;
		}
	}

}
