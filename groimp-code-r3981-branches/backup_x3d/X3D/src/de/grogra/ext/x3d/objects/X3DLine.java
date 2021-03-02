package de.grogra.ext.x3d.objects;

import javax.vecmath.Vector3d;
import de.grogra.graph.Graph;
import de.grogra.imp3d.objects.Cylinder;
import de.grogra.imp3d.objects.Line;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Transaction;
import de.grogra.ext.x3d.Attributes;
import de.grogra.ext.x3d.Util;

/**
 * This class is a groimp node and  represents a 
 * single line in a x3d indexed line set.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DLine extends Line {

	protected Cylinder c;
	//TODO: calculate radius of cylinders in dependency of view distance
	private float cRadius = 0.1f;
	
	protected boolean drawAsCylinder;
	//enh:field attr=Attributes.DRAW_AS_CYLINDER getter setter
	
	/**
	 * Creates a new line node. The line's start and end point is at (0, 0, 0).
	 * The id of the new line is 0.
	 */
	public X3DLine() {
		this(0, 0, 0, 0, 0, 0, 0);
	}
	
	/**
	 * Creates a new line node from start point (x, y, z) to another point.
	 * The end point is given relative to the start point as a vector.
	 * @param x
	 * @param y
	 * @param z
	 * @param dx
	 * @param dy
	 * @param dz
	 * @param id
	 */
	public X3DLine(float x, float y, float z, float dx, float dy, float dz, int id) {
		super(x, y, z, dx, dy, dz);
		this.setName("X3DLine." + id);
			
		Vector3d dirVec = new Vector3d(dx, dy, dz);
		c = new Cylinder((float) dirVec.length(), cRadius);
		this.setTransforming(true);
		
		Vector3d upVec = new Vector3d(0, 0, 1);

		c.setTransform(Util.vectorsToTransMatrix(dirVec, upVec));
		
		c.setName("X3DLineCylinder." + id);
	}
	
	public void fieldModified(PersistenceField field, int[] indices, Transaction t) {
		super.fieldModified(field, indices, t);
		if (!Transaction.isApplying(t))	{
			if (field.overlaps(indices, drawAsCylinder$FIELD, null)) {
				if (drawAsCylinder) {
					this.addEdgeBitsTo(c, Graph.SUCCESSOR_EDGE, null);
				}
				else {
					this.removeEdgeBitsTo(c, Graph.SUCCESSOR_EDGE, null);
				}
			}
		}
	}
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field drawAsCylinder$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (X3DLine.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 0:
					((X3DLine) o).drawAsCylinder = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 0:
					return ((X3DLine) o).isDrawAsCylinder ();
			}
			return super.getBoolean (o);
		}
	}

	static
	{
		$TYPE = new NType (new X3DLine ());
		$TYPE.addManagedField (drawAsCylinder$FIELD = new _Field ("drawAsCylinder", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 0));
		$TYPE.declareFieldAttribute (drawAsCylinder$FIELD, Attributes.DRAW_AS_CYLINDER);
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
		return new X3DLine ();
	}

	public boolean isDrawAsCylinder ()
	{
		return drawAsCylinder;
	}

	public void setDrawAsCylinder (boolean value)
	{
		this.drawAsCylinder = (boolean) value;
	}

//enh:end
}
