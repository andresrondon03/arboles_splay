"""
VISTA del ejemplo básico: salida por consola con la traza de rotaciones y el dibujo ASCII.
No modifica el árbol: solo recibe datos y LEE los nodos para dibujarlos.
"""

from typing import List, Optional

from model.rotation_type import RotationType
from model.splay_tree import Node


class TreeView:

    def show_section(self, title: str) -> None:
        print(f"=== {title} ===")

    def show_blank_line(self) -> None:
        print()

    def show_operation(self, call: str, result: str, rotations: List[RotationType]) -> None:
        """Muestra una operación, su resultado y los casos de splay que se aplicaron."""
        steps = ", ".join(r.label() for r in rotations) if rotations else "ninguna"
        print(f"{call:<12} -> {result:<11} | rotaciones: {steps}")

    def show_inorder(self, keys: List[int]) -> None:
        print(f"Inorder  : {keys}   (siempre ordenado)")

    def show_preorder(self, keys: List[int]) -> None:
        print(f"Preorder : {keys}   (refleja la forma actual)")

    def show_summary(self, size: int, height: int, root_key: Optional[int]) -> None:
        print(f"Tamaño = {size} | altura = {height} | raíz = {root_key}")

    def show_tree(self, root: Optional[Node]) -> None:
        """
        Dibuja el árbol girado 90°: la raíz queda a la izquierda,
        los hijos derechos arriba (/) y los izquierdos abajo (\\).
        """
        if root is None:
            print("(árbol vacío)")
            return
        self._draw(root, 0, " ")

    def _draw(self, node: Optional[Node], depth: int, branch: str) -> None:
        if node is None:
            return
        self._draw(node.right, depth + 1, "/")
        prefix = "" if depth == 0 else "    " * (depth - 1) + "    " + branch + "-- "
        print(prefix + str(node.key))
        self._draw(node.left, depth + 1, "\\")
