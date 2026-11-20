package controller;

import model.SplayTree;
import view.TreeView;

/**
 * CONTROLADOR del ejemplo básico: ejecuta las operaciones sobre el modelo y
 * pide a la vista que muestre el resultado y los casos de splay aplicados.
 */
public class BasicController {

    private final SplayTree<Integer, String> model;
    private final TreeView view;

    public BasicController(SplayTree<Integer, String> model, TreeView view) {
        this.model = model;
        this.view = view;
        this.model.enableTrace(true);        // queremos ver qué casos de splay ocurren
    }

    public void insert(int key) {
        boolean added = model.insert(key, "valor-" + key);
        view.showOperation("insert(" + key + ")", added ? "insertada" : "ya existía",
                model.getLastRotations());
    }

    public void search(int key) {
        boolean found = model.search(key);
        view.showOperation("search(" + key + ")", found ? "encontrada" : "no existe",
                model.getLastRotations());
    }

    public void delete(int key) {
        boolean deleted = model.delete(key);
        view.showOperation("delete(" + key + ")", deleted ? "eliminada" : "no existe",
                model.getLastRotations());
    }

    private void showTree() {
        view.showTree(model.getRoot());
    }

    /** Escenario de demostración, pensado para ver los tres casos: Zig, Zig-Zig y Zig-Zag. */
    public void runDemo() {
        view.showSection("1) Inserciones ascendentes: cada clave nueva es hija de la raíz (ZIG)");
        for (int key : new int[] {10, 20, 30, 40, 50}) {
            insert(key);
        }
        showTree();

        view.showBlankLine();
        view.showSection("2) Buscar la clave más profunda (10): ZIG-ZIG acorta el camino");
        search(10);
        showTree();

        view.showBlankLine();
        view.showSection("3) Insertar 25 (camino en zigzag): ZIG-ZAG");
        insert(25);
        showTree();

        view.showBlankLine();
        view.showSection("4) Búsqueda fallida (35): sube el último nodo visitado");
        search(35);
        showTree();

        view.showBlankLine();
        view.showSection("5) Eliminación con split/join");
        delete(20);
        delete(99);
        showTree();

        view.showBlankLine();
        view.showSection("6) Recorridos y estado final");
        view.showInorder(model.inorder());
        view.showPreorder(model.preorder());
        view.showSummary(model.size(), model.height(), model.rootKey());
    }
}
