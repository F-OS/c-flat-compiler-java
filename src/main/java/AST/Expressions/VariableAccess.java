/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;


import AST.Expression;
import utils.Entry;
import visitor.Visitor;


public final class VariableAccess extends Expression {
	public final String ident;

	public VariableAccess(String ident, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.ident = ident;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}

