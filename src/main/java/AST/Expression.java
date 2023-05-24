/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST;

import visitor.Visitor;

public abstract class Expression extends Statement {
	protected Expression(int line, int character) {
		super(line, character);
	}

	public abstract Object accept(Visitor visitor);
}

