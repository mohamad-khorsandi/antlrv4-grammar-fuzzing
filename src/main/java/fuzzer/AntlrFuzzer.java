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
import visitors.GenerateVisitor;

import java.io.IOException;

//todo check if map of ctx depth is really working(cache)

public class AntlrFuzzer {
    private final GenerateVisitor generator;

    public AntlrFuzzer(String grammar_path, Integer seed) throws IOException {
        GrammarSpecContext entireTree = parse(grammar_path);
        MapUtil<String, RuleSpecContext> rules = makeRuleMap(entireTree);
        generator = new GenerateVisitor(rules, seed);
    }

    public String fuzz(String stringRule, FuzzParams params) {
        return generator.generate(stringRule, params);
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
}
