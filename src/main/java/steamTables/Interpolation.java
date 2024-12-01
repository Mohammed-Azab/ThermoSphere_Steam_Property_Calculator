package steamTables;

public class Interpolation {

    /**
     * Performs linear interpolation.
     * Formula: y = y1 + (x - x1) * ((y2 - y1) / (x2 - x1))
     *
     * @param x1 the first x-coordinate
     * @param y1 the y-coordinate corresponding to x1
     * @param x2 the second x-coordinate
     * @param y2 the y-coordinate corresponding to x2
     * @param x  the x-value for which y needs to be interpolated
     * @return the interpolated y-value
     */
    public static double linear(double x1, double y1, double x2, double y2, double x) {
        if (x1 == x2) {
            throw new IllegalArgumentException("x1 and x2 cannot be the same value for interpolation.");
        }
        return y1 + (x - x1) * ((y2 - y1) / (x2 - x1));
    }

    /**
     * Performs bilinear interpolation.
     * Used for interpolating in two dimensions.
     *
     * @param x1 the first x-coordinate
     * @param x2 the second x-coordinate
     * @param y1 the first y-coordinate
     * @param y2 the second y-coordinate
     * @param q11 the value at (x1, y1)
     * @param q21 the value at (x2, y1)
     * @param q12 the value at (x1, y2)
     * @param q22 the value at (x2, y2)
     * @param x the x-value for which interpolation is needed
     * @param y the y-value for which interpolation is needed
     * @return the interpolated value at (x, y)
     */
    public static double bilinear(double x1, double x2, double y1, double y2,
                                  double q11, double q21, double q12, double q22,
                                  double x, double y) {
        double r1 = linear(x1, q11, x2, q21, x);
        double r2 = linear(x1, q12, x2, q22, x);
        return linear(y1, r1, y2, r2, y);
    }

    /**
     * Finds the nearest indices in a sorted array for interpolation.
     *
     * @param array the sorted array of values
     * @param value the value to find indices for
     * @return an array of size 2 containing the lower and upper indices
     */
    public static int[] findBoundingIndices(double[] array, double value) {
        int lowerIndex = -1;
        int upperIndex = -1;

        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] <= value && value <= array[i + 1]) {
                lowerIndex = i;
                upperIndex = i + 1;
                break;
            }
        }

        if (lowerIndex == -1 || upperIndex == -1) {
            throw new IllegalArgumentException("Value out of bounds for interpolation.");
        }

        return new int[]{lowerIndex, upperIndex};
    }
}
