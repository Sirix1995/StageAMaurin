/*
 * Copyright (C) 2011 Abteilung Oekoinformatik, Biometrie und Waldwachstum, 
 * Buesgeninstitut, Georg-August-Universitaet Göttingen
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
package de.grogra.mtg;

/**
 * @author Ong Yongzhi
 * @since  2011-11-24
 */
import javax.vecmath.Vector3d;

import de.grogra.graph.DoubleAttribute;
import de.grogra.graph.IntAttribute;
import de.grogra.graph.ObjectAttribute;
import de.grogra.graph.StringAttribute;
import de.grogra.util.Quantity;

public class Attributes extends de.grogra.rgg.Attributes
{
	public static final ObjectAttribute MTG_NODE_DATA
		= init (new ObjectAttribute (MTGNodeData.class, false,null), "mtgNodeData",I18N);
	
	//public static final ObjectAttribute MTG_POLYGONS
	//= init (new ObjectAttribute (PolygonMesh.class, false,null), "mtgPolygons",I18N);
	
//	public static final DoubleAttribute MTG_DIST
//	= init (new DoubleAttribute (Quantity.LENGTH), "mtgDist", I18N);
	public static final DoubleAttribute MTG_DIST
	= init (new DoubleAttribute (null), "mtgDist", I18N);
	
	public static final IntAttribute MTG_VISIBLE_SIDES
	= init (new IntAttribute(), "mtgVisibleSides", I18N);

	public static final StringAttribute MTG_CLASS
	= init (new StringAttribute(), "mtgClass", I18N);
	
	public static final IntAttribute MTG_SCALE
	= init (new IntAttribute(), "mtgScale", I18N);
	
	public static final IntAttribute MTG_CLASS_ID
	= init (new IntAttribute(), "mtgClassID", I18N);
	
	public static final IntAttribute MTG_ID
	= init (new IntAttribute(), "mtgID", I18N);
	
	public static final IntAttribute STD_ATT_FLAG
	= init (new IntAttribute(), "stdAttFlag", I18N);

	public static final DoubleAttribute L1
	= init (new DoubleAttribute(null), "L1", I18N);
	
	public static final DoubleAttribute L2
	= init (new DoubleAttribute(null), "L2", I18N);
	
	public static final DoubleAttribute L3
	= init (new DoubleAttribute(null), "L3", I18N);
	
	public static final DoubleAttribute DAB
	= init (new DoubleAttribute(null), "DAB", I18N);
	
	public static final DoubleAttribute DAC
	= init (new DoubleAttribute(null), "DAC", I18N);
	
	public static final DoubleAttribute DBC
	= init (new DoubleAttribute(null), "DBC", I18N);
	
	public static final DoubleAttribute XX
	= init (new DoubleAttribute(null), "XX", I18N);
	
	public static final DoubleAttribute YY
	= init (new DoubleAttribute(null), "YY", I18N);
	
	public static final DoubleAttribute ZZ
	= init (new DoubleAttribute(null), "ZZ", I18N);
	
	public static final DoubleAttribute Length
	= init (new DoubleAttribute(null), "Azimut", I18N);
	
	public static final DoubleAttribute Azimut
	= init (new DoubleAttribute(null), "Azimut", I18N);
	
	public static final DoubleAttribute Alpha
	= init (new DoubleAttribute(null), "Alpha", I18N);
	
	public static final DoubleAttribute AA
	= init (new DoubleAttribute(null), "AA", I18N);
	
	public static final DoubleAttribute BB
	= init (new DoubleAttribute(null), "BB", I18N);
	
	public static final DoubleAttribute CC
	= init (new DoubleAttribute(null), "CC", I18N);
	
	public static final DoubleAttribute TopDia
	= init (new DoubleAttribute(null), "TopDia", I18N);
	
	public static final DoubleAttribute BotDia
	= init (new DoubleAttribute(null), "BotDia", I18N);
	
	public static final DoubleAttribute Position
	= init (new DoubleAttribute(null), "Position", I18N);
	
	public static final IntAttribute Category
	= init (new IntAttribute(), "Category", I18N);
	//= init (new DoubleAttribute(null), "Category", I18N);
	
	public static final ObjectAttribute DirectionPrimary
	= init (new ObjectAttribute (Vector3d.class, false,null), "DirectionPrimary",I18N);
	
	public static final IntAttribute Order
	= init (new IntAttribute(), "Order", I18N);
	
	public static final IntAttribute DATA_FLAG
	= init (new IntAttribute(), "dataFlag", I18N);
	
	/*
	public static final BooleanAttribute hasL1              = init( new BooleanAttribute(), "hasL1"                ,I18N);
	public static final BooleanAttribute hasL2              = init( new BooleanAttribute(), "hasL2"                ,I18N);
	public static final BooleanAttribute hasL3              = init( new BooleanAttribute(), "hasL3"                ,I18N);
	public static final BooleanAttribute hasDAB             = init( new BooleanAttribute(), "hasDAB"               ,I18N);
	public static final BooleanAttribute hasDAC             = init( new BooleanAttribute(), "hasDAC"               ,I18N);
	public static final BooleanAttribute hasDBC             = init( new BooleanAttribute(), "hasDBC"               ,I18N);
	public static final BooleanAttribute hasXX              = init( new BooleanAttribute(), "hasXX"                ,I18N);
	public static final BooleanAttribute hasYY              = init( new BooleanAttribute(), "hasYY"                ,I18N);
	public static final BooleanAttribute hasZZ              = init( new BooleanAttribute(), "hasZZ"                ,I18N);
	public static final BooleanAttribute hasLength          = init( new BooleanAttribute(), "hasLength"            ,I18N);
	public static final BooleanAttribute hasAzimut          = init( new BooleanAttribute(), "hasAzimut"            ,I18N);
	public static final BooleanAttribute hasAlpha           = init( new BooleanAttribute(), "hasAlpha"            ,I18N);
	public static final BooleanAttribute hasAA              = init( new BooleanAttribute(), "hasAA"                ,I18N);
	public static final BooleanAttribute hasBB              = init( new BooleanAttribute(), "hasBB"                ,I18N);
	public static final BooleanAttribute hasCC              = init( new BooleanAttribute(), "hasCC"                ,I18N);
	public static final BooleanAttribute hasTopDia          = init( new BooleanAttribute(), "hasTopDia"            ,I18N);
	public static final BooleanAttribute hasBotDia          = init( new BooleanAttribute(), "hasBotDia"            ,I18N);
	public static final BooleanAttribute hasPosition        = init( new BooleanAttribute(), "hasPosition"          ,I18N);
	public static final BooleanAttribute hasCategory        = init( new BooleanAttribute(), "hasCategory"          ,I18N);
	public static final BooleanAttribute hasDirectionPrimary= init( new BooleanAttribute(), "hasDirectionPrimary"  ,I18N);
	public static final BooleanAttribute hasOrder           = init( new BooleanAttribute(), "hasOrder"             ,I18N);*/
}

