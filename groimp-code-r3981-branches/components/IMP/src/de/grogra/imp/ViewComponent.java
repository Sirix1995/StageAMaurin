
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

package de.grogra.imp;

import java.awt.image.RenderedImage;

import de.grogra.pf.registry.Item;
import de.grogra.util.*;
import de.grogra.xl.lang.ObjectConsumer;

/**
 * A <code>ViewComponent</code> represents the actual visualization
 * component of a {@link de.grogra.imp.View}. A <code>View</code>
 * typically contains menu components and a single
 * <code>ViewComponent</code>.
 * 
 * @author Ole Kniemeyer
 */
public interface ViewComponent extends de.grogra.pf.ui.ComponentWrapper
{
	/**
	 * Bit mask for {@link #repaint(int)} indicating that the whole
	 * scene has to be repainted.
	 */
	int SCENE = 1;
	
	/**
	 * Bit mask for {@link #repaint(int)} indicating that the
	 * selection state of objects has to be repainted.
	 */
	int SELECTION = 2;
	
	/**
	 * Bit mask for {@link #repaint(int)} indicating that the
	 * tools {@link View#getActiveTool ()} have to be repainted.
	 */
	int TOOLS = 4;
	
	/**
	 * Bit mask for {@link #repaint(int)} indicating that a repaint
	 * is requested due to a change of scene or display size.
	 */
	int CHANGED = 8;
	
	/**
	 * Smallest bit mask for {@link #repaint(int)} that can be used
	 * freely by implementations of <code>ViewComponent</code>. It is
	 * guaranteed that there is no conflict with the other bit masks.
	 */
	int MIN_USER_FLAG = 256;

	/**
	 * Bit mask combining {@link #SCENE}, {@link #SELECTION}, and
	 * {@link #TOOLS}.
	 */
	int ALL = SCENE | SELECTION | TOOLS;


	/**
	 * Sets the factory item which created this view component.
	 * 
	 * @param factory factory item
	 */
	void initFactory (Item factory);
	
	/**
	 * Returns the factory item which has been set by
	 * {@link #initFactory(Item)}, i.e., the item which
	 * created this component.
	 * 
	 * @return factory item of this component
	 */
	Item getFactory ();

	/**
	 * Initializes this component. This method sets the <code>view</code>
	 * within which this view component is used to display the graph.
	 * It also sets an event listener. this has to be informed of
	 * mouse and keys events within the view component by implementations
	 * of this method.
	 * 
	 * @param view the containing view
	 * @param listener mouse and key events will be reported to this listener
	 */
	void initView (View view, EventListener listener);
	
	/**
	 * Initiates a repaint of those parts of the view which are
	 * indicated by the <code>flags</code>, interpreted as a
	 * combination of bit masks. This method may be invoked from
	 * arbitrary threads.
	 * 
	 * @param flags the parts to be repainted 
	 */
	void repaint (int flags);

	
	/**
	 * Determines the global level-of-detail which should currently
	 * be used in this view component. The value lies between
	 * {@link View#LOD_MIN} and {@link View#LOD_MAX} inclusively. 
	 * 
	 * @return global level-of-detail
	 */
	int getGlobalLOD ();
	
	/**
	 * Initiates a rendering of the graph using the specified
	 * renderer.
	 * 
	 * @param r
	 */
	void render (Renderer r);
	
	void render (Renderer r, int widht, int height);

	void disposeRenderer (Renderer r);
	
	/**
	 * Instructs the view component to create a snapshot. The created snapshot
	 * has to be delivered to the provided <code>callback</code> as an
	 * instance of <code>RenderedImage</code>. This may happen asynchronously,
	 * i.e., in an arbitrary thread. 
	 * 
	 * @param callback callback which asynchronously receives the snapshot
	 */
	void makeSnapshot (ObjectConsumer<? super RenderedImage> callback);
}
