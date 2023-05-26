/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.Statement;
import utils.Entry;
import visitor.Visitor;

public final class Try extends Statement {
	public final Statement block;
	public final String catches;
	public final String catchesAs;
	public final Statement catch_;

	public Try(Statement block, String catches, String catchesAs, Statement catch_, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.block = block;
		this.catches = catches;
		this.catchesAs = catchesAs;
		this.catch_ = catch_;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
