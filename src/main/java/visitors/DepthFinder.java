package visitors;

import static parser.ANTLRv4Parser.*;
import utils.MapUtil;
import org.antlr.v4.runtime.ParserRuleContext;
import static main.SingletonInjector.ruleNameMap;

public class DepthFinder extends AbstractDepthFinder {
    final public MapUtil<ParserRuleContext, Integer> depthMap = new MapUtil<>();
    final public MapUtil<RuleSpecContext, Integer> ruleDepthMap = new MapUtil<>();

    public int depthOf(ParserRuleContext ctx) {
        if (ctx instanceof LabeledAltContext)
            return depthOfLabeledAlt((LabeledAltContext) ctx).getDepth();

        if (ctx instanceof AlternativeContext)
            return depthOfAlternative((AlternativeContext) ctx).getDepth();

        if (ctx instanceof LexerAltContext)
            return depthOfLexerAlt((LexerAltContext) ctx).getDepth();

        if (ctx instanceof AtomContext)
            return depthOfAtom((AtomContext) ctx).getDepth();

        if (ctx instanceof BlockContext)
            return depthOfBlock((BlockContext) ctx).getDepth();

        if (ctx instanceof LexerAtomContext)
            return depthOfLexerAtom((LexerAtomContext) ctx).getDepth();

        if (ctx instanceof LexerBlockContext)
            return depthOfLexerBlock((LexerBlockContext) ctx).getDepth();

        throw new RuntimeException();
    }

    private Depth depthOfLexerBlock(LexerBlockContext ctx) {
        if (! depthMap.containsKey(ctx))
            depthMap.put(ctx, super.visitLexerBlock(ctx).getDepth());

        return Depth.of(depthMap.get(ctx));
    }

    @Override
    public Depth depthOfRule(String ruleName) {
        if (ruleName.equals("EOF")) return Depth.of(0);
        if (! ruleNameMap.containsKey(ruleName)) throw new RuntimeException("no such a rule");
        var ctx = ruleNameMap.get(ruleName);
        if (callStack.search(ctx) > -1) return Depth.recur();
        if (ruleDepthMap.containsKey(ctx)) return Depth.of(ruleDepthMap.get(ctx));

        callStack.push(ctx);
        Depth res = super.visitRuleSpec(ctx);
        if (!res.isRecur())
            ruleDepthMap.put(ctx, res.getDepth());
        callStack.pop();

        return res;
    }

    public Depth depthOfLabeledAlt(LabeledAltContext ctx) {
        if (! depthMap.containsKey(ctx))
            depthMap.put(ctx, super.visitLabeledAlt(ctx).getDepth());

        return Depth.of(depthMap.get(ctx));
    }

    public Depth depthOfAlternative(AlternativeContext ctx) {
        if (! depthMap.containsKey(ctx))
            depthMap.put(ctx, super.visitAlternative(ctx).getDepth());

        return Depth.of(depthMap.get(ctx));
    }

    public Depth depthOfLexerAlt(LexerAltContext ctx) {
        if (! depthMap.containsKey(ctx))
            depthMap.put(ctx, super.visitLexerAlt(ctx).getDepth());

        return Depth.of(depthMap.get(ctx));
    }

    public Depth depthOfBlock(BlockContext ctx) {
        if (! depthMap.containsKey(ctx))
            depthMap.put(ctx, super.visitBlock(ctx).getDepth());

        return Depth.of(depthMap.get(ctx));
    }

    public Depth depthOfAtom(AtomContext ctx) {
        if (! depthMap.containsKey(ctx))
            depthMap.put(ctx, super.visitAtom(ctx).getDepth());

        return Depth.of(depthMap.get(ctx));
    }

    public Depth depthOfLexerAtom(LexerAtomContext ctx) {
        if (! depthMap.containsKey(ctx))
            depthMap.put(ctx, super.visitLexerAtom(ctx).getDepth());

        return Depth.of(depthMap.get(ctx));
    }
}
