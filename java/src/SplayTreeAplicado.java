import controller.AppliedController;
import view.AppliedView;

/**
 * EJEMPLO 2 - Aplicado: el Árbol Splay en uso real.
 *  - Caché LRU: las entradas más consultadas quedan en la raíz.
 *  - Benchmark: Splay vs BST convencional con distintos patrones de acceso.
 *
 * Ejecución (desde la carpeta java/):
 *   javac -d out $(find src -name "*.java")
 *   java -cp out SplayTreeAplicado
 */
public class SplayTreeAplicado {

    public static void main(String[] args) {
        AppliedView view = new AppliedView();
        AppliedController controller = new AppliedController(view);
        controller.runDemo();
    }
}
