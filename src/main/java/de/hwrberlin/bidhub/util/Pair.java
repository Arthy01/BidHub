package de.hwrberlin.bidhub.util;

/**
 * Repräsentiert ein generisches Paar von zwei Werten.
 *
 * @param <K> Der Typ des Schlüssels in diesem Paar.
 * @param <V> Der Typ des Wertes in diesem Paar.
 */
public class Pair<K, V> {

    private K key;

    private V value;

    /**
     * Erstellt ein neues Paar mit dem angegebenen Schlüssel und Wert.
     *
     * @param key Der Schlüssel des Paares.
     * @param value Der Wert des Paares.
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Gibt den Schlüssel dieses Paares zurück.
     *
     * @return Der Schlüssel des Paares.
     */
    public K getKey() { return key; }

    /**
     * Gibt den Wert dieses Paares zurück.
     *
     * @return Der Wert des Paares.
     */
    public V getValue() { return value; }

    /**
     * Setzt den Schlüssel dieses Paares.
     *
     * @param key Der neue Schlüssel des Paares.
     */
    public void setKey(K key) { this.key = key; }

    /**
     * Setzt den Wert dieses Paares.
     *
     * @param value Der neue Wert des Paares.
     */
    public void setValue(V value) { this.value = value; }
}
