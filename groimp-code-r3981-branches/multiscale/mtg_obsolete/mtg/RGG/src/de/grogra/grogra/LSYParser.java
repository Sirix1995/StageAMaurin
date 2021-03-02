// $ANTLR 2.7.7 (20090803): "LSY.g" -> "LSYParser.java"$


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

import de.grogra.rgg.Library;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

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

/* class LSYParser extends antlr.LLkParser       implements LSYTokenTypes
, */ public class LSYParser extends de.grogra.xl.parser.Parser implements LSYTokenTypes {

	public static final long WARN_ON_REGISTER_PATTERN
		= de.grogra.xl.compiler.ProblemReporter.MIN_UNUSED_WARNING;

	public static final long WARN_ON_UNUSED_RULES
		= WARN_ON_REGISTER_PATTERN << 1;

	private AST classDef, initializer, firstModuleDef, lastModuleDef,
		derivation, interpretation;

	private Set knownModules, declaredMethods, declaredVars;
	private Map patterns;
	private int repetitionLevel;

	private static Map translations;
	
	String className;

	int localRegistersCount;
	Hashtable symbolTableForLocalRegisters = new Hashtable();
	ArrayList<AST> defaultsForLocalRegisters = new ArrayList<AST>();

	static
	{
		translations = new HashMap ();

		translations.put ("*0", "Axiom");
		translations.put ("$0", "AdjustLU");
		translations.put ("RG0", "RG");
		translations.put ("+0", "Plus");
		translations.put ("-0", "Minus");
		translations.put ("RH1", "RH");
		translations.put ("RL1", "RL");
		translations.put ("RU1", "RU");
		translations.put ("/0", "IncScale");

		translations.put ("D0", "D0");
		translations.put ("D1", "D");
		translations.put ("Dl1", "Dl");
		translations.put ("D+1", "DAdd");
		translations.put ("D*1", "DMul");
		translations.put ("Dl+1", "DlAdd");
		translations.put ("Dl*1", "DlMul");

		translations.put ("L0", "L0");
		translations.put ("L1", "L");
		translations.put ("Ll1", "Ll");
		translations.put ("L+1", "LAdd");
		translations.put ("L*1", "LMul");
		translations.put ("Ll+1", "LlAdd");
		translations.put ("Ll*1", "LlMul");

		translations.put ("N0", "N0");
		translations.put ("N1", "N");
		translations.put ("Nl1", "Nl");
		translations.put ("N+1", "NAdd");
		translations.put ("N*1", "NMul");
		translations.put ("Nl+1", "NlAdd");
		translations.put ("Nl*1", "NlMul");

		translations.put ("V0", "V0");
		translations.put ("V1", "V");
		translations.put ("Vl1", "Vl");
		translations.put ("V+1", "VAdd");
		translations.put ("V*1", "VMul");
		translations.put ("Vl+1", "VlAdd");
		translations.put ("Vl*1", "VlMul");

		translations.put ("C0", "C0");
		translations.put ("C1", "C");
		translations.put ("Cl1", "Cl");
		translations.put ("C+1", "CAdd");
		translations.put ("C*1", "CMul");
		translations.put ("Cl+1", "ClAdd");
		translations.put ("Cl*1", "ClMul");

		translations.put ("H0", "H0");
		translations.put ("H1", "H");
		translations.put ("Hl1", "Hl");
		translations.put ("H+1", "HAdd");
		translations.put ("H*1", "HMul");
		translations.put ("Hl+1", "HlAdd");
		translations.put ("Hl*1", "HlMul");

		translations.put ("U0", "U0");
		translations.put ("U1", "U");
		translations.put ("Ul1", "Ul");
		translations.put ("U+1", "UAdd");
		translations.put ("U*1", "UMul");
		translations.put ("Ul+1", "UlAdd");
		translations.put ("Ul*1", "UlMul");

		translations.put ("P0", "P0");
		translations.put ("P1", "P");
		translations.put ("Pl1", "Pl");

		translations.put ("F0", "F0");
		translations.put ("F1", "F");
		translations.put ("F+1", "FAdd");
		translations.put ("F*1", "FMul");

		translations.put ("f0", "M0");
		translations.put ("f1", "M");
		translations.put ("f+1", "MAdd");
		translations.put ("f*1", "MMul");
		translations.put ("@1", "MRel");
		translations.put ("OR1", "OR");

		translations.put ("RV0", "RV0");
		translations.put ("RV1", "RV");
		translations.put ("RV+1", "RVAdd");
		translations.put ("RV*1", "RVMul");

		translations.put ("M1", "InvokeMethod");
		translations.put ("K1", "K");
		translations.put ("KL1", "KL");
		translations.put ("K=1", "KAssignment");
		translations.put ("A2", "assignLocalRegister");
		translations.put ("A+2", "assignLocalRegisterAndAdd");
		translations.put ("A*2", "assignLocalRegisterAndMul");
		translations.put ("Ar2", "assignReferenceShoot");
		translations.put ("Ar+2", "assignReferenceShootAndAdd");
		translations.put ("Ar*2", "assignReferenceShootAndMul");
	}


	@Override
	protected AST parseGoalSymbol ()
		throws RecognitionException, TokenStreamException
	{
		return compilationUnit (className);
	}


	private static void translate (AST id, int arity)
	{
		String s = id.getText () + arity, t = (String) translations.get (s);
		id.setText ((t == null) ? s : t);
	}


	private static class PatternInfo
	{
		AST boundVars;
		AST expr, lastExpr;

		PatternInfo (AST boundVars, AST expr)
		{
			this.boundVars = boundVars;
			this.expr = expr;
			this.lastExpr = expr;
		}


		void replace (AST tree, AST list)
		{
			while (tree != null)
			{
				replace (tree.getFirstChild (), list);

				if (tree.getType () == IDENT)
				{
					String id = tree.getText ();
					AST b = boundVars;
					for (AST v = list; v != null;
						v = v.getNextSibling (), b = b.getNextSibling ())
					{
						if (id.equals (v.getText ()))
						{
							tree.setText (b.getText ());
							break;
						}
					}
				}
				tree = tree.getNextSibling ();
			}
		}

	}


	private void addVar (String name, int type)
	{
		addVar (name, type, null);
	}


	private void addVar (String name, int type, AST expr)
	{
		addVar (name, type, (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MODIFIERS)).add(astFactory.create(TRANSIENT_))), expr);
	}


	private void addVar (String name, int type, AST modifiers, AST expr)
	{
		addVar (name, astFactory.create(type), modifiers, expr);
	}


	private void addVar (String name, AST type, AST modifiers, AST expr)
	{
		if (expr != null)
		{
			expr = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ASSIGN)).add(expr));
		}
		classDef.addChild ((AST)astFactory.make( (new ASTArray(5)).add(astFactory.create(VARIABLE_DEF)).add(modifiers).add(type).add(astFactory.create(IDENT,name)).add(expr)));
		declaredVars.add (name);
	}


	private void addMethod (String name, AST body)
	{
		classDef.addChild ((AST)astFactory.make( (new ASTArray(7)).add(astFactory.create(METHOD)).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MODIFIERS)).add(astFactory.create(PUBLIC_)))).add(astFactory.create(VOID_)).add(astFactory.create(IDENT,name)).add(astFactory.create(PARAMETERS)).add(astFactory.create(THROWS)).add(body)));
	}


	@Override
	public void reset ()
	{
		super.reset ();
		classDef = null;
		initializer = null;
		firstModuleDef = null;
		lastModuleDef = null;
		derivation = null;
		interpretation = null;
		knownModules = null;
		declaredMethods = null;
		patterns = null;
		declaredVars = null;
		repetitionLevel = 0;
	}
	
	
	private AST getOperator (String name)
	{
		AST a = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add(astFactory.create(IDENT,"de")).add(astFactory.create(IDENT,"grogra")));
		a = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add(a).add(astFactory.create(IDENT,"xl")));
		a = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add(a).add(astFactory.create(IDENT,"lang")));
		a = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add(a).add(astFactory.create(IDENT,"Operators")));
		a = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add(a).add(astFactory.create(IDENT,name)));
		return a; 
	}


protected LSYParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public LSYParser(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected LSYParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public LSYParser(TokenStream lexer) {
  this(lexer,1);
}

public LSYParser(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final AST  compilationUnit(
		String clsName
	) throws RecognitionException, TokenStreamException {
		AST unit;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compilationUnit_AST = null;
		
				classDef = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(CLASS)).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MODIFIERS)).add(astFactory.create(PUBLIC_)))).add(astFactory.create(IDENT,clsName)).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(EXTENDS)).add(astFactory.create(IDENT,"LSystem")))));
				initializer = astFactory.create(SLIST);
				firstModuleDef = null;
				lastModuleDef = null;
				repetitionLevel = 0;
				patterns = new HashMap ();
				declaredMethods = new HashSet ();
				declaredVars = new HashSet ();
				knownModules = new HashSet (translations.values ());
				classDef.addChild ((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INSTANCE_INIT)).add(initializer)));
				unit = null;
				derivation = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(RULE_BLOCK)).add(astFactory.create(EMPTY)));
				interpretation = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(RULE_BLOCK)).add(astFactory.create(EMPTY)));
				addMethod ("derivation", (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SLIST)).add(derivation)));
				addMethod ("interpretation", (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SLIST)).add(interpretation)));
			
		
		{
		_loop3:
		do {
			if (((LA(1) >= 225 && LA(1) <= 232))) {
				declaration();
				AST tmp1_AST = null;
				tmp1_AST = astFactory.create(LT(1));
				match(COMMA);
			}
			else {
				break _loop3;
			}
			
		} while (true);
		}
		rule();
		moreRules();
		AST tmp2_AST = null;
		tmp2_AST = astFactory.create(LT(1));
		match(Token.EOF_TYPE);
		if ( inputState.guessing==0 ) {
			
						AST arrayDeclaration = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARRAY_DECLARATOR)).add(astFactory.create(FLOAT_)));
						AST arrayValues = astFactory.create(ARRAY_INIT);
						for(int i = 0; i < localRegistersCount; i++) {
							arrayValues.addChild(defaultsForLocalRegisters.get(i));
						}
						
						AST newArray = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(NEW)).add(arrayDeclaration).add(arrayValues));
						initializer.addChild((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ASSIGN)).add(astFactory.create(IDENT,"defaultValuesForLocalRegisters")).add(newArray)));
						
						if (firstModuleDef != null)
						{
							lastModuleDef.setNextSibling (classDef);
							unit = firstModuleDef;
						}
						else
						{
							unit = classDef;
						}
						unit = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(COMPILATION_UNIT)).add(unit));
					
		}
		returnAST = compilationUnit_AST;
		return unit;
	}
	
	public final void declaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaration_AST = null;
		Token  sid = null;
		AST sid_AST = null;
		AST sn_AST = null;
		AST l_AST = null;
		AST w_AST = null;
		Token  cid = null;
		AST cid_AST = null;
		AST cn_AST = null;
		Token  i = null;
		AST i_AST = null;
		AST rn_AST = null;
		
		switch ( LA(1)) {
		case 225:
		{
			match(225);
			sid = LT(1);
			sid_AST = astFactory.create(sid);
			match(IDENT);
			number();
			sn_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				
							String s = sid_AST.getText ();
							boolean needInt = false;
							if (s.length () == 1)
							{
								switch (s.charAt (0))
								{
									case 'L':
										s = "length";
										break;
									case 'D':
										s = "diameter";
										break;
									case 'H':
										s = "heartwood";
										break;
									case 'U':
										needInt = true;
										s = "internodeCount";
										break;
									case 'C':
										s = "carbon";
										break;
									case 'V':
										s = "tropism";
										break;
									case 'N':
										s = "parameter";
										break;
									case 'P':
										needInt = true;
										s = "color";
										break;
									case 'W':
										s = "angle";
										break;
								}
								if (needInt && (sn_AST.getType () == FLOAT_LITERAL))
								{
									sn_AST.setType (INT_LITERAL);
									sn_AST.setText (Integer.toString
										(Math.round (new Float (sn_AST.getText ()).floatValue ())));
								}
								sid_AST.setText (s);
								if (!"angle".equals (s))
								{
									sid_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add(astFactory.create(IDENT,"initialTurtleState")).add(sid_AST));
								}
							}
							initializer.addChild ((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ASSIGN)).add(sid_AST).add(sn_AST)));
						
			}
			break;
		}
		case 226:
		case 227:
		{
			{
			switch ( LA(1)) {
			case 226:
			{
				match(226);
				break;
			}
			case 227:
			{
				match(227);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			number();
			l_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				
							initializer.addChild ((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ASSIGN)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(DOT)).add(astFactory.create(IDENT,"initialTurtleState")).add(astFactory.create(IDENT,"length")))).add(l_AST)));
						
			}
			break;
		}
		case 228:
		case 229:
		{
			{
			switch ( LA(1)) {
			case 228:
			{
				match(228);
				break;
			}
			case 229:
			{
				match(229);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			number();
			w_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				
							initializer.addChild ((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ASSIGN)).add(astFactory.create(IDENT,"angle")).add(w_AST)));
						
			}
			break;
		}
		case 230:
		{
			match(230);
			cid = LT(1);
			cid_AST = astFactory.create(cid);
			match(IDENT);
			number();
			cn_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				
							addVar (cid_AST.getText (), FLOAT_,
									(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(MODIFIERS)).add(astFactory.create(STATIC_)).add(astFactory.create(FINAL_))), cn_AST);
						
			}
			break;
		}
		case 231:
		{
			match(231);
			i = LT(1);
			i_AST = astFactory.create(i);
			match(INT_LITERAL);
			number();
			rn_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				
							i_AST.setType (IDENT);
							i_AST.setText ("r" + i_AST.getText ());
							initializer.addChild ((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ASSIGN)).add(i_AST).add(rn_AST)));
						
			}
			break;
		}
		case 232:
		{
			match(232);
			var();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = declaration_AST;
	}
	
	public final void rule() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rule_AST = null;
		AST c_AST = null;
		AST id_AST = null;
		AST l_AST = null;
		AST t_AST = null;
		Token  cut = null;
		AST cut_AST = null;
		AST t2_AST = null;
		AST p_AST = null;
		boolean generative = true, isReg;
		
		{
		switch ( LA(1)) {
		case LPAREN:
		{
			AST tmp11_AST = null;
			tmp11_AST = astFactory.create(LT(1));
			match(LPAREN);
			expression();
			c_AST = (AST)returnAST;
			AST tmp12_AST = null;
			tmp12_AST = astFactory.create(LT(1));
			match(RPAREN);
			break;
		}
		case IDENT:
		case SUB:
		case MUL:
		case ADD:
		case DIV:
		case JREG_IDENT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		isReg=moduleIdent();
		id_AST = (AST)returnAST;
		{
		switch ( LA(1)) {
		case LPAREN:
		{
			AST tmp13_AST = null;
			tmp13_AST = astFactory.create(LT(1));
			match(LPAREN);
			boundVarList();
			l_AST = (AST)returnAST;
			AST tmp14_AST = null;
			tmp14_AST = astFactory.create(LT(1));
			match(RPAREN);
			break;
		}
		case 256:
		case 257:
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
		case 256:
		{
			match(256);
			rhs();
			t_AST = (AST)returnAST;
			{
			switch ( LA(1)) {
			case REM:
			{
				cut = LT(1);
				cut_AST = astFactory.create(cut);
				match(REM);
				break;
			}
			case EOF:
			case COMMA:
			case 258:
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
		case 257:
		{
			match(257);
			rhs();
			t2_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				t_AST = t2_AST; generative = false;
			}
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
		case 258:
		{
			match(258);
			expression();
			p_AST = (AST)returnAST;
			break;
		}
		case EOF:
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
			
						if (isReg)
						{
							if (exceptionList.isWarning (WARN_ON_REGISTER_PATTERN))
							{
								exceptionList.addSemanticWarning
									(Library.I18N.msg ("lsy.register-ignored",
													id_AST.getText ()),
									 id_AST);
							}
						}
						else
						{
							AST s = null;
							if (generative)
							{
								if (cut_AST != null)
								{
									s = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(astFactory.create(IDENT,"setOutConnectionEdges")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGLIST)).add(astFactory.create(INT_LITERAL,"0")))));
								}
							}
							s = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(SLIST)).add(t_AST).add(s));
							if (p_AST != null)
							{
								p_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(astFactory.create(IDENT,"ruleProbability")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGLIST)).add(p_AST))));
								if (c_AST != null)
								{
									s = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(IF)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(CAND)).add(c_AST).add(p_AST))).add(s));
								}
								else
								{
									s = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(IF)).add(p_AST).add(s));
								}
							}
							else if (c_AST != null)
							{
								s = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(IF)).add(c_AST).add(s));
							}
			
							l_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGLIST)).add(l_AST));
							int nc = l_AST.getNumberOfChildren ();
							String key = id_AST.getText () + generative + nc;
							translate (id_AST, nc);
							PatternInfo p = (PatternInfo) patterns.get (key);
							if (p == null)
							{
								patterns.put (key,
									p = new PatternInfo (l_AST.getFirstChild (), s));
								AST m = (nc == 0) ? (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(TYPE_PATTERN)).add(id_AST))
									: (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(PARAMETERIZED_PATTERN)).add(id_AST).add(l_AST));
								m = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(LABEL)).add(astFactory.create(IDENT,"lhs.")).add(m));
								(generative ? derivation : interpretation)
									.addChild ((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(RULE)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(QUERY)).add(astFactory.create(EMPTY)).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(COMPOUND_PATTERN)).add(m))))).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(SLIST)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(astFactory.create(IDENT,"patternMatched")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGLIST)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(astFactory.create(IDENT,"producer$getProducer")).add(astFactory.create(ARGLIST)))))))).add(s)))));
							}
							else
							{
								if (p.lastExpr.getType () == IF)
								{
									p.replace (s, l_AST.getFirstChild ());
									p.lastExpr.getFirstChild ().getNextSibling ()
										.setNextSibling (s);
									p.lastExpr = s;
								}
								else if (exceptionList.isWarning (WARN_ON_UNUSED_RULES))
								{
									exceptionList.addSemanticWarning
										(Library.I18N, "lsy.rule-unused", id_AST);
								}
							}
							if (s.getType () == IF)
							{
								s.addChild (astFactory.create(BREAK));
							}
						}
					
		}
		returnAST = rule_AST;
	}
	
	public final void moreRules() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST moreRules_AST = null;
		
		switch ( LA(1)) {
		case COMMA:
		{
			AST tmp18_AST = null;
			tmp18_AST = astFactory.create(LT(1));
			match(COMMA);
			{
			switch ( LA(1)) {
			case IDENT:
			case SUB:
			case MUL:
			case ADD:
			case DIV:
			case LPAREN:
			case JREG_IDENT:
			{
				rule();
				moreRules();
				break;
			}
			case EOF:
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
		case EOF:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = moreRules_AST;
	}
	
	public final void number() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST number_AST = null;
		boolean minus = false;
		
		{
		switch ( LA(1)) {
		case SUB:
		{
			match(SUB);
			if ( inputState.guessing==0 ) {
				minus = true;
			}
			break;
		}
		case INT_LITERAL:
		case FLOAT_LITERAL:
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
		case INT_LITERAL:
		{
			AST tmp20_AST = null;
			tmp20_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp20_AST);
			match(INT_LITERAL);
			break;
		}
		case FLOAT_LITERAL:
		{
			AST tmp21_AST = null;
			tmp21_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp21_AST);
			match(FLOAT_LITERAL);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			number_AST = (AST)currentAST.root;
			
					if (minus)
					{
						Token t = ((de.grogra.grammar.ASTWithToken) number_AST).token;
						String s = '-' + t.getText ();
						t.setText (s);
						number_AST.setText (s);
					}
				
		}
		number_AST = (AST)currentAST.root;
		returnAST = number_AST;
	}
	
	public final void var() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST var_AST = null;
		Token  id = null;
		AST id_AST = null;
		AST bf_AST = null;
		AST u1_AST = null;
		AST u2_AST = null;
		Token  poi = null;
		AST poi_AST = null;
		AST ep_AST = null;
		AST dn_AST = null;
		AST tn_AST = null;
		Token  rid = null;
		AST rid_AST = null;
		AST defValue_AST = null;
		Token  index = null;
		AST index_AST = null;
		Token  fn = null;
		AST fn_AST = null;
		Token  fa = null;
		AST fa_AST = null;
		
				AST m = null, a = null, p = astFactory.create(PARAMETERS), init = null;
				int t = FLOAT_, i = 0;
				Token varType = null;
			
		
		id = LT(1);
		id_AST = astFactory.create(id);
		match(IDENT);
		if ( inputState.guessing==0 ) {
			
						varType = LT (1);
					
		}
		{
		switch ( LA(1)) {
		case LITERAL_uniform:
		case LITERAL_normal:
		case LITERAL_binomial:
		case LITERAL_negbinomial:
		{
			binaryFunction();
			bf_AST = (AST)returnAST;
			number();
			u1_AST = (AST)returnAST;
			number();
			u2_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				
								switch (bf_AST.getType ())
								{
									case LITERAL_normal:
										float v = new Float (u2_AST.getText ()).floatValue ();
										if (v <= 0f)
										{
											exceptionList.addSemanticError
												(Library.I18N, "lsy.variance-nonpositive", u2_AST);
											v = 1f;
										}
										u2_AST.setType (FLOAT_LITERAL);
										u2_AST.setText (Float.toString ((float) Math.sqrt (v)));
										break;
								}
								bf_AST.setType (IDENT);
								m = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(bf_AST).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ARGLIST)).add(u1_AST).add(u2_AST))));
							
			}
			break;
		}
		case LITERAL_poisson:
		{
			poi = LT(1);
			poi_AST = astFactory.create(poi);
			match(LITERAL_poisson);
			number();
			ep_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				
								poi_AST.setType (IDENT);
								m = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(poi_AST).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGLIST)).add(ep_AST))));
							
			}
			break;
		}
		case LITERAL_distribution:
		{
			match(LITERAL_distribution);
			if ( inputState.guessing==0 ) {
				
								t = INT_;
								init = (AST)astFactory.make( (new ASTArray(5)).add(astFactory.create(VARIABLE_DEF)).add(astFactory.create(MODIFIERS)).add(astFactory.create(FLOAT_)).add(astFactory.create(IDENT,"p")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ASSIGN)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(astFactory.create(IDENT,"random")).add(astFactory.create(ARGLIST)))))));
								i = 0;
							
			}
			{
			int _cnt13=0;
			_loop13:
			do {
				if ((LA(1)==INT_LITERAL||LA(1)==FLOAT_LITERAL||LA(1)==SUB)) {
					number();
					dn_AST = (AST)returnAST;
					if ( inputState.guessing==0 ) {
						
											String s = Integer.toString (i++);
											AST expr = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(SUB_ASSIGN)).add(astFactory.create(IDENT,"p")).add(dn_AST));
											expr = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(QUESTION)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(LE)).add(expr).add(astFactory.create(FLOAT_LITERAL,"0")))).add(astFactory.create(INT_LITERAL,s)));
											if (m == null)
											{
												m = expr;
											}
											else
											{
												a.addChild (expr);
											}
											a = expr;
										
					}
				}
				else {
					if ( _cnt13>=1 ) { break _loop13; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt13++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				
								a.addChild (astFactory.create(INT_LITERAL,"0"));
							
			}
			break;
		}
		case LITERAL_table:
		{
			match(LITERAL_table);
			if ( inputState.guessing==0 ) {
				i = 0; a = astFactory.create(ARRAY_INIT);
			}
			{
			int _cnt15=0;
			_loop15:
			do {
				if ((LA(1)==INT_LITERAL||LA(1)==FLOAT_LITERAL||LA(1)==SUB)) {
					number();
					tn_AST = (AST)returnAST;
					if ( inputState.guessing==0 ) {
						a.addChild (tn_AST); i++;
					}
				}
				else {
					if ( _cnt15>=1 ) { break _loop15; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt15++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				
								m = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(RETURN)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(astFactory.create(IDENT,id.getText())).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGLIST)).add(astFactory.create(IDENT,"generation")))))));
								classDef.addChild ((AST)astFactory.make( (new ASTArray(7)).add(astFactory.create(METHOD)).add(astFactory.create(MODIFIERS)).add(astFactory.create(t)).add(astFactory.create(IDENT,id.getText())).add(astFactory.create(PARAMETERS)).add(astFactory.create(THROWS)).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SLIST)).add(m)))));
				
								String s = "$table_" + id_AST.getText () + '$';
								addVar (s, (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARRAY_DECLARATOR)).add(astFactory.create(FLOAT_))),
										(AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(MODIFIERS)).add(astFactory.create(STATIC_)).add(astFactory.create(FINAL_))), a);
								m = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(INDEX_OP)).add(astFactory.create(IDENT,s)).add((AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(QUESTION)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(LT)).add(astFactory.create(IDENT,"i")).add(astFactory.create(INT_LITERAL,Integer.toString(i- 1))))).add((AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(QUESTION)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(GT)).add(astFactory.create(IDENT,"i")).add(astFactory.create(INT_LITERAL,"0")))).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(astFactory.create(IDENT,"round")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGLIST)).add(astFactory.create(IDENT,"i")))))).add(astFactory.create(INT_LITERAL,"0")))).add(astFactory.create(INT_LITERAL,Integer.toString(i- 1))))));
								p.addChild ((AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(PARAMETER_DEF)).add(astFactory.create(MODIFIERS)).add(astFactory.create(FLOAT_)).add(astFactory.create(IDENT,"i"))));
							
			}
			break;
		}
		case LITERAL_generation:
		{
			match(LITERAL_generation);
			if ( inputState.guessing==0 ) {
				
								t = INT_;
								m = astFactory.create(IDENT,"generation");
							
			}
			break;
		}
		case LITERAL_register:
		{
			match(LITERAL_register);
			rid = LT(1);
			rid_AST = astFactory.create(rid);
			match(INT_LITERAL);
			if ( inputState.guessing==0 ) {
				
								String s = "r" + rid_AST.getText ();
								m = astFactory.create(IDENT,s);
							
			}
			break;
		}
		case LITERAL_local:
		{
			match(LITERAL_local);
			number();
			defValue_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				
								if(!symbolTableForLocalRegisters.containsKey(id_AST.getText())) {
									symbolTableForLocalRegisters.put(id_AST.getText(), localRegistersCount);
									defaultsForLocalRegisters.add(defValue_AST);
									m = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(astFactory.create(IDENT,"getLocalRegisterValue")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGLIST)).add(astFactory.create(INT_LITERAL,String.valueOf(localRegistersCount))))));
									localRegistersCount++;
								}
							
			}
			break;
		}
		case LITERAL_index:
		{
			match(LITERAL_index);
			{
			switch ( LA(1)) {
			case INT_LITERAL:
			{
				index = LT(1);
				index_AST = astFactory.create(index);
				match(INT_LITERAL);
				break;
			}
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
				
								t = INT_;
								String s = "$index"
									+ ((index_AST == null) ? "0" : index_AST.getText ()) + '$';
								m = astFactory.create(IDENT,s);
								if (!declaredVars.contains (s))
								{
									addVar (s, INT_);
								}
							
			}
			break;
		}
		case LITERAL_length:
		case LITERAL_diameter:
		case LITERAL_n_value:
		case LITERAL_v_value:
		case LITERAL_color:
		case LITERAL_order:
		case LITERAL_carbon:
		case LITERAL_q_value:
		case LITERAL_xcoordinate:
		case LITERAL_ycoordinate:
		case LITERAL_zcoordinate:
		{
			{
			switch ( LA(1)) {
			case LITERAL_length:
			{
				match(LITERAL_length);
				break;
			}
			case LITERAL_diameter:
			{
				match(LITERAL_diameter);
				break;
			}
			case LITERAL_n_value:
			{
				match(LITERAL_n_value);
				if ( inputState.guessing==0 ) {
					varType.setText ("parameter");
				}
				break;
			}
			case LITERAL_v_value:
			{
				match(LITERAL_v_value);
				if ( inputState.guessing==0 ) {
					varType.setText ("tropism");
				}
				break;
			}
			case LITERAL_color:
			{
				match(LITERAL_color);
				if ( inputState.guessing==0 ) {
					t = INT_;
				}
				break;
			}
			case LITERAL_order:
			{
				match(LITERAL_order);
				if ( inputState.guessing==0 ) {
					t = INT_;
				}
				break;
			}
			case LITERAL_carbon:
			{
				match(LITERAL_carbon);
				break;
			}
			case LITERAL_q_value:
			{
				match(LITERAL_q_value);
				if ( inputState.guessing==0 ) {
					varType.setText ("relPosition");
				}
				break;
			}
			case LITERAL_xcoordinate:
			{
				match(LITERAL_xcoordinate);
				break;
			}
			case LITERAL_ycoordinate:
			{
				match(LITERAL_ycoordinate);
				break;
			}
			case LITERAL_zcoordinate:
			{
				match(LITERAL_zcoordinate);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				
								varType.setType (IDENT);
								StringBuffer b = new StringBuffer (varType.getText ());
								b.setCharAt (0, Character.toUpperCase (b.charAt (0)));
								varType.setText (b.insert (0, "current").toString ());
								m = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(astFactory.create(varType)).add(astFactory.create(ARGLIST)));
							
			}
			break;
		}
		case LITERAL_function:
		{
			match(LITERAL_function);
			fn = LT(1);
			fn_AST = astFactory.create(fn);
			match(INT_LITERAL);
			fa = LT(1);
			fa_AST = astFactory.create(fa);
			match(INT_LITERAL);
			if ( inputState.guessing==0 ) {
				
								fn_AST.setType (IDENT);
								fn_AST.setText ("function" + fn_AST.getText ());
								AST last = null, first = null;
								int n = Integer.parseInt (fa_AST.getText ());
								for (i = 0; i < n; i++)
								{
									String s = "a" + i;
									AST next = astFactory.create(IDENT,s);
									if (first == null)
									{
										first = next;
									}
									else
									{
										last.setNextSibling (next);
									}
									last = next;
									p.addChild ((AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(PARAMETER_DEF)).add(astFactory.create(MODIFIERS)).add(astFactory.create(FLOAT_)).add(astFactory.create(IDENT,s))));
								}
								m = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(fn_AST).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGLIST)).add(first))));
							
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
						if (m != null)
						{
							String s = id_AST.getText ();
							if (!declaredMethods.add (s))
							{
								exceptionList.addSemanticError
									(Library.I18N.msg ("lsy.duplicate-var-declaration", s),
									 id);
							}
							s += '_';
							m = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(SLIST)).add(init).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(RETURN)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ASSIGN)).add(astFactory.create(IDENT,s)).add(m))))));
							classDef.addChild ((AST)astFactory.make( (new ASTArray(7)).add(astFactory.create(METHOD)).add(astFactory.create(MODIFIERS)).add(astFactory.create(t)).add(id_AST).add(p).add(astFactory.create(THROWS)).add(m)));
							addVar (s, t);
						}
					
		}
		returnAST = var_AST;
	}
	
	public final void binaryFunction() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST binaryFunction_AST = null;
		
		switch ( LA(1)) {
		case LITERAL_uniform:
		{
			AST tmp40_AST = null;
			tmp40_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp40_AST);
			match(LITERAL_uniform);
			binaryFunction_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_normal:
		{
			AST tmp41_AST = null;
			tmp41_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp41_AST);
			match(LITERAL_normal);
			binaryFunction_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_binomial:
		{
			AST tmp42_AST = null;
			tmp42_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp42_AST);
			match(LITERAL_binomial);
			binaryFunction_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_negbinomial:
		{
			AST tmp43_AST = null;
			tmp43_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp43_AST);
			match(LITERAL_negbinomial);
			binaryFunction_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = binaryFunction_AST;
	}
	
	public final void expression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;
		
		logicalOrExpression();
		astFactory.addASTChild(currentAST, returnAST);
		expression_AST = (AST)currentAST.root;
		returnAST = expression_AST;
	}
	
	public final boolean  moduleIdent() throws RecognitionException, TokenStreamException {
		boolean isJRegIdent;
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST moduleIdent_AST = null;
		isJRegIdent = false;
		
		{
		switch ( LA(1)) {
		case IDENT:
		{
			AST tmp44_AST = null;
			tmp44_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp44_AST);
			match(IDENT);
			break;
		}
		case JREG_IDENT:
		{
			AST tmp45_AST = null;
			tmp45_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp45_AST);
			match(JREG_IDENT);
			if ( inputState.guessing==0 ) {
				isJRegIdent = true;
			}
			break;
		}
		case ADD:
		{
			AST tmp46_AST = null;
			tmp46_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp46_AST);
			match(ADD);
			break;
		}
		case SUB:
		{
			AST tmp47_AST = null;
			tmp47_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp47_AST);
			match(SUB);
			break;
		}
		case MUL:
		{
			AST tmp48_AST = null;
			tmp48_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp48_AST);
			match(MUL);
			break;
		}
		case DIV:
		{
			AST tmp49_AST = null;
			tmp49_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp49_AST);
			match(DIV);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			moduleIdent_AST = (AST)currentAST.root;
			moduleIdent_AST.setType (IDENT);
		}
		moduleIdent_AST = (AST)currentAST.root;
		returnAST = moduleIdent_AST;
		return isJRegIdent;
	}
	
	public final void boundVarList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST boundVarList_AST = null;
		Token  id = null;
		AST id_AST = null;
		
		id = LT(1);
		id_AST = astFactory.create(id);
		astFactory.addASTChild(currentAST, id_AST);
		match(IDENT);
		if ( inputState.guessing==0 ) {
			
						if (declaredMethods.contains (id_AST.getText ()))
						{
							exceptionList.addSemanticError
								(Library.I18N, "lsy.bound-var-equals-var", id);
						}
					
		}
		{
		switch ( LA(1)) {
		case COMMA:
		{
			match(COMMA);
			boundVarList();
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
		boundVarList_AST = (AST)currentAST.root;
		returnAST = boundVarList_AST;
	}
	
	public final void rhs() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rhs_AST = null;
		AST c_AST = null;
		
				AST prev = null;
				AST nodes = null;
			
		
		{
		_loop28:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				rhsComponent();
				c_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					rhs_AST = (AST)currentAST.root;
					
									AST a = null;
									if (c_AST.getType () == NODE)
									{
										if (nodes == null)
										{
											nodes = astFactory.create(NODES);
											a = nodes;
										}
										nodes.addChild (c_AST);
									}
									else
									{
										a = c_AST;
										nodes = null;
									}
									if (a != null)
									{
										if (prev != null)
										{
											prev.setNextSibling (a);
										}
										else
										{
											rhs_AST = a;
										}
										prev = a;
									}
								
					currentAST.root = rhs_AST;
					currentAST.child = rhs_AST!=null &&rhs_AST.getFirstChild()!=null ?
						rhs_AST.getFirstChild() : rhs_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else {
				break _loop28;
			}
			
		} while (true);
		}
		returnAST = rhs_AST;
	}
	
	public final void rhsComponent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rhsComponent_AST = null;
		
		switch ( LA(1)) {
		case LBRACK:
		{
			tree();
			astFactory.addASTChild(currentAST, returnAST);
			rhsComponent_AST = (AST)currentAST.root;
			break;
		}
		case 259:
		{
			repetition();
			astFactory.addASTChild(currentAST, returnAST);
			rhsComponent_AST = (AST)currentAST.root;
			break;
		}
		case IDENT:
		case SUB:
		case MUL:
		case ADD:
		case DIV:
		case JREG_IDENT:
		{
			module();
			astFactory.addASTChild(currentAST, returnAST);
			rhsComponent_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = rhsComponent_AST;
	}
	
	public final void tree() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tree_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		
		lb = LT(1);
		lb_AST = astFactory.create(lb);
		astFactory.makeASTRoot(currentAST, lb_AST);
		match(LBRACK);
		rhs();
		astFactory.addASTChild(currentAST, returnAST);
		match(RBRACK);
		if ( inputState.guessing==0 ) {
			
					lb_AST.setType (TREE);
				
		}
		tree_AST = (AST)currentAST.root;
		returnAST = tree_AST;
	}
	
	public final void repetition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST repetition_AST = null;
		AST n_AST = null;
		AST s_AST = null;
		
		match(259);
		primaryExpression();
		n_AST = (AST)returnAST;
		AST tmp53_AST = null;
		tmp53_AST = astFactory.create(LT(1));
		match(LT);
		if ( inputState.guessing==0 ) {
			repetitionLevel++;
		}
		rhs();
		s_AST = (AST)returnAST;
		if ( inputState.guessing==0 ) {
			repetitionLevel--;
		}
		AST tmp54_AST = null;
		tmp54_AST = astFactory.create(LT(1));
		match(GT);
		if ( inputState.guessing==0 ) {
			repetition_AST = (AST)currentAST.root;
			
						String var = "$index" + repetitionLevel + '$',
							max = "$max" + repetitionLevel + '$';
						if (!declaredVars.contains (var))
						{
							addVar (var, INT_);
						}
						repetition_AST = (AST)astFactory.make( (new ASTArray(5)).add(astFactory.create(FOR)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(SLIST)).add((AST)astFactory.make( (new ASTArray(5)).add(astFactory.create(VARIABLE_DEF)).add(astFactory.create(MODIFIERS)).add(astFactory.create(INT_)).add(astFactory.create(IDENT,max)).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ASSIGN)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(astFactory.create(IDENT,"round")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGLIST)).add(n_AST))))))))).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ASSIGN)).add(astFactory.create(IDENT,var)).add(astFactory.create(INT_LITERAL,"0")))))).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(tmp53_AST)).add(astFactory.create(IDENT,var)).add(astFactory.create(IDENT,max)))).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ADD_ASSIGN)).add(astFactory.create(IDENT,var)).add(astFactory.create(INT_LITERAL,"1")))).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SLIST)).add(s_AST))));
					
			currentAST.root = repetition_AST;
			currentAST.child = repetition_AST!=null &&repetition_AST.getFirstChild()!=null ?
				repetition_AST.getFirstChild() : repetition_AST;
			currentAST.advanceChildToEnd();
		}
		returnAST = repetition_AST;
	}
	
	public final void module() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST module_AST = null;
		AST id_AST = null;
		AST args_AST = null;
		boolean isJReg;
		
		isJReg=moduleIdent();
		id_AST = (AST)returnAST;
		astFactory.addASTChild(currentAST, returnAST);
		moduleArgList();
		args_AST = (AST)returnAST;
		astFactory.addASTChild(currentAST, returnAST);
		if ( inputState.guessing==0 ) {
			module_AST = (AST)currentAST.root;
			
						if (isJReg)
						{
							if (args_AST.getNumberOfChildren () != 1)
							{
								exceptionList.addSemanticError
									(Library.I18N, "lsy.wrong-arg-count", id_AST);
								module_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(INVALID_EXPR)));
							}
							else
							{
								AST expr = args_AST.getFirstChild ();
								String v = "r" + id_AST.getText ().charAt (1);
								int t;
								switch (id_AST.getText ().charAt (2))
								{
									case '=':
										t = ASSIGN;
										break;
									case '+':
										t = ADD_ASSIGN;
										break;
									case '*':
										t = MUL_ASSIGN;
										break;
									default:
										throw new AssertionError ();
								}
								module_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(t)).add(astFactory.create(IDENT,v)).add(expr));
							}
						}
						else if(id_AST.getText().equals("K") || id_AST.getText().equals("KL") || id_AST.getText().equals("K="))
						{
							if(args_AST.getNumberOfChildren () != 1)
							{
								exceptionList.addSemanticError
									(Library.I18N, "lsy.wrong-arg-count", id_AST);
								module_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(INVALID_EXPR)));
							}
							else
							{
								String key = args_AST.getFirstChild().getText();
								String value = (String) symbolTableForLocalRegisters.get(key);
								if(value == null) {
									exceptionList.addSemanticError
										(Library.I18N, "lsy.unknown-localregister", id_AST);
									module_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(INVALID_EXPR)));
								} else {
									args_AST.getFirstChild().setText(value);
									args_AST.getFirstChild().setType(INT_LITERAL);
								}
							}
						}
						else if(id_AST.getText().equals("A") || id_AST.getText().equals("A+") || 
							    id_AST.getText().equals("A*") || id_AST.getText().equals("Ar") || 
							    id_AST.getText().equals("Ar+") || id_AST.getText().equals("Ar*"))
						{
							if(args_AST.getNumberOfChildren () != 2)
							{
								exceptionList.addSemanticError
									(Library.I18N, "lsy.wrong-arg-count", id_AST);
								module_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(INVALID_EXPR)));
							}
							else
							{
								String key = args_AST.getFirstChild().getText();
								String value = (String) symbolTableForLocalRegisters.get(key);
								if(value == null) {
									exceptionList.addSemanticError
										(Library.I18N, "lsy.unknown-localregister", id_AST);
									module_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(INVALID_EXPR)));
								} else {
									args_AST.getFirstChild().setText(value);
									args_AST.getFirstChild().setType(INT_LITERAL);
								}
							}
						}
						else
						{
							translate (id_AST, args_AST.getNumberOfChildren ());
							String s = id_AST.getText ();
							if (s.equals ("Plus") || s.equals ("Minus"))
							{
								args_AST.addChild (astFactory.create(IDENT,"angle"));
							}
							if (knownModules.add (s))
							{
								AST p = astFactory.create(PARAMETERS);
								for (int i = args_AST.getNumberOfChildren (); i > 0; i--)
								{
									String pid = "p" + i;
									p.addChild ((AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(PARAMETER_DEF)).add(astFactory.create(MODIFIERS)).add(astFactory.create(FLOAT_)).add(astFactory.create(IDENT,pid))));
								}
								p = (AST)astFactory.make( (new ASTArray(5)).add(astFactory.create(MODULE)).add(astFactory.create(MODIFIERS)).add(astFactory.create(IDENT,s)).add(astFactory.create(EXTENDS)).add(p));
								if (firstModuleDef == null)
								{
									firstModuleDef = p;
								}
								else
								{
									lastModuleDef.setNextSibling (p);
								}
								lastModuleDef = p;
							}
							module_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(NODE)).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(NEW)).add(module_AST))).add(astFactory.create(EMPTY)));
						}
					
			currentAST.root = module_AST;
			currentAST.child = module_AST!=null &&module_AST.getFirstChild()!=null ?
				module_AST.getFirstChild() : module_AST;
			currentAST.advanceChildToEnd();
		}
		module_AST = (AST)currentAST.root;
		returnAST = module_AST;
	}
	
	public final void primaryExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryExpression_AST = null;
		Token  id = null;
		AST id_AST = null;
		AST a_AST = null;
		Token  i = null;
		AST i_AST = null;
		AST so_AST = null;
		
		switch ( LA(1)) {
		case IDENT:
		{
			id = LT(1);
			id_AST = astFactory.create(id);
			match(IDENT);
			{
			switch ( LA(1)) {
			case LPAREN:
			{
				argList();
				a_AST = (AST)returnAST;
				break;
			}
			case EOF:
			case LT:
			case GT:
			case SUB:
			case MUL:
			case ADD:
			case DIV:
			case REM:
			case POW:
			case LE:
			case GE:
			case NOT_EQUALS:
			case EQUALS:
			case COR:
			case CAND:
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
				primaryExpression_AST = (AST)currentAST.root;
				
							if ((declaredMethods.contains (id_AST.getText ()))
								&& (a_AST == null))
							{
								a_AST = astFactory.create(ARGLIST);
							}
							if (a_AST != null)
							{
								primaryExpression_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(id_AST).add(a_AST));
							}
							else
							{
								primaryExpression_AST = id_AST;
							}
						
				currentAST.root = primaryExpression_AST;
				currentAST.child = primaryExpression_AST!=null &&primaryExpression_AST.getFirstChild()!=null ?
					primaryExpression_AST.getFirstChild() : primaryExpression_AST;
				currentAST.advanceChildToEnd();
			}
			break;
		}
		case LITERAL_if:
		{
			i = LT(1);
			i_AST = astFactory.create(i);
			astFactory.makeASTRoot(currentAST, i_AST);
			match(LITERAL_if);
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(COMMA);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(COMMA);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			if ( inputState.guessing==0 ) {
				i_AST.setType (QUESTION);
			}
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_sum:
		case LITERAL_sumd:
		case LITERAL_sump:
		{
			sumOp();
			so_AST = (AST)returnAST;
			sumRest(so_AST);
			astFactory.addASTChild(currentAST, returnAST);
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case LPAREN:
		{
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case INT_LITERAL:
		{
			AST tmp61_AST = null;
			tmp61_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp61_AST);
			match(INT_LITERAL);
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case FLOAT_LITERAL:
		{
			AST tmp62_AST = null;
			tmp62_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp62_AST);
			match(FLOAT_LITERAL);
			primaryExpression_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = primaryExpression_AST;
	}
	
	public final void moduleArgList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST moduleArgList_AST = null;
		
		{
		boolean synPredMatched38 = false;
		if (((LA(1)==INT_LITERAL||LA(1)==FLOAT_LITERAL||LA(1)==SUB))) {
			int _m38 = mark();
			synPredMatched38 = true;
			inputState.guessing++;
			try {
				{
				number();
				}
			}
			catch (RecognitionException pe) {
				synPredMatched38 = false;
			}
			rewind(_m38);
inputState.guessing--;
		}
		if ( synPredMatched38 ) {
			number();
			astFactory.addASTChild(currentAST, returnAST);
		}
		else if ((LA(1)==LPAREN)) {
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop40:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					expression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop40;
				}
				
			} while (true);
			}
			match(RPAREN);
		}
		else if ((_tokenSet_1.member(LA(1)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		if ( inputState.guessing==0 ) {
			moduleArgList_AST = (AST)currentAST.root;
			moduleArgList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGLIST)).add(moduleArgList_AST));
			currentAST.root = moduleArgList_AST;
			currentAST.child = moduleArgList_AST!=null &&moduleArgList_AST.getFirstChild()!=null ?
				moduleArgList_AST.getFirstChild() : moduleArgList_AST;
			currentAST.advanceChildToEnd();
		}
		moduleArgList_AST = (AST)currentAST.root;
		returnAST = moduleArgList_AST;
	}
	
	public final void logicalOrExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalOrExpression_AST = null;
		
		logicalAndExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop44:
		do {
			if ((LA(1)==COR)) {
				AST tmp66_AST = null;
				tmp66_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp66_AST);
				match(COR);
				logicalAndExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop44;
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
		
		equalityExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop47:
		do {
			if ((LA(1)==CAND)) {
				AST tmp67_AST = null;
				tmp67_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp67_AST);
				match(CAND);
				equalityExpression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop47;
			}
			
		} while (true);
		}
		logicalAndExpression_AST = (AST)currentAST.root;
		returnAST = logicalAndExpression_AST;
	}
	
	public final void equalityExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equalityExpression_AST = null;
		
		relationalExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop51:
		do {
			if ((LA(1)==NOT_EQUALS||LA(1)==EQUALS)) {
				{
				switch ( LA(1)) {
				case NOT_EQUALS:
				{
					AST tmp68_AST = null;
					tmp68_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp68_AST);
					match(NOT_EQUALS);
					break;
				}
				case EQUALS:
				{
					AST tmp69_AST = null;
					tmp69_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp69_AST);
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
				break _loop51;
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
		
		additiveExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop55:
		do {
			if ((_tokenSet_2.member(LA(1)))) {
				{
				switch ( LA(1)) {
				case LT:
				{
					AST tmp70_AST = null;
					tmp70_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp70_AST);
					match(LT);
					break;
				}
				case GT:
				{
					AST tmp71_AST = null;
					tmp71_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp71_AST);
					match(GT);
					break;
				}
				case LE:
				{
					AST tmp72_AST = null;
					tmp72_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp72_AST);
					match(LE);
					break;
				}
				case GE:
				{
					AST tmp73_AST = null;
					tmp73_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp73_AST);
					match(GE);
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
				break _loop55;
			}
			
		} while (true);
		}
		relationalExpression_AST = (AST)currentAST.root;
		returnAST = relationalExpression_AST;
	}
	
	public final void additiveExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST additiveExpression_AST = null;
		
		multiplicativeExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop59:
		do {
			if ((LA(1)==SUB||LA(1)==ADD)) {
				{
				switch ( LA(1)) {
				case ADD:
				{
					AST tmp74_AST = null;
					tmp74_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp74_AST);
					match(ADD);
					break;
				}
				case SUB:
				{
					AST tmp75_AST = null;
					tmp75_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp75_AST);
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
				break _loop59;
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
		_loop63:
		do {
			if ((LA(1)==MUL||LA(1)==DIV||LA(1)==REM)) {
				{
				switch ( LA(1)) {
				case MUL:
				{
					AST tmp76_AST = null;
					tmp76_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp76_AST);
					match(MUL);
					break;
				}
				case DIV:
				{
					AST tmp77_AST = null;
					tmp77_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp77_AST);
					match(DIV);
					break;
				}
				case REM:
				{
					AST tmp78_AST = null;
					tmp78_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp78_AST);
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
				break _loop63;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			multiplicativeExpression_AST = (AST)currentAST.root;
			
						AST t = multiplicativeExpression_AST;
						if (t.getType () == DIV)
						{
							t = t.getFirstChild ();
							t.setNextSibling ((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(TYPECAST)).add(astFactory.create(FLOAT_)).add(t.getNextSibling())));
						}
					
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
			AST tmp79_AST = null;
			tmp79_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp79_AST);
			match(POW);
			powerExpression();
			astFactory.addASTChild(currentAST, returnAST);
			break;
		}
		case EOF:
		case LT:
		case GT:
		case SUB:
		case MUL:
		case ADD:
		case DIV:
		case REM:
		case LE:
		case GE:
		case NOT_EQUALS:
		case EQUALS:
		case COR:
		case CAND:
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
		powerExpression_AST = (AST)currentAST.root;
		returnAST = powerExpression_AST;
	}
	
	public final void unaryExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryExpression_AST = null;
		
		switch ( LA(1)) {
		case SUB:
		{
			AST tmp80_AST = null;
			tmp80_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp80_AST);
			match(SUB);
			if ( inputState.guessing==0 ) {
				unaryExpression_AST = (AST)currentAST.root;
				unaryExpression_AST.setType (NEG);
			}
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			unaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case ADD:
		{
			AST tmp81_AST = null;
			tmp81_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp81_AST);
			match(ADD);
			if ( inputState.guessing==0 ) {
				unaryExpression_AST = (AST)currentAST.root;
				unaryExpression_AST.setType (POS);
			}
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			unaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case NOT:
		{
			AST tmp82_AST = null;
			tmp82_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp82_AST);
			match(NOT);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			unaryExpression_AST = (AST)currentAST.root;
			break;
		}
		case INT_LITERAL:
		case FLOAT_LITERAL:
		case IDENT:
		case LPAREN:
		case LITERAL_if:
		case LITERAL_sum:
		case LITERAL_sumd:
		case LITERAL_sump:
		{
			primaryExpression();
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
	
	public final void argList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST argList_AST = null;
		Token  lp = null;
		AST lp_AST = null;
		
		lp = LT(1);
		lp_AST = astFactory.create(lp);
		astFactory.makeASTRoot(currentAST, lp_AST);
		match(LPAREN);
		expression();
		astFactory.addASTChild(currentAST, returnAST);
		{
		_loop74:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				expression();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				break _loop74;
			}
			
		} while (true);
		}
		match(RPAREN);
		if ( inputState.guessing==0 ) {
			lp_AST.setType (ARGLIST);
		}
		argList_AST = (AST)currentAST.root;
		returnAST = argList_AST;
	}
	
	public final void sumOp() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST sumOp_AST = null;
		
		switch ( LA(1)) {
		case LITERAL_sum:
		{
			AST tmp85_AST = null;
			tmp85_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp85_AST);
			match(LITERAL_sum);
			sumOp_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_sumd:
		{
			AST tmp86_AST = null;
			tmp86_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp86_AST);
			match(LITERAL_sumd);
			sumOp_AST = (AST)currentAST.root;
			break;
		}
		case LITERAL_sump:
		{
			AST tmp87_AST = null;
			tmp87_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp87_AST);
			match(LITERAL_sump);
			sumOp_AST = (AST)currentAST.root;
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = sumOp_AST;
	}
	
	public final void sumRest(
		AST op
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST sumRest_AST = null;
		AST e1_AST = null;
		AST e2_AST = null;
		
		AST tmp88_AST = null;
		tmp88_AST = astFactory.create(LT(1));
		match(LPAREN);
		expression();
		e1_AST = (AST)returnAST;
		{
		switch ( LA(1)) {
		case COMMA:
		{
			AST tmp89_AST = null;
			tmp89_AST = astFactory.create(LT(1));
			match(COMMA);
			expression();
			e2_AST = (AST)returnAST;
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
		AST tmp90_AST = null;
		tmp90_AST = astFactory.create(LT(1));
		match(RPAREN);
		if ( inputState.guessing==0 ) {
			sumRest_AST = (AST)currentAST.root;
			
					op.setType (IDENT);
					op.setText (op.getText () + "Generator");
					AST gen = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(op).add(astFactory.create(ARGLIST)));
					if (e2_AST != null)
					{
						gen = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(GUARD)).add(gen).add(e1_AST));
						e1_AST = e2_AST;
					}
					sumRest_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL)).add(getOperator("sum")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGLIST)).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ELIST)).add(gen).add(e1_AST))))));
				
			currentAST.root = sumRest_AST;
			currentAST.child = sumRest_AST!=null &&sumRest_AST.getFirstChild()!=null ?
				sumRest_AST.getFirstChild() : sumRest_AST;
			currentAST.advanceChildToEnd();
		}
		returnAST = sumRest_AST;
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
		"PACKAGE",
		"MODULE",
		"CLASS",
		"INTERFACE",
		"IMPORT_ON_DEMAND",
		"STATIC_IMPORT_ON_DEMAND",
		"SINGLE_TYPE_IMPORT",
		"SINGLE_STATIC_IMPORT",
		"EXTENDS",
		"IMPLEMENTS",
		"PARAMETERS",
		"PARAMETER_DEF",
		"SUPER",
		"ARGLIST",
		"SLIST",
		"INSTANTIATOR",
		"METHOD",
		"THROWS",
		"SEMI",
		"CONSTRUCTOR",
		"VARIABLE_DEF",
		"ASSIGN",
		"ARRAY_DECLARATOR",
		"DECLARING_TYPE",
		"VOID_",
		"BOOLEAN_",
		"BYTE_",
		"SHORT_",
		"CHAR_",
		"INT_",
		"LONG_",
		"FLOAT_",
		"DOUBLE_",
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
		"LINE",
		"LEFT_RIGHT_ARROW",
		"PLUS_LEFT_ARROW",
		"PLUS_ARROW",
		"PLUS_LINE",
		"PLUS_LEFT_RIGHT_ARROW",
		"SLASH_LEFT_ARROW",
		"SLASH_ARROW",
		"SLASH_LINE",
		"SLASH_LEFT_RIGHT_ARROW",
		"TYPE_PATTERN",
		"WRAPPED_TYPE_PATTERN",
		"NAME_PATTERN",
		"TREE",
		"CONTEXT",
		"EXPR",
		"ROOT",
		"METHOD_PATTERN",
		"METHOD_CALL",
		"DOT",
		"APPLICATION_CONDITION",
		"PARAMETERIZED_PATTERN",
		"\"-\"",
		"LEFT_ARROW",
		"ARROW",
		"X_LEFT_RIGHT_ARROW",
		"TRAVERSAL",
		"QUESTION",
		"\"*\"",
		"\"+\"",
		"RANGE_EXACTLY",
		"RANGE_MIN",
		"RANGE",
		"RULE",
		"DOUBLE_ARROW_RULE",
		"EXEC_RULE",
		"PRODUCE",
		"WITH",
		"UNARY_PREFIX",
		"TYPECAST",
		"TYPECHECK",
		"COM",
		"\"!\"",
		"NEG",
		"POS",
		"\"/\"",
		"\"%\"",
		"\"^\"",
		"SHL",
		"SHR",
		"USHR",
		"\"<=\"",
		"\">=\"",
		"CMP",
		"\"!=\"",
		"\"==\"",
		"OR",
		"XOR",
		"AND",
		"\"||\"",
		"\"&&\"",
		"ARRAY_INIT",
		"RULE_BLOCK",
		"ELIST",
		"SHELL_BLOCK",
		"THIS",
		"QUALIFIED_SUPER",
		"IF",
		"RETURN",
		"YIELD",
		"THROW",
		"SYNCHRONIZED_",
		"ASSERT",
		"LABELED_STATEMENT",
		"BREAK",
		"CONTINUE",
		"TRY",
		"CATCH",
		"FINALLY",
		"NODES",
		"NODE",
		"FOR",
		"ENHANCED_FOR",
		"WHILE",
		"DO",
		"SWITCH",
		"SWITCH_GROUP",
		"CASE",
		"DEFAULT",
		"NULL_LITERAL",
		"INVALID_EXPR",
		"LONG_LEFT_ARROW",
		"LONG_ARROW",
		"LONG_LEFT_RIGHT_ARROW",
		"INSTANCEOF",
		"CLASS_LITERAL",
		"QUOTE",
		"ADD_ASSIGN",
		"SUB_ASSIGN",
		"MUL_ASSIGN",
		"DIV_ASSIGN",
		"REM_ASSIGN",
		"POW_ASSIGN",
		"SHR_ASSIGN",
		"USHR_ASSIGN",
		"SHL_ASSIGN",
		"AND_ASSIGN",
		"XOR_ASSIGN",
		"OR_ASSIGN",
		"DEFERRED_ASSIGN",
		"DEFERRED_RATE_ASSIGN",
		"DEFERRED_ADD",
		"DEFERRED_SUB",
		"DEFERRED_MUL",
		"DEFERRED_DIV",
		"DEFERRED_REM",
		"DEFERRED_POW",
		"DEFERRED_OR",
		"DEFERRED_AND",
		"DEFERRED_XOR",
		"DEFERRED_SHL",
		"DEFERRED_SHR",
		"DEFERRED_USHR",
		"INC",
		"DEC",
		"POST_INC",
		"POST_DEC",
		"IN",
		"GUARD",
		"ARRAY_ITERATOR",
		"QUERY_EXPR",
		"INVOKE_OP",
		"QUALIFIED_NEW",
		"INDEX_OP",
		"NEW",
		"DIMLIST",
		"MODIFIERS",
		"ANNOTATION",
		"PRIVATE_",
		"PUBLIC_",
		"PROTECTED_",
		"STATIC_",
		"TRANSIENT_",
		"FINAL_",
		"ABSTRACT_",
		"NATIVE_",
		"VOLATILE_",
		"STRICT_",
		"ITERATING_",
		"CONST_",
		"VARARGS_",
		"STATIC_MEMBER_CLASSES",
		"MARKER",
		"SINGLE_ELEMENT",
		"NORMAL",
		"\"(\"",
		"\")\"",
		"\"[\"",
		"\"]\"",
		"\":\"",
		"\",\"",
		"JREG_IDENT",
		"\"\\\\set\"",
		"\"\\\\length\"",
		"\"\\\\laenge\"",
		"\"\\\\angle\"",
		"\"\\\\winkel\"",
		"\"\\\\const\"",
		"\"\\\\register\"",
		"\"\\\\var\"",
		"\"uniform\"",
		"\"normal\"",
		"\"binomial\"",
		"\"negbinomial\"",
		"\"poisson\"",
		"\"distribution\"",
		"\"table\"",
		"\"generation\"",
		"\"register\"",
		"\"local\"",
		"\"index\"",
		"\"length\"",
		"\"diameter\"",
		"\"n_value\"",
		"\"v_value\"",
		"\"color\"",
		"\"order\"",
		"\"carbon\"",
		"\"q_value\"",
		"\"xcoordinate\"",
		"\"ycoordinate\"",
		"\"zcoordinate\"",
		"\"function\"",
		"\"#\"",
		"\"##\"",
		"\"?\"",
		"\"&\"",
		"\"if\"",
		"\"sum\"",
		"\"sumd\"",
		"\"sump\""
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[10];
		data[0]=2048L;
		data[1]=17592590794752L;
		data[3]=4563402752L;
		data[4]=8L;
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = new long[10];
		data[0]=4611686018427389954L;
		data[1]=52776962883584L;
		data[3]=7247757312L;
		data[4]=12L;
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 6917529027641081856L, 3377699720527872L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	
	}
