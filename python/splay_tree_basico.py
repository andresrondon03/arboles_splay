"""
EJEMPLO 1 - Básico: insertar, buscar y eliminar, viendo qué caso de splay
(Zig, Zig-Zig, Zig-Zag) se aplica en cada paso y cómo queda el árbol.

Solo arma las tres piezas del MVC y delega en el controlador.

Ejecución (desde la carpeta python/):
    python splay_tree_basico.py
"""

from controller.basic_controller import BasicController
from model.splay_tree import SplayTree
from view.tree_view import TreeView


def main() -> None:
    model = SplayTree()
    view = TreeView()
    controller = BasicController(model, view)
    controller.run_demo()


if __name__ == "__main__":
    main()
