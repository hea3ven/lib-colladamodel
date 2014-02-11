package com.hea3ven.colladamodel.client.model.collada;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.IModelCustomLoader;
import net.minecraftforge.client.model.ModelFormatException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ColladaModelLoader implements IModelCustomLoader {

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
		ColladaAsset asset = new ColladaAsset(doc);
		return asset.getModel(asset.getRootSceneId());
	}

}
