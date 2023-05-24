/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package utils;

public class Entry<K, V> {
	private final K key;
	private final V value;

	public Entry(K key, V value) {
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