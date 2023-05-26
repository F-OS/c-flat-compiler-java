/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package TypeAndSymbolAnnotator.Types;

import TypeAndSymbolAnnotator.Scope;
import TypeAndSymbolAnnotator.Type;

public final class ObjectType extends Type {
	private final String name;
	private final Scope associatedScope;

	public ObjectType(String name, Scope associatedScope) {
		this.name = name;
		this.associatedScope = associatedScope;
	}

	@Override
	public String toString() {
		return "Object" + (name != null ? " with name " + name : "");
	}
}
