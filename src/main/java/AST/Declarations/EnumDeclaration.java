/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Declarations;


import java.util.List;

import AST.Declaration;
import utils.Entry;
import visitor.Visitor;

public final class EnumDeclaration extends Declaration {
	public final String name;

	public record EnumMember(String name, long id) {

	}

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
}
