import parser.ANTLRv4Parser;
import parser.ANTLRv4ParserBaseVisitor;
//import Utils.*;

public class Generator extends ANTLRv4ParserBaseVisitor<StringBuilder> {

    //parser rules
    @Override
    public StringBuilder visitRuleAltList(ANTLRv4Parser.RuleAltListContext ctx) {
        return Utils.randomElem(ctx.labeledAlt()).accept(this);
        //todo check for empty alts
    }

    @Override
    public StringBuilder visitAlternative(ANTLRv4Parser.AlternativeContext ctx) {
        StringBuilder result = new StringBuilder();
        ctx.element().forEach(elementContext ->
                result.append(elementContext.accept(this)));
        return result;
    }

    @Override
    public StringBuilder visitElement(ANTLRv4Parser.ElementContext ctx) {

        return new StringBuilder("result");
    }


}