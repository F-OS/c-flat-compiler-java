/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.Expression;
import AST.Statement;
import visitor.Visitor;

import java.util.List;

import utils.Entry;

public final class Switch extends Statement {
	public final Expression switchon;
	public final List<Entry<Expression, Statement>> cases;

	public Switch(Expression switchon, List<Entry<Expression, Statement>> cases, int line, int character) {
		super(line, character);
		this.switchon = switchon;
		this.cases = cases;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}