package com.hea3ven.colladamodel.client.model.collada;

import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

public class Rotation extends Transform {

	private Vec3 vec;
	private double angle;
	private Animation animation;

	public Rotation(Vec3 vec, double angle) {
		this.vec = vec;
		this.angle = angle;

		this.animation = null;
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
	public void setAnimation(String paramName, Animation anim) {
		if (paramName.equals("ANGLE"))
			animation = anim;
	}

	public void applyAnimation(double time) {
		GL11.glRotated((animation == null) ? angle : animation.getValue(time),
				vec.xCoord, vec.yCoord, vec.zCoord);
	}

	@Override
	public double getAnimationLength() {
		return animation.getAnimationLength();
	}

}
