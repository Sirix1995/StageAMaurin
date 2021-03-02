
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

import java.awt.*;
import de.grogra.persistence.*;
import de.grogra.util.EnumerationType;

public final class StrokeAdapter extends ShareableBase
{
	public static final int SOLID = 0;
	public static final int DASHED = 1;
	public static final int DOTTED = 2;
	public static final int DASHED_DOTTED = 3;
	public static final int DASHED_DOUBLE_DOTTED = 4;
	public static final int DASHED_TRIPLE_DOTTED = 5;

	private static final float[][] DASHES = {
		null,
		{0.5f, 0.5f},
		{0.1f, 0.9f},
		{0.5f, 0.2f, 0.1f, 0.2f},
		{0.4f, 0.15f, 0.075f, 0.15f, 0.075f, 0.15f},
		{0.31f, 0.12f, 0.07f, 0.12f, 0.07f, 0.12f, 0.07f, 0.12f}};
		
	private static final de.grogra.util.I18NBundle I18N
		= de.grogra.imp2d.IMP2D.I18N;

	private static final EnumerationType LINE_STYLE_TYPE
		= new EnumerationType ("lineStyle", I18N, DASHES.length);

	private static final EnumerationType LINE_JOIN_TYPE
		= new EnumerationType ("lineJoin", I18N, 3);

	private static final EnumerationType CAP_STYLE_TYPE
		= new EnumerationType ("capStyle", I18N, 3);

	//enh:sco SCOType

	float lineWidth = 0.01f;
	//enh:field setmethod=setLineWidth getter	

	int lineStyle;
	//enh:field type=LINE_STYLE_TYPE setmethod=setLineStyle getter	

	float dashLength = 0.2f;
	//enh:field setmethod=setDashLength getter	

	int lineJoin;
	//enh:field type=LINE_JOIN_TYPE setmethod=setLineJoin getter	
	
	float miterLimit = 1;
	//enh:field setmethod=setMiterLimit getter	
	
	int capStyle;
	//enh:field type=CAP_STYLE_TYPE setmethod=setCapStyle getter	
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field lineWidth$FIELD;
	public static final Type.Field lineStyle$FIELD;
	public static final Type.Field dashLength$FIELD;
	public static final Type.Field lineJoin$FIELD;
	public static final Type.Field miterLimit$FIELD;
	public static final Type.Field capStyle$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (StrokeAdapter representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 6;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((StrokeAdapter) o).setLineStyle ((int) value);
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((StrokeAdapter) o).setLineJoin ((int) value);
					return;
				case Type.SUPER_FIELD_COUNT + 5:
					((StrokeAdapter) o).setCapStyle ((int) value);
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
					return ((StrokeAdapter) o).getLineStyle ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((StrokeAdapter) o).getLineJoin ();
				case Type.SUPER_FIELD_COUNT + 5:
					return ((StrokeAdapter) o).getCapStyle ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((StrokeAdapter) o).setLineWidth ((float) value);
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((StrokeAdapter) o).setDashLength ((float) value);
					return;
				case Type.SUPER_FIELD_COUNT + 4:
					((StrokeAdapter) o).setMiterLimit ((float) value);
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
					return ((StrokeAdapter) o).getLineWidth ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((StrokeAdapter) o).getDashLength ();
				case Type.SUPER_FIELD_COUNT + 4:
					return ((StrokeAdapter) o).getMiterLimit ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new StrokeAdapter ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (StrokeAdapter.class);
		lineWidth$FIELD = Type._addManagedField ($TYPE, "lineWidth", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		lineStyle$FIELD = Type._addManagedField ($TYPE, "lineStyle", 0 | Type.Field.SCO, LINE_STYLE_TYPE, null, Type.SUPER_FIELD_COUNT + 1);
		dashLength$FIELD = Type._addManagedField ($TYPE, "dashLength", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 2);
		lineJoin$FIELD = Type._addManagedField ($TYPE, "lineJoin", 0 | Type.Field.SCO, LINE_JOIN_TYPE, null, Type.SUPER_FIELD_COUNT + 3);
		miterLimit$FIELD = Type._addManagedField ($TYPE, "miterLimit", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 4);
		capStyle$FIELD = Type._addManagedField ($TYPE, "capStyle", 0 | Type.Field.SCO, CAP_STYLE_TYPE, null, Type.SUPER_FIELD_COUNT + 5);
		$TYPE.validate ();
	}

	public int getLineStyle ()
	{
		return lineStyle;
	}

	public int getLineJoin ()
	{
		return lineJoin;
	}

	public int getCapStyle ()
	{
		return capStyle;
	}

	public float getLineWidth ()
	{
		return lineWidth;
	}

	public float getDashLength ()
	{
		return dashLength;
	}

	public float getMiterLimit ()
	{
		return miterLimit;
	}

//enh:end


	private transient Stroke stroke;


	public void setLineWidth (float f)
	{
		this.lineWidth = f;
		stroke = null;
	}


	public void setLineStyle (int i)
	{
		this.lineStyle = i;
		stroke = null;
	}


	public void setDashLength (float f)
	{
		this.dashLength = f;
		stroke = null;
	}


	public void setLineJoin (int i)
	{
		this.lineJoin = i;
		stroke = null;
	}


	public void setMiterLimit (float f)
	{
		this.miterLimit = f;
		stroke = null;
	}


	public void setCapStyle (int i)
	{
		this.capStyle = i;
		stroke = null;
	}


	public Stroke getStroke ()
	{
		Stroke s = stroke;
		if (s == null)
		{
			float[] a = DASHES[lineStyle];
			if (a != null)
			{
				a = (float[]) a.clone ();
				for (int i = 0; i < a.length; i++)
				{
					a[i] *= dashLength;
				}
			}
			stroke = s = new BasicStroke
				(lineWidth, capStyle, lineJoin, miterLimit, a, 0);
		}
		return s;
	}

}
