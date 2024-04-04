package visitors;

import static parser.ANTLRv4Parser.*;

import fuzzer.AntlrFuzzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.MapUtil;
import org.antlr.v4.runtime.ParserRuleContext;

public class DepthVisitor extends AbstractDepthVisitor {
    final private MapUtil<ParserRuleContext, Integer> depthMap = new MapUtil<>();
    final private MapUtil<RuleSpecContext, Integer> ruleDepthMap = new MapUtil<>();
    final private MapUtil<String, RuleSpecContext> rules;

    public DepthVisitor(MapUtil<String, RuleSpecContext> rules) {
        this.rules = rules;
    }

    public int depthOf(ParserRuleContext ctx) {
        if (depthMap.containsKey(ctx))
            return depthMap.simpleGet(ctx);

        if (ctx instanceof LabeledAltContext)
            depthMap.put(ctx, super.visitLabeledAlt((LabeledAltContext) ctx).getDepth());

        else if (ctx instanceof AlternativeContext)
            depthMap.put(ctx, super.visitAlternative((AlternativeContext) ctx).getDepth());

        else if (ctx instanceof LexerAltContext)
            depthMap.put(ctx, super.visitLexerAlt((LexerAltContext) ctx).getDepth());

        else if (ctx instanceof AtomContext)
            depthMap.put(ctx, super.visitAtom((AtomContext) ctx).getDepth());

        else if (ctx instanceof BlockContext)
            depthMap.put(ctx, super.visitBlock((BlockContext) ctx).getDepth());

        else if (ctx instanceof LexerAtomContext)
            depthMap.put(ctx, super.visitLexerAtom((LexerAtomContext) ctx).getDepth());

        else if (ctx instanceof LexerBlockContext)
            depthMap.put(ctx, super.visitLexerBlock((LexerBlockContext) ctx).getDepth());

        else
            throw new RuntimeException();

        return depthMap.get(ctx);
    }

    @Override
    public Depth depthOfRule(String ruleName) {
        if (ruleName.equals("EOF")) return Depth.of(0);
        if (! rules.containsKey(ruleName)) throw new RuntimeException("no such a rule");
        var ctx = rules.get(ruleName);
        if (callStack.search(ctx) > -1) return Depth.recur();
        if (ruleDepthMap.containsKey(ctx)) return Depth.of(ruleDepthMap.get(ctx));

        callStack.push(ctx);
        Depth res = super.visitRuleSpec(ctx);
        if (!res.isRecur())
            ruleDepthMap.put(ctx, res.getDepth());
        callStack.pop();

        return res;
    }
}
