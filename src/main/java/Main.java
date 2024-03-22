package main.java;
import main.java.parser.ANTLRv4Lexer;
import main.java.parser.ANTLRv4Parser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import java.io.IOException;
import java.util.HashMap;

import org.antlr.v4.runtime.tree.ParseTreeWalker;
public class Main {
    public static HashMap<String, ANTLRv4Parser.RuleSpecContext> ruleMap = new HashMap<>();
    static Generator generator = new Generator();

    public static void main(String[] args) throws IOException {
        CharStream in = CharStreams.fromFileName(Config.INPUT_GRAMMAR);
        ANTLRv4Lexer lexer = new ANTLRv4Lexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ANTLRv4Parser parser = new ANTLRv4Parser(tokens);
        parser.setErrorHandler(new HardErrorStrategy());
        ANTLRv4Parser.GrammarSpecContext entireTree = parser.grammarSpec();

        makeRuleMap(entireTree);

        String result = String.valueOf(generator.generate(Config.STARTING_RULE));
        result = result.replaceAll("(?<=[{};])", "\n");
        System.out.println(result);
    }

    public static void makeRuleMap (ANTLRv4Parser.GrammarSpecContext entireTree) {
        for (ANTLRv4Parser.RuleSpecContext rule : entireTree.rules().ruleSpec())
            if (rule.lexerRuleSpec() != null)
                ruleMap.put(rule.lexerRuleSpec().TOKEN_REF().getText(), rule);
            else if (rule.parserRuleSpec() != null)
                ruleMap.put(rule.parserRuleSpec().RULE_REF().getText(), rule);


        FragmentExtractor listener = new FragmentExtractor(ruleMap);
        new ParseTreeWalker().walk(listener, entireTree);
    }
}