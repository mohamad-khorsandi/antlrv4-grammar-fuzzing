package main;

import parser.ANTLRv4Parser;
import visitors.Generator;

import java.io.IOException;

import static main.SingletonInjector.*;
import static utils.MainHelper.makeRuleMap;
import static utils.MainHelper.parse;

public class AntlrFuzzer {

    public static void main(String[] args) throws IOException {

        ANTLRv4Parser.GrammarSpecContext entireTree = parse(Config.GRAMMAR_PATH);
        makeRuleMap(entireTree);

        ruleNameMap.keySet().forEach(depthFinder::depthOfRule);

        String result = String.valueOf(new Generator(Config.MAX_DEPTH).generate(Config.STARTING_RULE));
        System.out.println(result);
//        ParseResult<CompilationUnit> parseResult = new JavaParser().parse(result);
//        analyzeSyntax(parseResult);
//        result = prettify(parseResult);
//        System.out.println(result);
    }



}
