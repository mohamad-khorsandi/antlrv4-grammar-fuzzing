package main.java.utils;
import main.java.parser.ANTLRv4Parser.*;
import java.util.*;

import static main.java.Config.*;
import static main.java.Main.rand;

public class GenHelper {
    public static StringBuilder c2sb(char c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        return sb;
    }

    public static int ebnfCount(EbnfSuffixContext ctx) {
        if (ctx == null ) {
          return 1;
        } else if (ctx.PLUS() != null) {
            return (int) (Math.abs(rand.nextGaussian()) * SIGMA + 1) ;
        } else if (ctx.STAR() != null) {
            return (int) (Math.abs(rand.nextGaussian()) * SIGMA) ;
        } else if (ctx.QUESTION() != null){
            return rand.nextInt(2);
        } else {
            throw new RuntimeException();
        }
    }

    public static int ebnfCount(BlockSuffixContext ctx) {
        if (ctx == null)
            return 1;
        else return ebnfCount(ctx.ebnfSuffix());
    }

    public static StringBuilder refineLiteral(String str) {
        return new StringBuilder(str.substring(1, str.length()-1));
    }

    public static String replaceScapeChars(String s) {
        HashMap<Character, Character> map =new HashMap<>();

        map.put('r', '\r');
        map.put('t', '\t');
        map.put('n', '\n');
        map.put('\'', '\'');
        map.put('\\', '\\');

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length()-1; i++) {
            if (s.charAt(i) == '\\') {
                i++;
                if (map.containsKey(s.charAt(i)))
                    result.append(map.get(s.charAt(i)));
                else throw new RuntimeException();
            } else {
                result.append(s.charAt(i));
            }
        }
        result.append(s.charAt(s.length()-1));
        return result.toString();
    }
}
