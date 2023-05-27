/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.*;
import utils.*;
import visitor.*;

import java.util.*;

public final class Switch extends Statement {
	public final Expression switchon;
	public final List<Entry<Expression, Statement>> cases;

	public Switch(Expression switchon, List<Entry<Expression, Statement>> cases, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.switchon = switchon;
		this.cases = cases;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "Switch";
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Switch{expr=").append(switchon).append(", ");
		for (Entry<Expression, Statement> entry : cases) {
			sb.append("Case{condition=").append(entry.key()).append(", block=").append(entry.value()).append("},");
		}
		sb.append("}").append("@(").append(getLine()).append(", ").append(getCharacter()).append(")");
		return sb.toString();
	}

}
