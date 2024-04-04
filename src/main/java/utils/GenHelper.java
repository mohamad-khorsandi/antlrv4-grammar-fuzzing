package utils;
import exception_handling.NotImpelException;
import parser.ANTLRv4Parser.*;
import org.antlr.v4.runtime.ParserRuleContext;

import static fuzzer.StateLessData.scapeChars;

public class GenHelper {

    public StringBuilder c2sb(char c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        return sb;
    }

    public String refineLiteral(String str) {
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
