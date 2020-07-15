package com.mafra.musico;

import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneStream extends Thread {

	private AudioFormat format;

	private TargetDataLine targetLine;

	private int delay = 100;
	private byte[] buffer;
	private volatile int[] data;
	private volatile int offset = 0;
	private long timeStart;
	private int pick;

	public MicrophoneStream() {
		super("Microphone");
		configDefaultFormat();
	}

	public AudioFormat getFormat() {
		return format;
	}

	/**
	 * Equivalent to getFormat().getSampleSizeInBits() / 8
	 * 
	 * @return the sample size, in bytes
	 */
	public int getSampleSize() {
		return format.getSampleSizeInBits() / 8;
	}

	public TargetDataLine getTargetLine() {
		return targetLine;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public synchronized int[] getData(int length) {
		return data == null ? null : Arrays.copyOf(data, length);
	}

	public synchronized int[] getLastData(int length) {
		int offset = this.offset;
		int[] lastData = new int[length];
		if (data == null) {
			return lastData;
		}
		if (offset > length) {
			System.arraycopy(data, offset - length, lastData, 0, length);
		} else {
			int rem = length - offset;
			System.arraycopy(data, data.length - rem, lastData, 0, rem);
			System.arraycopy(data, 0, lastData, rem, offset);
		}
		return lastData;
	}

	public void configFormat(AudioFormat format) {
		if (format.isBigEndian() || format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
			throw new IllegalArgumentException("AudioFormat not supported.");
		}
		this.format = format;
	}

	public void configFormat(float sampleRate, int sampleSizeInBits, int channels) {
		boolean signed = true;
		boolean bigEndian = false;
		format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		configFormat(format);
	}

	public void configDefaultFormat() {
		configFormat(44100, 16, 1);
	}

	private synchronized void bufferToData(int bOffset, int length) {
		int sampleSize = getSampleSize();
		int range = 1 << (format.getSampleSizeInBits() - 1);
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
		int rem = (data.length - offset) * getSampleSize();
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
			startLine();
		} catch (LineUnavailableException e) {
			throw new RuntimeException(e);
		}
		// armazena o que cabe no delay
		buffer = new byte[format.getChannels() * getSampleSize() * (int) (format.getSampleRate() * delay / 1000)];
		// armazena o que cabe em 1s
		data = new int[format.getChannels() * (int) format.getSampleRate()];
		offset = 0;
		timeStart = System.currentTimeMillis();
		pick = 0;
		long dt;
		while (!isInterrupted()) {
			int len = targetLine.read(buffer, 0, buffer.length);
			if (len < 0) {
				break;
			} else if (len > 0) {
				bufferToDataWrap(len);
			}
			pick++;
			dt = timeStart + pick * delay - System.currentTimeMillis();
			if (dt > 0) {
				try {
					Thread.sleep(dt);
				} catch (InterruptedException e) {
					break;
				}
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
