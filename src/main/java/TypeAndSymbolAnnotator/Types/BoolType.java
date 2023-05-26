/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator.Types;

import TypeAndSymbolAnnotator.Type;

public final class BoolType extends Type {
	public BoolType() {
		promotionlist.add(IntegerType.getInstance());
	}

	public String toString() {
		return "Boolean";
	}
}