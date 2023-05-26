/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.Expression;
import AST.Expressions.OpEnums.BinaryOps;
import utils.Entry;
import visitor.Visitor;

public final class BinaryOp extends Op {
	public final Expression left;
	public final BinaryOps op;
	public final Expression right;

	public BinaryOp(Expression left, BinaryOps op, Expression right, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.left = left;
		this.op = op;
		this.right = right;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public BinaryOps getOp() {
		return op;
	}
}