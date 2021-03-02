
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

package de.grogra.docking;

import java.awt.*;
import javax.swing.*;

public class DockSplitPane extends JSplitPane
	implements DockContainer, FixedSize
{
	private final DockManager manager;
	private int fixedSize = 0;
	private float propLocation = -1;


	public DockSplitPane (DockManager manager, int orientation)
	{
		super (orientation);
		this.manager = manager;
//		setDividerSize (4);
		setContinuousLayout (false);
		setResizeWeight (0.5);
	}


	public int getDockComponentCount ()
	{
		return (leftComponent != null) ? (rightComponent != null ? 2 : 1)
			: (rightComponent != null ? 1 : 0);
	}


	public DockComponent getDockComponent (int index)
	{
		return (DockComponent) ((leftComponent != null)
								? ((index == 0) ? leftComponent : rightComponent)
								: rightComponent);
	}


	public void findDockPositions (DockPositionList list, Point relDrag)
	{
		if (orientation == HORIZONTAL_SPLIT)
		{
			DockPosition.addDockPositions (list, (DockComponent) leftComponent,
										   DockPosition.RIGHT
										   | DockPosition.TOP
										   | DockPosition.BOTTOM
										   | DockPosition.CENTER,
										   relDrag, this, null);
			DockPosition.addDockPositions (list, (DockComponent) rightComponent,
										   DockPosition.TOP
										   | DockPosition.BOTTOM
										   | DockPosition.CENTER,
										   relDrag, this, null);
		}
		else
		{
			DockPosition.addDockPositions (list, (DockComponent) leftComponent,
										   DockPosition.BOTTOM
										   | DockPosition.LEFT
										   | DockPosition.RIGHT
										   | DockPosition.CENTER,
										   relDrag, this, null);
			DockPosition.addDockPositions (list, (DockComponent) rightComponent,
										   DockPosition.LEFT
										   | DockPosition.RIGHT
										   | DockPosition.CENTER,
										   relDrag, this, null);
		}
	}


	public DockComponent remove (DockComponent dc)
	{
		DockComponent c = (DockComponent)
			((dc == leftComponent) ? rightComponent : leftComponent);
		setLeftComponent (null);
		setRightComponent (null);
		DockContainer p = getDockParent ();
		return (p != null) ? p.replace (this, c) : c;
	}


	public DockContainer getDockParent ()
	{
		return DockManager.getDockParent (this);
	}


	public void add (int position, DockComponent d)
	{
		if (d instanceof DockableToolBar)
		{
			((DockableToolBar) d).setOrientation
				((getOrientation () == HORIZONTAL_SPLIT)
				 ? JToolBar.VERTICAL : JToolBar.HORIZONTAL);
		}
		d = manager.wrap (d, true);
		if (position >= 0)
		{
			if ((position & DockPosition.FIRST_EDGE) != 0)
			{
				if (getLeftComponent () != null)
				{
					Component t = getLeftComponent ();
					setLeftComponent ((Component) d);
					setRightComponent (t);
					return;
				}
			}
			else if ((position & DockPosition.SECOND_EDGE) != 0)
			{
				if (getRightComponent () != null)
				{
					Component t = getRightComponent ();
					setRightComponent ((Component) d);
					setLeftComponent (t);
					return;
				}
			}
		}
		add ((Component) d, null);
	}


	public DockComponent replace (DockContainer dc, DockComponent d)
	{
		d = manager.wrap (d, true);
		DockManager.replace0 ((Component) dc, (Component) d);
		return d;
	}


	@Override
	protected void addImpl (Component comp, Object constraints, int index) 
	{
		super.addImpl (comp, constraints, index);
		updateFixedSize ();
	}


	private void updateFixedSize ()
	{
		int lf = (leftComponent instanceof FixedSize)
			? ((FixedSize) leftComponent).getFixedSize () : 0,
			rf = (rightComponent instanceof FixedSize)
			? ((FixedSize) rightComponent).getFixedSize () : 0;
		if (lf == rf)
		{
			setResizeWeight (0.5);
		}
		else if ((((lf & FIXED_WIDTH) != 0)
				  && (orientation == HORIZONTAL_SPLIT))
				 || (((lf & FIXED_HEIGHT) != 0)
					 && (orientation == VERTICAL_SPLIT)))
		{
			setResizeWeight (0);
		}
		else if ((((rf & FIXED_WIDTH) != 0) 
				  && (orientation == HORIZONTAL_SPLIT))
				 || (((rf & FIXED_HEIGHT) != 0)
					 && (orientation == VERTICAL_SPLIT)))
		{
			setResizeWeight (1);
		}
		else
		{
			setResizeWeight (0.5);
		}
		int fs = rf & lf;
		if (fs != fixedSize)
		{
			fixedSize = fs;
			if (isValid ())
			{
				resetToPreferredSizes ();
			}
			if (getParent () instanceof DockSplitPane)
			{
				((DockSplitPane) getParent ()).updateFixedSize ();
			}
		}
/*		setEnabled (!(((((rf | lf) & FIXED_WIDTH) != 0)
					   && (orientation == HORIZONTAL_SPLIT))
					  || ((((rf | lf) & FIXED_HEIGHT) != 0)
						  && (orientation == VERTICAL_SPLIT))));
*/
	}


	public float getProportionalDividerLocation ()
	{
		return (float) getDividerLocation ()
			/ (((getOrientation() == VERTICAL_SPLIT) ? getHeight ()
				: getWidth ()) - getDividerSize ());
	}


	void setProportionalDividerLocation (float location)
	{
		propLocation = location;
		setDividerLocation (location);
	}


	void updateDividerLocation ()
	{
		if ((propLocation > 0) && (getWidth () > 0) && (getHeight () > 0))
		{
			setDividerLocation (propLocation);
			propLocation = -1;
		}
	}


	public int getFixedSize ()
	{
		return fixedSize;
	}


	public void toFront (DockComponent c)
	{
	}

}
