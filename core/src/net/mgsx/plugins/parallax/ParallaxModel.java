package net.mgsx.plugins.parallax;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

import net.mgsx.core.storage.Storable;

public class ParallaxModel implements Component, Storable
{
	public float rateX = 1, rateY = 1;
	public Vector3 cameraOrigin = new Vector3();
	public Vector3 objectOrigin = new Vector3();
}
