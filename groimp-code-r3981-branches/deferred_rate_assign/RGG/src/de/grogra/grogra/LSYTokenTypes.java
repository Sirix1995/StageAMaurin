// $ANTLR 2.7.7 (2006-11-01): "LSY.g" -> "LSYParser.java"$


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

public interface LSYTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int BOOLEAN_LITERAL = 4;
	int INT_LITERAL = 5;
	int LONG_LITERAL = 6;
	int FLOAT_LITERAL = 7;
	int DOUBLE_LITERAL = 8;
	int CHAR_LITERAL = 9;
	int STRING_LITERAL = 10;
	int IDENT = 11;
	int COMPILATION_UNIT = 12;
	int PACKAGE = 13;
	int MODULE = 14;
	int CLASS = 15;
	int INTERFACE = 16;
	int IMPORT_ON_DEMAND = 17;
	int STATIC_IMPORT_ON_DEMAND = 18;
	int SINGLE_TYPE_IMPORT = 19;
	int SINGLE_STATIC_IMPORT = 20;
	int EXTENDS = 21;
	int IMPLEMENTS = 22;
	int PARAMETERS = 23;
	int PARAMETER_DEF = 24;
	int SUPER = 25;
	int ARGLIST = 26;
	int SLIST = 27;
	int INSTANTIATOR = 28;
	int METHOD = 29;
	int THROWS = 30;
	int SEMI = 31;
	int CONSTRUCTOR = 32;
	int VARIABLE_DEF = 33;
	int ASSIGN = 34;
	int ARRAY_DECLARATOR = 35;
	int DECLARING_TYPE = 36;
	int VOID_ = 37;
	int BOOLEAN_ = 38;
	int BYTE_ = 39;
	int SHORT_ = 40;
	int CHAR_ = 41;
	int INT_ = 42;
	int LONG_ = 43;
	int FLOAT_ = 44;
	int DOUBLE_ = 45;
	int INSTANCE_INIT = 46;
	int STATIC_INIT = 47;
	int EMPTY = 48;
	int QUERY = 49;
	int COMPOUND_PATTERN = 50;
	int LABEL = 51;
	int PATTERN_WITH_BLOCK = 52;
	int MINIMAL = 53;
	int LATE_MATCH = 54;
	int SINGLE_MATCH = 55;
	int OPTIONAL_MATCH = 56;
	int SINGLE_OPTIONAL_MATCH = 57;
	int ANY = 58;
	int FOLDING = 59;
	int SEPARATE = 60;
	int LT = 61;
	int GT = 62;
	int LINE = 63;
	int LEFT_RIGHT_ARROW = 64;
	int PLUS_LEFT_ARROW = 65;
	int PLUS_ARROW = 66;
	int PLUS_LINE = 67;
	int PLUS_LEFT_RIGHT_ARROW = 68;
	int SLASH_LEFT_ARROW = 69;
	int SLASH_ARROW = 70;
	int SLASH_LINE = 71;
	int SLASH_LEFT_RIGHT_ARROW = 72;
	int TYPE_PATTERN = 73;
	int WRAPPED_TYPE_PATTERN = 74;
	int NAME_PATTERN = 75;
	int TREE = 76;
	int CONTEXT = 77;
	int EXPR = 78;
	int ROOT = 79;
	int METHOD_PATTERN = 80;
	int METHOD_CALL = 81;
	int DOT = 82;
	int APPLICATION_CONDITION = 83;
	int PARAMETERIZED_PATTERN = 84;
	int SUB = 85;
	int LEFT_ARROW = 86;
	int ARROW = 87;
	int X_LEFT_RIGHT_ARROW = 88;
	int TRAVERSAL = 89;
	int QUESTION = 90;
	int MUL = 91;
	int ADD = 92;
	int RANGE_EXACTLY = 93;
	int RANGE_MIN = 94;
	int RANGE = 95;
	int RULE = 96;
	int DOUBLE_ARROW_RULE = 97;
	int EXEC_RULE = 98;
	int PRODUCE = 99;
	int WITH = 100;
	int UNARY_PREFIX = 101;
	int TYPECAST = 102;
	int TYPECHECK = 103;
	int COM = 104;
	int NOT = 105;
	int NEG = 106;
	int POS = 107;
	int DIV = 108;
	int REM = 109;
	int POW = 110;
	int SHL = 111;
	int SHR = 112;
	int USHR = 113;
	int LE = 114;
	int GE = 115;
	int CMP = 116;
	int NOT_EQUALS = 117;
	int EQUALS = 118;
	int OR = 119;
	int XOR = 120;
	int AND = 121;
	int COR = 122;
	int CAND = 123;
	int ARRAY_INIT = 124;
	int RULE_BLOCK = 125;
	int ELIST = 126;
	int SHELL_BLOCK = 127;
	int THIS = 128;
	int QUALIFIED_SUPER = 129;
	int IF = 130;
	int RETURN = 131;
	int YIELD = 132;
	int THROW = 133;
	int SYNCHRONIZED_ = 134;
	int ASSERT = 135;
	int LABELED_STATEMENT = 136;
	int BREAK = 137;
	int CONTINUE = 138;
	int TRY = 139;
	int CATCH = 140;
	int FINALLY = 141;
	int NODES = 142;
	int NODE = 143;
	int FOR = 144;
	int ENHANCED_FOR = 145;
	int WHILE = 146;
	int DO = 147;
	int SWITCH = 148;
	int SWITCH_GROUP = 149;
	int CASE = 150;
	int DEFAULT = 151;
	int NULL_LITERAL = 152;
	int INVALID_EXPR = 153;
	int LONG_LEFT_ARROW = 154;
	int LONG_ARROW = 155;
	int LONG_LEFT_RIGHT_ARROW = 156;
	int INSTANCEOF = 157;
	int CLASS_LITERAL = 158;
	int QUOTE = 159;
	int ADD_ASSIGN = 160;
	int SUB_ASSIGN = 161;
	int MUL_ASSIGN = 162;
	int DIV_ASSIGN = 163;
	int REM_ASSIGN = 164;
	int POW_ASSIGN = 165;
	int SHR_ASSIGN = 166;
	int USHR_ASSIGN = 167;
	int SHL_ASSIGN = 168;
	int AND_ASSIGN = 169;
	int XOR_ASSIGN = 170;
	int OR_ASSIGN = 171;
	int DEFERRED_ASSIGN = 172;
	int DEFERRED_ADD = 173;
	int DEFERRED_SUB = 174;
	int DEFERRED_MUL = 175;
	int DEFERRED_DIV = 176;
	int DEFERRED_REM = 177;
	int DEFERRED_POW = 178;
	int DEFERRED_OR = 179;
	int DEFERRED_AND = 180;
	int DEFERRED_XOR = 181;
	int DEFERRED_SHL = 182;
	int DEFERRED_SHR = 183;
	int DEFERRED_USHR = 184;
	int INC = 185;
	int DEC = 186;
	int POST_INC = 187;
	int POST_DEC = 188;
	int IN = 189;
	int GUARD = 190;
	int ARRAY_ITERATOR = 191;
	int QUERY_EXPR = 192;
	int INVOKE_OP = 193;
	int QUALIFIED_NEW = 194;
	int INDEX_OP = 195;
	int NEW = 196;
	int DIMLIST = 197;
	int MODIFIERS = 198;
	int ANNOTATION = 199;
	int PRIVATE_ = 200;
	int PUBLIC_ = 201;
	int PROTECTED_ = 202;
	int STATIC_ = 203;
	int TRANSIENT_ = 204;
	int FINAL_ = 205;
	int ABSTRACT_ = 206;
	int NATIVE_ = 207;
	int VOLATILE_ = 208;
	int STRICT_ = 209;
	int ITERATING_ = 210;
	int CONST_ = 211;
	int VARARGS_ = 212;
	int STATIC_MEMBER_CLASSES = 213;
	int MARKER = 214;
	int SINGLE_ELEMENT = 215;
	int NORMAL = 216;
	int LPAREN = 217;
	int RPAREN = 218;
	int LBRACK = 219;
	int RBRACK = 220;
	int COLON = 221;
	int COMMA = 222;
	int JREG_IDENT = 223;
	// "\\set" = 224
	// "\\length" = 225
	// "\\laenge" = 226
	// "\\angle" = 227
	// "\\winkel" = 228
	// "\\const" = 229
	// "\\register" = 230
	// "\\var" = 231
	int LITERAL_uniform = 232;
	int LITERAL_normal = 233;
	int LITERAL_binomial = 234;
	int LITERAL_negbinomial = 235;
	int LITERAL_poisson = 236;
	int LITERAL_distribution = 237;
	int LITERAL_table = 238;
	int LITERAL_generation = 239;
	int LITERAL_register = 240;
	int LITERAL_local = 241;
	int LITERAL_index = 242;
	int LITERAL_length = 243;
	int LITERAL_diameter = 244;
	int LITERAL_n_value = 245;
	int LITERAL_v_value = 246;
	int LITERAL_color = 247;
	int LITERAL_order = 248;
	int LITERAL_carbon = 249;
	int LITERAL_q_value = 250;
	int LITERAL_xcoordinate = 251;
	int LITERAL_ycoordinate = 252;
	int LITERAL_zcoordinate = 253;
	int LITERAL_function = 254;
	// "#" = 255
	// "##" = 256
	// "?" = 257
	// "&" = 258
	int LITERAL_if = 259;
	int LITERAL_sum = 260;
	int LITERAL_sumd = 261;
	int LITERAL_sump = 262;
}