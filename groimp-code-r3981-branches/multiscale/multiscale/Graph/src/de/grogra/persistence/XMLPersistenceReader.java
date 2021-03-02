
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

import java.io.EOFException;
import java.io.IOException;

import org.xml.sax.SAXException;

import de.grogra.persistence.ManageableType.ArrayComponent;
import de.grogra.persistence.ManageableType.Field;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.util.IOWrapException;
import de.grogra.util.SAXElement;
import de.grogra.util.Utils;
import de.grogra.util.WrapException;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;

/**
 * An <code>XMLPersistenceReader</code> is used to read the serialization
 * output of an {@link de.grogra.persistence.XMLPersistenceWriter}.
 * 
 * @author Ole Kniemeyer
 */
public class XMLPersistenceReader implements PersistenceInput
{
	private static final String NAMESPACE = XMLPersistenceWriter.NAMESPACE;
	private static final int SINGLE_FIELD = 0;
	private static final int BUFFER = 1;
	private static final int ELEMENT_WITH_ELEMENTS = 2;
	private static final int ELEMENT_WITH_ATTRIBUTES = 3;
	private boolean wholeBuffer = false;

	private final PersistenceManager manager;
	private final PersistenceBindings bindings;
	private int pos;
	private String buffer;
	private SAXElement element;
	private int inputType;
	private int attributeIndex;

	private final ObjectList<Object> persistentObjectsToResolve = new ObjectList<Object> (50);
	private final ObjectList<Object> sharedObjectsToResolve = new ObjectList<Object> (50);


	public XMLPersistenceReader (PersistenceManager manager,
								 PersistenceBindings bindings)
	{
		this.manager = manager;
		this.bindings = bindings;
	}


	public PersistenceBindings getBindings ()
	{
		return bindings;
	}


	public PersistenceManager getPersistenceManager ()
	{
		return manager;
	}


	private void setBuffer (String buffer)
	{
		this.buffer = buffer;
		pos = 0;
		inputType = BUFFER;
		wholeBuffer = true;
	}


	private int getc () throws IOException
	{
		if (pos < buffer.length ())
		{
			return buffer.charAt (pos++);
		}
		else
		{
			pos++;
			return -1;
		}
	}


	private char getchar () throws IOException
	{
		int c = getc ();
		if (c < 0)
		{
			throw new EOFException ();
		}
		return (char) c;
	}


	private void consume (char c) throws IOException
	{
		if (c != getc ())
		{
			throw new IOException ("Expected " + c);
		}
	}


	private void consumeWS () throws IOException
	{
		int c;
		while (((c = getc ()) >= 0) && Character.isWhitespace ((char) c))
			;
		pos--;
	}


	private int nextDelimiter (boolean eqIsDelim) throws IOException
	{
		consumeWS ();
		int p = pos;
	loop:
		while (true)
		{
			switch (getc ())
			{
				case -1:
				case ' ':
				case ',':
				case ':':
				case '(':
				case ')':
				case '{':
				case '}':
				case '[':
				case ']':
				case '\'':
				case '"':
					break loop;
				case '=':
					if (eqIsDelim)
					{
						break loop;
					}
					break;
			}
		}
		int n = pos - 1;
		pos = p;
		return n;
	}


	public boolean readBoolean () throws IOException
	{
		beginRead ((char) 0);
		int n = nextDelimiter (true);
		boolean b = buffer.regionMatches (pos, "true", 0, 4);
		pos = n;
		return b;
	}


	public byte readByte () throws IOException
	{
		return (byte) readInt ();
	}


	public int readUnsignedByte () throws IOException
	{
		return readInt () & 255;
	}


	public short readShort () throws IOException
	{
		return (short) readInt ();
	}


	public int readUnsignedShort () throws IOException
	{
		return readInt () & 0xffff;
	}


	public char readChar () throws IOException
	{
		beginRead ((char) 0);
		consumeWS ();
		consume ('\'');
		char c = getchar ();
		if (c == '\\')
		{
			c = getchar ();
		}
		consume ('\'');
		return c;
	}


	public int readInt () throws IOException
	{
		try
		{
			beginRead ((char) 0);
			int n = nextDelimiter (true);
			return Integer.parseInt (buffer.substring (pos, pos = n));
		}
		catch (NumberFormatException e)
		{
			throw new IOWrapException (e);
		}
	}


	private long readLong (char prefix) throws IOException
	{
		try
		{
			if (inputType == SINGLE_FIELD)
			{
				return Long.parseLong (element.getValue (NAMESPACE, "value"));
			}
			else
			{
				consumeWS ();
				if (prefix > 0)
				{
					consume (prefix);
				}
				int n = nextDelimiter (true);
				return Long.parseLong (buffer.substring (pos, pos = n));
			}
		}
		catch (NumberFormatException e)
		{
			throw new IOWrapException (e);
		}
	}


	public long readLong () throws IOException
	{
		return readLong ((char) 0); 
	}


	public float readFloat () throws IOException
	{
		try
		{
			beginRead ((char) 0);
			int n = nextDelimiter (true);
			return Float.parseFloat (buffer.substring (pos, pos = n));
		}
		catch (NumberFormatException e)
		{
			throw new IOWrapException (e);
		}
	}


	public double readDouble () throws IOException
	{
		try
		{
			beginRead ((char) 0);
			int n = nextDelimiter (true);
			return Double.parseDouble (buffer.substring (pos, pos = n));
		}
		catch (NumberFormatException e)
		{
			throw new IOWrapException (e);
		}
	}


	public String readName () throws IOException
	{
		return buffer.substring (pos, pos = nextDelimiter (true));
	}


	public String peekName () throws IOException
	{
		return buffer.substring (pos, nextDelimiter (true));
	}


	private final StringBuffer sb = new StringBuffer ();

	private boolean wasQuoted;

	public String readString () throws IOException
	{
		consumeWS ();
		if (wasQuoted = (getc () == '"'))
		{
			sb.setLength (0);
			char c;
			while ((c = getchar ()) != '"')
			{
				if (c == '\\')
				{
					c = getchar ();
				}
				sb.append (c);
			}
			return sb.toString ();
		}
		else
		{
			pos--;
			int n = nextDelimiter (false);
			return buffer.substring (pos, pos = n);
		}
	}


	private Type fieldType;

	public int getNextObjectKind () throws IOException
	{
		switch (inputType)
		{
			case BUFFER:
				if (!wholeBuffer)
				{
					consumeWS ();
				}
				if (Reflection.equal (fieldType, Type.STRING))
				{
					return STRING_OBJECT;
				}
				int c = getc ();
				pos--;
				switch (c)
				{
					case '{':
						return ARRAY_OBJECT;
					case '#':
						return PERSISTENT_OBJECT_ID;
					case '*':
						return SHARED_OBJECT_REFERENCE;
					case '[':
					case '(':
						return MANAGEABLE_OBJECT;
					case '@':
						return SERIALIZED_OBJECT;
					default:
						return PLAIN_OBJECT;
				}
			case SINGLE_FIELD:
				String s = element.name;
				if (s.equals ("data"))
				{
					return STRING_OBJECT;
				}
				else if (s.equals ("object"))
				{
					return MANAGEABLE_OBJECT;
				}
				else if (s.equals ("shared"))
				{
					return SHARED_OBJECT_REFERENCE;
				}
				else if (s.equals ("null"))
				{
					return NULL_OBJECT;
				}
				else if (s.equals ("ref"))
				{
					return PERSISTENT_OBJECT_ID;
				}
				else if (s.equals ("string"))
				{
					return PLAIN_OBJECT;
				}
				else if (s.equals ("array"))
				{
					return ARRAY_OBJECT;
				}
				else if (s.equals ("serialized"))
				{
					return SERIALIZED_OBJECT;
				}
				else
				{
					throw new IOException (s);
				}
			case ELEMENT_WITH_ELEMENTS:
				if (element.name.equals ("array"))
				{
					return ARRAY_OBJECT;
				}
				// no break
			case ELEMENT_WITH_ATTRIBUTES:
				return MANAGEABLE_OBJECT;
			default:
				throw new AssertionError ();
		}
	}


	public Object readObject (int kind, Type type) throws IOException
	{
		String b;
		switch (inputType)
		{
			case BUFFER:
				if (wholeBuffer)
				{
					b = (wasQuoted = buffer.startsWith ("\""))
						? Utils.unquote (buffer) : buffer;
				}
				else
				{
					b = readString ();
				}
				break;
			case SINGLE_FIELD:
				if (kind == NULL_OBJECT)
				{
					return null;
				}
				else
				{
					b = element.getValue (NAMESPACE, "value");
					if (wasQuoted = b.startsWith ("\""))
					{
						b = Utils.unquote (b);
					}
				}
				break;
			default:
				throw new AssertionError ();
		}
		if ((inputType != SINGLE_FIELD) && !wasQuoted && "null".equals (b))
		{
			return null;
		}
		else if (type.getImplementationClass () == String.class)
		{
			return b;
		}
		else if ((kind == PLAIN_OBJECT) && type.isStringSerializable ())
		{
			return type.valueOf (b);
		}
		else if ((kind == PLAIN_OBJECT) && (type.getImplementationClass () == Class.class))
		{
			try
			{
				return bindings.getTypeLoader ().classForName (b);
			}
			catch (Exception e)
			{
				throw new IOWrapException (e);
			}
		}
		else if (kind == SERIALIZED_OBJECT)
		{
			int offset = 0;
			if (inputType != SINGLE_FIELD)
			{
				if (b.startsWith ("@"))
				{
					offset = 1;
				}
				else
				{
					throw new IOException ();
				}
			}
			try
			{
				return Utils.decodeBase64 (b, offset, b.length () - offset,
										   bindings.getTypeLoader ());
			}
			catch (ClassNotFoundException e)
			{
				throw new IOWrapException (e);
			}
		}
		if (kind == STRING_OBJECT)
		{
			System.err.println ("Wrong data for type " + type + ": " + b);
			return null;
		}
		throw new AssertionError (kind);
	}


	public Shareable readSharedObject () throws IOException
	{
		beginRead ('*');
		int n = nextDelimiter (true);
		String pn = buffer.substring (pos, pos = n);
		consume (':');
		SharedObjectProvider p = bindings.getSOBinding ().lookup (pn);
		ResolvableReference r = p.readReference (this);
		if (r.isResolvable ())
		{
			return r.resolve ();
		}
		else
		{
			sharedObjectsToResolve.push (r, totalField.dup (), persistentObject)
				.push (totalIndices.isEmpty () ? null : totalIndices.toArray ());
			return null;
		}
	}


	public long readPersistentObjectId () throws IOException
	{
		return readLong ('#');
	}


	public PersistenceCapable readPersistentObject () throws IOException
	{
		long id = readPersistentObjectId ();
		PersistenceCapable o = manager.getObject (id);
		if (o != null)
		{
			return o;
		}
		else
		{
			persistentObjectsToResolve.push (new Long (id), totalField.dup (), persistentObject)
				.push (totalIndices.isEmpty () ? null : totalIndices.toArray ());
			return null;
		}
	}


	private void beginRead (char prefix) throws IOException
	{
		if (inputType == SINGLE_FIELD)
		{
			setBuffer (element.getValue (NAMESPACE, "value"));
		}
		else
		{
			consumeWS ();
			if (prefix > 0)
			{
				consume (prefix);
			}
		}
	}


	private final ObjectList stack = new ObjectList ();
	private final IndirectField totalField = new IndirectField ();
	private final IntList totalIndices = new IntList ();
	private PersistenceCapable persistentObject;


	public int beginArray () throws IOException
	{
		switch (inputType)
		{
			case ELEMENT_WITH_ELEMENTS:
				int n = 0;
				element = element.children;
				for (SAXElement e = element; e != null; e = e.next)
				{
					n++;
				}
				return n;
			case BUFFER:
				wholeBuffer = false;
				consumeWS ();
				consume ('{');
				return -1;
			case SINGLE_FIELD:
				setBuffer (element.getValue (NAMESPACE, "value"));
				wholeBuffer = false;
				return -1;
			default:
				throw new AssertionError ();
		}
	}


	private void beginFieldImpl ()
	{
		stack.push (element);
		if (element.name.equals ("object"))
		{
			if (element.children != null)
			{
			}
			else if (element.getIndex (NAMESPACE, "value") >= 0)
			{
				inputType = SINGLE_FIELD;
			}
			else
			{
				inputType = ELEMENT_WITH_ATTRIBUTES;
				attributeIndex = 0;
			}
		}
		else if (element.name.equals ("array"))
		{
			if (element.getIndex (NAMESPACE, "value") >= 0)
			{
				inputType = SINGLE_FIELD;
			}
		}
		else
		{
			inputType = SINGLE_FIELD;
		}
	}


	private void endFieldImpl ()
	{
		Object o = stack.pop ();
		if (o == this)
		{
			inputType = ELEMENT_WITH_ATTRIBUTES;
		}
		else if (o != null)
		{
			inputType = ELEMENT_WITH_ELEMENTS;
			element = (SAXElement) o;
		}
		switch (inputType)
		{
			case ELEMENT_WITH_ELEMENTS:
				element = element.next;
				break;
			case ELEMENT_WITH_ATTRIBUTES:
				attributeIndex++;
				break;
		}
	}


	public boolean beginComponent (ArrayComponent c, int index) throws IOException
	{
		if (inputType == ELEMENT_WITH_ELEMENTS)
		{
			beginFieldImpl ();
		}
		else
		{
			consumeWS ();
			switch (getc ())
			{
				case -1:
				case '}':
					return false;
				case ',':
					break;
				default:
					pos--;
					break;
			}
			stack.push (null);
		}
		fieldType = c.getType ();
		totalField.add (c);
		totalIndices.add (index);
		return true;
	}


	public void endComponent ()
	{
		totalField.pop ();
		totalIndices.pop ();
		endFieldImpl ();
	}


	public void endArray ()
	{
	}


	private boolean fieldsProvided;

	public ManageableType beginManaged () throws IOException
	{
		wholeBuffer = false;
		Object s = null;
		String t = null;
		switch (inputType)
		{
			case SINGLE_FIELD:
				setBuffer (element.getValue (NAMESPACE, "value"));
				// no break
			case ELEMENT_WITH_ATTRIBUTES:
				t = element.getValue (NAMESPACE, "type");
				break;
			case ELEMENT_WITH_ELEMENTS:
				t = element.getValue (NAMESPACE, "type");
				element = element.children;
				break;
			case BUFFER:
				consumeWS ();
				switch (getc ())
				{
					case '[':
						t = readName ();
						break;
					case '(':
						break;
					default:
						throw new IOException ();
				}
				s = this;
				break;
			default:
				throw new AssertionError ();
		}
		stack.push (s);
		if (inputType == BUFFER)
		{
			consumeWS ();
			int b = pos;
			int n = nextDelimiter (true);
			fieldsProvided = (n == b)
				|| ((n < buffer.length ()) && (buffer.charAt (n) == '='));
		}
		else
		{
			fieldsProvided = true;
		}
		if (t == null)
		{
			return null;
		}
		else
		{
			t = replaceType (t);
			ManageableType type = bindings.resolveType (t);
			if (type == null)
			{
				throw new IOException (t);
			}
			return type;
		}
	}

	
	protected String replaceType (String type)
	{
		if ("de.grogra.imp3d.shading.Lambert".equals (type))
		{
			return "de.grogra.imp3d.shading.Phong";
		}
		else if ("de.grogra.imp3d.shading.MappedShader".equals (type))
		{
			return "de.grogra.imp3d.shading.SideSwitchShader";
		}
		else if ("de.grogra.imp3d.shading.Graytone".equals (type))
		{
			return "de.grogra.math.Graytone";
		}
		else if ("de.grogra.imp3d.shading.RGBColor".equals (type))
		{
			return "de.grogra.math.RGBColor";
		}
		else if ("de.grogra.imp3d.objects.NullWithShader".equals (type))
		{
			return "de.grogra.imp3d.objects.ShadedNull";
		}
		else if ("de.grogra.rgg.Surface".equals (type))
		{
			return "de.grogra.imp3d.objects.NURBSSurface";
		}
		else if ("de.grogra.lsystem.DTDShoot".equals (type))
		{
			return "de.grogra.grogra.DTGShoot";
		}
		else if (type.startsWith ("de.grogra.lsystem."))
		{
			return "de.grogra.turtle." + type.substring (18);
		}
		else
		{
			return type;
		}
	}

	protected Field getManagedField (ManageableType t, String name)
	{
		return t.getManagedField (name);
	}

	public boolean areFieldsProvided ()
	{
		return fieldsProvided;
	}


	public Field beginField (ManageableType type, Field field)
		throws IOException
	{
		do
		{
			String n;
			switch (inputType)
			{
				case ELEMENT_WITH_ELEMENTS:
					if (element == null)
					{
						return null;
					}
					n = element.getValue (NAMESPACE, "name");
					beginFieldImpl ();
					break;
				case ELEMENT_WITH_ATTRIBUTES:
					while (true)
					{
						if (attributeIndex >= element.getLength ())
						{
							return null;
						}
						if (element.getURI (attributeIndex).length () == 0)
						{
							break;
						}
						attributeIndex++;
					}
					stack.push (this);
					n = element.getLocalName (attributeIndex);
					setBuffer (element.getValue (attributeIndex));
					break;
				case BUFFER:
					consumeWS ();
					if (getc () == ',')
					{
						consumeWS ();
					}
					else
					{
						pos--;
					}
					if (field != null)
					{
						stack.push (null);
						fieldType = field.getType ();
						totalField.add (field);
						return null;
					}
					int i = nextDelimiter (true);
					if ((i >= buffer.length ()) || (buffer.charAt (i) != '='))
					{
						return null;
					}
					stack.push (null);
					n = buffer.substring (pos, i);
					pos = i + 1;
					break;
				default:
					throw new AssertionError ();
			}
			if (n == null)
			{
				throw new IOException ("Unnamed field");
			}
			field = getManagedField (type, n);
			if (field == null)
			{
				System.err.println ("Field " + n + " cannot be found in " + type);
				endFieldImpl ();
				if (inputType == BUFFER)
				{
					int depth = 0;
					consumeWS ();
					do
					{
						switch (getc ())
						{
							case '(':
							case '[':
							case '{':
								depth++;
								break;
							case ')':
							case ']':
							case '}':
								depth--;
								break;
							case '\'':
								pos--;
								readChar ();
								break;
							case '\"':
								pos--;
								readString ();
								break;
							default:
								if (depth == 0)
								{
									pos--;
									readString ();
								}
								break;
						}
					} while (depth > 0);
				}
			}
		} while (field == null);
		fieldType = field.getType ();
		totalField.add (field);
		return field;
	}


	public void endField ()
	{
		totalField.pop ();
		endFieldImpl ();
	}


	public void endManaged () throws IOException
	{
		if (stack.pop () != null)
		{
			consumeWS ();
			int c = getc ();
			if ((c != ')') && (c != ']'))
			{
				throw new IOException ("Unexpected character " + (char) c);
			}
		}
	}


	public void setNested (boolean nested)
	{
	}


	public final void readElements (PersistenceCapable object,
									SAXElement parent) throws SAXException
	{
		this.element = parent.children;
		inputType = ELEMENT_WITH_ELEMENTS;
		readFields (object);
	}


	private final SAXElement attrElement = new SAXElement ();

	public final void readAttribute (PersistenceCapable object, String uri,
									 String name, String value)
		throws SAXException
	{
		if ("".equals (uri))
		{
			inputType = ELEMENT_WITH_ATTRIBUTES;
			attrElement.clear ();
			attrElement.addAttribute ("", name, name, "CDATA", value);
			attributeIndex = 0;
			element = attrElement;
			readFields (object);
			return;
		}
		throw new SAXException (uri + ':' + name);
	}


	private void readFields (PersistenceCapable object) throws SAXException
	{ 
		try
		{
			totalField.clear ();
			persistentObject = object;
			object.getManageableType ().readObject (this, object, true);
		}
		catch (IOException e)
		{
			throw new SAXException (e);
		}
	}


	/**
	 * Deserializes a string into an existing object.
	 * The string must have been generated by
	 * {@link XMLPersistenceWriter#toString(Manageable)} with an
	 * object argument of the same class.
	 * 
	 * @param object the object's content will be set using the serialized
	 * description in <code>value</code>
	 * @param value the serialized description of the object
	 */
	public void valueOf (Manageable object, String value)
	{
		setBuffer (value);
		wholeBuffer = false;
		totalField.clear ();
		try
		{
			object.getManageableType ().readObject (this, object, true);
		}
		catch (IOException e)
		{
			throw new WrapException (e);
		}
	}

	
	/**
	 * Deserializes a string into an object of the given type.
	 * The string must have been generated by
	 * {@link XMLPersistenceWriter#toString(Type, Object)} with the
	 * same <code>type</code> as argument.
	 * 
	 * @param type the type of the object to deserialize
	 * @param s the serialized description of the object
	 * @return deserialized object
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws java.lang.reflect.InvocationTargetException
	 */
	public Object valueOf (Type type, String s)
		throws InstantiationException, IllegalAccessException,
		java.lang.reflect.InvocationTargetException,
		ClassNotFoundException
	{
		Object o;
		if (type.getImplementationClass () == String.class)
		{
			o = s;
		}
		else if (type.isStringSerializable ())
		{
			o = type.valueOf (s);
		}
		else if (type.getImplementationClass () == Class.class)
		{
			o = bindings.getTypeLoader ().classForName (s);
		}
		else if (s.startsWith (XMLPersistenceWriter.SERIALIZED_PREFIX))
		{
			try
			{
				o = Utils.decodeBase64 (s, XMLPersistenceWriter.SERIALIZED_PREFIX.length (),
										s.length () - XMLPersistenceWriter.SERIALIZED_PREFIX.length (),
										bindings.getTypeLoader ());
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				o = null;
			}
		}
		else
		{
			o = type.newInstance ();
			if (o instanceof Manageable)
			{
				valueOf ((Manageable) o, s);
			}
		}
		return o;
		
	}

	private static final int SOME_RESOLVED = 1;
	private static final int SOME_UNRESOLVED = 2;

	private int resolve (ObjectList<Object> list)
	{
		int resolved = 0;
		for (int i = list.size () - 4; i >= 0; i -= 4)
		{
			Object o = list.get (i);
			if (o != null)
			{
				boolean b;
				if (o instanceof Long)
				{
					o = manager.getObject ((Long) o);
					b = o != null;
				}
				else if (b = ((ResolvableReference) o).isResolvable ())
				{
					o = ((ResolvableReference) o).resolve ();
				}
				if (b)
				{
					resolved |= SOME_RESOLVED;
					((IndirectField) list.get (i + 1))
						.setObject ((PersistenceCapable) list.get (i + 2),
									(int[]) list.get (i + 3), o);
					list.set (i, null);
					list.set (i + 1, null);
					list.set (i + 2, null);
					list.set (i + 3, null);
				}
				else
				{
					resolved |= SOME_UNRESOLVED;
				}
			}
		}
		return resolved;
	}

	public void resolve ()
	{
		while (true)
		{
			int resolved = resolve (persistentObjectsToResolve) | resolve (sharedObjectsToResolve);
			if ((resolved & SOME_UNRESOLVED) == 0)
			{
				return;
			}
			if ((resolved & SOME_RESOLVED) == 0)
			{
				System.err.println ("unresolvable: " + persistentObjectsToResolve + " " + sharedObjectsToResolve);
				return;
			}
		}
	}

}
