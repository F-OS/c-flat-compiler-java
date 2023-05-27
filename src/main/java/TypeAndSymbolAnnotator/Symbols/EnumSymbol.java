/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator.Symbols;

import TypeAndSymbolAnnotator.*;

import java.util.*;

public final class EnumSymbol extends Symbol {
	public final List<String> members;

	public EnumSymbol(String name, List<String> members) {
		super(name);
		this.members = members;
	}

	@Override
	public String toString() {
		String membersString = String.join(", ", members);
		return "enum " + name + " {\n    members: " + membersString + "\n}";
	}
}
