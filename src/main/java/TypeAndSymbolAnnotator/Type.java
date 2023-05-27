/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator;

import java.util.*;

public abstract class Type {
	protected final List<Type> promotionlist = new ArrayList<>();

	@Override
	public abstract String toString();

	public boolean canPromoteTo(Type right) {
		return promotionlist.stream().anyMatch(t -> t == right);
	}
}
