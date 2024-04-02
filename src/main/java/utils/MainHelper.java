package utils;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import exception_handling.HardErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import parser.ANTLRv4Lexer;
import parser.ANTLRv4Parser;

import java.io.IOException;
import static fuzzer.SingletonInjector.*;

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

    public static void makeRuleMap (ANTLRv4Parser.GrammarSpecContext entireTree) {
        for (ANTLRv4Parser.RuleSpecContext rule : entireTree.rules().ruleSpec()) {
            if (rule.lexerRuleSpec() != null)
                ruleNameMap.put(rule.lexerRuleSpec().TOKEN_REF().getText(), rule);
            else if (rule.parserRuleSpec() != null)
                ruleNameMap.put(rule.parserRuleSpec().RULE_REF().getText(), rule);
        }
    }

    public static ANTLRv4Parser.GrammarSpecContext parse(String grammarPath) throws IOException {
        CharStream in = CharStreams.fromFileName(grammarPath);
        ANTLRv4Lexer lexer = new ANTLRv4Lexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ANTLRv4Parser parser = new ANTLRv4Parser(tokens);
        parser.setErrorHandler(new HardErrorStrategy());
        return parser.grammarSpec();
    }

}
