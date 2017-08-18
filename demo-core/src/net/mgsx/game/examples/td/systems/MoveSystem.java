package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Direction;
import net.mgsx.game.examples.td.components.Speed;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class MoveSystem extends IteratingSystem
{
	public MoveSystem() {
		super(Family.all(Transform2DComponent.class, Direction.class, Speed.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		Direction direction = Direction.components.get(entity);
		Speed speed = Speed.components.get(entity);
		
		transform.position.mulAdd(direction.vector, speed.current * deltaTime);
	}
}
