/*
 * Copyright (c) 2023.
 *
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 *
 */

package scanner.token;

import scanner.Token;

public final class Unimplemented extends Token {
	private final char characterlit;

	public Unimplemented(int line, int character, char characterlit) {
		super(line, character);
		this.characterlit = characterlit;
	}

	public Unimplemented(int line, int character) {
		super(line, character);
		characterlit = '\0';
	}

	public Unimplemented() {
		super(-1, -1);
		characterlit = '\0';
	}

	public char getChar() {
		return characterlit;
	}

	@Override
	public boolean equals(Object o) {
		return false;
	}

	@Override
	public String toString() {
		return "Token{type=Unimplemented, " +
					   "char='" + characterlit + '\'' +
					   '}';
	}
}