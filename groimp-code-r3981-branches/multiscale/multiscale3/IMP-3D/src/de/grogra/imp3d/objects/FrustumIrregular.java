/**
 * 
 */
package de.grogra.imp3d.objects;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import de.grogra.graph.GraphState;
import de.grogra.imp.PickList;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;

/**
 * This class represents a frustum with a variable number of sectors for the top and bottom surface.
 * This radii for each sector is variable.
 * 
 * Primarily meant to represent a layer of a tree crown but can also be used generally as a 3D object.
 * 
 * @author yong
 * @since 30.7.2013
 */
public class FrustumIrregular extends FrustumBase implements Renderable {

	protected int sectorCount;
	
	protected float[] baseRadii;
	
	protected float[] topRadii;
	
	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}
	
	public static final NType $TYPE;
	public static final NType.Field sectorCount$Field;
	public static final NType.Field baseRadii$FIELD;
	public static final NType.Field topRadii$FIELD;
	public static final NType.Field baseOpen$FIELD;
	public static final NType.Field topOpen$FIELD;
	
	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (FrustumIrregular.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 0:
					((FrustumIrregular) o).sectorCount = value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 0:
					return ((FrustumIrregular) o).getSectorCount ();
			}
			return super.getInt (o);
		}
		
		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 1:
					((FrustumIrregular) o).setBaseRadii((float[])value);
					return;
				case 2:
					((FrustumIrregular) o).setTopRadii((float[])value);
					return;
			}
			super.setObject (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 1:
					return ((FrustumIrregular) o).getBaseRadii ();
				case 2:
					return ((FrustumIrregular) o).getTopRadii ();
			}
			return super.getObject (o);
		}
	}
	
	static
	{
		$TYPE = new NType (FrustumIrregular.class);
		$TYPE.addManagedField (sectorCount$Field = new _Field ("sectorCount", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.INT, null, 0));
		$TYPE.addManagedField (baseRadii$FIELD = new _Field ("baseRadii", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.OBJECT, null, 1));
		$TYPE.addManagedField (topRadii$FIELD = new _Field ("topRadii", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.OBJECT, null, 2));
		$TYPE.addManagedField (baseOpen$FIELD = new NType.BitField ($TYPE, "baseOpen", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, BASE_OPEN_MASK));
		$TYPE.addManagedField (topOpen$FIELD = new NType.BitField ($TYPE, "topOpen", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, TOP_OPEN_MASK));
		$TYPE.declareFieldAttribute (sectorCount$Field, Attributes.SECTOR_COUNT);
		$TYPE.declareFieldAttribute (baseRadii$FIELD, Attributes.BASE_RADII);
		$TYPE.declareFieldAttribute (topRadii$FIELD, Attributes.TOP_RADII);
		$TYPE.declareFieldAttribute (baseOpen$FIELD, Attributes.BASE_OPEN);
		$TYPE.declareFieldAttribute (topOpen$FIELD, Attributes.TOP_OPEN);
		initType ();
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
		return new FrustumIrregular ();
	}
	
	@Override
	public void draw(Object object, boolean asNode, RenderState rs) {
		
		GraphState gs = rs.getRenderGraphState ();
		float len = (float) gs.getDouble (object, asNode, Attributes.LENGTH);
		
		rs.drawFrustumIrregular (len, gs.getInt (object, asNode, Attributes.SECTOR_COUNT),
				(float[])gs.getObject (object, asNode, Attributes.BASE_RADII),
				(float[])gs.getObject(object, asNode, Attributes.TOP_RADII),
				!gs.getBoolean (object, asNode, Attributes.BASE_OPEN),
				!gs.getBoolean (object, asNode, Attributes.TOP_OPEN),
				gs.getBoolean (object, asNode, Attributes.SCALE_V) ? len : 1, null, RenderState.CURRENT_HIGHLIGHT, null);
	}

	public FrustumIrregular ()
	{
		this (8, new float[]{1,1,1,1,1,1,1,1}, new float[]{1,1,1,1,1,1,1,1});
	}

	public FrustumIrregular (float len, int sectorCount, float[] baseRadii, float[] topRadii)
	{
		super ();
		setLength (len);
		
		//check for valid sector count
		if(sectorCount <= 0)
			sectorCount = 1;
		
		//init sector count
		this.sectorCount = sectorCount;
		
		
		if((baseRadii == null) || (topRadii == null))
		{
			resetRadii(sectorCount,1);
			return;
		}
		
		if((baseRadii.length < sectorCount) || (topRadii.length < sectorCount))
		{
			resetRadii(sectorCount,1);
			return;
		}
		
		this.baseRadii = new float[baseRadii.length];
		this.topRadii = new float[topRadii.length];
		System.arraycopy(baseRadii, 0, this.baseRadii, 0, baseRadii.length);
		System.arraycopy(topRadii, 0, this.topRadii, 0, topRadii.length);
	}

	public FrustumIrregular (int sectorCount, float[] baseRadii, float[] topRadii)
	{
		super ();
		setLength (length);
		
		//check for valid sector count
		if(sectorCount <= 0)
			sectorCount = 1;
		
		//init sector count
		this.sectorCount = sectorCount;
		
		
		if((baseRadii == null) || (topRadii == null))
		{
			resetRadii(sectorCount,1);
			return;
		}
		
		if((baseRadii.length < sectorCount) || (topRadii.length < sectorCount))
		{
			resetRadii(sectorCount,1);
			return;
		}
		
		this.baseRadii = new float[baseRadii.length];
		this.topRadii = new float[topRadii.length];
		System.arraycopy(baseRadii, 0, this.baseRadii, 0, baseRadii.length);
		System.arraycopy(topRadii, 0, this.topRadii, 0, topRadii.length);
	}

	@Override
	public void pick(Object object, boolean asNode, Point3d origin,
			Vector3d direction, Matrix4d transformation, PickList list) {
		// TODO Auto-generated method stub
		
	}
	
	public int getSectorCount()
	{
		return sectorCount;
	}
	
	public float[] getBaseRadii()
	{
		return baseRadii;
	}
	
	public float[] getTopRadii()
	{
		return topRadii;
	}
	
	public void setSectorCount(int value)
	{
		this.sectorCount = value;
	}
	
	public void setBaseRadii(float[] value)
	{
		this.baseRadii = value.clone();
	}
	
	public void setTopRadii(float[] value)
	{
		this.topRadii = value.clone();
	}
	
	public void setBaseRadii(int index, float value)
	{
		baseRadii[index] = value;
	}
	
	public void setTopRadii(int index, float value)
	{
		topRadii[index] = value;
	}
	
	public boolean isBaseOpen ()
	{
		return (bits & BASE_OPEN_MASK) != 0;
	}

	public void setBaseOpen (boolean v)
	{
		if (v) bits |= BASE_OPEN_MASK; else bits &= ~BASE_OPEN_MASK;
	}

	public boolean isTopOpen ()
	{
		return (bits & TOP_OPEN_MASK) != 0;
	}

	public void setTopOpen (boolean v)
	{
		if (v) bits |= TOP_OPEN_MASK; else bits &= ~TOP_OPEN_MASK;
	}

	public void resetRadii(int sectorCount, float radiusValue)
	{
		this.baseRadii = new float[sectorCount];
		this.topRadii = new float[sectorCount];
		
		for(int i=0; i<sectorCount; ++i)
		{
			baseRadii[i] = radiusValue;
			topRadii[i] = radiusValue;
		}
	}
	
	public float radiusMax()
	{
		float max = 0;
		for(int i=0; i<sectorCount; ++i)
		{
			if(baseRadii[i]>max)
				max=baseRadii[i];
			if(topRadii[i]>max)
				max=topRadii[i];
		}
		return max;
	}
	
	/**
	 * Calculates the estimated surface area of this irregular frustum
	 * 
	 * @return area
	 */
	@Override
	public double getSurfaceArea() {
		double sur = 0;
		
		return sur;
	}

	/**
	 * Calculates the estimated volume of this irregular frustum.
	 * Assumes area of sector changes linearly from bottom to top.
	 * 
	 * @return volume
	 */
	@Override
	public double getVolume() {
		double vol = 0;
		double sectorAngle = 2.0 * Math.PI / (double)sectorCount;
		
		for(int i=0; i<sectorCount; ++i)
		{
//			double a1 = i*sectorAngle;
//			double a2 = (i==sectorCount-1)?0:(i+1)*sectorAngle;
			int i2 = (i==sectorCount-1)?0:(i+1);
			
			//area of top triangle
//			double x1 = topRadii[i] * Math.cos(a1);
//			double y1 = topRadii[i] * Math.sin(a1);
//			double x2 = topRadii[i2] * Math.cos(a2);
//			double y2 = topRadii[i2] * Math.sin(a2);
			double area1 = 0.5 * topRadii[i] * topRadii[i2] * sectorAngle; 
			//area of bottom triangle
//			double x3 = baseRadii[i] * Math.cos(a1);
//			double y3 = baseRadii[i] * Math.sin(a1);
//			double x4 = baseRadii[i2] * Math.cos(a2);
//			double y4 = baseRadii[i2] * Math.sin(a2);
			double area2 = 0.5 * baseRadii[i] * baseRadii[i2] * sectorAngle;
			
			double smallerArea = (area1<=area2)?area1:area2;
			double deltaArea = Math.abs(area1-area2);
			
			vol += ((smallerArea*length) + (deltaArea*length*0.5));
		}
		
		return vol;
	}
}
