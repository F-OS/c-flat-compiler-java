/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator.Symbols;

import TypeAndSymbolAnnotator.*;

public final class StructSymbol extends Symbol {
	public final Scope associatedScope;

	public StructSymbol(String name, Scope associatedScope) {
		super(name);
		this.associatedScope = associatedScope;
	}

	@Override
	public String toString() {
		return "struct " + name + " {\n    " + associatedScope + "\n}";
	}
}
