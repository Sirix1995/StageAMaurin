package de.grogra.grogra;

import java.io.IOException;

import javax.vecmath.Tuple3f;

import de.grogra.grammar.LexicalException;
import de.grogra.grammar.Token;
import de.grogra.grammar.Tokenizer;

public final class DTDTokenizer extends Tokenizer {
	static final int N = Token.MIN_UNUSED;

	static final int S = Token.MIN_UNUSED + 1;

	static final int COMM = Token.MIN_UNUSED + 2; // for "<,{"

	static final int DTDINDENT = Token.MIN_UNUSED + 3; // for "}"

	static final int SHAPE = Token.MIN_UNUSED + 4; // for "#"

	static final int LAUBPARA = Token.MIN_UNUSED + 5;
	
	DTDTokenizer() {
		super(EOL_IS_SIGNIFICANT | FLOAT_IS_DEFAULT | MINUS_IS_SIGN | EVALUATE_NUMBERS | CREATE_TOKEN_LOCATOR);
		//super(EOL_IS_SIGNIFICANT | FLOAT_IS_DEFAULT | MINUS_IS_SIGN | CREATE_TOKEN_LOCATOR);
		addToken(COMM, "<", false);
		addToken(COMM, ">", false);
		addToken(COMM, "{", false);
		addToken(COMM, "}", false);
		addToken(DTDINDENT, "L", false);
		addToken(DTDINDENT, "l", false);
		addToken(DTDINDENT, "Q", false);
		addToken(DTDINDENT, "q", false);
		addToken(DTDINDENT, "A", false);
		addToken(DTDINDENT, "a", false);
		addToken(DTDINDENT, "O", false);
		addToken(DTDINDENT, ":", false);
		addToken(DTDINDENT, "\\", false);
		addToken(DTDINDENT, "o", false);
		addToken(DTDINDENT, "G", false);
		addToken(DTDINDENT, "g", false);
		addToken(DTDINDENT, "J", false);
		addToken(DTDINDENT, "j", false);
		addToken(DTDINDENT, "T", false);
		addToken(DTDINDENT, "t", false);
		addToken(DTDINDENT, "R", false);
		addToken(DTDINDENT, "r", false);
		addToken(DTDINDENT, "S", false);
		addToken(DTDINDENT, "s", false);
		addToken(DTDINDENT, "E", false);
		addToken(DTDINDENT, "e", false);
		addToken(DTDINDENT, "I", false);
		addToken(DTDINDENT, "i", false);
		addToken(DTDINDENT, "N", false);
		addToken(DTDINDENT, "n", false);
		addToken(DTDINDENT, "D", false);
		addToken(DTDINDENT, "d", false);
		addToken(DTDINDENT, "C", false);
		addToken(DTDINDENT, "c", false);
		addToken(DTDINDENT, "B", false);
		addToken(DTDINDENT, "b", false);
		addToken(DTDINDENT, "F", false);
		addToken(DTDINDENT, "f", false);
		addToken(DTDINDENT, "M", false);
		addToken(DTDINDENT, "m", false);
		addToken(DTDINDENT, "P", false);
		addToken(DTDINDENT, "p", false);
		addToken(DTDINDENT, "X", false);
		addToken(DTDINDENT, "x", false);
		addToken(DTDINDENT, "V", false);
		addToken(DTDINDENT, "v", false);
		addToken(DTDINDENT, "K", false);
		addToken(DTDINDENT, "k", false);
		addToken(DTDINDENT, "+", false);
		addToken(DTDINDENT, "-", false);
		addToken(DTDINDENT, "S", false);
		addToken(DTDINDENT, "s", false);
		addToken(DTDINDENT, "W", false);
		addToken(DTDINDENT, "w", false);
		addToken(DTDINDENT, "(", false);
		addToken(DTDINDENT, ")", false);
		addToken(DTDINDENT, "?", false);
		addToken(DTDINDENT, ";", false);
		addToken(DTDINDENT, ".", false);
		addToken(DTDINDENT, ",", false);
		addToken(DTDINDENT, "'", false);
		addToken(DTDINDENT, "/", false);
		addToken(LAUBPARA, "\\leafarea", false);
		addToken(LAUBPARA, "\\leaflength", false);
		addToken(LAUBPARA, "\\leafbreadth", false);
		addToken(LAUBPARA, "\\leafobject", false);
		addToken(LAUBPARA, "\\phyllotaxy", false);
		addToken(LAUBPARA, ",", false);
		addToken(LAUBPARA, "\\min_intn", false);
		addToken(SHAPE, "#", false);
	}

	void getTuple(Tuple3f tuple) throws IOException, LexicalException {
		tuple.set(getFloat(), getFloat(), getFloat());
	}

	void consumeFloats(int n) throws IOException, LexicalException {
		for (int i = 0; i < n; i++) {
			getFloat();
		}
	}

	protected boolean isWhitespace(char c) {
		return (c < 32) || super.isWhitespace(c);
	}

	protected boolean isIdentifierStart(char c) {
		return !isWhitespace(c) && super.isIdentifierStart(c);
	}

	protected boolean isIdentifierPart(char c) {
		return !isWhitespace(c) && super.isIdentifierPart(c);
	}
}
