package com.hea3ven.colladamodel.client.model.collada;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

public class Geometry {

	private String name;
	private List<Transform> transforms;
	private List<Face> faces;

	public Geometry() {
		this.name = null;
		this.transforms = new LinkedList<Transform>();
		this.faces = new LinkedList<Face>();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addTransform(Transform transform) {
		this.transforms.add(transform);
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
