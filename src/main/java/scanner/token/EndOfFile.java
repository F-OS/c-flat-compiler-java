/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package scanner.token;

import scanner.Token;

public final class EndOfFile extends Token {
	public EndOfFile() {
		this(-1, -1);
	}

	public EndOfFile(int curline, int curcharacter) {
		super(curline, curcharacter);
	}

	@Override
	public String toString() {
		return "Token{type=EndOfFile, " + ", line=" + line + ", character=" + charnum + '}';
	}
}
