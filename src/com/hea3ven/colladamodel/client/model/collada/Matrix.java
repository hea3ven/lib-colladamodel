package com.hea3ven.colladamodel.client.model.collada;

import java.nio.DoubleBuffer;

public class Matrix extends Transform {
	private DoubleBuffer matrix;

	public Matrix(String id, DoubleBuffer matrix) {
		this.matrix = matrix;
	}

	public DoubleBuffer getMatrix() {
		return matrix;
	}

	public void setMatrix(DoubleBuffer matrix) {
		this.matrix = matrix;
	}

	@Override
	public void apply() {
		// matrix.rewind();
		// GL11.glLoadMatrix(matrix);
	}

	@Override
	public void applyAnimation(double frame) {
		apply();
	}

	@Override
	public void addAnimation(String paramName, Animation anim) {
		// TODO Auto-generated method stub

	}

    @Override
    public double getAnimationLength()
    {
        // TODO Auto-generated method stub
        return 0;
    }

}
