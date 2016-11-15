package net.mgsx.game.plugins.particle2d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.boundary.components.BoundaryComponent;
import net.mgsx.game.plugins.particle2d.components.Particle2DComponent;

public class P2DCullingSystem extends IteratingSystem
{

	public P2DCullingSystem() {
		super(Family.all(Particle2DComponent.class, BoundaryComponent.class).get(), GamePipeline.AFTER_LOGIC);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		BoundaryComponent boundary = BoundaryComponent.components.get(entity);
		Particle2DComponent particle = Particle2DComponent.components.get(entity);
		
		if(boundary.justInside){
			particle.start();
		}
		else if(boundary.justOutside){
			particle.stop();
		}
	}

}
