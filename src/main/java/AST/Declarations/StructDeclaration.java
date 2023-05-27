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

public final class StructDeclaration extends Declaration {
	public final String name;
	public final List<Declaration> members;

	public StructDeclaration(String name, List<Declaration> members, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.name = name;
		this.members = members;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "StructDeclaration";
	}

	public String toString() {
		String bodyMembers;
		if (members != null && !members.isEmpty()) {
			bodyMembers = "{" + members.stream().map(Declaration::toString).collect(Collectors.joining(", ")) + "}";
		} else {
			bodyMembers = "{}";
		}
		bodyMembers += "@(" + getLine() + ", " + getCharacter() + ")";
		return "StructDeclaration{" +
			   "name=" +
			   name +
			   ", members=" +
			   bodyMembers +
			   "}@(" + getLine() + ", " + getCharacter() + ")";
	}
}