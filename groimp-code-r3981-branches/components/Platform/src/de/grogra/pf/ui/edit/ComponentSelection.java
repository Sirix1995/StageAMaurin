/*
 * Copyright (C) 2012 GroIMP Developer Team
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

package de.grogra.pf.ui.edit;

import de.grogra.graph.GraphState;
import de.grogra.pf.registry.ComponentDescriptor;
import de.grogra.pf.ui.ComponentWrapper;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.util.ComponentWrapperImpl;
import de.grogra.util.Disposable;

public class ComponentSelection 
{

	private GraphState state = null;
	private Context context = null;
	private ComponentDescriptor componentDescriptor = null;

	public ComponentSelection(Context context) {
		this.context = context;
	}

	public ComponentSelection (Context context, GraphState state, ComponentDescriptor componentDescriptor)
	{
		this.context = context;
		this.state = state;
		this.componentDescriptor= componentDescriptor;
	}
	

	public GraphState getState() {
		return state;
	}

	public void setState(GraphState state) {
		this.state = state;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ComponentDescriptor getComponentDescriptor() {
		return componentDescriptor;
	}

	public void setComponentDescriptor(ComponentDescriptor component) {
		this.componentDescriptor = component;
	}

	
	public ComponentWrapper createComponentDescriptionComponent (Disposable disposable)
	{
		return new ComponentWrapperImpl(componentDescriptor.getDescriptionPanel(), disposable);
	}
	
}
