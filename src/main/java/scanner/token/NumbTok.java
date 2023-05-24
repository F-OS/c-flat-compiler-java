/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package scanner.token;

import scanner.Token;

/**
 * Class for storing tokens containing a number.
 */
public final class NumbTok extends Token {
	private final possible_states states;
	private final long integer;
	private final double floating;

	/**
	 * Creates a new character token at the specified line and character, with the specified value.
	 *
	 * @param curline      the line in the source code.
	 * @param curcharacter the character in the source code.
	 * @param flot         the value.
	 */
	public NumbTok(int curline, int curcharacter, double flot) {
		super(curline, curcharacter);
		states = possible_states.isfloating;
		floating = flot;
		integer = Long.MAX_VALUE;
	}

	/**
	 * Creates a new character token at the specified line and character, with the specified value.
	 *
	 * @param curline      the line in the source code.
	 * @param curcharacter the character in the source code.
	 * @param integ        the value.
	 */
	public NumbTok(int curline, int curcharacter, long integ) {
		super(curline, curcharacter);
		states = possible_states.isinteger;
		integer = integ;
		floating = Double.NaN;
	}

	/**
	 * @throws InstantiationException if used. NumbTok should not be used without a value, use instanceof instead.
	 */
	public NumbTok() throws InstantiationException {
		super(-1, -1);
		throw new InstantiationException("No-arg constructors not supported by NumbTok");
	}

	/**
	 * @return Whether NumbTok is an integer.
	 */
	public boolean isInt() {
		switch (states) {

			case isinteger -> {
				return true;
			}
			case isfloating -> {
				return false;
			}
			default -> throw new IllegalStateException("Number used without definition.");
		}
	}

	/**
	 * @return NumbTok as a double.
	 */
	public double getFloating() {
		if (states != possible_states.isfloating) {
			throw new IllegalStateException("Number token used as floating when not floating.");
		}
		return floating;
	}

	/**
	 * @return NumbTok as an integer.
	 */
	public long getInteger() {
		if (states != possible_states.isinteger) {
			throw new IllegalStateException("Number token used as integer when not integer.");
		}
		return integer;
	}

	/**
	 * @param o the object to compare to.
	 * @return Whether the two objects are equal.
	 */
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

	/**
	 * @return Token{type=Number, state=[integer|floating], [integer|floating]=numb, line=[line], character=[character]}
	 */
	public String toString() {
		switch (states) {

			case isinteger -> {
				return "Token{type=Number, state=integer, " +
							   "integer=" + integer +
							   ", line=" + line + ", character=" + charnum + '}';
			}
			case isfloating -> {
				return "Token{type=Number, state=floating, " +
							   "double=" + floating +
							   ", line=" + line + ", character=" + charnum + '}';

			}
			default -> throw new IllegalStateException("Unexpected value: " + states);
		}
	}

	/**
	 * @return A (hopefully unique hash code)
	 */
	@Override
	public int hashCode() {
		long temp;
		int result = line;
		result = 193 * result + charnum;
		result = 31 * result + (int) (integer ^ (integer >>> 32));
		temp = Double.doubleToLongBits(floating);
		result = 389 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	enum possible_states {isinteger, isfloating, isnull}
}