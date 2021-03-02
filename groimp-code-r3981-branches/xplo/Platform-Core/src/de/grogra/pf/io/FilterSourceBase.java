
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

import de.grogra.util.*;
import de.grogra.pf.registry.*;

public abstract class FilterSourceBase implements FilterSource
{
	private final IOFlavor flavor;
	private final Registry registry;
	private final ModifiableMap metaData;
	private boolean autoProgress = true;
	private String progressText;
	private float progressValue = AUTO_PROGRESS;
	private float progressResolution = 0.01f;
	private ProgressMonitor monitor;


	public FilterSourceBase (IOFlavor flavor, Registry registry, ModifiableMap metaData)
	{
		this.flavor = flavor;
		this.registry = registry;
		this.metaData = (metaData == null) ? new StringMap () : metaData;
	}


	public final Filter getFilter ()
	{
		return null;
	}


	public final IOFlavor getFlavor ()
	{
		return flavor;
	}


	public final Registry getRegistry ()
	{
		return registry;
	}


	public <V> V getMetaData (MetaDataKey<V> key, V defaultValue)
	{
		return (V) metaData.get (key.toString (), defaultValue);
	}


	public <V> void setMetaData (MetaDataKey<V> key, V value)
	{
		metaData.put (key.toString (), value);
	}


	public void initProgressMonitor (ProgressMonitor monitor)
	{
		this.monitor = monitor;
	}


	protected final boolean useAutoProgress ()
	{
		return autoProgress;
	}


	public void setProgress (String text, float progress)
	{
		if (progress != AUTO_PROGRESS)
		{
			autoProgress = false;
			setProgress0 (text, progress);
		}
		else
		{
			setProgress0 (text, (progressValue == AUTO_PROGRESS)
						  ? ProgressMonitor.INDETERMINATE_PROGRESS
						  : progressValue);
		}
	}


	protected void setProgress0 (String text, float progress)
	{
		if (text == null)
		{
			text = progressText;
		}
		if (Utils.equal (text, progressText)
			&& (Math.abs (progress - progressValue) < progressResolution))
		{
			return;
		}
		progressText = text;
		progressValue = progress;
		setProgressImpl (text, progress);
	}


	protected void setProgressImpl (String text, float progress)
	{
		if (monitor != null)
		{
			monitor.setProgress (text, progress);
		}
	}

	
	@Override
	public String toString ()
	{
		return super.toString () + '[' + getSystemId () + ']';
	}
}
