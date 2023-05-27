/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.*;
import utils.*;
import visitor.*;

public final class DoWhile extends Statement {
	public final Expression conditional;
	public final Statement body;

	public DoWhile(Expression conditional, Statement body, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.conditional = conditional;
		this.body = body;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "DoWhile";
	}

	@Override
	public String toString() {
		return "DoWhile{conditional=" + conditional.toString() + ", body={" +
			   body.toString() +
			   "}}@(" + getLine() + ", " + getCharacter() + ")";
	}
}
