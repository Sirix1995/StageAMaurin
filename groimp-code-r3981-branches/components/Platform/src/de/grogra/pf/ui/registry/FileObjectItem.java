
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

import de.grogra.graph.impl.Node.NType;
import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ProgressMonitor;
import de.grogra.pf.registry.LazyObjectItem;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.Workbench;
import de.grogra.util.MimeType;
import de.grogra.util.StringMap;

public class FileObjectItem extends LazyObjectItem
{
	MimeType mimeType;
	//enh:field getter

	String systemId;
	//enh:field getter

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field mimeType$FIELD;
	public static final NType.Field systemId$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (FileObjectItem.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((FileObjectItem) o).mimeType = (MimeType) value;
					return;
				case 1:
					((FileObjectItem) o).systemId = (String) value;
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
					return ((FileObjectItem) o).getMimeType ();
				case 1:
					return ((FileObjectItem) o).getSystemId ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new FileObjectItem ());
		$TYPE.addManagedField (mimeType$FIELD = new _Field ("mimeType", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (MimeType.class), null, 0));
		$TYPE.addManagedField (systemId$FIELD = new _Field ("systemId", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 1));
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
		return new FileObjectItem ();
	}

	public MimeType getMimeType ()
	{
		return mimeType;
	}

	public String getSystemId ()
	{
		return systemId;
	}

//enh:end

	private FileObjectItem ()
	{
		super (null, true);
	}


	public FileObjectItem (String systemId, MimeType mimeType, Object object,
						   String type)
	{
		super (IO.toSimpleName (systemId), true);
		this.systemId = systemId;
		this.mimeType = mimeType;
		setBaseObject (object);
		setType (type);
	}


	public FileObjectItem (FileSource fs, Object object, String type)
	{
		this (fs.getSystemId (), fs.getFlavor ().getMimeType (), object, type);
	}


	public FileSource createFileSource ()
	{
		String id = systemId;
		if (getRegistry ().isRootRegistry ())
		{
			id = "pluginfs:" + getPluginDescriptor ().getName () + '/' + id;
		}
		return FileSource.createFileSource (id, mimeType, this, new StringMap (this));
	}


	@Override
	protected void activateImpl ()
	{
		Object f = getRegistry ().getProjectFile (systemId);
		if (f != null)
		{
			getRegistry ().getFileSystem ().setMimeType (f, mimeType);
		}
	}

	
	@Override
	protected boolean hasNullValue ()
	{
		return false;
	}


	@Override
	protected Object fetchBaseObject ()
	{
		FilterSource s = IO.createPipeline
			(createFileSource (),
			 IOFlavor.valueOf (getObjectType ().getImplementationClass ()));
		if (!(s instanceof ObjectSource))
		{
			Workbench.current ().logGUIInfo
				(IO.I18N.msg ("openfile.unsupported", systemId,
							  IO.getDescription (mimeType)));
			return null;
		}
		s.initProgressMonitor
			(UI.createProgressAdapter (Workbench.current ()));
		try
		{
			return ((ObjectSource) s).getObject ();
		}
		catch (IOException e)
		{
			Workbench.current ().logGUIInfo
				(IO.I18N.msg ("openfile.failed", systemId), e);
			return null;
		}
		finally
		{
			s.setProgress (null, ProgressMonitor.DONE_PROGRESS);
		}
	}


	@Override
	public void addRequiredFiles (java.util.Collection list)
	{
		Object f = getRegistry ().getProjectFile (systemId);
		if (f != null)
		{
			list.add (f);
		}
	}

}
