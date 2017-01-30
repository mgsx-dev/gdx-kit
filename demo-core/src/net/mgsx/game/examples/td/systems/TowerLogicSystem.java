package net.mgsx.game.examples.td.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Life;
import net.mgsx.game.examples.td.components.Range;
import net.mgsx.game.examples.td.components.Shot;
import net.mgsx.game.examples.td.components.TileComponent;
import net.mgsx.game.examples.td.components.Tower;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class TowerLogicSystem extends IteratingSystem
{
	private ImmutableArray<Entity> enemies;
	
	private Array<Entity> candidates = new Array<Entity>();
	
	public TowerLogicSystem() {
		super(Family.all(Tower.class, TileComponent.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		enemies = engine.getEntitiesFor(Family.all(Transform2DComponent.class, Enemy.class).get());
		engine.addEntityListener(Family.all(Enemy.class).get(), new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				for(Entity towerEntity : getEntities()){
					Tower tower = Tower.components.get(towerEntity);
					if(tower.target == entity){
						tower.target = null;
					}
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
			}
		});
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Tower tower = Tower.components.get(entity);
		TileComponent tile = TileComponent.components.get(entity);
		
		tower.reload -= deltaTime;
		if(tower.reload < 0)
		{
			tower.reload = 0;
		}
		
		// check if target (if any) still in range
		Range range = Range.components.get(entity);
		if(tower.target != null && range != null)
		{
			Transform2DComponent transform = Transform2DComponent.components.get(tower.target);
			if(transform.position.dst2(tile.x + .5f, tile.y + .5f) > range.distance * range.distance){
				tower.target = null;
			}
		}
		
		// find best target if no targets
		if(tower.target == null){
			if(enemies.size() > 0){
				candidates.clear();
				
				for(Entity enemyEntity : enemies){
					if(range != null){
						Transform2DComponent transform = Transform2DComponent.components.get(enemyEntity);
						if(transform.position.dst2(tile.x + .5f, tile.y + .5f) <= range.distance * range.distance){
							candidates.add(enemyEntity);
						}
					}else{
						candidates.add(enemyEntity);
					}
				}
				
				candidates.sort(new Comparator<Entity>() {
					@Override
					public int compare(Entity o1, Entity o2) {
						Enemy e1 = Enemy.components.get(o1);
						Enemy e2 = Enemy.components.get(o2);
						return Float.compare(e1.home, e2.home);
					}
				});
				
				if(candidates.size > 0){
					tower.target = candidates.first();
				}
			}
		}
		
		// rotate canon to current target if any
		if(tower.target != null)
		{
			Transform2DComponent targetTransform = Transform2DComponent.components.get(tower.target);
			float angle = MathUtils.atan2(targetTransform.position.y - tile.y - .5f, targetTransform.position.x - tile.x - .5f) * MathUtils.radiansToDegrees;
			
			
			// rotate to target
			float deltaAngle = (angle - tower.angle);
			if(deltaAngle > 180){
				deltaAngle -= 360;
			}else if(deltaAngle < -180){
				deltaAngle += 360;
			}
			
			
			float angularVelocityMin = -tower.angleVelocity * deltaTime;
			float angularVelocityMax = tower.angleVelocity * deltaTime;
			
			if(deltaAngle > angularVelocityMax) deltaAngle = angularVelocityMax;
			else if(deltaAngle < angularVelocityMin) deltaAngle = angularVelocityMin;
			tower.angle += deltaAngle;
			
			deltaAngle = (angle - tower.angle);
			if(deltaAngle > 180){
				deltaAngle -= 360;
			}else if(deltaAngle < -180){
				deltaAngle += 360;
			}
			if(deltaAngle == 0)
			{
				// shoot it
				if(tower.reload <= 0){
					tower.reload += tower.reloadRequired;
					
					
					// find best target : closest to home in range
					
					Transform2DComponent target = Transform2DComponent.components.get(tower.target);
					
					Life enemyLife = Life.components.get(tower.target);
					if(enemyLife != null){
						enemyLife.current -= tower.reloadRequired * tower.damages; // XXX before impact .... // XXX depends on reload ...
					}
					
					Entity shotEntity = getEngine().createEntity();
					Shot shot = getEngine().createComponent(Shot.class);
					shot.start.set(.5f, 0).rotate(tower.angle).add(tile.x+.5f, tile.y+.5f);
					shot.end.set(target.position);
					shotEntity.add(shot);
					getEngine().addEntity(shotEntity);
				}
			}
			else
			{
				
			}
			
		}
		
		
		
	}
}
