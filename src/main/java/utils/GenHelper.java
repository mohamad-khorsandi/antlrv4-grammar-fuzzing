package utils;
import exception_handling.NotImpelException;
import parser.ANTLRv4Parser.*;
import org.antlr.v4.runtime.ParserRuleContext;

import static fuzzer.FuzzerConfig.PLUS_STAR_GAUSSIAN_SIGMA;
import static fuzzer.FuzzerConfig.QUESTION_BERNOULLI_PROP;
import static fuzzer.SingletonInjector.*;


public class GenHelper {
    public static StringBuilder c2sb(char c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        return sb;
    }
    public static int bernoulli(double p) {
        if (rand.nextDouble() < p)
            return 1;
        else
            return 0;
    }
    public static int ebnfCount(ParserRuleContext parserRule, EbnfSuffixContext ctx, int limit) {
        if (ctx == null ) return 1;

        if (depthFinder.depthOf(parserRule) > limit)
            if (DepthHelper.zeroRepPossible(ctx))
                return 0;
            else
                throw new RuntimeException();

        if (ctx.PLUS() != null) {
            return (int) (Math.abs(rand.nextGaussian()) * PLUS_STAR_GAUSSIAN_SIGMA + 1) ;
        } else if (ctx.STAR() != null) {
            return (int) (Math.abs(rand.nextGaussian()) * PLUS_STAR_GAUSSIAN_SIGMA) ;
        } else if (ctx.QUESTION() != null){
            return bernoulli(QUESTION_BERNOULLI_PROP);
        } else {
            throw new RuntimeException();
        }
    }

    public static int ebnfCount(ParserRuleContext parserRule, BlockSuffixContext ctx, int limit) {
        if (ctx == null)
            return 1;
        else return ebnfCount(parserRule, ctx.ebnfSuffix(), limit);
    }

    public static String refineLiteral(String str) {
        return str.substring(1, str.length()-1);
    }

    public static StringBuilder replaceScapeChars(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length()-1; i++) {
            if (s.charAt(i) == '\\') {
                i++;
                result.append(getScapeChar(s.charAt(i)));
            } else {
                result.append(s.charAt(i));
            }
        }
        result.append(s.charAt(s.length()-1));
        return result;
    }

    public static char getScapeChar(char afterSlash) {
        if (afterSlash == 'u')
            throw new NotImpelException();
        return scapeChars.get(afterSlash);
    }
}
