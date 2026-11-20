package model;

/**
 * Tipos de paso que puede ejecutar la rutina splay.
 * El modelo los REGISTRA (no imprime nada); la vista decide cómo mostrarlos.
 */
public enum RotationType {

    /** El padre es la raíz: una sola rotación simple. */
    ZIG("ZIG"),

    /** x y su padre son hijos del mismo lado: primero rota el padre, luego x. */
    ZIG_ZIG("ZIG-ZIG"),

    /** x y su padre son hijos de lados opuestos: x rota dos veces. */
    ZIG_ZAG("ZIG-ZAG");

    private final String label;

    RotationType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
