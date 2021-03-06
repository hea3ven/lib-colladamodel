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

package com.hea3ven.colladamodel.client.model.animation;

import com.hea3ven.colladamodel.client.model.interpolation.Interpolation;

public class KeyFrame {
	private double frame;
	private double value;
	private float[] valueMatrix;
	private Interpolation interpolation;

	public KeyFrame(double frame, double value, Interpolation interpolation) {
		this.frame = frame;
		this.value = value;
		this.valueMatrix = null;
		this.interpolation = interpolation;
	}

	public KeyFrame(double frame, float[] value, Interpolation interpolation) {
		this.frame = frame;
		this.valueMatrix = value;
		this.interpolation = interpolation;
	}

	public double getFrame() {
		return frame;
	}

	public void setFrame(double frame) {
		this.frame = frame;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double interpolate(double time, KeyFrame nextFrame) {
		return interpolation.interpolate(time, this, nextFrame);
	}

}
