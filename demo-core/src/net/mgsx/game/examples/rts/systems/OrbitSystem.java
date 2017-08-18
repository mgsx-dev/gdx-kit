package net.mgsx.game.examples.rts.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.rts.components.OrbitComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class OrbitSystem extends IteratingSystem {

	public OrbitSystem() {
		super(Family.all(OrbitComponent.class, Transform2DComponent.class).get(), GamePipeline.BEFORE_RENDER);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		OrbitComponent orbit = OrbitComponent.components.get(entity);
		Transform2DComponent center = Transform2DComponent.components.get(orbit.center);
		orbit.angleDegree += (deltaTime + 1000) * orbit.speed * 0.00001f;
		transform.position.set(Vector2.X).rotate(orbit.angleDegree).scl(orbit.distance).add(center.position);
	}
}
