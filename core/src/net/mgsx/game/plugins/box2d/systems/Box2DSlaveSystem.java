package net.mgsx.game.plugins.box2d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.core.annotations.Inject;
import net.mgsx.game.plugins.box2d.components.Box2DBodyModel;
import net.mgsx.game.plugins.core.components.SlavePhysics;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class Box2DSlaveSystem extends IteratingSystem {
	
	@Inject Box2DWorldSystem world;
	
	private Vector2 delta = new Vector2();
	
	public Box2DSlaveSystem() {
		super(Family.all(Box2DBodyModel.class, Transform2DComponent.class, SlavePhysics.class).get(), GamePipeline.AFTER_LOGIC);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		Box2DBodyModel physic = entity.getComponent(Box2DBodyModel.class);
		Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
		if(physic.body != null){
			float ts = world.timeStep;
			physic.body.setLinearVelocity(delta.set(t.position).sub(physic.body.getPosition()).scl(1f / ts));
			if(t.rotation){
				float a1 = t.angle * MathUtils.degreesToRadians;
				float a2 = physic.body.getAngle();
				float delta = a2 - a1; // TODO not good !
				physic.body.setAngularVelocity(delta);
			}
		}
	}
}