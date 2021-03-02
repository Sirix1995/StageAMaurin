
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
import antlr.collections.AST;

public class RecognitionException extends antlr.RecognitionException
	implements UserException, DetailedException, Comparable<RecognitionException>
{
	public static final long IS_ERROR = 1;
	public static final long MIN_UNUSED = IS_ERROR << 1;

	public long warningBits = IS_ERROR;
	boolean isError = true;
	int stamp = 0;
	
	private int start = -1, end = -1;
	private Input input;


	public RecognitionException (String msg)
	{
		super (msg, null, -1, -1);
	}


	public boolean isError ()
	{
		return isError;
	}


	public String getCategoryDescription ()
	{
		return Tokenizer.I18N.msg (isError () ? "grammar.error"
								   : "grammar.warning");
	}


	public final void complete (Input input, int begin, int end)
	{
		if (this.input == null)
		{	    
			this.input = input;
			this.fileName = input.getSourceName ();
		}
		if (this.start < 0)
		{
			this.start = begin;
		}
		if (this.end < 0)
		{
			this.end = end;
		}
	}


	public final RecognitionException set (Input input, int begin, int end)
	{
		this.input = input;
		this.fileName = input.getSourceName ();
		this.start = begin;
		this.end = end;
		return this;
	}


	public final RecognitionException set (antlr.RecognitionException ex)
	{
		fileName = ex.fileName;
		line = ex.line;
		column = ex.column;
		if (ex instanceof RecognitionException)
		{
			input = ((RecognitionException) ex).input;
			start = ((RecognitionException) ex).start;
			end = ((RecognitionException) ex).end;
		}
		return this;
	}


	public final RecognitionException set (Tokenizer tokenizer)
	{
		tokenizer.set (this);
		return this;
	}


	public final RecognitionException set (antlr.Token token)
	{
		if (token != null)
		{
			fileName = token.getFilename ();
			if (token instanceof Token)
			{
				Token t = (Token) token;
				set (t.getTokenizer ().getInput (), t.getStartPosition (),
					 t.getEndPosition ());
			}
			else
			{
				line = token.getLine ();
				column = token.getColumn ();
			}
		}
		return this;
	}


	private void set1 (AST node)
	{
		if (node instanceof ASTWithToken)
		{
			antlr.Token token = ((ASTWithToken) node).token;
			if (token instanceof Token)
			{
				Token t = (Token) token;
				input = t.getTokenizer ().getInput ();
				fileName = input.getSourceName ();
				if ((start < 0) || (start > t.getStartPosition ()))
				{
					start = t.getStartPosition ();
				}
				if (end < t.getEndPosition ())
				{
					end = t.getEndPosition ();
				}
			}
		}
	}


	private void setTree (AST node)
	{
		set1 (node);
		for (node = node.getFirstChild (); node != null;
			 node = node.getNextSibling ())
		{
			setTree (node);
		}
	}


	public final RecognitionException set (AST node)
	{
		if (node != null)
		{
			setTree (node);
		}
		return this;
	}


	@Override
	public int getLine ()
	{
		return ((line >= 0) || (input == null) || (start < 0)) ? line
			: (line = input.getLineAt (start));
	}


	@Override
	public int getColumn ()
	{
		return ((column >= 0) || (input == null) || (start < 0)) ? column
			: (column = input.getColumnAt (start, 1));
	}


	
	private static final String INDENT = "        ";

	private static void append (StringBuffer b, String s, int line, int tabWidth)
	{
		String ls = Integer.toString (line);
		for (int i = ls.length (); i < 6; i++)
		{
			b.append (' ');
		}
		b.append (ls).append (". ");
		int c = 0, l = s.length ();
		for (int i = 0; i < l; i++)
		{
			if (s.charAt (i) == '\t')
			{
				int t = (c / tabWidth + 1) * tabWidth;
				while (t > c)
				{
					c++;
					b.append (' ');
				}
			}
			else
			{
				c++;
				b.append (s.charAt (i));
			}
		}
		b.append ('\n');
	}


	public String getDetailedMessage (boolean html)
	{
		return getDetailedMessage (1, 1, 8, html);
	}


	public String getDetailedMessage (int firstLine, int firstColumn, int tabWidth,
									  boolean html)
	{
		String f = (fileName == null) ? "input" : fileName;
		StringBuffer b = new StringBuffer ();
		int beginHTMLCheck;
		if ((input == null) || (start < 0) || (end < 0))
		{
			if (html)
			{
				b.append ("<a href=\"").append (f).append ("\">").append (f)
					.append ("</a>:<br>");
			}
			else
			{
				b.append (f).append (":\n");
			}
			beginHTMLCheck = b.length ();
		}
		else
		{
			int l0 = input.getLineAt (start),
				c0 = input.getColumnAt (start, tabWidth),
				l1 = input.getLineAt (end),
				c1 = input.getColumnAt (end, tabWidth);
			if (html)
			{
				b.append ("<a href=\"").append (f).append ('#').append (l0)
					.append (':').append (input.getColumnAt (start, 1))
					.append ('-').append (l1).append (':')
					.append (input.getColumnAt (end, 1)).append ("\">");
			}
			b.append (f).append (' ').append (l0 + firstLine)
				.append (':').append (c0 + firstColumn)
				.append (" - ").append (l1 + firstLine)
				.append (':').append (c1 + firstColumn)
				.append (html ? "</a> :<br><pre>\n" : " :\n");
			beginHTMLCheck = b.length ();
			boolean singleLine = l0 == l1;
			if (singleLine)
			{
				append (b, input.getTextForLine (l0), firstLine + l0, tabWidth);
			}
			b.append (INDENT);
			for (int i = c0; i > 0; i--)
			{
				b.append (' ');
			}
			if (singleLine)
			{
				b.append ('^');
				int n = c1 - c0 - 1;
				if (n > 0)
				{
					while (--n > 0)
					{
						b.append ('-');
					}
					b.append ('^');
				}
				b.append ('\n');
			}
			else
			{
				b.append ('<');
				for (int i = input.getLineLength (l0, tabWidth) - c0; i > 1; i--)
				{
					b.append ('-');
				}
				b.append ('\n');
				append (b, input.getTextForLine (l0), firstLine + l0, tabWidth);
				if (l0 < l1 - 1)
				{
					b.append ("   . . .\n");
				}
				append (b, input.getTextForLine (l1), firstLine + l1, tabWidth);
				b.append (INDENT);
				for (int i = c1; i > 1; i--)
				{
					b.append ('-');
				}
				b.append (">\n");
			}
			if (html)
			{
				b.append ((char) -1);
			}
		}
		b.append ("*** ").append (getCategoryDescription ())
			.append (": ").append (getMessage ()).append ('\n');
		if (html)
		{
			Utils.escapeForXML (b, beginHTMLCheck);
			for (int i = b.length () - 1; i >= beginHTMLCheck; i--)
			{
				if (b.charAt (i) == (char) -1)
				{
					b.replace (i, i + 1, "</pre>\n");
				}
			}
			b.append ("<br>\n");
		}
		return b.toString ();
	}

	
	public void dispose ()
	{
		input = null;
	}


	public static RecognitionException convert (antlr.ANTLRException e,
												String[] tokenNames)
		throws java.io.IOException
	{	
		if (e instanceof antlr.TokenStreamRecognitionException)
		{
			e = ((antlr.TokenStreamRecognitionException) e).recog;
		}
		if (e instanceof RecognitionException)
		{
			return (RecognitionException) e;
		}
		else if (e instanceof antlr.MismatchedTokenException)
		{
			antlr.MismatchedTokenException f
				= (antlr.MismatchedTokenException) e;
			RecognitionException r = new SyntacticException
				((f.mismatchType == antlr.MismatchedTokenException.TOKEN)
				 ? Tokenizer.I18N.msg ("grammar.unexpected-token1",
									   tokenNames[f.expecting])
				 : Tokenizer.I18N.msg ("grammar.unexpected-token0"));
			if (f.token != null)
			{
				r.set (f.token);
			}
			else
			{
				r.set1 (f.node);
			}
			return r;
		}
		else if (e instanceof antlr.NoViableAltException)
		{
			antlr.NoViableAltException f = (antlr.NoViableAltException) e;
			RecognitionException r = new SyntacticException
				(Tokenizer.I18N.msg ("grammar.unexpected-token0"));
			if (f.token != null)
			{
				r.set (f.token);
			}
			else
			{
				r.set1 (f.node);
			}
			return r;
		}
		else if (e instanceof antlr.SemanticException)
		{
			return new SemanticException (e.getMessage ())
				.set ((antlr.SemanticException) e);
		}
		else if (e instanceof antlr.RecognitionException)
		{
			return new RecognitionException (e.getMessage ())
				.set ((antlr.RecognitionException) e);
		}
		else if (e instanceof antlr.TokenStreamIOException)
		{
			throw ((antlr.TokenStreamIOException) e).io;
		}
		else
		{
			return new RecognitionException (e.getMessage ());
		}
	}


	public int compareTo (RecognitionException e)
	{
		if (getFilename () == null)
		{
			return -1;
		}
		if (e.getFilename () == null)
		{
			return 1;
		}
		int i;
		if ((i = getFilename ().compareTo (e.getFilename ())) != 0)
		{
			return i;
		}
		if (isError != e.isError)
		{
			return isError ? -1 : 1;
		}
		if (start < 0)
		{
			return -1;
		}
		if (e.start < 0)
		{
			return 1;
		}
		i = start - e.start;
		if (i != 0)
		{
			return i;
		}
		return stamp - e.stamp;
	}

}
