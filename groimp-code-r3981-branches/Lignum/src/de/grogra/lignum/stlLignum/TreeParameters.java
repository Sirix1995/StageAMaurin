/*
 * Copyright (C) 2016 GroIMP Developer Team
 *
 * Department Ecoinformatics, Biometrics and Forest Growth,
 * University of Göttingen, Germany
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */ 

package de.grogra.lignum.stlLignum;

import de.grogra.lignum.jadt.ParametricCurve;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Michael Henke
 */
public class TreeParameters {

	/**
	 * Needle mass - tree segment area relationship (kgC/m^2)
	 */
	public double LGPaf = 0;
	
	/**
	 * Maximum size of a leaf (m2)
	 */
	public double LGPaleafmax = 0;

	/**
	 * Length of segment (new) forking off (lateral) = LGPapical * Len of not forking off
	 */
	public double LGPapical = 0;
	
	/**
	 * Foliage - root relationship
	 */
	public double LGPar = 0;
	
	/**
	 * Degree of filling (proportion leaf surface fills of geometric shape -
	 * e.g. ellipsis - of the leaf).
	 */
	public double LGPdof = 0;
	
	/**
	 * Parameter controlling random variation in lengths of new segments.
	 * Realization may differ with tree species
	 */
	public double LGPlen_random = 0;
	
	/**
	 * Minimum length (m) allowed for a new segment
	 */
	public double LGPLmin = 0;
	
	/**
	 * Length - radius relationship of a tree segment, R = LGPlr * L
	 */
	public double LGPlr = 0;
	
	/**
	 * Maintenance respiration rate of foliage
	 */
	public double LGPmf = 0;
	
	/**
	 * Maintenance respiration rate of roots
	 */
	public double LGPmr = 0;
	
	/**
	 * Maintenance respiration rate of sapwood
	 */
	public double LGPms = 0;
	
	/**
	 * Needle angle (radian)
	 */
	public double LGPna = 0;
	
	/**
	 * Needle length (m) (LGPna and LGPnl define the cylinder of foliage in CfTreeSegment)
	 */
	public double LGPnl = 0;
	
	/**
	 * Proportion of bound solar radiation used in photosynthesis
	 * Photosynthetic efficiency (= photos. rate = LGPpr * intercepted rad.)
	 */
	public double LGPpr = 0;
	
	/**
	 * Segment shortening factor (becoming obsolete due to vigour index)
	 */
	public double LGPq = 0;
	
	/**
	 * Density of wood in general (is usually used in Segment)
	 */
	public double LGPrhoW = 0;
	
	/**
	 * Density of root hair
	 */
	public double LGPrho_hair = 0;
	
	/**
	 * Density of root sapwood
	 */
	public double LGPrho_root = 0;
	
	/**
	 * Specific foliage area (= leaf area(total) / leaf weight)
	 */
	public double LGPsf = 0;
	
	/**
	 * Senescence rate of roots
	 */
	public double LGPsr = 0;
	
	/**
	 * Senescence rate of sapwood
	 */
	public double LGPss = 0;
	
	/**
	 * Transmission coefficient (light) for leaf, should be about 0.06 for green leaf.
	 * NOTE: be careful here, the attenuation coefficient is the opposite (1-tauL)
	 */
	public double LGPtauL = 0;
	
	/**
	 * Fraction of heartwood in newly created tree segments
	 */
	public double LGPxi = 0;
	
	/**
	 * Foliage mass (kgC) supported by 1 m^2 of sapwood
	 */
	public double LGPyc = 0;
	
	/**
	 * Accuracy for finding root of P-M-dW(lambda), i.e. allocation (see Zbrent)
	 */
	public double LGPzbrentEpsilon = 0;
	
	
	
	
	/**
	 * Adjusted length. For example for making branches below 0.002 to have length 0 and
	 * branches between 0.002 and 0.01 to have length 0.002 (short segments)...
	 */
	public ParametricCurve LGMAL;
	
	/**
	 * Foliage mortality
	 */ 
	public ParametricCurve LGMFM;
	
	/**
	 * Relative length of a new tree segment
	 */ 
	public ParametricCurve LGMIP;
	
	/**
	 * Number of new buds
	 */
	public ParametricCurve LGMNB;
	
	/**
	 * The effect of light to number of the buds.
	 * If no effect the value is always 1 of this function
	 */
	public ParametricCurve LGMLONB;
	
	/**
	 * Vigour index function
	 */
	public ParametricCurve LGMVI;
	
	/**
	 * The effect of vigour index to the number of the buds.
	 * If no effect the value is always 1 of this function
	 */
	public ParametricCurve LGMVIONB;

	
	//double[][] function1 = {{0,0}, {0.1,0.5}, {0.2,0.75}, {0.3,1}};

	//TODO: function readFromFile(String fileName) ... 
}