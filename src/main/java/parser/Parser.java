/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import AST.*;
import AST.Statements.*;
import scanner.*;
import scanner.Token.*;
import utils.*;

import java.util.*;


public class Parser {
	protected final ParsingContext context;

	public Parser(List<Token> tokenStream) {
		this.context = new ParsingContext(tokenStream);
	}

	public Parser(ParsingContext context) {
		this.context = context;
	}

	private Parser() {
		throw new IllegalArgumentException("Must provide a token stream.");
	}

	Token getNextToken() {
		return context.getNextToken();
	}

	public boolean nextTokenIsType(TokenType tokType) {
		Token tok = context.getNextToken();
		return tok.type == tokType;
	}

	public String matchIdent(String error) {
		Token idt = match(TokenType.IDENTIFIER, error);
		return idt.text;
	}

	public Token match(TokenType expectedTokenType, String error) {
		Token currentToken = getCurrentToken();
		if (expectedTokenType == TokenType.SEMICOLON && context.semicolonExempt()) {
			context.clearSemicolonExempt();
			return new Token(TokenType.SEMICOLON, ";", getCurrentLocation().key(), getCurrentLocation().value());
		}
		if (currentToken.type == expectedTokenType) {
			consumeToken();
			return currentToken;
		} else {
			throw new RuntimeException("Syntax error: " + error + " Expected " + expectedTokenType +
									   " but found " + currentToken.type + " at line " + currentToken.line +
									   ", char " + currentToken.charNum);
		}
	}

	Token getCurrentToken() {
		return context.getCurrentToken();
	}

	Entry<Integer, Integer> getCurrentLocation() {
		return new Entry<>(getCurrentToken().line, getCurrentToken().charNum);
	}

	public Token consumeToken() {
		Token tok = getCurrentToken();
		context.advanceLocation(1);
		return tok;
	}

	public void matchList(List<TokenType> expectedTokenType, String error) {
		Token currentToken = getCurrentToken();
		if (expectedTokenType.contains(currentToken.type)) {
			consumeToken();
		} else {
			throw new RuntimeException("Syntax error: " + error + " Expected " + String.join(", or ",
					expectedTokenType.stream().map(Enum::toString).toList()) +
									   " but found " + currentToken.type + " at line " + currentToken.line +
									   ", char " + currentToken.charNum);
		}
	}

	public Statement parseBlock() {
		Token tok = match(TokenType.LBRACE, "Blocks must start with braces.");

		List<Declaration> block = new ArrayList<>(64);
		while (getCurrentToken().type != TokenType.RBRACE) {
			block.add(DeclarationParser.parseDeclaration(context));
			if (curTokenIsType(TokenType.EOF)) {
				throw new RuntimeException("Unterminated block starting at line: " + tok.line);
			}
		}
		match(TokenType.RBRACE, "Blocks must end with braces.");

		return new Block(block, new Entry<>(tok.line, tok.charNum));
	}

	public boolean curTokenIsType(TokenType tokType) {
		Token tok = getCurrentToken();
		return tok.type == tokType;
	}
}