/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import AST.Expressions.OpEnums.*;
import org.jetbrains.annotations.*;
import scanner.*;
import scanner.Token.*;
import utils.*;

import java.util.*;

public final class OpPrecTable {
	private static final List<List<Entry<TokenType, BinaryOps>>> precedence = List.of(
			List.of(new Entry<>(TokenType.OR, BinaryOps.Or)),
			List.of(new Entry<>(TokenType.AND, BinaryOps.And)),
			List.of(new Entry<>(TokenType.BITWISE_OR, BinaryOps.Bitwise_Or)),
			List.of(new Entry<>(TokenType.BITWISE_XOR, BinaryOps.Bitwise_Xor)),
			List.of(new Entry<>(TokenType.BITWISE_AND, BinaryOps.Bitwise_And)),
			List.of(new Entry<>(TokenType.EQUALTO, BinaryOps.EqualTo),
					new Entry<>(TokenType.NOTEQUALTO, BinaryOps.NotEqualTo)),
			List.of(
					new Entry<>(TokenType.GREATERTHAN, BinaryOps.GreaterThan),
					new Entry<>(TokenType.GREATEREQUAL, BinaryOps.GreaterEqual),
					new Entry<>(TokenType.LESSTHAN, BinaryOps.LessThan),
					new Entry<>(TokenType.LESSEQUAL, BinaryOps.LessEqual)
			),
			List.of(
					new Entry<>(TokenType.BITWISE_LSHIFT, BinaryOps.Bitwise_LS),
					new Entry<>(TokenType.BITWISE_RSHIFT, BinaryOps.Bitwise_RS)
			),
			List.of(new Entry<>(TokenType.ADD, BinaryOps.Add), new Entry<>(TokenType.SUB, BinaryOps.Sub)),
			List.of(new Entry<>(TokenType.MUL, BinaryOps.Mul),
					new Entry<>(TokenType.DIV, BinaryOps.Div),
					new Entry<>(TokenType.MOD, BinaryOps.Mod)),
			List.of(new Entry<>(TokenType.POW, BinaryOps.Pow))
	);
	private static final int tablesize = precedence.size();

	private OpPrecTable() {
	}

	public static boolean inTableAt(int prec, Token symb) {
		if (prec >= tablesize) {
			return false;
		}
		List<Entry<TokenType, BinaryOps>> table = precedence.get(prec);
		for (Entry<TokenType, BinaryOps> entry : table) {
			if (entry.key() == symb.type) {
				return true;
			}
		}
		return false;
	}

	public static BinaryOps mapSymbolToOp(int precedence, Token token) {
		List<Entry<TokenType, BinaryOps>> table = getTable(precedence);
		if (table == null) {
			throw new IllegalArgumentException("Precedence table used without checking first if a valid precedence " +
											   "number was provided. Precedence number is " + precedence + " which " +
											   "is above the table size of " + tablesize);
		}
		for (Entry<TokenType, BinaryOps> entry : table) {
			if (entry.key() == token.type) {
				return entry.value();
			}
		}
		throw new IllegalArgumentException("Unknown token: " + token);
	}

	@Contract(pure = true)
	public static @Nullable List<Entry<TokenType, BinaryOps>> getTable(int tableidx) {
		if (tableidx >= tablesize) {
			return null;
		}
		return precedence.get(tableidx);
	}

	public static boolean hasTable(int precedence) {
		return precedence <= tablesize;
	}
}