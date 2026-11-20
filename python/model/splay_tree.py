"""
MODELO: Árbol Splay (bottom-up, iterativo, con puntero al padre).

Este es el ÚNICO árbol del proyecto: lo usan el ejemplo básico, la caché y el benchmark.
No imprime nada. Si se activa la traza (enable_trace), registra qué casos de splay
(Zig, Zig-Zig, Zig-Zag) se aplicaron en la última operación, para que la vista los muestre.
"""

from typing import Any, List, Optional, Tuple

from model.rotation_type import RotationType
from model.search_tree import SearchTree


class Node:
    """Nodo del árbol. La vista solo LEE key/value/left/right; nunca los modifica."""

    __slots__ = ("key", "value", "left", "right", "parent")

    def __init__(self, key: Any, value: Any = None) -> None:
        self.key = key
        self.value = value
        self.left: Optional["Node"] = None
        self.right: Optional["Node"] = None
        self.parent: Optional["Node"] = None


class SplayTree(SearchTree):
    """Árbol Splay genérico: las claves deben ser comparables entre sí."""

    def __init__(self) -> None:
        self._root: Optional[Node] = None
        self._size = 0
        self._comparisons = 0
        self._tracing = False
        self._last_rotations: List[RotationType] = []

    # ------------------------------------------------------------------
    # Consultas de estado (solo datos)
    # ------------------------------------------------------------------

    def size(self) -> int:
        return self._size

    def is_empty(self) -> bool:
        return self._root is None

    def root_key(self) -> Optional[Any]:
        """Clave de la raíz, o None si el árbol está vacío."""
        return None if self._root is None else self._root.key

    def get_root(self) -> Optional[Node]:
        """Raíz para lectura (la vista la usa para dibujar el árbol)."""
        return self._root

    def height(self) -> int:
        """Altura (número de nodos del camino más largo). Pensada para demos pequeñas."""
        return self._height(self._root)

    def _height(self, node: Optional[Node]) -> int:
        if node is None:
            return 0
        return 1 + max(self._height(node.left), self._height(node.right))

    def comparisons(self) -> int:
        return self._comparisons

    def reset_comparisons(self) -> None:
        self._comparisons = 0

    # ------------------------------------------------------------------
    # Traza opcional de los casos de splay
    # ------------------------------------------------------------------

    def enable_trace(self, enabled: bool) -> None:
        """Activa o desactiva el registro de rotaciones (desactivado por defecto: no cuesta nada)."""
        self._tracing = enabled

    def get_last_rotations(self) -> List[RotationType]:
        """Casos de splay aplicados durante la última operación (insert, search, get o delete)."""
        return list(self._last_rotations)

    def _start_operation(self) -> None:
        if self._tracing:
            self._last_rotations.clear()

    def _record(self, rotation: RotationType) -> None:
        if self._tracing:
            self._last_rotations.append(rotation)

    # ------------------------------------------------------------------
    # Rotaciones
    # ------------------------------------------------------------------

    def _rotate_right(self, x: Node) -> None:
        """Rotación a la derecha: x es hijo IZQUIERDO de su padre p y sube sobre él."""
        p = x.parent
        g = p.parent

        p.left = x.right               # el subárbol derecho de x pasa a ser hijo izquierdo de p
        if x.right is not None:
            x.right.parent = p
        x.right = p                    # p baja y se vuelve hijo derecho de x
        p.parent = x

        x.parent = g                   # x ocupa el lugar que tenía p
        if g is None:
            self._root = x
        elif g.left is p:
            g.left = x
        else:
            g.right = x

    def _rotate_left(self, x: Node) -> None:
        """Rotación a la izquierda: x es hijo DERECHO de su padre p y sube sobre él."""
        p = x.parent
        g = p.parent

        p.right = x.left               # el subárbol izquierdo de x pasa a ser hijo derecho de p
        if x.left is not None:
            x.left.parent = p
        x.left = p                     # p baja y se vuelve hijo izquierdo de x
        p.parent = x

        x.parent = g                   # x ocupa el lugar que tenía p
        if g is None:
            self._root = x
        elif g.left is p:
            g.left = x
        else:
            g.right = x

    # ------------------------------------------------------------------
    # Splay: lleva el nodo x hasta la raíz
    # ------------------------------------------------------------------

    def _splay(self, x: Node) -> None:
        while x.parent is not None:
            p = x.parent
            g = p.parent

            if g is None:
                # Caso ZIG: el padre es la raíz -> una sola rotación
                self._record(RotationType.ZIG)
                if x is p.left:
                    self._rotate_right(x)
                else:
                    self._rotate_left(x)
            elif x is p.left and p is g.left:
                # Caso ZIG-ZIG (izquierda-izquierda): primero el padre, luego x
                self._record(RotationType.ZIG_ZIG)
                self._rotate_right(p)
                self._rotate_right(x)
            elif x is p.right and p is g.right:
                # Caso ZIG-ZIG (derecha-derecha)
                self._record(RotationType.ZIG_ZIG)
                self._rotate_left(p)
                self._rotate_left(x)
            elif x is p.right and p is g.left:
                # Caso ZIG-ZAG (izquierda-derecha): x rota dos veces
                self._record(RotationType.ZIG_ZAG)
                self._rotate_left(x)
                self._rotate_right(x)
            else:
                # Caso ZIG-ZAG (derecha-izquierda)
                self._record(RotationType.ZIG_ZAG)
                self._rotate_right(x)
                self._rotate_left(x)

    # ------------------------------------------------------------------
    # Operaciones
    # ------------------------------------------------------------------

    def insert(self, key: Any, value: Any = None) -> bool:
        """
        Inserta una clave como en un BST y luego hace splay del nodo nuevo.
        Si la clave ya existe, se actualiza su valor y se hace splay del nodo existente.

        Devuelve True si se insertó, False si ya existía.
        """
        self._start_operation()
        if self._root is None:
            self._root = Node(key, value)
            self._size += 1
            return True
        current = self._root
        parent = None
        while current is not None:
            parent = current
            if key < current.key:
                current = current.left
            elif key > current.key:
                current = current.right
            else:
                current.value = value        # duplicado: se actualiza y se acerca a la raíz
                self._splay(current)
                return False
        node = Node(key, value)
        node.parent = parent
        if key < parent.key:
            parent.left = node
        else:
            parent.right = node
        self._size += 1
        self._splay(node)
        return True

    def search(self, key: Any) -> bool:
        """Busca una clave: si existe sube a la raíz; si no, sube el último nodo visitado."""
        self._start_operation()
        return self._access(key) is not None

    def get(self, key: Any) -> Optional[Any]:
        """Como search(), pero devuelve el valor asociado (o None si la clave no existe)."""
        self._start_operation()
        node = self._access(key)
        return None if node is None else node.value

    def _access(self, key: Any) -> Optional[Node]:
        """Descenso tipo BST + splay. Devuelve el nodo encontrado o None."""
        current = self._root
        last = None
        while current is not None:
            last = current
            self._comparisons += 1
            if key < current.key:
                current = current.left
            elif key > current.key:
                current = current.right
            else:
                self._splay(current)
                return current
        if last is not None:
            self._splay(last)                # fallo: igual se amortiza el camino recorrido
        return None

    def delete(self, key: Any) -> bool:
        """
        Elimina una clave usando split/join:
         1. _access(key): si existe, queda en la raíz.
         2. SPLIT: se separa el subárbol izquierdo (L) y el derecho (R).
         3. JOIN: se sube el máximo de L a la raíz de L (queda sin hijo derecho)
                  y se cuelga R como su hijo derecho.

        Devuelve True si la clave existía y fue eliminada.
        """
        self._start_operation()
        if self._access(key) is None:
            return False
        left = self._root.left               # --- SPLIT ---
        right = self._root.right
        if left is not None:
            left.parent = None
        if right is not None:
            right.parent = None
        self._root.left = None
        self._root.right = None

        if left is None:                     # --- JOIN ---
            self._root = right               # no había nada a la izquierda
        else:
            self._root = left                # trabajamos temporalmente con L como árbol propio
            maximum = left
            while maximum.right is not None:
                maximum = maximum.right
            self._splay(maximum)             # el máximo de L sube y no tiene hijo derecho
            self._root.right = right
            if right is not None:
                right.parent = self._root
        self._size -= 1
        return True

    # ------------------------------------------------------------------
    # Recorridos (iterativos: seguros incluso con árboles degenerados)
    # ------------------------------------------------------------------

    def inorder(self) -> List[Any]:
        """Inorder (izquierda, raíz, derecha): las claves en orden ascendente."""
        return [key for key, _ in self.entries()]

    def preorder(self) -> List[Any]:
        """Preorder (raíz, izquierda, derecha): refleja la forma actual del árbol."""
        result: List[Any] = []
        stack = [self._root] if self._root is not None else []
        while stack:
            node = stack.pop()
            result.append(node.key)
            if node.right is not None:
                stack.append(node.right)
            if node.left is not None:
                stack.append(node.left)
        return result

    def entries(self) -> List[Tuple[Any, Any]]:
        """Pares (clave, valor) en orden ascendente."""
        result: List[Tuple[Any, Any]] = []
        stack: List[Node] = []
        current = self._root
        while current is not None or stack:
            while current is not None:
                stack.append(current)
                current = current.left
            current = stack.pop()
            result.append((current.key, current.value))
            current = current.right
        return result
