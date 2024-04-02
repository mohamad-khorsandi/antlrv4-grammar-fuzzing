package fuzzer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import parser.ANTLRv4Parser;
import visitors.Generator;

import java.io.IOException;

import static fuzzer.FuzzerConfig.GRAMMAR_PATH;
import static fuzzer.SingletonInjector.depthFinder;
import static fuzzer.SingletonInjector.ruleNameMap;

import static utils.MainHelper.*;

public class AntlrFuzzer {

    public static void fuzz() throws IOException {
        ANTLRv4Parser.GrammarSpecContext entireTree = parse(GRAMMAR_PATH);
        makeRuleMap(entireTree);

        ruleNameMap.keySet().forEach(depthFinder::depthOfRule);

        String result = String.valueOf(new Generator(FuzzerConfig.MAX_DEPTH).generate(FuzzerConfig.STARTING_RULE));

        ParseResult<CompilationUnit> parseResult = new JavaParser().parse(result);
        analyzeSyntax(parseResult);
        result = prettify(parseResult);
        System.out.println(result);
    }



}
