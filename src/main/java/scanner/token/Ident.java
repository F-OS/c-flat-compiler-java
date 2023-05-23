/*
 * Copyright (c) 2023.
 *
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 *
 */

package scanner.token;

import scanner.Token;

import java.util.Objects;


public final class Ident extends Token {
	private final String identifier;

	public Ident(int line, int character, String ident) {
		super(line, character);
		identifier = ident;
	}

	public Ident(int line, int character) {
		super(line, character);
		identifier = null;
	}

	public Ident() {
		super(-1, -1);
		identifier = null;
	}

	public String getIdentifier() {
		return identifier;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Ident other) {
			return Objects.equals(identifier, other.identifier) && getTokenLoc().equals(other.getTokenLoc());
		} else if (o instanceof String) {
			return Objects.equals(identifier, o);
		}
		return false;
	}

	@Override
	public String toString() {
		return "Token{type=Ident, " +
					   "identifier='" + identifier + '\'' +
					   '}';
	}
}
