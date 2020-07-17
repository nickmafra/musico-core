package com.mafra.musico.monitor;

import com.mafra.musico.FFT;
import com.mafra.musico.MicrophoneStream;
import com.mafra.musico.Util;

public class Monitor {

	private static final int MIN_SLEEP = 10;

	private int frequenciaMinima = 20;
	private int frequenciaRenderizacao = 1000; // Hz

	private int width = 1024;
	private int height = 512;

	private double rememberRate = 0.9;
	private double forgetRate = 1 - rememberRate;

	private double shufleRate = 0.3;

	private double logBase = Math.pow(2, 1.0 / 50);

	public void executar() throws InterruptedException {
		MicrophoneStream mp = new MicrophoneStream();
		Graph graph = new Graph();
		graph.setPositive(true);
		graph.setPanelSize(width, height);
		int sleep = 1000 / frequenciaRenderizacao; // ms
		int size = Integer.highestOneBit((int) mp.getFormat().getSampleRate() / frequenciaMinima);
		double[] memory = null;

		mp.start();
		try {
			Thread.sleep(mp.getDelay());
			int sampleSize = mp.getFormat().getSampleSizeInBits();
			graph.start();
			graph.repaint();
			while (graph.isVisible()) {
				if (sleep >= MIN_SLEEP) {
					Thread.sleep(sleep);
				}
				int[] rawData = mp.getLastData(2 * size);
				if (rawData == null) {
					continue;
				}
				double[] data = Util.normalizeFft(FFT.fft(Util.toDouble(rawData, sampleSize)));
				data = Util.logArray(data, logBase);
				data = Util.shuffle(data, shufleRate);
				data = Util.normalize(data, 0, 20, 0, 1);
				//data = Util.calcLog(data);
				//data = Util.calcMeanSquared(data);
				if (memory == null) {
					memory = data;
				}
				data = Util.updateMemory(memory, data, rememberRate, forgetRate);

				graph.setData(data);
				graph.repaint();
			}
		} finally {
			mp.interrupt();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new Monitor().executar();
	}

}
