/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.*;
import AST.Expressions.OpEnums.*;
import utils.*;
import visitor.*;

public final class UnaryOp extends Op {
	public final UnaryOps op;
	public final Expression inner;

	public UnaryOp(UnaryOps op, Expression inner, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.op = op;
		this.inner = inner;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public UnaryOps getOp() {
		return op;
	}

	@Override
	public String nodeToString() {
		return "UnaryOp";
	}

	@Override
	public String toString() {
		return "UnaryOp{op=" + op.toString() + ", inner=" + inner.toString() +
			   "}@(" + getLine() + ", " + getCharacter() + ")";
	}
}