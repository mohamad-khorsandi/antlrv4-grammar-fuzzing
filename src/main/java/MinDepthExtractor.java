package main.java;

import main.java.parser.ANTLRv4ParserBaseVisitor;
import main.java.utils.MapUtil;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

import static main.java.parser.ANTLRv4Parser.*;
import static main.java.Main.ruleMap;

public class MinDepthExtractor extends ANTLRv4ParserBaseVisitor<Integer> {
    MapUtil<String, Integer> minimumDepth = new MapUtil<>();

    public Integer minDepth(String ruleName) {
        System.out.println("RULE: " + ruleName);

        if (ruleName.equals("EOF"))
            return defaultResult();

        if (! ruleMap.containsKey(ruleName))
            throw new RuntimeException("not such a rule: " + ruleName);

        return ruleMap.get(ruleName).accept(this);
    }

    //general methods----------------------------------------------------------------------
    @Override
    protected Integer defaultResult() {
        return null;
    }

    @Override
    protected Integer aggregateResult(Integer aggregate, Integer nextResult) {
        if (aggregate == null && nextResult != null) {
            return nextResult;
        } else if (aggregate != null && nextResult == null) {
            return aggregate;
        } else if (aggregate == null) {
            return null;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public Integer visitErrorNode(ErrorNode node) {
        throw new RuntimeException();
    }

    @Override
    public Integer visitChildren(RuleNode node) {
        Integer result = this.defaultResult();
        int n = node.getChildCount();

        for(int i = 0; i < n; ++i) {
            ParseTree c = node.getChild(i);
            Integer childResult = c.accept(this);
            result = this.aggregateResult(result, childResult);
        }

        return result;
    }

    @Override
    public Integer visitRuleSpec(RuleSpecContext ctx) {
        var res = super.visitRuleSpec(ctx);
        if (res == null)
            throw new RuntimeException();
        return res;
    }
}
