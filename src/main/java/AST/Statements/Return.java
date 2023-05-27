/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.*;
import utils.*;
import visitor.*;

public final class Return extends Statement {
	public final Expression expr;

	public final boolean nullRet;

	public Return(Expression expr, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.expr = expr;
		nullRet = this.expr == null;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "Return";
	}

	@Override
	public String toString() {
		if (nullRet) {
			return "Return{expr=null}" + "@(" + getLine() + ", " + getCharacter() + ")";
		} else {
			return "Return{expr=" + expr + "}" + "@(" + getLine() + ", " + getCharacter() + ")";
		}
	}
}
