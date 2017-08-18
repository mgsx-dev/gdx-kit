package net.mgsx.game.examples.td.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.PathFollower;
import net.mgsx.game.examples.td.components.Road;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

/**
 * System responsible for enemy analysis (distance from home) used by tower
 * to choose most dangerous target.
 * 
 * @author mgsx
 *
 */
public class EnemyAnalysisSystem extends IteratingSystem
{
	public EnemyAnalysisSystem() {
		super(Family.all(Enemy.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Enemy enemy = Enemy.components.get(entity);
		
		// path is the best hint for home distance.
		// TODO not true in case of multiple possible logic path.
		PathFollower path = PathFollower.components.get(entity);
		if(path != null){
			enemy.home = path.t;
			return;
		}
		
		MapSystem map = getEngine().getSystem(MapSystem.class);
		Transform2DComponent transform = Transform2DComponent.components.get(entity);
		if(transform != null)
		{
			// try with tile distance
			// TODO accuracy at tile granularity.
			
			Entity entityTile = map.getTile((int)transform.position.x, (int)transform.position.y);
			if(entityTile != null){
				Road entityRoad = Road.components.get(entityTile);
				if(entityRoad != null){
					enemy.home = entityRoad.home;
					return;
				}
			}
			// not on a road tile, so flying distance is used.
			// TODO find closest home and compute distance
		}
		
		// distance is zero when no hints which has sense : unknown is more dangerous than knwon.
		enemy.home = 0;
	}
}
