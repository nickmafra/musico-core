package com.mafra.musico;

import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneStream extends Thread {

	private AudioFormat format;
	private float sampleRate = 44100;
	private int sampleSizeInBits = 16; // in bits
	private int channels = 1;

	private int sampleSize; // in bytes
	private int frameSize; // in bytes
	private int range;
	private TargetDataLine targetLine;

	private int delay = 100;
	private byte[] buffer;
	private volatile int[] data;
	private volatile int offset = 0;

	public MicrophoneStream() {
		super("Microphone");
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

	public TargetDataLine getTargetLine() {
		return targetLine;
	}

	public int[] getData() {
		return data == null ? null : Arrays.copyOf(data, data.length);
	}

	public int[] getLastData(int length) {
		int offset = this.offset;
		int[] lastData = new int[length];
		if (offset > length) {
			System.arraycopy(data, offset - length, lastData, 0, length);
		} else {
			int rem = length - offset;
			System.arraycopy(data, data.length - rem, lastData, 0, rem);
			System.arraycopy(data, 0, lastData, rem, offset);
		}
		return lastData;
	}

	public void configFormat() {
		sampleSize = sampleSizeInBits / 8;
		frameSize = channels * sampleSize;
		range = (int) Math.pow(2, sampleSizeInBits);
		format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, sampleSizeInBits, channels, frameSize,
				sampleRate, false);
	}

	private void bufferToData(int bOffset, int length) {
		for (int i = 0; i < length; i += sampleSize) {
			byte b0 = buffer[bOffset + i];
			byte b1 = buffer[bOffset + i + 1];
			data[offset] = (b1<<8) | (b0 & 0xFF);
			if (data[offset] < -range || data[offset] > range) {
				throw new RuntimeException("Value out of range: " + data[offset]);
			}
			offset++;
		}
		if (offset == data.length) {
			offset = 0;
		}
	}

	private void bufferToDataWrap(int length) {
		int rem = (data.length - offset) * sampleSize;
		int bOffset = 0;
		if (length > rem) {
			bufferToData(0, rem);
			bOffset = rem;
			length -= rem;
		}
		bufferToData(bOffset, length);
	}

	private void startLine() throws LineUnavailableException {
		if (targetLine != null && targetLine.isOpen()) {
			stopLine();
		}
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		targetLine = (TargetDataLine) AudioSystem.getLine(info);
		targetLine.open();
		targetLine.start();
	}

	private void stopLine() {
		targetLine.stop();
		targetLine.close();
		targetLine = null;
	}

	@Override
	public void run() {
		try {
			configFormat();
			startLine();
		} catch (LineUnavailableException e) {
			throw new RuntimeException(e);
		}
		// armazena o que cabe no delay
		buffer = new byte[frameSize * (int) (sampleRate * delay / 1000)];
		// armazena o que cabe em 1s
		data = new int[channels * (int) sampleRate];
		offset = 0;
		while (!isInterrupted()) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				break;
			}
			int len = targetLine.read(buffer, 0, buffer.length);
			if (len < 0) {
				break;
			} else if (len > 0) {
				bufferToDataWrap(len);
			}
		}
		stopLine();
	}
	
	private volatile boolean interrupted;

	@Override
	public void interrupt() {
		super.interrupt();
		interrupted = true;
	}
	
	@Override
	public boolean isInterrupted() {
		return super.isInterrupted() || interrupted;
	}
}
