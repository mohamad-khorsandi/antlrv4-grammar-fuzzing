package utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import static fuzzer.StateLessData.log;

public class PostProcessor {
    public static void analyzeSyntax(String result) {
        ParseResult<CompilationUnit> parseResult = new JavaParser().parse(result);
        if (parseResult.isSuccessful()) {
            log.info("generated code is syntactically correct.");
        } else {
            parseResult.getProblems().forEach(System.err::println);
        }
    }

    public static String prettify(String result) {
        ParseResult<CompilationUnit> parseResult = new JavaParser().parse(result);
        CompilationUnit cu = parseResult.getResult().orElseThrow(() -> new RuntimeException("Failed to parse code"));

        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();

        return printer.print(cu);
    }
}
