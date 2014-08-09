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
	private ColladaSourceType type;
	private float[] float_data;
	private float[][] float4x4_data;
	private String[] string_data;
	private String[] params;
	private int count;
	private int stride;

	public ColladaSource(String id, String[] params, int count, int stride) {
		this.id = id;
		this.params = params;
		this.count = count;
		this.stride = stride;
	}

	public ColladaSource(String id, String[] params, int stride,
			float[] float_data) {
		this(id, params, float_data.length / stride, stride);
		this.float_data = float_data;
		this.type = ColladaSourceType.FLOAT;
	}

	public ColladaSource(String id, String param, String[] string_data) {
		this(id, new String[] { param }, string_data.length, 1);
		this.string_data = string_data;
		this.type = ColladaSourceType.NAME;
	}

	public ColladaSource(String id, String param, float[][] float4x4_data) {
		this(id, new String[] { param }, float4x4_data.length, 1);
		this.float4x4_data = float4x4_data;
		this.type = ColladaSourceType.FLOAT4x4;
	}

	public String getId() {
		return id;
	}

	public int getCount() {
		return count;
	}
	
	public ColladaSourceType getType() {
		return type;
	}

	public int getStride() {
		return stride;
	}

	public String[] getParams() {
		return params;
	}

	private int getParamOffset(String param) {
		for (int i = 0; i < params.length; i++) {
			if (params[i].equals(param))
				return i;
		}
		return 0;
	}

	public float getFloat(String param, Integer index) {
		return getFloat(getParamOffset(param), index);
	}

	public float getDouble(String param, Integer index) {
		return getDouble(getParamOffset(param), index);
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

	public float[] getFloat4x4(String param, Integer index) {
		return getFloat4x4(getParamOffset(param), index);
	}

	public float[] getFloat4x4(Integer paramOffset, Integer index) {
		return float4x4_data[index * stride + paramOffset];
	}
}
