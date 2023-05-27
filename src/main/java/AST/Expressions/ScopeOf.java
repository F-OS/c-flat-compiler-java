/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.*;
import utils.*;
import visitor.*;

public final class ScopeOf extends Expression {
	public final String inScope;
	public final Expression perform;

	public ScopeOf(String inScope, Expression perform, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.inScope = inScope;
		this.perform = perform;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "ScopeOf";
	}

	@Override
	public String toString() {
		return "ScopeOf{" +
			   "inScope=" + perform + ", do=" +
			   perform.toString() +
			   "}@(" + getLine() + ", " + getCharacter() + ")";
	}

}
