package de.grogra.imp3d.objects;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import de.grogra.graph.GraphState;
import de.grogra.imp.PickList;
import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;

public class PrismRectangular extends ShadedNull implements Pickable, Renderable {

	protected float y;
	protected float xPos;
	protected float xNeg;
	protected float zPos;
	protected float zNeg;
	
	public static final NType.Field y$Field;
	public static final NType.Field xPos$FIELD;
	public static final NType.Field xNeg$FIELD;
	public static final NType.Field zPos$FIELD;
	public static final NType.Field zNeg$FIELD;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6863702261193185103L;
	
	public static final NType $TYPE;
	
	static
	{
		$TYPE = new NType (PrismRectangular.class);
		$TYPE.addManagedField (y$Field = new _Field ("y", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (xPos$FIELD = new _Field ("xPos", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (xNeg$FIELD = new _Field ("xNeg", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (zPos$FIELD = new _Field ("zPos", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 3));
		$TYPE.addManagedField (zNeg$FIELD = new _Field ("zNeg", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 4));
		$TYPE.declareFieldAttribute (y$Field, Attributes.Y);
		$TYPE.declareFieldAttribute (xPos$FIELD, Attributes.XPOS);
		$TYPE.declareFieldAttribute (xNeg$FIELD, Attributes.XNEG);
		$TYPE.declareFieldAttribute (zPos$FIELD, Attributes.ZPOS);
		$TYPE.declareFieldAttribute (zNeg$FIELD, Attributes.ZNEG);
		initType ();
		$TYPE.validate ();
	}
	
	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (FrustumIrregular.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((PrismRectangular) o).y = value;
					return;
				case 1:
					((PrismRectangular) o).xPos = value;
					return;
				case 2:
					((PrismRectangular) o).xNeg = value;
					return;
				case 3:
					((PrismRectangular) o).zPos = value;
					return;
				case 4:
					((PrismRectangular) o).zNeg = value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 0:
					return ((PrismRectangular) o).y;
				case 1:
					return ((PrismRectangular) o).xPos;
				case 2:
					return ((PrismRectangular) o).xNeg;
				case 3:
					return ((PrismRectangular) o).zPos;
				case 4:
					return ((PrismRectangular) o).zNeg;
			}
			return super.getFloat (o);
		}
	}
	
	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}
	
	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new PrismRectangular ();
	}
	
	public PrismRectangular ()
	{
		this (1,1,1,1,1);
	}

	public PrismRectangular (float y, float xPos, float xNeg, float zPos, float zNeg)
	{
		super ();
		this.y = y;
		this.xPos = xPos;
		this.xNeg = xNeg;
		this.zPos = zPos;
		this.zNeg = zNeg;
	}
	
	@Override
	public void draw(Object object, boolean asNode, RenderState rs) {
		
		GraphState gs = rs.getRenderGraphState ();
		
		rs.drawPrismRectangular(gs.getFloat(object, asNode, Attributes.Y), 
				gs.getFloat(object, asNode, Attributes.XPOS), 
				gs.getFloat(object, asNode, Attributes.XNEG), 
				gs.getFloat(object, asNode, Attributes.ZPOS), 
				gs.getFloat(object, asNode, Attributes.ZNEG),
				RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe (),
				null
				);
	}

	@Override
	public void pick(Object object, boolean asNode, Point3d origin,
			Vector3d direction, Matrix4d transformation, PickList list) {
		// TODO Auto-generated method stub
		
	}
	
	
}


