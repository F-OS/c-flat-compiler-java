/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package utils;

public class ASTFormatter {
	private static final String INDENTATION = "\t";

	private ASTFormatter() {
	}

	public static String formatAST(String rawAST) {
		StringBuilder formattedAST = new StringBuilder();
		int indentLevel = 0;

		for (int i = 0; i < rawAST.length(); i++) {
			char c = rawAST.charAt(i);

			if (c == '{') {
				formattedAST.append('{').append('\n');
				indentLevel++;
				appendIndentation(formattedAST, indentLevel);
			} else if (c == '}') {
				formattedAST.append('\n');
				indentLevel--;
				appendIndentation(formattedAST, indentLevel);
				formattedAST.append(c);

				// Check if the closing brace is followed by a location annotation
				int locationStartIndex = rawAST.indexOf("@(", i);
				int locationEndIndex = rawAST.indexOf(')', locationStartIndex);

				if (locationStartIndex != -1 && locationEndIndex != -1) {
					String location = rawAST.substring(locationStartIndex, locationEndIndex + 1);
					formattedAST.append(location);
					// Skip the location annotation.
					i = locationEndIndex;
				}
			} else if (c == ',') {
				formattedAST.append(c).append('\n');
				appendIndentation(formattedAST, indentLevel);

			} else if (!Character.isWhitespace(c)) {
				formattedAST.append(c);
			}
		}

		return formattedAST.toString();
	}

	private static void appendIndentation(StringBuilder stringBuilder, int indentLevel) {
		for (int i = 0; i < indentLevel; i++) {
			stringBuilder.append(INDENTATION);
		}
	}
}