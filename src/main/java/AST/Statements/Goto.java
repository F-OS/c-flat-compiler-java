/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.*;
import utils.*;
import visitor.*;

public final class Goto extends Statement {
	public final String gotoident;

	public Goto(String gotoident, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.gotoident = gotoident;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "Goto";
	}

	@Override
	public String toString() {
		return "Continue{" + gotoident + "}@(" + getLine() + ", " + getCharacter() + ")";
	}
}
