package com.mafra.musico;

import java.util.Random;

public class Util {

	public static final Random rdm = new Random();

	public static double[] randomArray(int length, int precision) {
		double p = (precision - 1) / 2;
		double[] x = new double[length];
		for (int i = 0; i < length; i++) {
			x[i] = (rdm.nextInt(precision) - p) / p;
		}
		return x;
	}

	public static void roundArray(double[] x, int precision) {
		double p = (precision - 1) / 2;
		for (int i = 0; i < x.length; i++) {
			x[i] = Math.round(p * x[i]) / p;
		}
	}

	public static double[] toDouble(int[] x, int sizeInBits) {
		double precision = 1 << (sizeInBits - 1);
		double[] y = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			y[i] = x[i] / precision;
		}
		return y;
	}

}
