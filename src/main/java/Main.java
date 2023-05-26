import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import AST.Statement;
import parser.StatementParser;
import scanner.Token;
import scanner.Tokenizer;

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
		Main.runcode(code);
	}

	private static void runPrompt() {
		Scanner reader = new Scanner(System.in, StandardCharsets.UTF_8);
		while (true) {
			System.out.print("> ");
			String line = reader.nextLine();
			if (line != null && !line.isEmpty()) {
				runcode(line);
			} else {
				break;
			}
		}
	}

	private static void runcode(String line) {
		List<Token> tokens = Tokenizer.tokenize(line);
		Statement exprTree = StatementParser.parseStatement(tokens).key();
		System.out.println(exprTree);
	}
}