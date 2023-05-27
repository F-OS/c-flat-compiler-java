/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST;

public abstract class Expression extends Statement {
	protected Expression(int line, int character) {
		super(line, character);
	}

	public abstract String nodeToString();

	public void assertIsConditional(String type)
	{
		throw new RuntimeException("ERROR: Bad conditional. " + toString()
								   + " is not a binary conditional in " + type + " statement on line " +
								   getLine()
								   + ", character" + getCharacter() + " .");
	}
}


