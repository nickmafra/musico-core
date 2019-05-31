package com.mafra.musico;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Graph extends JFrame {
	private static final long serialVersionUID = 1L;

	private GraphPanel panel;
	
	private int width = 1024, height = 1024;
	private double[] data;

	public Graph() {
		super("Graph");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = new GraphPanel();
		getContentPane().add(panel);
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
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
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
			
			int y0 = height / 2;
			g2.setColor(Color.WHITE);
			for (int x = 0; x < width; x++) {
				int y = (int) (y0 + data[x] * height / 2);
				y = y < 0 ? 0 : y > height ? height : y;
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
		
		double[] data;
		while(graph.isActive()) {
			Thread.sleep(1000);
			data = TestUtil.randomArray(length, precision);
			graph.setData(data);
			graph.repaint();
		}
	}
}
