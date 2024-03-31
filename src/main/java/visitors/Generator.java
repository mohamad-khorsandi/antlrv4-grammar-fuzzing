package visitors;

import main.SingletonInjector;
import exception_handling.NotImpelException;
import parser.ANTLRv4Parser.*;
import utils.ListUtil;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.function.Predicate;


import static main.SingletonInjector.*;
import static utils.DepthHelper.notNull;
import static utils.GenHelper.*;
import static utils.ListUtil.randElem;

public class Generator extends AbstractGenerator {
    protected Predicate<ParserRuleContext> filter = (alt) -> depthFinder.depthOf(alt) <= depthLimit;

    public Generator(int maxDepth) {
        this.depthLimit = maxDepth;
    }

    public Generator() {
        this.depthLimit = null;
    }

    public StringBuilder generate(String ruleName) {
        if (ruleName.equals("EOF")) return defaultResult();

        if (! ruleNameMap.containsKey(ruleName))
            throw new RuntimeException("no such a rule");

        var curMind = depthFinder.ruleDepthMap.get(ruleNameMap.get(ruleName));

        if (curMind > depthLimit)
            throw new RuntimeException("string can not be produced in this depth limit");
        print(curMind, ruleName);

        if (! ruleNameMap.containsKey(ruleName)) throw new RuntimeException("not such a rule: " + ruleName);

        if (--depthLimit < 0) throw new RuntimeException();
        StringBuilder tmp = this.visitRuleSpec(ruleNameMap.get(ruleName));
        log.debug(ruleName + " " + tmp.toString());
        depthLimit++;
        return tmp;
    }

    public void print(int curMind, String ruleName) {
        log.debug("rule: " + ruleName + " " + curMind);
        log.trace("depth limit: " + depthLimit);
    }

    //parser----------------------------------------------------------------------
    @Override
    public StringBuilder visitRuleAltList(RuleAltListContext ctx) {
        log.trace("visitRuleAltList");
        ArrayList<ParserRuleContext> res = new ArrayList<>();
        for (var a : ctx.labeledAlt())
            if(filter.test(a))
                res.add(a);

        return randElem(res).accept(this);
    }

    @Override
    public StringBuilder visitElement(ElementContext ctx) {
        log.trace("visitElement");
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
        log.trace("visitAtom");
        if (ctx.ruleref() != null) return generate(ctx.ruleref().getText()); //RULE_REF

        else if (ctx.notSet() != null || ctx.terminalDef() != null)
            return notNull(ctx.notSet(), ctx.terminalDef()).accept(this);

        else if (ctx.DOT() != null) throw new NotImpelException();

        else throw new RuntimeException();
    }

    @Override
    public StringBuilder visitEbnf(EbnfContext ctx) {
        log.trace("visitEbnf");
        StringBuilder result = new StringBuilder();
        int count = ebnfCount(ctx.block(), ctx.blockSuffix(), depthLimit);

        for (int i = 0; i < count; i++)
            result.append(ctx.block().accept(this));

        return result;
    }

    @Override
    public StringBuilder visitAltList (AltListContext ctx) {
        log.trace("visitAltList");

        return ListUtil.by(ctx.alternative(), filter).randElem().accept(this);
    }

    // lexer----------------------------------------------------------------------
    @Override
    public StringBuilder visitLexerAltList(LexerAltListContext ctx) {
        log.trace("visitLexerAltList");

        return ListUtil.by(ctx.lexerAlt(), filter).randElem().accept(this);
    }

    @Override
    public StringBuilder visitLexerElement(LexerElementContext ctx) {
        log.trace("visitLexerElement");
        StringBuilder result = new StringBuilder();
        int count = ebnfCount((ParserRuleContext) ctx.getChild(0), ctx.ebnfSuffix(), depthLimit);

        for (int i = 0; i < count; i++)
            result.append(ctx.getChild(0).accept(this));

        return result;
    }

    @Override
    public StringBuilder visitLexerAtom(LexerAtomContext ctx) {
        log.trace("visitLexerAtom");
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
        log.trace("visitLexerBlock");
        return super.visitLexerBlock(ctx);
    }

    @Override
    public StringBuilder visitCharacterRange(CharacterRangeContext ctx) {
        log.trace("visitCharacterRange");
        if (ctx.STRING_LITERAL(0).getText().length() != 1 || ctx.STRING_LITERAL(1).getText().length() != 1)
            throw new NotImpelException();

        int rand = SingletonInjector.rand.randInRange(
                ctx.STRING_LITERAL(0).getText().charAt(1),
                ctx.STRING_LITERAL(1).getText().charAt(1)
        );
        return c2sb((char) rand);
    }
}


