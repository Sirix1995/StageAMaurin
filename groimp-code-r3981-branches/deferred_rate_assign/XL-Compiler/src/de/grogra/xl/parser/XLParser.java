// $ANTLR 2.7.7 (2006-11-01): "XL.g" -> "XLParser.java"$


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

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

/* class XLParser extends antlr.LLkParser       implements XLTokenTypes
, */ public class XLParser extends de.grogra.xl.parser.Parser implements XLTokenTypes {

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
			shellPackage = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PACKAGE)).add(packageName));
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


protected XLParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public XLParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected XLParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public XLParser(TokenStream lexer) {
  this(lexer,2);
}

public XLParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final AST  compilationUnit() throws RecognitionException, TokenStreamException {
		AST unit;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compilationUnit_AST = null;
		AST m_AST = null;
		
				unit = null;
			
		
		{
		switch ( LA(1)) {
		case PACKAGE:
		{
			packageDeclaration();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case EOF:
		case MODULE:
		case CLASS:
		case INTERFACE:
		case SINGLE_TYPE_IMPORT:
		case SEMI:
		case SYNCHRONIZED_:
		case ANNOTATION:
		case PRIVATE_:
		case PUBLIC_:
		case PROTECTED_:
		case STATIC_:
		case TRANSIENT_:
		case FINAL_:
		case ABSTRACT_:
		case NATIVE_:
		case VOLATILE_:
		case STRICT_:
		case CONST_:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		_loop4:
		do {
			if ((LA(1)==SINGLE_TYPE_IMPORT)) {
				importDeclaration();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop4;
			}
			
		} while (true);
		}
		{
		_loop7:
		do {
			switch ( LA(1)) {
			case MODULE:
			case CLASS:
			case INTERFACE:
			case SYNCHRONIZED_:
			case ANNOTATION:
			case PRIVATE_:
			case PUBLIC_:
			case PROTECTED_:
			case STATIC_:
			case TRANSIENT_:
			case FINAL_:
			case ABSTRACT_:
			case NATIVE_:
			case VOLATILE_:
			case STRICT_:
			case CONST_:
			{
				modifiers();
				m_AST = (AST)returnAST;
				{
				switch ( LA(1)) {
				case CLASS:
				{
					classDeclaration(m_AST);
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case INTERFACE:
				{
					interfaceDeclaration(m_AST);
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case MODULE:
				{
					moduleDeclaration(m_AST);
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			case SEMI:
			{
				match(SEMI);
				break;
			}
			default:
			{
				break _loop7;
			}
			}
		} while (true);
		}
		AST tmp2_AST = null;
		tmp2_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp2_AST);
		match(Token.EOF_TYPE);
		if ( inputState.guessing==0 ) {
			compilationUnit_AST = (AST)currentAST.root;
			
						compilationUnit_AST.setType (COMPILATION_UNIT);
						unit = compilationUnit_AST;
					
		}
		compilationUnit_AST = (AST)currentAST.root;
		returnAST = compilationUnit_AST;
		return unit;
	}
	
	public final void packageDeclaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST packageDeclaration_AST = null;
		
		AST tmp3_AST = null;
		tmp3_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp3_AST);
		match(PACKAGE);
		name();
		astFactory.addASTChild(currentAST, returnAST);
		match(SEMI);
		packageDeclaration_AST = (AST)currentAST.root;
		returnAST = packageDeclaration_AST;
	}
	
	public final void importDeclaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST importDeclaration_AST = null;
		Token  i = null;
		AST i_AST = null;
		Token  st = null;
		AST st_AST = null;
		AST id_AST = null;
		Token  iod = null;
		AST iod_AST = null;
		Token  semi = null;
		AST semi_AST = null;
		
		i = LT(1);
		i_AST = astFactory.create(i);
		astFactory.makeASTRoot(currentAST, i_AST);
		match(SINGLE_TYPE_IMPORT);
		{
		switch ( LA(1)) {
		case STATIC_:
		{
			st = LT(1);
			st_AST = astFactory.create(st);
			match(STATIC_);
			break;
		}
		case IDENT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		name();
		id_AST = (AST)returnAST;
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case DOT:
		{
			match(DOT);
			iod = LT(1);
			iod_AST = astFactory.create(iod);
			match(MUL);
			break;
		}
		case SEMI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		semi = LT(1);
		semi_AST = astFactory.create(semi);
		match(SEMI);
		if ( inputState.guessing==0 ) {
			
					if (st != null)
					{
						if (iod != null)
						{
							i_AST.setType (STATIC_IMPORT_ON_DEMAND);
						}
						else
						{
							if (id_AST.getType () != DOT)
							{
								throw new MismatchedTokenException (tokenNames, semi_AST, DOT, false);
							}
							i_AST.setFirstChild (id_AST.getFirstChild ());
							i_AST.setType (SINGLE_STATIC_IMPORT);
						}
					}
					else if (iod != null)
					{
						i_AST.setType (IMPORT_ON_DEMAND);
					}
				
		}
		importDeclaration_AST = (AST)currentAST.root;
		returnAST = importDeclaration_AST;
	}
	
	public final void modifiers() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST modifiers_AST = null;
		
		{
		_loop417:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				modifier();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop417;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			modifiers_AST = (AST)currentAST.root;
			modifiers_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MODIFIERS)).add(modifiers_AST));
			currentAST.root = modifiers_AST;
			currentAST.child = modifiers_AST!=null &&modifiers_AST.getFirstChild()!=null ?
				modifiers_AST.getFirstChild() : modifiers_AST;
			currentAST.advanceChildToEnd();
		}
		modifiers_AST = (AST)currentAST.root;
		returnAST = modifiers_AST;
	}
	
	public final void classDeclaration(
		AST mods
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classDeclaration_AST = null;
		
		insert(mods);
		astFactory.addASTChild(currentAST, returnAST);
		AST tmp6_AST = null;
		tmp6_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp6_AST);
		match(CLASS);
		AST tmp7_AST = null;
		tmp7_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp7_AST);
		match(IDENT);
		{
		switch ( LA(1)) {
		case EXTENDS:
		case IMPLEMENTS:
		case LCURLY:
		{
			{
			switch ( LA(1)) {
			case EXTENDS:
			{
				classExtendsClause();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case IMPLEMENTS:
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case IMPLEMENTS:
			{
				implementsClause();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(LCURLY);
			{
			_loop40:
			do {
				if ((_tokenSet_1.member(LA(1)))) {
					typeMember(false, false);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop40;
				}
				
			} while (true);
			}
			match(RCURLY);
			break;
		}
		case LPAREN:
		{
			formalParameterList(false);
			astFactory.addASTChild(currentAST, returnAST);
			match(LPAREN);
			compoundPattern();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		classDeclaration_AST = (AST)currentAST.root;
		returnAST = classDeclaration_AST;
	}
	
	public final void interfaceDeclaration(
		AST mods
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interfaceDeclaration_AST = null;
		
		insert(mods);
		astFactory.addASTChild(currentAST, returnAST);
		AST tmp12_AST = null;
		tmp12_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp12_AST);
		match(INTERFACE);
		AST tmp13_AST = null;
		tmp13_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp13_AST);
		match(IDENT);
		{
		switch ( LA(1)) {
		case EXTENDS:
		{
			interfaceExtendsClause();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(LCURLY);
		{
		_loop48:
		do {
			if ((_tokenSet_1.member(LA(1)))) {
				typeMember(true, false);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop48;
			}
			
		} while (true);
		}
		match(RCURLY);
		interfaceDeclaration_AST = (AST)currentAST.root;
		returnAST = interfaceDeclaration_AST;
	}
	
	public final void moduleDeclaration(
		AST mods
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST moduleDeclaration_AST = null;
		Token  id = null;
		AST id_AST = null;
		AST params_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		AST args_AST = null;
		AST b_AST = null;
		
		insert(mods);
		astFactory.addASTChild(currentAST, returnAST);
		AST tmp16_AST = null;
		tmp16_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp16_AST);
		match(MODULE);
		id = LT(1);
		id_AST = astFactory.create(id);
		astFactory.addASTChild(currentAST, id_AST);
		match(IDENT);
		moduleParameterDeclarationList(id);
		params_AST = (AST)returnAST;
		{
		switch ( LA(1)) {
		case EXTENDS:
		{
			classExtendsClause();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case LPAREN:
			{
				lp = LT(1);
				lp_AST = astFactory.create(lp);
				match(LPAREN);
				argList(false);
				args_AST = (AST)returnAST;
				AST tmp17_AST = null;
				tmp17_AST = astFactory.create(LT(1));
				match(RPAREN);
				if ( inputState.guessing==0 ) {
					
										((ASTWithToken) args_AST).initialize (lp);
										args_AST.setType (ARGLIST);
									
				}
				break;
			}
			case IMPLEMENTS:
			case SEMI:
			case DOT:
			case RULE:
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case DOT:
			{
				moduleCtorBlock();
				b_AST = (AST)returnAST;
				break;
			}
			case IMPLEMENTS:
			case SEMI:
			case RULE:
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case IMPLEMENTS:
		case SEMI:
		case RULE:
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case IMPLEMENTS:
		{
			implementsClause();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case SEMI:
		case RULE:
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		insert(params_AST);
		astFactory.addASTChild(currentAST, returnAST);
		insert(args_AST);
		astFactory.addASTChild(currentAST, returnAST);
		insert(b_AST);
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case LCURLY:
		{
			match(LCURLY);
			{
			_loop30:
			do {
				if ((_tokenSet_1.member(LA(1)))) {
					typeMember(false, false);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop30;
				}
				
			} while (true);
			}
			match(RCURLY);
			{
			switch ( LA(1)) {
			case RULE:
			{
				moduleInst();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case IDENT:
			case MODULE:
			case CLASS:
			case INTERFACE:
			case SEMI:
			case VOID_:
			case BOOLEAN_:
			case BYTE_:
			case SHORT_:
			case CHAR_:
			case INT_:
			case LONG_:
			case FLOAT_:
			case DOUBLE_:
			case SYNCHRONIZED_:
			case ANNOTATION:
			case PRIVATE_:
			case PUBLIC_:
			case PROTECTED_:
			case STATIC_:
			case TRANSIENT_:
			case FINAL_:
			case ABSTRACT_:
			case NATIVE_:
			case VOLATILE_:
			case STRICT_:
			case CONST_:
			case LCURLY:
			case RCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case RULE:
		{
			moduleInst();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case SEMI:
		{
			match(SEMI);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		moduleDeclaration_AST = (AST)currentAST.root;
		returnAST = moduleDeclaration_AST;
	}
	
	public final AST  simpleCompilationUnit(
		AST mods, AST name, AST ext, AST impl
	) throws RecognitionException, TokenStreamException {
		AST unit;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST simpleCompilationUnit_AST = null;
		
				unit = null;
			
		
		{
		_loop10:
		do {
			if ((LA(1)==SINGLE_TYPE_IMPORT)) {
				importDeclaration();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop10;
			}
			
		} while (true);
		}
		simpleCompilationUnitMembers(mods, name, ext, impl);
		astFactory.addASTChild(currentAST, returnAST);
		AST tmp21_AST = null;
		tmp21_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp21_AST);
		match(Token.EOF_TYPE);
		if ( inputState.guessing==0 ) {
			simpleCompilationUnit_AST = (AST)currentAST.root;
			
						simpleCompilationUnit_AST.setType (COMPILATION_UNIT);
						unit = simpleCompilationUnit_AST;
					
		}
		simpleCompilationUnit_AST = (AST)currentAST.root;
		returnAST = simpleCompilationUnit_AST;
		return unit;
	}
	
	public final void simpleCompilationUnitMembers(
		AST mods, AST name, AST ext, AST impl
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST simpleCompilationUnitMembers_AST = null;
		
				AST instance = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(VARIABLE_DEF)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(MODIFIERS)).add(astFactory.create(PUBLIC_)).add(astFactory.create(STATIC_)))).add(astFactory.create(DECLARING_TYPE)).add(astFactory.create(IDENT,"INSTANCE")));
				instance.setNextSibling ((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INSTANCE_INIT)).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SLIST)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ASSIGN)).add(astFactory.create(IDENT,"INSTANCE")).add(astFactory.create(THIS))))))));
			
		
		insert(mods);
		astFactory.addASTChild(currentAST, returnAST);
		insert(name);
		astFactory.addASTChild(currentAST, returnAST);
		insert(ext);
		astFactory.addASTChild(currentAST, returnAST);
		insert(impl);
		astFactory.addASTChild(currentAST, returnAST);
		insert(instance);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop13:
		do {
			if ((_tokenSet_1.member(LA(1)))) {
				typeMember(false, false);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop13;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			simpleCompilationUnitMembers_AST = (AST)currentAST.root;
			
						simpleCompilationUnitMembers_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CLASS)).add(simpleCompilationUnitMembers_AST));
					
			currentAST.root = simpleCompilationUnitMembers_AST;
			currentAST.child = simpleCompilationUnitMembers_AST!=null &&simpleCompilationUnitMembers_AST.getFirstChild()!=null ?
				simpleCompilationUnitMembers_AST.getFirstChild() : simpleCompilationUnitMembers_AST;
			currentAST.advanceChildToEnd();
		}
		simpleCompilationUnitMembers_AST = (AST)currentAST.root;
		returnAST = simpleCompilationUnitMembers_AST;
	}
	
	public final void insert(
		AST list
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST insert_AST = null;
		insert_AST = list;
		
		returnAST = insert_AST;
	}
	
	public final void typeMember(
		boolean iface, boolean anonymous
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeMember_AST = null;
		Token  si = null;
		AST si_AST = null;
		AST mods_AST = null;
		AST t_AST = null;
		Token  st = null;
		AST st_AST = null;
		AST id_AST = null;
		AST params_AST = null;
		AST v_AST = null;
		Token  st2 = null;
		AST st2_AST = null;
		AST id2_AST = null;
		AST params2_AST = null;
		
				int overload = 0;
				AST varArgs;
			
		
		if (((LA(1)==LCURLY))&&(!iface)) {
			block();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				typeMember_AST = (AST)currentAST.root;
				typeMember_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INSTANCE_INIT)).add(typeMember_AST));
				currentAST.root = typeMember_AST;
				currentAST.child = typeMember_AST!=null &&typeMember_AST.getFirstChild()!=null ?
					typeMember_AST.getFirstChild() : typeMember_AST;
				currentAST.advanceChildToEnd();
			}
			typeMember_AST = (AST)currentAST.root;
		}
		else if (((LA(1)==STATIC_) && (LA(2)==LCURLY))&&(!iface)) {
			si = LT(1);
			si_AST = astFactory.create(si);
			astFactory.makeASTRoot(currentAST, si_AST);
			match(STATIC_);
			block();
			astFactory.addASTChild(currentAST, returnAST);
			setType(si_AST, STATIC_INIT);
			astFactory.addASTChild(currentAST, returnAST);
			typeMember_AST = (AST)currentAST.root;
		}
		else if ((_tokenSet_2.member(LA(1))) && (_tokenSet_3.member(LA(2)))) {
			modifiers();
			mods_AST = (AST)returnAST;
			{
			switch ( LA(1)) {
			case VOID_:
			{
				if ( inputState.guessing==0 ) {
					typeMember_AST = (AST)currentAST.root;
					typeMember_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(METHOD)).add(mods_AST));
					currentAST.root = typeMember_AST;
					currentAST.child = typeMember_AST!=null &&typeMember_AST.getFirstChild()!=null ?
						typeMember_AST.getFirstChild() : typeMember_AST;
					currentAST.advanceChildToEnd();
				}
				AST tmp22_AST = null;
				tmp22_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp22_AST);
				match(VOID_);
				{
				switch ( LA(1)) {
				case MUL:
				{
					st2 = LT(1);
					st2_AST = astFactory.create(st2);
					match(MUL);
					if ( inputState.guessing==0 ) {
						st2_AST.setType (ITERATING_); mods_AST.addChild (st2_AST);
					}
					break;
				}
				case IDENT:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				overload=methodIdent();
				id2_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				varArgs=formalParameterList(true);
				params2_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					
									mods_AST.addChild (varArgs);
									if (overload > 0)
									{
										CompilerBase.checkOperatorFunction
											(params2_AST, mods_AST, id2_AST, overload, exceptionList);
									}
								
				}
				throwsList();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case LBRACK:
				{
					rulesInCurrentGraph();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case LCURLY:
				{
					block();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case SEMI:
				{
					AST tmp23_AST = null;
					tmp23_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp23_AST);
					match(SEMI);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			case CLASS:
			{
				classDeclaration(mods_AST);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case INTERFACE:
			{
				interfaceDeclaration(mods_AST);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case MODULE:
			{
				moduleDeclaration(mods_AST);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
				if ((_tokenSet_4.member(LA(1))) && (_tokenSet_5.member(LA(2)))) {
					typeSpec();
					t_AST = (AST)returnAST;
					{
					boolean synPredMatched62 = false;
					if (((LA(1)==IDENT||LA(1)==MUL) && (_tokenSet_6.member(LA(2))))) {
						int _m62 = mark();
						synPredMatched62 = true;
						inputState.guessing++;
						try {
							{
							{
							switch ( LA(1)) {
							case MUL:
							{
								match(MUL);
								break;
							}
							case IDENT:
							{
								break;
							}
							default:
							{
								throw new NoViableAltException(LT(1), getFilename());
							}
							}
							}
							methodIdent();
							match(LPAREN);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched62 = false;
						}
						rewind(_m62);
inputState.guessing--;
					}
					if ( synPredMatched62 ) {
						if ( inputState.guessing==0 ) {
							typeMember_AST = (AST)currentAST.root;
							typeMember_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD)).add(mods_AST).add(t_AST));
							currentAST.root = typeMember_AST;
							currentAST.child = typeMember_AST!=null &&typeMember_AST.getFirstChild()!=null ?
								typeMember_AST.getFirstChild() : typeMember_AST;
							currentAST.advanceChildToEnd();
						}
						{
						switch ( LA(1)) {
						case MUL:
						{
							st = LT(1);
							st_AST = astFactory.create(st);
							match(MUL);
							if ( inputState.guessing==0 ) {
								st_AST.setType (ITERATING_); mods_AST.addChild (st_AST);
							}
							break;
						}
						case IDENT:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						overload=methodIdent();
						id_AST = (AST)returnAST;
						astFactory.addASTChild(currentAST, returnAST);
						varArgs=formalParameterList(true);
						params_AST = (AST)returnAST;
						astFactory.addASTChild(currentAST, returnAST);
						if ( inputState.guessing==0 ) {
							
												mods_AST.addChild (varArgs);
												if (overload > 0)
												{
													CompilerBase.checkOperatorFunction
														(params_AST, mods_AST, id_AST, overload, exceptionList);
												}
											
						}
						throwsList();
						astFactory.addASTChild(currentAST, returnAST);
						{
						switch ( LA(1)) {
						case LBRACK:
						{
							rulesInCurrentGraph();
							astFactory.addASTChild(currentAST, returnAST);
							break;
						}
						case LCURLY:
						{
							block();
							astFactory.addASTChild(currentAST, returnAST);
							break;
						}
						case SEMI:
						{
							AST tmp24_AST = null;
							tmp24_AST = astFactory.create(LT(1));
							astFactory.addASTChild(currentAST, tmp24_AST);
							match(SEMI);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
					}
					else if ((LA(1)==IDENT) && (_tokenSet_7.member(LA(2)))) {
						variableDeclarators(mods_AST, t_AST, true);
						v_AST = (AST)returnAST;
						AST tmp25_AST = null;
						tmp25_AST = astFactory.create(LT(1));
						match(SEMI);
						if ( inputState.guessing==0 ) {
							typeMember_AST = (AST)currentAST.root;
							typeMember_AST = v_AST;
							currentAST.root = typeMember_AST;
							currentAST.child = typeMember_AST!=null &&typeMember_AST.getFirstChild()!=null ?
								typeMember_AST.getFirstChild() : typeMember_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					
					}
				}
				else if (((LA(1)==IDENT) && (LA(2)==LPAREN))&&(!(iface || anonymous))) {
					if ( inputState.guessing==0 ) {
						typeMember_AST = (AST)currentAST.root;
						typeMember_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CONSTRUCTOR)).add(mods_AST));
						currentAST.root = typeMember_AST;
						currentAST.child = typeMember_AST!=null &&typeMember_AST.getFirstChild()!=null ?
							typeMember_AST.getFirstChild() : typeMember_AST;
						currentAST.advanceChildToEnd();
					}
					AST tmp26_AST = null;
					tmp26_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp26_AST);
					match(IDENT);
					varArgs=formalParameterList(true);
					astFactory.addASTChild(currentAST, returnAST);
					throwsList();
					astFactory.addASTChild(currentAST, returnAST);
					constructorBody();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						
										mods_AST.addChild (varArgs);
									
					}
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			typeMember_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==SEMI)) {
			match(SEMI);
			typeMember_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = typeMember_AST;
	}
	
	public final AST  shellStatements() throws RecognitionException, TokenStreamException {
		AST list;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST shellStatements_AST = null;
		AST mods_AST = null;
		AST t_AST = null;
		AST i_AST = null;
		Token  eof = null;
		AST eof_AST = null;
		
				list = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(COMPILATION_UNIT)).add(shellPackage));
			
		
		{
		_loop18:
		do {
			boolean synPredMatched17 = false;
			if (((_tokenSet_8.member(LA(1))) && (_tokenSet_9.member(LA(2))))) {
				int _m17 = mark();
				synPredMatched17 = true;
				inputState.guessing++;
				try {
					{
					switch ( LA(1)) {
					case SYNCHRONIZED_:
					case ANNOTATION:
					case PRIVATE_:
					case PUBLIC_:
					case PROTECTED_:
					case STATIC_:
					case TRANSIENT_:
					case FINAL_:
					case ABSTRACT_:
					case NATIVE_:
					case VOLATILE_:
					case STRICT_:
					case CONST_:
					{
						modifier();
						break;
					}
					case IDENT:
					case BOOLEAN_:
					case BYTE_:
					case SHORT_:
					case CHAR_:
					case INT_:
					case LONG_:
					case FLOAT_:
					case DOUBLE_:
					{
						typeSpec();
						match(IDENT);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
				}
				catch (RecognitionException pe) {
					synPredMatched17 = false;
				}
				rewind(_m17);
inputState.guessing--;
			}
			if ( synPredMatched17 ) {
				modifiers();
				mods_AST = (AST)returnAST;
				typeSpec();
				t_AST = (AST)returnAST;
				variableDeclarators(mods_AST, t_AST, true);
				astFactory.addASTChild(currentAST, returnAST);
				match(SEMI);
			}
			else if ((LA(1)==SINGLE_TYPE_IMPORT)) {
				importDeclaration();
				i_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					list.addChild (i_AST);
				}
			}
			else if ((_tokenSet_10.member(LA(1))) && (_tokenSet_11.member(LA(2)))) {
				assignmentExpression();
				astFactory.addASTChild(currentAST, returnAST);
				match(SEMI);
			}
			else if ((_tokenSet_12.member(LA(1))) && (_tokenSet_13.member(LA(2)))) {
				statementNoExprNoLabel();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop18;
			}
			
		} while (true);
		}
		eof = LT(1);
		eof_AST = astFactory.create(eof);
		astFactory.makeASTRoot(currentAST, eof_AST);
		match(Token.EOF_TYPE);
		if ( inputState.guessing==0 ) {
			
					eof_AST.setType (SHELL_BLOCK);
					list.addChild ((AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(CLASS)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(MODIFIERS)).add(astFactory.create(PUBLIC_)).add(astFactory.create(ABSTRACT_)))).add(astFactory.create(IDENT,shell)).add((AST)astFactory.make( (new ASTArray(7)).add(astFactory.create(METHOD)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(MODIFIERS)).add(astFactory.create(PUBLIC_)).add(astFactory.create(STATIC_)))).add(astFactory.create(VOID_)).add(astFactory.create(IDENT,"execute")).add(astFactory.create(PARAMETERS)).add(astFactory.create(THROWS)).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SLIST)).add(eof_AST)))))));
				
		}
		shellStatements_AST = (AST)currentAST.root;
		returnAST = shellStatements_AST;
		return list;
	}
	
	public final void modifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST modifier_AST = null;
		
		switch ( LA(1)) {
		case PRIVATE_:
		{
			AST tmp30_AST = null;
			tmp30_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp30_AST);
			match(PRIVATE_);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case PUBLIC_:
		{
			AST tmp31_AST = null;
			tmp31_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp31_AST);
			match(PUBLIC_);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case PROTECTED_:
		{
			AST tmp32_AST = null;
			tmp32_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp32_AST);
			match(PROTECTED_);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case STATIC_:
		{
			AST tmp33_AST = null;
			tmp33_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp33_AST);
			match(STATIC_);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case TRANSIENT_:
		{
			AST tmp34_AST = null;
			tmp34_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp34_AST);
			match(TRANSIENT_);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case FINAL_:
		{
			AST tmp35_AST = null;
			tmp35_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp35_AST);
			match(FINAL_);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case ABSTRACT_:
		{
			AST tmp36_AST = null;
			tmp36_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp36_AST);
			match(ABSTRACT_);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case NATIVE_:
		{
			AST tmp37_AST = null;
			tmp37_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp37_AST);
			match(NATIVE_);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case SYNCHRONIZED_:
		{
			AST tmp38_AST = null;
			tmp38_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp38_AST);
			match(SYNCHRONIZED_);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case VOLATILE_:
		{
			AST tmp39_AST = null;
			tmp39_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp39_AST);
			match(VOLATILE_);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case STRICT_:
		{
			AST tmp40_AST = null;
			tmp40_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp40_AST);
			match(STRICT_);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case CONST_:
		{
			AST tmp41_AST = null;
			tmp41_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp41_AST);
			match(CONST_);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		case ANNOTATION:
		{
			annotation();
			astFactory.addASTChild(currentAST, returnAST);
			modifier_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = modifier_AST;
	}
	
	public final void typeSpec() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeSpec_AST = null;
		AST t_AST = null;
		
		type();
		t_AST = (AST)returnAST;
		typeSpecRest(t_AST);
		astFactory.addASTChild(currentAST, returnAST);
		typeSpec_AST = (AST)currentAST.root;
		returnAST = typeSpec_AST;
	}
	
	public final void variableDeclarators(
		AST mods, AST t, boolean allowInitializers
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variableDeclarators_AST = null;
		
		variableDeclarator(mods, t, allowInitializers);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop434:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				variableDeclarator(getASTFactory ().dupTree (mods),
							   getASTFactory ().dupTree (t), allowInitializers);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop434;
			}
			
		} while (true);
		}
		variableDeclarators_AST = (AST)currentAST.root;
		returnAST = variableDeclarators_AST;
	}
	
	public final void assignmentExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignmentExpression_AST = null;
		AST e_AST = null;
		
		rangeExpression();
		e_AST = (AST)returnAST;
		assignmentExpressionRest(e_AST);
		astFactory.addASTChild(currentAST, returnAST);
		assignmentExpression_AST = (AST)currentAST.root;
		returnAST = assignmentExpression_AST;
	}
	
	public final void statementNoExprNoLabel() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statementNoExprNoLabel_AST = null;
		Token  d = null;
		AST d_AST = null;
		Token  rc = null;
		AST rc_AST = null;
		Token  tr = null;
		AST tr_AST = null;
		Token  s = null;
		AST s_AST = null;
		
		switch ( LA(1)) {
		case LCURLY:
		{
			block();
			astFactory.addASTChild(currentAST, returnAST);
			statementNoExprNoLabel_AST = (AST)currentAST.root;
			break;
		}
		case LBRACK:
		{
			rulesInCurrentGraph();
			astFactory.addASTChild(currentAST, returnAST);
			statementNoExprNoLabel_AST = (AST)currentAST.root;
			break;
		}
		case RULE:
		{
			d = LT(1);
			d_AST = astFactory.create(d);
			astFactory.addASTChild(currentAST, d_AST);
			match(RULE);
			productionStatementsAsList();
			astFactory.addASTChild(currentAST, returnAST);
			rc = LT(1);
			rc_AST = astFactory.create(rc);
			astFactory.makeASTRoot(currentAST, rc_AST);
			match(SEMI);
			setType(d_AST, EMPTY);
			astFactory.addASTChild(currentAST, returnAST);
			setType(rc_AST, PRODUCE);
			astFactory.addASTChild(currentAST, returnAST);
			statementNoExprNoLabel_AST = (AST)currentAST.root;
			break;
		}
		case IF:
		case SYNCHRONIZED_:
		case FOR:
		case WHILE:
		case DO:
		case SWITCH:
		{
			controlStatement(false);
			astFactory.addASTChild(currentAST, returnAST);
			statementNoExprNoLabel_AST = (AST)currentAST.root;
			break;
		}
		case TRY:
		{
			tr = LT(1);
			tr_AST = astFactory.create(tr);
			astFactory.makeASTRoot(currentAST, tr_AST);
			match(TRY);
			block();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case FINALLY:
			{
				match(FINALLY);
				block();
				astFactory.addASTChild(currentAST, returnAST);
				setType(tr_AST, FINALLY);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CATCH:
			{
				{
				int _cnt214=0;
				_loop214:
				do {
					if ((LA(1)==CATCH)) {
						catchClause();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt214>=1 ) { break _loop214; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt214++;
				} while (true);
				}
				{
				switch ( LA(1)) {
				case FINALLY:
				{
					AST tmp44_AST = null;
					tmp44_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp44_AST);
					match(FINALLY);
					block();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case EOF:
				case BOOLEAN_LITERAL:
				case INT_LITERAL:
				case LONG_LITERAL:
				case FLOAT_LITERAL:
				case DOUBLE_LITERAL:
				case CHAR_LITERAL:
				case STRING_LITERAL:
				case IDENT:
				case CLASS:
				case SINGLE_TYPE_IMPORT:
				case SUPER:
				case SEMI:
				case VOID_:
				case BOOLEAN_:
				case BYTE_:
				case SHORT_:
				case CHAR_:
				case INT_:
				case LONG_:
				case FLOAT_:
				case DOUBLE_:
				case LT:
				case GT:
				case LINE:
				case LEFT_RIGHT_ARROW:
				case PLUS_LEFT_ARROW:
				case PLUS_ARROW:
				case PLUS_LINE:
				case PLUS_LEFT_RIGHT_ARROW:
				case SLASH_LEFT_ARROW:
				case SLASH_ARROW:
				case SLASH_LINE:
				case SLASH_LEFT_RIGHT_ARROW:
				case CONTEXT:
				case SUB:
				case LEFT_ARROW:
				case MUL:
				case ADD:
				case RULE:
				case DOUBLE_ARROW_RULE:
				case EXEC_RULE:
				case COM:
				case NOT:
				case DIV:
				case REM:
				case POW:
				case SHL:
				case SHR:
				case USHR:
				case LE:
				case GE:
				case CMP:
				case OR:
				case XOR:
				case AND:
				case COR:
				case CAND:
				case THIS:
				case IF:
				case RETURN:
				case YIELD:
				case THROW:
				case SYNCHRONIZED_:
				case ASSERT:
				case BREAK:
				case CONTINUE:
				case TRY:
				case FOR:
				case WHILE:
				case DO:
				case SWITCH:
				case CASE:
				case DEFAULT:
				case NULL_LITERAL:
				case LONG_LEFT_ARROW:
				case LONG_ARROW:
				case LONG_LEFT_RIGHT_ARROW:
				case QUOTE:
				case INC:
				case DEC:
				case IN:
				case GUARD:
				case NEW:
				case ANNOTATION:
				case PRIVATE_:
				case PUBLIC_:
				case PROTECTED_:
				case STATIC_:
				case TRANSIENT_:
				case FINAL_:
				case ABSTRACT_:
				case NATIVE_:
				case VOLATILE_:
				case STRICT_:
				case CONST_:
				case ELSE:
				case LPAREN:
				case RPAREN:
				case LBRACK:
				case RBRACK:
				case LCURLY:
				case RCURLY:
				case COMMA:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			statementNoExprNoLabel_AST = (AST)currentAST.root;
			break;
		}
		case RETURN:
		case YIELD:
		case THROW:
		case ASSERT:
		case BREAK:
		case CONTINUE:
		{
			statementSemi();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMI);
			statementNoExprNoLabel_AST = (AST)currentAST.root;
			break;
		}
		case SEMI:
		{
			s = LT(1);
			s_AST = astFactory.create(s);
			astFactory.addASTChild(currentAST, s_AST);
			match(SEMI);
			setType(s_AST, SLIST);
			astFactory.addASTChild(currentAST, returnAST);
			statementNoExprNoLabel_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = statementNoExprNoLabel_AST;
	}
	
	public final void name() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST name_AST = null;
		
		AST tmp46_AST = null;
		tmp46_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp46_AST);
		match(IDENT);
		{
		_loop444:
		do {
			if ((LA(1)==DOT) && (LA(2)==IDENT)) {
				AST tmp47_AST = null;
				tmp47_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp47_AST);
				match(DOT);
				AST tmp48_AST = null;
				tmp48_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp48_AST);
				match(IDENT);
			}
			else {
				break _loop444;
			}
			
		} while (true);
		}
		name_AST = (AST)currentAST.root;
		returnAST = name_AST;
	}
	
	public final void moduleParameterDeclarationList(
		Token id
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST moduleParameterDeclarationList_AST = null;
		Token  l = null;
		AST l_AST = null;
		
		switch ( LA(1)) {
		case LPAREN:
		{
			l = LT(1);
			l_AST = astFactory.create(l);
			astFactory.makeASTRoot(currentAST, l_AST);
			match(LPAREN);
			{
			switch ( LA(1)) {
			case IDENT:
			case SUPER:
			case BOOLEAN_:
			case BYTE_:
			case SHORT_:
			case CHAR_:
			case INT_:
			case LONG_:
			case FLOAT_:
			case DOUBLE_:
			case SYNCHRONIZED_:
			case ANNOTATION:
			case PRIVATE_:
			case PUBLIC_:
			case PROTECTED_:
			case STATIC_:
			case TRANSIENT_:
			case FINAL_:
			case ABSTRACT_:
			case NATIVE_:
			case VOLATILE_:
			case STRICT_:
			case CONST_:
			{
				moduleParameterDeclaration();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop79:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						moduleParameterDeclaration();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop79;
					}
					
				} while (true);
				}
				break;
			}
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RPAREN);
			setType(l_AST, PARAMETERS);
			astFactory.addASTChild(currentAST, returnAST);
			moduleParameterDeclarationList_AST = (AST)currentAST.root;
			break;
		}
		case EXTENDS:
		case IMPLEMENTS:
		case SEMI:
		case RULE:
		case LCURLY:
		{
			if ( inputState.guessing==0 ) {
				moduleParameterDeclarationList_AST = (AST)currentAST.root;
				
							moduleParameterDeclarationList_AST = astFactory.create(id);
							moduleParameterDeclarationList_AST.setType (PARAMETERS);
						
				currentAST.root = moduleParameterDeclarationList_AST;
				currentAST.child = moduleParameterDeclarationList_AST!=null &&moduleParameterDeclarationList_AST.getFirstChild()!=null ?
					moduleParameterDeclarationList_AST.getFirstChild() : moduleParameterDeclarationList_AST;
				currentAST.advanceChildToEnd();
			}
			moduleParameterDeclarationList_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = moduleParameterDeclarationList_AST;
	}
	
	public final void classExtendsClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classExtendsClause_AST = null;
		
		AST tmp51_AST = null;
		tmp51_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp51_AST);
		match(EXTENDS);
		name();
		astFactory.addASTChild(currentAST, returnAST);
		classExtendsClause_AST = (AST)currentAST.root;
		returnAST = classExtendsClause_AST;
	}
	
	public final boolean  argList(
		boolean allowOpenArg
	) throws RecognitionException, TokenStreamException {
		boolean hasOpenArg;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST argList_AST = null;
		
				hasOpenArg = false;
			
		
		{
		boolean synPredMatched389 = false;
		if (((LA(1)==RPAREN) && (_tokenSet_14.member(LA(2))))) {
			int _m389 = mark();
			synPredMatched389 = true;
			inputState.guessing++;
			try {
				{
				match(RPAREN);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched389 = false;
			}
			rewind(_m389);
inputState.guessing--;
		}
		if ( synPredMatched389 ) {
		}
		else if ((_tokenSet_15.member(LA(1))) && (_tokenSet_14.member(LA(2)))) {
			arg(allowOpenArg);
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop391:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					arg(allowOpenArg);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop391;
				}
				
			} while (true);
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		if ( inputState.guessing==0 ) {
			argList_AST = (AST)currentAST.root;
			
						for (AST n = argList_AST; n != null; n = n.getNextSibling ())
						{
							if (n.getType () == EMPTY)
							{
								hasOpenArg = true;
								break;
							}
						}
						argList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGLIST)).add(argList_AST));
					
			currentAST.root = argList_AST;
			currentAST.child = argList_AST!=null &&argList_AST.getFirstChild()!=null ?
				argList_AST.getFirstChild() : argList_AST;
			currentAST.advanceChildToEnd();
		}
		argList_AST = (AST)currentAST.root;
		returnAST = argList_AST;
		return hasOpenArg;
	}
	
	public final void moduleCtorBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST moduleCtorBlock_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		
		match(DOT);
		lp = LT(1);
		lp_AST = astFactory.create(lp);
		astFactory.makeASTRoot(currentAST, lp_AST);
		match(LPAREN);
		{
		switch ( LA(1)) {
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case CONTEXT:
		case SUB:
		case ADD:
		case COM:
		case NOT:
		case THIS:
		case SYNCHRONIZED_:
		case NULL_LITERAL:
		case QUOTE:
		case INC:
		case DEC:
		case NEW:
		case ANNOTATION:
		case PRIVATE_:
		case PUBLIC_:
		case PROTECTED_:
		case STATIC_:
		case TRANSIENT_:
		case FINAL_:
		case ABSTRACT_:
		case NATIVE_:
		case VOLATILE_:
		case STRICT_:
		case CONST_:
		case LPAREN:
		{
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RPAREN);
		setType(lp_AST, SLIST);
		astFactory.addASTChild(currentAST, returnAST);
		moduleCtorBlock_AST = (AST)currentAST.root;
		returnAST = moduleCtorBlock_AST;
	}
	
	public final void implementsClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST implementsClause_AST = null;
		
		AST tmp55_AST = null;
		tmp55_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp55_AST);
		match(IMPLEMENTS);
		name();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop44:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				name();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop44;
			}
			
		} while (true);
		}
		implementsClause_AST = (AST)currentAST.root;
		returnAST = implementsClause_AST;
	}
	
	public final void moduleInst() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST moduleInst_AST = null;
		Token  sp = null;
		AST sp_AST = null;
		Token  s = null;
		AST s_AST = null;
		
		sp = LT(1);
		sp_AST = astFactory.create(sp);
		astFactory.makeASTRoot(currentAST, sp_AST);
		match(RULE);
		setType(sp_AST, SLIST);
		astFactory.addASTChild(currentAST, returnAST);
		productionStatements();
		astFactory.addASTChild(currentAST, returnAST);
		s = LT(1);
		s_AST = astFactory.create(s);
		astFactory.makeASTRoot(currentAST, s_AST);
		match(SEMI);
		setType(s_AST, INSTANTIATOR);
		astFactory.addASTChild(currentAST, returnAST);
		moduleInst_AST = (AST)currentAST.root;
		returnAST = moduleInst_AST;
	}
	
	public final void expression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;
		
		expressionOrDecl();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop258:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				expressionOrDecl();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop258;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			expression_AST = (AST)currentAST.root;
			
						if ((expression_AST != null)
							&& ((expression_AST.getNextSibling () != null)
								|| (expression_AST.getType () == VARIABLE_DEF)))
						{
							expression_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ELIST)).add(expression_AST));
						}
					
			currentAST.root = expression_AST;
			currentAST.child = expression_AST!=null &&expression_AST.getFirstChild()!=null ?
				expression_AST.getFirstChild() : expression_AST;
			currentAST.advanceChildToEnd();
		}
		expression_AST = (AST)currentAST.root;
		returnAST = expression_AST;
	}
	
	public final void setType(
		AST a, int type
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST setType_AST = null;
		
				if (a != null)
				{
					a.setType (type);
				}
			
		
		returnAST = setType_AST;
	}
	
	public final void productionStatements() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST productionStatements_AST = null;
		AST s_AST = null;
		
				AST prev = null;
			
		
		{
		_loop165:
		do {
			if ((_tokenSet_16.member(LA(1)))) {
				productionStatement(prev);
				s_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					
									if (s_AST != null)
									{
										prev = s_AST;
									}
								
				}
			}
			else {
				break _loop165;
			}
			
		} while (true);
		}
		productionStatements_AST = (AST)currentAST.root;
		returnAST = productionStatements_AST;
	}
	
	public final AST  formalParameterList(
		boolean allowVarArgs
	) throws RecognitionException, TokenStreamException {
		AST varArgs;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST formalParameterList_AST = null;
		Token  l = null;
		AST l_AST = null;
		
				varArgs = null;
			
		
		l = LT(1);
		l_AST = astFactory.create(l);
		astFactory.makeASTRoot(currentAST, l_AST);
		match(LPAREN);
		{
		switch ( LA(1)) {
		case IDENT:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case SYNCHRONIZED_:
		case ANNOTATION:
		case PRIVATE_:
		case PUBLIC_:
		case PROTECTED_:
		case STATIC_:
		case TRANSIENT_:
		case FINAL_:
		case ABSTRACT_:
		case NATIVE_:
		case VOLATILE_:
		case STRICT_:
		case CONST_:
		{
			varArgs=parameterDeclaration(allowVarArgs);
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop70:
			do {
				if (((LA(1)==COMMA))&&(varArgs == null)) {
					match(COMMA);
					varArgs=parameterDeclaration(allowVarArgs);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop70;
				}
				
			} while (true);
			}
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RPAREN);
		setType(l_AST, PARAMETERS);
		astFactory.addASTChild(currentAST, returnAST);
		formalParameterList_AST = (AST)currentAST.root;
		returnAST = formalParameterList_AST;
		return varArgs;
	}
	
	public final void compoundPattern() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compoundPattern_AST = null;
		
		qvDeclarations();
		astFactory.addASTChild(currentAST, returnAST);
		connectedPatternList();
		astFactory.addASTChild(currentAST, returnAST);
		if ( inputState.guessing==0 ) {
			compoundPattern_AST = (AST)currentAST.root;
			
						compoundPattern_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(COMPOUND_PATTERN)).add(compoundPattern_AST));
					
			currentAST.root = compoundPattern_AST;
			currentAST.child = compoundPattern_AST!=null &&compoundPattern_AST.getFirstChild()!=null ?
				compoundPattern_AST.getFirstChild() : compoundPattern_AST;
			currentAST.advanceChildToEnd();
		}
		compoundPattern_AST = (AST)currentAST.root;
		returnAST = compoundPattern_AST;
	}
	
	public final void interfaceExtendsClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST interfaceExtendsClause_AST = null;
		
		AST tmp60_AST = null;
		tmp60_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp60_AST);
		match(EXTENDS);
		name();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop51:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				name();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop51;
			}
			
		} while (true);
		}
		interfaceExtendsClause_AST = (AST)currentAST.root;
		returnAST = interfaceExtendsClause_AST;
	}
	
	public final void overloadableOperator() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST overloadableOperator_AST = null;
		Token  c = null;
		AST c_AST = null;
		Token  l = null;
		AST l_AST = null;
		Token  a = null;
		AST a_AST = null;
		
		switch ( LA(1)) {
		case NOT:
		{
			AST tmp62_AST = null;
			tmp62_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp62_AST);
			match(NOT);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case COM:
		{
			AST tmp63_AST = null;
			tmp63_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp63_AST);
			match(COM);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case COR:
		{
			AST tmp64_AST = null;
			tmp64_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp64_AST);
			match(COR);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case CAND:
		{
			AST tmp65_AST = null;
			tmp65_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp65_AST);
			match(CAND);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case ADD:
		{
			AST tmp66_AST = null;
			tmp66_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp66_AST);
			match(ADD);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case SUB:
		{
			AST tmp67_AST = null;
			tmp67_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp67_AST);
			match(SUB);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case MUL:
		{
			AST tmp68_AST = null;
			tmp68_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp68_AST);
			match(MUL);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DIV:
		{
			AST tmp69_AST = null;
			tmp69_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp69_AST);
			match(DIV);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case REM:
		{
			AST tmp70_AST = null;
			tmp70_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp70_AST);
			match(REM);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case POW:
		{
			AST tmp71_AST = null;
			tmp71_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp71_AST);
			match(POW);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case ADD_ASSIGN:
		{
			AST tmp72_AST = null;
			tmp72_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp72_AST);
			match(ADD_ASSIGN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case SUB_ASSIGN:
		{
			AST tmp73_AST = null;
			tmp73_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp73_AST);
			match(SUB_ASSIGN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case MUL_ASSIGN:
		{
			AST tmp74_AST = null;
			tmp74_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp74_AST);
			match(MUL_ASSIGN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DIV_ASSIGN:
		{
			AST tmp75_AST = null;
			tmp75_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp75_AST);
			match(DIV_ASSIGN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case REM_ASSIGN:
		{
			AST tmp76_AST = null;
			tmp76_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp76_AST);
			match(REM_ASSIGN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case POW_ASSIGN:
		{
			AST tmp77_AST = null;
			tmp77_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp77_AST);
			match(POW_ASSIGN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case SHL:
		{
			AST tmp78_AST = null;
			tmp78_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp78_AST);
			match(SHL);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case SHR:
		{
			AST tmp79_AST = null;
			tmp79_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp79_AST);
			match(SHR);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case USHR:
		{
			AST tmp80_AST = null;
			tmp80_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp80_AST);
			match(USHR);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case SHL_ASSIGN:
		{
			AST tmp81_AST = null;
			tmp81_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp81_AST);
			match(SHL_ASSIGN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case SHR_ASSIGN:
		{
			AST tmp82_AST = null;
			tmp82_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp82_AST);
			match(SHR_ASSIGN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case USHR_ASSIGN:
		{
			AST tmp83_AST = null;
			tmp83_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp83_AST);
			match(USHR_ASSIGN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case XOR:
		{
			AST tmp84_AST = null;
			tmp84_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp84_AST);
			match(XOR);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case OR:
		{
			AST tmp85_AST = null;
			tmp85_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp85_AST);
			match(OR);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case AND:
		{
			AST tmp86_AST = null;
			tmp86_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp86_AST);
			match(AND);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case XOR_ASSIGN:
		{
			AST tmp87_AST = null;
			tmp87_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp87_AST);
			match(XOR_ASSIGN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case OR_ASSIGN:
		{
			AST tmp88_AST = null;
			tmp88_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp88_AST);
			match(OR_ASSIGN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case AND_ASSIGN:
		{
			AST tmp89_AST = null;
			tmp89_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp89_AST);
			match(AND_ASSIGN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case INC:
		{
			AST tmp90_AST = null;
			tmp90_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp90_AST);
			match(INC);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEC:
		{
			AST tmp91_AST = null;
			tmp91_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp91_AST);
			match(DEC);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case EQUALS:
		{
			AST tmp92_AST = null;
			tmp92_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp92_AST);
			match(EQUALS);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case NOT_EQUALS:
		{
			AST tmp93_AST = null;
			tmp93_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp93_AST);
			match(NOT_EQUALS);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case GE:
		{
			AST tmp94_AST = null;
			tmp94_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp94_AST);
			match(GE);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case GT:
		{
			AST tmp95_AST = null;
			tmp95_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp95_AST);
			match(GT);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case LE:
		{
			AST tmp96_AST = null;
			tmp96_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp96_AST);
			match(LE);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case LT:
		{
			AST tmp97_AST = null;
			tmp97_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp97_AST);
			match(LT);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case CMP:
		{
			AST tmp98_AST = null;
			tmp98_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp98_AST);
			match(CMP);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case COLON:
		{
			c = LT(1);
			c_AST = astFactory.create(c);
			astFactory.addASTChild(currentAST, c_AST);
			match(COLON);
			setType(c_AST, RANGE);
			astFactory.addASTChild(currentAST, returnAST);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case IN:
		{
			AST tmp99_AST = null;
			tmp99_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp99_AST);
			match(IN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case GUARD:
		{
			AST tmp100_AST = null;
			tmp100_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp100_AST);
			match(GUARD);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case PLUS_LEFT_ARROW:
		{
			AST tmp101_AST = null;
			tmp101_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp101_AST);
			match(PLUS_LEFT_ARROW);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case PLUS_ARROW:
		{
			AST tmp102_AST = null;
			tmp102_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp102_AST);
			match(PLUS_ARROW);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case PLUS_LINE:
		{
			AST tmp103_AST = null;
			tmp103_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp103_AST);
			match(PLUS_LINE);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case PLUS_LEFT_RIGHT_ARROW:
		{
			AST tmp104_AST = null;
			tmp104_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp104_AST);
			match(PLUS_LEFT_RIGHT_ARROW);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case SLASH_LEFT_ARROW:
		{
			AST tmp105_AST = null;
			tmp105_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp105_AST);
			match(SLASH_LEFT_ARROW);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case SLASH_ARROW:
		{
			AST tmp106_AST = null;
			tmp106_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp106_AST);
			match(SLASH_ARROW);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case SLASH_LINE:
		{
			AST tmp107_AST = null;
			tmp107_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp107_AST);
			match(SLASH_LINE);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case SLASH_LEFT_RIGHT_ARROW:
		{
			AST tmp108_AST = null;
			tmp108_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp108_AST);
			match(SLASH_LEFT_RIGHT_ARROW);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case LINE:
		{
			AST tmp109_AST = null;
			tmp109_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp109_AST);
			match(LINE);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case LEFT_RIGHT_ARROW:
		{
			AST tmp110_AST = null;
			tmp110_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp110_AST);
			match(LEFT_RIGHT_ARROW);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case LONG_LEFT_ARROW:
		{
			AST tmp111_AST = null;
			tmp111_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp111_AST);
			match(LONG_LEFT_ARROW);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case LONG_ARROW:
		{
			AST tmp112_AST = null;
			tmp112_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp112_AST);
			match(LONG_ARROW);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case LONG_LEFT_RIGHT_ARROW:
		{
			AST tmp113_AST = null;
			tmp113_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp113_AST);
			match(LONG_LEFT_RIGHT_ARROW);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case LBRACK:
		{
			l = LT(1);
			l_AST = astFactory.create(l);
			astFactory.addASTChild(currentAST, l_AST);
			match(LBRACK);
			setType(l_AST, INDEX_OP);
			astFactory.addASTChild(currentAST, returnAST);
			match(RBRACK);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case LEFT_ARROW:
		{
			a = LT(1);
			a_AST = astFactory.create(a);
			astFactory.addASTChild(currentAST, a_AST);
			match(LEFT_ARROW);
			setType(a_AST, X_LEFT_RIGHT_ARROW);
			astFactory.addASTChild(currentAST, returnAST);
			match(ARROW);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case QUOTE:
		{
			AST tmp116_AST = null;
			tmp116_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp116_AST);
			match(QUOTE);
			match(QUOTE);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEFERRED_ASSIGN:
		{
			AST tmp118_AST = null;
			tmp118_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp118_AST);
			match(DEFERRED_ASSIGN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEFERRED_RATE_ASSIGN:
		{
			AST tmp119_AST = null;
			tmp119_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp119_AST);
			match(DEFERRED_RATE_ASSIGN);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEFERRED_POW:
		{
			AST tmp120_AST = null;
			tmp120_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp120_AST);
			match(DEFERRED_POW);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEFERRED_MUL:
		{
			AST tmp121_AST = null;
			tmp121_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp121_AST);
			match(DEFERRED_MUL);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEFERRED_DIV:
		{
			AST tmp122_AST = null;
			tmp122_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp122_AST);
			match(DEFERRED_DIV);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEFERRED_REM:
		{
			AST tmp123_AST = null;
			tmp123_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp123_AST);
			match(DEFERRED_REM);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEFERRED_ADD:
		{
			AST tmp124_AST = null;
			tmp124_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp124_AST);
			match(DEFERRED_ADD);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEFERRED_SUB:
		{
			AST tmp125_AST = null;
			tmp125_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp125_AST);
			match(DEFERRED_SUB);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEFERRED_SHL:
		{
			AST tmp126_AST = null;
			tmp126_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp126_AST);
			match(DEFERRED_SHL);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEFERRED_SHR:
		{
			AST tmp127_AST = null;
			tmp127_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp127_AST);
			match(DEFERRED_SHR);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEFERRED_USHR:
		{
			AST tmp128_AST = null;
			tmp128_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp128_AST);
			match(DEFERRED_USHR);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEFERRED_AND:
		{
			AST tmp129_AST = null;
			tmp129_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp129_AST);
			match(DEFERRED_AND);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEFERRED_XOR:
		{
			AST tmp130_AST = null;
			tmp130_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp130_AST);
			match(DEFERRED_XOR);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		case DEFERRED_OR:
		{
			AST tmp131_AST = null;
			tmp131_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp131_AST);
			match(DEFERRED_OR);
			overloadableOperator_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = overloadableOperator_AST;
	}
	
	public final int  methodIdent() throws RecognitionException, TokenStreamException {
		int symbol;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST methodIdent_AST = null;
		Token  id = null;
		AST id_AST = null;
		AST o_AST = null;
		
				symbol = 0;
			
		
		id = LT(1);
		id_AST = astFactory.create(id);
		astFactory.addASTChild(currentAST, id_AST);
		match(IDENT);
		{
		boolean synPredMatched56 = false;
		if ((((LA(1)==LPAREN) && (LA(2)==RPAREN))&&(id_AST.getText ().equals ("operator")))) {
			int _m56 = mark();
			synPredMatched56 = true;
			inputState.guessing++;
			try {
				{
				match(LPAREN);
				match(RPAREN);
				match(LPAREN);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched56 = false;
			}
			rewind(_m56);
inputState.guessing--;
		}
		if ( synPredMatched56 ) {
			AST tmp132_AST = null;
			tmp132_AST = astFactory.create(LT(1));
			match(LPAREN);
			AST tmp133_AST = null;
			tmp133_AST = astFactory.create(LT(1));
			match(RPAREN);
			if ( inputState.guessing==0 ) {
				
								symbol = INVOKE_OP;
							
			}
		}
		else if (((_tokenSet_17.member(LA(1))))&&(id_AST.getText ().equals ("operator"))) {
			overloadableOperator();
			o_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				
								symbol = o_AST.getType ();
							
			}
		}
		else if ((LA(1)==LPAREN) && (_tokenSet_18.member(LA(2)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		methodIdent_AST = (AST)currentAST.root;
		returnAST = methodIdent_AST;
		return symbol;
	}
	
	public final void block() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST block_AST = null;
		Token  lc = null;
		AST lc_AST = null;
		
		lc = LT(1);
		lc_AST = astFactory.create(lc);
		astFactory.makeASTRoot(currentAST, lc_AST);
		match(LCURLY);
		{
		_loop193:
		do {
			if ((_tokenSet_19.member(LA(1)))) {
				blockStatement();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop193;
			}
			
		} while (true);
		}
		match(RCURLY);
		setType(lc_AST, SLIST);
		astFactory.addASTChild(currentAST, returnAST);
		block_AST = (AST)currentAST.root;
		returnAST = block_AST;
	}
	
	public final void throwsList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST throwsList_AST = null;
		
		switch ( LA(1)) {
		case THROWS:
		{
			AST tmp135_AST = null;
			tmp135_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp135_AST);
			match(THROWS);
			name();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop75:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					name();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop75;
				}
				
			} while (true);
			}
			throwsList_AST = (AST)currentAST.root;
			break;
		}
		case SEMI:
		case LBRACK:
		case LCURLY:
		{
			if ( inputState.guessing==0 ) {
				throwsList_AST = (AST)currentAST.root;
				throwsList_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(THROWS)));
				currentAST.root = throwsList_AST;
				currentAST.child = throwsList_AST!=null &&throwsList_AST.getFirstChild()!=null ?
					throwsList_AST.getFirstChild() : throwsList_AST;
				currentAST.advanceChildToEnd();
			}
			throwsList_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = throwsList_AST;
	}
	
	public final void rulesInCurrentGraph() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rulesInCurrentGraph_AST = null;
		AST r_AST = null;
		
				ASTWithToken e = new ASTWithToken (EMPTY, "");
				e.token = LT (1);
			
		
		rules(e);
		r_AST = (AST)returnAST;
		astFactory.addASTChild(currentAST, returnAST);
		rulesInCurrentGraph_AST = (AST)currentAST.root;
		returnAST = rulesInCurrentGraph_AST;
	}
	
	public final void constructorBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constructorBody_AST = null;
		Token  l = null;
		AST l_AST = null;
		
				boolean explicit = false;
			
		
		l = LT(1);
		l_AST = astFactory.create(l);
		astFactory.makeASTRoot(currentAST, l_AST);
		match(LCURLY);
		setType(l_AST, SLIST);
		astFactory.addASTChild(currentAST, returnAST);
		explicit=constructorInvocation();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop196:
		do {
			if ((_tokenSet_19.member(LA(1)))) {
				blockStatement();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop196;
			}
			
		} while (true);
		}
		match(RCURLY);
		if ( inputState.guessing==0 ) {
			
						if (!explicit)
						{
							AST sci = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(CONSTRUCTOR)).add(astFactory.create(SUPER)).add(astFactory.create(ARGLIST)));
							((ASTWithToken) sci).initialize (l);
							sci.setType (CONSTRUCTOR);
							sci.setNextSibling (l_AST.getFirstChild ());
							l_AST.setFirstChild (sci);
						}
					
		}
		constructorBody_AST = (AST)currentAST.root;
		returnAST = constructorBody_AST;
	}
	
	public final AST  parameterDeclaration(
		boolean allowVarArgs
	) throws RecognitionException, TokenStreamException {
		AST varArg;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parameterDeclaration_AST = null;
		AST m_AST = null;
		AST t_AST = null;
		Token  v = null;
		AST v_AST = null;
		Token  id = null;
		AST id_AST = null;
		AST b_AST = null;
		
				varArg = null;
			
		
		modifiers();
		m_AST = (AST)returnAST;
		typeSpec();
		t_AST = (AST)returnAST;
		{
		if (((LA(1)==VARARGS_))&&(allowVarArgs)) {
			v = LT(1);
			v_AST = astFactory.create(v);
			match(VARARGS_);
		}
		else if ((LA(1)==IDENT)) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		id = LT(1);
		id_AST = astFactory.create(id);
		match(IDENT);
		declaratorBrackets(t_AST);
		b_AST = (AST)returnAST;
		if ( inputState.guessing==0 ) {
			parameterDeclaration_AST = (AST)currentAST.root;
			
						varArg = v_AST;
						if (varArg != null)
						{
							b_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARRAY_DECLARATOR)).add(b_AST));
						}
						parameterDeclaration_AST = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(PARAMETER_DEF)).add(m_AST).add(b_AST).add(id_AST));
					
			currentAST.root = parameterDeclaration_AST;
			currentAST.child = parameterDeclaration_AST!=null &&parameterDeclaration_AST.getFirstChild()!=null ?
				parameterDeclaration_AST.getFirstChild() : parameterDeclaration_AST;
			currentAST.advanceChildToEnd();
		}
		returnAST = parameterDeclaration_AST;
		return varArg;
	}
	
	public final void declaratorBrackets(
		AST type
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaratorBrackets_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		
		insert(type);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop84:
		do {
			if ((LA(1)==LBRACK)) {
				lb = LT(1);
				lb_AST = astFactory.create(lb);
				astFactory.makeASTRoot(currentAST, lb_AST);
				match(LBRACK);
				setArrayDeclarator_RBRACK(lb_AST);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop84;
			}
			
		} while (true);
		}
		declaratorBrackets_AST = (AST)currentAST.root;
		returnAST = declaratorBrackets_AST;
	}
	
	public final void moduleParameterDeclaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST moduleParameterDeclaration_AST = null;
		AST m_AST = null;
		AST t_AST = null;
		Token  id = null;
		AST id_AST = null;
		AST b_AST = null;
		Token  method = null;
		AST method_AST = null;
		Token  d = null;
		AST d_AST = null;
		
		switch ( LA(1)) {
		case IDENT:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case SYNCHRONIZED_:
		case ANNOTATION:
		case PRIVATE_:
		case PUBLIC_:
		case PROTECTED_:
		case STATIC_:
		case TRANSIENT_:
		case FINAL_:
		case ABSTRACT_:
		case NATIVE_:
		case VOLATILE_:
		case STRICT_:
		case CONST_:
		{
			modifiers();
			m_AST = (AST)returnAST;
			typeSpec();
			t_AST = (AST)returnAST;
			id = LT(1);
			id_AST = astFactory.create(id);
			match(IDENT);
			declaratorBrackets(t_AST);
			b_AST = (AST)returnAST;
			{
			switch ( LA(1)) {
			case RETURN:
			{
				AST tmp138_AST = null;
				tmp138_AST = astFactory.create(LT(1));
				match(RETURN);
				method = LT(1);
				method_AST = astFactory.create(method);
				match(IDENT);
				AST tmp139_AST = null;
				tmp139_AST = astFactory.create(LT(1));
				match(LPAREN);
				AST tmp140_AST = null;
				tmp140_AST = astFactory.create(LT(1));
				match(RPAREN);
				break;
			}
			case RPAREN:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				moduleParameterDeclaration_AST = (AST)currentAST.root;
				moduleParameterDeclaration_AST = (AST)astFactory.make( (new ASTArray(5)).add(astFactory.create(PARAMETER_DEF)).add(m_AST).add(b_AST).add(id_AST).add(method_AST));
				currentAST.root = moduleParameterDeclaration_AST;
				currentAST.child = moduleParameterDeclaration_AST!=null &&moduleParameterDeclaration_AST.getFirstChild()!=null ?
					moduleParameterDeclaration_AST.getFirstChild() : moduleParameterDeclaration_AST;
				currentAST.advanceChildToEnd();
			}
			break;
		}
		case SUPER:
		{
			AST tmp141_AST = null;
			tmp141_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp141_AST);
			match(SUPER);
			d = LT(1);
			d_AST = astFactory.create(d);
			astFactory.makeASTRoot(currentAST, d_AST);
			match(DOT);
			AST tmp142_AST = null;
			tmp142_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp142_AST);
			match(IDENT);
			setType(d_AST, PARAMETER_DEF);
			astFactory.addASTChild(currentAST, returnAST);
			moduleParameterDeclaration_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = moduleParameterDeclaration_AST;
	}
	
	public final void setArrayDeclarator_RBRACK(
		AST d
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST setArrayDeclarator_RBRACK_AST = null;
		d.setType (ARRAY_DECLARATOR);
		
		AST tmp143_AST = null;
		tmp143_AST = astFactory.create(LT(1));
		match(RBRACK);
		returnAST = setArrayDeclarator_RBRACK_AST;
	}
	
	public final void rules(
		AST graph
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rules_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		Token  rb = null;
		AST rb_AST = null;
		
		lb = LT(1);
		lb_AST = astFactory.create(lb);
		astFactory.makeASTRoot(currentAST, lb_AST);
		match(LBRACK);
		setType(lb_AST, RULE_BLOCK);
		astFactory.addASTChild(currentAST, returnAST);
		insert(graph);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop88:
		do {
			switch ( LA(1)) {
			case BOOLEAN_LITERAL:
			case INT_LITERAL:
			case LONG_LITERAL:
			case FLOAT_LITERAL:
			case DOUBLE_LITERAL:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case IDENT:
			case SUPER:
			case VOID_:
			case BOOLEAN_:
			case BYTE_:
			case SHORT_:
			case CHAR_:
			case INT_:
			case LONG_:
			case FLOAT_:
			case DOUBLE_:
			case LT:
			case GT:
			case LINE:
			case LEFT_RIGHT_ARROW:
			case PLUS_LEFT_ARROW:
			case PLUS_ARROW:
			case PLUS_LINE:
			case PLUS_LEFT_RIGHT_ARROW:
			case SLASH_LEFT_ARROW:
			case SLASH_ARROW:
			case SLASH_LINE:
			case SLASH_LEFT_RIGHT_ARROW:
			case CONTEXT:
			case SUB:
			case LEFT_ARROW:
			case RULE:
			case DOUBLE_ARROW_RULE:
			case EXEC_RULE:
			case COM:
			case NOT:
			case XOR:
			case THIS:
			case SYNCHRONIZED_:
			case NULL_LITERAL:
			case LONG_LEFT_ARROW:
			case LONG_ARROW:
			case LONG_LEFT_RIGHT_ARROW:
			case QUOTE:
			case DEC:
			case ANNOTATION:
			case PRIVATE_:
			case PUBLIC_:
			case PROTECTED_:
			case STATIC_:
			case TRANSIENT_:
			case FINAL_:
			case ABSTRACT_:
			case NATIVE_:
			case VOLATILE_:
			case STRICT_:
			case CONST_:
			case LPAREN:
			case LBRACK:
			{
				rule();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LCURLY:
			{
				script();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				break _loop88;
			}
			}
		} while (true);
		}
		rb = LT(1);
		rb_AST = astFactory.create(rb);
		astFactory.makeASTRoot(currentAST, rb_AST);
		match(RBRACK);
		setType(rb_AST, SLIST);
		astFactory.addASTChild(currentAST, returnAST);
		rules_AST = (AST)currentAST.root;
		returnAST = rules_AST;
	}
	
	public final void rule() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rule_AST = null;
		AST q_AST = null;
		Token  r1 = null;
		AST r1_AST = null;
		Token  r2 = null;
		AST r2_AST = null;
		AST p1_AST = null;
		Token  r3 = null;
		AST r3_AST = null;
		AST p2_AST = null;
		
		{
		switch ( LA(1)) {
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case LT:
		case GT:
		case LINE:
		case LEFT_RIGHT_ARROW:
		case PLUS_LEFT_ARROW:
		case PLUS_ARROW:
		case PLUS_LINE:
		case PLUS_LEFT_RIGHT_ARROW:
		case SLASH_LEFT_ARROW:
		case SLASH_ARROW:
		case SLASH_LINE:
		case SLASH_LEFT_RIGHT_ARROW:
		case CONTEXT:
		case SUB:
		case LEFT_ARROW:
		case COM:
		case NOT:
		case XOR:
		case THIS:
		case SYNCHRONIZED_:
		case NULL_LITERAL:
		case LONG_LEFT_ARROW:
		case LONG_ARROW:
		case LONG_LEFT_RIGHT_ARROW:
		case QUOTE:
		case DEC:
		case ANNOTATION:
		case PRIVATE_:
		case PUBLIC_:
		case PROTECTED_:
		case STATIC_:
		case TRANSIENT_:
		case FINAL_:
		case ABSTRACT_:
		case NATIVE_:
		case VOLATILE_:
		case STRICT_:
		case CONST_:
		case LPAREN:
		case LBRACK:
		{
			query();
			q_AST = (AST)returnAST;
			break;
		}
		case RULE:
		case DOUBLE_ARROW_RULE:
		case EXEC_RULE:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
						if (q_AST == null)
						{
							q_AST = astFactory.create(EMPTY);
						}
					
		}
		{
		switch ( LA(1)) {
		case RULE:
		case DOUBLE_ARROW_RULE:
		{
			{
			switch ( LA(1)) {
			case RULE:
			{
				r1 = LT(1);
				r1_AST = astFactory.create(r1);
				match(RULE);
				break;
			}
			case DOUBLE_ARROW_RULE:
			{
				r2 = LT(1);
				r2_AST = astFactory.create(r2);
				match(DOUBLE_ARROW_RULE);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			productionStatements();
			p1_AST = (AST)returnAST;
			AST tmp144_AST = null;
			tmp144_AST = astFactory.create(LT(1));
			match(SEMI);
			if ( inputState.guessing==0 ) {
				rule_AST = (AST)currentAST.root;
				
								if (r2_AST != null)
								{
									r1_AST = r2_AST;
									r1_AST.setType (RULE);
									r2_AST = astFactory.create(DOUBLE_ARROW_RULE);
								}
								rule_AST = (AST)astFactory.make( (new ASTArray(4)).add(r1_AST).add(r2_AST).add(q_AST).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SLIST)).add(p1_AST))));
							
				currentAST.root = rule_AST;
				currentAST.child = rule_AST!=null &&rule_AST.getFirstChild()!=null ?
					rule_AST.getFirstChild() : rule_AST;
				currentAST.advanceChildToEnd();
			}
			break;
		}
		case EXEC_RULE:
		{
			r3 = LT(1);
			r3_AST = astFactory.create(r3);
			match(EXEC_RULE);
			statement();
			p2_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				rule_AST = (AST)currentAST.root;
				
								r3_AST.setType (RULE);
								rule_AST = (AST)astFactory.make( (new ASTArray(4)).add(r3_AST).add(astFactory.create(EXEC_RULE)).add(q_AST).add(p2_AST));
							
				currentAST.root = rule_AST;
				currentAST.child = rule_AST!=null &&rule_AST.getFirstChild()!=null ?
					rule_AST.getFirstChild() : rule_AST;
				currentAST.advanceChildToEnd();
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		returnAST = rule_AST;
	}
	
	public final void script() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST script_AST = null;
		
		match(LCURLY);
		{
		_loop190:
		do {
			if ((_tokenSet_19.member(LA(1)))) {
				blockStatement();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop190;
			}
			
		} while (true);
		}
		match(RCURLY);
		script_AST = (AST)currentAST.root;
		returnAST = script_AST;
	}
	
	public final void query() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST query_AST = null;
		
		{
		boolean synPredMatched106 = false;
		if (((LA(1)==LPAREN) && (_tokenSet_20.member(LA(2))))) {
			int _m106 = mark();
			synPredMatched106 = true;
			inputState.guessing++;
			try {
				{
				match(LPAREN);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched106 = false;
			}
			rewind(_m106);
inputState.guessing--;
		}
		if ( synPredMatched106 ) {
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
		}
		else if ((_tokenSet_21.member(LA(1))) && (_tokenSet_22.member(LA(2)))) {
			insert(astFactory.create(EMPTY));
			astFactory.addASTChild(currentAST, returnAST);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		compoundPattern();
		astFactory.addASTChild(currentAST, returnAST);
		if ( inputState.guessing==0 ) {
			query_AST = (AST)currentAST.root;
			
						query_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(QUERY)).add(query_AST));
					
			currentAST.root = query_AST;
			currentAST.child = query_AST!=null &&query_AST.getFirstChild()!=null ?
				query_AST.getFirstChild() : query_AST;
			currentAST.advanceChildToEnd();
		}
		query_AST = (AST)currentAST.root;
		returnAST = query_AST;
	}
	
	public final void statement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_AST = null;
		AST s_AST = null;
		Token  r = null;
		AST r_AST = null;
		AST rb_AST = null;
		Token  lbl = null;
		AST lbl_AST = null;
		
		if ((_tokenSet_10.member(LA(1))) && (_tokenSet_23.member(LA(2)))) {
			statementExpression();
			s_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case RULE:
			{
				r = LT(1);
				r_AST = astFactory.create(r);
				astFactory.makeASTRoot(currentAST, r_AST);
				match(RULE);
				productionStatementsAsList();
				astFactory.addASTChild(currentAST, returnAST);
				match(SEMI);
				setType(r_AST, PRODUCE);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case DOT:
			{
				AST tmp150_AST = null;
				tmp150_AST = astFactory.create(LT(1));
				match(DOT);
				rules(s_AST);
				rb_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					statement_AST = (AST)currentAST.root;
					statement_AST = rb_AST;
					currentAST.root = statement_AST;
					currentAST.child = statement_AST!=null &&statement_AST.getFirstChild()!=null ?
						statement_AST.getFirstChild() : statement_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case SEMI:
			{
				match(SEMI);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			statement_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==IDENT) && (LA(2)==COLON)) {
			AST tmp152_AST = null;
			tmp152_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp152_AST);
			match(IDENT);
			lbl = LT(1);
			lbl_AST = astFactory.create(lbl);
			astFactory.makeASTRoot(currentAST, lbl_AST);
			match(COLON);
			statement();
			astFactory.addASTChild(currentAST, returnAST);
			setType(lbl_AST, LABELED_STATEMENT);
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
		}
		else if ((_tokenSet_12.member(LA(1)))) {
			statementNoExprNoLabel();
			astFactory.addASTChild(currentAST, returnAST);
			statement_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = statement_AST;
	}
	
	public final void lparenArgs() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lparenArgs_AST = null;
		
		AST tmp153_AST = null;
		tmp153_AST = astFactory.create(LT(1));
		match(LPAREN);
		argList(true);
		AST tmp154_AST = null;
		tmp154_AST = astFactory.create(LT(1));
		match(RPAREN);
		{
		boolean synPredMatched96 = false;
		if (((LA(1)==LCURLY) && (_tokenSet_10.member(LA(2))))) {
			int _m96 = mark();
			synPredMatched96 = true;
			inputState.guessing++;
			try {
				{
				closureRange();
				}
			}
			catch (RecognitionException pe) {
				synPredMatched96 = false;
			}
			rewind(_m96);
inputState.guessing--;
		}
		if ( synPredMatched96 ) {
			throwException();
			closureRange();
		}
		else if ((_tokenSet_24.member(LA(1))) && ((LA(2) >= BOOLEAN_LITERAL && LA(2) <= RCONTEXT))) {
			{
			match(_tokenSet_24);
			}
			matchNot(EOF);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		returnAST = lparenArgs_AST;
	}
	
	public final void closureRange() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST closureRange_AST = null;
		Token  r = null;
		AST r_AST = null;
		
		r = LT(1);
		r_AST = astFactory.create(r);
		astFactory.makeASTRoot(currentAST, r_AST);
		match(LCURLY);
		singleExpression();
		astFactory.addASTChild(currentAST, returnAST);
		setType(r_AST, RANGE_EXACTLY);
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case COMMA:
		{
			match(COMMA);
			setType(r_AST, RANGE_MIN);
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case BOOLEAN_LITERAL:
			case INT_LITERAL:
			case LONG_LITERAL:
			case FLOAT_LITERAL:
			case DOUBLE_LITERAL:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case IDENT:
			case SUPER:
			case VOID_:
			case BOOLEAN_:
			case BYTE_:
			case SHORT_:
			case CHAR_:
			case INT_:
			case LONG_:
			case FLOAT_:
			case DOUBLE_:
			case CONTEXT:
			case SUB:
			case ADD:
			case COM:
			case NOT:
			case THIS:
			case NULL_LITERAL:
			case QUOTE:
			case INC:
			case DEC:
			case NEW:
			case LPAREN:
			{
				singleExpression();
				astFactory.addASTChild(currentAST, returnAST);
				setType(r_AST, RANGE);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case RCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RCURLY);
		closureRange_AST = (AST)currentAST.root;
		returnAST = closureRange_AST;
	}
	
	public final void throwException() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST throwException_AST = null;
		if (this != null) throw new RecognitionException ();
		
		returnAST = throwException_AST;
	}
	
	public final void lparenElist() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lparenElist_AST = null;
		
		AST tmp159_AST = null;
		tmp159_AST = astFactory.create(LT(1));
		match(LPAREN);
		expression();
		AST tmp160_AST = null;
		tmp160_AST = astFactory.create(LT(1));
		match(RPAREN);
		{
		boolean synPredMatched101 = false;
		if (((LA(1)==LCURLY) && (_tokenSet_10.member(LA(2))))) {
			int _m101 = mark();
			synPredMatched101 = true;
			inputState.guessing++;
			try {
				{
				closureRange();
				}
			}
			catch (RecognitionException pe) {
				synPredMatched101 = false;
			}
			rewind(_m101);
inputState.guessing--;
		}
		if ( synPredMatched101 ) {
			throwException();
			closureRange();
		}
		else if ((_tokenSet_24.member(LA(1))) && ((LA(2) >= BOOLEAN_LITERAL && LA(2) <= RCONTEXT))) {
			{
			match(_tokenSet_24);
			}
			matchNot(EOF);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		returnAST = lparenElist_AST;
	}
	
	public final void qvDeclarations() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST qvDeclarations_AST = null;
		
		boolean synPredMatched110 = false;
		if (((_tokenSet_8.member(LA(1))) && (_tokenSet_9.member(LA(2))))) {
			int _m110 = mark();
			synPredMatched110 = true;
			inputState.guessing++;
			try {
				{
				varDeclaration(false);
				match(SEMI);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched110 = false;
			}
			rewind(_m110);
inputState.guessing--;
		}
		if ( synPredMatched110 ) {
			varDeclaration(false);
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMI);
			qvDeclarations();
			astFactory.addASTChild(currentAST, returnAST);
			qvDeclarations_AST = (AST)currentAST.root;
		}
		else if ((_tokenSet_25.member(LA(1))) && (_tokenSet_26.member(LA(2)))) {
			qvDeclarations_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = qvDeclarations_AST;
	}
	
	public final void connectedPatternList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST connectedPatternList_AST = null;
		Token  c = null;
		AST c_AST = null;
		
		connectedPattern();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop113:
		do {
			if ((LA(1)==COMMA)) {
				c = LT(1);
				c_AST = astFactory.create(c);
				astFactory.addASTChild(currentAST, c_AST);
				match(COMMA);
				setType(c_AST, SEPARATE);
				astFactory.addASTChild(currentAST, returnAST);
				connectedPattern();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop113;
			}
			
		} while (true);
		}
		connectedPatternList_AST = (AST)currentAST.root;
		returnAST = connectedPatternList_AST;
	}
	
	public final void varDeclaration(
		boolean allowInitializers
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST varDeclaration_AST = null;
		AST m_AST = null;
		AST t_AST = null;
		AST v_AST = null;
		
		modifiers();
		m_AST = (AST)returnAST;
		typeSpec();
		t_AST = (AST)returnAST;
		variableDeclarators(m_AST, t_AST, allowInitializers);
		v_AST = (AST)returnAST;
		if ( inputState.guessing==0 ) {
			varDeclaration_AST = (AST)currentAST.root;
			varDeclaration_AST = v_AST;
			currentAST.root = varDeclaration_AST;
			currentAST.child = varDeclaration_AST!=null &&varDeclaration_AST.getFirstChild()!=null ?
				varDeclaration_AST.getFirstChild() : varDeclaration_AST;
			currentAST.advanceChildToEnd();
		}
		returnAST = varDeclaration_AST;
	}
	
	public final void connectedPattern() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST connectedPattern_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		
		boolean synPredMatched116 = false;
		if (((LA(1)==LPAREN) && (_tokenSet_20.member(LA(2))))) {
			int _m116 = mark();
			synPredMatched116 = true;
			inputState.guessing++;
			try {
				{
				lparenElist();
				}
			}
			catch (RecognitionException pe) {
				synPredMatched116 = false;
			}
			rewind(_m116);
inputState.guessing--;
		}
		if ( synPredMatched116 ) {
			lp = LT(1);
			lp_AST = astFactory.create(lp);
			astFactory.makeASTRoot(currentAST, lp_AST);
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			setType(lp_AST, APPLICATION_CONDITION);
			astFactory.addASTChild(currentAST, returnAST);
			connectedPattern_AST = (AST)currentAST.root;
		}
		else if ((_tokenSet_25.member(LA(1))) && (_tokenSet_27.member(LA(2)))) {
			{
			int _cnt118=0;
			_loop118:
			do {
				if ((_tokenSet_25.member(LA(1)))) {
					connectedPatternPart();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt118>=1 ) { break _loop118; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt118++;
			} while (true);
			}
			connectedPattern_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = connectedPattern_AST;
	}
	
	public final void connectedPatternPart() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST connectedPatternPart_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		
		switch ( LA(1)) {
		case LBRACK:
		{
			lb = LT(1);
			lb_AST = astFactory.create(lb);
			astFactory.makeASTRoot(currentAST, lb_AST);
			match(LBRACK);
			setType(lb_AST, TREE);
			astFactory.addASTChild(currentAST, returnAST);
			connectedPatternList();
			astFactory.addASTChild(currentAST, returnAST);
			match(RBRACK);
			connectedPatternPart_AST = (AST)currentAST.root;
			break;
		}
		case CONTEXT:
		{
			AST tmp166_AST = null;
			tmp166_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp166_AST);
			match(CONTEXT);
			connectedPatternList();
			astFactory.addASTChild(currentAST, returnAST);
			match(RCONTEXT);
			connectedPatternPart_AST = (AST)currentAST.root;
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case LT:
		case GT:
		case LINE:
		case LEFT_RIGHT_ARROW:
		case PLUS_LEFT_ARROW:
		case PLUS_ARROW:
		case PLUS_LINE:
		case PLUS_LEFT_RIGHT_ARROW:
		case SLASH_LEFT_ARROW:
		case SLASH_ARROW:
		case SLASH_LINE:
		case SLASH_LEFT_RIGHT_ARROW:
		case SUB:
		case LEFT_ARROW:
		case COM:
		case NOT:
		case XOR:
		case THIS:
		case NULL_LITERAL:
		case LONG_LEFT_ARROW:
		case LONG_ARROW:
		case LONG_LEFT_RIGHT_ARROW:
		case QUOTE:
		case DEC:
		case LPAREN:
		{
			labeledPrimaryPatternNoDot();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case LCURLY:
			{
				block();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					connectedPatternPart_AST = (AST)currentAST.root;
					connectedPatternPart_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PATTERN_WITH_BLOCK)).add(connectedPatternPart_AST));
					currentAST.root = connectedPatternPart_AST;
					currentAST.child = connectedPatternPart_AST!=null &&connectedPatternPart_AST.getFirstChild()!=null ?
						connectedPatternPart_AST.getFirstChild() : connectedPatternPart_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case BOOLEAN_LITERAL:
			case INT_LITERAL:
			case LONG_LITERAL:
			case FLOAT_LITERAL:
			case DOUBLE_LITERAL:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case IDENT:
			case SUPER:
			case VOID_:
			case BOOLEAN_:
			case BYTE_:
			case SHORT_:
			case CHAR_:
			case INT_:
			case LONG_:
			case FLOAT_:
			case DOUBLE_:
			case LT:
			case GT:
			case LINE:
			case LEFT_RIGHT_ARROW:
			case PLUS_LEFT_ARROW:
			case PLUS_ARROW:
			case PLUS_LINE:
			case PLUS_LEFT_RIGHT_ARROW:
			case SLASH_LEFT_ARROW:
			case SLASH_ARROW:
			case SLASH_LINE:
			case SLASH_LEFT_RIGHT_ARROW:
			case CONTEXT:
			case SUB:
			case LEFT_ARROW:
			case RULE:
			case DOUBLE_ARROW_RULE:
			case EXEC_RULE:
			case COM:
			case NOT:
			case XOR:
			case THIS:
			case NULL_LITERAL:
			case LONG_LEFT_ARROW:
			case LONG_ARROW:
			case LONG_LEFT_RIGHT_ARROW:
			case QUOTE:
			case DEC:
			case LPAREN:
			case RPAREN:
			case LBRACK:
			case RBRACK:
			case COMMA:
			case RCONTEXT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			connectedPatternPart_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = connectedPatternPart_AST;
	}
	
	public final void labeledPrimaryPatternNoDot() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST labeledPrimaryPatternNoDot_AST = null;
		Token  c = null;
		AST c_AST = null;
		Token  d = null;
		AST d_AST = null;
		
		boolean synPredMatched123 = false;
		if (((LA(1)==IDENT) && (LA(2)==COLON))) {
			int _m123 = mark();
			synPredMatched123 = true;
			inputState.guessing++;
			try {
				{
				match(IDENT);
				match(COLON);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched123 = false;
			}
			rewind(_m123);
inputState.guessing--;
		}
		if ( synPredMatched123 ) {
			AST tmp168_AST = null;
			tmp168_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp168_AST);
			match(IDENT);
			c = LT(1);
			c_AST = astFactory.create(c);
			astFactory.makeASTRoot(currentAST, c_AST);
			match(COLON);
			{
			switch ( LA(1)) {
			case DOT:
			{
				d = LT(1);
				d_AST = astFactory.create(d);
				match(DOT);
				setType(d_AST, ANY);
				astFactory.addASTChild(currentAST, returnAST);
				primaryNodePatternRest(d_AST);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case BOOLEAN_LITERAL:
			case INT_LITERAL:
			case LONG_LITERAL:
			case FLOAT_LITERAL:
			case DOUBLE_LITERAL:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case IDENT:
			case SUPER:
			case VOID_:
			case BOOLEAN_:
			case BYTE_:
			case SHORT_:
			case CHAR_:
			case INT_:
			case LONG_:
			case FLOAT_:
			case DOUBLE_:
			case LT:
			case GT:
			case LINE:
			case LEFT_RIGHT_ARROW:
			case PLUS_LEFT_ARROW:
			case PLUS_ARROW:
			case PLUS_LINE:
			case PLUS_LEFT_RIGHT_ARROW:
			case SLASH_LEFT_ARROW:
			case SLASH_ARROW:
			case SLASH_LINE:
			case SLASH_LEFT_RIGHT_ARROW:
			case SUB:
			case LEFT_ARROW:
			case COM:
			case NOT:
			case XOR:
			case THIS:
			case NULL_LITERAL:
			case LONG_LEFT_ARROW:
			case LONG_ARROW:
			case LONG_LEFT_RIGHT_ARROW:
			case QUOTE:
			case DEC:
			case LPAREN:
			{
				primaryPattern();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			setType(c_AST, LABEL);
			astFactory.addASTChild(currentAST, returnAST);
			labeledPrimaryPatternNoDot_AST = (AST)currentAST.root;
		}
		else if ((_tokenSet_28.member(LA(1))) && (_tokenSet_27.member(LA(2)))) {
			primaryPattern();
			astFactory.addASTChild(currentAST, returnAST);
			labeledPrimaryPatternNoDot_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = labeledPrimaryPatternNoDot_AST;
	}
	
	public final void primaryNodePatternRest(
		AST first
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryNodePatternRest_AST = null;
		Token  f = null;
		AST f_AST = null;
		
		insert(first);
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case AND:
		{
			AST tmp169_AST = null;
			tmp169_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp169_AST);
			match(AND);
			intersectionPattern();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop130:
			do {
				if ((LA(1)==AND)) {
					match(AND);
					intersectionPattern();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop130;
				}
				
			} while (true);
			}
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case LT:
		case GT:
		case LINE:
		case LEFT_RIGHT_ARROW:
		case PLUS_LEFT_ARROW:
		case PLUS_ARROW:
		case PLUS_LINE:
		case PLUS_LEFT_RIGHT_ARROW:
		case SLASH_LEFT_ARROW:
		case SLASH_ARROW:
		case SLASH_LINE:
		case SLASH_LEFT_RIGHT_ARROW:
		case CONTEXT:
		case SUB:
		case LEFT_ARROW:
		case RULE:
		case DOUBLE_ARROW_RULE:
		case EXEC_RULE:
		case COM:
		case NOT:
		case OR:
		case XOR:
		case THIS:
		case NULL_LITERAL:
		case LONG_LEFT_ARROW:
		case LONG_ARROW:
		case LONG_LEFT_RIGHT_ARROW:
		case QUOTE:
		case DEC:
		case LPAREN:
		case RPAREN:
		case LBRACK:
		case RBRACK:
		case LCURLY:
		case COMMA:
		case RCONTEXT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case OR:
		{
			f = LT(1);
			f_AST = astFactory.create(f);
			astFactory.makeASTRoot(currentAST, f_AST);
			match(OR);
			AST tmp171_AST = null;
			tmp171_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp171_AST);
			match(IDENT);
			setType(f_AST, FOLDING);
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop133:
			do {
				if ((LA(1)==OR)) {
					match(OR);
					AST tmp173_AST = null;
					tmp173_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp173_AST);
					match(IDENT);
				}
				else {
					break _loop133;
				}
				
			} while (true);
			}
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case LT:
		case GT:
		case LINE:
		case LEFT_RIGHT_ARROW:
		case PLUS_LEFT_ARROW:
		case PLUS_ARROW:
		case PLUS_LINE:
		case PLUS_LEFT_RIGHT_ARROW:
		case SLASH_LEFT_ARROW:
		case SLASH_ARROW:
		case SLASH_LINE:
		case SLASH_LEFT_RIGHT_ARROW:
		case CONTEXT:
		case SUB:
		case LEFT_ARROW:
		case RULE:
		case DOUBLE_ARROW_RULE:
		case EXEC_RULE:
		case COM:
		case NOT:
		case XOR:
		case THIS:
		case NULL_LITERAL:
		case LONG_LEFT_ARROW:
		case LONG_ARROW:
		case LONG_LEFT_RIGHT_ARROW:
		case QUOTE:
		case DEC:
		case LPAREN:
		case RPAREN:
		case LBRACK:
		case RBRACK:
		case LCURLY:
		case COMMA:
		case RCONTEXT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		primaryNodePatternRest_AST = (AST)currentAST.root;
		returnAST = primaryNodePatternRest_AST;
	}
	
	public final void primaryPattern() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryPattern_AST = null;
		AST pnp_AST = null;
		
		switch ( LA(1)) {
		case LT:
		case GT:
		case LINE:
		case LEFT_RIGHT_ARROW:
		case PLUS_LEFT_ARROW:
		case PLUS_ARROW:
		case PLUS_LINE:
		case PLUS_LEFT_RIGHT_ARROW:
		case SLASH_LEFT_ARROW:
		case SLASH_ARROW:
		case SLASH_LINE:
		case SLASH_LEFT_RIGHT_ARROW:
		case SUB:
		case LEFT_ARROW:
		case LONG_LEFT_ARROW:
		case LONG_ARROW:
		case LONG_LEFT_RIGHT_ARROW:
		case DEC:
		case LPAREN:
		{
			primaryEdgePattern();
			astFactory.addASTChild(currentAST, returnAST);
			primaryPattern_AST = (AST)currentAST.root;
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case COM:
		case NOT:
		case XOR:
		case THIS:
		case NULL_LITERAL:
		case QUOTE:
		{
			primaryNodePattern();
			pnp_AST = (AST)returnAST;
			primaryNodePatternRest(pnp_AST);
			astFactory.addASTChild(currentAST, returnAST);
			primaryPattern_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = primaryPattern_AST;
	}
	
	public final void traversalModifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST traversalModifier_AST = null;
		
		switch ( LA(1)) {
		case QUESTION:
		{
			AST tmp174_AST = null;
			tmp174_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp174_AST);
			match(QUESTION);
			traversalModifier_AST = (AST)currentAST.root;
			break;
		}
		case MUL:
		{
			AST tmp175_AST = null;
			tmp175_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp175_AST);
			match(MUL);
			traversalModifier_AST = (AST)currentAST.root;
			break;
		}
		case ADD:
		{
			AST tmp176_AST = null;
			tmp176_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp176_AST);
			match(ADD);
			traversalModifier_AST = (AST)currentAST.root;
			break;
		}
		case LCURLY:
		{
			closureRange();
			astFactory.addASTChild(currentAST, returnAST);
			traversalModifier_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = traversalModifier_AST;
	}
	
	public final void primaryEdgePattern() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryEdgePattern_AST = null;
		Token  cl = null;
		AST cl_AST = null;
		Token  co1 = null;
		AST co1_AST = null;
		Token  co2 = null;
		AST co2_AST = null;
		Token  co = null;
		AST co_AST = null;
		Token  a = null;
		AST a_AST = null;
		Token  la = null;
		AST la_AST = null;
		Token  ra = null;
		AST ra_AST = null;
		Token  lra = null;
		AST lra_AST = null;
		Token  l = null;
		AST l_AST = null;
		
		switch ( LA(1)) {
		case LPAREN:
		{
			cl = LT(1);
			cl_AST = astFactory.create(cl);
			astFactory.makeASTRoot(currentAST, cl_AST);
			match(LPAREN);
			{
			switch ( LA(1)) {
			case QUESTION:
			{
				match(QUESTION);
				{
				switch ( LA(1)) {
				case COLON:
				{
					co1 = LT(1);
					co1_AST = astFactory.create(co1);
					match(COLON);
					break;
				}
				case BOOLEAN_LITERAL:
				case INT_LITERAL:
				case LONG_LITERAL:
				case FLOAT_LITERAL:
				case DOUBLE_LITERAL:
				case CHAR_LITERAL:
				case STRING_LITERAL:
				case IDENT:
				case SUPER:
				case VOID_:
				case BOOLEAN_:
				case BYTE_:
				case SHORT_:
				case CHAR_:
				case INT_:
				case LONG_:
				case FLOAT_:
				case DOUBLE_:
				case LT:
				case GT:
				case LINE:
				case LEFT_RIGHT_ARROW:
				case PLUS_LEFT_ARROW:
				case PLUS_ARROW:
				case PLUS_LINE:
				case PLUS_LEFT_RIGHT_ARROW:
				case SLASH_LEFT_ARROW:
				case SLASH_ARROW:
				case SLASH_LINE:
				case SLASH_LEFT_RIGHT_ARROW:
				case CONTEXT:
				case SUB:
				case LEFT_ARROW:
				case COM:
				case NOT:
				case XOR:
				case THIS:
				case SYNCHRONIZED_:
				case NULL_LITERAL:
				case LONG_LEFT_ARROW:
				case LONG_ARROW:
				case LONG_LEFT_RIGHT_ARROW:
				case QUOTE:
				case DEC:
				case ANNOTATION:
				case PRIVATE_:
				case PUBLIC_:
				case PROTECTED_:
				case STATIC_:
				case TRANSIENT_:
				case FINAL_:
				case ABSTRACT_:
				case NATIVE_:
				case VOLATILE_:
				case STRICT_:
				case CONST_:
				case LPAREN:
				case LBRACK:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				compoundPattern();
				astFactory.addASTChild(currentAST, returnAST);
				match(RPAREN);
				setType(cl_AST, (co1_AST != null) ? SINGLE_OPTIONAL_MATCH : OPTIONAL_MATCH);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case COLON:
			{
				match(COLON);
				{
				switch ( LA(1)) {
				case QUESTION:
				{
					co2 = LT(1);
					co2_AST = astFactory.create(co2);
					match(QUESTION);
					break;
				}
				case BOOLEAN_LITERAL:
				case INT_LITERAL:
				case LONG_LITERAL:
				case FLOAT_LITERAL:
				case DOUBLE_LITERAL:
				case CHAR_LITERAL:
				case STRING_LITERAL:
				case IDENT:
				case SUPER:
				case VOID_:
				case BOOLEAN_:
				case BYTE_:
				case SHORT_:
				case CHAR_:
				case INT_:
				case LONG_:
				case FLOAT_:
				case DOUBLE_:
				case LT:
				case GT:
				case LINE:
				case LEFT_RIGHT_ARROW:
				case PLUS_LEFT_ARROW:
				case PLUS_ARROW:
				case PLUS_LINE:
				case PLUS_LEFT_RIGHT_ARROW:
				case SLASH_LEFT_ARROW:
				case SLASH_ARROW:
				case SLASH_LINE:
				case SLASH_LEFT_RIGHT_ARROW:
				case CONTEXT:
				case SUB:
				case LEFT_ARROW:
				case COM:
				case NOT:
				case XOR:
				case THIS:
				case SYNCHRONIZED_:
				case NULL_LITERAL:
				case LONG_LEFT_ARROW:
				case LONG_ARROW:
				case LONG_LEFT_RIGHT_ARROW:
				case QUOTE:
				case DEC:
				case ANNOTATION:
				case PRIVATE_:
				case PUBLIC_:
				case PROTECTED_:
				case STATIC_:
				case TRANSIENT_:
				case FINAL_:
				case ABSTRACT_:
				case NATIVE_:
				case VOLATILE_:
				case STRICT_:
				case CONST_:
				case LPAREN:
				case LBRACK:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				compoundPattern();
				astFactory.addASTChild(currentAST, returnAST);
				match(RPAREN);
				setType(cl_AST, (co2_AST != null) ? SINGLE_OPTIONAL_MATCH : SINGLE_MATCH);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case AND:
			{
				match(AND);
				compoundPattern();
				astFactory.addASTChild(currentAST, returnAST);
				match(RPAREN);
				setType(cl_AST, LATE_MATCH);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case BOOLEAN_LITERAL:
			case INT_LITERAL:
			case LONG_LITERAL:
			case FLOAT_LITERAL:
			case DOUBLE_LITERAL:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case IDENT:
			case SUPER:
			case VOID_:
			case BOOLEAN_:
			case BYTE_:
			case SHORT_:
			case CHAR_:
			case INT_:
			case LONG_:
			case FLOAT_:
			case DOUBLE_:
			case LT:
			case GT:
			case LINE:
			case LEFT_RIGHT_ARROW:
			case PLUS_LEFT_ARROW:
			case PLUS_ARROW:
			case PLUS_LINE:
			case PLUS_LEFT_RIGHT_ARROW:
			case SLASH_LEFT_ARROW:
			case SLASH_ARROW:
			case SLASH_LINE:
			case SLASH_LEFT_RIGHT_ARROW:
			case CONTEXT:
			case SUB:
			case LEFT_ARROW:
			case COM:
			case NOT:
			case XOR:
			case THIS:
			case SYNCHRONIZED_:
			case NULL_LITERAL:
			case LONG_LEFT_ARROW:
			case LONG_ARROW:
			case LONG_LEFT_RIGHT_ARROW:
			case QUOTE:
			case DEC:
			case ANNOTATION:
			case PRIVATE_:
			case PUBLIC_:
			case PROTECTED_:
			case STATIC_:
			case TRANSIENT_:
			case FINAL_:
			case ABSTRACT_:
			case NATIVE_:
			case VOLATILE_:
			case STRICT_:
			case CONST_:
			case LPAREN:
			case LBRACK:
			{
				compoundPattern();
				astFactory.addASTChild(currentAST, returnAST);
				match(RPAREN);
				setType(cl_AST, TRAVERSAL);
				astFactory.addASTChild(currentAST, returnAST);
				traversalModifier();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case COLON:
				{
					co = LT(1);
					co_AST = astFactory.create(co);
					astFactory.makeASTRoot(currentAST, co_AST);
					match(COLON);
					setType(co_AST, MINIMAL);
					astFactory.addASTChild(currentAST, returnAST);
					match(LPAREN);
					compoundPattern();
					astFactory.addASTChild(currentAST, returnAST);
					match(RPAREN);
					break;
				}
				case BOOLEAN_LITERAL:
				case INT_LITERAL:
				case LONG_LITERAL:
				case FLOAT_LITERAL:
				case DOUBLE_LITERAL:
				case CHAR_LITERAL:
				case STRING_LITERAL:
				case IDENT:
				case SUPER:
				case VOID_:
				case BOOLEAN_:
				case BYTE_:
				case SHORT_:
				case CHAR_:
				case INT_:
				case LONG_:
				case FLOAT_:
				case DOUBLE_:
				case LT:
				case GT:
				case LINE:
				case LEFT_RIGHT_ARROW:
				case PLUS_LEFT_ARROW:
				case PLUS_ARROW:
				case PLUS_LINE:
				case PLUS_LEFT_RIGHT_ARROW:
				case SLASH_LEFT_ARROW:
				case SLASH_ARROW:
				case SLASH_LINE:
				case SLASH_LEFT_RIGHT_ARROW:
				case CONTEXT:
				case SUB:
				case LEFT_ARROW:
				case RULE:
				case DOUBLE_ARROW_RULE:
				case EXEC_RULE:
				case COM:
				case NOT:
				case XOR:
				case THIS:
				case NULL_LITERAL:
				case LONG_LEFT_ARROW:
				case LONG_ARROW:
				case LONG_LEFT_RIGHT_ARROW:
				case QUOTE:
				case DEC:
				case LPAREN:
				case RPAREN:
				case LBRACK:
				case RBRACK:
				case LCURLY:
				case COMMA:
				case RCONTEXT:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case LT:
		{
			AST tmp186_AST = null;
			tmp186_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp186_AST);
			match(LT);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case GT:
		{
			AST tmp187_AST = null;
			tmp187_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp187_AST);
			match(GT);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case LINE:
		{
			AST tmp188_AST = null;
			tmp188_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp188_AST);
			match(LINE);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case LEFT_RIGHT_ARROW:
		{
			AST tmp189_AST = null;
			tmp189_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp189_AST);
			match(LEFT_RIGHT_ARROW);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case PLUS_LEFT_ARROW:
		{
			AST tmp190_AST = null;
			tmp190_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp190_AST);
			match(PLUS_LEFT_ARROW);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case PLUS_ARROW:
		{
			AST tmp191_AST = null;
			tmp191_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp191_AST);
			match(PLUS_ARROW);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case PLUS_LINE:
		{
			AST tmp192_AST = null;
			tmp192_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp192_AST);
			match(PLUS_LINE);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case PLUS_LEFT_RIGHT_ARROW:
		{
			AST tmp193_AST = null;
			tmp193_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp193_AST);
			match(PLUS_LEFT_RIGHT_ARROW);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case SLASH_LEFT_ARROW:
		{
			AST tmp194_AST = null;
			tmp194_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp194_AST);
			match(SLASH_LEFT_ARROW);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case SLASH_ARROW:
		{
			AST tmp195_AST = null;
			tmp195_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp195_AST);
			match(SLASH_ARROW);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case SLASH_LINE:
		{
			AST tmp196_AST = null;
			tmp196_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp196_AST);
			match(SLASH_LINE);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case SLASH_LEFT_RIGHT_ARROW:
		{
			AST tmp197_AST = null;
			tmp197_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp197_AST);
			match(SLASH_LEFT_RIGHT_ARROW);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case DEC:
		{
			a = LT(1);
			a_AST = astFactory.create(a);
			astFactory.makeASTRoot(currentAST, a_AST);
			match(DEC);
			setType(a_AST, SUB);
			astFactory.addASTChild(currentAST, returnAST);
			insert(astFactory.create(ANY));
			astFactory.addASTChild(currentAST, returnAST);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case LONG_LEFT_ARROW:
		{
			la = LT(1);
			la_AST = astFactory.create(la);
			astFactory.makeASTRoot(currentAST, la_AST);
			match(LONG_LEFT_ARROW);
			setType(la_AST, LEFT_ARROW);
			astFactory.addASTChild(currentAST, returnAST);
			insert(astFactory.create(ANY));
			astFactory.addASTChild(currentAST, returnAST);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case LONG_ARROW:
		{
			ra = LT(1);
			ra_AST = astFactory.create(ra);
			astFactory.makeASTRoot(currentAST, ra_AST);
			match(LONG_ARROW);
			setType(ra_AST, ARROW);
			astFactory.addASTChild(currentAST, returnAST);
			insert(astFactory.create(ANY));
			astFactory.addASTChild(currentAST, returnAST);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case LONG_LEFT_RIGHT_ARROW:
		{
			lra = LT(1);
			lra_AST = astFactory.create(lra);
			astFactory.makeASTRoot(currentAST, lra_AST);
			match(LONG_LEFT_RIGHT_ARROW);
			setType(lra_AST, X_LEFT_RIGHT_ARROW);
			astFactory.addASTChild(currentAST, returnAST);
			insert(astFactory.create(ANY));
			astFactory.addASTChild(currentAST, returnAST);
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case LEFT_ARROW:
		{
			l = LT(1);
			l_AST = astFactory.create(l);
			astFactory.makeASTRoot(currentAST, l_AST);
			match(LEFT_ARROW);
			selectorExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case ARROW:
			{
				match(ARROW);
				setType(l_AST, X_LEFT_RIGHT_ARROW);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SUB:
			{
				match(SUB);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		case SUB:
		{
			match(SUB);
			selectorExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case ARROW:
			{
				AST tmp201_AST = null;
				tmp201_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp201_AST);
				match(ARROW);
				break;
			}
			case SUB:
			{
				AST tmp202_AST = null;
				tmp202_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp202_AST);
				match(SUB);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			primaryEdgePattern_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = primaryEdgePattern_AST;
	}
	
	public final void primaryNodePattern() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryNodePattern_AST = null;
		AST t2_AST = null;
		AST a2_AST = null;
		AST u_AST = null;
		AST id_AST = null;
		Token  l = null;
		AST l_AST = null;
		AST r_AST = null;
		AST t1_AST = null;
		AST a1_AST = null;
		AST n_AST = null;
		AST r2_AST = null;
		Token  b = null;
		AST b_AST = null;
		
				boolean oa;
			
		
		switch ( LA(1)) {
		case COM:
		case NOT:
		{
			unaryOpNode();
			u_AST = (AST)returnAST;
			insertTree(EXPR, u_AST);
			astFactory.addASTChild(currentAST, returnAST);
			primaryNodePattern_AST = (AST)currentAST.root;
			break;
		}
		case XOR:
		{
			b = LT(1);
			b_AST = astFactory.create(b);
			astFactory.addASTChild(currentAST, b_AST);
			match(XOR);
			setType(b_AST, ROOT);
			astFactory.addASTChild(currentAST, returnAST);
			primaryNodePattern_AST = (AST)currentAST.root;
			break;
		}
		default:
			boolean synPredMatched137 = false;
			if (((_tokenSet_4.member(LA(1))) && (_tokenSet_29.member(LA(2))))) {
				int _m137 = mark();
				synPredMatched137 = true;
				inputState.guessing++;
				try {
					{
					builtInType();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched137 = false;
				}
				rewind(_m137);
inputState.guessing--;
			}
			if ( synPredMatched137 ) {
				typeSpec();
				t2_AST = (AST)returnAST;
				typeArg();
				a2_AST = (AST)returnAST;
				insertTree2((a2_AST != null) ? WRAPPED_TYPE_PATTERN : TYPE_PATTERN, t2_AST, a2_AST);
				astFactory.addASTChild(currentAST, returnAST);
				primaryNodePattern_AST = (AST)currentAST.root;
			}
			else {
				boolean synPredMatched139 = false;
				if (((LA(1)==IDENT) && (_tokenSet_29.member(LA(2))))) {
					int _m139 = mark();
					synPredMatched139 = true;
					inputState.guessing++;
					try {
						{
						name();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched139 = false;
					}
					rewind(_m139);
inputState.guessing--;
				}
				if ( synPredMatched139 ) {
					name();
					id_AST = (AST)returnAST;
					{
					boolean synPredMatched142 = false;
					if (((LA(1)==LPAREN) && (_tokenSet_15.member(LA(2))))) {
						int _m142 = mark();
						synPredMatched142 = true;
						inputState.guessing++;
						try {
							{
							lparenArgs();
							}
						}
						catch (RecognitionException pe) {
							synPredMatched142 = false;
						}
						rewind(_m142);
inputState.guessing--;
					}
					if ( synPredMatched142 ) {
						l = LT(1);
						l_AST = astFactory.create(l);
						astFactory.makeASTRoot(currentAST, l_AST);
						match(LPAREN);
						insert(id_AST);
						astFactory.addASTChild(currentAST, returnAST);
						oa=argList(true);
						astFactory.addASTChild(currentAST, returnAST);
						match(RPAREN);
						{
						if (((LA(1)==DOT))&&(!oa)) {
							patternSelectorRest(l_AST);
							r_AST = (AST)returnAST;
						}
						else if ((_tokenSet_30.member(LA(1)))) {
						}
						else {
							throw new NoViableAltException(LT(1), getFilename());
						}
						
						}
						if ( inputState.guessing==0 ) {
							primaryNodePattern_AST = (AST)currentAST.root;
							
											if (oa || (r_AST == null))
											{
												l_AST.setType (PARAMETERIZED_PATTERN);
											}
											else
											{
												l_AST.setType (METHOD_CALL);
												primaryNodePattern_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(METHOD_PATTERN)).add(r_AST));
											}
										
							currentAST.root = primaryNodePattern_AST;
							currentAST.child = primaryNodePattern_AST!=null &&primaryNodePattern_AST.getFirstChild()!=null ?
								primaryNodePattern_AST.getFirstChild() : primaryNodePattern_AST;
							currentAST.advanceChildToEnd();
						}
					}
					else {
						boolean synPredMatched145 = false;
						if (((_tokenSet_30.member(LA(1))) && (_tokenSet_31.member(LA(2))))) {
							int _m145 = mark();
							synPredMatched145 = true;
							inputState.guessing++;
							try {
								{
								match(LBRACK);
								match(RBRACK);
								}
							}
							catch (RecognitionException pe) {
								synPredMatched145 = false;
							}
							rewind(_m145);
inputState.guessing--;
						}
						if ( synPredMatched145 ) {
							typeSpecRest(id_AST);
							t1_AST = (AST)returnAST;
							typeArg();
							a1_AST = (AST)returnAST;
							insertTree2((a1_AST != null) ? WRAPPED_TYPE_PATTERN : TYPE_PATTERN, t1_AST, a1_AST);
							astFactory.addASTChild(currentAST, returnAST);
						}
						else if ((_tokenSet_30.member(LA(1))) && (_tokenSet_31.member(LA(2)))) {
							insertTree(NAME_PATTERN, id_AST);
							astFactory.addASTChild(currentAST, returnAST);
						}
						else {
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						primaryNodePattern_AST = (AST)currentAST.root;
					}
					else if ((_tokenSet_32.member(LA(1))) && (_tokenSet_33.member(LA(2)))) {
						primaryNoParen(true);
						n_AST = (AST)returnAST;
						{
						switch ( LA(1)) {
						case DOT:
						{
							patternSelectorRest(n_AST);
							r2_AST = (AST)returnAST;
							insertTree(METHOD_PATTERN, r2_AST);
							astFactory.addASTChild(currentAST, returnAST);
							break;
						}
						case BOOLEAN_LITERAL:
						case INT_LITERAL:
						case LONG_LITERAL:
						case FLOAT_LITERAL:
						case DOUBLE_LITERAL:
						case CHAR_LITERAL:
						case STRING_LITERAL:
						case IDENT:
						case SUPER:
						case VOID_:
						case BOOLEAN_:
						case BYTE_:
						case SHORT_:
						case CHAR_:
						case INT_:
						case LONG_:
						case FLOAT_:
						case DOUBLE_:
						case LT:
						case GT:
						case LINE:
						case LEFT_RIGHT_ARROW:
						case PLUS_LEFT_ARROW:
						case PLUS_ARROW:
						case PLUS_LINE:
						case PLUS_LEFT_RIGHT_ARROW:
						case SLASH_LEFT_ARROW:
						case SLASH_ARROW:
						case SLASH_LINE:
						case SLASH_LEFT_RIGHT_ARROW:
						case CONTEXT:
						case SUB:
						case LEFT_ARROW:
						case RULE:
						case DOUBLE_ARROW_RULE:
						case EXEC_RULE:
						case COM:
						case NOT:
						case OR:
						case XOR:
						case AND:
						case THIS:
						case NULL_LITERAL:
						case LONG_LEFT_ARROW:
						case LONG_ARROW:
						case LONG_LEFT_RIGHT_ARROW:
						case QUOTE:
						case DEC:
						case LPAREN:
						case RPAREN:
						case LBRACK:
						case RBRACK:
						case LCURLY:
						case COMMA:
						case RCONTEXT:
						{
							insertTree(EXPR, n_AST);
							astFactory.addASTChild(currentAST, returnAST);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						primaryNodePattern_AST = (AST)currentAST.root;
					}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}}
				returnAST = primaryNodePattern_AST;
			}
			
	public final void intersectionPattern() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST intersectionPattern_AST = null;
		Token  l = null;
		AST l_AST = null;
		
		switch ( LA(1)) {
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case COM:
		case NOT:
		case XOR:
		case THIS:
		case NULL_LITERAL:
		case QUOTE:
		{
			primaryNodePattern();
			astFactory.addASTChild(currentAST, returnAST);
			intersectionPattern_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			l = LT(1);
			l_AST = astFactory.create(l);
			astFactory.makeASTRoot(currentAST, l_AST);
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			setType(l_AST, APPLICATION_CONDITION);
			astFactory.addASTChild(currentAST, returnAST);
			intersectionPattern_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = intersectionPattern_AST;
	}
	
	public final void builtInType() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST builtInType_AST = null;
		
		switch ( LA(1)) {
		case BOOLEAN_:
		{
			AST tmp205_AST = null;
			tmp205_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp205_AST);
			match(BOOLEAN_);
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case BYTE_:
		{
			AST tmp206_AST = null;
			tmp206_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp206_AST);
			match(BYTE_);
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case SHORT_:
		{
			AST tmp207_AST = null;
			tmp207_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp207_AST);
			match(SHORT_);
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case CHAR_:
		{
			AST tmp208_AST = null;
			tmp208_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp208_AST);
			match(CHAR_);
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case INT_:
		{
			AST tmp209_AST = null;
			tmp209_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp209_AST);
			match(INT_);
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case LONG_:
		{
			AST tmp210_AST = null;
			tmp210_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp210_AST);
			match(LONG_);
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case FLOAT_:
		{
			AST tmp211_AST = null;
			tmp211_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp211_AST);
			match(FLOAT_);
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		case DOUBLE_:
		{
			AST tmp212_AST = null;
			tmp212_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp212_AST);
			match(DOUBLE_);
			builtInType_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = builtInType_AST;
	}
	
	public final void typeArg() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeArg_AST = null;
		
		boolean synPredMatched159 = false;
		if (((LA(1)==LPAREN) && (_tokenSet_10.member(LA(2))))) {
			int _m159 = mark();
			synPredMatched159 = true;
			inputState.guessing++;
			try {
				{
				lparenArgs();
				}
			}
			catch (RecognitionException pe) {
				synPredMatched159 = false;
			}
			rewind(_m159);
inputState.guessing--;
		}
		if ( synPredMatched159 ) {
			match(LPAREN);
			singleExpression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			typeArg_AST = (AST)currentAST.root;
		}
		else if ((_tokenSet_30.member(LA(1))) && (_tokenSet_31.member(LA(2)))) {
			typeArg_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = typeArg_AST;
	}
	
	public final void insertTree2(
		int rootType, AST list1, AST list2
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST insertTree2_AST = null;
		insertTree2_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(rootType)).add(list1).add(list2));
		
		returnAST = insertTree2_AST;
	}
	
	public final void unaryOpNode() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryOpNode_AST = null;
		
		{
		switch ( LA(1)) {
		case COM:
		{
			AST tmp215_AST = null;
			tmp215_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp215_AST);
			match(COM);
			break;
		}
		case NOT:
		{
			AST tmp216_AST = null;
			tmp216_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp216_AST);
			match(NOT);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		primaryNoCreator();
		astFactory.addASTChild(currentAST, returnAST);
		unaryOpNode_AST = (AST)currentAST.root;
		returnAST = unaryOpNode_AST;
	}
	
	public final void insertTree(
		int rootType, AST list
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST insertTree_AST = null;
		insertTree_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(rootType)).add(list));
		
		returnAST = insertTree_AST;
	}
	
	public final void patternSelectorRest(
		AST p
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST patternSelectorRest_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		
		insert(p);
		astFactory.addASTChild(currentAST, returnAST);
		{
		int _cnt156=0;
		_loop156:
		do {
			if ((LA(1)==DOT)) {
				AST tmp217_AST = null;
				tmp217_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp217_AST);
				match(DOT);
				AST tmp218_AST = null;
				tmp218_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp218_AST);
				match(IDENT);
				lp = LT(1);
				lp_AST = astFactory.create(lp);
				astFactory.makeASTRoot(currentAST, lp_AST);
				match(LPAREN);
				argList(false);
				astFactory.addASTChild(currentAST, returnAST);
				match(RPAREN);
				setType(lp_AST, METHOD_CALL);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				if ( _cnt156>=1 ) { break _loop156; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt156++;
		} while (true);
		}
		patternSelectorRest_AST = (AST)currentAST.root;
		returnAST = patternSelectorRest_AST;
	}
	
	public final void typeSpecRest(
		AST t
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeSpecRest_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		
		insert(t);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop412:
		do {
			if ((LA(1)==LBRACK) && (LA(2)==RBRACK)) {
				lb = LT(1);
				lb_AST = astFactory.create(lb);
				astFactory.makeASTRoot(currentAST, lb_AST);
				match(LBRACK);
				setArrayDeclarator_RBRACK(lb_AST);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop412;
			}
			
		} while (true);
		}
		typeSpecRest_AST = (AST)currentAST.root;
		returnAST = typeSpecRest_AST;
	}
	
	public final void primaryNoParen(
		boolean checkClosure
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryNoParen_AST = null;
		Token  id = null;
		AST id_AST = null;
		Token  id2 = null;
		AST id2_AST = null;
		Token  c0 = null;
		AST c0_AST = null;
		Token  lb1 = null;
		AST lb1_AST = null;
		Token  c1 = null;
		AST c1_AST = null;
		Token  lp1 = null;
		AST lp1_AST = null;
		Token  lp2 = null;
		AST lp2_AST = null;
		Token  s1 = null;
		AST s1_AST = null;
		Token  s2 = null;
		AST s2_AST = null;
		Token  lb2 = null;
		AST lb2_AST = null;
		Token  c2 = null;
		AST c2_AST = null;
		Token  c3 = null;
		AST c3_AST = null;
		
		switch ( LA(1)) {
		case IDENT:
		{
			id = LT(1);
			id_AST = astFactory.create(id);
			astFactory.addASTChild(currentAST, id_AST);
			match(IDENT);
			{
			_loop362:
			do {
				if ((LA(1)==DOT) && (LA(2)==IDENT)) {
					AST tmp220_AST = null;
					tmp220_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp220_AST);
					match(DOT);
					id2 = LT(1);
					id2_AST = astFactory.create(id2);
					astFactory.addASTChild(currentAST, id2_AST);
					match(IDENT);
				}
				else {
					break _loop362;
				}
				
			} while (true);
			}
			{
			if ((LA(1)==DOT) && (LA(2)==THIS)) {
				match(DOT);
				AST tmp222_AST = null;
				tmp222_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp222_AST);
				match(THIS);
			}
			else if ((LA(1)==DOT) && (LA(2)==CLASS)) {
				match(DOT);
				c0 = LT(1);
				c0_AST = astFactory.create(c0);
				astFactory.makeASTRoot(currentAST, c0_AST);
				match(CLASS);
				setType(c0_AST, CLASS_LITERAL);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				boolean synPredMatched365 = false;
				if (((LA(1)==DOT) && (LA(2)==SUPER))) {
					int _m365 = mark();
					synPredMatched365 = true;
					inputState.guessing++;
					try {
						{
						match(DOT);
						match(SUPER);
						matchNot(LPAREN);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched365 = false;
					}
					rewind(_m365);
inputState.guessing--;
				}
				if ( synPredMatched365 ) {
					match(DOT);
					AST tmp225_AST = null;
					tmp225_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp225_AST);
					match(SUPER);
				}
				else if ((LA(1)==LBRACK) && (LA(2)==RBRACK)) {
					{
					int _cnt367=0;
					_loop367:
					do {
						if ((LA(1)==LBRACK)) {
							lb1 = LT(1);
							lb1_AST = astFactory.create(lb1);
							astFactory.makeASTRoot(currentAST, lb1_AST);
							match(LBRACK);
							setArrayDeclarator_RBRACK(lb1_AST);
							astFactory.addASTChild(currentAST, returnAST);
						}
						else {
							if ( _cnt367>=1 ) { break _loop367; } else {throw new NoViableAltException(LT(1), getFilename());}
						}
						
						_cnt367++;
					} while (true);
					}
					match(DOT);
					c1 = LT(1);
					c1_AST = astFactory.create(c1);
					astFactory.makeASTRoot(currentAST, c1_AST);
					match(CLASS);
					setType(c1_AST, CLASS_LITERAL);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else if (((LA(1)==LPAREN) && (_tokenSet_15.member(LA(2))))&&(!checkClosure)) {
					lp1 = LT(1);
					lp1_AST = astFactory.create(lp1);
					astFactory.makeASTRoot(currentAST, lp1_AST);
					match(LPAREN);
					argList(false);
					astFactory.addASTChild(currentAST, returnAST);
					match(RPAREN);
					setType(lp1_AST, METHOD_CALL);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					boolean synPredMatched369 = false;
					if (((LA(1)==LPAREN) && (_tokenSet_15.member(LA(2))))) {
						int _m369 = mark();
						synPredMatched369 = true;
						inputState.guessing++;
						try {
							{
							lparenArgs();
							}
						}
						catch (RecognitionException pe) {
							synPredMatched369 = false;
						}
						rewind(_m369);
inputState.guessing--;
					}
					if ( synPredMatched369 ) {
						lp2 = LT(1);
						lp2_AST = astFactory.create(lp2);
						astFactory.makeASTRoot(currentAST, lp2_AST);
						match(LPAREN);
						argList(false);
						astFactory.addASTChild(currentAST, returnAST);
						match(RPAREN);
						setType(lp2_AST, METHOD_CALL);
						astFactory.addASTChild(currentAST, returnAST);
					}
					else if ((_tokenSet_34.member(LA(1))) && (_tokenSet_14.member(LA(2)))) {
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					}}
					}
					primaryNoParen_AST = (AST)currentAST.root;
					break;
				}
				case THIS:
				{
					AST tmp229_AST = null;
					tmp229_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp229_AST);
					match(THIS);
					primaryNoParen_AST = (AST)currentAST.root;
					break;
				}
				case SUPER:
				{
					AST tmp230_AST = null;
					tmp230_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp230_AST);
					match(SUPER);
					AST tmp231_AST = null;
					tmp231_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp231_AST);
					match(DOT);
					AST tmp232_AST = null;
					tmp232_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp232_AST);
					match(IDENT);
					{
					if (((LA(1)==LPAREN) && (_tokenSet_15.member(LA(2))))&&(!checkClosure)) {
						s1 = LT(1);
						s1_AST = astFactory.create(s1);
						astFactory.makeASTRoot(currentAST, s1_AST);
						match(LPAREN);
						argList(false);
						astFactory.addASTChild(currentAST, returnAST);
						match(RPAREN);
						setType(s1_AST, METHOD_CALL);
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						boolean synPredMatched372 = false;
						if (((LA(1)==LPAREN) && (_tokenSet_15.member(LA(2))))) {
							int _m372 = mark();
							synPredMatched372 = true;
							inputState.guessing++;
							try {
								{
								lparenArgs();
								}
							}
							catch (RecognitionException pe) {
								synPredMatched372 = false;
							}
							rewind(_m372);
inputState.guessing--;
						}
						if ( synPredMatched372 ) {
							s2 = LT(1);
							s2_AST = astFactory.create(s2);
							astFactory.makeASTRoot(currentAST, s2_AST);
							match(LPAREN);
							argList(false);
							astFactory.addASTChild(currentAST, returnAST);
							match(RPAREN);
							setType(s2_AST, METHOD_CALL);
							astFactory.addASTChild(currentAST, returnAST);
						}
						else if ((_tokenSet_34.member(LA(1))) && (_tokenSet_14.member(LA(2)))) {
						}
						else {
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						primaryNoParen_AST = (AST)currentAST.root;
						break;
					}
					case BOOLEAN_LITERAL:
					case INT_LITERAL:
					case LONG_LITERAL:
					case FLOAT_LITERAL:
					case DOUBLE_LITERAL:
					case CHAR_LITERAL:
					case STRING_LITERAL:
					case NULL_LITERAL:
					{
						literal();
						astFactory.addASTChild(currentAST, returnAST);
						primaryNoParen_AST = (AST)currentAST.root;
						break;
					}
					case BOOLEAN_:
					case BYTE_:
					case SHORT_:
					case CHAR_:
					case INT_:
					case LONG_:
					case FLOAT_:
					case DOUBLE_:
					{
						builtInType();
						astFactory.addASTChild(currentAST, returnAST);
						{
						_loop374:
						do {
							if ((LA(1)==LBRACK)) {
								lb2 = LT(1);
								lb2_AST = astFactory.create(lb2);
								astFactory.makeASTRoot(currentAST, lb2_AST);
								match(LBRACK);
								setArrayDeclarator_RBRACK(lb2_AST);
								astFactory.addASTChild(currentAST, returnAST);
							}
							else {
								break _loop374;
							}
							
						} while (true);
						}
						match(DOT);
						c2 = LT(1);
						c2_AST = astFactory.create(c2);
						astFactory.makeASTRoot(currentAST, c2_AST);
						match(CLASS);
						setType(c2_AST, CLASS_LITERAL);
						astFactory.addASTChild(currentAST, returnAST);
						primaryNoParen_AST = (AST)currentAST.root;
						break;
					}
					case VOID_:
					{
						AST tmp236_AST = null;
						tmp236_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp236_AST);
						match(VOID_);
						match(DOT);
						c3 = LT(1);
						c3_AST = astFactory.create(c3);
						astFactory.makeASTRoot(currentAST, c3_AST);
						match(CLASS);
						setType(c3_AST, CLASS_LITERAL);
						astFactory.addASTChild(currentAST, returnAST);
						primaryNoParen_AST = (AST)currentAST.root;
						break;
					}
					case QUOTE:
					{
						AST tmp238_AST = null;
						tmp238_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp238_AST);
						match(QUOTE);
						expression();
						astFactory.addASTChild(currentAST, returnAST);
						match(QUOTE);
						primaryNoParen_AST = (AST)currentAST.root;
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					returnAST = primaryNoParen_AST;
				}
				
	public final void selectorExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectorExpression_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		Token  invoke = null;
		AST invoke_AST = null;
		Token  w = null;
		AST w_AST = null;
		Token  q = null;
		AST q_AST = null;
		Token  n = null;
		AST n_AST = null;
		
		primary();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop353:
		do {
			switch ( LA(1)) {
			case LBRACK:
			{
				lb = LT(1);
				lb_AST = astFactory.create(lb);
				astFactory.makeASTRoot(currentAST, lb_AST);
				match(LBRACK);
				{
				switch ( LA(1)) {
				case BOOLEAN_LITERAL:
				case INT_LITERAL:
				case LONG_LITERAL:
				case FLOAT_LITERAL:
				case DOUBLE_LITERAL:
				case CHAR_LITERAL:
				case STRING_LITERAL:
				case IDENT:
				case SUPER:
				case VOID_:
				case BOOLEAN_:
				case BYTE_:
				case SHORT_:
				case CHAR_:
				case INT_:
				case LONG_:
				case FLOAT_:
				case DOUBLE_:
				case CONTEXT:
				case SUB:
				case ADD:
				case COM:
				case NOT:
				case THIS:
				case NULL_LITERAL:
				case QUOTE:
				case INC:
				case DEC:
				case NEW:
				case LPAREN:
				{
					singleExpression();
					astFactory.addASTChild(currentAST, returnAST);
					setType(lb_AST, INDEX_OP);
					astFactory.addASTChild(currentAST, returnAST);
					{
					_loop348:
					do {
						if ((LA(1)==COMMA)) {
							match(COMMA);
							singleExpression();
							astFactory.addASTChild(currentAST, returnAST);
						}
						else {
							break _loop348;
						}
						
					} while (true);
					}
					break;
				}
				case COLON:
				{
					match(COLON);
					setType(lb_AST, ARRAY_ITERATOR);
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(RBRACK);
				break;
			}
			case LPAREN:
			{
				invoke = LT(1);
				invoke_AST = astFactory.create(invoke);
				astFactory.makeASTRoot(currentAST, invoke_AST);
				match(LPAREN);
				{
				switch ( LA(1)) {
				case BOOLEAN_LITERAL:
				case INT_LITERAL:
				case LONG_LITERAL:
				case FLOAT_LITERAL:
				case DOUBLE_LITERAL:
				case CHAR_LITERAL:
				case STRING_LITERAL:
				case IDENT:
				case SUPER:
				case VOID_:
				case BOOLEAN_:
				case BYTE_:
				case SHORT_:
				case CHAR_:
				case INT_:
				case LONG_:
				case FLOAT_:
				case DOUBLE_:
				case CONTEXT:
				case SUB:
				case ADD:
				case COM:
				case NOT:
				case THIS:
				case NULL_LITERAL:
				case QUOTE:
				case INC:
				case DEC:
				case NEW:
				case LPAREN:
				{
					singleExpression();
					astFactory.addASTChild(currentAST, returnAST);
					{
					_loop351:
					do {
						if ((LA(1)==COMMA)) {
							match(COMMA);
							singleExpression();
							astFactory.addASTChild(currentAST, returnAST);
						}
						else {
							break _loop351;
						}
						
					} while (true);
					}
					break;
				}
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(RPAREN);
				setType(invoke_AST, INVOKE_OP);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
				if ((LA(1)==DOT) && (LA(2)==IDENT)) {
					AST tmp245_AST = null;
					tmp245_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp245_AST);
					match(DOT);
					AST tmp246_AST = null;
					tmp246_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp246_AST);
					match(IDENT);
					{
					if ((LA(1)==LPAREN) && (_tokenSet_15.member(LA(2)))) {
						lp = LT(1);
						lp_AST = astFactory.create(lp);
						astFactory.makeASTRoot(currentAST, lp_AST);
						match(LPAREN);
						argList(false);
						astFactory.addASTChild(currentAST, returnAST);
						match(RPAREN);
						setType(lp_AST, METHOD_CALL);
						astFactory.addASTChild(currentAST, returnAST);
					}
					else if ((_tokenSet_35.member(LA(1))) && (_tokenSet_14.member(LA(2)))) {
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					
					}
				}
				else if ((LA(1)==DOT) && (LA(2)==LPAREN)) {
					w = LT(1);
					w_AST = astFactory.create(w);
					astFactory.makeASTRoot(currentAST, w_AST);
					match(DOT);
					withInstanceRest(w_AST);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else if ((LA(1)==DOT) && (LA(2)==CONTEXT)) {
					match(DOT);
					q = LT(1);
					q_AST = astFactory.create(q);
					astFactory.makeASTRoot(currentAST, q_AST);
					match(CONTEXT);
					query();
					astFactory.addASTChild(currentAST, returnAST);
					match(RCONTEXT);
					setType(q_AST, QUERY_EXPR);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else if ((LA(1)==DOT) && (LA(2)==NEW)) {
					match(DOT);
					n = LT(1);
					n_AST = astFactory.create(n);
					astFactory.makeASTRoot(currentAST, n_AST);
					match(NEW);
					AST tmp251_AST = null;
					tmp251_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp251_AST);
					match(IDENT);
					match(LPAREN);
					argList(false);
					astFactory.addASTChild(currentAST, returnAST);
					match(RPAREN);
					setType(n_AST, QUALIFIED_NEW);
					astFactory.addASTChild(currentAST, returnAST);
					{
					switch ( LA(1)) {
					case LCURLY:
					{
						anonymousClassBody();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case SEMI:
					case ASSIGN:
					case LT:
					case GT:
					case LINE:
					case LEFT_RIGHT_ARROW:
					case PLUS_LEFT_ARROW:
					case PLUS_ARROW:
					case PLUS_LINE:
					case PLUS_LEFT_RIGHT_ARROW:
					case SLASH_LEFT_ARROW:
					case SLASH_ARROW:
					case SLASH_LINE:
					case SLASH_LEFT_RIGHT_ARROW:
					case DOT:
					case SUB:
					case LEFT_ARROW:
					case ARROW:
					case QUESTION:
					case MUL:
					case ADD:
					case RULE:
					case DIV:
					case REM:
					case POW:
					case SHL:
					case SHR:
					case USHR:
					case LE:
					case GE:
					case CMP:
					case NOT_EQUALS:
					case EQUALS:
					case OR:
					case XOR:
					case AND:
					case COR:
					case CAND:
					case LONG_LEFT_ARROW:
					case LONG_ARROW:
					case LONG_LEFT_RIGHT_ARROW:
					case INSTANCEOF:
					case QUOTE:
					case ADD_ASSIGN:
					case SUB_ASSIGN:
					case MUL_ASSIGN:
					case DIV_ASSIGN:
					case REM_ASSIGN:
					case POW_ASSIGN:
					case SHR_ASSIGN:
					case USHR_ASSIGN:
					case SHL_ASSIGN:
					case AND_ASSIGN:
					case XOR_ASSIGN:
					case OR_ASSIGN:
					case DEFERRED_ASSIGN:
					case DEFERRED_RATE_ASSIGN:
					case DEFERRED_ADD:
					case DEFERRED_SUB:
					case DEFERRED_MUL:
					case DEFERRED_DIV:
					case DEFERRED_REM:
					case DEFERRED_POW:
					case DEFERRED_OR:
					case DEFERRED_AND:
					case DEFERRED_XOR:
					case DEFERRED_SHL:
					case DEFERRED_SHR:
					case DEFERRED_USHR:
					case INC:
					case DEC:
					case IN:
					case GUARD:
					case LPAREN:
					case RPAREN:
					case LBRACK:
					case RBRACK:
					case RCURLY:
					case COLON:
					case COMMA:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
				}
			else {
				break _loop353;
			}
			}
		} while (true);
		}
		selectorExpression_AST = (AST)currentAST.root;
		returnAST = selectorExpression_AST;
	}
	
	public final void singleExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST singleExpression_AST = null;
		
		assignmentExpression();
		astFactory.addASTChild(currentAST, returnAST);
		singleExpression_AST = (AST)currentAST.root;
		returnAST = singleExpression_AST;
	}
	
	public final void productionStatement(
		AST prev
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST productionStatement_AST = null;
		Token  id = null;
		AST id_AST = null;
		Token  lbl = null;
		AST lbl_AST = null;
		AST n_AST = null;
		Token  c = null;
		AST c_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		AST e_AST = null;
		
		switch ( LA(1)) {
		case LCURLY:
		{
			script();
			astFactory.addASTChild(currentAST, returnAST);
			productionStatement_AST = (AST)currentAST.root;
			break;
		}
		case IF:
		case SYNCHRONIZED_:
		case FOR:
		case WHILE:
		case DO:
		case SWITCH:
		{
			controlStatement(true);
			astFactory.addASTChild(currentAST, returnAST);
			productionStatement_AST = (AST)currentAST.root;
			break;
		}
		case THROW:
		{
			AST tmp254_AST = null;
			tmp254_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp254_AST);
			match(THROW);
			primaryExpressionNode();
			astFactory.addASTChild(currentAST, returnAST);
			productionStatement_AST = (AST)currentAST.root;
			break;
		}
		case COMMA:
		{
			c = LT(1);
			c_AST = astFactory.create(c);
			astFactory.addASTChild(currentAST, c_AST);
			match(COMMA);
			setType(c_AST, SEPARATE);
			astFactory.addASTChild(currentAST, returnAST);
			productionStatement_AST = (AST)currentAST.root;
			break;
		}
		case LBRACK:
		{
			lb = LT(1);
			lb_AST = astFactory.create(lb);
			astFactory.makeASTRoot(currentAST, lb_AST);
			match(LBRACK);
			productionStatements();
			astFactory.addASTChild(currentAST, returnAST);
			match(RBRACK);
			setType(lb_AST, TREE);
			astFactory.addASTChild(currentAST, returnAST);
			productionStatement_AST = (AST)currentAST.root;
			break;
		}
		case BREAK:
		{
			AST tmp256_AST = null;
			tmp256_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp256_AST);
			match(BREAK);
			{
			if ((LA(1)==IDENT) && (_tokenSet_36.member(LA(2)))) {
				AST tmp257_AST = null;
				tmp257_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp257_AST);
				match(IDENT);
			}
			else if ((_tokenSet_36.member(LA(1))) && (_tokenSet_37.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			productionStatement_AST = (AST)currentAST.root;
			break;
		}
		case CONTINUE:
		{
			AST tmp258_AST = null;
			tmp258_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp258_AST);
			match(CONTINUE);
			{
			if ((LA(1)==IDENT) && (_tokenSet_36.member(LA(2)))) {
				AST tmp259_AST = null;
				tmp259_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp259_AST);
				match(IDENT);
			}
			else if ((_tokenSet_36.member(LA(1))) && (_tokenSet_37.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			productionStatement_AST = (AST)currentAST.root;
			break;
		}
		case LT:
		case GT:
		case LINE:
		case LEFT_RIGHT_ARROW:
		case PLUS_LEFT_ARROW:
		case PLUS_ARROW:
		case PLUS_LINE:
		case PLUS_LEFT_RIGHT_ARROW:
		case SLASH_LEFT_ARROW:
		case SLASH_ARROW:
		case SLASH_LINE:
		case SLASH_LEFT_RIGHT_ARROW:
		case SUB:
		case LEFT_ARROW:
		case MUL:
		case ADD:
		case DIV:
		case REM:
		case POW:
		case SHL:
		case SHR:
		case USHR:
		case LE:
		case GE:
		case CMP:
		case OR:
		case AND:
		case COR:
		case CAND:
		case LONG_LEFT_ARROW:
		case LONG_ARROW:
		case LONG_LEFT_RIGHT_ARROW:
		case INC:
		case DEC:
		case IN:
		case GUARD:
		{
			produceEdgeOp();
			e_AST = (AST)returnAST;
			edgeNode(e_AST, prev);
			astFactory.addASTChild(currentAST, returnAST);
			productionStatement_AST = (AST)currentAST.root;
			break;
		}
		default:
			if ((LA(1)==IDENT) && (LA(2)==COLON)) {
				id = LT(1);
				id_AST = astFactory.create(id);
				astFactory.addASTChild(currentAST, id_AST);
				match(IDENT);
				lbl = LT(1);
				lbl_AST = astFactory.create(lbl);
				astFactory.makeASTRoot(currentAST, lbl_AST);
				match(COLON);
				{
				switch ( LA(1)) {
				case IF:
				case SYNCHRONIZED_:
				case FOR:
				case WHILE:
				case DO:
				case SWITCH:
				case LPAREN:
				{
					{
					switch ( LA(1)) {
					case IF:
					case SYNCHRONIZED_:
					case FOR:
					case WHILE:
					case DO:
					case SWITCH:
					{
						controlStatement(true);
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case LPAREN:
					{
						productionBlock();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					setType(lbl_AST, LABELED_STATEMENT);
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case BOOLEAN_LITERAL:
				case INT_LITERAL:
				case LONG_LITERAL:
				case FLOAT_LITERAL:
				case DOUBLE_LITERAL:
				case CHAR_LITERAL:
				case STRING_LITERAL:
				case IDENT:
				case SUPER:
				case VOID_:
				case BOOLEAN_:
				case BYTE_:
				case SHORT_:
				case CHAR_:
				case INT_:
				case LONG_:
				case FLOAT_:
				case DOUBLE_:
				case COM:
				case NOT:
				case XOR:
				case THIS:
				case NULL_LITERAL:
				case QUOTE:
				{
					node(astFactory.create(EMPTY), id_AST, prev);
					n_AST = (AST)returnAST;
					if ( inputState.guessing==0 ) {
						productionStatement_AST = (AST)currentAST.root;
						productionStatement_AST = n_AST;
						currentAST.root = productionStatement_AST;
						currentAST.child = productionStatement_AST!=null &&productionStatement_AST.getFirstChild()!=null ?
							productionStatement_AST.getFirstChild() : productionStatement_AST;
						currentAST.advanceChildToEnd();
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				productionStatement_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_38.member(LA(1))) && (_tokenSet_39.member(LA(2)))) {
				node(astFactory.create(EMPTY), null, prev);
				astFactory.addASTChild(currentAST, returnAST);
				productionStatement_AST = (AST)currentAST.root;
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = productionStatement_AST;
	}
	
	public final void productionStatementsAsList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST productionStatementsAsList_AST = null;
		AST g_AST = null;
		
		productionStatements();
		g_AST = (AST)returnAST;
		if ( inputState.guessing==0 ) {
			productionStatementsAsList_AST = (AST)currentAST.root;
			
						productionStatementsAsList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SLIST)).add(g_AST));
					
			currentAST.root = productionStatementsAsList_AST;
			currentAST.child = productionStatementsAsList_AST!=null &&productionStatementsAsList_AST.getFirstChild()!=null ?
				productionStatementsAsList_AST.getFirstChild() : productionStatementsAsList_AST;
			currentAST.advanceChildToEnd();
		}
		returnAST = productionStatementsAsList_AST;
	}
	
	public final void productionBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST productionBlock_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		
		lp = LT(1);
		lp_AST = astFactory.create(lp);
		astFactory.makeASTRoot(currentAST, lp_AST);
		match(LPAREN);
		productionStatements();
		astFactory.addASTChild(currentAST, returnAST);
		match(RPAREN);
		setType(lp_AST, SLIST);
		astFactory.addASTChild(currentAST, returnAST);
		productionBlock_AST = (AST)currentAST.root;
		returnAST = productionBlock_AST;
	}
	
	public final void controlStatement(
		boolean prod
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlStatement_AST = null;
		Token  e = null;
		AST e_AST = null;
		Token  f = null;
		AST f_AST = null;
		Token  d = null;
		AST d_AST = null;
		Token  w = null;
		AST w_AST = null;
		
		switch ( LA(1)) {
		case IF:
		{
			AST tmp261_AST = null;
			tmp261_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp261_AST);
			match(IF);
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			controlBody(prod);
			astFactory.addASTChild(currentAST, returnAST);
			{
			if ((LA(1)==ELSE) && (_tokenSet_40.member(LA(2)))) {
				e = LT(1);
				e_AST = astFactory.create(e);
				match(ELSE);
				controlBody(prod);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((_tokenSet_41.member(LA(1))) && (_tokenSet_42.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			controlStatement_AST = (AST)currentAST.root;
			break;
		}
		case SWITCH:
		{
			AST tmp264_AST = null;
			tmp264_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp264_AST);
			match(SWITCH);
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			{
			if (((LA(1)==LPAREN))&&(prod)) {
				match(LPAREN);
				{
				_loop220:
				do {
					if ((LA(1)==CASE||LA(1)==DEFAULT)) {
						switchGroup(true);
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop220;
					}
					
				} while (true);
				}
				match(RPAREN);
			}
			else if ((LA(1)==LCURLY)) {
				match(LCURLY);
				{
				_loop222:
				do {
					if ((LA(1)==CASE||LA(1)==DEFAULT)) {
						switchGroup(false);
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop222;
					}
					
				} while (true);
				}
				match(RCURLY);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			controlStatement_AST = (AST)currentAST.root;
			break;
		}
		case FOR:
		{
			f = LT(1);
			f_AST = astFactory.create(f);
			astFactory.makeASTRoot(currentAST, f_AST);
			match(FOR);
			forControl(f_AST);
			astFactory.addASTChild(currentAST, returnAST);
			controlBody(prod);
			astFactory.addASTChild(currentAST, returnAST);
			controlStatement_AST = (AST)currentAST.root;
			break;
		}
		case DO:
		{
			d = LT(1);
			d_AST = astFactory.create(d);
			astFactory.makeASTRoot(currentAST, d_AST);
			match(DO);
			controlBody(prod);
			astFactory.addASTChild(currentAST, returnAST);
			match(WHILE);
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			{
			if (((_tokenSet_41.member(LA(1))) && (_tokenSet_42.member(LA(2))))&&(prod)) {
			}
			else if (((LA(1)==SEMI) && (_tokenSet_41.member(LA(2))))&&(!prod)) {
				AST tmp274_AST = null;
				tmp274_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp274_AST);
				match(SEMI);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			controlStatement_AST = (AST)currentAST.root;
			break;
		}
		case WHILE:
		{
			w = LT(1);
			w_AST = astFactory.create(w);
			astFactory.makeASTRoot(currentAST, w_AST);
			match(WHILE);
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			controlBody(prod);
			astFactory.addASTChild(currentAST, returnAST);
			controlStatement_AST = (AST)currentAST.root;
			break;
		}
		case SYNCHRONIZED_:
		{
			AST tmp277_AST = null;
			tmp277_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp277_AST);
			match(SYNCHRONIZED_);
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			{
			if (((LA(1)==LPAREN))&&(prod)) {
				productionBlock();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==LCURLY)) {
				block();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			controlStatement_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = controlStatement_AST;
	}
	
	public final void node(
		AST edge, AST id, AST prev
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST node_AST = null;
		Token  b = null;
		AST b_AST = null;
		AST u_AST = null;
		
		{
		switch ( LA(1)) {
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case THIS:
		case NULL_LITERAL:
		case QUOTE:
		{
			primaryExpressionNode();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case XOR:
		{
			b = LT(1);
			b_AST = astFactory.create(b);
			astFactory.addASTChild(currentAST, b_AST);
			match(XOR);
			setType(b_AST, ROOT);
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case COM:
		case NOT:
		{
			unaryOpNode();
			u_AST = (AST)returnAST;
			insertTree(UNARY_PREFIX, u_AST);
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			node_AST = (AST)currentAST.root;
			
					AST n = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(NODE)).add(node_AST).add(id).add(edge));
					if ((prev != null) && (prev.getType () == NODES))
					{
						prev.addChild (n);
						node_AST = null;
					}
					else
					{
						node_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(NODES)).add(n));
					}
				
			currentAST.root = node_AST;
			currentAST.child = node_AST!=null &&node_AST.getFirstChild()!=null ?
				node_AST.getFirstChild() : node_AST;
			currentAST.advanceChildToEnd();
		}
		node_AST = (AST)currentAST.root;
		returnAST = node_AST;
	}
	
	public final void primaryExpressionNode() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryExpressionNode_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		Token  w = null;
		AST w_AST = null;
		
		primaryNoParen(false);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop184:
		do {
			if ((LA(1)==DOT) && (LA(2)==IDENT)) {
				AST tmp280_AST = null;
				tmp280_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp280_AST);
				match(DOT);
				AST tmp281_AST = null;
				tmp281_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp281_AST);
				match(IDENT);
				{
				switch ( LA(1)) {
				case LPAREN:
				{
					lp = LT(1);
					lp_AST = astFactory.create(lp);
					astFactory.makeASTRoot(currentAST, lp_AST);
					match(LPAREN);
					argList(false);
					astFactory.addASTChild(currentAST, returnAST);
					match(RPAREN);
					setType(lp_AST, METHOD_CALL);
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case EOF:
				case BOOLEAN_LITERAL:
				case INT_LITERAL:
				case LONG_LITERAL:
				case FLOAT_LITERAL:
				case DOUBLE_LITERAL:
				case CHAR_LITERAL:
				case STRING_LITERAL:
				case IDENT:
				case SUPER:
				case SEMI:
				case VOID_:
				case BOOLEAN_:
				case BYTE_:
				case SHORT_:
				case CHAR_:
				case INT_:
				case LONG_:
				case FLOAT_:
				case DOUBLE_:
				case LT:
				case GT:
				case LINE:
				case LEFT_RIGHT_ARROW:
				case PLUS_LEFT_ARROW:
				case PLUS_ARROW:
				case PLUS_LINE:
				case PLUS_LEFT_RIGHT_ARROW:
				case SLASH_LEFT_ARROW:
				case SLASH_ARROW:
				case SLASH_LINE:
				case SLASH_LEFT_RIGHT_ARROW:
				case DOT:
				case SUB:
				case LEFT_ARROW:
				case MUL:
				case ADD:
				case COM:
				case NOT:
				case DIV:
				case REM:
				case POW:
				case SHL:
				case SHR:
				case USHR:
				case LE:
				case GE:
				case CMP:
				case OR:
				case XOR:
				case AND:
				case COR:
				case CAND:
				case THIS:
				case IF:
				case THROW:
				case SYNCHRONIZED_:
				case BREAK:
				case CONTINUE:
				case FOR:
				case WHILE:
				case DO:
				case SWITCH:
				case CASE:
				case DEFAULT:
				case NULL_LITERAL:
				case LONG_LEFT_ARROW:
				case LONG_ARROW:
				case LONG_LEFT_RIGHT_ARROW:
				case QUOTE:
				case INC:
				case DEC:
				case IN:
				case GUARD:
				case RPAREN:
				case LBRACK:
				case RBRACK:
				case LCURLY:
				case RCURLY:
				case COMMA:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
			else if ((LA(1)==DOT) && (LA(2)==LPAREN)) {
				w = LT(1);
				w_AST = astFactory.create(w);
				astFactory.makeASTRoot(currentAST, w_AST);
				match(DOT);
				withInstanceRest(w_AST);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop184;
			}
			
		} while (true);
		}
		primaryExpressionNode_AST = (AST)currentAST.root;
		returnAST = primaryExpressionNode_AST;
	}
	
	public final void produceEdgeOp() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST produceEdgeOp_AST = null;
		Token  l = null;
		AST l_AST = null;
		
		switch ( LA(1)) {
		case PLUS_LEFT_ARROW:
		{
			AST tmp283_AST = null;
			tmp283_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp283_AST);
			match(PLUS_LEFT_ARROW);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case PLUS_ARROW:
		{
			AST tmp284_AST = null;
			tmp284_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp284_AST);
			match(PLUS_ARROW);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case PLUS_LEFT_RIGHT_ARROW:
		{
			AST tmp285_AST = null;
			tmp285_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp285_AST);
			match(PLUS_LEFT_RIGHT_ARROW);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case PLUS_LINE:
		{
			AST tmp286_AST = null;
			tmp286_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp286_AST);
			match(PLUS_LINE);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case SLASH_LEFT_ARROW:
		{
			AST tmp287_AST = null;
			tmp287_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp287_AST);
			match(SLASH_LEFT_ARROW);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case SLASH_ARROW:
		{
			AST tmp288_AST = null;
			tmp288_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp288_AST);
			match(SLASH_ARROW);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case SLASH_LEFT_RIGHT_ARROW:
		{
			AST tmp289_AST = null;
			tmp289_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp289_AST);
			match(SLASH_LEFT_RIGHT_ARROW);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case SLASH_LINE:
		{
			AST tmp290_AST = null;
			tmp290_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp290_AST);
			match(SLASH_LINE);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case LEFT_RIGHT_ARROW:
		{
			AST tmp291_AST = null;
			tmp291_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp291_AST);
			match(LEFT_RIGHT_ARROW);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case LINE:
		{
			AST tmp292_AST = null;
			tmp292_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp292_AST);
			match(LINE);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case LONG_LEFT_ARROW:
		{
			AST tmp293_AST = null;
			tmp293_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp293_AST);
			match(LONG_LEFT_ARROW);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case LONG_LEFT_RIGHT_ARROW:
		{
			AST tmp294_AST = null;
			tmp294_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp294_AST);
			match(LONG_LEFT_RIGHT_ARROW);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case LONG_ARROW:
		{
			AST tmp295_AST = null;
			tmp295_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp295_AST);
			match(LONG_ARROW);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case LT:
		{
			AST tmp296_AST = null;
			tmp296_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp296_AST);
			match(LT);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case GT:
		{
			AST tmp297_AST = null;
			tmp297_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp297_AST);
			match(GT);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case LE:
		{
			AST tmp298_AST = null;
			tmp298_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp298_AST);
			match(LE);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case GE:
		{
			AST tmp299_AST = null;
			tmp299_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp299_AST);
			match(GE);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case SHL:
		{
			AST tmp300_AST = null;
			tmp300_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp300_AST);
			match(SHL);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case SHR:
		{
			AST tmp301_AST = null;
			tmp301_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp301_AST);
			match(SHR);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case USHR:
		{
			AST tmp302_AST = null;
			tmp302_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp302_AST);
			match(USHR);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case CMP:
		{
			AST tmp303_AST = null;
			tmp303_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp303_AST);
			match(CMP);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case ADD:
		{
			AST tmp304_AST = null;
			tmp304_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp304_AST);
			match(ADD);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case MUL:
		{
			AST tmp305_AST = null;
			tmp305_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp305_AST);
			match(MUL);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case DIV:
		{
			AST tmp306_AST = null;
			tmp306_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp306_AST);
			match(DIV);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case REM:
		{
			AST tmp307_AST = null;
			tmp307_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp307_AST);
			match(REM);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case POW:
		{
			AST tmp308_AST = null;
			tmp308_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp308_AST);
			match(POW);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case OR:
		{
			AST tmp309_AST = null;
			tmp309_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp309_AST);
			match(OR);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case COR:
		{
			AST tmp310_AST = null;
			tmp310_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp310_AST);
			match(COR);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case AND:
		{
			AST tmp311_AST = null;
			tmp311_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp311_AST);
			match(AND);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case CAND:
		{
			AST tmp312_AST = null;
			tmp312_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp312_AST);
			match(CAND);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case IN:
		{
			AST tmp313_AST = null;
			tmp313_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp313_AST);
			match(IN);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case GUARD:
		{
			AST tmp314_AST = null;
			tmp314_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp314_AST);
			match(GUARD);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case LEFT_ARROW:
		{
			l = LT(1);
			l_AST = astFactory.create(l);
			astFactory.makeASTRoot(currentAST, l_AST);
			match(LEFT_ARROW);
			selectorExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case ARROW:
			{
				match(ARROW);
				setType(l_AST, X_LEFT_RIGHT_ARROW);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SUB:
			{
				match(SUB);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case SUB:
		{
			match(SUB);
			selectorExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case ARROW:
			{
				AST tmp318_AST = null;
				tmp318_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp318_AST);
				match(ARROW);
				break;
			}
			case SUB:
			{
				AST tmp319_AST = null;
				tmp319_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp319_AST);
				match(SUB);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case INC:
		{
			AST tmp320_AST = null;
			tmp320_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp320_AST);
			match(INC);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		case DEC:
		{
			AST tmp321_AST = null;
			tmp321_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp321_AST);
			match(DEC);
			produceEdgeOp_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = produceEdgeOp_AST;
	}
	
	public final void edgeNode(
		AST edge, AST prev
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST edgeNode_AST = null;
		Token  id = null;
		AST id_AST = null;
		
		if ((_tokenSet_38.member(LA(1))) && (_tokenSet_39.member(LA(2)))) {
			node(edge, null, prev);
			astFactory.addASTChild(currentAST, returnAST);
			edgeNode_AST = (AST)currentAST.root;
		}
		else if ((LA(1)==IDENT) && (LA(2)==COLON)) {
			id = LT(1);
			id_AST = astFactory.create(id);
			match(IDENT);
			match(COLON);
			node(edge, id_AST, prev);
			astFactory.addASTChild(currentAST, returnAST);
			edgeNode_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = edgeNode_AST;
	}
	
	public final void primaryNoCreator() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryNoCreator_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		Token  q = null;
		AST q_AST = null;
		
		switch ( LA(1)) {
		case LPAREN:
		{
			lp = LT(1);
			lp_AST = astFactory.create(lp);
			astFactory.makeASTRoot(currentAST, lp_AST);
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			setType(lp_AST, EXPR);
			astFactory.addASTChild(currentAST, returnAST);
			primaryNoCreator_AST = (AST)currentAST.root;
			break;
		}
		case CONTEXT:
		{
			q = LT(1);
			q_AST = astFactory.create(q);
			astFactory.makeASTRoot(currentAST, q_AST);
			match(CONTEXT);
			insertTree(EMPTY, null);
			astFactory.addASTChild(currentAST, returnAST);
			query();
			astFactory.addASTChild(currentAST, returnAST);
			match(RCONTEXT);
			setType(q_AST, QUERY_EXPR);
			astFactory.addASTChild(currentAST, returnAST);
			primaryNoCreator_AST = (AST)currentAST.root;
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case THIS:
		case NULL_LITERAL:
		case QUOTE:
		{
			primaryNoParen(false);
			astFactory.addASTChild(currentAST, returnAST);
			primaryNoCreator_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = primaryNoCreator_AST;
	}
	
	public final void withInstanceRest(
		AST w
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST withInstanceRest_AST = null;
		
				w.setType (EXPR);
			
		
		match(LPAREN);
		{
		switch ( LA(1)) {
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case CONTEXT:
		case SUB:
		case ADD:
		case COM:
		case NOT:
		case THIS:
		case SYNCHRONIZED_:
		case NULL_LITERAL:
		case QUOTE:
		case INC:
		case DEC:
		case NEW:
		case ANNOTATION:
		case PRIVATE_:
		case PUBLIC_:
		case PROTECTED_:
		case STATIC_:
		case TRANSIENT_:
		case FINAL_:
		case ABSTRACT_:
		case NATIVE_:
		case VOLATILE_:
		case STRICT_:
		case CONST_:
		case LPAREN:
		{
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			setType(w, WITH);
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RPAREN);
		withInstanceRest_AST = (AST)currentAST.root;
		returnAST = withInstanceRest_AST;
	}
	
	public final void primaryExpressionNodeParen() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryExpressionNodeParen_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		
		switch ( LA(1)) {
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case THIS:
		case NULL_LITERAL:
		case QUOTE:
		{
			primaryExpressionNode();
			astFactory.addASTChild(currentAST, returnAST);
			primaryExpressionNodeParen_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			lp = LT(1);
			lp_AST = astFactory.create(lp);
			astFactory.makeASTRoot(currentAST, lp_AST);
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			setType(lp_AST, EXPR);
			astFactory.addASTChild(currentAST, returnAST);
			primaryExpressionNodeParen_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = primaryExpressionNodeParen_AST;
	}
	
	public final void blockStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST blockStatement_AST = null;
		AST m_AST = null;
		
		boolean synPredMatched206 = false;
		if (((_tokenSet_8.member(LA(1))) && (_tokenSet_9.member(LA(2))))) {
			int _m206 = mark();
			synPredMatched206 = true;
			inputState.guessing++;
			try {
				{
				varDeclarationPredicate();
				}
			}
			catch (RecognitionException pe) {
				synPredMatched206 = false;
			}
			rewind(_m206);
inputState.guessing--;
		}
		if ( synPredMatched206 ) {
			varDeclaration(true);
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMI);
			blockStatement_AST = (AST)currentAST.root;
		}
		else {
			boolean synPredMatched208 = false;
			if (((_tokenSet_43.member(LA(1))) && (_tokenSet_44.member(LA(2))))) {
				int _m208 = mark();
				synPredMatched208 = true;
				inputState.guessing++;
				try {
					{
					modifiers();
					match(CLASS);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched208 = false;
				}
				rewind(_m208);
inputState.guessing--;
			}
			if ( synPredMatched208 ) {
				modifiers();
				m_AST = (AST)returnAST;
				classDeclaration(m_AST);
				astFactory.addASTChild(currentAST, returnAST);
				blockStatement_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_40.member(LA(1))) && (_tokenSet_45.member(LA(2)))) {
				statement();
				astFactory.addASTChild(currentAST, returnAST);
				blockStatement_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			returnAST = blockStatement_AST;
		}
		
	public final boolean  constructorInvocation() throws RecognitionException, TokenStreamException {
		boolean explicit;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constructorInvocation_AST = null;
		Token  lp1 = null;
		AST lp1_AST = null;
		Token  qs = null;
		AST qs_AST = null;
		Token  lp2 = null;
		AST lp2_AST = null;
		
				explicit = true;
			
		
		boolean synPredMatched200 = false;
		if (((LA(1)==SUPER||LA(1)==THIS) && (LA(2)==LPAREN))) {
			int _m200 = mark();
			synPredMatched200 = true;
			inputState.guessing++;
			try {
				{
				{
				switch ( LA(1)) {
				case THIS:
				{
					match(THIS);
					break;
				}
				case SUPER:
				{
					match(SUPER);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(LPAREN);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched200 = false;
			}
			rewind(_m200);
inputState.guessing--;
		}
		if ( synPredMatched200 ) {
			{
			switch ( LA(1)) {
			case THIS:
			{
				AST tmp329_AST = null;
				tmp329_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp329_AST);
				match(THIS);
				break;
			}
			case SUPER:
			{
				AST tmp330_AST = null;
				tmp330_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp330_AST);
				match(SUPER);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			lp1 = LT(1);
			lp1_AST = astFactory.create(lp1);
			astFactory.makeASTRoot(currentAST, lp1_AST);
			match(LPAREN);
			argList(false);
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			setType(lp1_AST, CONSTRUCTOR);
			astFactory.addASTChild(currentAST, returnAST);
			constructorInvocation_AST = (AST)currentAST.root;
		}
		else {
			boolean synPredMatched203 = false;
			if (((_tokenSet_46.member(LA(1))) && (_tokenSet_47.member(LA(2))))) {
				int _m203 = mark();
				synPredMatched203 = true;
				inputState.guessing++;
				try {
					{
					selectorExpression();
					match(DOT);
					match(SUPER);
					match(LPAREN);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched203 = false;
				}
				rewind(_m203);
inputState.guessing--;
			}
			if ( synPredMatched203 ) {
				selectorExpression();
				astFactory.addASTChild(currentAST, returnAST);
				match(DOT);
				qs = LT(1);
				qs_AST = astFactory.create(qs);
				astFactory.makeASTRoot(currentAST, qs_AST);
				match(SUPER);
				lp2 = LT(1);
				lp2_AST = astFactory.create(lp2);
				astFactory.makeASTRoot(currentAST, lp2_AST);
				match(LPAREN);
				argList(false);
				astFactory.addASTChild(currentAST, returnAST);
				match(RPAREN);
				setType(lp2_AST, CONSTRUCTOR);
				astFactory.addASTChild(currentAST, returnAST);
				setType(qs_AST, QUALIFIED_SUPER);
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					((ASTWithToken) qs_AST).token = null;
				}
				constructorInvocation_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_48.member(LA(1))) && (_tokenSet_49.member(LA(2)))) {
				if ( inputState.guessing==0 ) {
					explicit = false;
				}
				constructorInvocation_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			returnAST = constructorInvocation_AST;
			return explicit;
		}
		
	public final void varDeclarationPredicate() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST varDeclarationPredicate_AST = null;
		
		modifiers();
		typeSpec();
		AST tmp334_AST = null;
		tmp334_AST = astFactory.create(LT(1));
		match(IDENT);
		AST tmp335_AST = null;
		tmp335_AST = astFactory.create(LT(1));
		matchNot(LAMBDA);
		returnAST = varDeclarationPredicate_AST;
	}
	
	public final void statementExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statementExpression_AST = null;
		AST e_AST = null;
		
		lambdaExpression();
		e_AST = (AST)returnAST;
		assignmentExpressionRest(e_AST);
		astFactory.addASTChild(currentAST, returnAST);
		statementExpression_AST = (AST)currentAST.root;
		returnAST = statementExpression_AST;
	}
	
	public final void catchClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST catchClause_AST = null;
		
		AST tmp336_AST = null;
		tmp336_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp336_AST);
		match(CATCH);
		match(LPAREN);
		modifiers();
		astFactory.addASTChild(currentAST, returnAST);
		name();
		astFactory.addASTChild(currentAST, returnAST);
		AST tmp338_AST = null;
		tmp338_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp338_AST);
		match(IDENT);
		match(RPAREN);
		block();
		astFactory.addASTChild(currentAST, returnAST);
		catchClause_AST = (AST)currentAST.root;
		returnAST = catchClause_AST;
	}
	
	public final void statementSemi() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statementSemi_AST = null;
		
		switch ( LA(1)) {
		case RETURN:
		case YIELD:
		{
			{
			switch ( LA(1)) {
			case RETURN:
			{
				AST tmp340_AST = null;
				tmp340_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp340_AST);
				match(RETURN);
				break;
			}
			case YIELD:
			{
				AST tmp341_AST = null;
				tmp341_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp341_AST);
				match(YIELD);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case BOOLEAN_LITERAL:
			case INT_LITERAL:
			case LONG_LITERAL:
			case FLOAT_LITERAL:
			case DOUBLE_LITERAL:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case IDENT:
			case SUPER:
			case VOID_:
			case BOOLEAN_:
			case BYTE_:
			case SHORT_:
			case CHAR_:
			case INT_:
			case LONG_:
			case FLOAT_:
			case DOUBLE_:
			case CONTEXT:
			case SUB:
			case ADD:
			case COM:
			case NOT:
			case THIS:
			case SYNCHRONIZED_:
			case NULL_LITERAL:
			case QUOTE:
			case INC:
			case DEC:
			case NEW:
			case ANNOTATION:
			case PRIVATE_:
			case PUBLIC_:
			case PROTECTED_:
			case STATIC_:
			case TRANSIENT_:
			case FINAL_:
			case ABSTRACT_:
			case NATIVE_:
			case VOLATILE_:
			case STRICT_:
			case CONST_:
			case LPAREN:
			{
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SEMI:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			statementSemi_AST = (AST)currentAST.root;
			break;
		}
		case BREAK:
		{
			AST tmp342_AST = null;
			tmp342_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp342_AST);
			match(BREAK);
			{
			switch ( LA(1)) {
			case IDENT:
			{
				AST tmp343_AST = null;
				tmp343_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp343_AST);
				match(IDENT);
				break;
			}
			case SEMI:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			statementSemi_AST = (AST)currentAST.root;
			break;
		}
		case CONTINUE:
		{
			AST tmp344_AST = null;
			tmp344_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp344_AST);
			match(CONTINUE);
			{
			switch ( LA(1)) {
			case IDENT:
			{
				AST tmp345_AST = null;
				tmp345_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp345_AST);
				match(IDENT);
				break;
			}
			case SEMI:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			statementSemi_AST = (AST)currentAST.root;
			break;
		}
		case THROW:
		{
			AST tmp346_AST = null;
			tmp346_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp346_AST);
			match(THROW);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			statementSemi_AST = (AST)currentAST.root;
			break;
		}
		case ASSERT:
		{
			AST tmp347_AST = null;
			tmp347_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp347_AST);
			match(ASSERT);
			singleExpressionNoRange();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case COLON:
			{
				match(COLON);
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SEMI:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			statementSemi_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = statementSemi_AST;
	}
	
	public final void controlBody(
		boolean prod
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST controlBody_AST = null;
		
		if (((_tokenSet_40.member(LA(1))) && (_tokenSet_50.member(LA(2))))&&(!prod)) {
			statement();
			astFactory.addASTChild(currentAST, returnAST);
			controlBody_AST = (AST)currentAST.root;
		}
		else if (((_tokenSet_51.member(LA(1))) && (_tokenSet_52.member(LA(2))))&&(prod)) {
			{
			switch ( LA(1)) {
			case LPAREN:
			{
				productionBlock();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LCURLY:
			{
				block();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case IF:
			case SYNCHRONIZED_:
			case FOR:
			case WHILE:
			case DO:
			case SWITCH:
			{
				controlStatement(true);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			controlBody_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = controlBody_AST;
	}
	
	public final void switchGroup(
		boolean prod
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST switchGroup_AST = null;
		
		{
		int _cnt230=0;
		_loop230:
		do {
			if ((LA(1)==CASE||LA(1)==DEFAULT) && (_tokenSet_53.member(LA(2)))) {
				switchGroupLabel();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				if ( _cnt230>=1 ) { break _loop230; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt230++;
		} while (true);
		}
		{
		if (((_tokenSet_54.member(LA(1))) && (_tokenSet_55.member(LA(2))))&&(prod)) {
			{
			switch ( LA(1)) {
			case BOOLEAN_LITERAL:
			case INT_LITERAL:
			case LONG_LITERAL:
			case FLOAT_LITERAL:
			case DOUBLE_LITERAL:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case IDENT:
			case SUPER:
			case VOID_:
			case BOOLEAN_:
			case BYTE_:
			case SHORT_:
			case CHAR_:
			case INT_:
			case LONG_:
			case FLOAT_:
			case DOUBLE_:
			case LT:
			case GT:
			case LINE:
			case LEFT_RIGHT_ARROW:
			case PLUS_LEFT_ARROW:
			case PLUS_ARROW:
			case PLUS_LINE:
			case PLUS_LEFT_RIGHT_ARROW:
			case SLASH_LEFT_ARROW:
			case SLASH_ARROW:
			case SLASH_LINE:
			case SLASH_LEFT_RIGHT_ARROW:
			case SUB:
			case LEFT_ARROW:
			case MUL:
			case ADD:
			case COM:
			case NOT:
			case DIV:
			case REM:
			case POW:
			case SHL:
			case SHR:
			case USHR:
			case LE:
			case GE:
			case CMP:
			case OR:
			case XOR:
			case AND:
			case COR:
			case CAND:
			case THIS:
			case IF:
			case THROW:
			case SYNCHRONIZED_:
			case BREAK:
			case CONTINUE:
			case FOR:
			case WHILE:
			case DO:
			case SWITCH:
			case CASE:
			case DEFAULT:
			case NULL_LITERAL:
			case LONG_LEFT_ARROW:
			case LONG_ARROW:
			case LONG_LEFT_RIGHT_ARROW:
			case QUOTE:
			case INC:
			case DEC:
			case IN:
			case GUARD:
			case RPAREN:
			case LBRACK:
			case LCURLY:
			case RCURLY:
			case COMMA:
			{
				productionStatements();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LPAREN:
			{
				productionBlock();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		else if (((_tokenSet_56.member(LA(1))) && (_tokenSet_50.member(LA(2))))&&(!prod)) {
			{
			_loop234:
			do {
				if ((_tokenSet_19.member(LA(1)))) {
					blockStatement();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop234;
				}
				
			} while (true);
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		if ( inputState.guessing==0 ) {
			switchGroup_AST = (AST)currentAST.root;
			switchGroup_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SWITCH_GROUP)).add(switchGroup_AST));
			currentAST.root = switchGroup_AST;
			currentAST.child = switchGroup_AST!=null &&switchGroup_AST.getFirstChild()!=null ?
				switchGroup_AST.getFirstChild() : switchGroup_AST;
			currentAST.advanceChildToEnd();
		}
		switchGroup_AST = (AST)currentAST.root;
		returnAST = switchGroup_AST;
	}
	
	public final void forControl(
		AST forRoot
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forControl_AST = null;
		AST s_AST = null;
		
				int ft;
			
		
		match(LPAREN);
		{
		if ((LA(1)==SEMI)) {
			insert(astFactory.create(SLIST));
			astFactory.addASTChild(currentAST, returnAST);
			basicForRest();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else {
			boolean synPredMatched240 = false;
			if (((_tokenSet_8.member(LA(1))) && (_tokenSet_9.member(LA(2))))) {
				int _m240 = mark();
				synPredMatched240 = true;
				inputState.guessing++;
				try {
					{
					varDeclarationPredicate();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched240 = false;
				}
				rewind(_m240);
inputState.guessing--;
			}
			if ( synPredMatched240 ) {
				ft=forVarControl();
				astFactory.addASTChild(currentAST, returnAST);
				setType(forRoot, ft);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((_tokenSet_10.member(LA(1))) && (_tokenSet_57.member(LA(2)))) {
				singleExpression();
				s_AST = (AST)returnAST;
				{
				switch ( LA(1)) {
				case SEMI:
				case COMMA:
				{
					forStatementListRest(s_AST);
					astFactory.addASTChild(currentAST, returnAST);
					basicForRest();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case RPAREN:
				{
					insert(s_AST);
					astFactory.addASTChild(currentAST, returnAST);
					insert(astFactory.create(VOID_));
					astFactory.addASTChild(currentAST, returnAST);
					setType(forRoot, ENHANCED_FOR);
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RPAREN);
			forControl_AST = (AST)currentAST.root;
			returnAST = forControl_AST;
		}
		
	public final void switchGroupLabel() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST switchGroupLabel_AST = null;
		
		{
		switch ( LA(1)) {
		case CASE:
		{
			AST tmp351_AST = null;
			tmp351_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp351_AST);
			match(CASE);
			conditionalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case DEFAULT:
		{
			AST tmp352_AST = null;
			tmp352_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp352_AST);
			match(DEFAULT);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(COLON);
		switchGroupLabel_AST = (AST)currentAST.root;
		returnAST = switchGroupLabel_AST;
	}
	
	public final void conditionalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conditionalExpression_AST = null;
		
		guardExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case QUESTION:
		{
			AST tmp354_AST = null;
			tmp354_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp354_AST);
			match(QUESTION);
			singleExpressionNoRange();
			astFactory.addASTChild(currentAST, returnAST);
			match(COLON);
			conditionalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case SEMI:
		case ASSIGN:
		case DOT:
		case RULE:
		case QUOTE:
		case ADD_ASSIGN:
		case SUB_ASSIGN:
		case MUL_ASSIGN:
		case DIV_ASSIGN:
		case REM_ASSIGN:
		case POW_ASSIGN:
		case SHR_ASSIGN:
		case USHR_ASSIGN:
		case SHL_ASSIGN:
		case AND_ASSIGN:
		case XOR_ASSIGN:
		case OR_ASSIGN:
		case DEFERRED_ASSIGN:
		case DEFERRED_RATE_ASSIGN:
		case DEFERRED_ADD:
		case DEFERRED_SUB:
		case DEFERRED_MUL:
		case DEFERRED_DIV:
		case DEFERRED_REM:
		case DEFERRED_POW:
		case DEFERRED_OR:
		case DEFERRED_AND:
		case DEFERRED_XOR:
		case DEFERRED_SHL:
		case DEFERRED_SHR:
		case DEFERRED_USHR:
		case RPAREN:
		case RBRACK:
		case RCURLY:
		case COLON:
		case COMMA:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		conditionalExpression_AST = (AST)currentAST.root;
		returnAST = conditionalExpression_AST;
	}
	
	public final void basicForRest() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST basicForRest_AST = null;
		AST s_AST = null;
		
		match(SEMI);
		{
		switch ( LA(1)) {
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case CONTEXT:
		case SUB:
		case ADD:
		case COM:
		case NOT:
		case THIS:
		case SYNCHRONIZED_:
		case NULL_LITERAL:
		case QUOTE:
		case INC:
		case DEC:
		case NEW:
		case ANNOTATION:
		case PRIVATE_:
		case PUBLIC_:
		case PROTECTED_:
		case STATIC_:
		case TRANSIENT_:
		case FINAL_:
		case ABSTRACT_:
		case NATIVE_:
		case VOLATILE_:
		case STRICT_:
		case CONST_:
		case LPAREN:
		{
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case SEMI:
		{
			insert(astFactory.create(BOOLEAN_LITERAL,"true"));
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(SEMI);
		{
		switch ( LA(1)) {
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case CONTEXT:
		case SUB:
		case ADD:
		case COM:
		case NOT:
		case THIS:
		case NULL_LITERAL:
		case QUOTE:
		case INC:
		case DEC:
		case NEW:
		case LPAREN:
		{
			singleExpression();
			s_AST = (AST)returnAST;
			forStatementListRest(s_AST);
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case RPAREN:
		{
			insert(astFactory.create(SLIST));
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		basicForRest_AST = (AST)currentAST.root;
		returnAST = basicForRest_AST;
	}
	
	public final int  forVarControl() throws RecognitionException, TokenStreamException {
		int forType;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forVarControl_AST = null;
		AST m_AST = null;
		AST t_AST = null;
		Token  id = null;
		AST id_AST = null;
		Token  c = null;
		AST c_AST = null;
		AST v_AST = null;
		
				forType = FOR;
			
		
		modifiers();
		m_AST = (AST)returnAST;
		typeSpec();
		t_AST = (AST)returnAST;
		id = LT(1);
		id_AST = astFactory.create(id);
		match(IDENT);
		{
		switch ( LA(1)) {
		case COLON:
		{
			c = LT(1);
			c_AST = astFactory.create(c);
			match(COLON);
			singleExpression();
			astFactory.addASTChild(currentAST, returnAST);
			insert(c_AST);
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				
								c_AST.setType (VARIABLE_DEF);
								c_AST.setFirstChild (m_AST);
								m_AST.setNextSibling (t_AST);
								t_AST.setNextSibling (id_AST);
								forType = ENHANCED_FOR;
							
			}
			break;
		}
		case SEMI:
		case ASSIGN:
		case LBRACK:
		case COMMA:
		{
			variableDeclaratorsRest(m_AST, t_AST, id_AST);
			v_AST = (AST)returnAST;
			insert((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SLIST)).add(v_AST)));
			astFactory.addASTChild(currentAST, returnAST);
			basicForRest();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		forVarControl_AST = (AST)currentAST.root;
		returnAST = forVarControl_AST;
		return forType;
	}
	
	public final void forStatementListRest(
		AST first
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forStatementListRest_AST = null;
		
		insert(first);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop249:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				singleExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop249;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			forStatementListRest_AST = (AST)currentAST.root;
			
					forStatementListRest_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SLIST)).add(forStatementListRest_AST));
				
			currentAST.root = forStatementListRest_AST;
			currentAST.child = forStatementListRest_AST!=null &&forStatementListRest_AST.getFirstChild()!=null ?
				forStatementListRest_AST.getFirstChild() : forStatementListRest_AST;
			currentAST.advanceChildToEnd();
		}
		forStatementListRest_AST = (AST)currentAST.root;
		returnAST = forStatementListRest_AST;
	}
	
	public final void variableDeclaratorsRest(
		AST mods, AST t, AST id
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variableDeclaratorsRest_AST = null;
		
		variableDeclaratorRest(mods, t, id, true);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop437:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				variableDeclarator(getASTFactory ().dupTree (mods),
							   getASTFactory ().dupTree (t), true);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop437;
			}
			
		} while (true);
		}
		variableDeclaratorsRest_AST = (AST)currentAST.root;
		returnAST = variableDeclaratorsRest_AST;
	}
	
	public final void singleExpressionNoRange() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST singleExpressionNoRange_AST = null;
		
		lambdaExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case ASSIGN:
		case ADD_ASSIGN:
		case SUB_ASSIGN:
		case MUL_ASSIGN:
		case DIV_ASSIGN:
		case REM_ASSIGN:
		case POW_ASSIGN:
		case SHR_ASSIGN:
		case USHR_ASSIGN:
		case SHL_ASSIGN:
		case AND_ASSIGN:
		case XOR_ASSIGN:
		case OR_ASSIGN:
		case DEFERRED_ASSIGN:
		case DEFERRED_RATE_ASSIGN:
		case DEFERRED_ADD:
		case DEFERRED_SUB:
		case DEFERRED_MUL:
		case DEFERRED_DIV:
		case DEFERRED_REM:
		case DEFERRED_POW:
		case DEFERRED_OR:
		case DEFERRED_AND:
		case DEFERRED_XOR:
		case DEFERRED_SHL:
		case DEFERRED_SHR:
		case DEFERRED_USHR:
		{
			{
			switch ( LA(1)) {
			case ASSIGN:
			{
				AST tmp360_AST = null;
				tmp360_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp360_AST);
				match(ASSIGN);
				break;
			}
			case ADD_ASSIGN:
			{
				AST tmp361_AST = null;
				tmp361_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp361_AST);
				match(ADD_ASSIGN);
				break;
			}
			case SUB_ASSIGN:
			{
				AST tmp362_AST = null;
				tmp362_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp362_AST);
				match(SUB_ASSIGN);
				break;
			}
			case MUL_ASSIGN:
			{
				AST tmp363_AST = null;
				tmp363_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp363_AST);
				match(MUL_ASSIGN);
				break;
			}
			case DIV_ASSIGN:
			{
				AST tmp364_AST = null;
				tmp364_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp364_AST);
				match(DIV_ASSIGN);
				break;
			}
			case REM_ASSIGN:
			{
				AST tmp365_AST = null;
				tmp365_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp365_AST);
				match(REM_ASSIGN);
				break;
			}
			case SHR_ASSIGN:
			{
				AST tmp366_AST = null;
				tmp366_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp366_AST);
				match(SHR_ASSIGN);
				break;
			}
			case USHR_ASSIGN:
			{
				AST tmp367_AST = null;
				tmp367_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp367_AST);
				match(USHR_ASSIGN);
				break;
			}
			case SHL_ASSIGN:
			{
				AST tmp368_AST = null;
				tmp368_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp368_AST);
				match(SHL_ASSIGN);
				break;
			}
			case AND_ASSIGN:
			{
				AST tmp369_AST = null;
				tmp369_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp369_AST);
				match(AND_ASSIGN);
				break;
			}
			case XOR_ASSIGN:
			{
				AST tmp370_AST = null;
				tmp370_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp370_AST);
				match(XOR_ASSIGN);
				break;
			}
			case OR_ASSIGN:
			{
				AST tmp371_AST = null;
				tmp371_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp371_AST);
				match(OR_ASSIGN);
				break;
			}
			case POW_ASSIGN:
			{
				AST tmp372_AST = null;
				tmp372_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp372_AST);
				match(POW_ASSIGN);
				break;
			}
			case DEFERRED_ASSIGN:
			{
				AST tmp373_AST = null;
				tmp373_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp373_AST);
				match(DEFERRED_ASSIGN);
				break;
			}
			case DEFERRED_RATE_ASSIGN:
			{
				AST tmp374_AST = null;
				tmp374_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp374_AST);
				match(DEFERRED_RATE_ASSIGN);
				break;
			}
			case DEFERRED_ADD:
			{
				AST tmp375_AST = null;
				tmp375_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp375_AST);
				match(DEFERRED_ADD);
				break;
			}
			case DEFERRED_SUB:
			{
				AST tmp376_AST = null;
				tmp376_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp376_AST);
				match(DEFERRED_SUB);
				break;
			}
			case DEFERRED_MUL:
			{
				AST tmp377_AST = null;
				tmp377_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp377_AST);
				match(DEFERRED_MUL);
				break;
			}
			case DEFERRED_DIV:
			{
				AST tmp378_AST = null;
				tmp378_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp378_AST);
				match(DEFERRED_DIV);
				break;
			}
			case DEFERRED_REM:
			{
				AST tmp379_AST = null;
				tmp379_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp379_AST);
				match(DEFERRED_REM);
				break;
			}
			case DEFERRED_SHR:
			{
				AST tmp380_AST = null;
				tmp380_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp380_AST);
				match(DEFERRED_SHR);
				break;
			}
			case DEFERRED_USHR:
			{
				AST tmp381_AST = null;
				tmp381_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp381_AST);
				match(DEFERRED_USHR);
				break;
			}
			case DEFERRED_SHL:
			{
				AST tmp382_AST = null;
				tmp382_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp382_AST);
				match(DEFERRED_SHL);
				break;
			}
			case DEFERRED_AND:
			{
				AST tmp383_AST = null;
				tmp383_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp383_AST);
				match(DEFERRED_AND);
				break;
			}
			case DEFERRED_XOR:
			{
				AST tmp384_AST = null;
				tmp384_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp384_AST);
				match(DEFERRED_XOR);
				break;
			}
			case DEFERRED_OR:
			{
				AST tmp385_AST = null;
				tmp385_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp385_AST);
				match(DEFERRED_OR);
				break;
			}
			case DEFERRED_POW:
			{
				AST tmp386_AST = null;
				tmp386_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp386_AST);
				match(DEFERRED_POW);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			singleExpressionNoRange();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case SEMI:
		case COLON:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		singleExpressionNoRange_AST = (AST)currentAST.root;
		returnAST = singleExpressionNoRange_AST;
	}
	
	public final void expressionOrDecl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expressionOrDecl_AST = null;
		
		boolean synPredMatched261 = false;
		if (((_tokenSet_8.member(LA(1))) && (_tokenSet_9.member(LA(2))))) {
			int _m261 = mark();
			synPredMatched261 = true;
			inputState.guessing++;
			try {
				{
				varDeclarationPredicate();
				}
			}
			catch (RecognitionException pe) {
				synPredMatched261 = false;
			}
			rewind(_m261);
inputState.guessing--;
		}
		if ( synPredMatched261 ) {
			singleDeclaration();
			astFactory.addASTChild(currentAST, returnAST);
			expressionOrDecl_AST = (AST)currentAST.root;
		}
		else if ((_tokenSet_10.member(LA(1))) && (_tokenSet_58.member(LA(2)))) {
			singleExpression();
			astFactory.addASTChild(currentAST, returnAST);
			expressionOrDecl_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = expressionOrDecl_AST;
	}
	
	public final void singleDeclaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST singleDeclaration_AST = null;
		AST m_AST = null;
		AST t_AST = null;
		
		modifiers();
		m_AST = (AST)returnAST;
		typeSpec();
		t_AST = (AST)returnAST;
		variableDeclarator(m_AST, t_AST, true);
		astFactory.addASTChild(currentAST, returnAST);
		singleDeclaration_AST = (AST)currentAST.root;
		returnAST = singleDeclaration_AST;
	}
	
	public final void lambdaExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lambdaExpression_AST = null;
		
		boolean synPredMatched276 = false;
		if (((_tokenSet_59.member(LA(1))) && (_tokenSet_60.member(LA(2))))) {
			int _m276 = mark();
			synPredMatched276 = true;
			inputState.guessing++;
			try {
				{
				typeSpec();
				match(IDENT);
				match(LAMBDA);
				}
			}
			catch (RecognitionException pe) {
				synPredMatched276 = false;
			}
			rewind(_m276);
inputState.guessing--;
		}
		if ( synPredMatched276 ) {
			lambdaExpression0();
			astFactory.addASTChild(currentAST, returnAST);
			lambdaExpression_AST = (AST)currentAST.root;
		}
		else {
			boolean synPredMatched278 = false;
			if (((_tokenSet_59.member(LA(1))) && (_tokenSet_60.member(LA(2))))) {
				int _m278 = mark();
				synPredMatched278 = true;
				inputState.guessing++;
				try {
					{
					match(VOID_);
					match(LAMBDA);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched278 = false;
				}
				rewind(_m278);
inputState.guessing--;
			}
			if ( synPredMatched278 ) {
				lambdaExpression0();
				astFactory.addASTChild(currentAST, returnAST);
				lambdaExpression_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_10.member(LA(1))) && (_tokenSet_61.member(LA(2)))) {
				conditionalExpression();
				astFactory.addASTChild(currentAST, returnAST);
				lambdaExpression_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			returnAST = lambdaExpression_AST;
		}
		
	public final void assignmentExpressionRest(
		AST e
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignmentExpressionRest_AST = null;
		
		insert(e);
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case ASSIGN:
		case ADD_ASSIGN:
		case SUB_ASSIGN:
		case MUL_ASSIGN:
		case DIV_ASSIGN:
		case REM_ASSIGN:
		case POW_ASSIGN:
		case SHR_ASSIGN:
		case USHR_ASSIGN:
		case SHL_ASSIGN:
		case AND_ASSIGN:
		case XOR_ASSIGN:
		case OR_ASSIGN:
		case DEFERRED_ASSIGN:
		case DEFERRED_RATE_ASSIGN:
		case DEFERRED_ADD:
		case DEFERRED_SUB:
		case DEFERRED_MUL:
		case DEFERRED_DIV:
		case DEFERRED_REM:
		case DEFERRED_POW:
		case DEFERRED_OR:
		case DEFERRED_AND:
		case DEFERRED_XOR:
		case DEFERRED_SHL:
		case DEFERRED_SHR:
		case DEFERRED_USHR:
		{
			{
			switch ( LA(1)) {
			case ASSIGN:
			{
				AST tmp387_AST = null;
				tmp387_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp387_AST);
				match(ASSIGN);
				break;
			}
			case ADD_ASSIGN:
			{
				AST tmp388_AST = null;
				tmp388_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp388_AST);
				match(ADD_ASSIGN);
				break;
			}
			case SUB_ASSIGN:
			{
				AST tmp389_AST = null;
				tmp389_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp389_AST);
				match(SUB_ASSIGN);
				break;
			}
			case MUL_ASSIGN:
			{
				AST tmp390_AST = null;
				tmp390_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp390_AST);
				match(MUL_ASSIGN);
				break;
			}
			case DIV_ASSIGN:
			{
				AST tmp391_AST = null;
				tmp391_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp391_AST);
				match(DIV_ASSIGN);
				break;
			}
			case REM_ASSIGN:
			{
				AST tmp392_AST = null;
				tmp392_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp392_AST);
				match(REM_ASSIGN);
				break;
			}
			case SHR_ASSIGN:
			{
				AST tmp393_AST = null;
				tmp393_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp393_AST);
				match(SHR_ASSIGN);
				break;
			}
			case USHR_ASSIGN:
			{
				AST tmp394_AST = null;
				tmp394_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp394_AST);
				match(USHR_ASSIGN);
				break;
			}
			case SHL_ASSIGN:
			{
				AST tmp395_AST = null;
				tmp395_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp395_AST);
				match(SHL_ASSIGN);
				break;
			}
			case AND_ASSIGN:
			{
				AST tmp396_AST = null;
				tmp396_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp396_AST);
				match(AND_ASSIGN);
				break;
			}
			case XOR_ASSIGN:
			{
				AST tmp397_AST = null;
				tmp397_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp397_AST);
				match(XOR_ASSIGN);
				break;
			}
			case OR_ASSIGN:
			{
				AST tmp398_AST = null;
				tmp398_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp398_AST);
				match(OR_ASSIGN);
				break;
			}
			case POW_ASSIGN:
			{
				AST tmp399_AST = null;
				tmp399_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp399_AST);
				match(POW_ASSIGN);
				break;
			}
			case DEFERRED_ASSIGN:
			{
				AST tmp400_AST = null;
				tmp400_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp400_AST);
				match(DEFERRED_ASSIGN);
				break;
			}
			case DEFERRED_RATE_ASSIGN:
			{
				AST tmp401_AST = null;
				tmp401_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp401_AST);
				match(DEFERRED_RATE_ASSIGN);
				break;
			}
			case DEFERRED_ADD:
			{
				AST tmp402_AST = null;
				tmp402_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp402_AST);
				match(DEFERRED_ADD);
				break;
			}
			case DEFERRED_SUB:
			{
				AST tmp403_AST = null;
				tmp403_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp403_AST);
				match(DEFERRED_SUB);
				break;
			}
			case DEFERRED_MUL:
			{
				AST tmp404_AST = null;
				tmp404_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp404_AST);
				match(DEFERRED_MUL);
				break;
			}
			case DEFERRED_DIV:
			{
				AST tmp405_AST = null;
				tmp405_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp405_AST);
				match(DEFERRED_DIV);
				break;
			}
			case DEFERRED_REM:
			{
				AST tmp406_AST = null;
				tmp406_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp406_AST);
				match(DEFERRED_REM);
				break;
			}
			case DEFERRED_SHR:
			{
				AST tmp407_AST = null;
				tmp407_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp407_AST);
				match(DEFERRED_SHR);
				break;
			}
			case DEFERRED_USHR:
			{
				AST tmp408_AST = null;
				tmp408_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp408_AST);
				match(DEFERRED_USHR);
				break;
			}
			case DEFERRED_SHL:
			{
				AST tmp409_AST = null;
				tmp409_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp409_AST);
				match(DEFERRED_SHL);
				break;
			}
			case DEFERRED_AND:
			{
				AST tmp410_AST = null;
				tmp410_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp410_AST);
				match(DEFERRED_AND);
				break;
			}
			case DEFERRED_XOR:
			{
				AST tmp411_AST = null;
				tmp411_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp411_AST);
				match(DEFERRED_XOR);
				break;
			}
			case DEFERRED_OR:
			{
				AST tmp412_AST = null;
				tmp412_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp412_AST);
				match(DEFERRED_OR);
				break;
			}
			case DEFERRED_POW:
			{
				AST tmp413_AST = null;
				tmp413_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp413_AST);
				match(DEFERRED_POW);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			singleExpression();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case SEMI:
		case DOT:
		case RULE:
		case QUOTE:
		case RPAREN:
		case RBRACK:
		case RCURLY:
		case COMMA:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		assignmentExpressionRest_AST = (AST)currentAST.root;
		returnAST = assignmentExpressionRest_AST;
	}
	
	public final void rangeExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rangeExpression_AST = null;
		Token  d = null;
		AST d_AST = null;
		
		lambdaExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop273:
		do {
			if ((LA(1)==COLON)) {
				d = LT(1);
				d_AST = astFactory.create(d);
				astFactory.makeASTRoot(currentAST, d_AST);
				match(COLON);
				lambdaExpression();
				astFactory.addASTChild(currentAST, returnAST);
				setType(d_AST, RANGE);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop273;
			}
			
		} while (true);
		}
		rangeExpression_AST = (AST)currentAST.root;
		returnAST = rangeExpression_AST;
	}
	
	public final void lambdaExpression0() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lambdaExpression0_AST = null;
		AST src_AST = null;
		Token  var = null;
		AST var_AST = null;
		Token  v1 = null;
		AST v1_AST = null;
		Token  la = null;
		AST la_AST = null;
		AST dest_AST = null;
		Token  v2 = null;
		AST v2_AST = null;
		Token  gen = null;
		AST gen_AST = null;
		AST expr_AST = null;
		
		{
		switch ( LA(1)) {
		case IDENT:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		{
			typeSpec();
			src_AST = (AST)returnAST;
			var = LT(1);
			var_AST = astFactory.create(var);
			match(IDENT);
			break;
		}
		case VOID_:
		{
			v1 = LT(1);
			v1_AST = astFactory.create(v1);
			match(VOID_);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		la = LT(1);
		la_AST = astFactory.create(la);
		match(LAMBDA);
		{
		switch ( LA(1)) {
		case IDENT:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		{
			typeSpec();
			dest_AST = (AST)returnAST;
			break;
		}
		case VOID_:
		{
			v2 = LT(1);
			v2_AST = astFactory.create(v2);
			match(VOID_);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case MUL:
		{
			gen = LT(1);
			gen_AST = astFactory.create(gen);
			match(MUL);
			setType(gen_AST, ITERATING_);
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case CONTEXT:
		case SUB:
		case ADD:
		case COM:
		case NOT:
		case THIS:
		case NULL_LITERAL:
		case QUOTE:
		case INC:
		case DEC:
		case NEW:
		case LPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		conditionalExpression();
		expr_AST = (AST)returnAST;
		if ( inputState.guessing==0 ) {
			lambdaExpression0_AST = (AST)currentAST.root;
			
					if (v1_AST != null)
					{
						src_AST = v1_AST;
					}
					if (v2_AST != null)
					{
						dest_AST = v2_AST;
					}
					StringBuffer cls = new StringBuffer ();
					AST param;
					AST method = astFactory.create(SLIST);
					if (isBuiltIn (src_AST.getType ()))
					{
						cls.append (Utils.firstToUpperCase (src_AST.getText ()));
						param = (src_AST.getType () == VOID_) ? null : (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(PARAMETER_DEF)).add(astFactory.create(MODIFIERS)).add(src_AST).add(var_AST));
					}
					else
					{
						cls.append ("Object");
						param = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(PARAMETER_DEF)).add(astFactory.create(MODIFIERS)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add(astFactory.create(IDENT,"java")).add(astFactory.create(IDENT,"lang")))).add(astFactory.create(IDENT,"Object")))).add(astFactory.create(IDENT,X_ID)));
						AST srcClone = getASTFactory ().dupTree (src_AST);
						method.addChild ((AST)astFactory.make( (new ASTArray(5)).add(astFactory.create(VARIABLE_DEF)).add(astFactory.create(MODIFIERS)).add(src_AST).add(var_AST).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ASSIGN)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(TYPECAST)).add(srcClone).add(astFactory.create(IDENT,X_ID))))))));
					}
					String eval;
					cls.append ("To");
					AST bdest;
					if (isBuiltIn (dest_AST.getType ()))
					{
						String s = Utils.firstToUpperCase (dest_AST.getText ());
						cls.append (s);
						eval = "evaluate" + s;
						bdest = dest_AST;
					}
					else
					{
						cls.append ("Object");
						eval = "evaluateObject";
						bdest = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add(astFactory.create(IDENT,"java")).add(astFactory.create(IDENT,"lang")))).add(astFactory.create(IDENT,"Object")));
						expr_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(TYPECHECK)).add(dest_AST).add(expr_AST));
					}
					if (gen_AST != null)
					{
						cls.append ("Generator");
						if (dest_AST.getType () == VOID_)
						{
							expr_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(TYPECAST)).add(astFactory.create(VOID_)).add(expr_AST));
						}
						method.addChild ((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(YIELD)).add(expr_AST)));
					}
					else
					{
						if (dest_AST.getType () == VOID_)
						{
							method.addChild (expr_AST);
							expr_AST = null;
						}
						method.addChild ((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(RETURN)).add(expr_AST)));
					}
					la_AST.setType (NEW);
					lambdaExpression0_AST =
						(AST)astFactory.make( (new ASTArray(4)).add(la_AST).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add(setToken(astFactory.create(IDENT,"de"),la_AST)).add(setToken(astFactory.create(IDENT,"grogra"),la_AST)))).add(setToken(astFactory.create(IDENT,"xl"),la_AST)))).add(setToken(astFactory.create(IDENT,"lang"),la_AST)))).add(setToken(astFactory.create(IDENT,cls.toString()),la_AST)))).add(astFactory.create(ARGLIST)).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CLASS)).add((AST)astFactory.make( (new ASTArray(7)).add(astFactory.create(METHOD)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(MODIFIERS)).add(astFactory.create(PUBLIC_)).add(gen_AST))).add(bdest).add(astFactory.create(IDENT,eval)).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PARAMETERS)).add(param))).add(astFactory.create(THROWS)).add(method))))));
				
			currentAST.root = lambdaExpression0_AST;
			currentAST.child = lambdaExpression0_AST!=null &&lambdaExpression0_AST.getFirstChild()!=null ?
				lambdaExpression0_AST.getFirstChild() : lambdaExpression0_AST;
			currentAST.advanceChildToEnd();
		}
		returnAST = lambdaExpression0_AST;
	}
	
	public final void guardExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST guardExpression_AST = null;
		
		logicalOrExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop287:
		do {
			if ((LA(1)==GUARD)) {
				AST tmp414_AST = null;
				tmp414_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp414_AST);
				match(GUARD);
				logicalOrExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop287;
			}
			
		} while (true);
		}
		guardExpression_AST = (AST)currentAST.root;
		returnAST = guardExpression_AST;
	}
	
	public final void logicalOrExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalOrExpression_AST = null;
		
		logicalAndExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop290:
		do {
			if ((LA(1)==COR)) {
				AST tmp415_AST = null;
				tmp415_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp415_AST);
				match(COR);
				logicalAndExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop290;
			}
			
		} while (true);
		}
		logicalOrExpression_AST = (AST)currentAST.root;
		returnAST = logicalOrExpression_AST;
	}
	
	public final void logicalAndExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalAndExpression_AST = null;
		
		inclusiveOrExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop293:
		do {
			if ((LA(1)==CAND)) {
				AST tmp416_AST = null;
				tmp416_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp416_AST);
				match(CAND);
				inclusiveOrExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop293;
			}
			
		} while (true);
		}
		logicalAndExpression_AST = (AST)currentAST.root;
		returnAST = logicalAndExpression_AST;
	}
	
	public final void inclusiveOrExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inclusiveOrExpression_AST = null;
		
		exclusiveOrExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop296:
		do {
			if ((LA(1)==OR)) {
				AST tmp417_AST = null;
				tmp417_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp417_AST);
				match(OR);
				exclusiveOrExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop296;
			}
			
		} while (true);
		}
		inclusiveOrExpression_AST = (AST)currentAST.root;
		returnAST = inclusiveOrExpression_AST;
	}
	
	public final void exclusiveOrExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exclusiveOrExpression_AST = null;
		
		andExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop299:
		do {
			if ((LA(1)==XOR)) {
				AST tmp418_AST = null;
				tmp418_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp418_AST);
				match(XOR);
				andExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop299;
			}
			
		} while (true);
		}
		exclusiveOrExpression_AST = (AST)currentAST.root;
		returnAST = exclusiveOrExpression_AST;
	}
	
	public final void andExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST andExpression_AST = null;
		
		equalityExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop302:
		do {
			if ((LA(1)==AND)) {
				AST tmp419_AST = null;
				tmp419_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp419_AST);
				match(AND);
				equalityExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop302;
			}
			
		} while (true);
		}
		andExpression_AST = (AST)currentAST.root;
		returnAST = andExpression_AST;
	}
	
	public final void equalityExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equalityExpression_AST = null;
		
		relationalExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop306:
		do {
			if ((LA(1)==NOT_EQUALS||LA(1)==EQUALS)) {
				{
				switch ( LA(1)) {
				case NOT_EQUALS:
				{
					AST tmp420_AST = null;
					tmp420_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp420_AST);
					match(NOT_EQUALS);
					break;
				}
				case EQUALS:
				{
					AST tmp421_AST = null;
					tmp421_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp421_AST);
					match(EQUALS);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				relationalExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop306;
			}
			
		} while (true);
		}
		equalityExpression_AST = (AST)currentAST.root;
		returnAST = equalityExpression_AST;
	}
	
	public final void relationalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relationalExpression_AST = null;
		
		shiftExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case SEMI:
		case ASSIGN:
		case LT:
		case GT:
		case LINE:
		case LEFT_RIGHT_ARROW:
		case PLUS_LEFT_ARROW:
		case PLUS_ARROW:
		case PLUS_LINE:
		case PLUS_LEFT_RIGHT_ARROW:
		case SLASH_LEFT_ARROW:
		case SLASH_ARROW:
		case SLASH_LINE:
		case SLASH_LEFT_RIGHT_ARROW:
		case DOT:
		case QUESTION:
		case RULE:
		case LE:
		case GE:
		case CMP:
		case NOT_EQUALS:
		case EQUALS:
		case OR:
		case XOR:
		case AND:
		case COR:
		case CAND:
		case LONG_LEFT_ARROW:
		case LONG_ARROW:
		case LONG_LEFT_RIGHT_ARROW:
		case QUOTE:
		case ADD_ASSIGN:
		case SUB_ASSIGN:
		case MUL_ASSIGN:
		case DIV_ASSIGN:
		case REM_ASSIGN:
		case POW_ASSIGN:
		case SHR_ASSIGN:
		case USHR_ASSIGN:
		case SHL_ASSIGN:
		case AND_ASSIGN:
		case XOR_ASSIGN:
		case OR_ASSIGN:
		case DEFERRED_ASSIGN:
		case DEFERRED_RATE_ASSIGN:
		case DEFERRED_ADD:
		case DEFERRED_SUB:
		case DEFERRED_MUL:
		case DEFERRED_DIV:
		case DEFERRED_REM:
		case DEFERRED_POW:
		case DEFERRED_OR:
		case DEFERRED_AND:
		case DEFERRED_XOR:
		case DEFERRED_SHL:
		case DEFERRED_SHR:
		case DEFERRED_USHR:
		case IN:
		case GUARD:
		case RPAREN:
		case RBRACK:
		case RCURLY:
		case COLON:
		case COMMA:
		{
			{
			_loop311:
			do {
				if ((_tokenSet_62.member(LA(1)))) {
					{
					switch ( LA(1)) {
					case LT:
					{
						AST tmp422_AST = null;
						tmp422_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp422_AST);
						match(LT);
						break;
					}
					case GT:
					{
						AST tmp423_AST = null;
						tmp423_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp423_AST);
						match(GT);
						break;
					}
					case LE:
					{
						AST tmp424_AST = null;
						tmp424_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp424_AST);
						match(LE);
						break;
					}
					case GE:
					{
						AST tmp425_AST = null;
						tmp425_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp425_AST);
						match(GE);
						break;
					}
					case IN:
					{
						AST tmp426_AST = null;
						tmp426_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp426_AST);
						match(IN);
						break;
					}
					case CMP:
					{
						AST tmp427_AST = null;
						tmp427_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp427_AST);
						match(CMP);
						break;
					}
					case LONG_LEFT_ARROW:
					{
						AST tmp428_AST = null;
						tmp428_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp428_AST);
						match(LONG_LEFT_ARROW);
						break;
					}
					case LONG_ARROW:
					{
						AST tmp429_AST = null;
						tmp429_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp429_AST);
						match(LONG_ARROW);
						break;
					}
					case LONG_LEFT_RIGHT_ARROW:
					{
						AST tmp430_AST = null;
						tmp430_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp430_AST);
						match(LONG_LEFT_RIGHT_ARROW);
						break;
					}
					case PLUS_LEFT_ARROW:
					{
						AST tmp431_AST = null;
						tmp431_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp431_AST);
						match(PLUS_LEFT_ARROW);
						break;
					}
					case PLUS_ARROW:
					{
						AST tmp432_AST = null;
						tmp432_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp432_AST);
						match(PLUS_ARROW);
						break;
					}
					case PLUS_LINE:
					{
						AST tmp433_AST = null;
						tmp433_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp433_AST);
						match(PLUS_LINE);
						break;
					}
					case PLUS_LEFT_RIGHT_ARROW:
					{
						AST tmp434_AST = null;
						tmp434_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp434_AST);
						match(PLUS_LEFT_RIGHT_ARROW);
						break;
					}
					case SLASH_LEFT_ARROW:
					{
						AST tmp435_AST = null;
						tmp435_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp435_AST);
						match(SLASH_LEFT_ARROW);
						break;
					}
					case SLASH_ARROW:
					{
						AST tmp436_AST = null;
						tmp436_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp436_AST);
						match(SLASH_ARROW);
						break;
					}
					case SLASH_LINE:
					{
						AST tmp437_AST = null;
						tmp437_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp437_AST);
						match(SLASH_LINE);
						break;
					}
					case SLASH_LEFT_RIGHT_ARROW:
					{
						AST tmp438_AST = null;
						tmp438_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp438_AST);
						match(SLASH_LEFT_RIGHT_ARROW);
						break;
					}
					case LINE:
					{
						AST tmp439_AST = null;
						tmp439_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp439_AST);
						match(LINE);
						break;
					}
					case LEFT_RIGHT_ARROW:
					{
						AST tmp440_AST = null;
						tmp440_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp440_AST);
						match(LEFT_RIGHT_ARROW);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					shiftExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop311;
				}
				
			} while (true);
			}
			break;
		}
		case INSTANCEOF:
		{
			AST tmp441_AST = null;
			tmp441_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp441_AST);
			match(INSTANCEOF);
			refTypeSpec();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		relationalExpression_AST = (AST)currentAST.root;
		returnAST = relationalExpression_AST;
	}
	
	public final void shiftExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST shiftExpression_AST = null;
		
		additiveExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop315:
		do {
			if (((LA(1) >= SHL && LA(1) <= USHR))) {
				{
				switch ( LA(1)) {
				case SHL:
				{
					AST tmp442_AST = null;
					tmp442_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp442_AST);
					match(SHL);
					break;
				}
				case SHR:
				{
					AST tmp443_AST = null;
					tmp443_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp443_AST);
					match(SHR);
					break;
				}
				case USHR:
				{
					AST tmp444_AST = null;
					tmp444_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp444_AST);
					match(USHR);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				additiveExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop315;
			}
			
		} while (true);
		}
		shiftExpression_AST = (AST)currentAST.root;
		returnAST = shiftExpression_AST;
	}
	
	public final void refTypeSpec() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST refTypeSpec_AST = null;
		AST t_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		
		switch ( LA(1)) {
		case IDENT:
		{
			name();
			t_AST = (AST)returnAST;
			typeSpecRest(t_AST);
			astFactory.addASTChild(currentAST, returnAST);
			refTypeSpec_AST = (AST)currentAST.root;
			break;
		}
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		{
			builtInType();
			astFactory.addASTChild(currentAST, returnAST);
			{
			int _cnt408=0;
			_loop408:
			do {
				if ((LA(1)==LBRACK)) {
					lb = LT(1);
					lb_AST = astFactory.create(lb);
					astFactory.makeASTRoot(currentAST, lb_AST);
					match(LBRACK);
					setArrayDeclarator_RBRACK(lb_AST);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt408>=1 ) { break _loop408; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt408++;
			} while (true);
			}
			refTypeSpec_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = refTypeSpec_AST;
	}
	
	public final void additiveExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST additiveExpression_AST = null;
		
		multiplicativeExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop319:
		do {
			if ((LA(1)==SUB||LA(1)==ADD)) {
				{
				switch ( LA(1)) {
				case ADD:
				{
					AST tmp445_AST = null;
					tmp445_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp445_AST);
					match(ADD);
					break;
				}
				case SUB:
				{
					AST tmp446_AST = null;
					tmp446_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp446_AST);
					match(SUB);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				multiplicativeExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop319;
			}
			
		} while (true);
		}
		additiveExpression_AST = (AST)currentAST.root;
		returnAST = additiveExpression_AST;
	}
	
	public final void multiplicativeExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST multiplicativeExpression_AST = null;
		
		powerExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop323:
		do {
			if ((LA(1)==MUL||LA(1)==DIV||LA(1)==REM)) {
				{
				switch ( LA(1)) {
				case MUL:
				{
					AST tmp447_AST = null;
					tmp447_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp447_AST);
					match(MUL);
					break;
				}
				case DIV:
				{
					AST tmp448_AST = null;
					tmp448_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp448_AST);
					match(DIV);
					break;
				}
				case REM:
				{
					AST tmp449_AST = null;
					tmp449_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp449_AST);
					match(REM);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				powerExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop323;
			}
			
		} while (true);
		}
		multiplicativeExpression_AST = (AST)currentAST.root;
		returnAST = multiplicativeExpression_AST;
	}
	
	public final void powerExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST powerExpression_AST = null;
		
		unaryExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case POW:
		{
			AST tmp450_AST = null;
			tmp450_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp450_AST);
			match(POW);
			powerExpression();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case SEMI:
		case ASSIGN:
		case LT:
		case GT:
		case LINE:
		case LEFT_RIGHT_ARROW:
		case PLUS_LEFT_ARROW:
		case PLUS_ARROW:
		case PLUS_LINE:
		case PLUS_LEFT_RIGHT_ARROW:
		case SLASH_LEFT_ARROW:
		case SLASH_ARROW:
		case SLASH_LINE:
		case SLASH_LEFT_RIGHT_ARROW:
		case DOT:
		case SUB:
		case QUESTION:
		case MUL:
		case ADD:
		case RULE:
		case DIV:
		case REM:
		case SHL:
		case SHR:
		case USHR:
		case LE:
		case GE:
		case CMP:
		case NOT_EQUALS:
		case EQUALS:
		case OR:
		case XOR:
		case AND:
		case COR:
		case CAND:
		case LONG_LEFT_ARROW:
		case LONG_ARROW:
		case LONG_LEFT_RIGHT_ARROW:
		case INSTANCEOF:
		case QUOTE:
		case ADD_ASSIGN:
		case SUB_ASSIGN:
		case MUL_ASSIGN:
		case DIV_ASSIGN:
		case REM_ASSIGN:
		case POW_ASSIGN:
		case SHR_ASSIGN:
		case USHR_ASSIGN:
		case SHL_ASSIGN:
		case AND_ASSIGN:
		case XOR_ASSIGN:
		case OR_ASSIGN:
		case DEFERRED_ASSIGN:
		case DEFERRED_RATE_ASSIGN:
		case DEFERRED_ADD:
		case DEFERRED_SUB:
		case DEFERRED_MUL:
		case DEFERRED_DIV:
		case DEFERRED_REM:
		case DEFERRED_POW:
		case DEFERRED_OR:
		case DEFERRED_AND:
		case DEFERRED_XOR:
		case DEFERRED_SHL:
		case DEFERRED_SHR:
		case DEFERRED_USHR:
		case IN:
		case GUARD:
		case RPAREN:
		case RBRACK:
		case RCURLY:
		case COLON:
		case COMMA:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		powerExpression_AST = (AST)currentAST.root;
		returnAST = powerExpression_AST;
	}
	
	public final void unaryExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryExpression_AST = null;
		Token  a = null;
		AST a_AST = null;
		AST op_AST = null;
		
		switch ( LA(1)) {
		case INC:
		{
			AST tmp451_AST = null;
			tmp451_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp451_AST);
			match(INC);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			unaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case DEC:
		{
			AST tmp452_AST = null;
			tmp452_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp452_AST);
			match(DEC);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			unaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case ADD:
		{
			a = LT(1);
			a_AST = astFactory.create(a);
			astFactory.makeASTRoot(currentAST, a_AST);
			match(ADD);
			setType(a_AST, POS);
			astFactory.addASTChild(currentAST, returnAST);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			unaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case SUB:
		{
			AST tmp453_AST = null;
			tmp453_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp453_AST);
			match(SUB);
			unaryExpression();
			op_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				unaryExpression_AST = (AST)currentAST.root;
				
							Token t = ((ASTWithToken) op_AST).token;
							if ((t != null)
								&& (((t.getType () == INT_LITERAL)
									 && ((de.grogra.grammar.IntLiteral) t).isDecimal ())
									|| ((t.getType () == LONG_LITERAL)
										&& ((de.grogra.grammar.LongLiteral) t).isDecimal ())))
							{
								t.setText ('-' + t.getText ());
								op_AST.setText (t.getText ());
								unaryExpression_AST = op_AST;
							}
							else
							{
								unaryExpression_AST.setType (NEG);
							}
						
				currentAST.root = unaryExpression_AST;
				currentAST.child = unaryExpression_AST!=null &&unaryExpression_AST.getFirstChild()!=null ?
					unaryExpression_AST.getFirstChild() : unaryExpression_AST;
				currentAST.advanceChildToEnd();
			}
			unaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case CONTEXT:
		case COM:
		case NOT:
		case THIS:
		case NULL_LITERAL:
		case QUOTE:
		case NEW:
		case LPAREN:
		{
			unaryExpressionNoBinaryOp();
			astFactory.addASTChild(currentAST, returnAST);
			unaryExpression_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = unaryExpression_AST;
	}
	
	public final void unaryExpressionNoBinaryOp() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryExpressionNoBinaryOp_AST = null;
		Token  lp1 = null;
		AST lp1_AST = null;
		Token  lp2 = null;
		AST lp2_AST = null;
		
		switch ( LA(1)) {
		case COM:
		{
			AST tmp454_AST = null;
			tmp454_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp454_AST);
			match(COM);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			unaryExpressionNoBinaryOp_AST = (AST)currentAST.root;
			break;
		}
		case NOT:
		{
			AST tmp455_AST = null;
			tmp455_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp455_AST);
			match(NOT);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			unaryExpressionNoBinaryOp_AST = (AST)currentAST.root;
			break;
		}
		default:
			boolean synPredMatched332 = false;
			if (((LA(1)==LPAREN) && (_tokenSet_59.member(LA(2))))) {
				int _m332 = mark();
				synPredMatched332 = true;
				inputState.guessing++;
				try {
					{
					match(LPAREN);
					{
					if (((LA(1) >= BOOLEAN_ && LA(1) <= DOUBLE_)) && (LA(2)==RPAREN)) {
						builtInType();
					}
					else if ((LA(1)==VOID_)) {
						match(VOID_);
					}
					else if ((_tokenSet_4.member(LA(1))) && (LA(2)==DOT||LA(2)==LBRACK)) {
						type();
						{
						int _cnt331=0;
						_loop331:
						do {
							if ((LA(1)==LBRACK)) {
								match(LBRACK);
								match(RBRACK);
							}
							else {
								if ( _cnt331>=1 ) { break _loop331; } else {throw new NoViableAltException(LT(1), getFilename());}
							}
							
							_cnt331++;
						} while (true);
						}
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					
					}
					match(RPAREN);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched332 = false;
				}
				rewind(_m332);
inputState.guessing--;
			}
			if ( synPredMatched332 ) {
				lp1 = LT(1);
				lp1_AST = astFactory.create(lp1);
				astFactory.makeASTRoot(currentAST, lp1_AST);
				match(LPAREN);
				setType(lp1_AST, TYPECAST);
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case IDENT:
				case BOOLEAN_:
				case BYTE_:
				case SHORT_:
				case CHAR_:
				case INT_:
				case LONG_:
				case FLOAT_:
				case DOUBLE_:
				{
					typeSpec();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case VOID_:
				{
					AST tmp456_AST = null;
					tmp456_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp456_AST);
					match(VOID_);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(RPAREN);
				unaryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpressionNoBinaryOp_AST = (AST)currentAST.root;
			}
			else {
				boolean synPredMatched335 = false;
				if (((LA(1)==LPAREN) && (_tokenSet_4.member(LA(2))))) {
					int _m335 = mark();
					synPredMatched335 = true;
					inputState.guessing++;
					try {
						{
						match(LPAREN);
						typeSpec();
						match(RPAREN);
						unaryExpressionNoBinaryOpPredicate();
						}
					}
					catch (RecognitionException pe) {
						synPredMatched335 = false;
					}
					rewind(_m335);
inputState.guessing--;
				}
				if ( synPredMatched335 ) {
					lp2 = LT(1);
					lp2_AST = astFactory.create(lp2);
					astFactory.makeASTRoot(currentAST, lp2_AST);
					match(LPAREN);
					setType(lp2_AST, TYPECAST);
					astFactory.addASTChild(currentAST, returnAST);
					typeSpec();
					astFactory.addASTChild(currentAST, returnAST);
					match(RPAREN);
					unaryExpressionNoBinaryOp();
					astFactory.addASTChild(currentAST, returnAST);
					unaryExpressionNoBinaryOp_AST = (AST)currentAST.root;
				}
				else if ((_tokenSet_46.member(LA(1))) && (_tokenSet_61.member(LA(2)))) {
					postfixExpression();
					astFactory.addASTChild(currentAST, returnAST);
					unaryExpressionNoBinaryOp_AST = (AST)currentAST.root;
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}}
			returnAST = unaryExpressionNoBinaryOp_AST;
		}
		
	public final void type() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST type_AST = null;
		
		switch ( LA(1)) {
		case IDENT:
		{
			name();
			astFactory.addASTChild(currentAST, returnAST);
			type_AST = (AST)currentAST.root;
			break;
		}
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		{
			builtInType();
			astFactory.addASTChild(currentAST, returnAST);
			type_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = type_AST;
	}
	
	public final void unaryExpressionNoBinaryOpPredicate() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryExpressionNoBinaryOpPredicate_AST = null;
		
		switch ( LA(1)) {
		case COM:
		{
			AST tmp459_AST = null;
			tmp459_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp459_AST);
			match(COM);
			unaryExpressionNoBinaryOpPredicate_AST = (AST)currentAST.root;
			break;
		}
		case NOT:
		{
			AST tmp460_AST = null;
			tmp460_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp460_AST);
			match(NOT);
			unaryExpressionNoBinaryOpPredicate_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			AST tmp461_AST = null;
			tmp461_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp461_AST);
			match(LPAREN);
			unaryExpressionNoBinaryOpPredicate_AST = (AST)currentAST.root;
			break;
		}
		case NEW:
		{
			AST tmp462_AST = null;
			tmp462_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp462_AST);
			match(NEW);
			unaryExpressionNoBinaryOpPredicate_AST = (AST)currentAST.root;
			break;
		}
		case CONTEXT:
		{
			AST tmp463_AST = null;
			tmp463_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp463_AST);
			match(CONTEXT);
			unaryExpressionNoBinaryOpPredicate_AST = (AST)currentAST.root;
			break;
		}
		case IDENT:
		{
			AST tmp464_AST = null;
			tmp464_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp464_AST);
			match(IDENT);
			unaryExpressionNoBinaryOpPredicate_AST = (AST)currentAST.root;
			break;
		}
		case THIS:
		{
			AST tmp465_AST = null;
			tmp465_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp465_AST);
			match(THIS);
			unaryExpressionNoBinaryOpPredicate_AST = (AST)currentAST.root;
			break;
		}
		case SUPER:
		{
			AST tmp466_AST = null;
			tmp466_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp466_AST);
			match(SUPER);
			unaryExpressionNoBinaryOpPredicate_AST = (AST)currentAST.root;
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case NULL_LITERAL:
		{
			literal();
			astFactory.addASTChild(currentAST, returnAST);
			unaryExpressionNoBinaryOpPredicate_AST = (AST)currentAST.root;
			break;
		}
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		{
			builtInType();
			astFactory.addASTChild(currentAST, returnAST);
			unaryExpressionNoBinaryOpPredicate_AST = (AST)currentAST.root;
			break;
		}
		case VOID_:
		{
			AST tmp467_AST = null;
			tmp467_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp467_AST);
			match(VOID_);
			unaryExpressionNoBinaryOpPredicate_AST = (AST)currentAST.root;
			break;
		}
		case QUOTE:
		{
			AST tmp468_AST = null;
			tmp468_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp468_AST);
			match(QUOTE);
			unaryExpressionNoBinaryOpPredicate_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = unaryExpressionNoBinaryOpPredicate_AST;
	}
	
	public final void postfixExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST postfixExpression_AST = null;
		Token  i = null;
		AST i_AST = null;
		Token  d = null;
		AST d_AST = null;
		
		arrowExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop338:
		do {
			switch ( LA(1)) {
			case INC:
			{
				i = LT(1);
				i_AST = astFactory.create(i);
				astFactory.makeASTRoot(currentAST, i_AST);
				match(INC);
				setType(i_AST, POST_INC);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case DEC:
			{
				d = LT(1);
				d_AST = astFactory.create(d);
				astFactory.makeASTRoot(currentAST, d_AST);
				match(DEC);
				setType(d_AST, POST_DEC);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				break _loop338;
			}
			}
		} while (true);
		}
		postfixExpression_AST = (AST)currentAST.root;
		returnAST = postfixExpression_AST;
	}
	
	public final void arrowExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrowExpression_AST = null;
		
		selectorExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop342:
		do {
			if ((LA(1)==LEFT_ARROW||LA(1)==ARROW)) {
				{
				switch ( LA(1)) {
				case ARROW:
				{
					AST tmp469_AST = null;
					tmp469_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp469_AST);
					match(ARROW);
					break;
				}
				case LEFT_ARROW:
				{
					AST tmp470_AST = null;
					tmp470_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp470_AST);
					match(LEFT_ARROW);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				selectorExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop342;
			}
			
		} while (true);
		}
		arrowExpression_AST = (AST)currentAST.root;
		returnAST = arrowExpression_AST;
	}
	
	public final void primary() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primary_AST = null;
		
		switch ( LA(1)) {
		case NEW:
		{
			AST tmp471_AST = null;
			tmp471_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp471_AST);
			match(NEW);
			creator();
			astFactory.addASTChild(currentAST, returnAST);
			primary_AST = (AST)currentAST.root;
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case CONTEXT:
		case THIS:
		case NULL_LITERAL:
		case QUOTE:
		case LPAREN:
		{
			primaryNoCreator();
			astFactory.addASTChild(currentAST, returnAST);
			primary_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = primary_AST;
	}
	
	public final void anonymousClassBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST anonymousClassBody_AST = null;
		Token  lc = null;
		AST lc_AST = null;
		
		lc = LT(1);
		lc_AST = astFactory.create(lc);
		astFactory.makeASTRoot(currentAST, lc_AST);
		match(LCURLY);
		{
		_loop356:
		do {
			if ((_tokenSet_1.member(LA(1)))) {
				typeMember(false, true);
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop356;
			}
			
		} while (true);
		}
		match(RCURLY);
		setType(lc_AST, CLASS);
		astFactory.addASTChild(currentAST, returnAST);
		anonymousClassBody_AST = (AST)currentAST.root;
		returnAST = anonymousClassBody_AST;
	}
	
	public final void creator() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST creator_AST = null;
		AST t_AST = null;
		Token  rb = null;
		AST rb_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		AST i_AST = null;
		AST l_AST = null;
		Token  l2 = null;
		AST l2_AST = null;
		
		type();
		t_AST = (AST)returnAST;
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case LPAREN:
		{
			match(LPAREN);
			argList(false);
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			{
			switch ( LA(1)) {
			case LCURLY:
			{
				anonymousClassBody();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SEMI:
			case ASSIGN:
			case LT:
			case GT:
			case LINE:
			case LEFT_RIGHT_ARROW:
			case PLUS_LEFT_ARROW:
			case PLUS_ARROW:
			case PLUS_LINE:
			case PLUS_LEFT_RIGHT_ARROW:
			case SLASH_LEFT_ARROW:
			case SLASH_ARROW:
			case SLASH_LINE:
			case SLASH_LEFT_RIGHT_ARROW:
			case DOT:
			case SUB:
			case LEFT_ARROW:
			case ARROW:
			case QUESTION:
			case MUL:
			case ADD:
			case RULE:
			case DIV:
			case REM:
			case POW:
			case SHL:
			case SHR:
			case USHR:
			case LE:
			case GE:
			case CMP:
			case NOT_EQUALS:
			case EQUALS:
			case OR:
			case XOR:
			case AND:
			case COR:
			case CAND:
			case LONG_LEFT_ARROW:
			case LONG_ARROW:
			case LONG_LEFT_RIGHT_ARROW:
			case INSTANCEOF:
			case QUOTE:
			case ADD_ASSIGN:
			case SUB_ASSIGN:
			case MUL_ASSIGN:
			case DIV_ASSIGN:
			case REM_ASSIGN:
			case POW_ASSIGN:
			case SHR_ASSIGN:
			case USHR_ASSIGN:
			case SHL_ASSIGN:
			case AND_ASSIGN:
			case XOR_ASSIGN:
			case OR_ASSIGN:
			case DEFERRED_ASSIGN:
			case DEFERRED_RATE_ASSIGN:
			case DEFERRED_ADD:
			case DEFERRED_SUB:
			case DEFERRED_MUL:
			case DEFERRED_DIV:
			case DEFERRED_REM:
			case DEFERRED_POW:
			case DEFERRED_OR:
			case DEFERRED_AND:
			case DEFERRED_XOR:
			case DEFERRED_SHL:
			case DEFERRED_SHR:
			case DEFERRED_USHR:
			case INC:
			case DEC:
			case IN:
			case GUARD:
			case LPAREN:
			case RPAREN:
			case LBRACK:
			case RBRACK:
			case RCURLY:
			case COLON:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case LBRACK:
		{
			match(LBRACK);
			{
			switch ( LA(1)) {
			case RBRACK:
			{
				rb = LT(1);
				rb_AST = astFactory.create(rb);
				astFactory.makeASTRoot(currentAST, rb_AST);
				match(RBRACK);
				setArrayDeclarator(rb_AST);
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop380:
				do {
					if ((LA(1)==LBRACK)) {
						lb = LT(1);
						lb_AST = astFactory.create(lb);
						astFactory.makeASTRoot(currentAST, lb_AST);
						match(LBRACK);
						setArrayDeclarator_RBRACK(lb_AST);
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop380;
					}
					
				} while (true);
				}
				arrayInitializer((rb_AST == null) ? t_AST
								   : (lb_AST == null) ? rb_AST : lb_AST);
				i_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					creator_AST = (AST)currentAST.root;
					creator_AST.setNextSibling (i_AST);
				}
				break;
			}
			case BOOLEAN_LITERAL:
			case INT_LITERAL:
			case LONG_LITERAL:
			case FLOAT_LITERAL:
			case DOUBLE_LITERAL:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case IDENT:
			case SUPER:
			case VOID_:
			case BOOLEAN_:
			case BYTE_:
			case SHORT_:
			case CHAR_:
			case INT_:
			case LONG_:
			case FLOAT_:
			case DOUBLE_:
			case CONTEXT:
			case SUB:
			case ADD:
			case COM:
			case NOT:
			case THIS:
			case SYNCHRONIZED_:
			case NULL_LITERAL:
			case QUOTE:
			case INC:
			case DEC:
			case NEW:
			case ANNOTATION:
			case PRIVATE_:
			case PUBLIC_:
			case PROTECTED_:
			case STATIC_:
			case TRANSIENT_:
			case FINAL_:
			case ABSTRACT_:
			case NATIVE_:
			case VOLATILE_:
			case STRICT_:
			case CONST_:
			case LPAREN:
			{
				dimensionList();
				l_AST = (AST)returnAST;
				{
				_loop382:
				do {
					if ((LA(1)==LBRACK) && (LA(2)==RBRACK)) {
						l2 = LT(1);
						l2_AST = astFactory.create(l2);
						astFactory.makeASTRoot(currentAST, l2_AST);
						match(LBRACK);
						setArrayDeclarator_RBRACK(l2_AST);
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop382;
					}
					
				} while (true);
				}
				if ( inputState.guessing==0 ) {
					creator_AST = (AST)currentAST.root;
					creator_AST.setNextSibling (l_AST);
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		creator_AST = (AST)currentAST.root;
		returnAST = creator_AST;
	}
	
	public final void literal() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST literal_AST = null;
		
		switch ( LA(1)) {
		case BOOLEAN_LITERAL:
		{
			AST tmp476_AST = null;
			tmp476_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp476_AST);
			match(BOOLEAN_LITERAL);
			literal_AST = (AST)currentAST.root;
			break;
		}
		case INT_LITERAL:
		{
			AST tmp477_AST = null;
			tmp477_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp477_AST);
			match(INT_LITERAL);
			literal_AST = (AST)currentAST.root;
			break;
		}
		case LONG_LITERAL:
		{
			AST tmp478_AST = null;
			tmp478_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp478_AST);
			match(LONG_LITERAL);
			literal_AST = (AST)currentAST.root;
			break;
		}
		case FLOAT_LITERAL:
		{
			AST tmp479_AST = null;
			tmp479_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp479_AST);
			match(FLOAT_LITERAL);
			literal_AST = (AST)currentAST.root;
			break;
		}
		case DOUBLE_LITERAL:
		{
			AST tmp480_AST = null;
			tmp480_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp480_AST);
			match(DOUBLE_LITERAL);
			literal_AST = (AST)currentAST.root;
			break;
		}
		case CHAR_LITERAL:
		{
			AST tmp481_AST = null;
			tmp481_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp481_AST);
			match(CHAR_LITERAL);
			literal_AST = (AST)currentAST.root;
			break;
		}
		case STRING_LITERAL:
		{
			AST tmp482_AST = null;
			tmp482_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp482_AST);
			match(STRING_LITERAL);
			literal_AST = (AST)currentAST.root;
			break;
		}
		case NULL_LITERAL:
		{
			AST tmp483_AST = null;
			tmp483_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp483_AST);
			match(NULL_LITERAL);
			literal_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = literal_AST;
	}
	
	public final void setArrayDeclarator(
		AST d
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST setArrayDeclarator_AST = null;
		d.setType (ARRAY_DECLARATOR);
		
		returnAST = setArrayDeclarator_AST;
	}
	
	public final void arrayInitializer(
		AST t
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayInitializer_AST = null;
		Token  lc = null;
		AST lc_AST = null;
		
		lc = LT(1);
		lc_AST = astFactory.create(lc);
		astFactory.makeASTRoot(currentAST, lc_AST);
		match(LCURLY);
		setType(lc_AST, ARRAY_INIT);
		astFactory.addASTChild(currentAST, returnAST);
		arrayInitializerRest(t);
		astFactory.addASTChild(currentAST, returnAST);
		arrayInitializer_AST = (AST)currentAST.root;
		returnAST = arrayInitializer_AST;
	}
	
	public final void dimensionList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dimensionList_AST = null;
		Token  r = null;
		AST r_AST = null;
		
		expression();
		astFactory.addASTChild(currentAST, returnAST);
		r = LT(1);
		r_AST = astFactory.create(r);
		astFactory.makeASTRoot(currentAST, r_AST);
		match(RBRACK);
		setType(r_AST, DIMLIST);
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop385:
		do {
			if ((LA(1)==LBRACK) && (_tokenSet_20.member(LA(2)))) {
				match(LBRACK);
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				match(RBRACK);
			}
			else {
				break _loop385;
			}
			
		} while (true);
		}
		dimensionList_AST = (AST)currentAST.root;
		returnAST = dimensionList_AST;
	}
	
	public final void arg(
		boolean allowEmpty
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arg_AST = null;
		Token  d = null;
		AST d_AST = null;
		
		if ((_tokenSet_10.member(LA(1)))) {
			singleExpression();
			astFactory.addASTChild(currentAST, returnAST);
			arg_AST = (AST)currentAST.root;
		}
		else if (((LA(1)==DOT))&&(allowEmpty)) {
			d = LT(1);
			d_AST = astFactory.create(d);
			astFactory.addASTChild(currentAST, d_AST);
			match(DOT);
			setType(d_AST, EMPTY);
			astFactory.addASTChild(currentAST, returnAST);
			arg_AST = (AST)currentAST.root;
		}
		else if (((LA(1)==RPAREN||LA(1)==COMMA))&&(allowEmpty)) {
			if ( inputState.guessing==0 ) {
				arg_AST = (AST)currentAST.root;
				
							(arg_AST = new ASTWithToken ()).initialize (LT (1));
							arg_AST.setType (EMPTY);
						
				currentAST.root = arg_AST;
				currentAST.child = arg_AST!=null &&arg_AST.getFirstChild()!=null ?
					arg_AST.getFirstChild() : arg_AST;
				currentAST.advanceChildToEnd();
			}
			arg_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = arg_AST;
	}
	
	public final void arrayInitializerRest(
		AST t
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayInitializerRest_AST = null;
		
		switch ( LA(1)) {
		case RCURLY:
		{
			match(RCURLY);
			arrayInitializerRest_AST = (AST)currentAST.root;
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case CONTEXT:
		case SUB:
		case ADD:
		case COM:
		case NOT:
		case THIS:
		case NULL_LITERAL:
		case QUOTE:
		case INC:
		case DEC:
		case NEW:
		case LPAREN:
		case LCURLY:
		{
			initializer(t);
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case RCURLY:
			{
				match(RCURLY);
				break;
			}
			case COMMA:
			{
				match(COMMA);
				arrayInitializerRest(t);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			arrayInitializerRest_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = arrayInitializerRest_AST;
	}
	
	public final void initializer(
		AST t
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST initializer_AST = null;
		
		if (((LA(1)==LCURLY))&&(t.getType () == ARRAY_DECLARATOR)) {
			arrayInitializer(t.getFirstChild ());
			astFactory.addASTChild(currentAST, returnAST);
			initializer_AST = (AST)currentAST.root;
		}
		else if ((_tokenSet_10.member(LA(1)))) {
			singleExpression();
			astFactory.addASTChild(currentAST, returnAST);
			initializer_AST = (AST)currentAST.root;
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		returnAST = initializer_AST;
	}
	
	public final void annotation() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST annotation_AST = null;
		
		AST tmp489_AST = null;
		tmp489_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp489_AST);
		match(ANNOTATION);
		name();
		astFactory.addASTChild(currentAST, returnAST);
		{
		switch ( LA(1)) {
		case LPAREN:
		{
			annotationWithParens();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case IDENT:
		case MODULE:
		case CLASS:
		case INTERFACE:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case SYNCHRONIZED_:
		case ANNOTATION:
		case PRIVATE_:
		case PUBLIC_:
		case PROTECTED_:
		case STATIC_:
		case TRANSIENT_:
		case FINAL_:
		case ABSTRACT_:
		case NATIVE_:
		case VOLATILE_:
		case STRICT_:
		case CONST_:
		case RPAREN:
		case RCURLY:
		case COMMA:
		{
			insert(astFactory.create(MARKER));
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		annotation_AST = (AST)currentAST.root;
		returnAST = annotation_AST;
	}
	
	public final void annotationWithParens() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST annotationWithParens_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		
		lp = LT(1);
		lp_AST = astFactory.create(lp);
		astFactory.makeASTRoot(currentAST, lp_AST);
		match(LPAREN);
		setType(lp_AST, NORMAL);
		astFactory.addASTChild(currentAST, returnAST);
		{
		if ((LA(1)==IDENT) && (LA(2)==ASSIGN)) {
			elementValuePair();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop424:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					elementValuePair();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop424;
				}
				
			} while (true);
			}
		}
		else if ((_tokenSet_63.member(LA(1))) && (_tokenSet_64.member(LA(2)))) {
			elementValue();
			astFactory.addASTChild(currentAST, returnAST);
			setType(lp_AST, SINGLE_ELEMENT);
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==RPAREN)) {
			setType(lp_AST, MARKER);
			astFactory.addASTChild(currentAST, returnAST);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		match(RPAREN);
		annotationWithParens_AST = (AST)currentAST.root;
		returnAST = annotationWithParens_AST;
	}
	
	public final void elementValuePair() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST elementValuePair_AST = null;
		
		AST tmp492_AST = null;
		tmp492_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp492_AST);
		match(IDENT);
		AST tmp493_AST = null;
		tmp493_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp493_AST);
		match(ASSIGN);
		elementValue();
		astFactory.addASTChild(currentAST, returnAST);
		elementValuePair_AST = (AST)currentAST.root;
		returnAST = elementValuePair_AST;
	}
	
	public final void elementValue() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST elementValue_AST = null;
		Token  lc = null;
		AST lc_AST = null;
		
		switch ( LA(1)) {
		case LCURLY:
		{
			lc = LT(1);
			lc_AST = astFactory.create(lc);
			astFactory.makeASTRoot(currentAST, lc_AST);
			match(LCURLY);
			setType(lc_AST, ARRAY_INIT);
			astFactory.addASTChild(currentAST, returnAST);
			elementValuesRest();
			astFactory.addASTChild(currentAST, returnAST);
			elementValue_AST = (AST)currentAST.root;
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case CONTEXT:
		case SUB:
		case ADD:
		case COM:
		case NOT:
		case THIS:
		case NULL_LITERAL:
		case QUOTE:
		case INC:
		case DEC:
		case NEW:
		case LPAREN:
		{
			conditionalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			elementValue_AST = (AST)currentAST.root;
			break;
		}
		case ANNOTATION:
		{
			annotation();
			astFactory.addASTChild(currentAST, returnAST);
			elementValue_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = elementValue_AST;
	}
	
	public final void elementValuesRest() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST elementValuesRest_AST = null;
		
		switch ( LA(1)) {
		case RCURLY:
		{
			match(RCURLY);
			elementValuesRest_AST = (AST)currentAST.root;
			break;
		}
		case BOOLEAN_LITERAL:
		case INT_LITERAL:
		case LONG_LITERAL:
		case FLOAT_LITERAL:
		case DOUBLE_LITERAL:
		case CHAR_LITERAL:
		case STRING_LITERAL:
		case IDENT:
		case SUPER:
		case VOID_:
		case BOOLEAN_:
		case BYTE_:
		case SHORT_:
		case CHAR_:
		case INT_:
		case LONG_:
		case FLOAT_:
		case DOUBLE_:
		case CONTEXT:
		case SUB:
		case ADD:
		case COM:
		case NOT:
		case THIS:
		case NULL_LITERAL:
		case QUOTE:
		case INC:
		case DEC:
		case NEW:
		case ANNOTATION:
		case LPAREN:
		case LCURLY:
		{
			elementValue();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case RCURLY:
			{
				match(RCURLY);
				break;
			}
			case COMMA:
			{
				match(COMMA);
				elementValuesRest();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			elementValuesRest_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = elementValuesRest_AST;
	}
	
	public final void variableDeclarator(
		AST mods, AST t, boolean allowInitializers
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variableDeclarator_AST = null;
		Token  id = null;
		AST id_AST = null;
		
		id = LT(1);
		id_AST = astFactory.create(id);
		match(IDENT);
		variableDeclaratorRest(mods, t, id_AST, allowInitializers);
		astFactory.addASTChild(currentAST, returnAST);
		variableDeclarator_AST = (AST)currentAST.root;
		returnAST = variableDeclarator_AST;
	}
	
	public final void variableDeclaratorRest(
		AST mods, AST t, AST id, boolean allowInitializers
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variableDeclaratorRest_AST = null;
		AST d_AST = null;
		AST v_AST = null;
		
		declaratorBrackets(t);
		d_AST = (AST)returnAST;
		{
		if (((LA(1)==ASSIGN))&&(allowInitializers)) {
			varInitializer(d_AST);
			v_AST = (AST)returnAST;
		}
		else if ((_tokenSet_65.member(LA(1)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		if ( inputState.guessing==0 ) {
			variableDeclaratorRest_AST = (AST)currentAST.root;
			variableDeclaratorRest_AST = (AST)astFactory.make( (new ASTArray(5)).add(astFactory.create(VARIABLE_DEF)).add(mods).add(d_AST).add(id).add(v_AST));
			currentAST.root = variableDeclaratorRest_AST;
			currentAST.child = variableDeclaratorRest_AST!=null &&variableDeclaratorRest_AST.getFirstChild()!=null ?
				variableDeclaratorRest_AST.getFirstChild() : variableDeclaratorRest_AST;
			currentAST.advanceChildToEnd();
		}
		returnAST = variableDeclaratorRest_AST;
	}
	
	public final void varInitializer(
		AST t
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST varInitializer_AST = null;
		
		AST tmp497_AST = null;
		tmp497_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp497_AST);
		match(ASSIGN);
		initializer(t);
		astFactory.addASTChild(currentAST, returnAST);
		varInitializer_AST = (AST)currentAST.root;
		returnAST = varInitializer_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"BOOLEAN_LITERAL",
		"INT_LITERAL",
		"LONG_LITERAL",
		"FLOAT_LITERAL",
		"DOUBLE_LITERAL",
		"CHAR_LITERAL",
		"STRING_LITERAL",
		"<identifier>",
		"COMPILATION_UNIT",
		"\"package\"",
		"\"module\"",
		"\"class\"",
		"\"interface\"",
		"IMPORT_ON_DEMAND",
		"STATIC_IMPORT_ON_DEMAND",
		"\"import\"",
		"SINGLE_STATIC_IMPORT",
		"\"extends\"",
		"\"implements\"",
		"PARAMETERS",
		"PARAMETER_DEF",
		"\"super\"",
		"ARGLIST",
		"SLIST",
		"INSTANTIATOR",
		"METHOD",
		"\"throws\"",
		"\";\"",
		"CONSTRUCTOR",
		"VARIABLE_DEF",
		"\"=\"",
		"ARRAY_DECLARATOR",
		"DECLARING_TYPE",
		"\"void\"",
		"\"boolean\"",
		"\"byte\"",
		"\"short\"",
		"\"char\"",
		"\"int\"",
		"\"long\"",
		"\"float\"",
		"\"double\"",
		"INSTANCE_INIT",
		"STATIC_INIT",
		"EMPTY",
		"QUERY",
		"COMPOUND_PATTERN",
		"LABEL",
		"PATTERN_WITH_BLOCK",
		"MINIMAL",
		"LATE_MATCH",
		"SINGLE_MATCH",
		"OPTIONAL_MATCH",
		"SINGLE_OPTIONAL_MATCH",
		"ANY",
		"FOLDING",
		"SEPARATE",
		"\"<\"",
		"\">\"",
		"\"---\"",
		"\"<->\"",
		"\"<+\"",
		"\"+>\"",
		"\"-+-\"",
		"\"<+>\"",
		"\"</\"",
		"\"/>\"",
		"\"-/-\"",
		"\"</>\"",
		"TYPE_PATTERN",
		"WRAPPED_TYPE_PATTERN",
		"NAME_PATTERN",
		"TREE",
		"\"(*\"",
		"EXPR",
		"ROOT",
		"METHOD_PATTERN",
		"METHOD_CALL",
		"\".\"",
		"APPLICATION_CONDITION",
		"PARAMETERIZED_PATTERN",
		"\"-\"",
		"\"<-\"",
		"\"->\"",
		"X_LEFT_RIGHT_ARROW",
		"TRAVERSAL",
		"\"?\"",
		"\"*\"",
		"\"+\"",
		"RANGE_EXACTLY",
		"RANGE_MIN",
		"RANGE",
		"\"==>\"",
		"\"==>>\"",
		"\"::>\"",
		"PRODUCE",
		"WITH",
		"UNARY_PREFIX",
		"TYPECAST",
		"TYPECHECK",
		"\"~\"",
		"\"!\"",
		"NEG",
		"POS",
		"\"/\"",
		"\"%\"",
		"\"**\"",
		"\"<<\"",
		"\">>\"",
		"\">>>\"",
		"\"<=\"",
		"\">=\"",
		"\"<=>\"",
		"\"!=\"",
		"\"==\"",
		"\"|\"",
		"\"^\"",
		"\"&\"",
		"\"||\"",
		"\"&&\"",
		"ARRAY_INIT",
		"RULE_BLOCK",
		"ELIST",
		"SHELL_BLOCK",
		"\"this\"",
		"QUALIFIED_SUPER",
		"\"if\"",
		"\"return\"",
		"\"yield\"",
		"\"throw\"",
		"\"synchronized\"",
		"\"assert\"",
		"LABELED_STATEMENT",
		"\"break\"",
		"\"continue\"",
		"\"try\"",
		"\"catch\"",
		"\"finally\"",
		"NODES",
		"NODE",
		"\"for\"",
		"ENHANCED_FOR",
		"\"while\"",
		"\"do\"",
		"\"switch\"",
		"SWITCH_GROUP",
		"\"case\"",
		"\"default\"",
		"\"null\"",
		"INVALID_EXPR",
		"\"<--\"",
		"\"-->\"",
		"\"<-->\"",
		"\"instanceof\"",
		"CLASS_LITERAL",
		"\"`\"",
		"\"+=\"",
		"\"-=\"",
		"\"*=\"",
		"\"/=\"",
		"\"%=\"",
		"\"**=\"",
		"\">>=\"",
		"\">>>=\"",
		"\"<<=\"",
		"\"&=\"",
		"\"^=\"",
		"\"|=\"",
		"\":=\"",
		"\":'=\"",
		"\":+=\"",
		"\":-=\"",
		"\":*=\"",
		"\":/=\"",
		"\":%=\"",
		"\":**=\"",
		"\":|=\"",
		"\":&=\"",
		"\":^=\"",
		"\":<<=\"",
		"\":>>=\"",
		"\":>>>=\"",
		"\"++\"",
		"\"--\"",
		"POST_INC",
		"POST_DEC",
		"\"in\"",
		"\"::\"",
		"ARRAY_ITERATOR",
		"QUERY_EXPR",
		"INVOKE_OP",
		"QUALIFIED_NEW",
		"INDEX_OP",
		"\"new\"",
		"DIMLIST",
		"MODIFIERS",
		"\"@\"",
		"\"private\"",
		"\"public\"",
		"\"protected\"",
		"\"static\"",
		"\"transient\"",
		"\"final\"",
		"\"abstract\"",
		"\"native\"",
		"\"volatile\"",
		"\"strictfp\"",
		"ITERATING_",
		"\"const\"",
		"\"...\"",
		"STATIC_MEMBER_CLASSES",
		"MARKER",
		"SINGLE_ELEMENT",
		"NORMAL",
		"\"else\"",
		"\"(\"",
		"\")\"",
		"\"[\"",
		"\"]\"",
		"\"{\"",
		"\"}\"",
		"\":\"",
		"\",\"",
		"\"=>\"",
		"\"*)\""
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[8];
		data[2]=64L;
		data[3]=1572608L;
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = new long[8];
		data[0]=70233452824576L;
		data[2]=64L;
		data[3]=2149056256L;
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = new long[8];
		data[0]=70231305340928L;
		data[2]=64L;
		data[3]=1572608L;
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = new long[8];
		data[0]=70231305340928L;
		data[1]=134479872L;
		data[2]=64L;
		data[3]=672661248L;
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 70093866272768L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = new long[8];
		data[0]=2048L;
		data[1]=134479872L;
		data[3]=536870912L;
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = new long[8];
		data[0]=-2305843009213691904L;
		data[1]=1152907211364631039L;
		data[2]=-3458764515498262528L;
		data[3]=9261023232L;
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = new long[8];
		data[0]=19327352832L;
		data[3]=17716740096L;
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = new long[8];
		data[0]=70093866272768L;
		data[2]=64L;
		data[3]=1572608L;
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = new long[8];
		data[0]=70093866272768L;
		data[1]=262144L;
		data[2]=64L;
		data[3]=538443520L;
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = new long[8];
		data[0]=70231338782704L;
		data[1]=3298805424128L;
		data[2]=864691130619396097L;
		data[3]=134217760L;
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = new long[8];
		data[0]=-2305772758547558416L;
		data[1]=1152907211440398847L;
		data[2]=-3458764514944614335L;
		data[3]=43622334240L;
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = new long[8];
		data[0]=2147483648L;
		data[1]=4294967296L;
		data[2]=1904380L;
		data[3]=2684354560L;
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = new long[8];
		data[0]=-2305772775726870542L;
		data[1]=1125885643665187327L;
		data[2]=-3746994887336325379L;
		data[3]=25368723232L;
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = new long[8];
		data[0]=-14L;
		for (int i = 1; i<=2; i++) { data[i]=-1L; }
		data[3]=137438953471L;
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = new long[8];
		data[0]=70231338782704L;
		data[1]=3298805686272L;
		data[2]=864691130619396097L;
		data[3]=17582522400L;
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = new long[8];
		data[0]=-2305772777874911248L;
		data[1]=1125885613600408063L;
		data[2]=-3746994887336327579L;
		data[3]=19864223744L;
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = new long[8];
		data[0]=-2305843009213693952L;
		data[1]=1152907211364631039L;
		data[2]=-3458764515498262528L;
		data[3]=9126805504L;
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = new long[8];
		data[0]=70093866272768L;
		data[2]=64L;
		data[3]=270008064L;
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = new long[8];
		data[0]=70233486299120L;
		data[1]=3303100391424L;
		data[2]=864691130621300477L;
		data[3]=2820144928L;
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = new long[8];
		data[0]=70231338782704L;
		data[1]=3298805424128L;
		data[2]=864691130619396161L;
		data[3]=135790368L;
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = new long[8];
		data[0]=-2305772777874911248L;
		data[1]=72060892579111423L;
		data[2]=576460754937446465L;
		data[3]=672661248L;
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = new long[8];
		data[0]=-2305772777874911248L;
		data[1]=252204908074508799L;
		data[2]=864691131089158209L;
		data[3]=97309425440L;
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = new long[8];
		data[0]=-2305772758547558416L;
		data[1]=1152907215735366143L;
		data[2]=-3458764514944614335L;
		data[3]=35032399648L;
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = new long[8];
		data[0]=-16L;
		data[1]=-469762049L;
		data[2]=-1L;
		data[3]=137438953471L;
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = new long[8];
		data[0]=-2305772777874911248L;
		data[1]=72060892579111423L;
		data[2]=576460754937446401L;
		data[3]=671088640L;
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = new long[8];
		data[0]=-2305772777874911248L;
		data[1]=252204908074508799L;
		data[2]=864691131089158209L;
		data[3]=97577860896L;
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = new long[8];
		data[0]=-2305772777874911248L;
		data[1]=252204908074508799L;
		data[2]=864691131089158209L;
		data[3]=98651602720L;
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = new long[8];
		data[0]=-2305772777874911248L;
		data[1]=72060892579103231L;
		data[2]=576460754937446401L;
		data[3]=134217728L;
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	private static final long[] mk_tokenSet_29() {
		long[] data = new long[8];
		data[0]=-2305772777874911248L;
		data[1]=252204907738964479L;
		data[2]=576460754937446401L;
		data[3]=90060095488L;
		return data;
	}
	public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	private static final long[] mk_tokenSet_30() {
		long[] data = new long[8];
		data[0]=-2305772777874911248L;
		data[1]=252204907738702335L;
		data[2]=576460754937446401L;
		data[3]=90060095488L;
		return data;
	}
	public static final BitSet _tokenSet_30 = new BitSet(mk_tokenSet_30());
	private static final long[] mk_tokenSet_31() {
		long[] data = new long[8];
		data[0]=-2305772758547443726L;
		data[1]=1152907241505169919L;
		data[2]=-3458764514930127107L;
		data[3]=102946570016L;
		return data;
	}
	public static final BitSet _tokenSet_31 = new BitSet(mk_tokenSet_31());
	private static final long[] mk_tokenSet_32() {
		long[] data = { 70231338782704L, 0L, 2164260865L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_32 = new BitSet(mk_tokenSet_32());
	private static final long[] mk_tokenSet_33() {
		long[] data = new long[8];
		data[0]=-2305772777874911248L;
		data[1]=252204908007399935L;
		data[2]=864691131089158209L;
		data[3]=90061668128L;
		return data;
	}
	public static final BitSet _tokenSet_33 = new BitSet(mk_tokenSet_33());
	private static final long[] mk_tokenSet_34() {
		long[] data = new long[8];
		data[0]=-2305772758547558414L;
		data[1]=1152907241505169919L;
		data[2]=-3458764514930129307L;
		data[3]=102944997376L;
		return data;
	}
	public static final BitSet _tokenSet_34 = new BitSet(mk_tokenSet_34());
	private static final long[] mk_tokenSet_35() {
		long[] data = new long[8];
		data[0]=-2305842989886341120L;
		data[1]=1152903917200474623L;
		data[2]=-3458764514961391616L;
		data[3]=32078036992L;
		return data;
	}
	public static final BitSet _tokenSet_35 = new BitSet(mk_tokenSet_35());
	private static final long[] mk_tokenSet_36() {
		long[] data = new long[8];
		data[0]=-2305772775727427600L;
		data[1]=1125885613600408063L;
		data[2]=-3746994887323744667L;
		data[3]=25501368320L;
		return data;
	}
	public static final BitSet _tokenSet_36 = new BitSet(mk_tokenSet_36());
	private static final long[] mk_tokenSet_37() {
		long[] data = new long[8];
		data[0]=-2305772775726788622L;
		data[1]=1125885643665449471L;
		data[2]=-3746994887323742467L;
		data[3]=34294202144L;
		return data;
	}
	public static final BitSet _tokenSet_37 = new BitSet(mk_tokenSet_37());
	private static final long[] mk_tokenSet_38() {
		long[] data = { 70231338782704L, 72060892572811264L, 2164260865L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_38 = new BitSet(mk_tokenSet_38());
	private static final long[] mk_tokenSet_39() {
		long[] data = new long[8];
		data[0]=-2305772775727427600L;
		data[1]=1125885613600678399L;
		data[2]=-3746994887323744667L;
		data[3]=25637158688L;
		return data;
	}
	public static final BitSet _tokenSet_39 = new BitSet(mk_tokenSet_39());
	private static final long[] mk_tokenSet_40() {
		long[] data = new long[8];
		data[0]=70233486266352L;
		data[1]=3303100391424L;
		data[2]=864691130621300477L;
		data[3]=2818572320L;
		return data;
	}
	public static final BitSet _tokenSet_40 = new BitSet(mk_tokenSet_40());
	private static final long[] mk_tokenSet_41() {
		long[] data = new long[8];
		data[0]=-2305772775726870542L;
		data[1]=1125885643665187327L;
		data[2]=-3746994887323742467L;
		data[3]=25704267552L;
		return data;
	}
	public static final BitSet _tokenSet_41 = new BitSet(mk_tokenSet_41());
	private static final long[] mk_tokenSet_42() {
		long[] data = new long[8];
		data[0]=-2305772758546919438L;
		data[1]=1152907241505169919L;
		data[2]=-3458764514930114819L;
		data[3]=137373417248L;
		return data;
	}
	public static final BitSet _tokenSet_42 = new BitSet(mk_tokenSet_42());
	private static final long[] mk_tokenSet_43() {
		long[] data = new long[8];
		data[0]=32768L;
		data[2]=64L;
		data[3]=1572608L;
		return data;
	}
	public static final BitSet _tokenSet_43 = new BitSet(mk_tokenSet_43());
	private static final long[] mk_tokenSet_44() {
		long[] data = new long[8];
		data[0]=34816L;
		data[2]=64L;
		data[3]=1572608L;
		return data;
	}
	public static final BitSet _tokenSet_44 = new BitSet(mk_tokenSet_44());
	private static final long[] mk_tokenSet_45() {
		long[] data = new long[8];
		data[0]=-2305772758547525648L;
		data[1]=1152907241505169919L;
		data[2]=-3458764514930127107L;
		data[3]=68586831648L;
		return data;
	}
	public static final BitSet _tokenSet_45 = new BitSet(mk_tokenSet_45());
	private static final long[] mk_tokenSet_46() {
		long[] data = new long[8];
		data[0]=70231338782704L;
		data[1]=8192L;
		data[2]=2164260865L;
		data[3]=134217760L;
		return data;
	}
	public static final BitSet _tokenSet_46 = new BitSet(mk_tokenSet_46());
	private static final long[] mk_tokenSet_47() {
		long[] data = new long[8];
		data[0]=-2305772777874911248L;
		data[1]=72060892847809023L;
		data[2]=864691131089158209L;
		data[3]=672661280L;
		return data;
	}
	public static final BitSet _tokenSet_47 = new BitSet(mk_tokenSet_47());
	private static final long[] mk_tokenSet_48() {
		long[] data = new long[8];
		data[0]=70233486299120L;
		data[1]=3303100391424L;
		data[2]=864691130621300477L;
		data[3]=7115112224L;
		return data;
	}
	public static final BitSet _tokenSet_48 = new BitSet(mk_tokenSet_48());
	private static final long[] mk_tokenSet_49() {
		long[] data = new long[8];
		data[0]=-2305772758547443726L;
		data[1]=1152907241505169919L;
		data[2]=-3458764514942710019L;
		data[3]=68318396192L;
		return data;
	}
	public static final BitSet _tokenSet_49 = new BitSet(mk_tokenSet_49());
	private static final long[] mk_tokenSet_50() {
		long[] data = new long[8];
		data[0]=-2305772758547001358L;
		data[1]=1152907241505169919L;
		data[2]=-3458764514930127107L;
		data[3]=68653940512L;
		return data;
	}
	public static final BitSet _tokenSet_50 = new BitSet(mk_tokenSet_50());
	private static final long[] mk_tokenSet_51() {
		long[] data = new long[8];
		data[2]=1900612L;
		data[3]=2281701376L;
		return data;
	}
	public static final BitSet _tokenSet_51 = new BitSet(mk_tokenSet_51());
	private static final long[] mk_tokenSet_52() {
		long[] data = new long[8];
		data[0]=-2305772775727394832L;
		data[1]=1125885617895383551L;
		data[2]=-3746994887336325379L;
		data[3]=24563416864L;
		return data;
	}
	public static final BitSet _tokenSet_52 = new BitSet(mk_tokenSet_52());
	private static final long[] mk_tokenSet_53() {
		long[] data = new long[8];
		data[0]=70231338782704L;
		data[1]=3298805424128L;
		data[2]=864691130619396097L;
		data[3]=8724152352L;
		return data;
	}
	public static final BitSet _tokenSet_53 = new BitSet(mk_tokenSet_53());
	private static final long[] mk_tokenSet_54() {
		long[] data = new long[8];
		data[0]=-2305772777874911248L;
		data[1]=1125885613600408063L;
		data[2]=-3746994887323744667L;
		data[3]=24561844224L;
		return data;
	}
	public static final BitSet _tokenSet_54 = new BitSet(mk_tokenSet_54());
	private static final long[] mk_tokenSet_55() {
		long[] data = new long[8];
		data[0]=-2305772775726870542L;
		data[1]=1125885643665449471L;
		data[2]=-3746994887323742467L;
		data[3]=34294202144L;
		return data;
	}
	public static final BitSet _tokenSet_55 = new BitSet(mk_tokenSet_55());
	private static final long[] mk_tokenSet_56() {
		long[] data = new long[8];
		data[0]=70233486299120L;
		data[1]=3303100391424L;
		data[2]=864691130633883389L;
		data[3]=7383547680L;
		return data;
	}
	public static final BitSet _tokenSet_56 = new BitSet(mk_tokenSet_56());
	private static final long[] mk_tokenSet_57() {
		long[] data = new long[8];
		data[0]=-2305772758547558416L;
		data[1]=1152907211440398847L;
		data[2]=-3458764514944614335L;
		data[3]=61070638880L;
		return data;
	}
	public static final BitSet _tokenSet_57 = new BitSet(mk_tokenSet_57());
	private static final long[] mk_tokenSet_58() {
		long[] data = new long[8];
		data[0]=-2305772758547558416L;
		data[1]=1152907211440398847L;
		data[2]=-3458764514944614335L;
		data[3]=62144380704L;
		return data;
	}
	public static final BitSet _tokenSet_58 = new BitSet(mk_tokenSet_58());
	private static final long[] mk_tokenSet_59() {
		long[] data = { 70231305226240L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_59 = new BitSet(mk_tokenSet_59());
	private static final long[] mk_tokenSet_60() {
		long[] data = new long[8];
		data[0]=2048L;
		data[1]=262144L;
		data[3]=34896609280L;
		return data;
	}
	public static final BitSet _tokenSet_60 = new BitSet(mk_tokenSet_60());
	private static final long[] mk_tokenSet_61() {
		long[] data = new long[8];
		data[0]=-2305772758547558416L;
		data[1]=1152907215735366143L;
		data[2]=-3458764514944614335L;
		data[3]=32079609632L;
		return data;
	}
	public static final BitSet _tokenSet_61 = new BitSet(mk_tokenSet_61());
	private static final long[] mk_tokenSet_62() {
		long[] data = { -2305843009213693952L, 7881299347898879L, 4611686018897149952L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_62 = new BitSet(mk_tokenSet_62());
	private static final long[] mk_tokenSet_63() {
		long[] data = new long[8];
		data[0]=70231338782704L;
		data[1]=3298805424128L;
		data[2]=864691130619396097L;
		data[3]=2281701664L;
		return data;
	}
	public static final BitSet _tokenSet_63 = new BitSet(mk_tokenSet_63());
	private static final long[] mk_tokenSet_64() {
		long[] data = new long[8];
		data[0]=-2305772777874911248L;
		data[1]=1152907211440398847L;
		data[2]=-3746994886801358783L;
		data[3]=7383547680L;
		return data;
	}
	public static final BitSet _tokenSet_64 = new BitSet(mk_tokenSet_64());
	private static final long[] mk_tokenSet_65() {
		long[] data = new long[8];
		data[0]=2147483648L;
		data[2]=2147483648L;
		data[3]=18522046464L;
		return data;
	}
	public static final BitSet _tokenSet_65 = new BitSet(mk_tokenSet_65());
	
	}
