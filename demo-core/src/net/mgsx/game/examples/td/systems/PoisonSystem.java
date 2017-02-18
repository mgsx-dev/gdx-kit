package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.examples.td.components.Poisoned;

public class PoisonSystem extends IteratingSystem
{
	public PoisonSystem() {
		super(Family.all(Poisoned.class, Life.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Poisoned poisoned = Poisoned.components.get(entity);
		poisoned.duration -= deltaTime;
		if(poisoned.duration <= 0)
		{
			entity.remove(Poisoned.class);
		}
		else
		{
			Life life = Life.components.get(entity);
			life.current -= poisoned.damageSpeed * deltaTime;
		}
	}
}
