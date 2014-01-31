package com.hea3ven.colladamodel.client.model.collada;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.model.IModelCustom;

public class Model implements IModelCustom {

	private Map<String, Geometry> geometries;

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
		// for (String geometryName : geometriesNames) {
		// geometries.get(geometryName).render(tessellator);
		// }
	}

	@Override
	public void renderPart(String partName) {
		Tessellator tessellator = Tessellator.instance;
		// geometries.get(partName).render(tessellator);
	}

	@Override
	public void renderAllExcept(String... excludedGroupNames) {
		Set<String> excludedSet = new HashSet<String>(
				Arrays.asList(excludedGroupNames));
		Tessellator tessellator = Tessellator.instance;
		// for (Geometry geometry : geometries.values()) {
		// if (!excludedSet.contains(geometry.getId()))
		// geometry.render(tessellator);
		// }

	}

	public void renderAnimationAll(int frame) {
		Tessellator tessellator = Tessellator.instance;
		for (Geometry geom : geometries.values()) {
			geom.renderAnimation(tessellator, frame);
		}
	}

}
