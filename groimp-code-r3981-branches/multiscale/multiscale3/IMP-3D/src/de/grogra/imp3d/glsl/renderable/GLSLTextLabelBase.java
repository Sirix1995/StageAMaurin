package de.grogra.imp3d.glsl.renderable;

import java.awt.Font;

import javax.vecmath.Color3f;
import javax.vecmath.Tuple3f;

import de.grogra.imp.objects.FontAdapter;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.objects.Null;
import de.grogra.imp3d.objects.Sphere;
import de.grogra.imp3d.objects.TextLabelBase;

import de.grogra.imp3d.objects.Attributes;
import de.grogra.graph.GraphState;

public abstract class GLSLTextLabelBase extends GLSLBitCache {
	
	protected static final int TOP_MASK = 1 << Null.USED_BITS;
	protected static final int BOTTOM_MASK = 2 << Null.USED_BITS;
	protected static final int VERTICAL_MASK = TOP_MASK | BOTTOM_MASK;
	protected static final int LEFT_MASK = 4 << Null.USED_BITS;
	protected static final int RIGHT_MASK = 8 << Null.USED_BITS;
	protected static final int HORIZONTAL_MASK = LEFT_MASK | RIGHT_MASK;

	protected static final int OUTLINED_MASK = 16 << GLSLBitCache.USED_BITS;
	protected static final int FILLED_MASK = 32 << GLSLBitCache.USED_BITS;

	public static final int USED_BITS = GLSLBitCache.USED_BITS + 6;

	protected Color3f color = new Color3f (1, 1, 1);

	protected Color3f fillColor = new Color3f (0, 0, 1);

	protected Font font = null;

	protected String caption = null;

	public void setAlignment (int horizontalAlignment, int verticalAlignment)
	{
		setHorizontalAlignment (horizontalAlignment);
		setVerticalAlignment (verticalAlignment);
	}

	public void setHorizontalAlignment (int alignment)
	{
		BITMASK &= ~(LEFT_MASK | RIGHT_MASK);
		if (alignment == Attributes.H_ALIGN_LEFT)
		{
			BITMASK |= LEFT_MASK;
		}
		else if (alignment == Attributes.H_ALIGN_RIGHT)
		{
			BITMASK |= RIGHT_MASK;
		}
	}


	public int getHorizontalAlignment ()
	{
		switch (BITMASK & (LEFT_MASK | RIGHT_MASK))
		{
			case LEFT_MASK:
				return Attributes.H_ALIGN_LEFT;
			case RIGHT_MASK:
				return Attributes.H_ALIGN_RIGHT;
			default:
				return Attributes.H_ALIGN_CENTER;
		}
	}
	public void setVerticalAlignment (int alignment)
	{
		BITMASK &= ~(TOP_MASK | BOTTOM_MASK);
		if (alignment == Attributes.V_ALIGN_TOP)
		{
			BITMASK |= TOP_MASK;
		}
		else if (alignment == Attributes.V_ALIGN_BOTTOM)
		{
			BITMASK |= BOTTOM_MASK;
		}
	}

	public int getVerticalAlignment ()
	{
		switch (BITMASK & (TOP_MASK | BOTTOM_MASK))
		{
			case TOP_MASK:
				return Attributes.V_ALIGN_TOP;
			case BOTTOM_MASK:
				return Attributes.V_ALIGN_BOTTOM;
			default:
				return Attributes.V_ALIGN_CENTER;
		}
	}

	/*


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


	 */
	
	@Override
	public void updateInstanceIndirect(Object state, boolean asNode, GraphState gs) {
//		this.radius = gs.getFloat (state, asNode, de.grogra.imp.objects.Attributes.RADIUS);		
	}

	@Override
	protected void updateInstanceByInstancing(Renderable reference,
			GraphState gs) {
		assert (reference instanceof TextLabelBase);
		TextLabelBase ref = (TextLabelBase) reference;
//		this.radius = gs.checkFloat (reference, true, de.grogra.imp.objects.Attributes.RADIUS, ref.getRadius());
	}

	
	@Override
	protected void updateInstanceDirect(Renderable reference) {
		assert (reference instanceof TextLabelBase);
		TextLabelBase ref = (TextLabelBase) reference;
		// will is handled by subclasses
//		caption = ref.getCaption ();
		font = FontAdapter.getFont (ref.getFont()); 
		getHorizontalAlignment ();
		getVerticalAlignment ();
		color = ref.getColor();
		fillColor = ref.getFillColor();
		setBool(FILLED_MASK, ref.isFilled());
		setBool(OUTLINED_MASK, ref.isOutlined());
	}

	@Override
	public void draw(OpenGLState glState, RenderState rs) {
		TextLabelBase.draw (caption, font, getHorizontalAlignment (),
				  getVerticalAlignment (), color,
				  fillColor, getBool(FILLED_MASK),
				  getBool(OUTLINED_MASK), rs);
	}
}
