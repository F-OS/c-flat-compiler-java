/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator.Types;

import TypeAndSymbolAnnotator.*;

public final class EnumMemberType extends Type {
	private final String name;
	private final long id;

	public EnumMemberType(String name, long id) {
		this.name = name;
		this.id = id;
	}

	@Override
	public String toString() {
		return "Enum member with name " + name + " and ID " + id;
	}
}
