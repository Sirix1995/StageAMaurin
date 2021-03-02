
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

package de.grogra.imp.edit;

import de.grogra.util.*;

final class UndoEdit extends Edit
{
	final boolean isUndo;
	private final Edit edit, originalEdit;


	UndoEdit (Edit edit)
	{
		this.edit = edit;
		if (edit instanceof UndoEdit)
		{
			isUndo = !((UndoEdit) edit).isUndo;
			originalEdit = ((UndoEdit) edit).originalEdit;
		}
		else
		{
			isUndo = true;
			originalEdit = edit;
		}
	}


	public Object getDescription (String type)
	{
		if (Utils.isStringDescription (type))
		{
			return null;//Main2.I18N.msg (isUndo ? "undo" : "redo",
						//		  originalEdit.getDescription (type));
		}
		else
		{
			return originalEdit.getDescription (type);
		}
	}


	@Override
	boolean addEdit (Edit edit)
	{
		return false;
	}

}
