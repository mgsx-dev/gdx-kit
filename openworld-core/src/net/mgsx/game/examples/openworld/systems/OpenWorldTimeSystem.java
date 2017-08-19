package net.mgsx.game.examples.openworld.systems;

import com.badlogic.ashley.core.EntitySystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Editable;
import net.mgsx.game.core.annotations.EditableSystem;

@EditableSystem
public class OpenWorldTimeSystem extends EntitySystem
{
	public transient float time;
	@Editable public float speed = 1;
	
	public OpenWorldTimeSystem() {
		super(GamePipeline.INPUT);
	}
	
	@Override
	public void update(float deltaTime) {
		time += deltaTime * speed ;
	}
}
