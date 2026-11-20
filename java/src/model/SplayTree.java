package model;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * MODELO: Árbol Splay (bottom-up, iterativo, con puntero al padre).
 *
 * Este es el ÚNICO árbol del proyecto: lo usan el ejemplo básico, la caché y el benchmark.
 * No imprime nada. Si se activa la traza (enableTrace), registra qué casos de splay
 * (Zig, Zig-Zig, Zig-Zag) se aplicaron en la última operación, para que la vista los muestre.
 *
 * @param <K> tipo de la clave (comparable)
 * @param <V> tipo del valor asociado
 */
public class SplayTree<K extends Comparable<K>, V> implements SearchTree<K, V> {

    /** Nodo del árbol. Los getters públicos permiten a la vista LEERLO sin modificarlo. */
    public static class Node<K, V> {
        private K key;
        private V value;
        private Node<K, V> left;
        private Node<K, V> right;
        private Node<K, V> parent;

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K key() {
            return key;
        }

        public V value() {
            return value;
        }

        public Node<K, V> left() {
            return left;
        }

        public Node<K, V> right() {
            return right;
        }
    }

    private Node<K, V> root;
    private int size;
    private long comparisons;
    private boolean tracing;
    private final List<RotationType> lastRotations = new ArrayList<>();

    // ------------------------------------------------------------------
    // Consultas de estado (solo datos)
    // ------------------------------------------------------------------

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return root == null;
    }

    /** Clave de la raíz, o null si el árbol está vacío. */
    public K rootKey() {
        return root == null ? null : root.key;
    }

    /** Raíz para lectura (la vista la usa para dibujar el árbol). */
    public Node<K, V> getRoot() {
        return root;
    }

    /** Altura (número de nodos del camino más largo). Pensada para demos pequeñas. */
    public int height() {
        return height(root);
    }

    private int height(Node<K, V> node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    @Override
    public long comparisons() {
        return comparisons;
    }

    @Override
    public void resetComparisons() {
        comparisons = 0;
    }

    // ------------------------------------------------------------------
    // Traza opcional de los casos de splay
    // ------------------------------------------------------------------

    /** Activa o desactiva el registro de rotaciones (desactivado por defecto: no cuesta nada). */
    public void enableTrace(boolean enabled) {
        this.tracing = enabled;
    }

    /** Casos de splay aplicados durante la última operación (insert, search, get o delete). */
    public List<RotationType> getLastRotations() {
        return Collections.unmodifiableList(new ArrayList<>(lastRotations));
    }

    private void startOperation() {
        if (tracing) {
            lastRotations.clear();
        }
    }

    private void record(RotationType type) {
        if (tracing) {
            lastRotations.add(type);
        }
    }

    // ------------------------------------------------------------------
    // Rotaciones
    // ------------------------------------------------------------------

    /** Rotación a la derecha: x es hijo IZQUIERDO de su padre p y sube sobre él. */
    private void rotateRight(Node<K, V> x) {
        Node<K, V> p = x.parent;
        Node<K, V> g = p.parent;

        p.left = x.right;              // el subárbol derecho de x pasa a ser hijo izquierdo de p
        if (x.right != null) {
            x.right.parent = p;
        }
        x.right = p;                   // p baja y se vuelve hijo derecho de x
        p.parent = x;

        x.parent = g;                  // x ocupa el lugar que tenía p
        if (g == null) {
            root = x;
        } else if (g.left == p) {
            g.left = x;
        } else {
            g.right = x;
        }
    }

    /** Rotación a la izquierda: x es hijo DERECHO de su padre p y sube sobre él. */
    private void rotateLeft(Node<K, V> x) {
        Node<K, V> p = x.parent;
        Node<K, V> g = p.parent;

        p.right = x.left;              // el subárbol izquierdo de x pasa a ser hijo derecho de p
        if (x.left != null) {
            x.left.parent = p;
        }
        x.left = p;                    // p baja y se vuelve hijo izquierdo de x
        p.parent = x;

        x.parent = g;                  // x ocupa el lugar que tenía p
        if (g == null) {
            root = x;
        } else if (g.left == p) {
            g.left = x;
        } else {
            g.right = x;
        }
    }

    // ------------------------------------------------------------------
    // Splay: lleva el nodo x hasta la raíz
    // ------------------------------------------------------------------

    private void splay(Node<K, V> x) {
        while (x.parent != null) {
            Node<K, V> p = x.parent;
            Node<K, V> g = p.parent;

            if (g == null) {
                // Caso ZIG: el padre es la raíz -> una sola rotación
                record(RotationType.ZIG);
                if (x == p.left) {
                    rotateRight(x);
                } else {
                    rotateLeft(x);
                }
            } else if (x == p.left && p == g.left) {
                // Caso ZIG-ZIG (izquierda-izquierda): primero el padre, luego x
                record(RotationType.ZIG_ZIG);
                rotateRight(p);
                rotateRight(x);
            } else if (x == p.right && p == g.right) {
                // Caso ZIG-ZIG (derecha-derecha)
                record(RotationType.ZIG_ZIG);
                rotateLeft(p);
                rotateLeft(x);
            } else if (x == p.right && p == g.left) {
                // Caso ZIG-ZAG (izquierda-derecha): x rota dos veces
                record(RotationType.ZIG_ZAG);
                rotateLeft(x);
                rotateRight(x);
            } else {
                // Caso ZIG-ZAG (derecha-izquierda)
                record(RotationType.ZIG_ZAG);
                rotateRight(x);
                rotateLeft(x);
            }
        }
    }

    // ------------------------------------------------------------------
    // Operaciones
    // ------------------------------------------------------------------

    /**
     * Inserta una clave como en un BST y luego hace splay del nodo nuevo.
     * Si la clave ya existe, se actualiza su valor y se hace splay del nodo existente.
     *
     * @return true si se insertó, false si ya existía
     */
    @Override
    public boolean insert(K key, V value) {
        startOperation();
        if (root == null) {
            root = new Node<>(key, value);
            size++;
            return true;
        }
        Node<K, V> current = root;
        Node<K, V> parent = null;
        int cmp = 0;
        while (current != null) {
            parent = current;
            cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                current.value = value;       // duplicado: se actualiza y se acerca a la raíz
                splay(current);
                return false;
            }
        }
        Node<K, V> node = new Node<>(key, value);
        node.parent = parent;
        if (cmp < 0) {
            parent.left = node;
        } else {
            parent.right = node;
        }
        size++;
        splay(node);
        return true;
    }

    /** Busca una clave: si existe sube a la raíz; si no, sube el último nodo visitado. */
    @Override
    public boolean search(K key) {
        startOperation();
        return access(key) != null;
    }

    /** Como search(), pero devuelve el valor asociado (o null si la clave no existe). */
    public V get(K key) {
        startOperation();
        Node<K, V> node = access(key);
        return node == null ? null : node.value;
    }

    /** Descenso tipo BST + splay. Devuelve el nodo encontrado o null. */
    private Node<K, V> access(K key) {
        Node<K, V> current = root;
        Node<K, V> last = null;
        while (current != null) {
            last = current;
            comparisons++;
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                splay(current);
                return current;
            }
        }
        if (last != null) {
            splay(last);                     // fallo: igual se amortiza el camino recorrido
        }
        return null;
    }

    /**
     * Elimina una clave usando split/join:
     *  1. access(key): si existe, queda en la raíz.
     *  2. SPLIT: se separa el subárbol izquierdo (L) y el derecho (R).
     *  3. JOIN: se sube el máximo de L a la raíz de L (queda sin hijo derecho)
     *           y se cuelga R como su hijo derecho.
     *
     * @return true si la clave existía y fue eliminada
     */
    public boolean delete(K key) {
        startOperation();
        if (access(key) == null) {
            return false;
        }
        Node<K, V> left = root.left;             // --- SPLIT ---
        Node<K, V> right = root.right;
        if (left != null) {
            left.parent = null;
        }
        if (right != null) {
            right.parent = null;
        }
        root.left = null;
        root.right = null;

        if (left == null) {                      // --- JOIN ---
            root = right;                        // no había nada a la izquierda
        } else {
            root = left;                         // trabajamos temporalmente con L como árbol propio
            Node<K, V> max = left;
            while (max.right != null) {
                max = max.right;
            }
            splay(max);                          // el máximo de L sube y no tiene hijo derecho
            root.right = right;
            if (right != null) {
                right.parent = root;
            }
        }
        size--;
        return true;
    }

    // ------------------------------------------------------------------
    // Recorridos (iterativos: seguros incluso con árboles degenerados)
    // ------------------------------------------------------------------

    /** Inorder (izquierda, raíz, derecha): las claves en orden ascendente. */
    public List<K> inorder() {
        List<K> result = new ArrayList<>();
        Deque<Node<K, V>> stack = new ArrayDeque<>();
        Node<K, V> current = root;
        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            result.add(current.key);
            current = current.right;
        }
        return result;
    }

    /** Preorder (raíz, izquierda, derecha): refleja la forma actual del árbol. */
    public List<K> preorder() {
        List<K> result = new ArrayList<>();
        Deque<Node<K, V>> stack = new ArrayDeque<>();
        if (root != null) {
            stack.push(root);
        }
        while (!stack.isEmpty()) {
            Node<K, V> node = stack.pop();
            result.add(node.key);
            if (node.right != null) {
                stack.push(node.right);
            }
            if (node.left != null) {
                stack.push(node.left);
            }
        }
        return result;
    }

    /** Pares (clave, valor) en orden ascendente. */
    public List<Map.Entry<K, V>> entries() {
        List<Map.Entry<K, V>> result = new ArrayList<>();
        Deque<Node<K, V>> stack = new ArrayDeque<>();
        Node<K, V> current = root;
        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            result.add(new AbstractMap.SimpleImmutableEntry<>(current.key, current.value));
            current = current.right;
        }
        return result;
    }
}
