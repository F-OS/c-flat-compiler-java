/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator.Symbols;

import TypeAndSymbolAnnotator.*;

public final class ObjectSymbol extends Symbol {
	public final Scope associatedScope;
	public final Symbol derived;

	public ObjectSymbol(String name, Scope associatedScope, Symbol derived) {
		super(name);
		this.associatedScope = associatedScope;
		this.derived = derived;
	}

	@Override
	public String toString() {
		return "object " + name + " of " + derived.name + " ";
	}
}
