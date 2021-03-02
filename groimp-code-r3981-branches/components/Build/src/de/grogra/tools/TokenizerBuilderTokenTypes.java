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

public interface TokenizerBuilderTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int ID = 4;
	int STRING = 5;
	int ASSIGN = 6;
	int LPAREN = 7;
	int RPAREN = 8;
	int INT = 9;
	int WS = 10;
	int SL_COMMENT = 11;
	int ML_COMMENT = 12;
	int ESC = 13;
	int DIGIT = 14;
	int XDIGIT = 15;
}
