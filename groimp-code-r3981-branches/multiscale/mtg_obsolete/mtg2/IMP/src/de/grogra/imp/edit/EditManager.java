
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

/* import java.util.Vector;
import de.grogra.persistence.*;
import de.grogra.graph.*;
import de.grogra.imp.IMPJobManager;
import de.grogra.imp.edit.*;
import de.grogra.util.*;
*/
public final class EditManager
{
/*	private final Editor editor;
	private long nextEditId;
	private final Vector list = new Vector (100);
	private final PersistenceManager pm;
	private final Transaction blank;
	private final Transaction.Reader reader;

	EditManager (Editor editor, PersistenceManager pm)
	{
		this.editor = editor;
		this.pm = pm;
		nextEditId = 0;//editor.getEditorId ();
		blank = pm.createBlankTransaction
			((IMPJobManager) null);//editor.getJobManager ());
		reader = blank.createReader ();
	}


	public long beginEdit (INode object, String objectName,
						   Attribute attribute)
	{
		return beginEdit (object, objectName, attribute,
						  Main2.I18N, "edit-attr", 0);
	}


	public long beginEdit (INode object, String objectName,
						   Attribute attribute,
						   I18NBundle bundle, String key, int spec)
	{
		return begin (new AttributeEdit (object, objectName, attribute,
										 bundle, key, spec));
	}


	public long beginEdit (INode object, String objectName,
						   PersistenceField field,
						   I18NBundle bundle, String key, int spec)
	{
		return begin (new FieldEdit (object, objectName, field,
									 bundle, key, spec));
	}


	private long begin (Edit e)
	{
		if ((list.size () == 0) || !((Edit) list.lastElement ()).addEdit (e))
		{
			e.id = nextEditId++;
			list.addElement (e);
		}
		return ((Edit) list.lastElement ()).id;
	}


	public int getEditCount ()
	{
		return list.size ();
	}


	public Edit getEdit (int index)
	{
		return (Edit) list.elementAt (index);
	}


	private int nextToRedo ()
	{
		int i = list.size () - 1;
		while ((i >= 0) && (list.elementAt (i) instanceof UndoEdit)
			   && !((UndoEdit) list.elementAt (i)).isUndo)
		{
			i--;
		}
		if ((i >= 0) && (list.elementAt (i) instanceof UndoEdit))
		{
			return i;
		}
		return -1;
	}


	private int nextToUndo ()
	{
		for (int i = list.size () - 1; i >= 0; i--)
		{
			if (!((list.elementAt (i) instanceof UndoEdit)
				  && ((UndoEdit) list.elementAt (i)).isUndo))
			{
				return i;
			}
		}
		return -1;
	}


	public boolean canUndo ()
	{
		return nextToUndo () >= 0;
	}


	public boolean canRedo ()
	{
		return nextToRedo () >= 0;
	}


	public void undo ()
	{
		undo (nextToUndo ());
	}


	public void redo ()
	{
		undo (nextToRedo ());
	}


	public void undo (int index)
	{
		if ((index < 0) || (index >= list.size ()))
		{
			return;
		}
		Edit e = (Edit) list.elementAt (index);
		UndoEdit u = new UndoEdit (e);
		u.id = nextEditId++;
		IMPJobManager j = null; //(IMPJobManager) editor.getJobManager ();
		j.setXAUserId (u.id);
		Transaction xa = j.getTransaction (pm, true);
		LogStore log = pm.getLocalLog ();
//	Transaction.Dump dump = new Transaction.Dump (System.out);
		for (LogStore.Entry se = log.getLastEntry (); se != null;
			 se = se.getPrevious ())
		{
			Transaction.Data d = (Transaction.Data) se.getData ();
			if (d.getUserId () < e.id)
			{
				break;
			}
			if (d.getUserId () == e.id)
			{
				blank.restore (d);
				reader.resetCursor ();
/*		try
				{
					System.out.println (d.getKey ());
					blank.dump (System.out);
					reader.supply (dump);
					reader.resetCursor ();
					System.out.println ();
					reader.supplyInverse (dump);
					reader.resetCursor ();
					System.out.println ();
				}
				catch (java.io.IOException ex)
				{
					throw new WrappingException (ex);
				}
* /
				j.undo (reader);
			}
		}
		list.removeElementAt (index);
		list.addElement (u);
	}


	public String toString ()
	{
		StringBuffer b = new StringBuffer (super.toString ());
		for (int i = 0; i < list.size (); i++)
		{
			b.append ("\n  ").append (list.elementAt (i));
		}
		return b.toString ();
	}


JobManager => prüft nach jedem durchlauf der schleife, ob sich etwas
am protokoll getan hat

wenn ja: füge undo-info hinzu

id für aktuellen zustand, muss bei jeder xa (auch remote) hochgezählt
werden. wird von serverconnection übernommen => globale xa-id.

fortlaufende nummerierung der transaktionen nach behandlung in serverconn

*/
}
