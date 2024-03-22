package main.java;
import java.util.*;
import java.util.regex.Pattern;

import main.java.parser.ANTLRv4Parser;

import static main.java.Config.*;

public class Utils {
    static Random random;
    public static <T> T randomElem(List<T> l) {
        return l.get(random.nextInt(l.size()));
    }
    public static int randInRange(int a, int b) {
        return random.nextInt(b - a + 1) + a;
    }

    static {
        if (SEED == null) random = new Random();
        else random = new Random(SEED);
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
            return (int) (Math.abs(random.nextGaussian()) * SIGMA + 1) ;
        } else if (ctx.STAR() != null) {
            return (int) (Math.abs(random.nextGaussian()) * SIGMA) ;
        } else if (ctx.QUESTION() != null){
            return random.nextInt(2);
        } else {
            throw new RuntimeException();
        }
    }

    public static int ebnfCount(ANTLRv4Parser.BlockSuffixContext ctx) {
        if (ctx == null)
            return 1;
        else return ebnfCount(ctx.ebnfSuffix());
    }

    public static StringBuilder refineLiteral(String str) {
        return new StringBuilder(str.substring(1, str.length()-1));
    }

    public static StringBuilder randomCharExcluding(String set) {
        HashSet<Character> notChars = new HashSet<>();
        charsOfSet(set, notChars);
        HashSet<Character> allowedChars = new HashSet<>(ALL_CHARS);
        allowedChars.removeAll(notChars);
        return c2sb(randomElem(new ArrayList<>(allowedChars)));
    }

    public static StringBuilder randomCharFrom(String set) {
        ArrayList<Character> chars = new ArrayList<>();
        charsOfSet(set, chars);
        return c2sb(randomElem(chars));
    }

    public static void charsOfSet(String set, Collection<Character> result) {
        set = specialReplace(set);

        for (int i = 1; i < set.length()-1; i++) {
            if (set.charAt(i+1) == '-') {
                char a = set.charAt(i);
                char b = set.charAt(i+2);
                for (int j = a; j <= b; j++)
                    result.add((char)j);
                i += 2;
            } else {
                result.add(set.charAt(i));
            }
        }
    }

    public static String specialReplace(String s) {
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
        return result.toString();
    }
}
