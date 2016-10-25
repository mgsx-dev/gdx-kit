package net.mgsx.game.examples.platformer.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

public class PlatformComponent implements Component
{
	public float time = 0;
	public float speed = 1;
	public Vector3 position = new Vector3();
}
