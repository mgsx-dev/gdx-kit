package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Damage;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Home;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.examples.td.components.PathFollower;

// TODO rename this syetem : it only crash enemy on home when ending path ...
public class EnemyLogicSystem extends IteratingSystem
{
	public EnemyLogicSystem() {
		super(Family.all(Enemy.class, PathFollower.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		PathFollower path = PathFollower.components.get(entity);
		Enemy enemy = Enemy.components.get(entity);
		
		if(enemy.homeTarget != null && path.t >= 1){
			// TODO home can change ... due to recycling, it can be wrong ...
			Home home = Home.components.get(enemy.homeTarget);
			if(home != null){
				Life homeLife = Life.components.get(enemy.homeTarget);
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
