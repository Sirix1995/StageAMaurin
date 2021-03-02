
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

package de.grogra.persistence;

import de.grogra.reflect.TypeId;

public abstract class TransactionApplier implements Transaction.Consumer
{
	protected Transaction transaction;


	public void begin ()
	{
	}


	public void end ()
	{
	}


	public void makePersistent (long id, ManageableType type)
	{
		if (!Transaction.isApplying (transaction))
		{
			throw new IllegalStateException
				("XAThreadState.makePersistent may only be invoked "
				 + "while applying a transaction");
		}
		PersistenceManager m = transaction.getPersistenceManager ();
		PersistenceCapable pc;
		try
		{
			pc = (PersistenceCapable) type.newInstance ();
		}
		catch (Exception e)
		{
			throw new FatalPersistenceException (e);
		}
		m.makePersistent (pc, id, transaction);
	}


	public void makeTransient (PersistenceCapable o, ManageableType type)
	{
		if (!Transaction.isApplying (transaction))
		{
			throw new IllegalStateException
				("XAThreadState.makeTransient may only be invoked "
				 + "while applying a transaction");
		}
		if (o.getPersistenceManager () == null)
		{
			throw new IllegalArgumentException (o + " is already transient");
		}
		o.getPersistenceManager ().makeTransient (o, transaction);
	}

	public void readData (PersistenceCapable o, Transaction.Reader reader)
	{
		if (!Transaction.isApplying (transaction))
		{
			throw new IllegalStateException
				("XAThreadState.readData may only be invoked "
				 + "while applying a transaction");
		}
		try
		{
			if (ManageableType.read (reader, o) != o)
			{
				throw new AssertionError ();
			}
		}
		catch (java.io.IOException e)
		{
			throw new FatalPersistenceException (e);
		}
	}

	public void setField (PersistenceCapable pc, PersistenceField field,
						  int[] indices, Transaction.Reader reader)
	{
		if (!Transaction.isApplying (transaction))
		{
			throw new IllegalStateException
				("XAThreadState.setField may only be invoked "
				 + "while applying a transaction");
		}
		switch (field.typeId)
		{
/*!!
#foreach ($type in $types)
$pp.setType($type)
			case TypeId.$pp.TYPE:
				field.readAndSet$pp.Type (pc, indices, reader);
				break;
#end
!!*/
//!! #* Start of generated code
// generated
			case TypeId.BOOLEAN:
				field.readAndSetBoolean (pc, indices, reader);
				break;
// generated
			case TypeId.BYTE:
				field.readAndSetByte (pc, indices, reader);
				break;
// generated
			case TypeId.SHORT:
				field.readAndSetShort (pc, indices, reader);
				break;
// generated
			case TypeId.CHAR:
				field.readAndSetChar (pc, indices, reader);
				break;
// generated
			case TypeId.INT:
				field.readAndSetInt (pc, indices, reader);
				break;
// generated
			case TypeId.LONG:
				field.readAndSetLong (pc, indices, reader);
				break;
// generated
			case TypeId.FLOAT:
				field.readAndSetFloat (pc, indices, reader);
				break;
// generated
			case TypeId.DOUBLE:
				field.readAndSetDouble (pc, indices, reader);
				break;
// generated
			case TypeId.OBJECT:
				field.readAndSetObject (pc, indices, reader);
				break;
//!! *# End of generated code
		}
		pc.fieldModified (field, indices, transaction);
	}


	public void insertComponent (PersistenceCapable pc, PersistenceField field,
								 int[] indices, Transaction.Reader reader)
	{
		if (!Transaction.isApplying (transaction))
		{
			throw new IllegalStateException
				("XAThreadState.insertComponent may only be invoked "
				 + "while applying a transaction");
		}
		switch (field.typeId)
		{
/*!!
#foreach ($type in $types)
$pp.setType($type)
			case TypeId.$pp.TYPE:
				field.readAndInsert$pp.Type (pc, indices, reader);
				break;
#end
!!*/
//!! #* Start of generated code
// generated
			case TypeId.BOOLEAN:
				field.readAndInsertBoolean (pc, indices, reader);
				break;
// generated
			case TypeId.BYTE:
				field.readAndInsertByte (pc, indices, reader);
				break;
// generated
			case TypeId.SHORT:
				field.readAndInsertShort (pc, indices, reader);
				break;
// generated
			case TypeId.CHAR:
				field.readAndInsertChar (pc, indices, reader);
				break;
// generated
			case TypeId.INT:
				field.readAndInsertInt (pc, indices, reader);
				break;
// generated
			case TypeId.LONG:
				field.readAndInsertLong (pc, indices, reader);
				break;
// generated
			case TypeId.FLOAT:
				field.readAndInsertFloat (pc, indices, reader);
				break;
// generated
			case TypeId.DOUBLE:
				field.readAndInsertDouble (pc, indices, reader);
				break;
// generated
			case TypeId.OBJECT:
				field.readAndInsertObject (pc, indices, reader);
				break;
//!! *# End of generated code
		}
		pc.fieldModified (field.getShallowSuperchain (), indices, transaction);
	}


	public void removeComponent (PersistenceCapable pc, PersistenceField field,
								 int[] indices, Transaction.Reader reader)
	{
		if (!Transaction.isApplying (transaction))
		{
			throw new IllegalStateException
				("XAThreadState.removeComponent may only be invoked "
				 + "while applying a transaction");
		}
		switch (field.typeId)
		{
/*!!
#foreach ($type in $types)
$pp.setType($type)
			case TypeId.$pp.TYPE:
				field.remove$pp.Type (pc, indices, null);
				break;
#end
!!*/
//!! #* Start of generated code
// generated
			case TypeId.BOOLEAN:
				field.removeBoolean (pc, indices, null);
				break;
// generated
			case TypeId.BYTE:
				field.removeByte (pc, indices, null);
				break;
// generated
			case TypeId.SHORT:
				field.removeShort (pc, indices, null);
				break;
// generated
			case TypeId.CHAR:
				field.removeChar (pc, indices, null);
				break;
// generated
			case TypeId.INT:
				field.removeInt (pc, indices, null);
				break;
// generated
			case TypeId.LONG:
				field.removeLong (pc, indices, null);
				break;
// generated
			case TypeId.FLOAT:
				field.removeFloat (pc, indices, null);
				break;
// generated
			case TypeId.DOUBLE:
				field.removeDouble (pc, indices, null);
				break;
// generated
			case TypeId.OBJECT:
				field.removeObject (pc, indices, null);
				break;
//!! *# End of generated code
		}
		pc.fieldModified (field.getShallowSuperchain (), indices, transaction);
	}

}
