/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.Declaration;
import AST.Statement;
import utils.Entry;
import visitor.Visitor;

import java.util.List;

public final class Block extends Statement {
	public final List<Declaration> statements;

	public Block(List<Declaration> statements, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.statements = statements;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
