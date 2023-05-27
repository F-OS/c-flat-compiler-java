/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.*;
import utils.*;
import visitor.*;

import java.util.*;
import java.util.stream.*;

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

	@Override
	public String nodeToString() {
		return "Block";
	}

	@Override
	public String toString() {
		return "Block{" + statements.stream().map(Declaration::toString).collect(Collectors.joining(", ")) +
			   "}@(" + getLine() + ", " + getCharacter() + ")";
	}
}
