package net.mgsx.game.examples.platformer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableComponent;
import net.mgsx.game.core.components.Duplicable;

@EditableComponent
public class FallingPlatform implements Component, Duplicable
{
	
	public final static ComponentMapper<FallingPlatform> components = ComponentMapper.getFor(FallingPlatform.class);
	
	public static enum State{
		INIT, RIGID, COLLAPSING, FALLING, DEAD
	}
	
	public Vector2 position = new Vector2();
	public float angle;
	
	public float timeout;
	
	@Editable public float collapseTime = 0.5f, regenerationDelay = 4;

	public State state = State.INIT;
	
	@Override
	public Component duplicate(Engine engine) {
		FallingPlatform clone = engine.createComponent(FallingPlatform.class);
		clone.state = state;
		clone.collapseTime = collapseTime;
		clone.position.set(position);
		clone.angle = angle;
		clone.timeout = 0;
		return clone;
	}
	
}
