package utils;
import exception_handling.NotImpelException;
import parser.ANTLRv4Parser.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fuzzer.StateLessData.log;
import static fuzzer.StateLessData.scapeChars;

public class GenHelper {
    private GenHelper() {
    }

    public static StringBuilder c2sb(char c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        return sb;
    }

    public static String refineLiteral(String str) {
        return str.substring(1, str.length() - 1);
    }
    
    public static StringBuilder replaceScapeChars(String input) {
        Matcher matcher = Pattern.compile("\\\\[btnfr\"'\\\\]|\\\\u[0-9a-fA-F]{4}")
                .matcher(input);
        StringBuilder result = new StringBuilder();
        //todo test this code
        while (matcher.find()) {
            String match = matcher.group();
            if (match.startsWith("\\u")) {
                log.warn("unicode characters found in the grammar");
                char unicodeChar = (char) Integer.parseInt(match.substring(2), 16);
                matcher.appendReplacement(result, Character.toString(unicodeChar));
            } else {
                char escapeChar = match.charAt(1);
                matcher.appendReplacement(result, scapeChars.getOrDefault(escapeChar, ""));
            }
        }

        matcher.appendTail(result);
        return result;
    }

    public static String getScapeChar(char afterSlash) {
        if (afterSlash == 'u')
            throw new NotImpelException();
        return scapeChars.get(afterSlash);
    }
}
