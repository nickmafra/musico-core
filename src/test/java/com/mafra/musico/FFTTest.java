package com.mafra.musico;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class FFTTest {

	@Test
	public void testFFT() {
		int length = 65536;
		int precision = 65536; // 2^16

		double[] xR = Util.randomArray(length, precision);
		double[] xI = Util.randomArray(length, precision);

		// verificação
		double[] yR = Arrays.copyOf(xR, length);
		double[] yI = Arrays.copyOf(xI, length);
		FFT.fft(yR, yI);
		FFT.ifft(yR, yI);
		Util.roundArray(yR, precision);
		Util.roundArray(yI, precision);
		for (int i = 0; i < length; i++) {
			assertThat(yR[i] + " + " + yI[i] + "i").isEqualTo(xR[i] + " + " + xI[i] + "i");
		}
	}

	private static final int QT_TESTS = 200;
	private static final int EXPECTED_FOREACH_TIME = 20; // ms
	private static final int TIMEOUT_FOREACH_TIME = 40; // ms

	@Test(timeout = QT_TESTS * TIMEOUT_FOREACH_TIME)
	public void testFFT_time() {
		int length = 65536;
		int precision = 65536; // 2^16
		
		double[] xR = Util.randomArray(length, precision);
		double[] xI = Util.randomArray(length, precision);

		long startTime = System.nanoTime();
		for (int i = 0; i < QT_TESTS; i++) {
			FFT.fft(xR, xI);
		}
		long time = (System.nanoTime() - startTime) / 1000_000;
		assertThat(time / QT_TESTS).isLessThanOrEqualTo(EXPECTED_FOREACH_TIME);
	}

}
