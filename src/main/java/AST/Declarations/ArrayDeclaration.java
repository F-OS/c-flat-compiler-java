/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Declarations;

import AST.*;
import utils.*;
import visitor.*;

import java.util.*;
import java.util.stream.*;

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

	@Override
	public String toString() {
		String defs;
		if (definition == null || definition.isEmpty()) {
			defs = "{}";
		} else {
			defs = "{" + definition.stream().map(Expression::toString).collect(Collectors.joining(", ")) + "}";
		}
		defs += "@(" + getLine() + ", " + getCharacter() + ")";
		return "ArrayDeclaration{typedVar=" + typedVar.toString() + ", size=" + size + ",definition=" + defs + "}@(" + getLine() + ", " +
			   getCharacter() + ")";
	}

	@Override
	public String nodeToString() {
		return "ArrayDeclaration";
	}
}