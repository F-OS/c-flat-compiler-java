/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.Statement;
import visitor.Visitor;

public final class Label extends Statement {
	public final String ident;

	public Label(String ident, int line, int character) {
		super(line, character);
		this.ident = ident;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}