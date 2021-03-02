
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

import org.objectweb.asm.Label;

import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.vmx.*;
import de.grogra.util.*;

public final class Switch extends BreakTarget
{
	private Int2IntMap labelToIndex;
	private int defaultIndex;

	
	public void initialize (Int2IntMap labelToIndex, int defaultIndex)
	{
		this.labelToIndex = labelToIndex;
		this.defaultIndex = defaultIndex;
	}


	@Override
	protected void evaluate (VMXState t)
	{
		int i = labelToIndex.get (getFirstExpression ().evaluateInt (t));
		if (i == 0)
		{
			i = defaultIndex;
		}
		if (i > 0)
		{
			for (Expression e = getExpression (i); e != null;
			 	 e = e.getNextExpression ())
			{
				e.evaluateAsVoid (t);
			}
		}
	}


	@Override
	protected void writeOperator (BytecodeWriter writer)
	{
		Expression e = getFirstExpression ();
		e.write (writer, false);
		
		Label[] exprLabels = new Label[getExpressionCount ()];
		
		for (int i = 0; i < exprLabels.length; i++)
		{
			exprLabels[i] = new Label ();
		}
		Label defLabel = exprLabels[(defaultIndex < 0) ? exprLabels.length - 1 : defaultIndex - 1];

		int[] keys = labelToIndex.getKeys (null);
		int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
		for (int i = 0; i < keys.length; i++)
		{
			min = Math.min (min, keys[i]);
			max = Math.max (max, keys[i]);
		}
		
		if ((keys.length > 0)
			&& (((long) max - min) < Math.max (10, 4 * keys.length)))
		{
			Label[] lbl = new Label[max - min + 1];
			for (int i = 0; i < lbl.length; i++)
			{
				lbl[i] = defLabel;
			}
			for (int i = 0; i < keys.length; i++)
			{
				lbl[keys[i] - min] = exprLabels[labelToIndex.getValueAt (i) - 1];
			}
			writer.visitTableSwitchInsn (min, max, defLabel, lbl);
		}
		else
		{
			Label[] lbl = new Label[keys.length];
			for (int i = 0; i < lbl.length; i++)
			{
				lbl[i] = exprLabels[labelToIndex.getValueAt (i) - 1];
			}
			writer.visitLookupSwitchInsn (defLabel, keys, lbl);
		}
		
		int i = 0;
		while ((e = e.getNextExpression ()) != null)
		{
			writer.visitLabel (exprLabels[i++]);
			e.write (writer, true);
		}
		writer.visitLabel (exprLabels[exprLabels.length - 1]);
	}

}
