package net.mgsx.game.plugins.particle2d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;
import net.mgsx.game.plugins.particle2d.components.Particle2DComponent;

/**
 * only for dynamic particle (moving emitters)
 * 
 * @author mgsx
 *
 */
public class P2DBoundarySystem extends IteratingSystem
{

	public P2DBoundarySystem() {
		super(Family.all(Particle2DComponent.class, BoundaryComponent.class, Transform2DComponent.class).get(), GamePipeline.AFTER_LOGIC);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		BoundaryComponent boundary = BoundaryComponent.components.get(entity);
		Particle2DComponent particle = Particle2DComponent.components.get(entity);

		// TODO ? boundary.box.set(particle.effect.getBoundingBox());
		
		boundary.box.min.set(particle.localBox.min).add(transform.position.x, transform.position.y, 0);
		boundary.box.max.set(particle.localBox.max).add(transform.position.x, transform.position.y, 0);
		boundary.box.set(boundary.box.min, boundary.box.max);
	}

}
