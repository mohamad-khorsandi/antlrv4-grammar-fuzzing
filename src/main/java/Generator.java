package main.java;

import main.java.parser.ANTLRv4Parser;
import main.java.parser.ANTLRv4ParserBaseVisitor;
import org.antlr.v4.runtime.tree.ErrorNode;

import static main.java.Main.ruleMap;
import static main.java.Utils.*;

public class Generator extends ANTLRv4ParserBaseVisitor<StringBuilder> {
    static int depth = 0;
    public StringBuilder generate(String ruleName) {
        if (++depth > 100)
            Config.SIGMA = .1;

        if (ruleName.equals("EOF"))
            return defaultResult();

        if (! ruleMap.containsKey(ruleName))
            throw new RuntimeException("not such a rule: " + ruleName);
        System.out.println("RULE:" + ruleName);
        return this.visitRuleSpec(ruleMap.get(ruleName));
    }

    //general methods----------------------------------------------------------------------
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

    //parser----------------------------------------------------------------------
    @Override
    public StringBuilder visitRuleAltList(ANTLRv4Parser.RuleAltListContext ctx) {
        return Utils.randomElem(ctx.labeledAlt()).accept(this);
    }

    @Override
    public StringBuilder visitAlternative(ANTLRv4Parser.AlternativeContext ctx) {
        System.out.println("visitAlternative");
        StringBuilder result = new StringBuilder();
        ctx.element().forEach(elementContext ->
                result.append(elementContext.accept(this)));
        return result;
    }

    @Override
    public StringBuilder visitElement(ANTLRv4Parser.ElementContext ctx) {
        System.out.println("visitElement");
        if (ctx.labeledElement() != null) {
            throw new RuntimeException("not impel");

        } else if (ctx.atom() != null) {
            StringBuilder result = new StringBuilder();
            int count = Utils.ebnfCount(ctx.ebnfSuffix());

            for (int i = 0; i < count; i++)
                result.append(ctx.atom().accept(this));
            return result;

        } else if (ctx.ebnf() != null) {
            return ctx.ebnf().accept(this);

        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public StringBuilder visitAtom(ANTLRv4Parser.AtomContext ctx) {
        if (ctx.DOT() != null) {
            throw new RuntimeException("not impel");
        } else if (ctx.ruleref() != null) {
            return this.generate(ctx.ruleref().getText()); //RULE_REF
        } else {
            return ctx.getChild(0).accept(this);
        }
    }

    @Override
    public StringBuilder visitEbnf(ANTLRv4Parser.EbnfContext ctx) {
        System.out.println("visitEbnf");
        StringBuilder result = new StringBuilder();
        int count = Utils.ebnfCount(ctx.blockSuffix().ebnfSuffix());

        for (int i = 0; i < count; i++)
            result.append(ctx.block().accept(this));

        return result;
    }

    @Override
    public StringBuilder visitAltList(ANTLRv4Parser.AltListContext ctx) {
        System.out.println("visitAltList");
        return randomElem(ctx.alternative()).accept(this);
    }



    // lexer----------------------------------------------------------------------
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
        int count = ebnfCount(ctx.ebnfSuffix());

        for (int i = 0; i < count; i++)
            result.append(ctx.children.get(0).accept(this));

        return result;
    }

    @Override
    public StringBuilder visitLexerAtom(ANTLRv4Parser.LexerAtomContext ctx) {
        if (ctx.DOT() != null) {
            throw new RuntimeException("not impel");//intend to find usage
        } else if (ctx.LEXER_CHAR_SET() != null) {
            return Utils.randomCharFrom(ctx.LEXER_CHAR_SET().getText());
        } else {
            return ctx.getChild(0).accept(this);
        }
    }

    @Override
    public StringBuilder visitLexerBlock(ANTLRv4Parser.LexerBlockContext ctx) {
        return super.visitLexerBlock(ctx);
    }

    @Override
    public StringBuilder visitCharacterRange(ANTLRv4Parser.CharacterRangeContext ctx) {
        int rand = randInRange(
                ctx.STRING_LITERAL(0).getText().charAt(1),
                ctx.STRING_LITERAL(1).getText().charAt(1)
        );
        return c2sb((char) rand);
    }

    // common----------------------------------------------------------------------
    @Override
    public StringBuilder visitTerminalDef(ANTLRv4Parser.TerminalDefContext ctx) {
        if (ctx.TOKEN_REF() != null) {
            return this.generate(ctx.TOKEN_REF().getText()); //TOKEN_REF
        } else if (ctx.STRING_LITERAL() != null) {
            return Utils.refineLiteral(ctx.STRING_LITERAL().getText()); //STRING_LITERAL
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public StringBuilder visitNotSet(ANTLRv4Parser.NotSetContext ctx) {
        if (ctx.blockSet() != null) {
            throw new RuntimeException("not impel");

        } else if (ctx.setElement() != null) {
            return ctx.setElement().accept(this);

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
}


