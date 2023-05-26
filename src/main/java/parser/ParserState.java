/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import scanner.Token;

import java.util.List;

import utils.Entry;

public class ParserState {
	enum RequiresSemicolon {
		SEMICOLON_REQUIRED,
		NO_SEMICOLON_REQUIRED
	}

	private final List<Token> tokenStream;
	private int currentPosition;

	public ParserState(List<Token> tokenStream) {
		this.tokenStream = tokenStream;
		this.currentPosition = 0;
	}

	public Token getCurrentToken() {
		if (currentPosition < tokenStream.size()) {
			return tokenStream.get(currentPosition);
		}
		// Return a special "end of input" token
		return new Token(Token.TokenType.EOF, "", -1, -1);
	}

	public Token getNextToken() {
		if (currentPosition + 1 < tokenStream.size()) {
			return tokenStream.get(currentPosition + 1);
		}
		// Return a special "end of input" token
		return new Token(Token.TokenType.EOF, "", -1, -1);
	}

	public Token consumeToken() {
		Token tok = getCurrentToken();
		currentPosition++;
		return tok;
	}

	public boolean curTokenIsType(Token.TokenType tokType) {
		Token tok = getCurrentToken();
		return tok.type == tokType;
	}

	public boolean nextTokenIsType(Token.TokenType tokType) {
		Token tok = getNextToken();
		return tok.type == tokType;
	}

	public boolean isAnyOfType(List<Token.TokenType> tokType) {
		Token tok = getCurrentToken();
		return tokType.contains(tok.type);
	}

	public boolean isIdentifier() {
		Token tok = getCurrentToken();
		return tok.type == Token.TokenType.IDENTIFIER;
	}

	public String getTokenText() {
		Token tok = getCurrentToken();
		return tok.text;
	}

	public Token match(Token.TokenType expectedTokenType, String error) {
		Token currentToken = getCurrentToken();
		if (currentToken.type == expectedTokenType) {
			consumeToken();
			return currentToken;
		} else {
			throw new RuntimeException("Syntax error: " + error + " Expected " + expectedTokenType +
									   " but found " + currentToken.type + " at line " + currentToken.line +
									   ", char " + currentToken.charNum);
		}
	}

	public String matchIdent(String error) {
		Token idt = match(Token.TokenType.IDENTIFIER, error);
		return idt.text;
	}

	Entry<Integer, Integer> getCurrentLocation() {
		return new Entry<>(getCurrentToken().line, getCurrentToken().charNum);
	}

	List<Token> getTokenStream() {
		return tokenStream;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void advanceLocation(int loc) {
		currentPosition = loc;
	}

	public Token matchList(List<Token.TokenType> expectedTokenType, String error) {
		Token currentToken = getCurrentToken();
		if (expectedTokenType.contains(currentToken.type)) {
			consumeToken();
			return currentToken;
		} else {
			throw new RuntimeException("Syntax error: " + error + " Expected " + String.join(", or ",
					expectedTokenType.stream().map(Enum::toString).toList()) +
									   " but found " + currentToken.type + " at line " + currentToken.line +
									   ", char " + currentToken.charNum);
		}
	}
}