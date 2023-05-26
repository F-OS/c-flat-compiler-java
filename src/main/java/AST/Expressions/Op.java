/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.Expression;
import visitor.Visitor;

public abstract class Op extends Expression {
	protected Op(int line, int character) {
		super(line, character);
	}

	@Override
	public abstract Object accept(Visitor visitor);

	public abstract Object getOp();
}
