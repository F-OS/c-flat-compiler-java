/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator.Symbols;

import TypeAndSymbolAnnotator.*;
import com.sun.source.tree.Scope;

import java.util.*;
import java.util.stream.*;

public final class ClassSymbol extends Symbol {
	public final List<ClassSymbol> inherit;
	public final Scope associatedScope;

	public ClassSymbol(String name, List<ClassSymbol> inherit, Scope associatedScope) {
		super(name);
		this.inherit = inherit;
		this.associatedScope = associatedScope;
	}

	@Override
	public String toString() {
		String inheritString = inherit.stream()
									   .map(ClassSymbol::toString)
									   .collect(Collectors.joining(", "));
		return "class " + name + " : " + inheritString + " {\n    " + associatedScope + "\n}";
	}
}
