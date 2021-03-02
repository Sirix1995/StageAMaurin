
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.imp3d;

import javax.vecmath.Matrix4d;

import de.grogra.imp.View;
import de.grogra.math.TMatrix4d;
import de.grogra.math.TVector3d;
import de.grogra.math.Transform3D;
import de.grogra.math.Tuple3dType;
import de.grogra.math.Tuple3fType;
import de.grogra.persistence.SCOType;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Plugin;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.event.DragEvent;
import de.grogra.pf.ui.registry.CommandItem;
import de.grogra.pf.ui.registry.CommandPlugin;
import de.grogra.ray.physics.Spectrum3d;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.util.I18NBundle;

public final class IMP3D extends Plugin implements CommandPlugin
{
	public static final I18NBundle I18N = I18NBundle.getInstance (IMP3D.class);

	public static final SCOType SPECTRUM_3F = (SCOType) new SCOType (new Spectrum3f (), Tuple3fType.$TYPE).validate ();
	public static final SCOType SPECTRUM_3D = (SCOType) new SCOType (new Spectrum3d (), Tuple3dType.$TYPE).validate ();

	private static IMP3D PLUGIN;


	public static IMP3D getInstance ()
	{
		return PLUGIN;
	}


	public IMP3D ()
	{
		assert PLUGIN == null;
		PLUGIN = this;
	}


	public void run (Object info, Context ctx, CommandItem item)
	{
		String n = item.getName ();
		if ("move".equals (n))
		{
			DragEvent e = (DragEvent) info;
			if (e.draggingContinued ())
			{
				((View3D) e.getPanel ()).move (e.getDeltaX (), e.getDeltaY ());
			}
		}
		else if ("dolly".equals (n))
		{
			DragEvent e = (DragEvent) info;
			if (e.draggingContinued ())
			{
				((View3D) e.getPanel ()).dolly (e.getDeltaX (), e.getDeltaY ());
			}
		}
		else if ("rotate".equals (n))
		{
			DragEvent e = (DragEvent) info;
			if (e.draggingContinued ())
			{
				((View3D) e.getPanel ()).rotate (e.getDeltaX (), e.getDeltaY ());
			}
		}
		else if ("zoom".equals (n))
		{
			DragEvent e = (DragEvent) info;
			if (e.draggingContinued ())
			{
				((View3D) e.getPanel ()).zoom (e.getDeltaX (), e.getDeltaY ());
			}
		}
	}


	public static Transform3D toTransform (Matrix4d t)
	{
		double d;
		return ((d = t.m00 - 1) * d + (d = t.m01) * d + (d = t.m02) * d
				+ (d = t.m10) * d + (d = t.m11 - 1) * d + (d = t.m12) * d
				+ (d = t.m20) * d + (d = t.m21) * d + (d = t.m22 - 1) * d < 1e-8)
			? (Transform3D) new TVector3d (t.m03, t.m13, t.m23)
			: new TMatrix4d (t);
	}

	
	public static void fitCamera (Item item, Object info, Context ctx)
	{
		View w = View.get (ctx);
		if (!(w instanceof View3D))
		{
			return;
		}
		((View3D) w).fitCamera ();
	}

}
