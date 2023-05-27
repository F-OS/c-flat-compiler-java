/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.*;
import utils.*;
import visitor.*;

public final class For extends Statement {
	public final Declaration initializer;
	public final Declaration conditional;
	public final Declaration iteration;
	public final Statement body;

	public For(
			Declaration initializer, Declaration conditional, Declaration iteration,
			Statement body,
			Entry<Integer, Integer> loc
	) {
		super(loc.key(), loc.value());
		this.initializer = initializer;
		this.conditional = conditional;
		this.iteration = iteration;
		this.body = body;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "For";
	}

	@Override
	public String toString() {
		String initalization = initializer != null ? "initalization=" + initializer : "";
		String condit = conditional != null ? ", conditional=" + conditional : "";
		String inc = iteration != null ? ", iteration=" + iteration : "";
		return "For{" + initalization + condit + inc + ", body={" +
			   body.toString() +
			   "}}@(" + getLine() + ", " + getCharacter() + ")";
	}
}
