/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.Expression;
import utils.Entry;
import visitor.Visitor;

import java.util.List;

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
}