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

/**
 * Class for storing tokens containing an identifier.
 */
public final class Ident extends Token {
	private final String identifier;

	/**
	 * Creates a new character token at the specified line and character, with the specified value.
	 *
	 * @param curline      the line in the source code.
	 * @param curcharacter the character in the source code.
	 * @param ident        the value.
	 */
	public Ident(int curline, int curcharacter, String ident) {
		super(curline, curcharacter);
		identifier = ident;
	}

	/**
	 * @throws InstantiationException if used. Idents should not be used without a value, use instanceof instead.
	 */
	public Ident() throws InstantiationException {
		super(-1, -1);
		throw new InstantiationException("No-arg constructors not supported by Ident");
	}

	/**
	 * @return Ident's stored value.
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Override for checking if two Ident are equal. Returns true if and only if their line numbers, character numbers,
	 * and literals are equal.
	 *
	 * @param obj instance of CharTok
	 * @return true if they're equal, false if not.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Ident other) {
			return Objects.equals(identifier, other.identifier) && getTokenLoc().equals(other.getTokenLoc());
		}
		return false;
	}

	/**
	 * Overload for checking if an Ident instance matches a string.
	 *
	 * @param str a string
	 * @return true if the fields are equal, false if not.
	 */
	public boolean equals(String str) {
		return identifier.equals(str);
	}

	/**
	 * @return A (hopefully) unique hash code for the character.
	 */
	public int hashCode() {
		int result = line;
		result = 31 * result + charnum;
		result = 337 * result + identifier.hashCode();
		return result;
	}

	/**
	 * Returns a text representation of the token.
	 *
	 * @return "Token{type=Ident, identifier='[value]', line=[line], character=[charnum]}
	 */
	@Override
	public String toString() {
		return "Token{type=Ident, " + "identifier=" + identifier + ", line=" + line + ", character=" + charnum + '}';
	}
}
