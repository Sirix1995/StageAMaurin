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

package de.grogra.ray2.antialiasing;

import java.util.Random;

import javax.vecmath.Color4f;
import javax.vecmath.Point4d;

import de.grogra.ray.util.Ray;
import de.grogra.ray2.Resources;
import de.grogra.ray2.Scene;
import de.grogra.ray2.tracing.PixelwiseRenderer;

/**
 * This class implements a stratified stochastic supersampling strategy.
 * Each pixel rectangle is divided into a square grid of strata, for each
 * stratum a random ray is generated and its color determined.    
 * 
 * @author Michael Tauer
 * @author Ole Kniemeyer
 */
public class StochasticSupersampling extends NoAntialiasing
{
	public static final String GRID_SIZE = "stochasticsupersampling.gridsize";

	private Color4f tmpColor4f;
	private Point4d sumColor;

	private int gridSize = 3;

	@Override
	protected void initLocals ()
	{
		super.initLocals ();
		tmpColor4f = new Color4f ();
		sumColor = new Point4d ();
	}

	@Override
	public void initialize (PixelwiseRenderer tracer, Scene scene)
	{
		super.initialize (tracer, scene);
		setGridSize (Math.min (Math.max (tracer.getNumericOption (GRID_SIZE,
			gridSize).intValue (), 1), 200));
	}

	/**
	 * Sets the grid size for supersampling. A square grid
	 * of <code>size</code> by <code>size</code> strata will be used
	 * to generate the random rays.
	 * 
	 * @param size size of the grid edges
	 */
	public void setGridSize (int size)
	{
		gridSize = size;
	}

	public static int DEBUG_I = -1;
	public static int DEBUG_J = -1;

	public static int CURRENT_I;
	public static int CURRENT_J;

	@Override
	public void getColorOfRectangle (double x, double y, double width,
			double height, Color4f color, Random random)
	{
		
		random.nextInt ();
		processor.initializeBeforeTracing(random);
		
		Ray ray = list.rays[0];
		sumColor.set (0, 0, 0, 0);
		int n = 0;
		for (int i = 0; i < gridSize; i++)
		{
			CURRENT_I = i;
			for (int j = 0; j < gridSize; j++)
			{
				CURRENT_J = j;
//				PixelwiseRenderer.DEBUG_SUBPIXEL
//					= PixelwiseRenderer.DEBUG_PIXEL && ((DEBUG_I < 0) || ((i == DEBUG_I) && (j == DEBUG_J)));
				int rnd = random.nextInt ();

				// generate random locations in stratum (i, j)
				int ir = (i << 16) + (char) rnd;
				int jr = (j << 16) + (char) (rnd >> 16);
				env.uv.set ((float) (x + width * ir / (65536 * gridSize)),
					(float) (y + height * jr / (65536 * gridSize)));

				// generate a single ray origin for env.uv
				renderer.getCamera ().generateRandomOrigins (env, list, random);
				tmpSpectrum.set (ray.spectrum);

				// copy generated ray origin to env 
				env.point.set (ray.origin);
				env.globalToLocal.transform (env.point, env.localPoint);

				// generate ray direction
				renderer.getCamera ().generateRandomRays (env, null, tmpSpectrum, list, false, random);

				line.x = xPixel;
				line.y = yPixel;
				
				line.start = 0;
				line.end = Double.POSITIVE_INFINITY;
				line.origin.set (ray.origin);
				line.direction.set (ray.direction);
				
				// trace the ray
				processor.getColorFromRay (line, ray.spectrum, tmpColor4f, random);

				if ((tmpColor4f.x != tmpColor4f.x)
					|| (tmpColor4f.y != tmpColor4f.y)
					|| (tmpColor4f.z != tmpColor4f.z)
					|| (tmpColor4f.w != tmpColor4f.w))
				{
					System.err.println ("NaN at (" + x + ',' + y + ") + (" + i + ',' + j + ')');
				}
				else
				{
					// integrate the color values
					sumColor.x += tmpColor4f.x;
					sumColor.y += tmpColor4f.y;
					sumColor.z += tmpColor4f.z;
					sumColor.w += tmpColor4f.w;
					n++;
				}
			}
		}
		color.set (sumColor);
		if (n > 0)
		{
			color.scale (1f / n);
		}
	}

	@Override
	protected void appendStatisticsImpl (StringBuffer stats)
	{
		stats.append (Resources.msg ("antialiasing.stochastic.statistics",
			new Object[] {gridSize, gridSize}));
		super.appendStatisticsImpl (stats);
	}

}
