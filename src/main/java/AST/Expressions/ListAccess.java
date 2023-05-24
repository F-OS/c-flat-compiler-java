/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.Expression;
import visitor.Visitor;

public final class ListAccess extends Expression {
	public final String ident;
	public final Expression index;

	public ListAccess(String ident, Expression index, int line, int character) {
		super(line, character);
		this.ident = ident;
		this.index = index;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
