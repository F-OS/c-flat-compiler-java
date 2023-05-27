/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import scanner.*;
import scanner.Token.*;

import java.util.*;

public class ParsingContext {
	private final List<Token> tokenStream;
	private int currentPosition;
	private boolean semicolon = false;

	public ParsingContext(List<Token> tokenStream) {
		currentPosition = 0;
		this.tokenStream = tokenStream;
	}

	public void advanceLocation(int loc) {
		currentPosition += loc;
	}

	public Token getNextToken() {
		if (currentPosition + 1 < tokenStream.size()) {
			return tokenStream.get(currentPosition + 1);
		}
		// Return a special "end of input" token
		return new Token(TokenType.EOF, "", -1, -1);
	}

	public boolean semicolonExempt() {
		return semicolon;
	}

	public void setSemicolonExempt() {
		if (semicolon) {
			System.out.println("Suspicious use of semicolon exempt. Should not be set twice.");
		}
		semicolon = true;
	}

	public void clearSemicolonExempt() {
		if (!semicolon) {
			System.out.println("Suspicious use of semicolon exempt. Should not be cleared twice.");
		}
		semicolon = false;
	}

	public boolean isEmpty() {
		return getCurrentToken().type != TokenType.EOF;
	}

	public Token getCurrentToken() {
		if (currentPosition < tokenStream.size()) {
			return tokenStream.get(currentPosition);
		}
		// Return a special "end of input" token
		return new Token(TokenType.EOF, "", -1, -1);
	}
}
