/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST;

import TypeAndSymbolAnnotator.*;
import visitor.*;

public abstract class ASTRoot implements Visitable {
	private final int line;
	private final int character;
	private Type associatedType;

	private boolean isTyped;

	protected ASTRoot(int line, int character) {
		this.line = line;
		this.character = character;
	}

	public Type getAssociatedType() {
		return associatedType;
	}

	public void setAssociatedType(Type associatedType) {
		if (isTyped) {
			throw new IllegalStateException("Object " + this + " has already been typed.");
		}
		this.associatedType = associatedType;
	}

	public abstract String nodeToString();

	public int getLine() {
		return line;
	}

	public int getCharacter() {
		return character;
	}

	public record TypedVar(String name, String type) {
	}
}

