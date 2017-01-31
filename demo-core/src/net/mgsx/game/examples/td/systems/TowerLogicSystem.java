package net.mgsx.game.examples.td.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import net.mgsx.game.core.GamePipeline;
import net.mgsx.game.examples.td.components.Canon;
import net.mgsx.game.examples.td.components.Damage;
import net.mgsx.game.examples.td.components.Enemy;
import net.mgsx.game.examples.td.components.Range;
import net.mgsx.game.examples.td.components.Shot;
import net.mgsx.game.examples.td.components.SingleTarget;
import net.mgsx.game.examples.td.components.TileComponent;
import net.mgsx.game.plugins.core.components.Transform2DComponent;

public class TowerLogicSystem extends IteratingSystem
{
	private ImmutableArray<Entity> enemies;
	
	private Array<Entity> candidates = new Array<Entity>();
	
	public TowerLogicSystem() {
		super(Family.all(Canon.class, SingleTarget.class, TileComponent.class).get(), GamePipeline.LOGIC);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		enemies = engine.getEntitiesFor(Family.all(Transform2DComponent.class, Enemy.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) 
	{
		Canon tower = Canon.components.get(entity);
		TileComponent tile = TileComponent.components.get(entity);
		SingleTarget targeting = SingleTarget.components.get(entity);
		
		tower.reload -= deltaTime;
		if(tower.reload < 0)
		{
			tower.reload = 0;
		}
		
		// check if target (if any) still in range
		Range range = Range.components.get(entity);
		if(targeting.target != null && range != null)
		{
			Transform2DComponent transform = Transform2DComponent.components.get(targeting.target);
			if(transform.position.dst2(tile.x + .5f, tile.y + .5f) > range.distance * range.distance){
				targeting.target = null;
			}
		}
		
		// find best target if no targets
		if(targeting.target == null){
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
					targeting.target = candidates.first();
				}
			}
		}
		
		// rotate canon to current target if any
		if(targeting.target != null)
		{
			Transform2DComponent targetTransform = Transform2DComponent.components.get(targeting.target);
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
					
					
					Transform2DComponent target = Transform2DComponent.components.get(targeting.target);
					
					Damage towerDamage = Damage.components.get(entity);
					
					Entity shotEntity = getEngine().createEntity();
					Shot shot = getEngine().createComponent(Shot.class);
					shot.start.set(.5f, 0).rotate(tower.angle).add(tile.x+.5f, tile.y+.5f);
					shot.end.set(target.position);
					shotEntity.add(shot);
					
					SingleTarget singleTarget = getEngine().createComponent(SingleTarget.class);
					singleTarget.target = targeting.target;
					shotEntity.add(singleTarget);
					
					if(towerDamage != null){
						Damage shotDamage = getEngine().createComponent(Damage.class);
						shotDamage.amount = towerDamage.amount;
						shotEntity.add(shotDamage);
					}
					
					getEngine().addEntity(shotEntity);
				}
			}
			else
			{
				
			}
			
		}
		
		
		
	}
}
