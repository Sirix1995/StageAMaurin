
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

package de.grogra.xl.query;

import de.grogra.xl.lang.BooleanConsumer;
import de.grogra.xl.lang.ByteConsumer;
import de.grogra.xl.lang.CharConsumer;
import de.grogra.xl.lang.DoubleConsumer;
import de.grogra.xl.lang.FloatConsumer;
import de.grogra.xl.lang.IntConsumer;
import de.grogra.xl.lang.LongConsumer;
import de.grogra.xl.lang.ObjectConsumer;
import de.grogra.xl.lang.ShortConsumer;
import de.grogra.xl.lang.VoidConsumer;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.XBitSet;

public final class Query implements BytecodeSerialization.Serializable
{
	private final CompoundPattern predicate;
	private final boolean forProduction;
	private final Variable queryState;
	private final Variable[] variables;


	public Query (CompoundPattern predicate, boolean forProduction,
				  Variable queryState, Variable[] variables)
	{
		this.predicate = predicate;
		this.forProduction = forProduction;
		this.queryState = queryState;
		this.variables = variables;
	}

	
	public void write (BytecodeSerialization out) throws java.io.IOException
	{
		out.beginMethod (Utils.getConstructor (this));
		predicate.write (out);
		out.visitBoolean (forProduction);
		out.visitObject (queryState);
		Utils.writeArray (out, variables);
		out.endMethod ();
	}


	private transient Pattern.Matcher matcher;

	private synchronized Pattern.Matcher getMatcher (Graph src)
	{
		if (matcher == null)
		{
			//matcher is a linked-list of Matchers for patterns in the query
			matcher = src.createMatcher (predicate, new XBitSet (), new IntList ());
			if (matcher == null)
			{
				throw new RuntimeException ("No predicate matcher computable");
			}
//			System.out.println (matcher);
		}
		return matcher;
	}

	
	private void findMatches (Object consumer, Frame frame, QueryState qs)
	{
		//'queryState' is an instance of VMXState.Local as a member in this Query instance. It serves as a pointer into the call-stack (VMXState).
		//The VMXState instance (i.e. the call-stack) sets the reference to 'queryState' in its member variable astack[].
		//This is to allow sub-routines to retrieve the local stack of the parent routines.
		queryState.aset (frame, qs);
		
		//multiscale begin debug
		//CompoundPattern.prodTime = 0;
		//long startQuery = System.nanoTime();
		//multiscale end debug
		
		qs.findMatches (predicate, getMatcher (qs.getGraph ()), frame, variables, consumer, forProduction);
		
		//multiscale begin debug
		//long endQuery = System.nanoTime();
		//long duration = endQuery-startQuery;
		//double seconds = (double)duration / 1000000000.0;
		
		//System.out.println("Query Time: " + (seconds-CompoundPattern.prodTime));
		//System.out.println("Prod Time: " + CompoundPattern.prodTime);
		//System.out.println("Combine Time: " + ((seconds-CompoundPattern.prodTime)+CompoundPattern.prodTime));
		//multiscale end debug
	}

	public static Graph currentGraph (RuntimeModel model)
	{
		Graph g = model.currentGraph ();
		if (g == null)
		{
			throw new RuntimeException ("No current extent for " + model);
		}
		return g;
	}

/*!!
#foreach ($type in $types_void)
$pp.setType($type)

	public void find${pp.Type}Matches (${pp.Type}Consumer cons, Frame frame, QueryState qs)
	{
		findMatches (cons, frame, qs); 
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public void findBooleanMatches (BooleanConsumer cons, Frame frame, QueryState qs)
	{
		findMatches (cons, frame, qs); 
	}
// generated
// generated
// generated
	public void findByteMatches (ByteConsumer cons, Frame frame, QueryState qs)
	{
		findMatches (cons, frame, qs); 
	}
// generated
// generated
// generated
	public void findShortMatches (ShortConsumer cons, Frame frame, QueryState qs)
	{
		findMatches (cons, frame, qs); 
	}
// generated
// generated
// generated
	public void findCharMatches (CharConsumer cons, Frame frame, QueryState qs)
	{
		findMatches (cons, frame, qs); 
	}
// generated
// generated
// generated
	public void findIntMatches (IntConsumer cons, Frame frame, QueryState qs)
	{
		findMatches (cons, frame, qs); 
	}
// generated
// generated
// generated
	public void findLongMatches (LongConsumer cons, Frame frame, QueryState qs)
	{
		findMatches (cons, frame, qs); 
	}
// generated
// generated
// generated
	public void findFloatMatches (FloatConsumer cons, Frame frame, QueryState qs)
	{
		findMatches (cons, frame, qs); 
	}
// generated
// generated
// generated
	public void findDoubleMatches (DoubleConsumer cons, Frame frame, QueryState qs)
	{
		findMatches (cons, frame, qs); 
	}
// generated
// generated
// generated
	public void findObjectMatches (ObjectConsumer cons, Frame frame, QueryState qs)
	{
		findMatches (cons, frame, qs); 
	}
// generated
// generated
// generated
	public void findVoidMatches (VoidConsumer cons, Frame frame, QueryState qs)
	{
		findMatches (cons, frame, qs); 
	}
// generated
//!! *# End of generated code

}
