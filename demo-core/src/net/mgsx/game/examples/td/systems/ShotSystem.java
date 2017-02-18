package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Damage;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.examples.td.components.Shot;
import net.mgsx.game.examples.td.components.SingleTarget;
import net.mgsx.game.examples.td.components.Stunned;
import net.mgsx.game.examples.td.components.Stunning;

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
			if(singleTarget != null && singleTarget.target != null)
			{
				// remove life
				Life targetLife = Life.components.get(singleTarget.target);
				Damage damage = Damage.components.get(entity);
				if(targetLife != null && damage != null){
					targetLife.current -= damage.amount; 
				}
				
				// stun
				Stunning stunning = Stunning.components.get(entity);
				if(stunning != null)
				{
					Stunned stunned = getEngine().createComponent(Stunned.class);
					stunned.duration = stunning.duration;
					singleTarget.target.add(stunned);
				}
				
				// TODO freeze
				// TODO poison
				// TODO others ... ?
				
			}
			
			getEngine().removeEntity(entity);
			
			// TODO create shot explosion at this location
		}
	}
}
