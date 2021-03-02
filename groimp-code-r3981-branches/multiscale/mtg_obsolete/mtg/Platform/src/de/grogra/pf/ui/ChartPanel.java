
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

package de.grogra.pf.ui;

import de.grogra.pf.data.*;

public interface ChartPanel extends Panel
{
	int PREVIOUS_PLOT = -1;
	int AREA_PLOT = 0;
	int STACKED_AREA_PLOT = 1;
	int BAR_PLOT = 2;
	int STACKED_BAR_PLOT = 3;
	int LINE_PLOT = 4;
	int PIE_PLOT = 5;
	int SCATTER_PLOT = 6;
	int XY_PLOT = 7;
	int HISTOGRAM = 8;
	int STATISTICAL_BAR_PLOT = 9;
	int STATISTICS = 10;
	int HORIZONTAL_BAR_PLOT = 11;
	int WATERFALL_PLOT = 12;
	int TIME_SERIES_PLOT = 13;
	
	int threeDe = 100;
	
	int BAR_PLOT_3D = BAR_PLOT + threeDe;
	int HORIZONTAL_BAR_PLOT_3D = HORIZONTAL_BAR_PLOT + threeDe;
	int LINE_PLOT_3D = LINE_PLOT + threeDe;
	int PIE_PLOT_3D = PIE_PLOT + threeDe;
	int STACKED_BAR_PLOT_3D = STACKED_BAR_PLOT + threeDe;
	
	int PLOT_TYPE_MASK = 31;

	int X_LOG = 32;
	int Y_LOG = 64;

	void setChart (Dataset dataset, int plotType, de.grogra.util.Map options);
}
