/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package scanner.token;

import scanner.Token;

public final class NumbTok extends Token {
	private final possible_states states;
	private final long integer;
	private final double floating;

	public NumbTok(int line, int character, double floating) {
		super(line, character);
		states = possible_states.isfloating;
		this.floating = floating;
		integer = Long.MAX_VALUE;
	}

	public NumbTok(int line, int character, long integer) {
		super(line, character);
		states = possible_states.isinteger;
		this.integer = integer;
		floating = Double.NaN;
	}

	public NumbTok(int line, int character) {
		super(line, character);
		states = possible_states.isnull;
		integer = Long.MAX_VALUE;
		floating = Double.NaN;
	}

	public NumbTok() {
		super(-1, -1);
		states = possible_states.isnull;
		integer = Long.MAX_VALUE;
		floating = Double.NaN;
	}

	public boolean isInt() {
		switch (states) {

			case isinteger -> {
				return true;
			}
			case isfloating -> {
				return false;
			}
			case isnull -> {
				throw new IllegalStateException("Number token used without being defined.");
			}
			default -> throw new IllegalStateException("Unexpected value: " + states);
		}
	}

	public double getFloating() {
		if (states != possible_states.isinteger) {
			throw new IllegalStateException("Number token used as floating when not floating.");
		}
		return floating;
	}

	public long getInteger() {
		if (states != possible_states.isfloating) {
			throw new IllegalStateException("Number token used as integer when not integer.");
		}
		return integer;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		NumbTok number = (NumbTok) o;

		switch (states) {

			case isinteger -> {
				if (((NumbTok) o).states != possible_states.isinteger) {
					return false;
				}
				if (integer == number.integer) return true;
			}
			case isfloating -> {
				if (((NumbTok) o).states != possible_states.isfloating) {
					return false;
				}
				if (Double.compare(number.floating, floating) != 0) return true;
			}
			case isnull -> {
				return false;
			}
		}
		return false;
	}

	public String toString() {
		switch (states) {

			case isinteger -> {
				return "Token{type=Number, state=integer, " +
							   "integer=" + integer +
							   '}';
			}
			case isfloating -> {
				return "Token{type=Number, state=floating, " +
							   "double=" + floating +
							   '}';

			}
			case isnull -> {
				return "Token{type=Number, state=undef" + '}';
			}
			default -> throw new IllegalStateException("Unexpected value: " + states);
		}
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = states.hashCode();
		result = 31 * result + (int) (integer ^ (integer >>> 32));
		temp = Double.doubleToLongBits(floating);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	enum possible_states {isinteger, isfloating, isnull}
}