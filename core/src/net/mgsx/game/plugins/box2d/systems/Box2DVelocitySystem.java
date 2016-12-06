package net.mgsx.game.plugins.box2d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.core.components.Velocity2D;

public class Box2DVelocitySystem extends IteratingSystem
{
	public Box2DVelocitySystem() {
		super(Family.all(Velocity2D.class, Box2DBodyModel.class).get(), GamePipeline.BEFORE_PHYSICS);
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Box2DBodyModel physics = Box2DBodyModel.components.get(entity);
		Velocity2D velocity = Velocity2D.components.get(entity);
		physics.body.setLinearVelocity(velocity.velocity);
	}
}
