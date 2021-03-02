// This file has been automatically generated
// from /home/mhenke/Dokumente/src/eclipse/groimp/IMP-2D/src/de/grogra/imp2d/objects/RoundRectangleType.sco.

package de.grogra.imp2d.objects;

import java.awt.geom.*;
import de.grogra.persistence.*;

public class RoundRectangleType extends SCOType
{
	public static final RoundRectangleType $TYPE;

	public static final Field width$FIELD;
	public static final Field height$FIELD;
	public static final Field arcwidth$FIELD;
	public static final Field archeight$FIELD;

	static
	{
		$TYPE = new RoundRectangleType (RoundRectangle2D.Float.class, SCOType.$TYPE);
		width$FIELD = $TYPE.addManagedField ("width",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 0);
		width$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		height$FIELD = $TYPE.addManagedField ("height",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 1);
		height$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		arcwidth$FIELD = $TYPE.addManagedField ("arcwidth",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 2);
		arcwidth$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		archeight$FIELD = $TYPE.addManagedField ("archeight",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 3);
		archeight$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		$TYPE.validate ();
	}

	public RoundRectangleType (Class c, de.grogra.persistence.SCOType supertype)
	{
		super (c, supertype);
	}

	public RoundRectangleType (RoundRectangle2D.Float representative, de.grogra.persistence.SCOType supertype)
	{
		super (representative, supertype);
	}

	protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 4;

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 0:
					setWidth (((RoundRectangle2D.Float) o), value);;
					return;
				case SCOType.FIELD_COUNT + 1:
					setHeight (((RoundRectangle2D.Float) o), value);;
					return;
				case SCOType.FIELD_COUNT + 2:
					((RoundRectangle2D.Float) o).arcwidth = (float) value;
					return;
				case SCOType.FIELD_COUNT + 3:
					((RoundRectangle2D.Float) o).archeight = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 0:
					return ((RoundRectangle2D.Float) o).width;
				case SCOType.FIELD_COUNT + 1:
					return ((RoundRectangle2D.Float) o).height;
				case SCOType.FIELD_COUNT + 2:
					return ((RoundRectangle2D.Float) o).arcwidth;
				case SCOType.FIELD_COUNT + 3:
					return ((RoundRectangle2D.Float) o).archeight;
			}
			return super.getFloat (o, id);
		}


static void setWidth (RoundRectangle2D.Float s, float f)
{
	s.width = f;
	s.x = -0.5f * f;
}

static void setHeight (RoundRectangle2D.Float s, float f)
{
	s.height = f;
	s.y = -0.5f * f;
}
}
