import controller.BasicController;
import model.SplayTree;
import view.TreeView;

/**
 * EJEMPLO 1 - Básico: insertar, buscar y eliminar, viendo qué caso de splay
 * (Zig, Zig-Zig, Zig-Zag) se aplica en cada paso y cómo queda el árbol.
 *
 * Solo arma las tres piezas del MVC y delega en el controlador.
 *
 * Ejecución (desde la carpeta java/):
 *   javac -d out $(find src -name "*.java")
 *   java -cp out SplayTreeBasico
 */
public class SplayTreeBasico {

    public static void main(String[] args) {
        SplayTree<Integer, String> model = new SplayTree<>();
        TreeView view = new TreeView();
        BasicController controller = new BasicController(model, view);
        controller.runDemo();
    }
}
