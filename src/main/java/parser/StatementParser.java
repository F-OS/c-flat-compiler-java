/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import AST.*;
import AST.Expressions.*;
import AST.Expressions.OpEnums.*;
import AST.Statements.*;
import scanner.*;
import scanner.Token.*;
import utils.*;

import java.util.*;

public final class StatementParser extends Parser {
	public StatementParser(ParsingContext cont) {
		super(cont);
	}

	public static Statement parseStatement(ParsingContext cont) {
		StatementParser parser = new StatementParser(cont);
		return parser.parseStatement();
	}

	private static boolean isAssignment(Token currentToken) {
		return currentToken.type == TokenType.EQUATE || currentToken.type == TokenType.ADDASSIGN
			   || currentToken.type == TokenType.SUBASSIGN || currentToken.type == TokenType.MULASSIGN
			   || currentToken.type == TokenType.DIVASSIGN || currentToken.type == TokenType.MODASSIGN
			   || currentToken.type == TokenType.POWASSIGN || currentToken.type == TokenType.ANDASSIGN
			   || currentToken.type == TokenType.ORASSIGN || currentToken.type == TokenType.RSHIFTASSIGN
			   || currentToken.type == TokenType.LSHIFTASSIGN || currentToken.type == TokenType.XORASSIGN;
	}
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
				match(TokenType.SEMICOLON, "Statements must end with semicolons.");
			}
			case BREAK -> {
				consumeToken();
				retNode = new Break(loc);
				match(TokenType.SEMICOLON, "Statements must end with semicolons.");
			}
			case RETURN -> {
				consumeToken();
				retNode = parseReturn();
				match(TokenType.SEMICOLON, "Statements must end with semicolons.");
			}
			case GOTO -> {
				consumeToken();
				retNode = parseGoto();
				match(TokenType.SEMICOLON, "Statements must end with semicolons.");
			}
			case THROW -> {
				consumeToken();
				retNode = parseThrow();
				match(TokenType.SEMICOLON, "Statements must end with semicolons.");
			}
			case IDENTIFIER -> {
				if (nextTokenIsType(TokenType.COLON)) {
					retNode = parseLabel();
				} else {
					retNode = ExpressionParser.parseExpression(context);
					if (isAssignment(getCurrentToken())) {
						retNode = parseAssignment((Expression) retNode);
					}
					match(TokenType.SEMICOLON, "Statements must end with semicolons.");
				}
			}
			case LBRACE -> retNode = parseBlock();
			default -> {
				retNode = ExpressionParser.parseExpression(context);
				match(TokenType.SEMICOLON, "Statements must end with semicolons.");
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
		Token start = match(TokenType.LPAREN,
				"Each if statement must contain a conditional enclosed in " + "parentheses.");
		Expression conditional = getConditional("if");

		match(TokenType.RPAREN, "Each if statement must contain a conditional enclosed in parentheses.");

		Statement consequent = parseStatement();

		if (!(consequent instanceof Block)) {
			System.out.println("WARNING: If statement consequent block on line " + getCurrentLocation().key()
							   + " is not enclosed in braces.\n"
							   + " Please be aware that the language grammar is left-associative and"
							   + " will associate the nearest else with the nearest if. I.E  if (a) if (b) s; else s2; "
							   + "to if (a)\n" + "{\n" + "  if (b)\n" + "    s;\n" + "  else\n" + "    s2;\n" + "}");
		}

		Statement alternate = null;
		if (curTokenIsType(TokenType.ELSE)) {
			consumeToken();
			alternate = parseStatement();
			if (!(alternate instanceof Block)) {
				System.out.println("WARNING: If statement alternate (else) block on line " + getCurrentLocation().key()
								   + " is not enclosed in braces.\n"
								   + " Please be aware that the language grammar is left-associative and"
								   +
								   " will associate the nearest else with the nearest if. I.E  if (a) if (b) s; else s2; "
								   + "to if (a)\n" + "{\n" + "  if (b)\n" + "    s;\n" + "  else\n" + "    s2;\n" +
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
		Token start = match(TokenType.LPAREN, "Opening parenthesis expected after 'for'.");
		Declaration initializer = null;
		Expression conditional = null;
		Declaration iteration = null;

		if (!curTokenIsType(TokenType.RPAREN)) {
			if (curTokenIsType(TokenType.SEMICOLON)) {
				consumeToken();
			} else {
				initializer = DeclarationParser.parseDeclaration(context);
			}
		}
		if (!curTokenIsType(TokenType.RPAREN)) {
			if (curTokenIsType(TokenType.SEMICOLON)) {
				consumeToken();
			} else {
				conditional = getConditional("for");
			}
		}
		if (!curTokenIsType(TokenType.RPAREN)) {
			match(TokenType.SEMICOLON, "For loop conditionals must end with a semicolon.");
			context.setSemicolonExempt();
			iteration = parseStatement();
		}

		match(TokenType.RPAREN, "Error: Closing parenthesis expected after 'for'.");

		Statement block = parseStatement();
		if (!(block instanceof Block)) {
			System.out.println(
					"WARNING: For loop body on line " + getCurrentLocation().key() + " is not enclosed in braces.\n"
					+ " Please be aware that this language is not whitespace aware and statements following"
					+ " the first will not be associated with the loop.");
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
		Token start = match(TokenType.LPAREN, "Opening parenthesis expected after 'foreach'.");
		String itervar = match(TokenType.IDENTIFIER, """
				Iteration variable identifier expected after the opening \
				parenthesis of a \
				foreach \
				expression.""").text;
		match(TokenType.COLON,
				"Colon expected after the iteration variable identifier of a 'foreach' " + "expression.");
		String collectionvar = match(TokenType.IDENTIFIER, """
				Collection variable identifier expected after the \
				colon of a \
				foreach \
				expression.""").text;
		match(TokenType.RPAREN, "Closing parenthesis expected after 'foreach' control structure.");
		Statement block = parseStatement();
		if (!(block instanceof Block)) {
			System.out.println(
					"WARNING: Foreach loop body on line " + getCurrentLocation().key() + " is not enclosed in braces.\n"
					+ " Please be aware that this language is not whitespace aware and statements following"
					+ " the first will not be associated with the loop.");
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
		Token start = match(TokenType.LPAREN, "Opening parenthesis expected after 'while'.");
		Expression conditional = getConditional("while");
		match(TokenType.RPAREN, "Closing parenthesis expected after 'while' control structure.");
		Statement block = parseStatement();
		if (!(block instanceof Block)) {
			System.out.println(
					"WARNING: While loop body on line " + getCurrentLocation().key() + " is not enclosed in braces.\n"
					+ " Please be aware that this language is not whitespace aware and statements following"
					+ " the first will not be associated with the loop.");
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
		match(TokenType.WHILE, "While expected after do block.");
		match(TokenType.LPAREN, "Opening parenthesis expected after 'do' block.");
		Expression conditional = getConditional("do-while");
		match(TokenType.RPAREN, "Closing parenthesis expected after 'do' control structure.");
		if (!(block instanceof Block)) {
			System.out.println("WARNING: Do-While loop body on line " + getCurrentLocation().key()
							   + " is not enclosed in braces.\n"
							   + " Please be aware that this language is not whitespace aware and statements following"
							   + " the first will not be associated with the loop.");
		}
		match(TokenType.SEMICOLON, "Semicolon expected after 'do-while' conditional.");
		return new DoWhile(conditional, block, new Entry<>(start.line, start.charNum));
	}

	/*
	 * Checks whether the given node is a valid conditional. Not a substitute for
	 * typechecking but it's probably good to catch these things early.
	 */
	private Expression getConditional(String type) {
		Expression conditional = ExpressionParser.parseExpression(context);
		conditional.assertIsConditional(type);
		return conditional;
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
		Token start = match(TokenType.LPAREN,
				"Error: Switch statements must be followed by the expression to switch on.");

		Expression conditional = ExpressionParser.parseExpression(context);

		match(TokenType.RPAREN, "Error: Unclosed expression in switch block.");

		match(TokenType.LBRACE, "Error: Switch statements must be followed by a switch block.");

		List<Entry<Expression, Statement>> cases = new ArrayList<>(32);
		while (!curTokenIsType(TokenType.RBRACE)) {
			Expression exp;
			if (curTokenIsType(TokenType.DEFAULT)) {
				consumeToken();
				match(TokenType.COLON, "Error: Default expression must be followed by a colon.");
				exp = new Bool(true, new Entry<>(start.line, start.charNum));
			} else if (curTokenIsType(TokenType.CASE)) {
				consumeToken();
				Expression primary = ExpressionParser.parseExpression(context);
				match(TokenType.COLON,
						"Error on line " + start.line + ": Case expression must be followed by a" + " colon.");
				// You might wonder why variable access is in here.
				// During expression parsing, any identifiers that can't be read as a function
				// call, scoped access,
				// or array index are parsed as variables.
				// During semantic analysis and symbol-table building, the compiler substitutes
				// any variables
				// matching enum members with their defined values.
				// It just makes stuff easier.
				if ((!(primary instanceof Bool) && !(primary instanceof IntegerNode) && !(primary instanceof StringLit)
					 && !(primary instanceof CharNode) && !(primary instanceof VariableAccess))) {
					throw new RuntimeException(
							"Invalid pattern provided to switch case on line " + start.line + ". " + "Switch accepts "
							+ "Booleans, " + "integers, strings, characters, and enum members only.");
				}
				exp = primary;
			} else {
				throw new RuntimeException("Invalid identifier provided to switch case on line " + start.line + ". "
										   + "Switch accepts default:'s and case [primitive]:'s only.");
			}
			Statement block = parseStatement();

			cases.add(new Entry<>(exp, block));
			if (curTokenIsType(TokenType.SEMICOLON) || curTokenIsType(TokenType.EOF)) {
				throw new RuntimeException("Error: Unterminated switch block on line " + start.line + ". Did you forget"
										   + " a closing brace?");
			}
		}

		match(TokenType.RBRACE, "Error on line " + start.line + ": Switch blocks end with right braces.");

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
		TokenType type = tok.type;
		BinaryOps binaryOp;
		if (type == TokenType.EQUATE) {
			consumeToken();
			return new Assignment(lhs, ExpressionParser.parseExpression(context), loc);
		} else if (type == TokenType.ADDASSIGN) {
			binaryOp = BinaryOps.Add;
		} else if (type == TokenType.SUBASSIGN) {
			binaryOp = BinaryOps.Sub;
		} else if (type == TokenType.MULASSIGN) {
			binaryOp = BinaryOps.Mul;
		} else if (type == TokenType.DIVASSIGN) {
			binaryOp = BinaryOps.Div;
		} else if (type == TokenType.MODASSIGN) {
			binaryOp = BinaryOps.Mod;
		} else if (type == TokenType.POWASSIGN) {
			binaryOp = BinaryOps.Pow;
		} else if (type == TokenType.ANDASSIGN) {
			binaryOp = BinaryOps.Bitwise_And;
		} else if (type == TokenType.ORASSIGN) {
			binaryOp = BinaryOps.Bitwise_Or;
		} else if (type == TokenType.XORASSIGN) {
			binaryOp = BinaryOps.Bitwise_Xor;
		} else if (type == TokenType.LSHIFTASSIGN) {
			binaryOp = BinaryOps.Bitwise_LS;
		} else if (type == TokenType.RSHIFTASSIGN) {
			binaryOp = BinaryOps.Bitwise_RS;
		} else {
			throw new RuntimeException("Error on line " + tok.line + ": Malformed assignment.");
		}
		consumeToken();
		return new Assignment(lhs, new BinaryOp(lhs, binaryOp, ExpressionParser.parseExpression(context), loc), loc);
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
		String label = matchIdent("Expected a label.");
		Token tok = consumeToken();
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
		Token tok = getCurrentToken();
		if (tok.type == TokenType.SEMICOLON) {
			return new Return(null, new Entry<>(tok.line, tok.charNum));
		}
		return new Return(ExpressionParser.parseExpression(context), new Entry<>(tok.line, tok.charNum));
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

		match(TokenType.CATCH, "Try block must be followed by catch statement and block.");

		match(TokenType.LPAREN, "'catch' must be followed by an exception to catch.");

		String catches = matchIdent("""
				The opening parenthesis of a catch block must be followed by an exception \
				identifier.\
				.""");
		String catchesAs;
		if (!curTokenIsType(TokenType.COLON)) {
			System.out.println("WARNING: Exception not typed at line " + getCurrentLocation().key() + "Type will be "
							   + "inferred based on the first thrown exception in the try block.");
			catchesAs = "!!INFER!!";
		} else {
			consumeToken();
			catchesAs = matchIdent("A colon must be followed by a type identifier.");
		}
		match(TokenType.RPAREN, "Exception catch statements must be followed with a right parenthesis.");
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

		Token tok = match(TokenType.LPAREN, "Exceptions must have parameter lists enclosed in parenthesis.");

		List<Expression> params = new ArrayList<>(32);

		while (true) {
			if (curTokenIsType(TokenType.RPAREN)) {
				consumeToken();
				break;
			}
			if (curTokenIsType(TokenType.COMMA)) {
				consumeToken();
			} else if (curTokenIsType(TokenType.EOF) || curTokenIsType(TokenType.SEMICOLON)) {
				throw new RuntimeException("Error on line " + tok.line + ": Invalid exception. Parameter list is not"
										   + " " + "terminated.");
			} else {
				params.add(ExpressionParser.parseExpression(context));
			}
		}
		return new Throw(throws_, params, new Entry<>(tok.line, tok.charNum));
	}
}
