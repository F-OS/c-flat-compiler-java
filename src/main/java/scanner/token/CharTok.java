/*
 * Copyright (c) 2023.
 *
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 *
 */

package scanner.token;

import scanner.Token;

public final class CharTok extends Token {
	private final char characterlit;

	public CharTok(int line, int character, char characterlit) {
		super(line, character);
		this.characterlit = characterlit;
	}

	public CharTok(int line, int character) {
		super(line, character);
		characterlit = '\0';
	}

	public CharTok() {
		super(-1, -1);
		characterlit = '\0';
	}

	public char getChar() {
		return characterlit;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CharTok other) {
			return characterlit == other.characterlit && getTokenLoc().equals(other.getTokenLoc());
		}
		return false;
	}

	public boolean equals(char othercharacterlit) {
		return characterlit == othercharacterlit;
	}

	@Override
	public String toString() {
		return "Token{type=Char, " +
					   "char=\'" + characterlit + '\'' +
					   '}';
	}
}