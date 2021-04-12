package ipcconnect4.util;

import java.util.HashMap;

/**
 * Hash map with some convenient methods
 *
 * @param <K> key type
 * @param <V> value type
 */
public class BiHashMap<K, V> extends HashMap<K, V> {

    /**
     * Get the key of the first occurrence of the given value
     *
     * @param value
     * @return key
     */
    public K getFirstKey(V value) {
        return entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .findFirst().get().getKey();
    }
}
