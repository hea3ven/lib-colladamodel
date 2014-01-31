package com.hea3ven.colladamodel;

import net.minecraftforge.client.model.AdvancedModelLoader;

import com.hea3ven.colladamodel.client.model.collada.ColladaModelLoader;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "colladamodel")
public class ModColladaModel {

	@Instance("colladamodel")
	public static ModColladaModel instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		AdvancedModelLoader.registerModelHandler(new ColladaModelLoader());
	}

}
