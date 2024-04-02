package fuzzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.MapUtil;
import utils.RandUtil;
import visitors.DepthFinder;

import java.util.ArrayList;

import static fuzzer.FuzzerConfig.SEED;
import static parser.ANTLRv4Parser.*;

public class SingletonInjector {
    public static final RandUtil rand = new RandUtil();
    public static final MapUtil<String, RuleSpecContext> ruleNameMap = new MapUtil<>();
    public static final DepthFinder depthFinder = new DepthFinder();
    public static final Logger log = LoggerFactory.getLogger(SingletonInjector.class);
    public static final ArrayList<Character> printableChars = new ArrayList<>();
    public static final MapUtil<Character, Character> scapeChars =new MapUtil<>();


    static {
        rand.setSeed(SEED);
        for (int i = 32; i <= 126; i++) {
            printableChars.add((char) i);
        }

        scapeChars.put('b', '\b');
        scapeChars.put('t', '\t');
        scapeChars.put('n', '\n');
        scapeChars.put('f', '\f');
        scapeChars.put('r', '\r');
        scapeChars.put('\"', '\"');
        scapeChars.put('\'', '\'');
        scapeChars.put('\\', '\\');
    }
}