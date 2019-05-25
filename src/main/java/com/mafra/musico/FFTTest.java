package com.mafra.musico;

import java.util.Arrays;
import java.util.Random;

public class FFTTest {

	private static final Random rdm = new Random();

	private static double[] randomArray(int length, int precision) {
		double[] x = new double[length];
		for (int i = 0; i < length; i++) {
			x[i] = rdm.nextInt(precision);
		}
		return x;
	}

	private static void roundArray(double[] x) {
		for (int i = 0; i < x.length; i++) {
			x[i] = Math.round(x[i]);
		}
	}

	private static void roundedFFT(double[] xR, double[] xI) {
		FFT.fft(xR, xI);
		roundArray(xR);
		roundArray(xI);
	}

	public static void main(String[] args) {
		int length = 65536; // 2^16
		int precision = length;
		
		double[] xR = randomArray(length, precision);
		double[] xI = randomArray(length, precision);
		double[] yR, yI;

		System.out.println("Measuring time...");
		int tests = 200;
		long time = -System.currentTimeMillis();
		for (int i = 0; i < tests; i++) {
			yR = Arrays.copyOf(xR, length);
			yI = Arrays.copyOf(xI, length);
			roundedFFT(yR, yI);
		}
		time = System.currentTimeMillis() + time;
		System.out.println("Time elapsed (s): " + (time / 1000f) / tests);
		
		// verificação
		yR = Arrays.copyOf(xR, length);
		yI = Arrays.copyOf(xI, length);
		FFT.fft(yR, yI);
		FFT.ifft(yR, yI);
		roundArray(yR);
		roundArray(yI);
		for (int i = 0; i < length; i++) {
			if (yR[i] != xR[i] || yI[i] != xI[i]) {
				System.out.println("Fail!");
				System.out.println("Expected: " + xR[i] + " + " + xI[i] + "i");
				System.out.println("Resulted: " + yR[i] + " + " + yI[i] + "i");
				return;
			}
		}
		System.out.println("OK!");
	}

}
