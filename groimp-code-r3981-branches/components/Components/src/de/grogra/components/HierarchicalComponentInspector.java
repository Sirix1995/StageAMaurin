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

package de.grogra.components;

import java.util.Map;

import de.grogra.graph.impl.GraphManager;
import de.grogra.pf.boot.Main;
import de.grogra.pf.registry.ComponentDescriptor;
import de.grogra.pf.ui.Context;

public class HierarchicalComponentInspector extends ComponentInspector {

	public HierarchicalComponentInspector(Context ctx, GraphManager graph) {
		super(ctx, graph, Main.getRegistry().getComponentMap());
	}
	
	@Override
	public void initialize() {
		rootNode = new TreeNode(ComponentDescriptor.createCoreDescriptorDir("components"), null);
	}
	
	@Override
	public void buildTree() {
		clearTree(); //clear first. yong 11 jan 2013 - because some listeners refresh the tree using this method
		for(Map.Entry<String, ComponentDescriptor> entry : componentMap.entrySet()){
            rootNode.addChildAt(entry.getKey(), entry.getValue(), rootNode);
        }
	}
	
	public void clearTree() {
		rootNode = null;
		rootNode = new TreeNode(ComponentDescriptor.createCoreDescriptorDir("components"), null);
	}
	
}
