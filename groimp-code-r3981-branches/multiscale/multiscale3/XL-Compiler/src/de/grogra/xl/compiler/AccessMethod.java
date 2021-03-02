
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

import org.objectweb.asm.Opcodes;

import de.grogra.reflect.*;
import de.grogra.xl.compiler.CClass;

public class AccessMethod
{
	private final CClass type;
	private final Member member;
	private final boolean setter;

	private String name;
	private String descriptor;
	private int additionalArgCount;
	
	
	public AccessMethod (CClass type, Member member, boolean setter)
	{
		this.type = type;
		this.member = member;
		this.setter = setter;
	}
	
	
	public CClass getDeclaringClass ()
	{
		return type;
	}

	
	public String getDescriptor ()
	{
		if (descriptor != null)
		{
			return descriptor;
		}

		StringBuffer sb;
		if (member.getDescriptor ().charAt (0) != 'f')
		{
			sb = new StringBuffer (member.getDescriptor ());
			if ("<init>".equals (member.getName ()))
			{
				assert member.getDeclaringType () == type;
			}
			else
			{
				int s = sb.indexOf (member.getName ());
				sb.replace (s, s + member.getName ().length (), "access");
			}
			sb.setLength (sb.lastIndexOf (")") + 1);
			additionalArgCount = type.getAccessMethodDescriptor (sb, member);
			sb.append (((Method) member).getReturnType ().getDescriptor ());
		}
		else if (setter)
		{
			sb = new StringBuffer ("maccess;(")
				.append (((Field) member).getType ().getDescriptor ()).append (')');
			type.getAccessMethodDescriptor (sb, member);
			sb.append ('V');
		}
		else
		{
			sb = new StringBuffer ("maccess;()");
			type.getAccessMethodDescriptor (sb, member);
			sb.append (((Field) member).getType ().getDescriptor ());
		}
		name = sb.substring (1, sb.indexOf (";"));
		descriptor = sb.substring (sb.indexOf ("("));
		return descriptor;
	}
	
	
	public String getName ()
	{
		getDescriptor ();
		return name;
	}

	
	public int getAdditionalArgumentCount ()
	{
		getDescriptor ();
		return additionalArgCount;
	}
	
	
	void write (BytecodeWriter writer)
	{
		if (member.getDescriptor ().charAt (0) != 'f')
		{
			Method m = (Method) member;
			int index = 0;
			if (!Reflection.isStatic (m))
			{
				writer.visitVarInsn (Opcodes.ALOAD, 0);
				index = 1;
			}
			for (int a = 0; a < m.getParameterCount (); a++)
			{
				writer.visitLoad (index, m.getParameterType (a).getTypeId ());
				index += Reflection.hasCategory2 (m.getParameterType (a).getTypeId ())
					? 2 : 1;
			}
			writer.visitMethodInsn
				(Reflection.isStatic (m) ? Opcodes.INVOKESTATIC
				 : Opcodes.INVOKESPECIAL, m);
			writer.visitReturn (m.getReturnType ().getTypeId ());
		}
		else if (setter)
		{
			if (Reflection.isStatic (member))
			{
				writer.visitLoad (0, ((Field) member).getType ().getTypeId ());
				writer.visitFieldInsn (Opcodes.PUTSTATIC, (Field) member, null);
			}
			else
			{
				writer.visitVarInsn (Opcodes.ALOAD, 0);
				writer.visitLoad (1, ((Field) member).getType ().getTypeId ());
				writer.visitFieldInsn (Opcodes.PUTFIELD, (Field) member, null);
			}
			writer.visitInsn (Opcodes.RETURN);
		}
		else
		{
			if (Reflection.isStatic (member))
			{
				writer.visitFieldInsn (Opcodes.GETSTATIC, (Field) member, null);
			}
			else
			{
				writer.visitVarInsn (Opcodes.ALOAD, 0);
				writer.visitFieldInsn (Opcodes.GETFIELD, (Field) member, null);
			}
			writer.visitReturn (((Field) member).getType ().getTypeId ());
		}
	}
	
}
