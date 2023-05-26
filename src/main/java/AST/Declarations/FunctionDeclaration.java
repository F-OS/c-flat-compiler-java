/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Declarations;

import java.util.List;

import AST.Declaration;
import AST.Statement;
import utils.Entry;
import visitor.Visitor;

public final class FunctionDeclaration extends Declaration {
	public String name;
	public List<TypedVar> parameters;
	public String returnType;
	public Statement body;

	public FunctionDeclaration(String name, List<TypedVar> parameters, String returnType, Statement body, Entry<Integer,
																													   Integer> loc) {
		super(loc.key(), loc.value());
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