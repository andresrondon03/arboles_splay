package view;

import model.RotationType;
import model.SplayTree;

import java.util.List;
import java.util.stream.Collectors;

/**
 * VISTA del ejemplo básico: salida por consola con la traza de rotaciones y el dibujo ASCII.
 * No modifica el árbol: solo recibe datos y LEE los nodos para dibujarlos.
 */
public class TreeView {

    public void showSection(String title) {
        System.out.println("=== " + title + " ===");
    }

    public void showBlankLine() {
        System.out.println();
    }

    /** Muestra una operación, su resultado y los casos de splay que se aplicaron. */
    public void showOperation(String call, String result, List<RotationType> rotations) {
        String steps = rotations.isEmpty()
                ? "ninguna"
                : rotations.stream().map(RotationType::label).collect(Collectors.joining(", "));
        System.out.printf("%-12s -> %-11s | rotaciones: %s%n", call, result, steps);
    }

    public void showInorder(List<Integer> keys) {
        System.out.println("Inorder  : " + keys + "   (siempre ordenado)");
    }

    public void showPreorder(List<Integer> keys) {
        System.out.println("Preorder : " + keys + "   (refleja la forma actual)");
    }

    public void showSummary(int size, int height, Integer rootKey) {
        System.out.println("Tamaño = " + size + " | altura = " + height + " | raíz = " + rootKey);
    }

    /**
     * Dibuja el árbol girado 90°: la raíz queda a la izquierda,
     * los hijos derechos arriba (/) y los izquierdos abajo (\).
     */
    public void showTree(SplayTree.Node<Integer, String> root) {
        if (root == null) {
            System.out.println("(árbol vacío)");
            return;
        }
        draw(root, 0, ' ');
    }

    private void draw(SplayTree.Node<Integer, String> node, int depth, char branch) {
        if (node == null) {
            return;
        }
        draw(node.right(), depth + 1, '/');
        String prefix = depth == 0 ? "" : "    ".repeat(depth - 1) + "    " + branch + "-- ";
        System.out.println(prefix + node.key());
        draw(node.left(), depth + 1, '\\');
    }
}
