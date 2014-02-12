package com.hea3ven.colladamodel.client.model.collada;

public class LinearInterpolation implements Interpolation {

	@Override
	public double interpolate(double time, KeyFrame frame, KeyFrame nextFrame) {
		double s = (time - frame.getFrame())
				/ (nextFrame.getFrame() - frame.getFrame());
		return frame.getValue() + (nextFrame.getValue() - frame.getValue()) * s;
	}

}
