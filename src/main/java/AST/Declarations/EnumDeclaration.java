/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Declarations;


import AST.Declaration;
import visitor.Visitor;

import java.util.List;

public final class EnumDeclaration extends Declaration {
	public final String name;

	public record EnumMember(String name, long id) {

	}

	public final List<EnumMember> members;

	public EnumDeclaration(String name, List<EnumMember> members, int line, int character) {
		super(line, character);
		this.name = name;
		this.members = members;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
