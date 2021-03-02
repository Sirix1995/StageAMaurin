// $ANTLR 2.7.7 (2006-11-01): "Compiler.tree.g" -> "Compiler.java"$


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

package de.grogra.xl.compiler;

import java.util.List;
import java.lang.reflect.Array;

import de.grogra.util.*;
import de.grogra.xl.expr.*;
import de.grogra.xl.query.*;
import de.grogra.xl.util.*;
import de.grogra.xl.lang.*;
import de.grogra.reflect.*;
import de.grogra.reflect.Method;
import de.grogra.reflect.Field;
import de.grogra.grammar.ASTWithToken;
import de.grogra.grammar.RecognitionExceptionList;
import de.grogra.xl.compiler.scope.*;
import de.grogra.xl.compiler.pattern.*;
import de.grogra.xl.compiler.scope.Local;
import de.grogra.xl.compiler.scope.Package;
import de.grogra.xl.modules.Instantiator;
import de.grogra.xl.property.CompiletimeModel.Property;

public interface CompilerTokenTypes {
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
	int SCALE = 15;
	int CLASS = 16;
	int INTERFACE = 17;
	int IMPORT_ON_DEMAND = 18;
	int STATIC_IMPORT_ON_DEMAND = 19;
	int SINGLE_TYPE_IMPORT = 20;
	int SINGLE_STATIC_IMPORT = 21;
	int EXTENDS = 22;
	int IMPLEMENTS = 23;
	int PARAMETERS = 24;
	int PARAMETER_DEF = 25;
	int SUPER = 26;
	int ARGLIST = 27;
	int SLIST = 28;
	int INSTANTIATOR = 29;
	int METHOD = 30;
	int THROWS = 31;
	int SEMI = 32;
	int CONSTRUCTOR = 33;
	int VARIABLE_DEF = 34;
	int ASSIGN = 35;
	int ARRAY_DECLARATOR = 36;
	int DECLARING_TYPE = 37;
	int VOID_ = 38;
	int BOOLEAN_ = 39;
	int BYTE_ = 40;
	int SHORT_ = 41;
	int CHAR_ = 42;
	int INT_ = 43;
	int LONG_ = 44;
	int FLOAT_ = 45;
	int DOUBLE_ = 46;
	int INSTANCE_INIT = 47;
	int STATIC_INIT = 48;
	int EMPTY = 49;
	int QUERY = 50;
	int COMPOUND_PATTERN = 51;
	int LABEL = 52;
	int PATTERN_WITH_BLOCK = 53;
	int MINIMAL = 54;
	int LATE_MATCH = 55;
	int SINGLE_MATCH = 56;
	int OPTIONAL_MATCH = 57;
	int SINGLE_OPTIONAL_MATCH = 58;
	int ANY = 59;
	int FOLDING = 60;
	int SEPARATE = 61;
	int LT = 62;
	int GT = 63;
	int LINE = 64;
	int LEFT_RIGHT_ARROW = 65;
	int PLUS_LEFT_ARROW = 66;
	int PLUS_ARROW = 67;
	int PLUS_LINE = 68;
	int PLUS_LEFT_RIGHT_ARROW = 69;
	int SLASH_LEFT_ARROW = 70;
	int SLASH_ARROW = 71;
	int SLASH_LINE = 72;
	int SLASH_LEFT_RIGHT_ARROW = 73;
	int TYPE_PATTERN = 74;
	int WRAPPED_TYPE_PATTERN = 75;
	int NAME_PATTERN = 76;
	int TREE = 77;
	int CONTEXT = 78;
	int EXPR = 79;
	int ROOT = 80;
	int METHOD_PATTERN = 81;
	int METHOD_CALL = 82;
	int DOT = 83;
	int APPLICATION_CONDITION = 84;
	int PARAMETERIZED_PATTERN = 85;
	int SUB = 86;
	int LEFT_ARROW = 87;
	int ARROW = 88;
	int X_LEFT_RIGHT_ARROW = 89;
	int TRAVERSAL = 90;
	int QUESTION = 91;
	int MUL = 92;
	int ADD = 93;
	int RANGE_EXACTLY = 94;
	int RANGE_MIN = 95;
	int RANGE = 96;
	int RULE = 97;
	int DOUBLE_ARROW_RULE = 98;
	int EXEC_RULE = 99;
	int PRODUCE = 100;
	int WITH = 101;
	int UNARY_PREFIX = 102;
	int TYPECAST = 103;
	int TYPECHECK = 104;
	int COM = 105;
	int NOT = 106;
	int NEG = 107;
	int POS = 108;
	int DIV = 109;
	int REM = 110;
	int POW = 111;
	int SHL = 112;
	int SHR = 113;
	int USHR = 114;
	int LE = 115;
	int GE = 116;
	int CMP = 117;
	int NOT_EQUALS = 118;
	int EQUALS = 119;
	int OR = 120;
	int XOR = 121;
	int AND = 122;
	int COR = 123;
	int CAND = 124;
	int ARRAY_INIT = 125;
	int RULE_BLOCK = 126;
	int ELIST = 127;
	int SHELL_BLOCK = 128;
	int THIS = 129;
	int QUALIFIED_SUPER = 130;
	int IF = 131;
	int RETURN = 132;
	int YIELD = 133;
	int THROW = 134;
	int SYNCHRONIZED_ = 135;
	int ASSERT = 136;
	int LABELED_STATEMENT = 137;
	int BREAK = 138;
	int CONTINUE = 139;
	int TRY = 140;
	int CATCH = 141;
	int FINALLY = 142;
	int NODES = 143;
	int NODE = 144;
	int LCLIQUE = 145;
	int RCLIQUE = 146;
	int FOR = 147;
	int ENHANCED_FOR = 148;
	int WHILE = 149;
	int DO = 150;
	int SWITCH = 151;
	int SWITCH_GROUP = 152;
	int CASE = 153;
	int DEFAULT = 154;
	int NULL_LITERAL = 155;
	int INVALID_EXPR = 156;
	int LONG_LEFT_ARROW = 157;
	int LONG_ARROW = 158;
	int LONG_LEFT_RIGHT_ARROW = 159;
	int INSTANCEOF = 160;
	int CLASS_LITERAL = 161;
	int QUOTE = 162;
	int ADD_ASSIGN = 163;
	int SUB_ASSIGN = 164;
	int MUL_ASSIGN = 165;
	int DIV_ASSIGN = 166;
	int REM_ASSIGN = 167;
	int POW_ASSIGN = 168;
	int SHR_ASSIGN = 169;
	int USHR_ASSIGN = 170;
	int SHL_ASSIGN = 171;
	int AND_ASSIGN = 172;
	int XOR_ASSIGN = 173;
	int OR_ASSIGN = 174;
	int DEFERRED_ASSIGN = 175;
	int DEFERRED_RATE_ASSIGN = 176;
	int DEFERRED_ADD = 177;
	int DEFERRED_SUB = 178;
	int DEFERRED_MUL = 179;
	int DEFERRED_DIV = 180;
	int DEFERRED_REM = 181;
	int DEFERRED_POW = 182;
	int DEFERRED_OR = 183;
	int DEFERRED_AND = 184;
	int DEFERRED_XOR = 185;
	int DEFERRED_SHL = 186;
	int DEFERRED_SHR = 187;
	int DEFERRED_USHR = 188;
	int INC = 189;
	int DEC = 190;
	int POST_INC = 191;
	int POST_DEC = 192;
	int IN = 193;
	int GUARD = 194;
	int ARRAY_ITERATOR = 195;
	int QUERY_EXPR = 196;
	int INVOKE_OP = 197;
	int QUALIFIED_NEW = 198;
	int INDEX_OP = 199;
	int NEW = 200;
	int DIMLIST = 201;
	int MODIFIERS = 202;
	int ANNOTATION = 203;
	int PRIVATE_ = 204;
	int PUBLIC_ = 205;
	int PROTECTED_ = 206;
	int STATIC_ = 207;
	int TRANSIENT_ = 208;
	int FINAL_ = 209;
	int ABSTRACT_ = 210;
	int NATIVE_ = 211;
	int VOLATILE_ = 212;
	int STRICT_ = 213;
	int ITERATING_ = 214;
	int CONST_ = 215;
	int VARARGS_ = 216;
	int STATIC_MEMBER_CLASSES = 217;
	int MARKER = 218;
	int SINGLE_ELEMENT = 219;
	int NORMAL = 220;
}
