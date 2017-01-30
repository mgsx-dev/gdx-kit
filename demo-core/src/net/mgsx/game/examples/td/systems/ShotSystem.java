package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Shot;

public class ShotSystem extends IteratingSystem
{
	public ShotSystem() {
		super(Family.all(Shot.class).get(), GamePipeline.AFTER_LOGIC);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Shot shot = Shot.components.get(entity);
		shot.t += deltaTime * shot.speed;
		
		if(shot.t > 1){
			getEngine().removeEntity(entity);
		}
	}
}
