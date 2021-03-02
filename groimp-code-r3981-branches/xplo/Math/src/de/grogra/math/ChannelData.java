
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

package de.grogra.math;

import javax.vecmath.*;
import de.grogra.xl.util.*;
import de.grogra.xl.util.EHashMap.IdentityEntry;
import de.grogra.xl.util.EHashMap.ObjectEntry;

public final class ChannelData extends de.grogra.math.Pool
{
	private ChannelMap map;
	private final EHashMap dataForMap;
	private final IdentityEntry<ChannelMap,ChannelData> dfmKey;
	private final ChannelData source;
	private ChannelData sink;
	private final ObjectList inMaps, inMapData;
	private ChannelData defaultData;
	private EHashMap properties = null;
	private final ObjectEntry<String,Object> propKey;

	private boolean[] valid = new boolean[Channel.getCurrentChannelCount ()];
	private float[] floatData = new float[Channel.getCurrentChannelCount ()];
	private Object[] objectData = de.grogra.util.Utils.OBJECT_0;

	private final ObjectList dependencies = new ObjectList ();
	private final IntList usedDepChannels = new IntList ();
	private final Entry[] entryPool;
	private final ObjectEntry<String,Object>[] propertiesEntryPool;
	private final ObjectList depMapPool;
	private final ObjectList dataPool;
	private final ObjectList propertiesPool;
	private int usedDataCount = 0;

	
	private static final class Entry extends EHashMap.Entry
	{
		ChannelData dependent;
		int dependentChannel;

		
		void set (ChannelData dependent, int dependentChannel)
		{
			this.dependent = dependent;
			this.dependentChannel = dependentChannel;
			hashCode = dependent.hashCode () * 31 + dependentChannel;
		}
	
		
		@Override
		public void clear ()
		{
			dependent = null;
		}

		
		@Override
		public boolean keyEquals (EHashMap.Entry entry)
		{
			Entry e = (Entry) entry;
			return (e.dependent == dependent)
				&& (e.dependentChannel == dependentChannel);
		}

		
		@Override
		public void copyValue (EHashMap.Entry entry)
		{
			throw new AssertionError ();
		}

		
		@Override
		public String toString ()
		{
			return '[' + dependent.toString () + '.' + dependentChannel + ']';
		}
	}


	public ChannelData ()
	{
		this.map = null;
		this.source = null;
		this.defaultData = this;
		this.dataForMap = new EHashMap ();
		this.dfmKey = new EHashMap.IdentityEntry ();
		this.propKey = new ObjectEntry ();
		this.inMaps = null;
		this.inMapData = null;
		this.entryPool = new Entry[1];
		this.propertiesEntryPool = new ObjectEntry[1];
		this.depMapPool = new ObjectList ();
		this.dataPool = new ObjectList ();
		this.propertiesPool = new ObjectList ();
	}


	public void clear ()
	{
		assert source == null;
		dataForMap.clear ();
		clearImpl ();
		for (int i = usedDataCount - 1; i >= 0; i--)
		{
			((ChannelData) dataPool.get (i)).clearImpl ();
		}
		usedDataCount = 0;
		this.defaultData = this;
		sink = null;
	}
	
	
	private void clearImpl ()
	{
		map = null;
		defaultData = null;
		if (properties != null)
		{
			properties.clear ();
			propertiesPool.add (properties);
			properties = null;
		}
		while (!usedDepChannels.isEmpty ())
		{
			EHashMap m = (EHashMap) dependencies.set (usedDepChannels.pop (), null);
			depMapPool.add (m);
			m.clear ();
		}
		BooleanList.clear (valid, 0, valid.length);
		ObjectList.clear (objectData, 0, objectData.length);
		if (inMaps != null)
		{
			inMaps.clear ();
			inMapData.clear ();
		}
		requesting = null;
	}


	public void invalidate ()
	{
		assert source == null;
		BooleanList.clear (valid, 0, valid.length);
		for (int i = usedDataCount - 1; i >= 0; i--)
		{
			boolean[] v = ((ChannelData) dataPool.get (i)).valid; 
			BooleanList.clear (v, 0, v.length);
		}
	}


	public ChannelData initDefault (ChannelMap map)
	{
		if (defaultData.map != map)
		{
			if (defaultData != this)
			{
				throw new IllegalStateException ();
			}
			defaultData = createChannelData (map);
		}
		return defaultData;
	}


	private ChannelData (ChannelData source, ChannelMap map)
	{
		this.map = map;
		this.source = source;
		this.defaultData = null;
		this.dataForMap = source.dataForMap;
		this.dfmKey = source.dfmKey;
		this.entryPool = source.entryPool;
		this.propertiesEntryPool = source.propertiesEntryPool;
		this.propKey = source.propKey;
		this.depMapPool = source.depMapPool;
		this.propertiesPool = source.propertiesPool;
		this.dataPool = null;
		this.inMaps = new ObjectList (4, false);
		this.inMapData = new ObjectList (4);
	}

	
	private ChannelData createChannelData (ChannelMap map)
	{
		assert source == null;
		ChannelData cd;
		if (usedDataCount < dataPool.size)
		{
			cd = (ChannelData) dataPool.get (usedDataCount);
			cd.map = map;
		}
		else
		{
			cd = new ChannelData (this, map);
			dataPool.add (cd);
		}
		usedDataCount++;
		IdentityEntry<ChannelMap,ChannelData> e = (IdentityEntry<ChannelMap,ChannelData>) dataForMap.popEntryFromPool ();
		if (e == null)
		{
			e = new IdentityEntry<ChannelMap,ChannelData> ();
		}
		e.setKey (map);
		e.value = cd;
		dataForMap.put (e);
		return cd;
	}

	
	public ChannelMap getMap ()
	{
		return map;
	}


	public void setProperty (String name, Object value)
	{
		if (properties == null)
		{
			if (propertiesPool.isEmpty ())
			{
				properties = new EHashMap (propertiesEntryPool, 10, 0.75f);
			}
			else
			{
				properties = (EHashMap) propertiesPool.pop ();
			}
		}
		ObjectEntry<String,Object> e = (ObjectEntry<String,Object>) properties.popEntryFromPool ();
		if (e == null)
		{
			e = new ObjectEntry<String,Object> ();
		}
		e.setKey (name);
		e.value = value;
		properties.put (e);
	}


	public Object getProperty (String name)
	{
		if (properties == null)
		{
			return null;
		}
		propKey.setKey (name);
		ObjectEntry<String,Object> e = (ObjectEntry<String,Object>) properties.get (propKey);
		return (e != null) ? e.value : null;
	}


	public boolean isValid (int channel)
	{
		return (channel < valid.length) && valid[channel];
	}


	private ChannelData requesting;
	private int requestedChannel;

	public float getFloatValue (ChannelData req, int channel)
	{
		if ((channel < valid.length) && valid[channel])
		{
			if (req != null)
			{
				setDependency (channel, req, req.requestedChannel);
			}
			return floatData[channel];
		}
		else if (map != null)
		{
			ChannelData oldReq = requesting;
			int oldChannel = requestedChannel;
			requesting = req;
			requestedChannel = channel;
			float v = map.getFloatValue (this, channel);
			if (requestedChannel >= 0)
			{
				setFloat (channel, v);
			}
			requesting = oldReq;
			requestedChannel = oldChannel;
			return v;
		}
		else
		{
			return 0;
		}
	}


	public float forwardGetFloatValue (ChannelData source)
	{
		int c = requestedChannel;
		requestedChannel = -1;
		return source.getFloatValue (requesting, c);
	}


	public float getValidFloatValue (int channel)
	{
		return floatData[channel];
	}


	public Object getObjectValue (ChannelData req, int channel)
	{
		if ((channel < valid.length) && valid[channel])
		{
			if (req != null)
			{
				setDependency (channel, req, req.requestedChannel);
			}
			return objectData[channel];
		}
		else if (map != null)
		{
			ChannelData oldReq = requesting;
			int oldChannel = requestedChannel;
			requesting = req;
			requestedChannel = channel;
			Object v = map.getObjectValue (this, channel);
			if (requestedChannel >= 0)
			{
				setObject (channel, v);
			}
			requesting = oldReq;
			requestedChannel = oldChannel;
			return v;
		}
		else
		{
			return null;
		}
	}


	public Object forwardGetObjectValue (ChannelData source)
	{
		int c = requestedChannel;
		requestedChannel = -1;
		return source.getObjectValue (requesting, c);
	}


	public Object getValidObjectValue (int channel)
	{
		return objectData[channel];
	}

	private void setDependency (int channel,
								ChannelData dependent, int dependentChannel)
	{
		EHashMap m = (EHashMap) dependencies.get (channel);
		if (m == null)
		{
			if (depMapPool.isEmpty ())
			{
				m = new EHashMap (entryPool, 40, 0.5f);
			}
			else
			{
				m = (EHashMap) depMapPool.pop ();
			}
			dependencies.set (channel, m);
			usedDepChannels.add (channel);
		}
		Entry e = (Entry) m.popEntryFromPool ();
		if (e == null)
		{
			e = new Entry ();
		}
		e.set (dependent, dependentChannel);
		m.getOrPut (e);
	}


	private void invalidateDep (int channel, boolean indirect)
	{
		EHashMap m = (EHashMap) dependencies.get (channel);
		if (m == null)
		{
			return;
		}
		for (Entry e = (Entry) m.getFirstEntry (); e != null;
			 e = (Entry) e.listNext)
		{
			ChannelData dep = e.dependent;
			if ((indirect || (dep != this))
				&& dep.setValid (e.dependentChannel, false))
			{
				dep.invalidateDep (e.dependentChannel, true);
			}
		}
	}


	public void getTuple3f (Tuple3f out, ChannelData req, int channel)
	{
		out.x = getFloatValue (req, channel);
		out.y = getFloatValue (req, channel + 1);
		out.z = getFloatValue (req, channel + 2);
	}


	private boolean setValid (int channel, boolean value)
	{
		boolean old;
		boolean[] a = valid;
		if (value)
		{
			if (channel >= a.length)
			{
				System.arraycopy (a, 0, valid = new boolean[channel + 1], 0,
								  a.length);
				a = valid;
				old = false;
			}
			else
			{
				old = a[channel];
			}
			a[channel] = true;
		}
		else if (channel < a.length)
		{
			old = a[channel];
			a[channel] = false;
			if (channel < objectData.length)
			{
				objectData[channel] = null;
			}
		}
		else
		{
			old = false;
		}
		return old;
	}


	public void setFloat (int channel, float value)
	{
		float[] a;
		if (channel >= (a = floatData).length)
		{
			System.arraycopy (a, 0, floatData = new float[channel + 1], 0,
							  a.length);
			a = floatData;
		}
		float old = a[channel];
		a[channel] = value;
		if (setValid (channel, true) && (old != value))
		{
			invalidateDep (channel, false);
		}
		if (source != null)
		{
			if (requesting != null)
			{
				setDependency (channel, requesting, requesting.requestedChannel);
			}
			if (channel != requestedChannel)
			{
				setDependency (requestedChannel, this, channel);
			}
		}
	}


	public void setObject (int channel, Object value)
	{
		Object[] a;
		if (channel >= (a = objectData).length)
		{
			System.arraycopy (a, 0, objectData = new Object[channel + 1], 0,
							  a.length);
			a = objectData;
		}
		Object old = a[channel];
		a[channel] = value;
		if (setValid (channel, true) && (old != value))
		{
			invalidateDep (channel, false);
		}
		if (source != null)
		{
			if (requesting != null)
			{
				setDependency (channel, requesting, requesting.requestedChannel);
			}
			if (channel != requestedChannel)
			{
				setDependency (requestedChannel, this, channel);
			}
		}
	}


	public void setTuple3f (int channel, Tuple3f value)
	{
		setFloat (channel, value.x);
		setFloat (channel + 1, value.y);
		setFloat (channel + 2, value.z);
	}


	public void setTuple3f (int channel, float x, float y, float z)
	{
		setFloat (channel, x);
		setFloat (channel + 1, y);
		setFloat (channel + 2, z);
	}


	public void setTuple2f (int channel, Tuple2f value)
	{
		setFloat (channel, value.x);
		setFloat (channel + 1, value.y);
	}


	public void setTuple2f (int channel, float x, float y)
	{
		setFloat (channel, x);
		setFloat (channel + 1, y);
	}


	public ChannelData createSink (ChannelMap map)
	{
		return sink = getData (map, false);
	}


	public ChannelData getSink ()
	{
		return sink;
	}


	public ChannelData getData (ChannelMap map)
	{
		return getData (map, true);
	}


	private ChannelData getData (ChannelMap map, boolean noSink)
	{
		if (map == null)
		{
			return (this == source.defaultData) ? source : source.defaultData;
		}
		if (noSink)
		{
			int i = inMaps.indexOf (map);
			if (i >= 0)
			{
				return (ChannelData) inMapData.get (i);
			}
		}
		dfmKey.setKey (map);
		EHashMap.Entry e = dataForMap.get (dfmKey);
		ChannelData c;
		if (e != null)
		{
			c = (ChannelData) ((IdentityEntry) e).value;
		}
		else
		{
			c = (noSink ? source : this).createChannelData (map);
		}
		if (noSink)
		{
			inMaps.add (map);
			inMapData.add (c);
		}
		return c;
	}


	@Override
	public String toString ()
	{
		return "ChannelData@" + Integer.toHexString (hashCode ()) + '[' + map + ']';
	}

}
