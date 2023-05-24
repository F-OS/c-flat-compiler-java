/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import AST.ASTRoot.TypedVar;
import AST.*;
import AST.Declarations.*;
import AST.Expressions.OpEnums.BinaryOps;
import AST.Expressions.OpEnums.UnaryOps;
import AST.Statements.*;
import AST.Expressions.*;
import scanner.Token;
import scanner.token.*;
import scanner.token.Primitive.*;

import java.util.ArrayList;
import java.util.List;

import utils.Entry;

public class Parser {
	private ParserState state;

	private OperatorPrecedenceTable precTable;
	private ParserValidation error;

	public Parser(ArrayList<Token> toks) {
		state = new ParserState(toks);
		precTable = new OperatorPrecedenceTable();
		error = new ParserValidation(state);
	}

	/**
	 * Declarations
	 */

	/**
	 * Parses a declaration.
	 *
	 * @return The parsed declaration.
	 * ***Grammar:***
	 * * [Declaration] -> [SimpleVarDeclaration]
	 * * | [ArrayDeclaration]
	 * * | [EnumDeclaration]
	 * * | [ClassDeclaration]
	 * * | [FunctionDeclaration]
	 * * | [Statement]
	 */
	Declaration parseDeclaration() {
		if (state.getTokenAt(0) instanceof Ident tok) {
			switch (tok.getIdentifier()) {
				case "var" -> {
					state.removeTokenAt(0);
					return parseVarDeclaration();
				}
				case "array" -> {
					state.removeTokenAt(0);
					return parseArrayDeclaration();
				}
				case "enum" -> {
					state.removeTokenAt(0);
					return parseEnumDeclaration();
				}
				case "class" -> {
					state.removeTokenAt(0);
					return parseClassDeclaration();
				}
				case "fun" -> {
					state.removeTokenAt(0);
					return parseFunDeclaration();
				}
				case "struct" -> {
					state.removeTokenAt(0);
					return parseStructDeclaration();
				}
				default -> {
					return parseStatement();
				}
			}
		} else {
			return parseStatement();
		}
	}

	private Declaration parseVarDeclaration() {
		Token tok = state.getTokenAt(0);
		Token.Location loc = tok.getTokenLoc();

		String ident = error.expectIdentifierToken("a variable name", "variable definition");

		error.expectToken(List.of(Colon.class), error.ERROR_VAR_DEFINITION, parserStates.PARSER_DECLARATION_STAGE);

		String type = error.expectIdentifierToken("a variable type", "variable definition");

		TypedVar typedVar = new TypedVar(ident, type);

		Token next = error.expectToken(
				List.of(Assign.class, Semicolon.class),
				error.ERROR_VAR_ASSIGNMENT,
				parserStates.PARSER_DECLARATION_STAGE
		);

		if (next instanceof Assign) {
			Expression expr = parseExpression();
			error.expectToken(
					List.of(Semicolon.class),
					error.ERROR_EXPR_SEMICOLON,
					parserStates.PARSER_DECLARATION_STAGE
			);
			return new SimpleVarDeclaration(typedVar, expr, loc.line(), loc.character());
		} else if (next instanceof Semicolon) {
			return new SimpleVarDeclaration(typedVar, null, loc.line(), loc.character());
		} else {
			error.throwError(parserStates.PARSER_DECLARATION_STAGE, "Error: Failed to parse a variable declaration.");
			return null;
		}
	}

	/**
	 * Parses an array.
	 *
	 * @return The parsed declaration.
	 * <p>
	 * ***Grammar:***
	 * * ArrayDeclaration -> "array" Name ":" Type ["[" Number "]"] ["=" ExpressionList] ";"
	 * <p>
	 * * Name -> [Ident]ifier
	 * <p>
	 * * Type -> [Ident]ifier
	 */
	private Declaration parseArrayDeclaration() {
		Token tok = state.getTokenAt(0);
		Token.Location loc = tok.getTokenLoc();

		String ident = error.expectIdentifierToken("a variable name", "array definition");
		String type;

		Token next = error.expectToken(
				List.of(LBracket.class, Colon.class),
				error.ERROR_ARRAY_BRACES,
				parserStates.PARSER_DECLARATION_STAGE
		);
		long arraylen = -1;

		if (next instanceof LBracket) {
			next = error.expectToken(
					List.of(NumbTok.class),
					error.ERROR_ARRAY_LENGTH,
					parserStates.PARSER_DECLARATION_STAGE
			);
			NumbTok num = (NumbTok) next;
			if (!num.isInt()) {
				error.throwError(parserStates.PARSER_DECLARATION_STAGE, error.ERROR_ARRAY_LENGTH, num.getTokenLoc().line(), num.getTokenLoc().character());
			}

			arraylen = ((NumbTok) (next)).getInteger();

			error.expectToken(
					List.of(RBracket.class),
					error.ERROR_ARRAY_RSQUARE,
					parserStates.PARSER_DECLARATION_STAGE
			);
		}

		if (state.getTokenAt(0) instanceof Colon) {
			next = state.getTokenAt(0);
			state.removeTokenAt(0);
		}

		if (next instanceof Colon) {
			type = error.expectIdentifierToken("a variable type", "array definition");
		} else {
			type = "INFER";
		}

		next = error.expectToken(
				List.of(Assign.class, Semicolon.class),
				error.ERROR_ARRAY_ASSIGNMENT,
				parserStates.PARSER_DECLARATION_STAGE
		);

		if (next instanceof Assign) {
			List<Expression> initalizers = parseArrayInitalizers();
			error.expectToken(
					List.of(Semicolon.class),
					error.ERROR_ARRAY_INITIALIZER,
					parserStates.PARSER_DECLARATION_STAGE
			);
			TypedVar typedVar = new TypedVar(ident, type);
			return new ArrayDeclaration(typedVar, arraylen, initalizers, loc.line(), loc.character());
		}

		if (next instanceof Semicolon) {
			TypedVar typedVar = new TypedVar(ident, type);
			return new ArrayDeclaration(typedVar, arraylen, null, loc.line(), loc.character());
		} else {
			error.throwError(parserStates.PARSER_DECLARATION_STAGE, error.ERROR_MISSING_SEMICOLON);
		}
		return null;
	}

	/**
	 * Parses an array initalizer.
	 *
	 * @return The parsed expression.
	 * <p>
	 * ***Grammar:***
	 * * ExpressionList ->  "{" (Expression ",")+ "}"
	 * * * Expression
	 */
	private List<Expression> parseArrayInitalizers() {
		ArrayList<Expression> initalizerList = new ArrayList<>();
		Token next = error.expectToken(
				List.of(LBrace.class),
				error.ERROR_ARRAY_BRACES,
				parserStates.PARSER_DECLARATION_STAGE
		);
		while (!(next instanceof RBrace)) {
			initalizerList.add(parseExpression());
			next = state.getTokenAt(0);
			if (next instanceof RBrace) {
				break;
			} else if (next instanceof Comma) {
				state.removeTokenAt(0);
			} else {
				error.throwError(parserStates.PARSER_DECLARATION_STAGE, error.ERROR_ARRAY_INITIALIZER);
			}
		}
		error.expectToken(
				List.of(RBrace.class),
				error.ERROR_ARRAY_BRACES,
				parserStates.PARSER_DECLARATION_STAGE
		);
		return initalizerList;
	}

	/**
	 * Parses an enum.
	 *
	 * @return The parsed declaration.
	 * <p>
	 * ***Grammar:***
	 * * EnumDeclaration -> "enum" Name "{" EnumPairs "}"
	 * <p>
	 * * Name -> [Ident]ifier
	 * * EnumPairs ->  Name ":" Primary ","
	 */
	private Declaration parseEnumDeclaration() {
		Token.Location loc = state.getTokenAt(0).getTokenLoc();
		String name = error.expectIdentifierToken("an enum name", "enum declaration");

		error.expectToken(
				List.of(LBrace.class),
				"Error: An enum declaration must contain an identifier followed by a series of mappings.",
				parserStates.PARSER_DECLARATION_STAGE
		);

		ArrayList<EnumDeclaration.EnumMember> members = new ArrayList<>();
		long enumNumber = 0L;

		while (true) {
			Token nextToken = error.expectToken(
					List.of(Ident.class, RBrace.class),
					error.ERROR_ENUM_DECLARATION,
					parserStates.PARSER_DECLARATION_STAGE
			);

			if (nextToken instanceof Ident idt) {
				String paramName = idt.getIdentifier();

				Token tokenAfterName = error.expectToken(
						List.of(Colon.class, Comma.class),
						error.ERROR_ENUM_ENTRY,
						parserStates.PARSER_DECLARATION_STAGE
				);

				if (tokenAfterName instanceof Colon) {
					NumbTok numVal = (NumbTok) error.expectToken(
							List.of(NumbTok.class),
							"Error: Must define an enum entry as a number.",
							parserStates.PARSER_DECLARATION_STAGE
					);

					if (!numVal.isInt()) {
						error.throwError(parserStates.PARSER_DECLARATION_STAGE, "Error: Enums must be integers.");
					}

					long num = numVal.getInteger();
					members.add(new EnumDeclaration.EnumMember(paramName, num));
					enumNumber = num;
				} else if (tokenAfterName instanceof Comma) {
					members.add(new EnumDeclaration.EnumMember(paramName, enumNumber));
					enumNumber++;
				} else {
					error.throwError(parserStates.PARSER_DECLARATION_STAGE, "Error: Error in enum parsing");
				}
			} else if (nextToken instanceof RBrace) {
				if (members.isEmpty()) {
					System.out.println("WARNING: Empty enum definition at line " + state.getTokenAt(0).getTokenLoc().line() + ".");
				}
				break;
			} else {
				error.throwError(parserStates.PARSER_DECLARATION_STAGE, "Error: Error in enum parsing");
			}
		}

		return new EnumDeclaration(name, members, loc.line(), loc.character());
	}

	/**
	 * Parses a struct.
	 *
	 * @return The parsed declaration.
	 * <p>
	 * ***Grammar:***
	 * * StructDeclaration -> "struct" Name "{" DeclarationList "}"
	 * <p>
	 * * Name -> [Ident]ifier
	 */
	private Declaration parseStructDeclaration() {
		Token.Location loc = state.getTokenAt(0).getTokenLoc();
		String name = error.expectIdentifierToken("a struct name", "struct definition");
		List<Declaration> body = parseDeclarationBlock("struct");
		return new StructDeclaration(name, body, loc.line(), loc.character());
	}

	/**
	 * Parses a class.
	 *
	 * @return The parsed declaration.
	 * <p>
	 * ***Grammar:***
	 * * ClassDeclaration -> "struct" Name [":" InheritsFrom] "{" DeclarationList "}"
	 * <p>
	 * * Name -> [Ident]ifier
	 * <p>
	 * * InheritsFrom -> Name |
	 * * * "(" Name ("," Name)* ")"
	 */
	private Declaration parseClassDeclaration() {
		Token.Location loc = state.getTokenAt(0).getTokenLoc();
		String name = error.expectIdentifierToken("a class name", "class definition");
		List<String> inheritsFrom = new ArrayList<>();

		if (state.getTokenAt(0) instanceof Colon) {
			state.removeTokenAt(0);
			Token next = error.expectToken(
					List.of(LParen.class, Ident.class),
					error.ERROR_CLASS_INHERITANCE,
					parserStates.PARSER_DECLARATION_STAGE
			);

			if (next instanceof LParen) {
				while (true) {
					next = error.expectToken(
							List.of(Ident.class, RParen.class, Comma.class),
							error.ERROR_CLASS_INHERITANCE,
							parserStates.PARSER_DECLARATION_STAGE
					);
					if (next instanceof Ident) {
						inheritsFrom.add(((Ident) next).getIdentifier());
					} else if (next instanceof RParen) {
						break;
					} else if (next instanceof Comma) {
						// Commas are allowed between inherited classes
					} else {
						error.throwError(parserStates.PARSER_DECLARATION_STAGE, error.ERROR_FAILED_PARSE);
					}
				}
			} else {
				inheritsFrom.add(((Ident) next).getIdentifier());
			}
		}

		List<Declaration> body = parseDeclarationBlock("class");
		return new ClassDeclaration(name, body, inheritsFrom, loc.line(), loc.character());
	}

	/**
	 * Parses a declaration list.
	 *
	 * @return The parsed declaration list.
	 * <p>
	 * ***Grammar:***
	 * * DeclarationList -> "{" (Declarations)* "}"
	 */
	private List<Declaration> parseDeclarationBlock(String blockType) {
		Token next = error.expectToken(
				List.of(LBrace.class),
				"The body of " + blockType + " must be braced.",
				parserStates.PARSER_DECLARATION_STAGE
		);
		ArrayList<Declaration> list = new ArrayList<>();
		while (!(next instanceof RBrace || next instanceof EndOfFile)) {
			list.add(parseDeclaration());
			next = state.getTokenAt(0);
		}
		error.expectToken(
				List.of(RBrace.class),
				"The body of " + blockType + " must be braced.",
				parserStates.PARSER_DECLARATION_STAGE
		);
		return list;
	}

	/**
	 * Parses a function.
	 *
	 * @return A function node.
	 * <p>
	 * ***Grammar:***
	 * * Function -> "fun" funName Parameters ":" returnType Block
	 * * * funName -> Identifier
	 * * * returnType -> Identifier
	 */
	private Declaration parseFunDeclaration() {
		Token.Location loc = state.getTokenAt(0).getTokenLoc();
		String name = error.expectIdentifierToken("a function name", "function definition");
		String type;
		// Hacky fix.
		if ("fun".equals(name)) {
			error.throwError(parserStates.PARSER_DECLARATION_STAGE, "Either you are trying to define a function named fun or the parser was unable to synchronize properly. In either case stop it.");
		}
		List<TypedVar> params = parseParams("function");
		if (state.getTokenAt(0) instanceof Colon) {
			state.removeTokenAt(0);
			type = error.expectIdentifierToken("a return type", "function definition");
		} else {
			type = "INFER";
		}
		Block body = parseBlock();
		return new FunctionDeclaration(name, params, type, body, loc.line(), loc.character());
	}

	/**
	 * Parses the parameters of a function.
	 * A parameter definition must contain a left paren '(' followed by a list of parameters enclosed in parentheses.
	 * Each parameter is a typed Identifier.
	 *
	 * @return A list of strings representing the parsed parameters.
	 * <p>
	 * ***Grammar:***
	 * * Parameters -> "(" ParameterList ")"
	 * * ParameterList ->
	 * * * TypedIdentifier |
	 * * * ParameterList "," TypedIdentifier
	 */
	private List<TypedVar> parseParams(String statementType) {
		error.expectToken(
				List.of(LParen.class),
				error.ERROR_FUNCTION_PARAMS,
				parserStates.PARSER_EXPRESSION_STAGE
		);

		List<TypedVar> params = new ArrayList<>();
		boolean expectComma = false;

		while (true) {
			Token token = state.getTokenAt(0);
			if (token instanceof Ident) {
				if (expectComma) {
					System.out.println("WARNING: Expected a comma at identifier " + ((Ident) token).getIdentifier()
											   + " in parameter definition on line " + (token.getTokenLoc().line()) +
											   ", character" + (token.getTokenLoc().character()));
				}
				expectComma = true;

				String name = error.expectIdentifierToken("a parameter name", statementType);

				error.expectToken(
						List.of(Colon.class),
						"Error: A parameter definition must contain an identifier followed by a colon and then a typename.",
						parserStates.PARSER_EXPRESSION_STAGE
				);

				String type = error.expectIdentifierToken("a parameter type", statementType);

				params.add(new TypedVar(name, type));

			} else if (token instanceof RParen) {
				if (params.isEmpty()) {
					System.out.println(error.ERROR_EMPTY_PARAMETER_DEFINITION);
				}
				state.removeTokenAt(0);
				break;

			} else if (token instanceof Comma) {
				if (expectComma) {
					expectComma = false;
				} else {
					System.out.println(error.ERROR_UNEXPECTED_COMMA);
				}
				state.removeTokenAt(0);
			} else {
				error.expectToken(
						List.of(Ident.class, RParen.class, Comma.class),
						"parameter list",
						parserStates.PARSER_DECLARATION_STAGE
				);
			}
		}
		return params;
	}

	/*
	 * Statements
	 */

	/**
	 * Parses a statement.
	 *
	 * @return The parsed statement.
	 * <p>
	 * ***Grammar:***
	 * * [Statement] ->
	 * * * [If]
	 * * * [For]
	 * * * [ForEach]
	 * * * [While]
	 * * * [DoWhile]
	 * * * [Continue] ";"
	 * * * [Break] ";"
	 * * * [Switch]
	 * * * [Return] ";"
	 * * * [Goto] ";"
	 * * * [Try]
	 * * * [Throw] ";"
	 * * * [Label]
	 * * * [Assignment] ";"
	 * * * [Expression] ";"
	 * * * [Block]
	 */
	private Statement parseStatement() {
		Statement retNode;
		Token.Location loc = state.getTokenAt(0).getTokenLoc();
		if (state.getTokenAt(0) instanceof Ident tok) {
			switch (tok.getIdentifier()) {
				case "if" -> {
					state.removeTokenAt(0);
					state.setSemicolonExempt();
					return parseIf();
				}
				case "for" -> {
					state.removeTokenAt(0);
					state.setSemicolonExempt();
					return parseForLoop();
				}
				case "foreach" -> {
					state.removeTokenAt(0);
					state.setSemicolonExempt();
					return parseForEachLoop();
				}
				case "while" -> {
					state.removeTokenAt(0);
					state.setSemicolonExempt();
					return parseWhile();
				}
				case "do" -> {
					state.removeTokenAt(0);
					state.setSemicolonExempt();
					return parseDoWhile();
				}
				case "switch" -> {
					state.removeTokenAt(0);
					state.setSemicolonExempt();
					return parseSwitch();
				}
				case "try" -> {
					state.removeTokenAt(0);
					state.setSemicolonExempt();
					return parseTry();
				}

				case "continue" -> {
					state.removeTokenAt(0);
					retNode = new Continue(loc.line(), loc.character());
				}
				case "break" -> {
					state.removeTokenAt(0);
					retNode = new Break(loc.line(), loc.character());
				}
				case "return" -> {
					state.removeTokenAt(0);
					retNode = parseReturn();
				}
				case "goto" -> {
					state.removeTokenAt(0);
					retNode = parseGoto();
				}
				case "throw" -> {
					state.removeTokenAt(0);
					retNode = parseThrow();
				}

				default -> {
					if (state.getTokenAt(1) instanceof Colon) {
						state.setSemicolonExempt();
						return parseLabel();
					} else {
						Expression expr = parseExpression();
						if (state.isAssignment(state.getTokenAt(0))) {
							retNode = parseAssignment(expr);
						} else {
							retNode = expr;
						}
					}
				}
			}
		} else if (state.getTokenAt(0) instanceof LBrace) {
			retNode = parseBlock();
		} else {
			retNode = parseExpression();
		}
		error.expectToken(List.of(Semicolon.class), error.ERROR_MISSING_SEMICOLON, parserStates.PARSER_STATEMENT_STAGE);
		return retNode;
	}

	/**
	 * Parses an if statement.
	 *
	 * @return The parsed [If] statement.
	 * <p>
	 * ***Grammar:***
	 * * IfStatement ->
	 * * * "if" "(" Expression ")" Block
	 * * * "if" "(" Expression ")" Block "else" Block
	 */
	private Statement parseIf() {
		Token lparen = error.expectToken(
				List.of(LParen.class),
				error.ERROR_FUNCTION_PARAMS,
				parserStates.PARSER_EXPRESSION_STAGE
		);
		Expression conditional = parseExpression();
		if (conditional instanceof Op op) {
			Object opEnum = op.getOp();
			// This expression will not enter the kingdom of heaven.
			if (isaBooleanExpr(opEnum)) {
				// Good.
			} else {
				error.throwError(parserStates.PARSER_STATEMENT_STAGE, "Invalid conditional.");
			}
		} else if (conditional instanceof Bool) {
			System.out.println("WARNING: Conditional on line " + lparen.getTokenLoc().line() +
									   " always evaluates to " +
									   (((Bool) conditional).bool ? "true" : "false"));
		} else {
			error.throwError(parserStates.PARSER_STATEMENT_STAGE, "Invalid conditional.");
		}

		error.expectToken(List.of(RParen.class), error.ERROR_IF_CONDITIONAL, parserStates.PARSER_STATEMENT_STAGE);

		Block consequent = parseBlock();

		Block alternate = null;
		if (state.getTokenAt(0) instanceof Ident idt && idt.getIdentifier().equals("else")) {
			state.removeTokenAt(0);
			alternate = parseBlock();
		}

		return new If(conditional, consequent, alternate, lparen.getTokenLoc().line(), lparen.getTokenLoc().character());
	}

	/**
	 * Parses a block statement.
	 *
	 * @return The parsed [Block] statement.
	 * <p>
	 * ***Grammar:***
	 * * Block ->
	 * * * "{" (Declaration)* "}"
	 */
	private Block parseBlock() {
		Token tok = error.expectToken(
				List.of(LBrace.class),
				error.ERROR_BLOCK_EXPECTED,
				parserStates.PARSER_STATEMENT_STAGE
		);

		Token.Location loc = tok.getTokenLoc();

		List<Declaration> block = new ArrayList<>();
		while (!(tok instanceof RBrace)) {
			block.add(parseDeclaration());
			error.rejectTokens(
					List.of(EndOfFile.class),
					error.ERROR_UNTERMINATED_BLOCK,
					parserStates.PARSER_STATEMENT_STAGE
			);
			tok = state.getTokenAt(0);
		}

		error.expectToken(
				List.of(RBrace.class),
				error.ERROR_BLOCK_EXPECTED,
				parserStates.PARSER_STATEMENT_STAGE
		);

		return new Block(block, loc.line(), loc.character());
	}

	/**
	 * Parses a for loop statement.
	 *
	 * @return The parsed [For] loop statement.
	 * <p>
	 * ***Grammar:***
	 * * ForLoop ->
	 * * * "for" "(" [ Initializer ] [ Conditional ] [ Iteration ] ")" Block
	 * * Initializer ->
	 * * * e | Declaration | ";"
	 * * Conditional ->
	 * * * e | ExprStatement | ";"
	 * * Iteration ->
	 * * * e | Declaration | ";"
	 */
	private Statement parseForLoop() {
		Token tok = error.expectToken(
				List.of(LParen.class),
				error.ERROR_FOR_OPENING_PARENTHESIS,
				parserStates.PARSER_STATEMENT_STAGE
		);

		Token.Location loc = tok.getTokenLoc();

		Declaration initializer = null;
		Expression conditional = null;
		Declaration iteration = null;

		if (!(tok instanceof RParen)) {
			tok = state.getTokenAt(0);
			if (tok instanceof Semicolon) {
				state.removeTokenAt(0);
			} else {
				state.setSemicolonExempt();
				initializer = parseDeclaration();
			}
		}
		tok = state.getTokenAt(0);
		if (!(tok instanceof RParen)) {
			if (tok instanceof Semicolon) {
				state.removeTokenAt(0);
			} else {
				state.setSemicolonExempt();
				conditional = parseExpression();
				if (conditional instanceof Op op) {
					Object opEnum = op.getOp();
					// This expression will not enter the kingdom of heaven.
					if (isaBooleanExpr(opEnum)) {
						// Good.
					} else {
						error.throwError(parserStates.PARSER_STATEMENT_STAGE, "Invalid conditional.");
					}
				} else if (conditional instanceof Bool) {
					System.out.println("WARNING: Conditional on line " + loc.line() +
											   " always evaluates to " +
											   (((Bool) conditional).bool ? "true" : "false"));
				} else {
					error.throwError(parserStates.PARSER_STATEMENT_STAGE, "Invalid conditional.");
				}
			}
			error.expectToken(
					List.of(Semicolon.class),
					"For conditionals must be followed with a semicolon.",
					parserStates.PARSER_STATEMENT_STAGE
			);
		}
		tok = state.getTokenAt(0);
		if (!(tok instanceof RParen)) {
			state.setSemicolonExempt();
			iteration = parseDeclaration();
		}

		error.expectToken(
				List.of(RParen.class),
				error.ERROR_FOR_CLOSING_PARENTHESIS,
				parserStates.PARSER_STATEMENT_STAGE
		);

		Block block = parseBlock();

		return new For(initializer, conditional, iteration, block, loc.line(), loc.character());
	}

	private static boolean isaBooleanExpr(Object opEnum) {
		return opEnum.equals(BinaryOps.LessThan) || opEnum.equals(BinaryOps.LessEqual) ||
					   opEnum.equals(BinaryOps.EqualTo) || opEnum.equals(BinaryOps.GreaterEqual) ||
					   opEnum.equals(BinaryOps.NotEqualTo) || opEnum.equals(BinaryOps.GreaterThan) ||
					   opEnum.equals(BinaryOps.And) || opEnum.equals(BinaryOps.Or) ||
					   opEnum.equals(UnaryOps.Not);
	}

	/**
	 * Parses a foreach loop statement.
	 *
	 * @return The parsed [ForEach] loop statement.
	 * <p>
	 * ***Grammar:***
	 * * ForEachLoop ->
	 * * * "for" "(" Identifier ":" Identifier ")" Block
	 */
	private Statement parseForEachLoop() {
		Token tok = error.expectToken(
				List.of(LParen.class),
				error.ERROR_FOREACH_CONTROL_STRUCTURE,
				parserStates.PARSER_STATEMENT_STAGE
		);

		String itervar = error.expectIdentifierToken("an iteration variable", "foreach");

		error.expectToken(
				List.of(Colon.class),
				error.ERROR_FOREACH_LOOP_FORMAT,
				parserStates.PARSER_STATEMENT_STAGE
		);

		String collectionvar = error.expectIdentifierToken("a collection variable", "foreach");

		error.expectToken(
				List.of(RParen.class),
				error.ERROR_FOREACH_LOOP_TERMINATION,
				parserStates.PARSER_STATEMENT_STAGE
		);

		return new ForEach(itervar, collectionvar, parseBlock(), tok.getTokenLoc().line(), tok.getTokenLoc().character());
	}


	/**
	 * Parses a while loop statement.
	 *
	 * @return The parsed [While] loop statement.
	 * <p>
	 * ***Grammar:***
	 * * WhileLoop ->
	 * * * "while" "(" Expression ")" Block
	 */
	private Statement parseWhile() {
		Token tok = error.expectToken(
				List.of(LParen.class),
				error.ERROR_WHILE_CONDITIONAL,
				parserStates.PARSER_STATEMENT_STAGE
		);

		Token.Location loc = tok.getTokenLoc();

		Expression conditional = parseExpression();
		if (conditional instanceof Op op) {
			Object opEnum = op.getOp();
			// This expression will not enter the kingdom of heaven.
			if (isaBooleanExpr(opEnum)) {
				// Good.
			} else {
				error.throwError(parserStates.PARSER_STATEMENT_STAGE, "Invalid conditional.");
			}
		} else if (conditional instanceof Bool) {
			System.out.println("WARNING: Conditional on line " + loc.line() +
									   " always evaluates to " +
									   (((Bool) conditional).bool ? "true" : "false"));
		} else {
			error.throwError(parserStates.PARSER_STATEMENT_STAGE, "Invalid conditional.");
		}

		error.expectToken(
				List.of(RParen.class),
				error.ERROR_WHILE_UNCLOSED_PARENTHESIS,
				parserStates.PARSER_STATEMENT_STAGE
		);

		Block block = parseBlock();

		return new While(conditional, block, loc.line(), loc.character());
	}

	/**
	 * Parses a do-while loop statement.
	 *
	 * @return The parsed [DoWhile] loop statement.
	 * <p>
	 * ***Grammar:***
	 * * WhileLoop ->
	 * * * "do" Block "while" (" Expression ")" ";"
	 */
	private Statement parseDoWhile() {
		Block body = parseBlock();
		Ident tok = (Ident) error.expectToken(List.of(Ident.class), error.ERROR_DO_LOOP_TERMINATION, parserStates.PARSER_EXPRESSION_STAGE);
		error.expectIdent(tok.getIdentifier(), List.of("while"), error.ERROR_DO_LOOP_TERMINATION);
		Token.Location loc = tok.getTokenLoc();

		Token next = error.expectToken(
				List.of(LParen.class),
				error.ERROR_WHILE_CONDITIONAL,
				parserStates.PARSER_STATEMENT_STAGE
		);

		Expression conditional = parseExpression();
		if (conditional instanceof Op op) {
			Object opEnum = op.getOp();
			// This expression will not enter the kingdom of heaven.
			if (isaBooleanExpr(opEnum)) {
				// Good.
			} else {
				error.throwError(parserStates.PARSER_STATEMENT_STAGE, "Invalid conditional.");
			}
		} else if (conditional instanceof Bool) {
			System.out.println("WARNING: Conditional on line " + loc.line() +
									   " always evaluates to " +
									   (((Bool) conditional).bool ? "true" : "false"));
		} else {
			error.throwError(parserStates.PARSER_STATEMENT_STAGE, "Invalid conditional.");
		}

		error.expectToken(
				List.of(RParen.class),
				error.ERROR_WHILE_UNCLOSED_PARENTHESIS,
				parserStates.PARSER_STATEMENT_STAGE
		);

		error.expectToken(
				List.of(Semicolon.class),
				error.ERROR_DO_LOOP_END,
				parserStates.PARSER_STATEMENT_STAGE
		);
		return new DoWhile(conditional, body, loc.line(), loc.character());
	}

	/**
	 * Parses a switch statement
	 *
	 * @return The parsed [Switch] statement.
	 * <p>
	 * ***Grammar:***
	 * * SwitchStatement ->
	 * * * "switch" "(" Expression ")" CasesBlock
	 * * CasesBlock ->
	 * * * "{" (CaseBlock)* "}"
	 * * CaseBlock ->
	 * * * "case" Primary ":" Block |
	 * * * "default" ":" Block
	 */
	private Statement parseSwitch() {
		Token.Location loc = error.expectToken(
				List.of(LParen.class),
				error.ERROR_SWITCH_EXPRESSION,
				parserStates.PARSER_STATEMENT_STAGE
		).getTokenLoc();

		Expression conditional = parseExpression();

		error.expectToken(
				List.of(RParen.class),
				error.ERROR_UNCLOSED_EXPRESSION,
				parserStates.PARSER_STATEMENT_STAGE
		);

		error.expectToken(
				List.of(LBrace.class),
				error.ERROR_SWITCH_BLOCK,
				parserStates.PARSER_STATEMENT_STAGE
		);

		List<Entry<Expression, Statement>> cases = new ArrayList<>();
		while (!(state.getTokenAt(0) instanceof RBrace)) {
			Ident ident = (Ident) error.expectToken(
					List.of(Ident.class),
					error.ERROR_CASE_BLOCK_FORMAT,
					parserStates.PARSER_STATEMENT_STAGE
			);
			String identifier = ident.getIdentifier();
			Expression exp;
			Statement block;
			if (identifier.equals("default")) {
				error.expectToken(
						List.of(Colon.class),
						error.ERROR_CASE_EXPRESSION_COLON,
						parserStates.PARSER_STATEMENT_STAGE
				);
				exp = new Bool(true, loc.line(), loc.character());
				block = parseBlock();
			} else if (identifier.equals("case")) {
				Expression primary = parsePrimary();
				error.expectToken(
						List.of(Colon.class),
						error.ERROR_CASE_EXPRESSION_COLON,
						parserStates.PARSER_STATEMENT_STAGE
				);
				exp = primary;
				block = parseBlock();
			} else {
				error.throwError(parserStates.PARSER_STATEMENT_STAGE, "Invalid identifier provided to switch." +
																			  "Switch accepts default:'s and case [primitive]:'s only.");
				return null;
			}

			cases.add(new Entry<>(exp, block));

			error.rejectTokens(
					List.of(EndOfFile.class),
					error.ERROR_UNTERMINATED_SWITCH_BLOCK,
					parserStates.PARSER_STATEMENT_STAGE
			);
		}

		error.expectToken(
				List.of(RBrace.class),
				error.ERROR_SWITCH_BLOCK_END,
				parserStates.PARSER_STATEMENT_STAGE
		);

		return new Switch(conditional, cases, loc.line(), loc.character());
	}

	/**
	 * Parses an assignment
	 *
	 * @return The parsed [Assignment].
	 * <p>
	 * ***Grammar:***
	 * * AssignmentStatement ->
	 * * * Expression ("=" | "+=" | "-=" | "*=" | "/=" | "%=" | "**=" | "&=" | "|=" | "^=" | "<<=" | ">>=") Expression ";"
	 */

	private Statement parseAssignment(Expression lhs) {
		Token tok = state.getTokenAt(0);
		Token.Location loc = tok.getTokenLoc();
		state.removeTokenAt(0);
		if (state.isAssignment(tok) && !(tok instanceof Assign)) {
			if (tok instanceof AddAssign) {
				return new Assignment(lhs, new BinaryOp(lhs, BinaryOps.Add, parseExpression(), loc.line(), loc.character()), loc.line(), loc.character());
			} else if (tok instanceof SubAssign) {
				return new Assignment(lhs, new BinaryOp(lhs, BinaryOps.Sub, parseExpression(), loc.line(), loc.character()), loc.line(), loc.character());
			} else if (tok instanceof MulAssign) {
				return new Assignment(lhs, new BinaryOp(lhs, BinaryOps.Mul, parseExpression(), loc.line(), loc.character()), loc.line(), loc.character());
			} else if (tok instanceof DivAssign) {
				return new Assignment(lhs, new BinaryOp(lhs, BinaryOps.Div, parseExpression(), loc.line(), loc.character()), loc.line(), loc.character());
			} else if (tok instanceof ModAssign) {
				return new Assignment(lhs, new BinaryOp(lhs, BinaryOps.Mod, parseExpression(), loc.line(), loc.character()), loc.line(), loc.character());
			} else if (tok instanceof PowAssign) {
				return new Assignment(lhs, new BinaryOp(lhs, BinaryOps.Pow, parseExpression(), loc.line(), loc.character()), loc.line(), loc.character());
			} else if (tok instanceof AndAssign) {
				return new Assignment(lhs, new BinaryOp(lhs, BinaryOps.Bitwise_And, parseExpression(), loc.line(), loc.character()), loc.line(), loc.character());
			} else if (tok instanceof OrAssign) {
				return new Assignment(lhs, new BinaryOp(lhs, BinaryOps.Bitwise_Or, parseExpression(), loc.line(), loc.character()), loc.line(), loc.character());
			} else if (tok instanceof XorAssign) {
				return new Assignment(lhs, new BinaryOp(lhs, BinaryOps.Bitwise_Xor, parseExpression(), loc.line(), loc.character()), loc.line(), loc.character());
			} else if (tok instanceof LShiftAssign) {
				return new Assignment(lhs, new BinaryOp(lhs, BinaryOps.Bitwise_LS, parseExpression(), loc.line(), loc.character()), loc.line(), loc.character());
			} else if (tok instanceof RShiftAssign) {
				return new Assignment(lhs, new BinaryOp(lhs, BinaryOps.Bitwise_RS, parseExpression(), loc.line(), loc.character()), loc.line(), loc.character());
			} else {
				error.throwError(parserStates.PARSER_STATEMENT_STAGE, error.ERROR_ASSIGNMENT_MALFORMED, loc.line(), loc.character());
			}
		}
		return new Assignment(lhs, parseExpression(), loc.line(), loc.character());
	}

	/**
	 * Parses a label
	 *
	 * @return The parsed [Label].
	 * <p>
	 * ***Grammar:***
	 * * LabelStatement ->
	 * * * Identifier ":"
	 */
	private Statement parseLabel() {
		String label = error.expectIdentifierToken("a label", "label definition");
		Token.Location loc = error.expectToken(
				List.of(Colon.class),
				error.ERROR_INVALID_LABEL,
				parserStates.PARSER_EXPRESSION_STAGE
		).getTokenLoc();
		return new Label(label, loc.line(), loc.character());
	}

	/**
	 * Parses a return statement
	 *
	 * @return The parsed [Return] statement.
	 * <p>
	 * ***Grammar:***
	 * * ReturnStatement ->
	 * * * "return" [ ExpressionStatement ] ";"
	 */
	private Statement parseReturn() {
		Token.Location loc = state.getTokenAt(0).getTokenLoc();
		if (state.getTokenAt(0) instanceof Semicolon) {
			return new Return(null, loc.line(), loc.character());
		} else {
			Statement expr = new Return(parseExpression(), loc.line(), loc.character());
			return expr;
		}
	}

	/**
	 * Parses a goto statement
	 *
	 * @return The parsed [Goto] statement.
	 * <p>
	 * ***Grammar:***
	 * * GotoStatement ->
	 * * * "goto" Identifier ";"
	 */
	private Statement parseGoto() {
		Token.Location loc = state.getTokenAt(0).getTokenLoc();
		String label = error.expectIdentifierToken("a label", "goto");
		return new Goto(label, loc.line(), loc.character());
	}


	/**
	 * Parses a try statement
	 *
	 * @return The parsed [Try] statement.
	 * <p>
	 * ***Grammar:***
	 * * TryStatement ->
	 * * * "try" Block "catch" "(" Exception ")" Block
	 * * Exception ->
	 * * * ExceptionName ":" ExceptionType
	 * * ExceptionName ->
	 * * * Identifier
	 * * ExceptionType ->
	 * * * Identifier
	 */
	private Statement parseTry() {
		Statement block = parseBlock();

		Ident idt = (Ident) error.expectToken(List.of(Ident.class), "Unexpected token. Expecting ident 'catch'", parserStates.PARSER_EXPRESSION_STAGE);

		error.expectIdent(
				idt.getIdentifier(),
				List.of("catch"),
				error.ERROR_TRY_BLOCK_FORMAT);

		error.expectToken(
				List.of(LParen.class),
				error.ERROR_CATCH_PARENTHESIS,
				parserStates.PARSER_STATEMENT_STAGE
		);

		Token.Location loc = state.getTokenAt(0).getTokenLoc();

		String catches = error.expectIdentifierToken("an exception variable", "catch block");

		error.expectToken(
				List.of(Colon.class),
				error.ERROR_INVALID_CATCH_VARIABLE,
				parserStates.PARSER_STATEMENT_STAGE
		);

		String catchesAs = error.expectIdentifierToken("a type", "catch block");

		error.expectToken(
				List.of(RParen.class),
				error.ERROR_CATCH_TERMINATION,
				parserStates.PARSER_STATEMENT_STAGE
		);

		Statement catchBlock = parseBlock();

		return new Try(block, catches, catchesAs, catchBlock, loc.line(), loc.character());
	}

	/**
	 * Parses a throw statement
	 *
	 * @return The parsed [Throw] statement.
	 * <p>
	 * ***Grammar***
	 * * Throw ->
	 * * * "throw" ExceptionName Parameters ";"
	 * * ExceptionName ->
	 * * * Identifier
	 * * Parameters ->
	 * * * "(" ParameterList ")"
	 * * ParameterList ->
	 * * * Identifier |
	 * * * ParameterList "," Identifier
	 */
	private Statement parseThrow() {
		String throws_ = error.expectIdentifierToken("an exception", "throw statement");

		error.expectToken(
				List.of(LParen.class),
				error.ERROR_THROW_PARENTHESIS,
				parserStates.PARSER_STATEMENT_STAGE
		);

		List<Expression> params = new ArrayList<>();

		while (true) {
			Token tok = state.getTokenAt(0);
			if (tok instanceof RParen) {
				state.removeTokenAt(0);
				break;
			} else if (tok instanceof Comma) {
				state.removeTokenAt(0);
			} else if (tok instanceof EndOfFile || tok instanceof Semicolon) {
				error.throwError(parserStates.PARSER_EXPRESSION_STAGE, error.ERROR_INVALID_EXCEPTION_PARAMETERS);
			} else {
				params.add(parseExpression());
			}
		}

		Token.Location loc = state.getTokenAt(0).getTokenLoc();
		return new Throw(throws_, params, loc.line(), loc.character());
	}

	/*
	 * Expressions
	 */

	/**
	 * The entry point of the recursive descent parser.
	 * Starts parsing of the expression at the root level.
	 *
	 * @return An object of type [Expression] representing the parsed expression.
	 * <p>
	 * ***Grammar:***
	 * * Expression ->
	 * * * Lambda
	 */
	private Expression parseExpression() {
		return parseLambda();
	}

	/**
	 * Parses a [Lambda] expression.
	 * Lambda expressions should start with the keyword "lambda".
	 * If the first token is not "lambda", it continues parsing.
	 *
	 * @return An object of type [Expression] representing the parsed Lambda expression or a passthrough to Logical Or.
	 * <p>
	 * ***Grammar:***
	 * * Lambda ->
	 * * * LogicalOr  |
	 * * * "lambda" LambdaBody
	 */
	private Expression parseLambda() {
		Token next = state.getTokenAt(0);
		if (next instanceof Ident idt && idt.equals("lambda")) {
			state.removeTokenAt(0);
			Expression lambda = parseLambdaBody();
			state.setSemicolonExempt();
			return lambda;
		}
		return parseTernary();
	}

	/**
	 * Parses a lambda expression
	 * A lambda expression consists of parameters and a block of code.
	 *
	 * @return An object of type Lambda containing parameters and a block of code.
	 * <p>
	 * ***Grammar:***
	 * * LambdaBody ->
	 * * * Parameters Block
	 */
	private Expression parseLambdaBody() {
		Token next = state.getTokenAt(0);
		if (next instanceof EndOfFile) {
			error.throwError(parserStates.PARSER_EXPRESSION_STAGE, "lambda");
		}
		List<TypedVar> params = parseParams("lambda");
		error.expectToken(List.of(Colon.class), error.ERROR_MISSING_LAMBDA, parserStates.PARSER_EXPRESSION_STAGE);
		String type = error.expectIdentifierToken("a return type", "lambda statement");
		Block block = parseBlock();
		return new Lambda(params, block, type, next.getTokenLoc().line(), next.getTokenLoc().character());
	}

	/**
	 * Parses a ternary
	 * A ternary expression consists of a predicate followed by a consequent and alternate.
	 * An expression a ? b : c evaluates to b if the value of a is true, and otherwise to c.
	 *
	 * @return An object of type Expression containing a ternary.
	 * <p>
	 * ***Grammar:***
	 * * Ternary ->
	 * * * LogicalOr
	 * * * Expression "?" Expression ":" Expression
	 */
	private Expression parseTernary() {
		Token.Location loc = state.getTokenAt(0).getTokenLoc();
		Expression lhs = parseBinaryOpExpression(0);
		if (state.getTokenAt(0) instanceof QMark) {
			state.removeTokenAt(0);
			Expression consequent = parseBinaryOpExpression(0);
			error.expectToken(List.of(Colon.class), error.ERROR_TERNARY_COLON, parserStates.PARSER_EXPRESSION_STAGE);
			Expression alternate = parseBinaryOpExpression(0);
			return new Ternary(lhs, consequent, alternate, loc.line(), loc.character());
		}
		return lhs;
	}

	private Expression parseBinaryOpExpression(int precedence) {
		if (precTable.getTable(precedence).isEmpty()) {
			return parseUnary();
		}
		Expression lhs = parseBinaryOpExpression(precedence + 1);
		while (precTable.inTableAt(precedence, state.getTokenAt(0))) {
			Token.Location loc = state.getTokenAt(0).getTokenLoc();
			BinaryOps op = precTable.mapSymbolToOp(precedence, state.getTokenAt(0));
			state.removeTokenAt(0);
			Expression rhs = parseBinaryOpExpression(precedence + 1);
			lhs = new BinaryOp(lhs, op, rhs, loc.line(), loc.character());
		}
		return lhs;
	}

	/**
	 * Parses a unary expression.
	 *
	 * @return A [UnaryOp] expression representing the parsed unary expression or a passthrough to Logical Not.
	 * <p>
	 * ***Grammar:***
	 * * Unary ->
	 * * * "~" Unary |
	 * * * "-" Unary |
	 * * * LogicalNotExpression
	 */
	private Expression parseUnary() {
		Token token = state.getTokenAt(0);
		Token.Location loc = token.getTokenLoc();
		if (token instanceof Bitwise_Not) {
			state.removeTokenAt(0);
			return new UnaryOp(UnaryOps.BNot, parseUnary(), loc.line(), loc.character());
		} else if (token instanceof Sub) {
			state.removeTokenAt(0);
			return new UnaryOp(UnaryOps.Invert, parseUnary(), loc.line(), loc.character());
		} else {
			return parseLogicalNotExpression();
		}
	}

	/**
	 * Parses a logical not expression.
	 *
	 * @return A [UnaryOp] expression representing the parsed logical not expression or a passthrough to Modify.
	 * <p>
	 * ***Grammar:***
	 * * LogicalNotExpression ->
	 * * * "!" LogicalNotExpression |
	 * * * IncDec
	 */
	private Expression parseLogicalNotExpression() {
		Token token = state.getTokenAt(0);
		Token.Location loc = token.getTokenLoc();
		if (token instanceof Not) {
			state.removeTokenAt(0);
			return new UnaryOp(UnaryOps.Not, parseLogicalNotExpression(), loc.line(), loc.character());
		}
		return parseIncDec();
	}

	/**
	 * Parses an increment or decrement expression.
	 *
	 * @return A [Modify] expression representing the parsed increment or decrement expression or a passthrough to Call.
	 * <p>
	 * ***Grammar:***
	 * * IncDec ->
	 * * * "++" Call |
	 * * * "--" Call |
	 * * * Call "++" |
	 * * * Call "--" |
	 * * * Call
	 */
	private Expression parseIncDec() {
		Token token = state.getTokenAt(0);
		Token.Location loc = token.getTokenLoc();
		if (token instanceof Inc) {
			state.removeTokenAt(0);
			Expression result = parseCall();
			return new Modify(result, false, new IntegerNode(1, loc.line(), loc.character()), loc.line(), loc.character());
		} else if (token instanceof Dec) {
			state.removeTokenAt(0);
			Expression result = parseCall();
			return new Modify(result, false, new IntegerNode(-1, loc.line(), loc.character()), loc.line(), loc.character());
		} else {
			Expression result = parseCall();
			token = state.getTokenAt(0);
			loc = token.getTokenLoc();
			if (token instanceof Inc) {
				state.removeTokenAt(0);
				return new Modify(result, true, new IntegerNode(1, loc.line(), loc.character()), loc.line(), loc.character());
			} else if (token instanceof Dec) {
				state.removeTokenAt(0);
				return new Modify(result, true, new IntegerNode(-1, loc.line(), loc.character()), loc.line(), loc.character());
			} else {
				return result;
			}
		}
	}

	/**
	 * Parses a function call [Call], array access [ListAccess], variable access [VariableAccess], or field access [ScopeOf].
	 *
	 * @return An expression representing the parsed function call or a passthrough to Primary.
	 * <p>
	 * ***Grammar:***
	 * * CallExpression ->
	 * * * PrimaryExpression |
	 * * *  Ident "[" Expression "]" |
	 * * *  Ident "." Expression |
	 * * *  Ident "(" Parameters ")"
	 * * Parameters -> "(" ParameterList ")"
	 * * ParameterList ->
	 * * * Identifier |
	 * * *  ParameterList "," Identifier
	 */
	private Expression parseCall() {
		Token token = state.getTokenAt(0);
		Token.Location loc = token.getTokenLoc();

		if (token instanceof Ident idt) {
			state.removeTokenAt(0);
			String identifier = idt.getIdentifier();
			if (error.checkReserved(identifier)) {
				return parsePrimary();
			}

			token = state.getTokenAt(0);
			loc = token.getTokenLoc();
			if (token instanceof LBracket) {
				state.removeTokenAt(0);
				Expression idx = parseExpression();
				token = state.getTokenAt(0);
				if (!(token instanceof RBracket)) {
					error.throwError(parserStates.PARSER_EXPRESSION_STAGE, error.ERROR_LIST_CLOSING_BRACE);
				}
				state.removeTokenAt(0);
				return new ListAccess(identifier, idx, loc.line(), loc.character());
			} else if (token instanceof Dot) {
				state.removeTokenAt(0);
				Expression rhs = parseExpression();
				return new ScopeOf(identifier, rhs, loc.line(), loc.character());
			} else if (token instanceof LParen) {
				state.removeTokenAt(0);
				List<Expression> params = new ArrayList<>();

				while (true) {
					Token tok = state.getTokenAt(0);
					if (tok instanceof RParen) {
						state.removeTokenAt(0);
						break;
					} else if (tok instanceof Comma) {
						state.removeTokenAt(0);
					} else {
						error.rejectTokens(
								List.of(EndOfFile.class, Semicolon.class),
								"Invalid function call. Parameter list is not terminated.",
								parserStates.PARSER_EXPRESSION_STAGE
						);
						params.add(parseExpression());
					}
				}
				// Return the Call expression with identifier and parameters
				return new Call(identifier, params, loc.line(), loc.character());
			} else {
				return new VariableAccess(identifier, loc.line(), loc.character());
			}
		}

		return parsePrimary();
	}

	/**
	 * Parses a primary expression.
	 *
	 * @return An expression representing the parsed primary expression.
	 * <p>
	 * ***Grammar:***
	 * * PrimaryExpression ->
	 * * * [IntegerNode] |
	 * * * [Floating] |
	 * * * [Bool] |
	 * * * [StringTok] |
	 * * * [CharTok] |
	 * * * [reserved]Ident |
	 * * * "(" Expression ")"
	 */
	private Expression parsePrimary() {
		Token tok = state.getTokenAt(0);
		Token.Location loc = tok.getTokenLoc();

		if (tok instanceof NumbTok num) {
			if (num.isInt()) {
				state.removeTokenAt(0);
				return new IntegerNode(num.getInteger(), loc.line(), loc.character());
			} else {
				state.removeTokenAt(0);
				return new Floating(num.getFloating(), loc.line(), loc.character());
			}
		} else if (tok instanceof StringTok str) {
			state.removeTokenAt(0);
			return new StringLit(str.getStr(), loc.line(), loc.character());
		} else if (tok instanceof CharTok chtok) {
			state.removeTokenAt(0);
			return new CharNode(chtok.getCharacterLit(), loc.line(), loc.character());
		} else if (tok instanceof Ident idt) {
			String identifier = idt.toString();
			if (identifier.equals("true")) {
				state.removeTokenAt(0);
				return new Bool(true, loc.line(), loc.character());
			} else if (identifier.equals("false")) {
				state.removeTokenAt(0);
				return new Bool(false, loc.line(), loc.character());
			} else if (identifier.equals("nil")) {
				// TODO: This will be useful for objects at some point
				// Implement the necessary logic
			} else {
				error.throwError(parserStates.PARSER_EXPRESSION_STAGE, "Bad primitive: " + identifier);
			}
		} else if (tok instanceof LParen) {
			state.removeTokenAt(0);
			Expression parenthetical = parseExpression();
			error.expectToken(
					List.of(RParen.class),
					"Error, unterminated parenthetical.",
					parserStates.PARSER_EXPRESSION_STAGE
			);
			return parenthetical;
		}

		error.throwError(parserStates.PARSER_EXPRESSION_STAGE, "Unexpected Token: " + tok + " Expected one of: [integer, float, string literal, parenthesis, boolean, ident]");
		return null; // Dummy return statement, unreachable code
	}

	/*
	 * Public Interfaces
	 */
	public List<Declaration> parseProgram() {
		ArrayList<Declaration> decls = new ArrayList<>();
		ArrayList<String> errors = new ArrayList<>();
		Declaration decl = null;
		int errorNum = 0;
		while (!state.isEmpty() && !(state.getTokenAt(0) instanceof EndOfFile)) {
			try {
				decl = parseDeclaration();
			} catch (ParserException e) {
				errorNum++;
				errors.add(e.getMessage());
				decls = error.synchronize();
			} finally {
				state.appendDeclaration(decl);
				decls.add(decl);
			}
		}
		String errorStr = "Had " + errorNum + " Errors\n" + String.join("\n", errors);
		if (errorNum > 0) {
			System.out.println(errorStr);
		}
		return decls;
	}
}

