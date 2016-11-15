package net.mgsx.game.plugins.particle2d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.plugins.particle2d.components.Particle2DComponent;

// TODO 2 systems !
public class P2DUpdateSystem extends IteratingSystem 
{

	public P2DUpdateSystem() {
		super(Family.all(Particle2DComponent.class).get(), GamePipeline.AFTER_PHYSICS);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Particle2DComponent p = entity.getComponent(Particle2DComponent.class);
		if(p.effect != null){
			p.effect.update(deltaTime);
			if(p.effect.isComplete() && !p.paused){
				p.effect.free();
				p.effect = null;
				// TODO auto die entity ?
			}
		}
		
	}
}