/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Declarations;

import AST.Declaration;
import AST.Statements.Block;
import visitor.Visitor;

import java.util.List;

public final class FunctionDeclaration extends Declaration {
	public String name;
	public List<TypedVar> parameters;
	public String returnType;
	public Block body;

	public FunctionDeclaration(String name, List<TypedVar> parameters, String returnType, Block body, int line, int character) {
		super(line, character);
		this.name = name;
		this.parameters = parameters;
		this.returnType = returnType;
		this.body = body;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
}