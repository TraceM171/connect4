package ipcconnect4.util;

import java.util.HashMap;

public class BiHashMap<K, V> extends HashMap<K, V> {

    public K getFirstKey(V value) {
        return entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .findFirst().get().getKey();
    }
}
