package main.java;
import main.java.parser.ANTLRv4Lexer;

import main.java.parser.ANTLRv4Parser;
import main.java.utils.MapUtil;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
//todo does this work correct? (ebnfSuffix |)

//    element
//    : labeledElement (ebnfSuffix |)
//    | atom (ebnfSuffix |)
//    | ebnf
//    | actionBlock (QUESTION predicateOptions?)?
//    ;
import static main.java.Config.SEED;
import static main.java.parser.ANTLRv4Parser.*;

public class Main {
    public static MapUtil<String, RuleSpecContext> ruleMap = new MapUtil<>();
    public static MapUtil<String, Integer> depthMap = new MapUtil<>();
    public static Random random;
    static {
        if (SEED == null) random = new Random();
        else random = new Random(SEED);
    }


    public static void main(String[] args) throws IOException {
        GrammarSpecContext entireTree = parse(Config.GRAMMAR_PATH);

        makeRuleMap(entireTree);
        makeDepthMap();
        System.out.println();
//        String result = String.valueOf(new Generator().generate(Config.STARTING_RULE));
//        result = result.replaceAll("(?<=[{};])", "\n");
//        System.out.println(result);
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
        MinDepthExtractor minDepthExtractor = new MinDepthExtractor(depthMap);
//        minDepthExtractor.findMinDepth("ifThenStatement");
        ruleMap.keySet().forEach(minDepthExtractor::findMinDepth);
    }
}