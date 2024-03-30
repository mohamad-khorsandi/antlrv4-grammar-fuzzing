package main.java.visitors;
import main.java.parser.ANTLRv4Parser;
import main.java.parser.ANTLRv4ParserBaseVisitor;

import static main.java.utils.DepthHelper.*;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

import java.util.Objects;
import java.util.Stack;

import static main.java.parser.ANTLRv4Parser.*;

abstract public class DepthFinder extends ANTLRv4ParserBaseVisitor<Integer> {
    protected final Stack<RuleSpecContext> callStack = new Stack<>();
    abstract public Integer depthOfRule(String ruleName);

    @Override
    public Integer visitRuleSpec(RuleSpecContext ctx) {
        Integer res = super.visitRuleSpec(ctx);
        if (res == null)
            throw new RuntimeException();

        return res + 1;
    }

    //general methods----------------------------------------------------------------------
    @Override
    protected Integer defaultResult() {
        return null;
    }

    @Override
    protected Integer aggregateResult(Integer aggregate, Integer nextResult) {
        if (aggregate == null && nextResult != null) {
            return nextResult;
        } else if (aggregate != null && nextResult == null) {
            return aggregate;
        } else if (aggregate == null) {
            return null;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public Integer visitErrorNode(ErrorNode node) {
        throw new RuntimeException();
    }

    @Override
    public Integer visitChildren(RuleNode node) {
        Integer result = this.defaultResult();
        int n = node.getChildCount();

        for(int i = 0; i < n; ++i) {
            ParseTree c = node.getChild(i);
            Integer childResult = c.accept(this);
            result = this.aggregateResult(result, childResult);
        }

        return result;
    }

    //parser -------------------------------------------------------------------------
    @Override
    public Integer visitRuleAltList(RuleAltListContext ctx) {
        return ctx.labeledAlt().stream().map(la -> la.accept(this))
                .filter(Objects::nonNull).min(Integer::compareTo).orElseThrow();
    }

    @Override
    public Integer visitAlternative(AlternativeContext ctx) {
        if (ctx.element().isEmpty()) return 0;

        else return ctx.element().stream().map(a -> a.accept(this))
                .filter(Objects::nonNull).max(Integer::compareTo).orElse(null);

    }

    @Override
    public Integer visitElement(ElementContext ctx) {
        if (ctx.labeledElement() != null)
            throw new RuntimeException();

        else if (ctx.actionBlock() != null)
            return 0;

        else if (ctx.atom() != null)
            if (zeroRepPossible(ctx.ebnfSuffix()))
                return 0;
            else
                return ctx.atom().accept(this);

        else if (ctx.ebnf() != null)
            return ctx.ebnf().accept(this);

        else throw new RuntimeException();
    }

    @Override
    public Integer visitAtom(AtomContext ctx) {
        if (ctx.ruleref() != null)
            return this.depthOfRule(ctx.ruleref().getText()); //RULE_REF

        else if (ctx.notSet() != null || ctx.terminalDef() != null)
            return notNull(ctx.notSet(), ctx.terminalDef()).accept(this);

        else if (ctx.DOT() != null) throw new RuntimeException();

        else throw new RuntimeException();
    }

    @Override
    public Integer visitEbnf(EbnfContext ctx) {
        if (zeroRepPossible(ctx.blockSuffix())) {
            return 0;
        } else {
            return ctx.block().accept(this);
        }
    }

    @Override
    public Integer visitAltList(AltListContext ctx) {
        return ctx.alternative().stream().map(a -> a.accept(this))
                .filter(Objects::nonNull).min(Integer::compareTo).orElseThrow();
    }

    //lexer ----------------------------------------------------------------------
    @Override
    public Integer visitLexerAltList(LexerAltListContext ctx) {
        return ctx.lexerAlt().stream().map(a -> a.accept(this))
                .filter(Objects::nonNull).min(Integer::compareTo).orElseThrow();
    }

    @Override
    public Integer visitLexerAlt(LexerAltContext ctx) {
        if (ctx.lexerElements().isEmpty())
            return 0;
        else
            return ctx.lexerElements().accept(this);
    }

    @Override
    public Integer visitLexerElements(LexerElementsContext ctx) {
        return ctx.lexerElement().stream().map(a -> a.accept(this))
                .filter(Objects::nonNull).max(Integer::compareTo).orElseThrow();
    }

    @Override
    public Integer visitLexerElement(LexerElementContext ctx) {
        if (ctx.actionBlock() != null) {
            return null;
        } else {
            if (zeroRepPossible(ctx.ebnfSuffix())) {
                return 0;
            } else {
                return ctx.getChild(0).accept(this);
            }
        }
    }

    @Override
    public Integer visitLexerAtom(LexerAtomContext ctx) {
        if (ctx.characterRange() != null || ctx.LEXER_CHAR_SET() != null)
            return 0;

        else if (ctx.terminalDef() != null || ctx.notSet() != null)
            return notNull(ctx.terminalDef(), ctx.notSet()).accept(this);

        else if (ctx.DOT() != null)
            throw new RuntimeException();

        else throw new RuntimeException();
    }

    // common----------------------------------------------------------------------

    @Override
    public Integer visitTerminalDef(ANTLRv4Parser.TerminalDefContext ctx) {

        if (ctx.TOKEN_REF() != null) {
            return depthOfRule(ctx.TOKEN_REF().getText()); //TOKEN_REF
        } else if (ctx.STRING_LITERAL() != null) {
            return 0; //STRING_LITERAL
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public Integer visitNotSet(ANTLRv4Parser.NotSetContext ctx) {
         if (ctx.setElement() != null) {
            return ctx.setElement().accept(this);

        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public Integer visitSetElement(ANTLRv4Parser.SetElementContext ctx) {
        if (ctx.LEXER_CHAR_SET() != null)
            return 0;

        else
            throw new RuntimeException();
    }

}
