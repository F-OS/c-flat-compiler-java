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

public final class For extends Statement {
	public final Declaration initializer;
	public final Declaration conditional;
	public final Declaration iteration;
	public final Statement body;

	public For(Declaration initializer, Declaration conditional, Declaration iteration, Statement body, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.initializer = initializer;
		this.conditional = conditional;
		this.iteration = iteration;
		this.body = body;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
