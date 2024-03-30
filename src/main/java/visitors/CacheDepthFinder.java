package main.java.visitors;

import static main.java.Main.ruleMap;
import static main.java.parser.ANTLRv4Parser.*;

import main.java.utils.MapUtil;
import org.antlr.v4.runtime.ParserRuleContext;

public class CacheDepthFinder extends DepthFinder {
    public MapUtil<ParserRuleContext, Integer> depthMap = new MapUtil<>();
    public MapUtil<RuleSpecContext, Integer> ruleDepthMap = new MapUtil<>();

    public int depthOf(ParserRuleContext ctx) {
        if (ctx instanceof LabeledAltContext)
            return depthOfLabeledAlt((LabeledAltContext) ctx);

        if (ctx instanceof AlternativeContext)
            return depthOfAlternative((AlternativeContext) ctx);

        if (ctx instanceof LexerAltContext)
            return depthOfLexerAlt((LexerAltContext) ctx);

        if (ctx instanceof AtomContext)
            return depthOfAtom((AtomContext) ctx);

        if (ctx instanceof BlockContext)
            return depthOfBlock((BlockContext) ctx);

        if (ctx instanceof LexerAtomContext)
            return depthOfLexerAtom((LexerAtomContext) ctx);

        throw new RuntimeException();
    }

    @Override
    public Integer depthOfRule(String ruleName) {
        if (ruleName.equals("basicForStatement"))
            System.out.println();
        if (ruleName.equals("EOF")) return 0;
        if (! ruleMap.containsKey(ruleName)) throw new RuntimeException("no such a rule");
        var ctx = ruleMap.get(ruleName);
        if (callStack.search(ctx) > -1) return defaultResult();
        if (ruleDepthMap.containsKey(ctx)) return ruleDepthMap.get(ctx);

        callStack.push(ctx);
        Integer res = super.visitRuleSpec(ctx);
        ruleDepthMap.put(ctx, res);
        callStack.pop();

        return res;
    }

    public Integer depthOfLabeledAlt(LabeledAltContext ctx) {
        if (! depthMap.containsKey(ctx))
            depthMap.put(ctx, super.visitLabeledAlt(ctx));

        return depthMap.get(ctx);
    }

    public Integer depthOfAlternative(AlternativeContext ctx) {
        if (! depthMap.containsKey(ctx))
            depthMap.put(ctx, super.visitAlternative(ctx));

        return depthMap.get(ctx);
    }

    public Integer depthOfLexerAlt(LexerAltContext ctx) {
        if (! depthMap.containsKey(ctx))
            depthMap.put(ctx, super.visitLexerAlt(ctx));

        return depthMap.get(ctx);
    }

    public Integer depthOfBlock(BlockContext ctx) {
        if (! depthMap.containsKey(ctx))
            depthMap.put(ctx, super.visitBlock(ctx));

        return depthMap.get(ctx);
    }

    public Integer depthOfAtom(AtomContext ctx) {
        if (! depthMap.containsKey(ctx))
            depthMap.put(ctx, super.visitAtom(ctx));

        return depthMap.get(ctx);
    }

    public Integer depthOfLexerAtom(LexerAtomContext ctx) {
        if (! depthMap.containsKey(ctx))
            depthMap.put(ctx, super.visitLexerAtom(ctx));

        return depthMap.get(ctx);
    }
}
