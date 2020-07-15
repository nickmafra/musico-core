package com.mafra.musico;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class FFTTest {

	@Test
	void testFFT() {
		int length = 65536;
		int precision = 65536; // 2^16

		double[] origR = Util.randomArray(length, precision);
		double[] origI = Util.randomArray(length, precision);

		// verificação
		double[] xR = Arrays.copyOf(origR, length);
		double[] xI = Arrays.copyOf(origI, length);
		FFT.fft(xR, xI);
		FFT.ifft(xR, xI);
		Util.roundArray(xR, precision);
		Util.roundArray(xI, precision);
		for (int i = 0; i < length; i++) {
			assertThat(xR[i] + " + " + xI[i] + "i").isEqualTo(origR[i] + " + " + origI[i] + "i");
		}
	}

	private static final int QT_TESTS = 200;
	private static final int EXPECTED_FOREACH_TIME = 20; // ms
	private static final int TIMEOUT_FOREACH_TIME = 40; // ms

	@Test
	@Timeout(value = QT_TESTS * TIMEOUT_FOREACH_TIME, unit = TimeUnit.MILLISECONDS)
	void testFFT_time() {
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
