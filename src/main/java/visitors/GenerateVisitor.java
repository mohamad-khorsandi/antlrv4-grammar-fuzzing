package visitors;


import exception_handling.NotImpelException;
import fuzzer.FuzzParams;
import parser.ANTLRv4Parser.*;
import utils.GenHelper;
import org.antlr.v4.runtime.ParserRuleContext;
import utils.MapUtil;
import utils.RandUtil;

import static fuzzer.StateLessData.log;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fuzzer.StateLessData.printableChars;
import static utils.DepthHelper.notNull;
import static utils.GenHelper.c2sb;

public class GenerateVisitor extends AbstractGenerateVisitor {
    private final Predicate<ParserRuleContext> depthFilter;
    private final DepthVisitor depthFinder;
    private final MapUtil<String, RuleSpecContext> rules;

    private FuzzParams params;
    private int remainingDepth;

    public GenerateVisitor(MapUtil<String, RuleSpecContext> rules, Integer seed) {
        super(seed);
        this.depthFinder = new DepthVisitor(rules);
        this.rules = rules;
        depthFilter = (alt) -> depthFinder.depthOf(alt) <= remainingDepth;

    }

    public String generate(FuzzParams params) {
        remainingDepth = params.maxDepth;
        this.params = params;
        return generate(params.startingRule).toString();
    }

    protected StringBuilder generate(String ruleName) {
        if (ruleName.equals("EOF")) return defaultResult();

        if (! rules.containsKey(ruleName))
            throw new RuntimeException("no such a rule " + ruleName);

        int minPosDepth = depthFinder.depthOfRule(ruleName).getDepth();

        if (minPosDepth > remainingDepth)
            throw new RuntimeException("string can not be produced in this depth limit");
        print(minPosDepth, ruleName);

        if (! rules.containsKey(ruleName)) throw new RuntimeException("not such a rule: " + ruleName);

        if (--remainingDepth < 0) throw new RuntimeException();
        StringBuilder tmp = this.visitRuleSpec(rules.get(ruleName)); //todo remove visit rule spec function
        remainingDepth++;
        return tmp;
    }

    public void print(int curMind, String ruleName) {
        log.debug("rule: " + ruleName + " " + curMind);
        log.trace("depth limit: " + remainingDepth);
    }

    //parser----------------------------------------------------------------------
    @Override
    public StringBuilder visitRuleAltList(RuleAltListContext ctx) {
        log.trace("visitRuleAltList");
        ArrayList<ParserRuleContext> res = new ArrayList<>();
        for (var a : ctx.labeledAlt())
            if(depthFilter.test(a))
                res.add(a);

        return rand.randElem(res).accept(this);
    }

    @Override
    public StringBuilder visitElement(ElementContext ctx) {
        log.trace("visitElement");
        if (ctx.labeledElement() != null) {
            return ctx.labeledElement().accept(this);

        } else if (ctx.atom() != null) {
            int atomMinDepth = depthFinder.depthOf(ctx.atom());
            int count = rand.ebnfCount(atomMinDepth, ctx.ebnfSuffix(), remainingDepth, params);

            StringBuilder result = new StringBuilder();
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
    public StringBuilder visitLabeledElement(LabeledElementContext ctx) {
        return notNull(ctx.atom(), ctx.block()).accept(this);
    }

    @Override
    public StringBuilder visitAtom(AtomContext ctx) {
        log.trace("visitAtom");
        if (ctx.ruleref() != null) return generate(ctx.ruleref().getText()); //RULE_REF

        else if (ctx.notSet() != null || ctx.terminalDef() != null)
            return notNull(ctx.notSet(), ctx.terminalDef()).accept(this);

        else if (ctx.DOT() != null)
            return c2sb(rand.randElem(printableChars));

        else throw new RuntimeException();
    }

    @Override
    public StringBuilder visitEbnf(EbnfContext ctx) {
        log.trace("visitEbnf");
        StringBuilder result = new StringBuilder();
        int count = rand.ebnfCount(depthFinder.depthOf(ctx.block()), ctx.blockSuffix(), remainingDepth, params);

        for (int i = 0; i < count; i++)
            result.append(ctx.block().accept(this));

        return result;
    }

    @Override
    public StringBuilder visitAltList (AltListContext ctx) {
        log.trace("visitAltList");
        List<AlternativeContext> feasibleAlts = ctx.alternative().stream().filter(depthFilter).collect(Collectors.toList());
        return rand.randElem(feasibleAlts).accept(this);
    }

    // lexer----------------------------------------------------------------------
    @Override
    public StringBuilder visitLexerAltList(LexerAltListContext ctx) {
        log.trace("visitLexerAltList");
        List<LexerAltContext> feasibleAlts = ctx.lexerAlt().stream().filter(depthFilter).collect(Collectors.toList());
        return rand.randElem(feasibleAlts).accept(this);
    }

    @Override
    public StringBuilder visitLexerElement(LexerElementContext ctx) {
        log.trace("visitLexerElement");

        int minDepth;
        if (ctx.lexerBlock() != null || ctx.lexerAtom() != null)
            minDepth = depthFinder.depthOf(notNull(ctx.lexerAtom(), ctx.lexerBlock()));
        else throw new NotImpelException();
        int count = rand.ebnfCount(minDepth, ctx.ebnfSuffix(), remainingDepth, params);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++)
            result.append(ctx.getChild(0).accept(this));

        return result;
    }

    @Override
    public StringBuilder visitLexerAtom(LexerAtomContext ctx) {
        log.trace("visitLexerAtom");
        if (ctx.DOT() != null) {
            return c2sb(rand.randElem(printableChars));
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

        int randNum = rand.randInRange(
                ctx.STRING_LITERAL(0).getText().charAt(1),
                ctx.STRING_LITERAL(1).getText().charAt(1)
        );
        return c2sb((char) randNum);
    }
}


