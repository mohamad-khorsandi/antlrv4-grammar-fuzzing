package main.java.utils;

import main.java.parser.ANTLRv4Parser;
import org.antlr.v4.runtime.RuleContext;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;


public class MapUtil<K,V> extends HashMap<K,V> {

    public void putOrThrow(K key, V val) {
        if (this.containsKey(key)) {
            throw new RuntimeException(key + "already exists in map");
        } else {
            put(key, val);
        }
    }

    public V getCache(K key, Function<K, V> provider) {
        if (this.containsKey(key))
            return this.get(key);
        else
            return this.put(key, provider.apply(key));

    }
}
