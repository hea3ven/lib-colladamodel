package com.hea3ven.colladamodel.client.model;

import com.hea3ven.colladamodel.ModColladaModel;

import net.minecraft.util.ResourceLocation;

public class AnimationState {
	private double frame;
	private boolean lock;
	private boolean repeat;
	private ResourceLocation resource;

	public AnimationState() {
		frame = 0.0d;
	}

	public void setAnimation(ResourceLocation resource) {
		this.setResource(resource);
		frame = 0.0d;
	}

	public double getFrame() {
		return frame;
	}

	public boolean addFrame() {
		if (!lock) {
			frame += 0.05d;
			if (ModColladaModel.getModelManager().getModel(getResource())
					.getAnimationLength() <= frame) {
				setLockFrame(lock);
				return false;
			}
		}
		return true;
	}

	public boolean isLockFrame() {
		return lock;
	}

	public void setLockFrame(boolean lock) {
		this.lock = lock;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public ResourceLocation getResource() {
		return resource;
	}

	public void setResource(ResourceLocation resource) {
		this.resource = resource;
	}

	public void render() {
		ModColladaModel.getModelManager().getModel(getResource())
				.renderAnimationAll(frame);
	}
}
