/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.*;
import utils.*;
import visitor.*;

import java.util.*;
import java.util.stream.*;

public final class Call extends Expression {
	public final String func;
	public final List<Expression> params;

	public Call(String func, List<Expression> params, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.func = func;
		this.params = params;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "Call";
	}

	@Override
	public String toString() {
		return "Call{name=" + func + ",\nparams=(" + params.stream().map(Expression::toString).collect(Collectors.joining(", ")) +
			   ")\n}@(" + getLine() + ", " + getCharacter() + ")";
	}

}
