
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

package de.grogra.pf.ui.tree;

public interface UINodeHandler
{
	int NODE_TYPE_MASK = 0x1f;

	int NT_UNDEFINED = 0;
	int NT_SEPARATOR = 1;
	int NT_ITEM = 2;
	int NT_MOUSE_MOTION = 3;
	int NT_LINK = 4;
	int NT_CHOICE_ITEM = 5;
	int NT_CHECKBOX_ITEM = 6;
	int NT_FILL = 7;
	int NT_SPECIAL = 8;
	int NT_SELECTABLE = 9;

	int NT_DIRECTORY = 0x10;
	int NT_GROUP = 0x11;
	int NT_ITEM_GROUP = 0x12;
	int NT_CHOICE_GROUP = 0x13;

	int NT_DIRECTORY_MASK = 0x10;
	
	String GET_SELECTABLE_METHOD = "getSelectable";
	
	String GET_IMMEDIATE_LISTENER_METHOD = "getImmediateListener";

	String ACTION_SELECT = "select";

	String ACTION_OPEN = "open";

	String ACTION_DELETE = "delete";

	String ACTION_RENAME = "rename";

	boolean nodesEqual (Object a, Object b);

	int getType (Object node);

	String getName (Object node);

	boolean isAvailable (Object node);

	boolean isEnabled (Object node);

	boolean isLeaf (Object node);

	Object resolveLink (Object node);

	Object getDescription (Object node, String type);

	void eventOccured (Object node, java.util.EventObject event);

	Object invoke (Object node, String method, Object arg);
}
