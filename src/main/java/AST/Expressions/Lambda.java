/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.*;
import utils.*;
import visitor.*;

import java.util.*;
import java.util.stream.*;

public final class Lambda extends Expression {
	public final List<TypedVar> params;
	public final Statement block;
	public final String returnType;

	public Lambda(List<TypedVar> params, Statement block, String returnType, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.params = params;
		this.block = block;
		this.returnType = returnType;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "Lambda";
	}

	@Override
	public String toString() {
		return "Lambda{" +
			   "parameters=(" +
			   params.stream().map(x -> x.name() + ": " + x.type()).collect(Collectors.joining(", ")) +
			   ") -> " + returnType + "," +
			   ", body={" +
			   block.toString() +
			   "}}@(" + getLine() + ", " + getCharacter() + ")\n";
	}
}
