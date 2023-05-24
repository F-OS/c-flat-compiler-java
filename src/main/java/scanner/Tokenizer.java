/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package scanner;

import scanner.token.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Tokenizer {
	private static final int INITIAL_TOK_LEN = 1024;
	private static final Pattern IS_FLOATING = Pattern.compile("^\\d+\\.\\d+");
	private static final Pattern GET_INTEGER = Pattern.compile("^\\b(0x[0-9a-fA-F]+|\\d+)\\b");
	private static final Pattern GET_DECIMAL = Pattern.compile("^\\b(0x[0-9a-fA-F]+|\\d+)\\.(0x[0-9a-fA-F]+|\\d+)\\b");

	private final String inputString;
	private final char[] inputStringChars;
	private final int inputStringLength;
	private final Map<String, Class<?>> primitiveMap;

	private final Set<String> mapKeys;

	private final Map<String, String> escapeSequences = Map.ofEntries(
			Map.entry("n", "\n"),
			Map.entry("r", "\r"),
			Map.entry("t", "\t"),
			Map.entry("b", "\b"),
			Map.entry("\\", "\\"),
			Map.entry("\"", "\"")
	);

	private int strIndex;
	private int line;
	private int currentCharacter;

	public Tokenizer(String inputString) {
		this.inputString = inputString;
		this.inputStringLength = inputString.length();
		this.inputStringChars = inputString.toCharArray();
		this.primitiveMap = PrimitiveMap.get();
		this.mapKeys = primitiveMap.keySet();
		this.strIndex = 0;
		this.line = 0;
		this.currentCharacter = 0;
	}

	public Tokenizer() throws InstantiationException {
		throw new InstantiationException("No-arg constructors not supported by Tokenizer");
	}

	public List<Token> tokenize() {
		List<Token> tokens = new ArrayList<>(INITIAL_TOK_LEN);
		while (strIndex < inputStringLength) {
			skipComments();
			skipWhitespace();
			tokens.add(getNextToken());
			skipComments();
			skipWhitespace();
		}
		tokens.add(new EndOfFile(line, currentCharacter));
		return tokens;
	}

	private Token getNextToken() {
		Token result;
		if (matchesPrimitive()) {
			Map.Entry<String, Class<?>> newtok = getPrimitiveToken();
			result = createTokenClassChecked(newtok.getValue());
			charForward(newtok.getKey().length());
		} else if (isCharLiteral()) {
			result = new CharTok(line, currentCharacter, parseCharacterLiteral());
		} else if (isIdentifier()) {
			result = new Ident(line, currentCharacter, parseIdent());
		} else if (isDigit()) {
			if (isFloating()) {
				result = new NumbTok(line, currentCharacter, parseDouble());
			} else {
				result = new NumbTok(line, currentCharacter, parseInteger());
			}
		} else if (isString()) {
			result = new StringTok(line, currentCharacter, parseString());
		} else {
			result = new Unimplemented(line, currentCharacter, inputStringChars[strIndex]);
			charForward(1);
		}
		return result;
	}

	private Token createTokenClassChecked(Class<?> value) {
		if (Token.class.isAssignableFrom(value)) {
			Class<? extends Token> klass = value.asSubclass(Token.class);
			return createTokenClass(klass);
		} else {
			throw new AssertionError("Non-primitive tokens should never be included in token mappings.");
		}
	}

	private boolean matchesPrimitive() {
		for (String key : mapKeys) {
			if (inputString.startsWith(key, strIndex)) {
				return true;
			}
		}
		return false;
	}

	private Map.Entry<String, Class<?>> getPrimitiveToken() {
		Map.Entry<String, Class<?>> longest = Map.entry("", Object.class);
		for (Map.Entry<String, Class<?>> entry : primitiveMap.entrySet()) {
			String key = entry.getKey();
			if (inputString.startsWith(key, strIndex) && key.length() > longest.getKey().length()) {
				longest = entry;
			}
		}
		return longest;
	}

	private <T extends Token> T createTokenClass(Class<T> tokenClass) {
		try {
			return tokenClass.getDeclaredConstructor(int.class, int.class).newInstance(line, currentCharacter);
		} catch (Exception e) {
			// Handle any exceptions that may occur during instantiation
			e.printStackTrace();

			return (T) new Unimplemented(line, currentCharacter, '\0');
		}
	}

	private String parseString() {
		StringBuilder stringLiteral = new StringBuilder(64);
		charForward(1);
		while (true) {
			if (inputStringChars[strIndex] == '\\') {
				charForward(1);
				String key = String.valueOf(inputStringChars[strIndex]);
				stringLiteral.append(escapeSequences.getOrDefault(key, key).charAt(0));
				charForward(1);
			} else if (inputStringChars[strIndex] == '\n' || inputStringChars[strIndex] == '\r') {
				error("Unterminated String Literal.");
				break;
			} else if (inputStringChars[strIndex] == '"') {
				charForward(1);
				break;
			} else {
				stringLiteral.append(inputStringChars[strIndex]);
				charForward(1);
			}

			if (strIndex > inputStringLength) {
				error("Unterminated String Literal.");
				break;
			}
		}
		return stringLiteral.toString();
	}


	private long parseInteger() {
		Matcher match = GET_INTEGER.matcher(inputString.substring(strIndex));
		if (match.find()) {
			charForward(match.group(0).length());
			return Long.parseLong(match.group(0));
		} else {
			throw new AssertionError("Invalid integer even though isNumber provided assurance. This should never happen");
		}
	}

	private double parseDouble() {
		Matcher match = GET_DECIMAL.matcher(inputString.substring(strIndex));

		if (match.find()) {
			String groupret = match.group(0);
			charForward(groupret.length());
			return Double.parseDouble(groupret);
		} else {
			throw new AssertionError("Invalid decimal even though isFloating provided assurance. This should never happen");
		}
	}

	private void skipWhitespace() {
		while (strIndex < inputStringLength && isWhitespace(inputStringChars[strIndex])) {
			if (inputStringChars[strIndex] == '\n' || inputStringChars[strIndex] == '\r') {
				charForward(1);
				currentCharacter = 0;
				line++;
			} else {
				charForward(1);
			}
		}
	}

	private void skipComments() {
		if (inputString.startsWith("//", strIndex)) {
			charForward(2);
			while (strIndex < inputStringLength && (inputStringChars[strIndex] != '\n' && inputStringChars[strIndex] != '\r')) {
				charForward(1);
			}
			currentCharacter = 0;
			line++;
		}
	}

	private static boolean isWhitespace(char in) {
		return in == '\n' || in == '\t' || in == ' ' || in == '\r' || Character.isWhitespace(in);
	}

	private void charForward(int count) {
		currentCharacter += count;
		strIndex += count;
	}

	private boolean isCharLiteral() {
		return inputStringChars[strIndex] == '\'';
	}

	private char parseCharacterLiteral() {
		charForward(1);
		char charLit;
		if (inputStringChars[strIndex] == '\\') {
			charForward(1);
			String key = String.valueOf(inputStringChars[strIndex]);
			charLit = escapeSequences.getOrDefault(key, key).charAt(0);
		} else {
			charLit = inputStringChars[strIndex];
		}
		charForward(1);
		if (inputStringChars[strIndex] == '\'') {
			charForward(1);
		} else {
			error("Unterminated string literal.");
		}
		return charLit;
	}

	private boolean isIdentifier() {
		return Character.isAlphabetic(inputStringChars[strIndex]) || inputStringChars[strIndex] == '_';
	}

	private String parseIdent() {
		StringBuilder ident = new StringBuilder(64);
		while (strIndex < inputStringLength && isIdentifier()) {
			ident.append(inputStringChars[strIndex]);
			charForward(1);
		}
		return ident.toString();
	}

	private boolean isDigit() {
		return Character.isDigit(inputStringChars[strIndex]);
	}

	private boolean isFloating() {
		return IS_FLOATING.matcher(inputString.substring(strIndex)).find();
	}

	private boolean isString() {
		return inputStringChars[strIndex] == '"';
	}

	private void error(String message) {
		System.out.println("ERROR - Tokenizer: At line: " + line + ", character: " + currentCharacter + ", " + message);
	}

	@Override
	public String toString() {
		return "Tokenizer{" +
					   "inputString='" + inputString + '\'' +
					   ", inputStringChars=" + Arrays.toString(inputStringChars) +
					   ", inputStringLength=" + inputStringLength +
					   ", primitiveMap=" + primitiveMap +
					   ", escapeSequences=" + escapeSequences +
					   ", strIndex=" + strIndex +
					   ", line=" + line +
					   ", currentCharacter=" + currentCharacter +
					   '}';
	}
}
