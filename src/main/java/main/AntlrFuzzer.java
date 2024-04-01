package main;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import parser.ANTLRv4Parser;
import visitors.Generator;

import java.io.IOException;

import static main.SingletonInjector.*;
import static utils.MainHelper.*;

public class AntlrFuzzer {

    public static void main(String[] args) throws IOException {

        ANTLRv4Parser.GrammarSpecContext entireTree = parse(Config.GRAMMAR_PATH);
        makeRuleMap(entireTree);

        ruleNameMap.keySet().forEach(depthFinder::depthOfRule);

        String result = String.valueOf(new Generator(Config.MAX_DEPTH).generate(Config.STARTING_RULE));


        ParseResult<CompilationUnit> parseResult = new JavaParser().parse(result);
        analyzeSyntax(parseResult);
        result = prettify(parseResult);
        System.out.println(result);
    }



}
