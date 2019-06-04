package com.mafra.musico;

import java.util.Arrays;

public class FFTTest {

	public static void main(String[] args) {
		int length = 65536;
		int precision = 65536; // 2^16
		
		double[] xR = Util.randomArray(length, precision);
		double[] xI = Util.randomArray(length, precision);
		double[] yR, yI;

		System.out.println("Measuring time...");
		int tests = 200;
		long time = -System.currentTimeMillis();
		for (int i = 0; i < tests; i++) {
			yR = Arrays.copyOf(xR, length);
			yI = Arrays.copyOf(xI, length);
			Util.roundedFFT(yR, yI, precision);
		}
		time = System.currentTimeMillis() + time;
		System.out.println("Time elapsed (s): " + (time / 1000f) / tests);
		
		// verificação
		yR = Arrays.copyOf(xR, length);
		yI = Arrays.copyOf(xI, length);
		FFT.fft(yR, yI);
		FFT.ifft(yR, yI);
		Util.roundArray(yR, precision);
		Util.roundArray(yI, precision);
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
