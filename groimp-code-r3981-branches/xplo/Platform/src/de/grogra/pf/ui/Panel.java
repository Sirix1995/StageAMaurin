
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

package de.grogra.pf.ui;

import java.awt.Cursor;
import de.grogra.pf.ui.tree.*;
import de.grogra.util.*;

/**
 * A {@link de.grogra.pf.ui.Workbench} presents its information
 * in a set of <code>Panel</code>s.
 * Panels are displayed within {@link de.grogra.pf.ui.Window}s, they have
 * their own title and menu bar and may be re-arranged by Drag&amp;Drop.
 *
 * @author Ole Kniemeyer
 */
public interface Panel extends Context, Disposable
{
	String PANEL_ID = "panelId";
	
	int DEFAULT_CURSOR = Cursor.DEFAULT_CURSOR;
	int WAIT_CURSOR = Cursor.WAIT_CURSOR;
	int INC_WAIT_CURSOR = -2;
	int DEC_WAIT_CURSOR = -3;
	int INHERIT_CURSOR = -4;

	java.util.Map getUIPropertyMap ();

	String getPanelId ();

	UITree getMenu ();

	Panel resolve ();
	
	void initDecorator (Panel decorator);

	Panel getDecorator ();

	void setMenu (UITree menu);

	void setContent (ComponentWrapper content);
	
	ComponentWrapper getContent ();

	void show (boolean moveToFront, Panel keepInFront);

	void checkClose (Command ok);

	void setCursor (int cursor);
}
