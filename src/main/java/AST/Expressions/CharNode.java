/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.*;
import utils.*;
import visitor.*;

public final class CharNode extends Expression {
	public final char value;

	public CharNode(char chr, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.value = chr;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return "Char{" + value + "}@(" + getLine() + ", " + getCharacter() + ")";
	}

	@Override
	public String nodeToString() {
		return "CharNode";
	}
}
