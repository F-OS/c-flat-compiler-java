/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.*;
import utils.*;
import visitor.*;

public final class Floating extends Expression {
	public final double value;

	public Floating(double num, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.value = num;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "Floating";
	}

	@Override
	public String toString() {
		return "Floating{" + value + "}@(" + getLine() + ", " + getCharacter() + ")";
	}

}
