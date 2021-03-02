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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;

import de.grogra.grammar.ASTWithToken;
import de.grogra.grammar.Tokenizer;
import de.grogra.pf.io.ReaderSourceImpl;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ItemVisitor;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.TypeItem;
import de.grogra.pf.ui.Command;
import de.grogra.pf.ui.Console;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.JobManager;
import de.grogra.pf.ui.UI;
import de.grogra.pf.ui.Workbench;
import de.grogra.reflect.Field;
import de.grogra.reflect.Member;
import de.grogra.reflect.Method;
import de.grogra.reflect.Reflection;
import de.grogra.reflect.Type;
import de.grogra.rgg.Library;
import de.grogra.util.DelegatingClassLoader;
import de.grogra.util.DetailedException;
import de.grogra.util.Map;
import de.grogra.util.MimeType;
import de.grogra.util.Utils;
import de.grogra.xl.compiler.CClass;
import de.grogra.xl.compiler.scope.ClassPath;
import de.grogra.xl.compiler.scope.CompilationUnitScope;
import de.grogra.xl.compiler.scope.Package;
import de.grogra.xl.compiler.scope.PackageImportOnDemand;
import de.grogra.xl.compiler.scope.Scope;
import de.grogra.xl.lang.DisposableIterator;
import de.grogra.xl.parser.Parser;
import de.grogra.xl.parser.XLParser;

public final class ShellFilter extends RGGFilter
{
	public static final String SHELL_PACKAGE = "$shell$";
	public static final String SHELL_CLASS = "Shell";

	private final ClassPath path;
	private final CompilationUnitScope last;

	public ShellFilter (Registry reg, ClassPath path, String command,
			CompilationUnitScope last)
	{
		super (null, new ReaderSourceImpl (new StringReader (command),
			"console", MimeType.TEXT_PLAIN, reg, null));
		this.path = path;
		this.last = last;
	}

	@Override
	ClassPath getClassPath ()
	{
		return path;
	}

	@Override
	protected Scope getImports (ClassPath path) throws ClassNotFoundException
	{
		if (last == null)
		{
			return new PackageImportOnDemand (super.getImports (path), path.getPackage ("", true));
		}
		else
		{
			for (Scope s = last; s != null; s = s.getEnclosingScope ())
			{
				if (s.getEnclosingScope () instanceof Package)
				{
					s.insert (null);
					break;
				}
			}
			return last.getEnclosingScope ();
		}
	}

	@Override
	protected Parser createParser (Tokenizer t)
	{
		XLParser p = new XLParser (t);
		p.setShell (new ASTWithToken (IDENT, SHELL_PACKAGE), SHELL_CLASS);
		return p;
	}

	public static Console createConsole (Context ctx, Map params)
	{
		final Workbench wb = ctx.getWorkbench ();
		final Console cons = wb.getToolkit ().createConsole (ctx, params);

		class ConsoleRunnable implements Runnable, Command, ItemVisitor
		{
			static final String PROMPT = "> ";

			ClassPath path = new ClassPath (XLFilter.getLoaderForAll ());
			CClass shell;
			ArrayList<ClassLoader> loaders = new ArrayList<ClassLoader> ();

			ConsoleRunnable ()
			{
				shell = new CClass (SHELL_CLASS,
					SHELL_PACKAGE + '.' + SHELL_CLASS, Member.PUBLIC | Member.ABSTRACT,
					null, true);
				shell.initSupertype (Type.OBJECT);
				shell.initTypeLoader (path);
			}

			public void run ()
			{
				cons.getOut ().print (PROMPT);
				cons.getOut ().flush ();
				StringBuffer line = new StringBuffer ();
				try
				{
					int c;
					while ((c = cons.getIn ().read ()) >= 0)
					{
						if (c != '\n')
						{
							line.append ((char) c);
						}
						else if ((line.length () > 0)
							&& (line.charAt (line.length () - 1) == '\\'))
						{
							line.setCharAt (line.length () - 1, '\n');
						}
						else
						{
							UI.executeLockedly (wb.getRegistry ()
								.getProjectGraph (), true, this, line
								.toString (), wb, JobManager.ACTION_FLAGS);
							line.setLength (0);
						}
					}
				}
				catch (IOException e)
				{
					e.printStackTrace ();
				}
				synchronized (this)
				{
					path = null;
					shell = null;
				}
			}

			public void visit (Item item, Object info)
			{
				if (item instanceof TypeItem)
				{
					Type t = (Type) ((TypeItem) item).getObject ();
					if (t.getDeclaringType () == null)
					{
						path.getPackage (t.getPackage (), true).declareType (t);
						Class cls = t.getImplementationClass ();
						if (!Reflection.canLoad (path.getClassLoader (), cls))
						{
							ClassLoader x = cls.getClassLoader ();
							for (int i = loaders.size () - 1; i >= 0; i--)
							{
								if (Reflection.canLoad (loaders.get (i), cls))
								{
									return;
								}
							}
							for (int i = loaders.size () - 1; i >= 0; i--)
							{
								if (Reflection.isAncestorOrSame (loaders
									.get (i), x))
								{
									loaders.remove (i);
								}
							}
							loaders.add (x);
						}
					}
				}
			}

			public String getCommandName ()
			{
				return cons.getPanelId ();
			}

			private CompilationUnitScope lastScope;
			private Type lastShell;

			public synchronized void run (Object info, Context c)
			{
				String cmd = (String) info;
				Throwable ex = null;
				if ((shell != null) && (cmd.trim ().length () > 0))
				{
					for (int i = shell.getDeclaredMethodCount () - 1; i >= 0; i--)
					{
						shell.removeMethod (shell.getDeclaredMethod (i));
					}
					Registry reg = c.getWorkbench ().getRegistry ();
					loaders.clear ();
					Item
						.forAll (reg, "/classes", null, null, this, null, false);
					ClassLoader cl = path.getClassLoader ();
					if (!loaders.isEmpty ())
					{
						loaders.add (cl);
						cl = new DelegatingClassLoader (loaders
							.toArray (new ClassLoader[loaders.size ()]));
					}
					HashSet<String> removedFields = new HashSet<String> ();
					HashSet<Field> oldFields = new HashSet<Field> ();
					for (int i = shell.getDeclaredFieldCount () - 1; i >= 0; i--)
					{
						Field x = shell.getDeclaredField (i);
						if (!(Reflection.isPrimitive (x.getType ()) || Reflection
							.canLoad (cl, x.getType ()
								.getImplementationClass ())))
						{
							shell.removeField (x);
							removedFields.add (x.getDescriptor ());
						}
						else
						{
							oldFields.add (x);
						}
					}
					CompilationFilter f;
					Type compiledShell;
					try
					{
						if (cmd.charAt (cmd.length () - 1) != ';')
						{
							cmd += "\n;";
						}
						f = new CompilationFilter (null, new ShellFilter (reg,
							path, cmd, lastScope));
						compiledShell = f.compile (shell, cl)[0];
					}
					catch (IOException e)
					{
						for (int i = shell.getDeclaredFieldCount () - 1; i >= 0; i--)
						{
							Field x = shell.getDeclaredField (i);
							if (!oldFields.contains (x))
							{
								shell.removeField (x);
							}
						}
						f = null;
						compiledShell = null;
						Throwable t = Utils.getMainException (e);
						if (t instanceof DetailedException)
						{
							cons.getErr ().println (
								((DetailedException) t)
									.getDetailedMessage (false));
						}
						else
						{
							ex = t;
						}
					}
					if (f != null)
					{
						lastScope = f.getCompilationUnit ();
						if (lastShell != null)
						{
							for (int i = lastShell.getDeclaredFieldCount () - 1; i >= 0; i--)
							{
								Field x = lastShell.getDeclaredField (i);
								if ((x.getModifiers () & (Member.PUBLIC | Member.SYNTHETIC))
									== Member.PUBLIC)
								{
									Field y = Reflection.getDeclaredField (
										compiledShell, x.getDescriptor ());
									if ((y != null) && !removedFields.contains (x.getDescriptor ()))
									{
										try
										{
											Object value = Reflection.get (null, x);
											Reflection.set (null, y, value);
										}
										catch (Exception e)
										{
											cons.getErr ().println (x.getDescriptor () + " " + e);
										}
									}
								}
							}
						}
						lastShell = compiledShell;
						Method m = Reflection.getDeclaredMethod (compiledShell,
							"execute");
						if (m != null)
						{
							try
							{
								if (Runtime.INSTANCE.currentGraph () != null)
								{
									DisposableIterator i = Library.apply (1);
									try
									{
										while (i.next ())
										{
											m.invoke (null, null);
										}
										i.dispose (null);
									}
									catch (Throwable e)
									{
										i.dispose (e);
										Utils.rethrow (e);
									}
								}
								else
								{
									m.invoke (null, null);
								}
							}
							catch (Exception e)
							{
								ex = e;
							}
						}
					}
				}
				if (ex != null)
				{
					cons.getErr ().println (ex);
				}
				cons.getOut ().print (PROMPT);
				cons.getOut ().flush ();
				Utils.rethrow (ex);
			}
		}

		new Thread (new ConsoleRunnable (), "Console@" + ctx.getWorkbench ())
			.start ();
		return cons;
	}
}
