/*
 * Copyright (C) 2020 GroIMP Developer Team
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
 /**
 * @author      elsaromm
 * @version     1.0                                      
 * @since       2022.10.30
 */
package de.grogra.nurbseditor;

import de.grogra.pf.ui.*;
import de.grogra.pf.ui.swing.WindowSupport; 
import de.grogra.util.Map;
import de.grogra.nurbseditor2d.*;
import de.grogra.nurbseditor3d.*;

public class NURBSEditor {
	public static Panel createCurve2DPanel(Context ctx, Map params) {
		Curve2DPanel panel = (Curve2DPanel) new Curve2DPanel(ctx.getWorkbench()).initialize(( WindowSupport) ctx.getWindow(), params);
		return panel; 
	}
	
	public static Panel createCurve3DPanel(Context ctx, Map params) {
		Curve3DPanel panel = (Curve3DPanel) new Curve3DPanel(ctx.getWorkbench()).initialize(( WindowSupport) ctx.getWindow(), params);
		return panel; 
	}
}
