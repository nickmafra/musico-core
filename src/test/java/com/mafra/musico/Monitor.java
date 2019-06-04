package com.mafra.musico;

public class Monitor {

	public static void main(String[] args) {
		MicrophoneStream mp = new MicrophoneStream();
		Graph graph = new Graph();
		int size = Integer.highestOneBit((int) mp.getSampleRate());
		
		mp.start();
		try {
			try {
				Thread.sleep(mp.getDelay());
			} catch (InterruptedException e) {
				return;
			}
			graph.start();
			while (graph.isVisible()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					break;
				}
				int[] rawData = mp.getData(size);
				if (rawData == null) {
					continue;
				}
				double[] data = FFT.fft(Util.toDouble(rawData, mp.getSampleSizeInBits()));
				graph.setData(data);
				graph.repaint();
			}
		} finally {
			mp.interrupt();
		}
	}

}
