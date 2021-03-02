package de.grogra.imp2d.objects;

import java.awt.geom.*;
import de.grogra.persistence.*;
import de.grogra.util.*;
begin
Arc2D.Float
SCOType

>private static final EnumerationType ARC_TYPE
>	= new EnumerationType ("arcType", de.grogra.imp2d.IMP2D.I18N, 3);

float	width	quantity=LENGTH setexpr={setWidth ($, value);}
float	height	quantity=LENGTH setexpr={setHeight ($, value);}
float	start	quantity=ANGLE setexpr={$.start = value * 57.29578f} getexpr={$.start * 0.017453293f}
float	extent	quantity=ANGLE setexpr={$.extent = value * 57.29578f} getexpr={$.extent * 0.017453293f}
int		arcType	setmethod=setArcType getmethod=getArcType type=ARC_TYPE
end

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
