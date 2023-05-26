/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.Expression;
import AST.Statement;
import visitor.Visitor;

import java.util.List;

public final class Throw extends Statement {
	public final String ident;
	public final List<Expression> params;

	public Throw(String ident, List<Expression> params, int line, int character) {
		super(line, character);
		this.ident = ident;
		this.params = params;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}