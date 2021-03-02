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

import java.util.Collection;

import antlr.collections.AST;
import de.grogra.grammar.RecognitionExceptionList;
import de.grogra.reflect.Annotation;
import de.grogra.xl.compiler.scope.ClassPath;
import de.grogra.xl.compiler.scope.Scope;

/**
 * A <code>CompilationUnit</code> is a simple collection of items which
 * constitute the complete input of a single source file for the XL compiler.    
 * 
 * @author Ole Kniemeyer
 */
public final class CompilationUnit
{
	/**
	 * The class path for existing class files.
	 */
	public final ClassPath classPath;

	/**
	 * The root of the abstract syntax tree of the source file.
	 */
	public final AST tree;

	/**
	 * Name of source file.
	 */
	public final String source;

	/**
	 * Compilation problems are passed to this instance by the
	 * XL compiler.
	 */
	public final RecognitionExceptionList problems;

	/**
	 * The automatic imports for the compilation unit.
	 */
	public final Scope defaultImports;

	/**
	 * The default options for the compiler.
	 */
	public final CompilerOptions options;

	
	public final Annotation[] annotations;

	public CompilationUnit (ClassPath classPath, AST tree, String source,
			RecognitionExceptionList problems,
			Scope defaultImports, CompilerOptions options,
			Collection<Annotation> annotations)
	{
		this.classPath = classPath;
		this.tree = tree;
		this.source = source;
		this.problems = problems;
		this.defaultImports = defaultImports;
		this.options = options;
		this.annotations = (annotations == null) ? new Annotation[0] : annotations.toArray (new Annotation[0]);
	}

	@Override
	public String toString ()
	{
		return source;
	}
}
