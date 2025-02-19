package net.botwithus.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Random {

    public static int nextInt() {
        return nextInt(0, Integer.MAX_VALUE);
    }

    public static int nextInt(int max) {
        return nextInt(0, max);
    }

    public static int nextInt(int min, int max) {
        return min == max ? max : min + getSecureThreadLocalRandom().nextInt(max - min);
    }

    public static double nextDouble(double min, double max) {
        return min + getSecureThreadLocalRandom().nextDouble() * (max - min);
    }

    public static boolean nextBoolean() {
        return getSecureThreadLocalRandom().nextBoolean();
    }

    public static <E> E nextElement(Collection<E> collection) {
        if (!collection.isEmpty()) {
            if (!(collection instanceof List<E> list)) {
                return new ArrayList<>(collection).get(nextInt(0, collection.size()));
            }
            return list.get(nextInt(0, collection.size()));
        }
        return null;
    }

    public static <E> E nextElement(E[] array) {
        return array.length == 0 ? null : array[nextInt(0, array.length)];
    }

    public static ThreadLocalRandom getSecureThreadLocalRandom() {
        return ThreadLocalRandom.current();
    }

}
