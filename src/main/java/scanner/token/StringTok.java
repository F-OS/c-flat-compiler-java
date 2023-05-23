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

public final class StringTok extends Token {
	private final String str;

	public StringTok(int line, int character, String str) {
		super(line, character);
		this.str = str;
	}

	public StringTok(int line, int character) {
		super(line, character);
		str = null;
	}

	public StringTok() {
		super(-1, -1);
		str = null;
	}

	public String getStr() {
		return str;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof StringTok other) {
			return Objects.equals(str, other.str) && getTokenLoc().equals(other.getTokenLoc());
		}
		if (o instanceof String) {
			return Objects.equals(str, o);
		}
		return false;
	}

	@Override
	public String toString() {
		return "Token{type=String, " +
					   "string=\"" + str + '\"' +
					   '}';
	}
}