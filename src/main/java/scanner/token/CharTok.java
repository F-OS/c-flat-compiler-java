/*
 * Copyright (c) 2023.
 *
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 *
 */

package scanner.token;

import scanner.Token;

/**
 * Class for storing tokens containing a single character.
 */
public final class CharTok extends Token {
	private final char characterLit;

	/**
	 * Creates a new character token at the specified line and character, with the specified value.
	 *
	 * @param curline      the line in the source code.
	 * @param curcharacter the character in the source code.
	 * @param chr          the value.
	 */
	public CharTok(int curline, int curcharacter, char chr) {
		super(curline, curcharacter);
		characterLit = chr;
	}

	/**
	 * @throws InstantiationException if used. CharTok should not be used without a value, use instanceof instead.
	 */
	public CharTok() throws InstantiationException {
		super(-1, -1);
		throw new InstantiationException("No-arg constructors not supported by CharTok");
	}

	/**
	 * @return CharTok's stored value.
	 */
	public char getCharacterLit() {
		return characterLit;
	}

	/**
	 * Override for checking if two CharToks are equal. Returns true if and only if their line numbers, character numbers,
	 * and literals are equal.
	 *
	 * @param obj instance of CharTok
	 * @return true if they're equal, false if not.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CharTok other) {
			return characterLit == other.characterLit && getTokenLoc().equals(other.getTokenLoc());
		}
		return false;
	}

	/**
	 * Overload for checking if a CharTok instance matches a character.
	 *
	 * @param othercharacterlit a character
	 * @return true if the fields are equal, false if not.
	 */
	public boolean equals(char othercharacterlit) {
		return characterLit == othercharacterlit;
	}

	/**
	 * @return A (hopefully) unique hash code for the character.
	 */
	@Override
	public int hashCode() {
		int result = line;
		result = 31 * result + charnum;
		result = 337 * result + characterLit;
		return result;
	}

	/**
	 * Returns a text representation of the token.
	 *
	 * @return "Token{type=Char, char='[value]', line=[line], character=[charnum]}
	 */
	@Override
	public String toString() {
		return "Token{type=Char, " + "char='" + characterLit + '\'' + ", line=" + line + ", character=" + charnum + '}';
	}

}