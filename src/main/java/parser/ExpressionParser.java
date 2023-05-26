/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import AST.ASTRoot;
import AST.Expression;
import AST.Expressions.*;
import AST.Expressions.OpEnums.BinaryOps;
import AST.Expressions.OpEnums.UnaryOps;
import AST.Statement;
import scanner.Token;
import utils.Entry;

import java.util.ArrayList;
import java.util.List;

public class ExpressionParser extends ParserState {

	private ExpressionParser(List<Token> tokenStream) {
		super(tokenStream);
	}

	/**
	 * The entry point of the recursive descent parser.
	 * Starts parsing of the expression at the root level.
	 * Initalizes the expression parser object as well.
	 * Static because it's helpful for use in other parser types.
	 *
	 * @return An object of type [Expression] representing the parsed expression.
	 * <p>
	 * ***Grammar:***
	 * * Expression ->
	 * * * Lambda
	 */
	public static Expression parseExpression(List<Token> tokenStream) {
		ExpressionParser parser = new ExpressionParser(tokenStream);
		return parser.parseLambda();
	}

	public Expression parseExpression() {
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
	 * * LambdaBody ->
	 * * * Parameters ":" ReturnType Block
	 */
	private Expression parseLambda() {
		if (isIdentifier() && getTokenText().equals("lambda")) {
			consumeToken();
			List<ASTRoot.TypedVar> params = parseFuncParams("lambda");
			match(Token.TokenType.COLON, "Lambdas require a colon and a return type.");
			String type = matchIdent("Expected an identifier for the lambda's return type.");
			Entry<Statement, Integer> block =
					StatementParser.parseStatement(getTokenStream().subList(getCurrentPosition(), getTokenStream().size()));
			advanceLocation(block.value());
			return new Lambda(params, block.key(), type, getCurrentLocation());
		}
		return parseTernary();
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
	private List<ASTRoot.TypedVar> parseFuncParams(String statementType) {
		match(Token.TokenType.LPAREN, statementType + " must start with a left parenthesis.");
		List<ASTRoot.TypedVar> params = new ArrayList<>();
		boolean expectComma = false;
		while (true) {
			if (curTokenIsType(Token.TokenType.IDENTIFIER)) {
				if (expectComma) {
					Token curTok = getCurrentToken();
					System.out.println("WARNING: Expected a comma at identifier " + curTok.text
									   + " in parameter definition on line " + curTok.line +
									   ", character" + curTok.charNum + " .");
				}
				expectComma = true;
				String name = getTokenText();
				match(Token.TokenType.COLON, "A parameter definition must contain an identifier followed by a " +
											 "colon and then a typename.");
				String type = matchIdent("Expected a typename after lambda params.");
				params.add(new ASTRoot.TypedVar(name, type));
			} else if (curTokenIsType(Token.TokenType.RPAREN)) {
				if (params.isEmpty()) {
					Token curTok = getCurrentToken();
					System.out.println("WARNING: Empty parameter list " + curTok.text
									   + " in parameter definition on line " + curTok.line +
									   ", character" + curTok.charNum + " .");
				}
				consumeToken();
				break;
			} else if (curTokenIsType(Token.TokenType.COMMA)) {
				if (expectComma) {
					expectComma = false;
				} else {
					Token curTok = getCurrentToken();
					System.out.println("WARNING: Unexpected comma " + curTok.text
									   + " in parameter definition on line " + curTok.line +
									   ", character" + curTok.charNum + " .");
				}
				consumeToken();
			} else {
				matchList(List.of(Token.TokenType.IDENTIFIER, Token.TokenType.RPAREN, Token.TokenType.COMMA),
						"Invalid parameter list.");
			}
		}
		return params;
	}

	/**
	 * Parses a ternary.
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
		Entry<Integer, Integer> loc = getCurrentLocation();
		Expression lhs = parseBinaryOpExpression(0);
		if (curTokenIsType(Token.TokenType.QMARK)) {
			consumeToken();
			Expression consequent = parseBinaryOpExpression(0);
			match(Token.TokenType.COLON, "Ternaries require alternate expressions of the form pred ? cons : alt");
			Expression alternate = parseBinaryOpExpression(0);
			return new Ternary(lhs, consequent, alternate, loc);
		}
		return lhs;
	}

	/**
	 * Parses an expression of the form expr op expr.
	 * Takes a precedence parameter to determine expression precedence.
	 *
	 * @param precedence
	 * @return An object of type Expression containing a binary expression.
	 */
	private Expression parseBinaryOpExpression(int precedence) {
		if (OpPrecTable.getTable(precedence) == null) {
			return parseUnary();
		}
		Expression lhs = parseBinaryOpExpression(precedence + 1);
		while (OpPrecTable.inTableAt(precedence, getCurrentToken())) {
			Entry<Integer, Integer> loc = getCurrentLocation();
			BinaryOps op = OpPrecTable.mapSymbolToOp(precedence, consumeToken());
			System.out.println("Parsing: " + op + " at precedence: " + precedence);
			Expression rhs = parseBinaryOpExpression(precedence + 1);
			lhs = new BinaryOp(lhs, op, rhs, loc);
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
		Entry<Integer, Integer> loc = getCurrentLocation();
		if (curTokenIsType(Token.TokenType.BITWISE_NOT)) {
			consumeToken();
			return new UnaryOp(UnaryOps.BNot, parseUnary(), loc);
		} else if (curTokenIsType(Token.TokenType.SUB)) {
			consumeToken();
			return new UnaryOp(UnaryOps.Invert, parseUnary(), loc);
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
		Entry<Integer, Integer> loc = getCurrentLocation();
		if (curTokenIsType(Token.TokenType.NOT)) {
			consumeToken();
			return new UnaryOp(UnaryOps.Not, parseLogicalNotExpression(), loc);
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
		if (curTokenIsType(Token.TokenType.INC)) {
			consumeToken();
			Expression result = parseCall();
			return new Modify(result, false, new IntegerNode(1, getCurrentLocation()), getCurrentLocation());
		} else if (curTokenIsType(Token.TokenType.DEC)) {
			consumeToken();
			Expression result = parseCall();
			return new Modify(result, false, new IntegerNode(-1, getCurrentLocation()), getCurrentLocation());
		} else {
			Expression result = parseCall();
			if (curTokenIsType(Token.TokenType.INC)) {
				consumeToken();
				return new Modify(result, true, new IntegerNode(1, getCurrentLocation()), getCurrentLocation());
			} else if (curTokenIsType(Token.TokenType.DEC)) {
				consumeToken();
				return new Modify(result, true, new IntegerNode(-1, getCurrentLocation()), getCurrentLocation());
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
	 */
	private Expression parseCall() {
		Entry<Integer, Integer> loc = getCurrentLocation();
		if (curTokenIsType(Token.TokenType.IDENTIFIER)) {
			String identifier = consumeToken().text;
			loc = getCurrentLocation();
			if (curTokenIsType(Token.TokenType.LBRACKET)) {
				consumeToken();
				Expression idx = parseExpression();
				match(Token.TokenType.RBRACKET, "Lists require a closing brace.");
				return new ListAccess(identifier, idx, getCurrentLocation());
			} else if (curTokenIsType(Token.TokenType.DOT)) {
				consumeToken();
				Expression rhs = parseExpression();
				return new ScopeOf(identifier, rhs, getCurrentLocation());
			} else if (curTokenIsType(Token.TokenType.LPAREN)) {
				consumeToken();
				List<Expression> params = parseExprParams();
				// Return the Call expression with identifier and parameters
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
		List<Expression> params = new ArrayList<>();
		while (true) {
			Token tok = getCurrentToken();
			if (curTokenIsType(Token.TokenType.RPAREN)) {
				consumeToken();
				break;
			} else if (curTokenIsType(Token.TokenType.COMMA)) {
				consumeToken();
			} else {
				if (curTokenIsType(Token.TokenType.EOF) || curTokenIsType(Token.TokenType.SEMICOLON)) {
					Token currentToken = getCurrentToken();
					throw new RuntimeException("Syntax error: " + "Invalid function call. Parameter list is not " +
											   "terminated." + "Expected an identifier, comma, or closing" +
											   " parenthesis," +
											   " but found " + currentToken.type + " at line " + currentToken.line +
											   ", char " + currentToken.charNum);
				}
				params.add(parseExpression());
			}
		}
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
		if (curTokenIsType(Token.TokenType.INTCONST)) {
			Entry<Integer, Integer> loc = getCurrentLocation();
			return new IntegerNode(Integer.parseInt(consumeToken().text), loc);
		} else if (curTokenIsType(Token.TokenType.FLOATCONST)) {
			Entry<Integer, Integer> loc = getCurrentLocation();
			return new Floating(Double.parseDouble(consumeToken().text), loc);
		} else if (curTokenIsType(Token.TokenType.STRINGLIT)) {
			Entry<Integer, Integer> loc = getCurrentLocation();
			return new StringLit(consumeToken().text, loc);
		} else if (curTokenIsType(Token.TokenType.CHARLIT)) {
			Entry<Integer, Integer> loc = getCurrentLocation();
			return new CharNode(consumeToken().text.charAt(0), loc);
		} else if (curTokenIsType(Token.TokenType.TRUE)) {
			Entry<Integer, Integer> loc = getCurrentLocation();
			consumeToken();
			return new Bool(true, loc);
		} else if (curTokenIsType(Token.TokenType.FALSE)) {
			Entry<Integer, Integer> loc = getCurrentLocation();
			consumeToken();
			return new Bool(false, loc);
		} else if (curTokenIsType(Token.TokenType.LPAREN)) {
			consumeToken();
			Expression parenthetical = parseExpression();
			match(Token.TokenType.LPAREN, "Error, unterminated parenthetical.");
			return parenthetical;
		} else {
			Token currentToken = getCurrentToken();
			throw new RuntimeException("Syntax error: " + "Unexpected Token. " + "Expected one of integer, float, " +
									   "string literal, parenthetical expression, boolean, or ident" +
									   " but found " + currentToken.type + " at line " + currentToken.line +
									   ", char " + currentToken.charNum);
		}
	}
}
