/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.common.util;

import illarion.common.types.Range;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * This is the Java implementation of the FastMath class. This should only be
 * used in case the native implementation is not available or results in
 * problems.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("NonReproducibleMathCall")
public final class FastMath {
    /**
     * A "close to zero" double epsilon value for use.
     */
    public static final double DBL_EPSILON = 2.220446049250313E-16d;

    /**
     * A value to multiply a degree value by, to convert it to radians.
     */
    public static final float DEG_TO_RAD = (float) Math.PI / 180.0f;

    /**
     * The Euler value that is the base of the natural logarithms.
     */
    public static final float E = (float) Math.E;

    /**
     * A "close to zero" float epsilon value for use. Also this is the delta of
     * the result floating point result that is meet for sure.
     */
    public static final float FLT_EPSILON = 1.1920928955078125E-5f;

    /**
     * The value PI/2 as a float. (90 degrees)
     */
    public static final float HALF_PI = 0.5f * (float) Math.PI;

    /**
     * The value 1/PI as a float.
     */
    public static final float INV_PI = 1.0f / (float) Math.PI;

    /**
     * The value 1/(2PI) as a float.
     */
    public static final float INV_TWO_PI = 1.0f / (2.0f * (float) Math.PI);

    /**
     * The float value of one third for use.
     */
    public static final float ONE_THIRD = 1f / 3f;

    /**
     * The value PI as a float. (180 degrees)
     */
    public static final float PI = (float) Math.PI;

    /**
     * The value PI/4 as a float. (45 degrees)
     */
    public static final float QUARTER_PI = 0.25f * (float) Math.PI;

    /**
     * A value to multiply a radian value by, to convert it to degrees.
     */
    public static final float RAD_TO_DEG = 180.0f / (float) Math.PI;

    /**
     * The value 2PI as a float. (360 degrees)
     */
    public static final float TWO_PI = 2.0f * (float) Math.PI;

    /**
     * The used random value generator.
     */
    @NotNull
    private static final Random RANDOM = new Random();

    /**
     * Lookup table for the fast square root table function.
     */
    @NotNull
    private static final int[] SQRT_TABLE = {0, 16, 22, 27, 32, 35, 39, 42, 45, 48, 50, 53, 55, 57, 59, 61, 64, 65, 67,
                                             69, 71, 73, 75, 76, 78, 80, 81, 83, 84, 86, 87, 89, 90, 91, 93, 94, 96, 97,
                                             98, 99, 101, 102, 103, 104, 106, 107, 108, 109, 110, 112, 113, 114, 115,
                                             116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 128, 128, 129, 130,
                                             131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 144,
                                             145, 146, 147, 148, 149, 150, 150, 151, 152, 153, 154, 155, 155, 156, 157,
                                             158, 159, 160, 160, 161, 162, 163, 163, 164, 165, 166, 167, 167, 168, 169,
                                             170, 170, 171, 172, 173, 173, 174, 175, 176, 176, 177, 178, 178, 179, 180,
                                             181, 181, 182, 183, 183, 184, 185, 185, 186, 187, 187, 188, 189, 189, 190,
                                             191, 192, 192, 193, 193, 194, 195, 195, 196, 197, 197, 198, 199, 199, 200,
                                             201, 201, 202, 203, 203, 204, 204, 205, 206, 206, 207, 208, 208, 209, 209,
                                             210, 211, 211, 212, 212, 213, 214, 214, 215, 215, 216, 217, 217, 218, 218,
                                             219, 219, 220, 221, 221, 222, 222, 223, 224, 224, 225, 225, 226, 226, 227,
                                             227, 228, 229, 229, 230, 230, 231, 231, 232, 232, 233, 234, 234, 235, 235,
                                             236, 236, 237, 237, 238, 238, 239, 240, 240, 241, 241, 242, 242, 243, 243,
                                             244, 244, 245, 245, 246, 246, 247, 247, 248, 248, 249, 249, 250, 250, 251,
                                             251, 252, 252, 253, 253, 254, 254, 255};

    /**
     * Private constructor to ensure that no instance is created.
     */
    private FastMath() {
        // nothing to do
    }

    /**
     * Returns absolute value of a value. This means a negative value gets
     * negated (so it becomes positive) and a positive value remains the same.
     * This version works on byte values. <br>
     * Special case: In case the argument is set to Byte.MIN_VALUE the result
     * will be Byte.MIN_VALUE as well.
     *
     * @param value the value to work with
     * @return the absolute value of the value supplied as parameter
     * @see #abs(double)
     * @see #abs(float)
     * @see #abs(int)
     * @see #abs(long)
     * @see #abs(short)
     */
    @Contract(pure = true)
    public static byte abs(byte value) {
        return (byte) ((value ^ (value >> 7)) - (value >> 7));
    }

    /**
     * Returns absolute value of a value. This means a negative value gets
     * negated (so it becomes positive) and a positive value remains the same.
     * This version works on double values.
     *
     * @param value the value to work with
     * @return the absolute value of the value supplied as parameter
     * @see #abs(byte)
     * @see #abs(float)
     * @see #abs(int)
     * @see #abs(long)
     * @see #abs(short)
     */
    @Contract(pure = true)
    public static double abs(double value) {
        if (value < 0.d) {
            return -value;
        }
        return value;
    }

    /**
     * Returns absolute value of a value. This means a negative value gets
     * negated (so it becomes positive) and a positive value remains the same.
     * This version works on float values.
     *
     * @param value the value to work with
     * @return the absolute value of the value supplied as parameter
     * @see #abs(byte)
     * @see #abs(double)
     * @see #abs(int)
     * @see #abs(long)
     * @see #abs(short)
     */
    @Contract(pure = true)
    public static float abs(float value) {
        if (value < 0.f) {
            return -value;
        }
        return value;
    }

    /**
     * Returns absolute value of a value. This means a negative value gets
     * negated (so it becomes positive) and a positive value remains the same.
     * This version works on integer values. <br>
     * Special case: In case the argument is set to Integer.MIN_VALUE the result
     * will be Integer.MIN_VALUE as well.
     *
     * @param value the value to work with
     * @return the absolute value of the value supplied as parameter
     * @see #abs(byte)
     * @see #abs(double)
     * @see #abs(float)
     * @see #abs(long)
     * @see #abs(short)
     */
    @Contract(pure = true)
    public static int abs(int value) {
        return (value ^ (value >> 31)) - (value >> 31);
    }

    /**
     * Returns absolute value of a value. This means a negative value gets
     * negated (so it becomes positive) and a positive value remains the same.
     * This version works on long values. <br>
     * Special case: In case the argument is set to Long.MIN_VALUE the result
     * will be Long.MIN_VALUE as well.
     *
     * @param value the value to work with
     * @return the absolute value of the value supplied as parameter
     * @see #abs(byte)
     * @see #abs(double)
     * @see #abs(float)
     * @see #abs(int)
     * @see #abs(short)
     */
    @Contract(pure = true)
    public static long abs(long value) {
        return (value ^ (value >> 63L)) - (value >> 63L);
    }

    /**
     * Returns absolute value of a value. This means a negative value gets
     * negated (so it becomes positive) and a positive value remains the same.
     * This version works on short values.
     *
     * @param value the value to work with
     * @return the absolute value of the value supplied as parameter
     * @see #abs(byte)
     * @see #abs(double)
     * @see #abs(float)
     * @see #abs(int)
     * @see #abs(long)
     */
    @Contract(pure = true)
    public static short abs(short value) {
        return (short) ((value ^ (value >> 15)) - (value >> 15));
    }

    /**
     * Returns the arc cosine of a value; the returned angle is in the range 0.0
     * through pi. This function results in a error message all and is only
     * implemented to ensure that no one calls that function for a false data
     * type by mistake.
     *
     * @param value the value whose arc cosine is to be returned
     * @return the arc cosine of the argument
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> fail", pure = true)
    public static byte acos(byte value) {
        throw new OutOfCoffeeException("A arc cosine from a byte value? No point in that.");
    }

    /**
     * Returns the arc cosine of a value; the returned angle is in the range 0.0
     * through pi.
     * <ul>
     * <li>If the argument is NaN or its absolute value is greater than 1, then
     * the result is NaN.</li>
     * </ul>
     *
     * @param value the value whose arc cosine is to be returned
     * @return the arc cosine of the argument
     */
    @Contract(pure = true)
    public static double acos(double value) {
        if (Double.isNaN(value)) {
            return Double.NaN;
        }

        if (-1.d < value) {
            if (value < 1.d) {
                return Math.acos(value);
            }
            return Double.NaN;
        }
        return Double.NaN;
    }

    /**
     * Returns the arc cosine of a value; the returned angle is in the range 0.0
     * through pi.
     * <ul>
     * <li>If the argument is NaN or its absolute value is greater than 1, then
     * the result is NaN.</li>
     * </ul>
     *
     * @param value the value whose arc cosine is to be returned
     * @return the arc cosine of the argument
     */
    @Contract(pure = true)
    public static float acos(float value) {
        if (Float.isNaN(value)) {
            return Float.NaN;
        }

        if (-1.f < value) {
            if (value < 1.f) {
                return (float) Math.acos(value);
            }
            return Float.NaN;
        }
        return Float.NaN;
    }

    /**
     * Returns the arc cosine of a value; the returned angle is in the range 0.0
     * through pi. This function results in a error message all and is only
     * implemented to ensure that no one calls that function for a false data
     * type by mistake.
     *
     * @param value the value whose arc cosine is to be returned
     * @return the arc cosine of the argument
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> fail", pure = true)
    public static int acos(int value) {
        throw new OutOfCoffeeException("A arc cosine from a integer value? No point in that.");
    }

    /**
     * Returns the arc cosine of a value; the returned angle is in the range 0.0
     * through pi. This function results in a error message all and is only
     * implemented to ensure that no one calls that function for a false data
     * type by mistake.
     *
     * @param value the value whose arc cosine is to be returned
     * @return the arc cosine of the argument
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> fail", pure = true)
    public static long acos(long value) {
        throw new OutOfCoffeeException("A arc cosine from a long value? No point in that.");
    }

    /**
     * Returns the arc cosine of a value; the returned angle is in the range 0.0
     * through pi. This function results in a error message all and is only
     * implemented to ensure that no one calls that function for a false data
     * type by mistake.
     *
     * @param value the value whose arc cosine is to be returned
     * @return the arc cosine of the argument
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> fail", pure = true)
    public static short acos(short value) {
        throw new OutOfCoffeeException("A arc cosine from a short value? No point in that.");
    }

    /**
     * Returns the arc sine of a value; the returned angle is in the range -pi/2
     * through pi/2. This function results in a error message all and is only
     * implemented to ensure that no one calls that function for a false data
     * type by mistake.
     *
     * @param value the value whose arc sine is to be returned
     * @return the arc sine of the argument
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> fail", pure = true)
    public static byte asin(byte value) {
        throw new OutOfCoffeeException("A arc sine from a byte value? No point in that.");
    }

    /**
     * Returns the arc sine of a value; the returned angle is in the range -pi/2
     * through pi/2. Special cases:
     * <ul>
     * <li>If the argument is NaN or its absolute value is greater than 1, then
     * the result is NaN.</li>
     * <li>If the argument is zero, then the result is a zero with the same sign
     * as the argument.</li>
     * </ul>
     *
     * @param value the value whose arc sine is to be returned
     * @return the arc sine of the argument
     */
    @Contract(pure = true)
    public static double asin(double value) {
        if (!isNumber(value)) {
            return Double.NaN;
        }
        if (-1.0f < value) {
            if (value < 1.0f) {
                return Math.asin(value);
            }

            return Double.NaN;
        }

        return Double.NaN;
    }

    /**
     * Returns the arc sine of a value; the returned angle is in the range -pi/2
     * through pi/2. Special cases:
     * <ul>
     * <li>If the argument is NaN or its absolute value is greater than 1, then
     * the result is NaN.</li>
     * <li>If the argument is zero, then the result is a zero with the same sign
     * as the argument.</li>
     * </ul>
     *
     * @param value the value whose arc sine is to be returned
     * @return the arc sine of the argument
     */
    @Contract(pure = true)
    public static float asin(float value) {
        if (!isNumber(value)) {
            return Float.NaN;
        }
        if (-1.0f < value) {
            if (value < 1.0f) {
                return (float) Math.asin(value);
            }

            return Float.NaN;
        }

        return Float.NaN;
    }

    /**
     * Returns the arc sine of a value; the returned angle is in the range -pi/2
     * through pi/2. This function results in a error message all and is only
     * implemented to ensure that no one calls that function for a false data
     * type by mistake.
     *
     * @param value the value whose arc sine is to be returned
     * @return the arc sine of the argument
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> fail", pure = true)
    public static int asin(int value) {
        throw new OutOfCoffeeException("A arc sine from a integer value? No point in that.");
    }

    /**
     * Returns the arc sine of a value; the returned angle is in the range -pi/2
     * through pi/2. This function results in a error message all and is only
     * implemented to ensure that no one calls that function for a false data
     * type by mistake.
     *
     * @param value the value whose arc sine is to be returned
     * @return the arc sine of the argument
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> fail", pure = true)
    public static long asin(long value) {
        throw new OutOfCoffeeException("A arc sine from a long value? No point in that.");
    }

    /**
     * Returns the arc sine of a value; the returned angle is in the range -pi/2
     * through pi/2. This function results in a error message all and is only
     * implemented to ensure that no one calls that function for a false data
     * type by mistake.
     *
     * @param value the value whose arc sine is to be returned
     * @return the arc sine of the argument
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> fail", pure = true)
    public static short asin(short value) {
        throw new OutOfCoffeeException("A arc sine from a short value? No point in that.");
    }

    /**
     * Returns the arc tangent of a value; the returned angle is in the range
     * -pi/2 through pi/2. This version of the function works using byte values.
     *
     * @param value the value whose arc tangent is to be returned
     * @return the arc tangent of the argument
     */
    @Contract(pure = true)
    public static float atan(byte value) {
        return (float) Math.atan(value);
    }

    /**
     * Returns the arc tangent of a value; the returned angle is in the range
     * -pi/2 through pi/2. This version of the function works using double
     * values. Special cases:
     * <ul>
     * <li>If the argument is NaN, then the result is NaN.</li>
     * <li>If the argument is zero, then the result is a zero with the same sign
     * as the argument.</li>
     * </ul>
     *
     * @param value the value whose arc tangent is to be returned
     * @return the arc tangent of the argument
     */
    @Contract(pure = true)
    public static double atan(double value) {
        return Math.atan(value);
    }

    /**
     * Returns the arc tangent of a value; the returned angle is in the range
     * -pi/2 through pi/2. This version of the function works using float
     * values. Special cases:
     * <ul>
     * <li>If the argument is NaN, then the result is NaN.</li>
     * <li>If the argument is zero, then the result is a zero with the same sign
     * as the argument.</li>
     * </ul>
     *
     * @param value the value whose arc tangent is to be returned
     * @return the arc tangent of the argument
     */
    @Contract(pure = true)
    public static float atan(float value) {
        return (float) Math.atan(value);
    }

    /**
     * Returns the arc tangent of a value; the returned angle is in the range
     * -pi/2 through pi/2. This version of the function works using integer
     * values.
     *
     * @param value the value whose arc tangent is to be returned
     * @return the arc tangent of the argument
     */
    @Contract(pure = true)
    public static float atan(int value) {
        return (float) Math.atan(value);
    }

    /**
     * Returns the arc tangent of a value; the returned angle is in the range
     * -pi/2 through pi/2. This version of the function works using long values.
     *
     * @param value the value whose arc tangent is to be returned
     * @return the arc tangent of the argument
     */
    @Contract(pure = true)
    public static double atan(long value) {
        return Math.atan(value);
    }

    /**
     * Returns the arc tangent of a value; the returned angle is in the range
     * -pi/2 through pi/2. This version of the function works using short
     * values.
     *
     * @param value the value whose arc tangent is to be returned
     * @return the arc tangent of the argument
     */
    @Contract(pure = true)
    public static float atan(short value) {
        return (float) Math.atan(value);
    }

    /**
     * Round a byte value to the next larger byte value.<br>
     * <b>This function throws a exception at any time because its absolutely
     * useless to round a byte value. This function is only implemented to
     * prevent useless function calls.</b>
     *
     * @param value the value that needs to be rounded
     * @return the next larger short of the argument
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> fail", pure = true)
    public static byte ceil(byte value) {
        throw new OutOfCoffeeException("Rounding up a byte value? Seriously?");
    }

    /**
     * Round a double value to the next larger long value. Special cases:
     * <ul>
     * <li>If the argument is NaN or infinite, then the result is 0.</li>
     * <li>If the value is already the integer value, the same value is returned</li>
     * </ul>
     *
     * @param value the value that needs to be rounded
     * @return the next larger long of the argument
     */
    @Contract(pure = true)
    public static long ceil(double value) {
        if (!isNumber(value)) {
            return 0L;
        }

        long possibleValue = (long) value;
        if ((value - possibleValue) > 0.d) {
            ++possibleValue;
        }
        if (possibleValue < value) {
            --possibleValue;
        }
        return possibleValue;
    }

    /**
     * Round a float value to the next larger integer value. Special cases:
     * <ul>
     * <li>If the argument is NaN or infinite, then the result is 0.</li>
     * <li>If the value is already the integer value, the same value is returned</li>
     * </ul>
     *
     * @param value the value that needs to be rounded
     * @return the next larger integer of the argument
     */
    @Contract(pure = true)
    public static int ceil(float value) {
        if (!isNumber(value)) {
            return 0;
        }

        if (value >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < 0.f) {
            return (int) value;
        }
        return (int) value + 1;
    }

    /**
     * Round a integer value to the next larger integer value.<br>
     * <b>This function throws a exception at any time because its absolutely
     * useless to round a integer value. This function is only implemented to
     * prevent useless function calls.</b>
     *
     * @param value the value that needs to be rounded
     * @return the next larger integer of the argument
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> fail", pure = true)
    public static int ceil(int value) {
        throw new OutOfCoffeeException("Rounding up a integer value? Seriously?");
    }

    /**
     * Round a long value to the next larger long value.<br>
     * <b>This function throws a exception at any time because its absolutely
     * useless to round a long value. This function is only implemented to
     * prevent useless function calls.</b>
     *
     * @param value the value that needs to be rounded
     * @return the next larger long of the argument
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> fail", pure = true)
    public static long ceil(long value) {
        throw new OutOfCoffeeException("Rounding up a long value? Seriously?");
    }

    /**
     * Round a short value to the next larger short value.<br>
     * <b>This function throws a exception at any time because its absolutely
     * useless to round a short value. This function is only implemented to
     * prevent useless function calls.</b>
     *
     * @param value the value that needs to be rounded
     * @return the next larger short of the argument
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> fail", pure = true)
    public static short ceil(short value) {
        throw new OutOfCoffeeException("Rounding up a short value? Seriously?");
    }

    /**
     * Limit a value within set borders. In case the value is larger then the
     * maximum value the maximum value is returned. In case the value is smaller
     * then the minimum value, the minimum value is returned. Otherwise the
     * input value is returned.
     *
     * @param value the value that is put into the limits
     * @param min the minimal allowed value, its not allowed to set NaN here
     * @param max the maximal allowed value, its not allowed to set NaN here
     * @return the input value clamped between the minimal and the maximal
     * allowed value
     */
    @Contract(pure = true)
    public static byte clamp(byte value, byte min, byte max) {
        if (max < min) {
            throw new IllegalArgumentException("Minimal value must not be greater then the maximal value.");
        }
        if (value >= max) {
            return max;
        }
        if (value <= min) {
            return min;
        }
        return value;
    }

    /**
     * Limit a value within set borders. In case the value is larger then the
     * maximum value the maximum value is returned. In case the value is smaller
     * then the minimum value, the minimum value is returned. Otherwise the
     * input value is returned. Special cases:
     * <ul>
     * <li>If the input value is NaN the result value will be NaN.</li>
     * </ul>
     *
     * @param value the value that is put into the limits
     * @param min the minimal allowed value, its not allowed to set NaN here
     * @param max the maximal allowed value, its not allowed to set NaN here
     * @return the input value clamped between the minimal and the maximal
     * allowed value
     */
    @Contract(pure = true)
    public static double clamp(
            double value, double min, double max) {
        if (Double.isNaN(value)) {
            return Double.NaN;
        }
        if (Double.isNaN(min) || Double.isNaN(max)) {
            throw new IllegalArgumentException("NaN is not valid value for the top and bottom border.");
        }
        if (max < min) {
            throw new IllegalArgumentException("Minimal value must not be greater then the maximal value.");
        }
        if (value >= max) {
            return max;
        }
        if (value <= min) {
            return min;
        }
        return value;
    }

    /**
     * Limit a value within set borders. In case the value is larger then the
     * maximum value the maximum value is returned. In case the value is smaller
     * then the minimum value, the minimum value is returned. Otherwise the
     * input value is returned. Special cases:
     * <ul>
     * <li>If the input value is NaN the result value will be NaN.</li>
     * </ul>
     *
     * @param value the value that is put into the limits
     * @param min the minimal allowed value, its not allowed to set NaN here
     * @param max the maximal allowed value, its not allowed to set NaN here
     * @return the input value clamped between the minimal and the maximal
     * allowed value
     */
    @Contract(pure = true)
    public static float clamp(
            float value, float min, float max) {
        if (Float.isNaN(value)) {
            return Float.NaN;
        }
        if (Float.isNaN(min) || Float.isNaN(max)) {
            throw new IllegalArgumentException("NaN is not valid value for the top and bottom border.");
        }
        if (max < min) {
            throw new IllegalArgumentException("Minimal value must not be greater then the maximal value.");
        }
        if (value >= max) {
            return max;
        }
        if (value <= min) {
            return min;
        }
        return value;
    }

    /**
     * Limit a value within set borders. In case the value is larger then the
     * maximum value the maximum value is returned. In case the value is smaller
     * then the minimum value, the minimum value is returned. Otherwise the
     * input value is returned.
     *
     * @param value the value that is put into the limits
     * @param min the minimal allowed value, its not allowed to set NaN here
     * @param max the maximal allowed value, its not allowed to set NaN here
     * @return the input value clamped between the minimal and the maximal
     * allowed value
     */
    @Contract(pure = true)
    public static int clamp(int value, int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("Minimal value must not be greater then the maximal value.");
        }
        if (value >= max) {
            return max;
        }
        if (value <= min) {
            return min;
        }
        return value;
    }

    /**
     * Limit a value within set borders. In case the value is larger then the
     * maximum value the maximum value is returned. In case the value is smaller
     * then the minimum value, the minimum value is returned. Otherwise the
     * input value is returned.
     *
     * @param value the value that is put into the limits
     * @param range the range that defines the maximal and the minimal value
     * @return the input value clamped between the minimal and the maximal
     * allowed value
     */
    @Contract(pure = true)
    public static int clamp(int value, @NotNull Range range) {
        return clamp(value, range.getMin(), range.getMax());
    }

    /**
     * Limit a value within set borders. In case the value is larger then the
     * maximum value the maximum value is returned. In case the value is smaller
     * then the minimum value, the minimum value is returned. Otherwise the
     * input value is returned.
     *
     * @param value the value that is put into the limits
     * @param min the minimal allowed value, its not allowed to set NaN here
     * @param max the maximal allowed value, its not allowed to set NaN here
     * @return the input value clamped between the minimal and the maximal
     * allowed value
     */
    @Contract(pure = true)
    public static long clamp(long value, long min, long max) {
        if (max < min) {
            throw new IllegalArgumentException("Minimal value must not be greater then the maximal value.");
        }
        if (value >= max) {
            return max;
        }
        if (value <= min) {
            return min;
        }
        return value;
    }

    /**
     * Limit a value within set borders. In case the value is larger then the
     * maximum value the maximum value is returned. In case the value is smaller
     * then the minimum value, the minimum value is returned. Otherwise the
     * input value is returned.
     *
     * @param value the value that is put into the limits
     * @param min the minimal allowed value, its not allowed to set NaN here
     * @param max the maximal allowed value, its not allowed to set NaN here
     * @return the input value clamped between the minimal and the maximal
     * allowed value
     */
    @Contract(pure = true)
    public static short clamp(
            short value, short min, short max) {
        if (max < min) {
            throw new IllegalArgumentException("Minimal value must not be greater then the maximal value.");
        }
        if (value >= max) {
            return max;
        } else {
            return (value <= min) ? min : value;
        }
    }

    /**
     * These functions shall produce a value with the magnitude of x and the
     * sign of y.
     *
     * @param magnitude the value that stores the magnitude of the result value
     * @param sign the value that stores the sign of the result value
     * @return a value with the magnitude of the argument x and the sign of the
     * argument y
     */
    @Contract(pure = true)
    public static byte copySign(byte magnitude, byte sign) {
        if (((magnitude < 0) && (sign > 0)) || ((magnitude > 0) && (sign < 0))) {
            return (byte) -magnitude;
        }
        return magnitude;
    }

    /**
     * These functions shall produce a value with the magnitude of x and the
     * sign of y. In case either x or y are NaN the result value is NaN too.
     *
     * @param magnitude the value that stores the magnitude of the result value
     * @param sign the value that stores the sign of the result value
     * @return a value with the magnitude of the argument x and the sign of the
     * argument y
     */
    @Contract(pure = true)
    public static double copySign(double magnitude, double sign) {
        if (Double.isNaN(magnitude) || Double.isNaN(sign)) {
            return Double.NaN;
        }
        if (((magnitude < 0.d) && (sign > 0.d)) || ((magnitude > 0.d) && (sign < 0.d))) {
            return -magnitude;
        }
        return magnitude;
    }

    /**
     * These functions shall produce a value with the magnitude of x and the
     * sign of y. In case either x or y are NaN the result value is NaN too.
     *
     * @param magnitude the value that stores the magnitude of the result value
     * @param sign the value that stores the sign of the result value
     * @return a value with the magnitude of the argument x and the sign of the
     * argument y
     */
    @Contract(pure = true)
    public static float copySign(float magnitude, float sign) {
        if (Float.isNaN(magnitude) || Float.isNaN(sign)) {
            return Float.NaN;
        }
        if (((magnitude < 0.f) && (sign > 0.f)) || ((magnitude > 0.f) && (sign < 0.f))) {
            return -magnitude;
        }
        return magnitude;
    }

    /**
     * These functions shall produce a value with the magnitude of x and the
     * sign of y.
     *
     * @param magnitude the value that stores the magnitude of the result value
     * @param sign the value that stores the sign of the result value
     * @return a value with the magnitude of the argument x and the sign of the
     * argument y
     */
    @Contract(pure = true)
    public static int copySign(int magnitude, int sign) {
        if (((magnitude < 0) && (sign > 0)) || ((magnitude > 0) && (sign < 0))) {
            return -magnitude;
        }
        return magnitude;
    }

    /**
     * These functions shall produce a value with the magnitude of x and the
     * sign of y.
     *
     * @param magnitude the value that stores the magnitude of the result value
     * @param sign the value that stores the sign of the result value
     * @return a value with the magnitude of the argument x and the sign of the
     * argument y
     */
    @Contract(pure = true)
    public static long copySign(long magnitude, long sign) {
        if (((magnitude < 0L) && (sign > 0L)) || ((magnitude > 0L) && (sign < 0L))) {
            return -magnitude;
        }
        return magnitude;
    }

    /**
     * These functions shall produce a value with the magnitude of x and the
     * sign of y.
     *
     * @param magnitude the value that stores the magnitude of the result value
     * @param sign the value that stores the sign of the result value
     * @return a value with the magnitude of the argument x and the sign of the
     * argument y
     */
    @Contract(pure = true)
    public static short copySign(short magnitude, short sign) {
        if (((magnitude < 0) && (sign > 0)) || ((magnitude > 0) && (sign < 0))) {
            return (short) -magnitude;
        }
        return magnitude;
    }

    /**
     * Returns cosine of a value.
     *
     * @param fValue The value to cosine, in radians.
     * @return the cosine of the value
     */
    @Contract(pure = true)
    public static float cos(float fValue) {
        return sin(fValue + HALF_PI);
    }

    /**
     * Check if two values are equal.
     *
     * @param value1 the first value
     * @param value2 the second value
     * @return {@code true} if both values are equal
     */
    @Contract(pure = true)
    public static boolean equals(int value1, int value2) {
        return value1 == value2;
    }

    /**
     * Check if two values are nearly equal.
     *
     * @param value1 the first value
     * @param value2 the second value
     * @param delta the maximal allowed difference of with values
     * @return <code>true<code> if the difference between both values is less
     * or equal then delta
     */
    @Contract(pure = true)
    public static boolean equals(
            int value1, int value2, int delta) {
        return abs(value1 - value2) <= delta;
    }

    /**
     * Check if two values are nearly equal.
     *
     * @param value1 the first value
     * @param value2 the second value
     * @param delta the maximal allowed difference of with values
     * @return <code>true<code> if the difference between both values is less
     * or equal then delta
     */
    @Contract(pure = true)
    public static boolean equals(float value1, float value2, float delta) {
        return abs(value1 - value2) <= delta;
    }

    /**
     * Returns E^fValue
     *
     * @param fValue Value to raise to a power
     * @return the e^fValue calculation result
     */
    @Contract(pure = true)
    public static float exp(float fValue) {
        return (float) Math.exp(fValue);
    }

    /**
     * Returns a number rounded down.
     *
     * @param value The value to round
     * @return The given number rounded down
     */
    @Contract(pure = true)
    public static long floor(double value) {
        if (!isNumber(value)) {
            return 0;
        }
        return (long) value;
    }

    /**
     * Returns a number rounded down.
     *
     * @param value The value to round
     * @return The given number rounded down
     */
    @Contract(pure = true)
    public static int floor(float value) {
        if (!isNumber(value)) {
            return 0;
        }
        return (int) value;
    }

    /**
     * Get the inverted square root.
     *
     * @param fValue The value to process.
     * @return the inverted square root value of the parameter
     */
    @Contract(pure = true)
    public static float invSqrt(float fValue) {
        return (float) (1.f / Math.sqrt(fValue));
    }

    /**
     * Check if the double value is a real number. This means it not NaN and not
     * infinite.
     *
     * @param dValue the double value to check
     * @return {@code true} in case this float value is a real number
     */
    @Contract(pure = true)
    public static boolean isNumber(double dValue) {
        return !Double.isInfinite(dValue) && !Double.isNaN(dValue);
    }

    /**
     * Check if the float value is a real number. This means it not NaN and not
     * infinite.
     *
     * @param fValue the float value to check
     * @return {@code true} in case this float value is a real number
     */
    @Contract(pure = true)
    public static boolean isNumber(float fValue) {
        return !Float.isInfinite(fValue) && !Float.isNaN(fValue);
    }

    /**
     * Check if a number is a valid power of two. Such as the values 2, 4, 8, 16
     * and so on.
     *
     * @param number The number to test.
     * @return {@code true} in case the number is a power of two
     */
    @Contract(pure = true)
    public static boolean isPowerOfTwo(int number) {
        return (number > 0) && ((number & (number - 1)) == 0);
    }

    /**
     * Linear interpolation from startValue to endValue by the given percent.
     * Basically: ((1 - percent) * startValue) + (percent * endValue)
     *
     * @param percent Percent value to use. Valid values between 0.f and 1.f
     * @param startValue Beginning value. 0% of f
     * @param endValue ending value. 100% of f
     * @return The interpolated value between startValue and endValue.
     */
    @Contract(pure = true)
    public static float LERP(float percent, float startValue, float endValue) {
        return ((1.f - percent) * startValue) + (percent * endValue);
    }

    /**
     * Returns the log base E of a value.
     *
     * @param fValue The value to log
     * @return The log of fValue base E
     */
    @Contract(pure = true)
    public static float log(float fValue) {
        return (float) Math.log(fValue);
    }

    /**
     * Returns the logarithm of value with given base, calculated as
     * log(value)/log(base), so that pow(base, return)==value
     *
     * @param value The value to log
     * @param base Base of logarithm
     * @return The logarithm of value with given base
     */
    @Contract(pure = true)
    public static float log(float value, float base) {
        return (float) (Math.log(value) / Math.log(base));
    }

    /**
     * Get the next larger power of two number.
     *
     * @param number the number the search for the next larger power of two
     * number shall start at
     * @return the found power of two number
     */
    @Contract(pure = true)
    public static int nearestPowerOfTwo(int number) {
        return (int) Math.pow(2, Math.ceil(Math.log(number) / Math.log(2)));
    }

    /**
     * Returns a random float between 0 and 1.
     *
     * @return A random float between <tt>0.0f</tt> (inclusive) to <tt>1.0f</tt>
     * (exclusive)
     */
    public static float nextRandomFloat() {
        return RANDOM.nextFloat();
    }

    /**
     * Returns a random integer value.
     *
     * @return a random integer value, every value a integer can store is
     * possible
     */
    public static int nextRandomInt() {
        return RANDOM.nextInt();
    }

    /**
     * Returns a random float between min and max
     *
     * @param min the minimal value of the random number range (inclusive)
     * @param max the maximal value of the random number range (exclusive)
     * @return the generated random number
     */
    public static int nextRandomInt(int min, int max) {
        if (min == max) {
            return min;
        }
        if (min > max) {
            throw new IllegalArgumentException("The minimal value must not be larger then the maximal value.");
        }
        return RANDOM.nextInt(max - min) + min;
    }

    /**
     * Takes an value and expresses it in terms of min to max.
     *
     * @param val the value to normalize
     * @param min the bottom border of the range the value has to be in
     * @param max the top value of te range the value has to be in
     * @return the normalized value
     */
    @Contract(pure = true)
    public static float normalize(
            float val, float min, float max) {
        if (Float.isInfinite(val) || Float.isNaN(val)) {
            return 0f;
        }

        if (Float.isInfinite(min) || Float.isNaN(min) || Float.isInfinite(max) || Float.isNaN(max)) {
            throw new IllegalArgumentException(
                    "The minimal and the maximal border must not be NAN or infinite values.");
        }
        float range = max - min;

        if (range < FLT_EPSILON) {
            throw new IllegalArgumentException("Range between min and max is too small.");
        }
        float workVal = val;
        while (workVal > max) {
            workVal -= range;
        }
        while (workVal < min) {
            workVal += range;
        }
        return workVal;
    }

    /**
     * Returns a number raised to an exponent power. fBase^fExponent
     *
     * @param fBase The base value
     * @param fExponent The exponent value
     * @return base raised to exponent
     */
    @Contract(pure = true)
    public static float pow(float fBase, float fExponent) {
        return (float) Math.pow(fBase, fExponent);
    }

    /**
     * Return the closest integer to the double argument. Special cases:
     * <ul>
     * <li>If the argument is NaN, the result is 0.</li>
     * <li>If the argument is negative infinity or any value less than or equal
     * to the value of Long.MIN_VALUE, the result is equal to the value of
     * Long.MIN_VALUE.</li>
     * <li>If the argument is positive infinity or any value greater than or
     * equal to the value of Long.MAX_VALUE, the result is equal to the value of
     * Long.MAX_VALUE.</li>
     * </ul>
     *
     * @param value the value the closest integer is needed
     * @return the closest integer to the argument
     */
    @Contract(pure = true)
    public static long round(double value) {
        if (Double.isNaN(value)) {
            return 0;
        }
        if (value > Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        if (value < Long.MIN_VALUE) {
            return Long.MIN_VALUE;
        }
        return (long) (value + 0.5d);
    }

    /**
     * Return the closest integer to the float argument. Special cases:
     * <ul>
     * <li>If the argument is NaN, the result is 0.</li>
     * <li>If the argument is negative infinity or any value less than or equal
     * to the value of Integer.MIN_VALUE, the result is equal to the value of
     * Integer.MIN_VALUE.</li>
     * <li>If the argument is positive infinity or any value greater than or
     * equal to the value of Integer.MAX_VALUE, the result is equal to the value
     * of Integer.MAX_VALUE.</li>
     * </ul>
     *
     * @param value the value the closest integer is needed
     * @return the closest integer to the argument
     */
    @Contract(pure = true)
    public static int round(float value) {
        if (Float.isNaN(value)) {
            return 0;
        }
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }

        if (value > 0.0f) {
            return (int) (value + 0.5f);
        }
        return (int) (value - 0.5f);
    }

    /**
     * Returns 1 if the number is positive, -1 if the number is negative, and 0
     * otherwise
     *
     * @param fValue The float to examine
     * @return The float's sign
     */
    @Contract(pure = true)
    public static float sign(float fValue) {
        return Math.signum(fValue);
    }

    /**
     * Returns 1 if the number is positive, -1 if the number is negative, and 0
     * otherwise
     *
     * @param iValue The integer to examine
     * @return The integer's sign
     */
    @Contract(pure = true)
    public static int sign(int iValue) {
        if (iValue > 0) {
            return 1;
        }

        if (iValue < 0) {
            return -1;
        }

        return 0;
    }

    /**
     * Returns sine of a value.
     *
     * @param fValue the value to sine, in radians.
     * @return the sine of the value
     */
    @Contract(pure = true)
    public static float sin(float fValue) {
        float value = reduceSinAngle(fValue);
        if (abs(value) <= QUARTER_PI) {
            return (float) Math.sin(value);
        }

        return (float) Math.cos(HALF_PI - value);
    }

    /**
     * Returns the value squared. fValue ^ 2
     *
     * @param fValue The value to square.
     * @return The square of the given value.
     */
    @Contract(pure = true)
    public static float sqr(float fValue) {
        return fValue * fValue;
    }

    /**
     * Returns the value squared. fValue ^ 2
     *
     * @param iValue The value to square.
     * @return The square of the given value.
     */
    @Contract(pure = true)
    public static int sqr(int iValue) {
        return iValue * iValue;
    }

    /**
     * Returns the square root of a given value.
     *
     * @param fValue The value to square root
     * @return The square root of the given value
     */
    @Contract(pure = true)
    public static float sqrt(float fValue) {
        return (float) Math.sqrt(fValue);
    }

    /**
     * Returns the integer square root of a integer value. It acts like
     * {@code (int) Math.sqrt(int)} for values lower then 289, and for
     * values above it returns values close to the real results.
     *
     * @param iValue the value the square root is needed from
     * @return the square root of the value
     */
    @SuppressWarnings({"IfMayBeConditional", "OverlyComplexMethod"})
    @Contract(pure = true)
    public static int sqrt(int iValue) {
        if (iValue >= 0x10000) {
            if (iValue >= 0x1000000) {
                if (iValue >= 0x10000000) {
                    if (iValue >= 0x40000000) {
                        return SQRT_TABLE[iValue >> 24] << 8;
                    }
                    return SQRT_TABLE[iValue >> 22] << 7;
                } else if (iValue >= 0x4000000) {
                    return SQRT_TABLE[iValue >> 20] << 6;
                } else {
                    return SQRT_TABLE[iValue >> 18] << 5;
                }
            } else if (iValue >= 0x100000) {
                if (iValue >= 0x400000) {
                    return SQRT_TABLE[iValue >> 16] << 4;
                }
                return SQRT_TABLE[iValue >> 14] << 3;
            } else if (iValue >= 0x40000) {
                return SQRT_TABLE[iValue >> 12] << 2;
            } else {
                return SQRT_TABLE[iValue >> 10] << 1;
            }
        }
        if (iValue >= 0x100) {
            if (iValue >= 0x1000) {
                if (iValue >= 0x4000) {
                    return SQRT_TABLE[iValue >> 8];
                }
                return SQRT_TABLE[iValue >> 6] >> 1;
            } else if (iValue >= 0x400) {
                return SQRT_TABLE[iValue >> 4] >> 2;
            } else {
                return SQRT_TABLE[iValue >> 2] >> 3;
            }
        }
        if (iValue >= 0) {
            return SQRT_TABLE[iValue] >> 4;
        }
        throw new IllegalArgumentException("Can't get the square root of a negative number.");
    }

    /**
     * Returns the tangent of a value.
     *
     * @param fValue The value to tangent, in radians
     * @return The tangent of fValue
     */
    @Contract(pure = true)
    public static float tan(float fValue) {
        return (float) Math.tan(fValue);
    }

    /**
     * Convert a float value from radians to degrees.
     *
     * @param radians the value in radians
     * @return the value in degrees
     */
    @Contract(pure = true)
    public static float toDegree(float radians) {
        return radians * RAD_TO_DEG;
    }

    /**
     * Convert a float value from degrees to radians.
     *
     * @param degree the value in degrees
     * @return the value in radians
     */
    @Contract(pure = true)
    public static float toRadians(float degree) {
        return degree * DEG_TO_RAD;
    }

    /**
     * Fast Trig functions for x86. This forces the trig function to stay within
     * the safe area on the x86 processor (-45 degrees to +45 degrees) The
     * results may be very slightly off from what the Math and StrictMath trig
     * functions give due to rounding in the angle reduction but it will be very
     * very close.
     *
     * @param radians the original angle
     * @return the angle within the save limits
     */
    @Contract(pure = true)
    private static float reduceSinAngle(float radians) {
        float rad = (int) (radians / TWO_PI); // put us in -2PI to +2PI space
        rad = radians - (rad * TWO_PI);
        if (abs(rad) > PI) { // put us in -PI to +PI space
            rad -= TWO_PI;
        }
        if (abs(rad) > HALF_PI) {// put us in -PI/2 to +PI/2 space
            rad = PI - rad;
        }

        return rad;
    }
}
