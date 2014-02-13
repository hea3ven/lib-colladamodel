package com.hea3ven.colladamodel.client.model.collada;

import java.nio.DoubleBuffer;

import org.lwjgl.opengl.GL11;

public class Matrix extends Transform {
	private DoubleBuffer matrix;

	public Matrix(DoubleBuffer matrix) {
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
		matrix.rewind();
		GL11.glMultMatrix(matrix);
	}

	@Override
	public void applyAnimation(double frame) {
		apply();
	}

	@Override
	public void setAnimation(String paramName, Animation anim) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getAnimationLength() {
		// TODO Auto-generated method stub
		return 0;
	}

}
