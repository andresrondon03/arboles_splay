package model;

/**
 * MODELO: BST convencional, SIN balanceo ni reorganización.
 * Sirve como punto de comparación contra el Árbol Splay en el benchmark.
 */
public class PlainBst<K extends Comparable<K>, V> implements SearchTree<K, V> {

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V> root;
    private long comparisons;

    @Override
    public boolean insert(K key, V value) {
        if (root == null) {
            root = new Node<>(key, value);
            return true;
        }
        Node<K, V> current = root;
        while (true) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                if (current.left == null) {
                    current.left = new Node<>(key, value);
                    return true;
                }
                current = current.left;
            } else if (cmp > 0) {
                if (current.right == null) {
                    current.right = new Node<>(key, value);
                    return true;
                }
                current = current.right;
            } else {
                current.value = value;
                return false;
            }
        }
    }

    @Override
    public boolean search(K key) {
        Node<K, V> current = root;
        while (current != null) {
            comparisons++;
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public long comparisons() {
        return comparisons;
    }

    @Override
    public void resetComparisons() {
        comparisons = 0;
    }
}
