package com.hea3ven.colladamodel.client.model.collada;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

public class Rotation extends Transform {

	private Vec3 vec;
	private double angle;
	private List<Animation> animations;

	public Rotation(Vec3 vec, double angle) {
		this.vec = vec;
		this.angle = angle;

		this.animations = new LinkedList<Animation>();
	}

	public Vec3 getVec() {
		return vec;
	}

	public void apply() {
		GL11.glRotated(angle, vec.xCoord, vec.yCoord, vec.zCoord);
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	@Override
	public void addAnimation(String paramName, Animation anim) {
		if (paramName.equals("ANGLE"))
			animations.add(anim);
	}

	public void applyAnimation(int frame) {
		double angle = this.angle;
		for (Animation animation : animations) {
			angle = animation.getValue(frame);
		}
		GL11.glRotated(angle, vec.xCoord, vec.yCoord, vec.zCoord);
	}

}
