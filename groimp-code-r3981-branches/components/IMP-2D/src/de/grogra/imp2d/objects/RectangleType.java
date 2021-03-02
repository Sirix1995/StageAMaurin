// This file has been automatically generated
// from /home/mhenke/Dokumente/src/eclipse/groimp/IMP-2D/src/de/grogra/imp2d/objects/RectangleType.sco.

package de.grogra.imp2d.objects;

import java.awt.geom.*;
import de.grogra.persistence.*;

public class RectangleType extends SCOType
{
	public static final RectangleType $TYPE;

	public static final Field width$FIELD;
	public static final Field height$FIELD;

	static
	{
		$TYPE = new RectangleType (Rectangle2D.Float.class, SCOType.$TYPE);
		width$FIELD = $TYPE.addManagedField ("width",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 0);
		width$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		height$FIELD = $TYPE.addManagedField ("height",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 1);
		height$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		$TYPE.validate ();
	}

	public RectangleType (Class c, de.grogra.persistence.SCOType supertype)
	{
		super (c, supertype);
	}

	public RectangleType (Rectangle2D.Float representative, de.grogra.persistence.SCOType supertype)
	{
		super (representative, supertype);
	}

	protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 2;

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 0:
					setWidth (((Rectangle2D.Float) o), value);;
					return;
				case SCOType.FIELD_COUNT + 1:
					setHeight (((Rectangle2D.Float) o), value);;
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
					return ((Rectangle2D.Float) o).width;
				case SCOType.FIELD_COUNT + 1:
					return ((Rectangle2D.Float) o).height;
			}
			return super.getFloat (o, id);
		}


static void setWidth (Rectangle2D.Float s, float f)
{
	s.width = f;
	s.x = -0.5f * f;
}

static void setHeight (Rectangle2D.Float s, float f)
{
	s.height = f;
	s.y = -0.5f * f;
}
}
