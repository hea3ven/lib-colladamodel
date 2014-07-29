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
