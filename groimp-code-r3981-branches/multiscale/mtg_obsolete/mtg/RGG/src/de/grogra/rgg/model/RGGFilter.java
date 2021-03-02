
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

import java.util.ArrayList;
import java.util.List;
import de.grogra.grammar.ASTWithToken;
import de.grogra.grammar.Tokenizer;
import de.grogra.pf.io.FilterItem;
import de.grogra.pf.io.ReaderSource;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.RegistryContext;
import de.grogra.pf.registry.expr.Var;
import de.grogra.pf.ui.Workbench;
import de.grogra.reflect.Annotation;
import de.grogra.reflect.AnnotationImpl;
import de.grogra.xl.lang.ImplicitDoubleToFloat;
import de.grogra.xl.parser.Parser;
import de.grogra.xl.parser.XLParser;
import de.grogra.xl.parser.XLTokenTypes;
import de.grogra.xl.util.ObjectList;

public class RGGFilter extends XLFilter implements XLTokenTypes
{

	public RGGFilter (FilterItem item, ReaderSource source)
	{
		super (item, source);
	}


	@Override
	protected Parser createParser (Tokenizer t)
	{
		XLParser p = new XLParser (t);
		p.setSimple (new ASTWithToken (MODIFIERS, null).add (PUBLIC_, null).add (STATIC_MEMBER_CLASSES, null),
					 new ASTWithToken (IDENT, getClassName ()),
					 new ASTWithToken (EXTENDS, null).add (IDENT, "RGG"),
					 null);
		return p;
	}

	
	@Override
	protected String[] getPackageImports ()
	{
		List<String> imports = new ArrayList<String>();
		Registry reg = getRegistry().getRootRegistry();
		RegistryContext ctx = Workbench.current();
		Item dir = reg.getItem("/io/filter/rgg/packageimports");
		for (dir = (Item) dir.getBranch (); dir != null;
			 dir = (Item) dir.getSuccessor ())
		{
			if (dir.resolveLink (ctx) instanceof Var)
			{
				Var item = (Var) dir.resolveLink(ctx);
				imports.add(item.getName());
			}
		}
		String[] result = new String[imports.size()];
		imports.toArray(result);
		return result;
	}

	
	@Override
	protected Class[] getSingleTypeImports () throws ClassNotFoundException
	{
		List<Class> imports = new ArrayList<Class>();
		Registry reg = getRegistry().getRootRegistry();
		RegistryContext ctx = Workbench.current();
		Item dir = reg.getItem("/io/filter/rgg/singletypeimports");
		for (dir = (Item) dir.getBranch (); dir != null;
			 dir = (Item) dir.getSuccessor ())
		{
			if (dir.resolveLink (ctx) instanceof Var)
			{
				Var item = (Var) dir.resolveLink(ctx);
				imports.add(Class.forName(item.getName(),
						true, item.getClassLoader()));
			}
		}
		Class[] result = new Class[imports.size()];
		imports.toArray(result);
		return result;
	}

	
	@Override
	protected Class[] getStaticTypeImports () throws ClassNotFoundException
	{
		List<Class> imports = new ArrayList<Class>();
		Registry reg = getRegistry().getRootRegistry();
		RegistryContext ctx = Workbench.current();
		Item dir = reg.getItem("/io/filter/rgg/statictypeimports");
		for (dir = (Item) dir.getBranch (); dir != null;
			 dir = (Item) dir.getSuccessor ())
		{
			if (dir.resolveLink (ctx) instanceof Var)
			{
				Var item = (Var) dir.resolveLink(ctx);
				imports.add(Class.forName(item.getName(),
						true, item.getClassLoader()));
			}
		}
		Class[] result = new Class[imports.size()];
		imports.toArray(result);
		return result;
	}

	@Override
	protected ObjectList<Annotation> getEnclosingAnnotations ()
	{
		return super.getEnclosingAnnotations ().push (new AnnotationImpl (ImplicitDoubleToFloat.class).setValue ("value", true));
	}

}

