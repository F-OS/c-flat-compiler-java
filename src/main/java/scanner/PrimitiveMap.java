/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package scanner;

import scanner.token.Primitive;

import java.util.Map;

import static java.util.Map.entry;

enum PrimitiveMap {
	;
	private static final Map<String, Class> primitiveMap = Map.ofEntries(
			entry("<<=", Primitive.LShiftAssign.class),
			entry(">>=", Primitive.RShiftAssign.class),
			entry("**=", Primitive.PowAssign.class),
			entry("**", Primitive.Pow.class),
			entry("+=", Primitive.AddAssign.class),
			entry("-=", Primitive.SubAssign.class),
			entry("*=", Primitive.MulAssign.class),
			entry("/=", Primitive.DivAssign.class),
			entry("%=", Primitive.ModAssign.class),
			entry("&=", Primitive.AndAssign.class),
			entry("|=", Primitive.OrAssign.class),
			entry("^=", Primitive.XorAssign.class),
			entry("++", Primitive.Inc.class),
			entry("--", Primitive.Dec.class),
			entry(">>", Primitive.Bitwise_LShift.class),
			entry("<<", Primitive.Bitwise_RShift.class),
			entry("||", Primitive.Or.class),
			entry("&&", Primitive.And.class),
			entry("<=", Primitive.GreaterEqual.class),
			entry(">=", Primitive.LessEqual.class),
			entry("<", Primitive.GreaterThan.class),
			entry(">", Primitive.LessThan.class),
			entry("==", Primitive.EqualTo.class),
			entry("!=", Primitive.NotEqualTo.class),
			entry("^", Primitive.Bitwise_Xor.class),
			entry("~", Primitive.Bitwise_Not.class),
			entry("|", Primitive.Bitwise_Or.class),
			entry("&", Primitive.Bitwise_And.class),
			entry("!", Primitive.Not.class),
			entry("+", Primitive.Add.class),
			entry("-", Primitive.Sub.class),
			entry("*", Primitive.Mul.class),
			entry("?", Primitive.QMark.class),
			entry(":", Primitive.Colon.class),
			entry("/", Primitive.Div.class),
			entry("%", Primitive.Mod.class),
			entry(".", Primitive.Dot.class),
			entry("=", Primitive.Assign.class),
			entry(",", Primitive.Comma.class),
			entry(";", Primitive.Semicolon.class),
			entry("{", Primitive.LBrace.class),
			entry("}", Primitive.RBrace.class),
			entry("[", Primitive.LBracket.class),
			entry("]", Primitive.RBracket.class),
			entry("(", Primitive.LParen.class),
			entry(")", Primitive.RParen.class)
	);

	static Map<String, Class> get() {
		return PrimitiveMap.primitiveMap;
	}

}
