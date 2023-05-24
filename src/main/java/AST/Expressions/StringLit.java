/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.Expression;
import visitor.Visitor;

public final class StringLit extends Expression {
	public final String str;

	public StringLit(String str, int line, int character) {
		super(line, character);
		this.str = str;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
