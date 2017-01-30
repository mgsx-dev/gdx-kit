package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Frozen;

public class FrozenSystem extends IteratingSystem
{
	public FrozenSystem() {
		super(Family.all(Frozen.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Frozen frozen = Frozen.components.get(entity);
		frozen.timeout -= deltaTime;
		if(frozen.timeout <= 0)
		{
			entity.remove(Frozen.class);
		}
		
	}
}
