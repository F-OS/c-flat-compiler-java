import AST.*;
import parser.*;
import scanner.*;
import utils.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class Main {
	public static void main(String[] args) {
		if (args.length > 1) {
			System.out.println("Usage: interpreter [script]");
			System.exit(1);
		} else if (args.length == 1) {
			runFile(args[0]);
		} else {
			runPrompt();
		}
	}

	private static void runFile(String path) {
		String code = "";
		try {
			List<String> lines = Files.readAllLines(Path.of(path));
			code = String.join("\n", lines);
		} catch (IOException e) {
			System.out.println("The file " + path + " is not valid or you do not have correct permissions.");
		}
		runcode(code);
	}

	private static void runPrompt() {
		Scanner reader = new Scanner(System.in, StandardCharsets.UTF_8);
		System.out.print("> ");
		String line = reader.nextLine();
		do {
			runcode(line);
			System.out.print("> ");
			line = reader.nextLine();
		} while (line != null && !line.isEmpty());
		reader.close();
	}

	private static void runcode(String line) {
		List<Token> tokens = Tokenizer.tokenize(line);
		List<Declaration> declTree = parseProgram(tokens);
		for (Declaration decl : declTree) {
			String formattedAST = ASTFormatter.formatAST(decl.toString());
			System.out.println(formattedAST);
		}
	}

	private static List<Declaration> parseProgram(List<Token> tokens) {
		List<Declaration> decls = new ArrayList<>(128);
		ParsingContext context = new ParsingContext(tokens);
		do {
			Declaration decl = DeclarationParser.parseDeclaration(context);
			decls.add(decl);
		} while (context.isEmpty());
		return decls;
	}
}