/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package AST;

import AST.Declarations.*;
import AST.Expressions.*;
import AST.Statements.*;
import TypeAndSymbolAnnotator.Type;
import visitor.Visitable;
import visitor.Visitor;

public abstract class ASTRoot implements Visitable {
	private final int line;
	private final int character;
	private Type associatedType;

	private boolean isTyped;

	protected ASTRoot(int line, int character) {
		this.line = line;
		this.character = character;
	}

	public Type getAssociatedType() {
		return associatedType;
	}

	public void setAssociatedType(Type associatedType) {
		if (isTyped) {
			throw new IllegalStateException("Object has already been typed.");
		}
		this.associatedType = associatedType;
	}

	@Override
	public abstract Object accept(Visitor visitor);

	public String nodeToString(ASTRoot node) {
		if (node instanceof ArrayDeclaration) {
			return "Array Declaration";
		} else if (node instanceof ClassDeclaration) {
			return "Class Declaration";
		} else if (node instanceof EnumDeclaration) {
			return "Enum Declaration";
		} else if (node instanceof FunctionDeclaration) {
			return "Function Declaration";
		} else if (node instanceof SimpleVarDeclaration) {
			return "Var Declaration";
		} else if (node instanceof Assignment) {
			return "Assignment";
		} else if (node instanceof Block) {
			return "Block";
		} else if (node instanceof Break) {
			return "Break";
		} else if (node instanceof Continue) {
			return "Continue";
		} else if (node instanceof DoWhile) {
			return "Do While";
		} else if (node instanceof For) {
			return "For";
		} else if (node instanceof ForEach) {
			return "For Each";
		} else if (node instanceof Goto) {
			return "Goto";
		} else if (node instanceof If) {
			return "If";
		} else if (node instanceof Label) {
			return "Label";
		} else if (node instanceof Return) {
			return "Return";
		} else if (node instanceof Switch) {
			return "Switch";
		} else if (node instanceof Throw) {
			return "Throw";
		} else if (node instanceof Try) {
			return "Try";
		} else if (node instanceof While) {
			return "While";
		} else if (node instanceof BinaryOp) {
			return "Binary Op";
		} else if (node instanceof Bool) {
			return "Boolean";
		} else if (node instanceof Call) {
			return "Function Call";
		} else if (node instanceof CharNode) {
			return "Char";
		} else if (node instanceof Floating) {
			return "Floating number";
		} else if (node instanceof IntegerNode) {
			return "Integer number";
		} else if (node instanceof Lambda) {
			return "Lambda";
		} else if (node instanceof ListAccess) {
			return "List Access";
		} else if (node instanceof Modify) {
			return "Modify";
		} else if (node instanceof ScopeOf) {
			return "Scope Of";
		} else if (node instanceof StringLit) {
			return "String";
		} else if (node instanceof UnaryOp) {
			return "Unary Op";
		} else if (node instanceof VariableAccess) {
			return "Variable Access";
		} else if (node instanceof StructDeclaration) {
			return "Struct Declaration";
		} else if (node instanceof Ternary) {
			return "Ternary";
		}
		return "";
	}

	public int getLine() {
		return line;
	}

	public int getCharacter() {
		return character;
	}

	public record TypedVar(String name, String type) {
	}
}

