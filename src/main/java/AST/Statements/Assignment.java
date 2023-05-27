/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.*;
import utils.*;
import visitor.*;

public final class Assignment extends Statement {
	public final Expression ident;
	public final Expression expr;

	public Assignment(Expression ident, Expression expr, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.ident = ident;
		this.expr = expr;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "Assignment";
	}

	@Override
	public String toString() {
		return "Assignment{ident=" + ident.toString() + ", value=" + expr.toString() +
			   "}@(" + getLine() + ", " + getCharacter() + ")";
	}
}
