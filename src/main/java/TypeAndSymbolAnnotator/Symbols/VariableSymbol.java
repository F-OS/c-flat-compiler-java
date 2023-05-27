/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator.Symbols;

import TypeAndSymbolAnnotator.*;

public final class VariableSymbol extends Symbol {
	public final Type type;
	public boolean defined;

	public VariableSymbol(String name, Type type) {
		super(name);
		this.type = type;
	}

	@Override
	public String toString() {
		return "var " + name + ": " + type;
	}
}
