/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator.Types;

import TypeAndSymbolAnnotator.*;

public final class IntegerType extends Type {
	private static final IntegerType instance = new IntegerType();

	private IntegerType() {
		promotionlist.add(FloatingType.getInstance());
	}

	public static IntegerType getInstance() {
		return instance;
	}

	@Override
	public String toString() {
		return "Integer";
	}
}
