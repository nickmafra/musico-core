package com.mafra.musico;

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

	public static double[] normalize(double[] x, int unit) {
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			y[i] = ((double) x[i]) / unit;
		}
		return y;
	}

}
