package fr.lataverne.votereward.utils.collections;

import java.security.SecureRandom;
import java.util.NavigableMap;
import java.util.TreeMap;

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map;

    @SuppressWarnings ("FieldNotUsedInToString")
    private final SecureRandom random;

    private double total = 0;

    public RandomCollection() {
        this.random = new SecureRandom();
        this.map = new TreeMap<>();
    }

    public void add(double weight, E result) {
        if (weight <= 0) {
            return;
        }

        this.total += weight;
        this.map.put(this.total, result);
    }

    public E next() {
        double value = this.random.nextDouble() * this.total;
        return this.map.higherEntry(value).getValue();
    }

    @Override
    public String toString() {
        return "RandomCollection{" +
                "map=" + this.map +
                ", total=" + this.total +
                "}";
    }
}
