package main.java;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import main.java.parser.ANTLRv4Parser;

import static main.java.Config.ALL_CHARS;
import static main.java.Config.SIGMA;

public class Utils {
    static Random random = new Random();
    public static <T> T randomElem(List<T> l) {
        return l.get(random.nextInt(l.size()));
    }
    public static int randInRange(int a, int b) {
        return random.nextInt(b - a + 1) + a;
    }

    public static StringBuilder c2sb(char c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        return sb;
    }

    public static int ebnfCount(ANTLRv4Parser.EbnfSuffixContext ctx) {
        if (ctx == null ) {
          return 1;
        } else if (ctx.PLUS() != null) {
            return (int) Math.abs(random.nextGaussian()* SIGMA +1);
        } else if (ctx.STAR() != null) {
            return (int) Math.abs(random.nextGaussian()* SIGMA);
        } else if (ctx.QUESTION() != null){
            return random.nextInt(2);
        } else {
            throw new RuntimeException();
        }
    }

    public static StringBuilder refineLiteral(String str) {
        return new StringBuilder(str.substring(1, str.length()-1));
    }

    public static StringBuilder randomCharExcluding(String nots) {
        nots = specialReplace(nots);
        HashSet<Character> hashSet = new HashSet<>();
        for (int i = 1; i < nots.length()-1; i++)
            hashSet.add(nots.charAt(i));

        ArrayList<Character> possibleChars = new ArrayList<>();
        for (Character allChar : ALL_CHARS)
            if (!hashSet.contains(allChar))
                possibleChars.add(allChar);

        return c2sb(randomElem(possibleChars));
    }

    public static StringBuilder randomCharFrom(String str) {
        str = specialReplace(str);
        int index = randInRange(1, str.length()-1);
        return c2sb(str.charAt(index));
    }

    public static String specialReplace(String s) {
        return s.replaceAll("\n", String.valueOf((char) 10))
                .replaceAll("\r", String.valueOf((char) 13))
                .replaceAll("\t", String.valueOf((char) 9));
    }
}
