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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

public class Geometry {

	private String name;
	private List<Transform> transforms;
	private List<Face> faces;
	private HashMap<String, Transform> transformsById;

	public Geometry() {
		this.name = null;
		this.transforms = new LinkedList<Transform>();
		this.faces = new LinkedList<Face>();
		this.transformsById = new HashMap<String, Transform>();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addTransform(String id, Transform transform) {
		this.transforms.add(transform);
		this.transformsById.put(id, transform);
	}

	public Transform getTransform(String transId) {
		return transformsById.get(transId);
	}

	public void addFace(Face face) {
		faces.add(face);
	}

	public void render(Tessellator tessellator) {
		GL11.glPushMatrix();

		for (Transform trans : transforms) {
			trans.apply();
		}

		for (Face face : faces) {
			face.render(tessellator);
		}

		GL11.glPopMatrix();
	}

	public void renderAnimation(Tessellator tessellator, double frame) {
		GL11.glPushMatrix();

		for (Transform trans : transforms) {
			trans.applyAnimation(frame);
		}

		for (Face face : faces) {
			face.render(tessellator);
		}

		GL11.glPopMatrix();
	}

    public double getAnimationLength()
    {
        double animationLength = 0;
        for (Transform trans : transforms)
        {
            if(trans.getAnimationLength() > animationLength)
                animationLength = trans.getAnimationLength();
        }
        return animationLength;
    }

}
