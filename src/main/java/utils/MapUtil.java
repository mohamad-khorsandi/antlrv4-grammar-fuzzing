package main.java.utils;

import java.util.HashMap;


public class MapUtil<K,V> extends HashMap<K,V> {

    public V simpleGet(Object key) {
        return super.get(key);
    }

    public V simplePut(K key, V value) {
        return super.put(key, value);
    }

    @Override
    public V put(K key, V val) {
        if (val == null) throw new RuntimeException();

        if (this.containsKey(key)) {
            throw new RuntimeException(key + "already exists in map");
        } else {
            super.put(key, val);
        }
        return val;
    }

    @Override
    public V get(Object key) {
        if (!this.containsKey(key))
            throw new RuntimeException();
        return super.get(key);
    }
}
