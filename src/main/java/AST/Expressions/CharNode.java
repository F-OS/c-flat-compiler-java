/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.Expression;
import utils.Entry;
import visitor.Visitor;

public final class CharNode extends Expression {
	public final char char_;

	public CharNode(char char_, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.char_ = char_;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
