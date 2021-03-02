
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

package de.grogra.grammar;

import de.grogra.util.*;
import de.grogra.xl.util.ObjectList;

/**
 * This class manages a list of <code>RecognitionException</code>s.
 * 
 * @author Ole Kniemeyer
 */
public class RecognitionExceptionList extends RecognitionException
{
	private final long warningMask;
	private final long errorMask;

	private long nextWarningBits;
	private boolean nextWarningBitsSet = false;
	private ObjectList<RecognitionException> list = new ObjectList<RecognitionException> ();
	private int errorCount = 0, warningCount = 0;
	private ObjectList<RecognitionException> suppressedErrors = new ObjectList<RecognitionException> ();


	public RecognitionExceptionList (long warningMask, long errorMask)
	{
		super ("RecognitionExceptionList");
		this.warningMask = warningMask | errorMask
			| RecognitionException.IS_ERROR;
		this.errorMask = errorMask | RecognitionException.IS_ERROR;
	}


	public RecognitionExceptionList ()
	{
		this (-1L, 0);
	}


	public void check () throws RecognitionExceptionList
	{
		if (errorCount > 0)
		{
			throw this;
		}
	}


	public void reset ()
	{
		nextWarningBitsSet = false;
		errorCount = 0;
		warningCount = 0;
		list.clear ();
	}


	private static final RecognitionException[] EXCEPTION_0
		= new RecognitionException[0];

	/**
	 * Enables the addition of exceptions. <code>disableAdd</code> must have
	 * been invoked previously. This method returns those exceptions
	 * which would have been added between the corresponding invocation
	 * of <code>disableAdd</code> and this invocation, but were not due
	 * to <code>disableAdd</code>.
	 * 
	 * @return list of suppressed exceptions (never <code>null</code>,
	 * but the length may be 0)
	 */
	public RecognitionExceptionList enableAdd ()
	{
		int i = suppressedErrors.size ();
		while (suppressedErrors.get (i - 1) != null)
		{
			i--;
		}
		RecognitionExceptionList a = new RecognitionExceptionList (warningMask, errorMask);
		for (int j = i; j < suppressedErrors.size (); j++)
		{
			a.add (suppressedErrors.get (j));
		}
		suppressedErrors.setSize (i - 1);
		return a;
	}


	/**
	 * Disables the addition of exceptions to this list. Pairs of
	 * <code>disableAdd</code> and <code>enableAdd</code> may be nested.
	 * 
	 * @see #enableAdd()
	 */
	public void disableAdd ()
	{
		suppressedErrors.push (null);
	}


	public boolean isAddEnabled ()
	{
		return suppressedErrors.isEmpty ();
	}

	public boolean containsErrors ()
	{
		return errorCount > 0;
	}


	public int getErrorCount ()
	{
		return errorCount;
	}


	public boolean containsWarnings ()
	{
		return warningCount > 0;
	}

	
	public boolean isEmpty ()
	{
		return list.isEmpty ();
	}


	public boolean isWarning (long bits)
	{
		if ((bits & warningMask) == 0)
		{
			return false;
		}
		nextWarningBits = bits;
		nextWarningBitsSet = true;
		return true;
	}


	public void add (RecognitionException e)
	{
		nextWarningBitsSet = false;
		e.isError = (e.warningBits & errorMask) != 0;
		if (suppressedErrors.isEmpty ())
		{
			e.stamp = list.size ();
			list.addInOrder (e);
			if (e.isError)
			{
				errorCount++;
			}
			else
			{
				warningCount++;
			}
		}
		else
		{
			suppressedErrors.push (e);
		}
	}

	
	public void addAll (RecognitionExceptionList list)
	{
		for (int i = 0; i < list.list.size (); i++)
		{
			add (list.list.get (i));
		}
	}

	
	public void addAll (RecognitionException[] list)
	{
		for (int i = 0; i < list.length; i++)
		{
			add (list[i]);
		}
	}


	public void addWarning (RecognitionException warning, long warningBits)
	{
		warning.warningBits = warningBits;
		add (warning);
	}


	public void addWarning (RecognitionException warning)
	{
		assert nextWarningBitsSet;
		warning.warningBits = nextWarningBits;
		add (warning);
	}

/*!!
#foreach ($t in ["Lexical", "Syntactic", "Semantic"])

	public void add${t}Warning (String warning, antlr.Token token)
	{
		addWarning (new ${t}Exception (warning).set (token));
	}


	public void add${t}Warning (String warning, antlr.collections.AST node)
	{
		addWarning (new ${t}Exception (warning).set (node));
	}


	public void add${t}Warning (I18NBundle bundle, String key,
								antlr.Token token)
	{
		addWarning (new ${t}Exception (bundle.msg (key)).set (token));
	}


	public void add${t}Warning (I18NBundle bundle, String key,
								antlr.collections.AST node)
	{
		addWarning (new ${t}Exception (bundle.msg (key)).set (node));
	}


	public void add${t}Error (String error, antlr.Token token)
	{
		add (new ${t}Exception (error).set (token));
	}


	public void add${t}Error (String error, antlr.collections.AST node)
	{
		add (new ${t}Exception (error).set (node));
	}


	public void add${t}Error (I18NBundle bundle, String key, antlr.Token token)
	{
		add (new ${t}Exception (bundle.msg (key)).set (token));
	}


	public void add${t}Error (I18NBundle bundle, String key,
							  antlr.collections.AST node)
	{
		add (new ${t}Exception (bundle.msg (key)).set (node));
	}

#end
!!*/
//!! #* Start of generated code
// generated
	public void addLexicalWarning (String warning, antlr.Token token)
	{
		addWarning (new LexicalException (warning).set (token));
	}
// generated
// generated
	public void addLexicalWarning (String warning, antlr.collections.AST node)
	{
		addWarning (new LexicalException (warning).set (node));
	}
// generated
// generated
	public void addLexicalWarning (I18NBundle bundle, String key,
								antlr.Token token)
	{
		addWarning (new LexicalException (bundle.msg (key)).set (token));
	}
// generated
// generated
	public void addLexicalWarning (I18NBundle bundle, String key,
								antlr.collections.AST node)
	{
		addWarning (new LexicalException (bundle.msg (key)).set (node));
	}
// generated
// generated
	public void addLexicalError (String error, antlr.Token token)
	{
		add (new LexicalException (error).set (token));
	}
// generated
// generated
	public void addLexicalError (String error, antlr.collections.AST node)
	{
		add (new LexicalException (error).set (node));
	}
// generated
// generated
	public void addLexicalError (I18NBundle bundle, String key, antlr.Token token)
	{
		add (new LexicalException (bundle.msg (key)).set (token));
	}
// generated
// generated
	public void addLexicalError (I18NBundle bundle, String key,
							  antlr.collections.AST node)
	{
		add (new LexicalException (bundle.msg (key)).set (node));
	}
// generated
// generated
	public void addSyntacticWarning (String warning, antlr.Token token)
	{
		addWarning (new SyntacticException (warning).set (token));
	}
// generated
// generated
	public void addSyntacticWarning (String warning, antlr.collections.AST node)
	{
		addWarning (new SyntacticException (warning).set (node));
	}
// generated
// generated
	public void addSyntacticWarning (I18NBundle bundle, String key,
								antlr.Token token)
	{
		addWarning (new SyntacticException (bundle.msg (key)).set (token));
	}
// generated
// generated
	public void addSyntacticWarning (I18NBundle bundle, String key,
								antlr.collections.AST node)
	{
		addWarning (new SyntacticException (bundle.msg (key)).set (node));
	}
// generated
// generated
	public void addSyntacticError (String error, antlr.Token token)
	{
		add (new SyntacticException (error).set (token));
	}
// generated
// generated
	public void addSyntacticError (String error, antlr.collections.AST node)
	{
		add (new SyntacticException (error).set (node));
	}
// generated
// generated
	public void addSyntacticError (I18NBundle bundle, String key, antlr.Token token)
	{
		add (new SyntacticException (bundle.msg (key)).set (token));
	}
// generated
// generated
	public void addSyntacticError (I18NBundle bundle, String key,
							  antlr.collections.AST node)
	{
		add (new SyntacticException (bundle.msg (key)).set (node));
	}
// generated
// generated
	public void addSemanticWarning (String warning, antlr.Token token)
	{
		addWarning (new SemanticException (warning).set (token));
	}
// generated
// generated
	public void addSemanticWarning (String warning, antlr.collections.AST node)
	{
		addWarning (new SemanticException (warning).set (node));
	}
// generated
// generated
	public void addSemanticWarning (I18NBundle bundle, String key,
								antlr.Token token)
	{
		addWarning (new SemanticException (bundle.msg (key)).set (token));
	}
// generated
// generated
	public void addSemanticWarning (I18NBundle bundle, String key,
								antlr.collections.AST node)
	{
		addWarning (new SemanticException (bundle.msg (key)).set (node));
	}
// generated
// generated
	public void addSemanticError (String error, antlr.Token token)
	{
		add (new SemanticException (error).set (token));
	}
// generated
// generated
	public void addSemanticError (String error, antlr.collections.AST node)
	{
		add (new SemanticException (error).set (node));
	}
// generated
// generated
	public void addSemanticError (I18NBundle bundle, String key, antlr.Token token)
	{
		add (new SemanticException (bundle.msg (key)).set (token));
	}
// generated
// generated
	public void addSemanticError (I18NBundle bundle, String key,
							  antlr.collections.AST node)
	{
		add (new SemanticException (bundle.msg (key)).set (node));
	}
// generated
//!! *# End of generated code

	@Override
	public String getMessage ()
	{
		return (errorCount == 0)
			? Tokenizer.I18N.msg ("grammar.error-list-no-errors", warningCount)
			: Tokenizer.I18N.msg ("grammar.error-list-errors", errorCount, warningCount);
	}


	@Override
	public String getDetailedMessage (int firstLine, int firstColumn, int tabWidth,
									  boolean html)
	{
		StringBuffer b = new StringBuffer (getMessage ());
		if (html)
		{
			Utils.escapeForXML (b, 0);
		}
		int s = list.size ();
		boolean truncate;
		if (html && (s > 60))
		{
			s = 50;
			truncate = true;
		}
		else
		{
			truncate = false;
		}
		for (int i = 0; i < s; i++)
		{
			b.append (html ? "<br>\n" : "\n");
			b.append (list.get (i)
					  .getDetailedMessage (firstLine, firstColumn, tabWidth, html));
		}
		if (truncate)
		{
			b.append ("<br>\n")
				.append (Tokenizer.I18N.msg ("grammar.further-errors", list.size () - s));
		}
		return b.toString ();
	}

	
	@Override
	public void dispose ()
	{
		super.dispose ();
		for (int i = list.size () - 1; i >= 0; i--)
		{
			list.get (i).dispose ();
		}
	}

}
