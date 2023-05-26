/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.Expression;
import AST.Statement;
import utils.Entry;
import visitor.Visitor;

public final class If extends Statement {
	public final Expression conditional;
	public final Statement consequent;
	public final Statement alternate;

	public If(Expression conditional, Statement consequent, Statement alternate, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.conditional = conditional;
		this.consequent = consequent;
		this.alternate = alternate;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
