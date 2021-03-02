/*
 * Copyright (C) 2013 GroIMP Developer Team
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

package de.grogra.imp3d.objects;

import javax.vecmath.Tuple2d;
import javax.vecmath.Vector3f;

public interface LightDistributionIF {

	public int getWidth();
	public int getHeight();
	public double[][] getDistribution();
	public double[] getLinearCDF();
	public void setDistribution(double[][] lipdf);
	public void setDistributionEx(double[][] lipdf);
	public double getPower();
	public void setPower(double power);
	public double getDensityAt(Vector3f direction);
	public double map2direction(Vector3f outDirection, Tuple2d inPoint);

}
