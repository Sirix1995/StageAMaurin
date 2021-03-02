package de.grogra.imp2d.layout;


import javax.vecmath.Point2d;
import javax.vecmath.Vector2f;


import java.util.Random;

/**
 * A <code>DavidsonHarelLayout</code> computes a graph layout based on the
 * force based DavidsonHarel model.
 * 
 * @date 26.03.2006
 */
public class DavidsonHarelLayout extends ForceBasedLayout
{	
	//Variables
	
	//enh:sco
	
	private double attractionFactor = 5;	
	//enh:field
	
	private double repulsionFactor = 0.5;
	//enh:field

	
	/** Calculating the inital positions of the nodes
	 */
	@Override
	protected void setRandomPosition(Node nodeTemp, Random rnd)
	{	
		nodeTemp.x = (float) rnd.nextFloat();
		nodeTemp.y = (float) rnd.nextFloat();
	}//void setRandomPosition
	
	/** Computes the repelling force of two nodes s and t
	 * 
	 */
	@Override
	protected void computeForce(Node s, Node t, Vector2f force) {
		//force.set(repellingForce(s, t));

		double distance = t.distance(s);
		if (distance == 0)
			distance = 0.001;
		float rep = (float)(repulsionFactor / distance);
		
		force.sub(t, s);
		if (distance > 0)
		{
			force.scale((float)(1 / distance));
		}
		force.scale(rep);
	}//void computeForce Nodes

	/** Computes the attraction force of two nodes connected
	 * with Edge e
	 */
	@Override
	protected void computeForce(Edge e, Vector2f force) {
		double distance = e.source.distance(e.target);
		if (distance == 0)
			distance = 0.001;
		float attr = (float)(attractionFactor * Math.log(distance * distance));
		force.sub(e.source, e.target);
		if (distance > 0)
		{
			force.scale((float)(1 / distance));
		}
		force.scale(attr);
				
	}//void computeForce Edge
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field attractionFactor$FIELD;
	public static final Type.Field repulsionFactor$FIELD;

	public static class Type extends ForceBasedLayout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (DavidsonHarelLayout representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, ForceBasedLayout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = ForceBasedLayout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = ForceBasedLayout.Type.FIELD_COUNT + 2;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((DavidsonHarelLayout) o).attractionFactor = (double) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((DavidsonHarelLayout) o).repulsionFactor = (double) value;
					return;
			}
			super.setDouble (o, id, value);
		}

		@Override
		protected double getDouble (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((DavidsonHarelLayout) o).attractionFactor;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((DavidsonHarelLayout) o).repulsionFactor;
			}
			return super.getDouble (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new DavidsonHarelLayout ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (DavidsonHarelLayout.class);
		attractionFactor$FIELD = Type._addManagedField ($TYPE, "attractionFactor", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 0);
		repulsionFactor$FIELD = Type._addManagedField ($TYPE, "repulsionFactor", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

//enh:end
}//class DavidsonHarelLayout
