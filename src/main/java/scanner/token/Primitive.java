/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package scanner.token;

import scanner.Token;

/**
 * This class contains all tokens that do not store data,
 * such as tokens delimiting code blocks, statements and function parameters.
 *
 * @SuppressWarnings("SameParameterValue") is used because these classes are all created with reflection.
 */
@SuppressWarnings("MissingJavadoc")
public class Primitive {

	Primitive() throws InstantiationException {
		throw new InstantiationException("Primitive is abstract and only used to store static subclasses. Don't try to " +
												 "initialize it.");
	}

	@Override
	public final String toString() {
		return "Token{type=" + getClass().getSimpleName() + '}';
	}

	public static final class EndOfFile extends Token {
		public EndOfFile() {
			this(-1, -1);
		}

		public EndOfFile(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=EndOfFile, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class LParen extends Token {
		public LParen() {
			this(-1, -1);
		}

		LParen(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=LParen, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class RParen extends Token {
		public RParen() {
			this(-1, -1);
		}

		RParen(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=RParen, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class LBracket extends Token {
		public LBracket() {
			this(-1, -1);
		}

		LBracket(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=LBracket, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class RBracket extends Token {
		public RBracket() {
			this(-1, -1);
		}

		RBracket(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=RBracket, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class LBrace extends Token {
		public LBrace() {
			this(-1, -1);
		}

		LBrace(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=LBrace, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class RBrace extends Token {
		public RBrace() {
			this(-1, -1);
		}

		RBrace(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=RBrace, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Semicolon extends Token {
		public Semicolon() {
			this(-1, -1);
		}

		Semicolon(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Semicolon, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Comma extends Token {
		public Comma() {
			this(-1, -1);
		}

		Comma(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Comma, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Assign extends Token {
		public Assign() {
			this(-1, -1);
		}

		Assign(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Assign, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Dot extends Token {
		public Dot() {
			this(-1, -1);
		}

		Dot(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Dot, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class AddAssign extends Token {
		public AddAssign() {
			this(-1, -1);
		}

		AddAssign(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=AddAssign, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class SubAssign extends Token {
		public SubAssign() {
			this(-1, -1);
		}

		SubAssign(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=SubAssign, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class MulAssign extends Token {
		public MulAssign() {
			this(-1, -1);
		}

		MulAssign(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=MulAssign, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class DivAssign extends Token {
		public DivAssign() {
			this(-1, -1);
		}

		DivAssign(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=DivAssign, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class ModAssign extends Token {
		public ModAssign() {
			this(-1, -1);
		}

		ModAssign(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=ModAssign, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class PowAssign extends Token {
		public PowAssign() {
			this(-1, -1);
		}

		PowAssign(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=PowAssign, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class LShiftAssign extends Token {
		public LShiftAssign() {
			this(-1, -1);
		}

		LShiftAssign(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=LShiftAssign, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class RShiftAssign extends Token {
		public RShiftAssign() {
			this(-1, -1);
		}

		RShiftAssign(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=RShiftAssign, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class AndAssign extends Token {
		public AndAssign() {
			this(-1, -1);
		}

		AndAssign(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=AndAssign, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class OrAssign extends Token {
		public OrAssign() {
			this(-1, -1);
		}

		OrAssign(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=OrAssign, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class XorAssign extends Token {
		public XorAssign() {
			this(-1, -1);
		}

		XorAssign(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=XorAssign, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Inc extends Token {
		public Inc() {
			this(-1, -1);
		}

		Inc(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Inc, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Dec extends Token {
		public Dec() {
			this(-1, -1);
		}

		Dec(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Dec, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Add extends Token {
		public Add() {
			this(-1, -1);
		}

		Add(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Add, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Sub extends Token {
		public Sub() {
			this(-1, -1);
		}

		Sub(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Sub, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Mul extends Token {
		public Mul() {
			this(-1, -1);
		}

		Mul(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Mul, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Div extends Token {
		public Div() {
			this(-1, -1);
		}

		Div(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Div, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Pow extends Token {
		public Pow() {
			this(-1, -1);
		}

		Pow(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Pow, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Mod extends Token {
		public Mod() {
			this(-1, -1);
		}

		Mod(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Mod, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class GreaterThan extends Token {
		public GreaterThan() {
			this(-1, -1);
		}

		GreaterThan(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=GreaterThan, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class GreaterEqual extends Token {
		public GreaterEqual() {
			this(-1, -1);
		}

		GreaterEqual(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=GreaterEqual, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class EqualTo extends Token {
		public EqualTo() {
			this(-1, -1);
		}

		EqualTo(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=EqualTo, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class NotEqualTo extends Token {
		public NotEqualTo() {
			this(-1, -1);
		}

		NotEqualTo(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=NotEqualTo, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class LessEqual extends Token {
		public LessEqual() {
			this(-1, -1);
		}

		LessEqual(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=LessEqual, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class LessThan extends Token {
		public LessThan() {
			this(-1, -1);
		}

		LessThan(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=LessThan, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class And extends Token {
		public And() {
			this(-1, -1);
		}

		And(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=And, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Or extends Token {
		public Or() {
			this(-1, -1);
		}

		Or(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Or, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Not extends Token {
		public Not() {
			this(-1, -1);
		}

		Not(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Not, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class QMark extends Token {
		public QMark() {
			this(-1, -1);
		}

		QMark(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=QMark, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Colon extends Token {
		public Colon() {
			this(-1, -1);
		}

		Colon(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Colon, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Bitwise_And extends Token {
		public Bitwise_And() {
			this(-1, -1);
		}

		Bitwise_And(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Bitwise_And, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Bitwise_Or extends Token {
		public Bitwise_Or() {
			this(-1, -1);
		}

		Bitwise_Or(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Bitwise_Or, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Bitwise_Not extends Token {
		public Bitwise_Not() {
			this(-1, -1);
		}

		Bitwise_Not(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Bitwise_Not, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Bitwise_Xor extends Token {
		public Bitwise_Xor() {
			this(-1, -1);
		}

		Bitwise_Xor(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Bitwise_Xor, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Bitwise_LShift extends Token {
		public Bitwise_LShift() {
			this(-1, -1);
		}

		Bitwise_LShift(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Bitwise_LShift, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}

	@SuppressWarnings("SameParameterValue")
	public static final class Bitwise_RShift extends Token {
		public Bitwise_RShift() {
			this(-1, -1);
		}

		Bitwise_RShift(int curline, int curcharacter) {
			super(curline, curcharacter);
		}

		@Override
		public String toString() {
			return "Token{type=Bitwise_RShift, " + ", line=" + line + ", character=" + charnum + '}';
		}
	}
}