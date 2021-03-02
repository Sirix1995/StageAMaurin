
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

package de.grogra.grogra;

import de.grogra.pf.io.*;
import de.grogra.grammar.*;
import de.grogra.xl.parser.*;
import de.grogra.rgg.model.*;

public class LSYFilter extends XLFilter
{

	public LSYFilter (FilterItem item, ReaderSource source)
	{
		super (item, source);
	}


	@Override
	protected Tokenizer createTokenizer ()
	{
		return new LSYTokenizer ();
	}


	@Override
	protected Parser createParser (Tokenizer t)
	{
		LSYParser p = new LSYParser (t);
		p.className = getClassName ();
		return p;
	}

	
	@Override
	protected String[] getPackageImports ()
	{
		return new String[] {"java.lang", "de.grogra.turtle", "de.grogra.grogra"};
	}

	
	@Override
	protected Class[] getMemberTypeImports ()
	{
		return new Class[0];
	}

	
	@Override
	protected Class[] getSingleTypeImports ()
	{
		return new Class[] {de.grogra.rgg.Axiom.class};
	}

	
	@Override
	protected Class[] getStaticTypeImports ()
	{
		return new Class[0];
	}


	@Override
	protected boolean isD2FWidening ()
	{
		return true;
	}

}

