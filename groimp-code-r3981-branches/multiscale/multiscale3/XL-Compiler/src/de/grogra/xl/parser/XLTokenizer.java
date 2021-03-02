
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
import java.io.*;

public final class XLTokenizer extends Tokenizer implements XLTokenTypes
{

	private static XLTokenizer INSTANCE = new XLTokenizer ();


	public XLTokenizer (Reader in, String inName)
	{
		this ();
		setSource (new BufferedReader (in), inName);
	}



	public XLTokenizer ()
	{
		super (CREATE_TOKEN_LOCATOR | ENABLE_TYPE_SUFFIX | UNICODE_ESCAPES);
		if (INSTANCE != null)
		{
			copyTokenTablesFrom (INSTANCE);
			return;
		}
		addToken (new StartEndCommentStart ("/*", "*/"));
		addToken (new SingleLineCommentStart ());
		addToken (new StringStart ());
		addToken (new CharStart ());

		addToken (new BooleanLiteral (false), "false");
		addToken (new BooleanLiteral (true), "true");
/*!!
#parse ("de/grogra/xl/parser/XLTokenizer.inc")
!!*/
//!! #* Start of generated code
	addToken (PACKAGE, "package");
	addToken (MODULE, "module");
	addToken (SCALE, "scale");
	addToken (CLASS, "class");
	addToken (INTERFACE, "interface");
	addToken (SINGLE_TYPE_IMPORT, "import");
	addToken (EXTENDS, "extends");
	addToken (IMPLEMENTS, "implements");
	addToken (SUPER, "super");
	addToken (THROWS, "throws");
	addToken (SEMI, ";");
	addToken (ASSIGN, "=");
	addToken (VOID_, "void");
	addToken (BOOLEAN_, "boolean");
	addToken (BYTE_, "byte");
	addToken (SHORT_, "short");
	addToken (CHAR_, "char");
	addToken (INT_, "int");
	addToken (LONG_, "long");
	addToken (FLOAT_, "float");
	addToken (DOUBLE_, "double");
	addToken (LT, "<");
	addToken (GT, ">");
	addToken (LINE, "---");
	addToken (LEFT_RIGHT_ARROW, "<->");
	addToken (PLUS_LEFT_ARROW, "<+");
	addToken (PLUS_ARROW, "+>");
	addToken (PLUS_LINE, "-+-");
	addToken (PLUS_LEFT_RIGHT_ARROW, "<+>");
	addToken (SLASH_LEFT_ARROW, "</");
	addToken (SLASH_ARROW, "/>");
	addToken (SLASH_LINE, "-/-");
	addToken (SLASH_LEFT_RIGHT_ARROW, "</>");
	addToken (CONTEXT, "(*");
	addToken (DOT, ".");
	addToken (SUB, "-");
	addToken (LEFT_ARROW, "<-");
	addToken (ARROW, "->");
	addToken (QUESTION, "?");
	addToken (MUL, "*");
	addToken (ADD, "+");
	addToken (RULE, "==>");
	addToken (DOUBLE_ARROW_RULE, "==>>");
	addToken (EXEC_RULE, "::>");
	addToken (COM, "~");
	addToken (NOT, "!");
	addToken (DIV, "/");
	addToken (REM, "%");
	addToken (POW, "**");
	addToken (SHL, "<<");
	addToken (SHR, ">>");
	addToken (USHR, ">>>");
	addToken (LE, "<=");
	addToken (GE, ">=");
	addToken (CMP, "<=>");
	addToken (NOT_EQUALS, "!=");
	addToken (EQUALS, "==");
	addToken (OR, "|");
	addToken (XOR, "^");
	addToken (AND, "&");
	addToken (COR, "||");
	addToken (CAND, "&&");
	addToken (THIS, "this");
	addToken (IF, "if");
	addToken (RETURN, "return");
	addToken (YIELD, "yield");
	addToken (THROW, "throw");
	addToken (SYNCHRONIZED_, "synchronized");
	addToken (ASSERT, "assert");
	addToken (BREAK, "break");
	addToken (CONTINUE, "continue");
	addToken (TRY, "try");
	addToken (CATCH, "catch");
	addToken (FINALLY, "finally");
	addToken (LCLIQUE, "{#");
	addToken (RCLIQUE, "#}");
	addToken (FOR, "for");
	addToken (WHILE, "while");
	addToken (DO, "do");
	addToken (SWITCH, "switch");
	addToken (CASE, "case");
	addToken (DEFAULT, "default");
	addToken (NULL_LITERAL, "null");
	addToken (LONG_LEFT_ARROW, "<--");
	addToken (LONG_ARROW, "-->");
	addToken (LONG_LEFT_RIGHT_ARROW, "<-->");
	addToken (INSTANCEOF, "instanceof");
	addToken (QUOTE, "`");
	addToken (ADD_ASSIGN, "+=");
	addToken (SUB_ASSIGN, "-=");
	addToken (MUL_ASSIGN, "*=");
	addToken (DIV_ASSIGN, "/=");
	addToken (REM_ASSIGN, "%=");
	addToken (POW_ASSIGN, "**=");
	addToken (SHR_ASSIGN, ">>=");
	addToken (USHR_ASSIGN, ">>>=");
	addToken (SHL_ASSIGN, "<<=");
	addToken (AND_ASSIGN, "&=");
	addToken (XOR_ASSIGN, "^=");
	addToken (OR_ASSIGN, "|=");
	addToken (DEFERRED_ASSIGN, ":=");
	addToken (DEFERRED_RATE_ASSIGN, ":'=");
	addToken (DEFERRED_ADD, ":+=");
	addToken (DEFERRED_SUB, ":-=");
	addToken (DEFERRED_MUL, ":*=");
	addToken (DEFERRED_DIV, ":/=");
	addToken (DEFERRED_REM, ":%=");
	addToken (DEFERRED_POW, ":**=");
	addToken (DEFERRED_OR, ":|=");
	addToken (DEFERRED_AND, ":&=");
	addToken (DEFERRED_XOR, ":^=");
	addToken (DEFERRED_SHL, ":<<=");
	addToken (DEFERRED_SHR, ":>>=");
	addToken (DEFERRED_USHR, ":>>>=");
	addToken (INC, "++");
	addToken (DEC, "--");
	addToken (IN, "in");
	addToken (GUARD, "::");
	addToken (NEW, "new");
	addToken (ANNOTATION, "@");
	addToken (PRIVATE_, "private");
	addToken (PUBLIC_, "public");
	addToken (PROTECTED_, "protected");
	addToken (STATIC_, "static");
	addToken (TRANSIENT_, "transient");
	addToken (FINAL_, "final");
	addToken (ABSTRACT_, "abstract");
	addToken (NATIVE_, "native");
	addToken (VOLATILE_, "volatile");
	addToken (STRICT_, "strictfp");
	addToken (CONST_, "const");
	addToken (VARARGS_, "...");
	addToken (ELSE, "else");
	addToken (LPAREN, "(");
	addToken (RPAREN, ")");
	addToken (LBRACK, "[");
	addToken (RBRACK, "]");
	addToken (LCURLY, "{");
	addToken (RCURLY, "}");
	addToken (COLON, ":");
	addToken (COMMA, ",");
	addToken (LAMBDA, "=>");
	addToken (RCONTEXT, "*)");
	addToken (OBSERVABLE, "java.util.Observable");
//!! *# End of generated code
	}


	@Override
	protected Token convert (Token t)
	{
		if (t.getType () == IDENT)
		{
			String s = t.getText ();
			if (s.charAt (0) == '$')
			{
				if (s.equals ("$yield"))
				{
					t.setText ("yield");
				}
				else if (s.equals ("$module"))
				{
					t.setText ("module");
				}
				else if (s.equals ("$in"))
				{
					t.setText ("in");
				}
			}
		}
		return t;
	}


	public static void main (String[] args) throws Exception
	{
		File f = new File (args[0]);
		XLTokenizer t;
		
		t = new XLTokenizer ();
		t.setSource (new BufferedReader (new FileReader (f)), f.getName ());
		while (t.getToken ().getType () != Token.EOF_TYPE)
		{
		}

		t = new XLTokenizer ();
		t.setSource (new BufferedReader (new FileReader (f)), f.getName ());
		long time = System.currentTimeMillis ();
		int count = 0;
		while (t.nextToken ().getType () != Token.EOF_TYPE)
		{
			count++;
		}
		time = System.currentTimeMillis () - time;
		System.err.println (time + " ms, " + (f.length () / (float) time )
							+ " byte/ms");
		System.err.println (count + " tokens");
	}

}
