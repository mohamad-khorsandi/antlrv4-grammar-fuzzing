package main.java;

import main.java.parser.ANTLRv4Parser;
import main.java.parser.ANTLRv4ParserBaseListener;
import main.java.utils.MapUtil;
import main.java.utils.Utils;
import org.antlr.v4.runtime.ParserRuleContext;

public class MinimumDepthExtractor extends ANTLRv4ParserBaseListener {
    MapUtil<String, Integer> minimumDepth = new MapUtil<>();

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        if (ctx instanceof  ANTLRv4Parser.TerminalDefContext &&
                ((ANTLRv4Parser.TerminalDefContext) ctx).STRING_LITERAL() != null) {
            minimumDepth.put(((ANTLRv4Parser.TerminalDefContext) ctx).STRING_LITERAL().getText(), 0);
        } else {
            System.out.println(ctx.getText());
        }
    }
}
