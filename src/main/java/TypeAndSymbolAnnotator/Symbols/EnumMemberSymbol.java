/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator.Symbols;

import TypeAndSymbolAnnotator.Symbol;

public final class EnumMemberSymbol extends Symbol {
	public final long id;

	public EnumMemberSymbol(String name, long id) {
		super(name);
		this.id = id;
	}

	@Override
	public String toString() {
		return "enum member " + name + " - " + id;
	}
}
