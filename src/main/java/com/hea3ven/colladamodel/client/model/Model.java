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

package com.hea3ven.colladamodel.client.model;

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

	public Geometry getGeometry(String geomId) {
		return geometries.get(geomId);
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

	public void renderAnimationAll(double time) {
		Tessellator tessellator = Tessellator.instance;
		for (Geometry geom : geometries.values()) {
			geom.renderAnimation(tessellator, time);
		}
	}

	public void renderAnimationOnly(double time, String... geometriesNames) {
		Tessellator tessellator = Tessellator.instance;
		for (String geometryName : geometriesNames) {
			geometries.get(geometryName).renderAnimation(tessellator, time);
		}
	}

	public void renderAnimationPart(double time, String partName) {
		Tessellator tessellator = Tessellator.instance;
		geometries.get(partName).renderAnimation(tessellator, time);
	}

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
}
