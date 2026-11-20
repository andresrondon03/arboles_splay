package model;

/**
 * MODELO (datos de apoyo): generador pseudoaleatorio congruencial lineal.
 * Es idéntico en Java y Python, así que ambos lenguajes generan exactamente las mismas secuencias.
 */
public class Lcg {

    private long seed;

    public Lcg(long seed) {
        this.seed = seed;
    }

    /** Entero pseudoaleatorio en [0, bound). */
    public int nextInt(int bound) {
        seed = (seed * 1103515245L + 12345L) & 0x7FFFFFFFL;
        return (int) ((seed >> 8) % bound);
    }

    /** Permutación de 0..n-1 (Fisher-Yates). */
    public int[] shuffledKeys(int n) {
        int[] keys = new int[n];
        for (int i = 0; i < n; i++) {
            keys[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            int j = nextInt(i + 1);
            int tmp = keys[i];
            keys[i] = keys[j];
            keys[j] = tmp;
        }
        return keys;
    }
}
