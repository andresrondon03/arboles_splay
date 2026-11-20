"""
EJEMPLO 2 - Aplicado: el Árbol Splay en uso real.
 - Caché LRU: las entradas más consultadas quedan en la raíz.
 - Benchmark: Splay vs BST convencional con distintos patrones de acceso.

Ejecución (desde la carpeta python/):
    python splay_tree_aplicado.py
"""

from controller.applied_controller import AppliedController
from view.applied_view import AppliedView


def main() -> None:
    view = AppliedView()
    controller = AppliedController(view)
    controller.run_demo()


if __name__ == "__main__":
    main()
