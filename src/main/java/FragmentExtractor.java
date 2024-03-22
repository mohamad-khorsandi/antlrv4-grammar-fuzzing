package main.java;

import main.java.parser.ANTLRv4Parser;
import main.java.parser.ANTLRv4ParserBaseListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentExtractor extends ANTLRv4ParserBaseListener {
    public FragmentExtractor(HashMap<String, ANTLRv4Parser.RuleSpecContext> ruleMap) {
        this.ruleMap = ruleMap;
    }

    HashMap<String, ANTLRv4Parser.RuleSpecContext> ruleMap;

    @Override
    public void enterParserRuleSpec(ANTLRv4Parser.ParserRuleSpecContext ctx) {
        if (ctx.ruleModifiers() != null) {
            boolean isFragment = ctx.ruleModifiers().ruleModifier()
                    .stream().anyMatch(modif -> modif.FRAGMENT() != null);

            if (isFragment)
                ruleMap.put(ctx.RULE_REF().getText(), (ANTLRv4Parser.RuleSpecContext) ctx.getParent());
        }
    }

    @Override
    public void enterLexerRuleSpec(ANTLRv4Parser.LexerRuleSpecContext ctx) {
        if (ctx.FRAGMENT() != null)
            ruleMap.put(ctx.TOKEN_REF().getText(), (ANTLRv4Parser.RuleSpecContext) ctx.getParent());
    }
}