/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

public class ErrorTable {
	String ERROR_IF_CONDITIONAL = "Error: Each if statement must contain a conditional enclosed in parentheses.";

	String ERROR_EOF = "Unexpected EOF";

	String ERROR_BLOCK_EXPECTED = "Error: Block expected.";

	String ERROR_UNTERMINATED_BLOCK = "Error: Unterminated block.";

	String ERROR_FOR_OPENING_PARENTHESIS = "Error: Opening parenthesis expected after 'for'.";

	String ERROR_INVALID_CONDITIONAL = "Error: Invalid Conditional";

	String ERROR_FOR_CLOSING_PARENTHESIS = "Error: Closing parenthesis expected after 'for'.";

	String ERROR_FOREACH_CONTROL_STRUCTURE = "Error: Foreach loops must be followed by control structure.";

	String ERROR_FOREACH_LOOP_FORMAT =
			"Error: Foreach loops consist of an iteration variable, a colon ':', and a collection variable.";

	String ERROR_FOREACH_LOOP_TERMINATION = "Error: Foreach loop control structures must be terminated.";

	String ERROR_WHILE_CONDITIONAL = "Error: While loops must be followed by a conditional in parentheses.";

	String ERROR_WHILE_UNCLOSED_PARENTHESIS = "Error: Unclosed parenthesis in while loop conditional.";

	String ERROR_DO_LOOP_TERMINATION = "Error: Do loops must be followed by a 'while' block and control structure.";

	String ERROR_DO_LOOP_END = "Error: Do-while loops end with a semicolon after the while statement.";

	String ERROR_SWITCH_EXPRESSION = "Error: Switch statements must be followed by the expression to switch on.";

	String ERROR_UNCLOSED_EXPRESSION = "Error: Unclosed expression in switch block.";

	String ERROR_SWITCH_BLOCK = "Error: Switch statements must be followed by a switch block.";

	String ERROR_CASE_BLOCK_FORMAT =
			"Error: Case blocks consist of the keyword 'case' or 'default' followed by an expression, a colon, and a code block.";

	String ERROR_CASE_EXPRESSION_COLON = "Error: Case expression must be followed by a colon.";

	String ERROR_UNTERMINATED_SWITCH_BLOCK = "Error: Unterminated switch block. Did you forget a closing brace?";

	String ERROR_SWITCH_BLOCK_END = "Error: Switch blocks end with right braces.";

	String ERROR_MISSING_SEMICOLON = "Error: All statements must end with semicolons.";

	String ERROR_ASSIGNMENT_MALFORMED = "Error: Assignment malformed.";

	String ERROR_INVALID_LABEL = "Error: Labels consist of an identifier followed by a colon.";

	String ERROR_TRY_BLOCK_FORMAT =
			"Error: try blocks consist of a code block followed by a 'catch' statement and a parenthetical.";

	String ERROR_CATCH_PARENTHESIS = "Error: catch statements must be followed by a parenthetical.";

	String ERROR_INVALID_CATCH_VARIABLE = "Error: catch variable must be typed.";

	String ERROR_CATCH_TERMINATION = "Error: catch statements must be terminated.";

	String ERROR_THROW_PARENTHESIS = "Error: a throw expression must have its arguments in parentheses.";

	String ERROR_INVALID_EXCEPTION_PARAMETERS = "Error: Invalid exception. Parameter list is not terminated.";

	String ERROR_VAR_DEFINITION =
			"Error: A variable definition must contain an identifier followed by a colon and then a typename.";

	String ERROR_VAR_ASSIGNMENT =
			"Error: A variable definition must be followed by either an assignment or a semicolon.";

	String ERROR_EXPR_SEMICOLON = "Error: An expression in a definition must be followed by a semicolon.";

	String ERROR_ARRAY_DEFINITION =
			"Error: An array definition must contain an identifier followed by a colon and then a typename.";


	String ERROR_ARRAY_LENGTH = "Error: Array lengths must be whole numbers.";

	String ERROR_ARRAY_ASSIGNMENT =
			"Error: An array length expression must be followed by an assignment operator and initializer list or a semicolon.";

	String ERROR_ARRAY_BRACES =
			"Error: An array definition must be followed by either an assignment, a length in braces, or a semicolon.";

	String ERROR_ARRAY_INITIALIZER = "Error: Unexpected token in array initializer list.";

	String ERROR_ARRAY_RSQUARE = "Error: An array length expression must be followed by a right square bracket.";

	String ERROR_ARRAY_FAILED = "Error: Failed to parse an array declaration.";

	String ERROR_ARRAY_BRACKET = "Error: An array initializer must be followed by a right brace.";

	String ERROR_ENUM_DECLARATION = "Error: An enum declaration must contain a braced block of definitions.";

	String ERROR_ENUM_ENTRY =
			"Error: An enum definition is either an identifier followed by a comma or an identifier, a colon, a number, and then a comma.";

	String ERROR_UNEXPECTED_TOKEN = "Error: Unexpected token in %s.";

	String ERROR_FAILED_PARSE = "Error: Failed to parse.";

	String ERROR_EMPTY_PARAMETER_DEFINITION = "WARNING: Empty parameter definition.";

	String ERROR_UNEXPECTED_COMMA = "WARNING: Unexpected comma in parameter definition.";

	String ERROR_FUNCTION_PARAMS = "Error: A function parameter list must be enclosed in parentheses.";

	String ERROR_MISSING_LAMBDA = "Lambda needs type annotations.";

	String ERROR_PARAM_DEFINITION_MISSING_LEFT_PAREN =
			"A parameter definition must contain a left paren '(' followed by a list of parameters enclosed in parentheses.";

	String ERROR_PARAM_DEFINITION_MISSING_COLON =
			"A parameter definition must contain an identifier followed by a colon and then a typename.";

}
