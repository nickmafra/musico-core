package com.mafra.musico;

import java.util.Arrays;
import java.util.Random;

public class Util {

	public static final Random rdm = new Random();

	private Util() {}

	public static double[] randomArray(int length, int precision) {
		final int p = (precision - 1) / 2;
		double[] x = new double[length];
		for (int i = 0; i < length; i++) {
			x[i] = ((double) rdm.nextInt(precision) - p) / p;
		}
		return x;
	}

	public static void roundArray(double[] x, int precision) {
		final int p = (precision - 1) / 2;
		for (int i = 0; i < x.length; i++) {
			x[i] = ((double) Math.round(p * x[i])) / p;
		}
	}

	public static double[] toDouble(int[] x, int sizeInBits) {
		final int precision = 1 << (sizeInBits - 1);
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			y[i] = ((double) x[i]) / precision;
		}
		return y;
	}

	public static double[] calcMeanSquared(double[] data) {
		double mean = 0;
		for (double v : data) {
			mean += v;
		}
		mean /= data.length;
		double[] meanArray = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			double x = (data[i] - mean);
			meanArray[i] += x * x;
		}
		return meanArray;
	}

	public static double[] divides(double[] x, int value) {
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			y[i] = x[i] / value;
		}
		return y;
	}

	public static double[] normalizeFft(double[] frequencies) {
		return Arrays.copyOfRange(frequencies, 0, frequencies.length / 2);
	}

	public static double[] calcLog(double[] x) {
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			y[i] = Math.log(x[i]);
		}
		return y;
	}

	public static double normalize(double x, int minX, int maxX, int min, int max) {
		if (x < minX) {
			return min;
		}
		if (x > maxX) {
			return max;
		}
		return (max * (x - minX) + min * (maxX - x)) / (maxX - minX);
	}

	public static double[] normalize(double[] x, int minX, int maxX, int min, int max) {
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			y[i] = normalize(x[i], minX, maxX, min, max);
		}
		return y;
	}

	public static int logBase2(int n) {
		return 31 - Integer.numberOfLeadingZeros(n);
	}

	public static double log(int n, double base) {
		return Math.log(n) / Math.log(base);
	}

	public static double[] logArray(double[] x, double base) {
		int n = x.length;
		double[] y = new double[(int) Math.ceil(log(n, base))];

		int k = 0;
		int i = 0;
		while (i < x.length && k < y.length) {
			double p = Math.pow(base, k);
			if (i >= p) {
				y[k]  = x[i - 1];
			}
			int q = 0;
			while (i < p && i < x.length) {
				y[k] += x[i];
				q++;
				i++;
			}
			if (q > 1) {
				y[k] /= q;
			}
			k++;
		}
		return y;
	}

	public static double[] updateMemory(double[] memory, double[] newData, double rememberRate, double forgetRate) {
		if (memory.length != newData.length) {
			throw new IllegalArgumentException("lengths not equals");
		}
		for (int i = 0; i < memory.length; i++) {
			memory[i] = rememberRate * memory[i] + forgetRate * newData[i];
		}
		return memory;
	}

	private static double shuffle(double x, double x1, double rate) {
		return x1 * rate + (1 - rate) * x;
	}

	private static double shuffle(double x, double x1, double x2, double rate) {
		return (x1 + x2) * rate + (1 - 2 * rate) * x;
	}

	public static double[] shuffle(double[] x, double rate) {
		double[] y = new double[x.length];
		y[0] = shuffle(x[0], x[1], rate);
		for (int i = 1; i < x.length - 1; i++) {
			y[i] = shuffle(x[i], x[i - 1], x[i + 1], rate);
		}
		y[x.length - 1] = shuffle(x[x.length - 1], x[x.length - 2], rate);
		return y;
	}

}
