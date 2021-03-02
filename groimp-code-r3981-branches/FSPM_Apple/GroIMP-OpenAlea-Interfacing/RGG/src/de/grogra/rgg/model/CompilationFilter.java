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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import de.grogra.pf.io.FilterBase;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.FilterSource;
import de.grogra.pf.io.IO;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.ObjectSource;
import de.grogra.pf.io.ResourceLoader;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.TypeItem;
import de.grogra.pf.registry.Value;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.FileChooserResult;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.Window;
import de.grogra.pf.ui.Workbench;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.ClassAdapter.ClassLoaderWithPool;
import de.grogra.reflect.Type;
import de.grogra.rgg.Library;
import de.grogra.util.IOWrapException;
import de.grogra.util.MimeType;
import de.grogra.util.WrapException;
import de.grogra.vfs.MemoryFileSystem;
import de.grogra.xl.compiler.BytecodeWriter;
import de.grogra.xl.compiler.CClass;
import de.grogra.xl.compiler.CompilationUnit;
import de.grogra.xl.compiler.Compiler;
import de.grogra.xl.compiler.CompilerOptions;
import de.grogra.xl.compiler.Main;
import de.grogra.xl.compiler.scope.ClassPath;
import de.grogra.xl.compiler.scope.CompilationUnitScope;
import de.grogra.xl.util.ObjectList;

public class CompilationFilter extends FilterBase implements ObjectSource,
		ResourceLoader
{
	public static final MimeType COMPILATION_UNIT_TYPE = MimeType
		.valueOf (CompilationUnit.class);
	public static final IOFlavor CUNIT_FLAVOR = new IOFlavor (
		COMPILATION_UNIT_TYPE);

	public static final String DEACTIVATION_CATEGORY = "java.lang.Class";

	public static boolean WRITE_CLASSES = false;

	private CompilationUnitScope compilationUnit;

	private final ObjectList<FilterSource> sources = new ObjectList<FilterSource> ();
	
	public CompilationFilter (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (IOFlavor.RESOURCE_LOADER);
	}

	@Override
	public Object getObject ()
	{
		return this;
	}

	public CompilationUnitScope getCompilationUnit ()
	{
		return compilationUnit;
	}

	private static void add (ObjectList<Type<?>> list, Type<?> type)
	{
		list.add (type);
		for (int i = type.getDeclaredTypeCount () - 1; i >= 0; i--)
		{
			add (list, type.getDeclaredType (i));
		}
	}

	public Type<?>[] compile (CClass shell, ClassLoader parentLoader) throws IOException
	{
		return compile(shell, parentLoader, new MemoryFileSystem ("classes"));
	}

	private Type<?>[] compile (CClass shell, ClassLoader parentLoader, MemoryFileSystem fs) throws IOException
	{
		ObjectList<CompilationUnit> sourceList = new ObjectList<CompilationUnit> ();
		ObjectList<CompilationUnit> classList = new ObjectList<CompilationUnit> ();
		XLFilter.CLASSPATH.set (XLFilter.createDefaultClassPath (this));
		StringBuffer names = new StringBuffer();
		try
		{
			for (int i = -1; i < sources.size (); i++)
			{
				ObjectSource s = (i < 0) ? (ObjectSource) source : (ObjectSource) sources.get (i);
				CompilationUnit u = (CompilationUnit) s.getObject ();
				if (u.jarBytes != null)
				{
					classList.add (u);
				}
				else
				{
					sourceList.add (u);
					if (names.length() > 0)
					{
						names.append (", ");
					}
					names.append (s.getSystemId ());
				}
			}
		}
		finally
		{
			XLFilter.CLASSPATH.set (null);
		}
		CompilationUnit[] src = sourceList.toArray (new CompilationUnit[sourceList.size ()]);
		Compiler c = new Compiler ();
		CompilationUnitScope[] cus = c.compile (src, shell, null, null, null, true);
		if (cus.length > 0)
		{
			compilationUnit = cus[0];
		}
		int version = org.objectweb.asm.Opcodes.V1_5;
		CompilerOptions opts = null;
		if (src.length > 0)
		{
			opts = src[0].options;
			for (int i = 0; i < src.length; i++)
			{
				if (!src[i].options.supportsVersion (version))
				{
					opts = src[i].options;
					version = opts.javaVersion;
				}
				c.problems.addAll (src[i].problems);
			}
		}
		else
		{
			opts = new CompilerOptions();
		}
		if (c.problems.containsErrors ())
		{
			throw new IOWrapException (c.problems);
		}
		else if (c.problems.containsWarnings ())
		{
			Registry.current ().getLogger ().log (Workbench.GUI_INFO,
				Library.I18N.msg ("xl.compilation-warnings", names),
				c.problems);
		}
		else if (shell == null)
		{
			Registry.current ().getLogger ().log (Workbench.SOFT_GUI_INFO,
				Library.I18N.msg ("xl.compilation-successful", names));
		}
		ObjectList<Type<?>> types = new ObjectList<Type<?>> ();
		for (int j = 0; j < cus.length; j++)
		{
			Type<?>[] a = cus[j].getDeclaredTypes ();
			for (int k = 0; k < a.length; k++)
			{
				add (types, a[k]);
			}
		}
		for (int i = 0; i < classList.size(); i++)
		{
			fs.readJar(new ByteArrayInputStream(classList.get(i).jarBytes), false);
		}
		BytecodeWriter w = new BytecodeWriter (opts);
		for (CompilationUnitScope cs : cus)
		{
			w.write (cs, fs, fs.getRoot ());
		}
		if (parentLoader == null)
		{
			if (compilationUnit != null)
			{
				parentLoader = ClassPath.get (compilationUnit).getClassLoader ();
			}
			else
			{
				parentLoader = XLFilter.getLoaderForRegistry (this).getClassLoader();
			}
		}
		ClassAdapter.URLClassLoaderWithPool loader = new ClassAdapter.URLClassLoaderWithPool (
			new URL[] {fs.toURL (fs.getRoot ())}, parentLoader);
		
		if (WRITE_CLASSES)
		{
			File file = new File (System.getProperty ("user.home"), "classes.zip");
			OutputStream out = new BufferedOutputStream (new FileOutputStream (file));
			fs.writeJar (out);
			out.flush ();
			out.close ();
		}

		ObjectList<Type<?>> compiledTypes = new ObjectList<Type<?>>();
		ObjectList<Object> dirStack = new ObjectList<Object>();
		dirStack.push(fs.getRoot());
		while (!dirStack.isEmpty())
		{
			Object dir = dirStack.pop();
			int dc = fs.getChildCount(dir);
			for (int i = 0; i < dc; i++)
			{
				Object child = fs.getChild(dir, i);
				if (fs.isLeaf(child))
				{
					String name = fs.getPath(child);
					if (name.endsWith(".class"))
					{
						name = name.substring(0, name.length() - 6).replace('/', '.');
						try
						{
							compiledTypes.push(ClassAdapter.wrap (Class.forName (name, false, loader), loader));
						}
						catch (ClassNotFoundException e)
						{
							throw new IOWrapException (e);
						}
					}
				}
				else
				{
					dirStack.push(child);
				}
			}
		}
		return compiledTypes.toArray(new Type<?>[compiledTypes.size()]);
	}

	@Override
	public void loadResource (Registry r) throws IOException
	{
		MemoryFileSystem fs = new MemoryFileSystem("classes");
		Type<?>[] a = compile (null, null, fs);

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		OutputStream out = new BufferedOutputStream (bout);
		fs.writeJar (out);
		out.flush ();
		out.close ();
		for (int i = 0; i < a.length; i++)
		{
			r.getDirectory ("/classes", null).add (new TypeItem (a[i]));
		}
		r.getDirectory ("/compiled", null).add (new Value("rggc", bout.toByteArray()));
	}

	@Override
	public boolean addResource (FilterSource source)
	{
		FilterSource cu = IO.createPipeline (source, CUNIT_FLAVOR);
		if (cu == null)
		{
			return false;
		}
		sources.add (cu);
		return true;
	}

	@Override
	public String getJoinedDeactivationCategory ()
	{
		return DEACTIVATION_CATEGORY;
	}

	public static void exportCompiled (Item item, Object info, Context ctx)
	{
		Item i = ctx.getWorkbench().getRegistry().getItem("/compiled/rggc");
		if (!(i instanceof Value))
		{
			ctx.getWindow().showDialog(Library.I18N.getString("rgg.rgg-compiled-no-classes", "No compiled classes."),
									   Library.I18N.getString("rgg.rgg-compiled-no-classes-verbose", "No compiled classes."), Window.PLAIN_MESSAGE);
			return;
		}
		FileChooserResult fr = ctx.getWorkbench().chooseFileToSave(UI.I18N.getString("filedialog.saveproject", "Save Project"), IOFlavor.valueOf(CompiledProject.class), null);
		if (fr != null)
		{
			boolean mod = ctx.getWorkbench().isModified();
			ctx.getWorkbench().save(new CompiledProject(ctx.getWorkbench().getRegistry()), fr.file, fr.getMimeType());
			if (!mod)
			{
				ctx.getWorkbench().setModified(false);
			}
		}
	}
}
