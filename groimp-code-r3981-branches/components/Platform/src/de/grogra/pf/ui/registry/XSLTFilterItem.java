
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

package de.grogra.pf.ui.registry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.XSLTFilter;
import de.grogra.pf.ui.Workbench;
import de.grogra.util.MimeType;

public class XSLTFilterItem extends FilterItem
{
	private MimeType outputType;
	//enh:field

	private String file;
	//enh:field

	private Templates templates;
	private boolean templatesSet;

	private IOFlavor outputFlavor;


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field outputType$FIELD;
	public static final NType.Field file$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (XSLTFilterItem.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((XSLTFilterItem) o).outputType = (MimeType) value;
					return;
				case 1:
					((XSLTFilterItem) o).file = (String) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((XSLTFilterItem) o).outputType;
				case 1:
					return ((XSLTFilterItem) o).file;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new XSLTFilterItem ());
		$TYPE.addManagedField (outputType$FIELD = new _Field ("outputType", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (MimeType.class), null, 0));
		$TYPE.addManagedField (file$FIELD = new _Field ("file", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 1));
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new XSLTFilterItem ();
	}

//enh:end

	private XSLTFilterItem ()
	{
		super ();
	}


	@Override
	protected IOFlavor getInputFlavorImpl (String mimeType)
	{
		return new IOFlavor (new MimeType (mimeType, null),
							 IOFlavor.SAX, null);
	}


	@Override
	protected FilterSource createFilterImpl (FilterSource source)
	{
		Templates t = getTemplates ();
		return (t != null)
			? new XSLTFilter (this, source, t, getOutputFlavor ())
			: null;
	}

	@Override
	protected boolean useLazyFilter ()
	{
		return true;
	}

	@Override
	protected IOFlavor getOutputFlavorImpl ()
	{
		return (outputType != null) ? new IOFlavor (outputType, IOFlavor.SAX, null)
			: super.getOutputFlavorImpl (); 
	}


	public Templates getTemplates ()
	{
		if (!templatesSet)
		{
			synchronized (this)
			{
				if (!templatesSet)
				{
					templatesSet = true;
					URL u = getClassLoader ().getResource (file);
					if (u != null)
					{
						try
						{
							InputStream in = u.openStream ();
							try
							{
								templates = TransformerFactory.newInstance ()
									.newTemplates (new StreamSource (in, file)); 
							}
							finally
							{
								in.close ();
							}
						}
						catch (TransformerConfigurationException e)
						{
							Workbench.current ().logInfo
								("Cannot configure transformer", e);
						}
						catch (IOException e)
						{
							Workbench.current ().logInfo
								("Error reading " + u, e);
						}
					}
				}
			}
		}
		return templates;
	}

}
