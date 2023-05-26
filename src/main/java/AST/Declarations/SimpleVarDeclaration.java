/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Declarations;

import AST.Declaration;
import AST.Expression;
import utils.Entry;
import visitor.Visitor;

public final class SimpleVarDeclaration extends Declaration {
	public final TypedVar typedVar;
	public final Expression definition;

	public SimpleVarDeclaration(TypedVar variable, Expression definition, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		typedVar = variable;
		this.definition = definition;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}