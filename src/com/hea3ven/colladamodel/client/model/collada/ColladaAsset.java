package com.hea3ven.colladamodel.client.model.collada;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.util.Tuple;
import net.minecraft.util.Vec3;

public class ColladaAsset {

	private Map<String, Geometry> geometries;
	private Map<String, Model> models;
	private Map<String, Transform> transforms;
	private List<Tuple> animations;
	public String xAxis;
	public String zAxis;
	public String yAxis;

	public ColladaAsset() {
		geometries = new HashMap<String, Geometry>();
		models = new HashMap<String, Model>();
		transforms = new HashMap<String, Transform>();
		animations = new LinkedList<Tuple>();
	}

	public void SetUpAxis(String upAxis) {
		if (upAxis.equals("X_UP")) {
			xAxis = "Z";
			yAxis = "X";
			zAxis = "Y";
		} else if (upAxis.equals("Y_UP")) {
			xAxis = "X";
			yAxis = "Y";
			zAxis = "Z";
		} else if (upAxis.equals("Z_UP")) {
			xAxis = "Y";
			yAxis = "Z";
			zAxis = "X";
		}
	}

	public void addGeometry(String id, Geometry geom) {
		geometries.put(id, geom);
	}

	public Geometry getGeometry(String url) {
		return geometries.get(url);
	}

	public void addScene(String id, Model scene) {
		models.put(id, scene);
	}

	private int GetAxisIndex(String axis) {
		if (axis.equals("Y"))
			return 1;
		else if (axis.equals("Z"))
			return 2;
		else
			return 0;
	}

	public int GetXAxisIndex() {
		return GetAxisIndex(xAxis);
	}

	public int GetYAxisIndex() {
		return GetAxisIndex(yAxis);
	}

	public int GetZAxisIndex() {
		return GetAxisIndex(zAxis);
	}

	public void addTransform(String url, Transform trans) {
		transforms.put(url, trans);
	}

	public Model GetModel(String sceneId) {
		Model scene = models.get(sceneId);

		return scene;
	}

	public void assignAnimations(String sceneId) {
		for (Tuple t : animations) {
			String target = (String) t.getFirst();
			Animation anim = (Animation) t.getSecond();

			String geomName = target.substring(
					0,
					(target.contains(".")) ? target.indexOf('.') : (target
							.length() - 1));
			String paramName = (target.contains(".")) ? target.substring(target
					.indexOf('.') + 1) : null;

			if (paramName.equals(xAxis))
				paramName = "X";
			else if (paramName.equals(yAxis))
				paramName = "Y";
			else if (paramName.equals(zAxis))
				paramName = "Z";
			transforms.get(sceneId + "/" + geomName).addAnimation(paramName,
					anim);
		}
	}

	public void addAnimation(String target, Animation animation) {
		animations.add(new Tuple(target, animation));
	}

	public Vec3 toMinecraftCoords(double x, double y, double z) {
		return toMinecraftCoords(Vec3.createVectorHelper(x, y, z));
	}

	public Vec3 toMinecraftCoords(Vec3 vec) {
		if (yAxis.equals("X"))
			return Vec3.createVectorHelper(vec.zCoord, vec.yCoord, vec.zCoord); // TODO:
																				// fix
		else if (yAxis.equals("Y"))
			return Vec3.createVectorHelper(vec.xCoord, vec.zCoord, vec.yCoord); // TODO:
																				// fix
		else if (yAxis.equals("Z"))
			return Vec3.createVectorHelper(vec.yCoord, vec.zCoord, vec.xCoord);
		else
			return null;
	}
}
