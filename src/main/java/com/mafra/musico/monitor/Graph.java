package com.mafra.musico.monitor;

import com.mafra.musico.Util;

import javax.swing.*;
import java.awt.*;

public class Graph {

	private JFrame jFrame;
	private GraphPanel panel;
	
	private int width = 1024;
	private int height = 1024;
	private double[] data;
	private boolean positive;
	private int hueVariation = 110;

	public Graph() {
		jFrame = new JFrame("Graph");
		jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		panel = new GraphPanel();
		jFrame.getContentPane().add(panel);
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}

	public void setPanelSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setData(double[] data) {
		this.data = data;
	}

	public void start() {
		panel.setPreferredSize(new Dimension(width, height));
		jFrame.pack();
		jFrame.setLocationRelativeTo(null);
		jFrame.setVisible(true);
	}

	public boolean isActive() {
		return jFrame.isActive();
	}

	public void repaint() {
		jFrame.repaint();
	}

	public boolean isVisible() {
		return jFrame.isVisible();
	}

	public class GraphPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, getWidth(), getHeight());

			if (data == null) {
				return;
			}

			int y0 = positive ? height : height / 2;
			int h = positive ? height : height / 2;
			for (int x = 0; x < width; x++) {
				g2.setColor(Color.getHSBColor((x % hueVariation) / (float) hueVariation, 1, 1));
				double v = 0;
				int x0 = data.length * x / width;
				int x1 = data.length * (x + 1) / width;
				if (x0 == x1) {
					continue;
				}
				for (int i = x0; i < x1; i++) {
					v += data[i];
				}
				v /= x1 - x0;
				int y = (int) (y0 - v * h);
				y = y < 0 ? 0 : Math.min(y, height);
				g2.drawLine(x, y0, x, y);
			}
		}

	}

	public static void main(String[] args) throws InterruptedException {
		int length = 1024;
		int precision = 512;
		
		Graph graph = new Graph();
		graph.setPanelSize(length, precision);
		graph.start();

		while(graph.isActive()) {
			Thread.sleep(1000);
			graph.setData(Util.randomArray(length, precision));
			graph.repaint();
		}
	}
}
