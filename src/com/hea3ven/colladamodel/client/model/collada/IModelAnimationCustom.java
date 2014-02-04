package com.hea3ven.colladamodel.client.model.collada;

import net.minecraftforge.client.model.IModelCustom;

public interface IModelAnimationCustom extends IModelCustom {
	String getType();

	double getAnimationLength();

	void renderAnimationAll(double time);

	void renderAnimationOnly(double time, String... groupNames);

	void renderAnimationPart(double time, String partName);

	void renderAnimationAllExcept(double time, String... excludedGroupNames);
}
