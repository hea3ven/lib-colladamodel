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

package com.hea3ven.colladamodel.client.model.transform;

import java.nio.DoubleBuffer;

import org.lwjgl.opengl.GL11;

public class Matrix extends Transform {
	private DoubleBuffer matrix;

	public Matrix(String name, DoubleBuffer matrix) {
		super(name);
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

}
