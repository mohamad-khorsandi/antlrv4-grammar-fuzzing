import org.antlr.v4.runtime.tree.TerminalNode;
import parser.ANTLRv4Parser;

import java.util.List;
import java.util.Random;

public class Utils {
    static Random random = new Random();
    public static <T> T randomElem(List<T> l) {
        return l.get(random.nextInt(l.size()));
    }

    public static int ebnfSuffixToCount(ANTLRv4Parser.EbnfSuffixContext ctx) {
        if (ctx.PLUS() != null) {
            return Config.COUNT;
        } else if (ctx.STAR() != null) {
            return Config.COUNT;

        } else if (ctx.QUESTION() != null){
            return random.nextInt(2);
        } else {
            throw new RuntimeException();
        }
    }

    public static StringBuilder randomPrintableChar() {
        return randomCharInRange(32, 126);
    }

    public static StringBuilder randomCharInRange(int a, int b) {
        int randomAscii = (int) (Math.random() * (b - a + 1)) + a;
        return new StringBuilder(String.valueOf((char) randomAscii));
    }

    public static StringBuilder refineStringLiteral(TerminalNode terminalNode) {
        return new StringBuilder(terminalNode.getText().substring(1, terminalNode.getText().length()-1));
    }

    public static char refineCharLiteral(TerminalNode terminalNode) {
        return terminalNode.getText().charAt(1);
    }
}
