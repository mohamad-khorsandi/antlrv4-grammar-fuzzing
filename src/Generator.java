import org.antlr.v4.runtime.tree.TerminalNode;
import parser.ANTLRv4Parser;
import parser.ANTLRv4ParserBaseVisitor;

public class Generator extends ANTLRv4ParserBaseVisitor<StringBuilder> {
    //general methods
    @Override
    protected StringBuilder defaultResult() {
        return new StringBuilder();
    }

    @Override
    protected StringBuilder aggregateResult(StringBuilder aggregate, StringBuilder nextResult) {
        return aggregate.append(nextResult);
    }

    @Override
    public StringBuilder visitTerminal(TerminalNode terminalNode) {
        return new StringBuilder(terminalNode.getText());
    }

    //parser rules
    @Override
    public StringBuilder visitRuleAltList(ANTLRv4Parser.RuleAltListContext ctx) {
        return Utils.randomElem(ctx.labeledAlt()).accept(this);
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
        StringBuilder result = new StringBuilder();
        int count = 1;
        if (ctx.ebnfSuffix() != null)
            count = Utils.ebnfSuffixToCount(ctx.ebnfSuffix());

        for (int i = 0; i < count; i++)
            result.append(ctx.children.get(0).accept(this));

        return result;
    }

    @Override
    public StringBuilder visitLabeledElement(ANTLRv4Parser.LabeledElementContext ctx) {
        throw new RuntimeException("not impel");
    }

    @Override
    public StringBuilder visitAtom(ANTLRv4Parser.AtomContext ctx) {
        if (ctx.DOT() != null) {
            return Utils.randomPrintableChar();
        } else {
            return ctx.getChild(0).accept(this);
        }
    }

    // lexer
    @Override
    public StringBuilder visitLexerAltList(ANTLRv4Parser.LexerAltListContext ctx) {
        return Utils.randomElem(ctx.lexerAlt()).accept(this);
    }

    @Override
    public StringBuilder visitLexerElements(ANTLRv4Parser.LexerElementsContext ctx) {
        return Utils.randomElem(ctx.lexerElement()).accept(this);
    }


    @Override
    public StringBuilder visitLexerElement(ANTLRv4Parser.LexerElementContext ctx) {
        StringBuilder result = new StringBuilder();
        int count = 1;
        if (ctx.ebnfSuffix() != null)
            count = Utils.ebnfSuffixToCount(ctx.ebnfSuffix());

        for (int i = 0; i < count; i++)
            result.append(ctx.children.get(0).accept(this));

        return result;
    }

    @Override
    public StringBuilder visitLexerAtom(ANTLRv4Parser.LexerAtomContext ctx) {
        if (ctx.DOT() != null) {
            return Utils.randomPrintableChar();
        } else {
            return ctx.getChild(0).accept(this);
        }
    }

    // common
    @Override
    public StringBuilder visitCharacterRange(ANTLRv4Parser.CharacterRangeContext ctx) {
        return Utils.randomCharInRange(
                ctx.children.get(0).getText().charAt(0),
                ctx.children.get(2).getText().charAt(0)
        );
    }

    @Override
    public StringBuilder visitBlockSet(ANTLRv4Parser.BlockSetContext ctx) {
        return Utils.randomElem(ctx.setElement()).accept(this);
    }

}


