"""
CONTROLADOR del ejemplo básico: ejecuta las operaciones sobre el modelo y
pide a la vista que muestre el resultado y los casos de splay aplicados.
"""

from model.splay_tree import SplayTree
from view.tree_view import TreeView


class BasicController:

    def __init__(self, model: SplayTree, view: TreeView) -> None:
        self._model = model
        self._view = view
        self._model.enable_trace(True)       # queremos ver qué casos de splay ocurren

    def insert(self, key: int) -> None:
        added = self._model.insert(key, f"valor-{key}")
        self._view.show_operation(f"insert({key})", "insertada" if added else "ya existía",
                                  self._model.get_last_rotations())

    def search(self, key: int) -> None:
        found = self._model.search(key)
        self._view.show_operation(f"search({key})", "encontrada" if found else "no existe",
                                  self._model.get_last_rotations())

    def delete(self, key: int) -> None:
        deleted = self._model.delete(key)
        self._view.show_operation(f"delete({key})", "eliminada" if deleted else "no existe",
                                  self._model.get_last_rotations())

    def _show_tree(self) -> None:
        self._view.show_tree(self._model.get_root())

    def run_demo(self) -> None:
        """Escenario de demostración, pensado para ver los tres casos: Zig, Zig-Zig y Zig-Zag."""
        view = self._view
        view.show_section("1) Inserciones ascendentes: cada clave nueva es hija de la raíz (ZIG)")
        for key in (10, 20, 30, 40, 50):
            self.insert(key)
        self._show_tree()

        view.show_blank_line()
        view.show_section("2) Buscar la clave más profunda (10): ZIG-ZIG acorta el camino")
        self.search(10)
        self._show_tree()

        view.show_blank_line()
        view.show_section("3) Insertar 25 (camino en zigzag): ZIG-ZAG")
        self.insert(25)
        self._show_tree()

        view.show_blank_line()
        view.show_section("4) Búsqueda fallida (35): sube el último nodo visitado")
        self.search(35)
        self._show_tree()

        view.show_blank_line()
        view.show_section("5) Eliminación con split/join")
        self.delete(20)
        self.delete(99)
        self._show_tree()

        view.show_blank_line()
        view.show_section("6) Recorridos y estado final")
        view.show_inorder(self._model.inorder())
        view.show_preorder(self._model.preorder())
        view.show_summary(self._model.size(), self._model.height(), self._model.root_key())
