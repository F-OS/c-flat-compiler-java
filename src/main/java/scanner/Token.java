/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package scanner;

import java.util.Objects;

public class Token {
	public enum TokenType {
		INTCONST, FLOATCONST, STRINGLIT, CHARLIT, IDENTIFIER,
		LSHIFTASSIGN, RSHIFTASSIGN, POWASSIGN, ADDASSIGN, SUBASSIGN, MULASSIGN, DIVASSIGN, MODASSIGN, ANDASSIGN, ORASSIGN, XORASSIGN,
		INC, DEC,
		BITWISE_LSHIFT, BITWISE_RSHIFT, BITWISE_XOR, BITWISE_NOT, BITWISE_OR, BITWISE_AND,
		OR, AND, GREATEREQUAL, LESSEQUAL, GREATERTHAN, LESSTHAN, EQUALTO, NOTEQUALTO, NOT,
		ADD, SUB, MUL, DIV, MOD, POW,
		DOT, EQUATE, COMMA, SEMICOLON,
		LBRACE, RBRACE, LBRACKET, RBRACKET, LPAREN, RPAREN,
		QMARK, COLON,
		IF, ELSE, FOR, FOREACH, WHILE, DO, SWITCH, TRY, CONTINUE, BREAK, RETURN, GOTO, THROW, VAR, ARRAY, ENUM, CLASS, STRUCT, FUN,
		TRUE, FALSE,
		UNIMPLEMENTED, EOF
	}

	public final TokenType type;
	public final String text;
	public final int line;
	public final int charNum;
	public final int endNum;

	public Token(TokenType type, String text, int line, int col) {
		this.type = type;
		this.text = text;
		this.line = line;
		this.charNum = col;
		this.endNum = col + text.length();
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, text, line, charNum, endNum);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Token tok) {
			return
					this.type.equals(tok.type) &&
					this.text.equals(tok.text) &&
					this.line == tok.line &&
					this.charNum == tok.charNum;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		switch (type) {
			case INTCONST, FLOATCONST, STRINGLIT, CHARLIT, IDENTIFIER:
				return type + "(" + text + ")@" + line + ":" + charNum;
			default:
				return type + "@" + line + ":" + charNum;
		}
	}

}
