/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.Expression;
import visitor.Visitor;

public final class IntegerNode extends Expression {
	public final long num;

	public IntegerNode(long num, int line, int character) {
		super(line, character);
		this.num = num;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
