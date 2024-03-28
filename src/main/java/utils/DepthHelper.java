package main.java.utils;

import main.java.parser.ANTLRv4Parser;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class DepthHelper {
    public static boolean zeroRepPossible(ANTLRv4Parser.EbnfSuffixContext ctx) {
        if (ctx == null) return false;

        else if (ctx.PLUS() != null) return false;

        else if (ctx.STAR() != null | ctx.QUESTION() != null) return true;

        else throw new RuntimeException();
    }

    public static boolean zeroRepPossible(ANTLRv4Parser.BlockSuffixContext ctx) {
        if (ctx == null)
            return false;
        return zeroRepPossible(ctx.ebnfSuffix());
    }

    public static final <T> T findNotNull(T... list) {
        var tmp = Arrays.stream(list).filter(Objects::nonNull).collect(Collectors.toList());

        if(tmp.size() == 1) return tmp.get(0);
        else throw new RuntimeException();
    }
}
