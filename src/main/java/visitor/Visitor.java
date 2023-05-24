/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package visitor;

public interface Visitor {
	Object visit(Lambda node);

	Object visit(BinaryOp node);

	Object visit(UnaryOp node);

	Object visit(ListAccess node);

	Object visit(VariableAccess node);

	Object visit(Modify node);

	Object visit(ScopeOf node);

	Object visit(Call node);

	Object visit(IntegerNode node);

	Object visit(Floating node);

	Object visit(Bool node);

	Object visit(StringLit node);

	Object visit(CharNode node);

	Object visit(If node);

	Object visit(For node);

	Object visit(ForEach node);

	Object visit(While node);

	Object visit(DoWhile node);

	Object visit(Continue node);

	Object visit(Break node);

	Object visit(Label node);

	Object visit(Switch node);

	Object visit(Assignment node);

	Object visit(Return node);

	Object visit(Goto node);

	Object visit(Try node);

	Object visit(Throw node);

	Object visit(ExprStatement node);

	Object visit(Block node);

	Object visit(SimpleVarDeclaration node);

	Object visit(ArrayDeclaration node);

	Object visit(EnumDeclaration node);

	Object visit(ClassDeclaration node);

	Object visit(StructDeclaration node);

	Object visit(FunctionDeclaration node);

	Object visit(Ternary node);
}