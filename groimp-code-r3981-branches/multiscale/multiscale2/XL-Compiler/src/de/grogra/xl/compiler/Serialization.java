
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

package de.grogra.xl.compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.objectweb.asm.Opcodes;

import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Field;
import de.grogra.reflect.IntersectionType;
import de.grogra.reflect.Member;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.reflect.XField;
import de.grogra.xl.query.BytecodeSerialization;
import de.grogra.xl.query.Utils;
import de.grogra.xl.util.ObjectList;
import de.grogra.xl.vmx.Routine;
import de.grogra.xl.vmx.SerializationWithRoutine;

final class Serialization implements SerializationWithRoutine
{
	private static final Method READ_OBJECT
		= Reflection.getDeclaredMethod (ClassAdapter.wrap (ObjectInputStream.class),
										"readObject");
	
	private static final Type UTILS = ClassAdapter.wrap (Utils.class);
	private static final Type ISECT_TYPE = ClassAdapter.wrap (IntersectionType.class);

	private final BytecodeWriter out;
	private final XField serialized;

	private final ObjectList stack = new ObjectList ();
	
	private final int streamIndex;

	private final ObjectOutputStream objectOut;
	private final ByteArrayOutputStream objectOutBytes;
	
	private boolean array = false;
	private Type componentType = null;
	private int index = 0;
	private Method method = null;


	Serialization (CClass type, BytecodeWriter out, int classLoaderIndex, int unusedIndex)
		throws IOException
	{
		this.out = out;
		serialized = type.declareAuxField
			("serialized", Member.STATIC | Member.FINAL | Member.SYNTHETIC, Type.STRING);

		objectOutBytes = new ByteArrayOutputStream ();
		objectOut = new ObjectOutputStream (objectOutBytes);
		
		out.visitFieldInsn (Opcodes.GETSTATIC, serialized, null);
		out.visitVarInsn (Opcodes.ALOAD, classLoaderIndex);
		out.visitMethodInsn
			(Reflection.getDeclaredMethod (UTILS, "getObjectInput"));

		streamIndex = unusedIndex;
		out.visitVarInsn (Opcodes.ASTORE, streamIndex);
	}


	private void push ()
	{
		stack.push (array ? this : null).push (componentType)
			.push (index).push (method);
	}


	private void pop ()
	{
		method = (Method) stack.pop ();
		index = ((Integer) stack.pop ()).intValue ();
		componentType = (Type) stack.pop ();
		array = stack.pop () != null;
	}

/*!!
#foreach ($type in $primitives)
$pp.setType($type)

	public void visit$pp.Type ($type value)
	{
		out.visit${pp.prefix}const (value $pp.type2vm);
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public void visitBoolean (boolean value)
	{
		out.visiticonst (value  ? 1 : 0);
	}
// generated
// generated
// generated
	public void visitByte (byte value)
	{
		out.visiticonst (value );
	}
// generated
// generated
// generated
	public void visitShort (short value)
	{
		out.visiticonst (value );
	}
// generated
// generated
// generated
	public void visitChar (char value)
	{
		out.visiticonst (value );
	}
// generated
// generated
// generated
	public void visitInt (int value)
	{
		out.visiticonst (value );
	}
// generated
// generated
// generated
	public void visitLong (long value)
	{
		out.visitlconst (value );
	}
// generated
// generated
// generated
	public void visitFloat (float value)
	{
		out.visitfconst (value );
	}
// generated
// generated
// generated
	public void visitDouble (double value)
	{
		out.visitdconst (value );
	}
// generated
//!! *# End of generated code

	public void visitObject (Object value) throws IOException
	{
		if ((value == null) || (value instanceof String))
		{
			out.visitaconst (value);
		}
		else if (value instanceof Serializable)
		{
			((Serializable) value).write (this);
		}
		else
		{
			objectOut.writeObject (value);
			out.visitVarInsn (Opcodes.ALOAD, streamIndex);
			out.visitMethodInsn (READ_OBJECT);
		}
	}

	
	public void visitClass (Type cls)
	{
		out.visitaconst (cls);
	}


	public void visitType (Type type)
	{
		if (type instanceof IntersectionType)
		{
			type = ((IntersectionType) type).simplify ();
		}
		if (type instanceof IntersectionType)
		{
			IntersectionType it = (IntersectionType) type;
			out.visitTypeInsn (Opcodes.NEW, ISECT_TYPE);
			out.visitInsn (Opcodes.DUP);
			visitType (type.getSupertype ());
			out.visitMethodInsn (ISECT_TYPE, "<init>", "(Lde/grogra/reflect/Type;)V");
			for (int i = 0; i < it.getDeclaredInterfaceCount (); i++)
			{
				out.visitInsn (Opcodes.DUP);
				visitType (it.getDeclaredInterface (i));
				out.visitMethodInsn (ISECT_TYPE, "addInterface");
			}
		}
		else if (Reflection.isPrimitiveOrVoid (type))
		{
			out.visitFieldInsn (Opcodes.GETSTATIC, Type.class, type.getBinaryName ().toUpperCase (), Type.TYPE.getDescriptor ());
		}
		else
		{
			out.visitInsn (Opcodes.ICONST_0);
			out.visitNewArray (type);
			out.visitMethodInsn (UTILS, "toClassAdapter");
		}
	}


	public void beginArray (int length, Type type)
	{
		push ();
		array = true;
		index = 0;
		componentType = type;
		out.visiticonst (length);
		out.visitNewArray (type);
	}


	public void beginArrayComponent (int index)
	{
		out.visitInsn (Opcodes.DUP);
		out.visiticonst (index);
	}


	public void endArrayComponent ()
	{
		out.visitAStore (componentType.getTypeId ());
	}


	public void endArray ()
	{
		pop ();
	}


	public void beginMethod (Method method)
	{
		push ();
		array = false;
		this.method = method;
		if (method.getSimpleName ().equals ("<init>"))
		{
			out.visitTypeInsn (Opcodes.NEW, method.getDeclaringType ());
			out.visitInsn (Opcodes.DUP);
		}
	}


	public void endMethod () throws IOException
	{
		out.visitMethodInsn (method);
		pop ();
	}

	
	public void visitField (Field field)
	{
		out.visitFieldInsn
			(Reflection.isStatic (field) ? Opcodes.GETSTATIC : Opcodes.GETFIELD,
			 field, null);
	}

	
	public void visitRoutine (Routine routine)
	{
		visitField (((XMethod) routine).getRoutineField ());
	}

	
	public void flush () throws IOException
	{
		objectOut.flush ();
		objectOut.close ();
		serialized.setConstant (Utils.toString (objectOutBytes.toByteArray ()));
	}

}
