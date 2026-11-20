package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MODELO: caché de capacidad fija respaldada por el Árbol Splay (SplayTree).
 *
 * Cada get()/put() sube la entrada a la raíz, así que las más consultadas quedan arriba.
 * Política de expulsión LRU: cada entrada guarda un "reloj lógico" con su último acceso y,
 * al llenarse, se elimina la más antigua.
 *
 * NOTA DIDÁCTICA: encontrar la entrada más antigua recorre todo el árbol (O(n)); es aceptable
 * para una caché pequeña de demostración. Una caché LRU de producción combina un HashMap con
 * una lista doblemente enlazada para lograr O(1).
 */
public class LruCache<K extends Comparable<K>, V> {

    /** Valor guardado en el árbol: el dato más su último acceso. */
    private static class Entry<V> {
        V value;
        long lastAccess;

        Entry(V value, long lastAccess) {
            this.value = value;
            this.lastAccess = lastAccess;
        }
    }

    private final SplayTree<K, Entry<V>> tree = new SplayTree<>();
    private final int capacity;
    private long clock;
    private long hits;
    private long misses;
    private long evictions;

    public LruCache(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("La capacidad debe ser al menos 1");
        }
        this.capacity = capacity;
    }

    /** Acierto (hit): sube la entrada a la raíz y devuelve el valor. Fallo (miss): devuelve null. */
    public V get(K key) {
        Entry<V> entry = tree.get(key);
        if (entry == null) {
            misses++;
            return null;
        }
        hits++;
        entry.lastAccess = ++clock;
        return entry.value;
    }

    /** Inserta o actualiza una entrada; si se excede la capacidad, expulsa la menos usada. */
    public void put(K key, V value) {
        boolean added = tree.insert(key, new Entry<>(value, ++clock));
        if (added && tree.size() > capacity) {
            evictLeastRecentlyUsed();
        }
    }

    private void evictLeastRecentlyUsed() {
        K victimKey = null;
        long oldest = Long.MAX_VALUE;
        for (Map.Entry<K, Entry<V>> e : tree.entries()) {
            if (e.getValue().lastAccess < oldest) {
                oldest = e.getValue().lastAccess;
                victimKey = e.getKey();
            }
        }
        tree.delete(victimKey);
        evictions++;
    }

    // ---- Consultas de estado (solo datos, sin formato) ----

    public int size() {
        return tree.size();
    }

    public K rootKey() {
        return tree.rootKey();
    }

    public long hits() {
        return hits;
    }

    public long misses() {
        return misses;
    }

    public long evictions() {
        return evictions;
    }

    public double hitRate() {
        long total = hits + misses;
        return total == 0 ? 0.0 : (double) hits / total;
    }

    /** Claves por nivel (recorrido por niveles): la vista decide cómo mostrarlas. */
    public List<List<K>> levels(int maxLevels) {
        List<List<K>> result = new ArrayList<>();
        List<SplayTree.Node<K, Entry<V>>> level = new ArrayList<>();
        if (tree.getRoot() != null) {
            level.add(tree.getRoot());
        }
        for (int depth = 0; depth < maxLevels && !level.isEmpty(); depth++) {
            List<K> keys = new ArrayList<>();
            List<SplayTree.Node<K, Entry<V>>> next = new ArrayList<>();
            for (SplayTree.Node<K, Entry<V>> node : level) {
                keys.add(node.key());
                if (node.left() != null) {
                    next.add(node.left());
                }
                if (node.right() != null) {
                    next.add(node.right());
                }
            }
            result.add(keys);
            level = next;
        }
        return result;
    }
}
