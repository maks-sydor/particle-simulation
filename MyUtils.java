package com.example.particlesimulation;

import java.util.Arrays;

/**
 * A class that just has some useful functions, such as:
 *  - random(min, max), that generates a random value (int or double)
 *  - rnd(min, max) - the same, just with a shorter name
 *  - removeElement(T[] elements, index) - returns a list with the element removed
 */
public final class MyUtils
{
    public static double random(double min, double max)
    {
        return min + Math.random() * (max - min);
    }
    public static double rnd(double min, double max)
    {
        return random(min, max);
    }
    public static int rnd(int min, int max)
    {
        return (int) random(min, max);
    }

    public static <T> T[] removeElement(T[] elements, int index) {
        if (index < 0 || index >= elements.length) {
            throw new IllegalArgumentException("Index " + index + " is outside the valid range (0-" + (elements.length - 1) + ").");
        }

        T[] newElements = Arrays.copyOf(elements, elements.length - 1);
        System.arraycopy(elements, 0, newElements, 0, index);
        System.arraycopy(elements, index + 1, newElements, index, elements.length - index - 1);
        return newElements;
    }
}
