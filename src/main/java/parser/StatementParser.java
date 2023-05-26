/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import AST.Declaration;
import AST.Expression;
import AST.Expressions.*;
import AST.Expressions.OpEnums.BinaryOps;
import AST.Expressions.OpEnums.UnaryOps;
import AST.Statement;
import AST.Statements.*;
import scanner.Token;
import utils.Entry;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public final class StatementParser extends ParserState {
	private StatementParser(List<Token> tokenStream) {
		super(tokenStream);
	}

	public static Entry<Statement, Integer> parseStatement(List<Token> tokenStream) {
		StatementParser parser = new StatementParser(tokenStream);
		Statement parsed = parser.parseStatement();
		return new Entry<>(parsed, parser.getCurrentPosition());
	}

	private static boolean isAssignment(Token currentToken) {
		return currentToken.type == Token.TokenType.EQUATE
			   || currentToken.type == Token.TokenType.ADDASSIGN
			   || currentToken.type == Token.TokenType.SUBASSIGN
			   || currentToken.type == Token.TokenType.MULASSIGN
			   || currentToken.type == Token.TokenType.DIVASSIGN
			   || currentToken.type == Token.TokenType.MODASSIGN
			   || currentToken.type == Token.TokenType.POWASSIGN
			   || currentToken.type == Token.TokenType.ANDASSIGN
			   || currentToken.type == Token.TokenType.ORASSIGN
			   || currentToken.type == Token.TokenType.RSHIFTASSIGN
			   || currentToken.type == Token.TokenType.LSHIFTASSIGN
			   || currentToken.type == Token.TokenType.XORASSIGN;
	}

	/**
	 * Checks whether the given operand returns a boolean.
	 */
	private static boolean isaBooleanExpr(Object opEnum) {
		return opEnum == BinaryOps.LessThan || opEnum == BinaryOps.LessEqual ||
			   opEnum == BinaryOps.EqualTo || opEnum == BinaryOps.GreaterEqual ||
			   opEnum == BinaryOps.NotEqualTo || opEnum == BinaryOps.GreaterThan ||
			   opEnum == BinaryOps.And || opEnum == BinaryOps.Or ||
			   opEnum == UnaryOps.Not;
	}

	private Statement parseStatement() {
		Statement retNode = null;
		Entry<Integer, Integer> loc = getCurrentLocation();
		switch (getCurrentToken().type) {
			case IF -> {
				consumeToken();
				retNode = parseIf();
			}
			case FOR -> {
				consumeToken();
				retNode = parseForLoop();
			}
			case FOREACH -> {
				consumeToken();
				retNode = parseForEachLoop();
			}
			case WHILE -> {
				consumeToken();
				retNode = parseWhile();
			}
			case DO -> {
				consumeToken();
				retNode = parseDoWhile();
			}
			case SWITCH -> {
				consumeToken();
				retNode = parseSwitch();
			}
			case TRY -> {
				consumeToken();
				retNode = parseTry();
			}
			case CONTINUE -> {
				consumeToken();
				retNode = new Continue(loc);
				match(Token.TokenType.SEMICOLON, "Statements must end with semicolons.");
			}
			case BREAK -> {
				consumeToken();
				retNode = new Break(loc);
				match(Token.TokenType.SEMICOLON, "Statements must end with semicolons.");
			}
			case RETURN -> {
				consumeToken();
				retNode = parseReturn();
				match(Token.TokenType.SEMICOLON, "Statements must end with semicolons.");
			}
			case GOTO -> {
				consumeToken();
				retNode = parseGoto();
				match(Token.TokenType.SEMICOLON, "Statements must end with semicolons.");
			}
			case THROW -> {
				consumeToken();
				retNode = parseThrow();
				match(Token.TokenType.SEMICOLON, "Statements must end with semicolons.");
			}
			case IDENTIFIER -> {
				if (getNextToken().type == Token.TokenType.COLON) {
					retNode = parseLabel();
				} else {
					retNode = parseExpression();
					if (isAssignment(getCurrentToken())) {
						retNode = parseAssignment((Expression) retNode);
					}
					match(Token.TokenType.SEMICOLON, "Statements must end with semicolons.");
				}
			}
			case LBRACE -> {
				retNode = parseBlock();
			}
			default -> {
				retNode = parseExpression();
				match(Token.TokenType.SEMICOLON, "Statements must end with semicolons.");
			}
		}
		return retNode;
	}

	/**
	 * Parses an if statement.
	 *
	 * @return The parsed [If] statement.
	 * <p>
	 * ***Grammar:***
	 * * IfStatement ->
	 * * * "if" "(" Expression ")" Statement
	 * * * "if" "(" Expression ")" Statement "else" Statement
	 */
	private Statement parseIf() {
		Token start = match(Token.TokenType.LPAREN, "Each if statement must contain a conditional enclosed in " +
													"parentheses.");
		Expression conditional = getConditional("if");

		match(Token.TokenType.RPAREN, "Each if statement must contain a conditional enclosed in parentheses.");

		Statement consequent = parseStatement();

		if (!(consequent instanceof Block)) {
			System.out.println("WARNING: If statement consequent block on line " + getCurrentLocation().key() +
							   " is not enclosed in braces.\n" +
							   " Please be aware that the language grammar is left-associative and" +
							   " will associate the nearest else with the nearest if. I.E  if (a) if (b) s; else s2; " +
							   "to if (a)\n" +
							   "{\n" +
							   "  if (b)\n" +
							   "    s;\n" +
							   "  else\n" +
							   "    s2;\n" +
							   "}");
		}

		Statement alternate = null;
		if (curTokenIsType(Token.TokenType.ELSE)) {
			consumeToken();
			alternate = parseStatement();
			if (!(alternate instanceof Block)) {
				System.out.println(
						"WARNING: If statement alternate (else) block on line " + getCurrentLocation().key() +
						" is not enclosed in braces.\n" +
						" Please be aware that the language grammar is left-associative and" +
						" will associate the nearest else with the nearest if. I.E  if (a) if (b) s; else s2; " +
						"to if (a)\n" +
						"{\n" +
						"  if (b)\n" +
						"    s;\n" +
						"  else\n" +
						"    s2;\n" +
						"}");
			}

		}
		return new If(conditional, consequent, alternate, new Entry<>(start.line, start.charNum));
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
		Token start = match(Token.TokenType.LPAREN, "Opening parenthesis expected after 'for'.");
		Declaration initializer = null;
		Expression conditional = null;
		Declaration iteration = null;

		if (!(curTokenIsType(Token.TokenType.RPAREN))) {
			if (curTokenIsType(Token.TokenType.SEMICOLON)) {
				consumeToken();
			} else {
				initializer = parseDeclaration();
			}
		}
		if (!(curTokenIsType(Token.TokenType.RPAREN))) {
			if (curTokenIsType(Token.TokenType.SEMICOLON)) {
				consumeToken();
			} else {
				conditional = getConditional("for");
				match(Token.TokenType.SEMICOLON, "For loop conditionals must end with a semicolon.");
			}
		}
		if (!(curTokenIsType(Token.TokenType.RPAREN))) {
			iteration = parseStatement();
		}

		match(Token.TokenType.RPAREN, "Error: Closing parenthesis expected after 'for'.");

		Statement block = parseStatement();
		if (!(block instanceof Block)) {
			System.out.println("WARNING: For loop body on line " + getCurrentLocation().key() +
							   " is not enclosed in braces.\n" +
							   " Please be aware that this language is not whitespace aware and statements following" +
							   " the first will not be associated with the loop.");
		}
		return new For(initializer, conditional, iteration, block, new Entry<>(start.line, start.charNum));
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
		Token start = match(Token.TokenType.LPAREN, "Opening parenthesis expected after 'foreach'.");
		String itervar = match(Token.TokenType.IDENTIFIER, "Iteration variable identifier expected after the opening " +
														   "parenthesis of a " +
														   "foreach " +
														   "expression.").text;
		match(Token.TokenType.COLON, "Colon expected after the iteration variable identifier of a 'foreach' " +
									 "expression.");
		String collectionvar = match(Token.TokenType.IDENTIFIER, "Collection variable identifier expected after the " +
																 "colon of a " +
																 "foreach " +
																 "expression.").text;
		match(Token.TokenType.RPAREN, "Closing parenthesis expected after 'foreach' control structure.");
		Statement block = parseStatement();
		if (!(block instanceof Block)) {
			System.out.println("WARNING: Foreach loop body on line " + getCurrentLocation().key() +
							   " is not enclosed in braces.\n" +
							   " Please be aware that this language is not whitespace aware and statements following" +
							   " the first will not be associated with the loop.");
		}
		return new ForEach(itervar, collectionvar, block, new Entry<>(start.line, start.charNum));
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
		Token start = match(Token.TokenType.LPAREN, "Opening parenthesis expected after 'while'.");
		Expression conditional = getConditional("while");
		match(Token.TokenType.RPAREN, "Closing parenthesis expected after 'while' control structure.");
		Statement block = parseStatement();
		if (!(block instanceof Block)) {
			System.out.println("WARNING: While loop body on line " + getCurrentLocation().key() +
							   " is not enclosed in braces.\n" +
							   " Please be aware that this language is not whitespace aware and statements following" +
							   " the first will not be associated with the loop.");
		}
		return new While(conditional, block, new Entry<>(start.line, start.charNum));
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
	private Statement parseDoWhile() {
		Token start = getCurrentToken();
		Statement block = parseStatement();
		match(Token.TokenType.WHILE, "While expected after do block.");
		match(Token.TokenType.LPAREN, "Opening parenthesis expected after 'do' block.");
		Expression conditional = getConditional("do-while");
		match(Token.TokenType.RPAREN, "Closing parenthesis expected after 'do' control structure.");
		if (!(block instanceof Block)) {
			System.out.println("WARNING: Do-While loop body on line " + getCurrentLocation().key() +
							   " is not enclosed in braces.\n" +
							   " Please be aware that this language is not whitespace aware and statements following" +
							   " the first will not be associated with the loop.");
		}
		return new DoWhile(conditional, block, new Entry<>(start.line, start.charNum));
	}

	/*
	 * Checks whether the given node is a valid conditional. Not a substitute for typechecking but it's probably good
	 *  to catch these things early.
	 */
	private Expression getConditional(String type) {
		Expression conditional = parseExpression();
		if (conditional instanceof Op op) {
			Object opEnum = op.getOp();
			// This expression will not enter the kingdom of heaven.
			if (!isaBooleanExpr(opEnum)) {
				throw new RuntimeException("ERROR: Bad conditional. " + op.getOp().toString()
										   + " is not a boolean in " + type + " statement on line " + op.getLine() +
										   ", character" + op.getCharacter() + " .");
			}
		} else if (conditional instanceof Bool bool) {
			System.out.println("WARNING: Conditional on line " + getCurrentLocation().key() +
							   " always evaluates to " +
							   (bool.bool ? "true" : "false"));
		} else {
			throw new RuntimeException("ERROR: Bad conditional. " + conditional.toString()
									   + " is not a binary conditional in " + type + " statement on line " +
									   conditional.getLine() +
									   ", character" + conditional.getCharacter() + " .");
		}
		return conditional;
	}

	private Statement parseSwitch() {
		Token start =
				match(Token.TokenType.LPAREN, "Error: Switch statements must be followed by the expression to switch on.");

		Expression conditional = parseExpression();

		match(Token.TokenType.RPAREN, "Error: Unclosed expression in switch block.");

		match(Token.TokenType.LBRACE, "Error: Switch statements must be followed by a switch block.");

		List<Entry<Expression, Statement>> cases = new ArrayList<>(32);
		while (!curTokenIsType(Token.TokenType.RBRACE)) {
			Expression exp;
			Statement block;
			if ("default".equals(getTokenText())) {
				consumeToken();
				match(Token.TokenType.COLON, "Error: Default expression must be followed by a colon.");
				exp = new Bool(true, new Entry<>(start.line, start.charNum));
				block = parseStatement();
			} else if ("case".equals(getTokenText())) {
				consumeToken();
				Expression primary = parseExpression();
				match(Token.TokenType.COLON, "Error on line " + start.line + ": Case expression must be followed by a" +
											 " colon.");
				// You might wonder why variable access is in here.
				// During expression parsing, any identifiers that can't be read as a function call, scoped access,
				// or array index are parsed as variables.
				// During semantic analysis and symbol-table building, the compiler substitutes any variables
				// matching enum members with their defined values.
				// It just makes stuff easier.
				if (!((primary instanceof Bool) || (primary instanceof IntegerNode) || (primary instanceof StringLit) ||
					  (primary instanceof CharNode) || (primary instanceof VariableAccess))) {
					throw new RuntimeException("Invalid pattern provided to switch case on line " + start.line + ". " +
											   "Switch accepts " +
											   "Booleans, " +
											   "integers, strings, characters, and enum members only.");
				}
				exp = primary;
				block = parseStatement();
			} else {
				throw new RuntimeException("Invalid identifier provided to switch case on line " + start.line + ". " +
										   "Switch accepts default:'s and case [primitive]:'s only.");
			}

			cases.add(new Entry<>(exp, block));
			if (curTokenIsType(Token.TokenType.SEMICOLON) || curTokenIsType(Token.TokenType.EOF)) {
				throw new RuntimeException(
						"Error: Unterminated switch block on line " + start.line + ". Did you forget" +
						" a closing " +
						"brace?");
			}
		}

		match(Token.TokenType.RBRACE, "Error on line " + start.line + ": Switch blocks end with right braces.");

		return new Switch(conditional, cases, new Entry<>(start.line, start.charNum));
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
		Token tok = getCurrentToken();
		Entry<Integer, Integer> loc = new Entry<>(tok.line, tok.charNum);
		Token.TokenType type = tok.type;
		if (type != Token.TokenType.EQUATE) {
			BinaryOps binaryOp;
			if (type == Token.TokenType.ADDASSIGN) {
				binaryOp = BinaryOps.Add;
			} else if (type == Token.TokenType.SUBASSIGN) {
				binaryOp = BinaryOps.Sub;
			} else if (type == Token.TokenType.MULASSIGN) {
				binaryOp = BinaryOps.Mul;
			} else if (type == Token.TokenType.DIVASSIGN) {
				binaryOp = BinaryOps.Div;
			} else if (type == Token.TokenType.MODASSIGN) {
				binaryOp = BinaryOps.Mod;
			} else if (type == Token.TokenType.POWASSIGN) {
				binaryOp = BinaryOps.Pow;
			} else if (type == Token.TokenType.ANDASSIGN) {
				binaryOp = BinaryOps.Bitwise_And;
			} else if (type == Token.TokenType.ORASSIGN) {
				binaryOp = BinaryOps.Bitwise_Or;
			} else if (type == Token.TokenType.XORASSIGN) {
				binaryOp = BinaryOps.Bitwise_Xor;
			} else if (type == Token.TokenType.LSHIFTASSIGN) {
				binaryOp = BinaryOps.Bitwise_LS;
			} else if (type == Token.TokenType.RSHIFTASSIGN) {
				binaryOp = BinaryOps.Bitwise_RS;
			} else {
				throw new RuntimeException("Error on line " + tok.line + ": Malformed assignment.");
			}
			consumeToken();
			return new Assignment(lhs, new BinaryOp(lhs, binaryOp, parseExpression(), loc), loc);
		}
		consumeToken();
		return new Assignment(lhs, parseExpression(), loc);
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
		String label = getTokenText();
		Token tok = getNextToken();
		return new Label(label, new Entry<>(tok.line, tok.charNum));
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
		Token tok = getNextToken();
		if (tok.type == Token.TokenType.SEMICOLON) {
			return new Return(null, new Entry<>(tok.line, tok.charNum));
		} else {
			return new Return(parseExpression(), new Entry<>(tok.line, tok.charNum));
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
		Token tok = getCurrentToken();
		String label = matchIdent("Goto statements must be followed by a label.");
		return new Goto(label, new Entry<>(tok.line, tok.charNum));
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
		Token start = getCurrentToken();
		Statement block = parseBlock();

		match(Token.TokenType.CATCH, "Try block must be followed by catch statement and block.");

		match(Token.TokenType.LPAREN, "'catch' must be followed by an exception to catch.");

		String catches = matchIdent("The opening parenthesis of a catch block must be followed by an exception " +
									"identifier." +
									".");
		consumeToken();
		String catchesAs;
		if (!curTokenIsType(Token.TokenType.COLON)) {
			System.out.println("WARNING: Exception not typed at line " + getCurrentLocation().key() + "Type will be " +
							   "inferred based on the first thrown exception in the try block.");
			catchesAs = "!!INFER!!";
		} else {
			consumeToken();
			catchesAs = matchIdent("A colon must be followed by a type identifier.");
		}
		match(Token.TokenType.RPAREN, "Exception catch statements must be followed with a right parenthesis.");
		Statement catchBlock = parseBlock();

		return new Try(block, catches, catchesAs, catchBlock, new Entry<>(start.line, start.charNum));
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
		String throws_ = matchIdent("An exception to throw must come after a throw statement.");

		Token tok = match(Token.TokenType.LPAREN, "Exceptions must have parameter lists enclosed in parenthesis.");

		List<Expression> params = new ArrayList<>(32);

		while (true) {
			if (curTokenIsType(Token.TokenType.LPAREN)) {
				consumeToken();
				break;
			} else if (curTokenIsType(Token.TokenType.COMMA)) {
				consumeToken();
			} else if (curTokenIsType(Token.TokenType.EOF) || curTokenIsType(Token.TokenType.SEMICOLON)) {
				throw new RuntimeException("Error on line " + tok.line + ": Invalid exception. Parameter list is not" +
										   " " +
										   "terminated.");
			} else {
				params.add(parseExpression());
			}
		}
		return new Throw(throws_, params, new Entry<>(tok.line, tok.charNum));
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


	private Declaration parseDeclaration() {
		Entry<Declaration, Integer> declaration =
				DeclarationParser.parseDeclaration(getTokenStream().subList(getCurrentPosition(),
						getTokenStream().size()));
		advanceLocation(declaration.value());
		return declaration.key();
	}

	private Expression parseExpression() {
		Entry<Expression, Integer> expression =
				ExpressionParser.parseExpression(getTokenStream().subList(getCurrentPosition(),
						getTokenStream().size()));
		advanceLocation(expression.value());
		return expression.key();
	}
}
