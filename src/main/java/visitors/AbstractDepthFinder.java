package visitors;
import exception_handling.NotImpelException;
import parser.ANTLRv4Parser;
import parser.ANTLRv4ParserBaseVisitor;

import static utils.DepthHelper.*;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

import java.util.Objects;
import java.util.Stack;

import static parser.ANTLRv4Parser.*;

abstract public class AbstractDepthFinder extends ANTLRv4ParserBaseVisitor<Depth> {
    protected final Stack<RuleSpecContext> callStack = new Stack<>();
    abstract public Depth depthOfRule(String ruleName);

    @Override
    public Depth visitRuleSpec(RuleSpecContext ctx) {
        Depth res = super.visitRuleSpec(ctx);

        res.inc();
        return res;
    }

    //general methods----------------------------------------------------------------------
    @Override
    protected Depth defaultResult() {
        return Depth.of(0);
    }

    @Override
    public Depth visitErrorNode(ErrorNode node) {
        throw new RuntimeException();
    }

    @Override
    public Depth visitChildren(RuleNode node) {
        Depth res = defaultResult();
        int n = node.getChildCount();

        for(int i = 0; i < n; ++i) {
            ParseTree c = node.getChild(i);
            Depth childResult = c.accept(this);
            if (childResult.isRecur())
                return Depth.recur();

            if (res.getDepth() < childResult.getDepth())
                res = childResult;
        }

        return res;
    }

    //parser -------------------------------------------------------------------------
    @Override
    public Depth visitRuleAltList(RuleAltListContext ctx) {
        return ctx.labeledAlt().stream().map(la -> la.accept(this))
                .min(Depth::compareTo).orElseThrow();
    }

    @Override
    public Depth visitAlternative(AlternativeContext ctx) {
        if (ctx.element().isEmpty()) return Depth.of(0);

        else return ctx.element().stream().map(a -> a.accept(this))
                .max(Depth::compareTo).orElse(null);
    }

    @Override
    public Depth visitElement(ElementContext ctx) {
        if (ctx.labeledElement() != null)
            throw new NotImpelException();

        else if (ctx.actionBlock() != null)
            return Depth.of(0);

        else if (ctx.atom() != null)
            if (zeroRepPossible(ctx.ebnfSuffix()))
                return Depth.of(0);
            else
                return ctx.atom().accept(this);

        else if (ctx.ebnf() != null)
            return ctx.ebnf().accept(this);

        else throw new RuntimeException();
    }

    @Override
    public Depth visitAtom(AtomContext ctx) {
        if (ctx.ruleref() != null)
            return this.depthOfRule(ctx.ruleref().getText()); //RULE_REF

        else if (ctx.notSet() != null || ctx.terminalDef() != null)
            return notNull(ctx.notSet(), ctx.terminalDef()).accept(this);

        else if (ctx.DOT() != null) throw new RuntimeException();

        else throw new RuntimeException();
    }

    @Override
    public Depth visitEbnf(EbnfContext ctx) {
        if (zeroRepPossible(ctx.blockSuffix())) {
            return Depth.of(0);
        } else {
            return ctx.block().accept(this);
        }

    }

    @Override
    public Depth visitAltList(AltListContext ctx) {
        return ctx.alternative().stream().map(a -> a.accept(this))
                .min(Depth::compareTo).orElseThrow();
    }

    //lexer ----------------------------------------------------------------------
    @Override
    public Depth visitLexerAltList(LexerAltListContext ctx) {
        return ctx.lexerAlt().stream().map(a -> a.accept(this))
                .min(Depth::compareTo).orElseThrow();
    }

    @Override
    public Depth visitLexerAlt(LexerAltContext ctx) {
        if (ctx.lexerElements().isEmpty())
            return Depth.of(0);
        else
            return ctx.lexerElements().accept(this);
    }

    @Override
    public Depth visitLexerElements(LexerElementsContext ctx) {
        return ctx.lexerElement().stream().map(a -> a.accept(this))
                .filter(Objects::nonNull).max(Depth::compareTo).orElseThrow();
    }

    @Override
    public Depth visitLexerElement(LexerElementContext ctx) {
        if (ctx.actionBlock() != null) {
            return null;
        } else {
            if (zeroRepPossible(ctx.ebnfSuffix())) {
                return Depth.of(0);
            } else {
                return ctx.getChild(0).accept(this);
            }
        }
    }

    @Override
    public Depth visitLexerAtom(LexerAtomContext ctx) {
        if (ctx.characterRange() != null || ctx.LEXER_CHAR_SET() != null)
            return Depth.of(0);

        else if (ctx.terminalDef() != null || ctx.notSet() != null)
            return notNull(ctx.terminalDef(), ctx.notSet()).accept(this);

        else if (ctx.DOT() != null)
            throw new RuntimeException();

        else throw new RuntimeException();
    }

    // common----------------------------------------------------------------------

    @Override
    public Depth visitTerminalDef(ANTLRv4Parser.TerminalDefContext ctx) {

        if (ctx.TOKEN_REF() != null) {
            return depthOfRule(ctx.TOKEN_REF().getText()); //TOKEN_REF
        } else if (ctx.STRING_LITERAL() != null) {
            return Depth.of(0); //STRING_LITERAL
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public Depth visitNotSet(ANTLRv4Parser.NotSetContext ctx) {
         if (ctx.setElement() != null) {
            return ctx.setElement().accept(this);

        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public Depth visitSetElement(ANTLRv4Parser.SetElementContext ctx) {
        if (ctx.LEXER_CHAR_SET() != null)
            return Depth.of(0);

        else
            throw new RuntimeException();
    }

}
