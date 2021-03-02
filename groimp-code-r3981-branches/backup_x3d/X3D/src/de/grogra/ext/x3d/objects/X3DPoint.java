package de.grogra.ext.x3d.objects;

import de.grogra.graph.Graph;
import de.grogra.imp3d.objects.Point;
import de.grogra.imp3d.objects.Sphere;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.Transaction;
import de.grogra.ext.x3d.Attributes;

/**
 * This class is a groimp node and  represents a 
 * single point in a x3d point set.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public class X3DPoint extends Point {
	
	protected Sphere s;
	//TODO: calculate size of spheres in dependency of view distance
	private float sRadius = 0.1f;
	
	protected boolean drawAsSphere;
	//enh:field attr=Attributes.DRAW_AS_SPHERE getter setter
	
	/**
	 * Creates a new single point with id 0.
	 */
	public X3DPoint() {
		this(0);
	}
	
	/**
	 * Creates a new single point. The name is "X3DPoint.<id>".
	 * @param id
	 */
	public X3DPoint(int id) {
		this.setName("X3DPoint." + id);
		s = new Sphere(sRadius);
		s.setName("X3DPointSphere." + id);
	}
	
	public void setColor(float r, float g, float b) {
		super.setColor(r, g, b);
		s.setColor(r, g, b);
	}
	
	public void fieldModified(PersistenceField field, int[] indices, Transaction t) {
		super.fieldModified(field, indices, t);
		if (!Transaction.isApplying(t))	{
			if (field.overlaps(indices, drawAsSphere$FIELD, null)) {
				if (drawAsSphere) {
					this.addEdgeBitsTo(s, Graph.SUCCESSOR_EDGE, null);
				}
				else {
					this.removeEdgeBitsTo(s, Graph.SUCCESSOR_EDGE, null);
				}
			}
			else if (field.overlaps(indices, color$FIELD, null)) {
				s.setColor(getColor().x, getColor().y, getColor().z);
			}
		}
	}
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field drawAsSphere$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (X3DPoint.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 0:
					((X3DPoint) o).drawAsSphere = (boolean) value;
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
					return ((X3DPoint) o).isDrawAsSphere ();
			}
			return super.getBoolean (o);
		}
	}

	static
	{
		$TYPE = new NType (new X3DPoint ());
		$TYPE.addManagedField (drawAsSphere$FIELD = new _Field ("drawAsSphere", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 0));
		$TYPE.declareFieldAttribute (drawAsSphere$FIELD, Attributes.DRAW_AS_SPHERE);
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
		return new X3DPoint ();
	}

	public boolean isDrawAsSphere ()
	{
		return drawAsSphere;
	}

	public void setDrawAsSphere (boolean value)
	{
		this.drawAsSphere = (boolean) value;
	}

//enh:end
}
