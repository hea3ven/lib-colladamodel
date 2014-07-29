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

public class ColladaSource {

	private String id;
	private float[] float_data;
	private String[] string_data;
	private String[] params;
	private int count;
	private int stride;

	public ColladaSource() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public float[] getFloatData() {
		return float_data;
	}

	public void setData(float[] data) {
		this.float_data = data;
	}

	public String[] getStringData() {
		return string_data;
	}

	public void setData(String[] data) {
		this.string_data = data;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getStride() {
		return stride;
	}

	public void setStride(int stride) {
		this.stride = stride;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	private int getParamOffset(String param) {
		for (int i = 0; i < params.length; i++) {
			if (params[i].equals(param))
				return i;
		}
		return 0;
	}

	public float getFloat(String param, Integer index) {
		return float_data[index * stride + getParamOffset(param)];
	}

	public float getDouble(String param, Integer index) {
		return float_data[index * stride + getParamOffset(param)];
	}

	public float getFloat(Integer paramOffset, Integer index) {
		return float_data[index * stride + paramOffset];
	}

	public float getDouble(Integer paramOffset, Integer index) {
		return float_data[index * stride + paramOffset];
	}

	public Vec3 getVec3(Integer index, String param1, String param2,
			String param3) {
		if (getStride() == 3)
			return Vec3.createVectorHelper(getDouble(param1, index),
					getDouble(param2, index), getDouble(param3, index));
		else
			return null;
	}

	public Vec3 getVec2(Integer index, String param1, String param2) {
		return Vec3.createVectorHelper(getDouble(param1, index),
				getDouble(param2, index), 0);
	}

	public String getString(String param, Integer index) {
		return getString(getParamOffset(param), index);
	}

	public String getString(Integer paramOffset, Integer index) {
		return string_data[index * stride + paramOffset];
	}
}
