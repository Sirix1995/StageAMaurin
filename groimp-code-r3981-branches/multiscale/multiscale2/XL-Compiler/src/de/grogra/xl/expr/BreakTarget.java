
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

package de.grogra.xl.expr;

import de.grogra.xl.vmx.*;
import org.objectweb.asm.Label;
import de.grogra.xl.compiler.BytecodeWriter;

public abstract class BreakTarget extends VoidExpression
{
	private int label = -1;
	private Label bcLabel = null;

	
	public boolean isInitialized ()
	{
		return label >= 0;
	}

	
	public void initialize (int label)
	{
		if (label < 0)
		{
			throw new IllegalArgumentException ();
		}
		this.label = label;
	}
	
	
	public int getLabel ()
	{
		return label;
	}

	
	public Label getBytecodeLabel ()
	{
		assert label >= 0;
		if (bcLabel == null)
		{
			bcLabel = new Label ();
		}
		return bcLabel;
	}


	@Override
	protected final void evaluateVoidImpl (VMXState t)
	{
		if (label >= 0)
		{
			try
			{
				evaluate (t);
			}
			catch (AbruptCompletion.Break e)
			{
				if (e.getLabel () == label)
				{
					e.dispose ();
				}
				else
				{
					throw e;
				}
			}
		}
		else
		{
			evaluate (t);
		}
	}
	

	@Override
	protected void writeImpl (BytecodeWriter writer, boolean discard)
	{
		assert discard : this;
		writeOperator (writer);
		if (bcLabel != null)
		{
			writer.visitLabel (bcLabel);
		}
	}

	
	@Override
	public void writeFinally (BytecodeWriter writer, int lbl, ControlTransfer transfer)
	{
		if ((lbl >= 0) && (lbl == label))
		{
			transfer.writeTransfer (writer, this);
		}
		else
		{
			super.writeFinally (writer, lbl, transfer);
		}
	}


	protected abstract void evaluate (VMXState t);
	

	@Override
	protected String paramString ()
	{
		return super.paramString () + ",label=" + label;
	}

}
