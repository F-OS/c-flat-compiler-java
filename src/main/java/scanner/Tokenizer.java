/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package scanner;

import static scanner.Token.TokenType.*;
import static scanner.Token.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This refactored tokenizer is an adaptation of:
 *  [https://github.com/mpartel/minicompiler/blob/master/src/main/java/minicompiler/Tokenizer.java]
 * I owe a big debt to Martin Pärtel for his tokenizer class.
 */
public final class Tokenizer {
	private static final Pattern IS_FLOATING = Pattern.compile("^\\d+\\.\\d+");
	private static final Pattern GET_INTEGER = Pattern.compile("^\\b(0x[0-9a-fA-F]+|\\d+)\\b");
	private static final Pattern GET_DECIMAL = Pattern.compile("^\\b(0x[0-9a-fA-F]+|\\d+)\\.(0x[0-9a-fA-F]+|\\d+)\\b");
	private static final Pattern GET_IDENTIFIER = Pattern.compile("[a-zA-Z_]\\w*");
	private static final LinkedHashMap<String, Token.TokenType> keywords = new LinkedHashMap<>();

	private static final LinkedHashMap<String, Token.TokenType> primitives = new LinkedHashMap<>();

	static {
		keywords.put("if", IF);
		keywords.put("else", ELSE);
		keywords.put("for", FOR);
		keywords.put("foreach", FOREACH);
		keywords.put("while", WHILE);
		keywords.put("do", DO);
		keywords.put("switch", SWITCH);
		keywords.put("try", TRY);
		keywords.put("continue", CONTINUE);
		keywords.put("break", BREAK);
		keywords.put("return", RETURN);
		keywords.put("goto", GOTO);
		keywords.put("throw", THROW);
		keywords.put("fun", FUN);
		keywords.put("var", VAR);
		keywords.put("array", ARRAY);
		keywords.put("enum", ENUM);
		keywords.put("catch", CATCH);
		keywords.put("class", CLASS);
		keywords.put("struct", STRUCT);
		keywords.put("true", TRUE);
		keywords.put("false", FALSE);
		primitives.put("<<=", LSHIFTASSIGN);
		primitives.put(">>=", RSHIFTASSIGN);
		primitives.put("**=", POWASSIGN);
		primitives.put("**", POW);
		primitives.put("+=", ADDASSIGN);
		primitives.put("-=", SUBASSIGN);
		primitives.put("*=", MULASSIGN);
		primitives.put("/=", DIVASSIGN);
		primitives.put("%=", MODASSIGN);
		primitives.put("&=", ANDASSIGN);
		primitives.put("|=", ORASSIGN);
		primitives.put("^=", XORASSIGN);
		primitives.put("++", INC);
		primitives.put("--", DEC);
		primitives.put(">>", BITWISE_LSHIFT);
		primitives.put("<<", BITWISE_RSHIFT);
		primitives.put("||", OR);
		primitives.put("&&", AND);
		primitives.put("<=", GREATEREQUAL);
		primitives.put(">=", LESSEQUAL);
		primitives.put("<", GREATERTHAN);
		primitives.put(">", LESSTHAN);
		primitives.put("==", EQUALTO);
		primitives.put("!=", NOTEQUALTO);
		primitives.put("^", BITWISE_XOR);
		primitives.put("~", BITWISE_NOT);
		primitives.put("|", BITWISE_OR);
		primitives.put("&", BITWISE_AND);
		primitives.put("!", NOT);
		primitives.put("+", ADD);
		primitives.put("-", SUB);
		primitives.put("*", MUL);
		primitives.put("?", QMARK);
		primitives.put(":", COLON);
		primitives.put("/", DIV);
		primitives.put("%", MOD);
		primitives.put(".", DOT);
		primitives.put("=", EQUATE);
		primitives.put(",", COMMA);
		primitives.put(";", SEMICOLON);
		primitives.put("{", LBRACE);
		primitives.put("}", RBRACE);
		primitives.put("[", LBRACKET);
		primitives.put("]", RBRACKET);
		primitives.put("(", LPAREN);
		primitives.put(")", RPAREN);
	}

	private final ArrayList<Token> result;
	private final Map<String, String> escapeSequences = Map.ofEntries(Map.entry("n", "\n"), Map.entry("r", "\r"),
			Map.entry("t", "\t"), Map.entry("b", "\b"), Map.entry("\\", "\\"), Map.entry("\"", "\""));
	private String input;
	private int line;
	private int col;

	private Tokenizer(String in) {
		result = new ArrayList<>();
		input = in;
		line = 1;
		col = 1;
	}

	public static ArrayList<Token> tokenize(String input) {
		Tokenizer tokenizer = new Tokenizer(input);
		tokenizer.tokenize();
		return tokenizer.result;
	}

	private boolean isPrimitive() {
		for (Map.Entry<String, Token.TokenType> entry : primitives.entrySet()) {
			String key = entry.getKey();
			if (tryToken(key, primitives.get(key))) {
				return true;
			}
		}

		return false;
	}

	private void tokenize() {
		skipWhitespace();
		skipComments();
		while (!input.isEmpty()) {
			if (!isPrimitive()) {
				if (isDigit()) {
					if (isFloating()) {
						tryRegex(GET_DECIMAL, FLOATCONST);
					} else {
						tryRegex(GET_INTEGER, INTCONST);
					}
				} else if (isIdent()) {
					Matcher idt = GET_IDENTIFIER.matcher(input);
					idt.find();
					String out = idt.group(0);
					Token.TokenType type = keywords.getOrDefault(out, IDENTIFIER);
					consumeInput(out.length());
					result.add(new Token(type, out, line, col));
				} else if (isCharLiteral()) {
					result.add(new Token(CHARLIT, parseCharacterLiteral(), line, col));
				} else if (isString()) {
					int strbegin = line;
					int strbeginc = col;
					String newstr = parseString();
					result.add(new Token(STRINGLIT, newstr, strbegin, strbeginc));
				} else {
					error("Unable to recognize " + input.charAt(0));
					result.add(new Token(UNIMPLEMENTED, input.substring(0, 1), line, col));
					consumeInput(1);
				}
			}
			skipWhitespace();
			skipComments();
		}
		result.add(new Token(EOF, "EOF", line, col));
	}

	private void skipWhitespace() {
		int i = 0;
		while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
			i++;
		}
		consumeInput(i);
	}

	private void skipComments() {
		if (input.startsWith("//")) {
			consumeInput(2);
			while (input.charAt(0) != '\n' && input.charAt(0) != '\r') {
				consumeInput(1);
			}
			consumeInput(1);
		}
		skipWhitespace();
	}

	private boolean tryToken(String expected, Token.TokenType tokenType) {
		if (input.startsWith(expected)) {
			result.add(new Token(tokenType, expected, line, col));
			consumeInput(expected.length());
			return true;
		}
		return false;
	}

	private boolean isDigit() {
		return Character.isDigit(input.charAt(0));
	}

	private boolean isFloating() {
		return IS_FLOATING.matcher(input).find();
	}

	private boolean tryRegex(Pattern p, Token.TokenType ty) {
		Matcher m = p.matcher(input);
		if (m.lookingAt()) {
			result.add(new Token(ty, m.group(), line, col));
			consumeInput(m.end());
			return true;
		}
		return false;
	}

	private boolean isIdent() {
		return Character.isAlphabetic(input.charAt(0)) || input.charAt(0) == '_';
	}

	private void consumeInput(int amount) {
		for (int i = 0; i < amount; ++i) {
			char c = input.charAt(i);
			if (c == '\n' || c == '\r') {
				line++;
				col = 1;
			} else {
				col++;
			}
		}
		input = input.substring(amount);
	}

	private boolean isCharLiteral() {
		return input.charAt(0) == '\'';
	}

	private String parseCharacterLiteral() {
		consumeInput(1);
		String charLit;
		if (input.charAt(0) == '\\') {
			consumeInput(1);
			String key = input.substring(0, 1);
			charLit = escapeSequences.getOrDefault(key, key);
		} else {
			charLit = input.substring(0, 1);
		}
		consumeInput(1);
		if (input.charAt(0) == '\'') {
			consumeInput(1);
		} else {
			error("Unterminated string literal.");
		}
		return charLit;
	}

	private boolean isString() {
		return input.charAt(0) == '"';
	}

	private String parseString() {
		StringBuilder stringLiteral = new StringBuilder(64);
		consumeInput(1);
		while (true) {
			char c = input.charAt(0);
			if (c == '\\') {
				consumeInput(1);
				String key = input.substring(0, 1);
				stringLiteral.append(escapeSequences.getOrDefault(key, key).charAt(0));
			} else if (c == '\n' || c == '\r') {
				error("Unterminated String Literal.");
				break;
			} else if (c == '"') {
				consumeInput(1);
				break;
			} else {
				stringLiteral.append(c);
			}
			consumeInput(1);

			if (input.isEmpty()) {
				error("Unterminated String Literal.");
				break;
			}
		}
		return stringLiteral.toString();
	}

	private void error(String message) {
		System.out.println("ERROR - Tokenizer: At line: " + line + ", character: " + col + ", " + message);
	}

}
