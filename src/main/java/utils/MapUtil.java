package main.java.utils;

import main.java.parser.ANTLRv4Parser;
import org.antlr.v4.runtime.RuleContext;

import java.util.HashMap;


public class MapUtil<K,V> extends HashMap<K,V> {

    public void putOrThrow(K key, V val) {
        if (this.containsKey(key)) {
            throw new RuntimeException(key + "already exists in map");
        } else {
            put(key, val);
        }
    }
}
