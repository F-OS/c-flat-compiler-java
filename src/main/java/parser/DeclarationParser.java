/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import java.util.ArrayList;
import java.util.List;

import AST.ASTRoot;
import AST.Declaration;
import AST.Expression;
import AST.Statement;
import AST.Declarations.*;
import AST.Statements.*;
import scanner.Token;
import utils.Entry;

public final class DeclarationParser extends ParserState {

	private DeclarationParser(List<Token> tokenStream) {
		super(tokenStream);
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
	public static Entry<Declaration, Integer> parseDeclaration(List<Token> tokenStream) {
		DeclarationParser parser = new DeclarationParser(tokenStream);
		Declaration parsed = parser.parseDeclaration();
		return new Entry<>(parsed, parser.getCurrentPosition());
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
		Declaration retNode = null;
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
				return parseStatement();
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
		if (!curTokenIsType(Token.TokenType.COLON)) {
			System.out.println("WARNING: Variable not typed at line " + loc.key() + "Type will be "
							   + "inferred based on the variable's use.");
			type = "!!INFER!!";
		} else {
			consumeToken();
			type = matchIdent("A colon must be followed by a type identifier.");
		}

		ASTRoot.TypedVar typedVar = new ASTRoot.TypedVar(ident, type);

		if (getCurrentToken().type == Token.TokenType.EQUATE) {
			consumeToken();
			Expression expr = parseExpression();
			match(Token.TokenType.EQUATE, "Expected semicolon after definition.");
			return new SimpleVarDeclaration(typedVar, expr, loc);
		}

		if (getCurrentToken().type == Token.TokenType.SEMICOLON) {
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

		if (getCurrentToken().type == Token.TokenType.LBRACKET) {
			consumeToken();
			arraylen = Integer.parseInt(match(Token.TokenType.INTCONST, "Array lengths must be whole integers.").text);
			match(Token.TokenType.INTCONST, "Array lengths must end with right brackets.");
		}

		String type;
		if (!curTokenIsType(Token.TokenType.COLON)) {
			System.out.println("WARNING: Variable not typed at line " + loc.key() + "Type will be "
							   + "inferred based on the variable's use.");
			type = "!!INFER!!";
		} else {
			consumeToken();
			type = matchIdent("A colon must be followed by a type identifier.");
		}

		Declaration decl;
		if (getCurrentToken().type == Token.TokenType.EQUATE) {
			List<Expression> initalizers = parseArrayInitalizers();
			ASTRoot.TypedVar typedVar = new ASTRoot.TypedVar(ident, type);
			decl = new ArrayDeclaration(typedVar, arraylen, initalizers, loc);
		} else {
			ASTRoot.TypedVar typedVar = new ASTRoot.TypedVar(ident, type);
			decl = new ArrayDeclaration(typedVar, arraylen, null, loc);
		}

		if (getCurrentToken().type == Token.TokenType.SEMICOLON) {
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
		while (true) {
			consumeToken();
			initalizerList.add(parseExpression());
			if (getCurrentToken().type == Token.TokenType.RBRACE) {
				consumeToken();
				break;
			} else if (getCurrentToken().type == Token.TokenType.COMMA) {
				consumeToken();
			} else {
				throw new RuntimeException("Error: Unexpected token in array initializer list.");
			}
		}
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

		match(Token.TokenType.LBRACE, "Error: An enum declaration must contain an identifier followed by a series of " +
									  "mappings enclosed in left braces.");

		ArrayList<EnumDeclaration.EnumMember> members = new ArrayList<>();
		long enumNumber = 0L;

		while (true) {
			if (getCurrentToken().type == Token.TokenType.IDENTIFIER) {
				String paramName = getTokenText();
				consumeToken();

				if (getCurrentToken().type == Token.TokenType.COLON) {
					long num = Integer.parseInt(match(Token.TokenType.INTCONST, "Error: Must define an enum entry as " +
																				"a number.").text);

					members.add(new EnumDeclaration.EnumMember(paramName, num));
					enumNumber = num;
				} else if (getCurrentToken().type == Token.TokenType.COMMA) {
					consumeToken();
					members.add(new EnumDeclaration.EnumMember(paramName, enumNumber));
					enumNumber++;
				} else {
					throw new RuntimeException("Error on line " + loc.key() + ": Error in enum parsing. Expected " +
											   "either an identifier or colon.");
				}
			} else if (getCurrentToken().type == Token.TokenType.RBRACE) {
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
		List<Declaration> body = parseDeclarationBlock("struct");
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

		if (getCurrentToken().type == Token.TokenType.COLON) {
			consumeToken();

			if (getCurrentToken().type == Token.TokenType.LPAREN) {
				while (true) {
					if (getCurrentToken().type == Token.TokenType.IDENTIFIER) {
						inheritsFrom.add(getTokenText());
						consumeToken();
					} else if (getCurrentToken().type == Token.TokenType.RPAREN) {
						consumeToken();
						break;
					} else if (getCurrentToken().type == Token.TokenType.COMMA) {
						consumeToken();
						// Commas are allowed between inherited classes
					} else {
						throw new RuntimeException("Error on line " + loc.key() + ": Error in class parsing. Expected" +
												   " " +
												   "either a right parenthesis, a comma, or an identifier in " +
												   "inheritance list." +
												   ".");
					}
				}
			} else {
				inheritsFrom.add(getTokenText());
				consumeToken();
			}
		}

		List<Declaration> body = parseDeclarationBlock("class");
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
		Token loc = match(Token.TokenType.LBRACE, "The body of " + blockType + " must be braced.");
		ArrayList<Declaration> list = new ArrayList<>();
		while (!(getCurrentToken().type != Token.TokenType.RBRACE || getCurrentToken().type != Token.TokenType.EOF)) {
			list.add(parseDeclaration());
		}
		match(Token.TokenType.RBRACE, "The body of " + blockType + " must be braced.");
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
		List<ASTRoot.TypedVar> params = parseParams("function");
		String type;
		if (!curTokenIsType(Token.TokenType.COLON)) {
			System.out.println("WARNING: Variable not typed at line " + loc.key() + "Type will be "
							   + "inferred based on the variable's use.");
			type = "!!INFER!!";
		} else {
			consumeToken();
			type = matchIdent("A colon must be followed by a type identifier.");
		}
		Statement body = parseBlock();
		return new FunctionDeclaration(name, params, type, body, loc);
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
	private Statement parseBlock() {
		Token tok = match(Token.TokenType.LBRACE, "Blocks must start with braces.");

		List<Declaration> block = new ArrayList<>(64);
		while (getCurrentToken().type != Token.TokenType.RBRACE) {
			block.add(parseDeclaration());
			if (curTokenIsType(Token.TokenType.EOF)) {
				throw new RuntimeException("Unterminated block starting at line: " + tok.line);
			}
		}
		match(Token.TokenType.RBRACE, "Blocks must end with braces.");

		return new Block(block, new Entry<>(tok.line, tok.charNum));
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
	private List<ASTRoot.TypedVar> parseParams(String statementType) {
		match(Token.TokenType.LPAREN, "Error: A function parameter list must be enclosed in parentheses.");

		List<ASTRoot.TypedVar> params = new ArrayList<>(32);
		boolean expectComma = false;
		Token curTok;
		while (true) {
			if (curTokenIsType(Token.TokenType.IDENTIFIER)) {
				if (expectComma) {
					curTok = getCurrentToken();
					System.out.println("WARNING: Expected a comma at identifier " + curTok.text
									   + " in parameter definition on line " + curTok.line + ", character" +
									   curTok.charNum
									   + " .");
				}
				expectComma = true;
				String name = getTokenText();
				match(Token.TokenType.COLON, "A parameter definition must contain an identifier followed by a "
											 + "colon and then a typename.");
				String type = matchIdent("Expected a typename after colon in function params.");
				params.add(new ASTRoot.TypedVar(name, type));
			} else if (curTokenIsType(Token.TokenType.RPAREN)) {
				curTok = consumeToken();
				break;
			} else if (curTokenIsType(Token.TokenType.COMMA)) {
				if (!expectComma) {
					curTok = getCurrentToken();
					System.out.println("WARNING: Unexpected comma " + curTok.text + " in parameter definition on line "
									   + curTok.line + ", character" + curTok.charNum + " .");
				}
				expectComma = false;
				consumeToken();
			} else {
				matchList(List.of(Token.TokenType.IDENTIFIER, Token.TokenType.RPAREN, Token.TokenType.COMMA),
						"Invalid parameter list.");
			}
		}
		if (params.isEmpty()) {
			System.out.println("WARNING: Empty parameter list " + curTok.text + " in parameter definition on line "
							   + curTok.line + ", character" + curTok.charNum + " .");
		}
		return params;
	}


	private Statement parseStatement() {
		Entry<Statement, Integer> statement = StatementParser
													  .parseStatement(getTokenStream().subList(getCurrentPosition(),
															  getTokenStream().size()));
		advanceLocation(statement.value());
		return statement.key();
	}

	private Expression parseExpression() {
		Entry<Expression, Integer> expression = ExpressionParser
														.parseExpression(getTokenStream().subList(getCurrentPosition(), getTokenStream().size()));
		advanceLocation(expression.value());
		return expression.key();
	}
}