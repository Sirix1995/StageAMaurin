package de.grogra.imp2d.objects;

import java.awt.geom.*;
import de.grogra.persistence.*;
begin
RoundRectangle2D.Float
SCOType
float	width	quantity=LENGTH setexpr={setWidth ($, value);}
float	height	quantity=LENGTH setexpr={setHeight ($, value);}
float	arcwidth	quantity=LENGTH
float	archeight	quantity=LENGTH
end

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
