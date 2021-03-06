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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.soap.Node;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.ModelFormatException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.hea3ven.colladamodel.client.model.Face;
import com.hea3ven.colladamodel.client.model.Geometry;
import com.hea3ven.colladamodel.client.model.Model;
import com.hea3ven.colladamodel.client.model.animation.Animation;
import com.hea3ven.colladamodel.client.model.animation.AnimationSampler;
import com.hea3ven.colladamodel.client.model.animation.IAnimable;
import com.hea3ven.colladamodel.client.model.animation.KeyFrame;
import com.hea3ven.colladamodel.client.model.interpolation.BezierInterpolation;
import com.hea3ven.colladamodel.client.model.interpolation.Interpolation;
import com.hea3ven.colladamodel.client.model.interpolation.LinearInterpolation;
import com.hea3ven.colladamodel.client.model.transform.Matrix;
import com.hea3ven.colladamodel.client.model.transform.Rotation;
import com.hea3ven.colladamodel.client.model.transform.Scale;
import com.hea3ven.colladamodel.client.model.transform.Transform;
import com.hea3ven.colladamodel.client.model.transform.Translation;

public class ColladaAsset {

	public String xAxis;
	public String zAxis;
	public String yAxis;

	private Element root;
	private XPath xpath = XPathFactory.newInstance().newXPath();

	public ColladaAsset(Document doc) {
		this.root = doc.getDocumentElement();

		String upAxis = GetXPathString("asset/up_axis");
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
		} else
			throw new ModelFormatException("Invalid up axis configuration");

	}

	public ColladaAsset() {
	}

	private String GetXPathString(String path) {
		return GetXPathString(root, path);
	}

	private String GetXPathString(Element node, String path) {
		try {
			return (String) xpath.evaluate(path, node, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			throw new ModelFormatException(
					"Could not get the string for the path '" + path + "'", e);
		}
	}

	private Element GetXPathElement(String path) {
		return GetXPathElement(root, path);
	}

	private Element GetXPathElement(Element node, String path) {
		try {
			return (Element) xpath.evaluate(path, node, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new ModelFormatException(
					"Could not get the element for the path '" + path + "'", e);
		}
	}

	private Collection<Element> GetXPathElementList(String path) {
		return GetXPathElementList(root, path);
	}

	private Collection<Element> GetXPathElementList(Element node, String path) {
		try {
			LinkedList<Element> result = new LinkedList<Element>();
			NodeList nodes = (NodeList) xpath.evaluate(path, node,
					XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++)
				result.add((Element) nodes.item(i));
			return result;
		} catch (XPathExpressionException e) {
			throw new ModelFormatException(
					"Could not get the node list for the path '" + path + "'",
					e);
		}
	}

	private Collection<Element> GetXmlChildren(Element node) {
		LinkedList<Element> result = new LinkedList<Element>();
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				result.add((Element) nodes.item(i));
			}
		}
		return result;
	}

	private String parseURL(String url) {
		return url.substring(1);
	}

	private String[] splitData(String data) {
		return data.trim().split("\\s+");
	}

	private int[] splitDataInt(String data) {
		String[] dataSplit = splitData(data);
		int[] ret = new int[dataSplit.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = Integer.parseInt(dataSplit[i]);
		}
		return ret;
	}

	private double[] splitDataDouble(String data) {
		String[] dataSplit = splitData(data);
		double[] ret = new double[dataSplit.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = Double.parseDouble(dataSplit[i]);
		}
		return ret;
	}

	public String getRootSceneId() {
		return parseURL(GetXPathString("scene/instance_visual_scene/@url"));
	}

	public Model getModel(String id) {
		Element sceneElem = GetXPathElement(String.format(
				"library_visual_scenes/visual_scene[@id='%s']", id));
		return parseScene(sceneElem);
	}

	private Model parseScene(Element sceneElem) {
		Model model = new Model();
		for (Element nodeElem : GetXPathElementList(sceneElem, "node")) {
			Geometry geom = parseSceneNode(nodeElem);
			if (geom != null)
				model.addGeometry(geom);
		}
		return model;
	}

	private Geometry parseSceneNode(Element nodeElem) {
		String geomURL = GetXPathString(nodeElem, "instance_geometry/@url");
		if (geomURL == "")
			return null;
		Geometry geom = getGeometry(parseURL(geomURL));

		String nodeId = nodeElem.getAttribute("id");
		geom.setName(nodeId);

		for (Element child : GetXmlChildren(nodeElem)) {
			Transform trans = null;
			String transId = null;
			if (child.getTagName() == "translate") {
				trans = parseTranslation(child);
				transId = child.getAttribute("sid");
			} else if (child.getTagName() == "rotate") {
				trans = parseRotation(child);
				transId = child.getAttribute("sid");
			} else if (child.getTagName() == "scale") {
				trans = parseScale(child);
				transId = child.getAttribute("sid");
			} else if (child.getTagName() == "matrix") {
				// TODO:
				trans = parseMatrix(child);
				transId = child.getAttribute("sid");
			}

			if (trans != null)
				geom.addTransform(trans);
		}

		return geom;

	}

	private Translation parseTranslation(Element transElem) {
		double[] transData = splitDataDouble(transElem.getTextContent());
		if (transData.length != 3)
			throw new ModelFormatException("Invalid translate data");

		return new Translation(transElem.getAttribute("id"), toMinecraftCoords(
				transData[0], transData[1], transData[2]));
	}

	private Rotation parseRotation(Element rotElem) {
		double[] rotData = splitDataDouble(rotElem.getTextContent());
		if (rotData.length != 4)
			throw new ModelFormatException("Invalid rotate data");

		return new Rotation(rotElem.getAttribute("id"), toMinecraftCoords(
				rotData[0], rotData[1], rotData[2]), rotData[3]);
	}

	private Scale parseScale(Element scaleElem) {
		double[] scaleData = splitDataDouble(scaleElem.getTextContent());
		if (scaleData.length != 3)
			throw new ModelFormatException("Invalid scale data");

		return new Scale(scaleElem.getAttribute("id"), toMinecraftCoords(
				scaleData[0], scaleData[1], scaleData[2]));
	}

	private Matrix parseMatrix(Element matrixElem) {
		double[] matrixData = splitDataDouble(matrixElem.getTextContent());
		if (matrixData.length != 16)
			throw new ModelFormatException("Invalid matrix data");

		double tmp = matrixData[7];
		matrixData[7] = matrixData[11];
		matrixData[11] = -tmp;

		ByteBuffer matrixBytes = ByteBuffer.allocateDirect(16 * 8);
		matrixBytes.order(ByteOrder.nativeOrder());
		matrixBytes.clear();
		DoubleBuffer matrix = matrixBytes.asDoubleBuffer();
		matrix.clear();
		for (int j = 0; j < 4; j++) {
			matrix.put(matrixData[j]);
			matrix.put(matrixData[j + 4]);
			matrix.put(matrixData[j + 8]);
			matrix.put(matrixData[j + 12]);
		}
		return new Matrix(matrixElem.getAttribute("id"),
				toMinecraftCoords(matrix));
	}

	public Geometry getGeometry(String id) {
		Element geomElem = GetXPathElement(String.format(
				"library_geometries/geometry[@id='%s']", id));
		return parseGeometry(geomElem);
	}

	private Geometry parseGeometry(Element geomElem) {
		Geometry geom = new Geometry();
		for (Element meshElem : GetXPathElementList(geomElem, "mesh")) {
			parseMesh(geom, meshElem);
		}
		return geom;
	}

	private void parseMesh(Geometry geom, Element meshElem) {
		for (Element child : GetXmlChildren(meshElem)) {
			if (child.getTagName() == "triangles") {
				parseMeshTriangles(geom, meshElem, child);
			} else if (child.getNodeName() == "polylist") {
				parseMeshPolylist(geom, meshElem, child);
			} else if (child.getNodeName() == "polygons") {
				parseMeshPolygons(geom, meshElem, child);
			}
		}
	}

	private void parseMeshTriangles(Geometry geom, Element meshElem,
			Element triElem) {
		ColladaSource[] dataSrcs = parseMeshInputSources(meshElem, triElem);

		int count = Integer.parseInt(triElem.getAttribute("count"));
		int[] refs = splitDataInt(GetXPathElement(triElem, "p")
				.getTextContent());
		if (refs.length != (count * 9))
			throw new ModelFormatException("Wrong number of data elements");

		for (int q = 0; q < count; q++) {
			Vec3[] vertex = new Vec3[3];
			Vec3[] normal = new Vec3[3];
			Vec3[] texCoords = new Vec3[3];
			for (int r = 0; r < 3; r++) {
				vertex[r] = toMinecraftCoords(dataSrcs[0].getVec3(refs[q * 9
						+ r * 3], "X", "Y", "Z"));
				normal[r] = toMinecraftCoords(dataSrcs[1].getVec3(refs[q * 9
						+ r * 3 + 1], "X", "Y", "Z"));
				texCoords[r] = dataSrcs[2].getVec2(refs[q * 9 + r * 3 + 2],
						"S", "T");
			}
			Face poly = new Face();
			poly.setVertex(vertex, normal, texCoords);
			geom.addFace(poly);
		}
	}

	private ColladaSource[] parseMeshInputSources(Element meshElem,
			Element defElem) {
		ColladaSource[] dataSrcs = new ColladaSource[3];
		String verticesId = parseURL(GetXPathString(defElem,
				"input[@semantic='VERTEX']/@source"));
		String srcId = parseURL(GetXPathString(meshElem,
				String.format("vertices[@id='%s']/input/@source", verticesId)));
		dataSrcs[0] = parseSource(GetXPathElement(meshElem,
				String.format("source[@id='%s']", srcId)));

		String normalsId = parseURL(GetXPathString(defElem,
				"input[@semantic='NORMAL']/@source"));
		dataSrcs[1] = parseSource(GetXPathElement(meshElem,
				String.format("source[@id='%s']", normalsId)));

		String texcoordId = parseURL(GetXPathString(defElem,
				"input[@semantic='TEXCOORD']/@source"));
		dataSrcs[2] = parseSource(GetXPathElement(meshElem,
				String.format("source[@id='%s']", texcoordId)));
		return dataSrcs;
	}

	private void parseMeshPolylist(Geometry geom, Element meshElem,
			Element polylistElem) {
		ColladaSource[] dataSrcs = parseMeshInputSources(meshElem, polylistElem);

		int count = Integer.parseInt(polylistElem.getAttribute("count"));
		int[] vcount = splitDataInt(GetXPathElement(polylistElem, "vcount")
				.getTextContent());
		int[] refs = splitDataInt(GetXPathElement(polylistElem, "p")
				.getTextContent());
		if (vcount.length != count)
			throw new ModelFormatException("Wrong number of data elements");

		int p = 0;
		for (int q = 0; q < vcount.length; q++) {
			Vec3[] vertex = new Vec3[vcount[q]];
			Vec3[] normal = new Vec3[vcount[q]];
			Vec3[] texCoords = new Vec3[vcount[q]];
			for (int r = 0; r < vcount[q]; r++) {
				vertex[r] = toMinecraftCoords(dataSrcs[0].getVec3(refs[p * 3],
						"X", "Y", "Z"));
				normal[r] = toMinecraftCoords(dataSrcs[1].getVec3(
						refs[p * 3 + 1], "X", "Y", "Z"));
				texCoords[r] = dataSrcs[2].getVec2(refs[p * 3 + 2], "S", "T");
				p++;
			}
			Face poly = new Face();
			poly.setVertex(vertex, normal, texCoords);
			geom.addFace(poly);
		}
	}

	private void parseMeshPolygons(Geometry geom, Element meshElem,
			Element polyElem) {
		ColladaSource[] dataSrcs = parseMeshInputSources(meshElem, polyElem);

		int count = Integer.parseInt(polyElem.getAttribute("count"));
		Collection<Element> polysData = GetXPathElementList(polyElem, "p");
		if (polysData.size() != count)
			throw new ModelFormatException("Wrong number of data elements");

		for (Element pElem : polysData) {
			int[] refs = splitDataInt(pElem.getTextContent());
			Vec3[] vertex = new Vec3[refs.length / 3];
			Vec3[] normal = new Vec3[refs.length / 3];
			Vec3[] texCoords = new Vec3[refs.length / 3];
			for (int r = 0; r < refs.length / 3; r++) {
				vertex[r] = toMinecraftCoords(dataSrcs[0].getVec3(refs[r * 3],
						"X", "Y", "Z"));
				normal[r] = toMinecraftCoords(dataSrcs[1].getVec3(
						refs[r * 3 + 1], "X", "Y", "Z"));
				texCoords[r] = dataSrcs[2].getVec2(refs[r * 3 + 2], "S", "T");
			}
			Face poly = new Face();
			poly.setVertex(vertex, normal, texCoords);
			geom.addFace(poly);
		}

	}

	public ColladaSource parseSource(Element srcElem) {

		String id = srcElem.getAttribute("id");

		Element data_array = GetXPathElement(srcElem, "float_array");
		if (data_array == null) {
			data_array = GetXPathElement(srcElem, "Name_array");
		}
		if (data_array == null)
			throw new ModelFormatException(
					"Could not find the data array for the source");

		int data_count;
		try {
			data_count = Integer.parseInt(data_array.getAttribute("count"));
		} catch (NumberFormatException ex) {
			throw new ModelFormatException(
					"Could not parse the count attribute of the <float_array>",
					ex);
		}

		float float_data[] = null;
		String name_data[] = null;
		if (data_array.getNodeName() == "float_array")
			float_data = new float[data_count];
		else if (data_array.getNodeName() == "Name_array")
			name_data = new String[data_count];

		int i = 0;
		String data_string = data_array.getTextContent();
		for (String val : splitData(data_string)) {
			if (data_array.getNodeName() == "float_array")
				float_data[i] = Float.parseFloat(val);
			else if (data_array.getNodeName() == "Name_array")
				name_data[i] = val;
			i++;
			if (i > data_count)
				throw new ModelFormatException("Too many values in the data");
		}
		if (i < data_count - 1)
			throw new ModelFormatException("Not enough values in the data");

		Element accessorNode = GetXPathElement(srcElem,
				"technique_common/accessor");
		int count = 0;
		int stride = 1;
		try {
			count = Integer.parseInt(accessorNode.getAttribute("count"));
			if (accessorNode.getAttribute("stride") != "")
				stride = Integer.parseInt(accessorNode.getAttribute("stride"));
		} catch (NumberFormatException ex) {
			throw new ModelFormatException(
					"Could not parse the count attribute of the <float_array>",
					ex);
		}

		ColladaSource source = null;

		Collection<Element> paramElems = GetXPathElementList(accessorNode,
				"param");
		if (stride == 1) {
			Element paramElem = paramElems.iterator().next();
			if (paramElem.getAttribute("type").equals("float4x4")) {
				float[][] float4x4_data = new float[count][16];
				for (int j = 0; j < count; j++) {
					for (int k = 0; k < 16; k++) {
						float4x4_data[j][k] = float_data[j * 16 + k];
					}

				}
				source = new ColladaSource(id, paramElem.getAttribute("name"),
						float4x4_data);
			} else if (paramElem.getAttribute("type").equals("name")) {
				source = new ColladaSource(id, paramElem.getAttribute("name"),
						name_data);
			}
		}
		if (source == null) {
			String[] params = new String[paramElems.size()];
			i = 0;
			for (Element paramElem : paramElems) {
				params[i++] = paramElem.getAttribute("name");
			}
			source = new ColladaSource(id, params, stride, float_data);
		}
		return source;
	}

	private Animation parseAnimation(Element animElem) {
		List<IAnimable> children = new LinkedList<IAnimable>();
		Element channelNode = GetXPathElement(animElem, "channel");
		if (channelNode != null) {
			String samplerId = parseURL(channelNode.getAttribute("source"));
			Element samplerElem = GetXPathElement(animElem,
					String.format("sampler[@id='%s']", samplerId));
			HashMap<String, ColladaSource> sources = parseAnimationInputSources(
					animElem, samplerElem);

			ColladaSource outputSource = sources.get("OUTPUT");
			List<KeyFrame> frames = new LinkedList<KeyFrame>();
			for (int i = 0; i < sources.get("INPUT").getCount(); i++) {
				int frame = (int) Math.floor(sources.get("INPUT").getDouble(
						"TIME", i));
				String interpName = sources.get("INTERPOLATION")
						.getString(0, i);
				Interpolation interp = null;
				if (interpName.equals("LINEAR")) {

					interp = new LinearInterpolation();
				} else if (interpName.equals("BEZIER")) {
					if (i + 1 < sources.get("INPUT").getCount())
						interp = new BezierInterpolation(sources.get(
								"OUT_TANGENT").getDouble("Y", i), sources.get(
								"IN_TANGENT").getDouble("Y", i + 1));
					else
						interp = new LinearInterpolation();
				} else {
					throw new ModelFormatException(String.format(
							"Invalid interpolation method %s", interpName));
				}
				if (outputSource.getType() == ColladaSourceType.FLOAT) {
					KeyFrame keyFrame = new KeyFrame(frame,
							outputSource.getDouble(0, i), interp);
					frames.add(keyFrame);
				} else if (outputSource.getType() == ColladaSourceType.FLOAT4x4) {
					KeyFrame keyFrame = new KeyFrame(frame,
							outputSource.getFloat4x4(0, i), interp);
					frames.add(keyFrame);
				}
			}

			String[] targetParts = channelNode.getAttribute("target").split(
					"[/.]");
			AnimationSampler sampler = new AnimationSampler(targetParts[0],
					targetParts[1], (targetParts.length == 3) ? targetParts[2]
							: null, frames);
			children.add(sampler);
		}
		for (Element subAnimationElem : GetXPathElementList(animElem,
				"animation")) {
			children.add(parseAnimation(subAnimationElem));
		}

		Animation animation = new Animation(animElem.getAttribute("id"),
				children);
		return animation;
	}

	private HashMap<String, ColladaSource> parseAnimationInputSources(
			Element animElem, Element samplerElem) {
		HashMap<String, ColladaSource> sources = new HashMap<String, ColladaSource>();
		for (Element inputElem : GetXPathElementList(samplerElem, "input")) {
			sources.put(
					inputElem.getAttribute("semantic"),
					parseSource(GetXPathElement(animElem, String.format(
							"source[@id='%s']",
							parseURL(inputElem.getAttribute("source"))))));
		}
		return sources;
	}

	private String toMinecraftParam(String param) {
		if (param.equals(xAxis))
			return "X";
		else if (param.equals(yAxis))
			return "Y";
		else if (param.equals(zAxis))
			return "Z";
		else
			return param;
	}

	public Vec3 toMinecraftCoords(double x, double y, double z) {
		return toMinecraftCoords(Vec3.createVectorHelper(x, y, z));
	}

	public Vec3 toMinecraftCoords(Vec3 vec) {
		if (yAxis.equals("X"))
			return Vec3.createVectorHelper(vec.zCoord, vec.xCoord, vec.yCoord);
		else if (yAxis.equals("Y"))
			return Vec3.createVectorHelper(vec.xCoord, vec.yCoord, vec.zCoord);
		else if (yAxis.equals("Z"))
			return Vec3.createVectorHelper(vec.yCoord, vec.zCoord, vec.xCoord);
		else
			return null;
	}

	public DoubleBuffer toMinecraftCoords(DoubleBuffer matrix) {
		return matrix;
	}
}
