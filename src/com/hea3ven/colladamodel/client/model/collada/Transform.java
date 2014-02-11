package com.hea3ven.colladamodel.client.model.collada;

public abstract class Transform {

	public Transform() {
	}

	public abstract void apply();

	public abstract void applyAnimation(double frame);

	public abstract void setAnimation(String paramName, Animation anim);

    public abstract double getAnimationLength();
}
