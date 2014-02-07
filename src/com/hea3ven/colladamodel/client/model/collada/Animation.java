package com.hea3ven.colladamodel.client.model.collada;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Animation {
	private List<KeyFrame> frames;

	public Animation() {
		frames = new LinkedList<KeyFrame>();
	}

	public void addKeyFrame(KeyFrame keyFrame) {
		frames.add(keyFrame);
	}

	public double getValue(double time) {
		KeyFrame prevFrame = null;
		KeyFrame nextFrame = null;
		for (Iterator<KeyFrame> i = frames.iterator(); i.hasNext();) {
			nextFrame = i.next();
			if (time <= nextFrame.getFrame())
				break;
			prevFrame = nextFrame;
		}
		if (prevFrame == null)
			return nextFrame.getValue();
		if (prevFrame == nextFrame)
			return nextFrame.getValue();

		return prevFrame.interpolate(time, nextFrame);
	}

	public double getAnimationLength() {
		return frames.get(frames.size() - 1).getFrame();
	}
}
