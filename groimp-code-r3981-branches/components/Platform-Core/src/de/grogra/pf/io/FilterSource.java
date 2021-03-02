
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

import java.io.File;
import java.net.URL;

/**
 * Instances of <code>FilterSource</code> represent data sources in the
 * pipeline of {@link de.grogra.pf.io.Filter}s.
 * 
 * @author Ole Kniemeyer
 */
public interface FilterSource extends de.grogra.pf.registry.RegistryContext
{
	final class MetaDataKey<V>
	{
		private final String key;

		public MetaDataKey (String key)
		{
			key.getClass ();
			this.key = key;
		}

		public boolean equals (Object o)
		{
			return (o == this) || ((o instanceof MetaDataKey) && ((MetaDataKey) o).key.equals (key));
		}

		public int hashCode ()
		{
			return key.hashCode ();
		}

		public String toString ()
		{
			return key;
		}
	}

	MetaDataKey<File> DESTINATION_FILE = new MetaDataKey<File> ("destfile");

	MetaDataKey<URL> DESTINATION_URL = new MetaDataKey<URL> ("desturl");

	float AUTO_PROGRESS = -2;


	/**
	 * Returns the {@link Filter} from which this instance obtains its data.
	 * If there is no such filter, i.e., if this is the first object
	 * in the filter pipeline, this method returns <code>null</code>.
	 * 
	 * @return the filter from which data is obtained, or <code>null</code>
	 */
	Filter getFilter ();

	/**
	 * Returns the {@link IOFlavor} of this data source. Depending on the
	 * flavor, this instance has to implement corresponding subinterfaces
	 * of <code>FilterSource</code>.
	 * 
	 * @return the flavor of the data
	 */
	IOFlavor getFlavor ();

	/**
	 * Returns a system id which identifies this source.
	 * 
	 * @return system id
	 */
	String getSystemId ();

	<V> V getMetaData (MetaDataKey<V> key, V defaultValue);

	<V> void setMetaData (MetaDataKey<V> key, V value);

	void initProgressMonitor (ProgressMonitor monitor);

	void setProgress (String text, float progress);
}
