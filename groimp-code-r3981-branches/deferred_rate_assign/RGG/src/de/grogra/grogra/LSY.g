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

package de.grogra.grogra;

import de.grogra.rgg.Library;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
}


class LSYParser extends Parser;


options
{
	k = 1;
	importVocab = Compiler;
	exportVocab = LSY;
	buildAST = true;
	defaultErrorHandler = false;
	classHeaderPrefix="/*";
	classHeaderSuffix="*/ public class LSYParser extends de.grogra.xl.parser.Parser implements LSYTokenTypes";
}


tokens
{
	LPAREN = "(";
	RPAREN = ")";
	LBRACK = "[";
	RBRACK = "]";
	COLON = ":";
	COMMA = ",";
	EQUALS = "==";
	NOT = "!";
	NOT_EQUALS = "!=";
	DIV = "/";
	ADD = "+";
	SUB = "-";
	MUL = "*";
	POW = "^";
	REM = "%";
	GE = ">=";
	GT = ">";
	LE = "<=";
	LT = "<";
	COR = "||";
	CAND = "&&";

	JREG_IDENT;
}


{
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
		String s = #id.getText () + arity, t = (String) translations.get (s);
		#id.setText ((t == null) ? s : t);
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
		addVar (name, type, #([MODIFIERS], [TRANSIENT_]), expr);
	}


	private void addVar (String name, int type, AST modifiers, AST expr)
	{
		addVar (name, #[type], modifiers, expr);
	}


	private void addVar (String name, AST type, AST modifiers, AST expr)
	{
		if (expr != null)
		{
			expr = #([ASSIGN], expr);
		}
		classDef.addChild (#([VARIABLE_DEF], modifiers, type,
							 [IDENT, name], expr));
		declaredVars.add (name);
	}


	private void addMethod (String name, AST body)
	{
		classDef.addChild (#([METHOD], #([MODIFIERS], [PUBLIC_]), #[VOID_],
							 #[IDENT, name], #[PARAMETERS], #[THROWS], body));
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
		AST a = #([DOT], [IDENT, "de"], [IDENT, "grogra"]);
		a = #([DOT], a, [IDENT, "xl"]);
		a = #([DOT], a, [IDENT, "lang"]);
		a = #([DOT], a, [IDENT, "Operators"]);
		a = #([DOT], a, [IDENT, name]);
		return a; 
	}

}


compilationUnit![String clsName] returns [AST unit]
	{
		classDef = #([CLASS], #([MODIFIERS], [PUBLIC_]), [IDENT, clsName],
							  #([EXTENDS], [IDENT, "LSystem"]));
		initializer = #[SLIST];
		firstModuleDef = null;
		lastModuleDef = null;
		repetitionLevel = 0;
		patterns = new HashMap ();
		declaredMethods = new HashSet ();
		declaredVars = new HashSet ();
		knownModules = new HashSet (translations.values ());
		classDef.addChild (#([INSTANCE_INIT], initializer));
		unit = null;
		derivation = #([RULE_BLOCK], [EMPTY]);
		interpretation = #([RULE_BLOCK], [EMPTY]);
		addMethod ("derivation", #([SLIST], derivation));
		addMethod ("interpretation", #([SLIST], interpretation));
	}
	:   (declaration COMMA)*
		rule moreRules
		EOF
		{
			AST arrayDeclaration = #([ARRAY_DECLARATOR], [FLOAT_]);
			AST arrayValues = #[ARRAY_INIT];
			for(int i = 0; i < localRegistersCount; i++) {
				arrayValues.addChild(defaultsForLocalRegisters.get(i));
			}
			
			AST newArray = #([NEW], arrayDeclaration, arrayValues);
			initializer.addChild(#([ASSIGN], [IDENT, "defaultValuesForLocalRegisters"], newArray));
			
			if (firstModuleDef != null)
			{
				lastModuleDef.setNextSibling (classDef);
				unit = firstModuleDef;
			}
			else
			{
				unit = classDef;
			}
			unit = #([COMPILATION_UNIT], unit);
		}
	;


moreRules!
	:	COMMA (rule moreRules)?
	|
	;


declaration!
	:   "\\set" sid:IDENT sn:number
		{
			String s = #sid.getText ();
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
				if (needInt && (#sn.getType () == FLOAT_LITERAL))
				{
					#sn.setType (INT_LITERAL);
					#sn.setText (Integer.toString
						(Math.round (new Float (#sn.getText ()).floatValue ())));
				}
				#sid.setText (s);
				if (!"angle".equals (s))
				{
					#sid = #([DOT], [IDENT, "initialTurtleState"], sid);
				}
			}
			initializer.addChild (#([ASSIGN], sid, sn));
		}
//	|   "\\ask" aid:IDENT q:STRING_LITERAL
	|   ("\\length" | "\\laenge") l:number
		{
			initializer.addChild (#([ASSIGN], #([DOT], [IDENT, "initialTurtleState"], [IDENT, "length"]), l));
		}
	|   ("\\angle" | "\\winkel") w:number
		{
			initializer.addChild (#([ASSIGN], #[IDENT, "angle"], w));
		}
	|   "\\const" cid:IDENT cn:number
		{
			addVar (#cid.getText (), FLOAT_,
					#([MODIFIERS], [STATIC_], [FINAL_]), #cn);
		}
	|   "\\register" i:INT_LITERAL rn:number
		{
			#i.setType (IDENT);
			#i.setText ("r" + #i.getText ());
			initializer.addChild (#([ASSIGN], i, rn));
		}
	|   "\\var" var
	;


binaryFunction
	:	"uniform" | "normal" | "binomial" | "negbinomial"
	;


var!
	{
		AST m = null, a = null, p = #[PARAMETERS], init = null;
		int t = FLOAT_, i = 0;
		Token varType = null;
	}
	:   id:IDENT
		{
			varType = LT (1);
		}
		(	bf:binaryFunction u1:number u2:number
			{
				switch (#bf.getType ())
				{
					case LITERAL_normal:
						float v = new Float (#u2.getText ()).floatValue ();
						if (v <= 0f)
						{
							exceptionList.addSemanticError
								(Library.I18N, "lsy.variance-nonpositive", #u2);
							v = 1f;
						}
						#u2.setType (FLOAT_LITERAL);
						#u2.setText (Float.toString ((float) Math.sqrt (v)));
						break;
				}
				#bf.setType (IDENT);
				m = #([METHOD_CALL], bf, #([ARGLIST], u1, u2));
			}
		|	poi:"poisson" ep:number
			{
				#poi.setType (IDENT);
				m = #([METHOD_CALL], poi, #([ARGLIST], ep));
			}
		|   "distribution"
			{
				t = INT_;
				init = #([VARIABLE_DEF], [MODIFIERS], [FLOAT_],
							 [IDENT, "p"],
							 #([ASSIGN], #([METHOD_CALL], [IDENT, "random"],
										   [ARGLIST])));
				i = 0;
			}
			(dn:number
				{
					String s = Integer.toString (i++);
					AST expr = #([SUB_ASSIGN], [IDENT, "p"], dn);
					expr = #([QUESTION], #([LE], expr, [FLOAT_LITERAL, "0"]),
										 [INT_LITERAL, s]);
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
			)+
			{
				a.addChild (#[INT_LITERAL, "0"]);
			}
//		|   "user_request"
		|   "table" { i = 0; a = #[ARRAY_INIT]; }
			(tn:number { a.addChild (#tn); i++; } )+
			{
				m = #([RETURN],
						#([METHOD_CALL], [IDENT, #id.getText ()],
						  #([ARGLIST], [IDENT, "generation"])));
				classDef.addChild (#([METHOD], [MODIFIERS], [t],
									 [IDENT, #id.getText ()], [PARAMETERS],
									 [THROWS], #([SLIST], m)));

				String s = "$table_" + #id.getText () + '$';
				addVar (s, #([ARRAY_DECLARATOR], [FLOAT_]),
						#([MODIFIERS], [STATIC_], [FINAL_]), a);
				m = #([INDEX_OP], [IDENT, s],
						#([QUESTION],
							#([LT], [IDENT, "i"],
									[INT_LITERAL, Integer.toString (i - 1)]),
							#([QUESTION],
								#([GT], [IDENT, "i"], [INT_LITERAL, "0"]),
								#([METHOD_CALL], [IDENT, "round"],
									#([ARGLIST], [IDENT, "i"])),
								[INT_LITERAL, "0"]),
							[INT_LITERAL, Integer.toString (i - 1)]));
				p.addChild (#([PARAMETER_DEF], [MODIFIERS],
							[FLOAT_], [IDENT, "i"]));
			}
//		|   "array" INT_LITERAL INT_LITERAL (INT_LITERAL)+
		|   "generation"
			{
				t = INT_;
				m = #[IDENT, "generation"];
			}
		|   "register" rid:INT_LITERAL
			{
				String s = "r" + #rid.getText ();
				m = #[IDENT, s];
			}
		|	"local" defValue:number
			{
				if(!symbolTableForLocalRegisters.containsKey(#id.getText())) {
					symbolTableForLocalRegisters.put(#id.getText(), localRegistersCount);
					defaultsForLocalRegisters.add(#defValue);
					m = #([METHOD_CALL], [IDENT, "getLocalRegisterValue"], #([ARGLIST], [INT_LITERAL, String.valueOf(localRegistersCount)]));
					localRegistersCount++;
				}
			}
		|   "index" (index:INT_LITERAL)?
			{
				t = INT_;
				String s = "$index"
					+ ((#index == null) ? "0" : #index.getText ()) + '$';
				m = #[IDENT, s];
				if (!declaredVars.contains (s))
				{
					addVar (s, INT_);
				}
			}
		|	(	"length"
			|	"diameter"
			|	"n_value" {varType.setText ("parameter");}
			|	"v_value" {varType.setText ("tropism");}
			|	"color" {t = INT_;}
			|	"order" {t = INT_;}
			|	"carbon"
			|	"q_value" {varType.setText ("relPosition");}
			|	"xcoordinate"
			|	"ycoordinate"
			|	"zcoordinate"
			)
			{
				varType.setType (IDENT);
				StringBuffer b = new StringBuffer (varType.getText ());
				b.setCharAt (0, Character.toUpperCase (b.charAt (0)));
				varType.setText (b.insert (0, "current").toString ());
				m = #([METHOD_CALL], [varType], [ARGLIST]);
			}
//		|   "depth"
		|   "function" fn:INT_LITERAL fa:INT_LITERAL
			{
				#fn.setType (IDENT);
				#fn.setText ("function" + #fn.getText ());
				AST last = null, first = null;
				int n = Integer.parseInt (#fa.getText ());
				for (i = 0; i < n; i++)
				{
					String s = "a" + i;
					AST next = #[IDENT, s];
					if (first == null)
					{
						first = next;
					}
					else
					{
						last.setNextSibling (next);
					}
					last = next;
					p.addChild (#([PARAMETER_DEF], [MODIFIERS],
								  [FLOAT_], [IDENT, s]));
				}
				m = #([METHOD_CALL], fn, #([ARGLIST], first));
			}
		)
		{
			if (m != null)
			{
				String s = #id.getText ();
				if (!declaredMethods.add (s))
				{
					exceptionList.addSemanticError
						(Library.I18N.msg ("lsy.duplicate-var-declaration", s),
						 id);
				}
				s += '_';
				m = #([SLIST], init,
					  #([RETURN], #([ASSIGN], [IDENT, s], m)));
				classDef.addChild (#([METHOD], #[MODIFIERS], [t], id, p, [THROWS], m));
				addVar (s, t);
			}
		}
	;


rule!
	{ boolean generative = true, isReg; }
	:   (LPAREN c:expression RPAREN)?
		isReg=id:moduleIdent (LPAREN l:boundVarList RPAREN)?
		(   "#" t:rhs (cut:REM)?
        |   "##" t2:rhs { #t = #t2; generative = false; }
		)
		("?" p:expression)?
		{
			if (isReg)
			{
				if (exceptionList.isWarning (WARN_ON_REGISTER_PATTERN))
				{
					exceptionList.addSemanticWarning
						(Library.I18N.msg ("lsy.register-ignored",
										#id.getText ()),
						 #id);
				}
			}
			else
			{
				AST s = null;
				if (generative)
				{
					if (#cut != null)
					{
						s = #([METHOD_CALL], [IDENT, "setOutConnectionEdges"],
							  #([ARGLIST], [INT_LITERAL, "0"]));
					}
				}
				s = #([SLIST], t, s);
				if (#p != null)
				{
					#p = #([METHOD_CALL], [IDENT, "ruleProbability"], #([ARGLIST], p));
					if (#c != null)
					{
						s = #([IF], #([CAND], c, p), s);
					}
					else
					{
						s = #([IF], p, s);
					}
				}
				else if (#c != null)
				{
					s = #([IF], c, s);
				}

				#l = #([ARGLIST], l);
				int nc = #l.getNumberOfChildren ();
				String key = #id.getText () + generative + nc;
				translate (#id, nc);
				PatternInfo p = (PatternInfo) patterns.get (key);
				if (p == null)
				{
					patterns.put (key,
						p = new PatternInfo (#l.getFirstChild (), s));
					AST m = (nc == 0) ? #([TYPE_PATTERN], id)
						: #([PARAMETERIZED_PATTERN], id, l);
					m = #([LABEL], [IDENT, "lhs."], m);
					(generative ? derivation : interpretation)
						.addChild (#([RULE],
									 #([QUERY], [EMPTY], #([COMPOUND_PATTERN], m)),
									 #([SLIST],
									   #([METHOD_CALL], [IDENT, "patternMatched"],
									     #([ARGLIST],
										   #([METHOD_CALL], [IDENT, "producer$getProducer"],
											 [ARGLIST]))),
									   s)));
				}
				else
				{
					if (p.lastExpr.getType () == IF)
					{
						p.replace (s, #l.getFirstChild ());
						p.lastExpr.getFirstChild ().getNextSibling ()
							.setNextSibling (s);
						p.lastExpr = s;
					}
					else if (exceptionList.isWarning (WARN_ON_UNUSED_RULES))
					{
						exceptionList.addSemanticWarning
							(Library.I18N, "lsy.rule-unused", #id);
					}
				}
				if (s.getType () == IF)
				{
					s.addChild (#[BREAK]);
				}
			}
		}
	;


boundVarList
	:   id:IDENT
		{
			if (declaredMethods.contains (#id.getText ()))
			{
				exceptionList.addSemanticError
					(Library.I18N, "lsy.bound-var-equals-var", id);
			}
		}
		(COMMA! boundVarList)?
	;


rhs!
	{
		AST prev = null;
		AST nodes = null;
	}
	:	(	c:rhsComponent
			{
				AST a = null;
				if (#c.getType () == NODE)
				{
					if (nodes == null)
					{
						nodes = #[NODES];
						a = nodes;
					}
					nodes.addChild (#c);
				}
				else
				{
					a = #c;
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
						#rhs = a;
					}
					prev = a;
				}
			}
		)*
	;

rhsComponent
	:	tree | repetition | module
	;


tree
	:	lb:LBRACK^ rhs RBRACK!
	{
		#lb.setType (TREE);
	}
	;

repetition!
	:   "&" n:primaryExpression
			LT { repetitionLevel++; } s:rhs { repetitionLevel--; } GT
		{
			String var = "$index" + repetitionLevel + '$',
				max = "$max" + repetitionLevel + '$';
			if (!declaredVars.contains (var))
			{
				addVar (var, INT_);
			}
			#repetition = #([FOR],
				#([SLIST], #([VARIABLE_DEF], [MODIFIERS],
							 [INT_], [IDENT, max],
							 #([ASSIGN], #([METHOD_CALL], [IDENT, "round"],
										   #([ARGLIST], n)))),
						   #([ASSIGN], [IDENT, var], [INT_LITERAL, "0"])),
				#([LT], [IDENT, var], [IDENT, max]),
				#([ADD_ASSIGN], [IDENT, var], [INT_LITERAL, "1"]),
				#([SLIST], s));
		}
	;


module
	{ boolean isJReg; }
	:   isJReg=id:moduleIdent args:moduleArgList
		{
			if (isJReg)
			{
				if (#args.getNumberOfChildren () != 1)
				{
					exceptionList.addSemanticError
						(Library.I18N, "lsy.wrong-arg-count", #id);
					#module = #([INVALID_EXPR]);
				}
				else
				{
					AST expr = #args.getFirstChild ();
					String v = "r" + #id.getText ().charAt (1);
					int t;
					switch (#id.getText ().charAt (2))
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
					#module = #([t], [IDENT, v], expr);
				}
			}
			else if(#id.getText().equals("K") || #id.getText().equals("KL") || #id.getText().equals("K="))
			{
				if(#args.getNumberOfChildren () != 1)
				{
					exceptionList.addSemanticError
						(Library.I18N, "lsy.wrong-arg-count", #id);
					#module = #([INVALID_EXPR]);
				}
				else
				{
					String key = #args.getFirstChild().getText();
					String value = (String) symbolTableForLocalRegisters.get(key);
					if(value == null) {
						exceptionList.addSemanticError
							(Library.I18N, "lsy.unknown-localregister", #id);
						#module = #([INVALID_EXPR]);
					} else {
						#args.getFirstChild().setText(value);
						#args.getFirstChild().setType(INT_LITERAL);
					}
				}
			}
			else if(#id.getText().equals("A") || #id.getText().equals("A+") || 
				    #id.getText().equals("A*") || #id.getText().equals("Ar") || 
				    #id.getText().equals("Ar+") || #id.getText().equals("Ar*"))
			{
				if(#args.getNumberOfChildren () != 2)
				{
					exceptionList.addSemanticError
						(Library.I18N, "lsy.wrong-arg-count", #id);
					#module = #([INVALID_EXPR]);
				}
				else
				{
					String key = #args.getFirstChild().getText();
					String value = (String) symbolTableForLocalRegisters.get(key);
					if(value == null) {
						exceptionList.addSemanticError
							(Library.I18N, "lsy.unknown-localregister", #id);
						#module = #([INVALID_EXPR]);
					} else {
						#args.getFirstChild().setText(value);
						#args.getFirstChild().setType(INT_LITERAL);
					}
				}
			}
			else
			{
				translate (#id, #args.getNumberOfChildren ());
				String s = #id.getText ();
				if (s.equals ("Plus") || s.equals ("Minus"))
				{
					#args.addChild (#[IDENT, "angle"]);
				}
				if (knownModules.add (s))
				{
					AST p = #[PARAMETERS];
					for (int i = #args.getNumberOfChildren (); i > 0; i--)
					{
						String pid = "p" + i;
						p.addChild (#([PARAMETER_DEF], [MODIFIERS],
									  [FLOAT_], [IDENT, pid]));
					}
					p = #([MODULE], [MODIFIERS], [IDENT, s], [EXTENDS], p);
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
				#module = #([NODE], #([NEW], module), [EMPTY]);
			}
		}
	;


moduleIdent returns [boolean isJRegIdent]
	{ isJRegIdent = false; }
	:   (   IDENT
		|   JREG_IDENT { isJRegIdent = true; }
		|   ADD
		|   SUB
		|   MUL
		|	DIV
		)
		{ #moduleIdent.setType (IDENT); }
	;


moduleArgList
	:   (	(number) => number
		|	LPAREN! expression (COMMA! expression)* RPAREN!
		|
		)
		{ #moduleArgList = #([ARGLIST], moduleArgList); }
	;


// the mother of all expressions
expression
	:   logicalOrExpression
	;


// logical or (||)  (level 11)
logicalOrExpression
	:	logicalAndExpression (COR^ logicalAndExpression)*
	;


// logical and (&&)  (level 10)
logicalAndExpression
	:   equalityExpression (CAND^ equalityExpression)*
	;


// equality/inequality (==/!=) (level 6)
equalityExpression
	:	relationalExpression ((NOT_EQUALS^ | EQUALS^) relationalExpression)*
	;


// boolean relational expressions (level 5)
relationalExpression
	:   additiveExpression ((LT^ | GT^ | LE^ | GE^) additiveExpression)*
	;


// binary addition/subtraction (level 3)
additiveExpression
	:   multiplicativeExpression ((ADD^ | SUB^) multiplicativeExpression)*
	;


// multiplication/division/modulo (level 2)
multiplicativeExpression
	:	powerExpression ((MUL^ | DIV^ | REM^ ) powerExpression)*
		{
			AST t = #multiplicativeExpression;
			if (t.getType () == DIV)
			{
				t = t.getFirstChild ();
				t.setNextSibling (#([TYPECAST], [FLOAT_],
									t.getNextSibling ()));
			}
		}
	;


// exponentiation (level 1), right associative
powerExpression
	:   unaryExpression (POW^ powerExpression)?
	;


unaryExpression
	:	SUB^ {#unaryExpression.setType (NEG);} unaryExpression
	|	ADD^  {#unaryExpression.setType (POS);} unaryExpression
	|	NOT^ unaryExpression
	|   primaryExpression
	;


primaryExpression
	:!  id:IDENT (a:argList)?
		{
			if ((declaredMethods.contains (#id.getText ()))
				&& (#a == null))
			{
				#a = #[ARGLIST];
			}
			if (#a != null)
			{
				#primaryExpression = #([METHOD_CALL], id, a);
			}
			else
			{
				#primaryExpression = #id;
			}
		}
	|   i:"if"^ LPAREN! expression COMMA! expression COMMA! expression RPAREN!
		{ #i.setType (QUESTION); }
	|	so:sumOp! sumRest[#so]
	|   LPAREN! expression RPAREN!
	|   INT_LITERAL
	|   FLOAT_LITERAL
	;
	

sumOp
	:	"sum" | "sumd" | "sump"
	;

	
sumRest![AST op]
	:	LPAREN e1:expression (COMMA e2:expression)? RPAREN
	{
		op.setType (IDENT);
		op.setText (op.getText () + "Generator");
		AST gen = #([METHOD_CALL], op, [ARGLIST]);
		if (#e2 != null)
		{
			gen = #([GUARD], gen, #e1);
			#e1 = #e2;
		}
		#sumRest = #([METHOD_CALL], getOperator ("sum"),
			#([ARGLIST], #([ELIST], gen, #e1)));
	}
	;


argList
	:   lp:LPAREN^ expression (COMMA! expression)* RPAREN!
		{ #lp.setType (ARGLIST); }
	;


number
	{ boolean minus = false; } 
	:   (SUB! { minus = true; })?
	(   INT_LITERAL
	|   FLOAT_LITERAL
	)
	{
		if (minus)
		{
			Token t = ((de.grogra.grammar.ASTWithToken) #number).token;
			String s = '-' + t.getText ();
			t.setText (s);
			#number.setText (s);
		}
	}
	;
