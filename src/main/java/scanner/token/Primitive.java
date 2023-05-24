/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package scanner.token;

import scanner.Token;

import java.util.ArrayList;

/**
 * This class contains all tokens which do not store data, such as tokens delimiting code blocks, statements, and function parameters.
 */
@SuppressWarnings("MissingJavadoc")
public class Primitive {

	@Override
	public final String toString() {
		return "Token{type=" + getClass().getSimpleName() + '}';
	}

	public static final class EndOfFile extends Token {
		public EndOfFile() {
			this(-1, -1);
		}

		public EndOfFile(int line, int character) {
			super(line, character);
		}

		public EndOfFile(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class LParen extends Token {
		public LParen() {
			this(-1, -1);
		}

		public LParen(int line, int character) {
			super(line, character);
		}

		public LParen(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class RParen extends Token {
		public RParen() {
			this(-1, -1);
		}

		public RParen(int line, int character) {
			super(line, character);
		}

		public RParen(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class LBracket extends Token {
		public LBracket() {
			this(-1, -1);
		}

		public LBracket(int line, int character) {
			super(line, character);
		}

		public LBracket(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class RBracket extends Token {
		public RBracket() {
			this(-1, -1);
		}

		public RBracket(int line, int character) {
			super(line, character);
		}

		public RBracket(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class LBrace extends Token {
		public LBrace() {
			this(-1, -1);
		}

		public LBrace(int line, int character) {
			super(line, character);
		}

		public LBrace(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class RBrace extends Token {
		public RBrace() {
			this(-1, -1);
		}

		public RBrace(int line, int character) {
			super(line, character);
		}

		public RBrace(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class Semicolon extends Token {
		public Semicolon() {
			this(-1, -1);
		}

		public Semicolon(int line, int character) {
			super(line, character);
		}

		public Semicolon(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class Comma extends Token {
		public Comma() {
			this(-1, -1);
		}

		public Comma(int line, int character) {
			super(line, character);
		}

		public Comma(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class Assign extends Token {
		public Assign() {
			this(-1, -1);
		}

		public Assign(int line, int character) {
			super(line, character);
		}

		public Assign(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class Dot extends Token {
		public Dot() {
			this(-1, -1);
		}

		public Dot(int line, int character) {
			super(line, character);
		}

		public Dot(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class AddAssign extends Token {
		public AddAssign() {
			this(-1, -1);
		}

		public AddAssign(int line, int character) {
			super(line, character);
		}

		public AddAssign(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class SubAssign extends Token {
		public SubAssign() {
			this(-1, -1);
		}

		public SubAssign(int line, int character) {
			super(line, character);
		}

		public SubAssign(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class MulAssign extends Token {
		public MulAssign() {
			this(-1, -1);
		}

		public MulAssign(int line, int character) {
			super(line, character);
		}

		public MulAssign(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class DivAssign extends Token {
		public DivAssign() {
			this(-1, -1);
		}

		public DivAssign(int line, int character) {
			super(line, character);
		}

		public DivAssign(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class ModAssign extends Token {
		public ModAssign() {
			this(-1, -1);
		}

		public ModAssign(int line, int character) {
			super(line, character);
		}

		public ModAssign(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class PowAssign extends Token {
		public PowAssign() {
			this(-1, -1);
		}

		public PowAssign(int line, int character) {
			super(line, character);
		}

		public PowAssign(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class LShiftAssign extends Token {
		public LShiftAssign() {
			this(-1, -1);
		}

		public LShiftAssign(int line, int character) {
			super(line, character);
		}

		public LShiftAssign(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class RShiftAssign extends Token {
		public RShiftAssign() {
			this(-1, -1);
		}

		public RShiftAssign(int line, int character) {
			super(line, character);
		}

		public RShiftAssign(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class AndAssign extends Token {
		public AndAssign() {
			this(-1, -1);
		}

		public AndAssign(int line, int character) {
			super(line, character);
		}

		public AndAssign(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class OrAssign extends Token {
		public OrAssign() {
			this(-1, -1);
		}

		public OrAssign(int line, int character) {
			super(line, character);
		}

		public OrAssign(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class XorAssign extends Token {
		public XorAssign() {
			this(-1, -1);
		}

		public XorAssign(int line, int character) {
			super(line, character);
		}

		public XorAssign(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class Inc extends Token {
		public Inc() {
			this(-1, -1);
		}

		public Inc(int line, int character) {
			super(line, character);
		}

		public Inc(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class Dec extends Token {
		public Dec() {
			this(-1, -1);
		}

		public Dec(int line, int character) {
			super(line, character);
		}

		public Dec(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class Add extends Token {
		public Add() {
			this(-1, -1);
		}

		public Add(int line, int character) {
			super(line, character);
		}

		public Add(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class Sub extends Token {
		public Sub() {
			this(-1, -1);
		}

		public Sub(int line, int character) {
			super(line, character);
		}

		public Sub(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class Mul extends Token {
		public Mul() {
			this(-1, -1);
		}

		public Mul(int line, int character) {
			super(line, character);
		}

		public Mul(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class Div extends Token {
		public Div() {
			this(-1, -1);
		}

		public Div(int line, int character) {
			super(line, character);
		}

		public Div(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class Pow extends Token {
		public Pow() {
			this(-1, -1);
		}

		public Pow(int line, int character) {
			super(line, character);
		}

		public Pow(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class Mod extends Token {
		public Mod() {
			this(-1, -1);
		}

		public Mod(int line, int character) {
			super(line, character);
		}

		public Mod(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class GreaterThan extends Token {
		public GreaterThan() {
			this(-1, -1);
		}

		public GreaterThan(int line, int character) {
			super(line, character);
		}

		public GreaterThan(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class GreaterEqual extends Token {
		public GreaterEqual() {
			this(-1, -1);
		}

		public GreaterEqual(int line, int character) {
			super(line, character);
		}

		public GreaterEqual(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class EqualTo extends Token {
		public EqualTo() {
			this(-1, -1);
		}

		public EqualTo(int line, int character) {
			super(line, character);
		}

		public EqualTo(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class NotEqualTo extends Token {
		public NotEqualTo() {
			this(-1, -1);
		}

		public NotEqualTo(int line, int character) {
			super(line, character);
		}

		public NotEqualTo(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class LessEqual extends Token {
		public LessEqual() {
			this(-1, -1);
		}

		public LessEqual(int line, int character) {
			super(line, character);
		}

		public LessEqual(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class LessThan extends Token {
		public LessThan() {
			this(-1, -1);
		}

		public LessThan(int line, int character) {
			super(line, character);
		}

		public LessThan(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class And extends Token {
		public And() {
			this(-1, -1);
		}

		public And(int line, int character) {
			super(line, character);
		}

		public And(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class Or extends Token {
		public Or() {
			this(-1, -1);
		}

		public Or(int line, int character) {
			super(line, character);
		}

		public Or(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class Not extends Token {
		public Not() {
			this(-1, -1);
		}

		public Not(int line, int character) {
			super(line, character);
		}

		public Not(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class QMark extends Token {
		public QMark() {
			this(-1, -1);
		}

		public QMark(int line, int character) {
			super(line, character);
		}

		public QMark(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class Colon extends Token {
		public Colon() {
			this(-1, -1);
		}

		public Colon(int line, int character) {
			super(line, character);
		}

		public Colon(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class Bitwise_And extends Token {
		public Bitwise_And() {
			this(-1, -1);
		}

		public Bitwise_And(int line, int character) {
			super(line, character);
		}

		public Bitwise_And(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class Bitwise_Or extends Token {
		public Bitwise_Or() {
			this(-1, -1);
		}

		public Bitwise_Or(int line, int character) {
			super(line, character);
		}

		public Bitwise_Or(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class Bitwise_Not extends Token {
		public Bitwise_Not() {
			this(-1, -1);
		}

		public Bitwise_Not(int line, int character) {
			super(line, character);
		}

		public Bitwise_Not(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}
	}

	public static final class Bitwise_Xor extends Token {
		public Bitwise_Xor() {
			this(-1, -1);
		}

		public Bitwise_Xor(int line, int character) {
			super(line, character);
		}

		public Bitwise_Xor(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class Bitwise_LShift extends Token {
		public Bitwise_LShift() {
			this(-1, -1);
		}

		public Bitwise_LShift(int line, int character) {
			super(line, character);
		}

		public Bitwise_LShift(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}

	public static final class Bitwise_RShift extends Token {
		public Bitwise_RShift() {
			this(-1, -1);
		}

		public Bitwise_RShift(int line, int character) {
			super(line, character);
		}

		public Bitwise_RShift(ArrayList<Object> in) {
			super((Integer) (in.get(0)), (Integer) (in.get(1)));
		}

	}
}