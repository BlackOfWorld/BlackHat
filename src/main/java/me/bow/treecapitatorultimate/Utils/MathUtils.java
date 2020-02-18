package me.bow.treecapitatorultimate.Utils;

import java.util.Random;

public class MathUtils {
    public static final Random rnd = new Random();

    public static int generateNumber(int max) {
        return rnd.nextInt(max + 1);
    }

    public static int generateNumber(int min, int max) {
        return rnd.nextInt((max - min) + 1) + min;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = rnd.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
}
