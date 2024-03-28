package main.java;

import main.java.parser.ANTLRv4Parser;
import main.java.parser.ANTLRv4ParserBaseVisitor;
import main.java.utils.GenHelper;
import org.antlr.v4.runtime.tree.ErrorNode;

import static main.java.Main.ruleMap;
import static main.java.utils.GenHelper.*;

public class Generator extends ANTLRv4ParserBaseVisitor<StringBuilder> {
    private int depth = 0;
    private final int maxDepth;

    public Generator(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public StringBuilder generate(String ruleName) {
        depth++;
        System.out.println("RULE: " + ruleName);
        System.out.println("DEPTH: " + depth);
        if (ruleName.equals("EOF"))
            return defaultResult();

        if (! ruleMap.containsKey(ruleName))
            throw new RuntimeException("not such a rule: " + ruleName);

        StringBuilder tmp = this.visitRuleSpec(ruleMap.get(ruleName));
        depth--;
        return tmp;
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
        System.out.println("visitRuleAltList");

        return GenHelper.randomElem(ctx.labeledAlt()).accept(this);
    }

    @Override
    public StringBuilder visitElement(ANTLRv4Parser.ElementContext ctx) {
        System.out.println("visitElement");
        if (ctx.labeledElement() != null) {
            throw new RuntimeException("not impel");

        } else if (ctx.atom() != null) {
            StringBuilder result = new StringBuilder();
            int count = GenHelper.ebnfCount(ctx.ebnfSuffix());

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
        System.out.println("visitAtom");
        if (ctx.ruleref() != null) return generate(ctx.ruleref().getText()); //RULE_REF

        else if (ctx.notSet() != null) return ctx.notSet().accept(this);

        else if (ctx.terminalDef() != null) return ctx.terminalDef().accept(this);

        else if (ctx.DOT() != null) throw new RuntimeException("not impel");

        else throw new RuntimeException();
    }

    @Override
    public StringBuilder visitEbnf(ANTLRv4Parser.EbnfContext ctx) {
        System.out.println("visitEbnf");
        StringBuilder result = new StringBuilder();
        int count = GenHelper.ebnfCount(ctx.blockSuffix());

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
        System.out.println("visitLexerAltList");
        return GenHelper.randomElem(ctx.lexerAlt()).accept(this);
    }

    @Override
    public StringBuilder visitLexerElement(ANTLRv4Parser.LexerElementContext ctx) {
        System.out.println("visitLexerElement");
        StringBuilder result = new StringBuilder();
        int count = ebnfCount(ctx.ebnfSuffix());

        for (int i = 0; i < count; i++)
            result.append(ctx.children.get(0).accept(this));

        return result;
    }

    @Override
    public StringBuilder visitLexerAtom(ANTLRv4Parser.LexerAtomContext ctx) {
        System.out.println("visitLexerAtom");
        if (ctx.DOT() != null) {
            throw new RuntimeException("not impel");
        } else if (ctx.LEXER_CHAR_SET() != null) {
            return GenHelper.randomCharFrom(ctx.LEXER_CHAR_SET().getText());
        } else {
            return ctx.getChild(0).accept(this);
        }
    }

    @Override
    public StringBuilder visitLexerBlock(ANTLRv4Parser.LexerBlockContext ctx) {
        System.out.println("visitLexerBlock");
        return super.visitLexerBlock(ctx);
    }

    @Override
    public StringBuilder visitCharacterRange(ANTLRv4Parser.CharacterRangeContext ctx) {
        System.out.println("visitCharacterRange");
        int rand = randInRange(
                ctx.STRING_LITERAL(0).getText().charAt(1),
                ctx.STRING_LITERAL(1).getText().charAt(1)
        );
        return c2sb((char) rand);
    }

    // common----------------------------------------------------------------------
    @Override
    public StringBuilder visitTerminalDef(ANTLRv4Parser.TerminalDefContext ctx) {
        System.out.println("visitTerminalDef");
        if (ctx.TOKEN_REF() != null) {
            return this.generate(ctx.TOKEN_REF().getText()); //TOKEN_REF
        } else if (ctx.STRING_LITERAL() != null) {
            return GenHelper.refineLiteral(ctx.STRING_LITERAL().getText()).append(" "); //STRING_LITERAL
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public StringBuilder visitNotSet(ANTLRv4Parser.NotSetContext ctx) {
        System.out.println("visitNotSet");
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
        System.out.println("visitSetElement");
        if (ctx.TOKEN_REF() != null) {
            throw new RuntimeException("not impel");

        } else if (ctx.STRING_LITERAL() != null) {
            throw new RuntimeException("not impel");

        } else if (ctx.characterRange() != null) {
            throw new RuntimeException("not impel");

        } else if (ctx.LEXER_CHAR_SET() != null) {
            return GenHelper.randomCharExcluding(ctx.LEXER_CHAR_SET().getText());

        }else {
            throw new RuntimeException();
        }
    }
}


