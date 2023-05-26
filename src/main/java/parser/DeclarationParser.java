/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import AST.Declaration;
import AST.Statement;
import scanner.Token;
import utils.Entry;

import java.util.List;

public class DeclarationParser extends ParserState {
	private DeclarationParser(List<Token> tokenStream) {
		super(tokenStream);
	}

	public static Entry<Declaration, Integer> parseDeclaration(List<Token> tokenStream) {
		DeclarationParser parser = new DeclarationParser(tokenStream);
		Declaration parsed = parser.parseDeclaration();
		return new Entry<>(parsed, parser.getCurrentPosition());
	}

	private Declaration parseDeclaration() {
		throw new UnsupportedOperationException("Not implemented");
	}
}
