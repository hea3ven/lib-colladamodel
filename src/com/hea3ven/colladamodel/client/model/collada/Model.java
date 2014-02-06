package com.hea3ven.colladamodel.client.model.collada;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.renderer.Tessellator;

public class Model implements IModelAnimationCustom {

	private Map<String, Geometry> geometries;
	private double animationLength;

	public Model() {
		geometries = new HashMap<String, Geometry>();
	}

	@Override
	public String getType() {
		return "dae";
	}

	public void addGeometry(Geometry geom) {
		geometries.put(geom.getName(), geom);
	}

	@Override
	public void renderAll() {
		Tessellator tessellator = Tessellator.instance;

		for (Geometry geom : geometries.values()) {
			geom.render(tessellator);
		}
	}

	@Override
	public void renderOnly(String... geometriesNames) {
		Tessellator tessellator = Tessellator.instance;
		for (String geometryName : geometriesNames) {
			geometries.get(geometryName).render(tessellator);
		}
	}

	@Override
	public void renderPart(String partName) {
		Tessellator tessellator = Tessellator.instance;
		geometries.get(partName).render(tessellator);
	}

	@Override
	public void renderAllExcept(String... excludedGroupNames) {
		Set<String> excludedSet = new HashSet<String>(
				Arrays.asList(excludedGroupNames));
		Tessellator tessellator = Tessellator.instance;
		for (Geometry geometry : geometries.values()) {
			if (!excludedSet.contains(geometry.getName()))
				geometry.render(tessellator);
		}

	}

	@Override
	public void renderAnimationAll(double time) {
		Tessellator tessellator = Tessellator.instance;
		for (Geometry geom : geometries.values()) {
			geom.renderAnimation(tessellator, time);
		}
	}

	@Override
	public void renderAnimationOnly(double time, String... geometriesNames) {
		Tessellator tessellator = Tessellator.instance;
		for (String geometryName : geometriesNames) {
			geometries.get(geometryName).renderAnimation(tessellator, time);
		}
	}

	@Override
	public void renderAnimationPart(double time, String partName) {
		Tessellator tessellator = Tessellator.instance;
		geometries.get(partName).renderAnimation(tessellator, time);
	}

	@Override
	public void renderAnimationAllExcept(double time,
			String... excludedGroupNames) {
		Set<String> excludedSet = new HashSet<String>(
				Arrays.asList(excludedGroupNames));
		Tessellator tessellator = Tessellator.instance;
		for (Geometry geometry : geometries.values()) {
			if (!excludedSet.contains(geometry.getName()))
				geometry.renderAnimation(tessellator, time);
		}
	}

	@Override
	public double getAnimationLength() {
		if (animationLength == -1)
			calculateAnimationLength();
		return animationLength;
	}

	private void calculateAnimationLength() {
		animationLength = 0;
		for (Geometry geom : geometries.values()) {
			if (geom.getAnimationLength() > animationLength)
				animationLength = geom.getAnimationLength();
		}
	}
}
