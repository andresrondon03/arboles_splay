package model;

/**
 * Contrato común de los árboles de búsqueda que se comparan en el benchmark
 * (el Árbol Splay y el BST convencional).
 */
public interface SearchTree<K extends Comparable<K>, V> {

    /** Inserta una clave. Devuelve true si es nueva, false si ya existía (se actualiza su valor). */
    boolean insert(K key, V value);

    /** Busca una clave. Devuelve true si existe. */
    boolean search(K key);

    /** Comparaciones de clave acumuladas durante las búsquedas. */
    long comparisons();

    void resetComparisons();
}
