/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import AST.Expression;
import AST.Statement;
import scanner.Token;
import utils.Entry;

import java.util.List;

public class StatementParser extends ParserState {
	private StatementParser(List<Token> tokenStream) {
		super(tokenStream);
	}

	public static Entry<Statement, Integer> parseStatement(List<Token> tokenStream) {
		StatementParser parser = new StatementParser(tokenStream);
		Statement parsed = parser.parseStatement();
		return new Entry<>(parsed, parser.getCurrentPosition());
	}

	public static Entry<Statement, Integer> parseBlock(List<Token> tokenStream) {
		StatementParser parser = new StatementParser(tokenStream);
		Statement parsed = parser.parseBlock();
		return new Entry<>(parsed, parser.getCurrentPosition());
	}

	private Statement parseStatement() {
		throw new UnsupportedOperationException("Not implemented");
	}

	private Statement parseBlock() {
		throw new UnsupportedOperationException("Not implemented");
	}
}
