package com.hea3ven.colladamodel.client.model.collada;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

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
			model.addGeometry(geom);
		}
		for (Element animElem : GetXPathElementList("library_animations/animation")) {
			parseAnimation(model, animElem);
		}
		return model;
	}

	private Geometry parseSceneNode(Element nodeElem) {
		String geomId = parseURL(GetXPathString(nodeElem,
				"instance_geometry/@url"));
		Geometry geom = getGeometry(geomId);

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
				// trans = parseScale(child);
				// transId = child.getAttribute("sid");
			}

			if (trans != null)
				geom.addTransform(transId, trans);
		}

		return geom;

	}

	private Translation parseTranslation(Element transElem) {
		double[] transData = splitDataDouble(transElem.getTextContent());
		if (transData.length != 3)
			throw new ModelFormatException("Invalid translate data");

		return new Translation(toMinecraftCoords(transData[0], transData[1],
				transData[2]));
	}

	private Rotation parseRotation(Element rotElem) {
		double[] rotData = splitDataDouble(rotElem.getTextContent());
		if (rotData.length != 4)
			throw new ModelFormatException("Invalid rotate data");

		return new Rotation(toMinecraftCoords(rotData[0], rotData[1],
				rotData[2]), rotData[3]);
	}

	private Scale parseScale(Element scaleElem) {
		double[] scaleData = splitDataDouble(scaleElem.getTextContent());
		if (scaleData.length != 3)
			throw new ModelFormatException("Invalid translate data");

		return new Scale(toMinecraftCoords(scaleData[0], scaleData[1],
				scaleData[2]));
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

	private ColladaSource parseSource(Element srcElem) {
		ColladaSource src = new ColladaSource();

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

		if (data_array.getNodeName() == "float_array")
			src.setData(float_data);
		else if (data_array.getNodeName() == "name_array")
			src.setData(name_data);

		Element accessorNode = GetXPathElement(srcElem,
				"technique_common/accessor");
		try {
			src.setCount(Integer.parseInt(accessorNode.getAttribute("count")));
			if (accessorNode.getAttribute("stride") != "")
				src.setStride(Integer.parseInt(accessorNode
						.getAttribute("stride")));
			else
				src.setStride(1);
		} catch (NumberFormatException ex) {
			throw new ModelFormatException(
					"Could not parse the count attribute of the <float_array>",
					ex);
		}

		Collection<Element> paramElems = GetXPathElementList(accessorNode,
				"param");
		String[] params = new String[paramElems.size()];
		i = 0;
		for (Element paramElem : paramElems) {
			params[i++] = paramElem.getAttribute("name");
		}

		src.setParams(params);
		return src;
	}

	private void parseAnimation(Model model, Element animElem) {
		Element channelNode = GetXPathElement(animElem, "channel");
		if (channelNode != null) {
			String samplerId = parseURL(channelNode.getAttribute("source"));
			Element samplerElem = GetXPathElement(animElem,
					String.format("sampler[@id='%s']", samplerId));
			HashMap<String, ColladaSource> sources = parseAnimationInputSources(
					animElem, samplerElem);

			Animation animation = new Animation();
			for (int i = 0; i < sources.get("INPUT").getCount(); i++) {
				int frame = (int) Math.floor(sources.get("INPUT").getDouble(
						"TIME", i) * 20);
				KeyFrame keyFrame = new KeyFrame(frame, sources.get("OUTPUT")
						.getDouble(0, i), new LinearInterpolation());
				animation.addKeyFrame(keyFrame);
			}

			String[] targetParts = channelNode.getAttribute("target").split(
					"[/.]");
			Transform trans = model.getGeometry(targetParts[0]).getTransform(
					targetParts[1]);
			if (targetParts.length == 3)
				trans.setAnimation(toMinecraftParam(targetParts[2]), animation);
			else
				trans.setAnimation(null, animation);
		}
		for (Element subAnimationElem : GetXPathElementList(animElem,
				"animation")) {
			parseAnimation(model, subAnimationElem);
		}

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
}
