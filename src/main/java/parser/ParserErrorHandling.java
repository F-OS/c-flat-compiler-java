/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import scanner.Token;
import scanner.token.Unimplemented;

class ParserErrorHandling extends ErrorTable {
	ParserState parserState;

	ParserErrorHandling(ParserState parser) {
		parserState = parser;
	}

	private enum parserStates {
		PARSER_EXPRESSION_STAGE,
		PARSER_STATEMENT_STAGE,
		PARSER_DECLARATION_STAGE,
		UNLABELED_PARSER_STATE
	}

	void throwError(parserStates state, String message, int line, int character) {
		if (parserState.getTokenAt(0) instanceof Unimplemented) {
			message = "Unimplemented token - Unable to recognize " +
							  ((Unimplemented) parserState.getTokenAt(0)).getcharacterLit();
		}
		if (state == parserStates.UNLABELED_PARSER_STATE) {
			throw new ParserException(message, line, character);
		}
		switch (state) {
			case PARSER_EXPRESSION_STAGE -> {
				throw new ParserException(message, line, character, "expression");
			}
			case PARSER_STATEMENT_STAGE -> {
				throw new ParserException(message, line, character, "statement");
			}
			case PARSER_DECLARATION_STAGE -> {
				throw new ParserException(message, line, character, "declaration");
			}
		}
	}

	void throwError(parserStates state, String message) {
		Token.Location loc = parserState.getTokenAt(0).getTokenLoc();
		throwError(state, message, loc.line(), loc.character());
	}

	void throwError(String message) {
		throwError(parserStates.UNLABELED_PARSER_STATE, message);
	}

	void synchronize() {

	}
}
