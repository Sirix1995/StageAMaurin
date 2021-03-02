package de.grogra.imp2d.objects;

import java.awt.geom.*;
import de.grogra.persistence.*;
begin
Ellipse2D.Float
SCOType
float	width	quantity=LENGTH setexpr={setWidth ($, value);}
float	height	quantity=LENGTH setexpr={setHeight ($, value);}
end

static void setWidth (Ellipse2D.Float s, float f)
{
	s.width = f;
	s.x = -0.5f * f;
}

static void setHeight (Ellipse2D.Float s, float f)
{
	s.height = f;
	s.y = -0.5f * f;
}
