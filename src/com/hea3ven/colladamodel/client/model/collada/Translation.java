/**
 * 
 * Copyright (c) 2014 Hea3veN
 * 
 *  This file is part of lib-colladamodel.
 *
 *  lib-colladamodel is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  lib-colladamodel is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with lib-colladamodel.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.hea3ven.colladamodel.client.model.collada;

import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

public class Translation extends Transform {
	private Vec3 vec;
	private Animation animationX;
	private Animation animationY;
	private Animation animationZ;

	public Translation(Vec3 vec) {
		this.vec = vec;
		this.animationX = null;
		this.animationY = null;
		this.animationZ = null;
	}

	public Vec3 getVec() {
		return vec;
	}

	@Override
	public void setAnimation(String paramName, Animation anim) {
		if (paramName.equals("X"))
			animationX = anim;
		else if (paramName.equals("Y"))
			animationY = anim;
		else if (paramName.equals("Z"))
			animationZ = anim;
	}

	public void apply() {
		GL11.glTranslated(vec.xCoord, vec.yCoord, vec.zCoord);
	}

	public void applyAnimation(double time) {
		GL11.glTranslated(
				(animationX == null) ? vec.xCoord : animationX.getValue(time),
				(animationY == null) ? vec.yCoord : animationY.getValue(time),
				(animationZ == null) ? vec.zCoord : animationZ.getValue(time));
	}

	@Override
	public double getAnimationLength() {
		double animationLength = 0;
		if (animationX != null
				&& animationX.getAnimationLength() > animationLength)
			animationLength = animationX.getAnimationLength();
		if (animationY != null
				&& animationY.getAnimationLength() > animationLength)
			animationLength = animationY.getAnimationLength();
		if (animationZ != null
				&& animationZ.getAnimationLength() > animationLength)
			animationLength = animationZ.getAnimationLength();
		return animationLength;
	}

}
