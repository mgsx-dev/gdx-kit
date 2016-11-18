package net.mgsx.game.plugins.box2d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.core.components.SlavePhysics;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class Box2DMasterSystem extends IteratingSystem {
	public Box2DMasterSystem() {
		super(Family.all(Box2DBodyModel.class, Transform2DComponent.class).exclude(SlavePhysics.class).get(), GamePipeline.AFTER_PHYSICS);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		Box2DBodyModel physic = entity.getComponent(Box2DBodyModel.class);
		Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
		if(physic.body != null){
			t.position.set(physic.body.getPosition());
			t.angle = physic.body.getAngle();
		}
	}
}