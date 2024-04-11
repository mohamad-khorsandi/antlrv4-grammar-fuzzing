package fuzzer;

import exception_handling.HardErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import parser.ANTLRv4Lexer;
import parser.ANTLRv4Parser;
import parser.ANTLRv4Parser.RuleSpecContext;
import parser.ANTLRv4Parser.GrammarSpecContext;
import utils.MapUtil;
import utils.PostProcessor;
import visitors.GenerateVisitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

//todo check if map of ctx depth is working(cache)

public class AntlrFuzzer {
    private final GenerateVisitor generator;

    public static void main(String[] args) throws IOException {
        AntlrFuzzer fuzzer = new AntlrFuzzer("src/main/resources/Java8.g4", 941295160);

        String result = fuzzer.fuzz("compilationUnit", 15, 2, .7);
        System.out.println(result);
        result = PostProcessor.prettify(result);
        System.out.println(result);
        PostProcessor.analyzeSyntax(result);
    }

    public AntlrFuzzer(String grammarPath, Integer seed) throws IOException {
        GrammarSpecContext entireTree = parse(grammarPath);
        MapUtil<String, RuleSpecContext> rules = makeRuleMap(entireTree);
        generator = new GenerateVisitor(rules, seed);
    }

    public AntlrFuzzer(InputStream grammarFile, Integer seed) throws IOException {
        GrammarSpecContext entireTree = parse(grammarFile);
        MapUtil<String, RuleSpecContext> rules = makeRuleMap(entireTree);
        generator = new GenerateVisitor(rules, seed);
    }

    public String fuzz(String startingRule, int maxDepth, double plusStarGaussianSigma, double questionBernoulliProp) {
        FuzzParams params = new FuzzParams(startingRule, maxDepth, plusStarGaussianSigma, questionBernoulliProp);
        return generator.generate(params);
    }

    public MapUtil<String, RuleSpecContext> makeRuleMap (GrammarSpecContext entireTree) {
        final MapUtil<String, RuleSpecContext> ruleMap = new MapUtil<>();
        for (RuleSpecContext rule : entireTree.rules().ruleSpec()) {
            if (rule.lexerRuleSpec() != null)
                ruleMap.put(rule.lexerRuleSpec().TOKEN_REF().getText(), rule);
            else if (rule.parserRuleSpec() != null)
                ruleMap.put(rule.parserRuleSpec().RULE_REF().getText(), rule);
        }
        return ruleMap;
    }

    private static GrammarSpecContext parse(String grammarPath) throws IOException {
        CharStream in = CharStreams.fromFileName(grammarPath);
        ANTLRv4Lexer lexer = new ANTLRv4Lexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ANTLRv4Parser parser = new ANTLRv4Parser(tokens);
        parser.setErrorHandler(new HardErrorStrategy());
        return parser.grammarSpec();
    }

    private static GrammarSpecContext parse(InputStream grammar) throws IOException {
        CharStream in = CharStreams.fromStream(grammar);
        ANTLRv4Lexer lexer = new ANTLRv4Lexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ANTLRv4Parser parser = new ANTLRv4Parser(tokens);
        parser.setErrorHandler(new HardErrorStrategy());
        return parser.grammarSpec();
    }
}
