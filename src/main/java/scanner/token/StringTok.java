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
 * Class for storing tokens containing a string.
 */
public final class StringTok extends Token {
	private final String str;

	/**
	 * Creates a new character token at the specified line and character, with the specified value.
	 *
	 * @param curline      the line in the source code.
	 * @param curcharacter the character in the source code.
	 * @param strIn        the value.
	 */
	public StringTok(int curline, int curcharacter, String strIn) {
		super(curline, curcharacter);
		str = strIn;
	}

	/**
	 * @throws InstantiationException if used. StringTok should not be used without a value, use instanceof instead.
	 */
	public StringTok() throws InstantiationException {
		super(-1, -1);
		throw new InstantiationException("No-arg constructors not supported by StringTok");
	}

	/**
	 * @return StringTok's stored value.
	 */
	public String getStr() {
		return str;
	}

	/**
	 * Override for checking if two StringToks are equal. Returns true if and only if their line numbers, character numbers,
	 * and literals are equal.
	 *
	 * @param obj instance of CharTok
	 * @return true if they're equal, false if not.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StringTok other) {
			return Objects.equals(str, other.str) && getTokenLoc().equals(other.getTokenLoc());
		}
		if (obj instanceof String) {
			return Objects.equals(str, obj);
		}
		return false;
	}

	/**
	 * Returns a text representation of the token.
	 *
	 * @return "Token{type=String, string="[value]", line=[line], character=[charnum]}
	 */
	@Override
	public String toString() {
		return "Token{type=String, " + "string=\"" + str + ", line=" + line + ", character=" + charnum + '}';
	}
}