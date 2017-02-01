package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Aiming;
import net.mgsx.game.examples.td.components.Damage;
import net.mgsx.game.examples.td.components.Lazer;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.examples.td.components.SingleTarget;

/**
 * Lazer logic implementation.
 * @author mgsx
 *
 */
public class LazerSystem extends IteratingSystem
{
	public LazerSystem() {
		super(Family.all(Lazer.class, Damage.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Lazer lazer = Lazer.components.get(entity);
		
		// release on targets
		SingleTarget targeting = SingleTarget.components.get(entity);
		Aiming aiming = Aiming.components.get(entity);
		if(targeting != null && targeting.target != null && (aiming== null || aiming.inSights))
		{
			// see if it can activate (already activated or enough energy)
			if(lazer.active || lazer.charge >= lazer.chargeMin){
				// remove life from target
				Damage damage = Damage.components.get(entity);
				Life targetLife = Life.components.get(targeting.target);
				if(targetLife != null && damage != null)
				{
					targetLife.current -= damage.amount * deltaTime;
				}
				lazer.active = true;
			}
		}
		else
		{
			// no target then shut down
			lazer.active = false;
		}
		
		// fill or empty lazer energy
		if(lazer.active)
		{
			lazer.charge -= lazer.emptySpeed * deltaTime;
			if(lazer.charge < 0){
				lazer.charge = 0;
				lazer.active = false;
			}
		}
		else
		{
			lazer.charge += lazer.fillSpeed * deltaTime;
			if(lazer.charge > lazer.chargeMax){
				lazer.charge = lazer.chargeMax;
			}
		}
	}
}
