package main.java;

import main.java.parser.ANTLRv4Parser;
import main.java.parser.ANTLRv4ParserBaseVisitor;
import main.java.utils.MapUtil;

import static main.java.utils.DepthHelper.*;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

import static main.java.parser.ANTLRv4Parser.*;
import static main.java.Main.ruleMap;

public class MinDepthExtractor extends ANTLRv4ParserBaseVisitor<Integer> {
    public MinDepthExtractor(MapUtil<String, Integer> depthMap) {
        this.depthMap = depthMap;
    }

    MapUtil<String, Integer> depthMap;
    Stack<String> callStack = new Stack<>();

    public Integer findMinDepth(String ruleName) {
        if (ruleName.equals("EOF"))
            return defaultResult();

        else if (! ruleMap.containsKey(ruleName))
            throw new RuntimeException("not such a rule: " + ruleName);

        else if (callStack.search(ruleName) > -1)
            return defaultResult();

        else if (depthMap.containsKey(ruleName))
            return depthMap.get(ruleName);

        else {
            callStack.push(ruleName);
            int depth = visitRuleSpec(ruleMap.get(ruleName));
            callStack.pop();
            depthMap.putOrThrow(ruleName, depth);
            return depth;
        }
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
        return ctx.element().stream().map(a -> a.accept(this))
                .filter(Objects::nonNull).max(Integer::compareTo).orElse(null);
    }

    @Override
    public Integer visitElement(ElementContext ctx) {
        if (ctx.labeledElement() != null)
            throw new RuntimeException();

        else if (ctx.actionBlock() != null)
            return null;

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
        if (ctx.ruleref() != null) return findMinDepth(ctx.ruleref().getText()); //RULE_REF

        else if (ctx.notSet() != null) return ctx.notSet().accept(this);

        else if (ctx.terminalDef() != null) return ctx.terminalDef().accept(this);

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
        if (ctx.characterRange() != null) return 0;

        else if (ctx.terminalDef() != null || ctx.notSet() != null)
            return findNotNull(ctx.terminalDef(), ctx.notSet()).accept(this);

        else if (ctx.LEXER_CHAR_SET() != null)
            return 0;

        else if (ctx.DOT() != null)
            throw new RuntimeException();

        else throw new RuntimeException();
    }

    // common----------------------------------------------------------------------
    @Override
    public Integer visitRuleSpec(RuleSpecContext ctx) {
        Integer res = super.visitRuleSpec(ctx);
        if (res == null)
            throw new RuntimeException();
        return res + 1;
    }

    @Override
    public Integer visitTerminalDef(ANTLRv4Parser.TerminalDefContext ctx) {
        if (ctx.TOKEN_REF() != null) {
            return this.findMinDepth(ctx.TOKEN_REF().getText()); //TOKEN_REF
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
