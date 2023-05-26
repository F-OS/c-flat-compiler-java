/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Declarations;

import java.util.List;

import AST.Declaration;
import AST.Expression;
import utils.Entry;
import visitor.Visitor;

public final class ArrayDeclaration extends Declaration {
	public final TypedVar typedVar;
	public final long size;
	public final List<Expression> definition;

	public ArrayDeclaration(TypedVar typedvar, long size, List<Expression> definition, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		typedVar = typedvar;
		this.size = size;
		this.definition = definition;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}