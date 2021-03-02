
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

package de.grogra.pf.io;

import java.net.MalformedURLException;
import java.net.URL;

import de.grogra.util.Map;
import de.grogra.util.Utils;

/**
 * This is a base class for the implementation of
 * <code>Filter</code>s. Its implementation of <code>getMetaData</code>
 * forwards to {@link #getImpl(Object, Object)} firstly, then to
 * the {@link #item} which defines this filter, then to
 * the meta data of the {@link #source}, and returns the first value
 * which could be found this way.
 * 
 * @author Ole Kniemeyer
 */
public abstract class FilterBase implements Filter, FilterSource, Map
{
	/**
	 * The defining <code>FilterItem</code> for this filter.
	 */
	protected final FilterItem item;

	/**
	 * The data source of this filter.
	 */
	protected final FilterSource source;

	private String systemId;
	private IOFlavor flavor;
	private Map metaData = null;


	/**
	 * Creates a new <code>FilterBase</code>, initializes its
	 * <code>item</code> and <code>source</code> fields and
	 * sets the system id to the system id of the <code>source</code>.
	 * 
	 * @param item the defining <code>FilterItem</code>
	 * @param source the data source
	 */
	public FilterBase (FilterItem item, FilterSource source)
	{
		this.item = item;
		this.source = source;
		setSystemId (source.getSystemId ());
	}


	public final FilterSource getSource ()
	{
		return source;
	}


	public final Filter getFilter ()
	{
		return this;
	}


	public final de.grogra.pf.registry.Registry getRegistry ()
	{
		return source.getRegistry ();
	}


	public final IOFlavor getFlavor ()
	{
		return flavor;
	}


	protected final void setFlavor (IOFlavor flavor)
	{
		this.flavor = flavor;
	}


	public final String getSystemId ()
	{
		return systemId;
	}


	protected final void setSystemId (String systemId)
	{
		this.systemId = systemId;
	}

	
	public URL toURL () throws MalformedURLException
	{
		return IO.toURL (this, systemId);
	}


	public void initProgressMonitor (ProgressMonitor monitor)
	{
		source.initProgressMonitor (monitor);
	}


	public void setProgress (String text, float progress)
	{
		source.setProgress (text, progress);
	}


	/**
	 * Sets the internal meta data map to the specified map.
	 * 
	 * @param metaData a value for the internal meta data map
	 */
	protected void setMetaData (Map metaData)
	{
		this.metaData = metaData;
	}

	public Object get (Object key, Object defaultValue)
	{
		return (key instanceof MetaDataKey) ? getMetaData ((MetaDataKey) key, defaultValue) : null;
	}

	public <V> V getMetaData (MetaDataKey<V> key, V defaultValue)
	{
		Object o = getImpl (key, Map.DEFAULT_VALUE);
		if (o != Map.DEFAULT_VALUE)
		{
			return (V) o;
		}
		if ((item != null)
			&& ((o = item.get (key.toString (), Map.DEFAULT_VALUE)) != Map.DEFAULT_VALUE))
		{
			return (V) o;
		}
		return source.getMetaData (key, defaultValue);
	}

	public <V> void setMetaData (MetaDataKey<V> key, V value)
	{
		source.setMetaData (key, value);
	}

	/**
	 * This method is invoked by {@link #get(Object, Object)}. This default
	 * implementation forwards to the internal meta data map
	 * (see {@link #setMetaData(Map)}), or returns <code>defaultValue</code>
	 * if no such internal map has been set.
	 * 
	 * @param key a key
	 * @param defaultValue a default value
	 * @return the associated value, or <code>defaultValue</code>
	 */
	protected Object getImpl (MetaDataKey key, Object defaultValue)
	{
		return (metaData != null) ? metaData.get (key.toString (), defaultValue)
			: defaultValue;
	}

	@Override
	public String toString ()
	{
		return getClass ().getName () + '[' + systemId + ',' + flavor
			+ ',' + source + ']';
	}

}
