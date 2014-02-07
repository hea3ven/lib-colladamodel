package com.hea3ven.colladamodel.client.model.collada;

public interface Interpolation {
	double interpolate(double time, KeyFrame frame, KeyFrame nextFrame);
}
