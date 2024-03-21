import org.antlr.v4.runtime.tree.ErrorNode;
import parser.ANTLRv4Parser;
import parser.ANTLRv4ParserBaseVisitor;
import main.java.Utils;

import static main.java.Utils.*;

public class Generator extends ANTLRv4ParserBaseVisitor<StringBuilder> {

    public StringBuilder generate(String ruleName) {
        if (ruleName.equals("EOF"))
            return defaultResult();

        if (! Main.ruleMap.containsKey(ruleName))
            throw new RuntimeException("not such a rule: " + ruleName);

        return this.visitRuleSpec(Main.ruleMap.get(ruleName));
    }

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
    public StringBuilder visitErrorNode(ErrorNode node) {
        throw new RuntimeException();
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
            throw new RuntimeException("not impel");//intend to find usage
//            return main.java.Utils.randomChar();
        } else if (ctx.ruleref() != null) {
            return this.generate(ctx.ruleref().getText()); //RULE_REF
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
            throw new RuntimeException("not impel");//intend to find usage
//            return main.java.Utils.randomChar();
        } else if (ctx.LEXER_CHAR_SET() != null) {
            return Utils.randomCharFrom(ctx.LEXER_CHAR_SET().getText());
        } else {
            return ctx.getChild(0).accept(this);
        }
    }

    // common
    @Override
    public StringBuilder visitCharacterRange(ANTLRv4Parser.CharacterRangeContext ctx) {
        int rand = randInRange(
                refineLiteral(ctx.STRING_LITERAL(0)),
                refineLiteral(ctx.STRING_LITERAL(1))
        )
        return (c2sb();
    }

    @Override
    public StringBuilder visitBlockSet(ANTLRv4Parser.BlockSetContext ctx) {
        throw new RuntimeException("not impel");
    }

    @Override
    public StringBuilder visitTerminalDef(ANTLRv4Parser.TerminalDefContext ctx) {
        if (ctx.TOKEN_REF() != null) {
            return this.generate(ctx.TOKEN_REF().getText()); //TOKEN_REF
        } else if (ctx.STRING_LITERAL() != null) {
            return Utils.refineLiteral(ctx.STRING_LITERAL()); //STRING_LITERAL
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public StringBuilder visitSetElement(ANTLRv4Parser.SetElementContext ctx) {
        if (ctx.TOKEN_REF() != null) {
            throw new RuntimeException("not impel");

        } else if (ctx.STRING_LITERAL() != null) {
            throw new RuntimeException("not impel");

        } else if (ctx.characterRange() != null) {
            throw new RuntimeException("not impel");

        } else if (ctx.LEXER_CHAR_SET() != null) {
            return Utils.randomCharExcluding(ctx.LEXER_CHAR_SET().getText());

        }else {
            throw new RuntimeException();
        }
    }

    @Override
    public StringBuilder visitNotSet(ANTLRv4Parser.NotSetContext ctx) {
        return super.visitNotSet(ctx);
    }
}


