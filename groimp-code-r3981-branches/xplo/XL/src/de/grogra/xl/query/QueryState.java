
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

import de.grogra.reflect.TypeId;
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
import de.grogra.xl.util.DoubleList;
import de.grogra.xl.util.EHashMap;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.LongList;
import de.grogra.xl.util.ObjectList;

public class QueryState
{
	public static final class Break extends Error
	{
		String predicateId;
		int pointer;
	}


	public static final int BINDING_MISMATCHED = 0;
	public static final int BINDING_MATCHED = 1;
	public static final int BINDING_PERFORMED = 2;

	
	public final ObjectList userStack0 = new ObjectList ();
	
	public final ObjectList userStack1 = new ObjectList ();
	
	public final IntList userIStack0 = new IntList ();
	
	private boolean breakMatching = false;
	
	private final Break breakAll = new Break ();

	final Graph graph;
	final RuntimeModel model;
	
	final Break breakPattern = new Break ();

	IntList istack = new IntList ();
	LongList lstack = new LongList ();
	FloatList fstack = new FloatList ();
	DoubleList dstack = new DoubleList ();
	ObjectList astack = new ObjectList ();
	
	private int sp;

	private Frame frame;

	private int vp;
	private int variableCount;
	private ObjectList<Variable> localVariables = new ObjectList<Variable> ();
	private IntList stackVariables = new IntList ();

	int newVariables (int length)
	{
		if (length < 0)
		{
			throw new IllegalArgumentException ();
		}
		int s = stackVariables.size ();
		localVariables.add (null);
		stackVariables.add (length);
		return s;
	}

	int mapVariables (int[] mapping)
	{
		int s = newVariables (mapping.length);
		for (int i = 0; i < mapping.length; i++)
		{
			mapVariable (mapping[i]);
		}
		setVariables (s);
		return s;
	}

	private void addVariable (Variable local)
	{
		localVariables.add (local);
		stackVariables.add (-1);
		local.unset (frame);
	}

	void addVariable ()
	{
		localVariables.add (null);
		stackVariables.add (sp);
		astack.set (sp++, this);
	}

	void mapVariable (int j)
	{
		if ((j < 0) || (j >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (j));
		}
		j = vp + j;
		int k = stackVariables.get (j);
		if (k >= 0)
		{
			localVariables.add (null);
			stackVariables.add (k);
		}
		else
		{
			localVariables.add (localVariables.get (j));
			stackVariables.add (-1);
		}
	}

	void setVariables (int fp)
	{
		variableCount = stackVariables.get (fp);
		vp = fp + 1;
	}

	int getVariables ()
	{
		return vp - 1;
	}

	Frame getFrame ()
	{
		return frame;
	}

	private Producer producer;
	
	private final NodeData[] entryPool = new NodeData[1];
	private final ObjectList<EHashMap<NodeData>> mapPool = new ObjectList<EHashMap<NodeData>> (16, false);

	private static final Object BOUND_MARK = new Object ();
	

	public QueryState (Graph extent)
	{
		this.graph = extent;
		this.model = extent.getModel ();
	}


	public Graph getGraph ()
	{
		return graph;
	}


	public RuntimeModel getModel ()
	{
		return model;
	}

	
	public void initialize ()
	{
		breakMatching = false;
	}

	
	public void breakMatching ()
	{
		breakMatching = true;
	}
	
	EHashMap<NodeData> allocateNodeMap ()
	{
		if (mapPool.isEmpty ())
		{
			return new EHashMap<NodeData> (entryPool, 32, 0.75f);
		}
		else
		{
			return mapPool.pop ();
		}
	}
	
	
	void disposeNodeMap (EHashMap<NodeData> map)
	{
		map.clear ();
		mapPool.add (map);
	}
	

	int enter (int size)
	{
		int s = sp;
		sp += size;
		return s;
	}

	
	void leave (int fp)
	{
		sp = fp;
	}

	int getSp ()
	{
		return sp;
	}

	boolean forProduction;
	private Object matchConsumer;
	private CompoundPattern pred;
	private Pattern.Matcher matcher;
	private int matcherSp;

	int deleteSp;

	private EHashMap<NodeData> nodeMap;

	private final NodeData nodeKey = new NodeData ();

	void findMatches
		(CompoundPattern pred, Pattern.Matcher matcher, Frame frame,
		 Variable[] variables, Object consumer, boolean forProduction)
	{
		this.frame = frame;
		sp = 0;
		int s = newVariables (variables.length);
		for (int i = 0; i < variables.length; i++)
		{
			if (variables[i] != null)
			{
				addVariable (variables[i]);
			}
			else
			{
				addVariable ();
			}
		}
		setVariables (s);
		
		this.matchConsumer = consumer;
		this.pred = pred;
		this.matcher = matcher;
		this.forProduction = forProduction;

		this.matcherSp = sp;
		try
		{
			matcher.findMatches (this, CONSUMER, s);
		}
		catch (Break e)
		{
			if (e != breakAll)
			{
				throw e;
			}
		}
		finally
		{
			istack.clear ();
			lstack.clear ();
			fstack.clear ();
			dstack.clear ();
			astack.clear ();
			localVariables.clear ();
			stackVariables.clear ();
			frame = null;
			dispose ();
		}
	}

	protected void dispose ()
	{
	}

	private static final MatchConsumer CONSUMER = new MatchConsumer ()
	{
		public void matchFound (QueryState qs, int arg)
		{
			qs.matchFoundImpl (arg);
		}
	};

	void matchFoundImpl (int s)
	{
		int fp = vp - 1;
		try
		{
			setVariables (s);
			if (forProduction)
			{
				nodeMap = CompoundPattern.Matcher.getNodeMap (this, matcherSp);
				if (producer == null)
				{
					producer = graph.createProducer (this);
				}
				((ObjectConsumer) matchConsumer).consume (producer);
			}
			else if (pred.getOutParameter () >= 0)
			{
				switch (pred.getParameterType (pred.getOutParameter ()).getTypeId ())
				{
/*!!
#foreach ($type in $types)
$pp.setType($type)
					case TypeId.$pp.TYPE:
						((${pp.Type}Consumer) matchConsumer).consume (($type) (${pp.prefix}bound (pred.getOutParameter ()) $pp.vm2type));
						break;
#end
!!*/
//!! #* Start of generated code
// generated
					case TypeId.BOOLEAN:
						((BooleanConsumer) matchConsumer).consume ((boolean) (ibound (pred.getOutParameter ())  != 0));
						break;
// generated
					case TypeId.BYTE:
						((ByteConsumer) matchConsumer).consume ((byte) (ibound (pred.getOutParameter ()) ));
						break;
// generated
					case TypeId.SHORT:
						((ShortConsumer) matchConsumer).consume ((short) (ibound (pred.getOutParameter ()) ));
						break;
// generated
					case TypeId.CHAR:
						((CharConsumer) matchConsumer).consume ((char) (ibound (pred.getOutParameter ()) ));
						break;
// generated
					case TypeId.INT:
						((IntConsumer) matchConsumer).consume ((int) (ibound (pred.getOutParameter ()) ));
						break;
// generated
					case TypeId.LONG:
						((LongConsumer) matchConsumer).consume ((long) (lbound (pred.getOutParameter ()) ));
						break;
// generated
					case TypeId.FLOAT:
						((FloatConsumer) matchConsumer).consume ((float) (fbound (pred.getOutParameter ()) ));
						break;
// generated
					case TypeId.DOUBLE:
						((DoubleConsumer) matchConsumer).consume ((double) (dbound (pred.getOutParameter ()) ));
						break;
// generated
					case TypeId.OBJECT:
						((ObjectConsumer) matchConsumer).consume ((Object) (abound (pred.getOutParameter ()) ));
						break;
//!! *# End of generated code
				}
			}
			else
			{
				((VoidConsumer) matchConsumer).consume ();
			}
		}
		finally
		{
			setVariables (fp);
		}
		if (breakMatching)
		{
			throw breakAll;
		}
	}

	
	public void visitMatch (Producer prod)
	{
		deleteSp = matcherSp;
		matcher.visitMatch (this, prod);
	}
	
	public NodeData getFirstNodeData ()
	{
		return nodeMap.getFirstEntry ();
	}
	

	public NodeData getNodeData (Object node)
	{
		nodeKey.setNode (node);
		return nodeMap.get (nodeKey);
	}


	public boolean hasInVariable ()
	{
		return pred.getInParameter () >= 0;
	}

	
	public boolean hasOutVariable ()
	{
		return pred.getOutParameter () >= 0;
	}

	
	public Object getInValue ()
	{
		return (pred.getInParameter () >= 0) ? abound (pred.getInParameter ()) : null;
	}

	
	public Object getOutValue ()
	{
		return (pred.getOutParameter () >= 0) ? abound (pred.getOutParameter ()) : null;
	}


/*!!
#foreach ($type in $vmtypes)
$pp.setType($type)

	public $type ${pp.prefix}bound (int index)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			return ${pp.prefix}stack.get (i);
		}
		else
		{
			return localVariables.get (vp + index).${pp.prefix}get (frame);
		}
	}
	
	public int ${pp.prefix}bind (int index, $type value)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			Object o = astack.get (i);
			if (o == this)
			{
#if ($pp.Object)
				istack.set (i, 1);
#else
				astack.set (i, BOUND_MARK);
#end
				${pp.prefix}stack.set (i, value);
				return BINDING_PERFORMED;
			}
			else
			{
				return
#if ($pp.Object)
				 	((o == value) || ((value != null) && value.equals (o)))
#else
				 	(${pp.prefix}stack.get (i) == value)
#end
					? BINDING_MATCHED : BINDING_MISMATCHED;
			}
		}
		else
		{
			Variable v = localVariables.get (vp + index);
			if (v.isSet (frame))
			{
#if ($pp.Object)
				Object o = v.aget (frame);
#end
				return
#if ($pp.Object)
				 	((o == value) || ((value != null) && value.equals (o)))
#else
				 	(v.${pp.prefix}get (frame) == value)
#end
					? BINDING_MATCHED : BINDING_MISMATCHED;
			}
			else
			{
				v.${pp.prefix}set (frame, value);
				return BINDING_PERFORMED;
			}
		}
	}


	public void ${pp.prefix}match (int index, $type value,
								   MatchConsumer consumer, int arg)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			Object o = astack.get (i);
			if (o == this)
			{
#if ($pp.Object)
				istack.set (i, 1);
#else
				astack.set (i, BOUND_MARK);
#end
				${pp.prefix}stack.set (i, value);
				try
				{
					consumer.matchFound (this, arg);
				}
				finally
				{
					astack.set (i, this);
				}
			}
			else if
#if ($pp.Object)
				 	((o == value) || ((value != null) && value.equals (o)))
#else
				 	(${pp.prefix}stack.get (i) == value)
#end
			{
				consumer.matchFound (this, arg);
			}
		}
		else
		{
			Variable v = localVariables.get (vp + index);
			if (v.isSet (frame))
			{
#if ($pp.Object)
				Object o = v.aget (frame);
#end
				if
#if ($pp.Object)
				 	((o == value) || ((value != null) && value.equals (o)))
#else
				 	(v.${pp.prefix}get (frame) == value)
#end
				{
					consumer.matchFound (this, arg);
				}
			}
			else
			{
				v.${pp.prefix}set (frame, value);
				try
				{
					consumer.matchFound (this, arg);
				}
				finally
				{
					v.unset (frame);
				}
			}
		}
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public int ibound (int index)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			return istack.get (i);
		}
		else
		{
			return localVariables.get (vp + index).iget (frame);
		}
	}
	
	public int ibind (int index, int value)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			Object o = astack.get (i);
			if (o == this)
			{
				astack.set (i, BOUND_MARK);
				istack.set (i, value);
				return BINDING_PERFORMED;
			}
			else
			{
				return
				 	(istack.get (i) == value)
					? BINDING_MATCHED : BINDING_MISMATCHED;
			}
		}
		else
		{
			Variable v = localVariables.get (vp + index);
			if (v.isSet (frame))
			{
				return
				 	(v.iget (frame) == value)
					? BINDING_MATCHED : BINDING_MISMATCHED;
			}
			else
			{
				v.iset (frame, value);
				return BINDING_PERFORMED;
			}
		}
	}
// generated
// generated
	public void imatch (int index, int value,
								   MatchConsumer consumer, int arg)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			Object o = astack.get (i);
			if (o == this)
			{
				astack.set (i, BOUND_MARK);
				istack.set (i, value);
				try
				{
					consumer.matchFound (this, arg);
				}
				finally
				{
					astack.set (i, this);
				}
			}
			else if
				 	(istack.get (i) == value)
			{
				consumer.matchFound (this, arg);
			}
		}
		else
		{
			Variable v = localVariables.get (vp + index);
			if (v.isSet (frame))
			{
				if
				 	(v.iget (frame) == value)
				{
					consumer.matchFound (this, arg);
				}
			}
			else
			{
				v.iset (frame, value);
				try
				{
					consumer.matchFound (this, arg);
				}
				finally
				{
					v.unset (frame);
				}
			}
		}
	}
// generated
// generated
// generated
	public long lbound (int index)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			return lstack.get (i);
		}
		else
		{
			return localVariables.get (vp + index).lget (frame);
		}
	}
	
	public int lbind (int index, long value)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			Object o = astack.get (i);
			if (o == this)
			{
				astack.set (i, BOUND_MARK);
				lstack.set (i, value);
				return BINDING_PERFORMED;
			}
			else
			{
				return
				 	(lstack.get (i) == value)
					? BINDING_MATCHED : BINDING_MISMATCHED;
			}
		}
		else
		{
			Variable v = localVariables.get (vp + index);
			if (v.isSet (frame))
			{
				return
				 	(v.lget (frame) == value)
					? BINDING_MATCHED : BINDING_MISMATCHED;
			}
			else
			{
				v.lset (frame, value);
				return BINDING_PERFORMED;
			}
		}
	}
// generated
// generated
	public void lmatch (int index, long value,
								   MatchConsumer consumer, int arg)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			Object o = astack.get (i);
			if (o == this)
			{
				astack.set (i, BOUND_MARK);
				lstack.set (i, value);
				try
				{
					consumer.matchFound (this, arg);
				}
				finally
				{
					astack.set (i, this);
				}
			}
			else if
				 	(lstack.get (i) == value)
			{
				consumer.matchFound (this, arg);
			}
		}
		else
		{
			Variable v = localVariables.get (vp + index);
			if (v.isSet (frame))
			{
				if
				 	(v.lget (frame) == value)
				{
					consumer.matchFound (this, arg);
				}
			}
			else
			{
				v.lset (frame, value);
				try
				{
					consumer.matchFound (this, arg);
				}
				finally
				{
					v.unset (frame);
				}
			}
		}
	}
// generated
// generated
// generated
	public float fbound (int index)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			return fstack.get (i);
		}
		else
		{
			return localVariables.get (vp + index).fget (frame);
		}
	}
	
	public int fbind (int index, float value)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			Object o = astack.get (i);
			if (o == this)
			{
				astack.set (i, BOUND_MARK);
				fstack.set (i, value);
				return BINDING_PERFORMED;
			}
			else
			{
				return
				 	(fstack.get (i) == value)
					? BINDING_MATCHED : BINDING_MISMATCHED;
			}
		}
		else
		{
			Variable v = localVariables.get (vp + index);
			if (v.isSet (frame))
			{
				return
				 	(v.fget (frame) == value)
					? BINDING_MATCHED : BINDING_MISMATCHED;
			}
			else
			{
				v.fset (frame, value);
				return BINDING_PERFORMED;
			}
		}
	}
// generated
// generated
	public void fmatch (int index, float value,
								   MatchConsumer consumer, int arg)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			Object o = astack.get (i);
			if (o == this)
			{
				astack.set (i, BOUND_MARK);
				fstack.set (i, value);
				try
				{
					consumer.matchFound (this, arg);
				}
				finally
				{
					astack.set (i, this);
				}
			}
			else if
				 	(fstack.get (i) == value)
			{
				consumer.matchFound (this, arg);
			}
		}
		else
		{
			Variable v = localVariables.get (vp + index);
			if (v.isSet (frame))
			{
				if
				 	(v.fget (frame) == value)
				{
					consumer.matchFound (this, arg);
				}
			}
			else
			{
				v.fset (frame, value);
				try
				{
					consumer.matchFound (this, arg);
				}
				finally
				{
					v.unset (frame);
				}
			}
		}
	}
// generated
// generated
// generated
	public double dbound (int index)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			return dstack.get (i);
		}
		else
		{
			return localVariables.get (vp + index).dget (frame);
		}
	}
	
	public int dbind (int index, double value)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			Object o = astack.get (i);
			if (o == this)
			{
				astack.set (i, BOUND_MARK);
				dstack.set (i, value);
				return BINDING_PERFORMED;
			}
			else
			{
				return
				 	(dstack.get (i) == value)
					? BINDING_MATCHED : BINDING_MISMATCHED;
			}
		}
		else
		{
			Variable v = localVariables.get (vp + index);
			if (v.isSet (frame))
			{
				return
				 	(v.dget (frame) == value)
					? BINDING_MATCHED : BINDING_MISMATCHED;
			}
			else
			{
				v.dset (frame, value);
				return BINDING_PERFORMED;
			}
		}
	}
// generated
// generated
	public void dmatch (int index, double value,
								   MatchConsumer consumer, int arg)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			Object o = astack.get (i);
			if (o == this)
			{
				astack.set (i, BOUND_MARK);
				dstack.set (i, value);
				try
				{
					consumer.matchFound (this, arg);
				}
				finally
				{
					astack.set (i, this);
				}
			}
			else if
				 	(dstack.get (i) == value)
			{
				consumer.matchFound (this, arg);
			}
		}
		else
		{
			Variable v = localVariables.get (vp + index);
			if (v.isSet (frame))
			{
				if
				 	(v.dget (frame) == value)
				{
					consumer.matchFound (this, arg);
				}
			}
			else
			{
				v.dset (frame, value);
				try
				{
					consumer.matchFound (this, arg);
				}
				finally
				{
					v.unset (frame);
				}
			}
		}
	}
// generated
// generated
// generated
	public Object abound (int index)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			return astack.get (i);
		}
		else
		{
			return localVariables.get (vp + index).aget (frame);
		}
	}
	
	public int abind (int index, Object value)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			Object o = astack.get (i);
			if (o == this)
			{
				istack.set (i, 1);
				astack.set (i, value);
				return BINDING_PERFORMED;
			}
			else
			{
				return
				 	((o == value) || ((value != null) && value.equals (o)))
					? BINDING_MATCHED : BINDING_MISMATCHED;
			}
		}
		else
		{
			Variable v = localVariables.get (vp + index);
			if (v.isSet (frame))
			{
				Object o = v.aget (frame);
				return
				 	((o == value) || ((value != null) && value.equals (o)))
					? BINDING_MATCHED : BINDING_MISMATCHED;
			}
			else
			{
				v.aset (frame, value);
				return BINDING_PERFORMED;
			}
		}
	}
// generated
// generated
	public void amatch (int index, Object value,
								   MatchConsumer consumer, int arg)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			Object o = astack.get (i);
			if (o == this)
			{
				istack.set (i, 1);
				astack.set (i, value);
				try
				{
					consumer.matchFound (this, arg);
				}
				finally
				{
					astack.set (i, this);
				}
			}
			else if
				 	((o == value) || ((value != null) && value.equals (o)))
			{
				consumer.matchFound (this, arg);
			}
		}
		else
		{
			Variable v = localVariables.get (vp + index);
			if (v.isSet (frame))
			{
				Object o = v.aget (frame);
				if
				 	((o == value) || ((value != null) && value.equals (o)))
				{
					consumer.matchFound (this, arg);
				}
			}
			else
			{
				v.aset (frame, value);
				try
				{
					consumer.matchFound (this, arg);
				}
				finally
				{
					v.unset (frame);
				}
			}
		}
	}
// generated
//!! *# End of generated code
	

	public int nullbind (int index)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			Object o = astack.get (i);
			if (o == this)
			{
				istack.set (i, 0);
				lstack.set (i, 0);
				fstack.set (i, Float.NaN);
				dstack.set (i, Float.NaN);
				astack.set (i, null);
				return BINDING_PERFORMED;
			}
			else
			{
				return BINDING_MISMATCHED;
			}
		}
		else
		{
			Variable v = localVariables.get (vp + index);
			if (v.isSet (frame))
			{
				return BINDING_MISMATCHED;
			}
			else
			{
				v.nullset (frame);
				return BINDING_PERFORMED;
			}
		}
	}

	public boolean isBound (int index)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			return astack.get (i) != this;
		}
		else
		{
			return localVariables.get (vp + index).isSet (frame); 
		}
	}


	public boolean isNull (int index)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			return (astack.get (i) == null) && (istack.get (i) == 0);
		}
		else
		{
			return localVariables.get (vp + index).isNull (frame);
		}
	}

	
	public void unbind (int index)
	{
		if ((index < 0) || (index >= variableCount))
		{
			throw new IndexOutOfBoundsException (Integer.toString (index));
		}
		int i = stackVariables.get (vp + index);
		if (i >= 0)
		{
			astack.set (i, this);
		}
		else
		{
			localVariables.get (vp + index).unset (frame); 
		}
	}


	void check (Break reason, String id, int pointer)
	{
		if ((reason != breakPattern) || (reason.pointer != pointer)
			|| (reason.predicateId != id))
		{
			throw reason;
		}
	}

	
	protected boolean excludeFromMatch (Object node, boolean context)
	{
		return false;
	}

	protected boolean allowsNoninjectiveMatches ()
	{
		return true;
	}

}
