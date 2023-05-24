/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Declarations;

import AST.ASTRoot;
import AST.Declaration;
import visitor.Visitor;

import java.util.List;

public final class StructDeclaration extends Declaration {
	public final String name;
	public final List<Declaration> members;

	public StructDeclaration(String name, List<Declaration> members, int line, int character) {
		super(line, character);
		this.name = name;
		this.members = members;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}