package net.mgsx.game.core.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class Transform2DComponent implements Component, Duplicable
{
	public Vector2 position = new Vector2();
	public float angle;
	@Override
	public Component duplicate() {
		Transform2DComponent clone = new Transform2DComponent();
		clone.position.set(position);
		clone.angle = angle;
		return clone;
	}
}
