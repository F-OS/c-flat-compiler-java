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

public final class Throw extends Statement {
	public final String ident;
	public final List<Expression> params;

	public Throw(String ident, List<Expression> params, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.ident = ident;
		this.params = params;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "Throw";
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Throw{ident=").append(ident).append(", param=(");
		for (Expression param : params) {
			sb.append(param).append(",");
		}
		sb.append(")}").append("@(").append(getLine()).append(", ").append(getCharacter()).append(")");
		return sb.toString();
	}
}
