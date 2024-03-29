package main.java;
import main.java.exception_handling.HardErrorStrategy;
import main.java.parser.ANTLRv4Lexer;

import main.java.parser.ANTLRv4Parser;
import main.java.utils.MapUtil;
import main.java.utils.RandUtil;
import main.java.visitors.Generator;
import main.java.visitors.MinDepthFinder;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import java.io.IOException;

import static main.java.parser.ANTLRv4Parser.*;

public class Main {
    public static MapUtil<String, RuleSpecContext> ruleMap = new MapUtil<>();
    public static RandUtil rand = new RandUtil(Config.SEED);
    public static MapUtil<String, Integer> ruleDepth;
    public static MapUtil<AlternativeContext, Integer> altDepth;
    public static MapUtil<LabeledAltContext, Integer> labelAltDepth;
    public static MapUtil<LexerAltContext, Integer> lexerAltDepth;

    public static void main(String[] args) throws IOException {
        GrammarSpecContext entireTree = parse(Config.GRAMMAR_PATH);

        makeRuleMap(entireTree);
        makeDepthMap();
        System.out.println();
        String result = String.valueOf(new Generator(20).generate(Config.STARTING_RULE));
        result = result.replaceAll("(?<=[{};])", "\n");
        System.out.println(result);
    }

    public static void makeRuleMap (GrammarSpecContext entireTree) {
        for (RuleSpecContext rule : entireTree.rules().ruleSpec()) {
            if (rule.lexerRuleSpec() != null)
                ruleMap.putOrThrow(rule.lexerRuleSpec().TOKEN_REF().getText(), rule);
            else if (rule.parserRuleSpec() != null)
                ruleMap.putOrThrow(rule.parserRuleSpec().RULE_REF().getText(), rule);
        }
    }

    public static GrammarSpecContext parse(String grammarPath) throws IOException {
        CharStream in = CharStreams.fromFileName(grammarPath);
        ANTLRv4Lexer lexer = new ANTLRv4Lexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ANTLRv4Parser parser = new ANTLRv4Parser(tokens);
        parser.setErrorHandler(new HardErrorStrategy());
        return parser.grammarSpec();
    }

    public static void makeDepthMap() {
        MinDepthFinder minDepthFinder = new MinDepthFinder();
        ruleMap.keySet().forEach(minDepthFinder::ruleMinCache);

        ruleDepth = minDepthFinder.ruleCache;
        altDepth = minDepthFinder.altCache;
        labelAltDepth = minDepthFinder.labelAltCache;
        lexerAltDepth = minDepthFinder.lexerAltCache;
//        minDepthExtractor.findMinDepth("ifThenStatement");

    }
}