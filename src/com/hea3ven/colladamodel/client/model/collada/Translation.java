package com.hea3ven.colladamodel.client.model.collada;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

public class Translation extends Transform {
	private Vec3 vec;
	private List<Animation> animationsX;
	private List<Animation> animationsY;
	private List<Animation> animationsZ;

	public Translation(Vec3 vec) {
		this.vec = vec;
		this.animationsX = new LinkedList<Animation>();
		this.animationsY = new LinkedList<Animation>();
		this.animationsZ = new LinkedList<Animation>();
	}

	public Vec3 getVec() {
		return vec;
	}

	@Override
	public void addAnimation(String paramName, Animation anim) {
		if (paramName.equals("X"))
			animationsX.add(anim);
		else if (paramName.equals("Y"))
			animationsY.add(anim);
		else if (paramName.equals("Z"))
			animationsZ.add(anim);
	}

	public void apply() {
		GL11.glTranslated(vec.xCoord, vec.yCoord, vec.zCoord);
	}

	public void applyAnimation(double time) {
		double x = vec.xCoord;
		double y = vec.yCoord;
		double z = vec.zCoord;
		for (Animation animation : animationsX)
			x = animation.getValue(time);
		for (Animation animation : animationsY)
			y = animation.getValue(time);
		for (Animation animation : animationsZ)
			z = animation.getValue(time);
		GL11.glTranslated(x, y, z);
	}

    @Override
    public double getAnimationLength()
    {
        double animationLength = 0;
        for (Animation animation : animationsX)
            if(animation.getAnimationLength() > animationLength)
                animationLength = animation.getAnimationLength();
        for (Animation animation : animationsY)
            if(animation.getAnimationLength() > animationLength)
                animationLength = animation.getAnimationLength();
        for (Animation animation : animationsZ)
            if(animation.getAnimationLength() > animationLength)
                animationLength = animation.getAnimationLength();
       return animationLength;
    }

}
