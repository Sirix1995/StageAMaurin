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

package de.grogra.rgg;

public final class Statistics
{
	public final int count;
	public final double sum;
	public final double squareSum;
	public final double cubeSum;
	public final double mean;
	public final double variance;
	public final double deviation;
	public final double skewness;
	public final double min;
	public final double max;

	public Statistics (int count, double sum, double squareSum, double cubeSum, double min, double max)
	{
		this.count = count;
		this.sum = sum;
		this.squareSum = squareSum;
		this.cubeSum = cubeSum;
		this.mean = sum / count;
		double v = (squareSum - sum * mean) / count;
		this.variance = (v > 0) ? v : 0;
		this.deviation = Math.sqrt (variance);
		this.skewness = (cubeSum + mean * (2 * mean * sum - 3 * squareSum))
			/ (count * deviation * variance);
		this.min = min;
		this.max = max;
	}

	@Override
	public String toString ()
	{
		return count + " values in [" + (float) min + ", " + (float) max
			+ "] : sum=" + (float) sum
			+ ", mean=" + (float) mean
			+ ", deviation=" + (float) deviation + "("
			+ (float) (100 * deviation / mean) + "%)"
			+ ", skewness=" + (float) skewness;
	}
}
