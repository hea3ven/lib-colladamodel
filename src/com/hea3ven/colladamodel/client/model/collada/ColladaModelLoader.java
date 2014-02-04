package com.hea3ven.colladamodel.client.model.collada;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.IModelCustomLoader;
import net.minecraftforge.client.model.ModelFormatException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ColladaModelLoader implements IModelCustomLoader {

	private XPath xpath = XPathFactory.newInstance().newXPath();

	@Override
	public String getType() {
		return "COLLADA model";
	}

	private static final String[] types = { "dae", "DAE" };

	@Override
	public String[] getSuffixes() {
		return types;
	}

	public static void init() {
		if (!AdvancedModelLoader.getSupportedSuffixes().contains("dae"))
			AdvancedModelLoader.registerModelHandler(new ColladaModelLoader());
	}

	@Override
	public IModelCustom loadInstance(ResourceLocation resource)
			throws ModelFormatException {
		IResource res;
		try {
			res = Minecraft.getMinecraft().getResourceManager()
					.getResource(resource);
		} catch (IOException e) {
			throw new ModelFormatException("IO Exception reading model format",
					e);
		}
		return LoadFromStream(res.getInputStream());
	}

	// @Override
	public IModelAnimationCustom loadAnimationInstance(ResourceLocation resource)
			throws ModelFormatException {
		IResource res;
		try {
			res = Minecraft.getMinecraft().getResourceManager()
					.getResource(resource);
		} catch (IOException e) {
			throw new ModelFormatException("IO Exception reading model format",
					e);
		}
		return LoadFromStream(res.getInputStream());
	}

	private Model LoadFromStream(InputStream stream) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			return LoadFromXml(builder.parse(stream));
		} catch (IOException e) {
			throw new ModelFormatException("IO Exception reading model format",
					e);
		} catch (ParserConfigurationException e) {
			throw new ModelFormatException(
					"Xml Parser Exception reading model format", e);
		} catch (SAXException e) {
			throw new ModelFormatException(
					"Xml Parsing Exception reading model format", e);
		}
	}

	private Model LoadFromXml(Document doc) {

		ColladaAsset asset = new ColladaAsset();

		String rootScene = null;
		Element root = doc.getDocumentElement();
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) children.item(i);
				if (child.getNodeName() == "asset")
					LoadAssetData(asset, child);
				else if (child.getNodeName() == "library_geometries")
					LoadGeometries(asset, child);
				else if (child.getNodeName() == "library_visual_scenes")
					LoadVisualScenes(asset, child);
				else if (child.getNodeName() == "library_animations")
					LoadAnimations(asset, child);
				else if (child.getNodeName() == "scene")
					rootScene = parseURL(((Element) child.getChildNodes().item(
							1)).getAttribute("url"));
			}
		}

		if (rootScene == null)
			throw new ModelFormatException("No root scene");

		asset.assignAnimations(rootScene);
		Model model = asset.GetModel(rootScene);
		return model;
	}

	private void LoadAssetData(ColladaAsset asset, Element assetNode) {

		Element upAxisElem = GetXPathElement(assetNode, "up_axis");
		asset.SetUpAxis(upAxisElem.getTextContent().toUpperCase());
	}

	private void LoadGeometries(ColladaAsset asset, Element geomsNode) {
		Collection<Element> geometries = GetXPathElementList(geomsNode,
				"geometry");
		for (Element geomElem : geometries) {
			LoadGeometry(asset, geomElem);
		}
	}

	private void LoadGeometry(ColladaAsset asset, Element geomElem) {
		Geometry geom = new Geometry();

		String id = LoadNodeId(geomElem);
		geom.setName(geomElem.getAttribute("name"));
		if (geom.getName().isEmpty())
			geom.setName(id);

		for (Element meshElem : GetXPathElementList(geomElem, "mesh")) {
			LoadMesh(asset, geom, meshElem);
		}
		asset.addGeometry(id, geom);
	}

	private String LoadNodeId(Element node) {
		String id = node.getAttribute("id");
		if (id == "")
			throw new ModelFormatException("Invalid node, missing id attribute");
		return id;
	}

	private void LoadMesh(ColladaAsset asset, Geometry geom, Element meshNode) {

		HashMap<String, ColladaSource> sources = new HashMap<String, ColladaSource>();

		NodeList children = meshNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) children.item(i);
				if (child.getNodeName() == "source") {
					ColladaSource source = LoadSource(child);
					sources.put(source.getId(), source);
				} else if (child.getNodeName() == "vertices") {
					String vertSrc = GetXPathString(child, "input/@source");
					sources.put(child.getAttribute("id"),
							sources.get(parseURL(vertSrc)));
				} else if (child.getNodeName() == "polylist"
						|| child.getNodeName() == "polygons"
						|| child.getNodeName() == "triangles") {
					ColladaSource vertexSrc = null;
					Integer vertexOffset = null;
					ColladaSource normalSrc = null;
					Integer normalOffset = null;
					ColladaSource texcoordSrc = null;
					Integer texcoordOffset = null;

					for (Element inputNode : GetXPathElementList(child, "input")) {
						if (inputNode.getAttribute("semantic").equals("VERTEX")) {
							vertexSrc = sources.get(parseURL(inputNode
									.getAttribute("source")));
							vertexOffset = Integer.parseInt(inputNode
									.getAttribute("offset"));
						} else if (inputNode.getAttribute("semantic").equals(
								"NORMAL")) {
							normalSrc = sources.get(parseURL(inputNode
									.getAttribute("source")));
							normalOffset = Integer.parseInt(inputNode
									.getAttribute("offset"));
						} else if (inputNode.getAttribute("semantic").equals(
								"TEXCOORD")) {
							texcoordSrc = sources.get(parseURL(inputNode
									.getAttribute("source")));
							texcoordOffset = Integer.parseInt(inputNode
									.getAttribute("offset"));
						}
					}

					if (child.getNodeName() == "triangles") {
						int count = Integer.parseInt(child
								.getAttribute("count"));
						int[] refs = splitDataInt(GetXPathElement(child, "p")
								.getTextContent());
						if (refs.length != (count * 9))
							throw new ModelFormatException(
									"Wrong number of data elements");

						int p = 0;
						for (int q = 0; q < count; q++) {
							Vec3[] vertex = new Vec3[3];
							Vec3[] normal = new Vec3[3];
							Vec3[] texCoords = new Vec3[3];
							for (int r = 0; r < 3; r++) {
								vertex[r] = asset.toMinecraftCoords(vertexSrc
										.getVec3(refs[q * 9 + r * 3],
												asset.yAxis, asset.xAxis,
												asset.zAxis));
								normal[r] = asset.toMinecraftCoords(normalSrc
										.getVec3(refs[q * 9 + r * 3 + 1],
												asset.yAxis, asset.xAxis,
												asset.zAxis));
								texCoords[r] = texcoordSrc.getVec2(refs[q * 9
										+ r * 3 + 2], "S", "T");
								p++;
							}
							Face poly = new Face();
							poly.setVertex(vertex, normal, texCoords);
							geom.addFace(poly);
						}
					} else if (child.getNodeName() == "polylist") {
						int count = Integer.parseInt(child
								.getAttribute("count"));
						int[] vcount = splitDataInt(GetXPathElement(child,
								"vcount").getTextContent());
						int[] refs = splitDataInt(GetXPathElement(child, "p")
								.getTextContent());
						if (vcount.length != count)
							throw new ModelFormatException(
									"Wrong number of data elements");

						int p = 0;
						for (int q = 0; q < vcount.length; q++) {
							Vec3[] vertex = new Vec3[vcount[q]];
							Vec3[] normal = new Vec3[vcount[q]];
							Vec3[] texCoords = new Vec3[vcount[q]];
							for (int r = 0; r < vcount[q]; r++) {
								vertex[r] = asset.toMinecraftCoords(vertexSrc
										.getVec3(refs[p * 3], asset.yAxis,
												asset.xAxis, asset.zAxis));
								normal[r] = asset.toMinecraftCoords(normalSrc
										.getVec3(refs[p * 3 + 1], asset.yAxis,
												asset.xAxis, asset.zAxis));
								texCoords[r] = texcoordSrc.getVec2(
										refs[p * 3 + 2], "S", "T");
								p++;
							}
							Face poly = new Face();
							poly.setVertex(vertex, normal, texCoords);
							geom.addFace(poly);
						}
					} else if (child.getNodeName() == "polygons") {
						int count = Integer.parseInt(child
								.getAttribute("count"));
						Collection<Element> polysData = GetXPathElementList(
								child, "p");
						if (polysData.size() != count)
							throw new ModelFormatException(
									"Wrong number of data elements");

						for (Element pElem : polysData) {
							int[] refs = splitDataInt(pElem.getTextContent());
							Vec3[] vertex = new Vec3[refs.length / 3];
							Vec3[] normal = new Vec3[refs.length / 3];
							Vec3[] texCoords = new Vec3[refs.length / 3];
							for (int r = 0; r < refs.length / 3; r++) {
								vertex[r] = asset.toMinecraftCoords(vertexSrc
										.getVec3(refs[r * 3], asset.yAxis,
												asset.xAxis, asset.zAxis));
								normal[r] = asset.toMinecraftCoords(normalSrc
										.getVec3(refs[r * 3 + 1], asset.yAxis,
												asset.xAxis, asset.zAxis));
								texCoords[r] = texcoordSrc.getVec2(
										refs[r * 3 + 2], "S", "T");
							}
							Face poly = new Face();
							poly.setVertex(vertex, normal, texCoords);
							geom.addFace(poly);
						}
					}
				}
			}
		}
	}

	private ColladaSource LoadSource(Element sourceNode) {
		ColladaSource src = new ColladaSource();

		src.setId(sourceNode.getAttribute("id"));

		Element data_array = GetXPathElement(sourceNode, "float_array");
		if (data_array == null) {
			data_array = GetXPathElement(sourceNode, "name_array");
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
		else if (data_array.getNodeName() == "name_array")
			name_data = new String[data_count];

		int i = 0;
		String data_string = data_array.getTextContent();
		for (String val : splitData(data_string)) {
			if (data_array.getNodeName() == "float_array")
				float_data[i] = Float.parseFloat(val);
			else if (data_array.getNodeName() == "name_array")
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

		Element accessorNode = GetXPathElement(sourceNode,
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

	private void LoadVisualScenes(ColladaAsset asset, Element visualScenesNode) {

		for (Element sceneElem : GetXPathElementList(visualScenesNode,
				"visual_scene")) {
			LoadVisualScene(asset, sceneElem);
		}
	}

	private void LoadVisualScene(ColladaAsset asset, Element visualSceneNode) {
		Model model = new Model();

		String sceneId = LoadNodeId(visualSceneNode);

		for (Element nodeElem : GetXPathElementList(visualSceneNode, "node")) {
			LoadNode(asset, sceneId, model, nodeElem);
		}
		asset.addScene(sceneId, model);
	}

	private void LoadNode(ColladaAsset asset, String sceneId, Model scene,
			Element nodeNode) {
		String nodeId = nodeNode.getAttribute("id");

		HashMap<String, ColladaSource> sources = new HashMap<String, ColladaSource>();
		List<Transform> transforms = new LinkedList<Transform>();
		Geometry geom = null;

		NodeList children = nodeNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) children.item(i);
				if (child.getNodeName() == "translate") {
					String[] transData = splitData(child.getTextContent());
					if (transData.length != 3)
						throw new ModelFormatException(
								"Invalid translate content for the node");

					String transformId = child.getAttribute("sid");
					Transform trans = new Translation(asset.toMinecraftCoords(
							Double.parseDouble(transData[0]),
							Double.parseDouble(transData[1]),
							Double.parseDouble(transData[2])));
					transforms.add(trans);
					asset.addTransform(sceneId + "/" + nodeId + "/"
							+ transformId, trans);
				} else if (child.getNodeName() == "rotate") {
					String[] rotateData = splitData(child.getTextContent());
					if (rotateData.length != 4)
						throw new ModelFormatException(
								"Invalid rotate content for the node");

					String transformId = child.getAttribute("sid");
					Transform trans = new Rotation(asset.toMinecraftCoords(
							Double.parseDouble(rotateData[0]),
							Double.parseDouble(rotateData[1]),
							Double.parseDouble(rotateData[2])),
							Double.parseDouble(rotateData[3]));
					transforms.add(trans);
					asset.addTransform(sceneId + "/" + nodeId + "/"
							+ transformId, trans);
				} else if (child.getNodeName() == "scale") {
					String[] scaleData = splitData(child.getTextContent());
					if (scaleData.length != 3)
						throw new ModelFormatException(
								"Invalid scale content for the node");

					String transformId = child.getAttribute("sid");
					Transform trans = new Scale(asset.toMinecraftCoords(
							Double.parseDouble(scaleData[0]),
							Double.parseDouble(scaleData[1]),
							Double.parseDouble(scaleData[2])));
					transforms.add(trans);
					asset.addTransform(sceneId + "/" + nodeId + "/"
							+ transformId, trans);
				} else if (child.getNodeName() == "matrix") {
					// String[] matrixData = splitData(child.getTextContent());
					// if (matrixData.length != 16)
					// throw new
					// ModelFormatException("Invalid matrix content for the node");
					//
					// ByteBuffer matrixBytes = ByteBuffer.allocateDirect(16 *
					// 8);
					// matrixBytes.clear();
					// DoubleBuffer matrix = matrixBytes.asDoubleBuffer();
					// matrix.clear();
					// for (int j = 0; j < 4; j++) {
					// matrix.put(Double.parseDouble(matrixData[j]));
					// matrix.put(Double.parseDouble(matrixData[j + 4]));
					// matrix.put(Double.parseDouble(matrixData[j + 8]));
					// matrix.put(Double.parseDouble(matrixData[j + 12]));
					// }
					// node.addTransform(new Matrix(child.getAttribute("sid"),
					// matrix));
				} else if (child.getNodeName() == "instance_geometry") {
					String url = parseURL(child.getAttribute("url"));
					geom = asset.getGeometry(url);
				}
			}
		}
		if (geom != null) {
			for (Transform transform : transforms) {
				geom.addTransform(transform);
			}
			scene.addGeometry(geom);
		}
	}

	private void LoadAnimations(ColladaAsset asset, Element animationsNode) {
		NodeList children = animationsNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) children.item(i);
				if (child.getNodeName() == "animation") {
					LoadAnimation(asset, child);
				}
			}
		}
	}

	private void LoadAnimation(ColladaAsset asset, Element animationNode) {

		Animation animation = new Animation();

		Element channelNode;
		channelNode = GetXPathElement(animationNode, "channel");
		if (channelNode != null) {
			String target = channelNode.getAttribute("target");

			String samplerPath = String.format("sampler[@id='%s']",
					parseURL(channelNode.getAttribute("source")));
			String inputId = parseURL(GetXPathString(animationNode, samplerPath
					+ "/input[@semantic='INPUT']/@source"));
			String outputId = parseURL(GetXPathString(animationNode,
					samplerPath + "/input[@semantic='OUTPUT']/@source"));
			// String interpolationId = parseURL(GetXPathString(xpath,
			// animationNode,
			// samplerPath + "/input[@semantic='INTERPOLATION']/@source"));
			// String inTangentId = parseURL(GetXPathString(xpath,
			// animationNode,
			// samplerPath + "/input[@semantic='IN_TANGENT']/@source"));
			// String outTangentId = parseURL(GetXPathString(xpath,
			// animationNode,
			// samplerPath + "/input[@semantic='OUT_TANGENT']/@source"));

			ColladaSource inputSource = LoadSource(GetXPathElement(
					animationNode, String.format("source[@id='%s']", inputId)));
			ColladaSource outputSource = LoadSource(GetXPathElement(
					animationNode, String.format("source[@id='%s']", outputId)));
			// ColladaSource interpolationSource = new ColladaSource();
			// interpolationSource.LoadFrom(GetXPathElement(xpath,
			// animationNode,
			// "source[@id='" + interpolationId + "']"));
			// ColladaSource inTangentSource = new ColladaSource();
			// inTangentSource.LoadFrom(GetXPathElement(xpath, animationNode,
			// "source[@id='" + inTangentId + "']"));
			// ColladaSource outTangentSource = new ColladaSource();
			// outTangentSource.LoadFrom(GetXPathElement(xpath, animationNode,
			// "source[@id='" + outTangentId + "']"));

			for (int i = 0; i < inputSource.getCount(); i++) {
				int frame = (int) Math
						.floor(inputSource.getDouble("TIME", i) * 20);
				KeyFrame keyFrame = new KeyFrame(frame, outputSource.getDouble(
						0, i));
				animation.addKeyFrame(keyFrame);
			}
			asset.addAnimation(target, animation);
		}
		for (Element subAnimationElem : GetXPathElementList(animationNode,
				"animation")) {
			LoadAnimation(asset, subAnimationElem);
		}
	}

	private String GetXPathString(Element node, String path) {
		try {
			return (String) xpath.evaluate(path, node, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			throw new ModelFormatException(
					"Could not get the string for the path '" + path + "'", e);
		}
	}

	private Element GetXPathElement(Element node, String path) {
		try {
			return (Element) xpath.evaluate(path, node, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new ModelFormatException(
					"Could not get the element for the path '" + path + "'", e);
		}
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

	private String parseURL(String url) {
		return url.substring(1);
	}

}
