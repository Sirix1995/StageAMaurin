
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
import de.grogra.xl.util.IntHashMap;

import java.io.*;

public final class JavaTokenizer extends Tokenizer implements XLTokenTypes
{
	private final IntHashMap nonJava;


	public JavaTokenizer (Reader in, String inName)
	{
		this ();
		setSource (new BufferedReader (in), inName);
	}


	private static JavaTokenizer INSTANCE = new JavaTokenizer ();

	public JavaTokenizer ()
	{
		super (CREATE_TOKEN_LOCATOR | ENABLE_TYPE_SUFFIX | UNICODE_ESCAPES);
		if (INSTANCE != null)
		{
			nonJava = INSTANCE.nonJava;
			copyTokenTablesFrom (INSTANCE);
			return;
		}
		nonJava = new IntHashMap ();
		for (int i = 0; i < XLParser.NON_JAVA_TOKENS.length; i++)
		{
			nonJava.put (XLParser.NON_JAVA_TOKENS[i], this);
		}
		addToken (new StartEndCommentStart ("/*", "*/"));
		addToken (new SingleLineCommentStart ());
		addToken (new StringStart ());
		addToken (new CharStart ());

		addToken (new BooleanLiteral (false), "false");
		addToken (new BooleanLiteral (true), "true");
/*!!
#set ($INSTANCE = "x")
#parse ("de/grogra/xl/parser/XLTokenizer.inc")
!!*/
//!! #* Start of generated code
	xaddToken (PACKAGE, "package");
	xaddToken (MODULE, "module");
	xaddToken (SCALE, "scale");
	xaddToken (CLASS, "class");
	xaddToken (INTERFACE, "interface");
	xaddToken (SINGLE_TYPE_IMPORT, "import");
	xaddToken (EXTENDS, "extends");
	xaddToken (IMPLEMENTS, "implements");
	xaddToken (SUPER, "super");
	xaddToken (THROWS, "throws");
	xaddToken (SEMI, ";");
	xaddToken (ASSIGN, "=");
	xaddToken (VOID_, "void");
	xaddToken (BOOLEAN_, "boolean");
	xaddToken (BYTE_, "byte");
	xaddToken (SHORT_, "short");
	xaddToken (CHAR_, "char");
	xaddToken (INT_, "int");
	xaddToken (LONG_, "long");
	xaddToken (FLOAT_, "float");
	xaddToken (DOUBLE_, "double");
	xaddToken (LT, "<");
	xaddToken (GT, ">");
	xaddToken (LINE, "---");
	xaddToken (LEFT_RIGHT_ARROW, "<->");
	xaddToken (PLUS_LEFT_ARROW, "<+");
	xaddToken (PLUS_ARROW, "+>");
	xaddToken (PLUS_LINE, "-+-");
	xaddToken (PLUS_LEFT_RIGHT_ARROW, "<+>");
	xaddToken (SLASH_LEFT_ARROW, "</");
	xaddToken (SLASH_ARROW, "/>");
	xaddToken (SLASH_LINE, "-/-");
	xaddToken (SLASH_LEFT_RIGHT_ARROW, "</>");
	xaddToken (CONTEXT, "(*");
	xaddToken (DOT, ".");
	xaddToken (SUB, "-");
	xaddToken (LEFT_ARROW, "<-");
	xaddToken (ARROW, "->");
	xaddToken (QUESTION, "?");
	xaddToken (MUL, "*");
	xaddToken (ADD, "+");
	xaddToken (RULE, "==>");
	xaddToken (DOUBLE_ARROW_RULE, "==>>");
	xaddToken (EXEC_RULE, "::>");
	xaddToken (COM, "~");
	xaddToken (NOT, "!");
	xaddToken (DIV, "/");
	xaddToken (REM, "%");
	xaddToken (POW, "**");
	xaddToken (SHL, "<<");
	xaddToken (SHR, ">>");
	xaddToken (USHR, ">>>");
	xaddToken (LE, "<=");
	xaddToken (GE, ">=");
	xaddToken (CMP, "<=>");
	xaddToken (NOT_EQUALS, "!=");
	xaddToken (EQUALS, "==");
	xaddToken (OR, "|");
	xaddToken (XOR, "^");
	xaddToken (AND, "&");
	xaddToken (COR, "||");
	xaddToken (CAND, "&&");
	xaddToken (THIS, "this");
	xaddToken (IF, "if");
	xaddToken (RETURN, "return");
	xaddToken (YIELD, "yield");
	xaddToken (THROW, "throw");
	xaddToken (SYNCHRONIZED_, "synchronized");
	xaddToken (ASSERT, "assert");
	xaddToken (BREAK, "break");
	xaddToken (CONTINUE, "continue");
	xaddToken (TRY, "try");
	xaddToken (CATCH, "catch");
	xaddToken (FINALLY, "finally");
	xaddToken (LCLIQUE, "{#");
	xaddToken (RCLIQUE, "#}");
	xaddToken (FOR, "for");
	xaddToken (WHILE, "while");
	xaddToken (DO, "do");
	xaddToken (SWITCH, "switch");
	xaddToken (CASE, "case");
	xaddToken (DEFAULT, "default");
	xaddToken (NULL_LITERAL, "null");
	xaddToken (LONG_LEFT_ARROW, "<--");
	xaddToken (LONG_ARROW, "-->");
	xaddToken (LONG_LEFT_RIGHT_ARROW, "<-->");
	xaddToken (INSTANCEOF, "instanceof");
	xaddToken (QUOTE, "`");
	xaddToken (ADD_ASSIGN, "+=");
	xaddToken (SUB_ASSIGN, "-=");
	xaddToken (MUL_ASSIGN, "*=");
	xaddToken (DIV_ASSIGN, "/=");
	xaddToken (REM_ASSIGN, "%=");
	xaddToken (POW_ASSIGN, "**=");
	xaddToken (SHR_ASSIGN, ">>=");
	xaddToken (USHR_ASSIGN, ">>>=");
	xaddToken (SHL_ASSIGN, "<<=");
	xaddToken (AND_ASSIGN, "&=");
	xaddToken (XOR_ASSIGN, "^=");
	xaddToken (OR_ASSIGN, "|=");
	xaddToken (DEFERRED_ASSIGN, ":=");
	xaddToken (DEFERRED_RATE_ASSIGN, ":'=");
	xaddToken (DEFERRED_ADD, ":+=");
	xaddToken (DEFERRED_SUB, ":-=");
	xaddToken (DEFERRED_MUL, ":*=");
	xaddToken (DEFERRED_DIV, ":/=");
	xaddToken (DEFERRED_REM, ":%=");
	xaddToken (DEFERRED_POW, ":**=");
	xaddToken (DEFERRED_OR, ":|=");
	xaddToken (DEFERRED_AND, ":&=");
	xaddToken (DEFERRED_XOR, ":^=");
	xaddToken (DEFERRED_SHL, ":<<=");
	xaddToken (DEFERRED_SHR, ":>>=");
	xaddToken (DEFERRED_USHR, ":>>>=");
	xaddToken (INC, "++");
	xaddToken (DEC, "--");
	xaddToken (IN, "in");
	xaddToken (GUARD, "::");
	xaddToken (NEW, "new");
	xaddToken (ANNOTATION, "@");
	xaddToken (PRIVATE_, "private");
	xaddToken (PUBLIC_, "public");
	xaddToken (PROTECTED_, "protected");
	xaddToken (STATIC_, "static");
	xaddToken (TRANSIENT_, "transient");
	xaddToken (FINAL_, "final");
	xaddToken (ABSTRACT_, "abstract");
	xaddToken (NATIVE_, "native");
	xaddToken (VOLATILE_, "volatile");
	xaddToken (STRICT_, "strictfp");
	xaddToken (CONST_, "const");
	xaddToken (VARARGS_, "...");
	xaddToken (ELSE, "else");
	xaddToken (LPAREN, "(");
	xaddToken (RPAREN, ")");
	xaddToken (LBRACK, "[");
	xaddToken (RBRACK, "]");
	xaddToken (LCURLY, "{");
	xaddToken (RCURLY, "}");
	xaddToken (COLON, ":");
	xaddToken (COMMA, ",");
	xaddToken (LAMBDA, "=>");
	xaddToken (RCONTEXT, "*)");
//!! *# End of generated code
	}

	
	private void xaddToken (int type, String text)
	{
		if (nonJava.get (type, null) == null)
		{
			addToken (type, text);
		}
	}

}
