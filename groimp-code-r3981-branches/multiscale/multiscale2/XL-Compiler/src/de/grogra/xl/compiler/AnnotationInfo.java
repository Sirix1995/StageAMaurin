
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

import java.util.HashSet;

import antlr.collections.AST;
import de.grogra.reflect.AnnotationImpl;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.util.I18NBundle;
import de.grogra.xl.compiler.scope.Scope;

class AnnotationInfo<T extends java.lang.annotation.Annotation> extends AnnotationImpl<T>
{
	private static final I18NBundle I18N = Compiler.I18N;

	final Compiler compiler;
	final Scope scope;
	final AST node;

	int run;
	boolean ok = true;

	private boolean typeIsAnnot = false;

	private HashSet<String> elements;

	AnnotationInfo (Compiler compiler, Type<T> type, Scope scope, AST node)
	{
		super (type);
		this.compiler = compiler;
		this.scope = scope;
		this.node = node;
		run = compiler.getRun ();
	}


	void init (AST clsNode)
	{
		if (elements == null)
		{
			elements = new HashSet<String> ();
			checkAnnotationClass (clsNode);
		}
	}

	
	void finish (boolean errors)
	{
		if (errors)
		{
			ok = false;
			return;
		}
		if (typeIsAnnot)
		{
			Type t = annotationType ();
			for (int i = t.getDeclaredMethodCount () - 1; i >= 0; i--)
			{
				Method m = t.getDeclaredMethod (i);
				if (!elements.contains (m.getSimpleName ())
					&& (t.getDefaultElementValue (m.getSimpleName ()) == null))
				{
					compiler.problems.addSemanticError
						(I18N.msg (ProblemReporter.ELEMENT_NOT_INITIALIZED, m.getSimpleName ()), node);
				}
			}
		}
	}


	Type addElement (AST element)
	{
		if (!typeIsAnnot)
		{
			return null;
		}
		String n = element.getText ();
		Method m = Reflection.getElementMethod (annotationType (), n);
		if (m != null)
		{
			if (!elements.add (n))
			{
				compiler.problems.addSemanticError
					(I18N.msg (ProblemReporter.DUPLICATE_ELEMENT, n), element);
			}
			return m.getReturnType ();
		}
		else if (!Reflection.isInvalid (annotationType ()))
		{
			compiler.problems.addSemanticError
				(I18N.msg (ProblemReporter.NO_ELEMENT, n), element);
		}
		return null;
	}

	void deferToCompilation ()
	{
		run = Compiler.COMPILATION;
		elements.clear ();
	}


	void checkAnnotationClass (AST pos)
	{
		Type type = annotationType ();
		if (!Reflection.isPublic (type))
		{
			compiler.problems.addSemanticError
				(I18N.msg (ProblemReporter.PUBLIC_CLASS_EXPECTED, type.getName ()), pos);
		}
		if (Reflection.isInvalid (type))
		{
			typeIsAnnot = false;
		}
		else if ((type.getDeclaredInterfaceCount () != 1)
				|| !Reflection.equal (java.lang.annotation.Annotation.class, type.getDeclaredInterface (0)))
		{
			compiler.problems.addSemanticError
				(I18N.msg (ProblemReporter.NO_ANNOTATION_TYPE, type), pos);
			typeIsAnnot = false;
		}
		else
		{
			typeIsAnnot = true;
		}
	}

}
