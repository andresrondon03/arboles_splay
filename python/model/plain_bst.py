"""
MODELO: BST convencional, SIN balanceo ni reorganización.
Sirve como punto de comparación contra el Árbol Splay en el benchmark.
"""

from typing import Any, Optional

from model.search_tree import SearchTree


class _BstNode:
    __slots__ = ("key", "value", "left", "right")

    def __init__(self, key: Any, value: Any) -> None:
        self.key = key
        self.value = value
        self.left: Optional["_BstNode"] = None
        self.right: Optional["_BstNode"] = None


class PlainBst(SearchTree):

    def __init__(self) -> None:
        self._root: Optional[_BstNode] = None
        self._comparisons = 0

    def insert(self, key: Any, value: Any = None) -> bool:
        if self._root is None:
            self._root = _BstNode(key, value)
            return True
        current = self._root
        while True:
            if key < current.key:
                if current.left is None:
                    current.left = _BstNode(key, value)
                    return True
                current = current.left
            elif key > current.key:
                if current.right is None:
                    current.right = _BstNode(key, value)
                    return True
                current = current.right
            else:
                current.value = value
                return False

    def search(self, key: Any) -> bool:
        current = self._root
        while current is not None:
            self._comparisons += 1
            if key < current.key:
                current = current.left
            elif key > current.key:
                current = current.right
            else:
                return True
        return False

    def comparisons(self) -> int:
        return self._comparisons

    def reset_comparisons(self) -> None:
        self._comparisons = 0
