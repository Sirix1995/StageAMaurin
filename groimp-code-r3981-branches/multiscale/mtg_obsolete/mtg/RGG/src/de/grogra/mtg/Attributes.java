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
import de.grogra.graph.ObjectAttribute;
import de.grogra.imp3d.IMP3D;
import de.grogra.imp3d.objects.Transformation;

public class Attributes extends de.grogra.rgg.Attributes
{
	public static final ObjectAttribute MTG_NODE_DATA
		= init (new ObjectAttribute (MTGNodeData.class, false,null), "mtgNodeData",I18N);
}
