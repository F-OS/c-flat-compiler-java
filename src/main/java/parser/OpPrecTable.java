/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import AST.Expressions.OpEnums.BinaryOps;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import scanner.Token;
import utils.Entry;

import java.util.List;

public final class OpPrecTable {
	private static final List<List<Entry<Token.TokenType, BinaryOps>>> precedence = List.of(
			List.of(new Entry<>(Token.TokenType.OR, BinaryOps.Or)),
			List.of(new Entry<>(Token.TokenType.AND, BinaryOps.And)),
			List.of(new Entry<>(Token.TokenType.BITWISE_OR, BinaryOps.Bitwise_Or)),
			List.of(new Entry<>(Token.TokenType.BITWISE_XOR, BinaryOps.Bitwise_Xor)),
			List.of(new Entry<>(Token.TokenType.BITWISE_AND, BinaryOps.Bitwise_And)),
			List.of(new Entry<>(Token.TokenType.EQUALTO, BinaryOps.EqualTo),
					new Entry<>(Token.TokenType.NOTEQUALTO, BinaryOps.NotEqualTo)),
			List.of(
					new Entry<>(Token.TokenType.GREATERTHAN, BinaryOps.GreaterThan),
					new Entry<>(Token.TokenType.GREATEREQUAL, BinaryOps.GreaterEqual),
					new Entry<>(Token.TokenType.LESSTHAN, BinaryOps.LessThan),
					new Entry<>(Token.TokenType.LESSEQUAL, BinaryOps.LessEqual)
			),
			List.of(
					new Entry<>(Token.TokenType.BITWISE_LSHIFT, BinaryOps.Bitwise_LS),
					new Entry<>(Token.TokenType.BITWISE_RSHIFT, BinaryOps.Bitwise_RS)
			),
			List.of(new Entry<>(Token.TokenType.ADD, BinaryOps.Add), new Entry<>(Token.TokenType.SUB, BinaryOps.Sub)),
			List.of(new Entry<>(Token.TokenType.MUL, BinaryOps.Mul),
					new Entry<>(Token.TokenType.DIV, BinaryOps.Div),
					new Entry<>(Token.TokenType.MOD, BinaryOps.Mod)),
			List.of(new Entry<>(Token.TokenType.POW, BinaryOps.Pow))
	);
	private static final int tablesize = precedence.size();


	@Contract(pure = true)
	public static @Nullable List<Entry<Token.TokenType, BinaryOps>> getTable(int tableidx) {
		if (tableidx >= tablesize) {
			return null;
		}
		return precedence.get(tableidx);
	}

	public static boolean inTableAt(int prec, Token symb) {
		if (prec >= tablesize) {
			return false;
		}
		List<Entry<Token.TokenType, BinaryOps>> table = precedence.get(prec);
		for (Entry<Token.TokenType, BinaryOps> entry : table) {
			if (entry.key() == symb.type) {
				return true;
			}
		}
		return false;
	}

	static public BinaryOps mapSymbolToOp(int precedence, Token token) {
		for (Entry<Token.TokenType, BinaryOps> entry : getTable(precedence)) {
			if (entry.key() == token.type) {
				return entry.value();
			}
		}
		throw new IllegalArgumentException("Unknown token: " + token);
	}
}