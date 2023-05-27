/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator.Symbols;

import TypeAndSymbolAnnotator.*;

public final class ArraySymbol extends Symbol {
	public final int size;
	public final Type elementType;
	public boolean defined;

	public ArraySymbol(String name, int size, Type elementType) {
		super(name);
		this.size = size;
		this.elementType = elementType;
	}

	@Override
	public String toString() {
		return "array " + name + "[" + size + "] of " + elementType;
	}
}
