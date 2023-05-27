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

public final class ClassDeclaration extends Declaration {
	public final String name;
	public final List<Declaration> members;
	public final List<String> inheritsFrom;

	public ClassDeclaration(
			String name, List<Declaration> members,
			List<String> inheritsFrom, Entry<Integer, Integer> loc
	) {
		super(loc.key(), loc.value());
		this.name = name;
		this.members = members;
		this.inheritsFrom = inheritsFrom;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		String inheritance;
		if (inheritsFrom != null && !inheritsFrom.isEmpty()) {
			inheritance = "{" + inheritsFrom.stream().map(String::toString).collect(Collectors.joining(", ")) + "}";
		} else {
			inheritance = "{}";
		}
		inheritance += "@(" + getLine() + ", " + getCharacter() + ")";
		String classMembers;
		if (members != null && !members.isEmpty()) {
			classMembers = "{" + members.stream().map(Declaration::toString).collect(Collectors.joining(", ")) + "}";
		} else {
			classMembers = "{}";
		}
		classMembers += "@(" + getLine() + ", " + getCharacter() + ")";
		return "ClassDeclaration{name=" + name + ", members=" + classMembers + ", inheritsFrom=" + inheritance + "}@(" + getLine() + ", " +
			   getCharacter() + ")";
	}

	@Override
	public String nodeToString() {
		return "ClassDeclaration";
	}
}