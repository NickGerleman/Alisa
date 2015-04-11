package util;

import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Concurrent Map where entries will be removed after a given timeout. Entries are removed on list operations
 * @author Nick Gerleman
 * @param <K> Key Type
 * @param <V> Value Type
 */
public class ConcurrentTimeoutMap<K, V> extends AbstractMap<K, V> {
    private ConcurrentSkipListMap<UniqueInstant, K> timeoutKeyMap = new ConcurrentSkipListMap<>();
    private Map<K, UniqueInstant> keyTimeoutMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<K, V> backingMap = new ConcurrentHashMap<>();

    /**
     * Maps the key to value and gives a timeout
     * @param key mapping key
     * @param value mapping value
     * @param timeout time at which to expire relative to now
     */
    public void putWithTimeout(K key, V value, TemporalAmount timeout) {
        UniqueInstant instant = new UniqueInstant(Instant.now().plus(timeout));
        removeStale();
        timeoutKeyMap.put(instant, key);
        keyTimeoutMap.put(key, instant);
        backingMap.put(key, value);
    }

    /**
     * Change the timeout of an item with the given key
     * @param key the key of the item who's timeout to change
     * @param timeout the new timeout, relative to the time changeTimeout is called
     * @return whether the item was found
     */
    public boolean changeTimeout(K key, TemporalAmount timeout) {
        UniqueInstant instant = keyTimeoutMap.get(key);
        if (instant == null) {
            return false;
        }
        UniqueInstant newInstant = new UniqueInstant(Instant.now().plus(timeout));
        timeoutKeyMap.remove(instant);
        timeoutKeyMap.put(newInstant, key);
        keyTimeoutMap.put(key, newInstant);
        return true;
    }

    /**
     * Force removal of stale entries
     */
    public void removeStale() {
        NavigableMap<UniqueInstant, K> timedOut = timeoutKeyMap.headMap(new UniqueInstant(Instant.now()));
        timedOut.forEach((instant, k) -> {
            backingMap.remove(k);
            timedOut.pollFirstEntry();
        });
    }

    @Override
    public Set<Map.Entry<K,V>> entrySet() {
        removeStale();
        return backingMap.entrySet() ;
    }

    @Override
    public int size() {
        removeStale();
        return backingMap.size();
    }

    @Override
    public V remove(Object key) {
        UniqueInstant instant = keyTimeoutMap.get(key);
        keyTimeoutMap.remove(key);
        if (instant == null) {
            return null;
        }
        timeoutKeyMap.remove(instant);
        return backingMap.remove(key);
    }

    @Override
    public V put(K key, V value) {
        return backingMap.put(key, value);
    }

    @Override
    public V get(Object o) {
        removeStale();
        return backingMap.get(o);
    }

    /**
     * Instant with an additional random component to guarantee uniqueness
     */
    private class UniqueInstant implements Comparable<UniqueInstant> {
        private final Instant instant;
        private final int id = new Random().nextInt();

        public UniqueInstant(Instant instant) {
            this.instant = instant;
        }

        @Override
        public int compareTo(UniqueInstant o) {
            int comp = instant.compareTo(o.instant);
            if (comp != 0) {
                return comp;
            }
            return id - o.id;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != UniqueInstant.class) {
                return false;
            }
            UniqueInstant other = (UniqueInstant) o;
            return  other.id == id && other.instant.equals(instant);
        }

        @Override
        public int hashCode() {
            return instant.hashCode() * 31 + id;
        }

        public Instant getInstant() {
            return instant;
        }

    }
}
