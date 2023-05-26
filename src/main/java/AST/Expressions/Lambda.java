/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.Expression;
import AST.Statement;
import utils.Entry;
import visitor.Visitor;

import java.util.List;

public final class Lambda extends Expression {
	public final List<TypedVar> params;
	public final Statement block;
	public final String returnType;

	public Lambda(List<TypedVar> params, Statement block, String returnType, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.params = params;
		this.block = block;
		this.returnType = returnType;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}