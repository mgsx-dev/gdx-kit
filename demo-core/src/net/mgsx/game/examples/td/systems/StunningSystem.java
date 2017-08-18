package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Speed;
import net.mgsx.game.examples.td.components.Stunned;

public class StunningSystem extends IteratingSystem
{
	public StunningSystem() {
		super(Family.all(Stunned.class, Speed.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Stunned stunned = Stunned.components.get(entity);
		stunned.duration -= deltaTime;
		if(stunned.duration <= 0){
			entity.remove(Stunned.class);
		}
	}
}
