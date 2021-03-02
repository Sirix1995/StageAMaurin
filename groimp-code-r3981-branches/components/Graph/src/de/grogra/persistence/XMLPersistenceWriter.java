
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

package de.grogra.persistence;

import java.io.IOException;
import java.io.Serializable;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.util.IOWrapException;
import de.grogra.util.SAXElement;
import de.grogra.util.Utils;
import de.grogra.util.WrapException;

/**
 * An <code>XMLPersistenceWriter</code> serializes data into
 * XML elements and attributes. The output can be deserialized
 * by an instance of {@link de.grogra.persistence.XMLPersistenceReader}.
 *
 * @author Ole Kniemeyer
 */
public class XMLPersistenceWriter implements PersistenceOutput
{
	public static final String NAMESPACE = "http://grogra.de/xmlpersistence";
	public static final String NS_PREFIX = "gx";

	public static final Attributes NS_ATTRIBUTE;
	static
	{
		AttributesImpl a = new AttributesImpl ();
		a.addAttribute ("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns", "CDATA", NAMESPACE);
		a.addAttribute ("http://www.w3.org/2000/xmlns/", "xmlns:" + NS_PREFIX, "xmlns:" + NS_PREFIX, "CDATA",
						NAMESPACE);
		NS_ATTRIBUTE = a;
	}


	protected final PersistenceOutputListener listener;
	private final ContentHandler ch;

	private StringBuffer buffer = new StringBuffer ();


	private void add (String name, String qName, String value)
	{
		atts.addAttribute (NAMESPACE, name, qName, "CDATA", value);
	}


	public XMLPersistenceWriter (ContentHandler ch,
								 PersistenceOutputListener listener)
	{
		this.ch = ch;
		this.listener = listener;
	}

	
	public ContentHandler getContentHandler ()
	{
		return ch;
	}

	private void checkWS ()
	{
		int i;
		if ((i = buffer.length ()) > 0)
		{
			switch (buffer.charAt (i - 1))
			{
				case ' ':
				case ':':
				case '=':
				case '(':
				case ')':
				case '{':
				case '}':
				case '[':
				case ']':
					return;
			}
			buffer.append (' ');
		}
	}


	public void beginExtent (PersistenceManager manager) throws IOException
	{
	}


	public void endExtent () throws IOException
	{
	}


	public void writeName (String v)
	{
		checkWS ();
		buffer.append (v);
	}


	public void writeString (String v)
	{
		checkWS ();
		Utils.quote (v, buffer);
	}

	private AttributesImpl atts = new SAXElement ();
	private String fieldName;

	private boolean beginWrite (char prefix) throws IOException
	{
		switch (outputContext)
		{
			case BUFFER_LIST:
				checkWS ();
				if (prefix > 0)
				{
					buffer.append (prefix);
				}
				return true;
			case BUFFER_MAP:
				checkWS ();
				buffer.append (fieldName).append ('=');
				if (prefix > 0)
				{
					buffer.append (prefix);
				}
				return true;
			case ELEMENT_WITH_ATTRIBUTES:
				buffer.setLength (0);
				if (prefix > 0)
				{
					buffer.append (prefix);
				}
				return false;
			case ELEMENT_WITH_ELEMENTS:
				buffer.setLength (0);
				return false;
			default:
				throw new AssertionError ();
		}
	}


	private void endWrite (String name) throws IOException
	{
		switch (outputContext)
		{
			case ELEMENT_WITH_ATTRIBUTES:
				atts.addAttribute ("", fieldName, fieldName, "CDATA",
								   buffer.toString ());
				break;
			case ELEMENT_WITH_ELEMENTS:
				atts.clear ();
				if (fieldName != null)
				{
					add ("name", NS_PREFIX + ":name", fieldName);
				}
				if (!name.equals ("null"))
				{
					add ("value", NS_PREFIX + ":value", buffer.toString ());
				}
				startElement (name, atts);
				endElement (name);
				break;
		}
		fieldName = null;
	}


	public void writeBoolean (boolean v) throws IOException
	{
		beginWrite ((char) 0);
		buffer.append (v);
		endWrite ("data");
	}


	public void writeByte (int v) throws IOException
	{
		beginWrite ((char) 0);
		buffer.append (v);
		endWrite ("data");
	}


	public void writeChar (int v) throws IOException
	{
		beginWrite ((char) 0);
		if (v == '\'')
		{
			buffer.append ("'\\''");
		}
		else
		{
			buffer.append ('\'').append (v).append ('\'');
		}
		endWrite ("data");
	}


	public void writeShort (int v) throws IOException
	{
		beginWrite ((char) 0);
		buffer.append (v);
		endWrite ("data");
	}


	public void writeInt (int v) throws IOException
	{
		beginWrite ((char) 0);
		buffer.append (v);
		endWrite ("data");
	}


	public void writeLong (long v) throws IOException
	{
		beginWrite ((char) 0);
		buffer.append (v);
		endWrite ("data");
	}


	public void writeFloat (float v) throws IOException
	{
		beginWrite ((char) 0);
		buffer.append (v);
		endWrite ("data");
	}


	public void writeDouble (double v) throws IOException
	{
		beginWrite ((char) 0);
		buffer.append (v);
		endWrite ("data");
	}


	public void writeNullObject () throws IOException
	{
		beginWrite ((char) 0);
		buffer.append ("null");
		endWrite ("null");
	}


	public void writeStringObject (String value) throws IOException
	{
		writeString0 (value, "data");
	}


	private void writeString0 (String value, String name) throws IOException
	{
		boolean b = beginWrite ((char) 0);
		if ("null".equals (value))
		{
			buffer.append ("\"null\"");
		}
		else if (b || (value.indexOf ('"') >= 0))
		{
			Utils.quote (value, buffer);
		}
		else
		{
			buffer.append (value);
		}
		endWrite (name);
	}


	public void writeObject (Object v, Type type) throws IOException
	{
		if (Reflection.isFinal (type) && type.isStringSerializable ())
		{
			writeString0 (v.toString (), "string");
		}
		else if (v instanceof Class)
		{
			writeString0 (((Class) v).getName (), "string");
		}
		else
		{
			beginWrite ('@');
			Utils.encodeBase64 (v, buffer);
			if ((outputContext == BUFFER_LIST)
				|| (outputContext == BUFFER_MAP))
			{
				buffer.append (' '); 
			}
			endWrite ("serialized");
		}
		if (listener != null)
		{
			listener.objectWritten (v);
		}
	}


	public void writePersistentObjectReference (PersistenceCapable o)
		throws IOException
	{
		beginWrite ('#');
		buffer.append (o.getId ());
		endWrite ("ref");
	}


	public void writeSharedObjectReference (Shareable o) throws IOException
	{
		beginWrite ('*');
		buffer.append (o.getProvider ().getProviderName ()).append (':');
		int co = outputContext;
		outputContext = BUFFER_LIST;
		String fn = fieldName;
		o.getProvider ().writeObject (o, this);
		outputContext = co;
		fieldName = fn;
		endWrite ("shared");
		if (listener != null)
		{
			listener.sharedObjectReferenceWritten (o);
		}
	}


	private int outputContext = ELEMENT_WITH_ELEMENTS;
	private long outputStack;

	private void pushOutput ()
	{
		outputStack = (outputStack << 2) | outputContext;
	}


	private void popOutput ()
	{
		outputContext = (int) outputStack & 3;
		outputStack >>>= 2;
	}


	private String arrayFieldName;

	public void beginArray (int length, Type ct) throws IOException
	{
		pushOutput ();
		switch (outputContext)
		{
			case ELEMENT_WITH_ATTRIBUTES:
				arrayFieldName = fieldName;
				// no break
			case BUFFER_LIST:
			case BUFFER_MAP:
				beginWrite ('{');
				outputContext = BUFFER_LIST;
				break;
			case ELEMENT_WITH_ELEMENTS:
				atts.clear ();
				if (fieldName != null)
				{
					add ("name", NS_PREFIX + ":name", fieldName);
				}
				if (Reflection.isPrimitive (ct))
				{
					outputContext = BUFFER_LIST;
					buffer.setLength (0);
				}
				else
				{
					startElement ("array", atts);
				}
				break;
		}
		fieldName = null;
	}


	public void endArray () throws IOException
	{
		int fo = outputContext;
		popOutput ();
		switch (outputContext)
		{
			case BUFFER_LIST:
			case BUFFER_MAP:
				buffer.append ('}');
				break;
			case ELEMENT_WITH_ATTRIBUTES:
				buffer.append ('}');
				fieldName = arrayFieldName;
				endWrite (null);
				break;
			case ELEMENT_WITH_ELEMENTS:
				if (fo == ELEMENT_WITH_ELEMENTS)
				{
					endElement ("array");
				}
				else
				{
					add ("value", NS_PREFIX + ":value", buffer.toString ());
					startElement ("array", atts);
					endElement ("array");
				}
				break;
		}
		fieldName = null;
	}


	private String managedFieldName;

	public boolean beginManaged (ManageableType type, boolean writeType)
		throws IOException
	{
		pushOutput ();
		switch (outputContext)
		{
			case ELEMENT_WITH_ATTRIBUTES:
				managedFieldName = fieldName;
				// no break
			case BUFFER_LIST:
			case BUFFER_MAP:
				if (writeType)
				{
					beginWrite ('[');
					buffer.append (type.getBinaryName ());
				}
				else
				{
					beginWrite ('(');
				}
				outputContext = (type.getSerializationMethod ()
						  != ManageableType.LIST_SERIALIZATION)
					? BUFFER_MAP : BUFFER_LIST;
				break;
			case ELEMENT_WITH_ELEMENTS:
				atts.clear ();
				if (fieldName != null)
				{
					add ("name", NS_PREFIX + ":name", fieldName);
				}
				if (writeType)
				{
					add ("type", NS_PREFIX + ":type", type.getBinaryName ());
				}
				switch (type.getSerializationMethod ())
				{
					case ManageableType.LIST_SERIALIZATION:
						outputContext = BUFFER_LIST;
						buffer.setLength (0);
						break;
					case ManageableType.FIELD_NAME_SERIALIZATION:
						outputContext = ELEMENT_WITH_ATTRIBUTES;
						break;
					case ManageableType.FIELD_NODE_SERIALIZATION:
						startElement ("object", atts);
						break;
				}
				break;
		}
		return outputContext != BUFFER_LIST;
	}


	public void endManaged (Object object, boolean writeType) throws IOException
	{
		int fo = outputContext;
		popOutput ();
		switch (outputContext)
		{
			case BUFFER_LIST:
			case BUFFER_MAP:
				buffer.append (writeType ? ']' : ')');
				break;
			case ELEMENT_WITH_ATTRIBUTES:
				buffer.append (writeType ? ']' : ')');
				fieldName = managedFieldName;
				endWrite (null);
				break;
			case ELEMENT_WITH_ELEMENTS:
				switch (fo)
				{
					case BUFFER_LIST:
						add ("value", NS_PREFIX + ":value", buffer.toString ());
						startElement ("object", atts);
						endElement ("object");
						break;
					case ELEMENT_WITH_ATTRIBUTES:
						startElement ("object", atts);
						endElement ("object");
						break;
					case ELEMENT_WITH_ELEMENTS:
						endElement ("object");
						break;
					default:
						throw new AssertionError ();
				}
				break;
		}
		fieldName = null;
		if (listener != null)
		{
			listener.objectWritten (object);
		}
	}


	static final int BUFFER_LIST = 0;
	static final int BUFFER_MAP = 1;
	static final int ELEMENT_WITH_ATTRIBUTES = 2;
	static final int ELEMENT_WITH_ELEMENTS = 3;


	protected void startElement (String name, Attributes attr) throws IOException
	{
		try
		{
			ch.startElement (NAMESPACE, name, name, attr);
		}
		catch (SAXException e)
		{
			throw new IOWrapException (e);
		}
	}


	protected void endElement (String name) throws IOException
	{
		try
		{
			ch.endElement (NAMESPACE, name, name);
		}
		catch (SAXException e)
		{
			throw new IOWrapException (e);
		}
	}


	public void beginFields ()
	{
	}


	public void beginField (ManageableType.Field field) throws IOException
	{
		fieldName = field.getSimpleName ();
	}


	public void endField (ManageableType.Field field) throws IOException
	{
	}


	public void endFields ()
	{
	}


	public void setNested (boolean nested)
	{
	}


	public void getAttributes (PersistenceCapable o, AttributesImpl attr)
		throws IOException
	{
		outputContext = ELEMENT_WITH_ATTRIBUTES;
		atts.setAttributes (attr);
		o.getManageableType ().write (o, this, true);
		attr.setAttributes (atts);
	}


	public void writeFields (Manageable o) throws IOException
	{
		outputContext = ELEMENT_WITH_ELEMENTS;
		o.getManageableType ().write (o, this, true);
	}


	/**
	 * This method serializes <code>o</code> into a single string which
	 * may be used, e.g., as an attribute value of an XML element.
	 * The value can be deserialized using
	 * {@link XMLPersistenceReader#valueOf(Manageable, String)}.
	 * 
	 * @param o the value to be serialized
	 * @return a string describing <code>o</code>
	 */
	public String toString (Manageable o)
	{
		outputContext = BUFFER_MAP;
		buffer.setLength (0);
		try
		{
			o.getManageableType ().write (o, this, false);
		}
		catch (IOException e)
		{
			throw new WrapException (e);
		}
		return buffer.toString ();
	}


	static final String SERIALIZED_PREFIX = "serialized:";

	/**
	 * This method serializes <code>o</code> of type <code>type</code>
	 * into a single string which
	 * may be used, e.g., as an attribute value of an XML element.
	 * The value can be deserialized using
	 * {@link XMLPersistenceReader#valueOf(Type, String)}.
	 * 
	 * @param type the type of <code>o</code>
	 * @param o the value to be serialized
	 * @return a string describing <code>o</code>
	 */
	public String toString (Type type, Object o)
	{
		String s;
		if (o == null)
		{
			return "null";
		}
		else if (o instanceof Manageable)
		{
			s = toString ((Manageable) o);
		}
		else if ((o instanceof CharSequence) || type.isStringSerializable ())
		{
			s = o.toString ();
		}
		else if (o instanceof Class)
		{
			s = ((Class) o).getName ();
		}
		else if (o instanceof Serializable)
		{
			try
			{
				StringBuffer buffer = new StringBuffer (SERIALIZED_PREFIX);
				Utils.encodeBase64 (o, buffer);
				s = buffer.toString ();
			}
			catch (IOException e)
			{
				e.printStackTrace ();
				return "null";
			}
		}
		else
		{
			return "null";
		}
		return s.equals ("null") ? "\"null\""
			: (s.indexOf ('"') >= 0) ? Utils.quote (s) : s;
		
	}
	
}
