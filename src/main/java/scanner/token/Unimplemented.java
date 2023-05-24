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
 * This class is used for tokens that aren't supported by the tokenizer.
 */
public final class Unimplemented extends Token {
	private final char characterLit;

	/**
	 * Creates a new unimplemented token at the specified line and character, with the specified value.
	 *
	 * @param curline      the line in the source code.
	 * @param curcharacter the character in the source code.
	 * @param chr          the value.
	 */
	public Unimplemented(int curline, int curcharacter, char chr) {
		super(curline, curcharacter);
		characterLit = chr;
	}

	/**
	 * @throws InstantiationException if used. CharTok should not be used without a value, use instanceof instead.
	 */
	public Unimplemented() throws InstantiationException {
		super(-1, -1);
		throw new InstantiationException("No-arg constructors not supported by Unimplemented");
	}


	/**
	 * @return The stored token.
	 */
	public char getcharacterLit() {
		return characterLit;
	}

	/**
	 * @return false. An unimplemented token is never equal to anything.
	 */
	@Override
	public boolean equals(Object o) {
		return false;
	}

	/**
	 * Returns a text representation of the token.
	 *
	 * @return "Token{type=Char, offending='[value]', line=[line], character=[charnum]}
	 */
	@Override
	public String toString() {
		return "Token{type=Unimplemented, " +
					   "char='" + characterLit + '\'' +
					   ", line=" + line +
					   ", character='" + charnum + "'}";
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
}