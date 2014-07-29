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

package com.hea3ven.colladamodel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.client.model.AdvancedModelLoader;

import com.hea3ven.colladamodel.client.model.ModelManager;
import com.hea3ven.colladamodel.client.model.collada.ColladaModelLoader;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "colladamodel", version = "1.0a1", dependencies = "required-after:Forge@[10.12.0.1024,)")
public class ModColladaModel {

	@Instance("colladamodel")
	public static ModColladaModel instance;

	private static ModelManager modelManager;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		AdvancedModelLoader.registerModelHandler(new ColladaModelLoader());
		if (event.getSide() == Side.CLIENT) {
			modelManager = new ModelManager();

			((IReloadableResourceManager) Minecraft.getMinecraft()
					.getResourceManager()).registerReloadListener(modelManager);
		}
	}

	public static ModelManager getModelManager() {
		return modelManager;
	}

}
