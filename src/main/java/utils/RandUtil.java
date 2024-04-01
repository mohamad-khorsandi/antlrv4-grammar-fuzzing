package utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import static main.SingletonInjector.*;
import static utils.GenHelper.*;

public class RandUtil extends Random {
    public RandUtil(Integer seed) {
        super();
        if (seed != null) {
          this.setSeed(seed);
        }
    }

    public int randInRange(int a, int b) {
        return rand.nextInt(b - a + 1) + a;
    }

    public StringBuilder randCharExcluding(String set) {
        HashSet<Character> notChars = new HashSet<>();
        charsOfSet(set, notChars);
        HashSet<Character> allowedChars = new HashSet<>(printableChars);
        allowedChars.removeAll(notChars);
        return c2sb(ListUtil.by(allowedChars).randElem());
    }

    public StringBuilder randomCharFrom(String set) {
        ListUtil<Character> chars = new ListUtil<>();
        charsOfSet(set, chars);
        return c2sb(chars.randElem());
    }

    private static void charsOfSet(String set, Collection<Character> result) {
        StringBuilder sb = replaceScapeChars(set);

        for (int i = 1; i < set.length()-1; i++) {
            if (set.charAt(i+1) == '-') {
                char a = sb.charAt(i);
                char b = set.charAt(i+2);
                for (int j = a; j <= b; j++)
                    result.add((char)j);
                i += 2;
            } else {
                result.add(set.charAt(i));
            }
        }
    }
}
