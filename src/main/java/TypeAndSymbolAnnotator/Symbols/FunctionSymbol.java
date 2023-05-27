/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator.Symbols;

import AST.ASTRoot.*;
import AST.*;
import TypeAndSymbolAnnotator.*;

import java.util.*;
import java.util.stream.*;

public final class FunctionSymbol extends Symbol {
	public final List<TypedVar> params;
	public final String returnType;
	public final List<Declaration> decls;

	public FunctionSymbol(String name, List<TypedVar> params, String returnType, List<Declaration> decls) {
		super(name);
		this.params = params;
		this.returnType = returnType;
		this.decls = decls;
	}

	@Override
	public String toString() {
		String paramsString = params.stream()
									  .map(TypedVar::toString)
									  .collect(Collectors.joining(", "));
		return name + ": (" + paramsString + ") -> " + returnType;
	}
}
