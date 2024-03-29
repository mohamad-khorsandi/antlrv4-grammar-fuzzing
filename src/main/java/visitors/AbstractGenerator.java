package main.java.visitors;

import main.java.exception_handling.NotImpelException;
import main.java.parser.ANTLRv4Parser;
import main.java.parser.ANTLRv4ParserBaseVisitor;
import main.java.utils.GenHelper;
import org.antlr.v4.runtime.tree.ErrorNode;

import static main.java.Main.rand;

abstract class AbstractGenerator extends ANTLRv4ParserBaseVisitor<StringBuilder> {
    protected Integer depthLimit;

    abstract public StringBuilder generate(String ruleName);

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
            throw new NotImpelException();

        } else if (ctx.setElement() != null) {
            return ctx.setElement().accept(this);

        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public StringBuilder visitSetElement(ANTLRv4Parser.SetElementContext ctx) {
        System.out.println("visitSetElement");

        if (ctx.TOKEN_REF() != null || ctx.STRING_LITERAL() != null || ctx.characterRange() != null) {
            throw new NotImpelException();

        } else if (ctx.LEXER_CHAR_SET() != null) {
            return rand.randCharExcluding(ctx.LEXER_CHAR_SET().getText());

        }else {
            throw new RuntimeException();
        }

    }
}
