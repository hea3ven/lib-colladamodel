package com.hea3ven.colladamodel.client.model.collada;

public abstract class Transform {

	public Transform() {
	}

	public abstract void apply();

	public abstract void applyAnimation(int frame);

	public abstract void addAnimation(String paramName, Animation anim);
}
