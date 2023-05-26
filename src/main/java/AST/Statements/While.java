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

public final class While extends Statement {
	public final Expression conditional;
	public final Statement body;

	public While(Expression conditional, Statement body, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.conditional = conditional;
		this.body = body;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
