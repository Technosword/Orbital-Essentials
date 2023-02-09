package it.thedarksword.essentialsvc.concurrent;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WeakConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

    private static final long serialVersionUID = 1L;

    private Map<K, Long> timeMap = new ConcurrentHashMap<>();
    private long expiryInMillis;
    private boolean mapAlive = true;

    public WeakConcurrentHashMap(Long expiryInMillis) {
        this.expiryInMillis = expiryInMillis;
        initialize();
    }

    void initialize() {
        new CleanerThread().start();
    }

    public Long getRemainingTime(K key) {
        if (!timeMap.containsKey(key)) return 0L;

        long currentTime = new Date().getTime();
        return (timeMap.get(key) + expiryInMillis) - currentTime;
    }

    @Override
    public V put(K key, V value) {
        if (!mapAlive) {
            initialize();
        }
        Date date = new Date();
        timeMap.put(key, date.getTime());

        V returnVal = super.put(key, value);

        return returnVal;
    }

    @Override
    public boolean containsKey(Object key) {
        long currentTime = new Date().getTime();
        if (timeMap.containsKey(key)) {
            if (((timeMap.get(key) + expiryInMillis) - currentTime) < 0L) {
                timeMap.remove(key);
                remove(key);
            }
        }

        return super.containsKey(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (!mapAlive) {
            initialize();
        }
        for (K key : m.keySet()) {
            put(key, m.get(key));
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (!mapAlive) {
            initialize();
        }
        if (!containsKey(key)) {
            return put(key, value);
        } else {
            return get(key);
        }
    }

    public void quitMap() {
        mapAlive = false;
    }

    public boolean isAlive() {
        return mapAlive;
    }

    class CleanerThread extends Thread {

        @Override
        public void run() {
            while (mapAlive) {
                cleanMap();
                try {
                    Thread.sleep(expiryInMillis / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void cleanMap() {
            long currentTime = new Date().getTime();
            for (K key : timeMap.keySet()) {
                if (((timeMap.get(key) + expiryInMillis) - currentTime) < 0L) {
                    V value = remove(key);
                    timeMap.remove(key);
                }
            }
        }
    }

}
