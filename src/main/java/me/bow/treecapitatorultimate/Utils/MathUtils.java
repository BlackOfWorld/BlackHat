package me.bow.treecapitatorultimate.Utils;

import java.util.Random;

public class MathUtils {
    private static final Random rnd = new Random();

    public static int generateNumber(int max) {
        return rnd.nextInt(max + 1);
    }

    public static int generateNumber(int min, int max) {
        return rnd.nextInt((max - min) + 1) + min;
    }
}