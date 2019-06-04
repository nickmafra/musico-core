package com.mafra.musico;

public class MicrophoneStreamTest {

	public static void main(String[] args) throws InterruptedException {
		MicrophoneStream phone = new MicrophoneStream();
		phone.start();
		Thread.sleep(1000);
		int n = 100;
		while (true) {
			Thread.sleep(100);
			double[] data = Util.toDouble(phone.getLastData(4410), 16);
			if (data == null) {
				continue;
			}
			double mean = 0;
			for (int i = 0; i < data.length; i++) {
				mean += data[i];
			}
			mean /= data.length;
			double level = 0;
			for (int i = 0; i < data.length; i++) {
				double x = (data[i] - mean);
				level += x * x;
			}
			level = 10 * Math.log10(Math.sqrt(level / data.length));
			System.out.println("n = " + n);
			System.out.println("Mean:\t" + String.format("%10f", mean));
			System.out.println("Level:\t" + String.format("%10f", level));
			n--;
			if (n <= 0) {
				phone.interrupt();
			}
			if (!phone.isAlive()) {
				System.out.println("Phone stoped!");
				break;
			}
		}
	}

}
