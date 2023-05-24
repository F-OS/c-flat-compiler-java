/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

public final class ParserException extends RuntimeException {
	ParserException() {
		super("Unlabelled exception");
	}

	ParserException(String strMessage, int line, int character, String in) {
		super("ERROR(Parser): Line " + line + ", character " + character + " while parsing " + in + " - " + strMessage);
	}

	ParserException(String strMessage, int line, int character) {
		super("ERROR(Parser): Line " + line + ", character " + character + " - " + strMessage);
	}
}
