/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import AST.ASTRoot;
import AST.Declaration;
import scanner.Token;
import scanner.token.Ident;
import scanner.token.EndOfFile;
import scanner.token.Primitive.RBrace;
import scanner.token.Primitive.Semicolon;
import scanner.token.Unimplemented;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class ParserValidation extends ErrorTable {
	private final ParserState parserState;
	private final List<String> synchronizeTokens = List.of(
			"if", "while", "for", "foreach", "do", "class", "struct", "var", "fun", "enum", "switch", "else",
			"throw", "try", "catch"
	);
	private final ArrayList<String> reserved;

	ParserValidation(ParserState parser) {
		parserState = parser;
		reserved = new ArrayList<>(Stream.concat(Stream.of("true", "false", "nil"), synchronizeTokens.stream()).toList());
	}


	// Utility method to generate an error message
	private String buildErrorMessage(String message, String receivedToken, String expectedTokens) {
		return String.format("%s Got '%s', expected %s.", message, receivedToken, expectedTokens);
	}

	// Utility method to generate an error message with line/column numbers
	private String buildErrorMessage(String message, String receivedToken, String expectedTokens, int line, int character) {
		return String.format("%s Got '%s', expected %s.", message, receivedToken, expectedTokens, line, character);
	}


	/**
	 * When parser errors occur, this function consumes all tokens up to the nearest synchronizeable token.
	 * Synchronizeable tokens are those which are likely to be followed by valid syntax,
	 * I.E control flow structures or semicolons.
	 * This allows the parser to continue without throwing out a bunch of errors.
	 * General logic from: <a href="https://www.rose-hulman.edu/class/cs/csse404/schedule/day28/28-ErrorRecoveryI.pdf">here</a>
	 *
	 * @return the current list of partial declarations.
	 */
	public ArrayList<Declaration> synchronize() {
		ArrayList<Declaration> declsSoFar = new ArrayList<>(parserState.getDeclarationList());
		parserState.getDeclarationList();
		while (!(parserState.getTokenAt(0) instanceof EndOfFile)) {
			Token cur = parserState.getTokenAt(0);
			if (cur instanceof RBrace || cur instanceof Semicolon) {
				parserState.removeTokenAt(0);
				return declsSoFar;
			} else if (cur instanceof Ident ident) {
				String identStr = ident.getIdentifier();
				if (synchronizeTokens.contains(identStr)) {
					return declsSoFar;
				} else {
					parserState.removeTokenAt(0);
				}
			} else {
				parserState.removeTokenAt(0);
			}
		}
		return declsSoFar;
	}

	/**
	 * Throws an error if the wrong token is passed.
	 * This function allows you to get a token from the stream at the given index.
	 * Pretty straightforward.
	 */
	public Token expectToken(List<Class<? extends Token>> expectedTokens, String errorMessage, parserStates parserStage) {
		if (parserState.isEmpty()) {
			throwError(parserStage, ERROR_EOF);
		}
		Token curtok = parserState.getTokenAt(0);
		if (expectedTokens.stream().noneMatch(expectedToken -> expectedToken.isInstance(curtok))) {
			int line = parserState.getTokenAt(0).getTokenLoc().line();
			int character = curtok.getTokenLoc().character();
			if (expectedTokens.stream().allMatch(expectedToken -> expectedToken.equals(Semicolon.class)) && parserState.isSemicolonExempt()) {
				return new Semicolon(line, character);
			}
			String tokenString = curtok.toString();
			String expectedTokensString = expectedTokens.stream().map(Class::getSimpleName).collect(Collectors.joining(" or "));
			String error = String.format("%s Got %s, expected %s.", errorMessage, tokenString, expectedTokensString);
			throwError(parserStage, error, line, character);
		}
		if (curtok instanceof Semicolon) {
			parserState.isSemicolonExempt();
		}
		parserState.removeTokenAt(0);
		return curtok;
	}

	// Utility method to throw an error without line/column numbers
	private void throwError(String message) {
		throwError(parserStates.UNLABELED_PARSER_STATE, message, 0, 0);
	}

	// Utility method to throw an error with line/column numbers
	void throwError(parserStates state, String message) {
		throwError(state, message, 0, 0);
	}


	/*
	 * Generates a line/column numbered exception.
	 */
	void throwError(parserStates state, String message, int line, int character) {
		if (parserState.getTokenAt(0) instanceof Unimplemented unimplemented) {
			message = "Unimplemented token - Unable to recognize " +
							  unimplemented.getcharacterLit();
		}
		if (state == parserStates.UNLABELED_PARSER_STATE) {
			throw new ParserException(message, line, character);
		}
		String stateOf = switch (state) {
			case PARSER_EXPRESSION_STAGE -> "expression";
			case PARSER_STATEMENT_STAGE -> "statement";
			case PARSER_DECLARATION_STAGE -> "declaration";
			case UNLABELED_PARSER_STATE -> "unknown";
		};
		throw new ParserException(message, line, character, stateOf);
	}

	/**
	 * Throws an error if the wrong identifier is passed.
	 * It does not remove the token if it gets a match.
	 * Again, pretty straightforward.
	 */
	public void expectIdent(String ident, List<String> expectedIdent, String errorMessage) {
		if (parserState.isEmpty()) {
			throwError(ERROR_EOF);
		}
		if (!expectedIdent.contains(ident)) {
			String expectedIdentString = String.join(" or ", expectedIdent);
			String error = String.format("%s Got '%s', expected %s.", errorMessage, ident, expectedIdentString);
			throwError(error);
		}
	}

	/**
	 * Throws an error if the wrong node is passed.
	 */
	public ASTRoot expectNode(ASTRoot receivedNode,
							  List<Class<? extends ASTRoot>> expectedNode,
							  String errorMessage,
							  parserStates parserStage) {
		if (parserState.isEmpty()) {
			throwError(parserStage, ERROR_EOF);
		}
		if (expectedNode.
					stream().
					noneMatch(
							expectedNodeClass -> expectedNodeClass.equals(receivedNode.getClass())
					)
		) {
			int line = receivedNode.getLine();
			int character = receivedNode.getCharacter();
			String tokenString = receivedNode.toString();
			String expectedTokensString = expectedNode.stream()
												  .map(Class::toString)
												  .collect(Collectors.joining(" or "));
			String error = String.format("%s Got %s, expected %s.", errorMessage, tokenString, expectedTokensString);
			throwError(parserStage, error, line, character);
		}
		return receivedNode;
	}

	/**
	 * Opposite of expectToken. This function throws an error if a token in the rejectedToken list is passed.
	 */
	public void rejectTokens(List<Class<? extends Token>> rejectedToken, String errorMessage, parserStates parserStage) {
		if (parserState.isEmpty()) {
			throwError(parserStage, ERROR_EOF);
		}
		Token curtok = parserState.getTokenAt(0);
		if (rejectedToken.stream().anyMatch(rejectedTokenClass -> rejectedTokenClass.isInstance(curtok))) {
			int line = curtok.getTokenLoc().line();
			int character = curtok.getTokenLoc().character();
			String tokenString = curtok.toString();
			String error = String.format("%s Unexpected %s.", errorMessage, tokenString);
			throwError(parserStage, error, line, character);
		}
	}

	/**
	 * Throws an error if an identifier is not passed.
	 * This function allows you to get a user-defined identifier.
	 * It removes and returns the identifier if it gets a match.
	 */
	public String expectIdentifierToken(String variableType, String statementType) {
		Token curTok = parserState.getTokenAt(0);
		if (!(parserState.getTokenAt(0) instanceof Ident)) {
			throwError(String.format("Error: missing an identifier in a %s. Got '%s', expecting identifier for %s.",
					statementType, parserState.getTokenAt(0), variableType)
			);
		}
		Ident identifier = (Ident) curTok;
		String strIdt = identifier.getIdentifier();
		if (reserved.contains(strIdt)) {
			throwError(
					String.format("Error: reserved token in %s. Got '%s', which cannot be used for %s.",
							statementType, strIdt, variableType));
		}
		parserState.removeTokenAt(0);
		return strIdt;
	}

	public boolean checkReserved(String idt) {
		return reserved.contains(idt);
	}
}
