package net.mgsx.game.plugins.core.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.core.components.Rotate2DAnimation;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class Rotation2DSystem extends IteratingSystem
{

	public Rotation2DSystem() {
		super(Family.all(Rotate2DAnimation.class, Transform2DComponent.class).get(), GamePipeline.LOGIC);
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Rotate2DAnimation animation = Rotate2DAnimation.components.get(entity);
		animation.time = (animation.time + animation.speed * deltaTime) % 360;
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		transform.angle = animation.time;
	}

}
