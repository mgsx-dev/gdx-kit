package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Follow;
import net.mgsx.game.examples.td.components.Speed;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class FollowSystem extends IteratingSystem
{
	private Vector2 delta = new Vector2();
	public FollowSystem() {
		super(Family.all(Follow.class, Speed.class, Transform2DComponent.class).get(), GamePipeline.AFTER_LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Follow follow = Follow.components.get(entity);
		Speed speed = Speed.components.get(entity);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		Transform2DComponent targetTransform = Transform2DComponent.components.get(follow.head);
		if(targetTransform == null) return;
		
		delta.set(targetTransform.position).sub(transform.position);
		float distance = delta.len();
		delta.nor();
		float angle = delta.angle();
		
		// transform.angle
		if(distance > follow.maxDistance)
			transform.position.mulAdd(delta, speed.current * deltaTime);
		else if(distance < follow.minDistance)
			transform.position.mulAdd(delta, -speed.current * deltaTime);
	}
}
