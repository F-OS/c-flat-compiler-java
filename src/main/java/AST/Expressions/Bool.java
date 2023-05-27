/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.*;
import utils.*;
import visitor.*;

public final class Bool extends Expression {
	public final boolean value;

	public Bool(boolean bool, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.value = bool;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "Bool";
	}

	@Override
	public String toString() {
		return "BoolNode{" + (value ? "true" : "false") +
			   "}@(" + getLine() + ", " + getCharacter() + ")";
	}

	@Override
	public void assertIsConditional(String type) {
		System.out.println("WARNING: Conditional on line " + getLine() + " in statement " + type + " always evaluates to "
						   + (value ? "true" : "false"));
	}
}
