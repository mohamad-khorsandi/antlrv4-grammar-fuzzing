package main.java;

import org.antlr.v4.runtime.tree.TerminalNode;
import parser.ANTLRv4Parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

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


    public static StringBuilder randomCharInRange(int a, int b) {
        return c2sb((char) randInRange(a, b));
    }

    public static StringBuilder refineLiteral(String str) {
        return new StringBuilder(str.substring(1, str.length()-1));
    }

    public static StringBuilder randomCharExcluding(String nots) {
        HashSet<Character> hashSet = new HashSet<>();
        for (int i = 1; i < nots.length()-1; i++)
            hashSet.add(nots.charAt(i));

        ArrayList<Character> possibleChars = new ArrayList<>();
        for (int i = 0; i < Config.ALL_CHARS.size(); i++)
            if (!hashSet.contains(Config.ALL_CHARS.get(i)))
                possibleChars.add(Config.ALL_CHARS.get(i));

        return c2sb(randomElem(possibleChars));
    }

    public static StringBuilder randomCharFrom(String str) {
        int index = randInRange(1, str.length()-1);
        return str.charAt(index);
    }
}
