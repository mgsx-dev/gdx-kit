package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Life;

public class LifeSystem extends IteratingSystem
{
	public LifeSystem() {
		super(Family.all(Life.class).get(), GamePipeline.AFTER_LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Life life = Life.components.get(entity);
		if(life.current < 0) // strictly inferior to prevent removing 0 life entity
		{
			getEngine().removeEntity(entity);
		}
	}
}
