/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import AST.ASTRoot.*;
import AST.*;
import AST.Expressions.*;
import AST.Expressions.OpEnums.*;
import scanner.*;
import scanner.Token.*;
import utils.*;

import java.util.*;

public final class ExpressionParser extends Parser {

	private ExpressionParser(List<Token> tokenStream) {
		super(tokenStream);
	}

	public ExpressionParser(ParsingContext context) {
		super(context);
	}


	/**
	 * The entry point of the recursive descent parser. Starts parsing of the
	 * expression at the root level. Initalizes the expression parser object as
	 * well. Static because it's helpful for use in other parser types.
	 *
	 * @return An object of type [Expression] representing the parsed expression.
	 * <p>
	 * ***Grammar:***
	 * * Expression ->
	 * * * Lambda
	 */
	public static Expression parseExpression(ParsingContext context) {
		ExpressionParser parser = new ExpressionParser(context);
		return parser.parseExpression();
	}


	public Expression parseExpression() {
		return parseLambda();
	}

	/**
	 * Parses a [Lambda] expression. Lambda expressions should start with the
	 * keyword "lambda". If the first token is not "lambda", it continues parsing.
	 *
	 * @return An object of type [Expression] representing the parsed Lambda
	 * expression or a passthrough to Logical Or.
	 * <p>
	 * ***Grammar:***
	 * * Lambda ->
	 * * * LogicalOr  |
	 * * * "lambda" LambdaBody
	 * * LambdaBody ->
	 * * * Parameters ":" ReturnType Block
	 */
	private Expression parseLambda() {
		if (curTokenIsType(TokenType.LAMBDA)) {
			consumeToken();
			List<TypedVar> params = parseLambdaParams();
			match(TokenType.COLON, "Lambdas require a colon and a return type.");
			String type = matchIdent("Expected an identifier for the lambda's return type.");
			Statement stmt = StatementParser.parseStatement(context);
			Lambda lambda = new Lambda(params, stmt, type, getCurrentLocation());
			context.setSemicolonExempt();
			return lambda;
		}
		return parseTernary();
	}

	/**
	 * Parses the parameters of a function. A parameter definition must contain a
	 * left paren '(' followed by a list of parameters enclosed in parentheses. Each
	 * parameter is a typed Identifier.
	 *
	 * @return A list of strings representing the parsed parameters.
	 * <p>
	 * ***Grammar:***
	 * * Parameters -> "(" ParameterList ")"
	 * * ParameterList ->
	 * * * TypedIdentifier |
	 * * * ParameterList "," TypedIdentifier
	 */
	private List<TypedVar> parseLambdaParams() {
		match(TokenType.LPAREN, "The parameters of a lambda expression must start with a left parenthesis.");
		List<TypedVar> params = new ArrayList<>(32);
		boolean expectComma = false;
		Token curTok;
		while (true) {
			if (curTokenIsType(TokenType.IDENTIFIER)) {
				if (expectComma) {
					curTok = getCurrentToken();
					System.out.println("WARNING: Expected a comma at identifier " + curTok.text
									   + " in parameter definition on line " + curTok.line + ", character" +
									   curTok.charNum
									   + " .");
				}
				expectComma = true;
				String name = consumeToken().text;
				match(TokenType.COLON, "A parameter definition must contain an identifier followed by a "
									   + "colon and then a typename.");
				String type = matchIdent("Expected a typename after lambda params.");
				params.add(new TypedVar(name, type));
			} else if (curTokenIsType(TokenType.RPAREN)) {
				curTok = consumeToken();
				break;
			} else if (curTokenIsType(TokenType.COMMA)) {
				if (!expectComma) {
					curTok = getCurrentToken();
					System.out.println("WARNING: Unexpected comma " + curTok.text + " in parameter definition on line "
									   + curTok.line + ", character" + curTok.charNum + " .");
				}
				expectComma = false;
				consumeToken();
			} else {
				matchList(List.of(TokenType.IDENTIFIER, TokenType.RPAREN, TokenType.COMMA),
						"Invalid parameter list.");
			}
		}
		if (params.isEmpty()) {
			System.out.println("WARNING: Empty parameter list " + curTok.text + " in parameter definition on line "
							   + curTok.line + ", character" + curTok.charNum + " .");
		}
		return params;
	}

	/**
	 * Parses a ternary. A ternary expression consists of a predicate followed by a
	 * consequent and alternate. An expression a ? b : c evaluates to b if the value
	 * of a is true, and otherwise to c.
	 *
	 * @return An object of type Expression containing a ternary.
	 * <p>
	 * ***Grammar:***
	 * * Ternary ->
	 * * * LogicalOr
	 * * * Expression "?" Expression ":" Expression
	 */
	private Expression parseTernary() {
		Entry<Integer, Integer> loc = getCurrentLocation();
		Expression lhs = parseBinaryOpExpression(0);
		if (!curTokenIsType(TokenType.QMARK)) {
			return lhs;
		}
		consumeToken();
		Expression consequent = parseBinaryOpExpression(0);
		match(TokenType.COLON, "Ternaries require alternate expressions of the form pred ? cons : alt");
		Expression alternate = parseBinaryOpExpression(0);
		return new Ternary(lhs, consequent, alternate, loc);
	}

	/**
	 * Parses an expression of the form expr op expr. Takes a precedence parameter
	 * to determine expression precedence.
	 *
	 * @param precedence The current precedence for the operator.
	 * @return An object of type Expression containing a binary expression.
	 */
	private Expression parseBinaryOpExpression(int precedence) {
		if (!OpPrecTable.hasTable(precedence)) {
			return parseUnary();
		}
		Expression lhs = parseBinaryOpExpression(precedence + 1);
		while (OpPrecTable.inTableAt(precedence, getCurrentToken())) {
			Entry<Integer, Integer> loc = getCurrentLocation();
			BinaryOps op = OpPrecTable.mapSymbolToOp(precedence, consumeToken());
			//System.out.println("Parsing: " + op + " at precedence: " + precedence);
			Expression rhs = parseBinaryOpExpression(precedence + 1);
			lhs = new BinaryOp(lhs, op, rhs, loc);
		}

		return lhs;
	}

	/**
	 * Parses a unary expression.
	 *
	 * @return A [UnaryOp] expression representing the parsed unary expression or a
	 * passthrough to Logical Not.
	 * <p>
	 * ***Grammar:***
	 * * Unary ->
	 * * * "~" Unary |
	 * * * "-" Unary |
	 * * * LogicalNotExpression
	 */
	private Expression parseUnary() {
		Entry<Integer, Integer> loc = getCurrentLocation();
		TokenType tokenType = getCurrentToken().type;
		return switch (tokenType) {
			case BITWISE_NOT -> {
				consumeToken();
				yield new UnaryOp(UnaryOps.BNot, parseUnary(), loc);
			}
			case SUB -> {
				consumeToken();
				yield new UnaryOp(UnaryOps.Invert, parseUnary(), loc);
			}
			default -> parseLogicalNotExpression();
		};
	}

	/**
	 * Parses a logical not expression.
	 *
	 * @return A [UnaryOp] expression representing the parsed logical not expression
	 * or a passthrough to Modify.
	 * <p>
	 * ***Grammar:***
	 * * LogicalNotExpression ->
	 * * * "!" LogicalNotExpression |
	 * * * IncDec
	 */
	private Expression parseLogicalNotExpression() {
		Entry<Integer, Integer> loc = getCurrentLocation();
		if (curTokenIsType(TokenType.NOT)) {
			consumeToken();
			return new UnaryOp(UnaryOps.Not, parseLogicalNotExpression(), loc);
		}
		return parseIncDec();
	}

	/**
	 * Parses an increment or decrement expression.
	 *
	 * @return A [Modify] expression representing the parsed increment or decrement
	 * expression or a passthrough to Call.
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
		int modifyby;
		boolean returnPrevious = false;
		Expression result;
		if (curTokenIsType(TokenType.INC)) {
			consumeToken();
			result = parseCall();
			modifyby = 1;
		} else if (curTokenIsType(TokenType.DEC)) {
			consumeToken();
			result = parseCall();
			modifyby = -1;
		} else {
			result = parseCall();
			if (curTokenIsType(TokenType.INC)) {
				consumeToken();
				modifyby = 1;
			} else if (curTokenIsType(TokenType.DEC)) {
				consumeToken();
				modifyby = -1;
			} else {
				return result;
			}
			returnPrevious = true;
		}
		return new Modify(result, returnPrevious, new IntegerNode(modifyby, getCurrentLocation()),
				getCurrentLocation());
	}

	/**
	 * Parses a function call [Call], array access [ListAccess], variable access
	 * [VariableAccess], or field access [ScopeOf].
	 *
	 * @return An expression representing the parsed function call or a passthrough
	 * to Primary.
	 * <p>
	 * ***Grammar:***
	 * * CallExpression ->
	 * * * PrimaryExpression |
	 * * *  Ident "[" Expression "]" |
	 * * *  Ident "." Expression |
	 * * *  Ident "(" Parameters ")"
	 */
	private Expression parseCall() {
		if (curTokenIsType(TokenType.IDENTIFIER)) {
			String identifier = consumeToken().text;
			Entry<Integer, Integer> loc = getCurrentLocation();
			if (curTokenIsType(TokenType.LBRACKET)) {
				consumeToken();
				Expression idx = parseExpression();
				match(TokenType.RBRACKET, "Lists require a closing brace.");
				return new ListAccess(identifier, idx, getCurrentLocation());
			}
			if (curTokenIsType(TokenType.DOT)) {
				consumeToken();
				Expression rhs = parseExpression();
				return new ScopeOf(identifier, rhs, getCurrentLocation());
			} else if (curTokenIsType(TokenType.LPAREN)) {
				consumeToken();
				List<Expression> params = parseExprParams();
				return new Call(identifier, params, loc);
			} else {
				return new VariableAccess(identifier, loc);
			}
		}
		return parsePrimary();
	}

	/**
	 * Parses parameters.
	 *
	 * @return A list of parameters.
	 * * * Parameters -> "(" ParameterList ")"
	 * * * ParameterList ->
	 * * * * Identifier |
	 * * * *  ParameterList "," Identifier
	 */
	private List<Expression> parseExprParams() {
		List<Expression> params = new ArrayList<>(32);

		while (!curTokenIsType(TokenType.RPAREN)) {
			if (curTokenIsType(TokenType.COMMA)) {
				consumeToken();
			} else {
				if (curTokenIsType(TokenType.EOF) || curTokenIsType(TokenType.SEMICOLON)) {
					Token currentToken = getCurrentToken();
					throw new RuntimeException(
							"Syntax error: Invalid function call. Parameter list is not terminated. Expected an identifier, comma, or closing parenthesis, but found "
							+ currentToken.type + " at line " + currentToken.line + ", char "
							+ currentToken.charNum);
				}
				params.add(parseExpression());
			}
		}
		// Consume the RPAREN token
		consumeToken();
		return params;
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
		Entry<Integer, Integer> loc = getCurrentLocation();
		Expression primitive;
		switch (getCurrentToken().type) {
			case INTCONST -> primitive = new IntegerNode(Integer.parseInt(consumeToken().text), loc);
			case FLOATCONST -> primitive = new Floating(Double.parseDouble(consumeToken().text), loc);
			case STRINGLIT -> primitive = new StringLit(consumeToken().text, loc);
			case CHARLIT -> primitive = new CharNode(consumeToken().text.charAt(0), loc);
			case TRUE -> {
				consumeToken();
				primitive = new Bool(true, loc);
			}
			case FALSE -> {
				consumeToken();
				primitive = new Bool(false, loc);
			}
			case LPAREN -> {
				consumeToken();
				primitive = parseExpression();
				match(TokenType.RPAREN, "Error, unterminated parenthetical.");
			}
			default -> {
				Token currentToken = getCurrentToken();
				throw new RuntimeException(
						"Syntax error: Unexpected Token. Expected one of integer, float, string literal, parenthetical expression, boolean, or ident but found "
						+ currentToken.type + " at line " + currentToken.line + ", char " + currentToken.charNum);
			}
		}
		return primitive;
	}
}