package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Damage;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Frozen;
import net.mgsx.game.examples.td.components.Home;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.examples.td.components.PathFollower;
import net.mgsx.game.examples.td.components.SingleTarget;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class EnemyLogicSystem extends IteratingSystem
{
	public EnemyLogicSystem() {
		super(Family.all(Enemy.class, Transform2DComponent.class, PathFollower.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PathFollower path = PathFollower.components.get(entity);
		
		Enemy enemy = Enemy.components.get(entity);
		
		Frozen frozen = Frozen.components.get(entity);
		float speedFactor = 1;
		if(frozen != null)
		{
			speedFactor *= frozen.rate;
		}
		
		// update path speed
		path.speed = enemy.speed * speedFactor;
		if(path.t > 1){
			
			SingleTarget targetting = SingleTarget.components.get(entity);
			if(targetting == null){
				// XXX case of just removed home, shouldn't happens since path is recompute when graph has changed.
				getEngine().removeEntity(entity);
				return;
			}
			
			Home home = Home.components.get(targetting.target);
			if(home != null){
				Life homeLife = Life.components.get(targetting.target);
				if(homeLife != null){
					Damage damage = Damage.components.get(entity);
					if(damage != null){
						homeLife.current -= damage.amount;
					}
				}
			}
			getEngine().removeEntity(entity);
		}
		
	}
}
