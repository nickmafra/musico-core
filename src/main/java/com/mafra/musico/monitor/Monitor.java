package com.mafra.musico.monitor;

import com.mafra.musico.FFT;
import com.mafra.musico.MicrophoneStream;
import com.mafra.musico.Util;

import java.util.Arrays;

public class Monitor {

	public static void main(String[] args) {
		try {
			executar();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void executar() throws InterruptedException {
		MicrophoneStream mp = new MicrophoneStream();
		Graph graph = new Graph();
		graph.setPositive(true);
		graph.setPanelSize(1024, 512);
		int frequenciaMinima = 20;
		int size = Integer.highestOneBit((int) mp.getFormat().getSampleRate() / frequenciaMinima);

		mp.start();
		try {
			Thread.sleep(mp.getDelay());
			int sampleSize = mp.getFormat().getSampleSizeInBits();
			graph.start();
			while (graph.isVisible()) {
				Thread.sleep(10);
				int[] rawData = mp.getLastData(size);
				if (rawData == null) {
					continue;
				}
				double[] frequencies = FFT.fft(Util.toDouble(rawData, sampleSize));
				double[] normal = Util.normalize(Arrays.copyOfRange(frequencies, 0, frequencies.length / 2), 5);
				graph.setData(normal);
				graph.repaint();
			}
		} finally {
			mp.interrupt();
		}
	}

}
