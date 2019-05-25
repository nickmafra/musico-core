package com.mafra.musico;

/**
 * Criado a partir do modelo do site <a href=
 * "https://introcs.cs.princeton.edu/java/97data/InplaceFFT.java.html">princeton.edu</a>
 * <p>
 * Desenvolvido por Charles Van Loan.
 * <br>
 * Adaptado por Nicolas Mafra.
 * </p>
 *
 */
public class FFT {

	// compute the FFT of x[], assuming its length is a power of 2
	private static void fft(double[] xR, double[] xI, boolean inv) {
		if (xR.length != xI.length) {
			throw new IllegalArgumentException("arrays of different lengths");
		}
		// check that length is a power of 2
		int n = xR.length;
		if (Integer.highestOneBit(n) != n) {
			throw new IllegalArgumentException("n is not a power of 2");
		}

		// bit reversal permutation
		int shift = 1 + Integer.numberOfLeadingZeros(n);
		for (int k = 0; k < n; k++) {
			int j = Integer.reverse(k) >>> shift;
			if (j > k) {
				// swap x[j] and x[k]
				double tempR = xR[j];
				double tempI = xI[j];
				xR[j] = xR[k];
				xI[j] = xI[k];
				xR[k] = tempR;
				xI[k] = tempI;
			}
		}

		// butterfly updates
		for (int L = 2; L <= n; L = L + L) {
			for (int k = 0; k < L / 2; k++) {
				double kth = (inv ? 2 : -2) * k * Math.PI / L;
				double wR = Math.cos(kth);
				double wI = Math.sin(kth);
				for (int j = 0; j < n / L; j++) {
					int pos1 = j * L + k;
					int pos2 = pos1 + L / 2;
					// t = w * x2
					double tR = wR * xR[pos2] - wI * xI[pos2];
					double tI = wR * xI[pos2] + wI * xR[pos2];
					// x2 = x1 - w * x2 = x1 - t
					xR[pos2] = xR[pos1] - tR;
					xI[pos2] = xI[pos1] - tI;
					// x1 = x1 + w * x2 = x1 + t
					xR[pos1] = xR[pos1] + tR;
					xI[pos1] = xI[pos1] + tI;
				}
			}
		}
		
		if (inv) {
			for (int k = 0; k < n; k++) {
				xR[k] /= n;
				xI[k] /= n;
			}
		}
	}

	public static void fft(double[] xR, double[] xI) {
		fft(xR, xI, false);
	}

	public static void ifft(double[] xR, double[] xI) {
		fft(xR, xI, true);
	}

	public static void fft(double[] x) {
		fft(x, new double[x.length], false);
	}

	public static void ifft(double[] x) {
		fft(x, new double[x.length], true);
	}

}
