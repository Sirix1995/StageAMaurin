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

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author yong
 * @since  2011-11-24
 */
public class MTGNodeData implements Serializable{

	private static final long serialVersionUID = -638762363144293593L;

	private HashMap<String, Object> data;
	
	public MTGNodeData()
	{
		data = new HashMap<String,Object>();
	}
	
	public Object getObject(String key)
	{
		return data.get(key);
	}
	
	public void setObject(String key, Object obj)
	{
		data.put(key, obj);
	}
}
