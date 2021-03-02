
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

import javax.vecmath.*;
import de.grogra.persistence.*;

public class CanvasCamera extends CameraBase
{
	public static final int OUTSIDE_Z_CLIPPING = -1;
	public static final int OUTSIDE_XY_CLIPPING = 0;
	public static final int INSIDE_CLIPPING = 1;

	int width, height;
	float fx, fy, dx, dy;
	Camera camera;


	public void setDimension (int width, int height)
	{
		this.width = width;
		this.height = height;
		dx = 0.5f * width;
		dy = 0.5f * height;
		fy = -(fx = dx);
	}


	public int projectWorld (Tuple3d point, Tuple2f point2D)
	{
		boolean c = camera.projectWorld (point.x, point.y, point.z, point2D);
		if (c)
		{
			point2D.x = point2D.x * fx + dx;
			point2D.y = point2D.y * fy + dy;
			if ((point2D.x >= 0) && (point2D.x < width)
				&& (point2D.y >= 0) && (point2D.y < height))
			{
				return INSIDE_CLIPPING;
			}
			return OUTSIDE_XY_CLIPPING;
		}
		else
		{
			return OUTSIDE_Z_CLIPPING;
		}
	}


	public int projectView (float x, float y, float z, Tuple2f point2D,
							boolean checkClip)
	{
		boolean c = camera.projectView (x, y, z, point2D, checkClip);
		if (c)
		{
			point2D.x = point2D.x * fx + dx;
			point2D.y = point2D.y * fy + dy;
			if ((point2D.x >= 0) && (point2D.x < width)
				&& (point2D.y >= 0) && (point2D.y < height))
			{
				return INSIDE_CLIPPING;
			}
			return OUTSIDE_XY_CLIPPING;
		}
		else
		{
			return OUTSIDE_Z_CLIPPING;
		}
	}


	private final Point2f linePoint = new Point2f ();

	boolean projectLine (float x1, float y1, float z1,
						 float x2, float y2, float z2,
						 java.awt.Point start, java.awt.Point end)
	{
		int xc1, yc1, xc2, yc2, i;
		float mZFar, mZNear, t;
		if ((z1 > (mZFar = -camera.getZFar ()))
			&& (z2 < (mZNear = -camera.getZNear ())))
		{
			if (z1 > mZNear)
			{
				t = (z1 - mZNear) / (z1 - z2);
				z1 = mZNear;
				x1 += t * (x2 - x1);
				y1 += t * (y2 - y1);
			}
			if (z2 < mZFar)
			{
				t = (z2 - mZFar) / (z1 - z2);
				z2 = mZFar;
				x2 += t * (x2 - x1);
				y2 += t * (y2 - y1);
			}
			Point2f p = linePoint;
			camera.projectView (x1, y1, z1, p, false);
			xc1 = (int) (p.x * fx + dx);
			yc1 = (int) (p.y * fy + dy);
			camera.projectView (x2, y2, z2, p, false);
			xc2 = (int) (p.x * fx + dx);
			yc2 = (int) (p.y * fy + dy);

			if (xc1 > xc2)
			{
				i = xc2; xc2 = xc1; xc1 = i;
				i = yc2; yc2 = yc1; yc1 = i;
			}
			if ((xc1 < width) && (xc2 >= 0))
			{
				if (xc1 < 0)
				{
					yc1 += (int) ((long) xc1 * (yc2 - yc1) / (xc1 - xc2));
					xc1 = 0;
				}
				if (xc2 >= width)
				{
					yc2 += (int) ((long) (xc2 - width + 1) * (yc2 - yc1)
								  / (xc1 - xc2));
					xc2 = width - 1;
				}

				if (yc1 > yc2)
				{
					i = xc2; xc2 = xc1; xc1 = i;
					i = yc2; yc2 = yc1; yc1 = i;
				}
				if ((yc1 < height) && (yc2 >= 0))
				{
					if (yc1 < 0)
					{
						xc1 += (int) ((long) yc1 * (xc2 - xc1) / (yc1 - yc2));
						yc1 = 0;
					}
					if (yc2 >= height)
					{
						xc2 += (int) ((long) (yc2 - height + 1) * (xc2 - xc1)
									  / (yc1 - yc2));
						yc2 = height - 1;
					}
					start.x = xc1;
					start.y = yc1;
					end.x = xc2;
					end.y = yc2;
					return true;
				}
			}
		}
		return false;
	}


	@Override
	public float getZNear ()
	{
		return camera.getZNear ();
	}


	@Override
	public float getZFar ()
	{
		return camera.getZFar ();
	}


	@Override
	public float getScaleAt (double x, double y, double z)
	{
		return camera.getScaleAt (x, y, z)
			* (float) Math.sqrt (Math.abs (fx * fy));
	}


	@Override
	public float getScaleAt (float z)
	{
		return camera.getScaleAt (z)
			* (float) Math.sqrt (Math.abs (fx * fy));
	}


	@Override
	public void getRay (float x, float y, Point3d origin, Vector3d direction)
	{
		camera.getRay ((x - dx) / fx, (y - dy) / fy, origin, direction);
	}


	@Override
	public Matrix4d getWorldToViewTransformation ()
	{
		return camera.getWorldToViewTransformation ();
	}


	public void setWorldToViewTransformation (Matrix4d t, Transaction ts)
	{
		throw new UnsupportedOperationException ();
	}

}
