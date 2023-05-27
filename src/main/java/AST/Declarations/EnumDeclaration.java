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

public final class EnumDeclaration extends Declaration {
	public final String name;
	public final List<EnumMember> members;

	public EnumDeclaration(String name, List<EnumMember> members, Entry<Integer, Integer> loc) {
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
		return "EnumDeclaration";
	}

	@Override
	public String toString() {
		String enumMembers;
		if (members != null && !members.isEmpty()) {
			enumMembers = "{" + members.stream().map(x -> "[" + x.name + ": " + x.id + "]").collect(Collectors.joining(", ")) + "}";
		} else {
			enumMembers = "{}";
		}
		enumMembers += "@(" + getLine() + ", " + getCharacter() + ")";
		return "EnumDeclaration{name=" + name + ", members=" + enumMembers + "}@(" + getLine() + ", " + getCharacter() + ")";
	}

	public record EnumMember(String name, long id) {
		@Override
		public String toString() {
			return "{" + name + ", " + id + "}";
		}
	}
}
