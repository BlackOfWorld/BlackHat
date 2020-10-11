package me.bow.treecapitatorultimate.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DoubleHashMap<K, V> extends HashMap<K, V> {

    Map<V, K> reverseMap = new HashMap<V, K>();

    @Override
    public V put(K key, V value) {
        reverseMap.put(value, key);
        return super.put(key, value);
    }

    public K getKey(V value) {
        return reverseMap.get(value);
    }

    public K removeValue(Object value) {
        Iterator<Map.Entry<K, V>> iterator = this.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            if (value.equals(entry.getValue())) {
                iterator.remove();
                this.reverseMap.remove(entry);
                return (K) entry.getKey();
            }
        }
        return null;
    }
}