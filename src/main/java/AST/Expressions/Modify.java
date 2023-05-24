/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.Expression;
import visitor.Visitor;

public final class Modify extends Expression {
	public final Expression ident;
	public final boolean returnPrevious;
	public final Expression modifyBy;

	public Modify(Expression ident, boolean returnPrevious, Expression modifyBy, int line, int character) {
		super(line, character);
		this.ident = ident;
		this.returnPrevious = returnPrevious;
		this.modifyBy = modifyBy;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
