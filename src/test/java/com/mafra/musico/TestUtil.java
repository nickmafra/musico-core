package com.mafra.musico;

import java.util.Random;

public class TestUtil {

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

	public static void roundedFFT(double[] xR, double[] xI, int precision) {
		FFT.fft(xR, xI);
		roundArray(xR, precision);
		roundArray(xI, precision);
	}

}
