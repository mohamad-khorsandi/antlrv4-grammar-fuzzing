package utils;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import static fuzzer.StateLessData.log;

public class MainHelper {
    public static void analyzeSyntax(ParseResult<CompilationUnit> parseResult) {
        if (parseResult.isSuccessful()) {
            log.info("generated code is syntactically correct.");
        } else {
            parseResult.getProblems().forEach(System.err::println);
            System.exit(0);
        }
    }

    public static String prettify(ParseResult<CompilationUnit> parseResult) {

        CompilationUnit cu = parseResult.getResult().orElseThrow(() -> new RuntimeException("Failed to parse code"));

        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();

        return printer.print(cu);
    }
}
