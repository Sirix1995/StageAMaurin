// $ANTLR 2.7.7 (2006-11-01): "vocabulary.g" -> "TokenizerBuilder.java"$


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

public class TokenizerBuilder extends antlr.LLkParser       implements TokenizerBuilderTokenTypes
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


protected TokenizerBuilder(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public TokenizerBuilder(TokenBuffer tokenBuf) {
  this(tokenBuf,3);
}

protected TokenizerBuilder(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public TokenizerBuilder(TokenStream lexer) {
  this(lexer,3);
}

public TokenizerBuilder(ParserSharedInputState state) {
  super(state,3);
  tokenNames = _tokenNames;
}

	public final void file() throws RecognitionException, TokenStreamException {
		
		Token  name = null;
		
		try {      // for error handling
			name = LT(1);
			match(ID);
			{
			_loop3:
			do {
				if ((LA(1)==ID||LA(1)==STRING)) {
					line();
				}
				else {
					break _loop3;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
	}
	
	public final void line() throws RecognitionException, TokenStreamException {
		
		Token  s1 = null;
		Token  lab = null;
		Token  s2 = null;
		Token  id = null;
		Token  para = null;
		Token  id2 = null;
		Token  i = null;
		Token t=null; Token s=null;
		
		try {      // for error handling
			{
			if ((LA(1)==STRING)) {
				s1 = LT(1);
				match(STRING);
				s = s1;
			}
			else if ((LA(1)==ID) && (LA(2)==ASSIGN) && (LA(3)==STRING)) {
				lab = LT(1);
				match(ID);
				t = lab;
				match(ASSIGN);
				s2 = LT(1);
				match(STRING);
				s = s2;
			}
			else if ((LA(1)==ID) && (LA(2)==LPAREN)) {
				id = LT(1);
				match(ID);
				t=id;
				match(LPAREN);
				para = LT(1);
				match(STRING);
				match(RPAREN);
			}
			else if ((LA(1)==ID) && (LA(2)==ASSIGN) && (LA(3)==INT)) {
				id2 = LT(1);
				match(ID);
				t=id2;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(ASSIGN);
			i = LT(1);
			match(INT);
			
			define (t, (s == null) ? null : s.getText (),
			(para == null) ? null : para.getText (),
								Integer.parseInt (i.getText ()));
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"ID",
		"STRING",
		"ASSIGN",
		"LPAREN",
		"RPAREN",
		"INT",
		"WS",
		"SL_COMMENT",
		"ML_COMMENT",
		"ESC",
		"DIGIT",
		"XDIGIT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 50L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	
	}
