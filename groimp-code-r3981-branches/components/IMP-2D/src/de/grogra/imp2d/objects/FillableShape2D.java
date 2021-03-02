
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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.vecmath.Color3f;
import javax.vecmath.Tuple3f;

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.EdgeImpl;
import de.grogra.imp.objects.FontAdapter;
import de.grogra.imp2d.AWTCanvas2D;
import de.grogra.imp2d.AWTCanvas2DIF;
import de.grogra.math.Tuple3fType;

public abstract class FillableShape2D extends Shape2DBase
{
	private static final Color3f DEFAULT_FILL_COLOR = new Color3f (0.7f, 0.7f, 0);

	
	protected static final int OUTLINED_MASK = 1 << Shape2DBase.USED_BITS;
	protected static final int FILLED_MASK = 2 << Shape2DBase.USED_BITS;

	public static final int USED_BITS = Shape2DBase.USED_BITS + 2;

	Color3f fillColor = new Color3f (DEFAULT_FILL_COLOR);
	//enh:field type=Tuple3fType.COLOR attr=Attributes.FILL_COLOR getter setter
	
	String caption = null;
	//enh:field attr=Attributes.CAPTION getter setter

	FontAdapter font = null;
	//enh:field type=FontAdapter.$TYPE attr=Attributes.FONT getter setter
	
	float verticalAlignment = 0;
	//enh:field attr=Attributes.CONTINUOUS_VERTICAL_ALIGNMENT getter setter

	int horizontalAlignment = Attributes.H_ALIGN_LEFT;
	//enh:field type=Attributes.HORIZONTAL_ALIGNMENT_TYPE attr=Attributes.HORIZONTAL_ALIGNMENT getter setter

	// boolean outlined
	//enh:field type=bits(OUTLINED_MASK) attr=Attributes.OUTLINED getter setter

	// boolean filled
	//enh:field type=bits(FILLED_MASK) attr=Attributes.FILLED getter setter

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field fillColor$FIELD;
	public static final NType.Field caption$FIELD;
	public static final NType.Field font$FIELD;
	public static final NType.Field verticalAlignment$FIELD;
	public static final NType.Field horizontalAlignment$FIELD;
	public static final NType.Field outlined$FIELD;
	public static final NType.Field filled$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (FillableShape2D.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 4:
					((FillableShape2D) o).horizontalAlignment = value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 4:
					return ((FillableShape2D) o).getHorizontalAlignment ();
			}
			return super.getInt (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 3:
					((FillableShape2D) o).verticalAlignment = value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 3:
					return ((FillableShape2D) o).getVerticalAlignment ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((FillableShape2D) o).fillColor = (Color3f) Tuple3fType.COLOR.setObject (((FillableShape2D) o).fillColor, value);
					return;
				case 1:
					((FillableShape2D) o).caption = (String) value;
					return;
				case 2:
					((FillableShape2D) o).font = (FontAdapter) FontAdapter.$TYPE.setObject (((FillableShape2D) o).font, value);
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
					return ((FillableShape2D) o).getFillColor ();
				case 1:
					return ((FillableShape2D) o).getCaption ();
				case 2:
					return ((FillableShape2D) o).getFont ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (FillableShape2D.class);
		$TYPE.addManagedField (fillColor$FIELD = new _Field ("fillColor", 0 | _Field.SCO, Tuple3fType.COLOR, null, 0));
		$TYPE.addManagedField (caption$FIELD = new _Field ("caption", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 1));
		$TYPE.addManagedField (font$FIELD = new _Field ("font", 0 | _Field.SCO, FontAdapter.$TYPE, null, 2));
		$TYPE.addManagedField (verticalAlignment$FIELD = new _Field ("verticalAlignment", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 3));
		$TYPE.addManagedField (horizontalAlignment$FIELD = new _Field ("horizontalAlignment", 0 | _Field.SCO, Attributes.HORIZONTAL_ALIGNMENT_TYPE, null, 4));
		$TYPE.addManagedField (outlined$FIELD = new NType.BitField ($TYPE, "outlined", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, OUTLINED_MASK));
		$TYPE.addManagedField (filled$FIELD = new NType.BitField ($TYPE, "filled", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, FILLED_MASK));
		$TYPE.declareFieldAttribute (fillColor$FIELD, Attributes.FILL_COLOR);
		$TYPE.declareFieldAttribute (caption$FIELD, Attributes.CAPTION);
		$TYPE.declareFieldAttribute (font$FIELD, Attributes.FONT);
		$TYPE.declareFieldAttribute (verticalAlignment$FIELD, Attributes.CONTINUOUS_VERTICAL_ALIGNMENT);
		$TYPE.declareFieldAttribute (horizontalAlignment$FIELD, Attributes.HORIZONTAL_ALIGNMENT);
		$TYPE.declareFieldAttribute (outlined$FIELD, Attributes.OUTLINED);
		$TYPE.declareFieldAttribute (filled$FIELD, Attributes.FILLED);
		$TYPE.validate ();
	}

	public int getHorizontalAlignment ()
	{
		return horizontalAlignment;
	}

	public void setHorizontalAlignment (int value)
	{
		this.horizontalAlignment = value;
	}

	public float getVerticalAlignment ()
	{
		return verticalAlignment;
	}

	public void setVerticalAlignment (float value)
	{
		this.verticalAlignment = value;
	}

	public Color3f getFillColor ()
	{
		return fillColor;
	}

	public void setFillColor (Color3f value)
	{
		fillColor$FIELD.setObject (this, value);
	}

	public String getCaption ()
	{
		return caption;
	}

	public void setCaption (String value)
	{
		caption$FIELD.setObject (this, value);
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


	public FillableShape2D ()
	{
		super ();
		setOutlined (true);
		setFilled (true);
	}


	private transient Color awtFillColor;

	@Override
	protected void drawShape (Object object, boolean asNode, AWTCanvas2DIF canvas, Shape s)
	{
		boolean filled, outlined;
		Tuple3f fc = null;
		String text;
		FontAdapter fa = null;
		float va = 0;
		int ha = 0;
		GraphState gs = canvas.getRenderGraphState ();
		if (object == this)
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				fc = fillColor;
				filled = (bits & FILLED_MASK) != 0;
				outlined = (bits & OUTLINED_MASK) != 0;
				text = caption;
				fa = font;
				va = verticalAlignment;
				ha = horizontalAlignment;
			}
			else
			{
				filled = gs.checkBoolean
					(this, true, Attributes.FILLED, (bits & FILLED_MASK) != 0);
				outlined = gs.checkBoolean
					(this, true, Attributes.OUTLINED, (bits & OUTLINED_MASK) != 0);
				fc = gs.checkObject (this, true, Attributes.FILL_COLOR, fillColor);
				text = gs.checkObject (this, true, Attributes.CAPTION, caption);
				if (text != null)
				{
					fa = gs.checkObject (this, true, Attributes.FONT, font);
					va = gs.checkFloat (this, true,
										Attributes.CONTINUOUS_VERTICAL_ALIGNMENT,
										verticalAlignment);
					ha = gs.checkInt (this, true,
									  Attributes.HORIZONTAL_ALIGNMENT,
									  horizontalAlignment);
				}
			}
		}
		else
		{
			filled = gs.getBooleanDefault (object, asNode, Attributes.FILLED, (bits & FILLED_MASK) != 0);
			outlined = gs.getBooleanDefault (object, asNode, Attributes.OUTLINED, (bits & OUTLINED_MASK) != 0);
			fc = gs.getObjectDefault (object, asNode, Attributes.FILL_COLOR, fillColor);
			text = gs.getObjectDefault (object, asNode, Attributes.CAPTION, caption);
			if (text != null)
			{
				fa = gs.getObjectDefault (object, asNode, Attributes.FONT, font);
				va = gs.getFloatDefault
					(object, asNode, Attributes.CONTINUOUS_VERTICAL_ALIGNMENT, verticalAlignment);
				ha = gs.getIntDefault
					(object, asNode, Attributes.HORIZONTAL_ALIGNMENT, horizontalAlignment);
			}
		}
		Graphics2D g = canvas.getGraphics ();
		Color oldColor = g.getColor ();
		if (filled)
		{
			g.setColor (awtFillColor = AWTCanvas2D.getColor (fc, awtFillColor));
			g.fill (s);
			g.setColor (oldColor);
		}
		if (outlined)
		{
			g.draw (s);
		}
		if (text != null)
		{
			g.scale (0.01, -0.01);
			Font f = FontAdapter.getFont (fa);
			g.setFont (f);
			FontMetrics fm = g.getFontMetrics ();
			char[] chars = text.toCharArray ();
			int lineCount = 1, width = 0, lineHeight;
			int len = chars.length, pos = 0;
			for (int i = 0; i <= len; i++)
			{
				if ((i == len) || (chars[i] == '\n'))
				{
					if (i > pos)
					{
						width = Math.max (width, fm.charsWidth (chars, pos, i - pos));
					}
					if (i < len - 1)
					{
						lineCount++;
					}
					pos = i + 1;
				}
			}
			lineHeight = fm.getHeight ();

			Rectangle2D sb = s.getBounds2D ();
			int dy = (int) (0.5f * (
				sb.getCenterY () * -200
				+ lineHeight * (2 - lineCount)
				- (sb.getHeight () * 100 + lineHeight * (lineCount + 1)) * va));
			int dx = (int) (sb.getCenterX () * 100);
			if(object instanceof EdgeImpl) {
//				System.out.println ("fshape  "+ canvas.getPool ().getFloatArray (0, 3)[1]+"  "+canvas.getPool ().getFloatArray (0, 3)[2]);				
				float[] d_pool =  canvas.getPool ().getFloatArray (0, 3);
				//dx += (int)(d_pool[1]*35);
				//dy += (int)(-1*d_pool[2]*35);
				double balance = Math.abs((d_pool[1]*d_pool[1]-d_pool[2]*d_pool[2])/(double) (d_pool[1]*d_pool[1]+d_pool[2]*d_pool[2]));//Math.sqrt
				balance = Math.acos(balance)/1.5;
				dx += (int)(d_pool[1]*55/(2-balance));
				dy += (int)(-1*d_pool[2]*60/(2-balance));
			}
			
			
			
			pos = 0;
			lineCount = 0;
			if (!filled)
			{
				g.setColor (awtFillColor = AWTCanvas2D.getColor (fc, awtFillColor));
			}
			//fetch slot nodes, which are GeneralPath objects
			if (s instanceof GeneralPath) {
				//TODO: turn the colour into an attribute of the object
				g.setColor (new Color(200,255,255));
			}
			for (int i = 0; i <= len; i++)
			{
				if ((i == len) || (chars[i] == '\n'))
				{
					if (i > pos)
					{
						int x;
						switch (ha)
						{
							case Attributes.H_ALIGN_CENTER:
								x = fm.charsWidth (chars, pos, i - pos) >> 1;
								break;
							case Attributes.H_ALIGN_LEFT:
								x = width >> 1;
								break;
							default:
								x = fm.charsWidth (chars, pos, i - pos) - (width >> 1);
								break;
						}
						if(s instanceof GeneralPath) {
							if(canvas.isShowSlotLabels () || object instanceof EdgeImpl)
								g.drawChars (chars, pos, i - pos, dx-5, dy-4);
						} else {
							g.drawChars (chars, pos, i - pos, dx - x, dy + lineHeight * lineCount);
						}
					}
					lineCount++;
					pos = i + 1;
				}
			}
			g.setColor (oldColor);
		}
	}

}