
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

import java.awt.*;
import javax.vecmath.*;
import de.grogra.imp.objects.*;
import de.grogra.imp.*;
import de.grogra.imp3d.*;
import de.grogra.graph.*;
import de.grogra.math.*;
import de.grogra.imp.edit.ViewSelection;

public abstract class TextLabelBase extends Label implements Pickable
{
	protected static final int OUTLINED_MASK = 1 << Label.USED_BITS;
	protected static final int FILLED_MASK = 2 << Label.USED_BITS;

	public static final int USED_BITS = Label.USED_BITS + 2;

	protected Color3f color = new Color3f (1, 1, 1);
	//enh:field type=Tuple3fType.COLOR attr=Attributes.COLOR getter setter

	protected Color3f fillColor = new Color3f (0, 0, 1);
	//enh:field type=Tuple3fType.COLOR attr=Attributes.FILL_COLOR getter setter

	protected FontAdapter font = null;
	//enh:field type=FontAdapter.$TYPE attr=Attributes.FONT getter setter

	// boolean outlined
	//enh:field type=bits(OUTLINED_MASK) attr=Attributes.OUTLINED getter setter

	// boolean filled
	//enh:field type=bits(FILLED_MASK) attr=Attributes.FILLED getter setter
	
	protected abstract String getCaption ();


	public static void pick (String caption, Font font,
							 int horizontal, int vertical,
							 Point3d origin, Vector3d direction,
							 Matrix4d transformation, PickList list)
	{
		Point3d b = list.p3d0;
		Tuple2f p = list.p2f0;
		b.set (0, 0, 0);
		transformation.transform (b);
		if (((View3D) list.getView ()).getCanvasCamera ()
			.projectWorld (b, p) > 0)
		{
			FontMetrics fm = ((Component) list.getView ().getComponent ())
				.getFontMetrics (font);

			int lc, w;
			if (caption == null)
			{
				lc = 1;
				w = 10;
			}
			else
			{
				lc = 1;
				w = 0;
				char[] chars = caption.toCharArray ();
				int len = chars.length;
				int pos = 0;
				for (int i = 0; i <= len; i++)
				{
					if ((i == len) || (chars[i] == '\n'))
					{
						if (i > pos)
						{
							w = Math.max (w, fm.charsWidth (chars, pos, i - pos));
							pos = i + 1;
						}
						if (i < len - 1)
						{
							lc++;
						}
					}
				}
			}
			int dx, dy, a = fm.getAscent (), d = fm.getDescent (),
				s = fm.charWidth ('x') / 2, h = fm.getHeight ();
			switch (horizontal)
			{
				case Attributes.H_ALIGN_LEFT:
					dx = 0;
					break;
				case Attributes.H_ALIGN_RIGHT:
					dx = -w;
					break;
				default:
					dx = w / -2;
					break;
			}
			switch (vertical)
			{
				case Attributes.V_ALIGN_TOP:
					dy = a;
					break;
				case Attributes.V_ALIGN_BOTTOM:
					dy = (1 - lc) * h - d;
					break;
				default:
					dy = (1 - lc) * h / 2;
					break;
			}
			int x1 = (int) p.x + dx - s, y1 = (int) p.y + dy - a - s;
			if ((x1 <= list.getViewX ()) && (y1 <= list.getViewY ())
				&& (list.getViewX () <= x1 + w + 2 * s)
				&& (list.getViewY () <= y1 + a + d + 2 * s + (lc - 1) * h))
			{
				b.set (0, 0, 0);
				list.add (de.grogra.vecmath.Math2.closestConnection (origin, direction, b));
			}
		}
	}

	
	private static final Color3f GRAY = new Color3f (0.5f, 0.5f, 0.5f);

	public static void draw (String caption, Font font,int horizontal, int vertical,
							 Tuple3f c, Tuple3f b, boolean filled, boolean outlined,
							 RenderState rs)
	{
		Tuple2f p = rs.getPool ().q2f0;
		Tuple3f t = rs.getPool ().q3f0;
		t.set (0, 0, 0);
		if (!rs.getWindowPos (t, p))
		{
			return;
		}
		int px = (int) p.x;
		int py = (int) p.y;
		int highlight = rs.getCurrentHighlight ();
		if ((highlight & (ViewSelection.MOUSE_OVER_SELECTED | ViewSelection.MOUSE_OVER)) != 0)
		{
			b = GRAY;
			Tuple3fType.setColor
				(t, ViewSelection.getColor (Tuple3fType.colorToInt (c), highlight, false));
			c = t;
		}
		FontMetrics fm = rs.getFontMetrics (font);
		int lc, w, len;
		char[] chars;
		if (caption == null)
		{
			chars = null;
			len = 0;
			lc = 1;
			w = 10;
		}
		else
		{
			lc = 1;
			w = 0;
			chars = caption.toCharArray ();
			len = chars.length;
			int pos = 0;
			for (int i = 0; i <= len; i++)
			{
				if ((i == len) || (chars[i] == '\n'))
				{
					if (i > pos)
					{
						w = Math.max (w, fm.charsWidth (chars, pos, i - pos));
					}
					if (i < len - 1)
					{
						lc++;
					}
					pos = i + 1;
				}
			}
		}
		int dx, dy, a = fm.getAscent (), d = fm.getDescent (),
			s = fm.charWidth ('x') / 2, h = fm.getHeight ();
		switch (horizontal)
		{
			case Attributes.H_ALIGN_LEFT:
				dx = 0;
				break;
			case Attributes.H_ALIGN_RIGHT:
				dx = -w;
				break;
			default:
				dx = w / -2;
				break;
		}
		switch (vertical)
		{
			case Attributes.V_ALIGN_TOP:
				dy = a;
				break;
			case Attributes.V_ALIGN_BOTTOM:
				dy = (1 - lc) * h - d;
				break;
			default:
				dy = (1 - lc) * h / 2;
				break;
		}
		if (filled)
		{
			rs.fillRectangle (px + dx - s, py + dy - a - s,
							  w + 2 * s, a + d + 2 * s + (lc - 1) * h, b);
		}
		if (outlined)
		{
			rs.drawRectangle (px + dx - s, py + dy - a - s,
							  w + 2 * s, a + d + 2 * s + (lc - 1) * h, c);
		}
		if (chars != null)
		{
			int pos = 0;
			lc = 0;
			for (int i = 0; i <= len; i++)
			{
				if ((i == len) || (chars[i] == '\n'))
				{
					if (i > pos)
					{
						rs.drawString (px + dx, py + dy + lc * h,
									   caption.substring (pos, i), font, c);
					}
					lc++;
					pos = i + 1;
				}
			}
		}
	}


	public void draw (Object object, boolean asNode, RenderState rs)
	{
		GraphState gs = rs.getRenderGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				draw (getCaption (), FontAdapter.getFont (font), getHorizontalAlignment (),
					  getVerticalAlignment (), color,
					  fillColor, (bits & FILLED_MASK) != 0,
					  (bits & OUTLINED_MASK) != 0, rs);
			}
			else
			{
				draw ((String) gs.checkObject (this, true, Attributes.CAPTION, getCaption ()),
					  FontAdapter.getFont ((FontAdapter) gs.checkObject (this, true, Attributes.FONT, font)),
					  gs.checkInt (this, true, Attributes.HORIZONTAL_ALIGNMENT, getHorizontalAlignment ()),
					  gs.checkInt (this, true, Attributes.VERTICAL_ALIGNMENT, getVerticalAlignment ()),
					  (Tuple3f) gs.checkObject (this, true, Attributes.COLOR, color), 
					  (Tuple3f) gs.checkObject (this, true, Attributes.FILL_COLOR, fillColor), 
					  gs.checkBoolean (this, true, Attributes.FILLED, (bits & FILLED_MASK) != 0),
					  gs.checkBoolean (this, true, Attributes.OUTLINED, (bits & OUTLINED_MASK) != 0),
					  rs);
			}
		}
		else
		{
			draw ((String) gs.getObject (object, asNode, Attributes.CAPTION),
				  FontAdapter.getFont ((FontAdapter) gs.getObject (object, asNode, Attributes.FONT)),
				  gs.getInt (object, asNode, Attributes.HORIZONTAL_ALIGNMENT),
				  gs.getInt (object, asNode, Attributes.VERTICAL_ALIGNMENT),
				  (Tuple3f) gs.getObject (object, asNode, Attributes.COLOR), 
				  (Tuple3f) gs.getObject (object, asNode, Attributes.FILL_COLOR), 
				  gs.getBoolean (object, asNode, Attributes.FILLED),
				  gs.getBoolean (object, asNode, Attributes.OUTLINED),
				  rs);
		}
	}


	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
					  Matrix4d t, PickList list)
	{
		GraphState gs = list.getGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				pick (getCaption (), FontAdapter.getFont (font), getHorizontalAlignment (),
					  getVerticalAlignment (),
					  origin, direction, t, list);
			}
			else
			{
				pick ((String) gs.checkObject (this, true, Attributes.CAPTION, getCaption ()),
					  FontAdapter.getFont ((FontAdapter) gs.checkObject (this, true, Attributes.FONT, font)),
					  gs.checkInt (this, true, Attributes.HORIZONTAL_ALIGNMENT, getHorizontalAlignment ()),
					  gs.checkInt (this, true, Attributes.VERTICAL_ALIGNMENT, getVerticalAlignment ()),
					  origin, direction, t, list);
			}
		}
		else
		{
			pick ((String) gs.getObject (object, asNode, Attributes.CAPTION),
				  FontAdapter.getFont ((FontAdapter) gs.getObject (object, asNode, Attributes.FONT)),
				  gs.getInt (object, asNode, Attributes.HORIZONTAL_ALIGNMENT),
				  gs.getInt (object, asNode, Attributes.VERTICAL_ALIGNMENT),
				  origin, direction, t, list);
		}
	}

//	enh:insert $TYPE.addIdentityAccessor (Attributes.SHAPE);
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field color$FIELD;
	public static final NType.Field fillColor$FIELD;
	public static final NType.Field font$FIELD;
	public static final NType.Field outlined$FIELD;
	public static final NType.Field filled$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (TextLabelBase.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((TextLabelBase) o).color = (Color3f) Tuple3fType.COLOR.setObject (((TextLabelBase) o).color, value);
					return;
				case 1:
					((TextLabelBase) o).fillColor = (Color3f) Tuple3fType.COLOR.setObject (((TextLabelBase) o).fillColor, value);
					return;
				case 2:
					((TextLabelBase) o).font = (FontAdapter) FontAdapter.$TYPE.setObject (((TextLabelBase) o).font, value);
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
					return ((TextLabelBase) o).getColor ();
				case 1:
					return ((TextLabelBase) o).getFillColor ();
				case 2:
					return ((TextLabelBase) o).getFont ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (TextLabelBase.class);
		$TYPE.addManagedField (color$FIELD = new _Field ("color", _Field.PROTECTED  | _Field.SCO, Tuple3fType.COLOR, null, 0));
		$TYPE.addManagedField (fillColor$FIELD = new _Field ("fillColor", _Field.PROTECTED  | _Field.SCO, Tuple3fType.COLOR, null, 1));
		$TYPE.addManagedField (font$FIELD = new _Field ("font", _Field.PROTECTED  | _Field.SCO, FontAdapter.$TYPE, null, 2));
		$TYPE.addManagedField (outlined$FIELD = new NType.BitField ($TYPE, "outlined", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, OUTLINED_MASK));
		$TYPE.addManagedField (filled$FIELD = new NType.BitField ($TYPE, "filled", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, FILLED_MASK));
		$TYPE.declareFieldAttribute (color$FIELD, Attributes.COLOR);
		$TYPE.declareFieldAttribute (fillColor$FIELD, Attributes.FILL_COLOR);
		$TYPE.declareFieldAttribute (font$FIELD, Attributes.FONT);
		$TYPE.declareFieldAttribute (outlined$FIELD, Attributes.OUTLINED);
		$TYPE.declareFieldAttribute (filled$FIELD, Attributes.FILLED);
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
		$TYPE.validate ();
	}

	public Color3f getColor ()
	{
		return color;
	}

	public void setColor (Color3f value)
	{
		color$FIELD.setObject (this, value);
	}

	public Color3f getFillColor ()
	{
		return fillColor;
	}

	public void setFillColor (Color3f value)
	{
		fillColor$FIELD.setObject (this, value);
	}

	public FontAdapter getFont ()
	{
		return font;
	}

	public void setFont (FontAdapter value)
	{
		font$FIELD.setObject (this, value);
	}

	public boolean isOutlined ()
	{
		return (bits & OUTLINED_MASK) != 0;
	}

	public void setOutlined (boolean v)
	{
		if (v) bits |= OUTLINED_MASK; else bits &= ~OUTLINED_MASK;
	}

	public boolean isFilled ()
	{
		return (bits & FILLED_MASK) != 0;
	}

	public void setFilled (boolean v)
	{
		if (v) bits |= FILLED_MASK; else bits &= ~FILLED_MASK;
	}

//enh:end


}