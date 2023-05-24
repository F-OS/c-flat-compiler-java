/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator;

import java.util.ArrayList;
import java.util.List;

public abstract class Type {
	public String toString() {
		return "!!BADTYPE!!";
	}

	protected List<Type> promotionlist = new ArrayList<>();

	public boolean canPromoteTo(Type right) {
		return promotionlist.stream().anyMatch(t -> t == right);
	}
}
