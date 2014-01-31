package com.hea3ven.colladamodel.client.model.collada;

public class KeyFrame {
	private int frame;
	private double value;

	public KeyFrame(int frame, double value) {
		this.frame = frame;
		this.value = value;
	}

	public int getFrame() {
		return frame;
	}

	public void setFrame(int frame) {
		this.frame = frame;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
