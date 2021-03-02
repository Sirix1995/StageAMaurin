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

//reflects one enum from LGMSymbols.h (cLignum)

//LGMAD = LIGNUM Attribute Double  *********************'

// These symbols are used to access (GetValue, SetValue) the
// variables (either attributes or other variables) of TreeSegment,
// Bud, BrachingPoint, BroadLeaf in LIGNUM (in any compartment
// except Tree). As regards GetValue, it may be that the value
// returned is calculated on the basis of other variables.



//If you add a new symbol be sure to document it carefully.

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public enum LGMAD {LGAA,LGAAbase,LGAAf,LGAAfb,LGAAfc,LGAAh, LGAAhair,LGAAhwbase,LGAAs,
    LGAAs0,LGAADbh,LGAAhwDbh,LGAAsDbh,LGAAsbase,LGAage,LGAcbase,LGAcollision,
    LGADbase, LGADbaseHw,LGADbh,LGADbhHw,LGADcb,LGAdof, LGAdR,
LGAH,LGAHcb,LGAHf,LGAHTop,LGAip,LGAiWf,LGAiWs,LGAL,LGALAIb,LGALAIc,
    LGAM, LGAMaxD,LGAomega,LGAP,LGAQabs,LGAQin,LGAR,LGARf,
    LGARh, LGARhair,LGARTop,LGASa,LGAsf,LGAstarm,LGAstatus, 
    LGAstate,LGAtauL, LGAtype,LGAV,LGAVf,LGAVfrustum,LGAVh,
    LGAVhair,LGAvi,LGAVs, LGAWf, LGAWf0, LGAWood, LGAWs, 
    LGAWh,LGAWhair,LGAWstem, LGAT

}



// 0  LGPaf  Needle mass - tree segment area (kgC/m^2)relationship
// 1  LGPaleafmax  Maximum size of a leaf (m2)
// 2  LGPapical   Length of segment (new) forking off (lateral) =
//                LGPapical * Len of not forking off
// 3  LGPar  Foliage - root relationship
// 4  LGPdof Degree of filling (proportion leaf surface fills of geometric
//          shape - e.g. ellipsis - of the leaf).
// 5. LGPLmin Minimum length (m) allowed for a new segment
// 6  LGPlr  Length - radius relationship of a tree segment
// 7  LGPmf  Maintenance respiration rate of foliage
// 8  LGPmr  Maintenance respiration rate of roots
// 9  LGPms  Maintenance respiration rate of sapwood
// 10  LGPna  Needle angle (radians)
// 11  LGPnl  Needle length (na and nl define the cylinder of foliage in
//           CfTreeSegment)
// 12 LGPpr  Photsynthetic efficiency (=photos. rate = pr * intercepted rad.)
// 13 LGPq   Segment shortening factor (becoming obsolete due to vigour
//           index)
// 14 LGPrhoW      Density of  wood in general (is usually used in Segment)
// 15 LGPrho_hair  Density of root hair
// 16 LGPrho_root  Density root sapwood
// 17 LGPsf        Specific leaf area (=leaf area/ leaf weight)
// 18 LGPsr        Senescence rate of roots
// 19 LGPss        Senescence rate of sapwood
// 20 LGPtauL      Transmission coefficient (light) for leaf
// 21 LGPxi        Fraction of heartwood in newly created tree segments
// 22 LGPyc	     Foliage mass (kgC) supported by 1 m^2 of sapwood
// 23 LGPzbrentEpsilon  Accuracy in numerical computation in root finding
//              (see Zbrent)
// 24 LGPlen_random Parameter controlling random variation in lengths of new segments.
//                 Realization may differ with tree species; see e.g.
//                 Lig-Crobas/include/ScotsPine.h
