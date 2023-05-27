/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Statements;

import AST.*;
import utils.*;
import visitor.*;

public final class ForEach extends Statement {
	public final String iterval;
	public final String collectionvar;
	public final Statement body;

	public ForEach(String iterval, String collectionvar, Statement body, Entry<Integer, Integer> loc) {
		super(loc.key(), loc.value());
		this.iterval = iterval;
		this.collectionvar = collectionvar;
		this.body = body;
	}

	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public String nodeToString() {
		return "ForEach";
	}

	public String toString() {
		String iter = ", iterationVariable=" + iterval;
		String collection = ", collectionVariable=" + collectionvar;
		return "ForEach{" + iter + collection + ", body={" +
			   body.toString() +
			   "}}@(" + getLine() + ", " + getCharacter() + ")";
	}
}
