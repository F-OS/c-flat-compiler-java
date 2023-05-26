/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator.Types;

import TypeAndSymbolAnnotator.Type;
import TypeAndSymbolAnnotator.Symbols.FunctionSymbol;

public final class LambdaType extends Type {
	final FunctionSymbol func;

	public LambdaType(FunctionSymbol func) {
		this.func = func;
	}

	@Override
	public String toString() {
		return "Anonymous Function" + (func != null ? " with signature " + func : "");
	}
}

