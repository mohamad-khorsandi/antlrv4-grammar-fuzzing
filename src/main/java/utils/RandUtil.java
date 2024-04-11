package utils;



import fuzzer.FuzzParams;

import java.util.*;
import static parser.ANTLRv4Parser.EbnfSuffixContext;
import static fuzzer.StateLessData.log;
import static fuzzer.StateLessData.printableChars;
import static utils.GenHelper.*;
import static parser.ANTLRv4Parser.BlockSuffixContext;

public class RandUtil extends Random {
    private RandUtil(long seed) {
        super(seed);
    }

    public static RandUtil by(Integer seed) {
        if (seed == null) {
            seed = Math.toIntExact(System.currentTimeMillis() % Integer.MAX_VALUE);
            log.info("random seed: " + seed);
        }
        return new RandUtil(seed);
    }

    public int randInRange(int a, int b) {
        return this.nextInt(b - a + 1) + a;
    }

    public StringBuilder randCharExcluding(String set) {
        HashSet<Character> notChars = new HashSet<>();
        charsOfSet(set, notChars);
        HashSet<Character> allowedChars = new HashSet<>(printableChars);
        allowedChars.removeAll(notChars);
        return c2sb(randElem(new ArrayList<>(allowedChars)));
    }

    public StringBuilder randomCharFrom(String set) {
        List<Character> chars = new ArrayList<>();
        charsOfSet(set, chars);
        return c2sb(randElem(chars));
    }

    private void charsOfSet(String set, Collection<Character> result) {
        StringBuilder sb = replaceScapeChars(set);

        for (int i = 1; i < sb.length()-1; i++) {
            if (sb.charAt(i+1) == '-') {
                char a = sb.charAt(i);
                char b = sb.charAt(i+2);
                for (int j = a; j <= b; j++)
                    result.add((char)j);
                i += 2;
            } else {
                result.add(sb.charAt(i));
            }
        }
    }

    public int bernoulli(double p) {
        if (nextDouble() < p)
            return 1;
        else
            return 0;
    }

    public int ebnfCount(int ruleMinDepth, EbnfSuffixContext ctx, int limit, FuzzParams params) {
        if (ctx == null ) return 1;

        if (ruleMinDepth > limit)
            if (DepthHelper.zeroRepPossible(ctx))
                return 0;
            else
                throw new RuntimeException();

        if (ctx.PLUS() != null) {
            return (int) (Math.abs(nextGaussian()) * params.plusStarGaussianSigma + 1) ;
        } else if (ctx.STAR() != null) {
            return (int) (Math.abs(nextGaussian()) * params.plusStarGaussianSigma) ;
        } else if (ctx.QUESTION() != null){
            return bernoulli(params.questionBernoulliProp);
        } else {
            throw new RuntimeException();
        }
    }

    public int ebnfCount(int ruleMinDepth, BlockSuffixContext ctx, int limit, FuzzParams params) {
        if (ctx == null)
            return 1;
        else return ebnfCount(ruleMinDepth, ctx.ebnfSuffix(), limit, params);
    }

    public <S> S randElem(List<S> list) {
        int a = this.nextInt(list.size());
        return list.get(a);
    }
}
