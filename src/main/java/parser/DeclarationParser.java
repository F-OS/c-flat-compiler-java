/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import AST.ASTRoot.*;
import AST.*;
import AST.Declarations.*;
import AST.Declarations.EnumDeclaration.*;
import scanner.*;
import scanner.Token.*;
import utils.*;

import java.util.*;

public final class DeclarationParser extends Parser {

	private DeclarationParser(List<Token> tokenStream) {
		super(tokenStream);
	}

	private DeclarationParser(ParsingContext parser) {
		super(parser);
	}

	/**
	 * The entry point of the recursive descent parser for declarations. Starts parsing of the
	 * declaration at the root level. Initalizes the expression parser object as
	 * well. Static because it's helpful for use in other parser types.
	 *
	 * @return An object of type [Expression] representing the parsed expression.
	 * <p>
	 * ***Grammar:*** * Expression -> * * Lambda
	 */
	public static Declaration parseDeclaration(ParsingContext cont) {
		DeclarationParser parser = new DeclarationParser(cont);
		return parser.parseDeclaration();
	}


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
		Declaration retNode;
		switch (getCurrentToken().type) {
			case VAR -> {
				consumeToken();
				retNode = parseVarDeclaration();
			}
			case ARRAY -> {
				consumeToken();
				retNode = parseArrayDeclaration();
			}
			case ENUM -> {
				consumeToken();
				retNode = parseEnumDeclaration();
			}
			case CLASS -> {
				consumeToken();
				retNode = parseClassDeclaration();
			}
			case FUN -> {
				consumeToken();
				retNode = parseFunDeclaration();
			}
			case STRUCT -> {
				consumeToken();
				retNode = parseStructDeclaration();
			}
			default -> {
				return StatementParser.parseStatement(context);
			}
		}
		return retNode;
	}

	/**
	 * Parses a var.
	 *
	 * @return The parsed declaration.
	 * <p>
	 * ***Grammar:***
	 * * [SimpleVarDeclaration] -> "var" Name ":" Type ["=" [Expression]] ";"
	 * <p>
	 * * * Name -> [Ident]ifier
	 * <p>
	 * * * Type -> [Ident]ifier
	 */
	private Declaration parseVarDeclaration() {
		Entry<Integer, Integer> loc = getCurrentLocation();

		String ident = matchIdent("Expected a variable name after 'var'.");
		String type;
		if (!curTokenIsType(TokenType.COLON)) {
			System.out.println("WARNING: Variable not typed at line " + loc.key() + ". Type will be "
							   + "inferred based on the variable's use.");
			type = "!!INFER!!";
		} else {
			consumeToken();
			type = matchIdent("A colon must be followed by a type identifier.");
		}

		TypedVar typedVar = new TypedVar(ident, type);

		if (getCurrentToken().type == TokenType.EQUATE) {
			consumeToken();
			Expression expr = ExpressionParser.parseExpression(context);
			match(TokenType.SEMICOLON, "Expected semicolon after definition.");
			return new SimpleVarDeclaration(typedVar, expr, loc);
		}

		if (getCurrentToken().type == TokenType.SEMICOLON) {
			consumeToken();
			return new SimpleVarDeclaration(typedVar, null, loc);
		} else {
			throw new RuntimeException("Unterminated declaration starting at line: " + loc.key());
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
		Entry<Integer, Integer> loc = getCurrentLocation();

		String ident = matchIdent("Expected an array name.");

		long arraylen = -1;

		if (getCurrentToken().type == TokenType.LBRACKET) {
			consumeToken();
			arraylen = Integer.parseInt(match(TokenType.INTCONST, "Array lengths must be whole integers.").text);
			match(TokenType.RBRACKET, "Array lengths must end with right brackets.");
		}

		String type;
		if (!curTokenIsType(TokenType.COLON)) {
			System.out.println("WARNING: Variable not typed at line " + loc.key() + ". Type will be "
							   + "inferred based on the variable's use.");
			type = "!!INFER!!";
		} else {
			consumeToken();
			type = matchIdent("A colon must be followed by a type identifier.");
		}

		Declaration decl;
		if (getCurrentToken().type == TokenType.EQUATE) {
			consumeToken();
			List<Expression> initalizers = parseArrayInitalizers();
			TypedVar typedVar = new TypedVar(ident, type);
			decl = new ArrayDeclaration(typedVar, arraylen, initalizers, loc);
		} else {
			TypedVar typedVar = new TypedVar(ident, type);
			decl = new ArrayDeclaration(typedVar, arraylen, null, loc);
		}

		if (getCurrentToken().type == TokenType.SEMICOLON) {
			consumeToken();
			return decl;
		} else {
			throw new RuntimeException("Unterminated declaration starting at line: " + loc.key());
		}
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
		match(TokenType.LBRACE, "Array initializers must start with braces.");
		while (true) {
			initalizerList.add(ExpressionParser.parseExpression(context));
			if (getCurrentToken().type == TokenType.RBRACE) {
				break;
			} else if (getCurrentToken().type == TokenType.COMMA) {
				consumeToken();
			} else {
				throw new RuntimeException("Error: Unexpected token in array initializer list.");
			}
		}
		match(TokenType.RBRACE, "Array initializers must end with braces.");
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
		Entry<Integer, Integer> loc = getCurrentLocation();
		String name = matchIdent("Expected an enum name.");

		match(TokenType.LBRACE, "Error: An enum declaration must contain an identifier followed by a series of " +
								"mappings enclosed in left braces.");

		ArrayList<EnumMember> members = new ArrayList<>();
		long enumNumber = 0L;

		while (true) {
			if (getCurrentToken().type == TokenType.IDENTIFIER) {
				String paramName = matchIdent("Expected an enum member.");
				if (getCurrentToken().type == TokenType.COLON) {
					long num = Integer.parseInt(match(TokenType.INTCONST, "Error: Must define an enum entry as " +
																		  "a number.").text);

					members.add(new EnumMember(paramName, num));
					enumNumber = num;
				} else if (getCurrentToken().type == TokenType.COMMA) {
					consumeToken();
					members.add(new EnumMember(paramName, enumNumber));
					enumNumber++;
				} else {
					throw new RuntimeException("Error on line " + loc.key() + ": Error in enum parsing. Expected " +
											   "either an identifier or colon.");
				}
			} else if (getCurrentToken().type == TokenType.RBRACE) {
				consumeToken();
				if (members.isEmpty()) {
					System.out.println(
							"WARNING: Empty enum definition at line " + getCurrentLocation().key() + ".");
				}
				break;
			} else {
				throw new RuntimeException("Error on line " + loc.key() + ": Error in enum parsing. Expected " +
										   "either a right brace or an identifier.");
			}
		}

		return new EnumDeclaration(name, members, loc);
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
		Entry<Integer, Integer> loc = getCurrentLocation();
		String name = matchIdent("Expected a struct name.");
		List<Declaration> body = parseDeclarationBlock("structs");
		return new StructDeclaration(name, body, loc);
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
		Entry<Integer, Integer> loc = getCurrentLocation();
		String name = matchIdent("Expected a class name.");
		List<String> inheritsFrom = new ArrayList<>();

		if (getCurrentToken().type != TokenType.COLON) {
			List<Declaration> body = parseDeclarationBlock("classes");
			return new ClassDeclaration(name, body, inheritsFrom, loc);
		}
		consumeToken();

		if (getCurrentToken().type == TokenType.LPAREN) {
			consumeToken();
			while (true) {
				if (getCurrentToken().type == TokenType.IDENTIFIER) {
					inheritsFrom.add(matchIdent("Expected a class to inherit from."));
				} else if (getCurrentToken().type == TokenType.RPAREN) {
					consumeToken();
					break;
				} else if (getCurrentToken().type == TokenType.COMMA) {
					consumeToken();
					// Commas are allowed between inherited classes
				} else {
					throw new RuntimeException("Error on line " + loc.key() + ": Error in class parsing. Expected" +
											   " " +
											   "either a right parenthesis, a comma, or an identifier in " +
											   "inheritance list. Got a " + getCurrentToken().type +
											   ".");
				}
			}
		} else {
			inheritsFrom.add(matchIdent("Expected a class to inherit from."));
		}

		List<Declaration> body = parseDeclarationBlock("classes");
		return new ClassDeclaration(name, body, inheritsFrom, loc);
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
		ArrayList<Declaration> list = new ArrayList<>();
		match(TokenType.LBRACE, "The bodies of " + blockType + " must be braced.");
		while ((getCurrentToken().type != TokenType.RBRACE && getCurrentToken().type != TokenType.EOF)) {
			list.add(parseDeclaration());
		}
		match(TokenType.RBRACE, "The bodies of " + blockType + " must be braced.");
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
		Entry<Integer, Integer> loc = getCurrentLocation();
		String name = matchIdent("Expected a function name.");
		// Hacky fix.
		if ("fun".equals(name)) {
			throw new RuntimeException("Error on line " + loc.key() + ". Either you are trying to define a function " +
									   "named fun or the parser was unable to synchronize properly. " +
									   "In either case stop it.");
		}
		List<TypedVar> params = parseParams(name);
		String type;
		if (!curTokenIsType(TokenType.COLON)) {
			System.out.println("WARNING: Function return not typed on line " + loc.key() + ". Void assumed.");
			type = "void";
		} else {
			consumeToken();
			type = matchIdent("A colon must be followed by a type identifier.");
		}
		Statement body = parseBlock();
		return new FunctionDeclaration(name, params, type, body, loc);
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
	private List<TypedVar> parseParams(String func) {
		match(TokenType.LPAREN, "Error: In " + func + ". The parameter list must be enclosed in parentheses.");

		List<TypedVar> params = new ArrayList<>(32);
		boolean expectComma = false;
		boolean allowEmpty = false;
		Token curTok;
		while (true) {
			if (curTokenIsType(TokenType.IDENTIFIER)) {
				if (expectComma) {
					curTok = getCurrentToken();
					System.out.println("WARNING: Expected a comma at identifier in parameter definition for function " + func + " on line "
									   + curTok.line + ", character " +
									   curTok.charNum
									   + ".");
				}
				expectComma = true;
				String name = matchIdent("Expected a parameter.");
				match(TokenType.COLON, "A parameter definition must contain an identifier followed by a "
									   + "colon and then a typename.");
				String type = matchIdent("Expected a typename after colon in function params.");
				params.add(new TypedVar(name, type));
			} else if (curTokenIsType(TokenType.RPAREN)) {
				curTok = consumeToken();
				break;
			} else if (curTokenIsType(TokenType.COMMA)) {
				if (!expectComma) {
					curTok = getCurrentToken();
					System.out.println("WARNING: Unexpected comma in parameter definition for function " + func + " on line "
									   + curTok.line + ", character " + curTok.charNum + ".");
				}
				expectComma = false;
				consumeToken();
			} else if (curTokenIsType(TokenType.NOT)) {
				consumeToken();
				allowEmpty = true;
			} else {
				matchList(List.of(TokenType.IDENTIFIER, TokenType.RPAREN, TokenType.COMMA),
						"Invalid parameter list.");
			}
		}
		if (params.isEmpty() && !allowEmpty) {
			System.out.println("WARNING: Empty parameter list in parameter definition for function " + func + " on line "
							   + curTok.line + ", character " + curTok.charNum + ". insert an exclamation mark to allow an empty parameter list." +
							   "\n\t\"fun " + func + "()\" -> \"fun " + func + "(!)\"");
		}
		return params;
	}
}