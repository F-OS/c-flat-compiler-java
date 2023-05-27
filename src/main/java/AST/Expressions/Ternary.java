/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.*;
import utils.*;
import visitor.*;

public final class Ternary extends Expression {
	public final Expression condition;
	public final Expression consequent;
	public final Expression alternate;

	public Ternary(Expression condition, Expression consequent, Expression alternate, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.condition = condition;
		this.consequent = consequent;
		this.alternate = alternate;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "Ternary";
	}

	@Override
	public String toString() {
		return "Ternary{condition=" + condition.toString() + ", consequent=" + consequent.toString() + ", alternate=" + alternate.toString() +
			   "}@(" + getLine() + ", " + getCharacter() + ")";
	}

}
