package com.hea3ven.colladamodel.client.model.collada;

public class LinearInterpolation implements Interpolation {

	@Override
	public double interpolate(double time, KeyFrame frame, KeyFrame nextFrame) {
		return frame.getValue()
				+ (nextFrame.getValue() - frame.getValue())
				* ((time - frame.getFrame()) / nextFrame.getFrame() - frame
						.getFrame());
	}

}
