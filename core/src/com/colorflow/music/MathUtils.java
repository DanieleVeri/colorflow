package com.colorflow.music;

import java.util.Arrays;

public final class MathUtils {

    public static void sort(double[] array, boolean descending) {
        Arrays.sort(array);
        double swap;
        if (descending) {
            for (int i = 0; i < array.length / 2; i++) {
                swap = array[i];
                array[i] = array[array.length - 1 - i];
                array[array.length - 1 - i] = swap;
            }
        }
    }

    public static void sort(int[] array, boolean descending) {
        Arrays.sort(array);
        int swap;
        if (descending) {
            for (int i = 0; i < array.length / 2; i++) {
                swap = array[i];
                array[i] = array[array.length - 1 - i];
                array[array.length - 1 - i] = swap;
            }
        }
    }

    public static int indexOf(double[] array, double value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(int[] array, double value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public static double mean(double[] array) {
        double sum = 0.0;
        int i = 0;
        for (double a : array) {
            if (!Double.isNaN(a)) {
                sum += a;
                i++;
            }
        }
        return sum / (double) i;
    }

    public static double mean(int[] array) {
        double sum = 0.0;
        int i = 0;
        for (double a : array) {
            if (!Double.isNaN(a)) {
                sum += a;
                i++;
            }
        }
        return sum / (double) i;
    }

    public static double rootMeanSquare(double[] array) {
        double sum = 0.0;
        int i = 0;
        for (double a : array) {
            if (!Double.isNaN(a)) {
                sum += a * a;
                i++;
            }
        }
        return Math.sqrt(sum / (double) i);
    }

    public static double weightedAvg(double[] array) {
        double sum = 0.0, w = 0;
        for (int i = 0; i < array.length; i++) {
            if (!Double.isNaN(array[i])) {
                sum += array[i] * i;
                w += array[i];
            }
        }
        return sum / w;
    }


    public static double var(double[] array) {
        double mean = mean(array);
        double temp = 0;
        int i = 0;
        for (double a : array) {
            if (!Double.isNaN(a)) {
                temp += (a - mean) * (a - mean);
                i++;
            }
        }
        return temp / (double) (i - 1);
    }

    public static double var(int[] array) {
        double mean = mean(array);
        double temp = 0;
        int i = 0;
        for (int a : array) {
            if (!Double.isNaN(a)) {
                temp += (a - mean) * (a - mean);
                i++;
            }
        }
        return temp / (double) (i - 1);
    }

    public static double[] autocorrelation(double[] array) {
        double sum;
        double[] result = new double[array.length];
        for (int l = 0; l < array.length; l++) {
            sum = 0.0;
            for (int i = 0; i < array.length; i++) {
                if (i - l >= 0 && i - l < array.length) {
                    sum += array[i] * array[i - l];
                }
            }
            result[l] = sum;
        }
        return result;
    }

    public static double[] modDFT(double[] signal) {
        double[] dft = new double[signal.length];
        for (int q = 0; q < dft.length; q++) {
            double sumR = 0, sumI = 0;
            for (int n = 0; n < signal.length; n++) {
                sumR += signal[n] * Math.cos(-2.0 * Math.PI * (double) n * (double) q / (double) signal.length);
                sumI += signal[n] * Math.sin(-2.0 * Math.PI * (double) n * (double) q / (double) signal.length);
            }
            dft[q] = Math.sqrt(sumR * sumR + sumI * sumI);
        }
        return dft;
    }

    public static boolean isMultiple(double a, double b) {
        double r = Math.max(a, b) / Math.min(a, b);
        if (r - Math.round(r) < 0.01) {
            return true;
        }
        return false;
    }

}
