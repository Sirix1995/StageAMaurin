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

package de.grogra.rgg.model;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import org.objectweb.asm.Opcodes;

import de.grogra.grammar.RecognitionException;
import de.grogra.grammar.Tokenizer;
import de.grogra.graph.impl.Node;
import de.grogra.pf.boot.Main;
import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ReaderSource;
import de.grogra.pf.io.ReaderSourceImpl;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ItemVisitor;
import de.grogra.pf.registry.Library;
import de.grogra.pf.registry.PluginDescriptor;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.RegistryContext;
import de.grogra.pf.ui.registry.SourceFile;
import de.grogra.reflect.Annotation;
import de.grogra.reflect.AnnotationImpl;
import de.grogra.reflect.Type;
import de.grogra.util.IOWrapException;
import de.grogra.util.MimeType;
import de.grogra.vfs.LocalFileSystem;
import de.grogra.xl.compiler.ASMTypeLoader;
import de.grogra.xl.compiler.CompilationUnit;
import de.grogra.xl.compiler.CompilerOptions;
import de.grogra.xl.compiler.UseExtension;
import de.grogra.xl.compiler.scope.ClassPath;
import de.grogra.xl.compiler.scope.Scope;
import de.grogra.xl.compiler.scope.SingleStaticImport;
import de.grogra.xl.modules.DefaultModuleSuperclass;
import de.grogra.xl.modules.InstantiationProducerType;
import de.grogra.xl.parser.Parser;
import de.grogra.xl.parser.XLParser;
import de.grogra.xl.parser.XLTokenizer;
import de.grogra.xl.query.UseModel;
import de.grogra.xl.util.ObjectList;

/**
 * An <code>XLFilter</code> parses a character stream representing XL source
 * code into a {@link CompilationUnit}. The auxiliary method
 * {@link #compile()} automatically compiles the resulting compilation unit
 * into the contained types.
 * 
 * @author Ole Kniemeyer
 */
public class XLFilter extends FilterBase implements ObjectSource
{
	public static boolean DUMP_TREE = false;

	private static final int LOADER_ID = Registry.allocatePropertyId ();

	/**
	 * Constructs a new filter which transforms the input <code>source</code>
	 * into an instance of {@link CompilationUnit} which can be compiled afterwards.
	 * 
	 * @param item the defining item for this filter within the registry, may be <code>null</code> 
	 * @param source the source from which the input stream of characters will be read
	 * 
	 * @see #getObject
	 * @see #compile()
	 */
	public XLFilter (FilterItem item, ReaderSource source)
	{
		super (item, source);
		setFlavor (CompilationFilter.CUNIT_FLAVOR);
	}

	/**
	 * Derives a legal class name from the sytem id of the source. May be used
	 * in subclasses which implicitly generate such a class name.
	 * 
	 * @return class name derived from system id of the source
	 */
	protected String getClassName ()
	{
		String s = IO.toSimpleName (source.getSystemId ());
		if ((s.length () == 0) || !Character.isJavaIdentifierStart (s.charAt (0)))
		{
			s = "Model" + s;
		}
		int i = 1;
		while ((i < s.length ()) && Character.isJavaIdentifierPart (s.charAt (i)))
		{
			i++;
		}
		return s.substring (0, i);
	}

	/**
	 * Creates the tokenizer to be used for scanning of the source.
	 * 
	 * @return tokenizer instance
	 */
	protected Tokenizer createTokenizer ()
	{
		return new XLTokenizer ();
	}

	/**
	 * Creates the parser which will subsequently be used to parse the token stream
	 * resulting from <code>t</code> into an abstract syntax tree.
	 * 
	 * @param t token stream input
	 * @return parser which parses token stream of <code>t</code>
	 */
	protected Parser createParser (Tokenizer t)
	{
		return new XLParser (t);
	}

	/**
	 * Defines the automatic package imports
	 * (e.g., <code>import java.lang.*;</code>).
	 * The implementation of
	 * <code>XLFilter</code> returns <code>{&quot;java.lang&quot;}</code>.
	 * 
	 * @return automatic package imports
	 */
	protected String[] getPackageImports ()
	{
		return new String[] {"java.lang"};
	}

	/**
	 * Defines the automatic member type imports
	 * (e.g., <code>import java.awt.PageAttributes.*;</code>).
	 * 
	 * @return types whose member types are imported automatically
	 */
	protected Class[] getMemberTypeImports ()
	{
		return new Class[0];
	}

	/**
	 * Defines the automatic single type imports
	 * (e.g., <code>import java.util.Map;</code>).
	 * 
	 * @return types which are imported automatically
	 */
	protected Class[] getSingleTypeImports () throws ClassNotFoundException
	{
		return new Class[0];
	}

	/**
	 * Defines the automatic static type imports
	 * (e.g., <code>import static java.lang.Math.*;</code>).
	 * 
	 * @return types whose static members are imported automatically
	 */
	protected Class[] getStaticTypeImports () throws ClassNotFoundException
	{
		return new Class[0];
	}

	protected ObjectList<Annotation> getEnclosingAnnotations ()
	{
		ObjectList<Annotation> list = new ObjectList<Annotation> ();
		list.add (new AnnotationImpl (UseModel.class).setValue ("value", Compiletime.class));
		list.add (new AnnotationImpl (de.grogra.xl.property.UseModel.class)
				  .setValue ("type", Node.class)
				  .setValue ("model", PropertyCompiletime.class));
		list.add (new AnnotationImpl (DefaultModuleSuperclass.class).setValue ("value", Node.class));
		list.add (new AnnotationImpl (InstantiationProducerType.class).setValue ("value", Instantiation.class));
		list.add (new AnnotationImpl (UseExtension.class).setValue ("value", CompilerExtension.class));
		return list;
	}

	/**
	 * Defines whether the conversion from <code>double</code> to
	 * <code>float</code> should be considered as widening, i.e.,
	 * should be done automatically if required.
	 * 
	 * @return implicit conversion from <code>double</code> to <code>float</code>?
	 */
	protected boolean isD2FWidening ()
	{
		return false;
	}

	ClassPath getClassPath ()
	{
		ClassPath cp = (ClassPath) CLASSPATH.get ();
		return (cp == null) ? createDefaultClassPath (this) : cp;
	}

	static synchronized ClassPath createDefaultClassPath (final RegistryContext ctx)
	{
		final ObjectList urls = new ObjectList ();
		Item.forAll (ctx, "/project/objects/files", null, null, new ItemVisitor ()
		{
			public void visit (Item item, Object info)
			{
				if ((item instanceof SourceFile)
					&& ((SourceFile) item).getMimeType ().getMediaType ().equals ("application/x-jar"))
				{
					urls.add (((SourceFile) item).toFileSource ().toURL ());
				}
			}
		}, null, false);

		Registry r = ctx.getRegistry ();
		Object[] a = (Object[]) r.getUserProperty (LOADER_ID);
		if (a == null)
		{
			a = new Object[2];
			r.setUserProperty (LOADER_ID, a);
		}
		if ((a[0] == null) || !a[0].equals (urls))
		{
			ASMTypeLoader loader = new ASMTypeLoader (getLoaderForAll (), r.getClassLoader ());
			try
			{
				loader.addJars ((URL[]) urls.toArray (new URL[urls.size ()]), true);
			}
			catch (IOException e)
			{
				e.printStackTrace ();
			}
			a[0] = urls;
			a[1] = loader;
		}
		return new ClassPath ((ASMTypeLoader) a[1]);
	}

	/**
	 * Constructs the scope which includes all automatic imports.
	 * 
	 * @param path class path which defines the existing classes and interfaces
	 * @return scope including automatic imports based on <code>path</code>
	 * 
	 * @see #getPackageImports
	 * @see #getMemberTypeImports
	 * @see #getSingleTypeImports
	 * @see #getStaticTypeImports
	 */
	protected Scope getImports (ClassPath path) throws ClassNotFoundException
	{
		return path.createImports (getPackageImports (),
			getMemberTypeImports (), getSingleTypeImports (),
			getStaticTypeImports ());
	}

	static final ThreadLocal CLASSPATH = new ThreadLocal ();

	public CompilationUnit getObject () throws IOException
	{
		try
		{
			Tokenizer t = createTokenizer ();
			String s = source.getSystemId ();
			s = s.substring (Math.max (s.lastIndexOf ('/'), s.lastIndexOf ('\\')) + 1);
			t.setSource (((ReaderSource) source).getReader (), s);
			Parser p = createParser (t);
			p.setDumpTree (DUMP_TREE);
			p.parse ();

			ClassPath cp = getClassPath ();
			CompilerOptions opts = new CompilerOptions ();
			opts.lineNumberInfo = true;
			opts.sourceInfo = true;
			try
			{
				Class.forName ("java.lang.Enum");
				opts.javaVersion = Opcodes.V1_5;
			}
			catch (Exception e)
			{
			}
			return new CompilationUnit (cp, p.getAST (), p.getFilename (), p
				.getExceptionList (), getImports (cp), opts, getEnclosingAnnotations ());
		}
		catch (RecognitionException e)
		{
			throw new IOWrapException (e);
		}
		catch (ClassNotFoundException e)
		{
			throw new IOWrapException (e);
		}
	}

	/**
	 * Compiles the source of this filter into an array of the types
	 * which are declared by the source. This included top-level types,
	 * nested types, and local and anonymous classes. The returned types
	 * are wrappers for true <code>Class</code> objects, i.e., they
	 * represent true classes loaded by the Java virtual machine.
	 * 
	 * @return array of compiled, declared types of the source code 
	 * @throws IOException if source cannot be read or if there is a
	 * lexical, syntactic or semantic error
	 */
	public Type[] compile () throws IOException
	{
		return new CompilationFilter (null, this).compile (null, null);
	}

	
	/**
	 * This auxiliary method compiles the source code of <code>input</code>
	 * to an array of types
	 * 
	 * @param input
	 * @param systemId
	 * @return array of compiled, declared types of the source code 
	 * @throws IOException if source cannot be read or if there is a
	 * lexical, syntactic or semantic error
	 * 
	 * @see #compile()
	 */
	public static Type[] compile (Reader input, String systemId) throws IOException
	{
		return new XLFilter (null, new ReaderSourceImpl (input, systemId, MimeType.TEXT_PLAIN, Registry.current (), null)).compile ();
	}


	private static ASMTypeLoader loaderForAll;

	public static synchronized ASMTypeLoader getLoaderForAll ()
	{
		if (loaderForAll == null)
		{
			String extensionDirs = System.getProperty ("java.ext.dirs", null);
			File[] ext = (extensionDirs != null) ? ASMTypeLoader
				.getExtensionClassPath (extensionDirs) : new File[0];
			loaderForAll = new ASMTypeLoader (null, Main.getLoaderForAll ());
			try
			{
				loaderForAll.addFiles (LocalFileSystem.FILE_ADAPTER, ASMTypeLoader.getBootClassPath (ext),
					false);
			}
			catch (IOException e)
			{
				e.printStackTrace ();
			}
			loaderForAll.addFiles (LocalFileSystem.FILE_ADAPTER, ext, false);
			loaderForAll.addFiles (LocalFileSystem.FILE_ADAPTER, ASMTypeLoader.getClassPath (System
				.getProperty ("java.class.path")), false);
			Item.forAll (Main.getRegistry ().getPluginDirectory (), null, null,
				new ItemVisitor ()
				{
					public void visit (Item item, Object info)
					{
						if (item instanceof Library)
						{
							Library lib = (Library) item;
							loaderForAll.addFiles (lib.getPluginDescriptor ().getFileSystem (), lib.getLibraryFiles (), false);
						}
						else if (item instanceof PluginDescriptor)
						{
							PluginDescriptor pd = (PluginDescriptor) item;
							loaderForAll.addFiles (pd.getFileSystem (), pd.getLibraryFiles (), false);
						}
					}
				}, null, false);
		}
		return loaderForAll;
	}

}
