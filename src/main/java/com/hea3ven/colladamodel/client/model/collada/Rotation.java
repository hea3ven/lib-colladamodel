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
		if (animation != null)
			return animation.getAnimationLength();
		else
			return 0.0d;
	}
}
