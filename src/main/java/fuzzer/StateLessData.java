package fuzzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.MapUtil;

import java.util.ArrayList;

public class StateLessData {
    public static final Logger log = LoggerFactory.getLogger("Logger");
    public static final ArrayList<Character> printableChars = new ArrayList<>();
    public static final MapUtil<Character, String> scapeChars =new MapUtil<>();

    static {
        for (int i = 32; i <= 126; i++) {
            printableChars.add((char) i);
        }
        scapeChars.put('b', "\b");
        scapeChars.put('t', "\t");
        scapeChars.put('n', "\n");
        scapeChars.put('f', "\f");
        scapeChars.put('r', "\r");
        scapeChars.put('\"', "\"");
        scapeChars.put('\'', "'");
        scapeChars.put('\\', "\\");
    }
}
