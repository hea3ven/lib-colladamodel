package com.hea3ven.colladamodel.client.model.collada;

public class BezierInterpolation implements Interpolation {
	public static double APPROXIMATION_EPSILON = 1.0e-09;
	public static double VERYSMALL = 1.0e-20;
	public static int MAXIMUM_ITERATIONS = 100;

	private double inTangent;
	private double outTangent;

	public BezierInterpolation(double outTangent, double inTangent) {
		this.outTangent = outTangent;
		this.inTangent = inTangent;
	}

	@Override
	public double interpolate(double time, KeyFrame frame, KeyFrame nextFrame) {
		double s = approximateCubicBezierParameter(time, frame.getFrame(),
				frame.getFrame() / 3.0f + nextFrame.getFrame() * 2.0f / 3.0f,
				frame.getFrame() * 2.0f / 3.0f + nextFrame.getFrame() / 3.0f,
				nextFrame.getFrame());
		return bezierInterpolate(s, frame.getValue(),
				outTangent / 3 + frame.getValue(), nextFrame.getValue()
						- inTangent / 3, nextFrame.getValue());
	}

	private double approximateCubicBezierParameter(double atX, double P0_X,
			double C0_X, double C1_X, double P1_X) {

		if (atX - P0_X < VERYSMALL)
			return 0.0f;

		if (P1_X - atX < VERYSMALL)
			return 1.0f;

		long iterationStep = 0;

		double u = 0.0f;
		double v = 1.0f;

		// iteratively apply subdivision to approach value atX
		while (iterationStep < MAXIMUM_ITERATIONS) {

			// de Casteljau Subdivision.
			double a = (P0_X + C0_X) * 0.5f;
			double b = (C0_X + C1_X) * 0.5f;
			double c = (C1_X + P1_X) * 0.5f;
			double d = (a + b) * 0.5f;
			double e = (b + c) * 0.5f;
			double f = (d + e) * 0.5f; // this one is on the curve!

			// The curve point is close enough to our wanted atX
			if (Math.abs(f - atX) < APPROXIMATION_EPSILON) {
				return clampToZeroOne((u + v) * 0.5f);
			}

			// dichotomy
			if (f < atX) {
				P0_X = f;
				C0_X = e;
				C1_X = c;
				u = (u + v) * 0.5f;
			} else {
				C0_X = a;
				C1_X = d;
				P1_X = f;
				v = (u + v) * 0.5f;
			}

			iterationStep++;
		}

		return clampToZeroOne((u + v) * 0.5f);
	}

	private double clampToZeroOne(double value) {
		if (value < 0.0f)
			return 0.0f;
		if (value > 1.0f)
			return 1.0f;
		return value;
	}

	private double bezierInterpolate(double s, double p0, double c0, double c1,
			double p1) {
		return (float) (Math.pow(1 - s, 3) * p0 + 3 * Math.pow(1 - s, 2) * s
				* c0 + 3 * (1 - s) * Math.pow(s, 2) * c1 + Math.pow(s, 3) * p1);
	}
}
