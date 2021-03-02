// This file has been automatically generated
// from /home/mhenke/Dokumente/src/eclipse/groimp/IMP-2D/src/de/grogra/imp2d/objects/ArcType.sco.

package de.grogra.imp2d.objects;

import java.awt.geom.*;
import de.grogra.persistence.*;
import de.grogra.util.*;

public class ArcType extends SCOType
{
	public static final ArcType $TYPE;

private static final EnumerationType ARC_TYPE
	= new EnumerationType ("arcType", de.grogra.imp2d.IMP2D.I18N, 3);
	public static final Field width$FIELD;
	public static final Field height$FIELD;
	public static final Field start$FIELD;
	public static final Field extent$FIELD;
	public static final Field arcType$FIELD;

	static
	{
		$TYPE = new ArcType (Arc2D.Float.class, SCOType.$TYPE);
		width$FIELD = $TYPE.addManagedField ("width",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 0);
		width$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		height$FIELD = $TYPE.addManagedField ("height",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 1);
		height$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		start$FIELD = $TYPE.addManagedField ("start",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 2);
		start$FIELD.setQuantity (de.grogra.util.Quantity.ANGLE);
		extent$FIELD = $TYPE.addManagedField ("extent",  0 | Field.SCO, de.grogra.reflect.Type.FLOAT, null, SCOType.FIELD_COUNT + 3);
		extent$FIELD.setQuantity (de.grogra.util.Quantity.ANGLE);
		arcType$FIELD = $TYPE.addManagedField ("arcType",  0 | Field.SCO, ARC_TYPE, null, SCOType.FIELD_COUNT + 4);
		$TYPE.validate ();
	}

	public ArcType (Class c, de.grogra.persistence.SCOType supertype)
	{
		super (c, supertype);
	}

	public ArcType (Arc2D.Float representative, de.grogra.persistence.SCOType supertype)
	{
		super (representative, supertype);
	}

	protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 5;

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 4:
					((Arc2D.Float) o).setArcType ((int) value);
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 4:
					return ((Arc2D.Float) o).getArcType ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case SCOType.FIELD_COUNT + 0:
					setWidth (((Arc2D.Float) o), value);;
					return;
				case SCOType.FIELD_COUNT + 1:
					setHeight (((Arc2D.Float) o), value);;
					return;
				case SCOType.FIELD_COUNT + 2:
					((Arc2D.Float) o).start = value * 57.29578f;
					return;
				case SCOType.FIELD_COUNT + 3:
					((Arc2D.Float) o).extent = value * 57.29578f;
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
					return ((Arc2D.Float) o).width;
				case SCOType.FIELD_COUNT + 1:
					return ((Arc2D.Float) o).height;
				case SCOType.FIELD_COUNT + 2:
					return ((Arc2D.Float) o).start * 0.017453293f;
				case SCOType.FIELD_COUNT + 3:
					return ((Arc2D.Float) o).extent * 0.017453293f;
			}
			return super.getFloat (o, id);
		}


static void setWidth (Arc2D.Float s, float f)
{
	s.width = f;
	s.x = -0.5f * f;
}

static void setHeight (Arc2D.Float s, float f)
{
	s.height = f;
	s.y = -0.5f * f;
}
}
