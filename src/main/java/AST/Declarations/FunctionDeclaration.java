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

public final class FunctionDeclaration extends Declaration {
	public final String name;
	public final List<TypedVar> parameters;
	public final String returnType;
	public final Statement body;

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

	@Override
	public String nodeToString() {
		return "FunctionDeclaration";
	}

	@Override
	public String toString() {
		String bodyStr = "{" + body.toString() + "}";
		bodyStr += "@(" + getLine() + ", " + getCharacter() + ")";
		return "FuncDeclaration{" +
			   "name=" +
			   name +
			   ", parameters=(" +
			   parameters.stream().map(x -> x.name() + ": " + x.type()).collect(Collectors.joining(", ")) +
			   ") -> " + returnType + ", " +
			   "body=" +
			   bodyStr +
			   "}@(" + getLine() + ", " + getCharacter() + ")\n";
	}

}