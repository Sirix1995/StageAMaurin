
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

import de.grogra.grammar.*;

public final class LSYTokenizer extends Tokenizer implements LSYTokenTypes
{

	@Override
	protected boolean isIdentifierPart (char c)
	{
		return super.isIdentifierPart (c) || (c == '\'');
	}


	private void addToken (String s)
	{
		addToken (IDENT, s, false);
		addToken (IDENT, s + "+", false);
		addToken (IDENT, s + "*", false);
		addToken (IDENT, s + "l", false);
		addToken (IDENT, s + "l+", false);
		addToken (IDENT, s + "l*", false);
	}


	public LSYTokenizer ()
	{
		super (CREATE_TOKEN_LOCATOR | FLOAT_IS_DEFAULT);
		addToken (new StartEndCommentStart ("/*", "*/"));
		addToken (new SingleLineCommentStart ());
		addToken (new StringStart ());
		for (int i = 0; i < 10; i++)
		{
			String s = "J" + i;
			addToken (JREG_IDENT, s + '=', false);
			addToken (JREG_IDENT, s + "*=", false);
			addToken (JREG_IDENT, s + "+=", false);
		}
		addToken (IDENT, "F", false);
		addToken (IDENT, "F+", false);
		addToken (IDENT, "F*", false);
		addToken (IDENT, "f", false);
		addToken (IDENT, "f+", false);
		addToken (IDENT, "f*", false);
		addToken (IDENT, "@", false);
		addToken (IDENT, "RH", false);
		addToken (IDENT, "RL", false);
		addToken (IDENT, "RU", false);
		addToken (IDENT, "RV", false);
		addToken (IDENT, "RV+", false);
		addToken (IDENT, "RV*", false);
		addToken (IDENT, "P", false);
		addToken (IDENT, "Pl", false);
		addToken (IDENT, "Light", false);
		addToken ("L");
		addToken ("D");
		addToken ("V");
		addToken ("N");
		addToken (LITERAL_function, "function", false);
		addToken (EQUALS, "=");
/*!!
#parse ("de/grogra/grogra/LSYTokenizer.inc")
!!*/
//!! #* Start of generated code
	addToken (LT, "<");
	addToken (GT, ">");
	addToken (SUB, "-");
	addToken (MUL, "*");
	addToken (ADD, "+");
	addToken (NOT, "!");
	addToken (DIV, "/");
	addToken (REM, "%");
	addToken (POW, "^");
	addToken (LE, "<=");
	addToken (GE, ">=");
	addToken (NOT_EQUALS, "!=");
	addToken (EQUALS, "==");
	addToken (COR, "||");
	addToken (CAND, "&&");
	addToken (LPAREN, "(");
	addToken (RPAREN, ")");
	addToken (LBRACK, "[");
	addToken (RBRACK, "]");
	addToken (COLON, ":");
	addToken (COMMA, ",");
	addToken (228, "\\set");
	addToken (229, "\\length");
	addToken (230, "\\laenge");
	addToken (231, "\\angle");
	addToken (232, "\\winkel");
	addToken (233, "\\const");
	addToken (234, "\\register");
	addToken (235, "\\var");
	addToken (LITERAL_uniform, "uniform");
	addToken (LITERAL_normal, "normal");
	addToken (LITERAL_binomial, "binomial");
	addToken (LITERAL_negbinomial, "negbinomial");
	addToken (LITERAL_poisson, "poisson");
	addToken (LITERAL_distribution, "distribution");
	addToken (LITERAL_table, "table");
	addToken (LITERAL_generation, "generation");
	addToken (LITERAL_register, "register");
	addToken (LITERAL_local, "local");
	addToken (LITERAL_index, "index");
	addToken (LITERAL_length, "length");
	addToken (LITERAL_diameter, "diameter");
	addToken (LITERAL_n_value, "n_value");
	addToken (LITERAL_v_value, "v_value");
	addToken (LITERAL_color, "color");
	addToken (LITERAL_order, "order");
	addToken (LITERAL_carbon, "carbon");
	addToken (LITERAL_q_value, "q_value");
	addToken (LITERAL_xcoordinate, "xcoordinate");
	addToken (LITERAL_ycoordinate, "ycoordinate");
	addToken (LITERAL_zcoordinate, "zcoordinate");
	addToken (LITERAL_function, "function");
	addToken (259, "#");
	addToken (260, "##");
	addToken (261, "?");
	addToken (262, "&");
	addToken (LITERAL_if, "if");
	addToken (LITERAL_sum, "sum");
	addToken (LITERAL_sumd, "sumd");
	addToken (LITERAL_sump, "sump");
//!! *# End of generated code
	}

	@Override
	protected Token convert (Token t)
	{
		if (t.getType () == IDENT)
		{
			String s = t.getText ();
			StringBuffer b = null;
			for (int i = s.length () - 1; i >= 0; i--)
			{
				if (s.charAt (i) == '\'')
				{
					if (b == null)
					{
						b = new StringBuffer (s);
					}
					b.replace (i, i + 1, '$' + Integer.toHexString (s.charAt (i)) + '_');  
				}
			}
			if (b != null)
			{
				t.setText (b.toString ());
			}
		}
		return t;
	}

}
