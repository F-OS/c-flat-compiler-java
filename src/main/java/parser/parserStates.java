/*
 * Copyright (c) 2023.
 * This file is part of the c-flat-compiler-java, which is released under the GPL-3.
 * See LICENSE or go to https://www.gnu.org/licenses/gpl-3.0.en.html for full license details.
 */

package parser;

enum parserStates {
	PARSER_EXPRESSION_STAGE,
	PARSER_STATEMENT_STAGE,
	PARSER_DECLARATION_STAGE,
	UNLABELED_PARSER_STATE
}
