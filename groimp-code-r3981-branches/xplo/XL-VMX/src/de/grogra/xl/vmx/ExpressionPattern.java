
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

package de.grogra.xl.vmx;

import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.xl.query.AbstractExpressionPattern;
import de.grogra.xl.query.BytecodeSerialization;
import de.grogra.xl.query.Frame;
import de.grogra.xl.query.MatchConsumer;
import de.grogra.xl.query.QueryState;
import de.grogra.xl.query.Utils;

public final class ExpressionPattern extends AbstractExpressionPattern
{
	
	final Routine routine;
	
	
	public ExpressionPattern (Type[] termTypes, int inTerm, int outTerm, int type,
								Routine routine)
	{
		super (termTypes, type, inTerm, outTerm);
		this.routine = routine;
	}


	public void write (BytecodeSerialization out) throws java.io.IOException
	{
		out.beginMethod (Utils.getConstructor (this));
		out.beginArray (getParameterCount (), Type.TYPE);
		for (int i = 0; i < getParameterCount (); i++)
		{
			out.beginArrayComponent (i);
			out.visitType (getParameterType (i));
			out.endArrayComponent ();
		}
		out.endArray ();
		out.visitInt (pathIn);
		out.visitInt (pathOut);
		out.visitInt (type);
		((SerializationWithRoutine) out).visitRoutine (routine);
		out.endMethod ();
	}

		
	@Override
	protected void findMatchesImpl
		(QueryState qs, MatchConsumer consumer, int arg, Frame frame)
	{
		VMXState.VMXFrame f = (VMXState.VMXFrame) frame;
		VMXState s = f.vmx;
		int n = getParameterCount ();
		for (int i = (type == EXPRESSION) ? 1 : 0; i < n; i++)
		{
			switch (getParameterType (i).getTypeId ())
			{
/*!!
#foreach ($type in $types)
$pp.setType($type)
				case TypeId.$pp.TYPE:
					s.${pp.prefix}push (qs.${pp.prefix}bound (i));
					break;
#end
!!*/
//!! #* Start of generated code
// generated
				case TypeId.BOOLEAN:
					s.ipush (qs.ibound (i));
					break;
// generated
				case TypeId.BYTE:
					s.ipush (qs.ibound (i));
					break;
// generated
				case TypeId.SHORT:
					s.ipush (qs.ibound (i));
					break;
// generated
				case TypeId.CHAR:
					s.ipush (qs.ibound (i));
					break;
// generated
				case TypeId.INT:
					s.ipush (qs.ibound (i));
					break;
// generated
				case TypeId.LONG:
					s.lpush (qs.lbound (i));
					break;
// generated
				case TypeId.FLOAT:
					s.fpush (qs.fbound (i));
					break;
// generated
				case TypeId.DOUBLE:
					s.dpush (qs.dbound (i));
					break;
// generated
				case TypeId.OBJECT:
					s.apush (qs.abound (i));
					break;
//!! *# End of generated code
			}
		}
		switch (type)
		{
			case EXPRESSION:
				s.apush (qs);
				s.ipush (0);
				VMXState.MatchConsumerInFrame mc = s.invokeInFrame (consumer, frame);
				s.apush (mc);
				s.ipush (arg);
				s.invoke (routine, -1, f.auth);
				mc.dispose ();
				break;
			case CONDITION:
				if (s.invoke (routine, -1, f.auth).iget () != 0)
				{
					consumer.matchFound (qs, arg);
				}
				break;
			case BLOCK:
				s.invoke (routine, -1, f.auth);
				consumer.matchFound (qs, arg);
				break;
			default:
				throw new AssertionError (type);
		}
	}

	
	@Override
	protected String paramString ()
	{
		return super.paramString () + ',' + type + ',' + routine;
	}

}
