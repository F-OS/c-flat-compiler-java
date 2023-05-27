/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.*;
import utils.*;
import visitor.*;

public final class Modify extends Expression {
	public final Expression ident;
	public final boolean returnPrevious;
	public final Expression modifyBy;

	public Modify(Expression ident, boolean returnPrevious, Expression modifyBy, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.ident = ident;
		this.returnPrevious = returnPrevious;
		this.modifyBy = modifyBy;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toString() {
		return "Modify{" +
			   "ident=" + ident + ", by=" +
			   modifyBy.toString() + (returnPrevious ? ", returnPrevious=true" : ", returnPrevious=false") +
			   "}@(" + getLine() + ", " + getCharacter() + ")";
	}

	public String nodeToString() {
		return "Modify";
	}

}
