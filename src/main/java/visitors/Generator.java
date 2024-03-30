package main.java.visitors;

import main.java.Main;
import main.java.exception_handling.NotImpelException;
import main.java.parser.ANTLRv4Parser.*;
import main.java.utils.ListUtil;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.function.Predicate;

import static main.java.Main.*;
import static main.java.utils.DepthHelper.notNull;
import static main.java.utils.GenHelper.*;
import static main.java.utils.ListUtil.randElem;

public class Generator extends AbstractGenerator {
    protected Predicate<ParserRuleContext> filter = (alt) -> depthFinder.depthOf(alt) <= depthLimit;

    public Generator(int maxDepth) {
        this.depthLimit = maxDepth;
    }

    public Generator() {
        this.depthLimit = null;
    }

    public StringBuilder generate(String ruleName) {
        var curMind = depthFinder.ruleDepthMap.get(ruleMap.get(ruleName));
        System.out.println("\nRULE: " + ruleName + " " + curMind);
        System.out.println("DEPTH LIMIT: " + depthLimit);
        if (curMind > depthLimit)
            throw new RuntimeException();

        if (ruleName.equals("EOF")) return defaultResult();
        if (! ruleMap.containsKey(ruleName)) throw new RuntimeException("not such a rule: " + ruleName);

        if (--depthLimit < 0) throw new RuntimeException();
        StringBuilder tmp = this.visitRuleSpec(ruleMap.get(ruleName));
        depthLimit++;
        return tmp;
    }

    //parser----------------------------------------------------------------------
    @Override
    public StringBuilder visitRuleAltList(RuleAltListContext ctx) {
        System.out.println("visitRuleAltList");
        ArrayList<ParserRuleContext> res = new ArrayList<>();
        for (var a : ctx.labeledAlt())
            if(filter.test(a))
                res.add(a);

        return randElem(res).accept(this);
    }

    @Override
    public StringBuilder visitElement(ElementContext ctx) {
        System.out.println("visitElement");
        if (ctx.labeledElement() != null) {
            throw new NotImpelException();

        } else if (ctx.atom() != null) {
            StringBuilder result = new StringBuilder();
            int count = ebnfCount(ctx.atom(), ctx.ebnfSuffix(), depthLimit);

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
    public StringBuilder visitAtom(AtomContext ctx) {
        System.out.println("visitAtom");
        if (ctx.ruleref() != null) return generate(ctx.ruleref().getText()); //RULE_REF

        else if (ctx.notSet() != null || ctx.terminalDef() != null)
            return notNull(ctx.notSet(), ctx.terminalDef()).accept(this);

        else if (ctx.DOT() != null) throw new NotImpelException();

        else throw new RuntimeException();
    }

    @Override
    public StringBuilder visitEbnf(EbnfContext ctx) {
        System.out.println("visitEbnf");
        StringBuilder result = new StringBuilder();
        int count = ebnfCount(ctx.block(), ctx.blockSuffix(), depthLimit);

        for (int i = 0; i < count; i++)
            result.append(ctx.block().accept(this));

        return result;
    }

    @Override
    public StringBuilder visitAltList (AltListContext ctx) {
        System.out.println("visitAltList");

        return ListUtil.by(ctx.alternative(), filter).randElem().accept(this);
    }

    // lexer----------------------------------------------------------------------
    @Override
    public StringBuilder visitLexerAltList(LexerAltListContext ctx) {
        System.out.println("visitLexerAltList");

        return ListUtil.by(ctx.lexerAlt(), filter).randElem().accept(this);
    }

    @Override
    public StringBuilder visitLexerElement(LexerElementContext ctx) {
        System.out.println("visitLexerElement");
        StringBuilder result = new StringBuilder();
        int count = ebnfCount((ParserRuleContext) ctx.getChild(0), ctx.ebnfSuffix(), depthLimit);

        for (int i = 0; i < count; i++)
            result.append(ctx.getChild(0).accept(this));

        return result;
    }

    @Override
    public StringBuilder visitLexerAtom(LexerAtomContext ctx) {
        System.out.println("visitLexerAtom");
        if (ctx.DOT() != null) {
            throw new NotImpelException();
        } else if (ctx.LEXER_CHAR_SET() != null) {
            return rand.randomCharFrom(ctx.LEXER_CHAR_SET().getText());
        } else {
            return ctx.getChild(0).accept(this);
        }
    }

    @Override
    public StringBuilder visitLexerBlock(LexerBlockContext ctx) {
        System.out.println("visitLexerBlock");
        return super.visitLexerBlock(ctx);
    }

    @Override
    public StringBuilder visitCharacterRange(CharacterRangeContext ctx) {
        System.out.println("visitCharacterRange");
        int rand = Main.rand.randInRange(
                ctx.STRING_LITERAL(0).getText().charAt(1),
                ctx.STRING_LITERAL(1).getText().charAt(1)
        );
        return c2sb((char) rand);
    }
}


