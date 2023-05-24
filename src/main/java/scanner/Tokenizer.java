/*
 * Copyright (c) 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of  MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package scanner;

import scanner.token.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Tokenizer {
	private static final int INITIAL_TOK_LEN = 1024;
	private static final Pattern IS_FLOATING = Pattern.compile("\\d+\\.\\d+");
	private static final Pattern GET_INTEGER = Pattern.compile("\\b(0x[0-9a-fA-F]+|\\d+)\\b");
	private static final Pattern GET_DECIMAL = Pattern.compile("\\b(0x[0-9a-fA-F]+|\\d+)\\.(0x[0-9a-fA-F]+|\\d+)\\b");

	private final String str;
	private final char[] strBytes;
	private final int strLen;
	private final Map<String, Class<?>> primitiveMap;
	private final Map<String, String> escapeSequences;

	private int strIdx;
	private int line;
	private int character;


	public Tokenizer(String strIn) {
		str = strIn;
		strLen = str.length();
		strBytes = str.toCharArray();
		primitiveMap = PrimitiveMap.get();
		escapeSequences = Map.ofEntries(
				Map.entry("n", "\n"),
				Map.entry("r", "\r"),
				Map.entry("t", "\t"),
				Map.entry("b", "\b"),
				Map.entry("\\", "\\"),
				Map.entry("\"", "\"")
		);
		strIdx = 0;
		line = 0;
		character = 0;
	}

	public Tokenizer() throws InstantiationException {
		throw new InstantiationException("No-arg constructors not supported by Tokenizer");
	}

	public List<Token> tokenize() {
		List<Token> tokens = new ArrayList<>(INITIAL_TOK_LEN);
		while (strIdx < strLen) {
			skipWhitespace();
			skipComments();
			tokens.add(getNextToken());
		}
		tokens.add(new Primitive.EndOfFile(line, character));
		return tokens;
	}

	private Token getNextToken() {
		Token result;
		if (matchesPrimitive()) {
			Map.Entry<String, Class<?>> newtok = getPrimitiveToken();
			result = createTokenClassChecked(newtok.getValue());
			charForward(newtok.getKey().length());
		} else if (isCharLiteral()) {
			result = new CharTok(line, character, parseCharacterLiteral());
		} else if (isIdentifier()) {
			result = new Ident(line, character, parseIdent());
		} else if (isDigit()) {
			if (isFloating()) {
				result = new NumbTok(line, character, parseDouble());
			} else {
				result = new NumbTok(line, character, parseInteger());
			}
		} else if (isString()) {
			result = new StringTok(line, character, parseString());
		} else {
			result = new Unimplemented(line, character, strBytes[strIdx]);
			charForward(1);
		}
		return result;
	}

	private Token createTokenClassChecked(Class<?> value) {
		if (Token.class.isAssignableFrom(value)) {
			Class<Token> klass = (Class<Token>) value;
			return createTokenClass(klass);
		} else {
			throw new AssertionError("Non-primitive tokens should never be included in token mappings.");
		}
	}

	private boolean matchesPrimitive() {
		for (Map.Entry<String, Class<?>> item : primitiveMap.entrySet()) {
			if (str.startsWith(item.getKey(), strIdx)) {
				return true;
			}
		}
		return false;
	}

	private Map.Entry<String, Class<?>> getPrimitiveToken() {
		Map.Entry<String, Class<?>> longest = Map.entry("", Object.class);
		for (Map.Entry<String, Class<?>> item : primitiveMap.entrySet()) {
			if (str.startsWith(item.getKey(), strIdx) && item.getKey().length() > longest.getKey().length()) {
				longest = item;
			}
		}
		return longest;
	}

	private <T extends Token> T createTokenClass(Class<Token> tokenClass) {
		try {
			return (T) tokenClass.getDeclaredConstructor(int.class, int.class).newInstance(line, character);
		} catch (Exception e) {
			// Handle any exceptions that may occur during instantiation
			e.printStackTrace();
			return null;
		}
	}

	private String parseString() {
		StringBuilder stringLiteral = new StringBuilder(64);
		charForward(1);
		while (true) {
			if (strBytes[strIdx] == '\\') {
				charForward(1);
				String key = String.valueOf(strBytes[strIdx]);
				if (escapeSequences.containsKey(key)) {
					charForward(1);
					stringLiteral.append(escapeSequences.get(key).charAt(0));
				} else {
					stringLiteral.append(key.charAt(0));
				}
			} else if (strBytes[strIdx] == '\n' || strBytes[strIdx] == '\r') {
				error("Unterminated String Literal.");
				break;
			} else if (strBytes[strIdx] == '"') {
				break;
			} else {
				stringLiteral.append(strBytes[strIdx]);
			}
			charForward(1);
			if (strIdx > strLen) {
				error("Unterminated String Literal.");
				break;
			}
		}
		return stringLiteral.toString();
	}


	private long parseInteger() {
		Matcher match = GET_INTEGER.matcher(str.substring(strIdx));
		if (match.find()) {
			charForward(match.group(0).length());
			return Long.parseLong(match.group(0));
		} else {
			throw new AssertionError("Invalid integer even though isNumber provided assurance. This should never happen");
		}
	}

	private double parseDouble() {
		Matcher match = GET_DECIMAL.matcher(str.substring(strIdx));
		if (match.find()) {
			charForward(match.group(0).length());
			return Double.parseDouble(match.group(0));
		} else {
			throw new AssertionError("Invalid decimal even though isFloating provided assurance. This should never happen");
		}
	}

	private void skipWhitespace() {
		while (strIdx < strLen && isWhitespace(strBytes[strIdx])) {
			if (strBytes[strIdx] == '\n' || strBytes[strIdx] == '\r') {
				charForward(1);
				character = 0;
				line++;
			} else {
				charForward(1);
			}
		}
	}


	private void skipComments() {
		if (str.startsWith("//", strIdx)) {
			charForward(2);
			while (strIdx < strLen && (strBytes[strIdx] != '\n' && strBytes[strIdx] != '\r')) {
				charForward(1);
			}
			character = 0;
			line++;
		}
	}

	private static boolean isWhitespace(char in) {
		return in == '\n' || in == '\t' || in == ' ' || in == '\r' || Character.isWhitespace(in);
	}

	private void charForward(int i) {
		character += i;
		strIdx += i;
	}

	private boolean isCharLiteral() {
		return strBytes[strIdx] == '\'';
	}

	private char parseCharacterLiteral() {
		charForward(1);
		char charLit;
		if (strBytes[strIdx] == '\\') {
			charForward(1);
			String key = String.valueOf(strBytes[strIdx]);
			charLit = escapeSequences.getOrDefault(key, key).charAt(0);
		} else {
			charLit = strBytes[strIdx];
		}
		charForward(1);
		if (strBytes[strIdx] == '\'') {
			charForward(1);
		} else {
			error("Unterminated string literal.");
		}
		return charLit;
	}

	private boolean isIdentifier() {
		return Character.isAlphabetic(strBytes[strIdx]) || strBytes[strIdx] == '_';
	}

	private String parseIdent() {
		StringBuilder ident = new StringBuilder(64);
		while (strIdx < strLen && isIdentifier()) {
			ident.append(strBytes[strIdx]);
			charForward(1);
		}
		return ident.toString();
	}

	private boolean isDigit() {
		return Character.isDigit(strBytes[strIdx]);
	}

	private boolean isFloating() {
		return IS_FLOATING.matcher(str.substring(strIdx)).matches();
	}

	private boolean isString() {
		return strBytes[strIdx] == '"';
	}

	private void error(String s) {
		System.out.println("ERROR - Tokenizer: At line:" + line + ", character: " + character + ", " + s);
	}

	@Override
	public String toString() {
		return "Tokenizer{" + "str='" + str + '\'' +
					   ", strBytes=" + Arrays.toString(strBytes) +
					   ", strLen=" + strLen +
					   ", primitiveMap=" + primitiveMap +
					   ", escapeSequences=" + escapeSequences +
					   ", strIdx=" + strIdx +
					   ", line=" + line +
					   ", character=" + character +
					   '}';
	}
}
