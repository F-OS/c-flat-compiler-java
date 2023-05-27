/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.*;
import utils.*;
import visitor.*;

public final class Continue extends Statement {
	public Continue(Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "Continue";
	}

	@Override
	public String toString() {
		return "Continue{null}@(" + getLine() + ", " + getCharacter() + ")";
	}
}
