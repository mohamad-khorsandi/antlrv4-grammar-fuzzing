package visitors;

import exception_handling.NotImpelException;
import parser.ANTLRv4Parser;
import parser.ANTLRv4ParserBaseVisitor;
import org.antlr.v4.runtime.tree.ErrorNode;
import utils.GenHelper;
import utils.RandUtil;

import static fuzzer.StateLessData.log;
import static utils.GenHelper.replaceScapeChars;

abstract class AbstractGenerateVisitor extends ANTLRv4ParserBaseVisitor<StringBuilder> {
    final protected GenHelper helper = new GenHelper();
    final protected RandUtil rand;

    public AbstractGenerateVisitor(Integer seed) {
        this.rand = RandUtil.by(seed, helper);
    }

    abstract protected StringBuilder generate(String ruleName);

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
        log.trace("visitTerminalDef");
        if (ctx.TOKEN_REF() != null) {
            return this.generate(ctx.TOKEN_REF().getText()); //TOKEN_REF
        } else if (ctx.STRING_LITERAL() != null) {
            String refined = helper.refineLiteral(ctx.STRING_LITERAL().getText());
            return replaceScapeChars(refined).append(" "); //STRING_LITERAL
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public StringBuilder visitNotSet(ANTLRv4Parser.NotSetContext ctx) {
        log.trace("visitNotSet");
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
        log.trace("visitSetElement");

        if (ctx.TOKEN_REF() != null || ctx.STRING_LITERAL() != null || ctx.characterRange() != null) {
            throw new NotImpelException();

        } else if (ctx.LEXER_CHAR_SET() != null) {
            return rand.randCharExcluding(ctx.LEXER_CHAR_SET().getText());

        }else {
            throw new RuntimeException();
        }

    }
}
