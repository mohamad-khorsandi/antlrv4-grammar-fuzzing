
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import parser.ANTLRv4Lexer;
import parser.ANTLRv4Parser;

import java.io.IOException;
import java.util.HashMap;

public class Main {
    static HashMap<String, ANTLRv4Parser.RuleSpecContext> ruleMap = new HashMap<>();
    static Generator generator = new Generator();

    public static void main(String[] args) throws IOException {
        CharStream in = CharStreams.fromFileName("src/resources/tarLang.g4");
        ANTLRv4Lexer lexer = new ANTLRv4Lexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ANTLRv4Parser parser = new ANTLRv4Parser(tokens);
        ANTLRv4Parser.GrammarSpecContext entireTree = parser.grammarSpec();

        makeRuleMap(entireTree, parser.getRuleNames());

        String result = String.valueOf(generator.generate(Config.STARTING_RULE));
        System.out.println("Evaluation result: " + result);
    }

    public static void makeRuleMap (ANTLRv4Parser.GrammarSpecContext entireTree, String[] ruleNames) {
        for (ANTLRv4Parser.RuleSpecContext rule : entireTree.rules().ruleSpec()) {
            ruleMap.put(rule.children.get(0).getChild(0).getText(), rule);
        }

    }
}