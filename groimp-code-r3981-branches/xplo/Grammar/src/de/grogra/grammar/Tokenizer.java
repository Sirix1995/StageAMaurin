
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

import java.io.*;
import de.grogra.util.*;

public class Tokenizer implements antlr.TokenStream
{
	public static final I18NBundle I18N
		= I18NBundle.getInstance (Tokenizer.class);

	public static final int EOL_IS_SIGNIFICANT = 1;
	public static final int FLOAT_IS_DEFAULT = 2;
	public static final int MINUS_IS_SIGN = 4;
	public static final int CREATE_TOKEN_LOCATOR = 8;
	public static final int EVALUATE_NUMBERS = 16;
	public static final int ENABLE_TYPE_SUFFIX = 32;
	public static final int UNICODE_ESCAPES = 64;

	protected final boolean eolIsSignificant, floatIsDefault, minusIsSign,
		createTokenLocator, evaluateNumbers, enableTypeSuffix, unicode;

	private Input input;

	private int tokenStart;

	private static final int INTEGER = 0;
	private static final int HEX = 1;
	private static final int FLOAT = 2;

	private static final int BUFFER_SIZE = 32;

	private TokenInfo[] tokenBuffer;
	private int bufferHead, bufferIndex;


	private StringMap tokenTableA = new StringMap (), tokenTableB = new StringMap ();


	private static final class TokenEntry
	{
		final Token token;
		final String string;

		TokenEntry (Token token, String string)
		{
			this.token = token;
			this.string = string;
		}
	}


	private static final class TokenInfo
	{
		Token token;
		int start, end;

		void set (Token token, int start, int end)
		{
			this.token = token;
			this.start = start;
			this.end = end;
		}
	}


	public Tokenizer (int flags)
	{
		eolIsSignificant = (flags & EOL_IS_SIGNIFICANT) != 0;
		floatIsDefault = (flags & FLOAT_IS_DEFAULT) != 0;
		minusIsSign = (flags & MINUS_IS_SIGN) != 0;
		createTokenLocator = (flags & CREATE_TOKEN_LOCATOR) != 0;
		evaluateNumbers = (flags & EVALUATE_NUMBERS) != 0;
		enableTypeSuffix = (flags & ENABLE_TYPE_SUFFIX) != 0;
		unicode = (flags & UNICODE_ESCAPES) != 0;
		tokenBuffer = new TokenInfo[BUFFER_SIZE];
		for (int i = 0; i < BUFFER_SIZE; i++)
		{
			tokenBuffer[i] = new TokenInfo ();
		}
	}


	protected void copyTokenTablesFrom (Tokenizer source)
	{
		tokenTableA = source.tokenTableA;
		tokenTableB = source.tokenTableB;
	}


	public final void addToken (Token token)
	{
		addToken (token, token.getText (), true);
	}


	public final void addToken (Token token, boolean identifierPrecedes)
	{
		addToken (token, token.getText (), identifierPrecedes);
	}


	public final void addToken (int tokenId, String string)
	{
		addToken (new Token (tokenId, string), string, true);
	}


	public final void addToken (int tokenId, String string,
								boolean identifierPrecedes)
	{
		addToken (new Token (tokenId, string), string, identifierPrecedes);
	}


	public final void addToken (Token token, String string)
	{
		addToken (token, string, true);
	}


	public final void addToken (Token token, String string,
								boolean identifierPrecedes)
	{
		TokenEntry e = new TokenEntry (token, string);
		(identifierPrecedes ? tokenTableA : tokenTableB).put (string, e);
	}


	public final void setInput (Input input)
	{
		this.input = input;
		reset ();
	}


	public final void setSource (Reader source)
	{
		setSource (source, null);
	}


	public final void setSource (Reader source, String name)
	{
		if (!(source instanceof BufferedReader))
		{
			source = new BufferedReader (source);
		}
		Input in = new Input (!unicode);
		in.setSource (source, name);
		setInput (in);
	}

	
	public final Input getInput ()
	{
		return input;
	}


	public final void reset ()
	{
		bufferHead = 0;
		bufferIndex = 0;
		for (int i = 0; i < BUFFER_SIZE; i++)
		{
			tokenBuffer[i].set (null, 0, 0);
		}
	}


	private TokenEntry get (int type)
	{
		StringMap v = tokenTableA;
		while (true)
		{
			for (int i = 0; i < v.size (); i++)
			{
				if (((TokenEntry) v.getValueAt (i)).token.getType () == type)
				{
					return (TokenEntry) v.getValueAt (i);
				}
			}
			if (v == tokenTableB)
			{
				return null;
			}
			v = tokenTableB;
		}
	}

	
	private static final int CHAR_CATEGORY_LENGTH = 256;

	private static final boolean[] ID_START, ID_PART;
	
	static
	{
		ID_START = new boolean[CHAR_CATEGORY_LENGTH];
		ID_PART = new boolean[CHAR_CATEGORY_LENGTH];
		for (int i = 0; i < CHAR_CATEGORY_LENGTH; i++)
		{
			ID_START[i] = Character.isJavaIdentifierStart ((char) i);
			ID_PART[i] = Character.isJavaIdentifierPart ((char) i);
		}
	}


	protected boolean isWhitespace (char c)
	{
		switch (c)
		{
			case Input.EOL:
				return !eolIsSignificant;
			case ' ':
			case '\t':
			case '\f':
				return true;
			default:
				return false;
		}
	}


	protected boolean isIdentifierStart (char c)
	{
		return (c < CHAR_CATEGORY_LENGTH) ? ID_START[c]
			: Character.isJavaIdentifierStart (c);
	}


	protected boolean isIdentifierPart (char c)
	{
		return (c < CHAR_CATEGORY_LENGTH) ? ID_PART[c]
			: Character.isJavaIdentifierPart (c);
	}


	private Token searchToken (StringMap table)
		throws IOException, LexicalException
	{
		int p = input.getPosition (), reset = p;
		int index = -1, from = 0, to = table.size (), len = 0;

		while (from < to)
		{
			int c = input.getChar ();
			int i = table.findIndex (input, p, len + 1, from, to);
			if (i >= 0)
			{
				reset = p + len + 1;
				index = i;
				from = i + 1;
			}
			else
			{
				i = ~i;
				from = i;
			}
			for (i = from; i < to; i++)
			{
				if (table.getKeyAt (i).charAt (len) != c)
				{
					to = i;
					break;
				}
			}
			len++;
		}

		input.reset (reset);
		if (index < 0)
		{
			return null;
		}
		TokenEntry e = (TokenEntry) table.getValueAt (index);
		Token t = e.token;
		if (t.getType () == Token.SEQUENCE_START)
		{
			Sequence sequence
				= ((SequenceStart) t).createSequence (e.string);
			t = sequence.getToken (input);
			return (t == null) ? getTokenImpl () : t;
		}
		return t;
	}


	private Token dupIfNecessary (Token t)
	{
		if (createTokenLocator)
		{
			return t.dup ();
		}
		else
		{
			return t;
		}
	}


	private Token getTokenImpl () throws IOException, LexicalException
	{
		int c;
		Token t;

		tokenStart = input.getPosition ();

		if (input.isClosed ())
		{
			return new Token (Token.EOF_TYPE, "");
		}
		do
		{
			c = input.getChar ();
		} while ((c != Input.EOF) && isWhitespace ((char) c));
		input.ungetChar ();

		if (c == Input.EOF)
		{
			return new Token (Token.EOF_TYPE, "");
		}

		tokenStart = input.getPosition ();

		t = searchToken (tokenTableB);
		if (t != null)
		{
			return dupIfNecessary (t);
		}

		if (isIdentifierStart ((char) c))
		{
			input.getChar ();
			StringBuffer b = input.getTmpBuffer ();
			b.append ((char) c);
			while (((c = input.getChar ()) != Input.EOF)
				   && isIdentifierPart ((char) c))
			{
				b.append ((char) c);
			}
			input.ungetChar ();
			int i = tokenTableA.findIndex
				(input, tokenStart, input.getPosition () - tokenStart,
				 0, tokenTableA.size ());
			if (i >= 0)
			{
				return dupIfNecessary
					(((TokenEntry) tokenTableA.getValueAt (i)).token);
			}
			else
			{
				return new Identifier (input.substring (tokenStart));
			}
		}

		t = checkForNumberLiteral ();
		if (t != null)
		{
			return t;
		}
		input.reset (tokenStart);

		t = searchToken (tokenTableA);
		if (t != null)
		{
			return dupIfNecessary (t);
		}
		throw new LexicalException (I18N.msg ("grammar.illegal-token"));
	}


	private Literal checkForNumberLiteral ()
		throws LexicalException, IOException
	{
		int p = input.getPosition (), type, r;
		int c = input.getChar (), c2;
		boolean minus;
		String s;

		minus = minusIsSign && (c == '-');
		if (minus)
		{
			c = input.getChar ();
		}
		if (c == Input.EOL)
		{
			return null;
		}
		c2 = input.getChar ();
		if (!(Character.isDigit ((char) c) || ((c == '.') && Character.isDigit ((char) c2))))
		{
			return null;
		}

		if ((c == '0') && !minus && ((c2 == 'x') || (c2 == 'X')))
		{
			type = HEX;
			p = input.getPosition ();
			while (Character.digit ((char) input.getChar (), 16) >= 0)
			{
			}
			input.ungetChar ();
		}
		else
		{
			type = INTEGER;
			input.ungetChar ();
			input.ungetChar ();
			boolean exponent = false, dotFound = false, needDigit = false;

		findSpan:
			while (true)
			{
				c = input.getChar ();
				switch (c)
				{
					case 'E':
					case 'e':
						type = FLOAT;
						exponent = true;
						needDigit = true;
						continue findSpan;
					case '-':
					case '+':
						if (!exponent)
						{
							break findSpan;
						}
						break;
					case '.':
						if (dotFound)
						{
							break findSpan;
						}
						dotFound = true;
						c = input.getChar ();
						input.ungetChar ();
						if (c == '.')
						{
							break findSpan;
						}
						type = FLOAT;
						break;
					default:
						if (!Character.isDigit ((char) c))
						{
							break findSpan;
						}
						needDigit = false;
						break;
				}
				exponent = false;
			}
			input.ungetChar ();
			
			if (needDigit)
			{
				throw new LexicalException
					(I18N.msg ("grammar.invalid-number-format"));
			}
		}

		s = input.substring (p);
		try
		{
			if (enableTypeSuffix)
			{
				switch (input.getChar ())
				{
					case 'f':
					case 'F':
						return new FloatLiteral (s);
					case 'd':
					case 'D':
						return new DoubleLiteral (s);
					case 'l':
					case 'L':
						switch (type)
						{
							case HEX:
								r = 16;
								break;
							case FLOAT:
								throw new LexicalException
									(I18N.msg
									 ("grammar.lsuffix-for-float"));
							default:
								r = ((s.length () > 1)
									 && (Character.digit (s.charAt (0), 10)
										 == 0))
									? 8 : 10;
						}
						return evaluateNumbers
							? new LongLiteral (Long.parseLong (s, r))
							: new LongLiteral (s, r);
				}
				input.ungetChar ();
			}
			switch (type)
			{
				case HEX:
					r = 16;
					break;
				case FLOAT:
					if (floatIsDefault)
					{
						return new FloatLiteral (s);
					}
					else
					{
						return new DoubleLiteral (s);
					}
				default:
					r = ((s.length () > 1)
						 && (Character.digit (s.charAt (0), 10) == 0))
						? 8 : 10;
			}
			return evaluateNumbers
				? new IntLiteral (Integer.parseInt (s, r))
				: new IntLiteral (s, r);
		}
		catch (NumberFormatException e)
		{
			throw new LexicalException
				(I18N.msg ("grammar.invalid-number-format"));
		}
	}


	protected Token convert (Token t)
	{
		return t;
	}


	public final Token getToken () throws IOException, LexicalException
	{
		Token t;
		if (bufferIndex == bufferHead)
		{
			try
			{
				t = convert (getTokenImpl ());
				if (createTokenLocator)
				{
					t.setTokenizer (this);
					t.setExtent (tokenStart, input.getPosition ());
				}
				tokenBuffer[bufferHead++ & (BUFFER_SIZE - 1)].set
					(t, tokenStart, input.getPosition ());
			}
			catch (LexicalException e)
			{
				complete (e);
				throw e;
			}
		}
		else
		{	
			t = tokenBuffer[bufferIndex & (BUFFER_SIZE - 1)].token;
		}
		bufferIndex++;
		return t;
	}


	public antlr.Token nextToken () throws antlr.TokenStreamException
	{
		try
		{
			return getToken ();
		}
		catch (LexicalException e)
		{
			throw new antlr.TokenStreamRecognitionException (e);
		}
		catch (IOException e)
		{
			throw new antlr.TokenStreamIOException (e);
		}
	}


	public final void ungetToken ()
	{
		if (bufferIndex == 0)
		{
			throw new AssertionError
				("ungetToken without invocation of getToken.");
		}
		else if (bufferIndex + BUFFER_SIZE == bufferHead)
		{
			throw new AssertionError
				("ungetToken: Insufficient buffer capacity.");
		}
		bufferIndex--;
	}


	public final NumberLiteral getNumberToken (int allowedLiterals)
		throws IOException, LexicalException
	{
		Token t = getToken ();
		int type = t.getType ();
		if ((type > Token.MAX_NUMBER_LITERAL)
			|| ((1 << type) & allowedLiterals) == 0)
		{
			StringBuffer b = null;
			String last = null;
			for (type = Token.MIN_NUMBER_LITERAL;
				 type <= Token.MAX_NUMBER_LITERAL; type++)
			{
				if (((1 << type) & allowedLiterals) != 0)
				{
					if (last != null)
					{
						if (b == null)
						{
							b = new StringBuffer (last);
						}
						else
						{
							b.append (", ").append (last);
						}
					}
					switch (type)
					{
						case Token.INT_LITERAL:
							last = "int";
							break;
						case Token.LONG_LITERAL:
							last = "long";
							break;
						case Token.FLOAT_LITERAL:
							last = "float";
							break;
						case Token.DOUBLE_LITERAL:
							last = "double";
							break;
						default:
							throw new AssertionError (type);
					}
				}
			}
			LexicalException e = new LexicalException
				((b == null)
				 ? I18N.msg ("grammar.number-expected1", last)
				 : I18N.msg ("grammar.number-expected", b.toString (), last));
			complete (e);
			throw e;
		}
		return (NumberLiteral) t;
	}


	public final int getInt () throws IOException, LexicalException
	{
		return getNumberToken (NumberLiteral.INT).intValue ();
	}


	public final float getFloat () throws IOException, LexicalException
	{
		return getNumberToken (NumberLiteral.INT | NumberLiteral.LONG
							   | NumberLiteral.FLOAT)
			.floatValue ();
	}


	public final void consume (int type) throws IOException,
		LexicalException, UnexpectedTokenException
	{
		Token t = getToken ();
		if (t.getType () != type)
		{
			UnexpectedTokenException e = new UnexpectedTokenException
				(getTokenString (), get (type).token.getText ());
			complete (e);
			throw e;
		}
	}


	void complete (RecognitionException e)
	{
		e.complete (input, tokenStart, input.getPosition ());
	}


	void set (RecognitionException e)
	{
		e.set (input, tokenStart, input.getPosition ());
	}


	private String getTokenString ()
	{
		TokenInfo info = tokenBuffer[(bufferIndex - 1) & (BUFFER_SIZE - 1)];
		return input.substring (info.start, info.end);
	}

}
