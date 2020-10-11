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

    public static float generateNumber(float max) {
        return rnd.nextFloat() * max;
    }

    public static float generateNumber(float min, float max) {
        return rnd.nextFloat() * (max - min) + min;
    }

    public static double generateNumber(double max) {
        return rnd.nextDouble() * max;
    }

    public static double generateNumber(double min, double max) {
        return rnd.nextDouble() * (max - min) + min;
    }

    public static double clamp(final double min, final double max, final double val) {
        return Math.min(max, Math.max(min, val));
    }

    public static float clamp(final float min, final float max, final float val) {
        return Math.min(max, Math.max(min, val));
    }

    public static int clamp(final int min, final int max, final int val) {
        return Math.min(max, Math.max(min, val));
    }

    public static short clamp(final short min, final short max, final short val) {
        return (short) Math.min(max, Math.max(min, val));
    }

    public static long clamp(final long min, final long max, final long val) {
        return Math.min(max, Math.max(min, val));
    }

    public static byte clamp(final byte min, final byte max, final byte val) {
        return (byte) Math.min(max, Math.max(min, val));
    }

    public static int toXY(final int width, final int x, final int y) {
        return y * width + x;
    }

    public static int toX(final int width, final int xy) {
        return xy % width;
    }

    public static int toY(final int width, final int xy) {
        return (int) Math.floor(xy / width);
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = rnd.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
    public static double map(double sourceNumber, double fromA, double fromB, double toA, double toB, int decimalPrecision) {
        double deltaA = fromB - fromA;
        double deltaB = toB - toA;
        double scale  = deltaB / deltaA;
        double negA   = -1 * fromA;
        double offset = (negA * scale) + toA;
        double finalNumber = (sourceNumber * scale) + offset;
        int calcScale = (int) Math.pow(10, decimalPrecision);
        return (double) Math.round(finalNumber * calcScale) / calcScale;
    }
}
