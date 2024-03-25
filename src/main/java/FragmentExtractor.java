package main.java;

import main.java.parser.ANTLRv4Parser;
import main.java.parser.ANTLRv4ParserBaseListener;
import main.java.utils.MapUtil;


public class FragmentExtractor extends ANTLRv4ParserBaseListener {
    public FragmentExtractor(MapUtil<String, ANTLRv4Parser.RuleSpecContext> ruleMap) {
        this.ruleMap = ruleMap;
    }

    MapUtil<String, ANTLRv4Parser.RuleSpecContext> ruleMap;

    @Override
    public void enterParserRuleSpec(ANTLRv4Parser.ParserRuleSpecContext ctx) {
        if (ctx.ruleModifiers() != null) {
            boolean isFragment = ctx.ruleModifiers().ruleModifier()
                    .stream().anyMatch(modif -> modif.FRAGMENT() != null);

            if (isFragment)
                ruleMap.putOrThrow(ctx.RULE_REF().getText(), (ANTLRv4Parser.RuleSpecContext) ctx.getParent());
        }
    }

    @Override
    public void enterLexerRuleSpec(ANTLRv4Parser.LexerRuleSpecContext ctx) {
        if (ctx.FRAGMENT() != null)
            ruleMap.putOrThrow(ctx.TOKEN_REF().getText(), (ANTLRv4Parser.RuleSpecContext) ctx.getParent());
    }
}