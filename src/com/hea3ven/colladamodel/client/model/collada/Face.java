package com.hea3ven.colladamodel.client.model.collada;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

public class Face {

	private Vec3[] vertex;
	private Vec3[] vertexNormals;
	private Vec3[] vertexTexCoord;

	public Face() {
		vertex = null;
		vertexNormals = null;
		vertexTexCoord = null;
	}

	public void render(Tessellator tessellator) {

		tessellator.startDrawing(GL11.GL_POLYGON);

		Vec3 faceNormal = calculateFaceNormal();
		tessellator.setNormal((float) -faceNormal.xCoord,
				(float) -faceNormal.yCoord, (float) -faceNormal.zCoord);

		float averageU = 0F;
		float averageV = 0F;

		for (int i = 0; i < vertexTexCoord.length; ++i) {
			averageU += vertexTexCoord[i].xCoord;
			averageV += vertexTexCoord[i].yCoord;
		}

		averageU = averageU / vertexTexCoord.length;
		averageV = averageV / vertexTexCoord.length;

		float offsetU, offsetV;

		for (int i = 0; i < vertex.length; i++) {
			offsetU = 0.0005F;
			offsetV = 0.0005F;

			if (vertexTexCoord[i].xCoord > averageU) {
				offsetU = -offsetU;
			}
			if (vertexTexCoord[i].yCoord > averageV) {
				offsetV = -offsetV;
			}

			tessellator.addVertexWithUV(vertex[i].xCoord, vertex[i].yCoord,
					vertex[i].zCoord, vertexTexCoord[i].xCoord + offsetU, 1
							- vertexTexCoord[i].yCoord - offsetV);
		}

		tessellator.draw();
	}

	private Vec3 calculateFaceNormal() {
		// Vec3 v1 = Vec3.createVectorHelper(vertex[1].xCoord -
		// vertex[0].xCoord,
		// vertex[1].yCoord - vertex[0].yCoord,
		// vertex[1].zCoord - vertex[0].zCoord);
		// Vec3 v2 = Vec3.createVectorHelper(vertex[2].xCoord -
		// vertex[0].xCoord,
		// vertex[2].yCoord - vertex[0].yCoord,
		// vertex[2].zCoord - vertex[0].zCoord);
		// Vec3 normalVector = null;
		//
		// return v1.crossProduct(v2).normalize();
		double sumX = 0;
		double sumY = 0;
		double sumZ = 0;
		for (int i = 0; i < vertexNormals.length; i++) {
			sumX += vertexNormals[i].xCoord;
			sumY += vertexNormals[i].yCoord;
			sumZ += vertexNormals[i].zCoord;
		}
		return Vec3.createVectorHelper(sumX / vertexNormals.length, sumY
				/ vertexNormals.length, sumZ / vertexNormals.length);
	}

	public void setVertex(Vec3[] vertex, Vec3[] normal, Vec3[] texCoords) {
		this.vertex = vertex;
		this.vertexNormals = normal;
		this.vertexTexCoord = texCoords;
	}
}
