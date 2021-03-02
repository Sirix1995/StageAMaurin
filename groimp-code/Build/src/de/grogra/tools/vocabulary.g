/* This file was created using the file tokdef.g of
 * the ANTLR source distribution as a basis.
 */

header
{

/*
 * Copyright (C) 2002 - 2005 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.grogra.tools;
}

class TokenizerBuilder extends Parser;

options
{
	k = 3;
}


{
	java.io.PrintWriter out;


    public static void main (String[] args) throws ANTLRException
    {
        VocabularyLexer l = new VocabularyLexer (System.in);
        new TokenizerBuilder (l).parse
        	(new java.io.PrintWriter (System.out));
    }


	public void parse (java.io.PrintWriter out) throws ANTLRException
	{
		this.out = out;
		file ();
	}


    void define (Token t, String label, String paraphrase, int type)
    {
        if (label != null)
        {
        	StringBuffer buf = new StringBuffer (label);
        	for (int i = buf.length () - 1; i >= 0; i--)
        	{
        		if (buf.charAt (i) == '#')
        		{
        			buf.insert (i, "$!{_grogra_de_undefined}");
        		}
        	}
        	label = buf.toString ();
            out.println ("\t$!{INSTANCE}addToken ("
						 + ((t == null) ? "" + type
                                        : "$!{INTERFACE}" + t.getText ())
						 + ", " + label + ");");
        }
    }

}

file :
	name:ID
	(line)*;

line
{ Token t=null; Token s=null; }
	:	(	s1:STRING {s = s1;}
		|	lab:ID {t = lab;} ASSIGN s2:STRING {s = s2;}
		|	id:ID {t=id;} LPAREN para:STRING RPAREN
		|	id2:ID {t=id2;}
		)
		ASSIGN
		i:INT
		{
            define (t, (s == null) ? null : s.getText (),
                    (para == null) ? null : para.getText (),
					Integer.parseInt (i.getText ()));
        }
	;

class VocabularyLexer extends Lexer;

options
{
	k = 2;
	testLiterals = false;
	charVocabulary = '\003'..'\377';
}

WS	:	(	' '
		|	'\t'
		|	'\r' ('\n')?	{newline();}
		|	'\n'		{newline();}
		)
		{ _ttype = Token.SKIP; }
	;

SL_COMMENT :
	"//"
	(~('\n'|'\r'))* ('\n'|'\r'('\n')?)
	{ _ttype = Token.SKIP; newline(); }
	;

ML_COMMENT :
   "/*"
   (
			'\n' { newline(); }
		|	'*' ~'/'
		|	~'*'
	)*
	"*/"
	{ _ttype = Token.SKIP; }
	;

LPAREN : '(' ;
RPAREN : ')' ;

ASSIGN : '=' ;

STRING
	:	'"' (ESC|~'"')* '"'
	;

protected
ESC	:	'\\'
		(	'n'
		|	'r'
		|	't'
		|	'b'
		|	'f'
		|	'"'
		|	'\''
		|	'\\'
		|	('0'..'3') ( DIGIT (DIGIT)? )?
		|	('4'..'7') (DIGIT)?
		|	'u' XDIGIT XDIGIT XDIGIT XDIGIT
		)
	;

protected
DIGIT
	:	'0'..'9'
	;

protected
XDIGIT :
		'0' .. '9'
	|	'a' .. 'f'
	|	'A' .. 'F'
	;

ID :
	('a'..'z'|'A'..'Z')
	('a'..'z'|'A'..'Z'|'_'|'0'..'9')*
	;

INT : (DIGIT)+
	;
