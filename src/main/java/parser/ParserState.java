/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import AST.Declaration;
import scanner.Token;
import scanner.token.EndOfFile;
import scanner.token.Primitive;
import scanner.token.Primitive.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ParserState {
	private final List<Token> tokenList;

	private List<Declaration> partialDeclarations;
	private boolean semicolonExempt = false;

	ParserState(List<Token> tokens) {
		tokenList = tokens;
		partialDeclarations = new ArrayList<>();
	}

	Token getTokenAt(int index) {
		if (index < tokenList.size()) {
			return tokenList.get(index);
		}
		return new EndOfFile(0, 0);
	}

	public void removeTokenAt(int index) {
		if (!tokenList.isEmpty() && !(tokenList.get(0) instanceof EndOfFile)) {
			tokenList.remove(index);
		}
	}

	public boolean isEmpty() {
		return tokenList.isEmpty();
	}

	public List<Declaration> getDeclarationList() {
		return partialDeclarations;
	}

	public void clearDeclarationList() {
		partialDeclarations = new ArrayList<>();
	}

	public void appendDeclaration(Declaration declaration) {
		partialDeclarations.add(declaration);
	}

	public boolean isSemicolonExempt() {
		if (semicolonExempt) {
			semicolonExempt = false;
			return true;
		}
		return false;
	}

	public void setSemicolonExempt() {
		semicolonExempt = true;
	}

	boolean isAssignment(Token token) {
		return token instanceof Assign
					   || token instanceof AddAssign
					   || token instanceof SubAssign
					   || token instanceof MulAssign
					   || token instanceof DivAssign
					   || token instanceof ModAssign
					   || token instanceof PowAssign
					   || token instanceof AndAssign
					   || token instanceof OrAssign
					   || token instanceof LShiftAssign
					   || token instanceof RShiftAssign
					   || token instanceof XorAssign;
	}
}