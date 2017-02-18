package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Frozen;
import net.mgsx.game.examples.td.components.Speed;
import net.mgsx.game.examples.td.components.Stunned;

public class SpeedSystem extends IteratingSystem
{
	public SpeedSystem() {
		super(Family.all(Speed.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Speed speed = Speed.components.get(entity);
		
		Frozen frozen = Frozen.components.get(entity);
		float speedFactor = 1;
		if(frozen != null)
		{
			speedFactor *= frozen.rate;
		}
		Stunned stunned = Stunned.components.get(entity);
		if(stunned != null)
		{
			speedFactor = 0;
		}
		
		speed.current = speed.base * speedFactor;
			
	}
}
