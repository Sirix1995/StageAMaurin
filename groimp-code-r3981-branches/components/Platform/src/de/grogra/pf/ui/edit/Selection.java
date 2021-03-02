
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

package de.grogra.pf.ui.edit;

import de.grogra.util.*;
import de.grogra.pf.ui.*;
import java.awt.datatransfer.Transferable;

/**
 * A <code>Selection</code> represents a selection for the GUI.
 * Only instances of <code>Selection</code> may be used as values
 * of the workbench selection (see
 * {@link de.grogra.pf.ui.UIProperty#WORKBENCH_SELECTION}).
 * 
 * @author Ole Kniemeyer
 */
public interface Selection extends Described
{
	/**
	 * Capability flag indicating that this selection can be converted
	 * to <code>Transferable</code> via {@link #toTransferable(boolean)}. 
	 */
	int TRANSFERABLE = 1;


	/**
	 * Capability flag indicating that this selection can be deleted
	 * by {@link #delete(boolean)}.
	 */
	int DELETABLE = 2;


	/**
	 * Capability flag indicating that this selection is part of
	 * a hierarchical structure.
	 */
	int HIERARCHICAL = 4;


	/**
	 * Returns the capabilities of this selection. The returned value
	 * is a combination of the flags {@link #TRANSFERABLE},
	 * {@link #DELETABLE}, {@link #HIERARCHICAL}.
	 * 
	 * @return this selection's capabilities
	 */
	int getCapabilities ();


	/**
	 * Returns an editor component for the GUI. The component is used in
	 * in the GUI to edit the properties of this selection.
	 * 
	 * @return a component responsible for editing this selection
	 */
	ComponentWrapper createPropertyEditorComponent ();


	/**
	 * Returns an editor menu component for the GUI. The component is used in
	 * in the GUI to edit the properties of this selection as part of a menu.
	 * 
	 * @return a menu component responsible for editing this selection
	 */
	ComponentWrapper createPropertyEditorMenu ();


	/**
	 * Converts this selection into {@link Transferable} for the
	 * clipboard. This method is only invoked if this selection
	 * has the capability {@link #TRANSFERABLE}. 
	 * 
	 * @param includeChildren <code>true</code> iff children in the hierarchy
	 * shall be included 
	 * @return a transferable object representing the data of this selection
	 */
	Transferable toTransferable (boolean includeChildren);


	/**
	 * Deletes this selection. The precise semantics of deletion depends on
	 * the selection. This method is only invoked if this selection
	 * has the capability {@link #DELETABLE}.
	 * 
	 * @param includeChildren <code>true</code> iff children in the hierarchy
	 * shall be included 
	 */
	void delete (boolean includeChildren);
	
	
	Context getContext ();
}
