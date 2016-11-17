package net.mgsx.game.examples.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.components.Duplicable;

@EditableComponent
public class FallingPlatform implements Component, Duplicable
{
	
	public final static ComponentMapper<FallingPlatform> components = ComponentMapper.getFor(FallingPlatform.class);
	
	public boolean isFalling;

	public Vector2 position = new Vector2();
	public float angle;
	
	public float timeout;

	@Override
	public Component duplicate() {
		FallingPlatform clone = new FallingPlatform(); // TODO pool
		clone.isFalling = isFalling;
		clone.position.set(position);
		clone.angle = angle;
		clone.timeout = 0;
		return clone;
	}
	
}
