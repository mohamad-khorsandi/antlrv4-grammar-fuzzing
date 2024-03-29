package main.java.visitors;

import main.java.Main;
import main.java.exception_handling.NotImpelException;
import main.java.parser.ANTLRv4Parser.*;
import main.java.utils.GenHelper;

import static main.java.Main.rand;

import static main.java.Main.ruleMap;
import static main.java.utils.DepthHelper.notNull;
import static main.java.utils.GenHelper.*;
import static main.java.utils.ListUtil.randElem;

public class Generator extends AbstractGenerator {
    public Generator(int maxDepth) {
        this.depthLimit = maxDepth;
    }

    public Generator() {
        this.depthLimit = null;
    }

    public StringBuilder generate(String ruleName) {
        System.out.println("RULE: " + ruleName);
        System.out.println("DEPTH limit: " + depthLimit);

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
        return randElem(ctx.labeledAlt()).accept(this);
    }

    @Override
    public StringBuilder visitElement(ElementContext ctx) {
        System.out.println("visitElement");
        if (ctx.labeledElement() != null) {
            throw new NotImpelException();

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
        int count = GenHelper.ebnfCount(ctx.blockSuffix());

        for (int i = 0; i < count; i++)
            result.append(ctx.block().accept(this));

        return result;
    }

    @Override
    public StringBuilder visitAltList (AltListContext ctx) {
        System.out.println("visitAltList");
        return randElem(ctx.alternative()).accept(this);
    }

    // lexer----------------------------------------------------------------------
    @Override
    public StringBuilder visitLexerAltList(LexerAltListContext ctx) {
        System.out.println("visitLexerAltList");
        return randElem(ctx.lexerAlt()).accept(this);
    }

    @Override
    public StringBuilder visitLexerElement(LexerElementContext ctx) {
        System.out.println("visitLexerElement");
        StringBuilder result = new StringBuilder();
        int count = ebnfCount(ctx.ebnfSuffix());

        for (int i = 0; i < count; i++)
            result.append(ctx.children.get(0).accept(this));

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


