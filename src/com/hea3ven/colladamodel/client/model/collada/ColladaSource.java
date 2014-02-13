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
