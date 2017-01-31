package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Damage;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.examples.td.components.Shot;
import net.mgsx.game.examples.td.components.SingleTarget;

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
			
			SingleTarget singleTarget = SingleTarget.components.get(entity);
			if(singleTarget != null)
			{
				Life targetLife = Life.components.get(singleTarget.target);
				Damage damage = Damage.components.get(entity);
				if(targetLife != null && damage != null){
					targetLife.current -= damage.amount; 
				}
			}
			
			getEngine().removeEntity(entity);
			
			// TODO create shot explosion at this location
		}
	}
}
