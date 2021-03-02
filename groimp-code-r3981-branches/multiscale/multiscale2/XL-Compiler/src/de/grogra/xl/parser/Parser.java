
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

package de.grogra.xl.parser;

import de.grogra.grammar.*;
import antlr.collections.AST;
import java.io.*;

public abstract class Parser extends antlr.LLkParser
{
	protected final RecognitionExceptionList exceptionList
		= createExceptionList ();

	private Tokenizer tokenizer = null;
	private final ASTWithTokenFactory factory = new ASTWithTokenFactory ();
	
	private boolean dumpTree;


	protected Parser (antlr.TokenBuffer buffer, int k)
	{
		super (buffer, k);
		if (buffer.getInput () instanceof Tokenizer)
		{
			setTokenizer ((Tokenizer) buffer.getInput ());
		}
	}


	protected Parser (antlr.TokenStream lexer, int k)
	{
		super (lexer, k);
		if (lexer instanceof Tokenizer)
		{
			setTokenizer ((Tokenizer) lexer);
		}
	}


	protected Parser (antlr.ParserSharedInputState state, int k)
	{
		super (state, k);
	}


	private void setTokenizer (Tokenizer t)
	{
		setFilename (t.getInput ().getSourceName ());
		tokenizer = t;
	}


	public void setSource (Reader source)
	{
		tokenizer.setSource (source);
		setFilename (tokenizer.getInput ().getSourceName ());
	}


	public void setSource (Reader source, String name)
	{
		tokenizer.setSource (source, name);
		setFilename (tokenizer.getInput ().getSourceName ());
	}

	
	public void setDumpTree (boolean dump)
	{
		this.dumpTree = dump;
	}


	protected abstract AST parseGoalSymbol ()
		throws antlr.RecognitionException, antlr.TokenStreamException;


	public void reset ()
	{
		inputState.reset ();
		exceptionList.reset ();
	}


	public final void parse () throws RecognitionException, IOException
	{
		setASTFactory (factory);
		exceptionList.reset ();
		try
		{
			returnAST = parseGoalSymbol ();
		}
		catch (antlr.ANTLRException e)
		{
			returnAST = null;
			exceptionList.add (RecognitionException.convert
							   (e, getTokenNames ()));
		}
		tokenizer.getInput ().close ();
		if (dumpTree)
		{
			printTree (returnAST, 0);
		}
		exceptionList.check ();
	}


	public final RecognitionExceptionList getExceptionList ()
	{
		return exceptionList;
	}


	protected RecognitionExceptionList createExceptionList ()
	{
		return new RecognitionExceptionList ();
	}


	public void printTree (AST root, int indentation)
	{
		while (root != null)
		{
			for (int i = 0; i < indentation; i++)
			{
				System.err.print ("    ");
			}
			System.err.println (getTokenNames ()[root.getType ()]
								+ " (" + root.getText () + ')');
			printTree (root.getFirstChild (), indentation + 1);
			root = root.getNextSibling ();
		}
	}

	static AST setToken (AST ast, AST tokenSrc)
	{
		if ((ast instanceof ASTWithToken)
			&& (tokenSrc instanceof ASTWithToken))
		{
			((ASTWithToken) ast).token = ((ASTWithToken) tokenSrc).token;
		}
		return ast;
	}

}
