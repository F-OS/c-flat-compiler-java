/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

import AST.Expressions.OpEnums.BinaryOps;
import scanner.Token;
import scanner.token.Primitive.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class OperatorPrecedenceTable {
	private final List<List<Entry<Class<? extends Token>, BinaryOps>>> precedence = List.of(
			List.of(new Entry<>(Or.class, BinaryOps.Or)),
			List.of(new Entry<>(And.class, BinaryOps.And)),
			List.of(new Entry<>(Bitwise_Or.class, BinaryOps.Bitwise_Or)),
			List.of(new Entry<>(Bitwise_Xor.class, BinaryOps.Bitwise_Xor)),
			List.of(new Entry<>(Bitwise_And.class, BinaryOps.Bitwise_And)),
			List.of(new Entry<>(EqualTo.class, BinaryOps.EqualTo), new Entry<>(NotEqualTo.class, BinaryOps.NotEqualTo)),
			List.of(
					new Entry<>(GreaterThan.class, BinaryOps.GreaterThan),
					new Entry<>(GreaterEqual.class, BinaryOps.GreaterEqual),
					new Entry<>(LessThan.class, BinaryOps.LessThan),
					new Entry<>(LessEqual.class, BinaryOps.LessEqual)
			),
			List.of(
					new Entry<>(Bitwise_LShift.class, BinaryOps.Bitwise_LS),
					new Entry<>(Bitwise_RShift.class, BinaryOps.Bitwise_RS)
			),
			List.of(new Entry<>(Add.class, BinaryOps.Add), new Entry<>(Sub.class, BinaryOps.Sub)),
			List.of(new Entry<>(Mul.class, BinaryOps.Mul),
					new Entry<>(Div.class, BinaryOps.Div),
					new Entry<>(Mod.class, BinaryOps.Mod)),
			List.of(new Entry<>(Pow.class, BinaryOps.Pow))
	);
	private final int tablesize = precedence.size();

	List<Entry<Class<? extends Token>, BinaryOps>> getTable(int tableidx) {
		if (tableidx >= tablesize) {
			return List.of();
		}
		return precedence.get(tableidx);
	}

	public boolean inTableAt(int prec, Token symb) {
		if (prec >= tablesize) {
			return false;
		}
		List<Entry<Class<? extends Token>, BinaryOps>> table = precedence.get(prec);
		for (Entry<Class<? extends Token>, BinaryOps> entry : table) {
			if (entry.getKey().isInstance(symb)) {
				return true;
			}
		}
		return false;
	}

	public BinaryOps mapSymbolToOp(int precedence, Token token) {
		for (Entry<Class<? extends Token>, BinaryOps> entry : getTable(precedence)) {
			if (entry.getKey().isInstance(token)) {
				return entry.getValue();
			}
		}
		throw new IllegalArgumentException("Unknown token: " + token);
	}

	private static class Entry<K, V> {
		private final K key;
		private final V value;

		Entry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		K getKey() {
			return key;
		}

		V getValue() {
			return value;
		}
	}
}