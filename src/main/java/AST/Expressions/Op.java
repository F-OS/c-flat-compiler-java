/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST.Expressions;

import AST.*;
import AST.Expressions.OpEnums.*;

public abstract class Op extends Expression {
	protected Op(int line, int character) {
		super(line, character);
	}

	public abstract Object getOp();

	@Override
	public void assertIsConditional(String type) {
		if (!isaBooleanExpr(getOp())) {
			throw new RuntimeException("ERROR: Bad conditional. " + getOp().toString() + " is not a boolean in "
									   + type + " statement on line " + getLine() + ", character" +
									   getCharacter() + " .");
		}
	}

	/**
	 * Checks whether the given operand returns a boolean.
	 */
	private static boolean isaBooleanExpr(Object opEnum) {
		return opEnum == BinaryOps.LessThan || opEnum == BinaryOps.LessEqual || opEnum == BinaryOps.EqualTo
			   || opEnum == BinaryOps.GreaterEqual || opEnum == BinaryOps.NotEqualTo || opEnum == BinaryOps.GreaterThan
			   || opEnum == BinaryOps.And || opEnum == BinaryOps.Or || opEnum == UnaryOps.Not;
	}

}
