package net.mgsx.game.plugins.particle2d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.particle2d.components.Particle2DComponent;

public class P2DTransformSystem extends IteratingSystem {

	public P2DTransformSystem() {
		super(Family.all(Particle2DComponent.class, Transform2DComponent.class).get(), GamePipeline.AFTER_PHYSICS);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Transform2DComponent t = entity.getComponent(Transform2DComponent.class);
		Particle2DComponent p = entity.getComponent(Particle2DComponent.class);
		p.position.set(t.position);
		p.effect.setPosition(t.position.x, t.position.y);
	}
}