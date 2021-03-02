header
{

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
import de.grogra.xl.compiler.ProblemReporter;
import de.grogra.xl.compiler.CompilerBase;
import de.grogra.util.Utils;
}


class XLParser extends Parser;


options
{
	k = 2;
	importVocab = Compiler;
	exportVocab = XL;
	buildAST = true;
	defaultErrorHandler = false;
	classHeaderPrefix = "/*";
	classHeaderSuffix = "*/ public class XLParser extends de.grogra.xl.parser.Parser implements XLTokenTypes";
}


tokens
{
	PACKAGE = "package";
	SINGLE_TYPE_IMPORT = "import";

	CLASS = "class";
	INTERFACE = "interface";
	EXTENDS = "extends";
	IMPLEMENTS = "implements";
	THROWS = "throws";

	VOID_ = "void";
	BOOLEAN_ = "boolean";
	BYTE_ = "byte";
	SHORT_ = "short";
	CHAR_ = "char";
	INT_ = "int";
	LONG_ = "long";
	FLOAT_ = "float";
	DOUBLE_ = "double";

	PUBLIC_ = "public";
	PROTECTED_ = "protected";
	PRIVATE_ = "private";
	STATIC_ = "static";
	TRANSIENT_ = "transient";
	FINAL_ = "final";
	ABSTRACT_ = "abstract";
	NATIVE_ = "native";
	SYNCHRONIZED_ = "synchronized";
	VOLATILE_ = "volatile";
	STRICT_ = "strictfp";
	CONST_ = "const";
	VARARGS_ = "...";

	IF = "if";
	ELSE = "else";

	FOR = "for";
	DO = "do";
	WHILE = "while";

	BREAK = "break";
	CONTINUE = "continue";
	RETURN = "return";

	TRY = "try";
	CATCH = "catch";
	FINALLY = "finally";

	SWITCH = "switch";
	CASE = "case";
	DEFAULT = "default";

	THROW = "throw";
	ASSERT = "assert";

	NULL_LITERAL = "null";
	SUPER = "super";
	THIS = "this";
	INSTANCEOF = "instanceof";
	NEW = "new";

	MODULE = "module";
	YIELD = "yield";
	IN = "in";

	QUESTION = "?";
	LPAREN = "(";
	RPAREN = ")";
	LBRACK = "[";
	RBRACK = "]";
	LCURLY = "{";
	RCURLY = "}";
	COLON = ":";
	COMMA = ",";
	DOT = ".";
	ASSIGN = "=";
	EQUALS = "==";
	NOT = "!";
	COM = "~";
	NOT_EQUALS = "!=";
	DIV = "/";
	DIV_ASSIGN = "/=";
	ADD = "+";
	ADD_ASSIGN = "+=";
	INC = "++";
	SUB = "-";
	SUB_ASSIGN = "-=";
	DEC = "--";
	MUL = "*";
	MUL_ASSIGN = "*=";
	POW = "**";
	POW_ASSIGN = "**=";
	REM = "%";
	REM_ASSIGN = "%=";
	SHL = "<<";
	SHL_ASSIGN = "<<=";
	SHR = ">>";
	SHR_ASSIGN = ">>=";
	USHR = ">>>";
	USHR_ASSIGN = ">>>=";
	GE = ">=";
	GT = ">";
	LE = "<=";
	LT = "<";
	CMP = "<=>";
	XOR = "^";
	XOR_ASSIGN = "^=";
	OR = "|";
	OR_ASSIGN = "|=";
	AND = "&";
	AND_ASSIGN = "&=";
	COR = "||";
	CAND = "&&";
	GUARD = "::";
	LAMBDA = "=>";

	DEFERRED_ASSIGN = ":=";
	DEFERRED_RATE_ASSIGN = ":'=";
	DEFERRED_POW = ":**=";
	DEFERRED_MUL = ":*=";
	DEFERRED_DIV = ":/=";
	DEFERRED_REM = ":%=";
	DEFERRED_ADD = ":+=";
	DEFERRED_SUB = ":-=";
	DEFERRED_SHL = ":<<=";
	DEFERRED_SHR = ":>>=";
	DEFERRED_USHR = ":>>>=";
	DEFERRED_AND = ":&=";
	DEFERRED_XOR = ":^=";
    DEFERRED_OR = ":|=";

	SEMI = ";";
	ANNOTATION = "@";

	LEFT_ARROW = "<-";
	ARROW = "->";

	LONG_LEFT_ARROW = "<--";
	LONG_ARROW = "-->";
	LONG_LEFT_RIGHT_ARROW = "<-->";

	PLUS_LEFT_ARROW = "<+";
	PLUS_ARROW = "+>";

	SLASH_LEFT_ARROW = "</";
	SLASH_ARROW = "/>";

	LINE = "---";
	PLUS_LINE = "-+-";
	SLASH_LINE = "-/-";

	LEFT_RIGHT_ARROW = "<->";
	PLUS_LEFT_RIGHT_ARROW = "<+>";
	SLASH_LEFT_RIGHT_ARROW = "</>";

	QUOTE = "`";

	CONTEXT = "(*";
	RCONTEXT = "*)";

	RULE = "==>";
	DOUBLE_ARROW_RULE = "==>>";
	EXEC_RULE = "::>";
}


{
	public static final int[] NON_JAVA_TOKENS =
		{MODULE, YIELD, IN, POW, POW_ASSIGN, GUARD, LAMBDA, DEFERRED_ASSIGN, DEFERRED_RATE_ASSIGN,
		 DEFERRED_POW, DEFERRED_MUL, DEFERRED_DIV, DEFERRED_REM, DEFERRED_ADD,
		 DEFERRED_SUB, DEFERRED_SHL, DEFERRED_SHR, DEFERRED_USHR, DEFERRED_AND,
		 DEFERRED_XOR, DEFERRED_OR,
		 LEFT_ARROW, ARROW, PLUS_LEFT_ARROW, PLUS_ARROW, LONG_LEFT_ARROW, LONG_ARROW, CMP,
		 QUOTE, CONTEXT, RCONTEXT, LONG_LEFT_RIGHT_ARROW, SLASH_ARROW, SLASH_LEFT_ARROW,
		 LINE, PLUS_LINE, SLASH_LINE, LEFT_RIGHT_ARROW, PLUS_LEFT_RIGHT_ARROW, SLASH_LEFT_RIGHT_ARROW,
		 RULE, DOUBLE_ARROW_RULE, EXEC_RULE};

	static final de.grogra.util.I18NBundle I18N
		= de.grogra.xl.compiler.Compiler.I18N;

	static final String X_ID = "_._func";

	public XLParser (java.io.Reader in, String inName)
	{
		this (new XLTokenizer (in, inName));
	}


	private AST modsAST, nameAST, extAST, implAST;
	private AST shellPackage;
	private String shell;

	@Override
	protected AST parseGoalSymbol ()
		throws RecognitionException, TokenStreamException
	{
		return (shell != null) ? shellStatements ()
			: (nameAST == null) ? compilationUnit ()
			: simpleCompilationUnit (modsAST, nameAST, extAST, implAST);
	}


	public void setSimple (AST mods, AST name, AST ext, AST impl)
	{
		modsAST = mods;
		nameAST = name;
		extAST = ext;
		implAST = impl;
	}
	
	
	public void setShell (AST packageName, String className)
	{
		if (packageName != null)
		{
			shellPackage = #([PACKAGE], packageName);
		}
		shell = className;
	}
	
	
	static boolean hasType (Token t, int type)
	{
		return (t != null) && (t.getType () == type);
	}


	static boolean isBuiltIn (int type)
	{
		switch (type)
		{
			case VOID_:
			case BOOLEAN_:
			case BYTE_:
			case SHORT_:
			case CHAR_:
			case INT_:
			case LONG_:
			case FLOAT_:
			case DOUBLE_:
				return true;
			default:
				return false;
		}
	}

}


compilationUnit returns [AST unit]
	{
		unit = null;
	}
	:   (packageDeclaration)?
		(importDeclaration)*
		(	m:modifiers!
			(	classDeclaration[#m]
			|	interfaceDeclaration[#m]
			|   moduleDeclaration[#m]
			)
		|	SEMI!
		)*
		EOF^
		{
			#compilationUnit.setType (COMPILATION_UNIT);
			unit = #compilationUnit;
		}
	;


simpleCompilationUnit[AST mods, AST name, AST ext, AST impl] returns [AST unit]
	{
		unit = null;
	}
	:	(importDeclaration)*
		simpleCompilationUnitMembers[mods, name, ext, impl]
		EOF^
		{
			#simpleCompilationUnit.setType (COMPILATION_UNIT);
			unit = #simpleCompilationUnit;
		}
	;


simpleCompilationUnitMembers[AST mods, AST name, AST ext, AST impl]
	{
		AST instance = #([VARIABLE_DEF],
			#([MODIFIERS], [PUBLIC_], [STATIC_]),
			[DECLARING_TYPE],
			[IDENT, "INSTANCE"]);
		instance.setNextSibling (#([INSTANCE_INIT],
			#([SLIST], #([ASSIGN], [IDENT, "INSTANCE"], [THIS]))));
	}
	:	insert[mods] insert[name] insert[ext] insert[impl]
		insert[instance]
		(typeMember[false, false])*
		{
			#simpleCompilationUnitMembers = #([CLASS], simpleCompilationUnitMembers);
		}
	;


shellStatements returns [AST list]
	{
		list = #([COMPILATION_UNIT], shellPackage);
	}
	:	(	(modifier | typeSpec IDENT) =>
			mods:modifiers! t:typeSpec! variableDeclarators[#mods, #t, true] SEMI!
		|!	i:importDeclaration {list.addChild (#i);}
		|	assignmentExpression SEMI!
		|	statementNoExprNoLabel
		)*
		eof:EOF^
	{
		#eof.setType (SHELL_BLOCK);
		list.addChild (#([CLASS], #([MODIFIERS], [PUBLIC_], [ABSTRACT_]), [IDENT, shell],
						 #([METHOD], #([MODIFIERS], [PUBLIC_], [STATIC_]), [VOID_],
						   [IDENT, "execute"], [PARAMETERS], [THROWS],
						   #([SLIST], eof))));
	}
	;


packageDeclaration
	:	PACKAGE^ name SEMI!
	;


importDeclaration
	:	i:SINGLE_TYPE_IMPORT^ (st:STATIC_!)? id:name (DOT! iod:MUL!)? semi:SEMI!
	{
		if (st != null)
		{
			if (iod != null)
			{
				#i.setType (STATIC_IMPORT_ON_DEMAND);
			}
			else
			{
				if (#id.getType () != DOT)
				{
					throw new MismatchedTokenException (tokenNames, #semi, DOT, false);
				}
				#i.setFirstChild (#id.getFirstChild ());
				#i.setType (SINGLE_STATIC_IMPORT);
			}
		}
		else if (iod != null)
		{
			#i.setType (IMPORT_ON_DEMAND);
		}
	}
	;


moduleDeclaration[AST mods]
	:	insert[mods]
		MODULE^ id:IDENT params:moduleParameterDeclarationList[id]!
		(	classExtendsClause
			(!	lp:LPAREN args:argList[false] RPAREN
				{
					((ASTWithToken) #args).initialize (lp);
					#args.setType (ARGLIST);
				}
			)?
			(	b:moduleCtorBlock!
			)?
		)?
		(implementsClause)?
		insert[#params]
		insert[#args]
		insert[#b]
		(	LCURLY!
			(typeMember[false, false])*
			RCURLY!
			(moduleInst)?
		|	moduleInst
		|	SEMI!
		)
	;


moduleCtorBlock
	:	DOT! lp:LPAREN^ (expression)? RPAREN! setType[#lp, SLIST]
	;


moduleInst
	:	sp:RULE^ setType[#sp, SLIST] productionStatements s:SEMI^ setType[#s, INSTANTIATOR]
	;


classDeclaration[AST mods]
	:	insert[mods]
		CLASS^ IDENT
		(	(classExtendsClause)? (implementsClause)?
			LCURLY! (typeMember[false, false])* RCURLY!
		|	formalParameterList[false]
			LPAREN! compoundPattern RPAREN!
		)
	;


classExtendsClause
	:	EXTENDS^ name
	;


implementsClause
	:	IMPLEMENTS^ name (COMMA! name)*
	;


interfaceDeclaration[AST mods]
	:	insert[mods]
		INTERFACE^ IDENT (interfaceExtendsClause)?
		LCURLY! (typeMember[true, false])* RCURLY!
	;


interfaceExtendsClause
	:	EXTENDS^ name (COMMA! name)*
	;


overloadableOperator
	:	NOT | COM | COR | CAND |
		ADD | SUB | MUL | DIV | REM | POW |
		ADD_ASSIGN | SUB_ASSIGN | MUL_ASSIGN | DIV_ASSIGN | REM_ASSIGN | POW_ASSIGN |
		SHL | SHR | USHR |
		SHL_ASSIGN | SHR_ASSIGN | USHR_ASSIGN |
		XOR | OR | AND |
		XOR_ASSIGN | OR_ASSIGN | AND_ASSIGN |
		INC | DEC |
		EQUALS | NOT_EQUALS | GE | GT | LE | LT | CMP |
		c:COLON setType[#c, RANGE] | IN | GUARD |
		PLUS_LEFT_ARROW | PLUS_ARROW | PLUS_LINE | PLUS_LEFT_RIGHT_ARROW |
		SLASH_LEFT_ARROW | SLASH_ARROW | SLASH_LINE | SLASH_LEFT_RIGHT_ARROW |
		LINE | LEFT_RIGHT_ARROW |
		LONG_LEFT_ARROW | LONG_ARROW | LONG_LEFT_RIGHT_ARROW |
		l:LBRACK setType[#l, INDEX_OP] RBRACK! |
		a:LEFT_ARROW setType[#a, X_LEFT_RIGHT_ARROW] ARROW! |
		QUOTE QUOTE! |
		DEFERRED_ASSIGN | DEFERRED_RATE_ASSIGN | DEFERRED_POW | DEFERRED_MUL | DEFERRED_DIV |
		DEFERRED_REM | DEFERRED_ADD | DEFERRED_SUB | DEFERRED_SHL |
		DEFERRED_SHR | DEFERRED_USHR | DEFERRED_AND | DEFERRED_XOR |
		DEFERRED_OR
	;


methodIdent returns [int symbol]
	{
		symbol = 0;
	}
	:	id:IDENT
		(!	{#id.getText ().equals ("operator")}?
			(LPAREN RPAREN LPAREN) =>
			LPAREN RPAREN
			{
				symbol = INVOKE_OP;
			}
		|!	{#id.getText ().equals ("operator")}?
			o:overloadableOperator
			{
				symbol = #o.getType ();
			}
		|
		)
	;


typeMember[boolean iface, boolean anonymous]
	{
		int overload = 0;
		AST varArgs;
	}
	:	{!iface}? block {#typeMember = #([INSTANCE_INIT], typeMember);} 
	|	{!iface}? si:STATIC_^ block setType[#si, STATIC_INIT]
	|	mods:modifiers!
		(	t:typeSpec!
			(	((MUL)? methodIdent LPAREN) =>
				{ #typeMember = #([METHOD], mods, t); }
				(st:MUL! {#st.setType (ITERATING_); #mods.addChild (#st);})?
				overload=id:methodIdent
				varArgs=params:formalParameterList[true]
				{
					#mods.addChild (varArgs);
					if (overload > 0)
					{
						CompilerBase.checkOperatorFunction
							(#params, #mods, #id, overload, exceptionList);
					}
				}
				throwsList (rulesInCurrentGraph | block | SEMI)
			|!	v:variableDeclarators[#mods, #t, true] SEMI
				{ #typeMember = #v; }
			)
		|   { #typeMember = #([METHOD], mods); }
			VOID_
			(st2:MUL! {#st2.setType (ITERATING_); #mods.addChild (#st2);})?
			overload=id2:methodIdent
			varArgs=params2:formalParameterList[true]
			{
				#mods.addChild (varArgs);
				if (overload > 0)
				{
					CompilerBase.checkOperatorFunction
						(#params2, #mods, #id2, overload, exceptionList);
				}
			}
			throwsList
			(	rulesInCurrentGraph
			|	block
			|	SEMI
			)
		|   {!(iface || anonymous)}?
			{ #typeMember = #([CONSTRUCTOR], mods); }
			IDENT varArgs=formalParameterList[true] throwsList constructorBody
			{
				#mods.addChild (varArgs);
			}
		|	classDeclaration[#mods]
		|	interfaceDeclaration[#mods]
		|	moduleDeclaration[#mods]
		)
	|	SEMI!
	;


formalParameterList[boolean allowVarArgs] returns[AST varArgs]
	{
		varArgs = null;
	}
	:	l:LPAREN^ (varArgs=parameterDeclaration[allowVarArgs]
				   ({varArgs == null}? COMMA! varArgs=parameterDeclaration[allowVarArgs])*)?
		RPAREN!
		setType[#l, PARAMETERS]
	;


parameterDeclaration![boolean allowVarArgs] returns[AST varArg]
	{
		varArg = null;
	}
	:	m:modifiers t:typeSpec ({allowVarArgs}? v:VARARGS_)? id:IDENT
		b:declaratorBrackets[#t]
		{
			varArg = #v;
			if (varArg != null)
			{
				#b = #([ARRAY_DECLARATOR], b);
			}
			#parameterDeclaration = #([PARAMETER_DEF], m, b, id);
		}
	;


throwsList
	:	THROWS^ name (COMMA! name)*
	|	{#throwsList = #([THROWS]);}
	;


moduleParameterDeclarationList[Token id]
	:	l:LPAREN^ (moduleParameterDeclaration
				   (COMMA! moduleParameterDeclaration)*)? RPAREN!
		setType[#l, PARAMETERS]
	|   {
			#moduleParameterDeclarationList = #[id];
			#moduleParameterDeclarationList.setType (PARAMETERS);
		}
	;


moduleParameterDeclaration
	:!	m:modifiers t:typeSpec id:IDENT
		b:declaratorBrackets[#t]
		(RETURN method:IDENT LPAREN RPAREN)?
		{ #moduleParameterDeclaration = #([PARAMETER_DEF], m, b, id, method); }
	|   SUPER d:DOT^ IDENT setType[#d, PARAMETER_DEF]
	;


declaratorBrackets[AST type]
	:	insert[type] (lb:LBRACK^ setArrayDeclarator_RBRACK[#lb])*
	;


rulesInCurrentGraph
	{
		ASTWithToken e = new ASTWithToken (EMPTY, "");
		e.token = LT (1);
	}
	:	r:rules[e]
	;

rules[AST graph]
	:   lb:LBRACK^ setType[#lb, RULE_BLOCK]
		insert[graph]
		(rule | script)*
		rb:RBRACK^ setType[#rb, SLIST]
	;


rule!
	:	(q:query)?
		{
			if (#q == null)
			{
				#q = #[EMPTY];
			}
		}
		(
			(r1:RULE | r2:DOUBLE_ARROW_RULE)
			p1:productionStatements SEMI
			{
				if (#r2 != null)
				{
					#r1 = #r2;
					#r1.setType (RULE);
					#r2 = #[DOUBLE_ARROW_RULE];
				}
				#rule = #(r1, r2, q, #([SLIST], p1));
			}
		|   r3:EXEC_RULE p2:statement
			{
				#r3.setType (RULE);
				#rule = #(r3, [EXEC_RULE], q, p2);
			}
		)
	;


lparenArgs!
	:   LPAREN argList[true] RPAREN
		(	(closureRange) => throwException closureRange
		|	~(QUESTION | MUL | ADD) .
		)
	;


lparenElist!
	:   LPAREN expression RPAREN
		(	(closureRange) => throwException closureRange
		|	~(QUESTION | MUL | ADD) .
		)
	;


query
	:	(	(LPAREN) => LPAREN! expression RPAREN!
		|	insert[#[EMPTY]]
		)
		compoundPattern
		{
			#query = #([QUERY], query);
		}
	;


compoundPattern
	:	qvDeclarations connectedPatternList
		{
			#compoundPattern = #([COMPOUND_PATTERN], compoundPattern);
		}
	;


qvDeclarations
	:   (varDeclaration[false] SEMI) => varDeclaration[false] SEMI! qvDeclarations
	|
	;


connectedPatternList
	:	connectedPattern (c:COMMA setType[#c, SEPARATE] connectedPattern)*
	;


connectedPattern
	:   (lparenElist) => lp:LPAREN^ expression RPAREN!
		setType[#lp, APPLICATION_CONDITION]
	|	(connectedPatternPart)+
	;
	

connectedPatternPart
	:	lb:LBRACK^ setType[#lb, TREE] connectedPatternList RBRACK!
	|	CONTEXT^ connectedPatternList RCONTEXT!
	|	labeledPrimaryPatternNoDot
		(block {#connectedPatternPart = #([PATTERN_WITH_BLOCK], connectedPatternPart);} )?
	;


labeledPrimaryPatternNoDot
	:	(IDENT COLON) => IDENT c:COLON^
		(	d:DOT! setType[#d, ANY] primaryNodePatternRest[#d]
		|	primaryPattern
		)
		setType[#c, LABEL]
	|	primaryPattern
	;


traversalModifier
	:	QUESTION
	|	MUL
	|	ADD
	|	closureRange
	;


primaryPattern
	:	primaryEdgePattern
	|	pnp:primaryNodePattern! primaryNodePatternRest[#pnp]
	;

primaryNodePatternRest[AST first]
	:	insert[first]
		(	AND^ intersectionPattern
			(AND! intersectionPattern)*
		)?
		(	f:OR^ IDENT setType[#f, FOLDING]
			(OR! IDENT)*
		)?
	;

intersectionPattern
	:	primaryNodePattern
	|	l:LPAREN^ expression RPAREN! setType[#l, APPLICATION_CONDITION]
	;


primaryNodePattern
	{
		boolean oa;
	}
	:	(builtInType) => t2:typeSpec! a2:typeArg!
		insertTree2[(#a2 != null) ? WRAPPED_TYPE_PATTERN : TYPE_PATTERN, #t2, #a2]
	|	u:unaryOpNode! insertTree[EXPR, #u]
	|	(name) => id:name!
		(   (lparenArgs) => l:LPAREN^ insert[#id] oa=argList[true] RPAREN!
			({!oa}? r:patternSelectorRest[#l]!)?
			{
				if (oa || (#r == null))
				{
					#l.setType (PARAMETERIZED_PATTERN);
				}
				else
				{
					#l.setType (METHOD_CALL);
					#primaryNodePattern = #([METHOD_PATTERN], r);
				}
			}
		|   (LBRACK RBRACK) => t1:typeSpecRest[#id]! a1:typeArg!
			insertTree2[(#a1 != null) ? WRAPPED_TYPE_PATTERN : TYPE_PATTERN, #t1, #a1]
		|   insertTree[NAME_PATTERN, #id]
		)
	|	n:primaryNoParen[true]!
		(	r2:patternSelectorRest[#n]! insertTree[METHOD_PATTERN, #r2]
		|	insertTree[EXPR, #n]
		)
	|	b:XOR setType[#b, ROOT]
	;


primaryEdgePattern
	:	cl:LPAREN^
		(	QUESTION! (co1:COLON!)? compoundPattern RPAREN!
			setType[#cl, (#co1 != null) ? SINGLE_OPTIONAL_MATCH : OPTIONAL_MATCH]
		|	COLON! (co2:QUESTION!)? compoundPattern RPAREN!
			setType[#cl, (#co2 != null) ? SINGLE_OPTIONAL_MATCH : SINGLE_MATCH]
		|	AND! compoundPattern RPAREN! setType[#cl, LATE_MATCH]
		|	compoundPattern RPAREN! setType[#cl, TRAVERSAL]
			traversalModifier
			(co:COLON^ setType[#co, MINIMAL] LPAREN! compoundPattern RPAREN!)?
		)
	|	LT
	|	GT
	|	LINE
	|	LEFT_RIGHT_ARROW
	|	PLUS_LEFT_ARROW
	|	PLUS_ARROW
	|	PLUS_LINE
	|	PLUS_LEFT_RIGHT_ARROW
	|	SLASH_LEFT_ARROW
	|	SLASH_ARROW
	|	SLASH_LINE
	|	SLASH_LEFT_RIGHT_ARROW
	|	a:DEC^ setType[#a, SUB] insert[#[ANY]]
	|	la:LONG_LEFT_ARROW^ setType[#la, LEFT_ARROW] insert[#[ANY]]
	|	ra:LONG_ARROW^ setType[#ra, ARROW] insert[#[ANY]]
	|	lra:LONG_LEFT_RIGHT_ARROW^ setType[#lra, X_LEFT_RIGHT_ARROW] insert[#[ANY]]
	|	l:LEFT_ARROW^ selectorExpression (ARROW! setType[#l, X_LEFT_RIGHT_ARROW] | SUB!)
	|	SUB! selectorExpression (ARROW^ | SUB^)
	;


patternSelectorRest[AST p]
	:	insert[p]
		(	DOT^ IDENT lp:LPAREN^ argList[false] RPAREN! setType[#lp, METHOD_CALL]
		)+
	;


typeArg
	:   (lparenArgs) => LPAREN! singleExpression RPAREN!
	|
	;


closureRange
	:	r:LCURLY^ singleExpression setType[#r, RANGE_EXACTLY]
		(COMMA! setType[#r, RANGE_MIN] (singleExpression setType[#r, RANGE])?)?
		RCURLY!
	;


productionStatements
	{
		AST prev = null;
	}
	:	(	s:productionStatement[prev]
			{
				if (#s != null)
				{
					prev = #s;
				}
			}
		)*
	;

productionStatementsAsList!
	:	g:productionStatements
		{
			#productionStatementsAsList = #([SLIST], g);
		}
	;

productionBlock
	:   lp:LPAREN^ productionStatements RPAREN! setType[#lp, SLIST]
	;


productionStatement[AST prev]
	:	script
	|	id:IDENT lbl:COLON^
		(	(controlStatement[true] | productionBlock)
			setType[#lbl, LABELED_STATEMENT]
		|	n:node[#[EMPTY], #id, prev]! {#productionStatement = #n;}
		)
	|	controlStatement[true]
	|	THROW^ primaryExpressionNode
	|	c:COMMA setType[#c, SEPARATE]
	|	lb:LBRACK^ productionStatements RBRACK! setType[#lb, TREE]
	|   BREAK^ (options {warnWhenFollowAmbig = false;}: IDENT)?
	|   CONTINUE^ (options {warnWhenFollowAmbig = false;}: IDENT)?
	|	node[#[EMPTY], null, prev]
	|	e:produceEdgeOp! edgeNode[#e, prev]
	;


produceEdgeOp
	:	PLUS_LEFT_ARROW
	|	PLUS_ARROW
	|	PLUS_LEFT_RIGHT_ARROW
	|	PLUS_LINE
	|	SLASH_LEFT_ARROW
	|	SLASH_ARROW
	|	SLASH_LEFT_RIGHT_ARROW
	|	SLASH_LINE
	|	LEFT_RIGHT_ARROW
	|	LINE
	|	LONG_LEFT_ARROW
	|	LONG_LEFT_RIGHT_ARROW
	|	LONG_ARROW
	|	LT
	|	GT
	|	LE
	|	GE
	|	SHL
	|	SHR
	|	USHR
	|	CMP
	|	ADD
	|	MUL
	|	DIV
	|	REM
	|	POW
	|	OR
	|	COR
	|	AND
	|	CAND
	|	IN
	|	GUARD
	|	l:LEFT_ARROW^ selectorExpression (ARROW! setType[#l, X_LEFT_RIGHT_ARROW] | SUB!)
	|	SUB! selectorExpression (ARROW^ | SUB^)
	|	INC
	|	DEC
	;


edgeNode[AST edge, AST prev]
	:	node[edge, null, prev]
	|	id:IDENT! COLON! node[edge, #id, prev]
	;


node[AST edge, AST id, AST prev]
	:
	(	primaryExpressionNode
	|	b:XOR setType[#b, ROOT]
	|	u:unaryOpNode! insertTree[UNARY_PREFIX, #u]
	)
	{
		AST n = #(#[NODE], node, id, edge);
		if ((prev != null) && (prev.getType () == NODES))
		{
			prev.addChild (n);
			#node = null;
		}
		else
		{
			#node = #([NODES], n);
		}
	}
	;


unaryOpNode
	:	(COM^ | NOT^) primaryNoCreator
	;


primaryExpressionNode
	:	primaryNoParen[false]
		(	DOT^ IDENT (lp:LPAREN^ argList[false] RPAREN!
						setType[#lp, METHOD_CALL] )?
		|	w:DOT^ withInstanceRest[#w]
		)*
	;

withInstanceRest[AST w]
	{
		w.setType (EXPR);
	}
	:	LPAREN! (expression setType[w, WITH])? RPAREN!
	;


primaryExpressionNodeParen
	:	primaryExpressionNode
	|   lp:LPAREN^ expression RPAREN! setType[#lp, EXPR]
	;


script
	:   LCURLY! (blockStatement)* RCURLY!
	;


block
	:   lc:LCURLY^ (blockStatement)* RCURLY! setType[#lc, SLIST]
	;


constructorBody
	{
		boolean explicit = false;
	}
	:   l:LCURLY^ setType[#l, SLIST]
		explicit=constructorInvocation
		(blockStatement)* RCURLY!
		{
			if (!explicit)
			{
				AST sci = #([CONSTRUCTOR], [SUPER], [ARGLIST]);
				((ASTWithToken) sci).initialize (l);
				sci.setType (CONSTRUCTOR);
				sci.setNextSibling (#l.getFirstChild ());
				#l.setFirstChild (sci);
			}
		}
	;


constructorInvocation returns [boolean explicit]
	{
		explicit = true;
	}
	:	((THIS | SUPER) LPAREN) => 	
		(THIS | SUPER) lp1:LPAREN^ argList[false] RPAREN!
		setType[#lp1, CONSTRUCTOR]
	|	(selectorExpression DOT SUPER LPAREN) =>
		selectorExpression DOT! qs:SUPER^ lp2:LPAREN^ argList[false] RPAREN!
		setType[#lp2, CONSTRUCTOR] setType[#qs, QUALIFIED_SUPER]
		{((ASTWithToken) #qs).token = null;}
		
	|	{explicit = false;}
	;


blockStatement
	:	(varDeclarationPredicate) => varDeclaration[true] SEMI!
	|	(modifiers CLASS) => m:modifiers! classDeclaration[#m]
	|	statement
	;


statement
	:	s:statementExpression
		(	r:RULE^ productionStatementsAsList SEMI! setType[#r, PRODUCE]
		|!	DOT rb:rules[#s] {#statement = #rb;}
		|	SEMI!
		)
	|   IDENT lbl:COLON^ statement setType[#lbl, LABELED_STATEMENT]
	|	statementNoExprNoLabel
	;



statementNoExprNoLabel
	:	block
	|	rulesInCurrentGraph
	|	d:RULE productionStatementsAsList rc:SEMI^ setType[#d, EMPTY] setType[#rc, PRODUCE]
	|	controlStatement[false]
	|	tr:TRY^ block
		(	FINALLY! block setType[#tr, FINALLY]
		|	(catchClause)+ (FINALLY^ block)?
		)
	|   statementSemi SEMI!
	|   s:SEMI setType[#s, SLIST]
	;


controlStatement[boolean prod]
	:	IF^ LPAREN! expression RPAREN! controlBody[prod]
		(options {warnWhenFollowAmbig = false;}:
			e:ELSE! controlBody[prod]
		)?
	|	SWITCH^ LPAREN! expression RPAREN!
		(	{prod}? LPAREN! (switchGroup[true])* RPAREN!
		|	LCURLY! (switchGroup[false])* RCURLY!
		)
	|	f:FOR^ forControl[#f] controlBody[prod]
	|	d:DO^ controlBody[prod] WHILE! LPAREN! expression RPAREN!
		(	{prod}?
		|	{!prod}? SEMI
		)
	|	w:WHILE^ LPAREN! expression RPAREN! controlBody[prod]
	|	SYNCHRONIZED_^ LPAREN! expression RPAREN!
		(	{prod}? productionBlock
		|	block
		)
	;

controlBody[boolean prod]
	:	{!prod}? statement
	|	{prod}? (productionBlock | block | controlStatement[true])
	;


catchClause
	:	CATCH^ LPAREN! modifiers name IDENT RPAREN! block
	;


switchGroup[boolean prod]
	:	(options {warnWhenFollowAmbig = false;}: switchGroupLabel)+
		({prod}?
			(	productionStatements
			|	productionBlock
			)
		|{!prod}?
			(blockStatement)*
		)
		{#switchGroup = #([SWITCH_GROUP], #switchGroup);}
	;


switchGroupLabel
	:	(CASE^ conditionalExpression | DEFAULT) COLON!
	;


forControl[AST forRoot]
	{
		int ft;
	}
	:	LPAREN!
		(	insert[#[SLIST]] basicForRest
		|	(varDeclarationPredicate) => ft=forVarControl
			setType[forRoot, ft]
		|	s:singleExpression!
			(	forStatementListRest[#s] basicForRest
			|	insert[#s] insert[#[VOID_]] setType[forRoot, ENHANCED_FOR]
			)
		)
		RPAREN!
	;


basicForRest
	:	SEMI! 
		(	expression
		|	insert[#[BOOLEAN_LITERAL, "true"]]
		)
		SEMI!
		(	s:singleExpression! forStatementListRest[#s]
		|	insert[#[SLIST]]
		)
	;


forVarControl returns [int forType]
	{
		forType = FOR;
	}
	:	m:modifiers! t:typeSpec! id:IDENT!
		(	c:COLON! singleExpression insert[#c]
			{
				#c.setType (VARIABLE_DEF);
				#c.setFirstChild (#m);
				#m.setNextSibling (#t);
				#t.setNextSibling (#id);
				forType = ENHANCED_FOR;
			}
		|	v:variableDeclaratorsRest[#m, #t, #id]! insert[#([SLIST], v)] basicForRest
		)
	;


forStatementListRest[AST first]
	:	insert[first] (COMMA! singleExpression)*
	{
		#forStatementListRest = #([SLIST], forStatementListRest);
	}
	;


statementSemi
	:	(RETURN^ | YIELD^) (expression)?
	|   BREAK^ (IDENT)?
	|   CONTINUE^ (IDENT)?
	|	THROW^ expression
	|	ASSERT^ singleExpressionNoRange (COLON! expression)?
	;


expression
	:	expressionOrDecl (COMMA! expressionOrDecl)*
		{
			if ((#expression != null)
				&& ((#expression.getNextSibling () != null)
					|| (#expression.getType () == VARIABLE_DEF)))
			{
				#expression = #([ELIST], expression);
			}
		}
	;


expressionOrDecl
	:	(varDeclarationPredicate) => singleDeclaration
	|	singleExpression
	;


singleExpression
	:	assignmentExpression
	;


statementExpression
	:	e:lambdaExpression! assignmentExpressionRest[#e]
	;


assignmentExpression
	:	e:rangeExpression! assignmentExpressionRest[#e]
	;
	

assignmentExpressionRest[AST e]
	:	insert[e]
		(	(	ASSIGN^
			|	ADD_ASSIGN^
			|	SUB_ASSIGN^
			|	MUL_ASSIGN^
			|	DIV_ASSIGN^
			|	REM_ASSIGN^
			|	SHR_ASSIGN^
			|	USHR_ASSIGN^
			|	SHL_ASSIGN^
			|	AND_ASSIGN^
			|	XOR_ASSIGN^
			|	OR_ASSIGN^
			|	POW_ASSIGN^
			|	DEFERRED_ASSIGN^
			|	DEFERRED_RATE_ASSIGN^
			|	DEFERRED_ADD^
			|	DEFERRED_SUB^
			|	DEFERRED_MUL^
			|	DEFERRED_DIV^
			|	DEFERRED_REM^
			|	DEFERRED_SHR^
			|	DEFERRED_USHR^
			|	DEFERRED_SHL^
			|	DEFERRED_AND^
			|	DEFERRED_XOR^
			|	DEFERRED_OR^
			|	DEFERRED_POW^
			)
			singleExpression
		)?
	;


singleExpressionNoRange
	:	lambdaExpression
		(	(	ASSIGN^
			|	ADD_ASSIGN^
			|	SUB_ASSIGN^
			|	MUL_ASSIGN^
			|	DIV_ASSIGN^
			|	REM_ASSIGN^
			|	SHR_ASSIGN^
			|	USHR_ASSIGN^
			|	SHL_ASSIGN^
			|	AND_ASSIGN^
			|	XOR_ASSIGN^
			|	OR_ASSIGN^
			|	POW_ASSIGN^
			|	DEFERRED_ASSIGN^
			|	DEFERRED_RATE_ASSIGN^
			|	DEFERRED_ADD^
			|	DEFERRED_SUB^
			|	DEFERRED_MUL^
			|	DEFERRED_DIV^
			|	DEFERRED_REM^
			|	DEFERRED_SHR^
			|	DEFERRED_USHR^
			|	DEFERRED_SHL^
			|	DEFERRED_AND^
			|	DEFERRED_XOR^
			|	DEFERRED_OR^
			|	DEFERRED_POW^
			)
			singleExpressionNoRange
		)?
	;
	

rangeExpression
	:	lambdaExpression (d:COLON^ lambdaExpression setType[#d, RANGE])*
	;


lambdaExpression
	:	(typeSpec IDENT LAMBDA) => lambdaExpression0
	|	(VOID_ LAMBDA) => lambdaExpression0
	|	conditionalExpression
	;


conditionalExpression
	:	guardExpression
		(QUESTION^ singleExpressionNoRange COLON! conditionalExpression)?
	;


lambdaExpression0!
	:	(	src:typeSpec var:IDENT
		|	v1:VOID_
		)
		la:LAMBDA
		(	dest:typeSpec
		|	v2:VOID_
		)
		(gen:MUL! setType[#gen, ITERATING_])?
		expr:conditionalExpression
	{
		if (#v1 != null)
		{
			#src = #v1;
		}
		if (#v2 != null)
		{
			#dest = #v2;
		}
		StringBuffer cls = new StringBuffer ();
		AST param;
		AST method = #[SLIST];
		if (isBuiltIn (#src.getType ()))
		{
			cls.append (Utils.firstToUpperCase (#src.getText ()));
			param = (#src.getType () == VOID_) ? null : #([PARAMETER_DEF],[MODIFIERS], src, var);
		}
		else
		{
			cls.append ("Object");
			param = #([PARAMETER_DEF], [MODIFIERS],
				#([DOT], #([DOT], [IDENT, "java"], [IDENT, "lang"]),
							[IDENT, "Object"]),
				#[IDENT, X_ID]);
			AST srcClone = getASTFactory ().dupTree (#src);
			method.addChild (#([VARIABLE_DEF], [MODIFIERS], src, var,
					#([ASSIGN], #([TYPECAST], srcClone, [IDENT, X_ID]))));
		}
		String eval;
		cls.append ("To");
		AST bdest;
		if (isBuiltIn (#dest.getType ()))
		{
			String s = Utils.firstToUpperCase (#dest.getText ());
			cls.append (s);
			eval = "evaluate" + s;
			bdest = #dest;
		}
		else
		{
			cls.append ("Object");
			eval = "evaluateObject";
			bdest = #([DOT], #([DOT], [IDENT, "java"], [IDENT, "lang"]),
						[IDENT, "Object"]);
			#expr = #([TYPECHECK], dest, expr);
		}
		if (#gen != null)
		{
			cls.append ("Generator");
			if (#dest.getType () == VOID_)
			{
				#expr = #([TYPECAST], [VOID_], expr);
			}
			method.addChild (#([YIELD], expr));
		}
		else
		{
			if (#dest.getType () == VOID_)
			{
				method.addChild (#expr);
				#expr = null;
			}
			method.addChild (#([RETURN], expr));
		}
		#la.setType (NEW);
		#lambdaExpression0 =
			#(la,
				#([DOT],
					#([DOT],
						#([DOT],
							(#[DOT], setToken([IDENT, "de"], la), setToken([IDENT, "grogra"], la)),
							setToken([IDENT, "xl"], la)),
						setToken([IDENT, "lang"], la)),
					setToken([IDENT, cls.toString ()], la)),
				[ARGLIST],
				#([CLASS],
					#([METHOD],
						#([MODIFIERS], [PUBLIC_], gen),
						bdest,
						[IDENT, eval],
						#([PARAMETERS], param),
						[THROWS],
						method)));
	}
	;


guardExpression
	:	logicalOrExpression (GUARD^ logicalOrExpression)*
	;


logicalOrExpression
	:	logicalAndExpression (COR^ logicalAndExpression)*
	;


logicalAndExpression
	:	inclusiveOrExpression (CAND^ inclusiveOrExpression)*
	;


inclusiveOrExpression
	:	exclusiveOrExpression (OR^ exclusiveOrExpression)*
	;


exclusiveOrExpression
	:	andExpression (XOR^ andExpression)*
	;


andExpression
	:	equalityExpression (AND^ equalityExpression)*
	;


equalityExpression
	:	relationalExpression ((NOT_EQUALS^ | EQUALS^) relationalExpression)*
	;


relationalExpression
	:	shiftExpression
		(	((LT^ | GT^ | LE^ | GE^ | IN^ | CMP^ | LONG_LEFT_ARROW^ | LONG_ARROW^ | LONG_LEFT_RIGHT_ARROW^ |
			  PLUS_LEFT_ARROW^ | PLUS_ARROW^ | PLUS_LINE^ | PLUS_LEFT_RIGHT_ARROW^ |
			  SLASH_LEFT_ARROW^ | SLASH_ARROW^ | SLASH_LINE^ | SLASH_LEFT_RIGHT_ARROW^ |
			  LINE^ | LEFT_RIGHT_ARROW^) shiftExpression)*
		|	INSTANCEOF^ refTypeSpec
		)
	;


shiftExpression
	:	additiveExpression
		((SHL^ | SHR^ | USHR^) additiveExpression)*
	;


additiveExpression
	:	multiplicativeExpression ((ADD^ | SUB^) multiplicativeExpression)*
	;


multiplicativeExpression
	:	powerExpression ((MUL^ | DIV^ | REM^) powerExpression)*
	;

powerExpression
	:	unaryExpression (POW^ powerExpression)?
	;

unaryExpression
	:	INC^ unaryExpression
	|	DEC^ unaryExpression
	|	a:ADD^ setType[#a, POS] unaryExpression
	|	SUB^ op:unaryExpression
		{
			Token t = ((ASTWithToken) #op).token;
			if ((t != null)
				&& (((t.getType () == INT_LITERAL)
					 && ((de.grogra.grammar.IntLiteral) t).isDecimal ())
					|| ((t.getType () == LONG_LITERAL)
						&& ((de.grogra.grammar.LongLiteral) t).isDecimal ())))
			{
				t.setText ('-' + t.getText ());
				#op.setText (t.getText ());
				#unaryExpression = #op;
			}
			else
			{
				#unaryExpression.setType (NEG);
			}
		}
	|	unaryExpressionNoBinaryOp
	;


unaryExpressionNoBinaryOp
	:	COM^ unaryExpression
	|	NOT^ unaryExpression
	|	(LPAREN (builtInType | VOID_ | type (LBRACK RBRACK)+) RPAREN) => 
		lp1:LPAREN^ setType[#lp1, TYPECAST] (typeSpec | VOID_) RPAREN!
		unaryExpression
	|	(LPAREN typeSpec RPAREN unaryExpressionNoBinaryOpPredicate) =>
		lp2:LPAREN^ setType[#lp2, TYPECAST] typeSpec RPAREN!
		unaryExpressionNoBinaryOp
	|	postfixExpression
	;

postfixExpression
	:	arrowExpression
		(	i:INC^ setType[#i, POST_INC]
		|	d:DEC^ setType[#d, POST_DEC]
		)*
	;


arrowExpression
	:	selectorExpression ((ARROW^ | LEFT_ARROW^) selectorExpression)*
	;
	

selectorExpression
	:	primary
		(options{warnWhenFollowAmbig = false;}:
			DOT^ IDENT
			(options{warnWhenFollowAmbig = false;}: // conflict with production for INVOKE_OP
				lp:LPAREN^ argList[false] RPAREN! setType[#lp, METHOD_CALL])?
		|	lb:LBRACK^
			(	singleExpression setType[#lb, INDEX_OP]
				(COMMA! singleExpression)*
			|	COLON! setType[#lb, ARRAY_ITERATOR]
			)
			RBRACK!
		|	invoke:LPAREN^ (singleExpression (COMMA! singleExpression)*)? RPAREN!
			setType[#invoke, INVOKE_OP]
		|	w:DOT^ withInstanceRest[#w]
		|	DOT! q:CONTEXT^ query RCONTEXT! setType[#q, QUERY_EXPR]
		|	DOT! n:NEW^ IDENT LPAREN! argList[false] RPAREN!
			setType[#n, QUALIFIED_NEW] (anonymousClassBody)?
		)*
	;

anonymousClassBody
	:	lc:LCURLY^ (typeMember[false, true])* RCURLY! setType[#lc, CLASS]
	;


primary
	:	NEW^ creator
	|	primaryNoCreator
	;


unaryExpressionNoBinaryOpPredicate
	:	COM
	|	NOT
	|	LPAREN
	|	NEW
	|	CONTEXT
	|	IDENT
	|	THIS
	|	SUPER
	|	literal
	|	builtInType
	|	VOID_
	|	QUOTE
	;

primaryNoCreator
	:	lp:LPAREN^ expression RPAREN! setType[#lp, EXPR]
	|   q:CONTEXT^ insertTree[EMPTY, null] query RCONTEXT! setType[#q, QUERY_EXPR]
	|	primaryNoParen[false]
	;


primaryNoParen[boolean checkClosure]
	:	id:IDENT (options{greedy=true;}: DOT^ id2:IDENT)*
		(options{greedy=true;}:
			DOT! THIS^
		|	DOT! c0:CLASS^ setType[#c0, CLASS_LITERAL]
		|	(DOT SUPER ~LPAREN) => DOT! SUPER^
		|	(lb1:LBRACK^ setArrayDeclarator_RBRACK[#lb1])+
			DOT! c1:CLASS^ setType[#c1, CLASS_LITERAL]
		|	{!checkClosure}? lp1:LPAREN^ argList[false] RPAREN!
			setType[#lp1, METHOD_CALL]
		|	(lparenArgs) => lp2:LPAREN^ argList[false] RPAREN!
			setType[#lp2, METHOD_CALL]
		|
		)
	|	THIS
	|	SUPER DOT^ IDENT
		(   {!checkClosure}? s1:LPAREN^ argList[false] RPAREN!
			setType[#s1, METHOD_CALL]
		|   (lparenArgs) => s2:LPAREN^ argList[false] RPAREN!
			setType[#s2, METHOD_CALL]
		|
		)
	|   literal
	|	builtInType (lb2:LBRACK^ setArrayDeclarator_RBRACK[#lb2])*
		DOT! c2:CLASS^ setType[#c2, CLASS_LITERAL]
	|   VOID_ DOT! c3:CLASS^ setType[#c3, CLASS_LITERAL]
	|	QUOTE^ expression QUOTE!
	;


creator
	:   t:type
		(   LPAREN! argList[false] RPAREN! (anonymousClassBody)?
		|   LBRACK!
			(   rb:RBRACK^ setArrayDeclarator[#rb]
				(lb:LBRACK^ setArrayDeclarator_RBRACK[#lb])*
				i:arrayInitializer[(#rb == null) ? #t
								   : (#lb == null) ? #rb : #lb]!
				{ #creator.setNextSibling (#i); }
			|   l:dimensionList!
				(options {warnWhenFollowAmbig = false;}:
					l2:LBRACK^ setArrayDeclarator_RBRACK[#l2])*
				{ #creator.setNextSibling (#l); }
			)
		)
	;


dimensionList
	:   expression r:RBRACK^ setType[#r, DIMLIST]
		(options {warnWhenFollowAmbig = false;}:
			LBRACK! expression RBRACK!)*
	;


argList[boolean allowOpenArg] returns [boolean hasOpenArg]
	{
		hasOpenArg = false;
	}
	:	(   (RPAREN) =>
		|	arg[allowOpenArg] (COMMA! arg[allowOpenArg])*
		)
		{
			for (AST n = #argList; n != null; n = n.getNextSibling ())
			{
				if (n.getType () == EMPTY)
				{
					hasOpenArg = true;
					break;
				}
			}
			#argList = #([ARGLIST], argList);
		}
	;


arg[boolean allowEmpty]
	:   singleExpression
	|   {allowEmpty}? d:DOT setType[#d, EMPTY]
	|   {allowEmpty}?
		{
			(#arg = new ASTWithToken ()).initialize (LT (1));
			#arg.setType (EMPTY);
		}
	;


arrayInitializer[AST t]
	:	lc:LCURLY^ setType[#lc, ARRAY_INIT] arrayInitializerRest[t]
	;


arrayInitializerRest[AST t]
	:   RCURLY!
	|   initializer[t] (RCURLY! | COMMA! arrayInitializerRest[t])
	;


initializer[AST t]
	:	{t.getType () == ARRAY_DECLARATOR}?
		arrayInitializer[t.getFirstChild ()]
	|	singleExpression
	;


varDeclaration![boolean allowInitializers]
	:	m:modifiers t:typeSpec v:variableDeclarators[#m, #t, allowInitializers]
		{ #varDeclaration = #v; }
	;


varDeclarationPredicate!
	:	modifiers typeSpec IDENT ~LAMBDA
	;


setArrayDeclarator_RBRACK![AST d]
	{ d.setType (ARRAY_DECLARATOR); }
	:	RBRACK
	;


setArrayDeclarator![AST d] options {defaultErrorHandler = false;}
	{ d.setType (ARRAY_DECLARATOR); }
	:
	;


setType![AST a, int type] options {defaultErrorHandler = false;}
	{
		if (a != null)
		{
			a.setType (type);
		}
	}
	:
	;


throwException!
	{ if (this != null) throw new RecognitionException (); }
	:
	;


insert![AST list] options {defaultErrorHandler = false;}
	{ #insert = list; }
	:
	;


insertTree![int rootType, AST list] options {defaultErrorHandler = false;}
	{ #insertTree = #([rootType], list); }
	:
	;

insertTree2![int rootType, AST list1, AST list2]
	options {defaultErrorHandler = false;}
	{ #insertTree2 = #([rootType], list1, list2); }
	:
	;

refTypeSpec
	:	t:name! typeSpecRest[#t]
	|	builtInType (lb:LBRACK^ setArrayDeclarator_RBRACK[#lb])+
	;


typeSpec
	:	t:type! typeSpecRest[#t]
	;


typeSpecRest[AST t]
	:   insert[t]
		(options {warnWhenFollowAmbig = false;}:
			lb:LBRACK^ setArrayDeclarator_RBRACK[#lb])*
	;


type
	:	name
	|	builtInType
	;


builtInType
	:	BOOLEAN_
	|	BYTE_
	|	SHORT_
	|	CHAR_
	|	INT_
	|	LONG_
	|	FLOAT_
	|	DOUBLE_
	;


modifiers
	:	(modifier)*
		{ #modifiers = #([MODIFIERS], #modifiers); }
	;


modifier
	:	PRIVATE_
	|	PUBLIC_
	|	PROTECTED_
	|	STATIC_
	|	TRANSIENT_
	|	FINAL_
	|	ABSTRACT_
	|	NATIVE_
	|	SYNCHRONIZED_
	|	VOLATILE_
	|	STRICT_
	|   CONST_
	|	annotation
	;


annotation
	:	ANNOTATION^ name
		(	annotationWithParens
		|	insert[#[MARKER]]
		)
	;


annotationWithParens
	:	lp:LPAREN^ setType[#lp, NORMAL]
		(	elementValuePair (COMMA! elementValuePair)*
		|	elementValue setType[#lp, SINGLE_ELEMENT]
		|	setType[#lp, MARKER]
		)
		RPAREN!
	;


elementValuePair
	:	IDENT ASSIGN^ elementValue
	;


elementValue
	:	(LCURLY) => lc:LCURLY^ setType[#lc, ARRAY_INIT] elementValuesRest
	|	conditionalExpression
	|	annotation
	;
	
	
elementValuesRest
	:	RCURLY!
	|	elementValue (RCURLY! | COMMA! elementValuesRest)
	;


singleDeclaration
	:	m:modifiers! t:typeSpec! variableDeclarator[#m, #t, true]
	;


variableDeclarators[AST mods, AST t, boolean allowInitializers]
	:	variableDeclarator[mods, t, allowInitializers]
		(	COMMA!
			variableDeclarator[getASTFactory ().dupTree (mods),
							   getASTFactory ().dupTree (t), allowInitializers]
		)*
	;


variableDeclaratorsRest[AST mods, AST t, AST id]
	:	variableDeclaratorRest[mods, t, id, true]
		(	COMMA!
			variableDeclarator[getASTFactory ().dupTree (mods),
							   getASTFactory ().dupTree (t), true]
		)*
	;


variableDeclarator[AST mods, AST t, boolean allowInitializers]
	:	id:IDENT! variableDeclaratorRest[mods, t, #id, allowInitializers]
	;


variableDeclaratorRest![AST mods, AST t, AST id, boolean allowInitializers]
	:	d:declaratorBrackets[t] ({allowInitializers}? v:varInitializer[#d])?
		{ #variableDeclaratorRest = #([VARIABLE_DEF], mods, d, id, v); }
	;


varInitializer[AST t]
	:	ASSIGN^ initializer[t]
	;


name
	:	IDENT (options{warnWhenFollowAmbig = false;}: DOT^ IDENT)*
	;


literal
	:   BOOLEAN_LITERAL
	|   INT_LITERAL
	|   LONG_LITERAL
	|   FLOAT_LITERAL
	|   DOUBLE_LITERAL
	|   CHAR_LITERAL
	|   STRING_LITERAL
	|	NULL_LITERAL
	;
